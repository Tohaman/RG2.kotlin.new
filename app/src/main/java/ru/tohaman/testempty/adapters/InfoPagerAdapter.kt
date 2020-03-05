package ru.tohaman.testempty.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.tohaman.testempty.ui.info.InfoAboutFragment
import ru.tohaman.testempty.ui.info.InfoMiniHelpFragment
import ru.tohaman.testempty.ui.info.InfoThanksFragment

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