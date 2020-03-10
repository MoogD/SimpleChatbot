package com.example.simplechatbot.utils

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import java.util.*

object LocaleHelper {

    fun onAttach(context: Context, language: Locale): Context = setLocale(context, language)

    private fun setLocale(context: Context, language: Locale): Context =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            updateResources(context, language)
        } else {
            updateResourcesLegacy(context, language)
        }

    @TargetApi(Build.VERSION_CODES.N)
    private fun updateResources(context: Context, language: Locale): Context {
        Locale.setDefault(language)

        val configuration = context.resources.configuration
        configuration.setLocale(language)
        configuration.setLayoutDirection(language)

        return context.createConfigurationContext(configuration)
    }

    @Suppress("DEPRECATION")
    private fun updateResourcesLegacy(context: Context, language: Locale): Context {
        Locale.setDefault(language)

        val resources = context.resources

        val configuration = resources.configuration
        configuration.locale = language
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLayoutDirection(language)
        }

        resources.updateConfiguration(configuration, resources.displayMetrics)

        return context
    }
}