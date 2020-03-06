package ru.tohaman.testempty.ui.learn

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.tohaman.testempty.DebugTag.TAG
import ru.tohaman.testempty.R
import ru.tohaman.testempty.databinding.FragmentLearnBinding
import ru.tohaman.testempty.ui.UiUtilViewModel
import timber.log.Timber


class LearnFragment : Fragment() {
    private val uiUtilViewModel by sharedViewModel<UiUtilViewModel>()
    private val learnViewModel by sharedViewModel<LearnViewModel>()

    private lateinit var binding : FragmentLearnBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        uiUtilViewModel.showBottomNav()
        val cubeTypes = learnViewModel.cubeTypes

        binding = FragmentLearnBinding.inflate(inflater, container, false)
            .apply {
                val viewPager2 =  learnViewPager
                val adapter = LearnPagerAdapter(this@LearnFragment)
                viewPager2.offscreenPageLimit = cubeTypes.size
                viewPager2.adapter = adapter

                val tabLayout = appBar.tabLayout
                tabLayout.tabMode = TabLayout.MODE_SCROLLABLE
                TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
                    tab.text = cubeTypes[position].name
                }.attach()
            }

        return binding.root
    }


    inner class LearnPagerAdapter (fragment: Fragment) : FragmentStateAdapter(fragment) {
        private val cubeTypes = learnViewModel.cubeTypes
        override fun getItemCount(): Int {
            val size = cubeTypes.size
            Timber.tag(TAG).d("Размер массива - $size")
            return size
        }

        override fun createFragment(position: Int): Fragment {
            return LearnMenuFragment.newInstance(cubeTypes[position].name)
        }
    }
}
