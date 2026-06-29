@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.ui.*
import com.example.ui.components.*
import com.example.ui.theme.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LifeOSMainApp(viewModel: LifeViewModel) {
    val context = LocalContext.current
    val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
    val accentColor by viewModel.accentColor.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toast.collectAsStateWithLifecycle()

    val primaryColor = Color(android.graphics.Color.parseColor(accentColor.hex))

    // Responsive configuration
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600

    // Modals visibility states
    var showAddSocialDialog by remember { mutableStateOf(false) }
    var editingSocial by remember { mutableStateOf<SocialAccount?>(null) }

    var showAddContactDialog by remember { mutableStateOf(false) }
    var editingContact by remember { mutableStateOf<Contact?>(null) }

    var showAddBioDialog by remember { mutableStateOf(false) }
    var editingBio by remember { mutableStateOf<Bio?>(null) }

    var showAddAssetDialog by remember { mutableStateOf(false) }
    var editingAsset by remember { mutableStateOf<ProfileAsset?>(null) }

    var showAddPasswordDialog by remember { mutableStateOf(false) }
    var editingPassword by remember { mutableStateOf<PasswordEntry?>(null) }

    var showAddDocumentDialog by remember { mutableStateOf(false) }
    var editingDocument by remember { mutableStateOf<Document?>(null) }

    var showAddNoteDialog by remember { mutableStateOf(false) }
    var editingNote by remember { mutableStateOf<Note?>(null) }

    var showAddTodoDialog by remember { mutableStateOf(false) }
    var editingTodo by remember { mutableStateOf<TodoItem?>(null) }

    // Dialog state handlers
    val openAddSocial: (SocialAccount?) -> Unit = {
        editingSocial = it
        showAddSocialDialog = true
    }
    val openAddContact: (Contact?) -> Unit = {
        editingContact = it
        showAddContactDialog = true
    }
    val openAddBio: (Bio?) -> Unit = {
        editingBio = it
        showAddBioDialog = true
    }
    val openAddAsset: (ProfileAsset?) -> Unit = {
        editingAsset = it
        showAddAssetDialog = true
    }
    val openAddPassword: (PasswordEntry?) -> Unit = {
        editingPassword = it
        showAddPasswordDialog = true
    }
    val openAddDocument: (Document?) -> Unit = {
        editingDocument = it
        showAddDocumentDialog = true
    }
    val openAddNote: (Note?) -> Unit = {
        editingNote = it
        showAddNoteDialog = true
    }
    val openAddTodo: (TodoItem?) -> Unit = {
        editingTodo = it
        showAddTodoDialog = true
    }

    MyApplicationTheme(darkTheme = isDarkMode, accentColor = primaryColor) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Main split navigation or mobile layout
                if (isTablet) {
                    // Tablet Layout: Classic macOS split design
                    Row(modifier = Modifier.fillMaxSize()) {
                        // Left Sidebar
                        SidebarView(
                            activeTab = activeTab,
                            onTabSelected = { viewModel.selectTab(it) },
                            isDarkMode = isDarkMode,
                            toggleDarkMode = { viewModel.toggleDarkMode() },
                            accentColor = accentColor,
                            modifier = Modifier
                                .width(250.dp)
                                .fillMaxHeight()
                        )

                        // Main content
                        Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                            Column(modifier = Modifier.fillMaxSize()) {
                                MacOSTitleBar(
                                    title = "LifeOS - " + activeTab.name.lowercase()
                                        .replaceFirstChar { it.uppercase() },
                                    onMinimizeClick = { viewModel.showToast("Window minimized to dock", "info") },
                                    onMaximizeClick = { viewModel.showToast("Expanded layout", "success") },
                                    onCloseClick = { viewModel.showToast("App cannot be closed", "warning") }
                                )

                                // Global Header Search bar
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 24.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    SkeuomorphicInsetField(
                                        value = searchQuery,
                                        onValueChange = { viewModel.setSearchQuery(it) },
                                        placeholder = "Search across LifeOS...",
                                        modifier = Modifier.weight(1f),
                                        leadingIcon = {
                                            Icon(
                                                Icons.Default.Search,
                                                contentDescription = "Search",
                                                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                            )
                                        }
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    SkeuomorphicButton(
                                        onClick = {
                                            when (activeTab) {
                                                ActiveTab.SOCIAL -> openAddSocial(null)
                                                ActiveTab.CONTACTS -> openAddContact(null)
                                                ActiveTab.BIOS -> openAddBio(null)
                                                ActiveTab.ASSETS -> openAddAsset(null)
                                                ActiveTab.PASSWORDS -> openAddPassword(null)
                                                ActiveTab.DOCUMENTS -> openAddDocument(null)
                                                ActiveTab.NOTES -> openAddNote(null)
                                                ActiveTab.TODOS -> openAddTodo(null)
                                                else -> viewModel.showToast("Please use designated managers to add items", "info")
                                            }
                                        }
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = "Add")
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Create New", fontWeight = FontWeight.SemiBold)
                                    }
                                }

                                Divider(
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f),
                                    thickness = 1.dp
                                )

                                Box(modifier = Modifier.weight(1f).padding(20.dp)) {
                                    MainViewContent(
                                        activeTab = activeTab,
                                        viewModel = viewModel,
                                        searchQuery = searchQuery,
                                        openAddSocial = openAddSocial,
                                        openAddContact = openAddContact,
                                        openAddBio = openAddBio,
                                        openAddAsset = openAddAsset,
                                        openAddPassword = openAddPassword,
                                        openAddDocument = openAddDocument,
                                        openAddNote = openAddNote,
                                        openAddTodo = openAddTodo
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // Mobile Layout: Phone Navigation style
                    Column(modifier = Modifier.fillMaxSize()) {
                        MacOSTitleBar(
                            title = "LifeOS",
                            onCloseClick = {},
                            onMinimizeClick = {},
                            onMaximizeClick = {}
                        )

                        // Mobile Search Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SkeuomorphicInsetField(
                                value = searchQuery,
                                onValueChange = { viewModel.setSearchQuery(it) },
                                placeholder = "Search LifeOS...",
                                modifier = Modifier.weight(1f),
                                leadingIcon = {
                                    Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Gray)
                                }
                            )
                        }

                        // Screen Content Area
                        Box(modifier = Modifier.weight(1f).padding(horizontal = 12.dp)) {
                            MainViewContent(
                                activeTab = activeTab,
                                viewModel = viewModel,
                                searchQuery = searchQuery,
                                openAddSocial = openAddSocial,
                                openAddContact = openAddContact,
                                openAddBio = openAddBio,
                                openAddAsset = openAddAsset,
                                openAddPassword = openAddPassword,
                                openAddDocument = openAddDocument,
                                openAddNote = openAddNote,
                                openAddTodo = openAddTodo
                            )
                        }

                        // Mobile Bottom Navigation Bar - Bento Capsule Style
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                                .fillMaxWidth()
                                .height(68.dp)
                                .skeuomorphicShadow(cornerRadius = 34.dp, offset = 6.dp, blur = 10.dp)
                                .background(
                                    MaterialTheme.colorScheme.background,
                                    RoundedCornerShape(34.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.04f),
                                    shape = RoundedCornerShape(34.dp)
                                )
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                BottomTabButton(
                                    icon = Icons.Default.Home,
                                    label = "Home",
                                    selected = activeTab == ActiveTab.DASHBOARD,
                                    onClick = { viewModel.selectTab(ActiveTab.DASHBOARD) }
                                )
                                BottomTabButton(
                                    icon = Icons.Default.Share,
                                    label = "Socials",
                                    selected = activeTab == ActiveTab.SOCIAL,
                                    onClick = { viewModel.selectTab(ActiveTab.SOCIAL) }
                                )
                                BottomTabButton(
                                    icon = Icons.Default.Contacts,
                                    label = "Contacts",
                                    selected = activeTab == ActiveTab.CONTACTS,
                                    onClick = { viewModel.selectTab(ActiveTab.CONTACTS) }
                                )
                                BottomTabButton(
                                    icon = Icons.Default.Notes,
                                    label = "Notes",
                                    selected = activeTab == ActiveTab.NOTES,
                                    onClick = { viewModel.selectTab(ActiveTab.NOTES) }
                                )
                                BottomTabButton(
                                    icon = Icons.Default.Settings,
                                    label = "Settings",
                                    selected = activeTab == ActiveTab.SETTINGS,
                                    onClick = { viewModel.selectTab(ActiveTab.SETTINGS) }
                                )
                            }
                        }
                    }
                }

                // Apple-inspired Floating Action Button (for quick access to launchpad/creation)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = if (isTablet) 32.dp else 80.dp, end = 24.dp)
                ) {
                    SkeuomorphicButton(
                        onClick = {
                            viewModel.selectTab(ActiveTab.DASHBOARD)
                            viewModel.showToast("Returned to Launch Dashboard", "success")
                        },
                        cornerRadius = 28.dp,
                        elevation = 8.dp,
                        backgroundColor = primaryColor,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(
                            Icons.Default.Widgets,
                            contentDescription = "Home Launch",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                // Custom macOS Glassmorphic Toast Notification
                toastMessage?.let { msg ->
                    LaunchedEffect(msg.id) {
                        delay(2500)
                        viewModel.clearToast()
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 64.dp)
                            .align(Alignment.TopCenter),
                        contentAlignment = Alignment.Center
                    ) {
                        SkeuomorphicCard(
                            cornerRadius = 14.dp,
                            elevation = 8.dp,
                            backgroundColor = if (isDarkMode) Color(0xCC2A2A2A) else Color(0xCCE6F0FA),
                            modifier = Modifier
                                .wrapContentWidth()
                                .height(56.dp)
                                .padding(horizontal = 24.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .fillMaxHeight(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                val tintColor = when (msg.type) {
                                    "success" -> AppleGreen
                                    "warning" -> AppleRed
                                    else -> AppleBlue
                                }
                                Icon(
                                    imageVector = when (msg.type) {
                                        "success" -> Icons.Default.CheckCircle
                                        "warning" -> Icons.Default.Warning
                                        else -> Icons.Default.Info
                                    },
                                    contentDescription = msg.type,
                                    tint = tintColor,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = msg.message,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // ────────────────────────────────────────────────────────
        // DIALOGS & BOTTOM SHEETS
        // ────────────────────────────────────────────────────────
        if (showAddSocialDialog) {
            AddSocialAccountDialog(
                editing = editingSocial,
                onDismiss = { showAddSocialDialog = false },
                onConfirm = { platform, username, dName, pUrl, bio, notes ->
                    viewModel.addOrUpdateSocialAccount(
                        id = editingSocial?.id ?: 0,
                        platform = platform,
                        username = username,
                        displayName = dName,
                        profileUrl = pUrl,
                        bio = bio,
                        notes = notes
                    )
                    showAddSocialDialog = false
                }
            )
        }

        if (showAddContactDialog) {
            AddContactDialog(
                editing = editingContact,
                onDismiss = { showAddContactDialog = false },
                onConfirm = { name, phone, wa, tg, dc, email, bEmail, addr, web, cat ->
                    viewModel.addOrUpdateContact(
                        id = editingContact?.id ?: 0,
                        fullName = name,
                        phoneNumber = phone,
                        whatsapp = wa,
                        telegram = tg,
                        discord = dc,
                        email = email,
                        businessEmail = bEmail,
                        address = addr,
                        website = web,
                        category = cat
                    )
                    showAddContactDialog = false
                }
            )
        }

        if (showAddBioDialog) {
            AddBioDialog(
                editing = editingBio,
                onDismiss = { showAddBioDialog = false },
                onConfirm = { title, content ->
                    viewModel.addOrUpdateBio(
                        id = editingBio?.id ?: 0,
                        title = title,
                        content = content,
                        isFavorite = editingBio?.isFavorite ?: false
                    )
                    showAddBioDialog = false
                }
            )
        }

        if (showAddAssetDialog) {
            AddAssetDialog(
                editing = editingAsset,
                onDismiss = { showAddAssetDialog = false },
                onConfirm = { title, type, url, desc ->
                    viewModel.addOrUpdateProfileAsset(
                        id = editingAsset?.id ?: 0,
                        title = title,
                        type = type,
                        assetUrl = url,
                        description = desc
                    )
                    showAddAssetDialog = false
                }
            )
        }

        if (showAddPasswordDialog) {
            AddPasswordDialog(
                editing = editingPassword,
                onDismiss = { showAddPasswordDialog = false },
                onConfirm = { site, user, pass, recovery, codes, notes ->
                    viewModel.addOrUpdatePassword(
                        id = editingPassword?.id ?: 0,
                        website = site,
                        username = user,
                        password = pass,
                        recoveryEmail = recovery,
                        backupCodes = codes,
                        tfaNotes = notes
                    )
                    showAddPasswordDialog = false
                }
            )
        }

        if (showAddDocumentDialog) {
            AddDocumentDialog(
                editing = editingDocument,
                onDismiss = { showAddDocumentDialog = false },
                onConfirm = { name, type, url, folder, isPinned, size ->
                    viewModel.addOrUpdateDocument(
                        id = editingDocument?.id ?: 0,
                        name = name,
                        type = type,
                        fileUrl = url,
                        folder = folder,
                        isPinned = isPinned,
                        fileSize = size
                    )
                    showAddDocumentDialog = false
                }
            )
        }

        if (showAddNoteDialog) {
            AddNoteDialog(
                editing = editingNote,
                onDismiss = { showAddNoteDialog = false },
                onConfirm = { title, content, cat, isPinned ->
                    viewModel.addOrUpdateNote(
                        id = editingNote?.id ?: 0,
                        title = title,
                        content = content,
                        category = cat,
                        isPinned = isPinned
                    )
                    showAddNoteDialog = false
                }
            )
        }

        if (showAddTodoDialog) {
            AddTodoDialog(
                editing = editingTodo,
                onDismiss = { showAddTodoDialog = false },
                onConfirm = { title, priority, reminder, due, isCompleted ->
                    viewModel.addOrUpdateTodo(
                        id = editingTodo?.id ?: 0,
                        title = title,
                        priority = priority,
                        reminder = reminder,
                        dueDate = due,
                        isCompleted = isCompleted
                    )
                    showAddTodoDialog = false
                }
            )
        }
    }
}

// ────────────────────────────────────────────────────────
// SIDEBAR COMPOSTION
// ────────────────────────────────────────────────────────
@Composable
fun SidebarView(
    activeTab: ActiveTab,
    onTabSelected: (ActiveTab) -> Unit,
    isDarkMode: Boolean,
    toggleDarkMode: () -> Unit,
    accentColor: AccentColor,
    modifier: Modifier = Modifier
) {
    val sidebarBg = if (isDarkMode) MacOSDarkSidebar else MacOSLightSidebar

    Column(
        modifier = modifier
            .background(sidebarBg)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f)
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            // Apple macOS Window Controls
            Row(
                modifier = Modifier.padding(bottom = 24.dp, start = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(AppleBlue, ApplePurple)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Widgets,
                        contentDescription = "Logo",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "LifeOS",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Personal Dashboard",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                }
            }

            // Tab Navigation Menu
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val menuItems = listOf(
                    Triple(ActiveTab.DASHBOARD, Icons.Default.Dashboard, "Dashboard"),
                    Triple(ActiveTab.SOCIAL, Icons.Default.Share, "Social Accounts"),
                    Triple(ActiveTab.CONTACTS, Icons.Default.Contacts, "Contacts"),
                    Triple(ActiveTab.BIOS, Icons.Default.Article, "Bio Manager"),
                    Triple(ActiveTab.ASSETS, Icons.Default.Image, "Profile Assets"),
                    Triple(ActiveTab.PASSWORDS, Icons.Default.Lock, "Password Vault"),
                    Triple(ActiveTab.DOCUMENTS, Icons.Default.Folder, "Documents"),
                    Triple(ActiveTab.NOTES, Icons.Default.Notes, "Rich Notes"),
                    Triple(ActiveTab.TODOS, Icons.Default.Checklist, "Todo Manager"),
                    Triple(ActiveTab.BRANDKIT, Icons.Default.ColorLens, "Brand Kit"),
                    Triple(ActiveTab.SETTINGS, Icons.Default.Settings, "Settings")
                )

                items(menuItems) { (tab, icon, label) ->
                    val isSelected = activeTab == tab
                    val activeBg = Color(android.graphics.Color.parseColor(accentColor.hex)).copy(alpha = 0.15f)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) activeBg else Color.Transparent)
                            .clickable { onTabSelected(tab) }
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            icon,
                            contentDescription = label,
                            tint = if (isSelected) Color(android.graphics.Color.parseColor(accentColor.hex)) else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = label,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp,
                            color = if (isSelected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        // Sidebar Footer options
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Dark Mode Quick Toggle skeuomorphic style
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isDarkMode) MacOSDarkCard else MacOSLightCard)
                    .clickable { toggleDarkMode() }
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                        contentDescription = "Theme",
                        tint = if (isDarkMode) ApplePurple else AppleOrange,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isDarkMode) "Dark Theme" else "Light Theme",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Switch(
                    checked = isDarkMode,
                    onCheckedChange = { toggleDarkMode() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = ApplePurple
                    ),
                    modifier = Modifier.scale(0.8f)
                )
            }

            // Quick copyright
            Text(
                text = " Apple-inspired LifeOS 2026",
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
            )
        }
    }
}

// ────────────────────────────────────────────────────────
// BOTTOM MOBILE NAV BUTTON
// ────────────────────────────────────────────────────────
@Composable
fun BottomTabButton(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val bgModifier = if (selected) {
        Modifier
            .skeuomorphicInset(cornerRadius = 14.dp, depth = 1.5.dp)
            .background(
                if (isDark) MacOSDarkSidebar else MacOSLightSidebar,
                RoundedCornerShape(14.dp)
            )
    } else {
        Modifier
    }

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .then(bgModifier)
            .clickable(
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            )
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            icon,
            contentDescription = label,
            tint = if (selected) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.6f),
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = label,
            fontSize = 9.sp,
            color = if (selected) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.6f),
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

