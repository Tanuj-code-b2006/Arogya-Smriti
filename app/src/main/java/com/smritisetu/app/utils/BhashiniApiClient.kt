package com.smritisetu.app.utils

import com.smritisetu.app.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object BhashiniApiClient {

    // Accessing Credentials from BuildConfig (securely stored in local.properties)
    private val USER_ID = BuildConfig.BHASHINI_USER_ID
    private val ULCA_API_KEY = BuildConfig.BHASHINI_API_KEY

    // Public multi-task (ASR+Translation+TTS) pipeline ID published in Bhashini's integration docs.
    private const val PIPELINE_ID = "64392f96daac500b55c543cd"

    private const val CONFIG_ENDPOINT = "https://meity-auth.ulcacontrib.org/ulca/apis/v0/model/getModelsPipeline"

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    fun isConfigured(): Boolean =
        USER_ID.isNotBlank() && !USER_ID.contains("PASTE_YOUR") &&
        ULCA_API_KEY.isNotBlank() && !ULCA_API_KEY.contains("PASTE_YOUR")

    private data class PipelineConfig(val serviceId: String, val callbackUrl: String, val authKey: String)
    private val configCache = mutableMapOf<String, PipelineConfig>()

    private suspend fun fetchPipelineConfig(taskType: String, langCode: String): PipelineConfig? =
        withContext(Dispatchers.IO) {
            val cacheKey = "$taskType:$langCode"
            configCache[cacheKey]?.let { return@withContext it }
            try {
                val body = JSONObject().apply {
                    put("pipelineTasks", JSONArray().put(JSONObject().apply {
                        put("taskType", taskType)
                        put("config", JSONObject().apply {
                            put("language", JSONObject().apply {
                                put("sourceLanguage", langCode)
                            })
                        })
                    }))
                    put("pipelineRequestConfig", JSONObject().put("pipelineId", PIPELINE_ID))
                }.toString().toRequestBody("application/json".toMediaType())

                val req = Request.Builder()
                    .url(CONFIG_ENDPOINT)
                    .addHeader("userID", USER_ID)
                    .addHeader("ulcaApiKey", ULCA_API_KEY)
                    .post(body)
                    .build()

                client.newCall(req).execute().use { resp ->
                    if (!resp.isSuccessful) return@withContext null
                    val json = JSONObject(resp.body?.string() ?: return@withContext null)
                    val serviceId = json.getJSONArray("pipelineResponseConfig")
                        .getJSONObject(0).getJSONArray("config")
                        .getJSONObject(0).getString("serviceId")
                    val endpoint = json.getJSONObject("pipelineInferenceAPIEndPoint")
                    val callbackUrl = endpoint.getString("callbackUrl")
                    val authKey = endpoint.getJSONObject("inferenceApiKey").getString("value")

                    PipelineConfig(serviceId, callbackUrl, authKey).also { configCache[cacheKey] = it }
                }
            } catch (e: Exception) {
                null
            }
        }

    /** Speech → Text, in the given Bhashini language code. Returns null on any failure (caller should fallback). */
    suspend fun speechToText(wavBase64: String, langCode: String): String? = withContext(Dispatchers.IO) {
        val cfg = fetchPipelineConfig("asr", langCode) ?: return@withContext null
        try {
            val body = JSONObject().apply {
                put("pipelineTasks", JSONArray().put(JSONObject().apply {
                    put("taskType", "asr")
                    put("config", JSONObject().apply {
                        put("language", JSONObject().put("sourceLanguage", langCode))
                        put("serviceId", cfg.serviceId)
                        put("audioFormat", "wav")
                        put("samplingRate", 16000)
                    })
                }))
                put("inputData", JSONObject().put("audio", JSONArray().put(
                    JSONObject().put("audioContent", wavBase64)
                )))
            }.toString().toRequestBody("application/json".toMediaType())

            val req = Request.Builder()
                .url(cfg.callbackUrl)
                .addHeader("Authorization", cfg.authKey)
                .post(body)
                .build()

            client.newCall(req).execute().use { resp ->
                if (!resp.isSuccessful) return@withContext null
                val json = JSONObject(resp.body?.string() ?: return@withContext null)
                json.getJSONArray("pipelineResponse").getJSONObject(0)
                    .getJSONArray("output").getJSONObject(0).getString("source")
            }
        } catch (e: Exception) {
            null
        }
    }

    /** Text → Speech (base64 WAV audio), in the given Bhashini language code. Returns null on failure. */
    suspend fun textToSpeech(text: String, langCode: String): String? = withContext(Dispatchers.IO) {
        val cfg = fetchPipelineConfig("tts", langCode) ?: return@withContext null
        try {
            val body = JSONObject().apply {
                put("pipelineTasks", JSONArray().put(JSONObject().apply {
                    put("taskType", "tts")
                    put("config", JSONObject().apply {
                        put("language", JSONObject().put("sourceLanguage", langCode))
                        put("serviceId", cfg.serviceId)
                        put("gender", "female")
                        put("samplingRate", 8000)
                    })
                }))
                put("inputData", JSONObject().put("input", JSONArray().put(
                    JSONObject().put("source", text)
                )))
            }.toString().toRequestBody("application/json".toMediaType())

            val req = Request.Builder()
                .url(cfg.callbackUrl)
                .addHeader("Authorization", cfg.authKey)
                .post(body)
                .build()

            client.newCall(req).execute().use { resp ->
                if (!resp.isSuccessful) return@withContext null
                val json = JSONObject(resp.body?.string() ?: return@withContext null)
                json.getJSONArray("pipelineResponse").getJSONObject(0)
                    .getJSONArray("audio").getJSONObject(0).getString("audioContent")
            }
        } catch (e: Exception) {
            null
        }
    }
}