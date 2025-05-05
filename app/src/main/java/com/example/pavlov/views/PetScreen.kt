package com.example.pavlov.views

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.pavlov.R
import com.example.pavlov.viewmodels.AnyEvent
import com.example.pavlov.viewmodels.PetAccessory
import com.example.pavlov.viewmodels.PetAccessoryType
import com.example.pavlov.viewmodels.PetEvent
import com.example.pavlov.viewmodels.PetState
import com.example.pavlov.viewmodels.SharedState
import com.example.pavlov.viewmodels.ShopItemInfo
import kotlin.math.roundToInt

@Composable
fun PetScreen(
state: PetState,
sharedState: SharedState,
onEvent: (AnyEvent) -> Unit,
onNavigate: (Screen) -> Unit,
) {
    Scaffold(
        topBar = {
            Column {
                PavlovTopBar(sharedState, onEvent = { onEvent(it) })
            }
        },
        bottomBar = {
            PavlovNavbar(
                activeScreen = sharedState.activeScreen,
                onNavigate = onNavigate
            )
        },
    ) { paddingValues ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AnimatedPetViewer()

            val scrollState = rememberScrollState()
            Column (
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PetAccessorySelectorWidget(
                    PetAccessoryType.HAT,
                    state.equippedHat,
                    state.purchasedHats,
                    onAccessoryChanged = { onEvent(PetEvent.equipAccessory(PetAccessoryType.HAT, it)) }
                )
                PetAccessorySelectorWidget(
                    PetAccessoryType.FACE,
                    state.equippedFace,
                    state.purchasedFaces,
                    onAccessoryChanged = { onEvent(PetEvent.equipAccessory(PetAccessoryType.FACE, it)) }
                )
                ShopSection(state.shopItems, onPurchase = { onEvent(PetEvent.purchaseAccessory(it))})
            }
        }
    }
}
@Composable
fun AnimatedPetViewer() {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.dog_animation)
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            iterations = LottieConstants.IterateForever,
            composition = composition,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ShopSection(
    shopItems: List<PetAccessory>,
    onPurchase: (PetAccessory) -> Unit
) {
    Text(text = "Shop", style = MaterialTheme.typography.displayMedium)
    shopItems.forEach { item ->
        ShopItem(item = item, onPurchase = {})
    }
}

@Composable
fun ShopItem(
    item: PetAccessory,
    onPurchase: (item: PetAccessory) -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(4.dp)
            .shadow(8.dp)
            .clip(RoundedCornerShape(8.dp))
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(10.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ShopItemPreview(item.resId, item.name)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalAlignment = Alignment.Start
        ){
            Text(
                text = item.name,
                style = MaterialTheme.typography.displaySmall
            )
            ShopItemPurchaseButton(item.price, onPurchase = {onPurchase(item)})
        }

    }
}

@Composable
fun ShopItemPreview(@DrawableRes itemResId: Int, itemName: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(12.dp)
    ) {
        Icon(
            painter = painterResource(id = itemResId),
            contentDescription = itemName,
            modifier = Modifier.size(72.dp),
            tint = Color.Unspecified
        )

    }
}

