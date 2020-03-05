package ru.tohaman.testempty.koin

import org.koin.dsl.module
import ru.tohaman.testempty.ui.UiUtilViewModel
import org.koin.androidx.viewmodel.dsl.viewModel

val myModule = module{

}

val viewModelsModule = module {
    viewModel { UiUtilViewModel() }
}
