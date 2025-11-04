# Task 30 Implementation Plan: Reduce Long Parameter Lists

## Decision: APPROVE

### Critical Evaluation

**Problem Analysis:**
The issue correctly identifies 5 instances of long parameter lists that exceed detekt thresholds:
- 4 Composable functions with 6-9 parameters
- 1 ViewModel with 8 constructor parameters (7 dependencies + 1 dispatcher)
- 1 private domain function with 6 parameters

**Is this worth doing?**
✅ **YES - This is a legitimate code quality improvement**

**Reasoning:**
1. **Real maintainability issue**: Parameter lists with 8-9 callbacks are difficult to work with
2. **Jetpack Compose best practice**: Google's official guidance recommends parameter objects for callbacks
3. **Clean Architecture alignment**: Grouping related use cases follows SRP and makes dependencies clearer
4. **Testing benefits**: Test setup becomes simpler with grouped parameters
5. **Low risk**: Refactoring is mechanical with clear automated verification (tests must pass)

**Trade-offs:**
- Adds new classes (parameter objects, use case container) - but these are simple data holders
- Slightly more code - but significantly improved readability and organization
- Need to update all call sites - but this is mechanical and compiler-verified

**Not over-engineering because:**
- These are established patterns (Compose callbacks object, use case grouping)
- Code is already complex enough to warrant organization
- Reduces cognitive load when reading/modifying composables

## Affected Files

### Files to Create
1. `/app/src/main/java/com/soyvictorherrera/scorecount/ui/scorescreen/components/CentralControlsCallbacks.kt`
2. `/app/src/main/java/com/soyvictorherrera/scorecount/ui/scorescreen/components/PlayerScoreCardCallbacks.kt`
3. `/app/src/main/java/com/soyvictorherrera/scorecount/ui/scorescreen/ScoreScreenCallbacks.kt`
4. `/app/src/main/java/com/soyvictorherrera/scorecount/domain/usecase/ScoreUseCases.kt`

### Files to Modify
1. `/app/src/main/java/com/soyvictorherrera/scorecount/ui/scorescreen/components/CentralControls.kt`
2. `/app/src/main/java/com/soyvictorherrera/scorecount/ui/scorescreen/components/PlayerScoreCard.kt`
3. `/app/src/main/java/com/soyvictorherrera/scorecount/ui/scorescreen/ScoreScreen.kt`
4. `/app/src/main/java/com/soyvictorherrera/scorecount/ui/scorescreen/ScoreViewModel.kt`
5. `/app/src/main/java/com/soyvictorherrera/scorecount/domain/calculator/ScoreCalculator.kt`
6. `/app/src/main/java/com/soyvictorherrera/scorecount/di/DataModule.kt` (or create new UseCaseModule.kt)
7. `/app/src/test/java/com/soyvictorherrera/scorecount/ui/scorescreen/ScoreViewModelTest.kt`
8. `/detekt-baseline.xml`

## Implementation Steps

### Step 1: Create Callback Parameter Objects for Composables

**1.1 Create CentralControlsCallbacks**

Create new file: `app/src/main/java/com/soyvictorherrera/scorecount/ui/scorescreen/components/CentralControlsCallbacks.kt`

```kotlin
package com.soyvictorherrera.scorecount.ui.scorescreen.components

/**
 * Callback actions for the CentralControls component.
 *
 * Groups all action callbacks to reduce parameter list complexity.
 */
data class CentralControlsCallbacks(
    val onReset: () -> Unit,
    val onSwitchServe: () -> Unit,
    val onStartNewGame: () -> Unit,
    val onNavigateToHistory: () -> Unit,
    val onNavigateToSettings: () -> Unit
)
```

**1.2 Create PlayerScoreCardCallbacks**

Create new file: `app/src/main/java/com/soyvictorherrera/scorecount/ui/scorescreen/components/PlayerScoreCardCallbacks.kt`

```kotlin
package com.soyvictorherrera.scorecount.ui.scorescreen.components

/**
 * Callback actions for the PlayerScoreCard component.
 *
 * Groups increment and decrement callbacks to reduce parameter list complexity.
 */
data class PlayerScoreCardCallbacks(
    val onIncrement: () -> Unit,
    val onDecrement: () -> Unit
)
```

**1.3 Create ScoreScreenCallbacks**

