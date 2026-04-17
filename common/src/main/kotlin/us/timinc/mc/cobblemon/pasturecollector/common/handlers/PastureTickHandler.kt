package us.timinc.mc.cobblemon.pasturecollector.common.handlers

import com.cobblemon.mod.common.api.drop.ItemDropEntry
import com.cobblemon.mod.common.util.asIdentifierDefaultingNamespace
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.ItemStack
import us.timinc.mc.cobblemon.droploottables.api.DropHandler
import us.timinc.mc.cobblemon.droploottables.api.DropTarget
import us.timinc.mc.cobblemon.pasturecollector.common.MOD_ID
import us.timinc.mc.cobblemon.pasturecollector.common.PastureCollector
import us.timinc.mc.cobblemon.pasturecollector.common.dropper.PastureDropper
import us.timinc.mc.cobblemon.pasturecollector.common.droptarget.PastureCollectorStorageTarget
import us.timinc.mc.cobblemon.pasturecollector.common.droptarget.PastureCollectorWorldPositionTarget
import us.timinc.mc.cobblemon.pasturecollector.common.event.PastureCollectorTickedEvent

object PastureTickHandler : DropHandler<PastureDropper.Context, PastureDropper, PastureCollectorTickedEvent> {
    enum class DropResult {
        CLIENT,
        FULL,
        PARTIAL,
        NONE,
        NO_DROP,
        CONTAINER_FULL
    }

    const val PARTICLE_AMOUNT = 3
    const val PARTICLE_OFFSET_Y = 0.1
    const val PARTICLE_POS_Y = 0.65
    const val PARTICLE_POS_XZ_RANDOMNESS_MIN = -0.15
    const val PARTICLE_POS_XZ_RANDOMNESS_MAX = 0.15

    override val dropperTypeId: ResourceLocation = PastureCollector.DataKeys.DropperTypes.PASTURE

    override val dropTargetTypes: MutableMap<ResourceLocation, (target: PastureCollectorTickedEvent) -> DropTarget?> =
        mutableMapOf(
            PastureCollector.DataKeys.DropTargetTypes.PASTURE_COLLECTOR_STORAGE to { evt ->
                PastureCollectorStorageTarget(evt.collector)
            },
            PastureCollector.DataKeys.DropTargetTypes.PASTURE_COLLECTOR_WORLD_POSITION to { evt ->
                PastureCollectorWorldPositionTarget(evt.collector)
            },
        )

    override val selectedDropTargetTypes: List<ResourceLocation> =
        PastureCollector.config.pastureDropTargets.map { it.asIdentifierDefaultingNamespace(MOD_ID) }

    override fun getContext(evt: PastureCollectorTickedEvent) = PastureDropper.Context(
        evt.collector.level as ServerLevel,
        evt.pokemon,
    )

    override fun getLevel(evt: PastureCollectorTickedEvent): ServerLevel? = evt.collector.level as? ServerLevel

    override fun processOtherDrops(evt: PastureCollectorTickedEvent): List<ItemStack> {
        if (!PastureCollector.config.baseCobblemonLootEnabled) return listOf()

        val baseDrops = evt.pokemon.form.drops.getDrops(pokemon = evt.pokemon)
        return baseDrops.mapNotNull { drop ->
            if (drop is ItemDropEntry) {
                val item = getLevel(evt)?.registryAccess()?.registryOrThrow(Registries.ITEM)?.get(drop.item)
                if (item == null) {
                    PastureCollector.debugger.debug("Unable to drop item ${drop.item}", true)
                    return@mapNotNull null
                }
                return@mapNotNull ItemStack(item, drop.quantityRange?.random() ?: drop.quantity)
            }
            return@mapNotNull null
        }
    }
}
