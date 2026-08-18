package com.samedtevin.bagcilarapp.util

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.samedtevin.bagcilarapp.databinding.AlertDialogAppearenceBinding
import com.samedtevin.bagcilarapp.databinding.AlertDialogTermsAndPolicyBinding

object AlertDialog {

    fun showTermsAndPolicy(context: Context, layoutInflater: LayoutInflater, onClick: (Boolean) -> Unit) {

        val binding = AlertDialogTermsAndPolicyBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(context).setView(binding.root).create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.show()

        binding.btnAccept.setOnClickListener {
            onClick(true)
            dialog.dismiss()
        }

        binding.btnCancel.setOnClickListener {
            onClick(false)
            dialog.dismiss()
        }
    }

    fun showAppearence(context: Context, layoutInflater: LayoutInflater){

        val binding = AlertDialogAppearenceBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(context).setView(binding.root).create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.show()
    }
}