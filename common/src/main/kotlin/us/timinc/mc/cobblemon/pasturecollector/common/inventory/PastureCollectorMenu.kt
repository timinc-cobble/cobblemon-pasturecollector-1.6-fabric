package us.timinc.mc.cobblemon.pasturecollector.common.inventory

import net.minecraft.world.Container
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ClickType
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import us.timinc.mc.cobblemon.pasturecollector.common.PastureCollector.Registries.Menu.PASTURE_COLLECTOR_MENU
import us.timinc.mc.cobblemon.pasturecollector.common.blocks.entities.PastureCollectorBlockEntity
import us.timinc.mc.cobblemon.pasturecollector.common.blocks.entities.PastureCollectorBlockEntity.Companion.CONTAINER_SIZE

class PastureCollectorMenu(syncId: Int, playerInventory: Inventory, val container: Container) :
    AbstractContainerMenu(PASTURE_COLLECTOR_MENU, syncId) {

    constructor(syncId: Int, playerInventory: Inventory) : this(
        syncId,
        playerInventory,
        SimpleContainer(CONTAINER_SIZE)
    )

    init {
        container.startOpen(playerInventory.player)

        addBlockInventory()
        addPlayerInventory(playerInventory)
        addPlayerHotbar(playerInventory)
    }

    override fun stillValid(player: Player): Boolean = container.stillValid(player)

    @Suppress("MagicNumber")
    private fun addPlayerInventory(playerInventory: Inventory) {
        repeat(3) { row ->
            repeat(9) { column ->
                addSlot(Slot(playerInventory, 9 + (column + (row * 9)), 8 + (column * 18), 48 + (row * 18)))
            }
        }
    }

    @Suppress("MagicNumber")
    private fun addPlayerHotbar(playerInventory: Inventory) {
        repeat(9) { column ->
            addSlot(Slot(playerInventory, column, 8 + (column * 18), 106))
        }
    }

    @Suppress("MagicNumber")
    private fun addBlockInventory() {
        repeat(CONTAINER_SIZE) { slot ->
            addSlot(Slot(container, slot, 53 + (slot * 18), 18))
        }
    }

    override fun canDragTo(slot: Slot): Boolean = slot.container is Inventory

    override fun clicked(index: Int, button: Int, clickType: ClickType, player: Player) {
        if (index < 0) return super.clicked(index, button, clickType, player)
        val slot = getSlot(index)
        val allowed = listOf(
            ClickType.PICKUP,
            ClickType.PICKUP_ALL
        )

        if (clickType in allowed && slot.container is SimpleContainer && !carried.isEmpty) return

        return super.clicked(index, button, clickType, player)
    }

    override fun quickMoveStack(player: Player, slotIndex: Int): ItemStack {
        var itemStack = ItemStack.EMPTY
        val slot = this.slots[slotIndex]

        if (!slot.hasItem()) return itemStack
        if (slot.container !is PastureCollectorBlockEntity) return itemStack

        itemStack = slot.item.copy()
        when (slotIndex >= CONTAINER_SIZE) {
            true -> {
                if (!this.moveItemStackTo(slot.item, 0, CONTAINER_SIZE - 1, true)) {
                    return ItemStack.EMPTY
                }
            }

            false -> {
                if (!this.moveItemStackTo(slot.item, CONTAINER_SIZE, this.slots.size, true)) {
                    return ItemStack.EMPTY
                }
            }
        }

        if (slot.item.isEmpty) {
            slot.setByPlayer(ItemStack.EMPTY)
        } else {
            slot.setChanged()
        }

        return itemStack
    }
}
