package us.timinc.mc.cobblemon.pasturecollector.fabric.extensions

import net.minecraft.world.phys.shapes.Shapes

object Shapes16 {
    private const val DEFAULT_UNIT = 16

    @Suppress("LongParameterList")
    fun box(
        x1: Int, y1: Int, z1: Int,
        x2: Int, y2: Int, z2: Int,
    ) = Shapes.box(
        x1.toDouble() / DEFAULT_UNIT, y1.toDouble() / DEFAULT_UNIT, z1.toDouble() / DEFAULT_UNIT,
        x2.toDouble() / DEFAULT_UNIT, y2.toDouble() / DEFAULT_UNIT, z2.toDouble() / DEFAULT_UNIT,
    )
}
