package us.timinc.mc.cobblemon.pasturecollector.blocks.entities

import com.cobblemon.mod.common.CobblemonBlocks
import com.cobblemon.mod.common.api.drop.ItemDropEntry
import com.cobblemon.mod.common.block.entity.PokemonPastureBlockEntity
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.minecraft.world.phys.Vec3
import us.timinc.mc.cobblemon.droploottables.api.droppers.FormDropContext
import us.timinc.mc.cobblemon.droploottables.lootconditions.LootConditions
import us.timinc.mc.cobblemon.pasturecollector.PastureCollectorMod
import us.timinc.mc.cobblemon.pasturecollector.container.VariedSlotContainer
import us.timinc.mc.cobblemon.pasturecollector.droppers.PastureBlockDropper
import us.timinc.mc.cobblemon.pasturecollector.inventory.PastureCollectorMenu
import us.timinc.mc.cobblemon.pasturecollector.network.BlockPosPayload

class PastureCollectorBlockEntity(override val pos: BlockPos, state: BlockState) :
    ExtendedScreenHandlerFactory<BlockPosPayload>,
    AbstractVariableSizeContainerEntity<PastureCollectorBlockEntity>(
        PastureCollectorBlockEntities.PASTURECOLLECTOR_BLOCKENTITYTYPE,
        pos,
        state
    ) {
    companion object {
        const val CONTAINER_SIZE: Int = 4

        val TITLE: Component = Component.translatable(
            ResourceLocation.fromNamespaceAndPath("block", "pasturecollector.pasture_collector").toLanguageKey()
        )

        enum class DropResult {
            FULL,
            PARTIAL,
            NONE,
            NO_DROP,
            CONTAINER_FULL
        }
    }

    override fun getScreenOpeningData(player: ServerPlayer): BlockPosPayload = BlockPosPayload(pos)

    override fun getDisplayName(): Component = TITLE

    override var inventory = VariedSlotContainer(
        size = CONTAINER_SIZE,
        onUpdate = { ->
            level?.let { onUpdate(it, blockState, blockState) }
        },
        onOpen = { -> },
        onClose = { -> }
    )

    override fun createMenu(syncId: Int, inventory: Inventory, player: Player) = PastureCollectorMenu(
        syncId, inventory, this
    )

    fun onUpdate(level: Level, oldState: BlockState, newState: BlockState) {
        level.sendBlockUpdated(blockPos, oldState, newState, Block.UPDATE_CLIENTS)
        setChanged()
    }

    fun attemptToGetDrop(level: ServerLevel, pos: BlockPos): DropResult {
        val chosenMon = getNearbyPastures(
            level
        ).flatMap { pasture ->
            pasture
                .tetheredPokemon
                .mapNotNull { it.getPokemon() }
                .filter { it.entity != null }
        }.randomOrNull() ?: return DropResult.NO_DROP

        val lootParams = LootParams(
            level,
            mapOf(
                LootContextParams.ORIGIN to pos.center,
                LootContextParams.THIS_ENTITY to chosenMon.entity!!,
                LootConditions.PARAMS.POKEMON_DETAILS to chosenMon,
                LootContextParams.BLOCK_ENTITY to this,
                LootContextParams.BLOCK_STATE to level.getBlockState(pos)
            ),
            mapOf(),
            0F
        )
        val drops = PastureBlockDropper.getDrops(lootParams, FormDropContext(chosenMon.form)).toMutableList()

        if (PastureCollectorMod.config.baseCobblemonLootEnabled && !PastureBlockDropper.lootTableExists(
                level,
                PastureBlockDropper.getFormDropId(chosenMon.form)
            )
        ) {
            val baseDrops = chosenMon.form.drops.getDrops(pokemon = chosenMon)
            drops.addAll(baseDrops.mapNotNull { drop ->
                if (drop is ItemDropEntry) {
                    val item = level.registryAccess().registryOrThrow(Registries.ITEM).get(drop.item)
                    if (item === null) {
                        PastureCollectorMod.debug("Unable to drop item ${drop.item}", true)
                        return@mapNotNull null
                    }
                    return@mapNotNull ItemStack(item, drop.quantityRange?.random() ?: drop.quantity)
                }
                drop.drop(null, level, pos.center.add(Vec3(0.0, 1.0, 0.0)), null)
                return@mapNotNull null
            })
        }

        if (drops.all { it.isEmpty }) return DropResult.NO_DROP

        var didAnyDrop = false
        drops
            .filter { !it.isEmpty }
            .forEach {
                if (!inventory.canAddItem(it)) return DropResult.CONTAINER_FULL
                inventory.addItem(it)
                didAnyDrop = true
            }

        if (!didAnyDrop) return DropResult.NONE
        if (drops.any { !it.isEmpty }) return DropResult.PARTIAL
        return DropResult.FULL
    }

    fun getNearbyPastures(level: ServerLevel): List<PokemonPastureBlockEntity> {
        val positionsToCheck = mutableListOf<BlockPos>(
            pos.north(), pos.east(), pos.south(), pos.west()
        )
        val listOfNearbyPastures = mutableListOf<PokemonPastureBlockEntity>()
        for (positionToCheck in positionsToCheck) {
            val targetBlockState = level.getBlockState(positionToCheck)
            val targetBlockEntity = level.getBlockEntity(positionToCheck)
            if (targetBlockState.block == CobblemonBlocks.PASTURE && targetBlockEntity is PokemonPastureBlockEntity) {
                listOfNearbyPastures.add(targetBlockEntity)
            }
        }
        return listOfNearbyPastures
    }
}