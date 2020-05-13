package ru.tohaman.testempty.dataSource

import ru.tohaman.testempty.DebugTag.TAG
import ru.tohaman.testempty.dataSource.entitys.Pair4Melting
import ru.tohaman.testempty.dataSource.entitys.ScrambleCondition
import ru.tohaman.testempty.dataSource.entitys.SolveCube
import timber.log.Timber
import java.util.*

fun generateScrambleWithParam(checkEdge: Boolean, checkCorner: Boolean, lenScramble: Int, azbuka: Array<String>): String {
    Timber.d("$TAG Ищем скрамбл подходящий по параметрам переплавок буфера и длине")
    var scramble = ""
    do {
        var result = true
        //сгенерируем скрамбл длинны указанной в поле ScrambleLength
        scramble = generateScramble(lenScramble)
        //scramble = "B' L U2 D F' U' L F'"   //для него решение (ТНПРКИХЦЧДО)Эк(БГФЖВЗМ)
        //разбираем кубик по скрамблу
        val genScrambleCube = runScramble(resetCube(), scramble)
        // получаем решение кубика (solve,isEdgeMelted,isCornerMelted)
        val condition = getSolve(genScrambleCube, azbuka)
        val isEdgeMelted = condition.edgeBuffer
        val isCornerMelted = condition.cornerBuffer

        Timber.d("$TAG Проверка Scramble $scramble, Переплавка буфера ребер - ${condition.edgeBuffer} , Переплавка буфера углов - ${condition.cornerBuffer}")
        if (isEdgeMelted && checkEdge) { result = false }
        if (isCornerMelted && checkCorner) { result = false }
    } while (!result)
    Timber.d("$TAG Таки скрамбл $scramble подошел под наши условия")

    return scramble
}

//получаем чистый собранный куб из 54 элементов (9*6), белый сверху зеленый к себе, цвета в виде кодов от 0 до 6
//на котором выполняем сгенерированный скрамбл и проверяем переплавки
fun resetCube(): IntArray {
    //Timber.d ("$TAG resetCube")
    val cube = IntArray(54)
    for (i in cube.indices) {
        cube[i] = i / 9
    }
    return cube
}

//Возвращаем трипл из 1.решения, 2.была ли переплавка буф.ребер, 3.была ли переплавка буф.углов
fun getSolve(mainCube: IntArray, azbuka: Array<String>):  ScrambleCondition  {
    var solve = "("
    var cube = mainCube.clone()
    var isEdgeMelted = false        //изначально считаем, что переплавок не было
    var isCornerMelted = false

    //решаем ребра
    do {
        //сначала ребра: смотрим что в буфере ребер
        val sumColor = getColorOfElement(cube, 23, 30)
        //если там буферный элемент бело-красный или красно-белый, то ставим признак переплавки
        if ((sumColor == 43) or (sumColor == 34)) { isEdgeMelted = true }
        // ставим на место ребро из буфера
        val sc = edgeBufferSolve(cube, mainEdge[sumColor]!!, solve, azbuka)
        // сохраняем результаты выполнения одной "буквы"
        solve = sc.solve
        cube = sc.cube
        // выполняем пока все ребра не будут на своих местах
    } while (!isAllEdgesOnItsPlace(cube).allComplete)

    solve = solve.trim { it <= ' ' }
    solve += ") "
    // Проверяем нужен ли экватор, и выполняем его если надо
    val j = solve.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray().size
    if (j % 2 != 0) {
        solve += "Эк "
        cube = ekvator(cube)
    }

    //решаем углы
    solve += "("
    do {
        //сначала ребра: смотрим что в буфере углов
        val sumColor = getColorOfElement(cube,18,11)
        //если там буферный элемент, то ставим признак переплавки
        //13 = сине-белый, 32 = бело-оранжевый, 21 = оранжево-синий
        if ((sumColor == 13) or (sumColor == 32) or (sumColor == 21)) { isCornerMelted = true }
        // ставим на место угол из буфера
        val sc = cornerBufferSolve(cube,  mainCorner[sumColor]!!, solve, azbuka)
        // сохраняем результаты выполнения одной "буквы"
        solve = sc.solve
        cube = sc.cube
        // выполняем пока все углы не будут на своих местах
    } while (!isAllCornersOnItsPlace(cube).allComplete)

    solve = solve.trim { it <= ' ' }
    solve += ")"
    return ScrambleCondition(solve, isEdgeMelted, isCornerMelted)
}


