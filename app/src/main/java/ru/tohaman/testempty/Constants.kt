package ru.tohaman.testempty

/**
 * Константы для записи в Preferences, чтобы отловить неверные указания в тексте на уровне компилятора
 */
object Constants {
    const val THEME = "theme"                               //Тема приложения (String)
    const val SHOW_FAB = "showFAB"                          //Отображать плавающую кнопку в основном меню (Boolean)
    const val TEXT_SIZE = "mainTextSize"                    //+ к изначально задуманному размеру текста в обучалках (Int)

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

}