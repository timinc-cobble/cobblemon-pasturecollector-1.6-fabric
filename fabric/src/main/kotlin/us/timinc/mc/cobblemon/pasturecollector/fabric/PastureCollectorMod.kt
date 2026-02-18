package us.timinc.mc.cobblemon.pasturecollector.fabric

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.minecraft.resources.ResourceLocation
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import us.timinc.mc.cobblemon.pasturecollector.fabric.blocks.PastureCollectorBlocks
import us.timinc.mc.cobblemon.pasturecollector.fabric.blocks.entities.PastureCollectorBlockEntities
import us.timinc.mc.cobblemon.pasturecollector.fabric.config.PastureCollectorConfig
import us.timinc.mc.cobblemon.pasturecollector.fabric.inventory.PastureCollectorMenus
import us.timinc.mc.cobblemon.timcore.ConfigBuilder

object PastureCollectorMod : ModInitializer {
    const val MOD_ID = "pasturecollector"
    lateinit var config: PastureCollectorConfig
    val logger: Logger = LogManager.getLogger(MOD_ID)

    override fun onInitialize() {
        config = ConfigBuilder.load(PastureCollectorConfig::class.java, "pasture_collector")
        PastureCollectorBlockEntities.register()
        PastureCollectorBlocks.register()
        PastureCollectorMenus.register()

        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register { _, _, _ ->
            config = ConfigBuilder.load(PastureCollectorConfig::class.java, "pasture_collector")
        }
    }

    fun modIdentifier(name: String): ResourceLocation {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, name)
    }

    fun debug(msg: String, bypassConfig: Boolean = false) {
        if (!config.debug && !bypassConfig) return
        logger.info(msg)
    }
}
