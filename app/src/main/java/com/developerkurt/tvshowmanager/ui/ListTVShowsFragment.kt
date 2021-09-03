package com.developerkurt.tvshowmanager.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.developerkurt.tvshowmanager.databinding.ListTvShowsFragmentBinding
import com.developerkurt.tvshowmanager.viewmodel.TVShowViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings

@WithFragmentBindings
@AndroidEntryPoint
class ListTVShowsFragment : Fragment()
{
    // This property is only valid between onCreateView and  onDestroyView.
    private val binding get() = _binding!!
    private var _binding: ListTvShowsFragmentBinding? = null


    private val viewModel: TVShowViewModel by viewModels()


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View?
    {
        _binding = ListTvShowsFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView()
    {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart()
    {
        super.onStart()
        viewModel.fetchMovies()
    }
}