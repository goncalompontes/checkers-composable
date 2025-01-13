package isel.tds.checkers

import isel.tds.checkers.storage.TextFileStorage
import isel.tds.checkers.ttt.model.*
import isel.tds.checkers.ttt.ui.*

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
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
            GridView(vm.board?.pieces, onClickCell = vm::play)
            StatusBar(vm.board, vm.sidePlayer)
        }

        vm.inputName?.let {
            StartOrJoinDialog(
                type = it,
                onCancel = vm::closeStartOrJoinDialog,
                onAction= if (it== InputName.ForStart) vm::start else vm::join
            ) }
        vm.errorMessage?.let { ErrorDialog(it, onClose = vm::hideError) }
        if (vm.isWaiting) waitingIndicator()
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}