Create new file: `app/src/main/java/com/soyvictorherrera/scorecount/ui/scorescreen/ScoreScreenCallbacks.kt`

```kotlin
package com.soyvictorherrera.scorecount.ui.scorescreen

/**
 * Callback actions for the ScoreScreen composables (Portrait and Landscape).
 *
 * Groups all game action callbacks to reduce parameter list complexity in screen-level composables.
 */
data class ScoreScreenCallbacks(
    val onIncrement: (Int) -> Unit,
    val onDecrement: (Int) -> Unit,
    val onReset: () -> Unit,
    val onSwitchServe: () -> Unit,
    val onStartNewGame: () -> Unit,
    val onNavigateToHistory: () -> Unit,
    val onNavigateToSettings: () -> Unit
)
```

### Step 2: Refactor CentralControls Component

**2.1 Update CentralControls.kt**

Modify the function signature to use the callbacks object:

```kotlin
@Composable
fun CentralControls(
    gameState: GameState,
    gameSettings: GameSettings,
    callbacks: CentralControlsCallbacks,
    modifier: Modifier = Modifier
) {
    // Update all callback references:
    // onReset -> callbacks.onReset
    // onSwitchServe -> callbacks.onSwitchServe
    // onStartNewGame -> callbacks.onStartNewGame
    // onNavigateToHistory -> callbacks.onNavigateToHistory
    // onNavigateToSettings -> callbacks.onNavigateToSettings
}
```

**Specific changes:**
- Line 33-42: Update function signature
- Line 55: `onClick = callbacks.onNavigateToHistory`
- Line 61: `onClick = callbacks.onNavigateToSettings`
- Line 94: `onClick = callbacks.onStartNewGame`
- Line 107: `onClick = callbacks.onReset`
- Line 120: `onClick = callbacks.onSwitchServe`

### Step 3: Refactor PlayerScoreCard Component

**3.1 Update PlayerScoreCard.kt**

Modify the function signature to use the callbacks object:

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScoreCard(
    playerName: String,
    score: Int,
    isServing: Boolean,
    isFinished: Boolean,
    showPlayerName: Boolean,
    callbacks: PlayerScoreCardCallbacks,
    modifier: Modifier = Modifier
) {
    // Update callback references:
    // onIncrement -> callbacks.onIncrement
    // onDecrement -> callbacks.onDecrement
}
```

**Specific changes:**
- Line 35-43: Update function signature
- Line 46: `onClick = callbacks.onIncrement`
- Line 108: `onClick = callbacks.onDecrement`
- Line 114: `onClick = callbacks.onIncrement`

### Step 4: Refactor ScoreScreen Composables

**4.1 Update ScoreScreenPortrait**

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoreScreenPortrait(
    gameState: GameState,
    gameSettings: GameSettings,
    callbacks: ScoreScreenCallbacks
) {
    // Update all callback references throughout the function
}
```

**Specific changes:**
- Line 83-92: Update function signature
- Line 106: `onClick = callbacks.onNavigateToHistory`
- Line 115: `onClick = callbacks.onNavigateToSettings`
- Line 136: `onReset = callbacks.onReset`
- Line 137: `onSwitchServe = callbacks.onSwitchServe`
- Line 138: `onStartNewGame = callbacks.onStartNewGame`
- Line 193: `onIncrement = { callbacks.onIncrement(gameState.player1.id) }`
- Line 194: `onDecrement = { callbacks.onDecrement(gameState.player1.id) }`
- Line 208: `onIncrement = { callbacks.onIncrement(gameState.player2.id) }`
- Line 209: `onDecrement = { callbacks.onDecrement(gameState.player2.id) }`

**4.2 Update ScoreScreenLandscape**

```kotlin
@Composable
fun ScoreScreenLandscape(
    gameState: GameState,
    gameSettings: GameSettings,
    callbacks: ScoreScreenCallbacks
) {
    // Update all callback references throughout the function
}
```

**Specific changes:**
- Line 217-226: Update function signature
- Line 244: `onIncrement = { callbacks.onIncrement(gameState.player1.id) }`
- Line 245: `onDecrement = { callbacks.onDecrement(gameState.player1.id) }`
- Line 249-257: Update CentralControls call to use CentralControlsCallbacks
- Line 266: `onIncrement = { callbacks.onIncrement(gameState.player2.id) }`
- Line 267: `onDecrement = { callbacks.onDecrement(gameState.player2.id) }`

