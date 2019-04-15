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
