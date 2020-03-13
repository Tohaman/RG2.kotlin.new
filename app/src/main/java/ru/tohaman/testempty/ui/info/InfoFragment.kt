package ru.tohaman.testempty.ui.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.tohaman.testempty.R
import ru.tohaman.testempty.adapters.InfoPagerAdapter
import ru.tohaman.testempty.databinding.FragmentInfoBinding
import ru.tohaman.testempty.ui.shared.UiUtilViewModel

class InfoFragment : Fragment() {
    private val uiUtilViewModel by sharedViewModel<UiUtilViewModel>()
    private lateinit var binding : FragmentInfoBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        uiUtilViewModel.showBottomNav()
        val adapter = InfoPagerAdapter(this)

        binding = FragmentInfoBinding.inflate(inflater, container, false)
            .apply {

                val vp = infoViewPager
                vp.adapter = adapter

                title = getString(R.string.info)

                val tabLayout = appBar.tabLayout
                TabLayoutMediator(tabLayout, vp) { tab, position ->
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