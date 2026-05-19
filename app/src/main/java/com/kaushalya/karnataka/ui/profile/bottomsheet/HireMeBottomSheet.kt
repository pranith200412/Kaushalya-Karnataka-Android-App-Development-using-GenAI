package com.kaushalya.karnataka.ui.profile.bottomsheet

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kaushalya.karnataka.R
import com.kaushalya.karnataka.databinding.BottomSheetHireMeBinding

class HireMeBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetHireMeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = BottomSheetHireMeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val name = arguments?.getString(KEY_NAME).orEmpty()
        val phone = arguments?.getString(KEY_PHONE).orEmpty()

        binding.messageTv.text = getString(R.string.hire_me_dialog_message, name)
        binding.callBtn.text = getString(R.string.hire_me_call_now, name)

        binding.callBtn.setOnClickListener {
            // We DIAL (intent ACTION_DIAL) rather than CALL so the user has a chance
            // to confirm — and so the demo doesn't need CALL_PHONE permission at runtime.
            val intent = Intent(Intent.ACTION_DIAL).apply { data = Uri.parse("tel:$phone") }
            try {
                startActivity(intent)
            } catch (_: Throwable) {
                // ignored: emulator may not have a dialer
            }
            dismiss()
        }

        binding.dismissBtn.setOnClickListener { dismiss() }

        // Simulated push notification — the assignment explicitly mentions this
        // ("Simulated notification") rather than a real call back-end.
        sendSimulatedNotification(name)
    }

    private fun sendSimulatedNotification(workerName: String) {
        val context = context ?: return
        val nm = ContextCompat.getSystemService(context, NotificationManager::class.java) ?: return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Hire-Me Requests",
                NotificationManager.IMPORTANCE_DEFAULT,
            )
            nm.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_phone)
            .setContentTitle(getString(R.string.hire_me_dialog_title))
            .setContentText(getString(R.string.hire_me_dialog_message, workerName))
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(getString(R.string.hire_me_dialog_message, workerName)))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        try {
            nm.notify(NOTIFICATION_ID, notification)
        } catch (_: SecurityException) {
            // POST_NOTIFICATIONS denied on Android 13+; ignore — the in-app sheet is enough.
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val CHANNEL_ID = "hire_me_demo"
        private const val NOTIFICATION_ID = 1001
        private const val KEY_NAME = "name"
        private const val KEY_PHONE = "phone"

        fun newInstance(workerName: String, phone: String) = HireMeBottomSheet().apply {
            arguments = Bundle().apply {
                putString(KEY_NAME, workerName)
                putString(KEY_PHONE, phone)
            }
        }
    }
}
