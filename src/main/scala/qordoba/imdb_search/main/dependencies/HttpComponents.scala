package qordoba.imdb_search.main.dependencies

import com.softwaremill.macwire.wire
import qordoba.imdb_search.context.health.HealthRouter
import qordoba.imdb_search.context.title.router.TitleRouter

trait HttpComponents { self: CommonLayer with BusinessLogicComponents =>
  lazy val healthRouter: HealthRouter = wire[HealthRouter]
  lazy val titleRouter: TitleRouter   = wire[TitleRouter]
}