**4.3 Update ScoreScreen (entry point)**

```kotlin
@Composable
fun ScoreScreen(
    viewModel: ScoreViewModel,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val gameState by viewModel.gameState.collectAsState()
    val gameSettings by viewModel.gameSettings.collectAsState()
    val configuration = LocalConfiguration.current

    val callbacks = ScoreScreenCallbacks(
        onIncrement = viewModel::incrementScore,
        onDecrement = viewModel::decrementScore,
        onReset = viewModel::resetGame,
        onSwitchServe = viewModel::manualSwitchServe,
        onStartNewGame = viewModel::resetGame,
        onNavigateToHistory = onNavigateToHistory,
        onNavigateToSettings = onNavigateToSettings
    )

    ScoreCountTheme {
        when (configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                ScoreScreenLandscape(
                    gameState = gameState,
                    gameSettings = gameSettings,
                    callbacks = callbacks
                )
            }
            else -> {
                ScoreScreenPortrait(
                    gameState = gameState,
                    gameSettings = gameSettings,
                    callbacks = callbacks
                )
            }
        }
    }
}
```

**Note on PlayerScoreCard updates:**
For both Portrait and Landscape, update PlayerScoreCard calls to use the callbacks object:

```kotlin
PlayerScoreCard(
    playerName = gameState.player1.name,
    score = gameState.player1.score,
    isServing = gameSettings.markServe && gameState.servingPlayerId == gameState.player1.id,
    isFinished = gameState.isFinished,
    showPlayerName = gameSettings.showNames,
    callbacks = PlayerScoreCardCallbacks(
        onIncrement = { callbacks.onIncrement(gameState.player1.id) },
        onDecrement = { callbacks.onDecrement(gameState.player1.id) }
    ),
    modifier = Modifier.weight(1f)
)
```

**Note on CentralControls update in Landscape:**

```kotlin
CentralControls(
    modifier = Modifier.padding(horizontal = 4.dp),
    gameState = gameState,
    gameSettings = gameSettings,
    callbacks = CentralControlsCallbacks(
        onReset = callbacks.onReset,
        onSwitchServe = callbacks.onSwitchServe,
        onStartNewGame = callbacks.onStartNewGame,
        onNavigateToHistory = callbacks.onNavigateToHistory,
        onNavigateToSettings = callbacks.onNavigateToSettings
    )
)
```

### Step 5: Create ScoreUseCases Container

**5.1 Create ScoreUseCases.kt**

Create new file: `app/src/main/java/com/soyvictorherrera/scorecount/domain/usecase/ScoreUseCases.kt`

```kotlin
package com.soyvictorherrera.scorecount.domain.usecase

import javax.inject.Inject

/**
 * Container for score-related use cases.
 *
 * Groups related use cases to reduce constructor parameter lists in ViewModels
 * and improve organization of business logic dependencies.
 */
data class ScoreUseCases @Inject constructor(
    val increment: IncrementScoreUseCase,
    val decrement: DecrementScoreUseCase,
    val switchServe: ManualSwitchServeUseCase,
    val reset: ResetGameUseCase,
    val saveMatch: SaveMatchUseCase
)
```

### Step 6: Refactor ScoreViewModel

**6.1 Update ScoreViewModel.kt constructor**

```kotlin
@HiltViewModel
class ScoreViewModel
    @Inject
    constructor(
        private val scoreRepository: com.soyvictorherrera.scorecount.domain.repository.ScoreRepository,
        private val scoreUseCases: ScoreUseCases,
        settingsRepository: SettingsRepository,
        @DefaultDispatcher private val dispatcher: CoroutineDispatcher
    ) : ViewModel() {
```

**6.2 Update use case references**

Replace all use case calls:
- Line 49: `scoreUseCases.increment(playerId)`
- Line 55: `scoreUseCases.decrement(playerId)`
- Line 61: `scoreUseCases.switchServe()`
- Line 67: `scoreUseCases.reset()`
- Line 82: `scoreUseCases.saveMatch(match)`

Remove individual use case properties from the constructor (lines 22-26).

### Step 7: Refactor ScoreCalculator.determineNextServer

