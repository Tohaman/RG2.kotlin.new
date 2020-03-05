package ru.tohaman.testempty.ui.info

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_info.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.tohaman.testempty.R
import ru.tohaman.testempty.adapters.InfoPagerAdapter
import ru.tohaman.testempty.ui.UiUtilViewModel

class InfoFragment : Fragment() {
    private val uiUtilViewModel by sharedViewModel<UiUtilViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_info, container, false)
        uiUtilViewModel.showBottomNav()

        val vp =  view.findViewById<ViewPager2>(R.id.infoViewPager)
        vp.adapter = InfoPagerAdapter(this)

        var tabLayout = view.findViewById<TabLayout>(R.id.tabLayout)
        TabLayoutMediator(tabLayout, vp) { tab, position ->
            var text = ""
            when (position) {
                0 -> text = "О программе"
                1 -> text = "Спасибо"
                2 -> text = "Помощь"
            }
            tab.text = text
        }.attach()

        return view
    }
}