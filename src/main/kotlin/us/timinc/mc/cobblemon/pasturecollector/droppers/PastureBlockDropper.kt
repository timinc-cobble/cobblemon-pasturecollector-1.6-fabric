package us.timinc.mc.cobblemon.pasturecollector.droppers

import com.cobblemon.mod.common.api.drop.ItemDropEntry
import com.cobblemon.mod.common.util.itemRegistry
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.storage.loot.LootParams
import us.timinc.mc.cobblemon.droploottables.api.droppers.AbstractFormDropper
import us.timinc.mc.cobblemon.droploottables.api.droppers.FormDropContext
import us.timinc.mc.cobblemon.pasturecollector.PastureCollectorMod.MOD_ID

object PastureBlockDropper : AbstractFormDropper("collector", MOD_ID) {
    override fun load() {}

    fun getDrops(params: LootParams, context: FormDropContext, lootTableExists: Boolean): List<ItemStack> {
        if (lootTableExists) return super.getDrops(params, context)

        val loot: MutableList<ItemStack> = ArrayList()
        val registry = params.level.itemRegistry

        // Calculated drops for current moment
        // Generates anew each call
        context.formData.drops.getDrops().forEach {
            // It can be anything, but we need items, so we check it here
            if (it is ItemDropEntry) {
                val itemStack = registry.get(it.item)?.defaultInstance ?: ItemStack.EMPTY
                loot.add(itemStack)
            }
        }

        return loot
    }
}