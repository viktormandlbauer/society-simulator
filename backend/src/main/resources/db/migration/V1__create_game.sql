-- =========================================
-- Base users + subtypes (player, gamemaster)
-- =========================================
CREATE TABLE IF NOT EXISTS users (
                                     id          BIGSERIAL PRIMARY KEY,
                                     username    VARCHAR(256) NOT NULL UNIQUE,
                                     created_at  TIMESTAMPTZ  NOT NULL,  -- set in Java
                                     updated_at  TIMESTAMPTZ  NOT NULL   -- set in Java
);

CREATE TABLE IF NOT EXISTS gamemaster (
                                          id BIGINT PRIMARY KEY,
                                          CONSTRAINT fk_gamemaster_user
                                              FOREIGN KEY (id) REFERENCES users(id)
                                                  ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS player (
                                      id BIGINT PRIMARY KEY,
                                      CONSTRAINT fk_player_user
                                          FOREIGN KEY (id) REFERENCES users(id)
                                              ON DELETE CASCADE
);

-- =========================================
-- Game entity
-- =========================================
CREATE TABLE IF NOT EXISTS game (
                                    id             BIGSERIAL PRIMARY KEY,
                                    status         VARCHAR(32)  NOT NULL DEFAULT 'CREATED',
                                    gamemaster_id  BIGINT       NOT NULL,
                                    theme          VARCHAR(256) NOT NULL,

                                    maxrounds      INT          NOT NULL DEFAULT 1,
                                    maxplayers     INT          NOT NULL DEFAULT 4,

                                    started_at     TIMESTAMPTZ  NOT NULL,  -- set in Java
                                    ended_at       TIMESTAMPTZ,            -- set in Java when game ends

                                    created_at     TIMESTAMPTZ  NOT NULL,  -- set in Java
                                    updated_at     TIMESTAMPTZ  NOT NULL,  -- set in Java

                                    messages       JSONB        NOT NULL DEFAULT '[]'::jsonb,

                                    CONSTRAINT fk_game_gamemaster
                                        FOREIGN KEY (gamemaster_id)
                                            REFERENCES gamemaster(id)
                                            ON DELETE RESTRICT,

                                    CONSTRAINT chk_game_messages_is_array
                                        CHECK (jsonb_typeof(messages) = 'array')
);

-- Helpful indexes
CREATE INDEX IF NOT EXISTS idx_game_gamemaster   ON game(gamemaster_id);
CREATE INDEX IF NOT EXISTS idx_game_status       ON game(status);
CREATE INDEX IF NOT EXISTS idx_game_messages_gin ON game USING GIN (messages);

-- =========================================
-- Game <-> Players (many-to-many via link entity)
-- Surrogate PK + uniqueness on (game_id, player_id)
-- =========================================
CREATE TABLE IF NOT EXISTS game_players (
                                            id         BIGSERIAL PRIMARY KEY,
                                            game_id    BIGINT NOT NULL,
                                            player_id  BIGINT NOT NULL,

                                            CONSTRAINT uq_game_player UNIQUE (game_id, player_id),

                                            CONSTRAINT fk_gp_game
                                                FOREIGN KEY (game_id)   REFERENCES game(id)   ON DELETE CASCADE,
                                            CONSTRAINT fk_gp_player
                                                FOREIGN KEY (player_id) REFERENCES player(id) ON DELETE CASCADE
);

-- Optional helper indexes for common filters
CREATE INDEX IF NOT EXISTS idx_gp_player ON game_players(player_id);
CREATE INDEX IF NOT EXISTS idx_gp_game   ON game_players(game_id);
