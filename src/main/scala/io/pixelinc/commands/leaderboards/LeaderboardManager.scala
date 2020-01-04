package io.pixelinc.commands.leaderboards

import java.util.UUID

import com.gmail.filoghost.holographicdisplays.api.HologramsAPI
import io.pixelinc.BITPlugin
import io.pixelinc.commands.leaderboards.types.FactionLeaderboard
import io.pixelinc.utils.HologramUtils
import org.bukkit.Location

import scala.collection.mutable.ListBuffer

object LeaderboardManager {

    private val leaderboardTypes: ListBuffer[ILeaderboard] = ListBuffer[ILeaderboard]()
    private val activeLeaderboards : ListBuffer[ActiveLeaderboard] = ListBuffer[ActiveLeaderboard]()

    def getLeaderboardTypeByName(name: String) : Option[ILeaderboard] = {
        leaderboardTypes.find(_.commandName == name)
    }

    def getTypes : Seq[String] = {
        leaderboardTypes.map(_.commandName).toSeq
    }

    def addActiveLeaderboard(leaderboard: ActiveLeaderboard): Unit = {
        activeLeaderboards.addOne(leaderboard)
        saveLeaderboard(leaderboard)
    }

    def getActiveLeaderboard(leaderboard: ILeaderboard): Option[ActiveLeaderboard] = {
       activeLeaderboards.find(_.leaderboard.commandName == leaderboard.commandName)
    }

    def getActiveLeaderboards(): Seq[ActiveLeaderboard] = {
        activeLeaderboards.toSeq
    }

    def isLeaderboardActive(leaderboard: ILeaderboard): Boolean = {
        activeLeaderboards.exists(_.leaderboard.commandName == leaderboard.commandName)
    }

    def deleteActiveLeaderboard(leaderboard: ILeaderboard): Unit = {
        val activeLeaderboard = getActiveLeaderboard(leaderboard)
        activeLeaderboard match {
            case Some(activeLeaderboard) =>
                HologramUtils.getHologram(activeLeaderboard.location) match {
                    case Some(hologram) => hologram.delete()

                    // Log to player?
                    case _ => BITPlugin.getLogger.warning(s"Could not get hologram of leaderboard ${leaderboard.commandName}")
                }

                // Delete leaderboard from the list and config
                activeLeaderboards -= activeLeaderboard
                deleteLeaderboardFromConfig(activeLeaderboard)
            case None => BITPlugin.getLogger.warning(s"Could not get active leaderboard of type ${leaderboard.commandName}")
        }
    }

    def moveLeaderboard(leaderboard: ILeaderboard, location: Location): Boolean = {
        val activeLeaderboard = getActiveLeaderboard(leaderboard)
        activeLeaderboard match {
            case Some(activeLeaderboard) =>
                HologramUtils.getHologram(activeLeaderboard.location) match {
                    case Some(hologram) =>
                        // Just passing in a location seems to not teleport to the right place????
                        // Makes sense to me!!
                        hologram.teleport(location.getWorld, location.getX, location.getY, location.getZ)
                        activeLeaderboard.location = location

                        saveLeaderboard(activeLeaderboard)
                        true
                    case None =>
                        BITPlugin.getLogger.warning(s"Failed to get hologram of leaderboard ${leaderboard.commandName}")
                        false

                }
            case None =>
                BITPlugin.getLogger.warning(s"Failed to get leaderboard of type ${leaderboard.commandName}")
                false
        }

    }

    def deleteLeaderboardFromConfig(leaderboard: ActiveLeaderboard): Unit = {
        BITPlugin.leaderboardConfig.set(s"leaderboards.${leaderboard.leaderboard.commandName}", null)
        BITPlugin.saveLeaderboardConfig
    }

    def saveLeaderboard(leaderboard: ActiveLeaderboard): Unit = {
        val config = BITPlugin.leaderboardConfig
        config.set(s"leaderboards.${leaderboard.leaderboard.commandName}.owner", leaderboard.creator.toString)
        config.set(s"leaderboards.${leaderboard.leaderboard.commandName}.location", LocationSerializer.serializeLocation(leaderboard.location))
        BITPlugin.saveLeaderboardConfig
    }

    def loadActiveLeaderboards(): Unit = {
        if (BITPlugin.leaderboardConfig.getConfigurationSection("leaderboards") == null)
            return

        BITPlugin.leaderboardConfig.getConfigurationSection("leaderboards").getKeys(false).forEach(key => {
            val leaderboardOwner = BITPlugin.leaderboardConfig.getString(s"leaderboards.$key.owner")
            val serializedLocation = BITPlugin.leaderboardConfig.getString(s"leaderboards.$key.location")

            LocationSerializer.deserializeLocation(serializedLocation) match {
                case Some(location) =>
                    val leaderboardType = getLeaderboardTypeByName(key)

                    leaderboardType match {
                        case Some(leaderboard) =>
                            val hologram = HologramsAPI.createHologram(BITPlugin, location)
                            if (leaderboard.handle(hologram)) {
                                addActiveLeaderboard(ActiveLeaderboard(location, UUID.fromString(leaderboardOwner), leaderboard))
                                BITPlugin.getLogger.info(s"Loaded $key leaderboard at $serializedLocation")
                            } else
                                BITPlugin.getLogger.warning(s"Could not handle leaderboard type $key at $serializedLocation")

                        case _ => BITPlugin.getLogger.warning(s"Failed to load leaderboard with type $key")
                    }
                case _ => BITPlugin.getLogger.warning(s"Could not load location from leaderboards.$key")
            }
        })
    }

    def registerAll(): Unit = {
       leaderboardTypes.addOne(new FactionLeaderboard)
    }

}
