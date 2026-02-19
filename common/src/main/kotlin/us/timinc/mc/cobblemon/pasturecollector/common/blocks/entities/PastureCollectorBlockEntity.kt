package us.timinc.mc.cobblemon.pasturecollector.common.blocks.entities

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.SimpleContainer
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import us.timinc.mc.cobblemon.pasturecollector.common.blocks.entities.PastureCollectorBlockEntities.PASTURE_COLLECTOR_BLOCK_ENTITY

class PastureCollectorBlockEntity(pos: BlockPos, state: BlockState) :
    BlockEntity(PASTURE_COLLECTOR_BLOCK_ENTITY, pos, state) {
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

//    override fun canPlaceItemThroughFace(i: Int, itemStack: ItemStack, direction: Direction?): Boolean = false

//    override fun canTakeItemThroughFace(i: Int, itemStack: ItemStack, direction: Direction): Boolean = true

//    override fun getScreenOpeningData(player: ServerPlayer): BlockPosPayload = BlockPosPayload(pos)

//    override fun getDisplayName(): Component = TITLE

//    override var inventory = VariedSlotContainer(
//        size = CONTAINER_SIZE,
//        onUpdate = { ->
//            level?.let { onUpdate(it, blockState, blockState) }
//        }
//    )

//    override fun createMenu(syncId: Int, inventory: Inventory, player: Player): PastureCollectorMenu = PastureCollectorMenu(
//        syncId, inventory, this
//    )

    fun onUpdate(level: Level, oldState: BlockState, newState: BlockState) {
//        level.sendBlockUpdated(pos, oldState, newState, Block.UPDATE_CLIENTS)
    }

//    fun attemptToGetDrop(level: ServerLevel, pos: BlockPos): DropResult {
//        val chosenMon = getNearbyPastures(
//            level
//        ).flatMap { pasture ->
//            pasture
//                .tetheredPokemon
//                .mapNotNull { it.getPokemon() }
//                .filter { it.entity != null }
//        }.randomOrNull() ?: return DropResult.NO_DROP
//
//        val lootParams = LootParams(
//            level,
//            mapOf(
//                LootContextParams.ORIGIN to pos.center,
//                LootContextParams.THIS_ENTITY to chosenMon.entity!!,
//                LootConditions.PARAMS.POKEMON_DETAILS to chosenMon,
//                LootContextParams.BLOCK_ENTITY to this,
//                LootContextParams.BLOCK_STATE to level.getBlockState(pos)
//            ),
//            mapOf<>(),
//            0F
//        )
//        val drops = PastureBlockDropper.getDrops(lootParams, FormDropContext(chosenMon.form)).toMutableList()
//
//        if (PastureCollectorMod.config.baseCobblemonLootEnabled && !PastureBlockDropper.lootTableExists(
//                level,
//                PastureBlockDropper.getFormDropId(chosenMon.form)
//            )
//        ) {
//            val baseDrops = chosenMon.form.drops.getDrops(pokemon = chosenMon)
//            drops.addAll(baseDrops.mapNotNull { drop ->
//                if (drop is ItemDropEntry) {
//                    val item = level.registryAccess().registryOrThrow(Registries.ITEM).get(drop.item)
//                    if (item === null) {
//                        PastureCollectorMod.debug("Unable to drop item ${drop.item}", true)
//                        return@mapNotNull null
//                    }
//                    return@mapNotNull ItemStack(item, drop.quantityRange?.random() ?: drop.quantity)
//                }
//                drop.drop(null, level, pos.center.add(Vec3(0.0, 1.0, 0.0)), null)
//                return@mapNotNull null
//            })
//        }
//
//        val nonEmpty = drops.filter { !it.isEmpty }
//        if (nonEmpty.isEmpty()) return DropResult.NO_DROP
//
//        val dropCount = nonEmpty.size
//        var skipCount = 0
//        nonEmpty
//            .forEach {
//                if (!inventory.canAddItem(it)) {
//                    skipCount++
//                    return@forEach
//                }
//                inventory.addItem(it)
//            }
//
//        return when (skipCount) {
//            0 -> DropResult.FULL
//            dropCount -> DropResult.CONTAINER_FULL
//            else -> DropResult.PARTIAL
//        }
//    }

//    fun getNearbyPastures(level: ServerLevel): List<PokemonPastureBlockEntity> {
//        val positionsToCheck = mutableListOf<BlockPos>(
//            pos.north(), pos.east(), pos.south(), pos.west()
//        )
//        val listOfNearbyPastures = mutableListOf<PokemonPastureBlockEntity>()
//        for (positionToCheck in positionsToCheck) {
//            val targetBlockState = level.getBlockState(positionToCheck)
//            val targetBlockEntity = level.getBlockEntity(positionToCheck)
//            if (targetBlockState.block == CobblemonBlocks.PASTURE && targetBlockEntity is PokemonPastureBlockEntity) {
//                listOfNearbyPastures.add(targetBlockEntity)
//            }
//        }
//        return listOfNearbyPastures
//    }
}
