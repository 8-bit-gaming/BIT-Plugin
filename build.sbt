val Name = "BIT"
val Version = "1.0-SNAPSHOT"
val GroupId = "io.pixelinc"

name := Name
version := Version
organization := GroupId

scalaVersion := "2.13.1"
scalacOptions += "-language:implicitConversions"

updateOptions := updateOptions.value.withLatestSnapshots(false)

packageOptions in (Compile, packageBin) +=
    Package.ManifestAttributes("Automatic-Module-Name" -> (GroupId + "." + Name.toLowerCase))

resolvers += "jitpack" at "https://jitpack.io"
resolvers += "spigot-repo" at "https://hub.spigotmc.org/nexus/content/repositories/snapshots/"
resolvers += "minebench" at "https://repo.minebench.de/"
resolvers += "jaybin" at "https://jcenter.bintray.com/"
resolvers += "rayzr" at "https://rayzr.dev/repo/"
resolvers += "codemc" at "https://repo.codemc.io/repository/maven-public/"
resolvers += "mojang" at "https://libraries.minecraft.net/"

libraryDependencies += "org.bukkit" % "bukkit" % "1.15.1-R0.1-SNAPSHOT" % "provided"
libraryDependencies += "com.github.Jannyboy11.ScalaPluginLoader" % "ScalaLoader" % "v0.12.2" % "provided"
libraryDependencies += "com.github.SaberLLC" % "Saber-Factions" % "1.6.x-SNAPSHOT" % "provided"
libraryDependencies += "com.gmail.filoghost.holographicdisplays" % "holographicdisplays-api" % "2.4.0" % "provided"

assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)
assemblyMergeStrategy in assembly := {
    case "plugin.yml"   => MergeStrategy.first /* always choose our own plugin.yml if we shade other plugins */
    case x              =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
}
assemblyJarName in assembly := Name + "-" + Version + ".jar"
