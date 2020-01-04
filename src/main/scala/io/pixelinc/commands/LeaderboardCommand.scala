package io.pixelinc.commands

import com.gmail.filoghost.holographicdisplays.api.HologramsAPI
import io.pixelinc.BITPlugin
import io.pixelinc.commands.leaderboards.{ActiveLeaderboard, LeaderboardManager}
import org.bukkit.ChatColor
import org.bukkit.command.{Command, CommandExecutor, CommandSender}
import org.bukkit.entity.Player

object LeaderboardCommand extends CommandExecutor {

    override def onCommand(sender: CommandSender, command: Command, label: String, args: Array[String]): Boolean = {
        if (!sender.isInstanceOf[Player])
            return false

        val player: Player = sender.asInstanceOf[Player]

        if (args.length >= 2) {
            val action = args(0)
            val leaderboardType: String = args(1)
            val leaderboard = LeaderboardManager.getLeaderboardTypeByName(leaderboardType) match {
                case Some(leaderboard) => leaderboard
                case _ =>
                    player.sendMessage(s"${ChatColor.RED}That leaderboard type is invalid, try ${LeaderboardManager.getTypes.mkString(", ")}")
                    return false
            }

            action match {
                case "create" =>
                    val location = player.getLocation

                    if (LeaderboardManager.isLeaderboardActive(leaderboard)) {
                        player.sendMessage(s"${ChatColor.RED}There is already an active leaderboard of this type!")
                        return false
                    }

                    val hologram = HologramsAPI.createHologram(BITPlugin, location)

                    if(leaderboard.handle(hologram)) {
                        player.sendMessage(s"${ChatColor.GREEN}Successfully created a $leaderboardType leaderboard at your current feet position!")
                        LeaderboardManager.addActiveLeaderboard(ActiveLeaderboard(location, player.getUniqueId, leaderboard))
                    }
                case "delete" =>
                    if (!LeaderboardManager.isLeaderboardActive(leaderboard)) {
                        player.sendMessage(s"${ChatColor.RED}There is not an active leaderboard of that type!")
                        return false
                    }

                    LeaderboardManager.deleteActiveLeaderboard(leaderboard)
                    player.sendMessage(s"${ChatColor.GREEN}Deleted leaderboard of type $leaderboardType")
                case "move" =>
                    val location = player.getLocation

                    if (!LeaderboardManager.isLeaderboardActive(leaderboard)) {
                        player.sendMessage(s"${ChatColor.RED}There is not an active leaderboard of that type!")
                        return false
                    }

                    if (LeaderboardManager.moveLeaderboard(leaderboard, location))
                        player.sendMessage(s"${ChatColor.GREEN}Moved leaderboard to your position")
                    else {
                        player.sendMessage(s"${ChatColor.RED}Failed to move leaderboard to your location!")
                        return false
                    }

                case _ => player.sendMessage(s"${ChatColor.RED}Invalid syntax, Try /leaderboard <create/delete/move/list> [name]")
            }
        } else {
            val builder = new StringBuilder()
            builder.append(ChatColor.GRAY)
            builder.append("Active leaderboards:\n")

            LeaderboardManager.getActiveLeaderboards().foreach(leaderboard => {
                builder.append(" ")
                builder.append(ChatColor.AQUA)
                builder.append(leaderboard.leaderboard.commandName)
                builder.append(": \n")

                builder.append(ChatColor.GRAY)
                builder.append("  Location: ")
                builder.append(ChatColor.RED)
                builder.append(s"${leaderboard.location.getX}:${leaderboard.location.getY}:${leaderboard.location.getZ}")
                builder.append("\n")

                builder.append(ChatColor.GRAY)
                builder.append("  Owner: ")
                builder.append(ChatColor.GREEN)
                builder.append(leaderboard.creator.toString)
                builder.append("\n")
            })

            player.sendMessage(builder.toString)
        }

        true
    }

}
