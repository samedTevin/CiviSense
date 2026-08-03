package com.samedtevin.bagcilarapp.adapter.viewpageradapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(list : List<Fragment>, fm: FragmentManager, lifecycle: Lifecycle ) : FragmentStateAdapter(fm, lifecycle){

    val fragments = list

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    override fun getItemCount(): Int {
        return fragments.size
    }
}