package com.kaushalya.karnataka.ui.profile.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kaushalya.karnataka.R
import com.kaushalya.karnataka.data.entity.Service
import com.kaushalya.karnataka.databinding.BottomSheetAddServiceBinding

class AddServiceBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetAddServiceBinding? = null
    private val binding get() = _binding!!

    var onSave: (name: String, price: Int, isStartingAt: Boolean) -> Unit = { _, _, _ -> }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = BottomSheetAddServiceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val name = arguments?.getString(KEY_NAME)
        val price = arguments?.getInt(KEY_PRICE) ?: 0
        val isStarting = arguments?.getBoolean(KEY_STARTING) ?: false
        val isEditing = !name.isNullOrEmpty() || price > 0 || arguments?.getBoolean(KEY_HAS_VALUE) == true

        if (isEditing) {
            binding.sheetTitleTv.setText(R.string.edit_service_title)
            binding.serviceNameEt.setText(name)
            if (price > 0) binding.priceEt.setText(price.toString())
            binding.startingAtSwitch.isChecked = isStarting
        }

        binding.cancelBtn.setOnClickListener { dismiss() }

        binding.saveBtn.setOnClickListener {
            val n = binding.serviceNameEt.text?.toString()?.trim().orEmpty()
            val priceText = binding.priceEt.text?.toString()?.trim().orEmpty()
            val parsedPrice = priceText.toIntOrNull()

            binding.serviceNameTil.error = null
            binding.priceTil.error = null

            var ok = true
            if (n.isBlank()) {
                binding.serviceNameTil.error = getString(R.string.err_name_required)
                ok = false
            }
            if (parsedPrice == null || parsedPrice <= 0) {
                binding.priceTil.error = getString(R.string.err_price_invalid)
                ok = false
            }
            if (!ok) return@setOnClickListener

            onSave(n, parsedPrice!!, binding.startingAtSwitch.isChecked)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val KEY_NAME = "name"
        private const val KEY_PRICE = "price"
        private const val KEY_STARTING = "starting"
        private const val KEY_HAS_VALUE = "has_value"

        fun newInstance(service: Service?): AddServiceBottomSheet =
            AddServiceBottomSheet().apply {
                if (service != null) {
                    arguments = Bundle().apply {
                        putString(KEY_NAME, service.name)
                        putInt(KEY_PRICE, service.price)
                        putBoolean(KEY_STARTING, service.isStartingAt)
                        putBoolean(KEY_HAS_VALUE, true)
                    }
                }
            }
    }
}
