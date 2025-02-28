package us.timinc.mc.cobblemon.pasturecollector.droppers

import com.cobblemon.mod.common.pokemon.FormData
import us.timinc.mc.cobblemon.pasturecollector.PastureCollector.modIdentifier
import us.timinc.mc.cobblemon.droploottables.api.droppers.AbstractFormDropper

object PastureBlockDropper : AbstractFormDropper("collector") {
    override fun load() {}

    // TODO: Push these back down to Drop Loot Tables, and add a modId param.
    override fun getFormDropId(form: FormData) =
        modIdentifier("$dropType/${form.species.resourceIdentifier.path}${if (form.name != "Normal") "/${form.name.lowercase()}" else ""}")

    override fun getAllDropId() =
        modIdentifier("$dropType/all")
}