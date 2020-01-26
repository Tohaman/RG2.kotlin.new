package ru.tohaman.testempty.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.Navigation
import ru.tohaman.testempty.R

class TmpFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tmp, container, false)

        view.findViewById<Button>(R.id.button2).setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.listViewFragment)
        }
        return view
    }
}
