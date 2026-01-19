/*
 * MyMileage – Your Smart Vehicle Mileage Tracker
 * Copyright (C) 2026 Yojit Ghadi
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

@file:OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)

package com.yg.mileage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.PendingActions
import androidx.compose.material3.AppBarWithSearch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialShapes.Companion.Cookie12Sided
import androidx.compose.material3.MaterialShapes.Companion.Cookie4Sided
import androidx.compose.material3.MaterialShapes.Companion.Cookie9Sided
import androidx.compose.material3.MaterialShapes.Companion.Ghostish
import androidx.compose.material3.MaterialShapes.Companion.Oval
import androidx.compose.material3.MaterialShapes.Companion.Pentagon
import androidx.compose.material3.MaterialShapes.Companion.Pill
import androidx.compose.material3.MaterialShapes.Companion.SoftBurst
import androidx.compose.material3.MaterialShapes.Companion.Sunny
import androidx.compose.material3.MaterialShapes.Companion.VerySunny
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SplitButtonDefaults
import androidx.compose.material3.SplitButtonLayout
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import com.yg.mileage.ui.theme.MyMileageTheme
import com.yg.mileage.ui.theme.primaryContainerLight
import com.yg.mileage.ui.theme.primaryLight

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun ExpressiveTopSearch() {
    AppBarWithSearch(
        state = rememberSearchBarState(), // Manages expanded/collapsed state
        inputField = {
            SearchBarDefaults.InputField(
                query = "",
                onQueryChange = {},
                onSearch = {},
                expanded = false,
                onExpandedChange = {},
                placeholder = {
                    // Centered text as shown in your image
                    Text("Search your marks", textAlign = TextAlign.Center)
                },
            )
        },
        navigationIcon = {
            IconButton(onClick = {}) {
                Icon(Icons.Default.Menu, contentDescription = "Menu")
            }
        },
        actions = {
            IconButton(onClick = {}) {
                Icon(Icons.Default.AccountCircle, contentDescription = "Profile")
            }
        }
    )
}

class TestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyMileageTheme {
                MainScreen()
            }
        }
    }
}

/**
 * Shape helpers for grouped lists and cards.
 * Compatible with Material3 Expressive or standard shape tokens.
 */
object MyMileageShapeDefaults {

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    fun topListItemShape(): RoundedCornerShape =
        RoundedCornerShape(
            topStart = MaterialTheme.shapes.large.topStart,
            topEnd = MaterialTheme.shapes.large.topEnd,
            bottomStart = MaterialTheme.shapes.extraSmall.bottomStart,
            bottomEnd = MaterialTheme.shapes.extraSmall.bottomEnd
        )

