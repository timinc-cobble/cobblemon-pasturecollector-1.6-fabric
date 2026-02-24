package us.timinc.mc.cobblemon.pasturecollector.common.inventory

import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import us.timinc.mc.cobblemon.pasturecollector.common.PastureCollector.Registries.Menu.PASTURE_COLLECTOR_MENU

class PastureCollectorMenu(syncId: Int, playerInventory: Inventory) :
    AbstractContainerMenu(PASTURE_COLLECTOR_MENU, syncId) {
    companion object {
        const val CONTAINER_SIZE = 4
    }

    val container: SimpleContainer = SimpleContainer(CONTAINER_SIZE)

    init {
        container.startOpen(playerInventory.player)

        addPlayerInventory(playerInventory)
        addPlayerHotbar(playerInventory)
        addBlockInventory()
    }

    override fun quickMoveStack(player: Player, i: Int): ItemStack {
        var itemStack = ItemStack.EMPTY
        val slot = this.slots[i]
        if (slot.hasItem()) {
            val itemStack2 = slot.item
            itemStack = itemStack2.copy()
            if (i < CONTAINER_SIZE) {
                if (!this.moveItemStackTo(itemStack2, CONTAINER_SIZE, this.slots.size, true)) {
                    return ItemStack.EMPTY
                }
            } else if (!this.moveItemStackTo(itemStack2, 0, CONTAINER_SIZE, false)) {
                return ItemStack.EMPTY
            }

            if (itemStack2.isEmpty) {
                slot.setByPlayer(ItemStack.EMPTY)
            } else {
                slot.setChanged()
            }
        }

        return itemStack
    }

    override fun stillValid(player: Player): Boolean = container.stillValid(player)

    @Suppress("MagicNumber")
    private fun addPlayerInventory(playerInventory: Inventory) {
        for (row in 0..2) {
            for (column in 0..8) {
                addSlot(Slot(playerInventory, 9 + (column + (row * 9)), 8 + (column * 18), 48 + (row * 18)))
            }
        }
    }

    @Suppress("MagicNumber")
    private fun addPlayerHotbar(playerInventory: Inventory) {
        for (column in 0..8) {
            addSlot(Slot(playerInventory, column, 8 + (column * 18), 106))
        }
    }

    @Suppress("MagicNumber")
    private fun addBlockInventory() {
        for (column in 0..<CONTAINER_SIZE) {
            addSlot(Slot(container, column, 53 + (column * 18), 18))
        }
    }
}
