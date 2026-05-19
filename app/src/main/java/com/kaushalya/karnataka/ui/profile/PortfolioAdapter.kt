package com.kaushalya.karnataka.ui.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.kaushalya.karnataka.R
import com.kaushalya.karnataka.data.entity.PortfolioPhoto
import com.kaushalya.karnataka.databinding.ItemPortfolioBinding
import com.kaushalya.karnataka.databinding.ItemPortfolioAddBinding
import java.io.File

class PortfolioAdapter(
    private var ownerMode: Boolean,
    private val onAddTap: () -> Unit,
    private val onDelete: (PortfolioPhoto) -> Unit,
) : ListAdapter<PortfolioAdapter.Row, RecyclerView.ViewHolder>(DIFF) {

    sealed class Row {
        data class Photo(val photo: PortfolioPhoto) : Row()
        object AddTile : Row()
    }

    fun setOwnerMode(value: Boolean) {
        if (ownerMode == value) return
        ownerMode = value
    }

    fun submitPhotos(photos: List<PortfolioPhoto>) {
        val list = mutableListOf<Row>()
        if (ownerMode) list += Row.AddTile
        photos.forEach { list += Row.Photo(it) }
        submitList(list)
    }

    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is Row.AddTile -> TYPE_ADD
            is Row.Photo -> TYPE_PHOTO
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_ADD) AddVH(ItemPortfolioAddBinding.inflate(inflater, parent, false))
        else PhotoVH(ItemPortfolioBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            Row.AddTile -> {
                (holder as AddVH).binding.root.setOnClickListener { onAddTap() }
            }
            is Row.Photo -> {
                val vh = holder as PhotoVH
                val data = if (item.photo.imageUri.startsWith("/")) File(item.photo.imageUri)
                else android.net.Uri.parse(item.photo.imageUri)
                vh.binding.photoIv.load(data) {
                    crossfade(true)
                    placeholder(R.drawable.ic_image_placeholder)
                    error(R.drawable.ic_image_placeholder)
                }
                vh.binding.deleteBtn.visibility = if (ownerMode) View.VISIBLE else View.GONE
                vh.binding.deleteBtn.setOnClickListener { onDelete(item.photo) }
            }
        }
    }

    inner class PhotoVH(val binding: ItemPortfolioBinding) : RecyclerView.ViewHolder(binding.root)
    inner class AddVH(val binding: ItemPortfolioAddBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        private const val TYPE_PHOTO = 0
        private const val TYPE_ADD = 1
        private val DIFF = object : DiffUtil.ItemCallback<Row>() {
            override fun areItemsTheSame(o: Row, n: Row) = when {
                o is Row.AddTile && n is Row.AddTile -> true
                o is Row.Photo && n is Row.Photo -> o.photo.id == n.photo.id
                else -> false
            }

            override fun areContentsTheSame(o: Row, n: Row) = o == n
        }
    }
}
