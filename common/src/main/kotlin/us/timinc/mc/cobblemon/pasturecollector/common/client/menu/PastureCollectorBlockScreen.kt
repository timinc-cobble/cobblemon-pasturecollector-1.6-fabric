package us.timinc.mc.cobblemon.pasturecollector.common.client.menu

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import us.timinc.mc.cobblemon.pasturecollector.common.PastureCollector.modResource
import us.timinc.mc.cobblemon.pasturecollector.common.inventory.PastureCollectorMenu

class PastureCollectorBlockScreen(menu: PastureCollectorMenu, inventory: Inventory, title: Component) :
    AbstractContainerScreen<PastureCollectorMenu>(menu, inventory, title) {
    companion object {
        val TEXTURE = modResource("textures/gui/pasture_collector_inventory.png")
        const val WIDTH = 176
        const val HEIGHT = 130
        const val LABEL_OFFSET_Y = 94
    }

    init {
        this.imageWidth = WIDTH
        this.imageHeight = HEIGHT
        this.inventoryLabelY = HEIGHT - LABEL_OFFSET_Y
    }

    override fun renderBg(guiGraphics: GuiGraphics, delta: Float, mouseX: Int, mouseY: Int) =
        guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight)

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(guiGraphics, mouseX, mouseY, delta)
        renderTooltip(guiGraphics, mouseX, mouseY)
    }
}
