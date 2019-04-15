package qordoba.imdb_search.context.title.service

import akka.Done
import akka.actor.ActorSystem
import akka.event.Logging
import akka.stream.Materializer
import akka.stream.scaladsl.Source
import qordoba.imdb_search.common.database.BaseDb.driver.api._
import qordoba.imdb_search.context.castmember.domain.{CastMember, CastMemberId, CastMemberName, KevinBaconDistance}
import qordoba.imdb_search.context.castmember.repository.CastMemberRepository
import qordoba.imdb_search.context.title.domian._
import qordoba.imdb_search.context.title.domian.dto.TopRatedTitlesResponse
import qordoba.imdb_search.context.title.repository.TitleRepository
import qordoba.imdb_search.main.config.AkkaStreamConfig

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

class TitleService(titleRepository: TitleRepository,
                   castMemberRepository: CastMemberRepository,
                   akkaStreamConfig: AkkaStreamConfig,
                   db: Database)
                  (implicit ec: ExecutionContext, mat: Materializer, system: ActorSystem) {
  import TitleService._

  private val logger = Logging(system, getClass)

  private val parallelismLevel = akkaStreamConfig.parallelismLevel
  private val batchSize        = akkaStreamConfig.kevinBaconCalculationsBatchSize

  def findTopRatedTitle(genre: TitleGenre): Future[Seq[TopRatedTitlesResponse]] = {
    logger.info(s"Find top rated movies for: $genre")
    db.run(titleRepository.findTopRatedTitles(genre, TitleCategory("movie"))).map {
      _.map {
        case (title, titleRating) => TopRatedTitlesResponse(title.name, titleRating.avgRating)
      }
    }
  }

  def findTypecastingByName(castMemberName: CastMemberName): Future[String] = {
    logger.info(s"Find typecasting for: $castMemberName")
    db.run(titleRepository.findTitlesByCastMemberName(castMemberName)).map(extractTypecast)
      .map {
        case None             => "no typecasting"
        case Some(titleGenre) => titleGenre.value
      }
  }

  def findTypecastingById(castMemberId: CastMemberId): Future[String] = {
    logger.info(s"Find typecasting for: $castMemberId")
    db.run(titleRepository.findTitlesByCastMemberId(castMemberId)).map(extractTypecast)
      .map {
        case None             => "no typecasting"
        case Some(titleGenre) => titleGenre.value
      }
  }

  def findCoincidenceByName(castMemberNames: (CastMemberName, CastMemberName)): Future[Seq[TitleShort]] = {
    logger.info(s"Find coincidence for: ${castMemberNames._1.value} and ${castMemberNames._2.value}")
    Future.sequence(Seq(
      db.run(titleRepository.findTitlesByCastMemberName(castMemberNames._1)),
      db.run(titleRepository.findTitlesByCastMemberName(castMemberNames._2)))
    ).map(titleSets =>
      findCommonTitles(titleSets(0), titleSets(1)).map(t => TitleShort(t.name, t.category))
    )
  }

  def findCoincidenceById(castMemberIds: (CastMemberId, CastMemberId)): Future[Seq[TitleShort]] = {
    logger.info(s"Find coincidence for: ${ castMemberIds._1.value} and ${castMemberIds._2.value}")
    Future.sequence(Seq(
      db.run(titleRepository.findTitlesByCastMemberId(castMemberIds._1)),
      db.run(titleRepository.findTitlesByCastMemberId(castMemberIds._2)))
    ).map(titleSets =>
      findCommonTitles(titleSets(0), titleSets(1)).map(t => TitleShort(t.name, t.category))
    )
  }

  def findKevinBaconDistance(castMemberName: CastMemberName): Future[Seq[Int]] = {
    db.run(castMemberRepository.findKevinBaconDistanceByName(castMemberName)).map(_.map(_.value))
  }

  def findKevinBaconDistance(castMemberId: CastMemberId): Future[Seq[Int]] = {
    db.run(castMemberRepository.findKevinBaconDistanceById(castMemberId)).map(_.map(_.value))
  }

  def resetKevinBaconDistance(): Future[Done] = {
    db.run(castMemberRepository.resetKevinBaconDistance()).map(_ => Done)
  }

  def calculateKevinBaconDistances(kevinBaconId: CastMemberId): Future[Done] = {
    logger.info("Start calculating Kevin Bacon distances")
    val zeroDistance = KevinBaconDistance(0)
    for {
      _ <- setDistanceKevinBacon(kevinBaconId)
      _ <- calculateDistances(zeroDistance)
    } yield Done
  }

  private def calculateDistances(currentDistance :KevinBaconDistance): Future[Done] = {
    updateDistancesByOne(currentDistance).flatMap { updatedCount =>
      if(updatedCount > 0) calculateDistances(currentDistance + 1) else Future.successful(Done)
    }
  }

  private def updateDistancesByOne(currentDistance: KevinBaconDistance): Future[Int] = {
    val countProcessed = (counter: Int, size: Int) => { counter + size }

    logger.info(s"Start updates for dist = ${currentDistance.value}")
    Source
      .fromPublisher(db.stream(castMemberRepository.findByKevinBaconDistancePublisher(currentDistance)))
      .grouped(batchSize)
      .mapAsyncUnordered(parallelismLevel)(castMembersBatch => updateDistanceAndAncestor(castMembersBatch, currentDistance))
      .runFold(0)(countProcessed)
      .map { processed =>
        logger.info(s"Total updates = $processed")
        processed
      }
  }

  private def updateDistanceAndAncestor(castMembersBatch: Seq[CastMember], currentDistance: KevinBaconDistance): Future[Int] = {
    logger.info(s"Update for distance = ${currentDistance.value}, castMembers batch size = ${castMembersBatch.length}")
    val updatesSeqFuture = castMembersBatch.map { castMember =>
      findUnseenNeighbourCastMembers(castMember)
        .map { neighbourCastMemberIds =>
          if(neighbourCastMemberIds.nonEmpty) {
            Some(castMemberRepository.updateKevinBaconDistanceWithAncestor(neighbourCastMemberIds, currentDistance + 1, castMember.id))
          } else {
            None
          }
        }
    }

    Future.sequence(updatesSeqFuture)
      .map(_.flatten)
      .flatMap(updates => db.run(DBIO.sequence(updates)))
      .map { updatesResults =>
        val sum = updatesResults.sum
        logger.info(s"Updated neighbours of castMember batch for distance = ${currentDistance.value}, count = $sum")
        sum
      }
  }


//  private def updateDistanceAndAncestor_tmp(castMembersBatch: Seq[CastMember], currentDistance: KevinBaconDistance): Future[Int] = {
//    logger.info(s"Update for distance = ${currentDistance.value}, castMembers batch size = ${castMembersBatch.length}")
//    var realCnt = 0
//    val updatesSeqFuture = castMembersBatch.map { castMember =>
//      findUnseenNeighbourCastMembers(castMember)
//        .map { neighbourCastMemberIds =>
//          if(neighbourCastMemberIds.nonEmpty) {
//            realCnt += 1
//            //            logger.info(s"Prepare update for neighbours [${neighbourCastMemberIds.length}] of castMember = $castMember, set dist to ${currentDistance + 1}")
//            Some(castMemberRepository.updateKevinBaconDistanceWithAncestor(neighbourCastMemberIds, currentDistance + 1, castMember.id))
//          } else {
//            None
//          }
//        }
//    }
//
//    logger.info(s"updatesSeqFuture size = ${updatesSeqFuture.length}, realCnt = $realCnt")
//    Future.sequence(updatesSeqFuture)
//      .map(_.flatten)
//      .map { updates =>
//        if(updates.isEmpty) Future.successful(0)
//        else {
//          logger.info(s"updatesSeqFuture size [AFTER Option clear] = ${updates.length}, realCnt = $realCnt")
//          db.run(DBIO.sequence(updates))
//            .map { updatesResults =>
//              val sum = updatesResults.sum
//              logger.info(s"Updated neighbours of castMember batch for distance = ${currentDistance.value}, count = $sum")
//              sum
//            }
//        }
//      }.flatten
//  }

  private def setDistanceKevinBacon(kevinBaconId: CastMemberId): Future[Done] =
    db.run(castMemberRepository.updateKevinBaconDistance(kevinBaconId, KevinBaconDistance(0))).map(_ => Done)

  private def findUnseenNeighbourCastMembers(castMember: CastMember): Future[Seq[CastMemberId]] =
    db.run(titleRepository.findUnseenNeighbourCastMemberIds(castMember.id))
}

object TitleService {
  def extractTypecast(titles: Seq[Title]): Option[TitleGenre] = {
    val genrePopularity = titles.foldLeft(mutable.Map[TitleGenre, Int]()){ (acc, title) =>
      title.genres.map { genre =>
        acc.get(genre) match {
          case None          => acc += (genre -> 1)
          case Some(counter) => acc += (genre -> (counter + 1))
        }
      }
      acc
    }

    val mostPopularGenre = genrePopularity.foldLeft((TitleGenre(""), 0)){
      (maxGenre, genreToCounter) => if (genreToCounter._2 > maxGenre._2) genreToCounter else maxGenre
    }

    if(mostPopularGenre._2 >= (titles.length.toDouble / 2)) Some(mostPopularGenre._1) else None
  }

  def findCommonTitles(titlesA: Seq[Title], titlesB: Seq[Title]): Seq[Title] =
    titlesA.intersect(titlesB)
}
