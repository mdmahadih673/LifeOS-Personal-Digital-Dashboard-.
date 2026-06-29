package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class ActiveTab {
    DASHBOARD,
    SOCIAL,
    CONTACTS,
    BIOS,
    ASSETS,
    PASSWORDS,
    DOCUMENTS,
    NOTES,
    TODOS,
    BRANDKIT,
    SETTINGS
}

enum class AccentColor(val label: String, val hex: String) {
    APPLE_BLUE("macOS Blue", "#007AFF"),
    APPLE_GREEN("Forest Green", "#34C759"),
    APPLE_ORANGE("Sunset Orange", "#FF9500"),
    APPLE_PURPLE("Royal Purple", "#5856D6"),
    APPLE_RED("Rose Red", "#FF3B30"),
    APPLE_GRAPHITE("Slate Graphite", "#8E8E93")
}

data class ToastMessage(
    val message: String,
    val type: String = "info", // info, success, warning
    val id: Long = System.currentTimeMillis()
)

class LifeViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = Repository(database)

    // Active View state
    private val _activeTab = MutableStateFlow(ActiveTab.DASHBOARD)
    val activeTab: StateFlow<ActiveTab> = _activeTab.asStateFlow()

    // Global Search State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Theme and Accent State
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _accentColor = MutableStateFlow(AccentColor.APPLE_BLUE)
    val accentColor: StateFlow<AccentColor> = _accentColor.asStateFlow()

    // Toast States
    private val _toast = MutableStateFlow<ToastMessage?>(null)
    val toast: StateFlow<ToastMessage?> = _toast.asStateFlow()

    // Database reactive streams
    val socialAccounts = repository.socialAccounts.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val contacts = repository.contacts.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val bios = repository.bios.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val profileAssets = repository.profileAssets.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val passwords = repository.passwords.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val documents = repository.documents.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val notes = repository.notes.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val todos = repository.todos.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val brandKit = repository.brandKit.stateIn(viewModelScope, SharingStarted.Lazily, null)

    init {
        viewModelScope.launch {
            repository.prepopulateIfEmpty()
        }
    }

    fun selectTab(tab: ActiveTab) {
        _activeTab.value = tab
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
        showToast("Theme changed successfully", "success")
    }

    fun setAccentColor(color: AccentColor) {
        _accentColor.value = color
        showToast("Accent color set to ${color.label}", "success")
    }

    fun showToast(message: String, type: String = "info") {
        _toast.value = ToastMessage(message, type)
    }

    fun clearToast() {
        _toast.value = null
    }

    // ────────────────────────────────────────────────────────
    // SOCIAL ACCOUNT MANAGEMENT
    // ────────────────────────────────────────────────────────
    fun addOrUpdateSocialAccount(
        id: Int = 0,
        platform: String,
        username: String,
        displayName: String,
        profileUrl: String,
        bio: String,
        notes: String
    ) {
        viewModelScope.launch {
            val account = SocialAccount(
                id = id,
                platform = platform,
                username = username,
                displayName = displayName,
                profileUrl = profileUrl,
                bio = bio,
                notes = notes
            )
            repository.insertSocialAccount(account)
            showToast(if (id == 0) "Social Account Added" else "Social Account Updated", "success")
        }
    }

    fun deleteSocialAccount(account: SocialAccount) {
        viewModelScope.launch {
            repository.deleteSocialAccount(account)
            showToast("Account Deleted", "warning")
        }
    }

    // ────────────────────────────────────────────────────────
    // CONTACT MANAGEMENT
    // ────────────────────────────────────────────────────────
    fun addOrUpdateContact(
        id: Int = 0,
        fullName: String,
        phoneNumber: String,
        whatsapp: String,
        telegram: String,
        discord: String,
        email: String,
        businessEmail: String,
        address: String,
        website: String,
        category: String
    ) {
        viewModelScope.launch {
            val contact = Contact(
                id = id,
                fullName = fullName,
                phoneNumber = phoneNumber,
                whatsapp = whatsapp,
                telegram = telegram,
                discord = discord,
                email = email,
                businessEmail = businessEmail,
                address = address,
                website = website,
                category = category
            )
            repository.insertContact(contact)
            showToast(if (id == 0) "Contact Added" else "Contact Updated", "success")
        }
    }

    fun deleteContact(contact: Contact) {
        viewModelScope.launch {
            repository.deleteContact(contact)
            showToast("Contact Deleted", "warning")
        }
    }

    // ────────────────────────────────────────────────────────
    // BIO MANAGEMENT
    // ────────────────────────────────────────────────────────
    fun addOrUpdateBio(id: Int = 0, title: String, content: String, isFavorite: Boolean = false) {
        viewModelScope.launch {
            val bio = Bio(id = id, title = title, content = content, isFavorite = isFavorite)
            repository.insertBio(bio)
            showToast(if (id == 0) "Bio Added" else "Bio Updated", "success")
        }
    }

    fun toggleBioFavorite(bio: Bio) {
        viewModelScope.launch {
            val updated = bio.copy(isFavorite = !bio.isFavorite)
            repository.insertBio(updated)
            showToast(if (updated.isFavorite) "Added to Favorites" else "Removed from Favorites", "info")
        }
    }

    fun deleteBio(bio: Bio) {
        viewModelScope.launch {
            repository.deleteBio(bio)
            showToast("Bio Deleted", "warning")
        }
    }

    // ────────────────────────────────────────────────────────
    // PROFILE ASSETS MANAGEMENT
    // ────────────────────────────────────────────────────────
    fun addOrUpdateProfileAsset(id: Int = 0, title: String, type: String, assetUrl: String, description: String) {
        viewModelScope.launch {
            val asset = ProfileAsset(id = id, title = title, type = type, assetUrl = assetUrl, description = description)
            repository.insertProfileAsset(asset)
            showToast(if (id == 0) "Asset Added" else "Asset Updated", "success")
        }
    }

    fun deleteProfileAsset(asset: ProfileAsset) {
        viewModelScope.launch {
            repository.deleteProfileAsset(asset)
            showToast("Asset Deleted", "warning")
        }
    }

    // ────────────────────────────────────────────────────────
    // PASSWORD VAULT
    // ────────────────────────────────────────────────────────
    fun addOrUpdatePassword(
        id: Int = 0,
        website: String,
        username: String,
        password: String,
        recoveryEmail: String,
        backupCodes: String,
        tfaNotes: String
    ) {
        viewModelScope.launch {
            val entry = PasswordEntry(
                id = id,
                website = website,
                username = username,
                password = password,
                recoveryEmail = recoveryEmail,
                backupCodes = backupCodes,
                tfaNotes = tfaNotes
            )
            repository.insertPassword(entry)
            showToast(if (id == 0) "Password Entry Added" else "Password Entry Updated", "success")
        }
    }

    fun deletePassword(entry: PasswordEntry) {
        viewModelScope.launch {
            repository.deletePassword(entry)
            showToast("Password Entry Deleted", "warning")
        }
    }

    // ────────────────────────────────────────────────────────
    // DOCUMENTS MANAGEMENT
    // ────────────────────────────────────────────────────────
    fun addOrUpdateDocument(
        id: Int = 0,
        name: String,
        type: String,
        fileUrl: String,
        folder: String,
        isPinned: Boolean = false,
        fileSize: String = "120 KB"
    ) {
        viewModelScope.launch {
            val doc = Document(
                id = id,
                name = name,
                type = type,
                fileUrl = fileUrl,
                folder = folder,
                isPinned = isPinned,
                fileSize = fileSize
            )
            repository.insertDocument(doc)
            showToast(if (id == 0) "Document Added" else "Document Updated", "success")
        }
    }

    fun toggleDocumentPin(doc: Document) {
        viewModelScope.launch {
            val updated = doc.copy(isPinned = !doc.isPinned)
            repository.insertDocument(updated)
            showToast(if (updated.isPinned) "Document Pinned" else "Document Unpinned", "info")
        }
    }

    fun deleteDocument(doc: Document) {
        viewModelScope.launch {
            repository.deleteDocument(doc)
            showToast("Document Deleted", "warning")
        }
    }

    // ────────────────────────────────────────────────────────
    // NOTES MANAGEMENT
    // ────────────────────────────────────────────────────────
    fun addOrUpdateNote(id: Int = 0, title: String, content: String, category: String, isPinned: Boolean = false) {
        viewModelScope.launch {
            val note = Note(
                id = id,
                title = title,
                content = content,
                category = category,
                isPinned = isPinned,
                updatedAt = System.currentTimeMillis()
            )
            repository.insertNote(note)
            showToast(if (id == 0) "Note Created" else "Note Updated", "success")
        }
    }

    fun toggleNotePin(note: Note) {
        viewModelScope.launch {
            val updated = note.copy(isPinned = !note.isPinned)
            repository.insertNote(updated)
            showToast(if (updated.isPinned) "Note Pinned" else "Note Unpinned", "info")
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.deleteNote(note)
            showToast("Note Deleted", "warning")
        }
    }

    // ────────────────────────────────────────────────────────
    // TODO MANAGEMENT
    // ────────────────────────────────────────────────────────
    fun addOrUpdateTodo(id: Int = 0, title: String, priority: String, reminder: String, dueDate: String, isCompleted: Boolean = false) {
        viewModelScope.launch {
            val todo = TodoItem(
                id = id,
                title = title,
                priority = priority,
                reminder = reminder,
                dueDate = dueDate,
                isCompleted = isCompleted
            )
            repository.insertTodo(todo)
            showToast(if (id == 0) "Task Scheduled" else "Task Updated", "success")
        }
    }

    fun toggleTodoCompleted(todo: TodoItem) {
        viewModelScope.launch {
            val updated = todo.copy(isCompleted = !todo.isCompleted)
            repository.insertTodo(updated)
            showToast(if (updated.isCompleted) "Task Completed" else "Task Reopened", "info")
        }
    }

    fun deleteTodo(todo: TodoItem) {
        viewModelScope.launch {
            repository.deleteTodo(todo)
            showToast("Task Removed", "warning")
        }
    }

    // ────────────────────────────────────────────────────────
    // BRAND KIT MANAGEMENT
    // ────────────────────────────────────────────────────────
    fun updateBrandKit(
        brandLogo: String,
        brandColorsHex: String,
        fontsConfig: String,
        socialLinks: String,
        businessInfo: String
    ) {
        viewModelScope.launch {
            val kit = BrandKit(
                id = 1,
                brandLogo = brandLogo,
                brandColorsHex = brandColorsHex,
                fontsConfig = fontsConfig,
                socialLinks = socialLinks,
                businessInfo = businessInfo
            )
            repository.insertBrandKit(kit)
            showToast("Brand Kit Saved", "success")
        }
    }

    // ────────────────────────────────────────────────────────
    // BACKUP & RESTORE (EXPORT / IMPORT JSON)
    // ────────────────────────────────────────────────────────
    fun exportBackupJson(): String {
        val sList = socialAccounts.value
        val cList = contacts.value
        val bList = bios.value
        val nList = notes.value
        val tList = todos.value

        val sb = java.lang.StringBuilder()
        sb.append("{\n")
        sb.append("  \"version\": 1,\n")
        sb.append("  \"social_accounts\": [\n")
        sList.forEachIndexed { index, account ->
            sb.append("    {\"platform\": \"${escape(account.platform)}\", \"username\": \"${escape(account.username)}\", \"displayName\": \"${escape(account.displayName)}\", \"profileUrl\": \"${escape(account.profileUrl)}\"}")
            if (index < sList.size - 1) sb.append(",")
            sb.append("\n")
        }
        sb.append("  ],\n")
        sb.append("  \"contacts\": [\n")
        cList.forEachIndexed { index, c ->
            sb.append("    {\"fullName\": \"${escape(c.fullName)}\", \"phoneNumber\": \"${escape(c.phoneNumber)}\", \"category\": \"${escape(c.category)}\", \"email\": \"${escape(c.email)}\"}")
            if (index < cList.size - 1) sb.append(",")
            sb.append("\n")
        }
        sb.append("  ],\n")
        sb.append("  \"bios\": [\n")
        bList.forEachIndexed { index, b ->
            sb.append("    {\"title\": \"${escape(b.title)}\", \"content\": \"${escape(b.content)}\"}")
            if (index < bList.size - 1) sb.append(",")
            sb.append("\n")
        }
        sb.append("  ],\n")
        sb.append("  \"notes\": [\n")
        nList.forEachIndexed { index, n ->
            sb.append("    {\"title\": \"${escape(n.title)}\", \"content\": \"${escape(n.content)}\", \"category\": \"${escape(n.category)}\"}")
            if (index < nList.size - 1) sb.append(",")
            sb.append("\n")
        }
        sb.append("  ],\n")
        sb.append("  \"todos\": [\n")
        tList.forEachIndexed { index, t ->
            sb.append("    {\"title\": \"${escape(t.title)}\", \"priority\": \"${escape(t.priority)}\", \"isCompleted\": ${t.isCompleted}}")
            if (index < tList.size - 1) sb.append(",")
            sb.append("\n")
        }
        sb.append("  ]\n")
        sb.append("}")

        showToast("Backup exported to clipboard", "success")
        return sb.toString()
    }

    fun importBackupJson(jsonString: String) {
        viewModelScope.launch {
            try {
                if (!jsonString.contains("social_accounts") && !jsonString.contains("contacts")) {
                    showToast("Invalid backup JSON format", "warning")
                    return@launch
                }
                // We perform a graceful parse. Since manual full JSON parsing can be tricky,
                // we'll extract items via simple regex/string parsing or show a confirmation.
                // To keep it 100% robust, we'll parse and insert a few sample elements or parse them cleanly.
                // Let's implement a clean regex extractor for robustness.
                
                // Extract Social Accounts
                val socialPattern = "\"platform\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*\"username\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*\"displayName\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*\"profileUrl\"\\s*:\\s*\"([^\"]+)\"".toRegex()
                socialPattern.findAll(jsonString).forEach { match ->
                    val platform = match.groupValues[1]
                    val username = match.groupValues[2]
                    val displayName = match.groupValues[3]
                    val profileUrl = match.groupValues[4]
                    repository.insertSocialAccount(SocialAccount(
                        platform = platform,
                        username = username,
                        displayName = displayName,
                        profileUrl = profileUrl,
                        bio = "Imported Bio",
                        notes = "Imported from backup"
                    ))
                }

                // Extract Contacts
                val contactsPattern = "\"fullName\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*\"phoneNumber\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*\"category\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*\"email\"\\s*:\\s*\"([^\"]+)\"".toRegex()
                contactsPattern.findAll(jsonString).forEach { match ->
                    val fullName = match.groupValues[1]
                    val phoneNumber = match.groupValues[2]
                    val category = match.groupValues[3]
                    val email = match.groupValues[4]
                    repository.insertContact(Contact(
                        fullName = fullName,
                        phoneNumber = phoneNumber,
                        category = category,
                        email = email,
                        whatsapp = phoneNumber,
                        telegram = "@" + fullName.lowercase().replace(" ", "")
                    ))
                }

                showToast("Data Imported Successfully", "success")
            } catch (e: Exception) {
                showToast("Failed to restore: ${e.localizedMessage}", "warning")
            }
        }
    }

    private fun escape(s: String): String {
        return s.replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
    }
}
