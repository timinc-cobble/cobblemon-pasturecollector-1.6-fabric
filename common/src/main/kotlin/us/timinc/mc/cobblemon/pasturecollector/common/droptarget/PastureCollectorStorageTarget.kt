package us.timinc.mc.cobblemon.pasturecollector.common.droptarget

import com.cobblemon.mod.common.util.sendParticlesServer
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.world.item.ItemStack
import net.minecraft.world.phys.Vec3
import us.timinc.mc.cobblemon.droploottables.api.DropTarget
import us.timinc.mc.cobblemon.pasturecollector.common.blocks.entities.PastureCollectorBlockEntity
import us.timinc.mc.cobblemon.pasturecollector.common.handlers.PastureTickHandler.PARTICLE_AMOUNT
import us.timinc.mc.cobblemon.pasturecollector.common.handlers.PastureTickHandler.PARTICLE_OFFSET_Y
import us.timinc.mc.cobblemon.pasturecollector.common.handlers.PastureTickHandler.PARTICLE_POS_XZ_RANDOMNESS_MAX
import us.timinc.mc.cobblemon.pasturecollector.common.handlers.PastureTickHandler.PARTICLE_POS_XZ_RANDOMNESS_MIN
import us.timinc.mc.cobblemon.pasturecollector.common.handlers.PastureTickHandler.PARTICLE_POS_Y
import us.timinc.mc.cobblemon.pasturecollector.common.handlers.PastureTickHandler.getLevel
import kotlin.random.Random

class PastureCollectorStorageTarget(val collector: PastureCollectorBlockEntity) : DropTarget {
    override fun dropTo(stack: ItemStack): ItemStack {
        collector.handleDropPlacement(stack)
        return ItemStack.EMPTY
    }
}
