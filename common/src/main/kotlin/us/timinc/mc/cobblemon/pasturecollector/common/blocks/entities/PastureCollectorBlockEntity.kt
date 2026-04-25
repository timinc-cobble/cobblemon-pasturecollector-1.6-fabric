package us.timinc.mc.cobblemon.pasturecollector.common.blocks.entities

import com.cobblemon.mod.common.CobblemonBlocks
import com.cobblemon.mod.common.block.entity.PokemonPastureBlockEntity
import com.cobblemon.mod.common.util.sendParticlesServer
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.Container
import net.minecraft.world.WorldlyContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3
import us.timinc.mc.cobblemon.pasturecollector.common.PastureCollector
import us.timinc.mc.cobblemon.pasturecollector.common.PastureCollector.PastureCollectorConfig.Companion.TargetMon
import us.timinc.mc.cobblemon.pasturecollector.common.PastureCollector.PastureCollectorConfig.Companion.TickType
import us.timinc.mc.cobblemon.pasturecollector.common.PastureCollector.Registries.Entity.PASTURE_COLLECTOR_BLOCK_ENTITY
import us.timinc.mc.cobblemon.pasturecollector.common.event.PasturePokemonTickedEvent
import us.timinc.mc.cobblemon.pasturecollector.common.inventory.PastureCollectorMenu
import kotlin.math.min
import kotlin.random.Random

