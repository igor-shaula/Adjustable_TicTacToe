import GameEngine.getTheNextSafeSpotFor
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

internal fun checkTheNextSpotDetectionBlock(startSpot: Coordinates) {
    println("\ncheckTheNextSpotDetectionBlock for given spot: $startSpot:")
    checkTheNextSpotDetectionForLineDirection(startSpot, LineDirection.XmY0)
    checkTheNextSpotDetectionForLineDirection(startSpot, LineDirection.XpY0)
    checkTheNextSpotDetectionForLineDirection(startSpot, LineDirection.X0Ym)
    checkTheNextSpotDetectionForLineDirection(startSpot, LineDirection.X0Yp)
    checkTheNextSpotDetectionForLineDirection(startSpot, LineDirection.XmYm)
    checkTheNextSpotDetectionForLineDirection(startSpot, LineDirection.XpYp)
    checkTheNextSpotDetectionForLineDirection(startSpot, LineDirection.XmYp)
    checkTheNextSpotDetectionForLineDirection(startSpot, LineDirection.XpYm)
    checkTheNextSpotDetectionForLineDirection(startSpot, LineDirection.None)
}

internal fun checkTheNextSpotDetectionForLineDirection(startSpot: Coordinates, direction: LineDirection) {
    val nextSpot = getTheNextSafeSpotFor(startSpot, direction)
    println("nextSpot on 3x3 field for $direction is $nextSpot")
    when {
        // lowest limit for X axis
        startSpot.x == 0 && (direction == LineDirection.XmYm || direction == LineDirection.XmY0 || direction == LineDirection.XmYp)
        -> assertEquals(Border, nextSpot)
        // lowest limit for Y axis
        startSpot.y == 0 && (direction == LineDirection.XmYm || direction == LineDirection.X0Ym || direction == LineDirection.XpYm)
        -> assertEquals(Border, nextSpot)
        // highest limit for X axis
        startSpot.x == 2 && (direction == LineDirection.XpYm || direction == LineDirection.XpY0 || direction == LineDirection.XpYp)
        -> assertEquals(Border, nextSpot)
        // highest limit for Y axis
        startSpot.y == 2 && (direction == LineDirection.XmYp || direction == LineDirection.X0Yp || direction == LineDirection.XpYp)
        -> assertEquals(Border, nextSpot)
        // right in the center -> adjacent spot exists in any direction
        startSpot.x == 1 && startSpot.y == 1
        -> {
            assertNotEquals(Border, nextSpot)
            assertTrue(nextSpot is Coordinates)
        }
    }
}

internal fun prepareClassic3x3GameField() {
    val gameField = GameField(3)
    val gameRules = GameRules(3)
    GameEngine.prepare(gameField, gameRules)
    println("\nprepareClassic3x3GameField: gameEngine ready with given field: ${gameField.print2dGameField()}")
}
