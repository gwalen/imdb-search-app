logLevel := Level.Warn

resolvers += "Flyway" at "https://davidmweber.github.io/flyway-sbt.repo"

addSbtPlugin("io.github.davidmweber"  % "flyway-sbt"          % "5.2.0")
addSbtPlugin("com.lucidchart"         % "sbt-scalafmt"        % "1.16")
addSbtPlugin("com.typesafe.sbt"       % "sbt-native-packager" % "1.3.18")
addSbtPlugin("io.spray"               % "sbt-revolver"        % "0.9.1")
addSbtPlugin("com.timushev.sbt"       % "sbt-updates"         % "0.3.4")