// ────────────────────────────────────────────────────────
// ROUTING DISPATCHER FOR SCREENS
// ────────────────────────────────────────────────────────
@Composable
fun MainViewContent(
    activeTab: ActiveTab,
    viewModel: LifeViewModel,
    searchQuery: String,
    openAddSocial: (SocialAccount?) -> Unit,
    openAddContact: (Contact?) -> Unit,
    openAddBio: (Bio?) -> Unit,
    openAddAsset: (ProfileAsset?) -> Unit,
    openAddPassword: (PasswordEntry?) -> Unit,
    openAddDocument: (Document?) -> Unit,
    openAddNote: (Note?) -> Unit,
    openAddTodo: (TodoItem?) -> Unit
) {
    when (activeTab) {
        ActiveTab.DASHBOARD -> DashboardScreen(viewModel)
        ActiveTab.SOCIAL -> SocialAccountsScreen(viewModel, searchQuery, openAddSocial)
        ActiveTab.CONTACTS -> ContactsScreen(viewModel, searchQuery, openAddContact)
        ActiveTab.BIOS -> BiosScreen(viewModel, searchQuery, openAddBio)
        ActiveTab.ASSETS -> AssetsScreen(viewModel, searchQuery, openAddAsset)
        ActiveTab.PASSWORDS -> PasswordVaultScreen(viewModel, searchQuery, openAddPassword)
        ActiveTab.DOCUMENTS -> DocumentsScreen(viewModel, searchQuery, openAddDocument)
        ActiveTab.NOTES -> NotesScreen(viewModel, searchQuery, openAddNote)
        ActiveTab.TODOS -> TodoScreen(viewModel, searchQuery, openAddTodo)
        ActiveTab.BRANDKIT -> BrandKitScreen(viewModel)
        ActiveTab.SETTINGS -> SettingsScreen(viewModel)
    }
}

