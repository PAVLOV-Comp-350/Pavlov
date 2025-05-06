package com.example.pavlov.views

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty
import com.airbnb.lottie.model.KeyPath
import com.airbnb.lottie.value.ScaleXY
import com.example.pavlov.R
import com.example.pavlov.viewmodels.AnyEvent
import com.example.pavlov.viewmodels.PetAccessory
import com.example.pavlov.viewmodels.PetAccessoryType
import com.example.pavlov.viewmodels.PetEvent
import com.example.pavlov.viewmodels.PetState
import com.example.pavlov.viewmodels.SharedState
import java.util.Locale

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AnimatedPetViewer(state)

            val scrollState = rememberScrollState()
            Column(
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
                    onAccessoryChanged = {
                        onEvent(
                            PetEvent.equipAccessory(
                                PetAccessoryType.HAT,
                                it
                            )
                        )
                    }
                )
                PetAccessorySelectorWidget(
                    PetAccessoryType.FACE,
                    state.equippedFace,
                    state.purchasedFaces,
                    onAccessoryChanged = {
                        onEvent(
                            PetEvent.equipAccessory(
                                PetAccessoryType.FACE,
                                it
                            )
                        )
                    }
                )
                ShopSection(
                    state.shopItems,
                    onPurchase = { onEvent(PetEvent.purchaseAccessory(it)) })
            }
        }
    }
}

/**
 * A temporary composable to help debug Lottie KeyPaths by logging them.
 * Add this to your layout when debugging, and remove afterwards.
 */
@Composable
fun LottieKeyPathDebugger(lottieResId: Int) {
    val context = LocalContext.current
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(lottieResId))

    // Use AndroidView to host a LottieAnimationView temporarily for debugging
    // This allows us to access the resolveKeyPath method from the core Lottie library
    AndroidView(
        modifier = Modifier.size(1.dp), // Make it very small/invisible in your layout
        factory = { context ->
            LottieAnimationView(context).apply {
                // Initial setup
            }
        },
        update = { lottieAnimationView ->
            // Set the composition on the view once it's loaded
            composition?.let { comp ->
                lottieAnimationView.setComposition(comp)

                // Resolve and log all key paths after the composition is set
                // This will print the hierarchy Lottie understands for your animation
                val keyPaths =
                    lottieAnimationView.resolveKeyPath(KeyPath("**")) // "**" is a wildcard for any path
                Log.d(
                    "LottieDebugger",
                    "--- All KeyPaths for R.raw.${context.resources.getResourceEntryName(lottieResId)} ---"
                )
                if (keyPaths.isEmpty()) {
                    Log.d(
                        "LottieDebugger",
                        "No key paths found. Composition might not be fully loaded or is empty."
                    )
                } else {
                    keyPaths.forEach { keyPath ->
                        // Log the string representation of the key path
                        Log.d("LottieDebugger", keyPath.keysToString())
                    }
                }
                Log.d("LottieDebugger", "--- End KeyPaths ---")

                // You can also try resolving a specific path to see what it finds:
                // val fezOpacityPaths = lottieAnimationView.resolveKeyPath(KeyPath("Fez", "Transform", "Opacity"))
                // Log.d("LottieDebugger", "--- 'Fez.Transform.Opacity' Resolved Paths ---")
                // if (fezOpacityPaths.isEmpty()) {
                //     Log.d("LottieDebugger", "No paths found for 'Fez.Transform.Opacity'")
                // } else {
                //     fezOpacityPaths.forEach { path -> Log.d("LottieDebugger", path.keys.joinToString(".")) }
                // }
                // Log.d("LottieDebugger", "--- End Resolved Paths ---")
            }
        }
    )

    // You don't need to display the LottieAnimation here, the AndroidView handles the debugging
}


