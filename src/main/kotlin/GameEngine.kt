/**
 * this class is the main contacting point for any game UI.
 * as any UI is meant to use only one game logic - let it be a singleton.
 */
@Suppress("unused")
object GameEngine {

    // let's not consume RAM with game objects until the game is not yet started - that's why these are nullable
    internal var gameField: GameField = GameField(MIN_GAME_FIELD_SIDE_SIZE)
    private var gameRules: GameRules = GameRules(MIN_WINNING_LINE_LENGTH)

    // -------
    // region PUBLIC API

    /**
     * create & provide the UI with a new game field, adjustability starts here - in the parameters
     */
    fun prepare(
        newGameField: GameField, newGameRules: GameRules
    ) {
        clear() // for all possible resources that could be used previously
        gameField = newGameField
        gameRules = newGameRules
    }

    // stop right now, count the achieved score for all players and show the result
    @Suppress("MemberVisibilityCanBePrivate")
    fun finish() {
        // todo: count and show the score here - a bit later
        Log.pl("the game is finished in the given state: ${gameField.print2dGameField()}")
        clear()
    }

    /*
        @Suppress("MemberVisibilityCanBePrivate")
        fun save() { // todo: here we might specify a filename
            // later
        }

        @Suppress("MemberVisibilityCanBePrivate")
        fun restore() { // todo: specify what exactly to restore, a file for example
            // later
        }
    */

    // this function is actually the single place for making moves and thus changing the game field
    fun makeNewMove(where: Coordinates, what: WhichPlayer) {
        if (gameField.placeNewDot(where, what)) {
            // analyze this new dot & detect if it creates or changes any lines
            val lineDirection = checkNewDotArea(where, what)
            Log.pl("makeNewMove: detected existing line in direction: $lineDirection")
            if (lineDirection != LineDirection.None) {
                // here we already have a detected line of 2 minimum dots, now let's measure its full potential length
                // we also have a proven placed dot of the same player in the detected line direction
                // so, we only have to inspect next potential dot of the same direction -> let's prepare the coordinates:
                val checkedNearCoordinates = getTheNextSafeSpotFor(where, lineDirection)
                if (checkedNearCoordinates is Coordinates) {
                    val lineTotalLength =
                        measureLineFrom(checkedNearCoordinates, lineDirection, 2) +
                                measureLineFrom(where, lineDirection.opposite(), 0)
                    Log.pl("makeNewMove: lineTotalLength = $lineTotalLength")
                    updateGameScore(what, lineTotalLength)
                } // else checkedNearCoordinates cannot be Border or anything else from Coordinates type
            }
        }
    }

    fun isRunning() = gameField.theMap.isNotEmpty()

    // needed for UI to draw current state of the game, or simply to update the UI before making a new move
    fun getCurrentField() = gameField.theMap

    // endregion PUBLIC API
    // --------
    // region ALL PRIVATE

    // immediately clear if anything is running at the moment
    private fun clear() {
        gameField.clear()
    }

    private fun checkNewDotArea(where: Coordinates, what: WhichPlayer): LineDirection {
        val x = where.x
        val y = where.y
        val minIndex = gameField.minIndex
        val maxIndex = gameField.maxIndex
        return when {
            x > minIndex && gameField.theMap[Coordinates(x - 1, y)] == what -> LineDirection.XmY0
            x < maxIndex && gameField.theMap[Coordinates(x + 1, y)] == what -> LineDirection.XpY0
            y > minIndex && gameField.theMap[Coordinates(x, y - 1)] == what -> LineDirection.X0Ym
            y < maxIndex && gameField.theMap[Coordinates(x, y + 1)] == what -> LineDirection.X0Yp
            x > minIndex && y > minIndex && gameField.theMap[Coordinates(x - 1, y - 1)] == what -> LineDirection.XmYm
            x < maxIndex && y < maxIndex && gameField.theMap[Coordinates(x + 1, y + 1)] == what -> LineDirection.XpYp
            x > minIndex && y < maxIndex && gameField.theMap[Coordinates(x - 1, y + 1)] == what -> LineDirection.XmYp
            x < maxIndex && y > minIndex && gameField.theMap[Coordinates(x + 1, y - 1)] == what -> LineDirection.XpYm
            else -> LineDirection.None
        }
    }

    internal fun measureLineFrom(start: Coordinates, lineDirection: LineDirection, startingLength: Int): Int {
        Log.pl("measureLineFrom: startingLength: $startingLength")
        // firstly measure in the given direction and then in the opposite, also recursively
        val nextCoordinates = getTheNextSafeSpotFor(start, lineDirection)
        Log.pl("measureLineFrom: start coordinates: $start")
        Log.pl("measureLineFrom: next coordinates: $nextCoordinates")
        return if (nextCoordinates is Coordinates && gameField.theMap[nextCoordinates] == gameField.theMap[start]) {
            measureLineFrom(nextCoordinates, lineDirection, startingLength + 1)
        } else {
            Log.pl("measureLineFrom: ELSE -> exit: $startingLength")
            startingLength
        }
    }

    internal fun getTheNextSafeSpotFor(start: Coordinates, lineDirection: LineDirection): GameSpace {
        @Suppress("SimplifyBooleanWithConstants")
        when {
            false || // just for the following cases' alignment
                    start.x <= gameField.minIndex && lineDirection == LineDirection.XmYm ||
                    start.x <= gameField.minIndex && lineDirection == LineDirection.XmY0 ||
                    start.x <= gameField.minIndex && lineDirection == LineDirection.XmYp ||
                    start.y <= gameField.minIndex && lineDirection == LineDirection.XmYm ||
                    start.y <= gameField.minIndex && lineDirection == LineDirection.X0Ym ||
                    start.y <= gameField.minIndex && lineDirection == LineDirection.XpYm ||
                    start.x >= gameField.maxIndex && lineDirection == LineDirection.XpYm ||
                    start.x >= gameField.maxIndex && lineDirection == LineDirection.XpY0 ||
                    start.x >= gameField.maxIndex && lineDirection == LineDirection.XpYp ||
                    start.y >= gameField.maxIndex && lineDirection == LineDirection.XmYp ||
                    start.y >= gameField.maxIndex && lineDirection == LineDirection.X0Yp ||
                    start.y >= gameField.maxIndex && lineDirection == LineDirection.XpYp ->
                return Border
        }
        return Coordinates(x = start.x + lineDirection.dx, y = start.y + lineDirection.dy)
    }

    private fun updateGameScore(whichPlayer: WhichPlayer, detectedLineLength: Int) {
        if (gameRules.isGameWon(detectedLineLength)) {
            Log.pl("player $whichPlayer wins with detectedLineLength: $detectedLineLength")
            finish()
        }
    }

    // endregion ALL PRIVATE
}
