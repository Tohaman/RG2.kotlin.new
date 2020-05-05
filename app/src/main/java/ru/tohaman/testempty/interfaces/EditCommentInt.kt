package ru.tohaman.testempty.interfaces

import android.view.View
import ru.tohaman.testempty.dbase.entitys.TimeNoteItem

interface EditCommentInt {
    fun editComment(view: View, item: TimeNoteItem)
}