package ru.tohaman.testempty.dbase

import android.content.Context
import org.koin.java.KoinJavaComponent.inject
import ru.tohaman.testempty.R
import ru.tohaman.testempty.dataSource.ItemsRepository
import ru.tohaman.testempty.DebugTag.TAG
import ru.tohaman.testempty.dbase.entitys.CubeType
import ru.tohaman.testempty.dbase.entitys.MainDBItem
import timber.log.Timber

class FillDB {
    companion object {
        private val repository : ItemsRepository by inject(ItemsRepository::class.java)

        suspend fun reCreateDB(context: Context) {
            repository.clearMainTable()
            repository.clearTypesTable()
            Timber.d( "$TAG Основная таблица очищена")
            insertWrongItem()
            insertPhasesToMainTable(context)
            updateComments()
            Timber.d( "$TAG Основная таблица заполнена")
            //insertCurrentPhases()
            //Timber.d( "$TAG Доп.таблица заполнена. База создана")
        }

        //TODO Возможно лишняя таблица, проверить и удалить, если будет не нужна
        private suspend fun insertCurrentPhases() {
            repository.clearCurrentTable()
            val curPhaseList = repository.getPhase("BEGIN")
            repository.insertItems2CurrentTable(curPhaseList)
        }

        private suspend fun insertWrongItem() {
            repository.insert2Main(MainDBItem("WRONG", 0, "Что-то пошло не так", R.drawable.ic_warning, R.string.wrong))
        }

        private suspend fun insertPhasesToMainTable(context: Context) {
            //Заполняем типы головоломок
            cubeTypesInit(context)
            //subMenus (пункты меню)
            phaseInit("BIG_MAIN", R.array.big_main_title, R.array.big_main_icon, R.array.big_main_descr, R.array.big_main_url, context)
            phaseInit("G2F", R.array.g2f_title, R.array.g2f_icon, R.array.g2f_descr, R.array.g2f_url, context)
            phaseInit("MAIN2X2", R.array.main2x2_title, R.array.main2x2_icon, R.array.main2x2_descr, R.array.main2x2_url, context)
            phaseInit("MAIN3X3", R.array.main3x3_title, R.array.main3x3_icon, R.array.main3x3_descr, R.array.main3x3_url, context)
            phaseInit("MAIN_F2L", R.array.main_f2l_title, R.array.main_f2l_icon, R.array.main_f2l_descr, R.array.main_f2l_url, context)
            phaseInit("OTHER3X3", R.array.other3x3_title, R.array.other3x3_icon, R.array.other3x3_descr, R.array.other3x3_url, context)
            phaseInit("OTHER", R.array.other_title, R.array.other_icon, R.array.other_descr, R.array.other_url, context)

            //Phases (обучалки)
            phaseInit("AXIS", R.array.axis_title, R.array.axis_icon, R.array.axis_descr, R.array.axis_url, context)
            phaseInit("BEGIN", R.array.begin_title, R.array.begin_icon, R.array.begin_descr, R.array.begin_url, context)
            phaseInit("BEGIN2X2", R.array.begin2x2_title, R.array.begin2x2_icon, R.array.begin2x2_descr, R.array.begin2x2_url, context)
            phaseInit("INTF2L", R.array.int_f2l_title, R.array.int_f2l_icon, R.array.int_f2l_descr, R.array.int_f2l_url, context)
            phaseInit("ROZOV", R.array.begin_rozov_title, R.array.begin_rozov_icon, R.array.begin_rozov_descr, R.array.begin_rozov_url, context)
            phaseInit("PATTERNS", R.array.patterns_title, R.array.patterns_icon, R.array.patterns_descr, R.array.patterns_url, context, R.array.patterns_comment)
        }

        private suspend fun updateComments() {
            val item = repository.getItem("MAIN3X3", 0)
            item.comment = "some Comment"
            repository.updateMainItem(item)
        }


        // Инициализация фазы, с заданными массивами Заголовков, Иконок, Описаний, ютуб-ссылок
        private suspend fun phaseInit(phase: String, titleArray: Int, iconArray: Int, descriptionArray: Int, urlArray: Int, context: Context, comment : Int = 0) {
            val emptyComment = Array (100) {""}
            val titles =  context.resources.getStringArray(titleArray)
            val icons = context.resources.obtainTypedArray (iconArray)
            val descriptions = context.resources.obtainTypedArray (descriptionArray)
            val urls = context.resources.getStringArray(urlArray)
            val comments = if (comment != 0) { context.resources.getStringArray(comment) } else { emptyComment}
            val list = (titles.indices).map { i->
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
    }
}