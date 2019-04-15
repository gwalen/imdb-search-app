package qordoba.imdb_search.context.castmember.repository

import qordoba.imdb_search.context.castmember.domain.{CastMember, CastMemberId, CastMemberName, KevinBaconDistance}
import slick.lifted.TableQuery
import qordoba.imdb_search.common.database.BaseDb.driver.api._
import slick.dbio.DBIOAction

class CastMemberRepository {

  private val castMembers = TableQuery[CastMembers]

  def insert(castMember: CastMember): DBIOAction[Int, NoStream, Effect.Write] = castMembers += castMember

  def insertBatch(castMemberBatch: Seq[CastMember]) = castMembers ++= castMemberBatch

  def resetKevinBaconDistance(): DBIOAction[Int, NoStream, Effect.Write] =
    castMembers.map(cm => (cm.kevinBaconDistance, cm.kevinBaconPathAncestorId)).update((CastMember.infiniteDistance, CastMemberId("")))

  def updateKevinBaconDistance(castMemberId: CastMemberId, dist: KevinBaconDistance): DBIOAction[Int, NoStream, Effect.Write] =
    castMembers.filter(_.id === castMemberId).map(_.kevinBaconDistance).update(dist)

  def updateKevinBaconDistanceWithAncestor(castMemberIds: Seq[CastMemberId], dist: KevinBaconDistance, ancestorId: CastMemberId): DBIOAction[Int, NoStream, Effect.Write] =
    castMembers.filter(_.id inSet castMemberIds).map(cm => (cm.kevinBaconDistance, cm.kevinBaconPathAncestorId)).update((dist, ancestorId))

  def findByKevinBaconDistancePublisher(dist: KevinBaconDistance): DBIOAction[Seq[CastMember], Streaming[CastMember], Effect.Read] =
    castMembers.filter(_.kevinBaconDistance === dist).result

  def findKevinBaconDistanceByName(name: CastMemberName): DBIOAction[Seq[KevinBaconDistance], NoStream, Effect.Read] =
    castMembers.filter(_.name === name).map(_.kevinBaconDistance).result

  def findKevinBaconDistanceById(id: CastMemberId): DBIOAction[Seq[KevinBaconDistance], NoStream, Effect.Read] =
    castMembers.filter(_.id === id).map(_.kevinBaconDistance).result

}
