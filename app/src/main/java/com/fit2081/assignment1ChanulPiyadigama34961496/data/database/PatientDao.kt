package com.fit2081.assignment1ChanulPiyadigama34961496.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fit2081.assignment1ChanulPiyadigama34961496.data.models.GenderSpecificScores
import com.fit2081.assignment1ChanulPiyadigama34961496.data.models.HeifaAverages
import com.fit2081.assignment1ChanulPiyadigama34961496.data.models.Patient

@Dao
interface PatientDao {
    //at runtime these methods are converted to SQL queries, based on their annotations. They
    //are still accessible as methods.
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertPatient(patient: Patient)

    @Query("SELECT * FROM patients WHERE userId = :userId")
    suspend fun getPatientById(userId: String): Patient?

    @Query("SELECT COUNT(*) FROM patients")
    suspend fun getPatientCount(): Int

    //Find patient with matching userId and phoneNumber
    @Query("SELECT * FROM patients WHERE userId = :userId AND phoneNumber = :phoneNumber")
    suspend fun validatePreRegisteredUser(userId: String, phoneNumber: String): Patient?

    //Claim pre-registered account, update name and password
    @Query("UPDATE patients SET name = :name WHERE userId = :userId")
    suspend fun claimAccount(userId: String, name: String)

    //ensures users dont register with the same userId
    @Query("SELECT (name != '') FROM patients WHERE userId = :userId")
    suspend fun isAccountClaimed(userId: String): Boolean

    @Query("SELECT userId FROM patients")
    suspend fun getAllPatientIds(): List<String>

    @Query("""
    SELECT userId, sex,
    json_group_array(
        json_object(
            'name', name,
            'userScore', score,
            'maxScore', maxScore
        )
    ) as scores
    FROM (
        SELECT 
            userId,
            sex,
            'Total Score' as name,
            CASE WHEN sex = 'Male' THEN heiTotalScoreMale ELSE heiTotalScoreFemale END as score,
            100.0 as maxScore
        FROM patients WHERE userId = :userId
        
        UNION ALL SELECT userId, sex, 'Vegetables',
            CASE WHEN sex = 'Male' THEN vegetablesHEIFAScoreMale ELSE vegetablesHEIFAScoreFemale END,
            10.0
        FROM patients WHERE userId = :userId
            
        UNION ALL SELECT userId, sex, 'Fruits',
            CASE WHEN sex = 'Male' THEN fruitHEIFAScoreMale ELSE fruitHEIFAScoreFemale END,
            10.0
        FROM patients WHERE userId = :userId
            
        UNION ALL SELECT userId, sex, 'Grains and Cereals',
            CASE WHEN sex = 'Male' THEN grainsAndCerealsHEIFAScoreMale ELSE grainsAndCerealsHEIFAScoreFemale END,
            10.0
        FROM patients WHERE userId = :userId

        UNION ALL SELECT userId, sex, 'Whole Grains',
            CASE WHEN sex = 'Male' THEN wholeGrainsHEIFAScoreMale ELSE wholeGrainsHEIFAScoreFemale END,
            10.0
        FROM patients WHERE userId = :userId

        UNION ALL SELECT userId, sex, 'Meat & Alternatives',
            CASE WHEN sex = 'Male' THEN meatAndAlternativesHEIFAScoreMale ELSE meatAndAlternativesHEIFAScoreFemale END,
            10.0
        FROM patients WHERE userId = :userId

        UNION ALL SELECT userId, sex, 'Dairy and Alternatives',
            CASE WHEN sex = 'Male' THEN dairyAndAlternativesHEIFAScoreMale ELSE dairyAndAlternativesHEIFAScoreFemale END,
            10.0
        FROM patients WHERE userId = :userId

        UNION ALL SELECT userId, sex, 'Water',
            CASE WHEN sex = 'Male' THEN waterHEIFAScoreMale ELSE waterHEIFAScoreFemale END,
            5
        FROM patients WHERE userId = :userId

        UNION ALL SELECT userId, sex, 'Discretionary',
            CASE WHEN sex = 'Male' THEN discretionaryHEIFAScoreMale ELSE discretionaryHEIFAScoreFemale END,
            10.0
        FROM patients WHERE userId = :userId

        UNION ALL SELECT userId, sex, 'Sodium',
            CASE WHEN sex = 'Male' THEN sodiumHEIFAScoreMale ELSE sodiumHEIFAScoreFemale END,
            10.0
        FROM patients WHERE userId = :userId

        UNION ALL SELECT userId, sex, 'Alcohol',
            CASE WHEN sex = 'Male' THEN alcoholHEIFAScoreMale ELSE alcoholHEIFAScoreFemale END,
            5
        FROM patients WHERE userId = :userId

        UNION ALL SELECT userId, sex, 'Sugar',
            CASE WHEN sex = 'Male' THEN sugarHEIFAScoreMale ELSE sugarHEIFAScoreFemale END,
            10.0
        FROM patients WHERE userId = :userId

        UNION ALL SELECT userId, sex, 'Unsaturated Fat',
            CASE WHEN sex = 'Male' THEN unsaturatedFatHEIFAScoreMale ELSE unsaturatedFatHEIFAScoreFemale END,
            10.0
        FROM patients WHERE userId = :userId

        UNION ALL SELECT userId, sex, 'Saturated Fat',
            CASE WHEN sex = 'Male' THEN saturatedFatHEIFAScoreMale ELSE saturatedFatHEIFAScoreFemale END,
            10.0
        FROM patients WHERE userId = :userId
    )
    GROUP BY userId, sex
""")
    suspend fun getGenderSpecificScores(userId: String): GenderSpecificScores?


    @Query("""
    SELECT 
        ROUND(AVG(CASE WHEN sex = 'Male' THEN heiTotalScoreMale END), 2) as maleMeanScore,
        ROUND(AVG(CASE WHEN sex = 'Female' THEN heiTotalScoreFemale END), 2) as femaleMeanScore
    FROM patients
""")
    suspend fun getAverageHeifaScores(): HeifaAverages
}