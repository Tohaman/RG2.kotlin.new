package ru.tohaman.testempty.ui.learn

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.tohaman.testempty.DebugTag.TAG

import ru.tohaman.testempty.databinding.FragmentLearnDetailMenuBinding
import ru.tohaman.testempty.ui.shared.UiUtilViewModel
import timber.log.Timber

class LearnDetailMenuFragment : Fragment() {
    private val args by navArgs<LearnDetailMenuFragmentArgs>()
    private val phaseId by lazy { args.id }
    private val phase by lazy { args.phase }
    private lateinit var binding: FragmentLearnDetailMenuBinding

    private val uiUtilViewModel by sharedViewModel<UiUtilViewModel>()
    private val learnViewModel by sharedViewModel<LearnViewModel>()
    private val detailViewModel by sharedViewModel<LearnDetailViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        uiUtilViewModel.hideBottomNav()
        binding = FragmentLearnDetailMenuBinding.inflate(inflater, container, false)
            .apply {
                lifecycleOwner = this@LearnDetailMenuFragment
                viewModel = detailViewModel.apply { setCurrentItems(phaseId, phase) }
                Timber.d("$TAG DetFragment with phase = $phase")
            }

        return binding.root
    }

    override fun onDestroyView() {
        uiUtilViewModel.showBottomNav()
        super.onDestroyView()
    }

}
