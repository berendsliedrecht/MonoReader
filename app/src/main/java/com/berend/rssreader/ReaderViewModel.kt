package com.berend.rssreader

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.text.HtmlCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.prof18.rssparser.RssParser
import kotlinx.coroutines.launch

/** A subscribed feed. Title is filled in from the feed itself; url is the key. */
data class Feed(val url: String, val title: String)

/** A display-ready article: HTML already flattened to plain text. */
data class Article(val title: String, val date: String, val body: String)

class ReaderViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("reader", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val parser = RssParser()

    var feeds by mutableStateOf<List<Feed>>(emptyList()); private set
    var addError by mutableStateOf<String?>(null); private set
    var isAdding by mutableStateOf(false); private set

    var openedFeed by mutableStateOf<Feed?>(null); private set
    var articles by mutableStateOf<List<Article>>(emptyList()); private set
    var isLoading by mutableStateOf(false); private set
    var loadError by mutableStateOf<String?>(null); private set

    init {
        feeds = runCatching {
            gson.fromJson(prefs.getString("feeds", null), Array<Feed>::class.java)?.toList()
        }.getOrNull().orEmpty()
    }

    /** Fetch the feed once to confirm it parses and to read its title, then subscribe. */
    fun addFeed(rawUrl: String) {
        val url = normalizeUrl(rawUrl)
        if (url.isBlank() || feeds.any { it.url == url }) return
        viewModelScope.launch {
            isAdding = true
            addError = null
            runCatching { parser.getRssChannel(url) }
                .onSuccess { channel ->
                    val title = channel.title?.trim().orEmpty().ifBlank { url }
                    feeds = feeds + Feed(url, title)
                    saveFeeds()
                }
                .onFailure { addError = "Could not load feed: ${it.message ?: "unknown error"}" }
            isAdding = false
        }
    }

    fun removeFeed(feed: Feed) {
        feeds = feeds.filterNot { it.url == feed.url }
        saveFeeds()
    }

    fun openFeed(feed: Feed) {
        openedFeed = feed
        articles = emptyList()
        loadFeed(feed)
    }

    fun closeFeed() {
        openedFeed = null
        articles = emptyList()
        loadError = null
    }

    fun refresh() {
        openedFeed?.let(::loadFeed)
    }

    private fun loadFeed(feed: Feed) {
        viewModelScope.launch {
            isLoading = true
            loadError = null
            runCatching { parser.getRssChannel(feed.url) }
                .onSuccess { channel ->
                    articles = channel.items.map { item ->
                        Article(
                            title = item.title?.trim().orEmpty().ifBlank { "(untitled)" },
                            date = item.pubDate?.trim().orEmpty(),
                            body = htmlToText(item.content ?: item.description.orEmpty()),
                        )
                    }
                }
                .onFailure {
                    loadError = it.message ?: "Failed to load feed"
                    articles = emptyList()
                }
            isLoading = false
        }
    }

    private fun saveFeeds() {
        prefs.edit().putString("feeds", gson.toJson(feeds)).apply()
    }

    private fun normalizeUrl(raw: String): String {
        val trimmed = raw.trim()
        if (trimmed.isBlank()) return ""
        return if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) trimmed
        else "https://$trimmed"
    }

    private fun htmlToText(html: String): String =
        HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT).toString().trim()
}
