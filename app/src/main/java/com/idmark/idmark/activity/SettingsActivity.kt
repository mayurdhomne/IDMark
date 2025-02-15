package com.idmark.idmark.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.idmark.R
import com.idmark.databinding.ActivitySettingBinding
import com.idmark.idmark.fragment.EditProfileFragment
import com.idmark.idmark.fragment.SecurityFragment

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupViewPager()
        setupTabLayout()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)

        binding.menuButton.setOnClickListener {
            // Open navigation drawer or menu
        }

        binding.searchButton.setOnClickListener {
            // Handle search click
        }
    }

    private fun setupViewPager() {
        binding.viewPager.adapter = SettingsPagerAdapter(this)
        binding.viewPager.isUserInputEnabled = false // Disable swipe navigation
    }

    private fun setupTabLayout() {
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Edit Profile"
                1 -> "Security"
                else -> ""
            }
        }.attach()

        // Apply background only when the tab is selected
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.view?.setBackgroundResource(R.drawable.edittext_background) // Apply bg on selected tab
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.view?.setBackgroundResource(android.R.color.transparent) // Remove bg from unselected tab
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Do nothing on reselect
            }
        })

        // Set the background for the initially selected tab
        binding.tabLayout.getTabAt(binding.tabLayout.selectedTabPosition)?.view?.setBackgroundResource(R.drawable.edittext_background)
    }

}

class SettingsPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> EditProfileFragment()
            1 -> SecurityFragment()
            else -> throw IllegalArgumentException("Invalid position $position")
        }
    }
}


