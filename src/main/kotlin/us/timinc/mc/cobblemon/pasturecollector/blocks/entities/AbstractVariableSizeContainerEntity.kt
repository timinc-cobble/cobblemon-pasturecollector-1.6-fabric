package us.timinc.mc.cobblemon.pasturecollector.blocks.entities

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.ContainerHelper
import net.minecraft.world.WorldlyContainer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import us.timinc.mc.cobblemon.pasturecollector.container.VariedSlotContainer

abstract class AbstractVariableSizeContainerEntity<T : BlockEntity>(
    type: BlockEntityType<T>,
    open val pos: BlockPos,
    state: BlockState
) : BlockEntity(type, pos, state),
    WorldlyContainer {
    abstract val inventory: VariedSlotContainer

    override fun getUpdatePacket(): Packet<ClientGamePacketListener> = ClientboundBlockEntityDataPacket.create(this)

    override fun canPlaceItemThroughFace(i: Int, itemStack: ItemStack, direction: Direction?): Boolean = false

    override fun canTakeItemThroughFace(i: Int, itemStack: ItemStack, direction: Direction): Boolean = true

    override fun clearContent() = inventory.clearContent()

    override fun getContainerSize(): Int = inventory.size

    override fun isEmpty(): Boolean = getItem(0).isEmpty

    override fun getItem(i: Int): ItemStack = inventory.getItem(i)

    override fun removeItem(i: Int, j: Int): ItemStack = inventory.removeItem(i, j)

    override fun removeItemNoUpdate(i: Int): ItemStack = inventory.removeItemNoUpdate(i)

    override fun setItem(i: Int, itemStack: ItemStack) = inventory.setItem(i, itemStack)

    override fun stillValid(player: Player): Boolean = inventory.stillValid(player)

    override fun getSlotsForFace(direction: Direction): IntArray {
        val result = IntArray(inventory.size)
        for (i in result.indices) {
            result[i] = i
        }
        return result
    }

    override fun loadAdditional(nbt: CompoundTag, provider: HolderLookup.Provider) {
        super.loadAdditional(nbt, provider)
        ContainerHelper.loadAllItems(nbt, inventory.items, provider)
    }

    override fun saveAdditional(nbt: CompoundTag, provider: HolderLookup.Provider) {
        ContainerHelper.saveAllItems(nbt, inventory.items, provider)
        super.saveAdditional(nbt, provider)
    }

    override fun getUpdateTag(provider: HolderLookup.Provider): CompoundTag {
        val nbt = super.getUpdateTag(provider)
        saveAdditional(nbt, provider)
        return nbt
    }
}