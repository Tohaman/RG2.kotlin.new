package ru.tohaman.rg2.koin

import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.room.Room
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.tohaman.rg2.dataSource.ItemsRepository
import ru.tohaman.rg2.dbase.*
import ru.tohaman.rg2.ui.MigrationsViewModel
import ru.tohaman.rg2.ui.games.*
import ru.tohaman.rg2.ui.info.DonateViewModel
import ru.tohaman.rg2.ui.info.InfoViewModel
import ru.tohaman.rg2.ui.learn.LearnDetailViewModel
import ru.tohaman.rg2.ui.learn.LearnViewModel
import ru.tohaman.rg2.ui.learn.MiniHelpViewModel
import ru.tohaman.rg2.ui.settings.SettingsViewModel
import ru.tohaman.rg2.ui.shared.UiUtilViewModel
import ru.tohaman.rg2.ui.youtube.YouTubeViewModel

private const val DATABASE_NAME = "room_base.db"
private const val OLD_DATABASE_NAME = "base.db"

val appModule = module{
    single<SharedPreferences> {
        //androidContext().getSharedPreferences("${androidContext().applicationInfo.packageName}_preferences", Context.MODE_PRIVATE)
        PreferenceManager.getDefaultSharedPreferences(androidContext())
    }
    single {
        Room.databaseBuilder(androidContext(), MainDb::class.java, DATABASE_NAME)
            .fallbackToDestructiveMigration() //На время разработки программы, каждый раз пересоздаем базу, вместо миграции
            //.addMigrations(MIGRATION_5_6)
            .build()
    }
    single {
        Room.databaseBuilder(androidContext(), OldDb::class.java, OLD_DATABASE_NAME)
            //.fallbackToDestructiveMigration() //На время разработки программы, каждый раз пересоздаем базу, вместо миграции
            .addMigrations(MIGRATION_1_4, MIGRATION_2_3, MIGRATION_3_4)
            .build()
    }
    //В ItemRepository нужно передать сылку на dao, берем его предыдущего пункта, т.е. get<MainDb>
    single { ItemsRepository(androidContext(),
                            get<MainDb>().mainDao,
                            get<MainDb>().cubeTypesDao,
                            get<MainDb>().movesDao,
                            get<MainDb>().azbukaDao,
                            get<MainDb>().timeNoteDao,
                            get<MainDb>().pllGameDao,
                            get<OldDb>().oldTimeDao,
                            get<OldDb>().oldBaseDao)}

}

val viewModelsModule = module {
    viewModel { UiUtilViewModel() }
    viewModel { GamesViewModel() }
    viewModel { SettingsViewModel() }
    viewModel { MiniHelpViewModel() }
    viewModel { YouTubeViewModel() }
    viewModel { MigrationsViewModel(androidApplication()) }
    viewModel { DonateViewModel(androidApplication()) }
    viewModel { PllTrainerViewModel(androidApplication()) }
    viewModel { InfoViewModel(androidApplication()) }
    viewModel { AzbukaTrainerViewModel(androidApplication()) }
    viewModel { ScrambleGeneratorViewModel() }
    viewModel { TimerViewModel(androidApplication()) }
    viewModel { TimerResultViewModel() }
    viewModel { LearnViewModel(androidContext()) }
    viewModel { LearnDetailViewModel(androidContext()) }
}