@Composable
fun ShopItemPurchaseButton(price: Int, onPurchase: (price: Int) -> Unit) {
    Button(
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
        onClick = { onPurchase(price) }
    ) {
        Text(
            text = "$price",
            fontSize = 18.sp,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Icon(
            modifier = Modifier
                .padding(vertical = 2.dp, horizontal = 6.dp)
                .size(20.dp),
            painter = painterResource(id = R.drawable.dog_treat),
            contentDescription = "Treat Icon",
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}

//@Composable
//fun PetAccessorySelector(type: PetAccessoryType, active: String) {
//    Row(
//        modifier = Modifier
//            .padding(4.dp)
//            .shadow(8.dp)
//            .clip(RoundedCornerShape(8.dp))
//            .fillMaxWidth()
//            .background(MaterialTheme.colorScheme.surfaceContainerLow)
//            .padding(10.dp),
//        horizontalArrangement = Arrangement.SpaceBetween,
//        verticalAlignment = Alignment.CenterVertically,
//    ) {
//        Text(text = equipmentCategory)
//        PetAccessorySelectorWidget(PetAccessoryType.HAT, PetAccessory.FEZ, {})
//    }
//}

@Composable
fun PetAccessorySelectorWidget(
    type: PetAccessoryType,
    active: PetAccessory,
    _availableAccessories: List<PetAccessory>,
    onAccessoryChanged: (PetAccessory) -> Unit,
) {
    var availableAccessories = remember(_availableAccessories) {
        _availableAccessories + PetAccessory.NONE
    }

    var currentIndex: Int by remember {
        mutableIntStateOf(availableAccessories
            .indexOfFirst { it == active }
            .coerceAtLeast(0)
        )
    }
    // Effect to update the internal currentIndex if the 'active' accessory changes externally
    LaunchedEffect(active, availableAccessories) {
        val newIndex = availableAccessories.indexOfFirst { it == active }
        if (newIndex != -1 && newIndex != currentIndex) {
            currentIndex = newIndex
        } else if (newIndex == -1 && currentIndex != 0) {
            currentIndex = 0
        }
    }
    // Effect to report the currently displayed accessory whenever the internal currentIndex changes
    LaunchedEffect(currentIndex) {
        onAccessoryChanged(availableAccessories[currentIndex])
    }
    // Define swipe threshold
    val swipeThreshold = 50.dp
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Use accessory type name for the slot title
        Text(
            text = "${type.name.lowercase().replaceFirstChar { if (it.isLowerCase()) it.uppercase() else it.toString() }}:",
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Use AnimatedContent to animate transitions between items based on the internal currentIndex
        AnimatedContent(
            targetState = currentIndex,
            transitionSpec = {
                // Determine the direction of the state change (index increasing or decreasing)
                val movingLeft = targetState > initialState || (initialState == availableAccessories.size - 1 && targetState == 0)

                if (movingLeft) {
                    // If index is increasing (moving to the next item, visually swiping left)
                    // The new content slides in from the right, and the old content slides out to the left
                    slideIntoContainer(towards = SlideDirection.Right, animationSpec = tween(300)) togetherWith
                            slideOutOfContainer(towards = SlideDirection.Left, animationSpec = tween(300))
                } else {
                    // If index is decreasing (moving to the previous item, visually swiping right)
                    // The new content slides in from the left, and the old content slides out to the right
                    slideIntoContainer(towards = SlideDirection.Left, animationSpec = tween(300)) togetherWith
                            slideOutOfContainer(towards = SlideDirection.Right, animationSpec = tween(300))
                }
            },
            label = "${type.name}Transition"
        ) { targetIndex -> // targetIndex is the new currentIndex
            val accessory = availableAccessories[targetIndex]
            var offsetX by remember { mutableFloatStateOf(0f) }
            Box(
                modifier = Modifier
                    .size(80.dp) // Set a fixed size for the swipe area/icon display
                    .offset { IntOffset(offsetX.roundToInt(), 0) } // Apply offset for visual drag feedback
                    .background(Color.LightGray.copy(alpha = 0.3f)) // Optional: background for the slot visual
                    .pointerInput(availableAccessories) { // Add swipe gesture detection; key on the list
                        var totalDragAmount = 0f
                        detectHorizontalDragGestures(
                            onHorizontalDrag = { change, dragAmount ->
                                totalDragAmount += dragAmount
                                offsetX = totalDragAmount
                            },
                            onDragEnd = {
                                // Check the total drag amount to determine if a swipe occurred
                                when {
                                    totalDragAmount > swipeThreshold.toPx() -> {
                                        // Swiped right
                                        currentIndex = (currentIndex - 1 + availableAccessories.size) % availableAccessories.size
                                    }
                                    totalDragAmount < -swipeThreshold.toPx() -> {
                                        // Swiped left
                                        currentIndex = (currentIndex + 1) % availableAccessories.size
                                    }
                                    // If drag amount is below threshold, it's not a swipe, reset offset
                                }
                                // Reset visual offset after drag ends
                                offsetX = 0f
                                totalDragAmount = 0f // Reset for the next gesture
                            },
                            onDragCancel = {
                                // Reset visual offset and drag amount if gesture is cancelled
                                offsetX = 0f
                                totalDragAmount = 0f
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                // Display the accessory icon
                if(accessory == PetAccessory.NONE) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = accessory.name,
                        modifier = Modifier.size(60.dp)
                    )
                } else {
                    Icon(
                        painter = painterResource(id = accessory.resId),
                        contentDescription = accessory.name,
                        modifier = Modifier.size(60.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Display the name of the currently selected accessory
        Text(text = availableAccessories[currentIndex].name, fontSize = 14.sp)
    }

}