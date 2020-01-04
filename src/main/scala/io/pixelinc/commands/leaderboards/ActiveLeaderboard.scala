package io.pixelinc.commands.leaderboards

import java.util.UUID

import org.bukkit.{Bukkit, Location}

case class ActiveLeaderboard(var location: Location, creator: UUID, leaderboard: ILeaderboard)

object LocationSerializer {

    def serializeLocation(location: Location): String = {
        location.getWorld.getName + "#" +
           location.getX.toString + "#" +
           location.getY.toString + "#" +
           location.getZ.toString
    }

    def deserializeLocation(locationString: String): Option[Location] = {
        locationString.split("#") match {
            case Array(worldName, x, y, z) => Some(new Location(Bukkit.getServer.getWorld(worldName), x.toDouble, y.toDouble, z.toDouble))
            case _ => None
        }
    }

}
