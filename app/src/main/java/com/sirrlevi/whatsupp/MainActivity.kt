@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package com.sirrlevi.whatsupp

import android.graphics.Bitmap
import android.graphics.Canvas
import android.content.Intent
import android.content.Context
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.sirrlevi.whatsupp.data.ContactEntity
import com.sirrlevi.whatsupp.data.MessageEntity
import com.sirrlevi.whatsupp.data.WhatsuppDatabase
import com.sirrlevi.whatsupp.ui.ChatFormatting
import kotlinx.coroutines.launch
import java.util.UUID

private object ThemeState {
    var mode by mutableStateOf("system")
    var systemIsDark by mutableStateOf(true)
    val isDark: Boolean get() = when (mode) {
        "dark" -> true
        "light" -> false
        else -> systemIsDark
    }
}

private val AppBg: Color get() = if (ThemeState.isDark) Color(0xFF0B141A) else Color(0xFFF7F9FA)
private val AppSurface: Color get() = if (ThemeState.isDark) Color(0xFF202C33) else Color(0xFFFFFFFF)
private val AppSurface2: Color get() = if (ThemeState.isDark) Color(0xFF111B21) else Color(0xFFF0F2F5)
private val AppGreen: Color get() = if (ThemeState.isDark) Color(0xFF075E54) else Color(0xFF008069)
private val AppAccent: Color get() = if (ThemeState.isDark) Color(0xFF00A884) else Color(0xFF008069)
private val AppText: Color get() = if (ThemeState.isDark) Color(0xFFE9EDEF) else Color(0xFF111B21)
private val AppMuted: Color get() = if (ThemeState.isDark) Color(0xFF8696A0) else Color(0xFF667781)
private val IncomingBubble: Color get() = if (ThemeState.isDark) Color(0xFF202C33) else Color(0xFFFFFFFF)
private val OutgoingBubble: Color get() = if (ThemeState.isDark) Color(0xFF005C4B) else Color(0xFFD9FDD3)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database = WhatsuppDatabase.get(applicationContext)
        val preferences = getSharedPreferences("whatsupp-preferences", Context.MODE_PRIVATE)
        ThemeState.mode = preferences.getString("theme_mode", "system") ?: "system"
        ThemeState.systemIsDark = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        setContent {
            val scheme = if (ThemeState.isDark) {
                darkColorScheme(
                    primary = AppAccent,
                    onPrimary = Color.White,
                    secondary = Color(0xFF53BDEB),
                    background = AppBg,
                    onBackground = AppText,
                    surface = AppSurface,
                    onSurface = AppText,
                    surfaceVariant = AppSurface2,
                    onSurfaceVariant = AppMuted
                )
            } else {
                lightColorScheme(
                    primary = AppAccent,
                    onPrimary = Color.White,
                    secondary = Color(0xFF087F72),
                    background = AppBg,
                    onBackground = AppText,
                    surface = AppSurface,
                    onSurface = AppText,
                    surfaceVariant = AppSurface2,
                    onSurfaceVariant = AppMuted
                )
            }
            SideEffect {
                window.statusBarColor = AppBg.toArgb()
                window.navigationBarColor = AppBg.toArgb()
            }
            MaterialTheme(colorScheme = scheme) {
                WhatsuppApp(database)
            }
        }
    }
}

@Composable
private fun WhatsuppApp(database: WhatsuppDatabase) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val contacts by database.contactDao().observeAll().collectAsState(initial = emptyList())
    var route by remember { mutableStateOf("home") }
    var activeContact by remember { mutableStateOf<ContactEntity?>(null) }
    var editingContact by remember { mutableStateOf<ContactEntity?>(null) }
    var showNewContact by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var themeMode by remember { mutableStateOf(ThemeState.mode) }
    var snackbarText by remember { mutableStateOf<String?>(null) }
    val snackbarHost = remember { androidx.compose.material3.SnackbarHostState() }

    LaunchedEffect(snackbarText) {
        snackbarText?.let {
            snackbarHost.showSnackbar(it)
            snackbarText = null
        }
    }

    fun notify(message: String) { snackbarText = message }
    fun saveContact(contact: ContactEntity) {
        scope.launch {
            database.contactDao().upsert(contact)
            showNewContact = false
            editingContact = null
            notify("Contact saved on this device")
        }
    }

    Scaffold(
        containerColor = AppBg,
        snackbarHost = { androidx.compose.material3.SnackbarHost(snackbarHost) }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                activeContact != null && route == "chat" -> ChatScreen(
                    contact = activeContact!!,
                    database = database,
                    onBack = { route = "home"; activeContact = null },
                    onProfile = { route = "profile" },
                    onNotify = ::notify
                )
                activeContact != null && route == "profile" -> ContactInfoScreen(
                    contact = activeContact!!,
                    database = database,
                    onBack = { route = "chat" },
                    onEdit = { editingContact = activeContact; showNewContact = true; route = "home" },
                    onNotify = ::notify
                )
                route == "contacts" -> ContactsScreen(
                    contacts = contacts,
                    onBack = { route = "home" },
                    onAdd = { editingContact = null; showNewContact = true },
                    onOpen = { activeContact = it; route = "chat" },
                    onEdit = { editingContact = it; showNewContact = true },
                    onDelete = { contact -> scope.launch { database.contactDao().delete(contact.id); notify("Contact deleted") } }
                )
                route == "settings" -> SettingsScreen(
                    themeMode = themeMode,
                    onThemeModeChange = { selected ->
                        themeMode = selected
                        ThemeState.mode = selected
                        context.getSharedPreferences("whatsupp-preferences", Context.MODE_PRIVATE)
                            .edit().putString("theme_mode", selected).apply()
                    },
                    onBack = { route = "home" },
                    onManageContacts = { route = "contacts" },
                    onClearData = {
                        scope.launch {
                            database.messageDao().deleteAll()
                            database.contactDao().deleteAll()
                            route = "home"
                            notify("All local Whatsupp data cleared")
                        }
                    }
                )
                else -> HomeScreen(
                    contacts = contacts,
                    database = database,
                    onOpenChat = { activeContact = it; route = "chat" },
                    onNewContact = { editingContact = null; showNewContact = true },
                    onManageContacts = { route = "contacts" },
                    onSettings = { route = "settings" },
                    onNotify = ::notify
                )
            }
        }
    }

    if (showNewContact) {
        ContactEditorDialog(
            existing = editingContact,
            onDismiss = { showNewContact = false; editingContact = null },
            onSave = ::saveContact
        )
    }
}

private enum class HomeTab(val label: String) {
    CHATS("Chats"), UPDATES("Updates"), COMMUNITIES("Communities"), CALLS("Calls")
}

