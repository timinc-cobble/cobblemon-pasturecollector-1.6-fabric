package us.timinc.mc.cobblemon.pasturecollector

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.minecraft.client.renderer.RenderType
import us.timinc.mc.cobblemon.pasturecollector.blocks.PastureCollectorBlocks

object PastureCollectorModClient : ClientModInitializer {
    override fun onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlocks(
            RenderType.cutout(),
            PastureCollectorBlocks.PASTURE_COLLECTOR
        )
    }
}