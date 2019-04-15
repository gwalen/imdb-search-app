CREATE TABLE IF NOT EXISTS titles (
  id           TEXT PRIMARY KEY,
  name         TEXT NOT NULL,
  category     TEXT NOT NULL,
  genres       TEXT [] NOT NULL
);

CREATE TABLE IF NOT EXISTS cast_members (
  id                   TEXT PRIMARY KEY,
  name                 TEXT NOT NULL,
  kevin_bacon_distance INTEGER NOT NULL
);

ALTER TABLE cast_members ADD COLUMN kevin_bacon_path_ancestor TEXT NOT NULL DEFAULT '';

CREATE TABLE IF NOT EXISTS titles__cast_members (
  title_id       TEXT NOT NULL,
  cast_member_id TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS title_ratings (
  title_id     TEXT PRIMARY KEY,
  avg_rating   DECIMAL,
  votes_number INTEGER
);

