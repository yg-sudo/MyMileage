package com.yg.mileage.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.yg.mileage.FuelType
import com.yg.mileage.TripStatus
import java.util.Date

@Database(
    entities = [VehicleEntity::class, TripEntity::class, CurrencyEntity::class, FuelPriceEntity::class],
    version = 9,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun vehicleDao(): VehicleDao
    abstract fun tripDao(): TripDao
    abstract fun currencyDao(): CurrencyDao
    abstract fun fuelPriceDao(): FuelPriceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mileage_database"
                )
                    .addMigrations(MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }
    
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
    
    @TypeConverter
    fun fromFuelType(value: FuelType?): String? {
        return value?.name
    }
    
    @TypeConverter
    fun toFuelType(value: String?): FuelType? {
        return value?.let { FuelType.valueOf(it) }
    }
    
    @TypeConverter
    fun fromTripStatus(value: TripStatus): String {
        return value.name
    }
    
    @TypeConverter
    fun toTripStatus(value: String): TripStatus {
        return TripStatus.valueOf(value)
    }
} 