package us.timinc.mc.cobblemon.pasturecollector.blocks

import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockBehaviour
import us.timinc.mc.cobblemon.pasturecollector.PastureCollector.modIdentifier

object PastureCollectorBlocks {
    val PASTURE_COLLECTOR = PastureCollectorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE))

    fun register() {
        Registry.register(
            BuiltInRegistries.BLOCK, modIdentifier("pasture_collector"), PASTURE_COLLECTOR
        )
        Registry.register(
            BuiltInRegistries.ITEM, modIdentifier("pasture_collector"), BlockItem(PASTURE_COLLECTOR, Item.Properties())
        )
    }
}