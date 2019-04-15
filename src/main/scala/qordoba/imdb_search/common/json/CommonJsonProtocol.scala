package qordoba.imdb_search.common.json

import java.net.URL
import java.time.format.DateTimeFormatter
import java.time.{Instant, LocalDate, LocalTime, ZonedDateTime}
import java.util.{Currency, UUID}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._
import scala.util.Try


trait CommonJsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {

  implicit val urlJsonFormat = new JsonFormat[URL] {
    override def read(json: JsValue): URL = json match {
      case JsString(url) => Try(new URL(url)).getOrElse(deserializationError("Invalid URL format"))
      case _             => deserializationError("URL should be string")
    }

    override def write(obj: URL): JsValue = JsString(obj.toString)
  }
}

object CommonJsonProtocol extends CommonJsonProtocol
