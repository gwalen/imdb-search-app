package qordoba.imdb_search.main.dependencies

import com.softwaremill.macwire.wire
import qordoba.imdb_search.context.castmember.repository.CastMemberRepository
import qordoba.imdb_search.context.title.repository.TitleRepository

trait DatabaseComponents { self =>

  lazy val titleRepository: TitleRepository           = wire[TitleRepository]
  lazy val castMemberRepository: CastMemberRepository = wire[CastMemberRepository]
}
