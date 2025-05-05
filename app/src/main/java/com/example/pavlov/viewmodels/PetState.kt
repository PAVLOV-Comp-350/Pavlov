package com.example.pavlov.viewmodels


import com.example.pavlov.R
import androidx.annotation.DrawableRes

enum class PetAccessoryType {
    NONE,
    HAT,
    FACE,
}

enum class PetAccessory(
    val type: PetAccessoryType,
    val displayName: String,
    val lottieLayerName: String,
    val price: Int,
    @DrawableRes val resId: Int
) {
    NONE(PetAccessoryType.NONE, "None", "none", -1, -1),
    FEZ(PetAccessoryType.HAT, "Fez", "Fez", 800, R.drawable.fez),
    DUNCE_CAP(PetAccessoryType.HAT, "Dunce Cap", "Dunce Cap", 100, R.drawable.dunce_cap),
    GLASSES(PetAccessoryType.FACE, "Glasses", "Glasses", 400, R.drawable.glasses),
    SUN_GLASSES(PetAccessoryType.FACE, "Sun Glasses", "Sunglasses", 1000, R.drawable.sunglasses);
    
    companion object  {
        fun getAccessoriesByType(type: PetAccessoryType): List<PetAccessory> {
            return entries.filter { it.type == type || it == NONE  }
                .sortedBy { it != NONE }
        }

        fun getUnPurchasedAccessories(purchased: List<PetAccessory>): List<PetAccessory> {
            return entries.filter { !purchased.contains(it) && it != NONE }
        }
    }
}

data class ShopItemInfo(
    @DrawableRes val resId: Int,
    val name: String,
    val price: Int,
)

data class PetState(
    val shopItems: List<PetAccessory> = emptyList(),
    val equippedHat: PetAccessory = PetAccessory.NONE,
    val purchasedHats: List<PetAccessory> = emptyList(),
    val equippedFace: PetAccessory = PetAccessory.NONE,
    val purchasedFaces: List<PetAccessory> = emptyList(),
)