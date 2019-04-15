package qordoba.imdb_search.common.database

import java.sql.{Date, Time}
import java.time.{LocalDate, LocalTime}

import com.github.tminglei.slickpg._
import qordoba.imdb_search.common.json.CommonJsonConversion
import pl.iterators.kebs.Kebs
import pl.iterators.kebs.enums.KebsEnums
import qordoba.imdb_search.context.title.domian.TitleGenre
import slick.basic.Capability

trait PostgresDriver extends ExPostgresProfile with PgSprayJsonSupport with PgDate2Support with PgArraySupport with PgEnumSupport {

  override val pgjson = "jsonb"

  override val api = PostgresApi

  override protected def computeCapabilities: Set[Capability] =
    super.computeCapabilities + slick.jdbc.JdbcCapabilities.insertOrUpdate

  object PostgresApi
      extends API
      with JsonImplicits
      with SprayJsonPlainImplicits
      with ArrayImplicits
      with DateTimeImplicits
      with Kebs
      with KebsEnums
      with CommonJsonConversion {

    implicit val marketFinancialProductWrapper = new SimpleArrayJdbcType[String]("text")
      .mapTo[TitleGenre](new TitleGenre(_), _.value).to(_.toList)

    implicit val localDateToDate = MappedColumnType.base[LocalDate, Date](
      l => Date.valueOf(l),
      d => d.toLocalDate
    )

    implicit val localTimeToTime = MappedColumnType.base[LocalTime, Time](
      l => Time.valueOf(l),
      d => d.toLocalTime
    )
  }
}

object PostgresDriver extends PostgresDriver
