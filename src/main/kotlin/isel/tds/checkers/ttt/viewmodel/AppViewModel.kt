package isel.tds.checkers.ttt.viewmodel

import androidx.compose.runtime.*
import isel.tds.checkers.storage.TextFileStorage
import isel.tds.checkers.ttt.model.*
import isel.tds.checkers.ttt.view.InputName
import kotlinx.coroutines.*

class AppViewModel(val scope: CoroutineScope) {

    private val storage = TextFileStorage<Name, Game>("games", GameSerializer)

    var clash by mutableStateOf(Clash(storage))
    var inputName by mutableStateOf<InputName?>(null) //StartOrJoinDialog
        private set                                   //  state
    var errorMessage by mutableStateOf<String?>(null) //ErrorDialog state
        private set
    var waitingJob by mutableStateOf<Job?>(null)
    var selectedSquare by mutableStateOf<Square?>(null)
    var possibleMoves by mutableStateOf<List<Square>?>(null)

    val isWaiting: Boolean get() = waitingJob != null

    val newAvailable: Boolean get() = clash.canNewBoard()

    private val turnAvailable: Boolean
        get() = (board as? Board.BoardRun)?.turn?.team == sidePlayer || newAvailable

    val board: Board? get() = (clash as? ClashRun)?.game?.board
    val hasClash: Boolean get() = clash is ClashRun
    val sidePlayer: TeamType? get() = (clash as? ClashRun)?.player
    val name: Name? get() = (clash as? ClashRun)?.id


    private fun exec(fx: Clash.() -> Clash): Unit =
        try {
            //throw Exception("My blow up")
            clash = clash.fx()
        } catch (e: Exception) {        // Report exceptions in ErrorDialog
            errorMessage = e.message
        }

    fun play(from: Square, to: Square) {
        exec {
            play(from, to).also {
                selectedSquare = null
                possibleMoves = null
            }
        }
        waitForOtherSide()
    }

    fun getValidMoves(from: Square?): List<Square>? {
        if (from == null) return null

        try {
            return board?.getPlays(from)
        } catch (e: Exception) {
            errorMessage = e.message
            return null
        }
    }

    private fun waitForOtherSide() {
        if (turnAvailable) return
        waitingJob = scope.launch(Dispatchers.IO) {
            do {
                delay(3000)
                try {
                    clash = clash.refresh()
                } catch (e: NoChangesException) { /* Ignore */
                } catch (e: Exception) {
                    errorMessage = e.message
                    if (e is GameDeletedException) clash = Clash(storage)
                }
            } while (!turnAvailable)
            waitingJob = null
        }
    }

    fun refresh() = exec(Clash::refresh)
    fun newBoard(): Unit = exec(Clash::newBoard)

    fun openStartDialog() {
        inputName = InputName.ForStart
    }

    fun openJoinDialog() {
        inputName = InputName.ForJoin
    }

    fun closeStartOrJoinDialog() {
        inputName = null
    }

    fun joinOrStart(name: Name) {
        closeStartOrJoinDialog()
        exec { joinOrStart(name) }
        waitForOtherSide()
    }

    fun start(name: Name) {
        closeStartOrJoinDialog()
        exec { joinOrStart(name) }
        waitForOtherSide()
    }

    fun join(name: Name) {
        closeStartOrJoinDialog()
        exec { joinOrStart(name) }
        waitForOtherSide()
    }

    fun tryPlay(square: Square?, teamType: TeamType?) {
        if (!turnAvailable) return
        
        if (square != null && isOwnSquare(square)) {
            selectedSquare = square
            possibleMoves = board?.getPlays(square)
        } else if (selectedSquare != null) {
            play(selectedSquare as Square, square as Square)
        }
    }

    fun isOwnSquare(square: Square?): Boolean {
        return (board as? Board.BoardRun)?.turn?.pieces?.contains(square) == true
    }

    fun hideError() {
        errorMessage = null
    }
}