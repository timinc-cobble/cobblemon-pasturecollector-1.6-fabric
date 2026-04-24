package us.timinc.mc.cobblemon.pasturecollector.common

import com.cobblemon.mod.common.api.Priority
import com.cobblemon.mod.common.api.reactive.EventObservable
import com.cobblemon.mod.common.item.group.CobblemonItemGroups
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockBehaviour
import us.timinc.mc.cobblemon.droploottables.DropLootTables
import us.timinc.mc.cobblemon.pasturecollector.common.PastureCollector.Registries.Block.PASTURE_COLLECTOR
import us.timinc.mc.cobblemon.pasturecollector.common.blocks.PastureCollectorBlock
import us.timinc.mc.cobblemon.pasturecollector.common.blocks.entities.PastureCollectorBlockEntity
import us.timinc.mc.cobblemon.pasturecollector.common.dropper.PastureDropper
import us.timinc.mc.cobblemon.pasturecollector.common.event.PasturePokemonTickedEvent
import us.timinc.mc.cobblemon.pasturecollector.common.handlers.PastureTickHandler
import us.timinc.mc.cobblemon.pasturecollector.common.inventory.PastureCollectorMenu
import us.timinc.mc.cobblemon.timcore.AbstractConfig
import us.timinc.mc.cobblemon.timcore.AbstractMod
import us.timinc.mc.cobblemon.timcore.BlockContainer

const val MOD_ID = "pasturecollector"

object PastureCollector : AbstractMod<PastureCollector.PastureCollectorConfig>(
    MOD_ID,
    PastureCollectorConfig::class.java
) {
    class PastureCollectorConfig : AbstractConfig() {
        companion object {
            @Suppress("unused")
            enum class TickType {
                TICK, // On every tick of the block entity.
                RANDOM_TICK, // On random tick of the block entity. Like wheat.
            }

            @Suppress("unused")
            enum class TargetMon {
                ALL, // Every mon in all the nearby pastures gets a chance to drop at the same time.
                RANDOM, // A random mon in all the nearby pastures gets a chance to drop.
            }

            enum class IntervalType {
                PER_POKEMON,
                PER_DROPPER,
            }
        }

        val baseCobblemonLootEnabled: Boolean = true
        val checkDiagonal: Boolean = false
        val chanceToDrop: Float = 1F
        val pastureDropTargets: List<String> = listOf(
            "pasturecollector:pasture_collector_storage"
        )
        val tickType: TickType = TickType.RANDOM_TICK
        val targetMon: TargetMon = TargetMon.RANDOM
        val blockTickInterval: Int = 0
        val playCryOnDrop: Boolean = false
    }

    object Registries {
        object Block {
            val PASTURE_COLLECTOR = registerBlock(
                "pasture_collector",
                BlockContainer(
                    blockBuilder = { PastureCollectorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)) },
                    tab = CobblemonItemGroups.BLOCKS_KEY
                )
            )
        }

        object Entity {
            val PASTURE_COLLECTOR_BLOCK_ENTITY: BlockEntityType<PastureCollectorBlockEntity> = Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                modResource("pasture_collector_entity"),
                BlockEntityType.Builder.of(
                    ::PastureCollectorBlockEntity,
                    PASTURE_COLLECTOR.block
                ).build(null)
            )
        }

        object Menu {
            val PASTURE_COLLECTOR_MENU: MenuType<PastureCollectorMenu> =
                MenuType.register("pasture_collector", ::PastureCollectorMenu)
        }
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

    object Events {
        val PASTURE_COLLECTOR_TICKED = EventObservable<PasturePokemonTickedEvent>()
    }

    init {
        @Suppress("UnusedExpression")
        Registries.Block
        @Suppress("UnusedExpression")
        Registries.Entity
        @Suppress("UnusedExpression")
        Registries.Menu

        Events.PASTURE_COLLECTOR_TICKED.subscribe(Priority.NORMAL, PastureTickHandler::handle)
    }
}