@Composable
private fun HomeScreen(
    contacts: List<ContactEntity>,
    database: WhatsuppDatabase,
    onOpenChat: (ContactEntity) -> Unit,
    onNewContact: () -> Unit,
    onManageContacts: () -> Unit,
    onSettings: () -> Unit,
    onNotify: (String) -> Unit
) {
    var tab by remember { mutableStateOf(HomeTab.CHATS) }
    var filter by remember { mutableStateOf("All") }
    var search by remember { mutableStateOf("") }
    var showMenu by remember { mutableStateOf(false) }
    var showArchived by remember { mutableStateOf(false) }
    var selectedContact by remember { mutableStateOf<ContactEntity?>(null) }
    val scope = rememberCoroutineScope()
    val latestMessages by produceState(initialValue = emptyMap<String, MessageEntity?>(), contacts, database) {
        value = contacts.associate { it.id to database.messageDao().latestForContact(it.id) }
    }
    val archivedCount = contacts.count { it.isArchived }
    val visibleContacts = contacts
        .filter { it.isArchived == showArchived }
        .filter { search.isBlank() || it.name.contains(search.trim(), true) || it.phone.contains(search.trim()) }
        .filter { contact ->
            when (filter) {
                "Unread" -> latestMessages[contact.id]?.let { !it.outgoing && !it.isDeleted } == true
                "Favorites" -> contact.isPinned
                else -> true
            }
        }
        .sortedWith(
            compareByDescending<ContactEntity> { it.isPinned }
                .thenByDescending { latestMessages[it.id]?.timestamp ?: it.createdAt }
        )

    Column(Modifier.fillMaxSize().background(AppBg)) {
        Row(
            Modifier.fillMaxWidth().padding(start = 20.dp, end = 12.dp, top = 12.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showArchived) {
                IconButton(onClick = { showArchived = false; search = ""; filter = "All" }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = AppText)
                }
                Text("Archived", fontSize = 22.sp, fontWeight = FontWeight.SemiBold, color = AppText, modifier = Modifier.weight(1f))
            } else {
                Text("Whatsupp", fontSize = 27.sp, fontWeight = FontWeight.Bold, color = AppText, modifier = Modifier.weight(1f))
                IconButton(onClick = { onNotify("Camera preview is not connected; choose an image from a chat attachment instead.") }) {
                    Icon(Icons.Default.CameraAlt, null, tint = AppText)
                }
                IconButton(onClick = onNewContact) { Icon(Icons.Default.Add, null, tint = AppText) }
            }
            Box {
                IconButton(onClick = { showMenu = true }) { Icon(Icons.Default.MoreVert, null, tint = AppText) }
                androidx.compose.material3.DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                    androidx.compose.material3.DropdownMenuItem(
                        text = { Text("Manage contacts") },
                        onClick = { showMenu = false; onManageContacts() },
                        leadingIcon = { Icon(Icons.Default.Contacts, null) }
                    )
                    androidx.compose.material3.DropdownMenuItem(
                        text = { Text("Settings") },
                        onClick = { showMenu = false; onSettings() },
                        leadingIcon = { Icon(Icons.Default.Settings, null) }
                    )
                    androidx.compose.material3.DropdownMenuItem(
                        text = { Text("About Whatsupp") },
                        onClick = { showMenu = false; onNotify("Whatsupp stores conversations locally on this device.") },
                        leadingIcon = { Icon(Icons.Default.Info, null) }
                    )
                }
            }
        }

        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            placeholder = { Text("Search contacts and chats", color = AppMuted) },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = AppMuted) },
            trailingIcon = {
                if (search.isNotEmpty()) {
                    IconButton(onClick = { search = "" }) { Icon(Icons.Default.Close, null, tint = AppMuted) }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).heightIn(min = 48.dp)
        )

        if (!showArchived) {
            Row(
                Modifier.fillMaxWidth().padding(start = 14.dp, end = 10.dp, top = 10.dp, bottom = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                listOf("All", "Unread", "Favorites").forEach { item ->
                    FilterChip(
                        selected = filter == item,
                        onClick = { filter = item; tab = HomeTab.CHATS },
                        label = { Text(item, fontSize = 12.sp) }
                    )
                }
                IconButton(onClick = onNewContact, modifier = Modifier.size(34.dp)) {
                    Icon(Icons.Default.Add, null, tint = AppMuted)
                }
            }
        }

        Box(Modifier.weight(1f).fillMaxWidth()) {
            when (tab) {
                HomeTab.CHATS -> {
                    Column(Modifier.fillMaxSize()) {
                        if (!showArchived) {
                            Row(
                                Modifier.fillMaxWidth().combinedClickable(onClick = { showArchived = true; search = ""; filter = "All" })
                                    .padding(horizontal = 20.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Archive, null, tint = AppMuted, modifier = Modifier.size(23.dp))
                                Spacer(Modifier.width(24.dp))
                                Text("Archived", color = AppText, fontSize = 15.sp, modifier = Modifier.weight(1f))
                                Text(archivedCount.toString(), color = AppMuted, fontSize = 12.sp)
                                Icon(Icons.Default.KeyboardArrowRight, null, tint = AppMuted)
                            }
                        }
                        if (visibleContacts.isEmpty()) {
                            if (showArchived) {
                                Column(Modifier.fillMaxSize().padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                    Icon(Icons.Default.Archive, null, tint = AppMuted, modifier = Modifier.size(46.dp))
                                    Spacer(Modifier.height(12.dp))
                                    Text("No archived chats", color = AppText, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                                }
                            } else {
                                EmptyHome(onAdd = onNewContact, onManage = onManageContacts)
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(bottom = 16.dp)
                            ) {
                                items(visibleContacts, key = { it.id }) { contact ->
                                    ChatListRow(
                                        contact = contact,
                                        database = database,
                                        onClick = { onOpenChat(contact) },
                                        onLongClick = { selectedContact = contact }
                                    )
                                }
                            }
                        }
                    }
                }
                HomeTab.UPDATES -> PlaceholderTab("Updates", "Create and manage local status previews.", Icons.Default.Update, onNotify)
                HomeTab.COMMUNITIES -> PlaceholderTab("Communities", "Your community spaces will appear here.", Icons.Default.Group, onNotify)
                HomeTab.CALLS -> PlaceholderTab("Calls", "Call history is a local visual record.", Icons.Default.Call, onNotify)
            }

            if (tab == HomeTab.CHATS && !showArchived) {
                androidx.compose.material3.FloatingActionButton(
                    onClick = onNewContact,
                    modifier = Modifier.align(Alignment.BottomEnd).padding(end = 18.dp, bottom = 16.dp),
                    containerColor = AppAccent,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(17.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "New chat")
                }
            }
        }

        Divider(color = if (ThemeState.isDark) Color(0xFF26343B) else Color(0xFFE2E7EA))
        Row(
            Modifier.fillMaxWidth().height(76.dp).background(AppSurface2),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            HomeTab.values().forEach { item ->
                val icon = when (item) {
                    HomeTab.CHATS -> Icons.Default.Chat
                    HomeTab.UPDATES -> Icons.Default.Update
                    HomeTab.COMMUNITIES -> Icons.Default.Group
                    HomeTab.CALLS -> Icons.Default.Call
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(78.dp).clip(RoundedCornerShape(18.dp)).combinedClickable(onClick = { tab = item; showArchived = false })
                ) {
                    Box(
                        Modifier.clip(CircleShape).background(if (tab == item && !showArchived) AppAccent.copy(alpha = 0.15f) else Color.Transparent)
                            .padding(horizontal = 18.dp, vertical = 5.dp)
                    ) {
                        Icon(icon, null, tint = if (tab == item && !showArchived) AppAccent else AppText, modifier = Modifier.size(23.dp))
                    }
                    Text(item.label, color = if (tab == item && !showArchived) AppText else AppMuted, fontSize = 11.sp, fontWeight = if (tab == item && !showArchived) FontWeight.SemiBold else FontWeight.Normal)
                }
            }
        }
    }

    selectedContact?.let { contact ->
        AlertDialog(
            onDismissRequest = { selectedContact = null },
            title = { Text(contact.name) },
            text = { Text(if (contact.isArchived) "Move this conversation back to your chat list?" else "Move this conversation to Archived?") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        database.contactDao().upsert(contact.copy(isArchived = !contact.isArchived))
                        onNotify(if (contact.isArchived) "Chat unarchived" else "Chat archived")
                    }
                    selectedContact = null
                }) { Text(if (contact.isArchived) "Unarchive" else "Archive") }
            },
            dismissButton = { TextButton(onClick = { selectedContact = null }) { Text("Cancel") } }
        )
    }
}

