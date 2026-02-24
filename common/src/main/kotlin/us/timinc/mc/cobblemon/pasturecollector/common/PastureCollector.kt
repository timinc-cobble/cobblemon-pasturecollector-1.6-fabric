package us.timinc.mc.cobblemon.pasturecollector.common

import com.cobblemon.mod.common.item.group.CobblemonItemGroups
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockBehaviour
import us.timinc.mc.cobblemon.droploottables.DropLootTables
import us.timinc.mc.cobblemon.pasturecollector.common.blocks.PastureCollectorBlock
import us.timinc.mc.cobblemon.pasturecollector.common.blocks.entities.PastureCollectorBlockEntities
import us.timinc.mc.cobblemon.pasturecollector.common.dropper.PastureDropper
import us.timinc.mc.cobblemon.pasturecollector.common.inventory.PastureCollectorMenus
import us.timinc.mc.cobblemon.timcore.AbstractConfig
import us.timinc.mc.cobblemon.timcore.AbstractMod
import us.timinc.mc.cobblemon.timcore.BlockContainer

const val MOD_ID = "pasturecollector"

object PastureCollector : AbstractMod<PastureCollector.PastureCollectorConfig>(
    MOD_ID,
    PastureCollectorConfig::class.java
) {
    class PastureCollectorConfig : AbstractConfig() {
        val baseCobblemonLootEnabled: Boolean = true
        val chanceToDrop: Float = 1F
        val resurrectedDropTargets: List<String> = listOf(
            "pasture_collector_storage",
            "pasture_collector_world_position"
        )
    }

    object DataKeys {
        object DropperTypes {
            val PASTURE = modResource("pasture")
        }

        object DropTargetTypes {
            val PASTURE_COLLECTOR_STORAGE = modResource("pasture_collector_storage")
            val PASTURE_COLLECTOR_WORLD_POSITION = modResource("pasture_collector_world_position")
        }
    }

    object DropperTypes {
        val PASTURE = DropLootTables.DropperTypes.register(
            DataKeys.DropperTypes.PASTURE,
            PastureDropper.DROPPER_TYPE
        )
    }

    object BlockRegistry {
        val PASTURE_COLLECTOR = registerBlock(
            "pasture_collector",
            BlockContainer(
                blockBuilder = { PastureCollectorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)) },
                tab = CobblemonItemGroups.BLOCKS_KEY
            )
        )
    }

    init {
        @Suppress("UnusedExpression")
        BlockRegistry
        PastureCollectorMenus.register()
        PastureCollectorBlockEntities.register()
    }
}
