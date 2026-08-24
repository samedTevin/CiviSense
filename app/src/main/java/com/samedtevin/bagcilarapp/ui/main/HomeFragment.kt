package com.samedtevin.bagcilarapp.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.samedtevin.bagcilarapp.databinding.FragmentHomeBinding
import com.samedtevin.bagcilarapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.samedtevin.bagcilarapp.R
import com.samedtevin.bagcilarapp.viewmodel.HomeViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    val binding get() = _binding!!
    private val viewmodel: UserViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewmodel.getUser()
        homeViewModel.getRandomAnnouncement()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnReportIssue.setOnClickListener { 
            findNavController().navigate(R.id.action_homeFragment_to_reportFragment)
        }

        // Navigate to the My Reports
        binding.cardMyReports.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_myReportsFragment)
        }

        binding.cardAskGemini.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_smartAssistantFragment)
        }

        binding.tvViewAll.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_announcementsFragment)
        }

        collectUser()
        collectRandomAnnouncement()
    }

    private fun collectUser(){
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewmodel.user.collect { user ->
                    if(user != null){
                        binding.tvFullName.text = "${user.fullName} \uD83D\uDC4B"
                    }
                    else{
                        binding.tvFullName.text = "Guest \uD83D\uDC4B"
                    }

                }
            }
        }
    }

    private fun collectRandomAnnouncement(){
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                homeViewModel.random.collect { random ->
                    binding.apply {
                        random?.let {
                            tvAnnouncementTitle.text = random.title
                            tvAnnouncementDescription.text = random.description
                            tvAnnouncementDate.text = random.createdAt?.toDate().let {
                                SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(it)
                            }
                            Glide.with(requireContext()).load(random.imageUrl).into(ivAnnouncement)

                            cvRandomAnnouncement.setOnClickListener {
                                val action = HomeFragmentDirections.actionHomeFragmentToAnnouncementDetailFragment(random.id)
                                findNavController().navigate(action)
                            }
                        }
                    }
                }
            }
        }
    }

}