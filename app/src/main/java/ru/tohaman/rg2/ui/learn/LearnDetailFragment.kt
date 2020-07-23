package ru.tohaman.rg2.ui.learn

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.jetbrains.annotations.NotNull
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.tohaman.rg2.DebugTag.TAG
import ru.tohaman.rg2.databinding.FragmentLearnDetailBinding
import ru.tohaman.rg2.dbase.entitys.MainDBItem
import ru.tohaman.rg2.ui.games.GameStates
import ru.tohaman.rg2.ui.shared.UiUtilViewModel
import timber.log.Timber


class LearnDetailFragment : Fragment() {
    private val args by navArgs<LearnDetailFragmentArgs>()
    private val phaseId by lazy { args.id }
    private val phase by lazy { args.phase }
    private lateinit var binding: FragmentLearnDetailBinding
    //private var currentId = 0

    private val uiUtilViewModel by sharedViewModel<UiUtilViewModel>()
    private val detailViewModel by sharedViewModel<LearnDetailViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("$TAG Create LearnDetailFragment with $phase, $phaseId")
        detailViewModel.setCurrentItems(phase, phaseId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Timber.d("$TAG onCreateView")
        uiUtilViewModel.hideBottomNav()

        binding = FragmentLearnDetailBinding.inflate(inflater, container, false)
            .apply {
                lifecycleOwner = viewLifecycleOwner
                viewModel = detailViewModel

                //Задаем адаптер для viewPager
                val adapter = DetailPagerAdapter(this@LearnDetailFragment)
                detailViewPager.adapter = adapter
                detailViewPager.offscreenPageLimit = 3

                //Связываем вкладки сверху viewPager с viewPager
                tabLayout.tabMode = TabLayout.MODE_SCROLLABLE
                TabLayoutMediator(tabLayout, detailViewPager) { tab, position ->
                    tab.text = adapter.getData()[position].title
                }.attach()

                //Передаем во viewModel номер текущей страницы, если пользователь свайпнул
                detailViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        detailViewModel.currentId = position
                    }
                })

                //Подписываемся. Обновляем адаптер при изменении liveCurrentItems во viewModel
                detailViewModel.liveCurrentItems.observe(viewLifecycleOwner, Observer {
                    it?.let {
                        Timber.d("$TAG detailCurItems - ${it.size}, ${detailViewModel.currentId}")
                        adapter.refreshItems(it)
                        detailViewPager.setCurrentItem(detailViewModel.currentId,false)
                    }
                })

                //Блокируем открытие левого меню по свайпу, оставляем только открытие по нажатию на FAB
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                detailViewModel.closeLeftMenu()
                detailViewModel.isLeftMenuOpen.observe(viewLifecycleOwner, Observer {
                    it?.let {
                        if (it) drawerLayout.openDrawer(GravityCompat.START)
                        else drawerLayout.closeDrawer(GravityCompat.START)
                    }
                })

                detailViewModel.onLeftMenuItemPressing.observe(viewLifecycleOwner, Observer {
                    detailViewPager.setCurrentItem(detailViewModel.currentId,false)
                })

                interceptBackPressing()
            }

        return binding.root
    }

    private fun @NotNull FragmentLearnDetailBinding.interceptBackPressing() {
        // Вызываем этот колбэк при нажатии кнопки back (если открыто боковое меню, то закрываем его, иначе выходим)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true /* enabled by default */) {
            override fun handleOnBackPressed() {
                //т.е. isLeftMenuOpen.value может быть null, то сравниваем с true
                if (detailViewModel.isLeftMenuOpen.value == true)
                    drawerLayout.closeDrawer(GravityCompat.START)
                else
                    findNavController().popBackStack()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }


    inner class DetailPagerAdapter (fragment: Fragment) : FragmentStateAdapter(fragment) {
        private var detailItems = listOf<MainDBItem>()

        override fun getItemCount(): Int {
            //Timber.d("$TAG getItemCount = ${detailItems.size} - $detailItems")
            return detailItems.count()
        }

        override fun createFragment(position: Int): Fragment {
            return LearnDetailItemFragment.newInstance(detailViewModel.getNumByID(detailItems[position].id))
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

}
