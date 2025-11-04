# Task 60: Create Domain Models for Detailed Scoring

**Status**: APPROVED
**Priority**: HIGH
**Epic**: #59 - Match Details Screen with Point-by-Point Score Progression Charts
**Phase**: Phase 1 - Task 1.1

## Critical Evaluation

### Decision: APPROVE

**Rationale**:
1. **Real Problem**: The current Match model only stores final scores (sets won), with no point-by-point or set-by-set data. This prevents implementing the Match Details screen which is essential for player performance analysis.
2. **Clear Business Value**: Enables players to analyze match performance, understand momentum shifts, and review scoring patterns - directly enhancing user experience.
3. **Architectural Foundation**: This is foundational work required for all subsequent tasks in the Match Details epic. Without these models, no other tasks can proceed.
4. **Follows Existing Patterns**: All changes align with the project's Clean Architecture approach and existing domain model patterns (immutable data classes, no Android dependencies).
5. **Not Refactoring for Refactoring's Sake**: This is genuine new functionality, not unnecessary changes to working code.

**Trade-offs Accepted**:
- Database migration complexity (acceptable - enables critical new feature)
- Increased data storage (acceptable - essential for feature functionality)
- Additional model complexity (acceptable - reflects real-world domain complexity)

## Analysis

### Current State

**Existing Models**:
```kotlin
// domain/model/Match.kt
data class Match(
    val id: String,
    val playerOneName: String,
    val playerTwoName: String,
    val playerOneScore: Int,    // Sets won
    val playerTwoScore: Int,    // Sets won
    val date: Long
)

// domain/model/Player.kt
data class Player(
    val id: Int,
    val name: String,
    val score: Int = 0          // Points in current set
)
```

**Architecture Patterns** (from ARCHITECTURE.md):
- Domain layer: Pure Kotlin data classes, no Android dependencies
- Immutable models (data classes with val properties)
- Repository pattern for data access
- Room database with entities mapped via mappers
- StateFlow for state, Flow for events

**Existing Database**:
- Single `MatchEntity` table (version 1)
- Only stores final scores, no detailed history
- Mapper pattern: `MatchMapper` converts between entity and domain

### Requirements from Implementation Guide

**New Models Needed**:
1. **Point**: Represents a single point in a set
   - Tracks who scored
   - Cumulative scores after point
   - Sequential ordering

2. **Set**: Represents a complete set
   - Contains all points played
   - Final score
   - Winner identification

3. **SetScore**: Simple score representation
   - Player 1 score
   - Player 2 score

**Model Updates**:
1. **Match**: Add sets list and winner
2. **Player**: Add color property for chart visualization

### Integration Points

**Where Models Will Be Used**:
1. **ScoreCalculator** (domain/calculator/ScoreCalculator.kt):
   - Currently generates `GameState` with current scores
   - Will need to track point-by-point history
   - Already pure business logic (no dependencies)

2. **SaveMatchUseCase** (domain/usecase/SaveMatchUseCase.kt):
   - Currently saves Match with final scores only
   - Will save complete Sets and Points

3. **Database Layer**:
   - New entities: `SetEntity`, `PointEntity`
   - New DAOs: `SetDao`, `PointDao`
   - Updated `MatchEntity` with winner
   - Database migration from v1 to v2

4. **UI Layer**:
   - MatchDetailsScreen (new) will consume full Match model
   - Charts will use Point data for visualization
   - Player colors for chart lines

## Implementation Plan

### Phase 1: Create New Domain Models

#### Step 1.1: Create Point Data Class
**File**: `/Users/victor.herrera/Workspace/scorecount/app/src/main/java/com/soyvictorherrera/scorecount/domain/model/Point.kt`

**Purpose**: Represents a single point scored in a set with cumulative score tracking.

