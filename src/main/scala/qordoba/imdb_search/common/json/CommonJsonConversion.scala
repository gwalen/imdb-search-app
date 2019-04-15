package qordoba.imdb_search.common.json

import pl.iterators.kebs.json.{KebsEnumFormats, KebsSpray}

trait CommonJsonConversion extends CommonJsonProtocol with KebsSpray.Snakified with KebsEnumFormats

object CommonJsonConversion extends CommonJsonConversion
