package us.timinc.mc.cobblemon.pasturecollector.common.dropper

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.util.toBlockPos
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.parameters.LootContextParam
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition
import us.timinc.mc.cobblemon.droploottables.api.DropContext
import us.timinc.mc.cobblemon.droploottables.api.Dropper
import us.timinc.mc.cobblemon.droploottables.api.Dropper.Companion.CodecPieces
import us.timinc.mc.cobblemon.droploottables.api.DropperType
import us.timinc.mc.cobblemon.pasturecollector.common.PastureCollector
import kotlin.jvm.optionals.getOrNull

class PastureDropper(
    override val trigger: ResourceLocation,
    override val lootTables: List<ResourceLocation>,
    override val conditions: List<LootItemCondition>,
    override val dropTarget: ResourceLocation?,
    val interval: Int,
) : Dropper<PastureDropper.Context>() {
    override fun getType(): DropperType<*, *> = PastureCollector.DropperTypes.PASTURE

    companion object {
        val CODEC: MapCodec<PastureDropper> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                CodecPieces.getTrigger(PastureDropper::trigger),
                CodecPieces.getTables(PastureDropper::lootTables),
                CodecPieces.getConditions(PastureDropper::conditions),
                CodecPieces.getDropTarget(PastureDropper::dropTarget),
                Codec.INT.fieldOf("ticks").forGetter(PastureDropper::interval),
            ).apply(instance) { trigger, lootTables, conditions, dropTarget, ticks ->
                PastureDropper(
                    trigger,
                    lootTables,
                    conditions,
                    dropTarget.getOrNull(),
                    ticks
                )
            }
        }

        val DROPPER_TYPE = DropperType(CODEC)
    }

    class Context(
        override val level: ServerLevel,
        val pokemonEntity: PokemonEntity,
    ) : DropContext {
        override fun toLootParams(): LootParams {
            val params = mutableMapOf<LootContextParam<out Any>, Any>(
                LootContextParams.ORIGIN to pokemonEntity.position().toBlockPos(),
                LootContextParams.THIS_ENTITY to pokemonEntity,
            )

            return LootParams(
                level,
                params,
                mapOf(),
                0F
            )
        }
    }

    override fun canDrop(context: Context): Boolean =
        !context.pokemonEntity.isBusy
                && context.pokemonEntity.ticksLived % interval == 0
                && super.canDrop(context)
}
