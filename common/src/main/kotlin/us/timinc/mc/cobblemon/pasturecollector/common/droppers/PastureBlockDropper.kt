package us.timinc.mc.cobblemon.pasturecollector.common.droppers

import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import us.timinc.mc.cobblemon.droploottables.DropLootTables
import us.timinc.mc.cobblemon.droploottables.api.DropContext
import us.timinc.mc.cobblemon.droploottables.api.Dropper
import kotlin.collections.mapOf

//
//import us.timinc.mc.cobblemon.droploottables.api.droppers.AbstractFormDropper
//import us.timinc.mc.cobblemon.pasturecollector.fabric.PastureCollectorFabricMod.MOD_ID
//
//object PastureBlockDropper : AbstractFormDropper("collector", MOD_ID) {
//    override fun load() {}
//}

//class PastureBlockDropper(
//
//): Dropper {
//    class Context(
//        override val level: ServerLevel,
//        val focusPokemon: Pokemon,
//        val
//    ) : DropContext {
//        override fun toLootParams(): LootParams = LootParams(
//            level,
//            mapOf(
//                LootContextParams.ORIGIN to focusPlayer.position(),
//                DropLootTables.LootParams.FOCUS_POKEMON to focusPokemon,
//                DropLootTables.LootParams.FOCUS_PLAYER to focusPlayer,
//            ),
//            mapOf(),
//            0f // no luck in pasture block
//        )
//    }
//}
