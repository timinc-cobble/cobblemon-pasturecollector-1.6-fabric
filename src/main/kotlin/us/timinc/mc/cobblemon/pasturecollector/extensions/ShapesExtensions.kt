package us.timinc.mc.cobblemon.pasturecollector.extensions

import net.minecraft.world.phys.shapes.Shapes

object Shapes16 {
    fun box(
        x1: Int, y1: Int, z1: Int,
        x2: Int, y2: Int, z2: Int,
    ) = Shapes.box(
        x1.toDouble() / 16, y1.toDouble() / 16, z1.toDouble() / 16,
        x2.toDouble() / 16, y2.toDouble() / 16, z2.toDouble() / 16,
    )
}