**7.1 Create parameter object**

Add this data class at the top of ScoreCalculator.kt (after imports, before the object):

```kotlin
/**
 * Parameters for determining the next server.
 * Groups related data to reduce parameter list complexity.
 */
private data class ServerDeterminationParams(
    val currentScores: Pair<Int, Int>,
    val currentServingPlayerId: Int,
    val playerIds: Pair<Int, Int>,
    val settings: GameSettings,
    val setEnded: Boolean,
    val lastSetWinnerId: Int?
)
```

**7.2 Update determineNextServer signature**

```kotlin
private fun determineNextServer(params: ServerDeterminationParams): Int {
    val (currentScores, currentServingPlayerId, playerIds, settings, setEnded, lastSetWinnerId) = params
    val (p1Id, p2Id) = playerIds

    // Rest of the function remains the same
}
```

**7.3 Update call site in incrementScore**

Around line 80-88, update the call:

```kotlin
val newServingPlayerId =
    determineNextServer(
        ServerDeterminationParams(
            currentScores = Pair(newP1Score, newP2Score),
            currentServingPlayerId = currentState.servingPlayerId ?: currentState.player1.id,
            playerIds = Pair(currentState.player1.id, currentState.player2.id),
            settings = settings,
            setEnded = setJustEnded,
            lastSetWinnerId = lastSetWinnerId
        )
    )
```

### Step 8: Update Hilt Module for ScoreUseCases

**Option A: Add to existing DataModule.kt (simpler)**

Add this provider to the DataModule object:

```kotlin
@Provides
@Singleton
fun provideScoreUseCases(
    scoreRepository: com.soyvictorherrera.scorecount.domain.repository.ScoreRepository,
    settingsRepository: SettingsRepository
): ScoreUseCases {
    return ScoreUseCases(
        increment = IncrementScoreUseCase(scoreRepository, settingsRepository),
        decrement = DecrementScoreUseCase(scoreRepository, settingsRepository),
        switchServe = ManualSwitchServeUseCase(scoreRepository),
        reset = ResetGameUseCase(scoreRepository, settingsRepository),
        saveMatch = SaveMatchUseCase(
            // Need to inject MatchRepository here - check current DI setup
        )
    )
}
```

**Wait - ScoreUseCases has @Inject constructor, so Hilt can auto-provide it!**

Actually, since we're using `@Inject constructor` on ScoreUseCases, Hilt will automatically know how to create it. No module change needed unless there are scoping requirements.

**Verify:** Use cases currently don't have @Inject constructors. Check if they need them or if they're provided manually.

Need to check: Do use cases have @Inject constructors? Let me look...

Based on the test file (line 38-42), use cases are instantiated directly with repositories. They likely don't have @Inject yet.

**Decision:** Add `@Inject constructor` to each use case, then ScoreUseCases will work automatically.

**Alternative if use cases can't have @Inject:**

Create a UseCaseModule:

```kotlin
package com.soyvictorherrera.scorecount.di

import com.soyvictorherrera.scorecount.domain.repository.MatchRepository
import com.soyvictorherrera.scorecount.domain.repository.ScoreRepository
import com.soyvictorherrera.scorecount.domain.repository.SettingsRepository
import com.soyvictorherrera.scorecount.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

    @Provides
    @ViewModelScoped
    fun provideScoreUseCases(
        scoreRepository: ScoreRepository,
        settingsRepository: SettingsRepository,
        matchRepository: MatchRepository
    ): ScoreUseCases {
        return ScoreUseCases(
            increment = IncrementScoreUseCase(scoreRepository, settingsRepository),
            decrement = DecrementScoreUseCase(scoreRepository, settingsRepository),
            switchServe = ManualSwitchServeUseCase(scoreRepository),
            reset = ResetGameUseCase(scoreRepository, settingsRepository),
            saveMatch = SaveMatchUseCase(matchRepository)
        )
    }
}
```

**Recommendation:** Check if use cases have @Inject. If not, create the UseCaseModule above.

### Step 9: Update Tests

**9.1 Update ScoreViewModelTest.kt**

Update the test setup (lines 44-54):

