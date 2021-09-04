package com.developerkurt.tvshowmanager.ui

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.developerkurt.tvshowmanager.R
import com.developerkurt.tvshowmanager.data.CreateTVShowListener
import com.developerkurt.tvshowmanager.databinding.AddTvShowFragmentBinding
import com.developerkurt.tvshowmanager.viewmodel.TVShowViewModel
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings
import java.text.SimpleDateFormat
import java.util.*

@WithFragmentBindings
@AndroidEntryPoint
class AddTVShowFragment : Fragment(), DatePickerDialog.OnDateSetListener
{
    // This property is only valid between onCreateView and  onDestroyView.
    private val binding get() = _binding!!
    private var _binding: AddTvShowFragmentBinding? = null

    private val viewModel: TVShowViewModel by viewModels()

    private val datePickerDialog by lazy {
        DatePickerDialog(
                requireContext(),
                R.style.TVShowManagerDatePicker,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View?
    {
        _binding = AddTvShowFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    @SuppressLint("SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbarAddTvShow.setNavigationOnClickListener {
            val action = AddTVShowFragmentDirections.actionAddTVShowFragmentToMainFragment()
            view.findNavController().navigate(action)
        }

        binding.etDate.setOnClickListener {
            datePickerDialog.show()
        }

        binding.btnSave.setOnClickListener {
            if (validateTheFields())
            {

                val title = binding.etTitle.text.toString()
                val dateString: String = binding.etDate.text.toString()


                val date: Date? = if (!dateString.isEmpty()) SimpleDateFormat("yyyy/MM/dd").parse(dateString) else null



                viewModel.createTVShow(
                        title = title,
                        releaseDate = date,
                        seasons = binding.etSeasons.text.toString().toDouble(),
                        createTVShowListener = object : CreateTVShowListener
                        {
                            override fun onSuccess()
                            {
                                Snackbar.make(binding.root, R.string.tv_show_saved_successfully, LENGTH_LONG).show()
                            }

                            override fun onError()
                            {
                                Snackbar.make(binding.root, R.string.tv_show_saving_error, LENGTH_LONG).show()
                            }

                        })
            }
        }


    }

    private fun validateTheFields(): Boolean
    {
        if (binding.etTitle.text.isNullOrEmpty())
        {
            binding.etTitle.error = "Name of the show cannot be empty!"
            return false
        }
        val seasonsString = binding.etSeasons.text.toString()

        if (seasonsString.isEmpty())
        {
            binding.etSeasons.error = "Seasons cannot be empty!"
            return false

        }

        try
        {
            seasonsString.toDouble()
        }
        catch (e: NumberFormatException)
        {
            binding.etSeasons.error = "Seasons has to be a valid number!"
            return false
        }

        return true
    }

    override fun onDestroyView()
    {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("SetTextI18n")
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int)
    {
        binding.etDate.setText("$year/$month/$dayOfMonth", TextView.BufferType.EDITABLE)
        datePickerDialog.dismiss()
    }

}