class PastureCollectorBlockEntity(val pos: BlockPos, state: BlockState) :
    BaseContainerBlockEntity(PASTURE_COLLECTOR_BLOCK_ENTITY, pos, state), WorldlyContainer {
    companion object {
        const val CONTAINER_SIZE: Int = 4

        const val PARTICLE_AMOUNT = 3
        const val PARTICLE_OFFSET_Y = 0.1
        const val PARTICLE_POS_Y = 0.65
        const val PARTICLE_POS_XZ_RANDOMNESS_MIN = -0.15
        const val PARTICLE_POS_XZ_RANDOMNESS_MAX = 0.15

        val TICKER = BlockEntityTicker<PastureCollectorBlockEntity> { level, _, _, entity ->
            if (PastureCollector.config.tickType != TickType.TICK) return@BlockEntityTicker
            if (level !is ServerLevel) return@BlockEntityTicker
            if ((level.gameTime % PastureCollector.config.blockTickInterval).toInt() != 0) return@BlockEntityTicker

            entity.intervalRep()
        }
    }

    private var items = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY)
    private var lastIntervalRep: Long = 0

    override fun getDefaultName(): Component = Component.translatable("pasturecollector.container.pasture_collector")
    override fun getContainerSize(): Int = CONTAINER_SIZE
    override fun getSlotsForFace(direction: Direction): IntArray = IntArray(CONTAINER_SIZE) { it }

    override fun canPlaceItemThroughFace(i: Int, itemStack: ItemStack, direction: Direction?): Boolean = false
    override fun canTakeItemThroughFace(i: Int, itemStack: ItemStack, direction: Direction): Boolean = true
    override fun getItem(i: Int): ItemStack = items[i]
    override fun setItem(i: Int, itemStack: ItemStack) {
        items[i] = itemStack
    }

    override fun canTakeItem(container: Container, i: Int, itemStack: ItemStack): Boolean = i in 0..<CONTAINER_SIZE

    override fun getItems(): NonNullList<ItemStack> = items

    override fun setItems(items: NonNullList<ItemStack>) {
        this.items = items
    }

    override fun createMenu(containerId: Int, inventory: Inventory): AbstractContainerMenu =
        PastureCollectorMenu(containerId, inventory, this)

    fun intervalRep() {
        val serverLevel = level as? ServerLevel ?: return
        if (serverLevel.gameTime - lastIntervalRep < PastureCollector.config.blockTickInterval) return
        val check = Random.nextFloat()
        if (check >= PastureCollector.config.chanceToDrop) return
        if (PastureCollector.config.targetMon == TargetMon.RANDOM) dropFromRandom() else dropFromAll()
        lastIntervalRep = serverLevel.gameTime
    }

    fun dropFromAll() {
        PastureCollector.Events.PASTURE_COLLECTOR_TICKED.post(*getNearbyPastures(level as ServerLevel).flatMap { pasture ->
            pasture.tetheredPokemon.mapNotNull { tethered ->
                tethered.getPokemon()?.entity?.let {
                    PasturePokemonTickedEvent(
                        it,
                        this
                    )
                }
            }
        }.toTypedArray())
    }

    /**
     * Randomly chooses one species from nearby pasture blocks.
     * Triggers [PasturePokemonTickedEvent] which decides if there are any drops it can get from species
     */
    fun dropFromRandom() {
        val chosenMon = getNearbyPastures(level as ServerLevel).flatMap { pasture ->
            pasture
                .tetheredPokemon
                .mapNotNull { it.getPokemon() }
                .filter { it.entity != null }
        }.randomOrNull() ?: return

        PastureCollector.Events.PASTURE_COLLECTOR_TICKED.emit(
            PasturePokemonTickedEvent(
                chosenMon.entity!!, this
            )
        )
    }

    /**
     * Get nearby pasture blocks.
     * Looks at 8 sides, up and down directions ignored
     *
     * @param level
     * @return List of nearby pasture block entities
     */
    fun getNearbyPastures(level: ServerLevel): List<PokemonPastureBlockEntity> {
        val positionsToCheck = mutableListOf<BlockPos>(
            pos.north(), pos.east(), pos.south(), pos.west()
        )

        if (PastureCollector.config.checkDiagonal) {
            positionsToCheck.addAll(
                listOf(
                    pos.north().west(), pos.north().east(),
                    pos.south().west(), pos.south().east()
                )
            )
        }

        val listOfNearbyPastures = mutableListOf<PokemonPastureBlockEntity>()
        for (positionToCheck in positionsToCheck) {
            val targetBlockState = level.getBlockState(positionToCheck)
            val targetBlockEntity = level.getBlockEntity(positionToCheck)
            if (targetBlockState.block == CobblemonBlocks.PASTURE && targetBlockEntity is PokemonPastureBlockEntity) {
                listOfNearbyPastures.add(targetBlockEntity)
            }
        }
        return listOfNearbyPastures
    }

    fun handleDropPlacement(drop: ItemStack): ItemStack {
        // TODO: This should be handled in DLT. If the drop is empty, it shouldn't try and drop it.
        if (drop.isEmpty) return ItemStack.EMPTY

        var particle: SimpleParticleType

        var remains: ItemStack
        if (canAddItem(drop)) {
            remains = addItem(drop)

            if (remains.isEmpty) {
                particle = ParticleTypes.COMPOSTER
            } else {
                particle = ParticleTypes.ASH
            }
        } else {
            remains = drop
            particle = ParticleTypes.SMALL_FLAME
        }

        particle.let {
            val posX = Random.nextDouble(PARTICLE_POS_XZ_RANDOMNESS_MIN, PARTICLE_POS_XZ_RANDOMNESS_MAX)
            val posZ = Random.nextDouble(PARTICLE_POS_XZ_RANDOMNESS_MIN, PARTICLE_POS_XZ_RANDOMNESS_MAX)
            level?.sendParticlesServer(
                it,
                pos.center.add(
                    Vec3(
                        posX,
                        PARTICLE_POS_Y,
                        posZ
                    )
                ),
                PARTICLE_AMOUNT,
                Vec3(0.0, PARTICLE_OFFSET_Y, 0.0),
                0.0
            )
        }

        return remains
    }

    fun canAddItem(itemStack: ItemStack): Boolean = this.items.any {
        it.isEmpty || ItemStack.isSameItemSameComponents(it, itemStack) && it.count < it.maxStackSize
    }

    fun addItem(itemStack: ItemStack): ItemStack {
        if (!canAddItem(itemStack)) return itemStack
        if (itemStack.isEmpty) return ItemStack.EMPTY

        val itemStack2 = itemStack.copy()
        this.moveItemToOccupiedSlotsWithSameType(itemStack2)

        return when (itemStack2.isEmpty) {
            true -> ItemStack.EMPTY
            false -> {
                this.moveItemToEmptySlots(itemStack2)
                if (itemStack2.isEmpty) ItemStack.EMPTY else itemStack2
            }
        }
    }

    private fun moveItemToEmptySlots(itemStack: ItemStack) {
        repeat(CONTAINER_SIZE) {
            val itemStack2 = this.getItem(it)
            if (itemStack2.isEmpty) {
                this.setItem(it, itemStack.copyAndClear())
                return
            }
        }
    }

    private fun moveItemToOccupiedSlotsWithSameType(itemStack: ItemStack) {
        repeat(CONTAINER_SIZE) {
            val itemStack2 = this.getItem(it)
            if (ItemStack.isSameItemSameComponents(itemStack2, itemStack)) {
                this.moveItemsBetweenStacks(itemStack, itemStack2)
                if (itemStack.isEmpty) {
                    return
                }
            }
        }
    }

    private fun moveItemsBetweenStacks(itemStack: ItemStack, itemStack2: ItemStack) {
        val i = this.getMaxStackSize(itemStack2)
        val j = min(itemStack.count, i - itemStack2.count)
        if (j > 0) {
            itemStack2.grow(j)
            itemStack.shrink(j)
            this.setChanged()
        }
    }

    override fun saveAdditional(compoundTag: CompoundTag, provider: HolderLookup.Provider) {
        super.saveAdditional(compoundTag, provider)
        compoundTag.putLong("last_interval_rep", lastIntervalRep)
    }

    override fun loadAdditional(compoundTag: CompoundTag, provider: HolderLookup.Provider) {
        super.loadAdditional(compoundTag, provider)
        this.lastIntervalRep = compoundTag.getLong("last_interval_rep")
    }
}