// Установка на свое место элемента цвета elementPosition находящегося в буфере ребер
// Возвращает SolveCube = куб после выполнения установки и решение solve + текущий ход
private fun edgeBufferSolve(cube: IntArray, elementPosition: Int, solve: String, azbuka: Array<String>): SolveCube {
    var tmpCube = cube
    var solv = solve

    if (!((elementPosition == 23) or (elementPosition == 30))) {           //проверяем, не буфер ли?, если нет, то добоавляем букву к решению
        solv += azbuka[elementPosition] + " "        //если буфер, то будем его переплавлять и букву уже
    }                                               //подставим в рекурсии
    when (elementPosition) {
        1 -> tmpCube = blind1(tmpCube)
        3 -> tmpCube = blind3(tmpCube)
        5 -> tmpCube = blind5(tmpCube)
        7 -> tmpCube = blind7(tmpCube)
        10 -> tmpCube = blind10(tmpCube)
        12 -> tmpCube = blind12(tmpCube)
        14 -> tmpCube = blind14(tmpCube)
        16 -> tmpCube = blind16(tmpCube)
        19 -> tmpCube = blind19(tmpCube)
        21 -> tmpCube = blind21(tmpCube)
        23 -> {                    // для бело-красного ребра
            val pair4Melting = isAllEdgesOnItsPlace(tmpCube)
            if (!pair4Melting.allComplete) {
                val sc = meltingEdge(tmpCube, solv, pair4Melting.elementsNotOnPlace, azbuka)
                solv = sc.solve
                tmpCube = sc.cube
            }}
        25 -> tmpCube = blind25(tmpCube)
        28 -> tmpCube = blind28(tmpCube)
        30 -> {                      //для красно-белого ребра
            val pair4Melting = isAllEdgesOnItsPlace(tmpCube)
            if (!pair4Melting.allComplete) {
                //переплавляем буфер (рекурсия)
                val sc = meltingEdge(tmpCube, solv, pair4Melting.elementsNotOnPlace, azbuka)
                solv = sc.solve
                tmpCube = sc.cube
            }}
        32 -> tmpCube = blind32(tmpCube)
        34 -> tmpCube = blind34(tmpCube)
        37 -> tmpCube = blind37(tmpCube)
        39 -> tmpCube = blind39(tmpCube)
        41 -> tmpCube = blind41(tmpCube)
        43 -> tmpCube = blind43(tmpCube)
        46 -> tmpCube = blind46(tmpCube)
        48 -> tmpCube = blind48(tmpCube)
        50 -> tmpCube = blind50(tmpCube)
        52 -> tmpCube = blind52(tmpCube)
    }
    return SolveCube(tmpCube, solv)
}

// Установка на свое место элемента цвета elementPosition находящегося в буфере углов
// Возвращает SolveCube = куб после выполнения установки и решение solve + текущий ход
private fun cornerBufferSolve(cube: IntArray, elementPosition: Int, solve: String, azbuka: Array<String>): SolveCube {
    var tmpCube = cube
    var solv = solve

    if (!(elementPosition == 18 || elementPosition == 11 || elementPosition == 6)) {           //если с не равно 18,11 или 6, то буфер не на месте и добавляем букву к решению.
        solv += azbuka[elementPosition] + " "
    }
    when (elementPosition) {
        0 -> tmpCube = blind0(tmpCube)
        2 -> tmpCube = blind2(tmpCube)
        6 -> {
            val pair4Melting = isAllCornersOnItsPlace(tmpCube)
            if (!pair4Melting.allComplete) {
                val sc = meltingCorner(tmpCube, solv, pair4Melting.elementsNotOnPlace, azbuka)
                solv = sc.solve
                tmpCube = sc.cube
            }
        }
        8 -> tmpCube = blind8(tmpCube)
        9 -> tmpCube = blind9(tmpCube)
        11 -> {
            val pair4Melting = isAllCornersOnItsPlace(tmpCube)
            if (!pair4Melting.allComplete) {
                val sc = meltingCorner(tmpCube, solv, pair4Melting.elementsNotOnPlace, azbuka)
                solv = sc.solve
                tmpCube = sc.cube
            }
        }
        15 -> tmpCube = blind15(tmpCube)
        17 -> tmpCube = blind17(tmpCube)
        18 -> {
            val pair4Melting = isAllCornersOnItsPlace(tmpCube)
            if (!pair4Melting.allComplete) {
                val sc = meltingCorner(tmpCube, solv, pair4Melting.elementsNotOnPlace, azbuka)
                solv = sc.solve
                tmpCube = sc.cube
            }
        }
        20 -> tmpCube = blind20(tmpCube)
        24 -> tmpCube = blind24(tmpCube)
        26 -> tmpCube = blind26(tmpCube)
        27 -> tmpCube = blind27(tmpCube)
        29 -> tmpCube = blind29(tmpCube)
        33 -> tmpCube = blind33(tmpCube)
        35 -> tmpCube = blind35(tmpCube)
        36 -> tmpCube = blind36(tmpCube)
        38 -> tmpCube = blind38(tmpCube)
        42 -> tmpCube = blind42(tmpCube)
        44 -> tmpCube = blind44(tmpCube)
        45 -> tmpCube = blind45(tmpCube)
        47 -> tmpCube = blind47(tmpCube)
        51 -> tmpCube = blind51(tmpCube)
        53 -> tmpCube = blind53(tmpCube)
    }
    return SolveCube(tmpCube, solv)
}

