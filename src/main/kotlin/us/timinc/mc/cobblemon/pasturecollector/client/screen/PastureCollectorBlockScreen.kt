package us.timinc.mc.cobblemon.pasturecollector.client.screen

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import us.timinc.mc.cobblemon.pasturecollector.PastureCollectorMod.modIdentifier
import us.timinc.mc.cobblemon.pasturecollector.inventory.PastureCollectorMenu

class PastureCollectorBlockScreen(menu: PastureCollectorMenu, inventory: Inventory, title: Component) :
    AbstractContainerScreen<PastureCollectorMenu>(menu, inventory, title) {
    val texture = modIdentifier("textures/gui/pasture_collector_inventory.png")

    init {
        this.imageWidth = 176
        this.imageHeight = 130
        this.inventoryLabelY = this.imageHeight - 94
    }

    override fun renderBg(guiGraphics: GuiGraphics, delta: Float, mouseX: Int, mouseY: Int) {
        guiGraphics.blit(texture, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight)
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(guiGraphics, mouseX, mouseY, delta)
        renderTooltip(guiGraphics, mouseX, mouseY)
    }
}