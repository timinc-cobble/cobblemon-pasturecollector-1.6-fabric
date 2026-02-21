package us.timinc.mc.cobblemon.pasturecollector.fabric

import com.cobblemon.mod.common.item.group.CobblemonItemGroups
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import us.timinc.mc.cobblemon.pasturecollector.common.PastureCollector
import us.timinc.mc.cobblemon.timcore.fabric.AbstractFabricMod

object PastureCollectorFabric : AbstractFabricMod(PastureCollector) {
    override fun onInitialize() {
        ItemGroupEvents.modifyEntriesEvent(CobblemonItemGroups.BLOCKS_KEY).register { group ->
            PastureCollector.blocks.forEach { (_, container) ->
                if (container.tab != null && container.item != null) {
                    group.accept { container.item }
                }
            }
        }
    }
}
