-- InvestigaWarma — Esquema de base de datos (SQLite / Room), versión 1
-- Generado a mano a partir de las entidades Room reales en
-- app/src/main/java/com/investigawarma/app/data/local/entity/

CREATE TABLE IF NOT EXISTS player_profile (
    id INTEGER NOT NULL PRIMARY KEY,
    alias TEXT NOT NULL,
    avatarId INTEGER NOT NULL,
    level INTEGER NOT NULL DEFAULT 1,
    xp INTEGER NOT NULL DEFAULT 0,
    createdAt INTEGER NOT NULL,
    soundEnabled INTEGER NOT NULL DEFAULT 1,
    hapticsEnabled INTEGER NOT NULL DEFAULT 1,
    onboardingCompleted INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS scientific_mission (
    id TEXT NOT NULL PRIMARY KEY,
    zone TEXT NOT NULL,
    orderIndex INTEGER NOT NULL,
    title TEXT NOT NULL,
    story TEXT NOT NULL,
    objective TEXT NOT NULL,
    mechanicType TEXT NOT NULL,
    difficulty INTEGER NOT NULL,
    xpReward INTEGER NOT NULL,
    starReward INTEGER NOT NULL,
    tags TEXT NOT NULL DEFAULT '[]',
    requiredMissionId TEXT
);
CREATE INDEX IF NOT EXISTS index_scientific_mission_zone_orderIndex ON scientific_mission(zone, orderIndex);
CREATE INDEX IF NOT EXISTS index_scientific_mission_requiredMissionId ON scientific_mission(requiredMissionId);

CREATE TABLE IF NOT EXISTS mission_step (
    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    missionId TEXT NOT NULL,
    orderIndex INTEGER NOT NULL,
    stepType TEXT NOT NULL,
    promptText TEXT NOT NULL,
    contentJson TEXT NOT NULL DEFAULT '{}',
    FOREIGN KEY (missionId) REFERENCES scientific_mission(id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS index_mission_step_missionId ON mission_step(missionId);

CREATE TABLE IF NOT EXISTS hypothesis (
    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    missionId TEXT NOT NULL,
    variableText TEXT NOT NULL,
    resultText TEXT NOT NULL,
    explanationText TEXT NOT NULL,
    isValidStructure INTEGER NOT NULL,
    createdAt INTEGER NOT NULL,
    FOREIGN KEY (missionId) REFERENCES scientific_mission(id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS index_hypothesis_missionId ON hypothesis(missionId);

CREATE TABLE IF NOT EXISTS experiment (
    id TEXT NOT NULL PRIMARY KEY,
    zone TEXT NOT NULL,
    title TEXT NOT NULL,
    description TEXT NOT NULL,
    simulatorType TEXT NOT NULL,
    difficulty INTEGER NOT NULL,
    missionId TEXT
);
CREATE INDEX IF NOT EXISTS index_experiment_zone ON experiment(zone);
CREATE INDEX IF NOT EXISTS index_experiment_missionId ON experiment(missionId);

CREATE TABLE IF NOT EXISTS experiment_parameter (
    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    experimentId TEXT NOT NULL,
    name TEXT NOT NULL,
    unit TEXT NOT NULL,
    minValue REAL NOT NULL,
    maxValue REAL NOT NULL,
    stepValue REAL NOT NULL,
    defaultValue REAL NOT NULL,
    FOREIGN KEY (experimentId) REFERENCES experiment(id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS index_experiment_parameter_experimentId ON experiment_parameter(experimentId);

CREATE TABLE IF NOT EXISTS experiment_result (
    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    experimentId TEXT NOT NULL,
    parameters TEXT NOT NULL,
    outcomeSummary TEXT NOT NULL,
    outcomeScore REAL NOT NULL,
    createdAt INTEGER NOT NULL,
    FOREIGN KEY (experimentId) REFERENCES experiment(id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS index_experiment_result_experimentId ON experiment_result(experimentId);

CREATE TABLE IF NOT EXISTS scientific_discovery (
    id TEXT NOT NULL PRIMARY KEY,
    missionId TEXT NOT NULL,
    title TEXT NOT NULL,
    description TEXT NOT NULL,
    unlockedAt INTEGER,
    FOREIGN KEY (missionId) REFERENCES scientific_mission(id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS index_scientific_discovery_missionId ON scientific_discovery(missionId);

CREATE TABLE IF NOT EXISTS collection_item (
    id TEXT NOT NULL PRIMARY KEY,
    key TEXT NOT NULL,
    name TEXT NOT NULL,
    description TEXT NOT NULL,
    category TEXT NOT NULL,
    requirementDescription TEXT NOT NULL,
    unlockedAt INTEGER,
    illustrationKey TEXT
);
CREATE UNIQUE INDEX IF NOT EXISTS index_collection_item_key ON collection_item(key);
CREATE INDEX IF NOT EXISTS index_collection_item_category ON collection_item(category);

CREATE TABLE IF NOT EXISTS badge (
    id TEXT NOT NULL PRIMARY KEY,
    key TEXT NOT NULL,
    name TEXT NOT NULL,
    description TEXT NOT NULL,
    category TEXT NOT NULL,
    unlockedAt INTEGER
);
CREATE UNIQUE INDEX IF NOT EXISTS index_badge_key ON badge(key);

CREATE TABLE IF NOT EXISTS scientific_journal (
    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    missionId TEXT,
    title TEXT NOT NULL,
    content TEXT NOT NULL,
    type TEXT NOT NULL,
    createdAt INTEGER NOT NULL,
    FOREIGN KEY (missionId) REFERENCES scientific_mission(id) ON DELETE SET NULL
);
CREATE INDEX IF NOT EXISTS index_scientific_journal_missionId ON scientific_journal(missionId);
CREATE INDEX IF NOT EXISTS index_scientific_journal_createdAt ON scientific_journal(createdAt);

CREATE TABLE IF NOT EXISTS voice_entry (
    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    journalId INTEGER NOT NULL,
    filePath TEXT NOT NULL,
    durationSeconds INTEGER NOT NULL,
    createdAt INTEGER NOT NULL,
    FOREIGN KEY (journalId) REFERENCES scientific_journal(id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS index_voice_entry_journalId ON voice_entry(journalId);

CREATE TABLE IF NOT EXISTS mission_progress (
    missionId TEXT NOT NULL PRIMARY KEY,
    status TEXT NOT NULL,
    attempts INTEGER NOT NULL DEFAULT 0,
    bestScore REAL NOT NULL DEFAULT 0,
    completedAt INTEGER,
    lastAttemptAt INTEGER,
    FOREIGN KEY (missionId) REFERENCES scientific_mission(id) ON DELETE CASCADE
);
CREATE UNIQUE INDEX IF NOT EXISTS index_mission_progress_missionId ON mission_progress(missionId);
CREATE INDEX IF NOT EXISTS index_mission_progress_status ON mission_progress(status);

CREATE TABLE IF NOT EXISTS challenge (
    id TEXT NOT NULL PRIMARY KEY,
    zone TEXT NOT NULL,
    type TEXT NOT NULL,
    title TEXT NOT NULL,
    dataJson TEXT NOT NULL,
    difficulty INTEGER NOT NULL
);
CREATE INDEX IF NOT EXISTS index_challenge_zone ON challenge(zone);
CREATE INDEX IF NOT EXISTS index_challenge_type ON challenge(type);

CREATE TABLE IF NOT EXISTS challenge_attempt (
    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    challengeId TEXT NOT NULL,
    success INTEGER NOT NULL,
    score REAL NOT NULL,
    createdAt INTEGER NOT NULL,
    FOREIGN KEY (challengeId) REFERENCES challenge(id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS index_challenge_attempt_challengeId ON challenge_attempt(challengeId);
CREATE INDEX IF NOT EXISTS index_challenge_attempt_createdAt ON challenge_attempt(createdAt);
