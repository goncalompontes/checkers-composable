package isel.tds.checkers

import androidx.compose.foundation.layout.Column
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.*
import isel.tds.checkers.ttt.view.*
import isel.tds.checkers.ttt.viewmodel.AppViewModel

@Composable
@Preview
fun FrameWindowScope.GridApp(onExit: () -> Unit) {

    val scope = rememberCoroutineScope()
    var vm: AppViewModel = remember { AppViewModel(scope) }

    MaterialTheme {
        MenuBar {
            Menu("Game") {
                Item("Start game", onClick = vm::openStartDialog)
                Item("Join game", onClick = vm::openJoinDialog)
                Item("Refresh", enabled = vm.hasClash, onClick = vm::refresh)
                Item("Exit", onClick = onExit)
            }
        }
        Column() {
            GridView(vm.board, vm.possibleMoves, onClickCell = vm::tryPlay)
            StatusBar(vm.board, vm.sidePlayer, vm.name)
        }

        vm.inputName?.let {
            StartOrJoinDialog(
                type = it,
                onCancel = vm::closeStartOrJoinDialog,
                onAction = vm::joinOrStart
            )
        }
        vm.errorMessage?.let { ErrorDialog(it, onClose = vm::hideError) }
    }
}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        state = WindowState(size = DpSize.Unspecified),
        title = "Checkers"
    ) {
        GridApp(::exitApplication)
    }
}