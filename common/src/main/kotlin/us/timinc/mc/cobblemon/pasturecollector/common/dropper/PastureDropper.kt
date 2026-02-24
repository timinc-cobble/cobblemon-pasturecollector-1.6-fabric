package us.timinc.mc.cobblemon.pasturecollector.common.dropper

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.BlockPos
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
    override val dropTarget: ResourceLocation?
) : Dropper<PastureDropper.Context>() {
    companion object {
        val CODEC: MapCodec<PastureDropper> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                CodecPieces.getTrigger(PastureDropper::trigger),
                CodecPieces.getTables(PastureDropper::lootTables),
                CodecPieces.getConditions(PastureDropper::conditions),
                CodecPieces.getDropTarget(PastureDropper::dropTarget),
            ).apply(instance) { trigger, lootTables, conditions, dropTarget ->
                PastureDropper(
                    trigger,
                    lootTables,
                    conditions,
                    dropTarget.getOrNull()
                )
            }
        }

        val DROPPER_TYPE = DropperType(CODEC)
    }

    override fun getType(): DropperType<*, *> = PastureCollector.DropperTypes.PASTURE

    class Context(
        override val level: ServerLevel,
        val collectorPos: BlockPos
    ) : DropContext {
        override fun toLootParams(): LootParams {
            val params = mutableMapOf<LootContextParam<out Any>, Any>(
                LootContextParams.ORIGIN to collectorPos,
            )

            return LootParams(
                level,
                params,
                mapOf(),
                0F
            )
        }
    }
}
