package qordoba.imdb_search.context.title.domian.dto
import qordoba.imdb_search.context.title.domian.{TitleAvgRating, TitleName}

case class TopRatedTitlesResponse(name: TitleName, avgRating: TitleAvgRating)
