package qordoba.imdb_search.context.title.service
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, Matchers}
import org.scalatest.concurrent.ScalaFutures
import qordoba.imdb_search.context.title.domian._

class TitleServiceSpec extends FlatSpec with Matchers with MockFactory with ScalaFutures { spec =>

  it should "find typecasting" in  {
    val titles = Seq(
      Title(TitleId(""), TitleName("a"), TitleCategory(""), List(TitleGenre("Horror"), TitleGenre("Drama"))),
      Title(TitleId(""), TitleName("b"), TitleCategory(""), List(TitleGenre("Comedy"), TitleGenre("Drama"))),
      Title(TitleId(""), TitleName("c"), TitleCategory(""), List(TitleGenre("Horror"), TitleGenre("Drama"))),
      Title(TitleId(""), TitleName("d"), TitleCategory(""), List(TitleGenre("Short"))),
      Title(TitleId(""), TitleName("e"), TitleCategory(""), List(TitleGenre("Action")))
    )
    TitleService.extractTypecast(titles) shouldEqual Some(TitleGenre("Drama"))
  }

  it should "find no typecasting if not present in given titles" in  {
    val titles = Seq(
      Title(TitleId(""), TitleName("a"), TitleCategory(""), List(TitleGenre("Horror"), TitleGenre("Drama"))),
      Title(TitleId(""), TitleName("b"), TitleCategory(""), List(TitleGenre("Comedy"), TitleGenre("Drama"))),
      Title(TitleId(""), TitleName("c"), TitleCategory(""), List(TitleGenre("Horror"))),
      Title(TitleId(""), TitleName("d"), TitleCategory(""), List(TitleGenre("Short"))),
      Title(TitleId(""), TitleName("e"), TitleCategory(""), List(TitleGenre("Action")))
    )
    TitleService.extractTypecast(titles) shouldEqual None
  }

  it should "find common titles for two title sets" in {
    val titlesA = Seq(
      Title(TitleId("1"), TitleName("a"), TitleCategory(""), List(TitleGenre("Horror"), TitleGenre("Drama"))),
      Title(TitleId("2"), TitleName("b"), TitleCategory(""), List(TitleGenre("Comedy"), TitleGenre("Drama"))),
      Title(TitleId("3"), TitleName("c"), TitleCategory(""), List(TitleGenre("Horror"))),
      Title(TitleId("4"), TitleName("d"), TitleCategory(""), List(TitleGenre("Short"))),
      Title(TitleId("5"), TitleName("e"), TitleCategory(""), List(TitleGenre("Action")))
    )

    val titlesB = Seq(
      Title(TitleId("1"), TitleName("a"), TitleCategory(""), List(TitleGenre("Horror"), TitleGenre("Drama"))),
      Title(TitleId("2"), TitleName("b"), TitleCategory(""), List(TitleGenre("Comedy"), TitleGenre("Drama"))),
      Title(TitleId("3"), TitleName("c"), TitleCategory(""), List(TitleGenre("Horror"))),
      Title(TitleId("10"), TitleName("c"), TitleCategory(""), List(TitleGenre("Drama"))),
      Title(TitleId("11"), TitleName("c"), TitleCategory(""), List(TitleGenre("Drama"))),
    )

    val commonTitles = Seq(
      Title(TitleId("1"), TitleName("a"), TitleCategory(""), List(TitleGenre("Horror"), TitleGenre("Drama"))),
      Title(TitleId("2"), TitleName("b"), TitleCategory(""), List(TitleGenre("Comedy"), TitleGenre("Drama"))),
      Title(TitleId("3"), TitleName("c"), TitleCategory(""), List(TitleGenre("Horror")))
    )

    TitleService.findCommonTitles(titlesA, titlesB) shouldEqual commonTitles
  }

}
