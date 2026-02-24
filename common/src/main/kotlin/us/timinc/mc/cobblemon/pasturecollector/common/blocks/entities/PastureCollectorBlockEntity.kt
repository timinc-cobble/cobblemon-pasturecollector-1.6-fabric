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
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity
import net.minecraft.world.level.block.state.BlockState
import us.timinc.mc.cobblemon.pasturecollector.common.PastureCollector
import us.timinc.mc.cobblemon.pasturecollector.common.PastureCollector.Registries.Entity.PASTURE_COLLECTOR_BLOCK_ENTITY
import us.timinc.mc.cobblemon.pasturecollector.common.event.PastureCollectorTickedEvent
import us.timinc.mc.cobblemon.pasturecollector.common.inventory.PastureCollectorMenu

@Suppress("TooManyFunctions")
class PastureCollectorBlockEntity(val pos: BlockPos, state: BlockState) :
    BaseContainerBlockEntity(PASTURE_COLLECTOR_BLOCK_ENTITY, pos, state), WorldlyContainer {
    companion object {
        const val CONTAINER_SIZE: Int = 4

        val TITLE: Component = Component.translatable(
            ResourceLocation.fromNamespaceAndPath("block", "pasturecollector.pasture_collector").toLanguageKey()
        )
    }

    private val items: NonNullList<ItemStack> = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY)

    override fun getDefaultName(): Component = TITLE
    override fun getContainerSize(): Int = CONTAINER_SIZE
    override fun canPlaceItemThroughFace(i: Int, itemStack: ItemStack, direction: Direction?): Boolean = false
    override fun canTakeItemThroughFace(i: Int, itemStack: ItemStack, direction: Direction) = true

    fun attemptToGetDrop() {
        if (level !is ServerLevel) return
        val chosenMon = getNearbyPastures(level as ServerLevel).flatMap { pasture ->
            pasture
                .tetheredPokemon
                .mapNotNull { it.getPokemon() }
                .filter { it.entity != null }
        }.randomOrNull() ?: return
        PastureCollector.debugger.debug("random tick::chance to drop checked::inside attemptToGetDrop", true)
        PastureCollector.Events.PASTURE_COLLECTOR_TICKED.emit(PastureCollectorTickedEvent(chosenMon, this))
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


    override fun getItems(): NonNullList<ItemStack> = items

    override fun setItems(items: NonNullList<ItemStack>) {
        this.items.clear()
        this.items.addAll(items.take(CONTAINER_SIZE))
    }

    override fun createMenu(containerId: Int, inventory: Inventory): AbstractContainerMenu =
        PastureCollectorMenu(containerId, inventory)

    override fun getSlotsForFace(direction: Direction): IntArray = IntArray(PastureCollectorMenu.CONTAINER_SIZE)

    fun putOrDropItem(stack: ItemStack) {
        if (level !is ServerLevel) return
        if (items.count { it.item != null && it.item != stack.item } == CONTAINER_SIZE) {
            dropItemToLevel(stack)
            return
        }

        var index = items.indexOfFirst { it.item == null || (it.item == stack.item) }
        if (index == -1) index = 0
        if (items[index].item != stack.item) {
            items[index] = stack
            return
        }
        while (items[index].count <= items[index].item.defaultMaxStackSize) {
            items[index].grow(1)
            stack.shrink(1)
        }
        if (stack.count > 0) {
            putOrDropItem(stack)
        }
    }

    fun dropItemToLevel(item: ItemStack) {
        if (level !is ServerLevel) return
        (level as ServerLevel).addFreshEntity(
            ItemEntity(
                level,
                pos.x.toDouble(),
                pos.y.plus(1).toDouble(),
                pos.z.toDouble(),
                item
            )
        )
    }
}
