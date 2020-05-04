package ru.tohaman.testempty.dbase

import android.content.Context
import org.koin.java.KoinJavaComponent.inject
import ru.tohaman.testempty.Constants.ANTONS_AZBUKA
import ru.tohaman.testempty.Constants.CURRENT_AZBUKA
import ru.tohaman.testempty.Constants.CUSTOM_AZBUKA
import ru.tohaman.testempty.Constants.MAKSIMS_AZBUKA
import ru.tohaman.testempty.R
import ru.tohaman.testempty.DebugTag.TAG
import ru.tohaman.testempty.dataSource.*
import ru.tohaman.testempty.dbase.entitys.BasicMove
import ru.tohaman.testempty.dbase.entitys.CubeType
import ru.tohaman.testempty.dbase.entitys.MainDBItem
import timber.log.Timber

class FillDB {
    companion object {
        private val repository : ItemsRepository by inject(ItemsRepository::class.java)

        suspend fun reCreateDB(context: Context) {
            //очищаем все таблицы
            repository.clearMainTable()
            repository.clearTypesTable()
            repository.clearMovesTable()
            Timber.d( "$TAG Основная таблица очищена")

            insertWrongItem()                       //добавляем "заглушку" неверную запись
            insertPhasesToMainTable(context)        //добавляем
            updateTestComments()                    //добавляем тестовые комментарии
            updateTestFavourites()                  //добавляем тестовое избранное
            azbukaInit()                            //Инициализируем азбуку
            Timber.d( "$TAG Основная таблица заполнена")
        }

        private suspend fun insertWrongItem() {
            repository.insert2Main(MainDBItem("WRONG", 0, "Что-то пошло не так", R.drawable.ic_warning, R.string.wrong))
        }

        private suspend fun insertPhasesToMainTable(context: Context) {
            //Заполняем типы головоломок
            cubeTypesInit(context)
            //subMenus (пункты меню)
            phaseInit("BIG_MAIN", R.array.big_main_title, R.array.big_main_icon, R.array.big_main_descr, R.array.big_main_url, context)
            phaseInit("BIG_CUBES",R.array.big_cubes_title,R.array.big_cubes_icon,R.array.big_cubes_descr,R.array.big_cubes_url,context)
            phaseInit("G2F", R.array.g2f_title, R.array.g2f_icon, R.array.g2f_descr, R.array.g2f_url, context)
            phaseInit("MAIN_F2L", R.array.main_f2l_title, R.array.main_f2l_icon, R.array.main_f2l_descr, R.array.main_f2l_url, context)
            phaseInit("MAIN2X2", R.array.main2x2_title, R.array.main2x2_icon, R.array.main2x2_descr, R.array.main2x2_url, context)
            phaseInit("MAIN3X3", R.array.main3x3_title, R.array.main3x3_icon, R.array.main3x3_descr, R.array.main3x3_url, context)
            phaseInit("MAIN_OTHER", R.array.other_title, R.array.other_icon, R.array.other_descr, R.array.other_url, context)
            phaseInit("MAIN_SKEWB", R.array.main_skewb_title, R.array.main_skewb_icon, R.array.main_skewb_descr, R.array.main_skewb_url, context)
            phaseInit("OTHER3X3", R.array.other3x3_title, R.array.other3x3_icon, R.array.other3x3_descr, R.array.other3x3_url, context)

            //Phases (обучалки)
            phaseInit("ACCEL",R.array.accel_title,R.array.accel_icon,R.array.accel_descr,R.array.accel_url,context)
            phaseInit("ADV2X2", R.array.adv2x2_title, R.array.adv2x2_icon, R.array.adv2x2_descr, R.array.adv2x2_url, context)
            phaseInit("ADVF2L",R.array.advf2l_title,R.array.advf2l_icon,R.array.advf2l_descr,R.array.advf2l_url,context)
            phaseInit("AXIS", R.array.axis_title, R.array.axis_icon, R.array.axis_descr, R.array.axis_url, context)
            phaseInit("BEGIN", R.array.begin_title, R.array.begin_icon, R.array.begin_descr, R.array.begin_url, context)
            phaseInit("BEGIN2X2", R.array.begin2x2_title, R.array.begin2x2_icon, R.array.begin2x2_descr, R.array.begin2x2_url, context)
            phaseInit("BEGIN4X4",R.array.begin4_title,R.array.begin4_icon,R.array.begin4_descr,R.array.begin4_url,context)
            phaseInit("BEGIN5X5",R.array.begin5_title,R.array.begin5_icon,R.array.begin5_descr,R.array.begin5_url,context)
            phaseInit("BLIND", R.array.blind_title, R.array.blind_icon,R.array.blind_descr,R.array.blind_url,context)
            phaseInit("INTF2L", R.array.int_f2l_title, R.array.int_f2l_icon, R.array.int_f2l_descr, R.array.int_f2l_url, context)
            phaseInit("MIRROR", R.array.mirror_title, R.array.mirror_icon, R.array.mirror_descr, R.array.mirror_url, context)
            phaseInit("OLL", R.array.oll_title, R.array.oll_icon, R.array.oll_descr, R.array.oll_url, context)
            phaseInit("ORTEGA", R.array.ortega_title, R.array.ortega_icon, R.array.ortega_descr, R.array.ortega_url, context)
            phaseInit("PATTERNS", R.array.patterns_title, R.array.patterns_icon, R.array.patterns_descr, R.array.patterns_url, context, R.array.patterns_comment)
            phaseInit("ROZOV", R.array.begin_rozov_title, R.array.begin_rozov_icon, R.array.begin_rozov_descr, R.array.begin_rozov_url, context)

            //Подсказки по азбуке вращений
            basicInit("BASIC3X3", R.array.basic_3x3_moves, R.array.basic_3x3_icons, R.array.basic_3x3_toasts, context)
            basicInit("BASIC4X4", R.array.basic_4x4_moves, R.array.basic_4x4_icons, R.array.basic_4x4_toasts, context)
            basicInit("BASIC5X5", R.array.basic_5x5_moves, R.array.basic_5x5_icons, R.array.basic_5x5_toasts, context)
            basicInit("BASIC_CLOVER", R.array.basic_clover_moves, R.array.basic_clover_icons, R.array.basic_clover_toasts, context)
            basicInit("BASIC_CONTAINER", R.array.basic_container_moves, R.array.basic_container_icons, R.array.basic_container_toasts, context)
            basicInit("BASIC_IVY", R.array.basic_ivy_moves, R.array.basic_ivy_icons, R.array.basic_ivy_toasts, context)
            basicInit("BASIC_PYRAMINX", R.array.basic_pyraminx_moves, R.array.basic_pyraminx_icons, R.array.basic_pyraminx_toasts, context)
            basicInit("BASIC_REDI", R.array.basic_redi_moves, R.array.basic_redi_icons, R.array.basic_redi_toasts, context)
            basicInit("BASIC_SKEWB", R.array.basic_skewb_moves, R.array.basic_skewb_icons, R.array.basic_skewb_toasts, context)
            basicInit("BASIC_SQUARE", R.array.basic_square_moves, R.array.basic_square_icons, R.array.basic_square_toasts, context)

            //Фазы для меню мини-Игр
            phaseInit("GAMES", R.array.games_title, R.array.games_icon, R.array.games_help, R.array.games_url, context)
        }

        private suspend fun updateTestComments() {
            val item1 = repository.getItem("MAIN3X3", 0)
            val item2 = repository.getItem("ROZOV", 0)
            val item3 = repository.getItem("ROZOV", 1)
            item1.comment = "Обязательно надо прочитать"
            item2.comment = "Основные движения + Пиф-паф (R U R' U')"
            item3.comment = "Английский пиф-паф (R' F R F')"
            repository.updateMainItem(item1)
            repository.updateMainItem(item2)
            repository.updateMainItem(item3)
        }

        private suspend fun updateTestFavourites() {
            val item1 = repository.getItem("PATTERNS", 1)
            val item2 = repository.getItem("AXIS", 0)
            val item3 = repository.getItem("BEGIN", 4)
            val item4 = repository.getItem("BIG_CUBES", 1)
            item1.favComment = "Красивый узор"
            item1.isFavourite = true
            item1.subId = 0
            item2.favComment = "Прикольная головоломка"
            item2.isFavourite = true
            item2.subId = 1
            item3.favComment = "Пиф-паф"
            item3.isFavourite = true
            item3.subId = 2
            item4.isFavourite = true
            item4.subId = 3
            val items = listOf(item1, item2, item3, item4)
            repository.updateMainItem(items)
        }


        // Инициализация фазы, с заданными массивами Заголовков, Иконок, Описаний, ютуб-ссылок
        private suspend fun phaseInit(phase: String, titleArray: Int, iconArray: Int, descriptionArray: Int, urlArray: Int, context: Context, comment : Int = 0) {
            val emptyComment = Array (100) {""}
            val titles =  context.resources.getStringArray(titleArray)
            val icons = context.resources.obtainTypedArray (iconArray)
            val descriptions = context.resources.obtainTypedArray (descriptionArray)
            val urls = context.resources.getStringArray(urlArray)
            val comments = if (comment != 0) { context.resources.getStringArray(comment) } else { emptyComment}
            val list = (titles.indices).map { i ->
                val defaultIcon = R.drawable.ic_alert
                val iconID = icons.getResourceId(i, defaultIcon)
                val descriptionID = descriptions.getResourceId(i, 0)
                MainDBItem(
                    phase,
                    i,
                    titles[i],
                    iconID,
                    descriptionID,
                    urls[i],
                    comments[i]
                )
            }.apply {  icons.recycle(); descriptions.recycle() }
            repository.insert2Main(list)
        }

        private suspend fun basicInit(type: String, movesArray: Int, iconsArray: Int, toastsArray: Int, context: Context) {
            val moves = context.resources.getStringArray(movesArray)
            val icons = context.resources.obtainTypedArray (iconsArray)
            val toasts = context.resources.getStringArray(toastsArray)
            val movesList = (moves.indices).map { i ->
                val iconId = icons.getResourceId(i, R.drawable.ic_alert)
                BasicMove(i, type, moves[i], iconId, toasts[i])
            }
            repository.insert2Moves(movesList)
        }

        private suspend fun cubeTypesInit(context: Context) {
            repository.insert2Type(getTypesInit(context))
        }

        private fun getTypesInit(context: Context) : List<CubeType> {
            val names = context.resources.getStringArray(R.array.ct_names)
            val initPhases = context.resources.getStringArray(R.array.ct_init_phases)
            val curPhases = context.resources.getStringArray(R.array.ct_current_phases)
            return (names.indices).map {i ->
                CubeType(i, names[i], initPhases[i], curPhases[i])
            }.apply {  }
        }

        private suspend fun azbukaInit() {
            val antonsAzbuka = getMyAzbuka()
            val maksimsAzbuka = getMaximAzbuka()
            var cube = resetCube()
            var dbAzbuka = setAzbukaDBItemFromSimple(antonsAzbuka, cube, ANTONS_AZBUKA)
            repository.insertAzbuka(dbAzbuka)
            dbAzbuka = setAzbukaDBItemFromSimple(maksimsAzbuka, cube, MAKSIMS_AZBUKA)
            repository.insertAzbuka(dbAzbuka)
            cube = moveZ(cube)
            cube = moveZ(cube)
            dbAzbuka = setAzbukaDBItemFromSimple(antonsAzbuka, cube, CURRENT_AZBUKA)
            repository.insertAzbuka(dbAzbuka)
            dbAzbuka = setAzbukaDBItemFromSimple(antonsAzbuka, cube, CUSTOM_AZBUKA)
            repository.insertAzbuka(dbAzbuka)
        }

    }
}