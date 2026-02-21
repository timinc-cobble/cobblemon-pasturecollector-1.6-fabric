package us.timinc.mc.cobblemon.pasturecollector.common.blocks.entities

import com.cobblemon.mod.common.CobblemonBlocks
import com.cobblemon.mod.common.block.entity.PokemonPastureBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.NonNullList
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.WorldlyContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity
import net.minecraft.world.level.block.state.BlockState
import us.timinc.mc.cobblemon.pasturecollector.common.blocks.entities.PastureCollectorBlockEntities.PASTURE_COLLECTOR_BLOCK_ENTITY

class PastureCollectorBlockEntity(val pos: BlockPos, state: BlockState) :
    RandomizableContainerBlockEntity(PASTURE_COLLECTOR_BLOCK_ENTITY, pos, state), WorldlyContainer {
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

    private val items: NonNullList<ItemStack> = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY)

    override fun getDefaultName(): Component = TITLE
    override fun getContainerSize(): Int = CONTAINER_SIZE
    override fun canPlaceItemThroughFace(i: Int, itemStack: ItemStack, direction: Direction?): Boolean = false
    override fun canTakeItemThroughFace(i: Int, itemStack: ItemStack, direction: Direction) = true

    fun onUpdate(level: Level, oldState: BlockState, newState: BlockState) {
        level.sendBlockUpdated(pos, oldState, newState, Block.UPDATE_CLIENTS)
    }

    fun attemptToGetDrop(level: ServerLevel, pos: BlockPos): DropResult = DropResult.NO_DROP
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


    override fun getItems(): NonNullList<ItemStack> = items

    override fun setItems(items: NonNullList<ItemStack>) {
        this.items.clear()
        this.items.addAll(items.take(CONTAINER_SIZE))
    }

    override fun createMenu(
        i: Int,
        inventory: Inventory?
    ): AbstractContainerMenu? {
        TODO("Not yet implemented")
    }

    override fun getSlotsForFace(direction: Direction?): IntArray? {
        TODO("Not yet implemented")
    }
}
