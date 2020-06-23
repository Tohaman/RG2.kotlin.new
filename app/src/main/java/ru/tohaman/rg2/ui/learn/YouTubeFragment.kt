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
import ru.tohaman.rg2.Constants
import ru.tohaman.rg2.DebugTag.TAG
import ru.tohaman.rg2.R
import ru.tohaman.rg2.databinding.ActivityYoutubeBinding
import ru.tohaman.rg2.databinding.FragmentLearnBinding
import ru.tohaman.rg2.dbase.entitys.CubeType
import ru.tohaman.rg2.ui.info.DonateViewModel
import ru.tohaman.rg2.ui.shared.UiUtilViewModel
import timber.log.Timber


class YouTubeFragment : Fragment() {

    private lateinit var binding : ActivityYoutubeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = ActivityYoutubeBinding.inflate(inflater, container, false)
            .apply {
            }

        return binding.root
    }
}
