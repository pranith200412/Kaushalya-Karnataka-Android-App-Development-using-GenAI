package com.kaushalya.karnataka.ui.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kaushalya.karnataka.R
import com.kaushalya.karnataka.data.entity.Review
import com.kaushalya.karnataka.databinding.ItemReviewBinding
import com.kaushalya.karnataka.util.Format

class ReviewAdapter : ListAdapter<Review, ReviewAdapter.VH>(DIFF) {

    inner class VH(val binding: ItemReviewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        val context = holder.itemView.context
        with(holder.binding) {
            reviewerNameTv.text = item.reviewerName
            commentTv.text = item.comment
            timestampTv.text = Format.timestamp(item.createdAt)

            starsRow.removeAllViews()
            val starSize = (16 * context.resources.displayMetrics.density).toInt()
            val starGap = (2 * context.resources.displayMetrics.density).toInt()
            repeat(5) { i ->
                val star = ImageView(context).apply {
                    setImageResource(
                        if (i < item.rating) R.drawable.ic_star
                        else R.drawable.ic_star_border
                    )
                    layoutParams = LinearLayout.LayoutParams(starSize, starSize).apply {
                        if (i < 4) marginEnd = starGap
                    }
                    importantForAccessibility = android.view.View.IMPORTANT_FOR_ACCESSIBILITY_NO
                }
                starsRow.addView(star)
            }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Review>() {
            override fun areItemsTheSame(o: Review, n: Review) = o.id == n.id
            override fun areContentsTheSame(o: Review, n: Review) = o == n
        }
    }
}
