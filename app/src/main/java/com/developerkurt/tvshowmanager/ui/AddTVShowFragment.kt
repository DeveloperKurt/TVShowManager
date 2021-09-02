package com.developerkurt.tvshowmanager.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.developerkurt.tvshowmanager.R
import com.developerkurt.tvshowmanager.viewmodel.TVShowViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddTVShowFragment : Fragment()
{

    private val viewModel: TVShowViewModel by viewModels()


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.add_tv_show_fragment, container, false)
    }

}