CREATE TABLE rooms
(
    id   UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE devices
(
    id         UUID PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    type_key   VARCHAR(50)  NOT NULL,
    room_id    UUID         NOT NULL REFERENCES rooms (id),
    state_json JSONB        NOT NULL
);

CREATE TABLE scenarios
(
    id          UUID PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    description TEXT
);

CREATE TABLE scenario_actions
(
    id              UUID PRIMARY KEY,
    scenario_id     UUID        NOT NULL REFERENCES scenarios (id) ON DELETE CASCADE,
    device_id       UUID        NOT NULL REFERENCES devices (id),
    action_key      VARCHAR(50) NOT NULL,
    parameter_value VARCHAR(100),
    action_order    INT         NOT NULL
);

CREATE TABLE execution_logs
(
    id          UUID PRIMARY KEY,
    created_at  TIMESTAMP NOT NULL,
    scenario_id UUID,
    device_id   UUID,
    action_key  VARCHAR(50),
    result_text TEXT      NOT NULL
);
