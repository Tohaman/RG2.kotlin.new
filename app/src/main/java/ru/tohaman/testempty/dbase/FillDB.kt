package ru.tohaman.testempty.dbase

import android.content.Context
import ru.tohaman.testempty.R
import ru.tohaman.testempty.dataSource.ItemsRepository
import ru.tohaman.testempty.utils.DebugTag.TAG
import timber.log.Timber

class FillDB {
    companion object {
        private val repository = ItemsRepository()
        suspend fun reCreateDB(context: Context) {
            repository.clearMainTable()
            Timber.tag(TAG).d("Основная таблица очищена")
            insertPhasesToMainTable(context)
            Timber.tag(TAG).d("Основная таблица заполнена")
            insertCurrentPhases()
            Timber.tag(TAG).d("Доп.таблица заполнена. База создана")
        }

        //TODO Возможно лишняя таблица, проверить и удалить, если будет не нужна
        private suspend fun insertCurrentPhases() {
            repository.clearCurrentTable()
            val curPhaseList = repository.getPhase("BEGIN")
            repository.insertItems2CurrentTable(curPhaseList)
        }

        private suspend fun insertPhasesToMainTable(context: Context) {
            //subMenus (пункты меню)
            phaseInit("G2F", R.array.g2f_title, R.array.g2f_icon, R.array.g2f_descr, R.array.g2f_url, context)
            phaseInit("MAIN3X3", R.array.main3x3_title, R.array.main3x3_icon, R.array.main3x3_descr, R.array.main3x3_url, context)
            //Phases (обучалки)
            phaseInit("AXIS", R.array.axis_title, R.array.axis_icon, R.array.axis_descr, R.array.axis_url, context)
            phaseInit("BEGIN", R.array.begin_title, R.array.begin_icon, R.array.begin_descr, R.array.begin_url, context)
            phaseInit("ROZOV", R.array.begin_rozov_title, R.array.begin_rozov_icon, R.array.begin_rozov_descr, R.array.begin_rozov_url, context)
        }


        // Инициализация фазы, с заданными массивами Заголовков, Иконок, Описаний, ютуб-ссылок
        private suspend fun phaseInit(phase: String, titleArray: Int, iconArray: Int, descriptionArray: Int, urlArray: Int, context: Context, comment : Int = 0) {
            val emptyComment = Array (100) {""}
            val titles =  context.resources.getStringArray(titleArray)
            val icons = context.resources.obtainTypedArray (iconArray)
            val descriptions = context.resources.obtainTypedArray (descriptionArray)
            val urls = context.resources.getStringArray(urlArray)
            val comments = if (comment != 0) { context.resources.getStringArray(comment) } else { emptyComment}
            for (i in titles.indices) {
                val defaultIcon = R.drawable.ic_alert
                val iconID = icons.getResourceId(i, defaultIcon)
                val descriptionID = descriptions.getResourceId(i, 0)
                val listPager = MainDBItem(phase, i, titles[i], iconID, descriptionID, urls[i], comments[i])
                repository.insert(listPager)
            }
            icons.recycle()
            descriptions.recycle()
        }
    }
}