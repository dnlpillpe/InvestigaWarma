# Base de Datos — InvestigaWarma

## 1. Motor y versión

SQLite (motor embebido de Android) a través de **Room 2.6.1**. Nombre del archivo: `investigawarma.db`. Versión de esquema: `1` (primera versión publicada, `exportSchema = true`).

## 2. Diagrama entidad-relación (Mermaid)

```mermaid
erDiagram
    PLAYER_PROFILE {
        int id PK
        string alias
        int avatarId
        int level
        int xp
        long createdAt
        bool soundEnabled
        bool hapticsEnabled
        bool onboardingCompleted
    }

    SCIENTIFIC_MISSION {
        string id PK
        string zone
        int orderIndex
        string title
        string story
        string objective
        string mechanicType
        int difficulty
        int xpReward
        int starReward
        string tags
        string requiredMissionId FK
    }

    MISSION_STEP {
        long id PK
        string missionId FK
        int orderIndex
        string stepType
        string promptText
        string contentJson
    }

    HYPOTHESIS {
        long id PK
        string missionId FK
        string variableText
        string resultText
        string explanationText
        bool isValidStructure
        long createdAt
    }

    EXPERIMENT {
        string id PK
        string zone
        string title
        string description
        string simulatorType
        int difficulty
        string missionId FK
    }

    EXPERIMENT_PARAMETER {
        long id PK
        string experimentId FK
        string name
        string unit
        float minValue
        float maxValue
        float stepValue
        float defaultValue
    }

    EXPERIMENT_RESULT {
        long id PK
        string experimentId FK
        string parameters
        string outcomeSummary
        float outcomeScore
        long createdAt
    }

    SCIENTIFIC_DISCOVERY {
        string id PK
        string missionId FK
        string title
        string description
        long unlockedAt
    }

    COLLECTION_ITEM {
        string id PK
        string key
        string name
        string description
        string category
        string requirementDescription
        long unlockedAt
    }

    BADGE {
        string id PK
        string key
        string name
        string description
        string category
        long unlockedAt
    }

    SCIENTIFIC_JOURNAL {
        long id PK
        string missionId FK
        string title
        string content
        string type
        long createdAt
    }

    VOICE_ENTRY {
        long id PK
        long journalId FK
        string filePath
        int durationSeconds
        long createdAt
    }

    MISSION_PROGRESS {
        string missionId PK_FK
        string status
        int attempts
        float bestScore
        long completedAt
        long lastAttemptAt
    }

    CHALLENGE {
        string id PK
        string zone
        string type
        string title
        string dataJson
        int difficulty
    }

    CHALLENGE_ATTEMPT {
        long id PK
        string challengeId FK
        bool success
        float score
        long createdAt
    }

    SCIENTIFIC_MISSION ||--o{ MISSION_STEP : "tiene"
    SCIENTIFIC_MISSION ||--o{ HYPOTHESIS : "recibe"
    SCIENTIFIC_MISSION ||--|| SCIENTIFIC_DISCOVERY : "otorga"
    SCIENTIFIC_MISSION ||--|| MISSION_PROGRESS : "tiene progreso"
    SCIENTIFIC_MISSION |o--o| EXPERIMENT : "puede enlazar"
    EXPERIMENT ||--o{ EXPERIMENT_PARAMETER : "define"
    EXPERIMENT ||--o{ EXPERIMENT_RESULT : "produce"
    SCIENTIFIC_JOURNAL ||--o{ VOICE_ENTRY : "puede tener"
    SCIENTIFIC_MISSION |o--o{ SCIENTIFIC_JOURNAL : "puede originar"
    CHALLENGE ||--o{ CHALLENGE_ATTEMPT : "registra"
```

## 3. Tablas, campos y restricciones

### 3.1 `player_profile`
Registro único (id fijo = 1). PK: `id`. Sin FKs. No contiene datos personales identificables (solo alias y `avatarId`, un índice 0-7).

### 3.2 `scientific_mission`
PK: `id` (ej. `"m01"`). Índices: `(zone, orderIndex)`, `requiredMissionId`. `tags` se persiste como JSON vía `Converters` (`List<String>`). `requiredMissionId` referencia a otra misión de la misma zona (no se declara `@ForeignKey` formal para permitir null en la primera misión de cada zona sin restricciones circulares; la integridad se garantiza desde el seeder).

### 3.3 `mission_step`
PK autogenerada `id`. FK `missionId → scientific_mission.id` (`CASCADE`). Índice en `missionId`. `stepType` ∈ {OBSERVE, QUESTION, HYPOTHESIS, EXPERIMENT, ANALYZE, DISCOVERY}. `contentJson` contiene el payload específico de cada paso.

### 3.4 `hypothesis`
PK autogenerada. FK `missionId → scientific_mission.id` (`CASCADE`). Índice en `missionId`. `isValidStructure` se calcula con `HypothesisValidator` en el momento de guardar.

