package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repository(private val db: AppDatabase) {

    val socialAccounts: Flow<List<SocialAccount>> = db.socialAccountDao().getAll()
    val contacts: Flow<List<Contact>> = db.contactDao().getAll()
    val bios: Flow<List<Bio>> = db.bioDao().getAll()
    val profileAssets: Flow<List<ProfileAsset>> = db.profileAssetDao().getAll()
    val passwords: Flow<List<PasswordEntry>> = db.passwordDao().getAll()
    val documents: Flow<List<Document>> = db.documentDao().getAll()
    val notes: Flow<List<Note>> = db.noteDao().getAll()
    val todos: Flow<List<TodoItem>> = db.todoDao().getAll()
    val brandKit: Flow<BrandKit?> = db.brandKitDao().getBrandKitFlow()

    // Inserts
    suspend fun insertSocialAccount(account: SocialAccount) = withContext(Dispatchers.IO) {
        db.socialAccountDao().insert(account)
    }

    suspend fun insertContact(contact: Contact) = withContext(Dispatchers.IO) {
        db.contactDao().insert(contact)
    }

    suspend fun insertBio(bio: Bio) = withContext(Dispatchers.IO) {
        db.bioDao().insert(bio)
    }

    suspend fun insertProfileAsset(asset: ProfileAsset) = withContext(Dispatchers.IO) {
        db.profileAssetDao().insert(asset)
    }

    suspend fun insertPassword(password: PasswordEntry) = withContext(Dispatchers.IO) {
        db.passwordDao().insert(password)
    }

    suspend fun insertDocument(document: Document) = withContext(Dispatchers.IO) {
        db.documentDao().insert(document)
    }

    suspend fun insertNote(note: Note) = withContext(Dispatchers.IO) {
        db.noteDao().insert(note)
    }

    suspend fun insertTodo(todo: TodoItem) = withContext(Dispatchers.IO) {
        db.todoDao().insert(todo)
    }

    suspend fun insertBrandKit(brandKit: BrandKit) = withContext(Dispatchers.IO) {
        db.brandKitDao().insert(brandKit)
    }

    // Deletes
    suspend fun deleteSocialAccount(account: SocialAccount) = withContext(Dispatchers.IO) {
        db.socialAccountDao().delete(account)
    }

    suspend fun deleteContact(contact: Contact) = withContext(Dispatchers.IO) {
        db.contactDao().delete(contact)
    }

    suspend fun deleteBio(bio: Bio) = withContext(Dispatchers.IO) {
        db.bioDao().delete(bio)
    }

    suspend fun deleteProfileAsset(asset: ProfileAsset) = withContext(Dispatchers.IO) {
        db.profileAssetDao().delete(asset)
    }

    suspend fun deletePassword(password: PasswordEntry) = withContext(Dispatchers.IO) {
        db.passwordDao().delete(password)
    }

    suspend fun deleteDocument(document: Document) = withContext(Dispatchers.IO) {
        db.documentDao().delete(document)
    }

    suspend fun deleteNote(note: Note) = withContext(Dispatchers.IO) {
        db.noteDao().delete(note)
    }

    suspend fun deleteTodo(todo: TodoItem) = withContext(Dispatchers.IO) {
        db.todoDao().delete(todo)
    }

    // Prepopulation of beautiful macOS/Skeuomorphic digital assets & details
    suspend fun prepopulateIfEmpty() = withContext(Dispatchers.IO) {
        val existingSocials = db.socialAccountDao().getAll().firstOrNull()
        if (existingSocials.isNullOrEmpty()) {
            // Social Accounts
            db.socialAccountDao().insert(SocialAccount(
                platform = "GitHub",
                username = "octocat",
                displayName = "The Octocat",
                profileUrl = "https://github.com/octocat",
                bio = "Design enthusiast & code tinker.",
                notes = "Primary developer portfolio account."
            ))
            db.socialAccountDao().insert(SocialAccount(
                platform = "LinkedIn",
                username = "steve-jobs",
                displayName = "Steve Jobs",
                profileUrl = "https://linkedin.com/in/steve-jobs",
                bio = "Innovator, designer, visionary.",
                notes = "Professional business profile."
            ))
            db.socialAccountDao().insert(SocialAccount(
                platform = "X (Twitter)",
                username = "elonmusk",
                displayName = "Elon Musk",
                profileUrl = "https://x.com/elonmusk",
                bio = "Mars & Cars, Chips & Dips",
                notes = "Latest tech thoughts & news."
            ))

            // Contacts
            db.contactDao().insert(Contact(
                fullName = "Craig Federighi",
                phoneNumber = "+1 (555) 019-2831",
                whatsapp = "+15550192831",
                telegram = "@hairforceone",
                discord = "hairforce1#0001",
                email = "craig@apple.com",
                businessEmail = "craig_fed@apple.com",
                address = "Infinite Loop, Cupertino, CA",
                website = "https://www.apple.com",
                category = "Business"
            ))
            db.contactDao().insert(Contact(
                fullName = "John Appleseed",
                phoneNumber = "+1 (555) 555-0199",
                whatsapp = "+15555550199",
                telegram = "@appleseed",
                discord = "appleseed#1234",
                email = "john@appleseed.com",
                category = "Friends"
            ))

            // Bios
            db.bioDao().insert(Bio(
                title = "Instagram Bio",
                content = "✨ Crafting elegant digital experiences.\n Apple Skeuomorphism Lover.\n💻 Mobile Dev @ Cupertino.",
                isFavorite = true
            ))
            db.bioDao().insert(Bio(
                title = "GitHub Bio",
                content = "🚀 Building beautiful Android Applications using Kotlin, Jetpack Compose, and Material 3.\n🛠️ Skeuomorphic Designer.",
                isFavorite = false
            ))

            // Profile Assets
            db.profileAssetDao().insert(ProfileAsset(
                title = "Corporate Headshot 2026",
                type = "Profile Photo",
                assetUrl = "headshot_corp",
                description = "Taken at annual tech summit, light grey clean backdrop."
            ))
            db.profileAssetDao().insert(ProfileAsset(
                title = "LifeOS Main Logo",
                type = "Logo",
                assetUrl = "lifeos_logo",
                description = "Polished silver apple-style 3D logo."
            ))
            db.profileAssetDao().insert(ProfileAsset(
                title = "Personal Portfolio QR",
                type = "QR Code",
                assetUrl = "portfolio_qr",
                description = "Scans directly to website."
            ))

            // Passwords
            db.passwordDao().insert(PasswordEntry(
                website = "https://icloud.com",
                username = "appleseed@me.com",
                password = "••••••••••••••••",
                recoveryEmail = "appleseed_backup@gmail.com",
                backupCodes = "8391-2819-2041",
                tfaNotes = "Authenticator active on iPhone 16 Pro"
            ))

            // Documents
            db.documentDao().insert(Document(
                name = "Professional_CV_2026.pdf",
                type = "PDF",
                fileUrl = "cv_doc",
                folder = "Career",
                isPinned = true,
                fileSize = "1.2 MB"
            ))
            db.documentDao().insert(Document(
                name = "Project_Milestones.zip",
                type = "ZIP",
                fileUrl = "milestones_zip",
                folder = "Projects",
                isPinned = false,
                fileSize = "45.8 MB"
            ))

            // Notes
            db.noteDao().insert(Note(
                title = "Skeuomorphism Principles",
                content = "Key features to build a premium skeuomorphic feel in Compose:\n- Inner inset shadows using custom drawBehind\n- Dual shadow styling: offset light highlight (white) on top-left, offset dark shadow (gray) on bottom-right\n- Soft blur and high-density border curves\n- Metallic gradients and subtle glassmorphic backdrops",
                category = "Ideas",
                isPinned = true
            ))
            db.noteDao().insert(Note(
                title = "Product Launch Plan",
                content = "1. Beta testing with internal team\n2. Design polish based on feedback\n3. Launch landing page\n4. App store submission",
                category = "Project Ideas",
                isPinned = false
            ))

            // Todos
            db.todoDao().insert(TodoItem(
                title = "Polishing Apple-inspired design layers",
                priority = "High",
                reminder = "10:00 AM",
                dueDate = "Today",
                isCompleted = false
            ))
            db.todoDao().insert(TodoItem(
                title = "Complete Brand Kit configuration",
                priority = "Medium",
                reminder = "2:30 PM",
                dueDate = "Tomorrow",
                isCompleted = false
            ))
            db.todoDao().insert(TodoItem(
                title = "Export JSON backup test",
                priority = "Low",
                isCompleted = true
            ))

            // Brand Kit
            db.brandKitDao().insert(BrandKit(
                brandLogo = "LifeOS 3D",
                brandColorsHex = "#007AFF,#34C759,#5856D6,#FF9500",
                fontsConfig = "SF Pro Display, SF Pro Icons",
                socialLinks = "github.com/lifeos, twitter.com/lifeos",
                businessInfo = "LifeOS Corp. Cupertino, CA"
            ))
        }
    }
}
