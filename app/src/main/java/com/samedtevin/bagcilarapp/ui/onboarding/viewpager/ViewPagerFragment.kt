package com.samedtevin.bagcilarapp.ui.onboarding.viewpager

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.samedtevin.bagcilarapp.R
import com.samedtevin.bagcilarapp.adapter.viewpageradapter.ViewPagerAdapter
import com.samedtevin.bagcilarapp.databinding.FragmentViewPagerBinding
import com.samedtevin.bagcilarapp.session.ApplicationSession
import kotlinx.coroutines.launch


class ViewPagerFragment : Fragment() {


    private var _binding: FragmentViewPagerBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var applicationSession: ApplicationSession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        applicationSession = ApplicationSession(requireContext())
    }

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
                saveAndNavigate()
            }
        }

        // Skips the onboarding
        binding.btnSkip.setOnClickListener {
            saveAndNavigate()
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

    private fun saveAndNavigate(){
        lifecycleScope.launch {
            applicationSession.saveOnboardingPref(true)
            findNavController().navigate(R.id.action_viewPagerFragment_to_welcomeFragment)
        }
    }


}