**Implementation**:
```kotlin
package com.soyvictorherrera.scorecount.domain.model

/**
 * Represents a single point in a set with cumulative score tracking.
 *
 * This model captures each point scored during a set, including:
 * - Sequential ordering of points
 * - Which player scored the point
 * - Cumulative scores for both players after this point
 *
 * Used for point-by-point analysis and score progression visualization.
 *
 * @property sequence The sequential number of this point (1, 2, 3... N)
 * @property scorerId The ID of the player who scored this point
 * @property player1Score Player 1's cumulative score after this point
 * @property player2Score Player 2's cumulative score after this point
 */
data class Point(
    val sequence: Int,
    val scorerId: Int,
    val player1Score: Int,
    val player2Score: Int
)
```

**Design Decisions**:
- `sequence`: Int starting from 1 (human-readable, matches spec)
- `scorerId`: Int (references Player.id, maintains consistency with existing Player model)
- Cumulative scores: Track both players' scores after this point (enables chart rendering)
- Immutable: All properties are `val` (follows existing pattern)
- No Android dependencies: Pure Kotlin (follows Clean Architecture)

**Validation Rules** (to be enforced at use-case level):
- sequence >= 1
- sequence must be continuous (no gaps)
- scorerId must be valid Player.id
- Cumulative scores never decrease
- Exactly one score increases by 1 per point

---

#### Step 1.2: Create SetScore Data Class
**File**: `/Users/victor.herrera/Workspace/scorecount/app/src/main/java/com/soyvictorherrera/scorecount/domain/model/SetScore.kt`

**Purpose**: Simple value object representing the final score of a set.

**Implementation**:
```kotlin
package com.soyvictorherrera.scorecount.domain.model

/**
 * Represents the final score of a set.
 *
 * Simple value object for displaying set outcomes.
 *
 * @property player1Score Player 1's final score in the set
 * @property player2Score Player 2's final score in the set
 */
data class SetScore(
    val player1Score: Int,
    val player2Score: Int
)
```

**Design Decisions**:
- Simple data holder (no business logic)
- Aligns with implementation guide specification
- Clear naming: `player1Score` / `player2Score` (consistent with existing Match model)
- Could use existing Player.score pattern, but SetScore is clearer for final scores

---

#### Step 1.3: Create Set Data Class
**File**: `/Users/victor.herrera/Workspace/scorecount/app/src/main/java/com/soyvictorherrera/scorecount/domain/model/Set.kt`

**Purpose**: Represents a complete set with point-by-point history and outcome.

**Implementation**:
```kotlin
package com.soyvictorherrera.scorecount.domain.model

/**
 * Represents a complete set in a match with point-by-point tracking.
 *
 * Contains the full history of points played during the set,
 * the final score, and the winner. Used for detailed match analysis
 * and score progression visualization.
 *
 * @property setNumber The set number in the match (1, 2, 3, etc.)
 * @property points List of all points played in sequential order
 * @property finalScore The final score of the set
 * @property winnerId The ID of the player who won this set
 */
data class Set(
    val setNumber: Int,
    val points: List<Point>,
    val finalScore: SetScore,
    val winnerId: Int
)
```

**Design Decisions**:
- `setNumber`: Int starting from 1 (human-readable, aligns with UI display)
- `points`: List<Point> (ordered collection, maintains sequence)
- `finalScore`: Uses SetScore value object (clear separation of concerns)
- `winnerId`: Int (references Player.id, consistent with scorerId in Point)
- Immutable list: `List` not `MutableList` (enforces immutability)

**Data Integrity** (to be validated at use-case level):
- points.size > 0 (every set has at least one point)
- points are in sequence order (1, 2, 3... N)
- Last point's cumulative scores match finalScore
- winnerId matches the player with higher finalScore

**Note**: Implementation guide shows `winner: Player` but using `winnerId: Int` is more consistent with the existing architecture:
- Reduces data duplication (player info already in Match)
- Simpler database relationships
- Easier to serialize/deserialize
- Follows pattern established by Point.scorerId

---

### Phase 2: Update Existing Domain Models

#### Step 2.1: Update Match Model
**File**: `/Users/victor.herrera/Workspace/scorecount/app/src/main/java/com/soyvictorherrera/scorecount/domain/model/Match.kt`

