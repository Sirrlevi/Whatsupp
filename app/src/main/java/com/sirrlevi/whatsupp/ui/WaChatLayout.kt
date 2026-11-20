package com.sirrlevi.whatsupp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/** One row of the message list: a date chip or a message. */
internal sealed class WaChatItem(val key: String) {
    class Day(val label: String) : WaChatItem("day-$label")
    class Msg(val bubble: WaBubble) : WaChatItem("msg-${bubble.id}")
}

/** Scrolling list of date chips and message rows. */
@Composable
internal fun WaMessageList(
    items: List<WaChatItem>,
    state: LazyListState,
    onLongClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        state = state,
        modifier = modifier,
        contentPadding = PaddingValues(top = 12.px, bottom = 18.px)
    ) {
        items(items, key = { it.key }) { item ->
            when (item) {
                is WaChatItem.Day -> WaDateChip(item.label)
                is WaChatItem.Msg -> WaMessageRow(item.bubble, onLongClick = { onLongClick(item.bubble.id) })
            }
        }
    }
}

/**
 * The chat screen frame: wallpaper behind everything, toolbar on top, message list in the middle and the message box
 * at the bottom. [topInset] and [bottomInset] default to the real status and navigation bar heights; tests pass fixed
 * sizes instead.
 */
@Composable
internal fun WaChatScaffold(
    topBar: @Composable () -> Unit,
    list: @Composable (Modifier) -> Unit,
    inputBar: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    topInset: Modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars),
    bottomInset: Modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars),
    wallpaper: @Composable BoxScope.() -> Unit = { WaDoodleWallpaper(Modifier.matchParentSize()) },
    overlay: @Composable BoxScope.() -> Unit = {}
) {
    val p = LocalWaPalette.current
    Box(modifier.fillMaxSize().background(p.wallpaper)) {
        wallpaper()
        Column(Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxWidth().background(p.topBar)) {
                Spacer(topInset.fillMaxWidth())
                topBar()
            }
            Box(Modifier.weight(1f).fillMaxWidth()) {
                list(Modifier.fillMaxSize())
                overlay()
            }
            inputBar()
            Spacer(bottomInset.fillMaxWidth())
        }
    }
}
