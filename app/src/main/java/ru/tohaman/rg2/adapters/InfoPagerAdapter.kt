package ru.tohaman.rg2.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.tohaman.rg2.ui.info.InfoAboutFragment
import ru.tohaman.rg2.ui.info.InfoMiniHelpFragment
import ru.tohaman.rg2.ui.info.InfoThanksFragment

class InfoPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> InfoAboutFragment()
            1 -> InfoThanksFragment()
            else -> InfoMiniHelpFragment()
        }
    }
}