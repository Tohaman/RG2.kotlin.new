package ru.tohaman.testempty.ui.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.tohaman.testempty.DebugTag
import ru.tohaman.testempty.DebugTag.TAG
import ru.tohaman.testempty.R
import ru.tohaman.testempty.adapters.InfoPagerAdapter
import ru.tohaman.testempty.databinding.FragmentInfoBinding
import ru.tohaman.testempty.ui.shared.UiUtilViewModel
import timber.log.Timber

class InfoFragment : Fragment() {
    private val uiUtilViewModel by sharedViewModel<UiUtilViewModel>()
    private val infoViewModel by sharedViewModel<InfoViewModel>()
    private lateinit var binding : FragmentInfoBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Timber.d("$TAG bottomNavShow")
        uiUtilViewModel.showBottomNav()
        val adapter = InfoPagerAdapter(this)

        binding = FragmentInfoBinding.inflate(inflater, container, false)
            .apply {

                title = getString(R.string.info)

                infoViewPager.adapter = adapter
                infoViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        infoViewModel.setBookMark(position)
                        super.onPageSelected(position)
                    }
                })
                infoViewPager.currentItem = infoViewPager.bookmark

                val tabLayout = appBar.tabLayout
                TabLayoutMediator(tabLayout, infoViewPager) { tab, position ->
                    var text = ""
                    when (position) {
                        0 -> text = "О программе"
                        1 -> text = "Спасибо"
                        2 -> text = "Помощь"
                    }
                    tab.text = text
                }.attach()
            }
        return binding.root
    }
}