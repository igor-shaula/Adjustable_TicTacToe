package gameLogic

import attt.Player
import constants.*
import geometry.Line
import geometry.abstractions.Coordinates
import geometry.abstractions.OneMoveProcessing
import geometry.conceptXY.NearestAreaScanWithXY
import players.PlayerModel
import utilities.Log

/**
 * represents the area/space where all players' marks are placed and exist through one active game session
 */
internal class GameField(
    sideLength: Int // the only required parameter, by the way it's impossible to add private setter here
) {
    internal var sideLength = 42 // for some specifics of Kotlin this value is correctly set only inside init-block
        private set(value) { // I'm doing this for prevent from changing anywhere outside this class
            // here we're applying all possible corrections that may be needed to keep the game rules reasonable
            field = if (value > MAX_GAME_FIELD_SIDE_SIZE) MAX_GAME_FIELD_SIDE_SIZE
            else if (value < MIN_GAME_FIELD_SIDE_SIZE) MIN_GAME_FIELD_SIDE_SIZE
            else value
            Log.pl("sideLength setter: initial value = $value, assigned to the field: $field")
        }

    init {
        this.sideLength = sideLength // this is not obvious but absolutely needed here - proven by tests
    }

    // let's NOT write default marks into the initial field for the game - to save memory & speed-up a new game start
    private val theMap: MutableMap<Coordinates, Player> = mutableMapOf() // initially empty to save memory

    /**
     * returns beautiful & simple String representation of the current state of game field
     */
    internal fun prepareForPrinting3dIn2d(
        chosenAlgorithm: OneMoveProcessing = NearestAreaScanWithXY(this), // this default value works for tests
        zAxisSize: Int = 1, // this default value works for tests
        givenMap: MutableMap<Coordinates, Player> = theMap
    ): String = buildString {
        for (y in 0 until sideLength) {
            append(SYMBOL_FOR_NEW_LINE)
            for (z in 0 until zAxisSize) { // will work only once for 2D
                for (x in 0 until sideLength) {
                    append(givenMap[chosenAlgorithm.getCoordinatesFor(x, y, z)]?.symbol ?: SYMBOL_FOR_ABSENT_MARK)
                        .append(SYMBOL_FOR_DIVIDER) // between adjacent marks inside one field slice
                }
                repeat(2) { append(SYMBOL_FOR_DIVIDER) } // between the fields for each slice of every Z axis value
            }
        }
    }

    internal fun prepareForPrintingPlayerLines(
        player: Player,
        allExistingLinesForThisPlayer: MutableSet<Line?>?,
        chosenAlgorithm: OneMoveProcessing,
        zAxisSize: Int,
    ): String {
        val onePlayerMap: MutableMap<Coordinates, Player> = mutableMapOf() // initially empty to save memory
        allExistingLinesForThisPlayer?.forEach { line ->
            line?.marks?.forEach { mark ->
                onePlayerMap[mark] = player
            }
        }
        return prepareForPrinting3dIn2d(chosenAlgorithm, zAxisSize, onePlayerMap)
    }

    fun prepareTheWinningLineForPrinting(
        winningLine: Line, chosenAlgorithm: OneMoveProcessing, zAxisSize: Int
    ): String {
        val onePlayerMap: MutableMap<Coordinates, Char> = mutableMapOf() // initially empty to save memory
        winningLine.marks.forEach { mark -> // each mark has coordinates relevant to chosenAlgorithm
            onePlayerMap[mark] = SYMBOL_FOR_FULL_BLOCK
        }
        return buildString {
            for (y in 0 until sideLength) {
                append(SYMBOL_FOR_NEW_LINE)
                for (z in 0 until zAxisSize) { // will work only once for 2D
                    for (x in 0 until sideLength) {
                        append(onePlayerMap[chosenAlgorithm.getCoordinatesFor(x, y, z)] ?: SYMBOL_FOR_ABSENT_MARK)
                            .append(SYMBOL_FOR_DIVIDER) // between adjacent marks inside one field slice
                    }
                    repeat(2) { append(SYMBOL_FOR_DIVIDER) } // between the fields for each slice of every Z axis value
                }
            }
        }
    }

    /**
     * allows to see what's inside this game field space for the given coordinates
     */
    internal fun getCurrentMarkAt(coordinates: Coordinates): Player? = theMap[coordinates]

    internal fun containsTheSameMark(what: Player?, potentialSpot: Coordinates) = what == theMap[potentialSpot]

    internal fun belongToTheSameRealPlayer(givenPlace: Coordinates, potentialSpot: Coordinates): Boolean {
        val newMark = theMap[potentialSpot] // optimization to do finding in map only once
        return newMark != null && newMark != PlayerModel.None && newMark == theMap[givenPlace]
    }

    /**
     * ensures that the game field has correct size & is clear, so it is safe to use it for a new game
     */
    internal fun isReady(): Boolean =
        sideLength in MIN_GAME_FIELD_SIDE_SIZE..MAX_GAME_FIELD_SIDE_SIZE && theMap.isEmpty()

    internal fun placeNewMark(where: Coordinates, what: Player): Boolean =
        if (theMap[where] == null || theMap[where] == PlayerModel.None) { // why None? - to ensure all cases coverage
            theMap[where] = what
            true // new mark is successfully placed
        } else {
            Log.pl("attempting to set a mark for player $what on the occupied coordinates: $where")
            // later we can also emit a custom exception here - to be caught on the UI side and ask for another point
            false // new mark is not placed because the space has been already occupied
        }

    fun isCompletelyOccupied(is3D: Boolean): Boolean {
        Log.pl("isCompletelyOccupied: theMap.size = ${theMap.size}")
        val maxNumberOfSpaces = if (is3D) {
            sideLength * sideLength * sideLength
        } else {
            sideLength * sideLength
        }
        Log.pl("maxNumberOfSpaces = $maxNumberOfSpaces")
        return theMap.size >= maxNumberOfSpaces
    }
}
