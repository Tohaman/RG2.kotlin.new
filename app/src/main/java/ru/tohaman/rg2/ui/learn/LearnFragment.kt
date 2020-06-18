package ru.tohaman.rg2.ui.learn

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.tohaman.rg2.DebugTag.TAG
import ru.tohaman.rg2.R
import ru.tohaman.rg2.databinding.FragmentLearnBinding
import ru.tohaman.rg2.dbase.entitys.CubeType
import ru.tohaman.rg2.ui.shared.UiUtilViewModel
import timber.log.Timber


class LearnFragment : Fragment() {
    private val uiUtilViewModel by sharedViewModel<UiUtilViewModel>()
    private val learnViewModel by sharedViewModel<LearnViewModel>()
    private val miniHelpViewModel by sharedViewModel<MiniHelpViewModel>()

    private var exit: Boolean = false
    private lateinit var binding : FragmentLearnBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This callback will only be called when MyFragment is at least Started.
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    if (!learnViewModel.canReturnToOnePhaseBack()) quitApp()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

        learnViewModel.initPhasesToArray()
        // The callback can be enabled or disabled here or in handleOnBackPressed()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Timber.d("$TAG bottomNavShow")
        uiUtilViewModel.showBottomNav()
        val adapter = LearnPagerAdapter(this@LearnFragment)

        binding = FragmentLearnBinding.inflate(inflater, container, false)
            .apply {
                viewModel = learnViewModel
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
                TabLayoutMediator(tabLayout, learnViewPager) { tab, position ->
                    //Timber.d("$TAG tabLayoutMediator = $tab position = $position")
                    tab.text = adapter.getData()[position].name
                }.attach()

                learnViewModel.liveDataCubeTypes.observe(viewLifecycleOwner, Observer {
                    it?.let {
                        val curType = learnViewModel.getCurrentType()
                        Timber.d("$TAG curType = $curType mutableCubeTypes = $it")
                        adapter.refreshItems(it)
                        //задаем именно smoothScroll=false, чтобы сразу открывалась нужная страница, без анимации пролистывания
                        learnViewPager.setCurrentItem(curType,false)
                    }
                })

                miniHelpViewModel.notShowMore.observe(viewLifecycleOwner, Observer {
                    it?.let {
                        if (!it) findNavController().navigate(R.id.action_destLearn_to_miniHelpDialog)
                    }
                })

//                val notShowMiniHelp = miniHelpViewModel.notShowMore.value ?: false
//                if (!notShowMiniHelp) findNavController().navigate(R.id.action_destLearn_to_miniHelpDialog)

//                val number = miniHelpViewModel.showingMiniHelpNumber()
//                number?.let {
//                    val action = LearnFragmentDirections.actionDestLearnToMiniHelpDialog(it)
//                    findNavController().navigate(action)
//                }
            }

        return binding.root
    }

    override fun onResume() {
        learnViewModel.initPhasesToArray()
        Timber.d("$TAG bottomNavShow")
        miniHelpViewModel.checkMiniHelp()
        uiUtilViewModel.showBottomNav()
        super.onResume()
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

        fun getData(): List<CubeType> = cubeTypes

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
