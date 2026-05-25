package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.automirrored.filled.CallMissed
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material.icons.automirrored.filled.CallReceived
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.model.CallDirection
import com.example.model.CallHistoryItem
import com.example.model.Contact
import com.example.model.RecentCall
import com.example.ui.components.ContactAvatar
import com.example.ui.components.NothingGlassCard
import com.example.ui.components.NothingSearchBar
import com.example.ui.theme.NothingBlack
import com.example.ui.theme.NothingBorderGray
import com.example.ui.theme.NothingCardGray
import com.example.ui.theme.NothingDarkGray
import com.example.ui.theme.NothingRed
import com.example.ui.theme.NothingWhiteMuted
import com.example.viewmodel.DialerViewModel

// HEADER HELPER COMPOSABLE WITH THE ESSENTIAL NOTHING ELEMENT
@Composable
fun NothingHeaderPrimal(title: String, subtitle: String? = null) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 4.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                tint = Color.LightGray,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More options",
                tint = Color.LightGray,
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = title,
            style = TextStyle(
                fontFamily = FontFamily.Serif,
                fontSize = 42.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }
}

// PERMISSION & DEFAULT DESIGN TUNING COMPOSABLE (NOTHING METRICS CARD)
@Composable
fun NothingPermissionSetupCard(
    state: com.example.viewmodel.DialerUiState,
    viewModel: DialerViewModel
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val launcher = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
    ) {
        viewModel.refreshData()
    }
    val permissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions()
    ) { _ ->
        viewModel.refreshData()
    }

    if (!state.isDefaultDialer || !state.contactsPermissionGranted || !state.logsPermissionGranted) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(NothingCardGray)
                .border(BorderStroke(1.dp, NothingBorderGray), RoundedCornerShape(16.dp))
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(NothingRed, CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "SYSTEM SYNCHRONIZATION",
                    style = TextStyle(
                        color = Color.White,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Set as default phone app and grant permission to load device contacts & logs.",
                style = TextStyle(
                    color = MaterialTheme.colorScheme.secondary,
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Notice: If Android blocks this with a 'Restricted Setting' error, please go to your device Settings -> Apps -> App Info -> 3 dots in top right -> Allow restricted settings.",
                style = TextStyle(
                    color = NothingRed,
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 11.sp,
                    lineHeight = 14.sp
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!state.isDefaultDialer) {
                    Button(
                        onClick = {
                            val intent = viewModel.promptSetDefaultDialerIntent(context)
                            if (intent != null) {
                                launcher.launch(intent)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NothingRed),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text(
                            text = "SET DEFAULT",
                            style = TextStyle(
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }
                }
                if (!state.contactsPermissionGranted || !state.logsPermissionGranted) {
                    Button(
                        onClick = {
                            permissionLauncher.launch(
                                arrayOf(
                                    android.Manifest.permission.READ_CONTACTS,
                                    android.Manifest.permission.READ_CALL_LOG,
                                    android.Manifest.permission.CALL_PHONE
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NothingWhiteMuted),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text(
                            text = "GRANT ACCESSIBILITY",
                            style = TextStyle(
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }
                }
            }
        }
    }
}

// BOTTOM FIXED FLOATING PILL NAV DECK
@Composable
fun NothingNavigationDeck(
    navController: NavController,
    currentRoute: String,
    onFabClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            // RED 9-DOTS FLOATING LAUNCHER FAB
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(NothingRed)
                    .clickable { onFabClick() }
                    .testTag("dialer_fab"),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    repeat(3) {
                        Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                            repeat(3) {
                                Box(
                                    modifier = Modifier
                                        .size(3.5.dp)
                                        .background(Color.White, CircleShape)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // SLIM GLASS CAPSULE BAR FOR 3 TABS
            Row(
                modifier = Modifier
                    .height(64.dp)
                    .width(280.dp)
                    .background(NothingCardGray, RoundedCornerShape(32.dp))
                    .border(BorderStroke(0.5.dp, NothingBorderGray), RoundedCornerShape(32.dp))
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // FAVORITES TAB (Heart Outline/Filled)
                IconButton(
                    onClick = {
                        if (currentRoute != "favorites") {
                            navController.navigate("favorites") {
                                popUpTo("recents") { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (currentRoute == "favorites") Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorites Tab",
                        tint = if (currentRoute == "favorites") NothingRed else Color.LightGray.copy(alpha = 0.5f),
                        modifier = Modifier.size(24.dp)
                    )
                }

                // RECENTS LOGS TAB (Clock Icon)
                IconButton(
                    onClick = {
                        if (currentRoute != "recents") {
                            navController.navigate("recents") {
                                popUpTo("recents") { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "Recents Tab",
                        tint = if (currentRoute == "recents") NothingRed else Color.LightGray.copy(alpha = 0.5f),
                        modifier = Modifier.size(24.dp)
                    )
                }

                // CONTACTS DIRECTORY TAB (Person Icon)
                IconButton(
                    onClick = {
                        if (currentRoute != "contacts") {
                            navController.navigate("contacts") {
                                popUpTo("recents") { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Contacts Tab",
                        tint = if (currentRoute == "contacts") NothingRed else Color.LightGray.copy(alpha = 0.5f),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 1) RECENT CALLS SCREEN
// -------------------------------------------------------------
@Composable
fun RecentsScreen(
    navController: NavController,
    viewModel: DialerViewModel
) {
    val state by viewModel.uiState.collectAsState()
    val recents by viewModel.filteredRecents.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NothingBlack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp)
        ) {
            NothingHeaderPrimal(title = "Recents")

            // Real-time permission guide banner card
            NothingPermissionSetupCard(state = state, viewModel = viewModel)

            NothingSearchBar(
                query = state.query,
                onQueryChange = { viewModel.onQueryChange(it) },
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (recents.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "[ Empty logs database ]",
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.secondary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp
                        )
                    )
                }
            } else {
                val (recentLogs, earlierLogs) = recents.partition { it.carrierTime.contains("ago") || it.carrierTime == "Just now" }

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 96.dp)
                ) {
                    if (recentLogs.isNotEmpty()) {
                        item {
                            Text(
                                text = "RECENT",
                                style = TextStyle(
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    letterSpacing = 1.5.sp
                                ),
                                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp, start = 4.dp)
                            )
                        }
                        items(recentLogs) { recent ->
                            RecentCallRow(
                                recent = recent,
                                onItemClick = {
                                    viewModel.selectContact(recent.contact.id)
                                    navController.navigate("detail")
                                },
                                onCallClick = {
                                    viewModel.startCall(recent.contact.id)
                                    viewModel.initiateRealCall(context, recent.contact.phone)
                                    navController.navigate("call")
                                }
                            )
                        }
                    }

                    if (earlierLogs.isNotEmpty()) {
                        item {
                            Text(
                                text = "EARLIER",
                                style = TextStyle(
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    letterSpacing = 1.5.sp
                                ),
                                modifier = Modifier.padding(top = 16.dp, bottom = 4.dp, start = 4.dp)
                            )
                        }
                        items(earlierLogs) { recent ->
                            RecentCallRow(
                                recent = recent,
                                onItemClick = {
                                    viewModel.selectContact(recent.contact.id)
                                    navController.navigate("detail")
                                },
                                onCallClick = {
                                    viewModel.startCall(recent.contact.id)
                                    viewModel.initiateRealCall(context, recent.contact.phone)
                                    navController.navigate("call")
                                }
                            )
                        }
                    }
                }
            }
        }

        NothingNavigationDeck(
            navController = navController,
            currentRoute = "recents",
            onFabClick = {
                viewModel.toggleKeypad()
            }
        )

        // SLIDING RETRO DIALPAD KEYPAD OVERLAY
        AnimatedVisibility(
            visible = state.isKeypadOpen,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            RetroDialpadOverlay(
                input = state.keypadInput,
                onDigitClick = { viewModel.appendKeypadDigit(it) },
                onClearClick = { viewModel.deleteFromKeypad() },
                onCloseClick = { viewModel.toggleKeypad() },
                showCallAction = true,
                onCallClick = { typedNumber ->
                    viewModel.initiateRealCall(context, typedNumber)
                    viewModel.startCall(typedNumber)
                    viewModel.toggleKeypad()
                    navController.navigate("call")
                }
            )
        }
    }
}

@Composable
fun RecentCallRow(
    recent: RecentCall,
    onItemClick: () -> Unit,
    onCallClick: () -> Unit
) {
    val isMissed = recent.direction == CallDirection.MISSED
    val secondaryTextColor = if (isMissed) NothingRed else Color.Gray

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() }
            .padding(vertical = 12.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ContactAvatar(
            imageUrl = recent.contact.image,
            name = recent.contact.name,
            size = 48.dp
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = recent.contact.name,
                style = TextStyle(
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 17.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = when (recent.direction) {
                        CallDirection.MISSED -> Icons.AutoMirrored.Filled.CallMissed
                        CallDirection.OUTGOING -> Icons.AutoMirrored.Filled.CallMade
                        CallDirection.INCOMING -> Icons.AutoMirrored.Filled.CallReceived
                    },
                    contentDescription = null,
                    tint = secondaryTextColor,
                    modifier = Modifier.size(14.dp).padding(end = 4.dp)
                )
                Text(
                    text = recent.carrierTime,
                    style = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 13.sp,
                        color = secondaryTextColor,
                        fontWeight = FontWeight.Normal
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        IconButton(
            onClick = { onCallClick() },
            modifier = Modifier
                .size(40.dp)
                .testTag("call_button_${recent.contact.id}")
        ) {
            Icon(
                imageVector = Icons.Outlined.Call,
                contentDescription = "Quick Call",
                tint = Color.LightGray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// -------------------------------------------------------------
// 2) FAVORITES GRID SCREEN
// -------------------------------------------------------------
@Composable
fun FavoritesScreen(
    navController: NavController,
    viewModel: DialerViewModel
) {
    val state by viewModel.uiState.collectAsState()
    val favorites by viewModel.filteredFavorites.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NothingBlack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp)
        ) {
            NothingHeaderPrimal(title = "Favorites", subtitle = "SPEED DIAL CHANNELS")

            // Real-time permission guide banner card
            NothingPermissionSetupCard(state = state, viewModel = viewModel)

            NothingSearchBar(
                query = state.query,
                onQueryChange = { viewModel.onQueryChange(it) },
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "SPEED CONTROLLER GRIDS",
                style = TextStyle(
                    color = MaterialTheme.colorScheme.secondary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    letterSpacing = 1.5.sp
                ),
                modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
            )

            if (favorites.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "[ No favorites matching query ]",
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.secondary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp
                        )
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 96.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(favorites) { favorite ->
                        NothingGlassCard(
                            onClick = {
                                viewModel.selectContact(favorite.id)
                                navController.navigate("detail")
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = favorite.name,
                                    style = TextStyle(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.SansSerif,
                                        fontSize = 13.sp
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(bottom = 10.dp)
                                )

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(115.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF090909))
                                        .border(BorderStroke(0.8.dp, NothingBorderGray), RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (favorite.image.isNotBlank()) {
                                        AsyncImage(
                                            model = favorite.image,
                                            contentDescription = favorite.name,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    } else {
                                        com.example.ui.components.NothingDotMatrixCharacter(
                                            char = if (favorite.name.isNotEmpty()) favorite.name.first() else '?',
                                            color = if (favorite.redAccent) NothingRed else Color.White,
                                            dotSize = 3.5.dp,
                                            spacing = 2.5.dp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        NothingNavigationDeck(
            navController = navController,
            currentRoute = "favorites",
            onFabClick = {
                viewModel.toggleKeypad()
            }
        )

        // SLIDING RETRO DIALPAD KEYPAD OVERLAY
        AnimatedVisibility(
            visible = state.isKeypadOpen,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            RetroDialpadOverlay(
                input = state.keypadInput,
                onDigitClick = { viewModel.appendKeypadDigit(it) },
                onClearClick = { viewModel.deleteFromKeypad() },
                onCloseClick = { viewModel.toggleKeypad() },
                showCallAction = true,
                onCallClick = { typedNumber ->
                    viewModel.initiateRealCall(context, typedNumber)
                    viewModel.startCall(typedNumber)
                    viewModel.toggleKeypad()
                    navController.navigate("call")
                }
            )
        }
    }
}

// -------------------------------------------------------------
// 3) CONTACT DETAIL SCREEN
// -------------------------------------------------------------
@Composable
fun ContactDetailScreen(
    navController: NavController,
    viewModel: DialerViewModel
) {
    val contact = viewModel.getSelectedContact()
    val history by viewModel.selectedContactHistory.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current

    if (contact == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(NothingBlack),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("[ Error loading profile ]", color = Color.White, fontFamily = FontFamily.Monospace)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { navController.popBackStack() },
                    colors = ButtonDefaults.buttonColors(containerColor = NothingRed)
                ) {
                    Text("Go Back", color = Color.White)
                }
            }
        }
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NothingBlack)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
        ) {
            // NAVBAR BACK ITEM
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .size(40.dp)
                            .background(NothingCardGray, CircleShape)
                            .border(BorderStroke(1.dp, NothingBorderGray), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back",
                            tint = Color.White
                        )
                    }
                }
            }

            // BIG HEADER DETAILS
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ContactAvatar(
                        imageUrl = contact.image,
                        name = contact.name,
                        size = 110.dp,
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                            .border(BorderStroke(2.dp, Color.White), CircleShape)
                    )

                    Text(
                        text = contact.name,
                        style = TextStyle(
                            color = Color.White,
                            fontFamily = FontFamily.Serif,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = contact.subtitle.uppercase(),
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.secondary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            letterSpacing = 1.25.sp,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }

            // MOBILE NUMBERS LIST CARD SECTION
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PhoneCard(
                        phone = contact.phone,
                        label = "Mobile",
                        onCallClick = {
                            viewModel.startCall(contact.id)
                            viewModel.initiateRealCall(context, contact.phone)
                            navController.navigate("call")
                        },
                        onSmsClick = {},
                        onVideoClick = {}
                    )

                    contact.secondaryPhone?.let { secPhone ->
                        PhoneCard(
                            phone = secPhone,
                            label = "Mobile",
                            onCallClick = {
                                viewModel.startCall(contact.id)
                                viewModel.initiateRealCall(context, secPhone)
                                navController.navigate("call")
                            },
                            onSmsClick = {},
                            onVideoClick = {}
                        )
                    }
                }
            }

            // HISTORY LOG FEED
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, bottom = 12.dp, start = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "HISTORY LOGS",
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.secondary,
                            letterSpacing = 1.5.sp
                        )
                    )
                    Text(
                        text = "View all",
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            items(history) { item ->
                CallHistoryLogItem(item)
                Spacer(modifier = Modifier.height(8.dp))
            }

            // MORE COMMUNICATIONS INTERNALS
            item {
                Text(
                    text = "MORE COMMUNICATIONS",
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        letterSpacing = 1.5.sp
                    ),
                    modifier = Modifier.padding(top = 20.dp, bottom = 12.dp, start = 4.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(NothingCardGray)
                        .border(BorderStroke(0.8.dp, NothingBorderGray), RoundedCornerShape(16.dp))
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFF25D366), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "W",
                            style = TextStyle(color = Color.White, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "WhatsApp Call & Chat",
                            style = TextStyle(color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        )
                        Text(
                            text = "Direct external tunnel active",
                            style = TextStyle(color = MaterialTheme.colorScheme.secondary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        )
                    }

                    IconButton(
                        onClick = {
                            viewModel.startCall(contact.id)
                            viewModel.initiateRealCall(context, contact.phone)
                            navController.navigate("call")
                        },
                        modifier = Modifier
                            .size(32.dp)
                            .background(NothingWhiteMuted, CircleShape)
                    ) {
                        Icon(imageVector = Icons.Default.Phone, contentDescription = "Call WA", tint = Color.LightGray, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }

        // FLOATING GLASS ACTION BAR CAPSULE
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 24.dp)
        ) {
            Row(
                modifier = Modifier
                    .height(56.dp)
                    .width(220.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color(0xE0121212))
                    .border(BorderStroke(1.dp, NothingBorderGray), RoundedCornerShape(28.dp))
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { /* Toggle Action */ }) {
                    Icon(
                        imageVector = if (contact.favorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (contact.favorite) NothingRed else Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(onClick = { /* Toggle Action */ }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Contact",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(onClick = { /* Toggle Action */ }) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(onClick = { /* Toggle Action */ }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PhoneCard(
    phone: String,
    label: String,
    onCallClick: () -> Unit,
    onSmsClick: () -> Unit,
    onVideoClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(NothingCardGray)
            .border(BorderStroke(0.8.dp, NothingBorderGray), RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = phone,
                style = TextStyle(
                    color = Color.White,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = label,
                style = TextStyle(
                    color = MaterialTheme.colorScheme.secondary,
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 11.sp
                )
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onVideoClick,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.VideoCall,
                    contentDescription = "Video Call",
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp)
                )
            }

            IconButton(
                onClick = onCallClick,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = "Voice Call",
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp)
                )
            }

            IconButton(
                onClick = onSmsClick,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Message",
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun DetailActionItem(
    icon: ImageVector,
    label: String,
    containerColor: Color,
    iconColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(containerColor)
            .clickable { onClick() }
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = iconColor,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                color = iconColor,
                letterSpacing = 1.sp
            )
        )
    }
}

@Composable
fun CallHistoryLogItem(item: CallHistoryItem) {
    val isMissed = item.direction == CallDirection.MISSED
    val secondaryColor = if (isMissed) NothingRed else Color.Gray

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = when (item.direction) {
                CallDirection.MISSED -> Icons.AutoMirrored.Filled.CallMissed
                CallDirection.OUTGOING -> Icons.AutoMirrored.Filled.CallMade
                CallDirection.INCOMING -> Icons.AutoMirrored.Filled.CallReceived
            },
            contentDescription = null,
            tint = secondaryColor,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.time,
                style = TextStyle(
                    color = Color.White,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = item.number,
                style = TextStyle(
                    color = Color.Gray,
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 13.sp
                )
            )
        }

        if (item.duration.isNotEmpty()) {
            Text(
                text = item.duration,
                style = TextStyle(
                    color = Color.White,
                    fontWeight = FontWeight.Light,
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 13.sp
                )
            )
        }
    }
}

// -------------------------------------------------------------
// 4) ACTIVE CALL SCREEN + RETRO KEYPAD OVERLAY
// -------------------------------------------------------------
@Composable
fun ActiveCallScreen(
    navController: NavController,
    viewModel: DialerViewModel
) {
    val state by viewModel.uiState.collectAsState()
    val contact = viewModel.getActiveCallContact() ?: return

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NothingBlack)
    ) {
        // LARGE TOP PROFILE WITH DARK INTENSE RADIENT COVER
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.55f)
        ) {
            AsyncImage(
                model = contact.image,
                contentDescription = "${contact.name} Call Poster",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Dynamic dark radial gradient overlay to maintain high premium dark feel
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.4f),
                                NothingBlack
                            ),
                            startY = 0f
                        )
                    )
            )
        }

        // FULL SCREEN MAIN LAYOUT
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // TIMER CHIP TOP & CONTACT DETAILS
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // TIMER CHIP DOTTED
                Box(
                    modifier = Modifier
                        .background(NothingCardGray, RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "CALL ACTIVE • ${viewModel.formatDuration(state.callDurationSeconds)}",
                        style = TextStyle(
                            color = Color.White,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = contact.name,
                    style = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Light,
                        color = Color.White,
                        fontSize = 36.sp,
                        textAlign = TextAlign.Center
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (state.isHoldOn) "[ CALL HELD ]" else contact.subtitle.uppercase(),
                    style = TextStyle(
                        color = if (state.isHoldOn) NothingRed else Color.Gray,
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        letterSpacing = 1.sp,
                        textAlign = TextAlign.Center
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // KEY CONTROL BUTTON PANEL & END CALL TRIGGER
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 2 ROW CHIPS CONTROLS
                Column(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 36.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Row 1
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ActiveCallControlBtn(
                            icon = Icons.Default.Mic,
                            label = "Mute",
                            isActive = state.isMuted,
                            onClick = { viewModel.toggleMute() }
                        )
                        ActiveCallControlBtn(
                            icon = Icons.Default.Phone, // custom dialpad fallback
                            label = "Keypad",
                            isActive = state.isKeypadOpen,
                            onClick = { viewModel.toggleKeypad() }
                        )
                        ActiveCallControlBtn(
                            icon = Icons.AutoMirrored.Filled.VolumeUp,
                            label = "Speaker",
                            isActive = state.isSpeakerOn,
                            onClick = { viewModel.toggleSpeaker() }
                        )
                    }

                    // Row 2
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ActiveCallControlBtn(
                            icon = Icons.Default.Add,
                            label = "Add Call",
                            isActive = false,
                            onClick = { /* Not requested */ }
                        )
                        ActiveCallControlBtn(
                            icon = Icons.Default.Info, // Hold fallback
                            label = "Hold",
                            isActive = state.isHoldOn,
                            onClick = { viewModel.toggleHold() }
                        )
                        ActiveCallControlBtn(
                            icon = Icons.Default.Star,
                            label = "Add Num",
                            isActive = false,
                            onClick = { /* Not requested */ }
                        )
                    }
                }

                // RED END CALL CIRCULAR FAB
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(NothingRed)
                        .clickable {
                            viewModel.stopCall()
                            navController.popBackStack()
                        }
                        .testTag("end_call_fab"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "End Call",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }

        // SLIDING RETRO DIALPAD KEYPAD OVERLAY
        AnimatedVisibility(
            visible = state.isKeypadOpen,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            RetroDialpadOverlay(
                input = state.keypadInput,
                onDigitClick = { viewModel.appendKeypadDigit(it) },
                onClearClick = { viewModel.deleteFromKeypad() },
                onCloseClick = { viewModel.toggleKeypad() }
            )
        }
    }
}

@Composable
fun ActiveCallControlBtn(
    icon: ImageVector,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(72.dp)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(if (isActive) Color.White else NothingCardGray)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) NothingBlack else Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            style = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontSize = 12.sp,
                color = if (isActive) Color.White else Color.Gray,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        )
    }
}

@Composable
fun RetroDialpadOverlay(
    input: String,
    onDigitClick: (String) -> Unit,
    onClearClick: () -> Unit,
    onCloseClick: () -> Unit,
    showCallAction: Boolean = false,
    onCallClick: (String) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
            .background(NothingDarkGray)
            .padding(horizontal = 24.dp, vertical = 20.dp)
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = onCloseClick) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Close keypad",
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // INPUT NUMBER BOX
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.wrapContentWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = input,
                    style = TextStyle(
                        color = Color.White,
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Light,
                        letterSpacing = 2.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (input.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(16.dp))
                    IconButton(onClick = onClearClick, modifier = Modifier.size(24.dp)) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Backspace",
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // TOUCH KEYBOARD CORES (4 rows)
        val keys = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf("*", "0", "#")
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            keys.forEach { rowKeys ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    rowKeys.forEach { key ->
                        RetroKeypadCell(
                            digit = key,
                            onKeyClick = { onDigitClick(key) }
                        )
                    }
                }
            }
        }

        if (showCallAction && input.isNotEmpty()) {
            Spacer(modifier = Modifier.height(32.dp))
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(NothingRed)
                    .clickable { onCallClick(input) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = "Place Real Call",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun RetroKeypadCell(
    digit: String,
    onKeyClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(NothingCardGray)
            .clickable { onKeyClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = digit,
            style = TextStyle(
                color = Color.White,
                fontWeight = FontWeight.Light,
                fontFamily = FontFamily.SansSerif,
                fontSize = 28.sp
            )
        )
    }
}

// -------------------------------------------------------------
// 5) INTEGRATED ALL CONTACTS LIST SCREEN
// -------------------------------------------------------------
@Composable
fun ContactsScreen(
    navController: NavController,
    viewModel: DialerViewModel
) {
    val state by viewModel.uiState.collectAsState()
    val contacts by viewModel.filteredContacts.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NothingBlack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp)
        ) {
            NothingHeaderPrimal(title = "Contacts", subtitle = "COMMUNICATION DIRECTORY")

            // Real-time permission guide banner card
            NothingPermissionSetupCard(state = state, viewModel = viewModel)

            NothingSearchBar(
                query = state.query,
                onQueryChange = { viewModel.onQueryChange(it) },
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "ALL DEVICE CONTACTS",
                style = TextStyle(
                    color = MaterialTheme.colorScheme.secondary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    letterSpacing = 1.5.sp
                ),
                modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
            )

            if (contacts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "[ Empty contacts database ]",
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.secondary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp
                        )
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 96.dp)
                ) {
                    items(contacts) { contact ->
                        ContactRow(
                            contact = contact,
                            onItemClick = {
                                viewModel.selectContact(contact.id)
                                navController.navigate("detail")
                            },
                            onCallClick = {
                                viewModel.startCall(contact.id)
                                viewModel.initiateRealCall(context, contact.phone)
                                navController.navigate("call")
                            }
                        )
                    }
                }
            }
        }

        NothingNavigationDeck(
            navController = navController,
            currentRoute = "contacts",
            onFabClick = {
                viewModel.toggleKeypad()
            }
        )

        // SLIDING RETRO DIALPAD KEYPAD OVERLAY
        AnimatedVisibility(
            visible = state.isKeypadOpen,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            RetroDialpadOverlay(
                input = state.keypadInput,
                onDigitClick = { viewModel.appendKeypadDigit(it) },
                onClearClick = { viewModel.deleteFromKeypad() },
                onCloseClick = { viewModel.toggleKeypad() },
                showCallAction = true,
                onCallClick = { typedNumber ->
                    viewModel.initiateRealCall(context, typedNumber)
                    viewModel.startCall(typedNumber)
                    viewModel.toggleKeypad()
                    navController.navigate("call")
                }
            )
        }
    }
}

@Composable
fun ContactRow(
    contact: Contact,
    onItemClick: () -> Unit,
    onCallClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() }
            .padding(vertical = 12.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ContactAvatar(
            imageUrl = contact.image,
            name = contact.name,
            size = 48.dp
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = contact.name,
                style = TextStyle(
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 17.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = contact.phone,
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Normal
                )
            )
        }

        IconButton(
            onClick = { onCallClick() },
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Call,
                contentDescription = "Quick Call",
                tint = Color.LightGray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
