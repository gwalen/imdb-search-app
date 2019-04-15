package qordoba.imdb_search.main.dependencies

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.stream.Materializer
import com.typesafe.config.{Config, ConfigFactory}
import qordoba.imdb_search.common.database.BaseDb.driver.api._
import qordoba.imdb_search.main.config._

import scala.concurrent.ExecutionContext

trait CommonLayer { self =>

  implicit val system: ActorSystem
  implicit def executor: ExecutionContext
  implicit val materializer: Materializer

  lazy val config: Config                  = ConfigFactory.load()
  implicit lazy val logger: LoggingAdapter = system.log

  lazy val db: Database = Database.forConfig("db", config)

  lazy val dbConfig = DatabaseConfig(
    config.getString("db.url"),
    config.getString("db.user"),
    config.getString("db.password"),
    config.getBoolean("db.flyway-migration-during-boot"))

  lazy val serverConfig: ServerConfig = ServerConfig(
    config.getString("http.interface"),
    config.getInt("http.port"),
    config.getString("http.hostname")
  )

  lazy val initFilesConfig = InitFilesConfig(
    config.getString("init.files.titles"),
    config.getString("init.files.title-ratings"),
    config.getString("init.files.cast-members"),
    config.getString("init.files.cast-for-titles")
  )

  lazy val akkaStreamConfig = AkkaStreamConfig(
    config.getInt("stream.parallelism-level"),
    config.getInt("stream.data-load-batch-size"),
    config.getInt("stream.kevin-bacon-calculations-batch-size")
  )

}
