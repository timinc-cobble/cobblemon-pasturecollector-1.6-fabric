package us.timinc.mc.cobblemon.pasturecollector.droppers

import com.cobblemon.mod.common.api.drop.ItemDropEntry
import com.cobblemon.mod.common.util.itemRegistry
import net.minecraft.client.Minecraft
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.storage.loot.LootParams
import us.timinc.mc.cobblemon.droploottables.api.droppers.AbstractFormDropper
import us.timinc.mc.cobblemon.droploottables.api.droppers.FormDropContext
import us.timinc.mc.cobblemon.droploottables.dropentries.DynamicItemDropEntry
import us.timinc.mc.cobblemon.pasturecollector.PastureCollectorMod.MOD_ID

object PastureBlockDropper : AbstractFormDropper("collector", MOD_ID) {
    override fun load() {}

    fun getDrops(params: LootParams, context: FormDropContext, lootTableExists: Boolean): List<ItemStack> {
        if (lootTableExists) return super.getDrops(params, context)

        val loot: MutableList<ItemStack> = ArrayList()
        val registry = params.level.itemRegistry

        // Calculated drops for current moment
        // see https://gitlab.com/cable-mc/cobblemon/-/blob/main/common/src/main/kotlin/com/cobblemon/mod/common/api/drop/DropTable.kt?ref_type=heads#L50
        context.formData.drops.getDrops().forEach {
            // See https://gitlab.com/cable-mc/cobblemon/-/blob/main/common/src/main/kotlin/com/cobblemon/mod/common/api/pokedex/filter/SearchFilter.kt#L55
            if (it is ItemDropEntry) {
                val itemStack = registry.get(it.item)?.defaultInstance ?: ItemStack.EMPTY
                loot.add(itemStack)
            }
        }

        return loot
    }
}