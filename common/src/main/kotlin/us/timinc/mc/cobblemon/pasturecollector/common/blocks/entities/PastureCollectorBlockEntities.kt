package us.timinc.mc.cobblemon.pasturecollector.common.blocks.entities

import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.entity.BlockEntityType
import us.timinc.mc.cobblemon.pasturecollector.common.PastureCollector
import us.timinc.mc.cobblemon.pasturecollector.common.PastureCollector.modResource

object PastureCollectorBlockEntities {
    lateinit var PASTURE_COLLECTOR_BLOCK_ENTITY: BlockEntityType<PastureCollectorBlockEntity>

    fun register() {
        PASTURE_COLLECTOR_BLOCK_ENTITY =
            Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                modResource("pasture_collector_entity"),
                BlockEntityType.Builder.of(
                    ::PastureCollectorBlockEntity,
                    PastureCollector.BlockRegistry.PASTURE_COLLECTOR.block
                ).build(null)
            )
    }
}
