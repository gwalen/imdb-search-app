import scala.io.Source

name         := "imdb-search-app"
organization := "qordoba"
version      := "1.0.0"
scalaVersion := "2.12.8"

scalacOptions ++= Seq(
  "-target:jvm-1.8",
  "-unchecked",
  "-feature",
  "-encoding",
  "utf8",
  "-deprecation", // Emit warning and location for usages of deprecated APIs.
  "-Ywarn-dead-code", // Warn when dead code is identified.
  "-Ywarn-infer-any", // Warn when a type argument is inferred to be `Any`.
  "-Ywarn-value-discard" // Warn when non-Unit expression results are unused.
)

resolvers ++= Seq(
  Resolver.typesafeRepo("releases")
)

libraryDependencies ++= Dependencies.allDependencies

// Migrations
enablePlugins(FlywayPlugin)
PostgresMigrations.settings

// SBT Native packager
enablePlugins(JavaAppPackaging)

// Integration tests
//lazy val ItTest = config("it") extend Test
//configs(ItTest)
//
//inConfig(ItTest)(
//  Defaults.testSettings ++
//    PostgresMigrations.itSettings ++
//    Seq(
//      executeTests  := (executeTests dependsOn flywayMigrate).value,
//      flywayMigrate := (flywayMigrate dependsOn flywayClean).value
//    ))

// Scapegoat
//scapegoatVersion in ThisBuild := "1.3.8"
//scapegoatDisabledInspections := Seq(
//  "IncorrectlyNamedExceptions",
//  "FinalModifierOnCaseClass"
//)
//scapegoatMaxErrors   := 0
//scapegoatMaxWarnings := 0
//scapegoatMaxInfos    := 0

// Load env variables for TEST from development.env file
//fork in Test := true
//javaOptions in Test := {
//  val envExport = """export\s(\w+=[^\s]+)""".r
//  Source.fromFile("development.env").getLines.toList.flatMap {
//    case envExport(env) => Some(s"-D$env")
//    case _              => None
//  }
//}
