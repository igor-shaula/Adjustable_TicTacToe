package attt

import constants.MIN_GAME_FIELD_SIDE_SIZE
import constants.MIN_NUMBER_OF_PLAYERS
import constants.MIN_WINNING_LINE_LENGTH
import gameLogic.GameSession

interface Game {

    /**
     * the only action for a selected/next player during the active game, this one is the most precise form
     */
    fun makeMove(x: Int, y: Int, z: Int = 0): Player

    /**
     * the same as makeMove() with 3 arguments, but this one is much more convenient for use in Java code
     */
    fun makeMove(x: Int, y: Int): Player = makeMove(x, y, 0)

    /**
     * the same as makeMove() - this reduction is made for convenience as this method is the most frequently used
     */
    fun m(x: Int, y: Int, z: Int = 0): Player = makeMove(x, y, z)

    /**
     * the same as m() with 3 arguments, but this one is much more convenient for use in Java code
     */
    fun m(x: Int, y: Int): Player = makeMove(x, y, 0)

    fun getLeader(): Player

    fun getWinner(): Player

    fun isGameWon(): Boolean

    fun isGameFinished(): Boolean

    /**
     * represents the current game field state in an associative way (as a dictionary or map).
     * Triple<Int, Int, Int> represents coordinates - X, Y, Z in this order (the same as for makeMove() method).
     */
    fun getCurrentFieldAsMapOfTriples(): Map<Triple<Int, Int, Int>, Player>

    fun getCurrentFieldAsMapOfPairs(z: Int = 0): Map<Pair<Int, Int>, Player>

    fun getCurrentFieldIn2dAsAString(): String

    fun printCurrentFieldIn2d()

    fun getExistingLinesForGivenPlayer(player: Player): List<List<Triple<Int, Int, Int>>>

    fun getExistingLinesForGivenPlayerAsAString(player: Player): String

    fun printExistingLinesForGivenPlayer(player: Player)

    fun getExistingLinesForLeadingPlayer(): List<List<Triple<Int, Int, Int>>>

    fun getExistingLinesForLeadingPlayerAsAString(): String

    fun printExistingLinesForLeadingPlayer()

    fun getExistingLinesForTheWinner(): List<List<Triple<Int, Int, Int>>>

    fun getExistingLinesForTheWinnerAsAString(): String

    fun printExistingLinesForTheWinner()

    fun getTheWinningLineAsListOfTriples(): List<Triple<Int, Int, Int>>

    fun getTheWinningLineAsAString(): String

    fun printTheWinningLine()

    companion object {
        /**
         * create AtttGame instance & provide the UI with a new game field, adjustability happens here - in the parameters
         */
        fun create(
            is3D: Boolean = false,
            desiredFieldSize: Int = MIN_GAME_FIELD_SIDE_SIZE,
            desiredMaxLineLength: Int = MIN_WINNING_LINE_LENGTH,
            desiredPlayerNumber: Int = MIN_NUMBER_OF_PLAYERS
        ): Game = GameSession(is3D, desiredFieldSize, desiredMaxLineLength, desiredPlayerNumber)

        /**
         * the shortest ever way to get the game ready
         */
        fun create(): Game =
            GameSession(false, MIN_GAME_FIELD_SIDE_SIZE, MIN_WINNING_LINE_LENGTH, MIN_NUMBER_OF_PLAYERS)

        /**
         * convenient for getting the game field of bigger size
         */
        fun create(desiredFieldSize: Int = MIN_GAME_FIELD_SIDE_SIZE): Game =
            GameSession(false, desiredFieldSize, MIN_WINNING_LINE_LENGTH, MIN_NUMBER_OF_PLAYERS)

        /**
         * more precise adjustment of winning condition for configurable game field
         */
        fun create(
            desiredFieldSize: Int = MIN_GAME_FIELD_SIDE_SIZE, desiredMaxLineLength: Int = MIN_WINNING_LINE_LENGTH
        ): Game = GameSession(false, desiredFieldSize, desiredMaxLineLength, MIN_NUMBER_OF_PLAYERS)

        /**
         * the shortest ever way to get the game ready in 3D
         */
        fun create(is3D: Boolean = false): Game =
            GameSession(is3D, MIN_GAME_FIELD_SIDE_SIZE, MIN_WINNING_LINE_LENGTH, MIN_NUMBER_OF_PLAYERS)

        /**
         * convenient for getting the game field of bigger size in 3D
         */
        fun create(
            is3D: Boolean = false, desiredFieldSize: Int = MIN_GAME_FIELD_SIDE_SIZE
        ): Game =
            GameSession(is3D, desiredFieldSize, MIN_WINNING_LINE_LENGTH, MIN_NUMBER_OF_PLAYERS)

        /**
         * more precise adjustment of winning condition for configurable 3D game field
         */
        fun create(
            is3D: Boolean = false,
            desiredFieldSize: Int = MIN_GAME_FIELD_SIDE_SIZE,
            desiredMaxLineLength: Int = MIN_WINNING_LINE_LENGTH
        ): Game = GameSession(is3D, desiredFieldSize, desiredMaxLineLength, MIN_NUMBER_OF_PLAYERS)
    }
}
