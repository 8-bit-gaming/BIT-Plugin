package io.pixelinc.commands


import java.util.UUID
import java.util.concurrent.TimeUnit

import io.pixelinc.BITPlugin
import io.pixelinc.tasks.TrailsTask
import org.bukkit.{Bukkit, ChatColor, Location, Material}
import org.bukkit.command.{Command, CommandExecutor, CommandSender}
import org.bukkit.entity.{ArmorStand, Arrow, Entity, EntityType, Player}
import org.bukkit.event.block.Action
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.{EventHandler, Listener}
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.util.Vector

import scala.language.postfixOps
import scala.collection.mutable

object TestCommand extends CommandExecutor with Listener {
    override def onCommand(sender: CommandSender, command: Command, label: String, args: Array[String]): Boolean = {
        true
    }

    private val cooldown: mutable.Map[String, Long] = mutable.Map[String, Long]()
    private val cooldownTime: Int = 1

    @EventHandler
    def onGrappleThrow(event: PlayerInteractEvent): Unit = {
        if ((event.getAction == Action.RIGHT_CLICK_AIR || event.getAction == Action.RIGHT_CLICK_BLOCK) && event.getItem != null && event.getItem.getType == Material.STICK) {
            if (event.getItem.hasItemMeta && (ChatColor.translateAlternateColorCodes('&', event.getItem.getItemMeta.getDisplayName) == "&cGRAPPLE")) {
                val player: Player = event.getPlayer
                if (player.hasPermission("bit.grapple")) {
                    val timeLeft = System.currentTimeMillis - cooldown.getOrElse(player.getUniqueId.toString, 0L)
                    if (TimeUnit.MILLISECONDS.toSeconds(timeLeft) < cooldownTime) {
                        return
                    }


                    val eyeLocation: Location = player.getEyeLocation
                    val direction: Vector = eyeLocation.getDirection

                    val arrow: Arrow = player.launchProjectile(classOf[Arrow])
                    arrow.setVelocity(direction.normalize().multiply(3))
                    arrow.setMetadata("grapple", new FixedMetadataValue(BITPlugin, true))

                    TrailsTask.arrows.addOne(arrow)

                    player.sendMessage(ChatColor.GREEN + "ZOOOOOOOOM.")
                    cooldown.addOne(player.getUniqueId.toString, System.currentTimeMillis)
                    event.setCancelled(true)
                }
            }
        }
    }

    @EventHandler
    def onGrappleHit(event: ProjectileHitEvent): Unit = {
        event.getEntity match {
            case arrow: Arrow if event.getEntity.getShooter.isInstanceOf[Player] =>
                if (!arrow.hasMetadata("grapple")) return
                val player: Player = event.getEntity.getShooter.asInstanceOf[Player]
                val isGrapple: Boolean = arrow.getMetadata("grapple").get(0).asBoolean()

                if (isGrapple) {
                    player.sendMessage(ChatColor.GREEN + "Grappling!!")
                    val arrowLocation: Location = arrow.getLocation
                    val direction = arrowLocation.toVector.subtract(player.getLocation.toVector).normalize()
                    player.setVelocity(direction.multiply(5))

                    Bukkit.getScheduler.runTaskLater(BITPlugin, new Runnable {
                        override def run(): Unit = {
                            TrailsTask.arrows -= arrow
                        }
                    }, 20 * 5L)
                }

            case _ =>
        }
    }
}
