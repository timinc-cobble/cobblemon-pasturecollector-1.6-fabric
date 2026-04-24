package us.timinc.mc.cobblemon.pasturecollector.common.event

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import us.timinc.mc.cobblemon.pasturecollector.common.blocks.entities.PastureCollectorBlockEntity

class PasturePokemonTickedEvent(
    val pokemonEntity: PokemonEntity,
    val collector: PastureCollectorBlockEntity
)
