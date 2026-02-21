package us.timinc.mc.cobblemon.pasturecollector.common.network

import net.minecraft.core.BlockPos
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import us.timinc.mc.cobblemon.droploottables.DropLootTables.modResource

class BlockPosPayload(val pos: BlockPos) : CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = ID

    companion object {
        val PACKET_ID = modResource("net.block_pos")
        val ID = CustomPacketPayload.Type<BlockPosPayload>(PACKET_ID)
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, BlockPosPayload> = StreamCodec.composite(
            BlockPos.STREAM_CODEC, BlockPosPayload::pos, ::BlockPosPayload
        )
    }
}
