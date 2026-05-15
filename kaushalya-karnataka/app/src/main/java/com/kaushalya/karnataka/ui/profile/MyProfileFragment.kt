package com.kaushalya.karnataka.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.kaushalya.karnataka.R
import com.kaushalya.karnataka.databinding.FragmentMyProfileBinding
import com.kaushalya.karnataka.util.Prefs

/**
 * Bottom-nav "My Profile" tab. Two states:
 *   1. No profile yet → empty state with "Create my profile" CTA.
 *   2. Profile exists → embeds [ProfileFragment] in owner mode (edit, add service…).
 */
class MyProfileFragment : Fragment() {

    private var _binding: FragmentMyProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMyProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        renderState()

        binding.createProfileBtn.setOnClickListener {
            findNavController().navigate(
                R.id.action_myProfile_to_addWorker,
                Bundle().apply { putLong("workerId", 0L) }
            )
        }
    }

    override fun onResume() {
        super.onResume()
        renderState()
    }

    private fun renderState() {
        val id = Prefs.currentWorkerId(requireContext())
        if (id == 0L) {
            binding.emptyStateLayout.visibility = View.VISIBLE
            binding.profileHostContainer.visibility = View.GONE
        } else {
            binding.emptyStateLayout.visibility = View.GONE
            binding.profileHostContainer.visibility = View.VISIBLE

            val embedded = EmbeddedProfileFragment().apply {
                arguments = Bundle().apply { putLong("workerId", id) }
            }
            childFragmentManager.beginTransaction()
                .replace(R.id.profileHostContainer, embedded)
                .commitNow()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /** Same content as [ProfileFragment] but with no back button (it's a top-level tab). */
    class EmbeddedProfileFragment : ProfileFragment() {
        override val showBackButton: Boolean get() = false
    }
}
