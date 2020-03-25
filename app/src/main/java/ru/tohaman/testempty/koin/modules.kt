package ru.tohaman.testempty.koin

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import ru.tohaman.testempty.ui.shared.UiUtilViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import ru.tohaman.testempty.dataSource.ItemsRepository
import ru.tohaman.testempty.dbase.MainDb
import ru.tohaman.testempty.dbase.entitys.BasicMove
import ru.tohaman.testempty.ui.learn.LearnDetailViewModel
import ru.tohaman.testempty.ui.learn.LearnViewModel

private const val DATABASE_NAME = "base.db"

val appModule = module{
    single<SharedPreferences> { androidContext().getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE) }
    single {
        Room.databaseBuilder(androidContext(), MainDb::class.java, DATABASE_NAME)
            .fallbackToDestructiveMigration() //На время разработки программы, каждый раз пересоздаем базу, вместо миграции
            //.addMigrations(MIGRATION_1_2)
            .build()
    }
    //В ItemRepository нужно передать сылку на dao, берем его предыдущего пункта, т.е. get<MainDb>
    single { ItemsRepository(get<MainDb>().mainDao, get<MainDb>().cubeTypesDao, get<MainDb>().movesDao)}

}

val viewModelsModule = module {
    viewModel { UiUtilViewModel() }
    viewModel { LearnViewModel(androidContext()) }
    viewModel { LearnDetailViewModel(androidContext()) }
}
