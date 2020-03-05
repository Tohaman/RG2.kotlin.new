package ru.tohaman.testempty.ui.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
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

        return view
    }
}