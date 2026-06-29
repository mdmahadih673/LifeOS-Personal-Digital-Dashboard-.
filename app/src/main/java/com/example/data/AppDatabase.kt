package com.example.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SocialAccountDao {
    @Query("SELECT * FROM social_accounts ORDER BY id DESC")
    fun getAll(): Flow<List<SocialAccount>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(account: SocialAccount)

    @Delete
    suspend fun delete(account: SocialAccount)
}

@Dao
interface ContactDao {
    @Query("SELECT * FROM contacts ORDER BY fullName ASC")
    fun getAll(): Flow<List<Contact>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contact: Contact)

    @Delete
    suspend fun delete(contact: Contact)
}

@Dao
interface BioDao {
    @Query("SELECT * FROM bios ORDER BY id DESC")
    fun getAll(): Flow<List<Bio>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bio: Bio)

    @Delete
    suspend fun delete(bio: Bio)
}

@Dao
interface ProfileAssetDao {
    @Query("SELECT * FROM profile_assets ORDER BY id DESC")
    fun getAll(): Flow<List<ProfileAsset>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(asset: ProfileAsset)

    @Delete
    suspend fun delete(asset: ProfileAsset)
}

@Dao
interface PasswordDao {
    @Query("SELECT * FROM passwords ORDER BY id DESC")
    fun getAll(): Flow<List<PasswordEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(password: PasswordEntry)

    @Delete
    suspend fun delete(password: PasswordEntry)
}

@Dao
interface DocumentDao {
    @Query("SELECT * FROM documents ORDER BY isPinned DESC, id DESC")
    fun getAll(): Flow<List<Document>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(document: Document)

    @Delete
    suspend fun delete(document: Document)
}

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY isPinned DESC, updatedAt DESC")
    fun getAll(): Flow<List<Note>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note)

    @Delete
    suspend fun delete(note: Note)
}

@Dao
interface TodoDao {
    @Query("SELECT * FROM todos ORDER BY isCompleted ASC, priority DESC, id DESC")
    fun getAll(): Flow<List<TodoItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(todo: TodoItem)

    @Delete
    suspend fun delete(todo: TodoItem)
}

@Dao
interface BrandKitDao {
    @Query("SELECT * FROM brand_kit WHERE id = 1 LIMIT 1")
    fun getBrandKitFlow(): Flow<BrandKit?>

    @Query("SELECT * FROM brand_kit WHERE id = 1 LIMIT 1")
    suspend fun getBrandKit(): BrandKit?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(brandKit: BrandKit)
}

@Database(
    entities = [
        SocialAccount::class,
        Contact::class,
        Bio::class,
        ProfileAsset::class,
        PasswordEntry::class,
        Document::class,
        Note::class,
        TodoItem::class,
        BrandKit::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun socialAccountDao(): SocialAccountDao
    abstract fun contactDao(): ContactDao
    abstract fun bioDao(): BioDao
    abstract fun profileAssetDao(): ProfileAssetDao
    abstract fun passwordDao(): PasswordDao
    abstract fun documentDao(): DocumentDao
    abstract fun noteDao(): NoteDao
    abstract fun todoDao(): TodoDao
    abstract fun brandKitDao(): BrandKitDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "life_os_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
