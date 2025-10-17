# Memory: Score-Count App Development

This file tracks the current state of development for the Score-Count application. Treat this file as the context that future you will need to finish any incomplete changes.

## Current Work: Fixed reset game serve indicator toggle bug (Task #43 - Done)

### Summary
Fixed bug where tapping "Reset Game" would toggle the serve indicator between Player 1 and Player 2 on consecutive resets, instead of always resetting to Player 1 (initial state).

### The Bug
When `winnerServesNextGame` setting was `false`, the `resetGame()` logic had this code:
```kotlin
if (!settings.winnerServesNextGame && currentServerId != null) {
    // Alternate server from current server
    if (currentServerId == player1Id) player2Id else player1Id
}
```

This caused the serve indicator to **toggle** on each reset:
- 1st reset: P2 serving → resets to P1 ✓
- 2nd reset: P1 serving → toggles to P2 ✗ (WRONG!)
- 3rd reset: P2 serving → toggles to P1 ✓
- 4th reset: P1 serving → toggles to P2 ✗ (WRONG!)

### Root Cause Analysis
The `currentServerId` parameter was originally intended for **set transitions** during gameplay (when a set ends and a new one begins). However, it was being misused for **manual resets**.

**Manual reset** should return to initial state (Player 1 serving), **not** alternate from the current state.

### The Fix
Simplified the logic to always reset to Player 1 unless `winnerServesNextGame` is `true`:

```kotlin
val firstServer =
    if (settings.winnerServesNextGame && lastGameWinnerId != null) {
        lastGameWinnerId  // Winner serves
    } else {
        player1Id  // Always reset to Player 1 (initial state)
    }
```

**Removed the buggy alternation logic entirely.** Also removed the unused `currentServerId` parameter from the `resetGame()` signature.

### Files Changed
1. **`ScoreCalculator.kt:165-181`**
   - Removed alternation logic branch
   - Removed `currentServerId` parameter
   - Simplified to two branches: winner serves OR Player 1 serves
   - Updated KDoc

2. **`ResetGameUseCase.kt:27-35`**
   - Removed `currentServerId` argument from `resetGame()` call

3. **`ScoreCalculatorTest.kt:598-701`** (Added 4 new tests)
   - `consecutive resets always set serve indicator to Player 1` - Core bug reproduction test
   - `reset from Player 1 serving maintains Player 1 as server`
   - `reset from Player 2 serving resets to Player 1`
   - `reset with winnerServesNextGame true uses lastGameWinnerId` - Ensures setting still works

4. **`ScoreUseCasesTest.kt:265-287`** (Fixed 1 existing test)
   - Updated test expectation from `assertEquals(2, ...)` to `assertEquals(1, ...)`
   - The old test was encoding the buggy behavior

### Acceptance Criteria Met
- ✅ Tapping "Reset Game" always sets serve indicator to Player 1
- ✅ Multiple consecutive resets maintain serve indicator on Player 1
- ✅ Serve indicator state properly initialized in reset logic
- ✅ Unit tests added to verify reset behavior

### Testing
- ✅ All 120 unit tests pass (4 new tests added)
- ✅ Build successful
- ✅ Lint checks pass
- ✅ detekt checks pass

### Key Insights
1. **Unused parameters are a code smell** - `currentServerId` was passed but only needed for set transitions during gameplay, not manual resets
2. **Tests can encode bugs** - One existing test expected the buggy behavior and needed fixing
3. **Simplicity wins** - Removed an entire conditional branch, making the code clearer
4. **Reset = Return to initial state** - Manual reset should always behave like starting a new game

### Status
✅ **COMPLETE - All tests passing, ready for PR**

---

## Previous Work: Fixed serve rotation bug (Task #42 - Done, Round 2)

### Summary
Fixed the serve rotation bug properly after user manual testing revealed my first attempt didn't actually fix anything. The issue was a fundamental misunderstanding of what the serve indicator represents and when rotation should occur.

### First Attempt (WRONG)
Initially replaced the buggy formula with an overly complex interval-based calculation that I then simplified back to the SAME broken formula:
```kotlin
(totalPoints - 1) % interval == 0  // This was THE ORIGINAL BUG!
```

This rotated at point 3 instead of after point 2.

### The Critical Insight
**The serve indicator shows WHO WILL SERVE THE NEXT POINT**, not who is currently serving.

After 2 points are scored with "Serve rotation after 2":
- We've completed an interval of 2 points
- The indicator should show Player 2 (who serves point 3)
- Rotation must happen WHEN totalPoints is a multiple of the interval

### The Correct Fix
Simple modulo check:
```kotlin
totalPoints % interval == 0
```

**Rotation timeline with interval=2:**
- After point 1: 1%2=1, NO rotate, P1 serves ✓
- After point 2: 2%2=0, ROTATE, P2 serves ✓
- After point 3: 3%2=1, NO rotate, P2 serves ✓
- After point 4: 4%2=0, ROTATE, P1 serves ✓

### Files Changed
- `app/src/main/java/com/soyvictorherrera/scorecount/domain/calculator/ScoreCalculator.kt:283-304`
  - Simplified to correct formula: `totalPoints % interval == 0`
  - Removed all dead code from first attempt

- `app/src/test/java/com/soyvictorherrera/scorecount/domain/calculator/ScoreCalculatorTest.kt`
  - Updated ALL 6 serve rotation tests to match correct behavior
  - Each test now properly documents when rotation occurs
  - All 113 tests passing

### Key Lessons Learned
1. **Manual testing is essential** - Unit tests passed but behavior was still wrong
2. **Understand the UX** - The indicator shows FUTURE state, not current state
3. **Question your assumptions** - I thought I "fixed" it but just rewrote the same bug
4. **Simpler is better** - The correct solution is literally `totalPoints % interval == 0`

### Testing
- ✅ All 113 unit tests pass
- ✅ Manual testing confirmed: After 2 points, indicator switches to P2
- ✅ Works with any "Set to" value (7, 11, 21)
- ✅ Works with any "Serve rotation after" value (2, 3, 5)
- ✅ Deuce transitions work correctly

### PR Feedback Addressed
- Consolidated 3 similar test cases into helper function `testServeRotationPattern()`
- Reduced test count from 119 to 113 while maintaining coverage
- All tests still passing

### Status
✅ **COMPLETE - Manually tested and confirmed working**

### Final Outcome
- Bug fixed with simple, correct formula: `totalPoints % interval == 0`
- PR #46 ready for approval
- User confirmed manual testing shows issue is resolved

---

## Previous Work: Fixed serve rotation bug (Task #42 - Done, Round 2)
