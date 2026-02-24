package us.timinc.mc.cobblemon.pasturecollector.fabric

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.minecraft.client.gui.screens.MenuScreens
import net.minecraft.client.renderer.RenderType
import us.timinc.mc.cobblemon.pasturecollector.common.PastureCollector.Registries.Block.PASTURE_COLLECTOR
import us.timinc.mc.cobblemon.pasturecollector.common.PastureCollector.Registries.Menu.PASTURE_COLLECTOR_MENU
import us.timinc.mc.cobblemon.pasturecollector.common.client.menu.PastureCollectorBlockScreen

object PastureCollectorModClient : ClientModInitializer {
    override fun onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlocks(
            RenderType.cutout(),
            PASTURE_COLLECTOR.block
        )
        MenuScreens.register(PASTURE_COLLECTOR_MENU, ::PastureCollectorBlockScreen)
    }
}