**Current Implementation**:
```kotlin
data class Match(
    val id: String,
    val playerOneName: String,
    val playerTwoName: String,
    val playerOneScore: Int,
    val playerTwoScore: Int,
    val date: Long
)
```

**Updated Implementation**:
```kotlin
package com.soyvictorherrera.scorecount.domain.model

/**
 * Represents a complete match with detailed set and point tracking.
 *
 * Contains the full match history including all sets played,
 * point-by-point progression, and match outcome.
 *
 * @property id Unique identifier for the match
 * @property playerOneName Name of player 1
 * @property playerTwoName Name of player 2
 * @property playerOneScore Number of sets won by player 1
 * @property playerTwoScore Number of sets won by player 2
 * @property date Match date as Unix timestamp (milliseconds)
 * @property sets List of all sets played in the match (empty for legacy matches)
 * @property winnerId ID of the match winner (null for ties or legacy matches)
 */
data class Match(
    val id: String,
    val playerOneName: String,
    val playerTwoName: String,
    val playerOneScore: Int,
    val playerTwoScore: Int,
    val date: Long,
    val sets: List<Set> = emptyList(),
    val winnerId: Int? = null
)
```

**Design Decisions**:
- **Backward Compatible**: New properties have default values
  - `sets = emptyList()`: Legacy matches without detailed data still work
  - `winnerId = null`: Legacy matches or ties have null winner
- **Preserve Existing Properties**: No breaking changes to existing code
  - playerOneScore/playerTwoScore remain (used in Match History list)
  - Redundancy acceptable for backward compatibility
- **winnerId**: Nullable Int (references Player.id)
  - Null for legacy matches (no point-by-point data)
  - Null for ties (edge case)
  - Aligns with Player.id type

**Migration Strategy**:
- Existing matches: sets = [], winnerId = null
- New matches: Full sets with point data, winnerId populated
- UI can check `sets.isEmpty()` to determine if detailed data exists

---

#### Step 2.2: Update Player Model
**File**: `/Users/victor.herrera/Workspace/scorecount/app/src/main/java/com/soyvictorherrera/scorecount/domain/model/Player.kt`

**Current Implementation**:
```kotlin
data class Player(
    val id: Int,
    val name: String,
    val score: Int = 0
)
```

**Updated Implementation**:
```kotlin
package com.soyvictorherrera.scorecount.domain.model

/**
 * Represents a player in the game.
 *
 * @property id Unique identifier for the player
 * @property name Player's display name
 * @property score Current score in the active set (default: 0)
 * @property color Player's color for UI visualization in hex format (default: null)
 *                Format: "#RRGGBB" (e.g., "#135BEC" for blue)
 *                Used for chart lines, UI accents, and visual identification
 */
data class Player(
    val id: Int,
    val name: String,
    val score: Int = 0,
    val color: String? = null
)
```

**Design Decisions**:
- **Color Type**: String (hex format "#RRGGBB")
  - Domain layer cannot use android.graphics.Color or androidx.compose.ui.graphics.Color
  - String hex is platform-independent
  - UI layer converts to appropriate Color type
  - Nullable: Legacy players won't have colors
- **Backward Compatible**: Default value maintains compatibility
- **Format Validation**: Will be enforced at use-case/data layer
  - Regex: `^#[0-9A-Fa-f]{6}$`
  - Examples: "#135BEC" (blue), "#FB923C" (orange)

**Alternative Considered - Custom Color Value Object**:
```kotlin
data class PlayerColor(val hex: String) {
    init {
        require(hex.matches(Regex("^#[0-9A-Fa-f]{6}$"))) { "Invalid hex color" }
    }
}
```
**Rejected** because:
- Adds complexity for simple value
- Domain model should be simple data holders
- Validation better at use-case level (more flexible)
- String is simpler for database storage

**Color Assignment Strategy** (for reference, implemented later):
- Player 1: "#135BEC" (blue - from implementation guide)
- Player 2: "#FB923C" (orange - from implementation guide)
- Assignment happens when creating GameState/saving Match

---

### Phase 3: File Organization and Package Structure

