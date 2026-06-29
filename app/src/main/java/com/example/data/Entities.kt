package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "social_accounts")
data class SocialAccount(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val platform: String,
    val username: String,
    val displayName: String,
    val profileUrl: String,
    val bio: String,
    val notes: String,
    val isFavorite: Boolean = false
)

@Entity(tableName = "contacts")
data class Contact(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fullName: String,
    val phoneNumber: String,
    val whatsapp: String = "",
    val telegram: String = "",
    val discord: String = "",
    val email: String = "",
    val businessEmail: String = "",
    val address: String = "",
    val website: String = "",
    val category: String // Personal, Business, Emergency, Family, Friends
)

@Entity(tableName = "bios")
data class Bio(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String, // Instagram Bio, Facebook Bio, GitHub Bio, LinkedIn About, Twitter Bio, Business Bio, Gaming Bio, Portfolio Bio
    val content: String,
    val isFavorite: Boolean = false
)

@Entity(tableName = "profile_assets")
data class ProfileAsset(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val type: String, // Profile Photo, Cover Photo, Logo, Brand Image, QR Code, Watermark, Thumbnail
    val assetUrl: String, // Path or Uri or Placeholder key
    val description: String = ""
)

@Entity(tableName = "passwords")
data class PasswordEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val website: String,
    val username: String,
    val password: String,
    val recoveryEmail: String = "",
    val backupCodes: String = "",
    val tfaNotes: String = ""
)

@Entity(tableName = "documents")
data class Document(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val type: String, // Resume, CV, Certificate, PDF, Image, Video, ZIP, Folder
    val fileUrl: String, // Simulated or real URI
    val folder: String = "Documents", // folder division
    val isPinned: Boolean = false,
    val fileSize: String = "0 KB"
)

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val category: String, // Ideas, Content Ideas, Project Ideas, Business Notes
    val isPinned: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "todos")
data class TodoItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val priority: String, // High, Medium, Low
    val reminder: String = "",
    val dueDate: String = "",
    val isCompleted: Boolean = false
)

@Entity(tableName = "brand_kit")
data class BrandKit(
    @PrimaryKey val id: Int = 1, // Only 1 brand kit row
    val brandLogo: String = "",
    val brandColorsHex: String = "#007AFF,#34C759,#FF9500", // Comma-separated hex list
    val fontsConfig: String = "Inter, SF Pro",
    val socialLinks: String = "",
    val businessInfo: String = ""
)
