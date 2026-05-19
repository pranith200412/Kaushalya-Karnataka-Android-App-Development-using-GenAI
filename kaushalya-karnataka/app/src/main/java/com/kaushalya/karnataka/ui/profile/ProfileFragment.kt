package com.kaushalya.karnataka.ui.profile

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.kaushalya.karnataka.KaushalyaApp
import com.kaushalya.karnataka.R
import com.kaushalya.karnataka.data.entity.Service
import com.kaushalya.karnataka.databinding.FragmentProfileBinding
import com.kaushalya.karnataka.ui.profile.bottomsheet.AddReviewBottomSheet
import com.kaushalya.karnataka.ui.profile.bottomsheet.AddServiceBottomSheet
import com.kaushalya.karnataka.ui.profile.bottomsheet.HireMeBottomSheet
import com.kaushalya.karnataka.ui.viewmodel.ProfileViewModel
import com.kaushalya.karnataka.util.AvatarBinder
import com.kaushalya.karnataka.util.Format
import com.kaushalya.karnataka.util.ImagePersist
import com.kaushalya.karnataka.util.Prefs
import kotlinx.coroutines.launch

open class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val workerId: Long
        get() = arguments?.getLong("workerId") ?: 0L

    /** Override in MyProfileFragment so the embedded version doesn't show a back button. */
    protected open val showBackButton: Boolean get() = true

    private val viewModel: ProfileViewModel by viewModels {
        ProfileViewModel.Factory(
            (requireActivity().application as KaushalyaApp).repository,
            workerId,
        )
    }

    private val pickPortfolioPhoto = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val path = ImagePersist.copyToInternal(requireContext(), uri)
            if (path != null) viewModel.addPortfolio(path)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backBtn.visibility = if (showBackButton) View.VISIBLE else View.GONE
        binding.backBtn.setOnClickListener { findNavController().navigateUp() }

        val isOwner = workerId != 0L && Prefs.currentWorkerId(requireContext()) == workerId

        binding.editProfileBtn.visibility = if (isOwner) View.VISIBLE else View.GONE
        binding.addServiceBtn.visibility = if (isOwner) View.VISIBLE else View.GONE
        binding.addPortfolioBtn.visibility = if (isOwner) View.VISIBLE else View.GONE
        // Reviews come from neighbours, not the worker themselves.
        binding.addReviewBtn.visibility = if (isOwner) View.GONE else View.VISIBLE
        binding.hireMeBtn.visibility = if (isOwner) View.GONE else View.VISIBLE

        val serviceAdapter = ServiceAdapter(
            ownerMode = isOwner,
            onEdit = { showAddServiceSheet(it) },
            onDelete = { showDeleteServiceDialog(it) },
        )
        binding.servicesRv.layoutManager = LinearLayoutManager(requireContext())
        binding.servicesRv.adapter = serviceAdapter
        binding.servicesRv.isNestedScrollingEnabled = false

        val reviewAdapter = ReviewAdapter()
        binding.reviewsRv.layoutManager = LinearLayoutManager(requireContext())
        binding.reviewsRv.adapter = reviewAdapter
        binding.reviewsRv.isNestedScrollingEnabled = false

        val portfolioAdapter = PortfolioAdapter(
            ownerMode = isOwner,
            onAddTap = { pickPortfolioPhoto.launch("image/*") },
            onDelete = { photo ->
                AlertDialog.Builder(requireContext())
                    .setMessage(R.string.confirm_delete_photo)
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.delete) { _, _ -> viewModel.deletePortfolio(photo) }
                    .show()
            },
        )
        binding.portfolioRv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.portfolioRv.adapter = portfolioAdapter

        binding.editProfileBtn.setOnClickListener {
            findNavController().navigate(
                R.id.action_profile_to_editWorker,
                Bundle().apply { putLong("workerId", workerId) }
            )
        }

        binding.addServiceBtn.setOnClickListener { showAddServiceSheet(service = null) }

        binding.addPortfolioBtn.setOnClickListener { pickPortfolioPhoto.launch("image/*") }

        binding.addReviewBtn.setOnClickListener {
            AddReviewBottomSheet.newInstance().also { sheet ->
                sheet.onSubmit = { name, rating, comment ->
                    viewModel.addReview(name, rating, comment)
                }
            }.show(parentFragmentManager, "add-review")
        }

        binding.hireMeBtn.setOnClickListener {
            val w = currentWorker?.worker ?: return@setOnClickListener
            HireMeBottomSheet.newInstance(w.name, w.phone)
                .show(parentFragmentManager, "hire-me")
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.worker.collect { renderWorker(it) } }
                launch {
                    viewModel.services.collect { list ->
                        serviceAdapter.submitList(list)
                        binding.servicesEmptyTv.visibility =
                            if (list.isEmpty() && !isOwner) View.VISIBLE else View.GONE
                    }
                }
                launch {
                    viewModel.reviews.collect { list ->
                        reviewAdapter.submitList(list)
                        binding.reviewsEmptyTv.visibility =
                            if (list.isEmpty()) View.VISIBLE else View.GONE
                    }
                }
                launch {
                    viewModel.portfolio.collect { list ->
                        portfolioAdapter.submitPhotos(list)
                        binding.portfolioEmptyTv.visibility =
                            if (list.isEmpty() && !isOwner) View.VISIBLE else View.GONE
                    }
                }
            }
        }
    }

    private var currentWorker: com.kaushalya.karnataka.data.entity.WorkerWithStats? = null

    private fun renderWorker(item: com.kaushalya.karnataka.data.entity.WorkerWithStats?) {
        currentWorker = item
        if (item == null) return
        val w = item.worker

        binding.nameTv.text = w.name
        binding.categoryPillTv.text = w.category
        binding.townPillTv.text = w.town
        binding.bioTv.text = w.bio.ifBlank {
            getString(R.string.town_dot_category, w.category, w.town)
        }
        binding.ratingValueTv.text = Format.rating(item.averageRating)
        binding.reviewsCountTv.text = Format.reviews(requireContext(), item.reviewCount)

        AvatarBinder.bind(
            binding.avatarContainer, binding.avatarInitialsTv, binding.avatarIv,
            w.name, w.avatarUri,
        )
    }

    private fun showAddServiceSheet(service: Service?) {
        AddServiceBottomSheet.newInstance(service).also { sheet ->
            sheet.onSave = { name, price, isStartingAt ->
                val toSave = (service ?: Service(workerId = workerId, name = "", price = 0))
                    .copy(name = name, price = price, isStartingAt = isStartingAt)
                viewModel.upsertService(toSave)
            }
        }.show(parentFragmentManager, "add-service")
    }

    private fun showDeleteServiceDialog(service: Service) {
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.confirm_delete_service)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.delete) { _, _ -> viewModel.deleteService(service) }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
