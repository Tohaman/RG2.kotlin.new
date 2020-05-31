package ru.tohaman.testempty.dataSource

import timber.log.Timber
import java.util.*

/**
 * Created by Toha on 22.12.2017. Внеклассовые методы, для ходов кубика Рубика
 * Алгоритмы постановки на место элементов для слепой сборки кубика
 *
 */

//получаем цвет переданных ячеек куба (двузначное число, первая и вторая цифры которого соответствую икомым цветам)
fun getColorOfElement(cube: IntArray, firstElement: Int, secondElement: Int): Int
        = (cube[firstElement] + 1) * 10 + cube[secondElement] + 1

//Генерация скрамбла определенной длинны (без учета переплавки буфера)
fun generateScramble(length: Int): String {
    Timber.d("TAG generateScramble $length")
    val random = Random()
    var scramble = ""
    var i = 0
    var prevRandom = random.nextInt(0..6)                     //генерируем число от 0 до 5
    var prevPrevRandom = random.nextInt(0..6)                 //генерируем число от 0 до 5
    val map = hashMapOf(0 to "R", 1 to "L", 2 to "F", 3 to "B", 4 to "U", 5 to "D")

    do {
        val curRandom = random.nextInt(0..6)                     //генерируем число от 0 до 5
        if (curRandom != prevRandom) {
            if ((curRandom / 2 != prevRandom / 2) or (curRandom != prevPrevRandom)) {
                i++                                                 //увеличиваем счетчик на 1
                // ход будет по часовой, против или двойной
                when (random.nextInt(3)) {
                //по часовой
                    0 -> { scramble = "$scramble${map[curRandom]} "  }      //просто добавляем букву
                //против часовой
                    1 -> { scramble = "$scramble${map[curRandom]}' " }      //добавляем букву c '
                //двойной
                    2 -> { scramble = "$scramble${map[curRandom]}2 " }      //добавляем двойку
                }
                prevPrevRandom = prevRandom                         // запоминаем -2 позиции от текущего числа
                prevRandom = curRandom                              //запоминаем это число в prevRandom
            }
        }
    } while (i < length)

    scramble = scramble.trim (' ')                 //убираем лишние пробелы
    return scramble
}

fun Random.nextInt(range: IntRange): Int {
    return range.first + nextInt(range.last - range.first)
}

fun runScramble(originalCube: IntArray, scramble: String): IntArray {
    val cube = originalCube.clone()
    var scrambleString = scramble
    scrambleString = scrambleString.replace("'", "1")
    scrambleString = scrambleString.replace("r", "Rw")
    scrambleString = scrambleString.replace("l", "Lw")
    scrambleString = scrambleString.replace("u", "Uw")
    scrambleString = scrambleString.replace("d", "Dw")
    scrambleString = scrambleString.replace("f", "Fw")
    scrambleString = scrambleString.replace("b", "Bw")
    scrambleString = scrambleString.replace("(", "")
    scrambleString = scrambleString.replace(")", "")
    scrambleString = scrambleString.replace("21", "2")
    //Преобразовываем строку в массив, разделитель пробел
    val arrayOfScramble = scrambleString.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    arrayOfScramble.indices
            .map { arrayOfScramble[it] }
            .forEach {
                when (it) {
                    "R" -> moveR(cube)
                    "R1" -> moveRb(cube)
                    "R2" -> moveR2(cube)
                    "F" -> moveF(cube)
                    "F1" -> moveFb(cube)
                    "F2" -> moveF2(cube)
                    "U" -> moveU(cube)
                    "U1" -> moveUb(cube)
                    "U2" -> moveU2(cube)
                    "L" -> moveL(cube)
                    "L1" -> moveLb(cube)
                    "L2" -> moveL2(cube)
                    "B" -> moveB(cube)
                    "B1" -> moveBb(cube)
                    "B2" -> moveB2(cube)
                    "D" -> moveD(cube)
                    "D1" -> moveDb(cube)
                    "D2" -> moveD2(cube)
                    "E" -> moveE(cube)
                    "E1" -> moveEb(cube)
                    "E2" -> moveE2(cube)
                    "M" -> moveM(cube)
                    "M1" -> moveMb(cube)
                    "M2" -> moveM2(cube)
                    "S" -> moveS(cube)
                    "S1" -> moveSb(cube)
                    "S2" -> moveS2(cube)
                    "Rw" -> moveRw(cube)
                    "Rw1" -> moveRwb(cube)
                    "Rw2" -> moveRw2(cube)
                    "Rw21" -> moveRw2(cube)
                    "Uw" -> moveUw(cube)
                    "Uw1" -> moveUwb(cube)
                    "Uw2" -> moveUw2(cube)
                    "Dw" -> moveDw(cube)
                    "Dw1" -> moveDwb(cube)
                    "Dw2" -> moveDw2(cube)
                    "Lw" -> moveLw(cube)
                    "Lw1" -> moveLwb(cube)
                    "Lw2" -> moveLw2(cube)
                    "Fw" -> moveFw(cube)
                    "Fw1" -> moveFwb(cube)
                    "Fw2" -> moveFw2(cube)
                    "Bw" -> moveBw(cube)
                    "Bw1" -> moveBwb(cube)
                    "Bw2" -> moveBw2(cube)
                    "z" -> moveZ(cube)
                    "z1" -> moveZb(cube)
                    "x" -> moveX(cube)
                    "x1" -> moveXb(cube)
                    "y" -> moveY(cube)
                    "y1" -> moveYb(cube)
                }
            }
    return cube
}

