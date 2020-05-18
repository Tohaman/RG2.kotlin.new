package ru.tohaman.testempty.ui.info

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.adapters.ImageViewBindingAdapter.setImageDrawable
import com.smarteist.autoimageslider.IndicatorAnimations
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView

import ru.tohaman.testempty.R
import ru.tohaman.testempty.adapters.SliderAdapter
import ru.tohaman.testempty.databinding.FragmentInfoMiniHelpBinding

/**
 * A simple [Fragment] subclass.
 */
class InfoMiniHelpFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentInfoMiniHelpBinding.inflate(inflater, container, false)
            .apply {
//                imageSlider.setSliderTransformAnimation(SliderAnimations.FADETRANSFORMATION)
//                imageSlider.setIndicatorAnimation(IndicatorAnimations.THIN_WORM)
                val adapter = SliderAdapter()
                adapter.refreshItems(galleryDrawables.shuffled())
                imageSlider.sliderAdapter = adapter

//                imageSlider.setIndicatorAnimation(IndicatorAnimations.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
//                imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
//                imageSlider.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
//                imageSlider.setIndicatorSelectedColor(Color.WHITE);
//                imageSlider.setIndicatorUnselectedColor(Color.GRAY);
//                imageSlider.setScrollTimeInSec(4); //set scroll delay in seconds :
//                imageSlider.startAutoCycle();
            }

        return binding.root
    }

    private val galleryDrawables = listOf(
        R.drawable.frame_1,
        R.drawable.frame_2,
        R.drawable.frame_3,
        R.drawable.frame_4,
        R.drawable.frame_5,
        R.drawable.frame_6,
        R.drawable.frame_7,
        R.drawable.frame_8,
        R.drawable.frame_9
    )

}