private fun meltingEdge(tmpCube: IntArray, solv: String, edgesListNotOnPlace: SortedMap<Int, Int>, azbuka: Array<String>): SolveCube {
    var positionOfElement = 0
    // цикл поиска свободной корзины
    var j = 0
    while (positionOfElement == 0) {
        var i = 0
        do {
            if (edgePriority[j] == edgesListNotOnPlace[i]) {
                positionOfElement = edgePriority[j]!!
            } //ищем ребра на своем месте по приоритету edgePriority
            i++
        } while (edgesListNotOnPlace[i] != null)
        j++
    }
    //переплавляем буфер (рекурсия)
    return edgeBufferSolve(tmpCube, positionOfElement, solv, azbuka)
}

private fun meltingCorner(tmpCube: IntArray, solv: String, cornersListNotOnPlace: SortedMap<Int, Int>, azbuka: Array<String>): SolveCube {
    var positionOfElement = 0
    // цикл поиска свободной корзины
    var j = 0
    while (positionOfElement == 0) {
        var i = 0
        do {
            if (cornerPriority[j] == cornersListNotOnPlace[i]) {
                positionOfElement = cornerPriority[j]!!
            } //ищем ребра на своем месте по приоритету cornerPriority
            i++
        } while (cornersListNotOnPlace[i] != null)
        j++
    }
    //переплавляем буфер (рекурсия)
    return cornerBufferSolve(tmpCube, positionOfElement, solv, azbuka)
}

private fun isAllEdgesOnItsPlace(cube: IntArray): Pair4Melting {    //проверяем все ли грани на своих местах
    //предположим что все на местах
    var result = true
    //Обнуляем список ребер стоящих на местах
    val edgesListNotOnPlace: SortedMap<Int, Int> = sortedMapOf(0 to 0)
    edgesListNotOnPlace.clear()
    var j = 0
    for (i in 0..52) {
        val secColor = dopEdge[i]
        if (secColor != null) {
            val firstColor = getColorOfElement(cube,i,secColor)
            if (mainEdge[firstColor] != i) {
                edgesListNotOnPlace[j] = i
                j++
                result = false
            }
        }
    }
    return Pair4Melting(result, edgesListNotOnPlace)
}

private fun isAllCornersOnItsPlace(cube: IntArray): Pair4Melting {    //проверяем все ли углы на своих местах
    //предположим что все на местах
    var result = true
    //Обнуляем список углов стоящих на своих местах
    val cornersListNotOnPlace: SortedMap<Int, Int> = sortedMapOf(0 to 0)
    cornersListNotOnPlace.clear()
    var j = 0
    //Будем проверять все элементы кубика
    for (i in 0..52) {
        //Проверяем данный элемент угол или ребро
        val secColor = dopCorner[i]
        if (secColor != null) {
            val fColor = getColorOfElement(cube,i,secColor)
            if (mainCorner[fColor] != i) {
                cornersListNotOnPlace[j] = i
                j++
                result = false
            }
        }
    }
    return Pair4Melting(result, cornersListNotOnPlace)
}

