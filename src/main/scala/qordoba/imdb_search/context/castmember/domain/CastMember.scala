package qordoba.imdb_search.context.castmember.domain

case class CastMember(
  id: CastMemberId,
  name: CastMemberName,
  kevinBaconDistance: KevinBaconDistance,
  kevinBaconPathAncestorId: CastMemberId
)

object CastMember {
  val infiniteDistance = KevinBaconDistance(-1)

  def fromRawData(rawCastMember: Array[String]): CastMember =
    CastMember(
      CastMemberId(rawCastMember(0)),
      CastMemberName(rawCastMember(1)),
      infiniteDistance,
      CastMemberId("")
    )
}

