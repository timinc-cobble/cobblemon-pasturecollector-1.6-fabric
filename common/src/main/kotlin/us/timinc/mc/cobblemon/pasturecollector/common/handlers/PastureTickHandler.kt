package us.timinc.mc.cobblemon.pasturecollector.common.handlers

import com.cobblemon.mod.common.api.drop.ItemDropEntry
import com.cobblemon.mod.common.util.asIdentifierDefaultingNamespace
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.ItemStack
import us.timinc.mc.cobblemon.droploottables.DropLootTables
import us.timinc.mc.cobblemon.droploottables.api.DropHandler
import us.timinc.mc.cobblemon.droploottables.api.DropTarget
import us.timinc.mc.cobblemon.droploottables.droptarget.PlayerDropTarget
import us.timinc.mc.cobblemon.droploottables.droptarget.PlayerEnderChestDropTarget
import us.timinc.mc.cobblemon.droploottables.droptarget.PokemonEntityDropTarget
import us.timinc.mc.cobblemon.droploottables.droptarget.PokemonHeldItemDropTarget
import us.timinc.mc.cobblemon.droploottables.droptarget.PokemonHeldItemReplaceDropTarget
import us.timinc.mc.cobblemon.pasturecollector.common.MOD_ID
import us.timinc.mc.cobblemon.pasturecollector.common.PastureCollector
import us.timinc.mc.cobblemon.pasturecollector.common.dropper.PastureDropper
import us.timinc.mc.cobblemon.pasturecollector.common.droptarget.PastureCollectorStorageTarget
import us.timinc.mc.cobblemon.pasturecollector.common.droptarget.PastureCollectorWorldPositionTarget
import us.timinc.mc.cobblemon.pasturecollector.common.event.PasturePokemonTickedEvent

object PastureTickHandler : DropHandler<PastureDropper.Context, PastureDropper, PasturePokemonTickedEvent> {
    override val dropperTypeId: ResourceLocation = PastureCollector.DataKeys.DropperTypes.PASTURE

    override val dropTargetTypes: MutableMap<ResourceLocation, (target: PasturePokemonTickedEvent) -> DropTarget?> =
        mutableMapOf(
            PastureCollector.DataKeys.DropTargetTypes.PASTURE_COLLECTOR_STORAGE to { evt ->
                PastureCollectorStorageTarget(evt.collector)
            },
            PastureCollector.DataKeys.DropTargetTypes.PASTURE_COLLECTOR_WORLD_POSITION to { evt ->
                PastureCollectorWorldPositionTarget(evt.collector)
            },
            DropLootTables.DataKeys.DropTargetTypes.PLAYER_ENDER_STORAGE to { evt ->
                evt.pokemonEntity.pokemon.getOwnerPlayer()?.let(::PlayerEnderChestDropTarget)
            },
            DropLootTables.DataKeys.DropTargetTypes.PLAYER_INVENTORY to { evt ->
                evt.pokemonEntity.pokemon.getOwnerPlayer()?.let(::PlayerDropTarget)
            },
            DropLootTables.DataKeys.DropTargetTypes.POKEMON_WORLD_POSITION to { evt ->
                PokemonEntityDropTarget(evt.pokemonEntity)
            },
            DropLootTables.DataKeys.DropTargetTypes.POKEMON_HELD_ITEM to { evt ->
                PokemonHeldItemDropTarget(evt.pokemonEntity.pokemon)
            },
            DropLootTables.DataKeys.DropTargetTypes.POKEMON_HELD_ITEM_REPLACE to { evt ->
                PokemonHeldItemReplaceDropTarget(evt.pokemonEntity.pokemon)
            },
        )

    override val selectedDropTargetTypes: List<ResourceLocation> =
        PastureCollector.config.pastureDropTargets.map { it.asIdentifierDefaultingNamespace(MOD_ID) }

    override fun getContext(evt: PasturePokemonTickedEvent) = PastureDropper.Context(
        evt.collector.level as ServerLevel,
        evt.pokemonEntity,
    )

    override fun getLevel(evt: PasturePokemonTickedEvent): ServerLevel? = evt.collector.level as? ServerLevel

    override fun processOtherDrops(evt: PasturePokemonTickedEvent): List<ItemStack> {
        if (!PastureCollector.config.baseCobblemonLootEnabled) return listOf()

        val baseDrops = evt.pokemonEntity.form.drops.getDrops(pokemon = evt.pokemonEntity.pokemon)
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

    override fun cleanup(evt: PasturePokemonTickedEvent, drops: MutableList<ItemStack>) {
        if (!PastureCollector.config.playCryOnDrop) return
        if (drops.all { it.isEmpty }) return

        evt.pokemonEntity.cry()
    }
}
