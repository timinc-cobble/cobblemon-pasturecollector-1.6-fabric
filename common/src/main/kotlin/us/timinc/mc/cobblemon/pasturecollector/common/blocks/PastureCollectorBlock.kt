package us.timinc.mc.cobblemon.pasturecollector.common.blocks

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
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
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import us.timinc.mc.cobblemon.pasturecollector.common.PastureCollector
import us.timinc.mc.cobblemon.pasturecollector.common.PastureCollector.PastureCollectorConfig.Companion.TickType
import us.timinc.mc.cobblemon.pasturecollector.common.blocks.entities.PastureCollectorBlockEntity
import us.timinc.mc.cobblemon.pasturecollector.common.exceptions.PastureCollectorEntityNotFound
import us.timinc.mc.cobblemon.pasturecollector.common.extensions.box16

class PastureCollectorBlock(properties: Properties) : BaseEntityBlock(properties) {
    companion object {
        val CODEC: MapCodec<PastureCollectorBlock> = simpleCodec(::PastureCollectorBlock)

        val SHAPE: VoxelShape = Shapes.or(
            box16(
                0, 0, 0,
                16, 12, 16
            ),
            box16(
                0, 12, 0,
                2, 14, 2
            ),
            box16(
                14, 12, 0,
                16, 14, 2
            ),
            box16(
                0, 12, 14,
                2, 14, 16
            ),
            box16(
                14, 12, 14,
                16, 14, 16
            ),
            box16(
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

    override fun <T : BlockEntity?> getTicker(
        level: Level,
        blockState: BlockState,
        blockEntityType: BlockEntityType<T?>
    ): BlockEntityTicker<T?>? = if (level !is ServerLevel) null else createTickerHelper(
        blockEntityType,
        PastureCollector.Registries.Entity.PASTURE_COLLECTOR_BLOCK_ENTITY,
        PastureCollectorBlockEntity.TICKER::tick
    )

    override fun randomTick(
        state: BlockState,
        level: ServerLevel,
        pos: BlockPos,
        random: RandomSource,
    ) {
        super.randomTick(state, level, pos, random)
        if (PastureCollector.config.tickType != TickType.RANDOM_TICK) return
        getBlockEntity(pos, level).intervalRep()
    }

    override fun useWithoutItem(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        player: Player,
        blockHitResult: BlockHitResult,
    ): InteractionResult {
        if (!level.isClientSide) {
            val entity = getBlockEntity(blockPos, level as ServerLevel)
            player.openMenu(entity)
        }
        return InteractionResult.sidedSuccess(level.isClientSide)
    }

    override fun getRenderShape(blockState: BlockState): RenderShape = RenderShape.MODEL

    override fun isRandomlyTicking(blockState: BlockState): Boolean = true

    fun getBlockEntity(pos: BlockPos, level: ServerLevel): PastureCollectorBlockEntity {
        val blockEntity =
            level.getBlockEntity(pos) as? PastureCollectorBlockEntity ?: throw PastureCollectorEntityNotFound(
                "Tried to get an entity for a Pasture Block, but it wasn't a Pasture Block Entity."
            )

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
