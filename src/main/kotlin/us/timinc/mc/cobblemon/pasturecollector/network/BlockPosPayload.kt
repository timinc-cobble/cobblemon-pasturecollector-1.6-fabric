package us.timinc.mc.cobblemon.pasturecollector.network

import io.netty.buffer.ByteBuf
import net.minecraft.core.BlockPos
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import us.timinc.mc.cobblemon.pasturecollector.PastureCollectorMod.modIdentifier

class BlockPosPayload(val pos: BlockPos) : CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = ID

    companion object {
        val PACKET_ID = modIdentifier("net.block_pos")
        val ID = CustomPacketPayload.Type<BlockPosPayload>(PACKET_ID)
        val STREAM_CODEC: StreamCodec<ByteBuf, BlockPosPayload> = StreamCodec.composite(
            BlockPos.STREAM_CODEC, BlockPosPayload::pos, ::BlockPosPayload
        )
    }
}
