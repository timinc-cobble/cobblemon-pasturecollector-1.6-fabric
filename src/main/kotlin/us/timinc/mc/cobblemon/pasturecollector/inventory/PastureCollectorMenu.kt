package us.timinc.mc.cobblemon.pasturecollector.inventory

import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ClickType
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import us.timinc.mc.cobblemon.pasturecollector.blocks.PastureCollectorBlocks
import us.timinc.mc.cobblemon.pasturecollector.blocks.entities.PastureCollectorBlockEntity
import us.timinc.mc.cobblemon.pasturecollector.container.VariedSlotContainer
import us.timinc.mc.cobblemon.pasturecollector.network.BlockPosPayload

class PastureCollectorMenu(
    syncId: Int,
    playerInventory: Inventory,
    val blockEntity: PastureCollectorBlockEntity,
) : AbstractContainerMenu(PastureCollectorMenus.PASTURE_COLLECTOR_INVENTORY, syncId) {
    // Client constructor
    constructor(syncId: Int, playerInventory: Inventory, payload: BlockPosPayload) : this(
        syncId,
        playerInventory,
        playerInventory.player.level().getBlockEntity(payload.pos) as PastureCollectorBlockEntity
    )

    private val context = ContainerLevelAccess.create(blockEntity.level!!, blockEntity.pos)

    init {
        val inventory = blockEntity.inventory
        checkContainerSize(inventory, blockEntity.inventory.size)
        inventory.startOpen(playerInventory.player)

        addPlayerInventory(playerInventory)
        addPlayerHotbar(playerInventory)
        addBlockInventory(blockEntity.inventory)
    }

    // Prevent dragging items into empty block inventory slots
    override fun canDragTo(slot: Slot): Boolean = slot.container is Inventory

    override fun canTakeItemForPickAll(itemStack: ItemStack, slot: Slot): Boolean = true

    override fun quickMoveStack(
        player: Player,
        slotIndex: Int,
    ): ItemStack {
        var newStack = ItemStack.EMPTY
        val slot = getSlot(slotIndex)
        if (slot != null && slot.hasItem()) {
            if (slot.container is Inventory) {
                return ItemStack.EMPTY
            }

            val inSlot: ItemStack = slot.item
            newStack = inSlot.copy()

            if (slotIndex < 36) {
                if (!moveItemStackTo(inSlot, 36, this.slots.size, true)) return ItemStack.EMPTY
            } else if (!moveItemStackTo(inSlot, 0, 36, false)) return ItemStack.EMPTY

            if (inSlot.isEmpty) slot.set(ItemStack.EMPTY)
            else slot.setChanged()
        }

        return newStack
    }

    override fun clicked(index: Int, button: Int, clickType: ClickType, player: Player) {
        // clicks outside window pass index -999
        // clicks on window, but not on slots pass index -1
        // removing this will provoke OOB exceptions
        if (index == -999 || index == -1) return super.clicked(index, button, clickType, player)

        val slot = getSlot(index)
        if (clickType == ClickType.PICKUP && slot.container is VariedSlotContainer && !carried.isEmpty) return

        super.clicked(index, button, clickType, player)
    }

    override fun stillValid(player: Player): Boolean =
        stillValid(context, player, PastureCollectorBlocks.PASTURE_COLLECTOR)

    private fun addPlayerInventory(playerInv: Inventory) {
        for (row in 0..2) {
            for (column in 0..8) {
                addSlot(Slot(playerInv, 9 + (column + (row * 9)), 8 + (column * 18), 48 + (row * 18)))
            }
        }
    }

    private fun addPlayerHotbar(playerInv: Inventory) {
        for (column in 0..8) {
            addSlot(Slot(playerInv, column, 8 + (column * 18), 106))
        }
    }

    private fun addBlockInventory(inventory: VariedSlotContainer) {
        for (column in 0..<inventory.size) {
            addSlot(Slot(inventory, column, 53 + (column * 18), 18))
        }
    }
}