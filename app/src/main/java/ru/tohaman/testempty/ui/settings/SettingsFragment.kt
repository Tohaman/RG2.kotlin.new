package ru.tohaman.testempty.ui.settings


import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import ru.tohaman.testempty.R


/**
 * A simple [Fragment] subclass.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
//        runBlocking (Dispatchers.IO){
//            //Пересоздаем базу при каждом запуске этого фрагмента, чтобы не пересоздавать при каждом входе
//            context?.let { FillDB.reCreateDB(it) }
//        }
    }


    companion object {
        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }

}
