package ru.tohaman.testempty.ui.learn

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
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

    private val uiUtilViewModel by sharedViewModel<UiUtilViewModel>()
    private val learnViewModel by sharedViewModel<LearnViewModel>()
    private val detailViewModel by sharedViewModel<LearnDetailViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        uiUtilViewModel.hideBottomNav()
        binding = FragmentLearnDetailBinding.inflate(inflater, container, false)
            .apply {
                lifecycleOwner = this@LearnDetailFragment
                viewModel = detailViewModel.apply { setCurrentItems(phaseId, phase) }
                Timber.d("$TAG DetFragment with phase = $phase")
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
            Timber.d("$TAG Обновляем список в адаптере LearnPagerAdapter")
            detailItems = items
            notifyDataSetChanged()
        }
    }

}
