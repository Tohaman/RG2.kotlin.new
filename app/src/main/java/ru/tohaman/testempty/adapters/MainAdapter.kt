package ru.tohaman.testempty.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.tohaman.testempty.databinding.ItemMainMenuBinding
import ru.tohaman.testempty.dbase.entitys.PhaseItem

class MainAdapter(private val onClickListener: OnClickListener) :
        PagedListAdapter<PhaseItem, MainAdapter.MenuViewHolder>(diffCallback) {

    //создает ViewHolder и инициализирует views для списка
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        //TODO сделать выбор в зависимости от ViewType
        return MenuViewHolder.from(parent)
    }


    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        //тут обновляем данные ячейки (вызываем биндер холдера) передаем туда ListPagerDBItem и onClickListener
        val item = getItem(position)!!
        holder.bind(item, onClickListener)
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)?.urlOrType) {
            "basic" -> 2
            else -> 1
        }
    }

    class MenuViewHolder private constructor(private val binding: ItemMainMenuBinding)
            : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PhaseItem, onClickListener: OnClickListener) {
            //Временно комментирует, т.к. в биндинге переменные другого типа (другого адаптера MenuAdapter)
            //binding.viewMenuItem = item
            //binding.clickListener = onClickListener
            //Метод executePendingBindings используется, чтобы биндинг не откладывался, а выполнился как можно быстрее. Это критично в случае с RecyclerView.
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup) : MenuViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding =  ItemMainMenuBinding.inflate(inflater, parent, false)

                return MenuViewHolder(binding)
            }
        }
    }


    class OnClickListener(val clickListener: (PhaseItem) -> Unit) {
        fun onClick(menuItem: PhaseItem) = clickListener(menuItem)
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
        private val diffCallback = object : DiffUtil.ItemCallback<PhaseItem>() {
            override fun areItemsTheSame(oldItem: PhaseItem, newItem: PhaseItem): Boolean =
                (oldItem.id == newItem.id) and (oldItem.phase == newItem.phase)

            override fun areContentsTheSame(oldItem: PhaseItem, newItem: PhaseItem): Boolean =
                oldItem == newItem
        }
    }

    //Чтобы передать адаптеру PagedList, мы будем использовать метод адаптера submitList.

}