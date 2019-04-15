package qordoba.imdb_search.main.dataloader
import java.io.FileInputStream
import java.nio.file.Paths
import java.util.zip.GZIPInputStream

import akka.Done
import akka.actor.ActorSystem
import akka.event.Logging
import akka.stream.{IOResult, Materializer}
import qordoba.imdb_search.common.database.BaseDb._
import qordoba.imdb_search.context.castmember.repository.CastMemberRepository
import qordoba.imdb_search.context.title.repository.TitleRepository
import akka.stream.scaladsl._
import akka.util.ByteString
import qordoba.imdb_search.context.castmember.domain.CastMember
import qordoba.imdb_search.context.title.domian._
import qordoba.imdb_search.main.config.{AkkaStreamConfig, InitFilesConfig}

import scala.concurrent.{ExecutionContext, Future}

class DataLoader(titleRepository: TitleRepository,
                 castMemberRepository: CastMemberRepository,
                 initFilesConfig: InitFilesConfig,
                 akkaStreamConfig: AkkaStreamConfig,
                 db: Database)(implicit ec: ExecutionContext, mat: Materializer, system: ActorSystem) {

  private val logger = Logging(system, getClass)

  private val columnDelimiter  = "\t"
  private val lineDelimiter    = "\n"
  private val linesToSkip      = 1
  private val parallelismLevel = akkaStreamConfig.parallelismLevel
  private val batchSize        = akkaStreamConfig.dataLoadBatchSize

  def loadData(): Future[Done] = {
    logger.info(s"Data loading start")
    for {
      _ <- loadTitles()
      _ <- loadTitleRatings()
      _ <- loadCastMembers()
      _ <- loadCastForTitle()
    } yield {
      logger.info(s"Data loading finished")
      Done
    }
  }

  def addIndexes(): Future[Done] = {
    logger.info(s"Start generating indexes")
    db.run(titleRepository.addIndexsSql()).map { _ =>
      logger.info(s"Generating indexes finished")
      Done
    }
  }

  private def loadTitles(): Future[Done] = {
    logger.info(s"Start loading data from file: ${initFilesConfig.titleFileName}")

    prepareSource(initFilesConfig.titleFileName)
      .via(Framing.delimiter(ByteString(lineDelimiter), 1024))
      .drop(linesToSkip)
      .map(_.utf8String.split(columnDelimiter))
      .grouped(batchSize)
      .mapAsyncUnordered(parallelismLevel)(parseAndInsertTitleBatch)
      .runWith(Sink.ignore)
  }

  private def parseAndInsertTitleBatch(rawTitleBatch: Seq[Array[String]]) =
    Future {
      val titles = rawTitleBatch.map(Title.fromRawData)
      db.run(titleRepository.insertTitleBatch(titles))
    }.flatten

  private def loadTitleRatings(): Future[Done] = {
    logger.info(s"Start loading data from file: ${initFilesConfig.titleRatingFileName}")

    prepareSource(initFilesConfig.titleRatingFileName)
      .via(Framing.delimiter(ByteString(lineDelimiter), 1024))
      .drop(linesToSkip)
      .map(_.utf8String.split(columnDelimiter))
      .grouped(batchSize)
      .mapAsyncUnordered(parallelismLevel)(parseAndInsertTitleRatingsBatch)
      .runWith(Sink.ignore)
  }

  private def parseAndInsertTitleRatingsBatch(rawTitleRatingBatch: Seq[Array[String]]) =
    Future {
      val titleRatings = rawTitleRatingBatch.map(TitleRating.fromRawData)
      db.run(titleRepository.insertTitleRatingBatch(titleRatings))
    }.flatten


  //TODO: write abstract method for stream reading and writing to DB
  private def loadCastMembers(): Future[Done] = {
    logger.info(s"Start loading data from file: ${initFilesConfig.castMembersFileName}")

    prepareSource(initFilesConfig.castMembersFileName)
      .via(Framing.delimiter(ByteString(lineDelimiter), 1024))
      .drop(linesToSkip)
      .map(_.utf8String.split(columnDelimiter))
      .grouped(batchSize)
      .mapAsyncUnordered(parallelismLevel)(parseAndInsertCastMembersBatch)
      .runWith(Sink.ignore)
  }

  private def parseAndInsertCastMembersBatch(rawCastMembersBatch: Seq[Array[String]]) =
    Future {
      val castMembers = rawCastMembersBatch.map(CastMember.fromRawData)
      db.run(castMemberRepository.insertBatch(castMembers))
    }.flatten

  private def loadCastForTitle(): Future[Done] = {
    logger.info(s"Start loading data from file: ${initFilesConfig.castForTitleFileName}")

    prepareSource(initFilesConfig.castForTitleFileName)
      .via(Framing.delimiter(ByteString(lineDelimiter), 1024))
      .drop(linesToSkip)
      .map(_.utf8String.split(columnDelimiter))
      .grouped(batchSize)
      .mapAsyncUnordered(parallelismLevel)(parseAndInsertCastForTitleBatch)
      .runWith(Sink.ignore)
  }

  private def parseAndInsertCastForTitleBatch(rawCastForTitleBatch: Seq[Array[String]]) =
    Future {
      val castForTitles = rawCastForTitleBatch.map(CastForTitle.fromRawData)
      db.run(titleRepository.insertCastForTitleBatch(castForTitles))
    }.flatten

  private def prepareSource(fileName: String): Source[ByteString, Future[IOResult]] =
    if (fileName.endsWith(".gz")) {
      val gzipInputStream = new GZIPInputStream(new FileInputStream(fileName))
      StreamConverters.fromInputStream(() => gzipInputStream)
    } else {
      FileIO.fromPath(Paths.get(fileName))
    }

}
