package io.pixelinc.utils

import com.gmail.filoghost.holographicdisplays.api.{Hologram, HologramsAPI}
import io.pixelinc.BITPlugin
import org.bukkit.Location

object HologramUtils {

    def getHologram(location: Location): Option[Hologram] = {
        val holograms = HologramsAPI.getHolograms(BITPlugin)
          .toArray(Array[Hologram]())

          holograms
          .find(hologram => {
              hologram.getX == location.getX &&
              hologram.getY == location.getY &&
              hologram.getZ == location.getZ
          })
    }

}
