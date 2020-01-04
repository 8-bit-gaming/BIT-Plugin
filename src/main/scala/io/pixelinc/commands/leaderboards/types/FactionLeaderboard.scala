package io.pixelinc.commands.leaderboards.types

import com.gmail.filoghost.holographicdisplays.api.Hologram
import com.gmail.filoghost.holographicdisplays.api.handler.TouchHandler
import com.massivecraft.factions.integration.Econ
import com.massivecraft.factions.{Faction, Factions}
import io.pixelinc.commands.leaderboards.ILeaderboard
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.{ChatColor, Material}

class FactionLeaderboard extends ILeaderboard {

    override var commandName: String = "factions"

    override def handle(hologram: Hologram): Boolean = {
        // This is how the official plugin does it so, /shrug
        val factionList = Factions.getInstance().getAllFactions
        factionList.remove(Factions.getInstance().getWilderness)
        factionList.remove(Factions.getInstance().getWarZone)
        factionList.remove(Factions.getInstance().getSafeZone)


        val topFactions: List[Faction] = factionList
          .toArray(Array[Faction]())
          .sortBy(faction => Econ.getBalance(faction.getAccountId))
          .reverse
          .take(5)
          .toList


        hologram.appendItemLine(new ItemStack(Material.DIAMOND_SWORD))
        hologram.appendTextLine("-- Faction Leaderboards -- ")
        var index = 1
        topFactions.foreach(faction => {
            val balance = Econ.getBalance(faction.getAccountId)
            val builder = new StringBuilder()

            if (index == 1) {
                builder.append(ChatColor.GOLD)
                builder.append("♛ ")
            }

            builder.append(s"$index.")
            builder.append(" ")
            builder.append(faction.getTag)
            builder.append(" ")
            builder.append(ChatColor.GREEN)
            builder.append("$")
            builder.append(Econ.moneyString(balance))

            val line = hologram.appendTextLine(builder.toString)
            line.setTouchHandler(new FactionHoloTouchHandler(faction))

            index += 1
        })

        true
    }

    private class FactionHoloTouchHandler(val faction: Faction) extends TouchHandler {
        override def onTouch(player: Player): Unit = {
            player.performCommand("f who " + faction.getTag)
        }
    }
}
