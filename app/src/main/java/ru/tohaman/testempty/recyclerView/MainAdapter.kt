package ru.tohaman.testempty.recyclerView

import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import ru.tohaman.testempty.dbase.ListPagerDBItem

class MainAdapter : PagedListAdapter<ListPagerDBItem, MenuViewHolder>(diffCallback) {

    //создает ViewHolder и инициализирует views для списка
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {

        return MenuViewHolder.create(parent)
    }


    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        //тут обновляем данные ячейки (вызываем биндер холдера) передаем туда ListPagerDBItem?
        holder.bindTo(getItem(position))
    }

    companion object {
        /**
         * В чем ключевая разница между areItemsTheSame и areContentsTheSame?
         * Рассмотрим на примере товаров. У Product есть три поля: id, name и price.
         * Для каждой пары сравниваемых товаров DiffUtil сначала вызовет метод areItemsTheSame,
         * чтобы определить, надо ли в принципе сравнивать эти товары. Т.е. cначала достаточно
         * сравнить их по id. Если id не равны, значит это разные товары и сравнивать их цены и
         * наименование нет смысла - скорее всего они также будут отличаться.
         * А вот если id равны, значит товар из старого списка и товар из нового списка - это один
         * и тот же товар и надо определить изменился ли он. В этом случае DiffUtil вызывает метод
         * areContentsTheSame, чтобы определить, есть ли отличия между старым товаром и новым.
         */
        private val diffCallback = object : DiffUtil.ItemCallback<ListPagerDBItem>() {
            override fun areItemsTheSame(oldItem: ListPagerDBItem, newItem: ListPagerDBItem): Boolean =
                (oldItem.id == newItem.id) and (oldItem.phase == newItem.phase)

            override fun areContentsTheSame(oldItem: ListPagerDBItem, newItem: ListPagerDBItem): Boolean =
                oldItem == newItem
        }
    }

    //Чтобы передать адаптеру PagedList, мы будем использовать метод адаптера submitList.

}