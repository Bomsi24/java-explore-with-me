CREATE TABLE IF NOT EXISTS stats
(
    id           INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    app          VARCHAR(255) NOT NULL,
    uri          VARCHAR(255) NOT NULL,
    ip           VARCHAR(255) NOT NULL,
    created_date TIMESTAMP    NOT NULL
);
CREATE INDEX stats_created_date ON stats (created_date);
CREATE INDEX stats_uri ON stats (uri);