package io.pixelinc

import com.massivecraft.factions.FactionsPlugin
import xyz.janboerman.scalaloader.plugin.{ScalaPlugin, ScalaPluginDescription}
import xyz.janboerman.scalaloader.plugin.description.{Scala, ScalaVersion}

@Scala(version = ScalaVersion.v2_13_1)
object BITPlugin
    extends ScalaPlugin(
        new ScalaPluginDescription("BIT", "1.0-SNAPSHOT")
        .description("The BIT Plugin")) {


    override def onEnable(): Unit = {
        getConfig.addDefault("enabled-worlds", java.util.List[String])
        getConfig.options.copyDefaults(true)
        saveConfig()

        getServer.getPluginManager.registerEvents(ChatListener, this)

        // Disable factions from handling chat tags.
        FactionsPlugin.getInstance().handleFactionTagExternally(true)
    }

}
