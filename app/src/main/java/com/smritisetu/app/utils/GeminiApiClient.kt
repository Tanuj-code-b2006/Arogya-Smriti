package com.smritisetu.app.utils

import com.smritisetu.app.BuildConfig
import com.smritisetu.app.data.LanguagePreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

object GeminiApiClient {

    // Accessing API Key from BuildConfig (securely stored in local.properties)
    private val API_KEY = BuildConfig.GEMINI_API_KEY

    private val ENDPOINT =
        "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$API_KEY"

    private val client = OkHttpClient()

    suspend fun getNextMemoryQuestionOrReply(
        conversationHistory: List<Pair<String, String>>,
        userJustSaid: String?,
        languageLabel: String = LanguagePreference.selected.value.label
    ): String = withContext(Dispatchers.IO) {
        try {
            // Build Context from Patient's Fed App Data & Medical Vault
            val systemPrompt = """
                You are a warm, loving, and patient AI Memory Companion named 'Dost' (দস্ত / दोस्त).
                Your sole purpose is to help the patient recall their life, routines, and loved ones through gentle conversation.
                
                PATIENT FED DATA CONTEXT (STRICT SOURCE OF TRUTH):
                -------------------------------------------------
                1. PATIENT PROFILE:
                   - Full Name: Anil Baruah (অনিল বৰুৱা)
                   - Age: 74 years old
                   - Location / Home: Sonapur, Kamrup, Assam (Near Guwahati)
                   - Primary Caregiver: Daughter Sunita Baruah (সুহিতা বৰুৱা)
                   - Son: Rahul Baruah
                   - Grandson: Aryan
                   - Doctor: Dr. Rina Deka (NEIGRIHMS Neurologist)
                   
                2. MEDICAL & DIAGNOSIS HISTORY:
                   - Diagnosis: Mild Cognitive Impairment (Early Dementia)
                   - MMSE Assessment Score: 22 / 30
                   - Known Allergies: Penicillin
                   - Active Daily Prescriptions:
                     * Donepezil 5mg (QD Morning after breakfast)
                     * Amlodipine 5mg (QD Morning for Blood Pressure)
                     * Vitamin D3 (60,000 IU Weekly)
                     
                3. DAILY ROUTINE & REMINDERS (TODAY'S SCHEDULE):
                   - 8:00 AM: Take Blood Pressure Medicine (Amlodipine)
                   - 10:00 AM: Drink a glass of water (Hydration)
                   - 1:00 PM: Have lunch
                   - 4:30 PM: Doctor Checkup with Dr. Rina Deka
                   - Morning Routine Steps: Brush teeth -> Breakfast -> Morning Medicine -> Morning walk in Sonapur -> Rest
                   
                4. CULTURAL & PERSONAL PREFERENCES:
                   - Native Culture: Assamese (Enjoys Bihu dance, Rongali Bihu festival, Muga Silk Mekhela Sador, Momos, Khar Assamese dish)
                   - Favorite Activities: Memory Match Game, Cultural Recognition, Walking in Sonapur village, Talking about daughter Sunita & grandson Aryan.

                STRICT BEHAVIOR RULES:
                -----------------------
                1. DATA FED STRICTNESS: You MUST frame all your questions, memory prompts, and conversations EXCLUSIVELY around the fed patient data listed above (his daughter Sunita, son Rahul, grandson Aryan, doctor Dr. Rina, morning walk in Sonapur, Amlodipine BP medicine, hydration, Bihu dance, Khar/Momos, etc.).
                2. DO NOT ask irrelevant or general trivia questions (like space, world geography, math, politics).
                3. Ask ONE simple, empathetic, memory-stimulating question at a time.
                4. LANGUAGE RULE: Respond STRICTLY in $languageLabel script and language.
                   - If language is 'Assamese', write in Assamese script.
                   - If language is 'Hindi', write in Devanagari Hindi script.
                   - If language is 'English', write in warm English.
                5. Keep responses concise (20-35 words max) so it is easy to hear via Voice Text-to-Speech.
            """.trimIndent()

            val contents = JSONArray()
            conversationHistory.forEach { (role, text) ->
                contents.put(JSONObject().apply {
                    put("role", role)
                    put("parts", JSONArray().put(JSONObject().put("text", text)))
                })
            }
            if (userJustSaid != null) {
                contents.put(JSONObject().apply {
                    put("role", "user")
                    put("parts", JSONArray().put(JSONObject().put("text", userJustSaid)))
                })
            }

            val requestBodyJson = JSONObject().apply {
                put("contents", contents)
                put("system_instruction", JSONObject().apply {
                    put("parts", JSONArray().put(JSONObject().put("text", systemPrompt)))
                })
            }

            val body = requestBodyJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder().url(ENDPOINT).post(body).build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext mockFallback(userJustSaid, languageLabel)
                val json = JSONObject(response.body?.string() ?: return@withContext mockFallback(userJustSaid, languageLabel))
                json.getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text")
            }
        } catch (e: Exception) {
            mockFallback(userJustSaid, languageLabel)
        }
    }

    private fun mockFallback(userJustSaid: String?, lang: String): String {
        return when {
            lang.contains("Hindi") || lang.contains("हिन्दी") ->
                "नमस्ते अनिल जी! 🙏 क्या आपने आज 8 बजे अपनी बीपी की दवाई (Amlodipine) ली? आपकी बेटी सुनीता जी ने याद दिलाया था।"
            lang.contains("Assamese") || lang.contains("অসমীয়া") ->
                "নমস্কাৰ অনিল ডাঙৰীয়া! 🙏 আপুনি আজি পুৱা ৮ বজাত বিপিৰ দৰব খালে নে? আপোনাৰ জীয়ৰী সুনীতাই সোঁৱৰাই দিছিল।"
            else ->
                "Hello Anil ji! 🙏 Did you take your 8:00 AM Blood Pressure medicine today? Your daughter Sunita reminded us."
        }
    }
}
