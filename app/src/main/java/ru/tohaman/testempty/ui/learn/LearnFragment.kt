package ru.tohaman.testempty.ui.learn

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.tohaman.testempty.DebugTag.TAG
import ru.tohaman.testempty.databinding.FragmentLearnBinding
import ru.tohaman.testempty.dbase.entitys.CubeType
import ru.tohaman.testempty.ui.MainActivity
import ru.tohaman.testempty.ui.shared.UiUtilViewModel
import timber.log.Timber


class LearnFragment : Fragment() {
    private val uiUtilViewModel by sharedViewModel<UiUtilViewModel>()
    private val learnViewModel by sharedViewModel<LearnViewModel>()

    private var exit: Boolean = false
    private lateinit var binding : FragmentLearnBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This callback will only be called when MyFragment is at least Started.
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    //TODO Пересмотреть вызов activity.finish на какой-нибудь super.onBackPressed
                    if (!learnViewModel.backOnePhase()) quitApp()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

        // The callback can be enabled or disabled here or in handleOnBackPressed()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        uiUtilViewModel.showBottomNav()
        val adapter = LearnPagerAdapter(this@LearnFragment)

        binding = FragmentLearnBinding.inflate(inflater, container, false)
            .apply {
                learnViewPager.adapter = adapter
                learnViewPager.offscreenPageLimit = 5

                learnViewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        learnViewModel.setCurrentCubeType(position)
                    }
                })

                val tabLayout = appBar.tabLayout
                tabLayout.tabMode = TabLayout.MODE_SCROLLABLE

                learnViewModel.liveDataCubeTypes.observe(viewLifecycleOwner, Observer {
                    it?.let {
                        val curType = learnViewModel.getCurrentType()
                        Timber.d("$TAG curType = $curType mutableCubeTypes = $it")
                        adapter.refreshItems(it)
                        //задаем именно smooyjScroll=false, чтобы сразу открывалась нужная страница, без анимации пролистывания
                        learnViewPager.setCurrentItem(curType,false)
                        TabLayoutMediator(tabLayout, learnViewPager) { tab, position ->
                            Timber.d("$TAG tabLayoutMediator = $tab position = $position")
                            tab.text = it[position].name
                        }.attach()
                    }
                })
                viewModel = learnViewModel
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
            Timber.d("$TAG Обновляем список в адаптере LearnPagerAdapter")
            cubeTypes = items
            notifyDataSetChanged()
        }
    }

    private fun quitApp() {
        if (exit) activity?.finish()
        else {
            //Тут еще можно вывести тост, что нажмите мол еще раз, чтобы выйти toast("Press Back again to exit.")
            exit = true
            Handler().postDelayed({ exit = false }, 2000)
        }
    }
}
