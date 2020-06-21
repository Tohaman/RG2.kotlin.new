package ru.tohaman.rg2.dataSource.entitys


/**
 * Класс для одной записи в Старом избранном, в новой версии используется атрибут в MainDBItem
 */
data class Favorite (
        val phase: String,
        val id: Int,
        var comment: String = "",
        var subID : String = ""
    ) {

    override
    fun equals(other: Any?): Boolean {
        //Переопределяем сравнение. При сравнение объектов не учитываем комментарий
        return if (other is Favorite) {
            (this.phase == other.phase) and (this.id == other.id) //and (this.subID == other.subID)
        } else {
            false
        }
    }
}