**New Files to Create**:
```
app/src/main/java/com/soyvictorherrera/scorecount/domain/model/
├── Point.kt           (NEW)
├── SetScore.kt        (NEW)
├── Set.kt             (NEW)
├── Match.kt           (UPDATE - add sets, winnerId)
└── Player.kt          (UPDATE - add color)
```

**No Changes Needed** (in this task):
- GameState.kt (unchanged - tracks current game state only)
- GameSettings.kt (unchanged)
- All calculator classes (unchanged - future task)
- All use cases (unchanged - future task)
- All data layer (unchanged - future task)

---

### Phase 4: Integration Considerations

#### Database Migration (Future Task 1.2)
**Required Changes**:
```sql
-- New tables
CREATE TABLE sets (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    match_id INTEGER NOT NULL,
    set_number INTEGER NOT NULL,
    final_score_player1 INTEGER NOT NULL,
    final_score_player2 INTEGER NOT NULL,
    winner_id INTEGER NOT NULL,
    FOREIGN KEY (match_id) REFERENCES matches(id)
);

CREATE TABLE points (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    set_id INTEGER NOT NULL,
    sequence INTEGER NOT NULL,
    scorer_id INTEGER NOT NULL,
    player1_score INTEGER NOT NULL,
    player2_score INTEGER NOT NULL,
    FOREIGN KEY (set_id) REFERENCES sets(id)
);

-- Update matches table
ALTER TABLE matches ADD COLUMN winner_id INTEGER;
```

**Mapper Pattern**:
- `SetMapper`: SetEntity ↔ Set (with Point list)
- `PointMapper`: PointEntity ↔ Point
- Update `MatchMapper`: Include sets relationship

#### Use Case Updates (Future Task 1.4)
**SaveMatchUseCase**:
- Accept updated Match with sets
- Persist all sets and points transactionally
- Maintain backward compatibility (empty sets = legacy match)

**ScoreCalculator Integration**:
- Track points during game (currently only tracks current scores)
- Build Point list as game progresses
- Populate Set when set ends
- No changes to core calculation logic (pure functions remain pure)

#### UI Layer (Future Phase 3)
**MatchDetailsScreen**:
- Consume full Match model with sets
- Display sets in reverse order (newest first)
- Pass Point list to chart component
- Handle legacy matches (empty sets)

**Chart Component**:
- Transform List<Point> to chart data
- Use Player.color for line colors
- X-axis: Point.sequence
- Y-axis: Point.player1Score / Point.player2Score

---

## Testing Strategy

### Unit Tests (Future - with use case implementation)

**Point Validation**:
```kotlin
@Test
fun `point sequence must be positive`()

@Test
fun `cumulative scores cannot decrease`()

@Test
fun `exactly one score increases per point`()
```

**Set Validation**:
```kotlin
@Test
fun `set final score matches last point`()

@Test
fun `points are in sequential order`()

@Test
fun `set must have at least one point`()

@Test
fun `winner must have higher score`()
```

**Match Validation**:
```kotlin
@Test
fun `match with empty sets is valid (legacy)`()

@Test
fun `match winnerId matches player with more sets`()

@Test
fun `backward compatibility - existing matches work`()
```

**Player Color**:
```kotlin
@Test
fun `valid hex color formats accepted`()

@Test
fun `invalid hex color formats rejected`()

@Test
fun `null color is valid (legacy players)`()
```

### Data Integrity Tests (Future - with mapper implementation)

**Mapper Tests**:
```kotlin
@Test
fun `SetMapper bidirectional mapping`()

@Test
fun `PointMapper bidirectional mapping`()

@Test
fun `MatchMapper includes sets relationship`()

@Test
fun `MatchMapper handles empty sets (legacy)`()
```

**Database Tests**:
```kotlin
@Test
fun `cascading delete - deleting match deletes sets and points`()

@Test
fun `query match with sets returns all points`()

@Test
fun `migration from v1 to v2 preserves existing matches`()
```

---

## Acceptance Criteria

