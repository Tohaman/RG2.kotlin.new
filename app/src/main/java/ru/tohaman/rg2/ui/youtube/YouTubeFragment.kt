package ru.tohaman.rg2.ui.youtube

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.tohaman.rg2.R

/**
 * A simple [Fragment] subclass.
 * Use the [YouTubeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class YouTubeFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_youtube, container, false)
    }

}