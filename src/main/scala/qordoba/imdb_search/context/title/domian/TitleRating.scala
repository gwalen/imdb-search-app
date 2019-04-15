package qordoba.imdb_search.context.title.domian

case class TitleRating(
  titleId: TitleId,
  avgRating: TitleAvgRating,
  votesNumber: TitleVotesNumber
)

object TitleRating {
  def fromRawData(rawTitleRating: Array[String]): TitleRating =
    TitleRating(
      TitleId(rawTitleRating(0)),
      TitleAvgRating(rawTitleRating(1).toDouble),
      TitleVotesNumber(rawTitleRating(2).toInt)
    )
}
