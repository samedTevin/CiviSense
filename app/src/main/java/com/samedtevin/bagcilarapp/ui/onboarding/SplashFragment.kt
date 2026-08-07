package com.samedtevin.bagcilarapp.ui.onboarding


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.samedtevin.bagcilarapp.R
import com.samedtevin.bagcilarapp.session.ApplicationSession
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class SplashFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var applicationSession: ApplicationSession


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        applicationSession = ApplicationSession(requireContext())

        lifecycleScope.launch {
            delay(3000)
            val isFinished = applicationSession.userOnboardingFlow.first()

            if (!isFinished) {
                findNavController().navigate(R.id.action_splashFragment_to_viewPagerFragment)
            } else if (firebaseAuth.currentUser != null && firebaseAuth.currentUser?.isEmailVerified == true) {
                findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
            } else {
                findNavController().navigate(R.id.action_splashFragment_to_welcomeFragment)
            }
        }
    }

}