### 3.5 `experiment`
PK: `id` (ej. `"exp_plant_01"`). Índices en `zone` y `missionId`. `missionId` nulo = experimento de exploración libre en el Laboratorio.

### 3.6 `experiment_parameter`
PK autogenerada. FK `experimentId → experiment.id` (`CASCADE`). Índice en `experimentId`. Define rango (`minValue`..`maxValue`), paso y valor por defecto de cada variable manipulable.

### 3.7 `experiment_result`
PK autogenerada. FK `experimentId → experiment.id` (`CASCADE`). Índice en `experimentId`. `parameters` (`Map<String, Float>`) persistido vía `Converters`. `outcomeScore` ∈ [0,1].

### 3.8 `scientific_discovery`
PK: `id`. FK `missionId → scientific_mission.id` (`CASCADE`). Índice en `missionId`. `unlockedAt` nulo hasta que se completa la misión.

### 3.9 `collection_item`
PK: `id`. Índice único en `key`, índice en `category`. `category` ∈ {INICIAL, INTERMEDIO, AVANZADO}. `unlockedAt` nulo hasta cumplir `requirementDescription` (verificado por `CollectionRepository.refreshBadges()`).

### 3.10 `badge`
PK: `id`. Índice único en `key`. `category` ∈ {PROGRESO, ZONA, HABILIDAD, DESAFIO}.

### 3.11 `scientific_journal`
PK autogenerada. FK `missionId → scientific_mission.id` (`ON DELETE SET NULL`, para conservar notas aunque la misión cambie). Índices en `missionId` y `createdAt`. `type` ∈ {TEXT, VOICE}.

### 3.12 `voice_entry`
PK autogenerada. FK `journalId → scientific_journal.id` (`CASCADE`). Índice en `journalId`. `filePath` apunta a almacenamiento privado (`filesDir/voice_journal/`). `durationSeconds` ≤ 60.

### 3.13 `mission_progress`
PK: `missionId` (también FK a `scientific_mission.id`, `CASCADE`) → relación 1:1. Índice único en `missionId`, índice en `status`. `status` ∈ {LOCKED, AVAILABLE, STARTED, COMPLETED, MASTERED}.

### 3.14 `challenge`
PK: `id`. Índices en `zone` y `type`. `type` ∈ {DETECTIVE, ORDER, PATTERN, CLASSIFY, BUILD}. `dataJson` contiene el payload tipado (ver `domain/model/ChallengePayloads.kt`).

### 3.15 `challenge_attempt`
PK autogenerada. FK `challengeId → challenge.id` (`CASCADE`). Índices en `challengeId` y `createdAt`. Usado para calcular rachas, precisión y repaso.

## 4. Datos semilla

| Tabla | Cantidad |
|---|---|
| `scientific_mission` | 40 |
| `mission_step` | 240 (6 por misión) |
| `scientific_discovery` | 40 |
| `experiment` | 30 |
| `experiment_parameter` | 90 (3 por experimento) |
| `challenge` | 50 |
| `collection_item` | 20 |
| `badge` | 15 |
| `mission_progress` | 40 (generado junto a las misiones: 5 en AVAILABLE, 35 en LOCKED al instalar) |

Ver `database/sample_data.sql` para un extracto representativo y `tools/generate_seed_data.py` para la generación completa y determinista.

## 5. Consultas importantes

```sql
-- Misiones de una zona en orden, con su estado de progreso
SELECT sm.*, mp.status
FROM scientific_mission sm
LEFT JOIN mission_progress mp ON mp.missionId = sm.id
WHERE sm.zone = 'SALA_OBSERVACION'
ORDER BY sm.orderIndex;

-- Precisión global en desafíos
SELECT
  (SELECT COUNT(*) FROM challenge_attempt WHERE success = 1) AS exitosos,
  (SELECT COUNT(*) FROM challenge_attempt) AS total;

-- Objetos de colección aún bloqueados
SELECT name, requirementDescription FROM collection_item WHERE unlockedAt IS NULL;

-- Racha de éxitos consecutivos en desafíos tipo DETECTIVE (más reciente primero)
SELECT ca.success
FROM challenge_attempt ca
JOIN challenge c ON c.id = ca.challengeId
WHERE c.type = 'DETECTIVE'
ORDER BY ca.createdAt DESC;
```

## 6. Estrategia de creación y transacciones

`AppDatabase.getInstance(context)` crea la base de datos con `Room.databaseBuilder` (sin `fallbackToDestructiveMigration`, ya que la v1.0.0 no tiene versiones previas que migrar). `DatabaseSeeder.seedIfNeeded()` se ejecuta una vez, en una corrutina de `Dispatchers.IO`, al arrancar `InvestigaWarmaApp`. Las operaciones que tocan varias tablas (completar una misión: `mission_progress` + `scientific_discovery` + posible desbloqueo de la siguiente misión) se ejecutan dentro de `db.withTransaction { }` para garantizar consistencia ante fallos.
