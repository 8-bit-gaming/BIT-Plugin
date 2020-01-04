package io.pixelinc.listeners

import com.massivecraft.factions.{FPlayer, FPlayers}
import io.pixelinc.BITPlugin
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.{EventHandler, Listener}

object ChatListener extends Listener {

    private val replaceString = "[FACTION]"
    private val whitelistedWorlds = BITPlugin.getConfig.getStringList("enabled-worlds")

    @EventHandler
    def onAsyncChat(event: AsyncPlayerChatEvent): Unit = {
        val worldName = event.getPlayer.getWorld.getName
        if (whitelistedWorlds.contains(worldName)) {
            val factionPlayer: FPlayer = FPlayers.getInstance().getByPlayer(event.getPlayer)
            var eventFormat: String = event.getFormat

            if (eventFormat.contains(replaceString))
                eventFormat = eventFormat.replace(replaceString, "")

            // This is done to show specific relation colours, ie, ally, enemy
            event.getRecipients.forEach(player => {
                val fPlayer = FPlayers.getInstance().getByPlayer(player)
                val format = s"${factionPlayer.getChatTag(fPlayer)} $eventFormat"

                player.sendMessage(String.format(format, event.getPlayer.getDisplayName, event.getMessage))
            })

            event.getRecipients.clear
            event.setFormat(s"${factionPlayer.getChatTag} $eventFormat")
        }
    }


}
