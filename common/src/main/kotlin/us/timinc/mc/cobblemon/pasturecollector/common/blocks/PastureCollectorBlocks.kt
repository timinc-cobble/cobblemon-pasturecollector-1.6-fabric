package us.timinc.mc.cobblemon.pasturecollector.common.blocks

import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockBehaviour

object PastureCollectorBlocks {
    val PASTURE_COLLECTOR = PastureCollectorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE))

    fun register() = Unit
}
