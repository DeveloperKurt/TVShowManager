package com.developerkurt.gamedatabase.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.developerkurt.tvshowmanager.R
import com.developerkurt.tvshowmanager.databinding.MovieListItemBinding
import com.developerkurt.tvshowmanager.model.Movie

class MovieListAdapter() : RecyclerView.Adapter<MovieListAdapter.MovieDataViewHolder>()
{
    private var movieList: MutableList<Movie> = mutableListOf()

    /**
     * Used as a listener to notify the viewmodel when the end of the list is reached, and more data should be fetched
     */
    var onEndOfListReached: (() -> Unit)? = null

    /**
     * Adds the [data] to the end of the adapter list
     */
    fun updateList(data: List<Movie>)
    {
        val currentListIndex = movieList.size
        with(movieList) {
            addAll(data)
        }

        //Only notifies the position where changes have been made
        notifyItemRangeInserted(currentListIndex, data.size)

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MovieDataViewHolder(
            MovieListItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false), parent.context)

    override fun onBindViewHolder(holder: MovieDataViewHolder, position: Int)
    {
        holder.bind(movieList[position])

        //End of the list is reached
        if (position == getItemCount() - 1)
        {
            onEndOfListReached?.invoke()
        }
    }

    override fun getItemCount(): Int = movieList.size


    inner class MovieDataViewHolder(var binding: MovieListItemBinding, val context: Context) : RecyclerView.ViewHolder(binding.root)
    {
        fun bind(movie: Movie)
        {
            binding.tvMovieName.text = movie.title
            binding.tvDate.text = String.format(context.getString(R.string.date), movie.releaseDate)
            binding.tvSeasons.text = String.format(context.getString(R.string.seasons), movie.seasons)
        }
    }


}