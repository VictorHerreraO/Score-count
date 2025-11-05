# Implementation Plan: Task 62 - Update DAOs and Repositories

## Task Information
- **Issue**: #62
- **Title**: Update DAOs and repositories
- **Type**: enhancement
- **Parent Epic**: #59 - Match Details Screen with Point-by-Point Score Progression Charts
- **Phase**: Phase 1 - Task 1.3 (Data Model & Persistence)

## Decision: APPROVE

### Critical Evaluation

**Should this task be implemented?** YES

**Rationale:**
1. **Real Problem**: The database entities (SetEntity, PointEntity) exist but there are no DAOs or repository methods to interact with them. This is a legitimate gap that prevents the data layer from functioning.

2. **Dependencies Met**: Task 1.2 has been completed:
   - SetEntity exists at `/Users/victor.herrera/Workspace/scorecount/app/src/main/java/com/soyvictorherrera/scorecount/data/database/entity/SetEntity.kt`
   - PointEntity exists at `/Users/victor.herrera/Workspace/scorecount/app/src/main/java/com/soyvictorherrera/scorecount/data/database/entity/PointEntity.kt`
   - MatchWithSets relationship class exists for nested queries
   - Database migration (MIGRATION_1_2) is already in place in AppDatabase.kt

3. **Necessary for Feature**: This task is essential for the Match Details Screen feature (#59). Without DAOs and repository methods:
   - Cannot save set and point data when a match is played
   - Cannot retrieve detailed match information for display
   - The database schema is incomplete without data access layer

4. **Follows Architecture**: The task aligns perfectly with the project's Clean Architecture pattern:
   - DAOs belong in the data layer (Room best practices)
   - Repository interfaces define domain contracts
   - Mappers convert between data and domain models
   - This follows the established pattern seen in MatchMapper and MatchDao

5. **Not Over-Engineering**: This is foundational infrastructure, not premature optimization. The entities exist, so the DAOs must exist to use them.

**Trade-offs:**
- **Benefit**: Enables complete match history tracking with point-by-point data
- **Cost**: Additional code to maintain, but minimal since Room generates DAO implementations
- **Complexity**: Adds nested queries (Match → Sets → Points), but Room handles this with @Relation annotations

**Recommendation**: APPROVE - This is essential infrastructure that unblocks the entire Match Details Screen feature.

---

## Current State Analysis

### What Exists
1. **Database Entities** (Task 1.2 completed):
   - `MatchEntity` with `winnerId` field
   - `SetEntity` with match relationship
   - `PointEntity` with set relationship
   - `MatchWithSets` and `SetWithPoints` relationship classes
   - Database v2 with MIGRATION_1_2 in place

2. **Domain Models**:
   - `Match` with `sets: List<Set>` and `winnerId` fields
   - `Set` with `points: List<Point>` field
   - `Point` model with sequence and scores
   - `SetScore` model for final scores

3. **Existing Data Layer**:
   - `MatchDao` with basic insert and getMatchList methods
   - `MatchRepository` interface with `getMatchList()` and `saveMatch()`
   - `MatchRepositoryImpl` delegating to `LocalMatchDataSource`
   - `LocalMatchDataSource` using MatchDao and MatchMapper
   - `MatchMapper` (currently only maps basic match fields, ignores sets/winnerId)

4. **DI Setup**:
   - `DataModule` provides AppDatabase and MatchDao
   - `DataSourceModule` provides LocalMatchDataSource
   - `RepositoryModule` binds MatchRepository to MatchRepositoryImpl

### What's Missing
1. **SetDao** - No DAO exists for Set entity
2. **PointDao** - No DAO exists for Point entity
3. **MatchDao methods** - Missing `getMatchById` with full relationship loading
4. **SetMapper** - No mapper for SetEntity ↔ Set
5. **PointMapper** - No mapper for PointEntity ↔ Point
6. **Repository method** - `getMatchById(id: String): Flow<Match>` not in interface
7. **Updated MatchMapper** - Doesn't map sets or winnerId fields
8. **Updated saveMatch** - Doesn't save sets and points

---

## Implementation Plan

### Feature Branch
**Name**: `enhancement/task-62-update-daos-repositories`
**Base**: `develop`

### Files to Create

1. **`app/src/main/java/com/soyvictorherrera/scorecount/data/database/dao/SetDao.kt`**
   - Create Room DAO interface for SetEntity
   - Methods:
     - `suspend fun insertSet(set: SetEntity): Long` - Returns set ID for point insertion
     - `suspend fun insertSets(sets: List<SetEntity>): List<Long>` - Bulk insert, returns IDs
     - `@Query("SELECT * FROM sets WHERE match_id = :matchId ORDER BY set_number ASC") fun getSetsForMatch(matchId: Long): Flow<List<SetEntity>>`

2. **`app/src/main/java/com/soyvictorherrera/scorecount/data/database/dao/PointDao.kt`**
   - Create Room DAO interface for PointEntity
   - Methods:
     - `suspend fun insertPoint(point: PointEntity)` - Single insert
     - `suspend fun insertPoints(points: List<PointEntity>)` - Bulk insert for efficiency
     - `@Query("SELECT * FROM points WHERE set_id = :setId ORDER BY sequence ASC") fun getPointsForSet(setId: Long): Flow<List<PointEntity>>`

3. **`app/src/main/java/com/soyvictorherrera/scorecount/data/mapper/SetMapper.kt`**
   - Create mapper class with @Inject constructor
   - Inject PointMapper dependency
   - Methods:
     - `fun mapFromEntity(entity: SetEntity, points: List<PointEntity>): Set` - Convert entity + points to domain
     - `fun mapToEntity(domain: Set, matchId: Long): SetEntity` - Convert domain to entity (needs matchId for FK)
     - `fun mapPointsToEntities(points: List<Point>, setId: Long): List<PointEntity>` - Helper for point mapping

4. **`app/src/main/java/com/soyvictorherrera/scorecount/data/mapper/PointMapper.kt`**
   - Create mapper class with @Inject constructor (no dependencies)
   - Methods:
     - `fun mapFromEntity(entity: PointEntity): Point` - Convert entity to domain
     - `fun mapToEntity(domain: Point, setId: Long): PointEntity` - Convert domain to entity (needs setId for FK)

5. **Unit Test Files**:
   - `app/src/test/java/com/soyvictorherrera/scorecount/data/mapper/SetMapperTest.kt`
   - `app/src/test/java/com/soyvictorherrera/scorecount/data/mapper/PointMapperTest.kt`

### Files to Modify

1. **`app/src/main/java/com/soyvictorherrera/scorecount/data/database/dao/MatchDao.kt`**
   - Add method: `@Transaction @Query("SELECT * FROM matches WHERE id = :matchId") fun getMatchById(matchId: Long): Flow<MatchWithSets?>`
   - Note: @Transaction ensures all relations are loaded atomically

2. **`app/src/main/java/com/soyvictorherrera/scorecount/domain/repository/MatchRepository.kt`**
   - Add method signature: `fun getMatchById(id: String): Flow<Match?>`
   - Return type is `Flow<Match?>` to handle non-existent matches gracefully

3. **`app/src/main/java/com/soyvictorherrera/scorecount/data/repository/MatchRepositoryImpl.kt`**
   - Implement new `getMatchById` method by delegating to dataSource
   - No changes needed to existing methods (saveMatch handled in datasource)

4. **`app/src/main/java/com/soyvictorherrera/scorecount/data/datasource/LocalMatchDataSource.kt`**
   - Update interface: Add `fun getMatchById(id: String): Flow<Match?>`
   - Update implementation:
     - Inject SetDao and PointDao (in addition to existing MatchDao)
     - Inject SetMapper and PointMapper
     - Implement `getMatchById` using MatchDao.getMatchById and mappers
     - Update `saveMatch` to save sets and points transactionally

5. **`app/src/main/java/com/soyvictorherrera/scorecount/data/mapper/MatchMapper.kt`**
   - Add SetMapper injection
   - Update `mapFromEntity(entity: MatchEntity): Match` to include winnerId (but keep sets empty for backward compatibility with existing queries)
   - Add overload: `fun mapFromEntity(matchWithSets: MatchWithSets): Match` - Maps complete match with nested data
   - Update `mapToEntity(domain: Match): MatchEntity` to include winnerId

6. **`app/src/main/java/com/soyvictorherrera/scorecount/data/database/AppDatabase.kt`**
   - Add abstract methods:
     - `abstract fun setDao(): SetDao`
     - `abstract fun pointDao(): PointDao`

7. **`app/src/main/java/com/soyvictorherrera/scorecount/di/DataModule.kt`**
   - Add provider methods:
     - `@Provides @Singleton fun provideSetDao(database: AppDatabase): SetDao = database.setDao()`
     - `@Provides @Singleton fun providePointDao(database: AppDatabase): PointDao = database.pointDao()`

8. **`app/src/test/java/com/soyvictorherrera/scorecount/data/mapper/MatchMapperTest.kt`**
   - Add tests for winnerId mapping
   - Add tests for mapFromEntity(MatchWithSets) with nested sets and points
   - Test backward compatibility with existing mapFromEntity(MatchEntity)

---

## Detailed Implementation Steps

### Step 1: Create PointMapper (No Dependencies)
**File**: `app/src/main/java/com/soyvictorherrera/scorecount/data/mapper/PointMapper.kt`

```kotlin
package com.soyvictorherrera.scorecount.data.mapper

import com.soyvictorherrera.scorecount.data.database.entity.PointEntity
import com.soyvictorherrera.scorecount.domain.model.Point
import javax.inject.Inject

class PointMapper @Inject constructor() {
    /**
     * Maps PointEntity to domain Point model.
     */
    fun mapFromEntity(entity: PointEntity): Point =
        Point(
            sequence = entity.sequence,
            scorerId = entity.scorerId,
            player1Score = entity.player1Score,
            player2Score = entity.player2Score
        )

    /**
     * Maps domain Point to PointEntity.
     * Requires setId for foreign key relationship.
     */
    fun mapToEntity(domain: Point, setId: Long): PointEntity =
        PointEntity(
            setId = setId,
            sequence = domain.sequence,
            scorerId = domain.scorerId,
            player1Score = domain.player1Score,
            player2Score = domain.player2Score
        )
}
```

**Why**: Start with PointMapper since it has no dependencies. This establishes the pattern for SetMapper.

### Step 2: Create PointMapper Tests
**File**: `app/src/test/java/com/soyvictorherrera/scorecount/data/mapper/PointMapperTest.kt`

Test cases:
- `mapFromEntity converts PointEntity to Point correctly`
- `mapToEntity converts Point to PointEntity with correct setId`
- `mapFromEntity handles all point sequences`
- `mapToEntity preserves scorer information`
- `bidirectional mapping preserves all data`
- `mapToEntity correctly assigns foreign key`

**Pattern**: Follow MatchMapperTest structure with JUnit 5 (@Test, assertEquals)

### Step 3: Create SetMapper (Depends on PointMapper)
**File**: `app/src/main/java/com/soyvictorherrera/scorecount/data/mapper/SetMapper.kt`

```kotlin
package com.soyvictorherrera.scorecount.data.mapper

import com.soyvictorherrera.scorecount.data.database.entity.SetEntity
import com.soyvictorherrera.scorecount.data.database.entity.PointEntity
import com.soyvictorherrera.scorecount.domain.model.Set
import com.soyvictorherrera.scorecount.domain.model.Point
import com.soyvictorherrera.scorecount.domain.model.SetScore
import javax.inject.Inject

class SetMapper @Inject constructor(
    private val pointMapper: PointMapper
) {
    /**
     * Maps SetEntity with its points to domain Set model.
     */
    fun mapFromEntity(entity: SetEntity, points: List<PointEntity>): Set =
        Set(
            setNumber = entity.setNumber,
            points = points.map(pointMapper::mapFromEntity),
            finalScore = SetScore(
                player1Score = entity.finalScoreP1,
                player2Score = entity.finalScoreP2
            ),
            winnerId = entity.winnerId
        )

    /**
     * Maps domain Set to SetEntity.
     * Requires matchId for foreign key relationship.
     */
    fun mapToEntity(domain: Set, matchId: Long): SetEntity =
        SetEntity(
            matchId = matchId,
            setNumber = domain.setNumber,
            finalScoreP1 = domain.finalScore.player1Score,
            finalScoreP2 = domain.finalScore.player2Score,
            winnerId = domain.winnerId
        )

    /**
     * Maps domain Points to PointEntity list.
     * Helper method for converting set's points to entities.
     */
    fun mapPointsToEntities(points: List<Point>, setId: Long): List<PointEntity> =
        points.map { pointMapper.mapToEntity(it, setId) }
}
```

**Why**: SetMapper needs PointMapper to convert nested points. This maintains single responsibility.

### Step 4: Create SetMapper Tests
**File**: `app/src/test/java/com/soyvictorherrera/scorecount/data/mapper/SetMapperTest.kt`

Test cases:
- `mapFromEntity converts SetEntity with points to Set correctly`
- `mapFromEntity handles empty points list`
- `mapToEntity converts Set to SetEntity with correct matchId`
- `mapPointsToEntities converts all points with correct setId`
- `bidirectional mapping preserves set data`
- `finalScore mapping works correctly`
- `winnerId is preserved in both directions`

### Step 5: Create PointDao
**File**: `app/src/main/java/com/soyvictorherrera/scorecount/data/database/dao/PointDao.kt`

```kotlin
package com.soyvictorherrera.scorecount.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.soyvictorherrera.scorecount.data.database.entity.PointEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PointDao {
    /**
     * Insert a single point.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPoint(point: PointEntity)

    /**
     * Insert multiple points in bulk.
     * Used when saving a complete set's points.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPoints(points: List<PointEntity>)

    /**
     * Get all points for a specific set, ordered by sequence.
     */
    @Query("SELECT * FROM points WHERE set_id = :setId ORDER BY sequence ASC")
    fun getPointsForSet(setId: Long): Flow<List<PointEntity>>
}
```

**Why**: Room generates implementation. Simple CRUD operations following MatchDao pattern.

### Step 6: Create SetDao
**File**: `app/src/main/java/com/soyvictorherrera/scorecount/data/database/dao/SetDao.kt`

```kotlin
package com.soyvictorherrera.scorecount.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.soyvictorherrera.scorecount.data.database.entity.SetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SetDao {
    /**
     * Insert a single set and return its generated ID.
     * Used when saving sets sequentially with their points.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSet(set: SetEntity): Long

    /**
     * Insert multiple sets in bulk and return their generated IDs.
     * Used when saving a complete match.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSets(sets: List<SetEntity>): List<Long>

    /**
     * Get all sets for a specific match, ordered by set number.
     */
    @Query("SELECT * FROM sets WHERE match_id = :matchId ORDER BY set_number ASC")
    fun getSetsForMatch(matchId: Long): Flow<List<SetEntity>>
}
```

**Why**: Returns IDs to enable inserting points with correct set_id foreign key. Follows Room best practices.

### Step 7: Update MatchDao
**File**: `app/src/main/java/com/soyvictorherrera/scorecount/data/database/dao/MatchDao.kt`

Add method:
```kotlin
/**
 * Get a complete match by ID with all sets and points.
 * Uses @Transaction to ensure atomic loading of relationships.
 */
@Transaction
@Query("SELECT * FROM matches WHERE id = :matchId")
fun getMatchById(matchId: Long): Flow<MatchWithSets?>
```

**Why**: @Transaction annotation ensures Room loads all @Relation data atomically. Returns Flow for reactive updates.

### Step 8: Update AppDatabase
**File**: `app/src/main/java/com/soyvictorherrera/scorecount/data/database/AppDatabase.kt`

Add abstract methods:
```kotlin
abstract fun setDao(): SetDao
abstract fun pointDao(): PointDao
```

**Why**: Room generates DAO implementations at compile time. These methods expose DAOs to DI.

### Step 9: Update DataModule
**File**: `app/src/main/java/com/soyvictorherrera/scorecount/di/DataModule.kt`

Add providers after `provideMatchDao`:
```kotlin
@Provides
@Singleton
fun provideSetDao(database: AppDatabase): SetDao = database.setDao()

@Provides
@Singleton
fun providePointDao(database: AppDatabase): PointDao = database.pointDao()
```

**Why**: Makes DAOs available for injection into LocalMatchDataSource. Singleton scope for efficiency.

### Step 10: Update MatchMapper
**File**: `app/src/main/java/com/soyvictorherrera/scorecount/data/mapper/MatchMapper.kt`

Changes:
1. Inject SetMapper in constructor
2. Update `mapFromEntity(entity: MatchEntity): Match` to include winnerId
3. Add new method `fun mapFromEntity(matchWithSets: MatchWithSets): Match`
4. Update `mapToEntity` to include winnerId

```kotlin
class MatchMapper @Inject constructor(
    private val setMapper: SetMapper
) {
    /**
     * Maps basic MatchEntity to Match (without sets).
     * Used for match list queries.
     */
    fun mapFromEntity(entity: MatchEntity): Match =
        Match(
            id = entity.id.toString(),
            playerOneName = entity.playerOneName,
            playerTwoName = entity.playerTwoName,
            playerOneScore = entity.playerOneScore,
            playerTwoScore = entity.playerTwoScore,
            date = entity.date,
            sets = emptyList(), // Sets not loaded in basic queries
            winnerId = entity.winnerId
        )

    /**
     * Maps complete MatchWithSets to Match (with all nested data).
     * Used for detailed match queries.
     */
    fun mapFromEntity(matchWithSets: MatchWithSets): Match =
        Match(
            id = matchWithSets.match.id.toString(),
            playerOneName = matchWithSets.match.playerOneName,
            playerTwoName = matchWithSets.match.playerTwoName,
            playerOneScore = matchWithSets.match.playerOneScore,
            playerTwoScore = matchWithSets.match.playerTwoScore,
            date = matchWithSets.match.date,
            sets = matchWithSets.sets.map { setWithPoints ->
                setMapper.mapFromEntity(setWithPoints.set, setWithPoints.points)
            },
            winnerId = matchWithSets.match.winnerId
        )

    /**
     * Maps Match to MatchEntity (basic fields only).
     */
    fun mapToEntity(domain: Match): MatchEntity =
        MatchEntity(
            id = domain.id.toLongOrNull() ?: 0,
            playerOneName = domain.playerOneName,
            playerTwoName = domain.playerTwoName,
            playerOneScore = domain.playerOneScore,
            playerTwoScore = domain.playerTwoScore,
            date = domain.date,
            winnerId = domain.winnerId
        )
}
```

**Why**: Two mapFromEntity methods handle different query scenarios. Basic queries don't load sets for performance.

### Step 11: Update MatchRepository Interface
**File**: `app/src/main/java/com/soyvictorherrera/scorecount/domain/repository/MatchRepository.kt`

Add method:
```kotlin
/**
 * Get a complete match by ID with all sets and points.
 * Returns null if match doesn't exist.
 */
fun getMatchById(id: String): Flow<Match?>
```

**Why**: Domain layer defines contract. Returns Flow for reactive updates. Nullable for missing matches.

### Step 12: Update LocalMatchDataSource Interface and Implementation
**File**: `app/src/main/java/com/soyvictorherrera/scorecount/data/datasource/LocalMatchDataSource.kt`

Changes:
1. Update interface: Add `getMatchById` method
2. Inject SetDao and PointDao
3. Inject SetMapper (MatchMapper already has it)
4. Implement `getMatchById`
5. Update `saveMatch` to save sets and points

```kotlin
interface MatchDataSource {
    fun getMatchList(): Flow<List<Match>>
    fun getMatchById(id: String): Flow<Match?>
    suspend fun saveMatch(match: Match)
}

class LocalMatchDataSource @Inject constructor(
    private val matchDao: MatchDao,
    private val setDao: SetDao,
    private val pointDao: PointDao,
    private val matchMapper: MatchMapper,
    private val setMapper: SetMapper
) : MatchDataSource {
    override fun getMatchList(): Flow<List<Match>> =
        matchDao.getMatchList().map {
            it.map(matchMapper::mapFromEntity)
        }

    override fun getMatchById(id: String): Flow<Match?> {
        val matchId = id.toLongOrNull() ?: return flowOf(null)
        return matchDao.getMatchById(matchId).map { matchWithSets ->
            matchWithSets?.let(matchMapper::mapFromEntity)
        }
    }

    override suspend fun saveMatch(match: Match) {
        // Save match entity
        val matchEntity = matchMapper.mapToEntity(match)
        matchDao.insert(matchEntity)

        val matchId = matchEntity.id

        // Save sets and points if present
        if (match.sets.isNotEmpty()) {
            match.sets.forEach { set ->
                // Save set and get generated ID
                val setEntity = setMapper.mapToEntity(set, matchId)
                val setId = setDao.insertSet(setEntity)

                // Save all points for this set
                if (set.points.isNotEmpty()) {
                    val pointEntities = setMapper.mapPointsToEntities(set.points, setId)
                    pointDao.insertPoints(pointEntities)
                }
            }
        }
    }
}
```

**Why**: Transactional save ensures data consistency. Sequential insert gets IDs for foreign keys.

### Step 13: Update MatchRepositoryImpl
**File**: `app/src/main/java/com/soyvictorherrera/scorecount/data/repository/MatchRepositoryImpl.kt`

Add method implementation:
```kotlin
override fun getMatchById(id: String): Flow<Match?> = dataSource.getMatchById(id)
```

**Why**: Simple pass-through following existing repository pattern. No business logic in repository.

### Step 14: Update MatchMapperTest
**File**: `app/src/test/java/com/soyvictorherrera/scorecount/data/mapper/MatchMapperTest.kt`

Add test cases:
- `mapFromEntity includes winnerId from MatchEntity`
- `mapFromEntity with MatchWithSets includes all sets`
- `mapFromEntity with MatchWithSets includes all points in each set`
- `mapFromEntity with MatchWithSets preserves set order`
- `mapFromEntity with MatchWithSets handles empty sets list`
- `mapToEntity includes winnerId`
- `mapToEntity handles null winnerId`

Update setUp to include SetMapper mock/fake.

### Step 15: Run Tests and Verify
```bash
./gradlew test
./gradlew ktlintCheck
```

Verify:
- All new mapper tests pass
- Updated MatchMapperTest passes
- No lint issues
- Code compiles successfully

---

## Acceptance Criteria

- [ ] **SetDao created** with insertSet, insertSets, getSetsForMatch methods
- [ ] **PointDao created** with insertPoint, insertPoints, getPointsForSet methods
- [ ] **MatchDao updated** with getMatchById returning Flow<MatchWithSets?>
- [ ] **SetMapper created** with bidirectional mapping (entity ↔ domain)
- [ ] **PointMapper created** with bidirectional mapping (entity ↔ domain)
- [ ] **MatchMapper updated** to:
  - Include winnerId in all mappings
  - Support mapFromEntity(MatchWithSets) for complete data
  - Maintain backward compatibility with existing queries
- [ ] **MatchRepository interface updated** with getMatchById method
- [ ] **MatchRepositoryImpl updated** with getMatchById implementation
- [ ] **LocalMatchDataSource updated** to:
  - Implement getMatchById
  - Update saveMatch to save sets and points transactionally
- [ ] **AppDatabase updated** with setDao() and pointDao() methods
- [ ] **DataModule updated** with SetDao and PointDao providers
- [ ] **All unit tests pass** for mappers
- [ ] **All existing tests still pass** (regression check)
- [ ] **Code follows existing patterns**:
  - JUnit 5 for tests
  - @Inject constructors
  - Flow return types for reactive data
  - Hilt dependency injection
  - No business logic in data layer

---

## Testing Strategy

### Unit Tests (Required)

1. **PointMapperTest** (6+ tests):
   - Entity to domain conversion
   - Domain to entity conversion with setId
   - Bidirectional mapping preservation
   - All fields mapped correctly

2. **SetMapperTest** (7+ tests):
   - Entity to domain with points
   - Domain to entity with matchId
   - Empty points list handling
   - Points to entities conversion
   - Bidirectional mapping
   - SetScore mapping
   - WinnerId preservation

3. **MatchMapperTest updates** (7+ new tests):
   - WinnerId mapping in basic conversion
   - MatchWithSets complete mapping
   - Nested sets and points
   - Set order preservation
   - Empty sets handling
   - Null winnerId handling

### Integration Tests (Deferred)
Room DAO tests with in-memory database would be ideal but are deferred to later tasks. The current project has no existing DAO tests, so we'll maintain consistency by focusing on mapper unit tests.

### Manual Testing Verification
After implementation, the Builder should verify:
1. Project builds successfully: `./gradlew assembleDebug`
2. All tests pass: `./gradlew test`
3. No lint errors: `./gradlew ktlintCheck`
4. DAOs compile (Room annotation processing works)

---

## Dependencies

**Requires (All Met):**
- Task 1.2 completed ✓ (SetEntity, PointEntity, MatchWithSets exist)
- Database migration in place ✓ (MIGRATION_1_2 exists)
- Room database configured ✓ (AppDatabase at v2)

**Blocks:**
- Task 1.4: Update score tracking (needs saveMatch to work with sets/points)
- Task 3.1+: UI components (need getMatchById to display match details)
- Task 4.1: MatchDetailsViewModel (needs getMatchById repository method)

---

## Time Estimate

**Total: 4-5 hours**

Breakdown:
- Step 1-2: PointMapper + tests (45 min)
- Step 3-4: SetMapper + tests (1 hour)
- Step 5-6: DAOs (30 min)
- Step 7-9: Database and DI updates (30 min)
- Step 10: MatchMapper updates (45 min)
- Step 11-13: Repository updates (30 min)
- Step 14: MatchMapperTest updates (45 min)
- Step 15: Testing and fixes (30 min)

---

## Risk Assessment

**Low Risk**

**Potential Issues:**
1. **Room compilation errors**: Room generates code at build time. If entities/DAOs have issues, build will fail.
   - **Mitigation**: Follow existing MatchDao pattern exactly. Test build frequently.

2. **Foreign key constraints**: SetEntity and PointEntity use CASCADE delete.
   - **Mitigation**: Room migration already created tables correctly. Verified in AppDatabase.kt.

3. **Mapper circular dependency**: SetMapper needs PointMapper, MatchMapper needs SetMapper.
   - **Mitigation**: PointMapper has no dependencies (create first), then SetMapper, then MatchMapper. Hilt handles injection graph.

4. **Transaction handling**: saveMatch must be atomic to prevent partial data.
   - **Mitigation**: Room's suspend functions are transaction-safe. Sequential insert/save in LocalMatchDataSource ensures consistency.

5. **Backward compatibility**: Existing match list queries shouldn't break.
   - **Mitigation**: Keep original mapFromEntity(MatchEntity) unchanged except for winnerId. New overload handles detailed queries.

**No Major Blockers**: All dependencies met, entities exist, patterns established.

---

## Post-Implementation Verification

After Builder completes:
1. ✓ All new files created in correct locations
2. ✓ All modified files updated correctly
3. ✓ Project builds: `./gradlew assembleDebug`
4. ✓ All tests pass: `./gradlew test`
5. ✓ Lint check passes: `./gradlew ktlintCheck`
6. ✓ Git status shows expected changes (10 new files, 9 modified files)

---

## Notes for Builder

- **Follow existing patterns**: Look at MatchMapper and MatchMapperTest as references
- **Test as you go**: Run tests after each mapper creation to catch issues early
- **Room will generate code**: DAOs don't need implementations, just interfaces
- **Foreign keys matter**: Always use returned IDs when inserting related entities
- **Flow vs StateFlow**: Use Flow for repository methods (not StateFlow - that's for state-based data like GameState)
- **Nullable types**: getMatchById returns Flow<Match?> because match might not exist
- **Transaction safety**: Room's suspend functions are transaction-safe, but be careful with multi-step operations

**Critical Implementation Order:**
1. Mappers first (PointMapper → SetMapper → MatchMapper update)
2. Tests for each mapper immediately after creation
3. DAOs next (they're just interfaces)
4. Database and DI updates
5. Repository updates last

This order ensures dependencies are met and allows incremental testing.

---

**Plan created by Analyzer Agent**
**Ready for Builder Agent execution**
