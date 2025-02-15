package com.idmark.idmark.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.idmark.databinding.FragmentSecurityBinding

class SecurityFragment : Fragment() {

    private var _binding: FragmentSecurityBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecurityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTwoFactorSwitch()
        setupSaveButton()
    }

    private fun setupTwoFactorSwitch() {
        val sharedPreferences = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        binding.twoFactorSwitch.isChecked = sharedPreferences.getBoolean("2FA_ENABLED", false)

        binding.twoFactorSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("2FA_ENABLED", isChecked).apply()
            Toast.makeText(requireContext(), if (isChecked) "2FA Enabled" else "2FA Disabled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupSaveButton() {
        binding.saveButton.setOnClickListener {
            val currentPassword = binding.currentPasswordInput.text.toString().trim()
            val newPassword = binding.newPasswordInput.text.toString().trim()

            if (currentPassword.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter both passwords", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Handle password change (API call)
            Toast.makeText(requireContext(), "Password Updated Successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
