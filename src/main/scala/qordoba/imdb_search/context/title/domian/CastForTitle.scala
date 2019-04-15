package qordoba.imdb_search.context.title.domian
import qordoba.imdb_search.context.castmember.domain.CastMemberId

case class CastForTitle(titleId: TitleId, castMemberId: CastMemberId)

object CastForTitle {
  def fromRawData(rawCastForTitle: Array[String]): CastForTitle =
    CastForTitle(
      TitleId(rawCastForTitle(0)),
      CastMemberId(rawCastForTitle(2))
    )
}
