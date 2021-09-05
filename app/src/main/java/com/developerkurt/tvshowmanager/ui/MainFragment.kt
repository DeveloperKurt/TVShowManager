package com.developerkurt.tvshowmanager.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.developerkurt.tvshowmanager.databinding.FragmentMainBinding


/**
 * A simple [Fragment] subclass.
 * Use the [MainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainFragment : Fragment()
{

    private var _binding: FragmentMainBinding? = null

    // This property is only valid between onCreateView and  onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View?
    {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        binding.fabAddMovie.setOnClickListener {

            val action = MainFragmentDirections.actionMainFragmentToAddTVShowFragment()
            view.findNavController().navigate(action)

        }

        binding.fabShowMovies.setOnClickListener {

            val action = MainFragmentDirections.actionMainFragmentToListTVShowsFragment()
            view.findNavController().navigate(action)

        }
    }

    override fun onDestroyView()
    {
        super.onDestroyView()
        _binding = null
    }
}