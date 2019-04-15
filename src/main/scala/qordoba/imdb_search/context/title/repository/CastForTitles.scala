package qordoba.imdb_search.context.title.repository

import qordoba.imdb_search.common.database.BaseDb.driver.api._
import qordoba.imdb_search.context.castmember.domain.CastMemberId
import qordoba.imdb_search.context.title.domian.{Title, CastForTitle, TitleId}
import slick.lifted.{ProvenShape, Tag}

class CastForTitles(tag: Tag) extends Table[CastForTitle](tag, "titles__cast_members") {

  def titleId: Rep[TitleId]           = column[TitleId]("title_id")
  def castMemberId: Rep[CastMemberId] = column[CastMemberId]("cast_member_id")

  override def * : ProvenShape[CastForTitle] = (titleId, castMemberId) <> ((CastForTitle.apply _).tupled, CastForTitle.unapply)
}
