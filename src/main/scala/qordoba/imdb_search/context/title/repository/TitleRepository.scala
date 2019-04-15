package qordoba.imdb_search.context.title.repository

import qordoba.imdb_search.context.title.domian._
import slick.lifted.TableQuery
import qordoba.imdb_search.common.database.BaseDb.driver.api._
import qordoba.imdb_search.context.castmember.domain.{CastMember, CastMemberId, CastMemberName, KevinBaconDistance}
import qordoba.imdb_search.context.castmember.repository.CastMembers

class TitleRepository {

  private val titles        = TableQuery[Titles]
  private val titleRatings  = TableQuery[TitleRatings]
  private val castForTitles = TableQuery[CastForTitles]
  private val castMembers   = TableQuery[CastMembers]

  private val topRatedLimit = 100

  def insert(title: Title): DBIOAction[Int, NoStream, Effect.Write] = titles += title

  def insertTitleBatch(titleBatch: Seq[Title]) = titles ++= titleBatch

  def insertTitleRatingBatch(titleRatingBatch: Seq[TitleRating]) = titleRatings ++= titleRatingBatch

  def insertCastForTitleBatch(castForTitleBatch: Seq[CastForTitle]) = castForTitles ++= castForTitleBatch

  def findTopRatedTitles(titleGenre: TitleGenre, movieCategory: TitleCategory): DBIOAction[Seq[(Title, TitleRating)], NoStream, Effect.Read] = {
    val query = for {
      title       <- titles.filter( t => titleGenre.bind === t.genres.any && t.category === movieCategory)
      titleRating <- titleRatings if title.id === titleRating.titleid
    } yield (title, titleRating)
    query.sortBy(t => (t._2.votesNumber.desc, t._2.avgRating.desc)).take(topRatedLimit).result
  }

  def findTitlesByCastMemberName(castMemberName: CastMemberName): DBIOAction[Seq[Title], NoStream, Effect.Read] = {
    val query = for {
      title        <- titles
      castMember   <- castMembers.filter(_.name === castMemberName)
      castForTitle <- castForTitles if castForTitle.titleId === title.id && castForTitle.castMemberId === castMember.id
    } yield title
    query.result
  }

  def findTitlesByCastMemberId(castMemberId: CastMemberId): DBIOAction[Seq[Title], NoStream, Effect.Read] = {
    val query = for {
      title        <- titles
      castMember   <- castMembers.filter(_.id === castMemberId)
      castForTitle <- castForTitles if castForTitle.titleId === title.id && castForTitle.castMemberId === castMember.id
    } yield title
    query.result
  }

  def findUnseenNeighbourCastMemberIds(castMemberId: CastMemberId): DBIOAction[Seq[CastMemberId], NoStream, Effect.Read] = {
    val queryTitleIds = for {
      title        <- titles
      castMember   <- castMembers.filter(_.id === castMemberId)
      castForTitle <- castForTitles if castForTitle.titleId === title.id && castForTitle.castMemberId === castMember.id
    } yield title.id

    val queryCastMembers = for {
      title        <- titles.filter(_.id in queryTitleIds)
      castMember   <- castMembers.filter(_.kevinBaconDistance === CastMember.infiniteDistance)
      castForTitle <- castForTitles if castForTitle.titleId === title.id && castForTitle.castMemberId === castMember.id
    } yield castMember.id
    queryCastMembers.result
  }

  def addIndexsSql(): DBIO[Unit] = DBIO.seq(
    sqlu"""CREATE INDEX idx_titles__cast_members_single_title ON titles__cast_members (title_id);""",
    sqlu"""CREATE INDEX idx_titles__cast_members_single_cast_memeber_id ON titles__cast_members (cast_member_id);""",
    sqlu"""CREATE INDEX idx_cast_member_name ON cast_members ("name");"""
  )
}
