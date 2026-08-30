package com.berend.rssreader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.berend.rssreader.ui.ArticleScreen
import com.berend.rssreader.ui.ArticlesScreen
import com.berend.rssreader.ui.FeedsScreen
import com.mudita.mmd.ThemeMMD

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ThemeMMD {
                ReaderApp()
            }
        }
    }
}

@Composable
fun ReaderApp(viewModel: ReaderViewModel = viewModel()) {
    var openedArticle by remember { mutableStateOf<Article?>(null) }

    Box(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
        val article = openedArticle
        when {
            article != null -> ArticleScreen(article = article, onBack = { openedArticle = null })
            viewModel.openedFeed != null -> ArticlesScreen(
                viewModel = viewModel,
                onOpenArticle = { openedArticle = it },
                onBack = { viewModel.closeFeed() },
            )
            else -> FeedsScreen(viewModel = viewModel)
        }
    }
}
