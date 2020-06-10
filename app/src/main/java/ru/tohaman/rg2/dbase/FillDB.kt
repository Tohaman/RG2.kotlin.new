package ru.tohaman.rg2.dbase

import android.content.Context
import org.koin.java.KoinJavaComponent.inject
import ru.tohaman.rg2.Constants.ANTONS_AZBUKA
import ru.tohaman.rg2.Constants.CURRENT_AZBUKA
import ru.tohaman.rg2.Constants.CUSTOM_AZBUKA
import ru.tohaman.rg2.Constants.MAKSIMS_AZBUKA
import ru.tohaman.rg2.R
import ru.tohaman.rg2.DebugTag.TAG
import ru.tohaman.rg2.dataSource.*
import ru.tohaman.rg2.dbase.entitys.*
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
            timeNoteInit()                          //добавляем тестовые записи для таблицы результатов сборки
            pllGameItemsInit(context)                      //добавляем записи для угадай PLL
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
            phaseInit("MAIN_PYRAMINX", R.array.main_pyraminx_title,R.array.main_pyraminx_icon,R.array.main_pyraminx_descr,R.array.main_pyraminx_url,context)
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
            phaseInit("BRICK", R.array.brick_title, R.array.brick_icon,R.array.brick_descr,R.array.brick_url,context)
            phaseInit("CLL",R.array.cll_title,R.array.cll_icon,R.array.cll_descr,R.array.cll_url,context,R.array.cll_comment)
            phaseInit("CLOVER", R.array.clover_title, R.array.clover_icon,R.array.clover_descr,R.array.clover_url,context)
            phaseInit("COLL",R.array.coll_title,R.array.coll_icon,R.array.coll_descr,R.array.coll_url,context,R.array.coll_comment)
            phaseInit("CONTAINER", R.array.container_title, R.array.container_icon,R.array.container_descr,R.array.container_url,context)
            phaseInit("CROSS", R.array.cross_title, R.array.cross_icon,R.array.cross_descr,R.array.cross_url,context)
            phaseInit("CUB2X2X3", R.array.cub_2x2x3_title, R.array.cub_2x2x3_icon,R.array.cub_2x2x3_descr,R.array.cub_2x2x3_url,context)
            phaseInit("CUB_3X3X2", R.array.cub_3x3x2_title, R.array.cub_3x3x2_icon,R.array.cub_3x3x2_descr,R.array.cub_3x3x2_url,context)
            phaseInit("CYLINDER", R.array.cylinder_title, R.array.cylinder_icon,R.array.cylinder_descr,R.array.cylinder_url,context)
            phaseInit("F2L",R.array.f2l_title,R.array.f2l_icon,R.array.f2l_descr,R.array.f2l_url,context)
            phaseInit("FISHER", R.array.fisher_title, R.array.fisher_icon,R.array.fisher_descr,R.array.fisher_url,context)
            phaseInit("GEAR", R.array.gear_title, R.array.gear_icon,R.array.gear_descr,R.array.gear_url,context)
            phaseInit("GHOST", R.array.ghost_title, R.array.ghost_icon,R.array.ghost_descr,R.array.ghost_url,context)
            phaseInit("INTF2L", R.array.int_f2l_title, R.array.int_f2l_icon, R.array.int_f2l_descr, R.array.int_f2l_url, context)
            phaseInit("IVY", R.array.ivy_title, R.array.ivy_icon,R.array.ivy_descr,R.array.ivy_url,context)
            phaseInit("KEYHOLE", R.array.keyhole_title, R.array.keyhole_icon,R.array.keyhole_descr,R.array.keyhole_url,context)
            phaseInit("MEGAMINX", R.array.megaminx_title, R.array.megaminx_icon,R.array.megaminx_descr,R.array.megaminx_url,context)
            phaseInit("MIRROR", R.array.mirror_title, R.array.mirror_icon, R.array.mirror_descr, R.array.mirror_url, context)
            phaseInit("OLL", R.array.oll_title, R.array.oll_icon, R.array.oll_descr, R.array.oll_url, context)
            phaseInit("ORTEGA", R.array.ortega_title, R.array.ortega_icon, R.array.ortega_descr, R.array.ortega_url, context)
            phaseInit("PATTERNS", R.array.patterns_title, R.array.patterns_icon, R.array.patterns_descr, R.array.patterns_url, context, R.array.patterns_comment)
            phaseInit("PENROSE", R.array.penrose_title, R.array.penrose_icon, R.array.penrose_descr, R.array.penrose_url, context)
            phaseInit("PENTACLE", R.array.pentacle_title, R.array.pentacle_icon, R.array.pentacle_descr, R.array.pentacle_url, context)
            phaseInit("PLL", R.array.pll_title, R.array.pll_icon, R.array.pll_descr, R.array.pll_url, context)
            phaseInit("PRISMA", R.array.prisma_title, R.array.prisma_icon, R.array.prisma_descr, R.array.prisma_url, context)
            phaseInit("PYRAMINX", R.array.pyraminx_title, R.array.pyraminx_icon,R.array.pyraminx_descr,R.array.pyraminx_url,context)
            phaseInit("PYRAMORPHIX", R.array.pyramorphix_title, R.array.pyramorphix_icon,R.array.pyramorphix_descr,R.array.pyramorphix_url,context)
            phaseInit("REDI", R.array.redi_title, R.array.redi_icon,R.array.redi_descr,R.array.redi_url,context)
            phaseInit("ROZOV", R.array.begin_rozov_title, R.array.begin_rozov_icon, R.array.begin_rozov_descr, R.array.begin_rozov_url, context)
            phaseInit("ROUX",R.array.roux_title,R.array.roux_icon,R.array.roux_descr,R.array.roux_url,context)
            phaseInit("SKEWB", R.array.skewb_title, R.array.skewb_icon,R.array.skewb_descr,R.array.skewb_url,context)
            phaseInit("SQUARE", R.array.square_title, R.array.square_icon,R.array.square_descr,R.array.square_url,context)
            phaseInit("SQ_STAR", R.array.sq_star_title, R.array.sq_star_icon,R.array.sq_star_descr,R.array.sq_star_url,context)
            phaseInit("SUDOKU", R.array.sudoku_title, R.array.sudoku_icon,R.array.sudoku_descr,R.array.sudoku_url,context)
            phaseInit("TW_CUBE", R.array.tw_cube_title, R.array.tw_cube_icon,R.array.tw_cube_descr,R.array.tw_cube_url,context)
            phaseInit("TW_SKEWB", R.array.tw_skewb_title, R.array.tw_skewb_icon,R.array.tw_skewb_descr,R.array.tw_skewb_url,context)
            phaseInit("WINDMILL", R.array.windmill_title, R.array.windmill_icon,R.array.windmill_descr,R.array.windmill_url,context)
            phaseInit("YAU4X4",R.array.yau_4x4_title,R.array.yau_4x4_icon,R.array.yau_4x4_descr,R.array.yau_4x4_url,context)

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
            icons.recycle()
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

        private suspend fun timeNoteInit() {
//            repository.deleteAllTimeNotes()
//            val t1 = TimeNoteItem(0,"0:10:03", Calendar.getInstance(), "R U R' U'", "Какой-то коммент")
//            val t2 = TimeNoteItem(0,"0:30:03", Calendar.getInstance(), "R F L B U2 L B' R F' D B R L F D R' D L", "Другой коммент")
//            val t3 = TimeNoteItem(0,"0:20:16", Calendar.getInstance(), "R F L B U2 L B' R F' D B R L F", "Третий коммент")
//            repository.insertTimeNote(t1)
//            repository.insertTimeNote(t2)
//            repository.insertTimeNote(t3)
        }

        private suspend fun pllGameItemsInit(context: Context) {
            repository.deletePllGameItems()
            val intNames = context.resources.getStringArray(R.array.pll_international_name)
            val maximNames = context.resources.getStringArray(R.array.pll_maxim_name)
            val scrambles = context.resources.getStringArray(R.array.pll_scramble)
            val pllImages = context.resources.obtainTypedArray(R.array.pll_image)
            val items = mutableListOf<PllGameItem>()
            intNames.indices.map {
                val iconId = pllImages.getResourceId(it, R.drawable.ic_alert)
                val item = PllGameItem(it, intNames[it], maximNames[it], "${maximNames[it]} (${intNames[it]})", "${maximNames[it]} (${intNames[it]})", scrambles[it], iconId, true)
                items.add(item)
            }
            repository.insertPllGameItem(items)
            pllImages.recycle()
        }
    }
}