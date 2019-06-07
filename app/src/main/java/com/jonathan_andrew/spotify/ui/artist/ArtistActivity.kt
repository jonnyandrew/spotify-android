package com.jonathan_andrew.spotify.ui.artist

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.jonathan_andrew.spotify.App
import com.jonathan_andrew.spotify.R
import com.jonathan_andrew.spotify.data.artists.ArtistApi
import com.jonathan_andrew.spotify.data.artists.LocalArtistsRepository
import com.jonathan_andrew.spotify.data.artists.ProxyArtistsRepository
import com.jonathan_andrew.spotify.data.artists.RemoteArtistsRepository
import com.jonathan_andrew.spotify.data.search.SearchApi
import com.jonathan_andrew.spotify.data.util.network.ConnectivityChangeReceiver
import com.jonathan_andrew.spotify.domain.use_cases.artists.GetArtistUseCase
import com.jonathan_andrew.spotify.ui.MviView
import com.jonathan_andrew.spotify.ui.util.gone
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import io.reactivex.rxkotlin.merge
import kotlinx.android.synthetic.main.activity_artist.*

class ArtistActivity : AppCompatActivity(), MviView<ArtistUiEvent, ArtistUiModel> {
    companion object {
        val ARG_ARTIST_ID = "arg_artist_id"
        fun startActivity(context: Context, artistId: String) {
            val intent = Intent(context, ArtistActivity::class.java).apply {
                putExtra(ARG_ARTIST_ID, artistId)
            }
            context.startActivity(intent)
        }
    }

    private val connectivityReceiver = ConnectivityChangeReceiver()

    private val presenter by lazy {
        val searchApi = SearchApi(App.instance.httpClient)
        val artistApi = ArtistApi(App.instance.httpClient)

        ArtistMviPresenter(this,
                GetArtistUseCase(
                        ProxyArtistsRepository(
                                LocalArtistsRepository(),
                                RemoteArtistsRepository(artistApi, searchApi)
                        )
                )
        )
    }

    private val artistId by lazy {
        intent.extras.getString(ARG_ARTIST_ID)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_artist)
        presenter.begin(this)
    }

    override val events: Observable<ArtistUiEvent>
            by lazy {
                listOf(
                        networkChanges
                ).merge()
                        .startWith(ArtistUiEvent.Load(artistId))
                        .cast(ArtistUiEvent::class.java)
            }

    override fun setUiModel(model: ArtistUiModel) {
        progressBar.gone = !model.loading
        status.gone = !(model.loading || model.error)
        status.setText(model.status)
        content.gone = model.loading || model.error
        name.text = model.name
        popularity.setText(model.popularity)
        genres.text = model.genre
        followers.text = getString(R.string.artist_followers, model.followers)
        image.gone = model.imageUrl == null
        if (model.imageUrl != null) {
            Picasso.with(this).load(model.imageUrl).into(image)
        }
        if (!model.loggedIn) {
            finish()
        }
    }

    private val networkChanges by lazy {
        connectivityReceiver.networkStates.filter { it == true }
                .firstElement()
                .map { ArtistUiEvent.Load(artistId) }
                .toObservable()
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
        super.onDestroy()
    }
}
