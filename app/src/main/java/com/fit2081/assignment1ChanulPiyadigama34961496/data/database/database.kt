package com.fit2081.assignment1ChanulPiyadigama34961496.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fit2081.assignment1ChanulPiyadigama34961496.data.Converters
import com.fit2081.assignment1ChanulPiyadigama34961496.data.models.FoodIntake
import com.fit2081.assignment1ChanulPiyadigama34961496.data.models.Patient
import com.fit2081.assignment1ChanulPiyadigama34961496.data.models.NutriCoachTips
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.fit2081.assignment1ChanulPiyadigama34961496.utils.loadDataFromCSV
import com.fit2081.assignment1ChanulPiyadigama34961496.utils.mapCsvRowToDataClass

@Database(entities = [Patient::class, FoodIntake::class, NutriCoachTips::class], version = 3)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    //at runtime, the abstract methods are implemented by Room to return the concrete DAOs created
    //using the interfaces
    abstract fun patientDao(): PatientDao
    abstract fun foodIntakeDao(): FoodIntakeDao
    abstract fun nutriCoachTipsDao(): NutriCoachTipsDao

    //the companion objects stores static variables and methods of the class
    companion object {
        //volatile forces this varaible to not be cached, and any threads that want to access
        //it will always read the most recent value, from the main memory.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        //Static method to create db, synchronized only allows one thread at a time even when
        //multiple threads are trying to access the method, if instance is null the db is created
        //and since volatile the next thread in line will not create a new instance, since itll recieve
        //the latest value of INSTANCE and it will not be null.
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    //provide the application context so the db lives as long as the app (lifecycle)
                    context.applicationContext,
                    //provide the database class, so it knows what to map from
                    AppDatabase::class.java,
                    "nutritrack_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance

                // Check if database needs to be populated
                CoroutineScope(Dispatchers.IO).launch {

                    if (instance.patientDao().getPatientCount() == 0) {
                        populateDatabase(context, instance)
                    }
                }

                instance
            }
        }


        private suspend fun populateDatabase(context: Context, database: AppDatabase) {
            val patientDao = database.patientDao()
            val csvData = loadDataFromCSV(context)

            csvData.forEach { row ->
                val patient = mapCsvRowToDataClass<Patient>(row)
                if (patient != null) {

                    patientDao.insertPatient(patient)
                }
            }
        }
    }
}