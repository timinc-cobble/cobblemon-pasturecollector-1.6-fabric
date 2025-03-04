package us.timinc.mc.cobblemon.pasturecollector.blocks.entities

import com.cobblemon.mod.common.CobblemonBlocks
import com.cobblemon.mod.common.api.drop.ItemDropEntry
import com.cobblemon.mod.common.block.entity.PokemonPastureBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.Container
import net.minecraft.world.ContainerHelper
import net.minecraft.world.InteractionResult
import net.minecraft.world.WorldlyContainer
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.minecraft.world.phys.Vec3
import us.timinc.mc.cobblemon.droploottables.api.droppers.FormDropContext
import us.timinc.mc.cobblemon.droploottables.lootconditions.LootConditions
import us.timinc.mc.cobblemon.pasturecollector.PastureCollectorMod
import us.timinc.mc.cobblemon.pasturecollector.droppers.PastureBlockDropper
import kotlin.math.min

class PastureCollectorBlockEntity(val pos: BlockPos, state: BlockState) :
    BlockEntity(PastureCollectorBlockEntities.PASTURECOLLECTOR_BLOCKENTITYTYPE, pos, state),
    WorldlyContainer {

    companion object {
        enum class DropResult {
            FULL,
            PARTIAL,
            NONE,
            NO_DROP
        }
    }

    val inventory = NonNullList.withSize(1, ItemStack.EMPTY)

    fun retrieveItemManually(level: ServerLevel): InteractionResult {
        val stack = getItem(0)
        if (stack.isEmpty) return InteractionResult.FAIL
        val tossPos = pos.center.add(Vec3(0.0, 0.65, 0.0))
        level.addFreshEntity(ItemEntity(level, tossPos.x, tossPos.y, tossPos.z, stack))
        clearContent()
        return InteractionResult.SUCCESS
    }

    fun onItemChanged(level: Level, oldState: BlockState, newState: BlockState) {
        level.sendBlockUpdated(blockPos, oldState, newState, Block.UPDATE_CLIENTS)
        setChanged()
    }

    override fun clearContent() {
        inventory.clear()
    }

    override fun getContainerSize(): Int = inventory.size

    override fun isEmpty(): Boolean = getItem(0).isEmpty

    override fun getItem(i: Int): ItemStack = inventory[i]

    override fun removeItem(i: Int, j: Int): ItemStack = ContainerHelper.removeItem(inventory, i, j)

    override fun removeItemNoUpdate(i: Int): ItemStack {
        if (inventory[i].isEmpty) return ItemStack.EMPTY
        val stack = getItem(i)
        inventory[i] = ItemStack.EMPTY
        return stack
    }

    override fun setItem(i: Int, itemStack: ItemStack) {
        val level = level ?: return
        val oldState = level.getBlockState(pos)
        inventory[i] = itemStack
        val newState = level.getBlockState(pos)
        onItemChanged(level, oldState, newState)
    }

    override fun stillValid(player: Player): Boolean = Container.stillValidBlockEntity(this, player)

    override fun getSlotsForFace(side: Direction): IntArray {
        val result = IntArray(inventory.size)
        for (i in result.indices) {
            result[i] = i
        }
        return result
    }

    override fun canPlaceItemThroughFace(i: Int, itemStack: ItemStack, direction: Direction?): Boolean = false

    override fun canTakeItemThroughFace(i: Int, itemStack: ItemStack, direction: Direction): Boolean = true

    override fun saveAdditional(data: CompoundTag, provider: HolderLookup.Provider) {
        super.saveAdditional(data, provider)
        ContainerHelper.saveAllItems(data, inventory, true, provider)
    }

    override fun loadAdditional(data: CompoundTag, provider: HolderLookup.Provider) {
        super.loadAdditional(data, provider)
        clearContent()
        ContainerHelper.loadAllItems(data, inventory, provider)
    }

    fun attemptToGetDrop(level: ServerLevel, pos: BlockPos): DropResult {
        val chosenMon = getNearbyPastures(
            level,
            pos
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

        if (PastureCollectorMod.config.baseCobblemonLootEnabled && !PastureBlockDropper.lootTableExists(level, PastureBlockDropper.getFormDropId(chosenMon.form))) {
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

        drops.shuffle()

        if (drops.all { it.isEmpty }) return DropResult.NO_DROP

        var didAnyDrop = false
        for (itemStack in inventory) {
            val toAdd = drops.firstOrNull { !it.isEmpty }
            if (toAdd === null) continue
            addStackWhereFits(toAdd)
            didAnyDrop = true
        }

        if (!didAnyDrop) return DropResult.NONE
        if (drops.any { !it.isEmpty }) return DropResult.PARTIAL
        return DropResult.FULL
    }

    private fun addStackWhereFits(stackToAdd: ItemStack) {
        for (i in 0 until inventory.size) {
            val stackInSlot = inventory[i]
            if (stackInSlot.isEmpty) {
                // This is a good slot because empty, just dump it in here.
                setItem(i, stackToAdd.copyAndClear())
                return
            }
            if (!stackToAdd.isStackable) continue
            if (stackInSlot.item == stackToAdd.item) {
                // Might be a good candidate, same item type.
                if (stackInSlot.count >= stackInSlot.maxStackSize) continue
                // Has space, let's stack.
                val transferCount = min(stackToAdd.count, stackInSlot.maxStackSize - stackInSlot.count)
                stackToAdd.shrink(transferCount)
                stackInSlot.grow(transferCount)
                if (stackToAdd.isEmpty) return
            }
        }
    }

    fun getNearbyPastures(level: ServerLevel, pos: BlockPos): List<PokemonPastureBlockEntity> {
        val positionsToCheck = mutableListOf<BlockPos>(
            blockPos.north(), blockPos.east(), blockPos.south(), blockPos.west()
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