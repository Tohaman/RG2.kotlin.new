package ru.tohaman.testempty.ui.learn

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.tohaman.testempty.DebugTag.TAG
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
        Timber.d("$TAG onCreate phase = $phase")
        currentId = phaseId
        detailViewModel.setCurrentItems(phaseId, phase)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        Timber.d("$TAG bottomNavHide")
        uiUtilViewModel.hideBottomNav()
        val adapter = DetailPagerAdapter(this)

        binding = FragmentLearnDetailBinding.inflate(inflater, container, false)
            .apply {
                lifecycleOwner = this@LearnDetailFragment
                //Timber.d("$TAG DetFragment with phase = $phase")
                detailViewPager.adapter = adapter
                title=""

                val tabLayout = appBar.tabLayout
                tabLayout.tabMode = TabLayout.MODE_SCROLLABLE


                //Timber.d("$TAG liveCurrentItems обновился curType = $curType, $it")
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
                        val curId = detailViewModel.getItemNum(currentId)
                        val tabCount = tabLayout.tabCount
                        TabLayoutMediator(tabLayout, detailViewPager) {tab, position ->
                            //Timber.d("$TAG обновляем заголовок в tabLayout ${tabCount} - ${position}")
                            if (position < tabCount) {
                                tab.text = it[position].title
                            }
                        }.attach()
                        detailViewPager.setCurrentItem(curId,true)
                    }
                })

                viewModel = detailViewModel
            }

        return binding.root
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
    }

}
