package com.kaushalya.karnataka.ui.profile.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.kaushalya.karnataka.R
import com.kaushalya.karnataka.databinding.BottomSheetAddReviewBinding

class AddReviewBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetAddReviewBinding? = null
    private val binding get() = _binding!!

    var onSubmit: (name: String, rating: Int, comment: String) -> Unit = { _, _, _ -> }

    private var rating = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = BottomSheetAddReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val stars = listOf(binding.star1, binding.star2, binding.star3, binding.star4, binding.star5)
        stars.forEachIndexed { idx, iv ->
            iv.setOnClickListener {
                rating = idx + 1
                renderStars(stars)
            }
        }

        binding.submitBtn.setOnClickListener {
            val name = binding.reviewerEt.text?.toString()?.trim().orEmpty()
            val comment = binding.commentEt.text?.toString()?.trim().orEmpty()

            binding.reviewerTil.error = null
            binding.commentTil.error = null

            var ok = true
            if (name.isBlank()) {
                binding.reviewerTil.error = getString(R.string.err_name_required)
                ok = false
            }
            if (comment.length < 5) {
                binding.commentTil.error = getString(R.string.err_review_short)
                ok = false
            }
            if (rating < 1) {
                Snackbar.make(binding.root, R.string.err_rating_required, Snackbar.LENGTH_SHORT).show()
                ok = false
            }
            if (!ok) return@setOnClickListener

            onSubmit(name, rating, comment)
            dismiss()
        }
    }

    private fun renderStars(stars: List<ImageView>) {
        stars.forEachIndexed { i, iv ->
            iv.setImageResource(
                if (i < rating) R.drawable.ic_star else R.drawable.ic_star_border
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = AddReviewBottomSheet()
    }
}
