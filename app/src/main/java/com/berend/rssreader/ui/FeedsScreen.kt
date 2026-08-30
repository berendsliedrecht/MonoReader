package com.berend.rssreader.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.berend.rssreader.ReaderViewModel
import com.mudita.mmd.components.buttons.OutlinedButtonMMD
import com.mudita.mmd.components.divider.HorizontalDividerMMD
import com.mudita.mmd.components.text.TextMMD
import com.mudita.mmd.components.text_field.TextFieldMMD
import com.mudita.mmd.components.top_app_bar.TopAppBarMMD

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedsScreen(viewModel: ReaderViewModel) {
    var url by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBarMMD(
            title = { TextMMD("Reader", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
        )

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Spacer(modifier = Modifier.height(8.dp))
            TextFieldMMD(
                value = url,
                onValueChange = { url = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { TextMMD("Feed URL, e.g. example.com/rss") },
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButtonMMD(
                onClick = {
                    viewModel.addFeed(url)
                    url = ""
                },
                enabled = url.isNotBlank() && !viewModel.isAdding,
                modifier = Modifier.fillMaxWidth(),
            ) {
                TextMMD(if (viewModel.isAdding) "Adding..." else "Add feed", fontSize = 16.sp)
            }
            viewModel.addError?.let {
                Spacer(modifier = Modifier.height(8.dp))
                TextMMD(it, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Plain scrollable column, not LazyColumnMMD: its scrollbar crashes when the
        // keyboard shrinks the viewport to a negative height.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            if (viewModel.feeds.isEmpty()) {
                TextMMD("No feeds yet. Add one above.", fontSize = 15.sp)
            }
            viewModel.feeds.forEachIndexed { index, feed ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                ) {
                    TextMMD(
                        text = feed.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.openFeed(feed) }
                            .padding(vertical = 10.dp),
                    )
                    IconButton(onClick = { viewModel.removeFeed(feed) }) {
                        Icon(Icons.Outlined.Delete, contentDescription = "Remove ${feed.title}")
                    }
                }
                if (index < viewModel.feeds.lastIndex) HorizontalDividerMMD()
            }
        }
    }
}
