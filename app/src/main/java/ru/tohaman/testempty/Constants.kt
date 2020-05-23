package ru.tohaman.testempty

import ru.tohaman.testempty.dataSource.entitys.TipsItem

/**
 * Константы для записи в Preferences, чтобы отловить неверные указания в тексте на уровне компилятора
 */
object Constants {
    const val THEME = "theme"                               //Тема приложения (String)
    const val SHOW_FAB = "showFAB"                          //Отображать плавающую кнопку в основном меню (Boolean)
    const val TEXT_SIZE = "mainTextSize"                    //+ к изначально задуманному размеру текста в обучалках (Int)
    //Константы для увеличения/уменьшения размера текста
    const val startValue: Float = 0.7f //начальное значение размера шрифта
    const val step = 0.15f //шаг увеличения коэффициента

    //Настройки Генератора скрмблов
    const val CURRENT_SCRAMBLE = "currentScramble"          //Последний придуманный скрамбл (текущий) (String)
    const val BUFFER_EDGE = "isBufferEdgeSet"               //Переплавка буфера ребер (Boolean)
    const val BUFFER_CORNER = "isBufferCornerSet"           //Переплавка буфера углов (Boolean)
    const val SCRAMBLE_LENGTH = "scrambleLength"            //Длина скрамбла (Int)
    const val SHOW_SOLVING = "showSolving"                  //Показывать решение для скрамбла (Boolean)

    //Настройки таймера
    const val TIMER_DELAYED = "isTimerDelayed"              //Нужна задержка перед срабатыванием Готовности таймера? (Boolean)
    const val TIMER_ONE_HANDED = "isOneHanded"              //Однорукий тайиер? (True - однорукий, False - двурукий)
    const val TIMER_METRONOM = "isMetronomEnabled"          //Включен ли метроном (Boolean)
    const val TIMER_NEED_SCRAMBLE = "needScramble"          //Включено ли отображение скрамбла в таймере (Boolean)
    const val TIMER_NEED_BACK = "needBackButton"            //Отображнеи кнопки "назад" (Boolean)
    const val TIMER_METRONOM_FREQ = "metronomFrequency"     //Частота метронома (раз в минуту) (Int)

    //Настройки игры Угадай букву блайнда
    const val TRAINING_EDGES = "trainingEdges"              //Тренируем углы? (Boolean)
    const val TRAINING_CORNERS = "trainingCorners"          //Тренируем ребра? (Boolean)
    const val TRAINING_TIMER = "trainingTimer"              //Игра на время (Boolean)
    const val TRAINING_TIMER_TIME = "trainingTimerTime"     //Время в сек. на ответ (Int)

    //Основное меню
    const val FAVOURITES = "FAVOURITES"                     //Название избранного
    const val CUR_CUBE_TYPE = "currentCubeType"             //Текущая головоломка (пункт меню)

    //Меню Информации
    const val INFO_BOOKMARK = "infoBookmark"                //Текущая выбранная закладка

    //Используются в таблице AzbukaItems для azbukaName
    const val ANTONS_AZBUKA = "ANTONS_AZBUKA"               //Имя моей азбуки
    const val MAKSIMS_AZBUKA = "MAKSIMS_AZBUKA"             //Азбука Максима
    const val CURRENT_AZBUKA = "CURRENT_AZBUKA"             //Текущая азбука
    const val CUSTOM_AZBUKA = "CUSTOM_AZBUKA"               //Сохраненная пользовательская азбука

    val galleryDrawables = listOf(
        TipsItem(R.drawable.frame_1, "Обязательно прочитайте этот раздел"),
        TipsItem(R.drawable.frame_2, "Добавляйте в избранное отдельные этапы или головоломки целиком"),
        TipsItem(R.drawable.frame_3, "Забыли азбуку? Нажмите зеленую кнопку"),
        TipsItem(R.drawable.frame_4, "Создвайте свои комментарии к этапам, и вы их увидите в меню"),
        TipsItem(R.drawable.frame_5, "Щелкните по скрамблу в генератое, чтобы задать свой скрамбл"),
        TipsItem(R.drawable.frame_6, "Щелкните по скрамблу в таймере, чтобы его сменить"),
        TipsItem(R.drawable.frame_7, "Таймер можно поставить на паузу нажав на верхнюю панель"),
        TipsItem(R.drawable.frame_8, "При сохранении результата можно сразу задать свой комментарий"),
        TipsItem(R.drawable.frame_9, "Эту кнопку можно отключить в настройках программы")
    )

}