//Алгоритм Запад
fun zapad(cube: IntArray): IntArray = runScramble(cube, "R U R' U' R' F R2 U' R' U' R U R' F'")

//Алгоритм Юг
fun yug(cube: IntArray): IntArray = runScramble(cube, "R U R' F' R U R' U' R' F R2 U' R' U'")

//Алгоритм Пиф-паф
fun pifPaf(cube: IntArray): IntArray = runScramble(cube, "R U R' U'")

//Алгоритм Экватор
fun ekvator(cube: IntArray): IntArray = runScramble(cube, "R U R' F' R U2 R' U2 R' F R U R U2 R' U'")

//Алгоритм Австралия
fun australia(cube: IntArray): IntArray = runScramble(cube, "F R U' R' U' R U R' F' R U R' U' R' F R F'")

//белосинее ребро
fun blind19(cube: IntArray): IntArray {
    var tmpCube = runScramble(cube, "M2 D' L2")
    tmpCube = zapad(tmpCube)
    tmpCube = runScramble(tmpCube, "L2 D M2")
    return tmpCube
}

//белозеленое
fun blind25(cube: IntArray): IntArray = yug(cube)

//белооранжевое
fun blind21(cube: IntArray): IntArray = zapad(cube)

//зеленобелое
fun blind46(cube: IntArray): IntArray {
    var tmpCube = runScramble(cube, "M D' L2")
    tmpCube = zapad(tmpCube)
    tmpCube = runScramble(tmpCube, "L2 D M'")
    return tmpCube
}

//зеленокрасное
fun blind50(cube: IntArray): IntArray {
    var tmpCube = runScramble(cube, "Dw2 L")
    tmpCube = zapad(tmpCube)
    tmpCube = runScramble(tmpCube, "L' Dw2")
    return tmpCube
}

//зеленожелтое
fun blind52(cube: IntArray): IntArray {
    var tmpCube = runScramble(cube, "M'")
    tmpCube = yug(tmpCube)
    tmpCube = runScramble(tmpCube, "M")
    return tmpCube
}

//зеленооранжевое
fun blind48(cube: IntArray): IntArray {
    var tmpCube = runScramble(cube, "L'")
    tmpCube = zapad(tmpCube)
    tmpCube = runScramble(tmpCube, "L")
    return tmpCube
}

//синебелое
fun blind7(cube: IntArray): IntArray {
    var tmpCube = runScramble(cube, "M")
    tmpCube = yug(tmpCube)
    tmpCube = runScramble(tmpCube, "M'")
    return tmpCube
}

//синекрасное
fun blind5(cube: IntArray): IntArray {
    var tmpCube = runScramble(cube, "Dw2 L'")
    tmpCube = zapad(tmpCube)
    tmpCube = runScramble(tmpCube, "L Dw2")
    return tmpCube
}

//синежелтое
fun blind1(cube: IntArray): IntArray {
    var tmpCube = runScramble(cube, "D2 M'")
    tmpCube = yug(tmpCube)
    tmpCube = runScramble(tmpCube, "M D2")
    return tmpCube
}

