package com.developerkurt.tvshowmanager.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.developerkurt.gamedatabase.adapters.MovieListAdapter
import com.developerkurt.gamedatabase.data.source.Result
import com.developerkurt.tvshowmanager.databinding.ListTvShowsFragmentBinding
import com.developerkurt.tvshowmanager.viewmodel.TVShowViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings

@WithFragmentBindings
@AndroidEntryPoint
class ListTVShowsFragment : BaseDataFragment()
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
    }

    override fun onResume()
    {
        super.onResume()
        subscribeUi(movieListAdapter)
    }

    private fun subscribeUi(movieListAdapter: MovieListAdapter)
    {
        viewModel.fetchMovies().observe(viewLifecycleOwner, { result ->

            if (result is Result.Success)
            {
                movieListAdapter.updateList(result.data)
            }

            handleDataStateChange(result)
        })

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

}