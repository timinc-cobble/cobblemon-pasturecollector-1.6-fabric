package us.timinc.mc.cobblemon.pasturecollector.common.droptarget

import net.minecraft.world.item.ItemStack
import us.timinc.mc.cobblemon.droploottables.api.DropTarget
import us.timinc.mc.cobblemon.pasturecollector.common.blocks.entities.PastureCollectorBlockEntity

class PastureCollectorWorldPositionTarget(val collector: PastureCollectorBlockEntity) : DropTarget {
    override fun dropTo(stack: ItemStack): ItemStack {
        collector.handleDropPlacement(stack)
        return ItemStack.EMPTY
    }
}