//синеоранжевое
fun blind3(cube: IntArray): IntArray {
    var tmpCube = runScramble(cube, "L")
    tmpCube = zapad(tmpCube)
    tmpCube = runScramble(tmpCube, "L'")
    return tmpCube
}

//оранжевобелое
fun blind14(cube: IntArray): IntArray {
    var tmpCube = runScramble(cube, "L2 D M'")
    tmpCube = yug(tmpCube)
    tmpCube = runScramble(tmpCube, "M D' L2")
    return tmpCube
}

//оранжевозеленое
fun blind16(cube: IntArray): IntArray {
    var tmpCube = runScramble(cube, "Dw' L")
    tmpCube = zapad(tmpCube)
    tmpCube = runScramble(tmpCube, "L' Dw")
    return tmpCube
}

//оранжевожелтое
fun blind12(cube: IntArray): IntArray {
    var tmpCube = runScramble(cube, "D M'")
    tmpCube = yug(tmpCube)
    tmpCube = runScramble(tmpCube, "M D'")
    return tmpCube
}

//оранжевосинее
fun blind10(cube: IntArray): IntArray {
    var tmpCube = runScramble(cube, "Dw L'")
    tmpCube = zapad(tmpCube)
    tmpCube = runScramble(tmpCube, "L Dw'")
    return tmpCube
}

//краснозеленое
fun blind34(cube: IntArray): IntArray {
    var tmpCube = runScramble(cube, "Dw' L'")
    tmpCube = zapad(tmpCube)
    tmpCube = runScramble(tmpCube, "L Dw")
    return tmpCube
}

//красножелтое
fun blind32(cube: IntArray): IntArray {
    var tmpCube = runScramble(cube, "D' M'")
    tmpCube = yug(tmpCube)
    tmpCube = runScramble(tmpCube, "M D")
    return tmpCube
}

//красносинее
fun blind28(cube: IntArray): IntArray {
    var tmpCube = runScramble(cube, "Dw L")
    tmpCube = zapad(tmpCube)
    tmpCube = runScramble(tmpCube, "L' Dw'")
    return tmpCube
}

//желтосинее
fun blind37(cube: IntArray): IntArray {
    var tmpCube = runScramble(cube, "D L2")
    tmpCube = zapad(tmpCube)
    tmpCube = runScramble(tmpCube, "L2 D'")
    return tmpCube
}

//желтокрасное
fun blind39(cube: IntArray): IntArray {
    var tmpCube = runScramble(cube, "D2 L2")
    tmpCube = zapad(tmpCube)
    tmpCube = runScramble(tmpCube, "L2 D2")
    return tmpCube
}

//желтозеленое
fun blind43(cube: IntArray): IntArray {
    var tmpCube = runScramble(cube, "D' L2")
    tmpCube = zapad(tmpCube)
    tmpCube = runScramble(tmpCube, "L2 D")
    return tmpCube
}

//желтооранжевое
fun blind41(cube: IntArray): IntArray {
    var tmpCube = runScramble(cube, "L2")
    tmpCube = zapad(tmpCube)
    tmpCube = runScramble(tmpCube, "L2")
    return tmpCube
}

//--------------------------------------------------------------------------------------------------

//белосинекрасный угол
fun blind20(cube: IntArray): IntArray {
    var tmpCube = runScramble(cube, "R D' F'")
    tmpCube = australia(tmpCube)
    tmpCube = runScramble(tmpCube, "F D R'")
    return tmpCube
}

//белокраснозеленый угол
fun blind26(cube: IntArray) = australia(cube)

//белозеленооранжевый угол
fun blind24(cube: IntArray): IntArray {
    var tmpCube = runScramble(cube, "F' D R")
    tmpCube = australia(tmpCube)
    tmpCube = runScramble(tmpCube, "R' D' F")
    return tmpCube
}

//зеленооранжевобелый
fun blind45(cube: IntArray): IntArray {
    var tmpCube = runScramble(cube, "F' D F'")
    tmpCube = australia(tmpCube)
    tmpCube = runScramble(tmpCube, "F D' F")
    return tmpCube
}

//зеленобелосиний
fun blind47(cube: IntArray): IntArray {
    var tmpCube = runScramble(cube, "F R")
    tmpCube = australia(tmpCube)
    tmpCube = runScramble(tmpCube, "R' F'")
    return tmpCube
}

