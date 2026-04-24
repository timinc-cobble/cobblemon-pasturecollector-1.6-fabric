package us.timinc.mc.cobblemon.pasturecollector.common.droptarget

import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import us.timinc.mc.cobblemon.droploottables.api.DropTarget
import us.timinc.mc.cobblemon.pasturecollector.common.blocks.entities.PastureCollectorBlockEntity

class PastureCollectorWorldPositionTarget(val collector: PastureCollectorBlockEntity) : DropTarget {
    override fun dropTo(stack: ItemStack): ItemStack {
        val level = collector.level as? ServerLevel ?: return stack
        val stackEntity = ItemEntity(
            level,
            collector.pos.x.toDouble(),
            collector.pos.y.toDouble(),
            collector.pos.z.toDouble(),
            stack
        )
        level.addFreshEntity(stackEntity)
        return ItemStack.EMPTY
    }
}
