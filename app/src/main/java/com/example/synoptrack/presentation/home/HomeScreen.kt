package com.example.synoptrack.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    // Dummy state variables for UI preview
    var searchQuery by remember { mutableStateOf("") }
    var selectedBottomTab by remember { mutableIntStateOf(0) }
    var isProfileMenuExpanded by remember { mutableStateOf(false) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(22.7196, 75.8577), 15f)
    }

    Scaffold(
        bottomBar = {
            HomeBottomBar(
                selectedTab = selectedBottomTab,
                onTabSelected = { selectedBottomTab = it }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 3. The Background: A GoogleMap filling the entire screen
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false, // We have custom FABs
                    myLocationButtonEnabled = false // We have custom FABs
                )
            )

            // Layering UI over the map
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .padding(top = 16.dp) // Top spacing
            ) {
                HomeTopBar(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    onProfileClick = { isProfileMenuExpanded = true },
                    isMenuExpanded = isProfileMenuExpanded,
                    onDismissMenu = { isProfileMenuExpanded = false }
                )
                Spacer(modifier = Modifier.height(16.dp))
                QuickAccessChips()
            }

            HomeFABs(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
fun HomeTopBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onProfileClick: () -> Unit,
    isMenuExpanded: Boolean,
    onDismissMenu: () -> Unit
) {
    // 4. The Floating Top Bar
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(56.dp),
        shape = RoundedCornerShape(32.dp),
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(modifier = Modifier.weight(1f)) {
                if (searchQuery.isEmpty()) {
                    Text(
                        text = "Search here...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                androidx.compose.foundation.text.BasicTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
                    singleLine = true
                )
            }

            Box {
                // Profile Image
                Surface(
                    modifier = Modifier
                        .size(36.dp)
                        .clickable { onProfileClick() },
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        modifier = Modifier.padding(4.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                // 5. Profile Menu Logic
                DropdownMenu(
                    expanded = isMenuExpanded,
                    onDismissRequest = onDismissMenu
                ) {
                    DropdownMenuItem(
                        text = { Text("Appearance") },
                        onClick = { onDismissMenu() }
                    )
                    DropdownMenuItem(
                        text = { Text("Location Settings") },
                        onClick = { onDismissMenu() }
                    )
                    DropdownMenuItem(
                        text = { Text("Your Profile") },
                        onClick = { onDismissMenu() }
                    )
                    DropdownMenuItem(
                        text = { Text("Settings") },
                        onClick = { onDismissMenu() }
                    )
                    DropdownMenuItem(
                        text = { Text("Logout") },
                        onClick = { onDismissMenu() }
                    )
                }
            }
        }
    }
}

@Composable
fun QuickAccessChips() {
    val chips = listOf(
        ChipData("Restaurants", Icons.Default.Restaurant),
        ChipData("Gas", Icons.Default.LocalGasStation),
        ChipData("Hotels", Icons.Default.Hotel),
        ChipData("Coffee", Icons.Default.LocalCafe),
        ChipData("Groceries", Icons.Default.ShoppingCart)
    )

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(chips) { chip ->
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp,
                modifier = Modifier.height(32.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                    Icon(
                        imageVector = chip.icon,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = chip.label,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

data class ChipData(val label: String, val icon: ImageVector)

@Composable
fun HomeFABs(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SmallFloatingActionButton(
            onClick = { /* Directions */ },
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Default.Directions, contentDescription = "Directions")
        }

        FloatingActionButton(
            onClick = { /* Re-center */ },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(Icons.Default.MyLocation, contentDescription = "Re-center")
        }
    }
}

@Composable
fun HomeBottomBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            icon = { Icon(if (selectedTab == 0) Icons.Filled.Home else Icons.Outlined.Home, contentDescription = "Home") },
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            icon = { Icon(if (selectedTab == 1) Icons.Filled.Chat else Icons.Outlined.Chat, contentDescription = "Chats") },
            label = { Text("Chats") }
        )
        NavigationBarItem(
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) },
            icon = { Icon(if (selectedTab == 2) Icons.Filled.Person else Icons.Outlined.Person, contentDescription = "Profile") },
            label = { Text("Profile") }
        )
    }
}