    @Composable
    fun middleListItemShape(): RoundedCornerShape =
        RoundedCornerShape(MaterialTheme.shapes.extraSmall.topStart)

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    fun bottomListItemShape(): RoundedCornerShape =
        RoundedCornerShape(
            topStart = MaterialTheme.shapes.extraSmall.topStart,
            topEnd = MaterialTheme.shapes.extraSmall.topEnd,
            bottomStart = MaterialTheme.shapes.large.bottomStart,
            bottomEnd = MaterialTheme.shapes.large.bottomEnd
        )

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    fun cardShape(): CornerBasedShape = MaterialTheme.shapes.large
}
@Preview
@Composable
fun MainScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Welcome to Expressive UI",
                style = MaterialTheme.typography.headlineMedium,
                color = primaryLight
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {},
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Text("Click Me")
            }
        }
    }
}
@Preview
@Composable
fun ContainedLoading() {
    LoadingIndicator(
        color = primaryLight,
        polygons = listOf(
            SoftBurst,
            Cookie9Sided,
            Pentagon,
            Pill,
            Sunny,
            Cookie4Sided,
            Oval,
            VerySunny,
            Ghostish,
            Cookie12Sided
        ),
    )
    ContainedLoadingIndicator()
}
@Preview
@Composable
fun SplitButtonDemo() {
    var checked2 by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize()
    ) {
        SplitButtonLayout(leadingButton = {
            SplitButtonDefaults.LeadingButton(
                onClick = { },
            ) {
                Icon(
                    Icons.Filled.Edit,
                    modifier = Modifier.size(SplitButtonDefaults.LeadingIconSize),
                    contentDescription = "Localized description",
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("My Button")
            }
        }, trailingButton = {
            SplitButtonDefaults.TrailingButton(
                checked = checked2,
                onCheckedChange = { checked2 = it },
            ) {
                val rotation: Float by animateFloatAsState(
                    targetValue = if (checked2) 180f else 0f, label = "Trailing Icon Rotation"
                )
                Icon(
                    Icons.Filled.KeyboardArrowDown,
                    modifier = Modifier
                        .size(SplitButtonDefaults.TrailingIconSize)
                        .graphicsLayer {
                            this.rotationZ = rotation
                        },
                    contentDescription = "Localized description"
                )
            }
        })

        DropdownMenu(expanded = checked2, onDismissRequest = { checked2 = false }) {
            DropdownMenuItem(
                text = { Text("Edit") },
                onClick = {},
                leadingIcon = { Icon(Icons.Outlined.Edit, contentDescription = null) })
            DropdownMenuItem(
                text = { Text("Settings") },
                onClick = {},
                leadingIcon = { Icon(Icons.Outlined.Settings, contentDescription = null) })
            HorizontalDivider()
            DropdownMenuItem(
                text = { Text("Send Feedback") },
                onClick = {},
                leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) })
        }
    }
}
/**
 * Optional Path to be created for the RoundRect if the corner radii are not identical This
 * is because Canvas has a built-in API for drawing round rectangles with the same corner
 * radii in all 4 corners. However, if each corner has a different corner radii, a path must
 * be drawn instead
 */

@Composable
fun ContactListItem(name: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 16.dp), // Add padding around the card
        shape = RoundedCornerShape(12.dp), // Adjust the corner radius as needed
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp) // Optional: Add elevation
    ) {
        Text(
            text = name,
            modifier = Modifier.padding(16.dp) // Padding for the content inside the card
        )
    }
}

