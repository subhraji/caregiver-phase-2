package com.example.caregiverphase2.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.caregiverphase2.ui.fragment.BidedJobFragment
import com.example.caregiverphase2.ui.fragment.ClosedJobFragment
import com.example.caregiverphase2.ui.fragment.CompleteJobFragment
import com.example.caregiverphase2.ui.fragment.UpcommingJobFragment

class HomeViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> BidedJobFragment()
            1 -> UpcommingJobFragment()
            else -> CompleteJobFragment()
        }
    }
}