package us.timinc.mc.cobblemon.pasturecollector.common.handlers

import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import us.timinc.mc.cobblemon.droploottables.api.DropHandler
import us.timinc.mc.cobblemon.droploottables.api.DropTarget
import us.timinc.mc.cobblemon.pasturecollector.common.PastureCollector
import us.timinc.mc.cobblemon.pasturecollector.common.blocks.entities.PastureCollectorBlockEntity
import us.timinc.mc.cobblemon.pasturecollector.common.dropper.PastureDropper
import us.timinc.mc.cobblemon.pasturecollector.common.droptarget.PastureCollectorStorageTarget
import us.timinc.mc.cobblemon.pasturecollector.common.droptarget.PastureCollectorWorldPositionTarget

class PastureTickHandler : DropHandler<PastureDropper.Context, PastureDropper, PastureCollectorBlockEntity> {
    override val dropperTypeId: ResourceLocation = PastureCollector.DataKeys.DropperTypes.PASTURE

    override val dropTargetTypes: MutableMap<ResourceLocation, (target: PastureCollectorBlockEntity) -> DropTarget?> =
        mutableMapOf(
            PastureCollector.DataKeys.DropTargetTypes.PASTURE_COLLECTOR_STORAGE to { blockEntity ->
                PastureCollectorStorageTarget(blockEntity)
            },
            PastureCollector.DataKeys.DropTargetTypes.PASTURE_COLLECTOR_WORLD_POSITION to { blockEntity ->
                PastureCollectorWorldPositionTarget(blockEntity)
            },
        )

    override val selectedDropTargetTypes: List<ResourceLocation>
        get() = TODO("Not yet implemented")

    override fun getContext(evt: PastureCollectorBlockEntity): PastureDropper.Context = PastureDropper.Context(
        evt.level as ServerLevel,
        evt.pos,
    )

    override fun getLevel(evt: PastureCollectorBlockEntity): ServerLevel? = evt.level as ServerLevel?
}
