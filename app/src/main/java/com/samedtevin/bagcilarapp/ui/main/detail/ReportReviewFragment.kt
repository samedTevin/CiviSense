package com.samedtevin.bagcilarapp.ui.main.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.samedtevin.bagcilarapp.R
import com.samedtevin.bagcilarapp.databinding.FragmentReportReviewBinding


class ReportReviewFragment : Fragment() {

    private var _binding: FragmentReportReviewBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentReportReviewBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

}