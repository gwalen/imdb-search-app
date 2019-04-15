package qordoba.imdb_search.context.title.domian

case class Title(
  id: TitleId,
  name: TitleName,
  category: TitleCategory,
  genres: List[TitleGenre]
)

object Title {
  def fromRawData(rawTitle: Array[String]): Title =
    Title(
      TitleId(rawTitle(0)),
      TitleName(rawTitle(2)),
      TitleCategory(rawTitle(1)),
      rawTitle(8).split(",").toList.map(TitleGenre)
    )
}
