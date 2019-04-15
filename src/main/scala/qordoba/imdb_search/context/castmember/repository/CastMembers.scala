package qordoba.imdb_search.context.castmember.repository

import qordoba.imdb_search.common.database.BaseDb.driver.api._
import qordoba.imdb_search.context.castmember.domain._
import slick.lifted.{ProvenShape, Tag}

class CastMembers(tag: Tag) extends Table[CastMember](tag, "cast_members") {

  def id: Rep[CastMemberId]                       = column[CastMemberId]("id", O.PrimaryKey)
  def name: Rep[CastMemberName]                   = column[CastMemberName]("name")
  def kevinBaconDistance: Rep[KevinBaconDistance] = column[KevinBaconDistance]("kevin_bacon_distance")
  def kevinBaconPathAncestorId: Rep[CastMemberId] = column[CastMemberId]("kevin_bacon_path_ancestor")

  override def * : ProvenShape[CastMember] = (id, name, kevinBaconDistance, kevinBaconPathAncestorId) <> ((CastMember.apply _).tupled, CastMember.unapply)
}
