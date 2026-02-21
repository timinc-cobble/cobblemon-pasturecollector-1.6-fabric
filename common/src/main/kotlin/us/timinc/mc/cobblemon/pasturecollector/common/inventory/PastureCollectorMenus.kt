package us.timinc.mc.cobblemon.pasturecollector.common.inventory

import net.minecraft.world.inventory.MenuType

object PastureCollectorMenus {
    val PASTURE_COLLECTOR = MenuType.register<PastureCollectorMenu>("pasture_collector", ::PastureCollectorMenu)

    fun register() {
    }
}
