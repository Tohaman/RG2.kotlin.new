package ru.tohaman.rg2.adapters

import android.view.*
import androidx.recyclerview.widget.RecyclerView
import ru.tohaman.rg2.databinding.ItemBasicMoveBinding
import ru.tohaman.rg2.dbase.entitys.BasicMove


//TODO можно переделать на использование универсального адаптера
// https://medium.com/@fraggjkee/recyclerview-2020-a-modern-way-of-dealing-with-lists-in-android-using-databinding-part-2-df69f0a741f8
// https://medium.com/@fraggjkee/recyclerview-2020-a-modern-way-of-dealing-with-lists-in-android-using-databinding-d97abf5fb55f

class MovesAdapter() : RecyclerView.Adapter<MovesAdapter.MovesHolder>() {
    //тут храним список, который надо отобразить
    private var items: List<BasicMove> = ArrayList()
    private var onClickCallBack: OnClickCallBack? = null

    fun attachCallBack(onClickCallBack: OnClickCallBack) {
        this.onClickCallBack = onClickCallBack
    }

    //создает ViewHolder и инициализирует views для списка
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovesHolder {
        return MovesHolder.from(parent)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: MovesHolder, position: Int) {
        //тут обновляем данные ячейки (вызываем биндер холдера) передаем туда BasicMove и onClickListener
        val item = items[position]
        holder.bind(item, onClickCallBack)
    }

    fun refreshItems(items: List<BasicMove>) {
        this.items = items
        notifyDataSetChanged()
    }

    class MovesHolder private constructor(private val binding: ItemBasicMoveBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: BasicMove, onClickCallBack: OnClickCallBack?) {
            binding.basicMoveItem = item
            binding.clickListener = onClickCallBack
            //Метод executePendingBindings используется, чтобы биндинг не откладывался, а выполнился как можно быстрее. Это критично в случае с RecyclerView.
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup) : MovesHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding =  ItemBasicMoveBinding.inflate(inflater, parent, false)

                return MovesHolder(binding)
            }
        }

    }

    interface OnClickCallBack {
        fun toastItem(menuItem: BasicMove)
    }

}