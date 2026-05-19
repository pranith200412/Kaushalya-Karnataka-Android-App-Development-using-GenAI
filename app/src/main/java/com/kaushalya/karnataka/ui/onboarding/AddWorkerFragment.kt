package com.kaushalya.karnataka.ui.onboarding

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.kaushalya.karnataka.KaushalyaApp
import com.kaushalya.karnataka.R
import com.kaushalya.karnataka.data.SeedData
import coil.load
import coil.transform.CircleCropTransformation
import com.kaushalya.karnataka.databinding.FragmentAddWorkerBinding
import com.kaushalya.karnataka.ui.viewmodel.AddWorkerViewModel
import com.kaushalya.karnataka.util.ImagePersist
import com.kaushalya.karnataka.util.Prefs
import kotlinx.coroutines.launch
import java.io.File

class AddWorkerFragment : Fragment() {

    private var _binding: FragmentAddWorkerBinding? = null
    private val binding get() = _binding!!

    private val workerId: Long
        get() = arguments?.getLong("workerId") ?: 0L

    private val viewModel: AddWorkerViewModel by viewModels {
        AddWorkerViewModel.Factory(
            (requireActivity().application as KaushalyaApp).repository,
            workerId,
        )
    }

    private var pickedAvatarPath: String? = null

    private val pickAvatar = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val path = ImagePersist.copyToInternal(requireContext(), uri)
            if (path != null) {
                pickedAvatarPath = path
                showAvatar(path)
            }
        }
    }

    private fun showAvatar(path: String) {
        binding.avatarPlaceholderIv.visibility = View.GONE
        binding.avatarIv.visibility = View.VISIBLE
        val data = if (path.startsWith("/")) File(path) else android.net.Uri.parse(path)
        binding.avatarIv.load(data) {
            crossfade(true)
            transformations(CircleCropTransformation())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAddWorkerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backBtn.setOnClickListener { findNavController().navigateUp() }

        binding.avatarPickerContainer.setOnClickListener { pickAvatar.launch("image/*") }
        binding.selectAvatarLabelTv.setOnClickListener { pickAvatar.launch("image/*") }

        // Populate category chips
        SeedData.CATEGORIES.forEach { cat ->
            val chip = Chip(requireContext()).apply {
                text = cat
                isCheckable = true
                isCheckedIconVisible = true
                setEnsureMinTouchTargetSize(false)
            }
            binding.categoryChips.addView(chip)
        }

        val isEditing = workerId != 0L
        binding.deleteBtn.visibility = if (isEditing) View.VISIBLE else View.GONE
        binding.screenTitleTv.text =
            if (isEditing) getString(R.string.edit_profile)
            else getString(R.string.become_worker_title)

        binding.deleteBtn.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setMessage(R.string.confirm_delete_profile)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.delete) { _, _ ->
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewModel.delete()
                        Prefs.clearCurrentWorker(requireContext())
                        findNavController().popBackStack(R.id.homeFragment, false)
                    }
                }
                .show()
        }

        binding.saveBtn.setOnClickListener { save() }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.initial.collect { existing ->
                    if (existing != null) populate(existing)
                }
            }
        }
    }

    private fun populate(w: com.kaushalya.karnataka.data.entity.Worker) {
        binding.nameEt.setText(w.name)
        binding.phoneEt.setText(w.phone)
        binding.townEt.setText(w.town)
        binding.bioEt.setText(w.bio)

        // Pre-select the matching category chip
        for (i in 0 until binding.categoryChips.childCount) {
            val chip = binding.categoryChips.getChildAt(i) as Chip
            if (chip.text.toString() == w.category) chip.isChecked = true
        }

        if (!w.avatarUri.isNullOrBlank()) {
            pickedAvatarPath = w.avatarUri
            showAvatar(w.avatarUri)
        }
    }

    private fun save() {
        val name = binding.nameEt.text?.toString()?.trim().orEmpty()
        val phone = binding.phoneEt.text?.toString()?.trim().orEmpty()
        val town = binding.townEt.text?.toString()?.trim().orEmpty()
        val bio = binding.bioEt.text?.toString()?.trim().orEmpty()
        val selectedChip =
            (0 until binding.categoryChips.childCount)
                .map { binding.categoryChips.getChildAt(it) as Chip }
                .firstOrNull { it.isChecked }
        val category = selectedChip?.text?.toString().orEmpty()

        binding.nameTil.error = null
        binding.phoneTil.error = null
        var ok = true
        if (name.isBlank()) { binding.nameTil.error = getString(R.string.err_name_required); ok = false }
        if (phone.isBlank()) { binding.phoneTil.error = getString(R.string.err_phone_required); ok = false }
        if (category.isBlank()) {
            com.google.android.material.snackbar.Snackbar.make(
                binding.root,
                getString(R.string.err_category_required),
                com.google.android.material.snackbar.Snackbar.LENGTH_SHORT,
            ).show()
            ok = false
        }
        if (!ok) return

        viewLifecycleOwner.lifecycleScope.launch {
            val newId = viewModel.save(name, category, phone, town, bio, pickedAvatarPath)
            Prefs.setCurrentWorkerId(requireContext(), newId)
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
