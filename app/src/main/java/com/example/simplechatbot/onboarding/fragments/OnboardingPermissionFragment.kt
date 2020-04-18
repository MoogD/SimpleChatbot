package com.example.simplechatbot.onboarding.fragments

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.simplechatbot.R
import com.example.simplechatbot.onboarding.OnboardingActivity
import com.example.simplechatbot.onboarding.OnboardingViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.onboarding_permissions.*
import timber.log.Timber
import javax.inject.Inject

class OnboardingPermissionFragment : Fragment() {

    private var titleRes: Int? = null
    private var textRes: Int? = null
    private var permissions: Array<String>? = null

    private var snackbar: Snackbar? = null

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private lateinit var onboardingViewModel: OnboardingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
        arguments?.apply {
            titleRes = getInt(ARG_TITLE_RES)
            textRes = getInt(ARG_TEXT_RES)
            permissions = getStringArrayList(ARG_PERMISSIONS)?.toTypedArray()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.onboarding_permissions, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onboardingViewModel = ViewModelProviders.of(
            requireActivity(),
            factory
        )[OnboardingViewModel::class.java]

        nextButton.isEnabled = true
        nextButton.setOnClickListener(::onAllowClick)
    }

    override fun onResume() {
        super.onResume()

        permissions?.let {
            val granted = checkPermissionsGranted(it)

            if (granted)
                onboardingViewModel.onNextStep()
        }
    }
    private fun checkPermissionsGranted(permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (context?.let {
                    ContextCompat.checkSelfPermission(
                        it,
                        permission
                    )
                } != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    private fun onPermissionsRequest(permissions: Array<String>) {
        activity?.let {
            ActivityCompat.requestPermissions(
                it,
                permissions,
                OnboardingActivity.REQUEST_PERMISSIONS
            )
        }
    }

    private fun onAllowClick(view: View) {
        Timber.i("$view was clicked")
        permissions?.let {
            onPermissionsRequest(it)
        } ?: throw Exception("There is no permissions in list to ask!")
    }

    fun onPermissionsResult(grantResults: IntArray) {
        if (grantResults.isNotEmpty()) {
            for (grantResult in grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    snackbar = Snackbar.make(
                        allow,
                        "You can not use the app without giving the permissions",
                        Snackbar.LENGTH_INDEFINITE
                    ).apply {
                        setAction("Open Settings") {
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri = Uri.fromParts("package", context.packageName, null)
                            intent.data = uri
                            startActivity(intent)
                        }
                        show()
                    }
                }
            }
        }
    }

    companion object {
        private const val ARG_TITLE_RES = "titleRes"
        private const val ARG_TEXT_RES = "textRes"
        private const val ARG_PERMISSIONS = "permissions"

        @JvmStatic
        fun newInstance(
            @StringRes titleRes: Int,
            @StringRes textRes: Int,
            permissions: ArrayList<String>
        ) =
            OnboardingPermissionFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_TITLE_RES, titleRes)
                    putInt(ARG_TEXT_RES, textRes)
                    putStringArrayList(ARG_PERMISSIONS, permissions)
                }
            }
    }
}
