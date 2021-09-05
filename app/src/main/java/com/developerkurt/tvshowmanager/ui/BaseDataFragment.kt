package com.developerkurt.tvshowmanager.ui

import androidx.fragment.app.Fragment
import com.developerkurt.gamedatabase.data.source.Result
import com.developerkurt.gamedatabase.data.source.Result.*
import com.developerkurt.tvshowmanager.R
import com.google.android.material.snackbar.Snackbar

/**
 * A class that forces fragments to apply the state pattern in a convenient way which is used throughout the application when fetching data.
 */
abstract class BaseDataFragment : Fragment()
{
    protected var displayedErrorSnackbar = false
    protected val failedToFetchSnackBar by lazy { Snackbar.make(this.requireView(), R.string.data_failed_to_fetch, Snackbar.LENGTH_LONG) }

    abstract protected fun changeLayoutStateToLoading()
    abstract protected fun changeLayoutStateToReady()
    abstract protected fun changeLayoutStateToError()

    open protected fun showFailedToFetchSnackBar() = failedToFetchSnackBar.show()

    protected open fun handleDataStateChange(result: Result<*>)
    {
        when (result)
        {

            is Loading -> changeLayoutStateToLoading()
            is Error ->
            {
                if (!displayedErrorSnackbar)
                {
                    showFailedToFetchSnackBar()
                    displayedErrorSnackbar = true
                }

                changeLayoutStateToError()

            }

            is Success ->
            {
                changeLayoutStateToReady()
                displayedErrorSnackbar = false
            }


        }
    }
}
