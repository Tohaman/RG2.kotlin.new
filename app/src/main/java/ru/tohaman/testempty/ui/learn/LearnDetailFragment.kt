package ru.tohaman.testempty.ui.learn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.tohaman.testempty.DebugTag.TAG
import ru.tohaman.testempty.adapters.DetailDiffUtils
import ru.tohaman.testempty.databinding.FragmentLearnDetailBinding
import ru.tohaman.testempty.dbase.entitys.MainDBItem
import ru.tohaman.testempty.ui.shared.UiUtilViewModel
import timber.log.Timber


class LearnDetailFragment : Fragment() {
    private val args by navArgs<LearnDetailFragmentArgs>()
    private val phaseId by lazy { args.id }
    private val phase by lazy { args.phase }
    private lateinit var binding: FragmentLearnDetailBinding
    private var currentId = 0

    private val uiUtilViewModel by sharedViewModel<UiUtilViewModel>()
    private val detailViewModel by sharedViewModel<LearnDetailViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("$TAG onCreate with $phaseId, $phase")
        currentId = phaseId
        detailViewModel.setCurrentItems(phaseId, phase)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Timber.d("$TAG onCreateView")
        uiUtilViewModel.hideBottomNav()

        binding = FragmentLearnDetailBinding.inflate(inflater, container, false)
            .apply {
                lifecycleOwner = this@LearnDetailFragment

                val adapter = DetailPagerAdapter(this@LearnDetailFragment)
                detailViewPager.adapter = adapter

                tabLayout.tabMode = TabLayout.MODE_SCROLLABLE
                //tabLayout.setupWithViewPager(detailViewPager)

                TabLayoutMediator(tabLayout, detailViewPager) { tab, position ->
                    //Timber.d("$TAG tabLayoutMediator = $tab position = $position")
                    tab.text = adapter.getData()[position].title
                }.attach()

                detailViewPager.offscreenPageLimit = 5
                detailViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        currentId = position
                    }
                })

                detailViewModel.liveCurrentItems.observe(viewLifecycleOwner, Observer {
                    it?.let {
                        Timber.d("$TAG detailCurItems - ${it.size}, $currentId")
                        adapter.refreshItems(it)
                        detailViewPager.setCurrentItem(currentId,false)
                    }
                })

                viewModel = detailViewModel
            }

        return binding.root
    }

    override fun onDestroy() {
        Timber.d ("$TAG onDestroy детаилФагмент")
        super.onDestroy()
    }

    inner class DetailPagerAdapter (fragment: Fragment) : FragmentStateAdapter(fragment) {
        private var detailItems = listOf<MainDBItem>()

        override fun getItemCount(): Int {
            //Timber.d("$TAG getItemCount = ${detailItems.size} - $detailItems")
            return detailItems.count()
        }

        override fun createFragment(position: Int): Fragment {
            return LearnDetailItemFragment.newInstance(detailItems[position])
        }

        //передаем новые данные и оповещаем адаптер о необходимости обновления списка
        fun refreshItems(items: List<MainDBItem>) {
            Timber.d("$TAG Обновляем список в адаптере DetailPagerAdapter $items")
            detailItems = items
            notifyDataSetChanged()
        }

        fun getData() : List<MainDBItem> {
            return detailItems
        }
    }

    inner class DetailPagerAdapter2 (fm: FragmentManager) : FragmentStatePagerAdapter(fm, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        private var detailItems = listOf<MainDBItem>()

        //передаем новые данные и оповещаем адаптер о необходимости обновления списка
        fun refreshItems(items: List<MainDBItem>) {
            Timber.d("$TAG Обновляем список в адаптере DetailPagerAdapter $items")

            detailItems = items
            notifyDataSetChanged()
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return detailItems[position].title
        }

        fun getData() : List<MainDBItem> {
            return detailItems
        }

        override fun getItem(position: Int): Fragment {
            return LearnDetailItemFragment.newInstance(detailItems[position])
        }

        override fun getCount(): Int {
            return detailItems.count()
        }
    }


}
