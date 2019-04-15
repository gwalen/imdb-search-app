package qordoba.imdb_search.context.castmember.domain

case class CastMemberId(value: String)
case class CastMemberName(value: String)
case class CastMemberCategory(value: String)
case class KevinBaconDistance(value: Int) {
  def +(i: Int): KevinBaconDistance = KevinBaconDistance(value + i)
}
