package us.timinc.mc.cobblemon.pasturecollector.common.container

import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Player

class VariedSlotContainer(
    val size: Int,
    private val onUpdate: () -> Unit,
    private val onOpen: () -> Unit = {},
    private val onClose: () -> Unit = {},
) : SimpleContainer(size) {
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
}
