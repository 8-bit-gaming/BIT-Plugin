package io.pixelinc.tasks

import org.bukkit.Particle.DustOptions
import org.bukkit.{Color, Location, Particle}
import org.bukkit.entity.Arrow
import org.bukkit.scheduler.BukkitRunnable

import scala.collection.mutable.ListBuffer

object TrailsTask extends BukkitRunnable {

    val arrows: ListBuffer[Arrow] = ListBuffer[Arrow]()

    override def run(): Unit = {
        arrows.filter(!_.isDead).foreach(arrow => {
            val location: Location = arrow.getLocation
            val dustOptions: DustOptions = new DustOptions(Color.fromRGB(0, 127, 255), 1)
            location.getWorld.spawnParticle(Particle.REDSTONE, location, 1, dustOptions)
        })
    }
}
