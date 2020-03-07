package ru.tohaman.testempty.ui.learn

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.tohaman.testempty.DebugTag
import ru.tohaman.testempty.databinding.FragmentLearnBinding
import ru.tohaman.testempty.dbase.entitys.CubeType
import ru.tohaman.testempty.ui.shared.UiUtilViewModel
import timber.log.Timber


class LearnFragment : Fragment() {
    private val uiUtilViewModel by sharedViewModel<UiUtilViewModel>()
    private val learnViewModel by sharedViewModel<LearnViewModel>()

    private lateinit var binding : FragmentLearnBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        uiUtilViewModel.showBottomNav()
        val adapter = LearnPagerAdapter(this@LearnFragment)

        binding = FragmentLearnBinding.inflate(inflater, container, false)
            .apply {
                val viewPager2 =  learnViewPager
                viewPager2.adapter = adapter

                val tabLayout = appBar.tabLayout
                tabLayout.tabMode = TabLayout.MODE_SCROLLABLE

                learnViewModel.mutableCubeTypes.observe(viewLifecycleOwner, Observer {
                    it?.let {
                        adapter.refreshItems(it)
                        viewPager2.offscreenPageLimit = it.size
                        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
                            tab.text = it[position].name
                        }.attach()
                    }
                })
            }

        return binding.root
    }


    inner class LearnPagerAdapter (fragment: Fragment) : FragmentStateAdapter(fragment) {
        private var cubeTypes = listOf<CubeType>()

        override fun getItemCount(): Int {
            return cubeTypes.size
        }

        override fun createFragment(position: Int): Fragment {
            return LearnMenuFragment.newInstance(cubeTypes[position])
        }

        //передаем новые данные и оповещаем адаптер о необходимости обновления списка
        fun refreshItems(items: List<CubeType>) {
            Timber.tag(DebugTag.TAG).d("Обновляем список в адаптере LearnPagerAdapter")
            cubeTypes = items
            notifyDataSetChanged()
        }
    }
}
