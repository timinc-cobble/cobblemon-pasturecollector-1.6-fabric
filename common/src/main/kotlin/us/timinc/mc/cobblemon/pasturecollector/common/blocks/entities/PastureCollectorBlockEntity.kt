package us.timinc.mc.cobblemon.pasturecollector.common.blocks.entities

import com.cobblemon.mod.common.CobblemonBlocks
import com.cobblemon.mod.common.block.entity.PokemonPastureBlockEntity
import com.cobblemon.mod.common.util.sendParticlesServer
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.NonNullList
import net.minecraft.core.NonNullList.of
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.SimpleContainer
import net.minecraft.world.WorldlyContainer
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3
import us.timinc.mc.cobblemon.pasturecollector.common.PastureCollector
import us.timinc.mc.cobblemon.pasturecollector.common.PastureCollector.Registries.Entity.PASTURE_COLLECTOR_BLOCK_ENTITY
import us.timinc.mc.cobblemon.pasturecollector.common.event.PastureCollectorTickedEvent
import us.timinc.mc.cobblemon.pasturecollector.common.handlers.PastureTickHandler
import us.timinc.mc.cobblemon.pasturecollector.common.handlers.PastureTickHandler.PARTICLE_AMOUNT
import us.timinc.mc.cobblemon.pasturecollector.common.handlers.PastureTickHandler.PARTICLE_OFFSET_Y
import us.timinc.mc.cobblemon.pasturecollector.common.handlers.PastureTickHandler.PARTICLE_POS_XZ_RANDOMNESS_MAX
import us.timinc.mc.cobblemon.pasturecollector.common.handlers.PastureTickHandler.PARTICLE_POS_XZ_RANDOMNESS_MIN
import us.timinc.mc.cobblemon.pasturecollector.common.handlers.PastureTickHandler.PARTICLE_POS_Y
import us.timinc.mc.cobblemon.pasturecollector.common.inventory.PastureCollectorMenu
import kotlin.collections.count
import kotlin.random.Random

@Suppress("TooManyFunctions")
class PastureCollectorBlockEntity(val pos: BlockPos, state: BlockState) :
    BaseContainerBlockEntity(PASTURE_COLLECTOR_BLOCK_ENTITY, pos, state), WorldlyContainer {
    companion object {
        const val CONTAINER_SIZE: Int = 4

        val TITLE: Component = Component.translatable(
            ResourceLocation.fromNamespaceAndPath("block", "pasturecollector.pasture_collector").toLanguageKey()
        )
    }

    private val container = SimpleContainer(CONTAINER_SIZE)

    override fun getDefaultName(): Component = TITLE
    override fun getContainerSize(): Int = CONTAINER_SIZE
    override fun getSlotsForFace(direction: Direction): IntArray = IntArray(CONTAINER_SIZE)
    override fun canPlaceItemThroughFace(i: Int, itemStack: ItemStack, direction: Direction?): Boolean = false
    override fun canTakeItemThroughFace(i: Int, itemStack: ItemStack, direction: Direction) = true
    override fun createMenu(containerId: Int, inventory: Inventory): AbstractContainerMenu =
        PastureCollectorMenu(containerId, inventory, this)

    /**
     * Randomly chooses one species from nearby pasture blocks.
     * Triggers [PastureCollectorTickedEvent] which decides if there are any drops it can get from species
     */
    fun attemptToGetDrop() {
        if (level !is ServerLevel) return
        val chosenMon = getNearbyPastures(level as ServerLevel).flatMap { pasture ->
            pasture
                .tetheredPokemon
                .mapNotNull { it.getPokemon() }
                .filter { it.entity != null }
        }.randomOrNull() ?: return
        PastureCollector.debugger.debug("random tick::chance to drop checked::inside attemptToGetDrop", true)
        PastureCollector.Events.PASTURE_COLLECTOR_TICKED.emit(PastureCollectorTickedEvent(chosenMon, this))
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


    override fun getItems(): NonNullList<ItemStack> = container.items

    override fun setItems(items: NonNullList<ItemStack>) {
        container.removeAllItems()
        container.items.addAll(items.take(CONTAINER_SIZE))
    }

    /**
     * Drop item to level
     *
     * @param item the stack we want to drop
     */
    fun dropItemToLevel(item: ItemStack): PastureTickHandler.DropResult {
        if (level !is ServerLevel) return PastureTickHandler.DropResult.NONE
        (level as ServerLevel).addFreshEntity(
            ItemEntity(
                level!!,
                pos.x.toDouble(),
                pos.y.plus(1).toDouble(),
                pos.z.toDouble(),
                item
            )
        )
        return PastureTickHandler.DropResult.CONTAINER_FULL
    }

    fun handleDropPlacement(drop: ItemStack) {
        var particle = ParticleTypes.CAMPFIRE_COSY_SMOKE

        if (container.canAddItem(drop)) {
            val remains = container.addItem(drop)

            if (remains.isEmpty) {
                particle = ParticleTypes.COMPOSTER
            } else {
                dropItemToLevel(remains)
                particle = ParticleTypes.ASH
            }

        } else {
            dropItemToLevel(drop)
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
    }
}
