package us.timinc.mc.cobblemon.pasturecollector.inventory

import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.flag.FeatureFlagSet
import net.minecraft.world.inventory.MenuType
import us.timinc.mc.cobblemon.pasturecollector.PastureCollectorMod.modIdentifier


object PastureCollectorMenus {
    val PASTURE_COLLECTOR_INVENTORY = MenuType(::PastureCollectorMenu, FeatureFlagSet.of())

    fun register() {
        Registry.register(BuiltInRegistries.MENU, modIdentifier("pasture_collector_menu"), PASTURE_COLLECTOR_INVENTORY)
    }
}