package com.yg.mileage.data

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Drop existing tables completely and recreate them
        db.execSQL("DROP TABLE IF EXISTS `vehicles`")
        db.execSQL("DROP TABLE IF EXISTS `trips`")
        
        // Create new vehicles table with id as primary key
        db.execSQL("""
            CREATE TABLE `vehicles` (
                `id` TEXT NOT NULL PRIMARY KEY,
                `name` TEXT NOT NULL,
                `fuelType` TEXT,
                `createdAt` INTEGER NOT NULL,
                `updatedAt` INTEGER NOT NULL
            )
        """)
        
        // Create new trips table with vehicleId
        db.execSQL("""
            CREATE TABLE `trips` (
                `id` TEXT NOT NULL PRIMARY KEY,
                `vehicleId` TEXT NOT NULL,
                `vehicleName` TEXT NOT NULL,
                `startMileage` REAL,
                `endMileage` REAL,
                `fuelFilled` REAL,
                `tripDistance` REAL,
                `fuelEfficiency` REAL,
                `status` TEXT NOT NULL,
                `createdAt` INTEGER NOT NULL,
                `updatedAt` INTEGER NOT NULL
            )
        """)
    }
}

val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // This migration ensures the schema is correct for version 6
        // Drop and recreate tables to ensure correct schema
        db.execSQL("DROP TABLE IF EXISTS `vehicles`")
        db.execSQL("DROP TABLE IF EXISTS `trips`")
        
        // Create vehicles table with correct schema
        db.execSQL("""
            CREATE TABLE `vehicles` (
                `id` TEXT NOT NULL PRIMARY KEY,
                `name` TEXT NOT NULL,
                `fuelType` TEXT,
                `createdAt` INTEGER NOT NULL,
                `updatedAt` INTEGER NOT NULL
            )
        """)
        
        // Create trips table with correct schema
        db.execSQL("""
            CREATE TABLE `trips` (
                `id` TEXT NOT NULL PRIMARY KEY,
                `vehicleId` TEXT NOT NULL,
                `vehicleName` TEXT NOT NULL,
                `startMileage` REAL,
                `endMileage` REAL,
                `fuelFilled` REAL,
                `tripDistance` REAL,
                `fuelEfficiency` REAL,
                `status` TEXT NOT NULL,
                `createdAt` INTEGER NOT NULL,
                `updatedAt` INTEGER NOT NULL
            )
        """)
    }
}

val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Helper to check for column existence
        fun hasColumn(db: SupportSQLiteDatabase, tableName: String, columnName: String): Boolean {
            val cursor = db.query("PRAGMA table_info($tableName)")
            val colIndex = cursor.getColumnIndex("name")
            var result = false
            while (cursor.moveToNext()) {
                if (cursor.getString(colIndex) == columnName) {
                    result = true
                    break
                }
            }
            cursor.close()
            return result
        }

        // Safely add userId to vehicles
        if (!hasColumn(db, "vehicles", "userId")) {
            db.execSQL("ALTER TABLE vehicles ADD COLUMN userId TEXT")
        }
        // For legacy rows (or if column just created)
        db.execSQL("UPDATE vehicles SET userId = 'legacy' WHERE userId IS NULL")

        // Safely add userId to trips
        if (!hasColumn(db, "trips", "userId")) {
            db.execSQL("ALTER TABLE trips ADD COLUMN userId TEXT")
        }
        db.execSQL("UPDATE trips SET userId = 'legacy' WHERE userId IS NULL")
    }
}

val MIGRATION_7_8 = object : Migration(7, 8) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Create currencies table
        db.execSQL("""
            CREATE TABLE `currencies` (
                `id` TEXT NOT NULL PRIMARY KEY,
                `code` TEXT NOT NULL,
                `name` TEXT NOT NULL,
                `symbol` TEXT NOT NULL,
                `isDefault` INTEGER NOT NULL DEFAULT 0
            )
        """)
        
        // Create fuel_prices table
        db.execSQL("""
            CREATE TABLE `fuel_prices` (
                `id` TEXT NOT NULL PRIMARY KEY,
                `fuelType` TEXT NOT NULL,
                `pricePerUnit` REAL NOT NULL,
                `currencyId` TEXT NOT NULL,
                `lastUpdated` INTEGER NOT NULL,
                `isActive` INTEGER NOT NULL DEFAULT 1
            )
        """)
        
        // Insert default currencies
        db.execSQL("INSERT INTO currencies (id, code, name, symbol, isDefault) VALUES ('usd', 'USD', 'US Dollar', '$', 1)")
        db.execSQL("INSERT INTO currencies (id, code, name, symbol, isDefault) VALUES ('eur', 'EUR', 'Euro', '€', 0)")
        db.execSQL("INSERT INTO currencies (id, code, name, symbol, isDefault) VALUES ('inr', 'INR', 'Indian Rupee', '₹', 0)")
        db.execSQL("INSERT INTO currencies (id, code, name, symbol, isDefault) VALUES ('gbp', 'GBP', 'British Pound', '£', 0)")
    }
}

val MIGRATION_8_9 = object : Migration(8, 9) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Helper to check column
        fun hasColumn(db: SupportSQLiteDatabase, tableName: String, columnName: String): Boolean {
            val cursor = db.query("PRAGMA table_info($tableName)")
            val colIndex = cursor.getColumnIndex("name")
            var result = false
            while (cursor.moveToNext()) {
                if (cursor.getString(colIndex) == columnName) {
                    result = true
                    break
                }
            }
            cursor.close()
            return result
        }
        // Only add columns if missing
        if (!hasColumn(db, "trips", "fuelCost")) {
            db.execSQL("ALTER TABLE trips ADD COLUMN fuelCost REAL")
        }
        if (!hasColumn(db, "trips", "fuelPricePerUnit")) {
            db.execSQL("ALTER TABLE trips ADD COLUMN fuelPricePerUnit REAL")
        }
        if (!hasColumn(db, "trips", "currencyId")) {
            db.execSQL("ALTER TABLE trips ADD COLUMN currencyId TEXT")
        }
    }
}