- [x] All domain models created with proper structure matching spec
- [x] Point model tracks sequence, scorer, and cumulative scores for both players
- [x] Set model contains list of Points and final score
- [x] SetScore model represents final set score
- [x] Match model includes list of Sets and winner reference (with backward compatibility)
- [x] Player model has color property for chart visualization
- [x] All models follow existing domain layer patterns:
  - Pure Kotlin data classes
  - No Android dependencies
  - Immutable (val properties)
  - Proper KDoc documentation
- [x] Backward compatibility maintained (default values for new properties)
- [x] Integration points documented for future tasks

---

## Dependencies

**None** - This is the foundational task.

**Blocks**:
- Task 1.2: Update database schema (needs these models)
- Task 1.3: Update DAOs and repositories (needs these models)
- Task 1.4: Update score tracking (needs these models)
- All subsequent tasks in Epic #59

---

## References

- Epic #59: https://github.com/VictorHerreraO/Score-count/issues/59
- Implementation Guide: `/Users/victor.herrera/Workspace/scorecount/match_details_screen/IMPLEMENTATION_GUIDE.md`
- Architecture: `/Users/victor.herrera/Workspace/scorecount/ARCHITECTURE.md`
- Existing Match Model: `/Users/victor.herrera/Workspace/scorecount/app/src/main/java/com/soyvictorherrera/scorecount/domain/model/Match.kt`
- Existing Player Model: `/Users/victor.herrera/Workspace/scorecount/app/src/main/java/com/soyvictorherrera/scorecount/domain/model/Player.kt`

---

## Notes for Builder

### Implementation Order

1. **Create Point.kt** - No dependencies
2. **Create SetScore.kt** - No dependencies
3. **Create Set.kt** - Depends on Point, SetScore
4. **Update Player.kt** - Add color property with default
5. **Update Match.kt** - Add sets and winnerId with defaults

### Code Style

- Follow existing patterns in domain/model/ directory
- Use KDoc comments (/** */) for all public properties
- Package: `com.soyvictorherrera.scorecount.domain.model`
- No imports from Android SDK or Compose
- All properties are `val` (immutable)
- Use descriptive property names (player1Score not p1Score)

### Validation Notes

- **Do NOT add validation logic to data classes** (keep them simple)
- Validation will be added in use cases (future task)
- Data classes are just data holders
- Business rules enforced at calculator/use-case level

### Testing Notes

- **Do NOT write tests yet** (domain models are data holders)
- Tests will be written when:
  - Use cases are implemented (validation logic)
  - Mappers are created (data layer)
  - Integration points are established

### Color Format

**Hex String Format**: "#RRGGBB"
- Must start with #
- Exactly 6 hex digits (0-9, A-F, a-f)
- Examples: "#135BEC", "#FB923C", "#000000", "#FFFFFF"
- Case insensitive: "#135bec" == "#135BEC"

**UI Layer Conversion** (for reference):
```kotlin
// In Compose UI layer
val playerColor = Color(android.graphics.Color.parseColor(player.color))
```

### Backward Compatibility

**Critical**: Existing code must continue to work:
- Match history screen uses playerOneScore/playerTwoScore
- GameState uses Player without color
- SaveMatchUseCase can save Match without sets
- MatchMapper can handle Match without sets/winnerId

**Future Migration**:
- Old matches: sets = [], winnerId = null
- New matches: Full sets with points, winnerId set
- Match Details screen checks: `if (sets.isEmpty())` show "No detailed data"

---

## Success Criteria for Builder

Task is complete when:
1. All 3 new files created (Point, SetScore, Set)
2. 2 existing files updated (Match, Player)
3. All models follow existing patterns (data classes, val, KDoc)
4. No compilation errors
5. No Android/Compose dependencies in domain layer
6. Backward compatibility maintained (defaults for new properties)
7. Code style matches existing domain models

**Do NOT**:
- Add validation logic (future task)
- Write tests (future task)
- Modify GameState (not needed yet)
- Create use cases (future task)
- Create database entities (future task)
- Modify any existing functionality

**You are ONLY creating/updating domain models** - pure data classes with no logic.
