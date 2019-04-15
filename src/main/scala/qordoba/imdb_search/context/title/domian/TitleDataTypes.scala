package qordoba.imdb_search.context.title.domian

case class TitleName(value: String)
case class TitleId(value: String)
case class TitleAvgRating(value: Double)
case class TitleVotesNumber(value: Int)
case class TitleCategory(value: String)
case class TitleGenre(value: String)

case class TitleShort(name: TitleName, category: TitleCategory)