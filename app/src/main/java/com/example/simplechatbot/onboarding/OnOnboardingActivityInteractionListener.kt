package com.example.simplechatbot.onboarding

interface OnOnboardingActivityInteractionListener {
    fun onNextStep(): Boolean
    fun checkPermissionsGranted(permissions: Array<String>): Boolean
    fun onPermissionsRequest(permissions: Array<String>)
}