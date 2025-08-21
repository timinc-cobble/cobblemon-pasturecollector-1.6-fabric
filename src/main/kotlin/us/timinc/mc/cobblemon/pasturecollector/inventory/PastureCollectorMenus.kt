package us.timinc.mc.cobblemon.pasturecollector.inventory

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import us.timinc.mc.cobblemon.pasturecollector.PastureCollectorMod.modIdentifier
import us.timinc.mc.cobblemon.pasturecollector.network.BlockPosPayload


object PastureCollectorMenus {
    val PASTURE_COLLECTOR_INVENTORY = ExtendedScreenHandlerType(::PastureCollectorMenu, BlockPosPayload.STREAM_CODEC)

    fun register() {
        Registry.register(BuiltInRegistries.MENU, modIdentifier("pasture_collector_menu"), PASTURE_COLLECTOR_INVENTORY)
    }
}