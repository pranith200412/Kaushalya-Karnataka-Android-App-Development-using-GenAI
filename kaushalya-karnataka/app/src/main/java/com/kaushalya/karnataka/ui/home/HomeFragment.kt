package com.kaushalya.karnataka.ui.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.kaushalya.karnataka.KaushalyaApp
import com.kaushalya.karnataka.R
import com.kaushalya.karnataka.data.SeedData
import com.kaushalya.karnataka.databinding.FragmentHomeBinding
import com.kaushalya.karnataka.ui.viewmodel.HomeViewModel
import com.kaushalya.karnataka.util.Prefs
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels {
        HomeViewModel.Factory((requireActivity().application as KaushalyaApp).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = WorkerAdapter { item ->
            findNavController().navigate(
                R.id.action_home_to_profile,
                Bundle().apply { putLong("workerId", item.worker.id) }
            )
        }
        binding.workerRv.layoutManager = LinearLayoutManager(requireContext())
        binding.workerRv.adapter = adapter
        binding.workerRv.isNestedScrollingEnabled = false

        setupCategoryChips()

        binding.searchEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.query.value = s?.toString().orEmpty()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.becomeWorkerFab.setOnClickListener {
            val currentId = Prefs.currentWorkerId(requireContext())
            findNavController().navigate(
                R.id.action_home_to_addWorker,
                Bundle().apply { putLong("workerId", currentId) }
            )
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.workers.collect { list ->
                    adapter.submitList(list)
                    val isEmpty = list.isEmpty()
                    binding.workerRv.visibility = if (isEmpty) View.GONE else View.VISIBLE
                    binding.emptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
                }
            }
        }
    }

    private fun setupCategoryChips() {
        val chipGroup = binding.categoryChipGroup
        val labels = listOf(getString(R.string.filter_all)) + SeedData.CATEGORIES

        labels.forEachIndexed { index, label ->
            val chip = Chip(requireContext()).apply {
                text = label
                isCheckable = true
                isCheckedIconVisible = false
                setChipBackgroundColorResource(R.color.chip_filter_bg)
                setTextColor(resources.getColorStateList(R.color.chip_filter_fg, null))
                chipStrokeWidth = 1f
                setChipStrokeColorResource(R.color.brand_outline_variant)
                setEnsureMinTouchTargetSize(false)
                if (index == 0) isChecked = true
                setOnClickListener {
                    viewModel.category.value = if (index == 0) null else label
                }
            }
            chipGroup.addView(chip)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
