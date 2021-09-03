package com.developerkurt.tvshowmanager.ui

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.developerkurt.tvshowmanager.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : FragmentActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}