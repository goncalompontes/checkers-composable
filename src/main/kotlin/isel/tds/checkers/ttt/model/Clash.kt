package isel.tds.checkers.ttt.model

import isel.tds.checkers.storage.Storage

@JvmInline
value class Name(private val value: String) {
    init { require(isValid(value)) { "Invalid name" } }
    override fun toString() = value
    companion object {
        fun isValid(value: String) =
            value.isNotEmpty() && value.all { it.isLetterOrDigit() } && value.none { it==' ' }
    }
}

typealias GameStorage = Storage<Name, Game>

open class Clash(val storage: GameStorage) {

    fun joinOrStart(id: Name): Clash {
        var team = TeamType.BLACK
        val game = storage.read(id) ?: Game().also { storage.create(id, it); team = TeamType.WHITE }
        return ClashRun(storage, game, id, team)
    }

    private fun runOper( oper: ClashRun.()-> Game): Clash {
        check(this is ClashRun) { "Clash not started" }
        return ClashRun(storage, oper(), id, player)
    }

    fun refresh() = runOper {
        (storage.read(id) as Game).also { check(game != it) }
    }

    fun newBoard() = runOper {
        Game().also { storage.update(id,it) }
    }

    fun play(from: Square, to: Square) = runOper {
        game.play(from, to).also {
            //check(player==(game.board as Board.BoardRun).turn.team) { "Not your turn" }
            storage.update(id,it)
        }
    }

}

class ClashRun(
    st: GameStorage,
    val game: Game,
    val id: Name,
    val player: TeamType
) : Clash(st)



