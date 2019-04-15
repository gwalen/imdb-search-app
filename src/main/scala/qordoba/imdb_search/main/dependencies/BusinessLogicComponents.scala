package qordoba.imdb_search.main.dependencies

import com.softwaremill.macwire.wire
import qordoba.imdb_search.context.title.service.TitleService
import qordoba.imdb_search.main.dataloader.DataLoader

trait BusinessLogicComponents { self: CommonLayer with DatabaseComponents =>

  lazy val dataLoader: DataLoader     = wire[DataLoader]
  lazy val titleService: TitleService = wire[TitleService]
}
