package ru.tohaman.rg2

import ru.tohaman.rg2.dataSource.entitys.TipsItem

/**
 * Константы для записи в Preferences, чтобы отловить неверные указания в тексте на уровне компилятора
 */
object Constants {
    const val THEME = "theme"                               //Тема приложения (String)
    const val SHOW_FAB = "showFAB"                          //Отображать плавающую кнопку в основном меню (Boolean)
    const val TEXT_SIZE = "mainTextSize"                    //+ к изначально задуманному размеру текста в обучалках (Int)
    const val START_COUNT = "startCount"                    //количество запусков
    const val LAST_VERSION = "version"                      //последняя сохраненная версия программы
    const val HELP_COUNT = "helpCount"                      //Сколько раз был показан миниХелп по программе при старте
    const val ON_START_MINI_HELP = "onStartHelpEnabled"     //true, если надо показывать окно миниХелпа при запуске
    const val FAVORITES = "favorites"                       //для извлечения Избранного из предыдущей версии программы, там хранилось в SP
    const val PAYED_COINS = "payCoins"                      //если 0, значит пользователь не донатил.

    //Константы для увеличения/уменьшения размера текста
    const val startValue: Float = 0.7f                      //начальное значение размера шрифта
    const val step = 0.15f                                  //шаг увеличения коэффициента
    //Другие настройки программы
    const val IS_SCREEN_ALWAYS_ON = "isScreenAlwaysON"      //Не выключаьб экран, если приложение запущено
    const val IS_VIDEO_SCREEN_ON = "isYouTubeDisplayAlwaysOn"   //Не выключать экран при просмотре YouTube в отдельном окне
    const val IS_TEXT_SELECTABLE = "isTextSelectable"       //Возможность выделить/скопировать текст из обучалки
    //Использование интернета
    const val ALL_INTERNET = "useAllInternet"               //Используем любой доступный интернет (Boolean)
    const val ONLY_WIFI = "useOnlyWiFi"                     //Используем интернет только при наличии Wi-Fi (Boolean)
    const val NOT_USE_INTERNET = "doNotUseInternet"         //Не использовать интернет

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
    const val SCRAMBLE_TEXT_SIZE = "scrambleTextSize"       //Размер текста для отображения скрабла в таймере (Int)

    //Настройки для PLL тренировки
    const val IS_2SIDE_RECOGNITION = "is2SideRecognition"       //Определение ситуации по 2-м сторонам если true (Boolean), иначе по 3-м
    const val PLL_RANDOM_SIDE = "pllRandomSide"                 //Случайная сторона для определения (Boolean)
    const val PLL_TRAINING_TIMER = "pllTrainingTimer"           //Игра на время (Boolean)
    const val PLL_TRAINING_TIMER_TIME = "pllTrainingTimerTime"  //Время в сек. на ответ (Int)
    const val ALL_PLL_COUNT = "allPllCount"                     //Показывать все 21 вариант PLL
    const val PLL_ANSWER_VARIANTS = "pllAnswerVariants"         //Сколько вариантов для выбора

    //Настройки игры Угадай букву блайнда
    const val TRAINING_EDGES = "trainingEdges"              //Тренируем углы? (Boolean)
    const val TRAINING_CORNERS = "trainingCorners"          //Тренируем ребра? (Boolean)
    const val TRAINING_TIMER = "trainingTimer"              //Игра на время (Boolean)
    const val TRAINING_TIMER_TIME = "trainingTimerTime"     //Время в сек. на ответ (Int)

    //Основное меню
    const val FAVOURITES = "FAVOURITES"                     //Название избранного
    const val CUR_CUBE_TYPE = "currentCubeType"             //Текущая головоломка (пункт меню)

    //Меню Информации
    const val INFO_BOOKMARK = "infoBookmark"                //Текущая выбранная закладка на странице Info

    //Используются в таблице AzbukaItems для azbukaName
    const val ANTONS_AZBUKA = "ANTONS_AZBUKA"               //Имя моей азбуки
    const val MAKSIMS_AZBUKA = "MAKSIMS_AZBUKA"             //Азбука Максима
    const val CURRENT_AZBUKA = "CURRENT_AZBUKA"             //Текущая азбука
    const val CUSTOM_AZBUKA = "CUSTOM_AZBUKA"               //Сохраненная пользовательская азбука

    //Для передачи номера фрагмента во фрагмент
    const val CUR_ITEM_ID = "itemId"                //id в фазе для выбранного фрагмента

    //Для приема/передачи параметров видео
    const val TIME = "time"
    const val LINK = "link"
    const val ALG = "alg"

    val galleryDrawables = listOf(
        TipsItem(R.drawable.frame_1, "Обязательно прочитайте этот раздел"),
        TipsItem(R.drawable.frame_10, "Смотрите в видео, как правильно выполнять алгоритмы"),
        TipsItem(R.drawable.frame_11, "Эта кнопка позволяет повторить видео с того момента, с которого Вы начали его смотреть"),
        TipsItem(R.drawable.frame_3, "Забыли азбуку? Нажмите зеленую кнопку"),
        TipsItem(R.drawable.frame_2, "Добавляйте в избранное отдельные этапы или головоломки целиком"),
        TipsItem(R.drawable.frame_13, "Открывайте избранное из любого места при помощи долгого тапа по любому сердечку"),
        TipsItem(R.drawable.frame_14, "Просто смахните элемент избранного в сторону, чтобы его удалить"),
        TipsItem(R.drawable.frame_15, "Меняйте пункты избранного местами"),
        TipsItem(R.drawable.frame_4, "Создавайте свои комментарии к этапам, и вы их увидите в меню"),
        TipsItem(R.drawable.frame_5, "Щелкните по скрамблу в генераторе, чтобы задать свой скрамбл"),
        TipsItem(R.drawable.frame_6, "Щелкните по скрамблу в таймере, чтобы его сменить"),
        TipsItem(R.drawable.frame_7, "Таймер можно поставить на паузу, нажав на верхнюю панель"),
        TipsItem(R.drawable.frame_8, "При сохранении результата в таймере можно сразу задать свой комментарий"),
        TipsItem(R.drawable.frame_12, "Возможность копировать текст обучалки можно включить в настройках"),
        TipsItem(R.drawable.frame_9, "Мешает кнопка? Отключите ее в настройках программы")
    )

    val PLLS_NAME = listOf<String>("Ga","","Y","F","H","","Gb","Na","Ra","Ja","Ua","Aa","Gc","Nb","Rb","Jb","Ub","Ab","Gd","","V","T","Z","E")

}