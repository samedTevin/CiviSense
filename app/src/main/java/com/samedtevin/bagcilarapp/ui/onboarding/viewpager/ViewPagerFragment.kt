package com.samedtevin.bagcilarapp.ui.onboarding.viewpager

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.samedtevin.bagcilarapp.R
import com.samedtevin.bagcilarapp.adapter.viewpageradapter.ViewPagerAdapter
import com.samedtevin.bagcilarapp.databinding.FragmentViewPagerBinding


class ViewPagerFragment : Fragment() {


    private var _binding: FragmentViewPagerBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentViewPagerBinding.inflate(layoutInflater,container,false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentList = arrayListOf<Fragment>(ReportIssueFragment(), AiAssistantFragment(),
            ReportTrackingFragment())

        // ViewPager2 Adapter
        viewPagerAdapter = ViewPagerAdapter(fragmentList,requireActivity().supportFragmentManager,lifecycle)
        binding.viewPager2.adapter = viewPagerAdapter

        // WormDots (Attaching it to the viewPager2)
        binding.wormDotsIndicator.attachTo(binding.viewPager2)


        // If the condition is false, the following page will be shown. Otherwise, it will go directly to home fragment
        binding.btnNext.setOnClickListener {
            if(binding.viewPager2.currentItem < viewPagerAdapter.itemCount - 1 ){
                binding.viewPager2.currentItem += 1
            }
            else{
                findNavController().navigate(R.id.action_viewPagerFragment_to_welcomeFragment)
            }
        }

        // Skips the onboarding
        binding.btnSkip.setOnClickListener {
            findNavController().navigate(R.id.action_viewPagerFragment_to_welcomeFragment)
        }

        changeTextOnButton()

    }

    // Changes text of the button based on the position of fragment.
    private fun changeTextOnButton(){
        binding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if(position == 2){
                    binding.btnNext.text = getString(R.string.get_started)
                }
                else{
                    binding.btnNext.text = getString(R.string.next)
                }
            }
        })
    }

}