package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.CallLog
import android.provider.ContactsContract
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.CallDirection
import com.example.model.CallHistoryItem
import com.example.model.Contact
import com.example.model.RecentCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class DialerUiState(
    val query: String = "",
    val selectedContactId: String? = null,
    val activeCallContactId: String? = null,
    val callDurationSeconds: Int = 0,
    val isMuted: Boolean = false,
    val isSpeakerOn: Boolean = false,
    val isHoldOn: Boolean = false,
    val isKeypadOpen: Boolean = false,
    val keypadInput: String = "",
    val isDefaultDialer: Boolean = false,
    val contactsPermissionGranted: Boolean = false,
    val logsPermissionGranted: Boolean = false,
    val callPermissionGranted: Boolean = false,
    val isRefreshing: Boolean = false
)

class DialerViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(DialerUiState())
    val uiState: StateFlow<DialerUiState> = _uiState.asStateFlow()

    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    private val _recents = MutableStateFlow<List<RecentCall>>(emptyList())
    private val _selectedContactHistory = MutableStateFlow<List<CallHistoryItem>>(emptyList())
    val selectedContactHistory: StateFlow<List<CallHistoryItem>> = _selectedContactHistory.asStateFlow()

    private var callTimerJob: Job? = null

    init {
        checkCurrentPermissionsAndState()
        refreshData()
    }

    // Filtered contacts based on search query
    val filteredContacts: StateFlow<List<Contact>> = combine(_contacts, _uiState) { contactsList, state ->
        if (state.query.isBlank()) {
            contactsList
        } else {
            contactsList.filter {
                it.name.contains(state.query, ignoreCase = true) ||
                        it.phone.contains(state.query) ||
                        it.subtitle.contains(state.query, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered favorites based on search query
    val filteredFavorites: StateFlow<List<Contact>> = combine(_contacts, _uiState) { contactsList, state ->
        val filtered = if (state.query.isBlank()) {
            contactsList
        } else {
            contactsList.filter {
                it.name.contains(state.query, ignoreCase = true) ||
                        it.phone.contains(state.query)
            }
        }
        filtered.filter { it.favorite }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered recent calls based on search query
    val filteredRecents: StateFlow<List<RecentCall>> = combine(_recents, _uiState) { recentsList, state ->
        if (state.query.isBlank()) {
            recentsList
        } else {
            recentsList.filter {
                it.contact.name.contains(state.query, ignoreCase = true) ||
                        it.contact.phone.contains(state.query)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onQueryChange(newQuery: String) {
        _uiState.value = _uiState.value.copy(query = newQuery)
    }

    fun selectContact(contactId: String?) {
        _uiState.value = _uiState.value.copy(selectedContactId = contactId)
        if (contactId != null) {
            viewModelScope.launch {
                _selectedContactHistory.value = withContext(Dispatchers.IO) { getHistoryForContact(contactId) }
            }
        } else {
            _selectedContactHistory.value = emptyList()
        }
    }

    fun checkCurrentPermissionsAndState() {
        val context = getApplication<Application>()
        val contactsGranted = ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
        val logsGranted = ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED
        val callGranted = ContextCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED
        
        val defaultDialer = isAppDefaultDialer(context)

        _uiState.value = _uiState.value.copy(
            contactsPermissionGranted = contactsGranted,
            logsPermissionGranted = logsGranted,
            callPermissionGranted = callGranted,
            isDefaultDialer = defaultDialer
        )
    }

    private fun cleanPhoneNumber(number: String): String {
        return number.filter { it.isDigit() }
    }

    fun refreshData() {
        _uiState.value = _uiState.value.copy(isRefreshing = true)
        viewModelScope.launch {
            checkCurrentPermissionsAndState()
            val state = _uiState.value
            
            val deviceContacts = if (state.contactsPermissionGranted) {
                withContext(Dispatchers.IO) { fetchRealDeviceContacts() }
            } else {
                emptyList()
            }
            _contacts.value = deviceContacts

            val deviceLogs = if (state.logsPermissionGranted) {
                withContext(Dispatchers.IO) { fetchRealDeviceCallLogs(deviceContacts) }
            } else {
                emptyList()
            }
            _recents.value = deviceLogs
            _uiState.value = _uiState.value.copy(isRefreshing = false)
        }
    }

     private fun fetchRealDeviceContacts(): List<Contact> {
        val list = mutableListOf<Contact>()
        try {
            val resolver = getApplication<Application>().contentResolver
            val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
            val projection = arrayOf(
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI,
                ContactsContract.CommonDataKinds.Phone.STARRED
            )
            
            resolver.query(uri, projection, null, null, "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC")?.use { cursor ->
                val idCol = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
                val nameCol = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val numCol = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                val photoCol = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI)
                val starredCol = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.STARRED)
                
                while (cursor.moveToNext()) {
                    val idRaw = if (idCol >= 0) cursor.getString(idCol) else null
                    val id = if (idRaw.isNullOrBlank()) java.util.UUID.randomUUID().toString() else idRaw
                    val name = if (nameCol >= 0) cursor.getString(nameCol) ?: "Unknown" else "Unknown"
                    val phone = if (numCol >= 0) cursor.getString(numCol) ?: "" else ""
                    val photo = if (photoCol >= 0) cursor.getString(photoCol) ?: "" else ""
                    val starredInt = if (starredCol >= 0) cursor.getInt(starredCol) else 0
                    val favorite = starredInt == 1
                    
                    if (phone.isNotBlank()) {
                        list.add(
                            Contact(
                                id = id,
                                name = name,
                                subtitle = "Contact",
                                phone = phone,
                                image = photo,
                                favorite = favorite,
                                redAccent = favorite
                            )
                        )
                    }
                }
            }
        } catch (e: SecurityException) {
            // Permission missing
        } catch (e: Exception) {
            // handle error
        }
        return list.distinctBy { it.phone }
    }

    private fun fetchRealDeviceCallLogs(contactsList: List<Contact>): List<RecentCall> {
        val list = mutableListOf<RecentCall>()
        try {
            val resolver = getApplication<Application>().contentResolver
            val uri = CallLog.Calls.CONTENT_URI
            val projection = arrayOf(
                CallLog.Calls._ID,
                CallLog.Calls.NUMBER,
                CallLog.Calls.CACHED_NAME,
                CallLog.Calls.TYPE,
                CallLog.Calls.DATE
            )
            
            val contactsMap = contactsList.associateBy { cleanPhoneNumber(it.phone) }
            
            resolver.query(uri, projection, null, null, "${CallLog.Calls.DATE} DESC LIMIT 80")?.use { cursor ->
                val idCol = cursor.getColumnIndex(CallLog.Calls._ID)
                val numCol = cursor.getColumnIndex(CallLog.Calls.NUMBER)
                val nameCol = cursor.getColumnIndex(CallLog.Calls.CACHED_NAME)
                val typeCol = cursor.getColumnIndex(CallLog.Calls.TYPE)
                val dateCol = cursor.getColumnIndex(CallLog.Calls.DATE)
                
                while (cursor.moveToNext()) {
                    val idRaw = if (idCol >= 0) cursor.getString(idCol) else null
                    val id = if (idRaw.isNullOrBlank()) java.util.UUID.randomUUID().toString() else idRaw
                    val phone = if (numCol >= 0) cursor.getString(numCol) ?: "" else ""
                    val cachedName = if (nameCol >= 0) cursor.getString(nameCol) else null
                    val type = if (typeCol >= 0) cursor.getInt(typeCol) else CallLog.Calls.INCOMING_TYPE
                    val dateMs = if (dateCol >= 0) cursor.getLong(dateCol) else 0L
                    
                    val direction = when (type) {
                        CallLog.Calls.MISSED_TYPE -> CallDirection.MISSED
                        CallLog.Calls.OUTGOING_TYPE -> CallDirection.OUTGOING
                        else -> CallDirection.INCOMING
                    }
                    
                    val timeDiff = System.currentTimeMillis() - dateMs
                    val timeString = when {
                        timeDiff < 60000 -> "Just now"
                        timeDiff < 3600000 -> "${timeDiff / 60000}m ago"
                        timeDiff < 86400000 -> "${timeDiff / 3600000}h ago"
                        else -> android.text.format.DateFormat.format("MMM dd", dateMs).toString()
                    }
                    
                    val cleanPhone = cleanPhoneNumber(phone)
                    val matchedContact = if (cleanPhone.isNotEmpty()) contactsMap[cleanPhone] else null
                    
                    val contact = if (matchedContact != null) {
                        matchedContact
                    } else {
                        Contact(
                            id = "unknown_$id",
                            name = if (!cachedName.isNullOrBlank()) cachedName else phone,
                            subtitle = "Mobile",
                            phone = phone,
                            image = "",
                            favorite = false
                        )
                    }
                    
                    list.add(
                        RecentCall(
                            id = id,
                            contact = contact,
                            direction = direction,
                            carrierTime = timeString
                        )
                    )
                }
            }
        } catch (e: SecurityException) {
            // Permission missing
        } catch (e: Exception) {
            // handle error
        }
        return list
    }

    // Direct interface to invoke system calling mechanism immediately
    fun initiateRealCall(context: Context, number: String) {
        if (number.isBlank()) return
        val cleanNumber = number.filter { it.isDigit() || it == '+' || it == '*' || it == '#' }
        try {
            val hasCallPermission = ContextCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED
            val intent = if (hasCallPermission) {
                Intent(Intent.ACTION_CALL).apply {
                    data = Uri.parse("tel:${Uri.encode(cleanNumber)}")
                }
            } else {
                Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:${Uri.encode(cleanNumber)}")
                }
            }
            if (context !is android.app.Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fail safe launcher mapping to DIAL intent handler
            val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:${Uri.encode(cleanNumber)}")
                if (context !is android.app.Activity) {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            }
            context.startActivity(dialIntent)
        }
    }

    fun startCall(contactId: String) {
        stopCall() // Ensure any existing call timer is cleaned up
        _uiState.value = _uiState.value.copy(
            activeCallContactId = contactId,
            callDurationSeconds = 0,
            isMuted = false,
            isSpeakerOn = false,
            isHoldOn = false,
            isKeypadOpen = false,
            keypadInput = ""
        )

        // Dynamic call timer ticking
        callTimerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _uiState.value = _uiState.value.copy(
                    callDurationSeconds = _uiState.value.callDurationSeconds + 1
                )
            }
        }
    }

    fun stopCall() {
        callTimerJob?.cancel()
        callTimerJob = null
        _uiState.value = _uiState.value.copy(
            activeCallContactId = null,
            callDurationSeconds = 0
        )
    }

    fun toggleMute() {
        _uiState.value = _uiState.value.copy(isMuted = !_uiState.value.isMuted)
    }

    fun toggleSpeaker() {
        _uiState.value = _uiState.value.copy(isSpeakerOn = !_uiState.value.isSpeakerOn)
    }

    fun toggleHold() {
        _uiState.value = _uiState.value.copy(isHoldOn = !_uiState.value.isHoldOn)
    }

    fun toggleKeypad() {
        _uiState.value = _uiState.value.copy(isKeypadOpen = !_uiState.value.isKeypadOpen)
    }

    fun deleteFromKeypad() {
        val currentInput = _uiState.value.keypadInput
        if (currentInput.isNotEmpty()) {
            _uiState.value = _uiState.value.copy(keypadInput = currentInput.dropLast(1))
        }
    }

    fun appendKeypadDigit(digit: String) {
        _uiState.value = _uiState.value.copy(
            keypadInput = _uiState.value.keypadInput + digit
        )
    }

    fun clearKeypad() {
        _uiState.value = _uiState.value.copy(keypadInput = "")
    }

    fun getSelectedContactId(): String? = _uiState.value.selectedContactId

    fun getSelectedContact(): Contact? {
        val id = _uiState.value.selectedContactId ?: return null
        return _contacts.value.find { it.id == id } ?: _recents.value.find { it.contact.id == id || "log_${it.id}" == id }?.contact
    }

    fun getActiveCallContact(): Contact? {
        val id = _uiState.value.activeCallContactId ?: return null
        return _contacts.value.find { it.id == id } ?: _recents.value.find { it.contact.id == id || "log_${it.id}" == id }?.contact
    }

    fun getHistoryForContact(contactId: String): List<CallHistoryItem> {
        val selected = _contacts.value.find { it.id == contactId } ?: _recents.value.find { it.contact.id == contactId || "log_${it.id}" == contactId }?.contact ?: return emptyList()
        if (!_uiState.value.logsPermissionGranted) {
            return emptyList()
        }
        val list = mutableListOf<CallHistoryItem>()
        try {
            val resolver = getApplication<Application>().contentResolver
            val uri = CallLog.Calls.CONTENT_URI
            val projection = arrayOf(
                CallLog.Calls._ID,
                CallLog.Calls.NUMBER,
                CallLog.Calls.TYPE,
                CallLog.Calls.DATE,
                CallLog.Calls.DURATION
            )
            
            val primaryClean = cleanPhoneNumber(selected.phone)
            val secondaryClean = selected.secondaryPhone?.let { cleanPhoneNumber(it) }
            
            val selection = if (secondaryClean != null && secondaryClean.isNotBlank()) {
                "${CallLog.Calls.NUMBER} LIKE ? OR ${CallLog.Calls.NUMBER} LIKE ?"
            } else {
                "${CallLog.Calls.NUMBER} LIKE ?"
            }
            
            val selectionArgs = if (secondaryClean != null && secondaryClean.isNotBlank()) {
                arrayOf("%$primaryClean%", "%$secondaryClean%")
            } else {
                arrayOf("%$primaryClean%")
            }
            
            resolver.query(uri, projection, selection, selectionArgs, "${CallLog.Calls.DATE} DESC LIMIT 50")?.use { cursor ->
                val idCol = cursor.getColumnIndex(CallLog.Calls._ID)
                val numCol = cursor.getColumnIndex(CallLog.Calls.NUMBER)
                val typeCol = cursor.getColumnIndex(CallLog.Calls.TYPE)
                val dateCol = cursor.getColumnIndex(CallLog.Calls.DATE)
                val durCol = cursor.getColumnIndex(CallLog.Calls.DURATION)
                
                while (cursor.moveToNext()) {
                    val idRaw = if (idCol >= 0) cursor.getString(idCol) else null
                    val id = if (idRaw.isNullOrBlank()) java.util.UUID.randomUUID().toString() else idRaw
                    val number = if (numCol >= 0) cursor.getString(numCol) ?: "" else ""
                    val type = if (typeCol >= 0) cursor.getInt(typeCol) else CallLog.Calls.INCOMING_TYPE
                    val dateMs = if (dateCol >= 0) cursor.getLong(dateCol) else 0L
                    val durationSec = if (durCol >= 0) cursor.getLong(durCol) else 0L
                    
                    val direction = when (type) {
                        CallLog.Calls.MISSED_TYPE -> CallDirection.MISSED
                        CallLog.Calls.OUTGOING_TYPE -> CallDirection.OUTGOING
                        else -> CallDirection.INCOMING
                    }
                    
                    val timeDiff = System.currentTimeMillis() - dateMs
                    val timeString = when {
                        timeDiff < 60000 -> "Just now"
                        timeDiff < 3600000 -> "${timeDiff / 60000}m ago"
                        timeDiff < 86400000 -> "${timeDiff / 3600000}h ago"
                        else -> android.text.format.DateFormat.format("MMM dd, yyyy", dateMs).toString()
                    }
                    
                    val durationStr = if (direction == CallDirection.MISSED) "" else {
                        val mins = durationSec / 60
                        val secs = durationSec % 60
                        if (mins > 0) "${mins}m ${secs}s" else "${secs}s"
                    }
                    
                    list.add(
                        CallHistoryItem(
                            id = id,
                            direction = direction,
                            time = timeString,
                            number = number,
                            duration = durationStr
                        )
                    )
                }
            }
        } catch (e: Exception) {
            // fail-safe fallback
        }
        return list
    }

    fun formatDuration(seconds: Int): String {
        val mins = seconds / 60
        val secs = seconds % 60
        return String.format("%02d:%02d", mins, secs)
    }

    // Role activation prompt helper
    fun isAppDefaultDialer(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val rm = context.getSystemService(Context.ROLE_SERVICE) as? android.app.role.RoleManager
            rm?.isRoleHeld(android.app.role.RoleManager.ROLE_DIALER) == true
        } else {
            val tm = context.getSystemService(Context.TELECOM_SERVICE) as? android.telecom.TelecomManager
            tm?.defaultDialerPackage == context.packageName
        }
    }

    fun promptSetDefaultDialerIntent(context: Context): Intent? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val rm = context.getSystemService(Context.ROLE_SERVICE) as? android.app.role.RoleManager
            if (rm?.isRoleAvailable(android.app.role.RoleManager.ROLE_DIALER) == true) {
                return rm.createRequestRoleIntent(android.app.role.RoleManager.ROLE_DIALER)
            }
        } else {
            return Intent(android.telecom.TelecomManager.ACTION_CHANGE_DEFAULT_DIALER).apply {
                putExtra(android.telecom.TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, context.packageName)
            }
        }
        return null
    }
}