@Composable
@Preview
fun SplitButton() {
    val options = listOf("Breakfast", "Lunch", "Snack", "Dinner")
    var selectedIndex by remember { mutableIntStateOf(0) }

    ButtonGroup(
        modifier = Modifier.padding(horizontal = 8.dp),
        overflowIndicator = {}
    ) {
        options.forEachIndexed { index, label ->
            toggleableItem(
                checked = selectedIndex == index,
                onCheckedChange = { selectedIndex = index },
                label = label,
            )
        }
}

@Composable
fun MLKitTextRecognitionButton() {
    Button(
        onClick = {  },
    ) {
        Text("Recognize Text")
    }
  }
}
@Preview(showSystemUi = true)
@Composable
fun BlankSettingsContainer() {
    Column(modifier = Modifier.padding(16.dp)) {

        // Top item (rounded top)
        Surface(
            shape = MyMileageShapeDefaults.topListItemShape(),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        ) {}

        Spacer(modifier = Modifier.height(2.dp))

        // Middle item (flat)
        Surface(
            shape = MyMileageShapeDefaults.middleListItemShape(),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        ) {}

        Spacer(modifier = Modifier.height(2.dp))

        // Bottom item (rounded bottom)
        Surface(
            shape = MyMileageShapeDefaults.bottomListItemShape(),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        ) {}
    }
Spacer(modifier = Modifier.height(100.dp))
    Text(text = "Test")
}
data class AbsoluteSmoothCornerShape(
    val cornerRadiusTL: Dp = 0.dp,
    val smoothnessAsPercentBR: Int = 0,
    val cornerRadiusTR: Dp = 0.dp,
    val smoothnessAsPercentTL: Int = 0,
    val cornerRadiusBL: Dp = 0.dp,
    val smoothnessAsPercentTR: Int = 0,
    val cornerRadiusBR: Dp = 0.dp,
    val smoothnessAsPercentBL: Int = 0
) : Shape {
    // Delegate to RoundedCornerShape with per-corner values (topStart, topEnd, bottomEnd, bottomStart)
    private fun asRounded(): RoundedCornerShape =
        RoundedCornerShape(
            topStart = cornerRadiusTL,
            topEnd = cornerRadiusTR,
            bottomEnd = cornerRadiusBR,
            bottomStart = cornerRadiusBL
        )

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return asRounded().createOutline(size, layoutDirection, density)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun FloatingNavBarDemoWithSmoothShape() {
    var expansionFraction by remember { mutableFloatStateOf(0f) }

    val navBarElevation = 6.dp
    val navBarCornerRadiusValue = 26.dp

    // Compute top corner radius based on expansion fraction (matches your logic)
    val animatedTopCornerRadius by animateDpAsState(
        targetValue = if (expansionFraction < 0.2f) {
            lerp(12.dp, navBarCornerRadiusValue, (expansionFraction / 0.2f).coerceIn(0f, 1f))
        } else {
            navBarCornerRadiusValue
        },
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium),
        label = "CornerRadiusAnim"
    )

    // Bottom radius — for demo we'll keep it equal to navBarCornerRadiusValue (you can change)
    val animatedBottomRadius = navBarCornerRadiusValue

    // Translate downward as expansionFraction increases (so the bar "hides" / moves)
    val translateY by animateDpAsState(
        targetValue = lerp(0.dp, 36.dp, expansionFraction),
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium),
        label = "TranslateYAnim"
    )

    val density = LocalDensity.current
    val translateYPx = with(density) { translateY.toPx() }

    // Build the shape using our AbsoluteSmoothCornerShape wrapper
    val actualShape = remember(animatedTopCornerRadius, animatedBottomRadius) {
        AbsoluteSmoothCornerShape(
            cornerRadiusTL = animatedTopCornerRadius,
            cornerRadiusTR = animatedTopCornerRadius,
            cornerRadiusBL = animatedBottomRadius,
            cornerRadiusBR = animatedBottomRadius,
            // smoothness parameters are present for API parity but ignored in this lightweight wrapper
            smoothnessAsPercentBR = 60,
            smoothnessAsPercentTL = 60,
            smoothnessAsPercentTR = 60,
            smoothnessAsPercentBL = 60
        )
    }

    Scaffold(
        bottomBar = {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .graphicsLayer { translationY = translateYPx }
            ) {
                Surface(
                    shape = actualShape,
                    tonalElevation = navBarElevation,
                    shadowElevation = navBarElevation,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .align(Alignment.Center)
                ) {
                    Row(
                        Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(onClick = { /* Handle Home click */ }) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Home"
                            )
                        }
                        IconButton(onClick = { /* Handle Search click */ }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search"
                            )
                        }
                        IconButton(onClick = { /* Handle Library click */ }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.LibraryBooks,
                                contentDescription = "Library"
                            )
                        }
                    }
                }
            }
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Simulated player expansion: ${"%.2f".format(expansionFraction)}")
                Spacer(modifier = Modifier.height(12.dp))
                Slider(
                    value = expansionFraction,
                    onValueChange = { expansionFraction = it },
                    valueRange = 0f..1f,
                    modifier = Modifier.padding(24.dp)
                )
            }
        }
    )
}
@Composable
@Preview(showSystemUi = true)
fun ProgressIndicators() {
    CircularProgressIndicator(modifier = Modifier.padding(top = 64.dp))
    CircularWavyProgressIndicator(modifier = Modifier.padding(top = 128.dp))
    LinearProgressIndicator(modifier = Modifier.padding(top = 256.dp))
    LinearWavyProgressIndicator()
}
@Composable
@Preview(showSystemUi = true)
fun Filter() {
    IconButton(
        onClick = { /* Handle Filter */ },
        shape = MaterialShapes.Square.toShape(),
        colors = IconButtonDefaults.iconButtonColors(containerColor = primaryContainerLight),
        modifier = Modifier.padding(50.dp)
    ) {
        Image(
            imageVector = Icons.Rounded.FilterList,
            contentDescription = "Filter"
        )
    }
}
@Composable
fun Test(
    selectedIndex: Int,
    onSelected: (Int) -> Unit
) {
    val options = listOf("All", "Done", "Draft")
    val checkedIcons = listOf(
        Icons.AutoMirrored.Filled.List,
        Icons.Filled.Done,
        Icons.Filled.PendingActions
    )
    val unCheckedIcons = listOf(
        Icons.AutoMirrored.Rounded.List,
        Icons.Outlined.Done,
        Icons.Rounded.PendingActions
    )

    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)
    ) {
        options.forEachIndexed { index, label ->
            ToggleButton(
                checked = selectedIndex == index,
                onCheckedChange = { onSelected(index) },
                modifier = Modifier.weight(1f),
                shapes =
                    when (index) {
                    0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                    options.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                    else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                }
            ) {
                Icon(
                    if (selectedIndex == index) checkedIcons[index] else unCheckedIcons[index],
                    contentDescription = label
                )
                Spacer(Modifier.size(ToggleButtonDefaults.IconSpacing))
                Text(label)
            }
        }
        IconButton(
            onClick = { /* Handle Filter */ },
            shape = MaterialShapes.Square.toShape(),
            colors = IconButtonDefaults.iconButtonColors(containerColor = primaryContainerLight),
        ) {
            Image(
                imageVector = Icons.Rounded.FilterList,
                contentDescription = "Filter"
            )
        }
    }
}
@Composable
fun FilterButtonGroup(
    selectedIndex: Int,
    onSelected: (Int) -> Unit
) {
    val options = listOf("All", "Done", "Draft")
    val checkedIcons = listOf(
        Icons.AutoMirrored.Filled.List,
        Icons.Filled.Done,
        Icons.Filled.PendingActions
    )
    val unCheckedIcons = listOf(
        Icons.AutoMirrored.Rounded.List,
        Icons.Outlined.Done,
        Icons.Rounded.PendingActions
    )

    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)
    ) {
        options.forEachIndexed { index, label ->
            ToggleButton(
                checked = selectedIndex == index,
                onCheckedChange = { onSelected(index) },
                modifier = Modifier.weight(1f),
                shapes =
                    when (index) {
                        0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                        options.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                        else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                    }
            ) {
                Icon(
                    if (selectedIndex == index) checkedIcons[index] else unCheckedIcons[index],
                    contentDescription = label
                )
                Spacer(Modifier.size(ToggleButtonDefaults.IconSpacing))
                Text(label)
            }
        }
        IconButton(
            onClick = { /* Handle Filter */ },
            shape = MaterialShapes.Square.toShape(),
            colors = IconButtonDefaults.iconButtonColors(containerColor = primaryContainerLight),
        ) {
            Image(
                imageVector = Icons.Rounded.FilterList,
                contentDescription = "Filter"
            )
        }
    }
}
@Composable
@Preview
fun TestPreview() {
    Test(selectedIndex = 0, onSelected = {})
}
@Composable
@Preview
fun M3EDropdown() {

}
fun learning() {
    // Create new local variables that can be changed
    val heck = "Heck, my phone is dead"
    val ohNo = "Oh no, my phone is dead"
    var conditionOfPhone = ohNo

    if (conditionOfPhone == heck) {
        println(heck)
    } else {
        println(ohNo)
    }
}
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExpressiveBarChart(modelProducer: CartesianChartModelProducer) {
    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberColumnCartesianLayer(
                ColumnCartesianLayer.ColumnProvider.series(
                    rememberLineComponent(
                        thickness = 16.dp,
                        shape = CorneredShape.Pill // <--- KEY COMPONENT
                    )
                )
            )
        ),
        modelProducer = modelProducer,
        // Apply Expressive Motion
        animationSpec = MaterialTheme.motionScheme.fastSpatialSpec() //defaultEffectsSpec(),
    )
}
@Composable
@Preview(showSystemUi = true)
fun ExpressiveBarChartPreview() {
    val modelProducer = remember { CartesianChartModelProducer() }
    LaunchedEffect(Unit) {
        modelProducer.runTransaction {
            /* * Add dummy data here.
             * The syntax depends on your Vico version, but usually looks like:
             */
            columnSeries {
                series(4, 12, 8, 16, 10) // Example values
            }
        }
    }
    ExpressiveBarChart(modelProducer = modelProducer)
}