@Composable
private fun EmptyHome(onAdd: () -> Unit, onManage: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(28.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Default.Chat, null, tint = AppMuted, modifier = Modifier.size(54.dp))
        Spacer(Modifier.height(14.dp))
        Text("Your chats will appear here", color = AppText, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        Text("Save a contact, then build a conversation with messages, replies and reactions.", color = AppMuted, fontSize = 14.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        Spacer(Modifier.height(20.dp))
        Button(onClick = onAdd, colors = ButtonDefaults.buttonColors(containerColor = AppAccent)) { Icon(Icons.Default.Add, null); Spacer(Modifier.width(8.dp)); Text("Add contact") }
        TextButton(onClick = onManage) { Text("Manage contacts") }
    }
}

@Composable
private fun PlaceholderTab(title: String, description: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onNotify: (String) -> Unit) {
    Column(Modifier.fillMaxSize().padding(30.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(icon, null, tint = AppMuted, modifier = Modifier.size(48.dp))
        Spacer(Modifier.height(14.dp))
        Text(title, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        Text(description, color = AppMuted, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        Spacer(Modifier.height(16.dp))
        OutlinedButton(onClick = { onNotify("This section is reserved for future local views.") }) { Text("Got it") }
    }
}

@Composable
private fun ChatListRow(
    contact: ContactEntity,
    database: WhatsuppDatabase,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    var lastMessage by remember(contact.id) { mutableStateOf<MessageEntity?>(null) }
    LaunchedEffect(contact.id) { lastMessage = database.messageDao().latestForContact(contact.id) }
    val unread = lastMessage?.let { !it.outgoing && !it.isDeleted } == true
    Row(
        Modifier.fillMaxWidth().combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ContactAvatar(contact, 54.dp)
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    contact.name,
                    color = AppText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    lastMessage?.let { ChatFormatting.time(it.timestamp) } ?: ChatFormatting.time(contact.createdAt),
                    color = if (unread) AppAccent else AppMuted,
                    fontSize = 11.sp
                )
            }
            Spacer(Modifier.height(5.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (lastMessage?.outgoing == true) {
                    Text(
                        when (lastMessage?.status) {
                            "READ", "DELIVERED" -> "✓✓"
                            else -> "✓"
                        },
                        color = if (lastMessage?.status == "READ") Color(0xFF53BDEB) else AppMuted,
                        fontSize = 12.sp
                    )
                    Spacer(Modifier.width(5.dp))
                }
                Text(
                    lastMessage?.let {
                        if (it.isDeleted) "This message was deleted"
                        else ChatFormatting.preview(it.body, it.kind)
                    } ?: contact.about,
                    color = if (unread) AppText else AppMuted,
                    fontSize = 13.sp,
                    fontWeight = if (unread) FontWeight.Medium else FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                if (unread) {
                    Box(Modifier.padding(start = 8.dp).size(19.dp).clip(CircleShape).background(AppAccent), contentAlignment = Alignment.Center) {
                        Text("1", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                } else if (contact.isPinned) {
                    Icon(Icons.Default.PushPin, null, tint = AppMuted, modifier = Modifier.padding(start = 7.dp).size(13.dp))
                }
            }
        }
    }
}

@Composable
private fun ContactAvatar(contact: ContactEntity, size: androidx.compose.ui.unit.Dp) {
    if (!contact.avatarUri.isNullOrBlank()) {
        AsyncImage(model = contact.avatarUri, contentDescription = "${contact.name} profile photo", modifier = Modifier.size(size).clip(CircleShape), contentScale = ContentScale.Crop)
    } else {
        Box(Modifier.size(size).clip(CircleShape).background(Color(0xFF3B2920)), contentAlignment = Alignment.Center) {
            Text(contact.name.trim().firstOrNull()?.uppercase() ?: "?", color = Color(0xFFE7B18F), fontSize = (size.value * 0.43f).sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun ContactsScreen(
    contacts: List<ContactEntity>, onBack: () -> Unit, onAdd: () -> Unit,
    onOpen: (ContactEntity) -> Unit, onEdit: (ContactEntity) -> Unit, onDelete: (ContactEntity) -> Unit
) {
    var menuContact by remember { mutableStateOf<ContactEntity?>(null) }
    Column(Modifier.fillMaxSize().background(AppBg)) {
        Row(Modifier.fillMaxWidth().height(58.dp).background(AppSurface2).padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
            Text("Contacts", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            IconButton(onClick = onAdd) { Icon(Icons.Default.Add, null) }
        }
        Text("SAVED IN WHATSUPP", color = AppMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 18.dp, top = 18.dp, bottom = 8.dp))
        LazyColumn {
            items(contacts, key = { it.id }) { contact ->
                Row(Modifier.fillMaxWidth().combinedClickable(onClick = { onOpen(contact) }, onLongClick = { menuContact = contact }).padding(horizontal = 16.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                    ContactAvatar(contact, 48.dp)
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text(contact.name, color = AppText, fontWeight = FontWeight.SemiBold)
                        Text(contact.phone.ifBlank { contact.about }, color = AppMuted, fontSize = 12.sp)
                    }
                    IconButton(onClick = { menuContact = contact }) { Icon(Icons.Default.MoreVert, null, tint = AppMuted) }
                }
            }
        }
    }
    menuContact?.let { contact ->
        AlertDialog(
            onDismissRequest = { menuContact = null },
            title = { Text(contact.name) },
            text = { Text("Edit this local contact or remove it from Whatsupp.") },
            confirmButton = { TextButton(onClick = { menuContact = null; onEdit(contact) }) { Text("Edit") } },
            dismissButton = { Row { TextButton(onClick = { menuContact = null; onDelete(contact) }) { Text("Delete", color = Color(0xFFFF6B6B)) }; TextButton(onClick = { menuContact = null }) { Text("Close") } } }
        )
    }
}

@Composable
private fun ChatScreen(
    contact: ContactEntity, database: WhatsuppDatabase, onBack: () -> Unit,
    onProfile: () -> Unit, onNotify: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    val messages by database.messageDao().observeForContact(contact.id).collectAsState(initial = emptyList())
    var composer by remember { mutableStateOf("") }
    var showMessageEditor by remember { mutableStateOf(false) }
    var showBatchEditor by remember { mutableStateOf(false) }
    var editingMessage by remember { mutableStateOf<MessageEntity?>(null) }
    var editorDefaultOutgoing by remember { mutableStateOf(true) }
    var replyTarget by remember { mutableStateOf<MessageEntity?>(null) }
    var selectedMessage by remember { mutableStateOf<MessageEntity?>(null) }
    var showAttachMenu by remember { mutableStateOf(false) }
    var showChatMenu by remember { mutableStateOf(false) }
    var showSearchMessages by remember { mutableStateOf(false) }
    var showEmojiPicker by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val context = LocalContext.current
    val wallpaperPreferences = remember(contact.id) {
        context.getSharedPreferences("whatsupp-chat-settings", Context.MODE_PRIVATE)
    }
    var wallpaperUri by remember(contact.id) {
        mutableStateOf(wallpaperPreferences.getString("wallpaper_${contact.id}", null))
    }
    val localView = LocalView.current
    val wallpaperPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (_: Exception) { }
            wallpaperUri = uri.toString()
            wallpaperPreferences.edit().putString("wallpaper_${contact.id}", uri.toString()).apply()
            onNotify("Chat wallpaper saved for ${contact.name}")
        }
    }
    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("image/png")) { uri ->
        if (uri != null) {
            try {
                val width = localView.width.coerceAtLeast(1)
                val height = localView.height.coerceAtLeast(1)
                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                localView.draw(Canvas(bitmap))
                context.contentResolver.openOutputStream(uri)?.use { output ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
                }
                bitmap.recycle()
                onNotify("Visible chat screen exported as PNG")
            } catch (_: Exception) {
                onNotify("Could not export this chat screen")
            }
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.lastIndex)
    }

    val attachLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                database.messageDao().insert(MessageEntity(
                    id = UUID.randomUUID().toString(), contactId = contact.id,
                    body = "", kind = "IMAGE", attachmentUri = uri.toString(), outgoing = true,
                    timestamp = System.currentTimeMillis(), status = "READ"
                ))
                onNotify("Photo added to the local conversation")
            }
        }
    }
    val audioLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                database.messageDao().insert(MessageEntity(
                    id = UUID.randomUUID().toString(), contactId = contact.id,
                    body = "", kind = "AUDIO", attachmentUri = uri.toString(), outgoing = true,
                    timestamp = System.currentTimeMillis(), status = "READ"
                ))
                onNotify("Local audio file added as a voice-message bubble")
            }
        }
    }

    Column(Modifier.fillMaxSize().background(AppBg)) {
        Row(Modifier.fillMaxWidth().height(60.dp).background(AppSurface2).padding(horizontal = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
            Row(Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).combinedClickable(onClick = onProfile).padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                ContactAvatar(contact, 40.dp)
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(contact.name, color = AppText, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                    Text(contact.about.ifBlank { "Available" }, color = AppMuted, fontSize = 11.sp)
                }
            }
            IconButton(onClick = { onNotify("Voice calls are not connected in Whatsupp.") }) { Icon(Icons.Default.Videocam, null, tint = AppText) }
            IconButton(onClick = { onNotify("Calls are local only. No call was placed.") }) { Icon(Icons.Default.Call, null, tint = AppText) }
            Box {
                IconButton(onClick = { showChatMenu = true }) { Icon(Icons.Default.MoreVert, null, tint = AppText) }
                androidx.compose.material3.DropdownMenu(
                    expanded = showChatMenu,
                    onDismissRequest = { showChatMenu = false }
                ) {
                    androidx.compose.material3.DropdownMenuItem(
                        text = { Text("Wallpaper") },
                        leadingIcon = { Icon(Icons.Default.Wallpaper, null) },
                        onClick = {
                            showChatMenu = false
                            wallpaperPicker.launch(androidx.activity.result.PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        }
                    )
                    if (!wallpaperUri.isNullOrBlank()) {
                        androidx.compose.material3.DropdownMenuItem(
                            text = { Text("Remove wallpaper") },
                            leadingIcon = { Icon(Icons.Default.Delete, null) },
                            onClick = {
                                showChatMenu = false
                                wallpaperUri = null
                                wallpaperPreferences.edit().remove("wallpaper_${contact.id}").apply()
                                onNotify("Default chat wallpaper restored")
                            }
                        )
                    }
                    androidx.compose.material3.DropdownMenuItem(
                        text = { Text("Search in chat") },
                        leadingIcon = { Icon(Icons.Default.Search, null) },
                        onClick = { showChatMenu = false; showSearchMessages = true }
                    )
                    androidx.compose.material3.DropdownMenuItem(
                        text = { Text("Save chat image") },
                        leadingIcon = { Icon(Icons.Default.CameraAlt, null) },
                        onClick = {
                            showChatMenu = false
                            exportLauncher.launch("Whatsupp-${contact.name.replace(Regex("[^A-Za-z0-9_-]"), "_")}-chat.png")
                        }
                    )
                    androidx.compose.material3.DropdownMenuItem(
                        text = { Text("Contact info") },
                        leadingIcon = { Icon(Icons.Default.Info, null) },
                        onClick = { showChatMenu = false; onProfile() }
                    )
                }
            }
        }
        Box(Modifier.weight(1f).fillMaxWidth()) {
            if (!wallpaperUri.isNullOrBlank()) {
                AsyncImage(
                    model = wallpaperUri,
                    contentDescription = "Chat wallpaper",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alpha = if (ThemeState.isDark) 0.42f else 0.56f
                )
                Box(
                    Modifier.fillMaxSize().background(
                        if (ThemeState.isDark) Color.Black.copy(alpha = 0.26f) else Color.White.copy(alpha = 0.24f)
                    )
                )
            } else {
                Box(
                    Modifier.fillMaxSize().background(AppBg).drawBehind {
                        val line = if (ThemeState.isDark) Color(0xFF26343B) else Color(0xFFDCE2E5)
                        val stepX = 116.dp.toPx()
                        val stepY = 138.dp.toPx()
                        val radius = 13.dp.toPx()
                        var x = 28.dp.toPx()
                        while (x < size.width) {
                            var y = 30.dp.toPx()
                            while (y < size.height) {
                                drawCircle(line, radius = radius, center = Offset(x, y), style = Stroke(width = 1.dp.toPx()))
                                drawLine(line, Offset(x - radius, y + radius), Offset(x + radius, y + radius * 1.7f), strokeWidth = 1.dp.toPx())
                                drawLine(line, Offset(x + radius * 1.5f, y - radius), Offset(x + radius * 2.3f, y - radius * 0.3f), strokeWidth = 1.dp.toPx())
                                y += stepY
                            }
                            x += stepX
                        }
                    }
                )
            }
            if (messages.isEmpty()) {
                Column(
                    Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("No messages yet", color = AppText)
                    Text("Add messages to build this conversation.", color = AppMuted, fontSize = 12.sp)
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { editingMessage = null; editorDefaultOutgoing = true; showMessageEditor = true }, colors = ButtonDefaults.buttonColors(containerColor = AppAccent)) { Text("Create first message") }
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 9.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    itemsIndexed(messages, key = { _, item -> item.id }) { index, message ->
                        val dateLabel = ChatFormatting.dateLabel(message.timestamp)
                        if (index == 0 || ChatFormatting.dateLabel(messages[index - 1].timestamp) != dateLabel) {
                            DateSeparator(dateLabel)
                        }
                        MessageBubble(
                            message = message,
                            reply = messages.firstOrNull { it.id == message.replyToId },
                            onLongClick = { selectedMessage = message }
                        )
                    }
                }
            }
        }
        if (replyTarget != null) {
            Row(Modifier.fillMaxWidth().background(AppSurface2).padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Replying to ${if (replyTarget!!.outgoing) "You" else contact.name}", color = AppAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text(ChatFormatting.preview(replyTarget!!.body, replyTarget!!.kind), color = AppMuted, fontSize = 12.sp, maxLines = 1)
                }
                IconButton(onClick = { replyTarget = null }) { Icon(Icons.Default.Close, null, tint = AppMuted) }
            }
        }
        Row(Modifier.fillMaxWidth().background(AppBg).padding(horizontal = 7.dp, vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { showEmojiPicker = true }) { Icon(Icons.Default.EmojiEmotions, null, tint = AppMuted) }
            OutlinedTextField(
                value = composer,
                onValueChange = { composer = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Message", color = AppMuted) },
                shape = RoundedCornerShape(26.dp),
                maxLines = 4,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                trailingIcon = {
                    Box {
                        IconButton(onClick = { showAttachMenu = true }) { Icon(Icons.Default.AttachFile, null, tint = AppMuted) }
                        androidx.compose.material3.DropdownMenu(expanded = showAttachMenu, onDismissRequest = { showAttachMenu = false }) {
                            androidx.compose.material3.DropdownMenuItem(text = { Text("Photo or video") }, onClick = { showAttachMenu = false; attachLauncher.launch("image/*") }, leadingIcon = { Icon(Icons.Default.Image, null) })
                            androidx.compose.material3.DropdownMenuItem(text = { Text("Create message with details") }, onClick = { showAttachMenu = false; editingMessage = null; editorDefaultOutgoing = true; showMessageEditor = true }, leadingIcon = { Icon(Icons.Default.Edit, null) })
                            androidx.compose.material3.DropdownMenuItem(text = { Text("Add contact-side reply") }, onClick = { showAttachMenu = false; editingMessage = null; editorDefaultOutgoing = false; showMessageEditor = true }, leadingIcon = { Icon(Icons.Default.Contacts, null) })
                            androidx.compose.material3.DropdownMenuItem(text = { Text("Add multiple messages") }, onClick = { showAttachMenu = false; showBatchEditor = true }, leadingIcon = { Icon(Icons.Default.Chat, null) })
                        }
                    }
                }
            )
            Spacer(Modifier.width(5.dp))
            IconButton(
                onClick = {
                    if (composer.isNotBlank()) {
                        scope.launch {
                            database.messageDao().insert(MessageEntity(
                                id = UUID.randomUUID().toString(), contactId = contact.id,
                                body = composer.trim(), outgoing = true,
                                timestamp = System.currentTimeMillis(), status = "READ",
                                replyToId = replyTarget?.id
                            ))
                            composer = ""
                            replyTarget = null
                        }
                    } else {
                        audioLauncher.launch("audio/*")
                    }
                },
                modifier = Modifier.size(48.dp).clip(CircleShape).background(AppAccent)
            ) { Icon(if (composer.isBlank()) Icons.Default.Mic else Icons.AutoMirrored.Filled.Send, null, tint = Color.White) }
        }
        Row(Modifier.fillMaxWidth().background(Color(0xFF101A20)).padding(vertical = 3.dp), horizontalArrangement = Arrangement.Center) {
            Text("Whatsupp · Local composer", color = AppMuted, fontSize = 9.sp, letterSpacing = 0.2.sp)
        }
    }

    if (showMessageEditor) {
        MessageEditorDialog(
            contact = contact,
            existing = editingMessage,
            initialReplyTo = replyTarget?.id,
            initialOutgoing = editorDefaultOutgoing,
            onDismiss = { showMessageEditor = false; editingMessage = null },
            onSave = { edited ->
                scope.launch { database.messageDao().insert(edited) }
                showMessageEditor = false
                editingMessage = null
                replyTarget = null
            }
        )
    }
    if (showBatchEditor) {
        BatchMessageEditorDialog(
            contact = contact,
            onDismiss = { showBatchEditor = false },
            onSave = { batch ->
                scope.launch {
                    batch.forEach { database.messageDao().insert(it) }
                    onNotify("${batch.size} messages added")
                }
                showBatchEditor = false
            }
        )
    }
    selectedMessage?.let { message ->
        MessageActionsDialog(
            message = message,
            onDismiss = { selectedMessage = null },
            onEdit = { selectedMessage = null; editingMessage = message; showMessageEditor = true },
            onReply = { selectedMessage = null; replyTarget = message },
            onReaction = { emoji ->
                scope.launch { database.messageDao().update(message.copy(reactions = ChatFormatting.toggleReaction(message.reactions, emoji))) }
                selectedMessage = null
            },
            onDelete = {
                scope.launch { database.messageDao().delete(message.id) }
                selectedMessage = null
            },
            onChangeDetails = {
                selectedMessage = null
                editingMessage = message
                showMessageEditor = true
            },
            onStatusChange = { status ->
                scope.launch { database.messageDao().update(message.copy(status = status)) }
                selectedMessage = null
            }
        )
    }
    if (showEmojiPicker) {
        EmojiPickerDialog(onDismiss = { showEmojiPicker = false }, onSelect = { emoji -> composer += emoji; showEmojiPicker = false })
    }
    if (showSearchMessages) {
        SearchMessagesDialog(messages = messages, contactName = contact.name, onDismiss = { showSearchMessages = false })
    }
}

