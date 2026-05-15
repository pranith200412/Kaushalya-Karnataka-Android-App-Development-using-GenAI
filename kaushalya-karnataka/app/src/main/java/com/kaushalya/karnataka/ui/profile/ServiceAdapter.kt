package com.kaushalya.karnataka.ui.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kaushalya.karnataka.R
import com.kaushalya.karnataka.data.entity.Service
import com.kaushalya.karnataka.databinding.ItemServiceBinding

class ServiceAdapter(
    private var ownerMode: Boolean,
    private val onEdit: (Service) -> Unit,
    private val onDelete: (Service) -> Unit,
) : ListAdapter<Service, ServiceAdapter.VH>(DIFF) {

    fun setOwnerMode(value: Boolean) {
        if (ownerMode == value) return
        ownerMode = value
        notifyItemRangeChanged(0, itemCount)
    }

    inner class VH(val binding: ItemServiceBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemServiceBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        val context = holder.itemView.context
        with(holder.binding) {
            serviceNameTv.text = item.name
            startingLabelTv.visibility = if (item.isStartingAt) View.VISIBLE else View.GONE
            pricePill.text = context.getString(R.string.price_with_currency, item.price)

            menuBtn.visibility = if (ownerMode) View.VISIBLE else View.GONE
            menuBtn.setOnClickListener { btn ->
                PopupMenu(context, btn).apply {
                    menu.add(0, 1, 0, context.getString(R.string.edit))
                    menu.add(0, 2, 1, context.getString(R.string.delete))
                    setOnMenuItemClickListener { mi ->
                        when (mi.itemId) {
                            1 -> { onEdit(item); true }
                            2 -> { onDelete(item); true }
                            else -> false
                        }
                    }
                }.show()
            }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Service>() {
            override fun areItemsTheSame(o: Service, n: Service) = o.id == n.id
            override fun areContentsTheSame(o: Service, n: Service) = o == n
        }
    }
}
