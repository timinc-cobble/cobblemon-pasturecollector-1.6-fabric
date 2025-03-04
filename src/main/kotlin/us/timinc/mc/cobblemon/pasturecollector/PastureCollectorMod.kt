package us.timinc.mc.cobblemon.pasturecollector

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.minecraft.resources.ResourceLocation
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import us.timinc.mc.cobblemon.pasturecollector.api.ConfigBuilder
import us.timinc.mc.cobblemon.pasturecollector.api.PastureCollectorConfig
import us.timinc.mc.cobblemon.pasturecollector.blocks.PastureCollectorBlocks
import us.timinc.mc.cobblemon.pasturecollector.blocks.entities.PastureCollectorBlockEntities

object PastureCollectorMod : ModInitializer {
    const val MOD_ID = "pasturecollector"
    lateinit var config: PastureCollectorConfig
    val logger: Logger = LogManager.getLogger(MOD_ID)

    override fun onInitialize() {
        config = ConfigBuilder.load(PastureCollectorConfig::class.java, "cobblemon/tim/PastureCollector")
        PastureCollectorBlockEntities.register()
        PastureCollectorBlocks.register()

        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register { _, _, _ ->
            config = ConfigBuilder.load(PastureCollectorConfig::class.java, "cobblemon/tim/PastureCollector")
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