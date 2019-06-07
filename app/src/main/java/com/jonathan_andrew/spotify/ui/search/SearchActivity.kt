package com.jonathan_andrew.spotify.ui.search

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.jakewharton.rxbinding2.widget.RxSearchView
import com.jonathan_andrew.spotify.App
import com.jonathan_andrew.spotify.R
import com.jonathan_andrew.spotify.data.artists.ArtistApi
import com.jonathan_andrew.spotify.data.artists.RemoteArtistsRepository
import com.jonathan_andrew.spotify.data.search.SearchApi
import com.jonathan_andrew.spotify.data.tracks.RemoteTracksRepository
import com.jonathan_andrew.spotify.data.util.network.ConnectivityChangeReceiver
import com.jonathan_andrew.spotify.domain.use_cases.search.SearchUseCase
import com.jonathan_andrew.spotify.login.ui.LoginActivity
import com.jonathan_andrew.spotify.ui.MviView
import com.jonathan_andrew.spotify.ui.artist.ArtistActivity
import com.jonathan_andrew.spotify.ui.gone
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.merge
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : AppCompatActivity(), MviView<SearchUiEvent, SearchUiModel> {
    private val presenter by lazy {
        val searchApi = SearchApi(App.instance.httpClient)
        val artistApi = ArtistApi(App.instance.httpClient)
        SearchMviPresenter(this,
                SearchUseCase(
                        RemoteTracksRepository(searchApi),
                        RemoteArtistsRepository(artistApi, searchApi)
                )
        )
    }

    private val recyclerAdapter = SearchRecyclerAdapter()

    private var finished = false

    private val localSubscriptions = CompositeDisposable()
    private val connectivityReceiver = ConnectivityChangeReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setupRecyclerView()
        searchInput.setIconifiedByDefault(false)
        searchInput.setQuery("Jackson", true)
        presenter.begin(this)
        localSubscriptions.add(
                recyclerAdapter.idOfArtistClicks.subscribe { artistId ->
                    navigateToArtistActivity(artistId)
                }
        )
    }


    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, SearchActivity::class.java))
        }
    }

    override val events: Observable<SearchUiEvent>
            by lazy {
                listOf(searchChanges, networkChanges)
                        .merge()
                        .cast(SearchUiEvent::class.java)
            }

    private val networkChanges by lazy {
        connectivityReceiver.networkStates.filter { it == true }
                .map {
                    SearchUiEvent.ChangeSearch(searchInput.query.toString())
                }.distinct {
            it.search
        }
    }

    private val searchChanges by lazy {
        RxSearchView.queryTextChangeEvents(searchInput)
                .map { SearchUiEvent.ChangeSearch(it.queryText().toString()) }
    }

    override fun setUiModel(model: SearchUiModel) {
        progressBar.gone = !model.loading
        status.gone = !model.loading && !model.error
        status.setText(model.status)
        list.gone = model.loading || model.error
        recyclerAdapter.tracks = model.tracks
        recyclerAdapter.artists = model.artists
        if (model.loggedOut) {
            navigateToLoginActivity()
        }
    }

    override fun onStart() {
        super.onStart()
        registerReceiver(connectivityReceiver, IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"))
        presenter.resume()
    }

    override fun onStop() {
        presenter.pause()
        unregisterReceiver(connectivityReceiver)
        super.onStop()
    }

    override fun onDestroy() {
        presenter.end()
        localSubscriptions.clear()
        super.onDestroy()
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        list.layoutManager = layoutManager
        list.adapter = recyclerAdapter
    }

    private fun navigateToLoginActivity() {
        if (finished) {
            return
        }
        finished = true
        finish()
        LoginActivity.startActivity(this)
    }

    private fun navigateToArtistActivity(artistId: String) {
        ArtistActivity.startActivity(this, artistId)
    }
}