@Composable
fun AnimatedPetViewer(state: PetState) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.dog_animation)
    )

    val props = rememberLottieDynamicProperties(
        rememberLottieDynamicProperty(
            property = LottieProperty.TRANSFORM_SCALE,
            keyPath = arrayOf("Doggo", "FezLayer"),
            callback = {
                if (state.equippedHat == PetAccessory.FEZ) it.startValue else ScaleXY(
                    0f,
                    0f
                )
            }
        ),
        rememberLottieDynamicProperty(
            property = LottieProperty.TRANSFORM_SCALE,
            keyPath = arrayOf("Doggo", "DunceCapLayer"),
            callback = {
                if (state.equippedHat == PetAccessory.DUNCE_CAP) it.startValue else ScaleXY(
                    0f,
                    0f
                )
            }
        ),
        rememberLottieDynamicProperty(
            property = LottieProperty.TRANSFORM_SCALE,
            keyPath = arrayOf("Doggo", "GlassesLayer"),
            callback = {
                if (state.equippedFace == PetAccessory.GLASSES) it.startValue else ScaleXY(
                    0f,
                    0f
                )
            }
        ),
        rememberLottieDynamicProperty(
            property = LottieProperty.TRANSFORM_SCALE,
            keyPath = arrayOf("Doggo", "SunglassesLayer"),
            callback = {
                if (state.equippedFace == PetAccessory.SUN_GLASSES) it.startValue else ScaleXY(
                    0f,
                    0f
                )
            }
        ),
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            iterations = LottieConstants.IterateForever,
            composition = composition,
            modifier = Modifier.fillMaxWidth(),
            dynamicProperties = props
        )
    }
}

@Composable
fun ShopSection(
    shopItems: List<PetAccessory>,
    onPurchase: (PetAccessory) -> Unit
) {
    if (shopItems.isNotEmpty()) {
        Text(text = "Shop", style = MaterialTheme.typography.displayMedium)
        shopItems.forEach { item ->
            ShopItem(item = item, onPurchase = { onPurchase(it) })
        }
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
        ) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.displaySmall
            )
            ShopItemPurchaseButton(item.price, onPurchase = { onPurchase(item) })
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

@Composable
fun PetAccessorySelectorWidget(
    type: PetAccessoryType,
    active: PetAccessory,
    availableAccessories: List<PetAccessory>,
    onAccessoryChanged: (PetAccessory) -> Unit,
) {
    var allAccessories = remember(availableAccessories) {
        availableAccessories + PetAccessory.NONE
    }

    Column(
        modifier = Modifier
            .padding(4.dp)
            .shadow(8.dp)
            .clip(RoundedCornerShape(8.dp))
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(10.dp),
    ) {
        val typeString = type.name
            .lowercase()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        Text(
            text = "$typeString Slot:",
            style = MaterialTheme.typography.displaySmall
        )
        LazyRow(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items(allAccessories) {
                Surface(
                    color = Color.Unspecified,
                    onClick = { onAccessoryChanged(it) },
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if(active == it)
                                MaterialTheme.colorScheme.surfaceContainerHighest
                            else
                                MaterialTheme.colorScheme.surfaceContainer
                        )
                        .padding(12.dp)

                ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = it.displayName,
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                            if (it == PetAccessory.NONE) {
                                Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(RoundedCornerShape(50))
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                )
                            } else {
                                Icon(
                                    painter = painterResource(id = it.resId),
                                    contentDescription = it.displayName,
                                    modifier = Modifier.size(72.dp),
                                    tint = Color.Unspecified
                                )
                            }
                    }
                }
            }
        }
    }

}

@Preview
@Composable
fun PreviewEquipmentSelector() {
    PetAccessorySelectorWidget(
        type = PetAccessoryType.HAT,
        active = PetAccessory.FEZ,
        availableAccessories = PetAccessory.getAccessoriesByType(
            PetAccessoryType.HAT
        ).filter { it != PetAccessory.NONE },
        onAccessoryChanged = {})
}
