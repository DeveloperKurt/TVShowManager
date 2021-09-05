package com.developerkurt.tvshowmanager.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.developerkurt.gamedatabase.adapters.MovieListAdapter
import com.developerkurt.gamedatabase.data.source.Result
import com.developerkurt.tvshowmanager.databinding.ListTvShowsFragmentBinding
import com.developerkurt.tvshowmanager.model.ShowcaseMovie
import com.developerkurt.tvshowmanager.viewmodel.TVShowViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings

@WithFragmentBindings
@AndroidEntryPoint
class ListTVShowsFragment : BaseDataFragment(), Observer<Result<List<ShowcaseMovie>>>
{
    // This property is only valid between onCreateView and  onDestroyView.
    private val binding get() = _binding!!
    private var _binding: ListTvShowsFragmentBinding? = null


    private val viewModel: TVShowViewModel by viewModels()

    private lateinit var movieListAdapter: MovieListAdapter


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View?
    {
        _binding = ListTvShowsFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbarTvShows.setNavigationOnClickListener {
            val action = ListTVShowsFragmentDirections.actionListTVShowsFragmentToMainFragment()
            view.findNavController().navigate(action)
        }
        movieListAdapter = MovieListAdapter()
        binding.rvMovies.adapter = movieListAdapter

        //fetch the list manually when it is called for the first time
        observeLiveDatas()

        //Fetch more data when the onEndOfListReached listener is invoked
        movieListAdapter.onEndOfListReached = {
            observeLiveDatas()
        }
    }


    private fun observeLiveDatas()
    {
        viewModel.fetchTVShows().observe(viewLifecycleOwner, this)

    }

    override fun changeLayoutStateToLoading()
    {
        binding.progressBar.visibility = View.VISIBLE
    }

    override fun changeLayoutStateToReady()
    {
        binding.progressBar.visibility = View.GONE

    }

    override fun changeLayoutStateToError()
    {
        binding.progressBar.visibility = View.GONE
    }

    override fun onDestroyView()
    {
        super.onDestroyView()
        _binding = null
    }

    /**
     * On new movie lists fetched
     */
    override fun onChanged(movieResult: Result<List<ShowcaseMovie>>)
    {

        if (movieResult is Result.Success)
        {
            movieListAdapter.updateList(movieResult.data)
        }

        handleDataStateChange(movieResult)
    }

}