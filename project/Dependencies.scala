import sbt._

object Versions {
  val akkaV               = "2.5.20"
  val akkaHttpV           = "10.1.7"
  val akkaHttpCorsV       = "0.3.4"
  val postgresqlV         = "42.2.5"
  val slickV              = "3.3.0"
  val slickPgV            = "0.17.2"
  val flywayV             = "5.2.4"
  val macwireV            = "2.3.1"
  val logbackV            = "1.2.3"
  val scalaTestV          = "3.0.5"
  val scalaMockV          = "3.6.0"
  val kebsV               = "1.6.2"
  val catsV               = "1.6.0"
  val commonsIoV          = "2.6"
  val enumeratumV         = "1.5.13"
}

object Dependencies {
  import Versions._

  lazy val allDependencies: Seq[ModuleID] = akkaBase ++ akkaHttp ++ slick ++ macwire ++ logback ++ test ++ others

  private lazy val akkaBase = Seq(
    "com.typesafe.akka" %% "akka-actor"     % akkaV,
    "com.typesafe.akka" %% "akka-stream"    % akkaV,
    "com.typesafe.akka" %% "akka-slf4j"     % akkaV,
    "com.typesafe.akka" %% "akka-testkit"   % akkaV % "test",
    "ch.megard"         %% "akka-http-cors" % akkaHttpCorsV
  )

  private lazy val akkaHttp = Seq(
    "com.typesafe.akka" %% "akka-http-core"       % akkaHttpV,
    "com.typesafe.akka" %% "akka-http"            % akkaHttpV,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpV,
    "com.typesafe.akka" %% "akka-http-testkit"    % akkaHttpV % "test"
  )

  private lazy val slick = Seq(
    "org.postgresql"      % "postgresql"           % postgresqlV,
    "com.typesafe.slick"  %% "slick"               % slickV,
    "com.typesafe.slick"  %% "slick-hikaricp"      % slickV,
    "com.github.tminglei" %% "slick-pg"            % slickPgV,
    "com.github.tminglei" %% "slick-pg_spray-json" % slickPgV,
    "com.github.tminglei" %% "slick-pg_jts"        % slickPgV
  )

  private lazy val macwire = Seq(
    "com.softwaremill.macwire" %% "macros" % macwireV % "provided",
    "com.softwaremill.macwire" %% "util"   % macwireV,
    "com.softwaremill.macwire" %% "proxy"  % macwireV
  )

  private lazy val logback = Seq(
    "ch.qos.logback"       % "logback-classic"          % logbackV
  )

  private lazy val test = Seq(
    "org.scalatest" %% "scalatest"                   % scalaTestV % "test",
    "org.scalamock" %% "scalamock-scalatest-support" % scalaMockV % "test"
  )

  private lazy val others = Seq(
    "pl.iterators"  %% "kebs-slick"      % kebsV,
    "pl.iterators"  %% "kebs-spray-json" % kebsV,
    "com.beachape"  %% "enumeratum"      % enumeratumV,
    "org.typelevel" %% "cats-core"       % catsV,
    "org.flywaydb"  % "flyway-core"      % flywayV,
    "commons-io"    % "commons-io"       % commonsIoV,
    "pl.iterators"  %% "kebs-avro"       % kebsV
  )
}