@Composable
private fun MessageBubble(message: MessageEntity, reply: MessageEntity?, onLongClick: () -> Unit) {
    if (message.isDeleted) return
    Row(Modifier.fillMaxWidth(), horizontalArrangement = if (message.outgoing) Arrangement.End else Arrangement.Start) {
        Column(Modifier.widthIn(max = 310.dp).clip(RoundedCornerShape(15.dp)).background(if (message.outgoing) OutgoingBubble else IncomingBubble)
            .combinedClickable(onClick = {}, onLongClick = onLongClick).padding(horizontal = 10.dp, vertical = 7.dp)) {
            if (reply != null) {
                Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(Color(0x33333333)).padding(8.dp)) {
                    Text(if (reply.outgoing) "You" else "Reply", color = Color(0xFF9FD6C5), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text(ChatFormatting.preview(reply.body, reply.kind), color = Color(0xFFCAD6D7), fontSize = 12.sp, maxLines = 2)
                }
                Spacer(Modifier.height(5.dp))
            }
            if (!message.attachmentUri.isNullOrBlank()) {
                when (message.kind) {
                    "IMAGE" -> AsyncImage(model = message.attachmentUri, contentDescription = "Photo attachment", modifier = Modifier.fillMaxWidth().heightIn(max = 230.dp).clip(RoundedCornerShape(9.dp)), contentScale = ContentScale.Crop)
                    "VIDEO" -> AttachmentPreview(Icons.Default.Videocam, "Video attachment")
                    "AUDIO" -> AttachmentPreview(Icons.Default.Mic, "Voice message")
                    else -> AttachmentPreview(Icons.Default.AttachFile, "Document attachment")
                }
                if (message.body.isNotBlank()) Spacer(Modifier.height(5.dp))
            }
            if (message.body.isNotBlank()) Text(message.body, color = AppText, fontSize = 15.sp, lineHeight = 20.sp)
            if (message.body.isBlank() && message.attachmentUri.isNullOrBlank()) Text("[empty message]", color = AppMuted, fontSize = 12.sp)
            Row(Modifier.fillMaxWidth().padding(top = 3.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.End) {
                if (message.edited) Text("edited  ", color = AppMuted, fontSize = 9.sp)
                Text(ChatFormatting.time(message.timestamp), color = Color(0xFFAFBFC2), fontSize = 9.sp)
                if (message.outgoing) {
                    Spacer(Modifier.width(3.dp))
                    Text(
                        when (message.status) {
                            "READ", "DELIVERED" -> "✓✓"
                            else -> "✓"
                        },
                        color = if (message.status == "READ") Color(0xFF53BDEB) else AppMuted,
                        fontSize = 11.sp
                    )
                }
            }
            val reactionList = ChatFormatting.reactions(message.reactions)
            if (reactionList.isNotEmpty()) {
                Spacer(Modifier.height(5.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    reactionList.forEach { emoji -> Text(emoji, modifier = Modifier.clip(CircleShape).background(Color(0x44242F35)).padding(horizontal = 7.dp, vertical = 2.dp), fontSize = 13.sp) }
                }
            }
        }
    }
}

@Composable
private fun DateSeparator(label: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.Center) {
        Text(label, color = Color(0xFFC3D3D3), fontSize = 10.sp, modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Color(0xFF23343B)).padding(horizontal = 10.dp, vertical = 5.dp))
    }
}

