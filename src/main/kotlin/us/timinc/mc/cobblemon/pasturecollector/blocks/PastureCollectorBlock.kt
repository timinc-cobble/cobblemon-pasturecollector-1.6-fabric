package us.timinc.mc.cobblemon.pasturecollector.blocks

import com.cobblemon.mod.common.util.sendParticlesServer
import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.Containers
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import us.timinc.mc.cobblemon.pasturecollector.blocks.entities.PastureCollectorBlockEntity
import us.timinc.mc.cobblemon.pasturecollector.extensions.Shapes16

class PastureCollectorBlock(properties: Properties) : BaseEntityBlock(properties) {
    companion object {
        val CODEC = simpleCodec(::PastureCollectorBlock)

        val SHAPE = Shapes.or(
            Shapes16.box(
                0, 0, 0,
                16, 12, 16
            ),

            Shapes16.box(
                0, 12, 0,
                2, 14, 2
            ),
            Shapes16.box(
                14, 12, 0,
                16, 14, 2
            ),
            Shapes16.box(
                0, 12, 14,
                2, 14, 16
            ),
            Shapes16.box(
                14, 12, 14,
                16, 14, 16
            ),

            Shapes16.box(
                0, 14, 0,
                16, 16, 16
            ),
        )
    }

    override fun codec(): MapCodec<out PastureCollectorBlock> = CODEC

    override fun newBlockEntity(blockPos: BlockPos, blockState: BlockState): BlockEntity =
        PastureCollectorBlockEntity(blockPos, blockState)

    override fun getShape(
        blockState: BlockState,
        blockGetter: BlockGetter,
        blockPos: BlockPos,
        collisionContext: CollisionContext,
    ): VoxelShape = SHAPE

    override fun randomTick(
        state: BlockState,
        level: ServerLevel,
        pos: BlockPos,
        random: RandomSource,
    ) {
        super.randomTick(state, level, pos, random)
        val particle = when (getBlockEntity(pos, level).attemptToGetDrop(level, pos)) {
            PastureCollectorBlockEntity.Companion.DropResult.NO_DROP -> null
            PastureCollectorBlockEntity.Companion.DropResult.NONE -> ParticleTypes.CAMPFIRE_COSY_SMOKE
            PastureCollectorBlockEntity.Companion.DropResult.PARTIAL -> ParticleTypes.CAMPFIRE_COSY_SMOKE
            PastureCollectorBlockEntity.Companion.DropResult.FULL -> ParticleTypes.COMPOSTER
        }
        particle?.let {
            level.sendParticlesServer(
                it,
                pos.center.add(Vec3(0.0, 0.65, 0.0)),
                3,
                Vec3(0.0, 0.1, 0.0),
                0.0
            )
        }
    }

    override fun useWithoutItem(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        player: Player,
        blockHitResult: BlockHitResult,
    ): InteractionResult {
        if (level !is ServerLevel) return InteractionResult.SUCCESS_NO_ITEM_USED
        return getBlockEntity(blockPos, level).retrieveItemManually(level)
    }

    override fun getRenderShape(blockState: BlockState): RenderShape = RenderShape.MODEL

    override fun isRandomlyTicking(blockState: BlockState): Boolean = true

    fun getBlockEntity(pos: BlockPos, level: ServerLevel): PastureCollectorBlockEntity {
        val blockEntity = level.getBlockEntity(pos)
        if (blockEntity !is PastureCollectorBlockEntity) throw Error("Tried to get an entity for a Pasture Block, but it wasn't a Pasture Block Entity.")
        return blockEntity
    }

    override fun onRemove(
        state: BlockState,
        world: Level,
        pos: BlockPos,
        newState: BlockState,
        moved: Boolean,
    ) {
        Containers.dropContentsOnDestroy(state, newState, world, pos)
        super.onRemove(state, world, pos, newState, moved)
    }
}