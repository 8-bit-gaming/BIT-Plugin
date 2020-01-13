package io.pixelinc

import java.io.File
import java.util

import com.massivecraft.factions.FactionsPlugin
import io.pixelinc.commands.{LeaderboardCommand, TestCommand}
import io.pixelinc.commands.leaderboards.LeaderboardManager
import io.pixelinc.listeners.ChatListener
import io.pixelinc.tasks.TrailsTask
import org.bukkit.configuration.file.YamlConfiguration
import xyz.janboerman.scalaloader.plugin.ScalaPluginDescription.Command
import xyz.janboerman.scalaloader.plugin.{ScalaPlugin, ScalaPluginDescription}
import xyz.janboerman.scalaloader.plugin.description.{Scala, ScalaVersion}


@Scala(version = ScalaVersion.v2_13_1)
object BITPlugin
    extends ScalaPlugin(
        new ScalaPluginDescription("BIT", "1.0-SNAPSHOT")
        .description("The BIT Plugin")
        .addCommand(new Command("leaderboard"))
        .addCommand(new Command("test"))) {

    // TODO: Make a custom config manager to ease management of them.
    private val leaderboardsFile = new File(getDataFolder, "leaderboards.yml")
    val leaderboardConfig = new YamlConfiguration

    override def onEnable(): Unit = {
        getConfig.addDefault("enabled-worlds", new util.ArrayList[String]())
        getConfig.options.copyDefaults(true)
        saveConfig()

        getServer.getPluginManager.registerEvents(ChatListener, this)

        // Disable factions from handling chat tags.
        FactionsPlugin.getInstance().handleFactionTagExternally(true)

        loadLeaderboardsConfig
        LeaderboardManager.registerAll
        LeaderboardManager.loadActiveLeaderboards

        getServer.getPluginManager.registerEvents(TestCommand, this)

        getCommand("leaderboard").setExecutor(LeaderboardCommand)
        getCommand("test").setExecutor(TestCommand)

        TrailsTask.runTaskTimerAsynchronously(BITPlugin, 0L, 1L)
    }

    def saveLeaderboardConfig: Unit = {
        leaderboardConfig.save(leaderboardsFile)
        getLogger.info("Saving leaderboards config...")
    }

    private def loadLeaderboardsConfig: Unit = {
        if (!leaderboardsFile.exists()) {
            leaderboardsFile.getParentFile.mkdirs()
            leaderboardsFile.createNewFile()
        }

        try {
            leaderboardConfig.load(leaderboardsFile)
            leaderboardConfig.addDefault("leaderboards", new util.ArrayList[String]())
            leaderboardConfig.options.copyDefaults(true)
            leaderboardConfig.save(leaderboardsFile)
        } catch {
            case e: Throwable => e.printStackTrace()
        }

    }

}
