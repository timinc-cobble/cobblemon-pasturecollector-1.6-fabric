package us.timinc.mc.cobblemon.pasturecollector.common.event

import com.cobblemon.mod.common.pokemon.Pokemon
import us.timinc.mc.cobblemon.pasturecollector.common.blocks.entities.PastureCollectorBlockEntity

class PastureCollectorTickedEvent(
    val pokemon: Pokemon,
    val collector: PastureCollectorBlockEntity
)
