package com.sirrlevi.whatsupp.screenshots

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.sirrlevi.whatsupp.ui.LocalWaPalette
import com.sirrlevi.whatsupp.ui.WaBubble
import com.sirrlevi.whatsupp.ui.WaChatItem
import com.sirrlevi.whatsupp.ui.WaChatScaffold
import com.sirrlevi.whatsupp.ui.WaDensityRoot
import com.sirrlevi.whatsupp.ui.WaDoodleWallpaper
import com.sirrlevi.whatsupp.ui.WaInputBar
import com.sirrlevi.whatsupp.ui.WaMessageList
import com.sirrlevi.whatsupp.ui.WaPalettes
import com.sirrlevi.whatsupp.ui.WaQuote
import com.sirrlevi.whatsupp.ui.WaScrollFab
import com.sirrlevi.whatsupp.ui.WaTopBar
import com.sirrlevi.whatsupp.ui.px
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File

/**
 * Draws the chat screen with made-up sample text into a 1080 x 2408 picture (the size of the reference phone), so the
 * layout can be compared with the reference screenshots. Only runs in the screenshots workflow.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = "w270dp-h602dp-640dpi")
class ChatRenderTest {
    @get:Rule
    val rule = createComposeRule()

    private fun render(name: String, content: @Composable () -> Unit) {
        // Drive the clock by hand: nothing may wait for "idle", so an endless animation cannot hang the test.
        rule.mainClock.autoAdvance = false
        rule.setContent {
            WaDensityRoot {
                CompositionLocalProvider(LocalWaPalette provides WaPalettes.Dark) { content() }
            }
        }
        repeat(8) { rule.mainClock.advanceTimeByFrame() }
        rule.mainClock.advanceTimeBy(400)
        val bitmap = rule.onRoot().captureToImage().asAndroidBitmap()
        val dir = File(System.getProperty("user.dir"), "build/ui-renders").apply { mkdirs() }
        File(dir, "$name.png").outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        println("RENDERED $name ${bitmap.width}x${bitmap.height}")
    }

    private fun msg(
        id: String,
        outgoing: Boolean,
        text: String,
        time: String,
        gap: Float,
        tail: Boolean,
        status: String = "READ",
        quote: WaQuote? = null,
        reactions: List<String> = emptyList(),
        edited: Boolean = false
    ) = WaChatItem.Msg(
        WaBubble(id, outgoing, text, "TEXT", null, time, status, edited, reactions, quote, tail, gap)
    )

    private val longText =
        "I was thinking we could try the new place near the station. They have a nice rooftop and the food " +
            "is supposed to be really good, but we should book a table first."

    private val items = listOf(
        WaChatItem.Day("Today"),
        msg("1", false, "Hey, are you free this evening?", "6:41 pm", 27.5f, true),
        msg("2", true, "Yes, after seven works for me", "6:42 pm", 27.5f, true),
        msg("3", true, "Where should we meet?", "6:42 pm", 4f, false, status = "DELIVERED"),
        msg("4", false, longText, "6:44 pm", 27.5f, true),
        msg("5", true, "Sounds perfect, I will call them now", "6:45 pm", 27.5f, true, quote = WaQuote("Sam", false, longText)),
        msg("6", false, "Great, see you there", "6:46 pm", 27.5f, true, reactions = listOf("👍")),
        msg("7", true, "Ok", "6:47 pm", 27.5f, true, status = "SENT"),
        msg("8", true, "Running ten minutes late, sorry about that. Start without me if you are hungry", "6:58 pm", 4f, false, edited = true)
    )

    @Test
    fun a_plainBox() {
        render("a_plain") { Box(Modifier.fillMaxSize().background(Color(0xFF030708))) }
    }

    @Test
    fun b_wallpaper() {
        render("b_wallpaper") { WaDoodleWallpaper(Modifier.fillMaxSize()) }
    }

    @Test
    fun c_topBar() {
        render("c_topbar") {
            Box(Modifier.fillMaxSize().background(Color(0xFF030708))) { sampleTopBar() }
        }
    }

    @Test
    fun d_inputBar() {
        render("d_inputbar") {
            Box(Modifier.fillMaxSize().background(Color(0xFF030708))) {
                Box(Modifier.align(Alignment.BottomStart)) { WaInputBar("", {}, {}, {}, {}, {}) }
            }
        }
    }

    @Test
    fun e_messages() {
        render("e_messages") {
            Box(Modifier.fillMaxSize().background(Color(0xFF030708))) {
                WaMessageList(items, rememberLazyListState(), {}, Modifier.fillMaxSize())
            }
        }
    }

    @Test
    fun f_chatScreenDark() {
        render("chat_dark") {
            WaChatScaffold(
                topInset = Modifier.height(87.px),
                bottomInset = Modifier,
                topBar = { sampleTopBar() },
                list = { mod -> WaMessageList(items, rememberLazyListState(), {}, mod) },
                inputBar = { WaInputBar("", {}, {}, {}, {}, {}) },
                overlay = {
                    WaScrollFab(
                        2, {},
                        Modifier.align(Alignment.BottomEnd).padding(end = 33.25f.px, bottom = 51.75f.px)
                    )
                }
            )
        }
    }

    @Composable
    private fun sampleTopBar() {
        WaTopBar(
            name = "Sam Taylor",
            subtitle = null,
            onBack = {}, onProfile = {}, onVideo = {}, onCall = {}, onMore = {},
            avatar = { mod -> Box(mod.background(Color(0xFF5B6B73))) }
        )
    }
}
