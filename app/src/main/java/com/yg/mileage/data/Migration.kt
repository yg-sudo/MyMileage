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
        // Add userId column to vehicles
        db.execSQL("ALTER TABLE vehicles ADD COLUMN userId TEXT")
        // For legacy rows, set as 'legacy'
        db.execSQL("UPDATE vehicles SET userId = 'legacy' WHERE userId IS NULL")

        // Add userId column to trips
        db.execSQL("ALTER TABLE trips ADD COLUMN userId TEXT")
        // For legacy rows, set as 'legacy'
        db.execSQL("UPDATE trips SET userId = 'legacy' WHERE userId IS NULL")
    }
}