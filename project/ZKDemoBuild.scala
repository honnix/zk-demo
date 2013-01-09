import sbt._
import Keys._

object Resolvers {
  val myResolvers = Seq()
}

object BuildSettings {

  import Resolvers._

  val buildOrganization = "com.honnix"
  val buildVersion = "0.1-SNAPSHOT"
  val buildScalaVersion = "2.10.0"

  val buildSettings = Defaults.defaultSettings ++ Seq(
    organization := buildOrganization,
    version := buildVersion,
    scalaVersion := buildScalaVersion,
    shellPrompt := ShellPrompt.buildShellPrompt,
    // publish to maven repository
    // publishTo := Some(Resolver.file("file", new File(Path.userHome.absolutePath + "/.m2/repository"))),
    resolvers ++= myResolvers
  )
}

object ShellPrompt {

  object devnull extends ProcessLogger {
    def info(s: => String) {}

    def error(s: => String) {}

    def buffer[T](f: => T): T = f
  }

  def currBranch = (
    ("git status -sb" lines_! devnull headOption)
      getOrElse "-" stripPrefix "## "
    )

  val buildShellPrompt = {
    (state: State) => {
      val currProject = Project.extract(state).currentProject.id
      "%s:%s:%s> ".format(
        currProject, currBranch, BuildSettings.buildVersion
      )
    }
  }
}

object Dependencies {
  // private val crossMappedDowngrade = CrossVersion.fullMapped {
  //   case "2.10.0" => "2.9.2"
  //   case x => x
  // }

  val zookeeper = "org.apache.zookeeper" % "zookeeper" % "3.4.5" excludeAll(
    ExclusionRule(organization = "javax.jms"),
    ExclusionRule(organization = "com.sun.jdmk"),
    ExclusionRule(organization = "com.sun.jmx")
  )
}

object ZKDemoBuild extends Build {

  import Dependencies._
  import BuildSettings._

  val commonDeps = Seq(
    zookeeper
  )

  lazy val zkDemo = Project(
    id = "zk-demo",
    base = file("."),
    settings = buildSettings ++ net.virtualvoid.sbt.graph.Plugin.graphSettings ++ Seq(
      libraryDependencies ++= commonDeps
    ))
}