@Composable
private fun AttachmentPreview(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(9.dp)).background(Color(0x33202C33)).padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = AppAccent, modifier = Modifier.size(25.dp))
        Spacer(Modifier.width(10.dp))
        Text(label, color = AppText, fontSize = 13.sp)
    }
}

@Composable
private fun MessageActionsDialog(
    message: MessageEntity, onDismiss: () -> Unit, onEdit: () -> Unit,
    onReply: () -> Unit, onReaction: (String) -> Unit, onDelete: () -> Unit,
    onChangeDetails: () -> Unit, onStatusChange: (String) -> Unit
) {
    val emojis = listOf("❤️", "😂", "👍", "😮", "😢", "🙏", "🔥", "🥹", "💀", "👏", "🤝", "✨")
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Message options") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("React", color = AppMuted, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                emojis.chunked(6).forEach { line ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        line.forEach { emoji -> TextButton(onClick = { onReaction(emoji) }, contentPadding = PaddingValues(horizontal = 4.dp)) { Text(emoji, fontSize = 20.sp) } }
                    }
                }
                TextButton(onClick = onReply) { Text("Reply") }
                TextButton(onClick = onEdit) { Text("Edit message") }
                TextButton(onClick = onChangeDetails) { Text("Edit sender, time, type and attachment") }
                if (message.outgoing) {
                    Text("Delivery status", color = AppMuted, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        FilterChip(selected = message.status == "SENT", onClick = { onStatusChange("SENT") }, label = { Text("Sent ✓", fontSize = 10.sp) })
                        FilterChip(selected = message.status == "DELIVERED", onClick = { onStatusChange("DELIVERED") }, label = { Text("Delivered ✓✓", fontSize = 10.sp) })
                    }
                    FilterChip(selected = message.status == "READ", onClick = { onStatusChange("READ") }, label = { Text("Seen · blue ✓✓", fontSize = 10.sp) })
                }
                TextButton(onClick = onDelete) { Text("Delete message", color = Color(0xFFFF7777)) }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Close") } }
    )
}

