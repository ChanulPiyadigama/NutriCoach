package com.fit2081.assignment1ChanulPiyadigama34961496.data

import android.content.Context
import android.util.Log
import com.fit2081.assignment1ChanulPiyadigama34961496.BuildConfig
import com.fit2081.assignment1ChanulPiyadigama34961496.data.database.AppDatabase
import com.fit2081.assignment1ChanulPiyadigama34961496.data.models.FoodIntake
import com.fit2081.assignment1ChanulPiyadigama34961496.data.models.FruitModel
import com.fit2081.assignment1ChanulPiyadigama34961496.data.models.NutriCoachTips
import com.fit2081.assignment1ChanulPiyadigama34961496.data.models.Patient
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NutriTrackRepository(private val context: Context) {
    private val database = AppDatabase.Companion.getDatabase(context)
    private val patientDao = database.patientDao()
    private val foodIntakeDao = database.foodIntakeDao()
    private val nutriCoachTipsDao = database.nutriCoachTipsDao()
    private val sessionManager = SessionManager(context)


    //create GeminiModel
    private val geminiModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY

    )

    //create the fruityvice api using retrofit and the interface of requests
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://www.fruityvice.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val fruityViceApi = retrofit.create(FruityViceApi::class.java)


    //NutriCoach tips operations

    //Generates a tip saves it to the database and returns it
    //HD: if the gemini model fails to generate a tip, it will try to get a random tip from the past,
    //if that fails due to a database issue, or if there are no previous tips, it will return a backup tip from a predefined list.
    suspend fun generateAndSaveTip(): Result<String> = withContext(Dispatchers.IO) {
        try {
            val userId = sessionManager.getUserId() ?: throw Exception("User not logged in")
            val patient = patientDao.getPatientById(userId) ?: throw Exception("Patient not found")
            val foodIntake = foodIntakeDao.getFoodIntakeByPatientId(userId)
            val scores = patientDao.getGenderSpecificScores(userId)

            try {
                // Try generating new tip with Gemini first
                val scoresPrompt = StringBuilder().apply {
                    scores?.scores?.forEach { score ->
                        append("* ${score.name}: ${score.userScore}/${score.maxScore}\n")
                    }
                }.toString()

                val prompt = """
                As a nutrition coach, provide a personalized health tip based on the following patient data:
                
                Patient Details:
                - Sex: ${patient.sex}
                - Scores: ${scoresPrompt}
                
                ${
                    foodIntake?.let {
                        """
                    Food Preferences:
                    - Dietary Profile: ${it.persona}
                    - Preferred Foods: ${
                            it.foodCategories.filter { entry -> entry.value }.keys.joinToString(
                                ", "
                            )
                        }
                    - Meal Times: ${it.timings.entries.joinToString(", ") { entry -> "${entry.key}: ${entry.value}" }}
                    """
                    } ?: "No food intake data available."
                }
                
                Provide a brief, encouraging tip (maximum 2 sentences) focusing on improving and maintaining a healthy diet.
            """.trimIndent()

                val response = geminiModel.generateContent(prompt).text
                if (!response.isNullOrBlank()) {
                    val tip = NutriCoachTips(patientId = userId, tip = response)
                    nutriCoachTipsDao.insertTip(tip)
                    return@withContext Result.success(response)
                }
                throw Exception("Empty response from Gemini")

            } catch (e: Exception) {
                // HD: If Gemini fails, try getting random tip from past
                val previousTips = nutriCoachTipsDao.getAllTipsByPatientId(userId)
                if (previousTips.isNotEmpty()) {
                    return@withContext Result.success(previousTips.random())
                }

                // HD: If no historical tips, use backup tip bank
                val backupTips = listOf(
                    "Try to eat a rainbow of fruits and vegetables each day for optimal nutrition.",
                    "Start your day with a protein-rich breakfast to maintain steady energy levels.",
                    "Stay hydrated by drinking water throughout the day.",
                    "Include whole grains in your meals for sustained energy.",
                    "Practice mindful eating by taking time to enjoy your meals."
                )
                return@withContext Result.success(backupTips.random())
            }
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }

    //retrieves patterns based on scores of all the patients
    suspend fun analyzePatientDataPatterns(): List<String> = withContext(Dispatchers.IO) {
        try {
            // Get all patient IDs
            val patientIds = patientDao.getAllPatientIds()

            // Get scores for each patient
            val allPatientScores = patientIds.mapNotNull { id ->
                patientDao.getGenderSpecificScores(id)
            }

            val prompt = """
        Analyze the following patient nutrition data and identify 3 interesting patterns across different genders and categories:
        
        Patient Data:
        ${
                allPatientScores.joinToString("\n\n") { scores ->
                    """
            Patient ${scores.userId}:
            - Gender: ${scores.sex}
            Scores:
            ${scores.scores.joinToString("\n") { "- ${it.name}: ${it.userScore}/${it.maxScore}" }}
            """
                }
            }
        
        Provide exactly 3 interesting patterns you observe in the data. Each pattern should focus on relationships between scores, gender differences, or notable trends. Format each pattern as a complete sentence. Do not include numbers or bullet points.
        """.trimIndent()

            val response = geminiModel.generateContent(prompt).text
                ?: throw Exception("Failed to generate patterns")

            // Split response into individual patterns
            response.split(". ")
                .filter { it.isNotBlank() }
                .map { it.trim() }
                .take(3)
        } catch (e: Exception) {
            throw Exception("Failed to analyze patterns: ${e.message}")
        }
    }

    suspend fun getAllPatientsTips(patientId: String): List<String> =
        withContext(Dispatchers.IO) {
            nutriCoachTipsDao.getAllTipsByPatientId(patientId)
        }

    //HD:deletes all tips for a specific patient
    suspend fun clearTipHistory(userId: String) = withContext(Dispatchers.IO) {
        nutriCoachTipsDao.deleteAllTipsByPatientId(userId)
    }




    //Fruit Information operations
    suspend fun getFruitInfo(name: String): Result<FruitModel> = withContext(Dispatchers.IO) {
        try {
            val response = fruityViceApi.getFruitInfo(name)
            //suspend function will return the result of the api call, Result obj can hold either FruitModel or Error
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Auth operations
    suspend fun getCurrentUser(): Patient? = withContext(Dispatchers.IO) {
        val currentUserId = sessionManager.getUserId()
        if (currentUserId != null) {
            patientDao.getPatientById(currentUserId)
        } else {
            null
        }
    }

    suspend fun validatePreRegisteredUser(userId: String, phoneNumber: String) =
        withContext(Dispatchers.IO) {
            patientDao.validatePreRegisteredUser(userId, phoneNumber)
        }

    suspend fun isAccountClaimed(userId: String) =
        withContext(Dispatchers.IO) {
            patientDao.isAccountClaimed(userId)
        }

    suspend fun claimAccount(userId: String, name: String) =
        withContext(Dispatchers.IO) {
            patientDao.claimAccount(userId, name)
        }

    //Patient operations
    suspend fun getAllPatientIds() =
        withContext(Dispatchers.IO) {
            patientDao.getAllPatientIds()
        }

    suspend fun getPatientById(userId: String) =
        withContext(Dispatchers.IO) {
            patientDao.getPatientById(userId)
        }

    suspend fun getGenderSpecificScores(userId: String) =
        withContext(Dispatchers.IO) {
            patientDao.getGenderSpecificScores(userId)
        }

    suspend fun getHeifaAverages() =
        withContext(Dispatchers.IO) {
            patientDao.getAverageHeifaScores()
        }


    // Food intake operations
    suspend fun getFoodIntakeByPatientId(patientId: String) =
        withContext(Dispatchers.IO) {
            foodIntakeDao.getFoodIntakeByPatientId(patientId)
        }

    suspend fun checkFoodIntakeCompletionByPatientId(patientId: String): Boolean {
        val foodIntake = foodIntakeDao.getFoodIntakeByPatientId(patientId) ?: return false

        // Apply full validation logic
        if (foodIntake.persona.isEmpty()) return false
        if (foodIntake.foodCategories.values.none { it }) return false
        if (foodIntake.timings.values.any { it == "00:00" }) return false

        // Check for unique times
        val uniqueTimes = foodIntake.timings.values.toSet()
        if (uniqueTimes.size < foodIntake.timings.size) return false

        return true
    }

    suspend fun insertFoodIntake(foodIntake: FoodIntake) =
        withContext(Dispatchers.IO) {
            foodIntakeDao.insertFoodIntake(foodIntake)
        }

    suspend fun updateFoodIntake(foodIntake: FoodIntake) =
        withContext(Dispatchers.IO) {
            Log.d("runs", foodIntake.toString())
            foodIntakeDao.updateFoodIntake(foodIntake)
        }
}