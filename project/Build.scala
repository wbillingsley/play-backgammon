import sbt._
import Keys._

object ApplicationBuild extends Build {

    val appName         = "play-backgammon"
    val appVersion      = "1.0"
      
    val appDependencies = Seq(
    )

    val main = play.Project(appName, appVersion, appDependencies).settings(
      // Add your own project settings here      
    )

}
