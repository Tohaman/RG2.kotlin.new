package ru.tohaman.testempty.ui.learn

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
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
    private val learnViewModel by sharedViewModel<LearnViewModel>()
    private val detailViewModel by sharedViewModel<LearnDetailViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentId = phaseId
        detailViewModel.setCurrentItems(phaseId, phase)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        uiUtilViewModel.hideBottomNav()
        val adapter = DetailPagerAdapter(this)

        binding = FragmentLearnDetailBinding.inflate(inflater, container, false)
            .apply {
                lifecycleOwner = this@LearnDetailFragment
                Timber.d("$TAG DetFragment with phase = $phase")
                detailViewPager.adapter = adapter
                title=""

                val tabLayout = appBar.tabLayout
                tabLayout.tabMode = TabLayout.MODE_SCROLLABLE

                detailViewModel.liveCurrentItems.observe(viewLifecycleOwner, Observer {
                    it?.let {
                        val curType = 0
                        Timber.d("$TAG liveCurrentItems обновился = $it")
                        adapter.refreshItems(it)
                        //detailViewPager.offscreenPageLimit = it.size
                        //задаем именно smooyjScroll=false, иначе некорректно работает при возврате во фрагмент
                        detailViewPager.setCurrentItem(curType,false)
                        TabLayoutMediator(tabLayout, detailViewPager) { tab, position ->
                            tab.text = it[position].title
                        }.attach()

                    }
                })

                viewModel = detailViewModel
            }

        return binding.root
    }

    override fun onDestroyView() {
        uiUtilViewModel.showBottomNav()
        super.onDestroyView()
    }

    inner class DetailPagerAdapter (fragment: Fragment) : FragmentStateAdapter(fragment) {
        private var detailItems = listOf<MainDBItem>()

        override fun getItemCount(): Int {
            return detailItems.size
        }

        override fun createFragment(position: Int): Fragment {
            return LearnDetailItemFragment.newInstance(detailItems[position])
        }

        //передаем новые данные и оповещаем адаптер о необходимости обновления списка
        fun refreshItems(items: List<MainDBItem>) {
            Timber.d("$TAG Обновляем список в адаптере DetailPagerAdapter")
            detailItems = items
            notifyDataSetChanged()
        }
    }

}