//зеленокрасножелтый
fun blind53(cube: IntArray): IntArray {
    var tmpCube = runScramble(cube, "R")
    tmpCube = australia(tmpCube)
    tmpCube = runScramble(tmpCube, "R'")
    return tmpCube
}

//зеленожелтооранжевый
fun blind51(cube: IntArray): IntArray {
    var tmpCube = runScramble(cube, "D F'")
    tmpCube = australia(tmpCube)
    tmpCube = runScramble(tmpCube, "F D'")
    return tmpCube
}

//синекраснобелый
fun blind8(cube: IntArray): IntArray {
    var tmpCube = runScramble(cube, "R'")
    tmpCube = australia(tmpCube)
    tmpCube = runScramble(tmpCube, "R")
    return tmpCube
}

//синежелтокрасный
fun blind2(cube: IntArray): IntArray {
    var tmpCube  = runScramble(cube, "D' F'")
    tmpCube = australia(tmpCube)
    tmpCube = runScramble(tmpCube, "F D")
    return tmpCube
}

//синеоранжевожелтый
fun blind0(cube: IntArray): IntArray {
    var tmpCube = runScramble(cube, "D2 R")
    tmpCube = australia(tmpCube)
    tmpCube = runScramble(tmpCube, "R' D2")
    return tmpCube
}

//оранжевобелозеленый
fun blind17(cube: IntArray): IntArray {
    var tmpCube = runScramble(cube, "F")
    tmpCube = australia(tmpCube)
    tmpCube = runScramble(tmpCube, "F'")
    return tmpCube
}

//оранжевозеленожелтый
fun blind15(cube: IntArray): IntArray {
    var tmpCube = runScramble(cube, "D R")
    tmpCube = australia(tmpCube)
    tmpCube = runScramble(tmpCube, "R' D'")
    return tmpCube
}

//оранжевожелтосиний
fun blind9(cube: IntArray): IntArray {
    var tmpCube = runScramble(cube, "D2 F'")
    tmpCube = australia(tmpCube)
    tmpCube = runScramble(tmpCube, "F D2")
    return tmpCube
}

//краснобелосиний
fun blind27(cube: IntArray): IntArray {
    var tmpCube = runScramble(cube, "R2 F'")
    tmpCube = australia(tmpCube)
    tmpCube = runScramble(tmpCube, "F R2")
    return tmpCube
}

//краснозеленобелый
fun blind33(cube: IntArray): IntArray {
    var tmpCube = runScramble(cube, "R' F'")
    tmpCube = australia(tmpCube)
    tmpCube = runScramble(tmpCube, "F R")
    return tmpCube
}

//красножелтозеленый
fun blind35(cube: IntArray): IntArray {
    var tmpCube = runScramble(cube, "F'")
    tmpCube = australia(tmpCube)
    tmpCube = runScramble(tmpCube, "F")
    return tmpCube
}

//красносинежелтый
fun blind29(cube: IntArray): IntArray {
    var tmpCube = runScramble(cube, "R F'")
    tmpCube = australia(tmpCube)
    tmpCube = runScramble(tmpCube, "F R'")
    return tmpCube
}

//желтосинеоранжевый
fun blind38(cube: IntArray): IntArray {
    var tmpCube = runScramble(cube, "D' R2")
    tmpCube = australia(tmpCube)
    tmpCube = runScramble(tmpCube, "R2 D")
    return tmpCube
}

//желтокрасносиний
fun blind36(cube: IntArray): IntArray {
    var tmpCube = runScramble(cube, "R2")
    tmpCube = australia(tmpCube)
    tmpCube = runScramble(tmpCube, "R2")
    return tmpCube
}

//желтозеленокрасный
fun blind42(cube: IntArray): IntArray {
    var tmpCube = runScramble(cube, "D R2")
    tmpCube = australia(tmpCube)
    tmpCube = runScramble(tmpCube, "R2 D'")
    return tmpCube
}

//желтооранжевозеленый
fun blind44(cube: IntArray): IntArray {
    var tmpCube = runScramble(cube, "D2 R2")
    tmpCube = australia(tmpCube)
    tmpCube = runScramble(tmpCube, "R2 D2")
    return tmpCube
}

