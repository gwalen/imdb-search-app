package qordoba.imdb_search.context.title.repository

import qordoba.imdb_search.context.title.domian._
import qordoba.imdb_search.common.database.BaseDb.driver.api._
import slick.lifted.{ProvenShape, Tag}

class Titles(tag: Tag) extends Table[Title](tag, "titles") {

  def id: Rep[TitleId]                   = column[TitleId]("id", O.PrimaryKey)
  def name: Rep[TitleName]               = column[TitleName]("name")
  def category: Rep[TitleCategory]       = column[TitleCategory]("category")
  def genres: Rep[List[TitleGenre]]      = column[List[TitleGenre]]("genres")

  override def * : ProvenShape[Title] = (id, name, category, genres) <> ((Title.apply _).tupled, Title.unapply)
}
