package com.berend.rssreader.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.berend.rssreader.Article
import com.berend.rssreader.ReaderViewModel
import com.mudita.mmd.components.divider.HorizontalDividerMMD
import com.mudita.mmd.components.text.TextMMD
import com.mudita.mmd.components.top_app_bar.TopAppBarMMD

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticlesScreen(
    viewModel: ReaderViewModel,
    onOpenArticle: (Article) -> Unit,
    onBack: () -> Unit,
) {
    BackHandler(onBack = onBack)

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBarMMD(
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            title = {
                TextMMD(
                    text = viewModel.openedFeed?.title.orEmpty(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                )
            },
            actions = {
                IconButton(onClick = viewModel::refresh) {
                    Icon(Icons.Outlined.Refresh, contentDescription = "Refresh")
                }
            },
        )

        when {
            viewModel.isLoading -> TextMMD("Loading...", fontSize = 15.sp, modifier = Modifier.padding(16.dp))
            viewModel.loadError != null ->
                TextMMD("Error: ${viewModel.loadError}", fontSize = 15.sp, modifier = Modifier.padding(16.dp))
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            viewModel.articles.forEachIndexed { index, article ->
                val read = viewModel.isRead(article)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.markRead(article)
                            onOpenArticle(article)
                        }
                        .padding(vertical = 12.dp),
                ) {
                    TextMMD(
                        text = article.title,
                        fontSize = 17.sp,
                        // Unread stands out in bold; read settles back to regular weight
                        fontWeight = if (read) FontWeight.Normal else FontWeight.Bold,
                    )
                    if (article.date.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        TextMMD(article.date, fontSize = 12.sp)
                    }
                }
                if (index < viewModel.articles.lastIndex) HorizontalDividerMMD()
            }
        }
    }
}