@Composable
private fun EmojiPickerDialog(onDismiss: () -> Unit, onSelect: (String) -> Unit) {
    val emojis = listOf("😀", "😂", "🥹", "😍", "😘", "😎", "😭", "😡", "🤔", "😮", "❤️", "🖤", "🔥", "👍", "👎", "🙏", "💀", "✨", "🥲", "🙃")
    AlertDialog(onDismissRequest = onDismiss, title = { Text("Choose emoji") }, text = {
        Column {
            emojis.chunked(5).forEach { row ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    row.forEach { emoji -> TextButton(onClick = { onSelect(emoji) }, contentPadding = PaddingValues(3.dp)) { Text(emoji, fontSize = 23.sp) } }
                }
            }
        }
    }, confirmButton = { TextButton(onClick = onDismiss) { Text("Close") } })
}

private data class BatchMessageDraft(
    val id: String = UUID.randomUUID().toString(),
    val body: String = "",
    val outgoing: Boolean = true,
    val dateTime: String = ChatFormatting.dateTimeForEditor(System.currentTimeMillis()),
    val status: String = "READ"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BatchMessageEditorDialog(
    contact: ContactEntity,
    onDismiss: () -> Unit,
    onSave: (List<MessageEntity>) -> Unit
) {
    var drafts by remember { mutableStateOf(listOf(BatchMessageDraft(), BatchMessageDraft(outgoing = false))) }
    var error by remember { mutableStateOf<String?>(null) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add messages") },
        text = {
            Column(
                Modifier.fillMaxWidth().heightIn(max = 520.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Build a sequence. Each row becomes a separate message in this chat.", color = AppMuted, fontSize = 12.sp)
                drafts.forEachIndexed { index, draft ->
                    Column(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(AppSurface2).padding(11.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Message ${index + 1}", color = AppAccent, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                            if (drafts.size > 1) {
                                IconButton(onClick = { drafts = drafts.filterIndexed { i, _ -> i != index } }, modifier = Modifier.size(30.dp)) {
                                    Icon(Icons.Default.Delete, null, tint = AppMuted)
                                }
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                            FilterChip(selected = draft.outgoing, onClick = { drafts = drafts.toMutableList().also { it[index] = draft.copy(outgoing = true) } }, label = { Text("You") })
                            FilterChip(selected = !draft.outgoing, onClick = { drafts = drafts.toMutableList().also { it[index] = draft.copy(outgoing = false) } }, label = { Text(contact.name, maxLines = 1, overflow = TextOverflow.Ellipsis) })
                        }
                        OutlinedTextField(
                            value = draft.body,
                            onValueChange = { value -> drafts = drafts.toMutableList().also { it[index] = draft.copy(body = value) } },
                            label = { Text("Message text") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2,
                            maxLines = 4
                        )
                        OutlinedTextField(
                            value = draft.dateTime,
                            onValueChange = { value -> drafts = drafts.toMutableList().also { it[index] = draft.copy(dateTime = value) } },
                            label = { Text("Date and time") },
                            supportingText = { Text("yyyy-MM-dd HH:mm") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (draft.outgoing) {
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                listOf("SENT", "DELIVERED", "READ").forEach { status ->
                                    FilterChip(
                                        selected = draft.status == status,
                                        onClick = { drafts = drafts.toMutableList().also { it[index] = draft.copy(status = status) } },
                                        label = { Text(when (status) { "READ" -> "Seen"; "DELIVERED" -> "Delivered"; else -> "Sent" }, fontSize = 10.sp) }
                                    )
                                }
                            }
                        }
                    }
                }
                OutlinedButton(
                    onClick = { drafts = drafts + BatchMessageDraft(outgoing = drafts.lastOrNull()?.outgoing?.not() ?: false) },
                    modifier = Modifier.fillMaxWidth()
                ) { Icon(Icons.Default.Add, null); Spacer(Modifier.width(6.dp)); Text("Add another message") }
                error?.let { Text(it, color = Color(0xFFFF7777), fontSize = 12.sp) }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (drafts.any { it.body.isBlank() }) {
                    error = "Fill in every message or remove the empty row."
                } else {
                    val batch = drafts.map { draft ->
                        MessageEntity(
                            id = UUID.randomUUID().toString(),
                            contactId = contact.id,
                            body = draft.body.trim(),
                            outgoing = draft.outgoing,
                            timestamp = ChatFormatting.parseDateTime(draft.dateTime),
                            status = if (draft.outgoing) draft.status else "READ"
                        )
                    }
                    onSave(batch)
                }
            }) { Text("Add all ${drafts.size}") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MessageEditorDialog(
    contact: ContactEntity,
    existing: MessageEntity?,
    initialReplyTo: String?,
    initialOutgoing: Boolean,
    onDismiss: () -> Unit,
    onSave: (MessageEntity) -> Unit
) {
    val context = LocalContext.current
    var body by remember { mutableStateOf(existing?.body.orEmpty()) }
    var dateTime by remember { mutableStateOf(ChatFormatting.dateTimeForEditor(existing?.timestamp ?: System.currentTimeMillis())) }
    var outgoing by remember { mutableStateOf(existing?.outgoing ?: initialOutgoing) }
    var kind by remember { mutableStateOf(existing?.kind ?: "TEXT") }
    var attachmentUri by remember { mutableStateOf(existing?.attachmentUri) }
    var status by remember { mutableStateOf(existing?.status ?: "READ") }
    var showKindMenu by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val mediaPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            attachmentUri = uri.toString()
            val mime = context.contentResolver.getType(uri)
            kind = if (mime?.startsWith("video/") == true) "VIDEO" else "IMAGE"
            try { context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION) } catch (_: Exception) { }
        }
    }
    val filePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            attachmentUri = uri.toString()
            if (kind == "TEXT") {
                val mime = context.contentResolver.getType(uri).orEmpty()
                kind = when {
                    mime.startsWith("audio/") -> "AUDIO"
                    mime.startsWith("video/") -> "VIDEO"
                    mime.startsWith("image/") -> "IMAGE"
                    else -> "FILE"
                }
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existing == null) "Create message" else "Edit message") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Who is speaking?", color = AppMuted, fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = outgoing, onClick = { outgoing = true }, label = { Text("You") })
                    FilterChip(selected = !outgoing, onClick = { outgoing = false }, label = { Text(contact.name, maxLines = 1, overflow = TextOverflow.Ellipsis) })
                }
                OutlinedTextField(value = body, onValueChange = { body = it }, label = { Text("Message text") }, modifier = Modifier.fillMaxWidth(), minLines = 2, maxLines = 5)
                OutlinedTextField(value = dateTime, onValueChange = { dateTime = it }, label = { Text("Date and time") }, supportingText = { Text("Format: yyyy-MM-dd HH:mm") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box {
                        OutlinedButton(onClick = { showKindMenu = true }) { Text("Type: $kind") }
                        androidx.compose.material3.DropdownMenu(expanded = showKindMenu, onDismissRequest = { showKindMenu = false }) {
                            listOf("TEXT", "IMAGE", "VIDEO", "AUDIO", "FILE").forEach { item ->
                                androidx.compose.material3.DropdownMenuItem(text = { Text(item) }, onClick = { kind = item; showKindMenu = false })
                            }
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                    OutlinedButton(onClick = { mediaPicker.launch(androidx.activity.result.PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)) }) {
                        Icon(Icons.Default.Image, null); Spacer(Modifier.width(5.dp)); Text("Media")
                    }
                    IconButton(onClick = { filePicker.launch("*/*") }) { Icon(Icons.Default.AttachFile, null) }
                }
                if (!attachmentUri.isNullOrBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Attachment selected", color = AppAccent, fontSize = 12.sp, modifier = Modifier.weight(1f))
                        TextButton(onClick = { attachmentUri = null }) { Text("Remove") }
                    }
                }
                Text("Message status", color = AppMuted, fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf("SENT", "DELIVERED", "READ").forEach { item ->
                        FilterChip(selected = status == item, onClick = { status = item }, label = { Text(item.lowercase().replaceFirstChar { it.uppercase() }, fontSize = 10.sp) })
                    }
                }
                error?.let { Text(it, color = Color(0xFFFF7777), fontSize = 12.sp) }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (body.isBlank() && attachmentUri.isNullOrBlank()) {
                    error = "Add message text or choose an attachment."
                } else {
                    val parsed = ChatFormatting.parseDateTime(dateTime)
                    onSave(MessageEntity(
                        id = existing?.id ?: UUID.randomUUID().toString(),
                        contactId = contact.id,
                        body = body.trim(),
                        kind = kind,
                        attachmentUri = attachmentUri,
                        outgoing = outgoing,
                        timestamp = parsed,
                        status = status,
                        replyToId = initialReplyTo ?: existing?.replyToId,
                        edited = existing != null,
                        reactions = existing?.reactions.orEmpty()
                    ))
                }
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun ContactEditorDialog(existing: ContactEntity?, onDismiss: () -> Unit, onSave: (ContactEntity) -> Unit) {
    val context = LocalContext.current
    var name by remember { mutableStateOf(existing?.name.orEmpty()) }
    var phone by remember { mutableStateOf(existing?.phone.orEmpty()) }
    var about by remember { mutableStateOf(existing?.about ?: "Available") }
    var avatarUri by remember { mutableStateOf(existing?.avatarUri) }
    var isPinned by remember { mutableStateOf(existing?.isPinned ?: false) }
    var error by remember { mutableStateOf<String?>(null) }
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            avatarUri = uri.toString()
            try { context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION) } catch (_: Exception) { }
        }
    }
    val phonePicker = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val uri = result.data?.data
        if (result.resultCode == android.app.Activity.RESULT_OK && uri != null) {
            try {
                val projection = arrayOf(
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.Contacts.PHOTO_THUMBNAIL_URI
                )
                context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                        val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                        val photoIndex = cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI)
                        if (nameIndex >= 0) name = cursor.getString(nameIndex).orEmpty()
                        if (numberIndex >= 0) phone = cursor.getString(numberIndex).orEmpty()
                        if (photoIndex >= 0) avatarUri = cursor.getString(photoIndex)
                    }
                }
                try { context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION) } catch (_: Exception) { }
            } catch (_: Exception) {
                error = "Could not import that contact. Enter the details manually."
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existing == null) "Save a contact" else "Edit contact") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                if (!avatarUri.isNullOrBlank()) AsyncImage(model = avatarUri, contentDescription = "Profile photo", modifier = Modifier.size(76.dp).clip(CircleShape), contentScale = ContentScale.Crop)
                else Box(Modifier.size(76.dp).clip(CircleShape).background(Color(0xFF3B2920)), contentAlignment = Alignment.Center) { Icon(Icons.Default.AccountCircle, null, tint = Color(0xFFE7B18F), modifier = Modifier.size(48.dp)) }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { imagePicker.launch(androidx.activity.result.PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }) { Icon(Icons.Default.Image, null); Spacer(Modifier.width(5.dp)); Text("Choose photo") }
                    OutlinedButton(onClick = { phonePicker.launch(Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)) }) { Icon(Icons.Default.Contacts, null); Spacer(Modifier.width(5.dp)); Text("Phone contacts") }
                }
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Display name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone number (optional)") }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = about, onValueChange = { about = it }, label = { Text("About / status") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                FilterChip(selected = isPinned, onClick = { isPinned = !isPinned }, label = { Text(if (isPinned) "★ Favorite contact" else "☆ Add to favorites") })
                error?.let { Text(it, color = Color(0xFFFF7777), fontSize = 12.sp) }
                Text("Only a local Whatsupp contact is created. No message or call can be made.", color = AppMuted, fontSize = 10.sp)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (name.isBlank()) error = "Enter a display name."
                else onSave(ContactEntity(
                    id = existing?.id ?: UUID.randomUUID().toString(),
                    name = name.trim(), phone = phone.trim(), avatarUri = avatarUri,
                    about = about.trim().ifBlank { "Available" },
                    createdAt = existing?.createdAt ?: System.currentTimeMillis(),
                    isPinned = isPinned,
                    notificationsEnabled = existing?.notificationsEnabled ?: true,
                    mediaVisibility = existing?.mediaVisibility ?: true,
                    disappearingMessages = existing?.disappearingMessages ?: false,
                    chatLocked = existing?.chatLocked ?: false,
                    isArchived = existing?.isArchived ?: false
                ))
            }) { Text("Save contact") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun ContactInfoScreen(
    contact: ContactEntity, database: WhatsuppDatabase, onBack: () -> Unit,
    onEdit: () -> Unit, onNotify: (String) -> Unit
) {
    val messages by database.messageDao().observeForContact(contact.id).collectAsState(initial = emptyList())
    val attachments = messages.filter { !it.attachmentUri.isNullOrBlank() }
    val media = attachments.filter { it.kind == "IMAGE" }
    val scope = rememberCoroutineScope()
    var notifications by remember(contact.id) { mutableStateOf(contact.notificationsEnabled) }
    var mediaVisible by remember(contact.id) { mutableStateOf(contact.mediaVisibility) }
    var chatLock by remember(contact.id) { mutableStateOf(contact.chatLocked) }
    var disappearing by remember(contact.id) { mutableStateOf(contact.disappearingMessages) }
    var pinned by remember(contact.id) { mutableStateOf(contact.isPinned) }
    var showSearch by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().background(AppBg)) {
        Row(Modifier.fillMaxWidth().height(56.dp).background(AppBg).padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
            Spacer(Modifier.weight(1f))
            IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, null) }
            IconButton(onClick = { onNotify("Contact details are stored locally.") }) { Icon(Icons.Default.MoreVert, null) }
        }
        LazyColumn(Modifier.fillMaxSize()) {
            item {
                Column(Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    if (!contact.avatarUri.isNullOrBlank()) AsyncImage(model = contact.avatarUri, contentDescription = "${contact.name} profile photo", modifier = Modifier.size(176.dp).clip(CircleShape), contentScale = ContentScale.Crop)
                    else Box(Modifier.size(176.dp).clip(CircleShape).background(Color(0xFF3B2920)), contentAlignment = Alignment.Center) {
                        Text(contact.name.firstOrNull()?.uppercase() ?: "?", color = Color(0xFFE7B18F), fontSize = 70.sp)
                    }
                    Spacer(Modifier.height(14.dp))
                    Text(contact.name, color = AppText, fontSize = 28.sp, fontWeight = FontWeight.Normal)
                    Text(contact.phone, color = AppMuted, fontSize = 16.sp)
                    Spacer(Modifier.height(12.dp))
                    Text(contact.about, color = AppMuted, fontSize = 14.sp)
                    Spacer(Modifier.height(22.dp))
                    Row(Modifier.fillMaxWidth().padding(horizontal = 26.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                        ProfileAction(Icons.Default.Call, "Voice") { onNotify("Voice calls are not connected.") }
                        ProfileAction(Icons.Default.Videocam, "Video") { onNotify("Video calls are not connected.") }
                        ProfileAction(Icons.Default.AttachMoney, "Pay") { onNotify("Payments are not available in this offline conversation editor.") }
                        ProfileAction(Icons.Default.Search, "Search") { showSearch = true }
                    }
                }
            }
            item {
                Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("Media, links, and docs", color = AppMuted, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                    Text(attachments.size.toString(), color = AppMuted)
                    Icon(Icons.Default.KeyboardArrowRight, null, tint = AppMuted)
                }
            }
            if (mediaVisible && media.isNotEmpty()) {
                item {
                    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                        media.take(4).forEach { item ->
                            AsyncImage(model = item.attachmentUri, contentDescription = "Shared media", modifier = Modifier.weight(1f).height(86.dp).clip(RoundedCornerShape(10.dp)), contentScale = ContentScale.Crop)
                        }
                    }
                    Spacer(Modifier.height(22.dp))
                }
            }
            item { ProfileInfoRow(Icons.Default.Wallpaper, "Chat wallpaper", "Change the background for this conversation") { onNotify("Open the conversation menu and choose Wallpaper.") } }
            item { ProfileInfoRow(Icons.Default.Image, "Manage storage", "${attachments.size} attachments · ${messages.size} messages saved locally") { onNotify("${messages.size} messages · ${attachments.size} attachments stored on this device") } }
            item { ProfileToggleRow(Icons.Default.Notifications, "Notifications", "Local alert preference", notifications) { enabled -> notifications = enabled; scope.launch { database.contactDao().getById(contact.id)?.let { database.contactDao().upsert(it.copy(notificationsEnabled = enabled)) } } } }
            item { ProfileToggleRow(Icons.Default.Image, "Media visibility", "Controls the shared-photo strip", mediaVisible) { enabled -> mediaVisible = enabled; scope.launch { database.contactDao().getById(contact.id)?.let { database.contactDao().upsert(it.copy(mediaVisibility = enabled)) } } } }
            item { ProfileInfoRow(Icons.Default.Shield, "Encryption", "Messages and calls stay on this device. Nothing is transmitted.") { onNotify("Whatsupp stores messages only in its local database.") } }
            item { ProfileToggleRow(Icons.Default.Update, "Disappearing messages", "Visual setting only", disappearing) { enabled -> disappearing = enabled; scope.launch { database.contactDao().getById(contact.id)?.let { database.contactDao().upsert(it.copy(disappearingMessages = enabled)) } } } }
            item { ProfileToggleRow(Icons.Default.Smartphone, "Chat lock", "Local UI preference only", chatLock) { enabled -> chatLock = enabled; scope.launch { database.contactDao().getById(contact.id)?.let { database.contactDao().upsert(it.copy(chatLocked = enabled)) } } } }
            item { ProfileToggleRow(Icons.Default.PushPin, "Pin chat", null, pinned) { enabled -> pinned = enabled; scope.launch { database.contactDao().getById(contact.id)?.let { database.contactDao().upsert(it.copy(isPinned = enabled)) } } } }
            item {
                Spacer(Modifier.height(20.dp))
                TextButton(onClick = onEdit, modifier = Modifier.padding(horizontal = 18.dp)) { Text("Edit contact") }
                Spacer(Modifier.height(30.dp))
            }
        }
    }
    if (showSearch) SearchMessagesDialog(messages = messages, contactName = contact.name, onDismiss = { showSearch = false })
}

