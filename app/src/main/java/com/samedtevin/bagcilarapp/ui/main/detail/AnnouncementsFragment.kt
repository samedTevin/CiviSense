package com.samedtevin.bagcilarapp.ui.main.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.samedtevin.bagcilarapp.R
import com.samedtevin.bagcilarapp.adapter.uiadapters.AnnouncementAdapter
import com.samedtevin.bagcilarapp.databinding.FragmentAnnouncementsBinding
import com.samedtevin.bagcilarapp.viewmodel.AnnouncementViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class AnnouncementsFragment : Fragment() {


    private var _binding: FragmentAnnouncementsBinding? = null
    val binding get() = _binding!!
    private val viewModel: AnnouncementViewModel by viewModels()
    private lateinit var announcementAdapter: AnnouncementAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        announcementAdapter = AnnouncementAdapter(mutableListOf())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAnnouncementsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvAnnouncements.adapter = announcementAdapter
        binding.rvAnnouncements.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAnnouncements.clipToPadding = false

        viewModel.getAnnouncements()
        viewModel.getLatestAnnouncement()
        collectLatestAnnouncement()
        collectAnnouncements()

        announcementAdapter.onItemClick = { announcement ->
            findNavController().navigate(R.id.action_announcementsFragment_to_announcementDetailFragment)
        }
    }

    private fun collectAnnouncements(){
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.announcement.collect { announcements ->
                    announcements?.let{
                        announcementAdapter.updateList(announcements)
                    }
                }
            }
        }
    }

    private fun collectLatestAnnouncement(){
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.latest.collect { announcement ->
                    announcement?.let {
                        Glide.with(binding.root).load(announcement.imageUrl).into(binding.ivLatestAnnouncement)
                        binding.tvLatestAnnouncementTitle.text = announcement.title
                        binding.tvLatestAnnouncementDate.text = announcement.createdAt?.toDate().let{
                            SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(it)
                        }
                    }

                }
            }
        }
    }

}