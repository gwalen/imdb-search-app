package qordoba.imdb_search.context.title.repository

import qordoba.imdb_search.common.database.BaseDb.driver.api._
import qordoba.imdb_search.context.title.domian._
import slick.lifted.{ProvenShape, Tag}

class TitleRatings(tag: Tag) extends Table[TitleRating](tag, "title_ratings") {

  def titleid: Rep[TitleId]              = column[TitleId]("title_id", O.PrimaryKey)
  def avgRating: Rep[TitleAvgRating]     = column[TitleAvgRating]("avg_rating")
  def votesNumber: Rep[TitleVotesNumber] = column[TitleVotesNumber]("votes_number")

  override def * : ProvenShape[TitleRating] = (titleid, avgRating, votesNumber) <> ((TitleRating.apply _).tupled, TitleRating.unapply)
}
