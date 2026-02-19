package us.timinc.mc.cobblemon.pasturecollector.fabric

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import us.timinc.mc.cobblemon.pasturecollector.PastureCollector
import us.timinc.mc.cobblemon.timcore.fabric.AbstractFabricMod

object PastureCollectorFabric : AbstractFabricMod(PastureCollector) {
    override fun onInitialize() {
        PastureCollector.blocks.forEach { (location, container) ->
            Registry.register(
                BuiltInRegistries.BLOCK, location, container.block
            )

            if (container.item != null) {
                Registry.register(
                    BuiltInRegistries.ITEM, location, container.item
                )
                if (container.tab != null) {
                    ItemGroupEvents.modifyEntriesEvent(container.tab).register { it.accept(container.item) }
                }
            }
        }
    }
}
