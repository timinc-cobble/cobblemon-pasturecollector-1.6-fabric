package us.timinc.mc.cobblemon.pasturecollector.common.droptarget

import net.minecraft.world.item.ItemStack
import us.timinc.mc.cobblemon.droploottables.api.DropTarget
import us.timinc.mc.cobblemon.pasturecollector.common.blocks.entities.PastureCollectorBlockEntity

class PastureCollectorWorldPositionTarget(val blockEntity: PastureCollectorBlockEntity) : DropTarget {
    override fun dropTo(stack: ItemStack): ItemStack {
        blockEntity.dropItemToLevel(stack)
        return ItemStack.EMPTY
    }
}
