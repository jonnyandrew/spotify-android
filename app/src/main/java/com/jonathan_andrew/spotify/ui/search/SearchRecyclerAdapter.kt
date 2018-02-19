package com.jonathan_andrew.spotify.ui.search

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jonathan_andrew.spotify.R
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.view_search_artist_result.view.*
import kotlinx.android.synthetic.main.view_search_track_result.view.*

class SearchRecyclerAdapter : RecyclerView.Adapter<SearchRecyclerAdapter.SearchViewHolder>() {
    private val idOfArtistClicksSubject: PublishSubject<String> = PublishSubject.create()
    val idOfArtistClicks = idOfArtistClicksSubject as Observable<String>
    companion object {
        val VIEW_TYPE_HEADER = 0
        val VIEW_TYPE_TRACK = 1
        val VIEW_TYPE_ARTIST = 2
    }

    sealed class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        class Track(itemView: View) : SearchViewHolder(itemView) {
            fun bind(track: TrackSearchResultUiModel) {
                itemView.title.text = track.title
                itemView.artists.text = track.artists
            }
        }

        class Artist(itemView: View) : SearchViewHolder(itemView) {
            fun bind(artist: ArtistSearchResultUiModel, clickListener: Observer<String>) {
                itemView.name.text = artist.name
                Picasso.with(itemView.context).load(artist.imageUrl).into(itemView.image)
                itemView.setOnClickListener {
                    clickListener.onNext(artist.id)
                }
            }
        }

        class Header(itemView: View) : SearchViewHolder(itemView) {
            fun bind(title: Int) {
                itemView.title.text = itemView.resources.getString(title)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val view = inflater.inflate(R.layout.view_search_header, parent, false)
                SearchViewHolder.Header(view)
            }
            VIEW_TYPE_ARTIST -> {
                val view = inflater.inflate(R.layout.view_search_artist_result, parent, false)
                SearchViewHolder.Artist(view)
            }
            VIEW_TYPE_TRACK -> {
                val view = inflater.inflate(R.layout.view_search_track_result, parent, false)
                SearchViewHolder.Track(view)
            }
            else -> throw IllegalArgumentException("Unknown viewType")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position >= artistsStartPosition() && position < artistsEndPosition()) {
            VIEW_TYPE_ARTIST
        } else if (position >= tracksStartPosition() && position < tracksEndPosition()) {
            VIEW_TYPE_TRACK
        } else if (position == artistsHeadPosition() || position == tracksHeadPosition()) {
            VIEW_TYPE_HEADER
        } else {
            throw IllegalStateException("Unknown position " + position)
        }
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val itemViewType = getItemViewType(position)
        if (itemViewType == VIEW_TYPE_TRACK) {
            (holder as SearchViewHolder.Track).bind(tracks[position - tracksStartPosition()])
        } else if (itemViewType == VIEW_TYPE_ARTIST) {
            (holder as SearchViewHolder.Artist).bind(artists[position - artistsStartPosition()], idOfArtistClicksSubject)
        } else if (itemViewType == VIEW_TYPE_HEADER) {
            (holder as SearchViewHolder.Header).bind(
                    if (position == tracksHeadPosition()) {
                        R.string.tracks
                    } else if (position == artistsHeadPosition()) {
                        R.string.artists
                    } else {
                        throw IllegalStateException()
                    }
            )
        }
    }

    var tracks: List<TrackSearchResultUiModel> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var artists: List<ArtistSearchResultUiModel> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = tracksEndPosition()

    private fun artistsHeadPosition() = 0
    private fun artistsStartPosition() = artistsHeadPosition() + if (artists.isNotEmpty()) {
        1
    } else {
        0
    }

    private fun artistsEndPosition() = artistsStartPosition() + artists.size
    private fun tracksHeadPosition() = artistsEndPosition()
    private fun tracksStartPosition() = tracksHeadPosition() + if (tracks.isNotEmpty()) {
        1
    } else {
        0
    }

    private fun tracksEndPosition() = artistsStartPosition() + tracks.size

}