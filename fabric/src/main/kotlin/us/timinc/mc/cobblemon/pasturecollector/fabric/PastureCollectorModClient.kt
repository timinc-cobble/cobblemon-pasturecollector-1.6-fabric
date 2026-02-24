package us.timinc.mc.cobblemon.pasturecollector.fabric

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.minecraft.client.gui.screens.MenuScreens
import net.minecraft.client.renderer.RenderType
import us.timinc.mc.cobblemon.pasturecollector.common.PastureCollector
import us.timinc.mc.cobblemon.pasturecollector.common.client.menu.PastureCollectorBlockScreen
import us.timinc.mc.cobblemon.pasturecollector.common.inventory.PastureCollectorMenus

object PastureCollectorModClient : ClientModInitializer {
    override fun onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlocks(
            RenderType.cutout(),
            PastureCollector.BlockRegistry.PASTURE_COLLECTOR.block
        )
        MenuScreens.register(PastureCollectorMenus.PASTURE_COLLECTOR, ::PastureCollectorBlockScreen)
    }
}