@Composable
private fun SearchMessagesDialog(messages: List<MessageEntity>, contactName: String, onDismiss: () -> Unit) {
    var query by remember { mutableStateOf("") }
    val matches = messages.filter { query.isNotBlank() && it.body.contains(query, ignoreCase = true) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Search in chat") },
        text = {
            Column {
                OutlinedTextField(value = query, onValueChange = { query = it }, label = { Text("Search messages") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                if (query.isNotBlank() && matches.isEmpty()) Text("No matching messages", color = AppMuted, fontSize = 12.sp)
                LazyColumn(Modifier.heightIn(max = 260.dp)) {
                    items(matches, key = { it.id }) { message ->
                        Column(Modifier.fillMaxWidth().padding(vertical = 7.dp)) {
                            Text("${if (message.outgoing) "You" else contactName} · ${ChatFormatting.time(message.timestamp)}", color = AppAccent, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                            Text(ChatFormatting.preview(message.body, message.kind), color = AppText, fontSize = 13.sp)
                        }
                        Divider(color = Color(0xFF27343A))
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Close") } }
    )
}

@Composable
private fun ProfileAction(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(64.dp).combinedClickable(onClick = onClick)) {
        Box(Modifier.size(58.dp).clip(CircleShape).background(AppSurface), contentAlignment = Alignment.Center) { Icon(icon, null, tint = AppText, modifier = Modifier.size(25.dp)) }
        Spacer(Modifier.height(8.dp))
        Text(label, color = AppText, fontSize = 12.sp)
    }
}

@Composable
private fun ProfileInfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().combinedClickable(onClick = onClick).padding(horizontal = 22.dp, vertical = 15.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = AppMuted, modifier = Modifier.size(25.dp))
        Spacer(Modifier.width(28.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = AppText, fontSize = 16.sp)
            if (subtitle.isNotBlank()) Text(subtitle, color = AppMuted, fontSize = 12.sp)
        }
    }
}

@Composable
private fun ProfileToggleRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String?, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 11.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = AppMuted, modifier = Modifier.size(25.dp))
        Spacer(Modifier.width(28.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = AppText, fontSize = 16.sp)
            subtitle?.let { Text(it, color = AppMuted, fontSize = 12.sp) }
        }
        Switch(checked = checked, onCheckedChange = onChange)
    }
}

@Composable
private fun SettingsScreen(
    themeMode: String,
    onThemeModeChange: (String) -> Unit,
    onBack: () -> Unit,
    onManageContacts: () -> Unit,
    onClearData: () -> Unit
) {
    var showConfirm by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().background(AppBg)) {
        Row(Modifier.fillMaxWidth().height(58.dp).background(AppSurface2).padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
            Text("Settings", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        }
        Column(Modifier.fillMaxWidth().padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(62.dp).clip(CircleShape).background(Color(0xFF3B2920)), contentAlignment = Alignment.Center) { Text("W", color = Color(0xFFE7B18F), fontSize = 30.sp) }
                Spacer(Modifier.width(14.dp))
                Column {
                    Text("Whatsupp", fontSize = 19.sp, fontWeight = FontWeight.SemiBold)
                    Text("Conversation editor · Local only", color = AppMuted, fontSize = 12.sp)
                }
            }
        }
        Divider(color = Color(0xFF27343A))
        ProfileInfoRow(Icons.Default.Contacts, "Contacts and chat profiles", "Create, import, edit or delete local contacts", onManageContacts)
        ProfileInfoRow(Icons.Default.Wallpaper, "Chats", "Theme, chat wallpapers and saved history") { showThemeDialog = true }
        ProfileInfoRow(Icons.Default.Shield, "Privacy and safety", "No messaging, calling or internet permission") { }
        ProfileInfoRow(Icons.Default.Info, "About", "Conversation edits stay on this device") { }
        Spacer(Modifier.weight(1f))
        Button(onClick = { showConfirm = true }, modifier = Modifier.fillMaxWidth().padding(18.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B3030))) {
            Icon(Icons.Default.Delete, null); Spacer(Modifier.width(8.dp)); Text("Clear all local data")
        }
        Text("Whatsupp · Conversation composer", color = AppMuted, fontSize = 10.sp, modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 20.dp))
    }
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("Choose theme") },
            text = {
                Column {
                    listOf("system" to "System default", "light" to "Light", "dark" to "Dark").forEach { (mode, label) ->
                        Row(
                            Modifier.fillMaxWidth().combinedClickable(onClick = { onThemeModeChange(mode); showThemeDialog = false }).padding(vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = themeMode == mode, onClick = { onThemeModeChange(mode); showThemeDialog = false })
                            Spacer(Modifier.width(10.dp))
                            Text(label, color = AppText)
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showThemeDialog = false }) { Text("Close") } }
        )
    }
    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            title = { Text("Clear everything?") },
            text = { Text("This permanently deletes all locally saved contacts and local messages from this app.") },
            confirmButton = { TextButton(onClick = { showConfirm = false; onClearData() }) { Text("Clear data", color = Color(0xFFFF7777)) } },
            dismissButton = { TextButton(onClick = { showConfirm = false }) { Text("Cancel") } }
        )
    }
}