// ────────────────────────────────────────────────────────
// Helper modifier scale
// ────────────────────────────────────────────────────────
fun Modifier.scale(scale: Float): Modifier = graphicsLayer(scaleX = scale, scaleY = scale)

// ────────────────────────────────────────────────────────
// 1. HOME DASHBOARD SCREEN
// ────────────────────────────────────────────────────────
@Composable
fun DashboardScreen(viewModel: LifeViewModel) {
    val context = LocalContext.current
    val socialAccounts by viewModel.socialAccounts.collectAsStateWithLifecycle()
    val todos by viewModel.todos.collectAsStateWithLifecycle()
    val notes by viewModel.notes.collectAsStateWithLifecycle()
    val brandKit by viewModel.brandKit.collectAsStateWithLifecycle()

    // Real-time Date and Time
    var currentTimeString by remember { mutableStateOf("") }
    var currentDateString by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        while (true) {
            val cal = Calendar.getInstance()
            currentTimeString = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(cal.time)
            currentDateString = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault()).format(cal.time)
            delay(1000)
        }
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Welcome Header Widget
        item {
            SkeuomorphicCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.95f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(verticalArrangement = Arrangement.Center) {
                        Text(
                            text = "Welcome Back, Creator!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentDateString,
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Text(
                            text = "Status: Online | Storage Status: 42% Free",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                    Apple3DIcon(emoji = "", size = 64.dp)
                }
            }
        }

        // Real-time Dynamic Clock & Quick Stats Card
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // macOS widget Clock
                SkeuomorphicCard(
                    modifier = Modifier
                        .weight(1f)
                        .height(120.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "CURRENT TIME",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentTimeString,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Storage usage widget
                SkeuomorphicCard(
                    modifier = Modifier
                        .weight(1f)
                        .height(120.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "SYSTEM STORAGE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = 0.58f,
                            color = AppleBlue,
                            trackColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp))
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "58.2 GB of 128 GB Used (45.8 MB ZIP cache)",
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Quick Launch Pad (One-click External Launches)
        item {
            Text(
                "QUICK LAUNCHPAD",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                modifier = Modifier.padding(start = 4.dp)
            )

            val launchers = listOf(
                LaunchpadItem("Facebook", "https://facebook.com", "📘"),
                LaunchpadItem("Instagram", "https://instagram.com", "📸"),
                LaunchpadItem("YouTube", "https://youtube.com", "🎥"),
                LaunchpadItem("GitHub", "https://github.com", "💻"),
                LaunchpadItem("LinkedIn", "https://linkedin.com", "💼"),
                LaunchpadItem("WhatsApp", "https://whatsapp.com", "💬"),
                LaunchpadItem("Telegram", "https://telegram.org", "✈️"),
                LaunchpadItem("Discord", "https://discord.com", "👾"),
                LaunchpadItem("Gmail", "https://mail.google.com", "✉️"),
                LaunchpadItem("ChatGPT", "https://chatgpt.com", "🤖")
            )

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                launchers.forEach { launcher ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(launcher.url))
                                    context.startActivity(intent)
                                    viewModel.showToast("Opening ${launcher.name}", "success")
                                } catch (e: Exception) {
                                    viewModel.showToast("Cannot launch: ${e.localizedMessage}", "warning")
                                }
                            }
                            .width(68.dp)
                    ) {
                        Apple3DIcon(emoji = launcher.emoji, size = 52.dp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = launcher.name,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // Quick Copy Center
        item {
            SkeuomorphicCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "QUICK COPY CENTER",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    val copyItems = listOf(
                        Pair("My Primary Email", "john.appleseed@icloud.com"),
                        Pair("Business Phone", "+1 (555) 555-0199"),
                        Pair("Personal Website", "https://appleseed.design"),
                        Pair("GitHub Handle", "github.com/appleseed"),
                        Pair("LinkedIn Profile", "linkedin.com/in/john-appleseed")
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        copyItems.forEach { (label, value) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.04f))
                                    .clickable {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("LifeOS Copy", value)
                                        clipboard.setPrimaryClip(clip)
                                        viewModel.showToast("Copied: $value", "success")
                                    }
                                    .padding(horizontal = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                                    Text(value, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }

        // Quick Tasks & Note Overview Row
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Pending tasks summary
                SkeuomorphicCard(
                    modifier = Modifier
                        .weight(1f)
                        .height(180.dp)
                ) {
                    val pending = todos.filter { !it.isCompleted }
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "TASKS OVERVIEW",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.weight(1f).padding(vertical = 4.dp)
                        ) {
                            if (pending.isEmpty()) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text("All completed! 🎉", fontSize = 12.sp, color = Color.Gray)
                                }
                            } else {
                                pending.take(3).forEach { todo ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.RadioButtonUnchecked,
                                            contentDescription = "Todo",
                                            tint = AppleBlue,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            todo.title,
                                            fontSize = 12.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }

                        Text(
                            "${pending.size} pending daily tasks left",
                            fontSize = 10.sp,
                            color = AppleBlue,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Brand Quick Check
                SkeuomorphicCard(
                    modifier = Modifier
                        .weight(1f)
                        .height(180.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "BRAND KIT QUICK",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )

                        brandKit?.let { bk ->
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("Logo: " + bk.brandLogo, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("Fonts: " + bk.fontsConfig, fontSize = 11.sp)
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    bk.brandColorsHex.split(",").take(4).forEach { colorHex ->
                                        val parsedColor = try {
                                            Color(android.graphics.Color.parseColor(colorHex.trim()))
                                        } catch (e: Exception) {
                                            null
                                        }
                                        if (parsedColor != null) {
                                            Box(
                                                modifier = Modifier
                                                    .size(16.dp)
                                                    .clip(CircleShape)
                                                    .background(parsedColor)
                                            )
                                        }
                                    }
                                }
                            }
                        } ?: Text("Brand kit empty", fontSize = 12.sp, color = Color.Gray)

                        Text(
                            "Customised for Appleseed Corp",
                            fontSize = 9.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

data class LaunchpadItem(val name: String, val url: String, val emoji: String)

// ────────────────────────────────────────────────────────
// 2. SOCIAL ACCOUNTS MANAGER
// ────────────────────────────────────────────────────────
@Composable
fun SocialAccountsScreen(
    viewModel: LifeViewModel,
    query: String,
    onAddClick: (SocialAccount?) -> Unit
) {
    val context = LocalContext.current
    val socialAccounts by viewModel.socialAccounts.collectAsStateWithLifecycle()

    val filtered = socialAccounts.filter {
        it.platform.contains(query, true) || it.username.contains(query, true) || it.displayName.contains(query, true)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "CONNECTED ACCOUNTS",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
            SkeuomorphicButton(onClick = { onAddClick(null) }) {
                Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Account", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (filtered.isEmpty()) {
            EmptyStateView("No accounts found. Tap Add to add a social handle.")
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 280.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filtered) { account ->
                    SocialCard(
                        account = account,
                        onEdit = { onAddClick(account) },
                        onDelete = { viewModel.deleteSocialAccount(account) },
                        onCopyUsername = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("username", account.username))
                            viewModel.showToast("Username copied!", "success")
                        },
                        onCopyUrl = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("profile_url", account.profileUrl))
                            viewModel.showToast("URL copied!", "success")
                        },
                        onOpen = {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(account.profileUrl))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                viewModel.showToast("Cannot open profile: ${e.localizedMessage}", "warning")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SocialCard(
    account: SocialAccount,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onCopyUsername: () -> Unit,
    onCopyUrl: () -> Unit,
    onOpen: () -> Unit
) {
    SkeuomorphicCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val emoji = when (account.platform.lowercase()) {
                        "facebook" -> "📘"
                        "instagram" -> "📸"
                        "github" -> "💻"
                        "linkedin" -> "💼"
                        "threads" -> "🧵"
                        "tiktok" -> "🎵"
                        "youtube" -> "🎥"
                        "x (twitter)" -> "🐦"
                        "discord" -> "👾"
                        "telegram" -> "✈️"
                        "whatsapp" -> "💬"
                        else -> "🌐"
                    }
                    Apple3DIcon(emoji = emoji, size = 44.dp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(account.platform, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(account.displayName, fontSize = 12.sp, color = Color.Gray)
                    }
                }

                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = AppleBlue, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AppleRed, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Username: @" + account.username,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            if (account.bio.isNotEmpty()) {
                Text(
                    text = account.bio,
                    fontSize = 11.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            if (account.notes.isNotEmpty()) {
                Text(
                    text = "Notes: " + account.notes,
                    fontSize = 10.sp,
                    color = Color.Gray.copy(alpha = 0.8f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SkeuomorphicButton(onClick = onCopyUsername, modifier = Modifier.weight(1f), cornerRadius = 8.dp) {
                    Text("Copy @", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                SkeuomorphicButton(onClick = onCopyUrl, modifier = Modifier.weight(1f), cornerRadius = 8.dp) {
                    Text("Copy URL", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                SkeuomorphicButton(
                    onClick = onOpen,
                    modifier = Modifier.weight(1.2f),
                    cornerRadius = 8.dp,
                    backgroundColor = AppleBlue
                ) {
                    Text("Open Profile", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

// ────────────────────────────────────────────────────────
// 3. CONTACT MANAGER SCREEN
// ────────────────────────────────────────────────────────
@Composable
fun ContactsScreen(
    viewModel: LifeViewModel,
    query: String,
    onAddClick: (Contact?) -> Unit
) {
    val context = LocalContext.current
    val contacts by viewModel.contacts.collectAsStateWithLifecycle()

    var activeCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Personal", "Business", "Emergency", "Family", "Friends")

    val filtered = contacts.filter {
        (activeCategory == "All" || it.category == activeCategory) &&
                (it.fullName.contains(query, true) || it.phoneNumber.contains(query, true) || it.email.contains(query, true))
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // macOS Styled Sub Header row
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category scroll row
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = cat == activeCategory
                    val activeBg = if (isSelected) AppleBlue else Color.Transparent
                    val textColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground

                    Box(
                        modifier = Modifier
                            .height(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(activeBg)
                            .clickable { activeCategory = cat }
                            .padding(horizontal = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(cat, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = textColor)
                    }
                }
            }

            SkeuomorphicButton(onClick = { onAddClick(null) }) {
                Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Contact", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (filtered.isEmpty()) {
            EmptyStateView("No contacts in category. Touch Add above.")
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 280.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filtered) { contact ->
                    ContactCard(
                        contact = contact,
                        onEdit = { onAddClick(contact) },
                        onDelete = { viewModel.deleteContact(contact) },
                        onCopy = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("phone", contact.phoneNumber))
                            viewModel.showToast("Copied phone number", "success")
                        },
                        onCall = {
                            try {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + contact.phoneNumber))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                viewModel.showToast("Cannot place call", "warning")
                            }
                        },
                        onWhatsapp = {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/" + contact.whatsapp))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                viewModel.showToast("WhatsApp not configured", "warning")
                            }
                        },
                        onTelegram = {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/" + contact.telegram.replace("@", "")))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                viewModel.showToast("Telegram failed", "warning")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ContactCard(
    contact: Contact,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onCopy: () -> Unit,
    onCall: () -> Unit,
    onWhatsapp: () -> Unit,
    onTelegram: () -> Unit
) {
    SkeuomorphicCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val emoji = when (contact.category.lowercase()) {
                        "family" -> "🏡"
                        "friends" -> "🤝"
                        "business" -> "💼"
                        "emergency" -> "🚨"
                        else -> "👤"
                    }
                    Apple3DIcon(emoji = emoji, size = 44.dp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(contact.fullName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(contact.category, fontSize = 11.sp, color = AppleBlue, fontWeight = FontWeight.Bold)
                    }
                }

                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = AppleBlue, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AppleRed, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Phone: " + contact.phoneNumber, fontSize = 12.sp)
                if (contact.email.isNotEmpty()) Text("Email: " + contact.email, fontSize = 11.sp, color = Color.Gray)
                if (contact.businessEmail.isNotEmpty()) Text("Business Email: " + contact.businessEmail, fontSize = 11.sp, color = Color.Gray)
                if (contact.address.isNotEmpty()) Text("Address: " + contact.address, fontSize = 11.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Quick Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SkeuomorphicButton(onClick = onCopy, modifier = Modifier.weight(1f), cornerRadius = 8.dp) {
                    Text("Copy", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                SkeuomorphicButton(onClick = onCall, modifier = Modifier.weight(1f), cornerRadius = 8.dp, backgroundColor = AppleGreen) {
                    Text("Call", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                if (contact.whatsapp.isNotEmpty()) {
                    SkeuomorphicButton(onClick = onWhatsapp, modifier = Modifier.weight(1.2f), cornerRadius = 8.dp) {
                        Text("WhatsApp", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
                if (contact.telegram.isNotEmpty()) {
                    SkeuomorphicButton(onClick = onTelegram, modifier = Modifier.weight(1f), cornerRadius = 8.dp) {
                        Text("TG", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ────────────────────────────────────────────────────────
// 4. BIO MANAGER SCREEN
// ────────────────────────────────────────────────────────
@Composable
fun BiosScreen(
    viewModel: LifeViewModel,
    query: String,
    onAddClick: (Bio?) -> Unit
) {
    val context = LocalContext.current
    val bios by viewModel.bios.collectAsStateWithLifecycle()

    val filtered = bios.filter {
        it.title.contains(query, true) || it.content.contains(query, true)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "SAVED BRAND BIOS",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
            SkeuomorphicButton(onClick = { onAddClick(null) }) {
                Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Bio Snippet", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (filtered.isEmpty()) {
            EmptyStateView("No bios saved. Tap Add Bio Snippet.")
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filtered) { bio ->
                    SkeuomorphicCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Apple3DIcon(emoji = "✍️", size = 36.dp)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(bio.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                }

                                Row {
                                    IconButton(onClick = { viewModel.toggleBioFavorite(bio) }) {
                                        Icon(
                                            if (bio.isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                                            contentDescription = "Fav",
                                            tint = if (bio.isFavorite) AppleRed else Color.Gray,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    IconButton(onClick = { onAddClick(bio) }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = AppleBlue, modifier = Modifier.size(18.dp))
                                    }
                                    IconButton(onClick = { viewModel.deleteBio(bio) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AppleRed, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.04f),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(12.dp)
                            ) {
                                Text(
                                    bio.content,
                                    fontSize = 13.sp,
                                    fontFamily = FontFamily.SansSerif,
                                    lineHeight = 18.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            SkeuomorphicButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("bio", bio.content))
                                    viewModel.showToast("Bio Copied to clipboard", "success")
                                },
                                modifier = Modifier.align(Alignment.End),
                                cornerRadius = 8.dp
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Copy Bio", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ────────────────────────────────────────────────────────
// 5. PROFILE ASSETS GALLERY SCREEN
// ────────────────────────────────────────────────────────
@Composable
fun AssetsScreen(
    viewModel: LifeViewModel,
    query: String,
    onAddClick: (ProfileAsset?) -> Unit
) {
    val assets by viewModel.profileAssets.collectAsStateWithLifecycle()

    val filtered = assets.filter {
        it.title.contains(query, true) || it.type.contains(query, true)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "PROFILE & BRAND ASSETS",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
            SkeuomorphicButton(onClick = { onAddClick(null) }) {
                Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Asset", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (filtered.isEmpty()) {
            EmptyStateView("No brand assets loaded. Touch Add Asset.")
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 160.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filtered) { asset ->
                    AssetGalleryCard(
                        asset = asset,
                        onEdit = { onAddClick(asset) },
                        onDelete = { viewModel.deleteProfileAsset(asset) }
                    )
                }
            }
        }
    }
}

@Composable
fun AssetGalleryCard(
    asset: ProfileAsset,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val gradientColor = when (asset.type.lowercase()) {
        "profile photo" -> listOf(AppleBlue, ApplePurple)
        "logo" -> listOf(AppleOrange, AppleRed)
        "qr code" -> listOf(AppleGraphite, Color.Black)
        else -> listOf(AppleGreen, AppleBlue)
    }

    SkeuomorphicCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(210.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Simulated visual backdrop box representing asset
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Brush.linearGradient(colors = gradientColor)),
                contentAlignment = Alignment.Center
            ) {
                val designIcon = when (asset.type.lowercase()) {
                    "profile photo" -> "👤"
                    "logo" -> ""
                    "qr code" -> "📱"
                    else -> "🖼️"
                }
                Text(designIcon, fontSize = 36.sp)
            }

            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                Text(
                    asset.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    asset.type,
                    color = AppleBlue,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    if (asset.description.isNotEmpty()) asset.description else "No description",
                    fontSize = 10.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = AppleBlue, modifier = Modifier.size(14.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AppleRed, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }
    }
}

// ────────────────────────────────────────────────────────
// 6. PASSWORD VAULT SCREEN
// ────────────────────────────────────────────────────────
@Composable
fun PasswordVaultScreen(
    viewModel: LifeViewModel,
    query: String,
    onAddClick: (PasswordEntry?) -> Unit
) {
    val passwords by viewModel.passwords.collectAsStateWithLifecycle()

    val filtered = passwords.filter {
        it.website.contains(query, true) || it.username.contains(query, true)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Lock, contentDescription = "Vault", tint = AppleRed)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "ENCRYPTED CREDENTIALS",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }
            SkeuomorphicButton(onClick = { onAddClick(null) }) {
                Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Secure Entry", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (filtered.isEmpty()) {
            EmptyStateView("Encrypted vault is empty. Keep logins secure here.")
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 280.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filtered) { entry ->
                    PasswordEntryCard(
                        entry = entry,
                        viewModel = viewModel,
                        onEdit = { onAddClick(entry) },
                        onDelete = { viewModel.deletePassword(entry) }
                    )
                }
            }
        }
    }
}

@Composable
fun PasswordEntryCard(
    entry: PasswordEntry,
    viewModel: LifeViewModel,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    var showPassword by remember { mutableStateOf(false) }

    SkeuomorphicCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Apple3DIcon(emoji = "🔐", size = 36.dp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(entry.website, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text("User: " + entry.username, fontSize = 11.sp, color = Color.Gray)
                    }
                }

                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = AppleBlue, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AppleRed, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f))
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (showPassword) entry.password else "••••••••••••",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.weight(1f)
                )

                Row {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Show",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("password", entry.password))
                            viewModel.showToast("Password copied safely", "success")
                        }
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = AppleBlue, modifier = Modifier.size(18.dp))
                    }
                }
            }

            if (entry.recoveryEmail.isNotEmpty() || entry.backupCodes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    if (entry.recoveryEmail.isNotEmpty()) {
                        Text("Recovery: " + entry.recoveryEmail, fontSize = 10.sp, color = Color.Gray)
                    }
                    if (entry.backupCodes.isNotEmpty()) {
                        Text("Codes: " + entry.backupCodes, fontSize = 10.sp, color = AppleRed, fontWeight = FontWeight.Bold)
                    }
                    if (entry.tfaNotes.isNotEmpty()) {
                        Text("2FA Info: " + entry.tfaNotes, fontSize = 10.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}

// ────────────────────────────────────────────────────────
// 7. DOCUMENTS MANAGER SCREEN
// ────────────────────────────────────────────────────────
@Composable
fun DocumentsScreen(
    viewModel: LifeViewModel,
    query: String,
    onAddClick: (Document?) -> Unit
) {
    val documents by viewModel.documents.collectAsStateWithLifecycle()

    var activeFolder by remember { mutableStateOf("All") }
    val folders = listOf("All", "Career", "Projects", "Legal", "Other")

    val filtered = documents.filter {
        (activeFolder == "All" || it.folder == activeFolder) &&
                (it.name.contains(query, true) || it.type.contains(query, true))
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Folder tabs
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                folders.forEach { f ->
                    val isSelected = f == activeFolder
                    Box(
                        modifier = Modifier
                            .height(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) AppleBlue else Color.Transparent)
                            .clickable { activeFolder = f }
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(f, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground)
                    }
                }
            }

            SkeuomorphicButton(onClick = { onAddClick(null) }) {
                Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Doc", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (filtered.isEmpty()) {
            EmptyStateView("No documents in directory. Touch Add Doc.")
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filtered) { doc ->
                    SkeuomorphicCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val docEmoji = when (doc.type.lowercase()) {
                                    "pdf" -> "📕"
                                    "zip" -> "🗄️"
                                    "image" -> "🖼️"
                                    "folder" -> "📁"
                                    else -> "📄"
                                }
                                Apple3DIcon(emoji = docEmoji, size = 38.dp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(doc.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text(doc.type + " file", fontSize = 10.sp, color = AppleBlue)
                                        Text(doc.fileSize, fontSize = 10.sp, color = Color.Gray)
                                        Text("Folder: " + doc.folder, fontSize = 10.sp, color = Color.Gray)
                                    }
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { viewModel.toggleDocumentPin(doc) }) {
                                    Icon(
                                        if (doc.isPinned) Icons.Default.PushPin else Icons.Outlined.PushPin,
                                        contentDescription = "Pin",
                                        tint = if (doc.isPinned) AppleOrange else Color.Gray,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                IconButton(onClick = { onAddClick(doc) }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = AppleBlue, modifier = Modifier.size(16.dp))
                                }
                                IconButton(onClick = { viewModel.deleteDocument(doc) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AppleRed, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ────────────────────────────────────────────────────────
// 8. RICH NOTES SCREEN
// ────────────────────────────────────────────────────────
@Composable
fun NotesScreen(
    viewModel: LifeViewModel,
    query: String,
    onAddClick: (Note?) -> Unit
) {
    val notes by viewModel.notes.collectAsStateWithLifecycle()

    var selectedCat by remember { mutableStateOf("All") }
    val categories = listOf("All", "Ideas", "Content Ideas", "Project Ideas", "Business Notes")

    val filtered = notes.filter {
        (selectedCat == "All" || it.category == selectedCat) &&
                (it.title.contains(query, true) || it.content.contains(query, true))
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = cat == selectedCat
                    Box(
                        modifier = Modifier
                            .height(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) AppleBlue else Color.Transparent)
                            .clickable { selectedCat = cat }
                            .padding(horizontal = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(cat, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground)
                    }
                }
            }

            SkeuomorphicButton(onClick = { onAddClick(null) }) {
                Icon(Icons.Default.NoteAdd, contentDescription = "Add", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("New Note", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (filtered.isEmpty()) {
            EmptyStateView("Notes library is empty. Touch New Note above.")
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 220.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filtered) { note ->
                    SkeuomorphicCard(
                        modifier = Modifier.fillMaxWidth().height(200.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        note.category,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AppleBlue
                                    )

                                    Row {
                                        IconButton(onClick = { viewModel.toggleNotePin(note) }, modifier = Modifier.size(24.dp)) {
                                            Icon(
                                                Icons.Default.PushPin,
                                                contentDescription = "Pin",
                                                tint = if (note.isPinned) AppleOrange else Color.Gray,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                        IconButton(onClick = { onAddClick(note) }, modifier = Modifier.size(24.dp)) {
                                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = AppleBlue, modifier = Modifier.size(14.dp))
                                        }
                                        IconButton(onClick = { viewModel.deleteNote(note) }, modifier = Modifier.size(24.dp)) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AppleRed, modifier = Modifier.size(14.dp))
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    note.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    note.content,
                                    fontSize = 11.sp,
                                    color = Color.Gray,
                                    maxLines = 4,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            val dateStr = SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(Date(note.updatedAt))
                            Text(
                                "Last updated: " + dateStr,
                                fontSize = 9.sp,
                                color = Color.Gray.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ────────────────────────────────────────────────────────
// 9. TODO MANAGER SCREEN
// ────────────────────────────────────────────────────────
@Composable
fun TodoScreen(
    viewModel: LifeViewModel,
    query: String,
    onAddClick: (TodoItem?) -> Unit
) {
    val todos by viewModel.todos.collectAsStateWithLifecycle()

    val filtered = todos.filter {
        it.title.contains(query, true)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "DAILY TASK PRIORITIES",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
            SkeuomorphicButton(onClick = { onAddClick(null) }) {
                Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Schedule Task", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (filtered.isEmpty()) {
            EmptyStateView("All caught up! Tap Schedule Task.")
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filtered) { todo ->
                    val priorityColor = when (todo.priority.lowercase()) {
                        "high" -> AppleRed
                        "medium" -> AppleOrange
                        else -> AppleGreen
                    }

                    SkeuomorphicCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                IconButton(onClick = { viewModel.toggleTodoCompleted(todo) }) {
                                    Icon(
                                        imageVector = if (todo.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                        contentDescription = "Complete Toggle",
                                        tint = if (todo.isCompleted) AppleGreen else AppleBlue,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = todo.title,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                        color = if (todo.isCompleted) Color.Gray else MaterialTheme.colorScheme.onBackground
                                    )
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text(
                                            todo.priority.uppercase() + " PRIORITY",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = priorityColor
                                        )
                                        if (todo.dueDate.isNotEmpty()) {
                                            Text("Due: " + todo.dueDate, fontSize = 10.sp, color = Color.Gray)
                                        }
                                        if (todo.reminder.isNotEmpty()) {
                                            Text("Reminder: " + todo.reminder, fontSize = 10.sp, color = Color.Gray)
                                        }
                                    }
                                }
                            }

                            Row {
                                IconButton(onClick = { onAddClick(todo) }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = AppleBlue, modifier = Modifier.size(16.dp))
                                }
                                IconButton(onClick = { viewModel.deleteTodo(todo) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AppleRed, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ────────────────────────────────────────────────────────
// 10. BRAND KIT SCREEN
// ────────────────────────────────────────────────────────
@Composable
fun BrandKitScreen(viewModel: LifeViewModel) {
    val brandKit by viewModel.brandKit.collectAsStateWithLifecycle()

    var logo by remember { mutableStateOf("") }
    var colorsHex by remember { mutableStateOf("") }
    var fonts by remember { mutableStateOf("") }
    var socialLinks by remember { mutableStateOf("") }
    var info by remember { mutableStateOf("") }

    // Sync state
    LaunchedEffect(brandKit) {
        brandKit?.let { bk ->
            logo = bk.brandLogo
            colorsHex = bk.brandColorsHex
            fonts = bk.fontsConfig
            socialLinks = bk.socialLinks
            info = bk.businessInfo
        }
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize().padding(8.dp)
    ) {
        item {
            Text(
                "GLOBAL BRAND KIT CONFIGURATION",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
        }

        item {
            SkeuomorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Brand Basics", fontWeight = FontWeight.Bold, color = AppleBlue)

                    Text("Brand Logo Designation", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    SkeuomorphicInsetField(
                        value = logo,
                        onValueChange = { logo = it },
                        placeholder = "e.g., Apple Corp."
                    )

                    Text("Brand Hex Colors (Comma Separated)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    SkeuomorphicInsetField(
                        value = colorsHex,
                        onValueChange = { colorsHex = it },
                        placeholder = "e.g., #007AFF,#34C759,#FF9500"
                    )

                    // Color Preview Chips
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                    ) {
                        colorsHex.split(",").forEach { hex ->
                            val parsedColor = try {
                                val trimmed = hex.trim()
                                if (trimmed.startsWith("#") && (trimmed.length == 7 || trimmed.length == 9)) {
                                    Color(android.graphics.Color.parseColor(trimmed))
                                } else {
                                    null
                                }
                            } catch (e: Exception) {
                                null
                            }
                            if (parsedColor != null) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(parsedColor)
                                )
                            }
                        }
                    }

                    Text("Global Font Config", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    SkeuomorphicInsetField(
                        value = fonts,
                        onValueChange = { fonts = it },
                        placeholder = "e.g., SF Pro, Inter"
                    )

                    Text("Social Reference Links", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    SkeuomorphicInsetField(
                        value = socialLinks,
                        onValueChange = { socialLinks = it },
                        placeholder = "e.g., github.com/corp"
                    )

                    Text("Business Operations Info", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    SkeuomorphicInsetField(
                        value = info,
                        onValueChange = { info = it },
                        placeholder = "e.g., Infinite Loop, CA"
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    SkeuomorphicButton(
                        onClick = {
                            viewModel.updateBrandKit(logo, colorsHex, fonts, socialLinks, info)
                        },
                        backgroundColor = AppleBlue,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Save Brand Specifications", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ────────────────────────────────────────────────────────
// 11. SETTINGS SCREEN
// ────────────────────────────────────────────────────────
@Composable
fun SettingsScreen(viewModel: LifeViewModel) {
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
    val activeAccent by viewModel.accentColor.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var importJsonText by remember { mutableStateOf("") }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize().padding(8.dp)
    ) {
        item {
            Text(
                "SYSTEM & CUSTOMIZATION",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
        }

        // Color Accents Grid
        item {
            SkeuomorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("System Accent Color", fontWeight = FontWeight.Bold, color = AppleBlue)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())
                    ) {
                        AccentColor.values().forEach { ac ->
                            val isSelected = ac == activeAccent
                            val strokeColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable { viewModel.setAccentColor(ac) }
                                    .padding(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(Color(android.graphics.Color.parseColor(ac.hex)))
                                        .border(
                                            width = if (isSelected) 3.dp else 1.dp,
                                            color = if (isSelected) MaterialTheme.colorScheme.onBackground else Color.Transparent,
                                            shape = CircleShape
                                        )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(ac.label, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }

        // Backup and Restore Section
        item {
            SkeuomorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Backup & Restore (JSON Engine)", fontWeight = FontWeight.Bold, color = AppleRed)
                    Text(
                        "Export all social account handles, CV files indices, note lists, and categories into a transportable text spec.",
                        fontSize = 12.sp, color = Color.Gray
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        SkeuomorphicButton(
                            onClick = {
                                val json = viewModel.exportBackupJson()
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("LifeOS Backup", json))
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Backup, contentDescription = "Export")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export JSON", fontWeight = FontWeight.Bold)
                        }

                        SkeuomorphicButton(
                            onClick = {
                                if (importJsonText.isNotEmpty()) {
                                    viewModel.importBackupJson(importJsonText)
                                    importJsonText = ""
                                } else {
                                    viewModel.showToast("Paste JSON backup into textbox first", "warning")
                                }
                            },
                            modifier = Modifier.weight(1f),
                            backgroundColor = AppleBlue
                        ) {
                            Icon(Icons.Default.Restore, contentDescription = "Restore", tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Import JSON", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Paste JSON Backup string here to restore:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    SkeuomorphicInsetField(
                        value = importJsonText,
                        onValueChange = { importJsonText = it },
                        placeholder = "Paste JSON backup text string here..."
                    )
                }
            }
        }
    }
}

// ────────────────────────────────────────────────────────
// COMMON SHARED VIEWS
// ────────────────────────────────────────────────────────
@Composable
fun EmptyStateView(message: String) {
    Column(
        modifier = Modifier.fillMaxWidth().height(240.dp).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Apple3DIcon(emoji = "📭", size = 64.dp)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = message,
            color = Color.Gray,
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )
    }
}

// ────────────────────────────────────────────────────────
// DIALOG IMPLEMENTATIONS
// ────────────────────────────────────────────────────────

@Composable
fun AddSocialAccountDialog(
    editing: SocialAccount?,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, String, String) -> Unit
) {
    var platform by remember { mutableStateOf(editing?.platform ?: "") }
    var username by remember { mutableStateOf(editing?.username ?: "") }
    var dName by remember { mutableStateOf(editing?.displayName ?: "") }
    var pUrl by remember { mutableStateOf(editing?.profileUrl ?: "") }
    var bio by remember { mutableStateOf(editing?.bio ?: "") }
    var notes by remember { mutableStateOf(editing?.notes ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        SkeuomorphicCard(
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(if (editing == null) "Create Social Handle" else "Edit Social Handle", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                Text("Platform Name", fontSize = 11.sp)
                SkeuomorphicInsetField(value = platform, onValueChange = { platform = it }, placeholder = "e.g., GitHub")

                Text("Username", fontSize = 11.sp)
                SkeuomorphicInsetField(value = username, onValueChange = { username = it }, placeholder = "e.g., octocat")

                Text("Display Name", fontSize = 11.sp)
                SkeuomorphicInsetField(value = dName, onValueChange = { dName = it }, placeholder = "e.g., The Octocat")

                Text("Profile URL", fontSize = 11.sp)
                SkeuomorphicInsetField(value = pUrl, onValueChange = { pUrl = it }, placeholder = "e.g., https://github.com/...")

                Text("Bio", fontSize = 11.sp)
                SkeuomorphicInsetField(value = bio, onValueChange = { bio = it }, placeholder = "Custom bio info...")

                Text("Notes", fontSize = 11.sp)
                SkeuomorphicInsetField(value = notes, onValueChange = { notes = it }, placeholder = "Personal reminder notes...")

                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SkeuomorphicButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancel")
                    }
                    SkeuomorphicButton(
                        onClick = {
                            if (platform.isNotEmpty() && username.isNotEmpty()) {
                                onConfirm(platform, username, dName, pUrl, bio, notes)
                            }
                        },
                        modifier = Modifier.weight(1.2f),
                        backgroundColor = AppleBlue
                    ) {
                        Text("Confirm Spec", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AddContactDialog(
    editing: Contact?,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, String, String, String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf(editing?.fullName ?: "") }
    var phone by remember { mutableStateOf(editing?.phoneNumber ?: "") }
    var wa by remember { mutableStateOf(editing?.whatsapp ?: "") }
    var tg by remember { mutableStateOf(editing?.telegram ?: "") }
    var dc by remember { mutableStateOf(editing?.discord ?: "") }
    var email by remember { mutableStateOf(editing?.email ?: "") }
    var bEmail by remember { mutableStateOf(editing?.businessEmail ?: "") }
    var addr by remember { mutableStateOf(editing?.address ?: "") }
    var web by remember { mutableStateOf(editing?.website ?: "") }
    var cat by remember { mutableStateOf(editing?.category ?: "Personal") }

    Dialog(onDismissRequest = onDismiss) {
        SkeuomorphicCard(
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(if (editing == null) "New Contact card" else "Edit Contact card", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                Text("Full Name", fontSize = 11.sp)
                SkeuomorphicInsetField(value = name, onValueChange = { name = it }, placeholder = "e.g., Craig Federighi")

                Text("Phone Number", fontSize = 11.sp)
                SkeuomorphicInsetField(value = phone, onValueChange = { phone = it }, placeholder = "e.g., +1 (555) 555-0199")

                Text("WhatsApp Direct", fontSize = 11.sp)
                SkeuomorphicInsetField(value = wa, onValueChange = { wa = it }, placeholder = "e.g., +15555550199")

                Text("Telegram Handle", fontSize = 11.sp)
                SkeuomorphicInsetField(value = tg, onValueChange = { tg = it }, placeholder = "e.g., @hairforceone")

                Text("Discord Tag", fontSize = 11.sp)
                SkeuomorphicInsetField(value = dc, onValueChange = { dc = it }, placeholder = "e.g., john#1234")

                Text("Personal Email", fontSize = 11.sp)
                SkeuomorphicInsetField(value = email, onValueChange = { email = it }, placeholder = "e.g., john@email.com")

                Text("Business Email", fontSize = 11.sp)
                SkeuomorphicInsetField(value = bEmail, onValueChange = { bEmail = it }, placeholder = "e.g., corp@company.com")

                Text("Office Address", fontSize = 11.sp)
                SkeuomorphicInsetField(value = addr, onValueChange = { addr = it }, placeholder = "Cupertino, CA")

                Text("Website", fontSize = 11.sp)
                SkeuomorphicInsetField(value = web, onValueChange = { web = it }, placeholder = "https://...")

                Text("Category Group", fontSize = 11.sp)
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Personal", "Business", "Emergency", "Family", "Friends").forEach { group ->
                        val selected = cat == group
                        Box(
                            modifier = Modifier
                                .height(32.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (selected) AppleBlue else Color.Gray.copy(alpha = 0.1f))
                                .clickable { cat = group }
                                .padding(horizontal = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(group, fontSize = 11.sp, color = if (selected) Color.White else MaterialTheme.colorScheme.onBackground)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SkeuomorphicButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancel")
                    }
                    SkeuomorphicButton(
                        onClick = {
                            if (name.isNotEmpty() && phone.isNotEmpty()) {
                                onConfirm(name, phone, wa, tg, dc, email, bEmail, addr, web, cat)
                            }
                        },
                        modifier = Modifier.weight(1.2f),
                        backgroundColor = AppleBlue
                    ) {
                        Text("Save Card", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AddBioDialog(
    editing: Bio?,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var title by remember { mutableStateOf(editing?.title ?: "Instagram Bio") }
    var content by remember { mutableStateOf(editing?.content ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        SkeuomorphicCard(
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(if (editing == null) "New Bio Snippet" else "Edit Bio Snippet", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                Text("Select Bio Segment", fontSize = 11.sp)
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Instagram Bio", "Facebook Bio", "GitHub Bio", "LinkedIn About", "Twitter Bio", "Gaming Bio").forEach { seg ->
                        val isSel = title == seg
                        Box(
                            modifier = Modifier
                                .height(32.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSel) AppleBlue else Color.Gray.copy(alpha = 0.1f))
                                .clickable { title = seg }
                                .padding(horizontal = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(seg, fontSize = 11.sp, color = if (isSel) Color.White else MaterialTheme.colorScheme.onBackground)
                        }
                    }
                }

                Text("Bio Content Snippet", fontSize = 11.sp)
                SkeuomorphicInsetField(
                    value = content,
                    onValueChange = { content = it },
                    placeholder = "Write bio snippet...",
                    modifier = Modifier.height(80.dp),
                    singleLine = false
                )

                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SkeuomorphicButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancel")
                    }
                    SkeuomorphicButton(
                        onClick = {
                            if (content.isNotEmpty()) {
                                onConfirm(title, content)
                            }
                        },
                        modifier = Modifier.weight(1.2f),
                        backgroundColor = AppleBlue
                    ) {
                        Text("Save Bio", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AddAssetDialog(
    editing: ProfileAsset?,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf(editing?.title ?: "") }
    var type by remember { mutableStateOf(editing?.type ?: "Profile Photo") }
    var desc by remember { mutableStateOf(editing?.description ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        SkeuomorphicCard(
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(if (editing == null) "Add Brand Asset" else "Edit Asset Specs", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                Text("Asset Name", fontSize = 11.sp)
                SkeuomorphicInsetField(value = title, onValueChange = { title = it }, placeholder = "e.g., High-Res Corporate Pfp")

                Text("Asset Category", fontSize = 11.sp)
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Profile Photo", "Cover Photo", "Logo", "Brand Image", "QR Code").forEach { category ->
                        val isSel = type == category
                        Box(
                            modifier = Modifier
                                .height(32.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSel) AppleBlue else Color.Gray.copy(alpha = 0.1f))
                                .clickable { type = category }
                                .padding(horizontal = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(category, fontSize = 11.sp, color = if (isSel) Color.White else MaterialTheme.colorScheme.onBackground)
                        }
                    }
                }

                Text("Description & Meta specs", fontSize = 11.sp)
                SkeuomorphicInsetField(value = desc, onValueChange = { desc = it }, placeholder = "Specs / dimensions / colors...")

                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SkeuomorphicButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancel")
                    }
                    SkeuomorphicButton(
                        onClick = {
                            if (title.isNotEmpty()) {
                                onConfirm(title, type, "asset_ref_placeholder", desc)
                            }
                        },
                        modifier = Modifier.weight(1.2f),
                        backgroundColor = AppleBlue
                    ) {
                        Text("Register Asset", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AddPasswordDialog(
    editing: PasswordEntry?,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, String, String) -> Unit
) {
    var site by remember { mutableStateOf(editing?.website ?: "") }
    var user by remember { mutableStateOf(editing?.username ?: "") }
    var pass by remember { mutableStateOf(editing?.password ?: "") }
    var recEmail by remember { mutableStateOf(editing?.recoveryEmail ?: "") }
    var codes by remember { mutableStateOf(editing?.backupCodes ?: "") }
    var notes by remember { mutableStateOf(editing?.tfaNotes ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        SkeuomorphicCard(
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(if (editing == null) "Register Credentials" else "Update Credentials", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                Text("Website Portal", fontSize = 11.sp)
                SkeuomorphicInsetField(value = site, onValueChange = { site = it }, placeholder = "e.g., https://gmail.com")

                Text("Username / Email", fontSize = 11.sp)
                SkeuomorphicInsetField(value = user, onValueChange = { user = it }, placeholder = "e.g., appleseed@me.com")

                Text("Password", fontSize = 11.sp)
                SkeuomorphicInsetField(value = pass, onValueChange = { pass = it }, placeholder = "Secure password string...")

                Text("Linked Recovery Email", fontSize = 11.sp)
                SkeuomorphicInsetField(value = recEmail, onValueChange = { recEmail = it }, placeholder = "e.g., recovery@me.com")

                Text("Emergency Backup Codes", fontSize = 11.sp)
                SkeuomorphicInsetField(value = codes, onValueChange = { codes = it }, placeholder = "XXXX-XXXX-XXXX")

                Text("2FA Info & Security Notes", fontSize = 11.sp)
                SkeuomorphicInsetField(value = notes, onValueChange = { notes = it }, placeholder = "Where is 2FA stored?")

                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SkeuomorphicButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancel")
                    }
                    SkeuomorphicButton(
                        onClick = {
                            if (site.isNotEmpty() && user.isNotEmpty() && pass.isNotEmpty()) {
                                onConfirm(site, user, pass, recEmail, codes, notes)
                            }
                        },
                        modifier = Modifier.weight(1.2f),
                        backgroundColor = AppleBlue
                    ) {
                        Text("Secure Save", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AddDocumentDialog(
    editing: Document?,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, Boolean, String) -> Unit
) {
    var name by remember { mutableStateOf(editing?.name ?: "") }
    var type by remember { mutableStateOf(editing?.type ?: "PDF") }
    var folder by remember { mutableStateOf(editing?.folder ?: "Documents") }
    var size by remember { mutableStateOf(editing?.fileSize ?: "1.2 MB") }

    Dialog(onDismissRequest = onDismiss) {
        SkeuomorphicCard(
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(if (editing == null) "Register Brand Document" else "Edit Document meta", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                Text("Document File Name", fontSize = 11.sp)
                SkeuomorphicInsetField(value = name, onValueChange = { name = it }, placeholder = "e.g., Resume_2026.pdf")

                Text("File Type Format", fontSize = 11.sp)
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("PDF", "ZIP", "Image", "Video", "Folder").forEach { ft ->
                        val isSel = type == ft
                        Box(
                            modifier = Modifier
                                .height(32.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSel) AppleBlue else Color.Gray.copy(alpha = 0.1f))
                                .clickable { type = ft }
                                .padding(horizontal = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(ft, fontSize = 11.sp, color = if (isSel) Color.White else MaterialTheme.colorScheme.onBackground)
                        }
                    }
                }

                Text("Folder Division Name", fontSize = 11.sp)
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Career", "Projects", "Legal", "Other").forEach { f ->
                        val isSel = folder == f
                        Box(
                            modifier = Modifier
                                .height(32.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSel) AppleBlue else Color.Gray.copy(alpha = 0.1f))
                                .clickable { folder = f }
                                .padding(horizontal = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(f, fontSize = 11.sp, color = if (isSel) Color.White else MaterialTheme.colorScheme.onBackground)
                        }
                    }
                }

                Text("Simulated File Size", fontSize = 11.sp)
                SkeuomorphicInsetField(value = size, onValueChange = { size = it }, placeholder = "e.g., 42.5 MB")

                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SkeuomorphicButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancel")
                    }
                    SkeuomorphicButton(
                        onClick = {
                            if (name.isNotEmpty()) {
                                onConfirm(name, type, "simulated_uri", folder, editing?.isPinned ?: false, size)
                            }
                        },
                        modifier = Modifier.weight(1.2f),
                        backgroundColor = AppleBlue
                    ) {
                        Text("Index Document", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AddNoteDialog(
    editing: Note?,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, Boolean) -> Unit
) {
    var title by remember { mutableStateOf(editing?.title ?: "") }
    var content by remember { mutableStateOf(editing?.content ?: "") }
    var cat by remember { mutableStateOf(editing?.category ?: "Ideas") }

    Dialog(onDismissRequest = onDismiss) {
        SkeuomorphicCard(
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(if (editing == null) "Create Sticky Note" else "Edit Sticky Note", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                Text("Note Title", fontSize = 11.sp)
                SkeuomorphicInsetField(value = title, onValueChange = { title = it }, placeholder = "e.g., Startup Business Plan")

                Text("Category Division", fontSize = 11.sp)
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Ideas", "Content Ideas", "Project Ideas", "Business Notes").forEach { c ->
                        val isSel = cat == c
                        Box(
                            modifier = Modifier
                                .height(32.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSel) AppleBlue else Color.Gray.copy(alpha = 0.1f))
                                .clickable { cat = c }
                                .padding(horizontal = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(c, fontSize = 11.sp, color = if (isSel) Color.White else MaterialTheme.colorScheme.onBackground)
                        }
                    }
                }

                Text("Rich Content Text", fontSize = 11.sp)
                SkeuomorphicInsetField(
                    value = content,
                    onValueChange = { content = it },
                    placeholder = "Write note body details...",
                    modifier = Modifier.height(130.dp),
                    singleLine = false
                )

                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SkeuomorphicButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancel")
                    }
                    SkeuomorphicButton(
                        onClick = {
                            if (title.isNotEmpty() && content.isNotEmpty()) {
                                onConfirm(title, content, cat, editing?.isPinned ?: false)
                            }
                        },
                        modifier = Modifier.weight(1.2f),
                        backgroundColor = AppleBlue
                    ) {
                        Text("Pin Note", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AddTodoDialog(
    editing: TodoItem?,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, Boolean) -> Unit
) {
    var title by remember { mutableStateOf(editing?.title ?: "") }
    var priority by remember { mutableStateOf(editing?.priority ?: "Medium") }
    var due by remember { mutableStateOf(editing?.dueDate ?: "Today") }
    var reminder by remember { mutableStateOf(editing?.reminder ?: "09:00 AM") }

    Dialog(onDismissRequest = onDismiss) {
        SkeuomorphicCard(
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(if (editing == null) "Schedule New Task" else "Update Daily Task", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                Text("Task Title", fontSize = 11.sp)
                SkeuomorphicInsetField(value = title, onValueChange = { title = it }, placeholder = "e.g., Complete UI branding")

                Text("Priority Status", fontSize = 11.sp)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("High", "Medium", "Low").forEach { p ->
                        val isSel = priority == p
                        val pColor = when (p.lowercase()) {
                            "high" -> AppleRed
                            "medium" -> AppleOrange
                            else -> AppleGreen
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) pColor else Color.Gray.copy(alpha = 0.1f))
                                .clickable { priority = p }
                                .padding(horizontal = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(p, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSel) Color.White else MaterialTheme.colorScheme.onBackground)
                        }
                    }
                }

                Text("Due Date", fontSize = 11.sp)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Today", "Tomorrow", "Next Week").forEach { d ->
                        val isSel = due == d
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) AppleBlue else Color.Gray.copy(alpha = 0.1f))
                                .clickable { due = d }
                                .padding(horizontal = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(d, fontSize = 11.sp, color = if (isSel) Color.White else MaterialTheme.colorScheme.onBackground)
                        }
                    }
                }

                Text("Reminder Alert Time", fontSize = 11.sp)
                SkeuomorphicInsetField(value = reminder, onValueChange = { reminder = it }, placeholder = "e.g., 09:00 AM")

                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SkeuomorphicButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancel")
                    }
                    SkeuomorphicButton(
                        onClick = {
                            if (title.isNotEmpty()) {
                                onConfirm(title, priority, reminder, due, editing?.isCompleted ?: false)
                            }
                        },
                        modifier = Modifier.weight(1.2f),
                        backgroundColor = AppleBlue
                    ) {
                        Text("Schedule", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
