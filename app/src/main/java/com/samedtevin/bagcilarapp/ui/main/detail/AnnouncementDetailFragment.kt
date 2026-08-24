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
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.samedtevin.bagcilarapp.R
import com.samedtevin.bagcilarapp.databinding.FragmentAnnouncementDetailBinding
import com.samedtevin.bagcilarapp.viewmodel.AnnouncementViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

@AndroidEntryPoint
class AnnouncementDetailFragment : Fragment() {
    private var _binding: FragmentAnnouncementDetailBinding? = null
    val binding get() = _binding!!
    private val args: AnnouncementDetailFragmentArgs by navArgs()
    private val viewModel: AnnouncementViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAnnouncementDetailBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val announcementId = args.announcementId
        viewModel.getSelectedAnnouncement(announcementId)
        collectSelectedAnnouncement()
    }

    private fun collectSelectedAnnouncement(){
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.selected.collect { selected ->
                    selected?.let{
                        Glide.with(binding.root).load(selected.imageUrl).into(binding.ivAnnouncement)
                        binding.tvTitle.text = selected.title
                        binding.tvDate.text = selected.createdAt?.toDate().let {
                            SimpleDateFormat("dd MMM yyyy").format(it)
                        }
                        binding.tvContent.text = selected.description
                    }
                }
            }
        }
    }



}