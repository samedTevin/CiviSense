package com.samedtevin.bagcilarapp.ui.main.detail

import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.samedtevin.bagcilarapp.R
import com.samedtevin.bagcilarapp.adapter.uiadapters.ChatAdapter
import com.samedtevin.bagcilarapp.databinding.FragmentSmartAssistantBinding
import com.samedtevin.bagcilarapp.model.ChatMessage
import com.samedtevin.bagcilarapp.viewmodel.SmartAssistantViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SmartAssistantFragment : Fragment() {
    private var _binding: FragmentSmartAssistantBinding? = null
    val binding get() = _binding!!
    private lateinit var chatAdapter: ChatAdapter
    private val smartViewModel: SmartAssistantViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSmartAssistantBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSendButton()
        setupInput()
        observeViewModel()
        setupFaqCards()

        binding.btnDelete.setOnClickListener {
            deleteConversation()
        }

        binding.btnRetry.setOnClickListener {
            smartViewModel.retry()
        }
    }

    private fun setupRecyclerView(){

        chatAdapter = ChatAdapter(mutableListOf())

        binding.rvAiChat.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = chatAdapter
        }

    }

    private fun setupSendButton(){
        binding.textInputLayout.setEndIconOnClickListener {
            sendMessage()
        }
    }

    private fun sendMessage(){

        binding.etAiQuestion.clearFocus()

        val question = binding.etAiQuestion.text?.toString()?.trim().orEmpty()

        if(question.isEmpty()) {
            binding.etAiQuestion.error = "Please enter a question"
            return
        }

        showChat()

        binding.etAiQuestion.text?.clear()

        smartViewModel.askAi(question)
    }

    private fun setupInput(){
        binding.etAiQuestion.setOnEditorActionListener { _, actionId, event ->

            val isSendAction = actionId == EditorInfo.IME_ACTION_SEND

            val isEnterPressed = event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN

            if(isSendAction || isEnterPressed){
                sendMessage()
                true
            }else{
                false
            }
        }
    }

    private fun observeViewModel(){

        viewLifecycleOwner.lifecycleScope.launch {
            smartViewModel.messages.collect { messages ->

                chatAdapter.submitMessages(messages)

                if(messages.isNotEmpty()){
                    showChat()
                    scrollToBottom()
                }else{
                    binding.rvAiChat.visibility = View.GONE
                    binding.onboardingLayout.visibility = View.VISIBLE
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            smartViewModel.isLoading.collect { isLoading ->
                binding.textInputLayout.isEnabled = !isLoading

                if(isLoading){
                    binding.errorLayout.visibility = View.GONE
                    chatAdapter.showLoading()
                }
                else{
                    chatAdapter.hideLoading()
                }
                scrollToBottom()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            smartViewModel.error.collect { error ->
                if(!error.isNullOrBlank()){
                    binding.tvError.text = error
                    binding.errorLayout.visibility = View.VISIBLE
                }else{
                    binding.errorLayout.visibility = View.GONE
                }
            }
        }
    }

    private fun showChat(){
        binding.onboardingLayout.visibility = View.GONE
        binding.rvAiChat.visibility = View.VISIBLE
        scrollToBottom()
    }

    private fun scrollToBottom(){

        binding.rvAiChat.post {
            if(chatAdapter.itemCount > 0){
                binding.rvAiChat.scrollToPosition(chatAdapter.itemCount - 1)
            }
        }
    }

    private fun setupFaqCards(){
        binding.cvFaq1.setOnClickListener {
            askQuestion("How do I submit a report?")
        }

        binding.cvFaq2.setOnClickListener {
            askQuestion("How can I track my reports?")
        }

        binding.cvFaq3.setOnClickListener {
            askQuestion("Is an account required to report?")
        }
    }

    private fun askQuestion(question: String){
        showChat()
        smartViewModel.askAi(question)
    }

    private fun deleteConversation(){
        MaterialAlertDialogBuilder(requireContext()).setTitle("Delete conversation?").setMessage("Your entire AI assistant conversation will be permanently deleted.")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Delete"){_,_ ->
                smartViewModel.clearMessage()
            }.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}