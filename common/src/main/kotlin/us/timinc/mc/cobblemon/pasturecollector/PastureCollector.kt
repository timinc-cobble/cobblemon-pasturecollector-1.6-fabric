@file:Suppress("UnusedExpression")

package us.timinc.mc.cobblemon.pasturecollector

import com.cobblemon.mod.common.item.group.CobblemonItemGroups
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockBehaviour
import us.timinc.mc.cobblemon.pasturecollector.common.blocks.PastureCollectorBlock
import us.timinc.mc.cobblemon.pasturecollector.common.blocks.PastureCollectorBlocks
import us.timinc.mc.cobblemon.timcore.AbstractConfig
import us.timinc.mc.cobblemon.timcore.AbstractMod
import us.timinc.mc.cobblemon.timcore.BlockContainer
import us.timinc.mc.cobblemon.timcore.ItemContainer

const val MOD_ID = "pasturecollector"

object PastureCollector : AbstractMod<PastureCollector.PastureCollectorConfig>(
    MOD_ID,
    PastureCollectorConfig::class.java
) {
    class PastureCollectorConfig : AbstractConfig() {
        val baseCobblemonLootEnabled: Boolean = true
        val chanceToDrop: Float = 1F
    }


    object BlockRegistry {
        val PASTURE_COLLECTOR_BLOCK = registerBlock(
            "pasture_collector",
            BlockContainer(
                blockBuilder = { PastureCollectorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)) },
                tab = CobblemonItemGroups.BLOCKS_KEY
            )
        )
    }

    init {
        BlockRegistry
    }
}
