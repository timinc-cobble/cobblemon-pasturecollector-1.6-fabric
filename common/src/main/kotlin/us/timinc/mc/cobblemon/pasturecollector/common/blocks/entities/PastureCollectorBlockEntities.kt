package us.timinc.mc.cobblemon.pasturecollector.common.blocks.entities

import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.entity.BlockEntityType
import us.timinc.mc.cobblemon.droploottables.DropLootTables.modResource
import us.timinc.mc.cobblemon.pasturecollector.common.blocks.PastureCollectorBlocks

object PastureCollectorBlockEntities {
    lateinit var PASTURECOLLECTOR_BLOCKENTITYTYPE: BlockEntityType<PastureCollectorBlockEntity>

    fun register() {
        PASTURECOLLECTOR_BLOCKENTITYTYPE =
            Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                modResource("pasture_collector_entity"),
                BlockEntityType.Builder.of(::PastureCollectorBlockEntity, PastureCollectorBlocks.PASTURE_COLLECTOR)
                    .build(null)
            )
    }
}