```kotlin
// Create ScoreUseCases container
val scoreUseCases = ScoreUseCases(
    increment = incrementScoreUseCase,
    decrement = decrementScoreUseCase,
    switchServe = manualSwitchServeUseCase,
    reset = resetGameUseCase,
    saveMatch = saveMatchUseCase
)

viewModel =
    ScoreViewModel(
        scoreRepository = fakeScoreRepository,
        scoreUseCases = scoreUseCases,
        settingsRepository = fakeSettingsRepository,
        dispatcher = testDispatcher
    )
```

**9.2 Update the "does not auto-save match when game is already finished" test**

Similar update around lines 243-260:

```kotlin
val isolatedScoreUseCases = ScoreUseCases(
    increment = incrementScoreUseCase,
    decrement = decrementScoreUseCase,
    switchServe = manualSwitchServeUseCase,
    reset = resetGameUseCase,
    saveMatch = isolatedSaveMatchUseCase
)

val isolatedViewModel =
    ScoreViewModel(
        scoreRepository = fakeScoreRepository,
        scoreUseCases = isolatedScoreUseCases,
        settingsRepository = fakeSettingsRepository,
        dispatcher = testDispatcher
    )
```

### Step 10: Update detekt-baseline.xml

Remove the 5 LongParameterList entries:

**Lines to remove:**
- Line 8: `LongParameterList:CentralControls.kt`
- Line 9: `LongParameterList:PlayerScoreCard.kt`
- Line 10: `LongParameterList:ScoreCalculator.kt`
- Line 11: `LongParameterList:ScoreScreen.kt`
- Line 12: `LongParameterList:ScoreViewModel.kt`

**Note:** Keep the LongMethod entries (lines 5-7) as those are separate issues.

After removing, the CurrentIssues section should have 27 entries instead of 32.

### Step 11: Verification

**11.1 Build verification**

```bash
./gradlew clean build
```

**11.2 Test verification**

```bash
./gradlew test
```

**11.3 Lint verification**

```bash
./gradlew lint
./gradlew ktlintCheck
```

**11.4 detekt verification**

```bash
./gradlew detekt
```

Should show no LongParameterList violations.

**11.5 Manual smoke test**

If possible, install and test the app:
```bash
./gradlew installDebug
```

Test all game interactions:
- Score increment/decrement
- Serve switching
- Game reset
- Navigation to history/settings
- Portrait and landscape orientations

## Important Notes

### Composable Callback Pattern
Using callback objects in Compose is a recommended practice. From Google's Compose guidelines, grouping related callbacks reduces complexity and makes the API clearer.

### Use Case Grouping
Grouping related use cases is a common pattern in Clean Architecture to:
1. Reduce ViewModel constructor complexity
2. Make dependencies more explicit (all score operations in one place)
3. Simplify testing (one container to mock/fake)

### Destructuring in determineNextServer
Using a parameter object with destructuring maintains readability while reducing the parameter count. The function body remains unchanged.

### No Behavioral Changes
This is a pure refactoring - no logic changes, only organizational improvements. All existing tests should pass without modification (except for setup code).

### Hilt Configuration
Since ScoreUseCases uses `@Inject constructor`, Hilt should auto-provide it IF all dependency use cases also have @Inject constructors. If not, we need the manual module provider.

**Action for Builder:** Check if use cases have @Inject constructors. If not, create UseCaseModule.kt as specified in Step 8.

## Acceptance Criteria Checklist

- [ ] CentralControls has 4 parameters (down from 8)
- [ ] PlayerScoreCard has 7 parameters (down from 8)
- [ ] ScoreScreenPortrait has 3 parameters (down from 9)
- [ ] ScoreScreenLandscape has 3 parameters (down from 9)
- [ ] ScoreViewModel has 4 constructor parameters (down from 8)
- [ ] determineNextServer has 1 parameter (down from 6)
- [ ] All callback objects created and properly used
- [ ] ScoreUseCases container created and integrated
- [ ] All tests pass without failures
- [ ] detekt baseline updated (5 LongParameterList entries removed)
- [ ] No new detekt violations introduced
- [ ] App builds successfully
- [ ] No functional changes to behavior (smoke test passes)

## Estimated Complexity

**Medium** - Mechanical refactoring across multiple files with clear patterns. Requires careful attention to detail but no complex logic changes.

**Time estimate:** 2-3 hours including testing and verification.

**Risk level:** Low - compiler catches most errors, tests verify behavior.
