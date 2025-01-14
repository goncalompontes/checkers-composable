package isel.tds.checkers.ttt.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import isel.tds.checkers.ttt.model.*

@Composable
fun StatusBar(board: Board?, you: TeamType?, name: Name?) {
    Row(modifier = Modifier.width(GRID_WIDTH)
        .background(Color.LightGray),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ){
        name?.let {
            Text("Game:${name}", style = MaterialTheme.typography.h5)
            Spacer(Modifier.width(30.dp))
        }
        you?.let{
            Text("You: ${you.name}", style = MaterialTheme.typography.h5)
            Spacer(Modifier.width(30.dp))
        }
        val text = when(board){
            null -> "Start a new game"
            is Board.BoardRun -> if (you == board.turn.team) "Your turn" else "Waiting..."
            is Board.BoardWin -> "Winner: ${board.winner}"
            is Board.BoardDraw -> "Draw"
        }
        Text(text, fontSize = 32.sp)
    }
}