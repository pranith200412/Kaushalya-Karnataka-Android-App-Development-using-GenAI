package com.kaushalya.karnataka.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kaushalya.karnataka.R
import com.kaushalya.karnataka.data.entity.WorkerWithStats
import com.kaushalya.karnataka.databinding.ItemWorkerBinding
import com.kaushalya.karnataka.util.AvatarBinder
import com.kaushalya.karnataka.util.Format

class WorkerAdapter(
    private val onClick: (WorkerWithStats) -> Unit,
) : ListAdapter<WorkerWithStats, WorkerAdapter.VH>(DIFF) {

    inner class VH(val binding: ItemWorkerBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) onClick(getItem(pos))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val inflater = LayoutInflater.from(parent.context)
        return VH(ItemWorkerBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        val context = holder.itemView.context
        val w = item.worker

        with(holder.binding) {
            nameTv.text = w.name
            townTv.text = context.getString(R.string.town_dot_category, w.town, w.category)
            bioTv.text = w.bio.ifBlank { context.getString(R.string.town_dot_category, w.category, w.town) }

            ratingValueTv.text = Format.rating(item.averageRating)

            servicesCountTv.text = Format.services(context, item.serviceCount)
            startingAtTv.text = item.minPrice
                ?.let { Format.price(context, it, isStartingAt = true) }
                .orEmpty()

            AvatarBinder.bind(avatarContainer, avatarInitialsTv, avatarIv, w.name, w.avatarUri)
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<WorkerWithStats>() {
            override fun areItemsTheSame(o: WorkerWithStats, n: WorkerWithStats) =
                o.worker.id == n.worker.id

            override fun areContentsTheSame(o: WorkerWithStats, n: WorkerWithStats) = o == n
        }
    }
}
