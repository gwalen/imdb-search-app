package qordoba.imdb_search.context.title.router

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import qordoba.imdb_search.main.dataloader.DataLoader
import qordoba.imdb_search.common.json.CommonJsonConversion._
import qordoba.imdb_search.context.title.domian.TitleGenre
import qordoba.imdb_search.context.title.service.TitleService
import akka.http.scaladsl.unmarshalling.Unmarshaller
import qordoba.imdb_search.context.castmember.domain.{CastMemberId, CastMemberName}

import scala.concurrent.ExecutionContext

class TitleRouter(dataLoader: DataLoader, titleService: TitleService)
                 (implicit ex: ExecutionContext, mat: Materializer) {
  import TitleRouter._

  val routes: Route =
    pathPrefix("titles") {
      (post & path("init") & pathEndOrSingleSlash) {
        complete {
          dataLoader.loadData()
          OK -> None
        }
      } ~ (post & path("add_indexes") & pathEndOrSingleSlash ) {
        complete {
          dataLoader.addIndexes()
          OK -> None
        }
      } ~ (post & path("reset_kevin_bacon_distances") & pathEndOrSingleSlash ) {
        complete {
          titleService.resetKevinBaconDistance()
          OK -> None
        }
      } ~ (get & path("top_rated") & pathEndOrSingleSlash & parameters('genre.as[TitleGenre])) { genre =>
        complete(titleService.findTopRatedTitle(genre).map(OK -> _))
      } ~ (get & path("typecasted") & pathEndOrSingleSlash & parameters('name.as[CastMemberName])) { name =>
        complete(titleService.findTypecastingByName(name).map(OK -> _))
      } ~ (get & path("typecasted") & pathEndOrSingleSlash & parameters('id.as[CastMemberId])) { id =>
        complete(titleService.findTypecastingById(id).map(OK -> _))
      } ~ (get & path("coincidence") & pathEndOrSingleSlash & parameters('nameA.as[CastMemberName], 'nameB.as[CastMemberName])) { (nameA, nameB) =>
        complete(titleService.findCoincidenceByName(nameA, nameB).map(OK -> _))
      } ~ (get & path("coincidence") & pathEndOrSingleSlash & parameters('idA.as[CastMemberId], 'idB.as[CastMemberId])) { (idA, idB) =>
        complete(titleService.findCoincidenceById(idA, idB).map(OK -> _))
      } ~ (post & path("calculate_kevin_bacon_distances") & pathEndOrSingleSlash & parameters('kevinBaconId.as[CastMemberId])) { kevinBaconId =>
        complete{
          titleService.calculateKevinBaconDistances(kevinBaconId).map(OK -> _)
          OK -> None
        }
      } ~ (get & path("kevin_bacon_distance") & pathEndOrSingleSlash & parameters('name.as[CastMemberName])) { name =>
        complete(titleService.findKevinBaconDistance(name).map(OK -> _))
      } ~ (get & path("kevin_bacon_distance") & pathEndOrSingleSlash & parameters('id.as[CastMemberId])) { id =>
        complete(titleService.findKevinBaconDistance(id).map(OK -> _))
      }
    }
}

object TitleRouter {

  implicit val titleGenreFromString: Unmarshaller[String, TitleGenre] = Unmarshaller.strict(str => TitleGenre(str))
  implicit val castMemberNameFromString: Unmarshaller[String, CastMemberName] = Unmarshaller.strict(str => CastMemberName(str))
  implicit val castMemberIdFromString: Unmarshaller[String, CastMemberId] = Unmarshaller.strict(str => CastMemberId(str))
}
