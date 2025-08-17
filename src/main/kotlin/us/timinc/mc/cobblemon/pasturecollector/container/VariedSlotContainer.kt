package us.timinc.mc.cobblemon.pasturecollector.container

import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage
import net.minecraft.core.Direction
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Player

class VariedSlotContainer(
    val size: Int,
    private val onUpdate: () -> Unit,
    private val onOpen: () -> Unit,
    private val onClose: () -> Unit,
) : SimpleContainer(size) {
    val storage: InventoryStorage = InventoryStorage.of(this, null)

    fun setChange() {
        super.setChanged()
        onUpdate()
    }

    override fun startOpen(player: Player) {
        super.startOpen(player)
        onOpen()
        onUpdate()
    }

    override fun stopOpen(player: Player) {
        super.stopOpen(player)
        onClose()
        onUpdate()
    }

    fun getProvider(direction: Direction) = storage
}