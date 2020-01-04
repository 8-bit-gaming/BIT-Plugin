package io.pixelinc.commands.leaderboards

import com.gmail.filoghost.holographicdisplays.api.Hologram

trait ILeaderboard {
    var commandName: String
    def handle(hologram: Hologram): Boolean
}
