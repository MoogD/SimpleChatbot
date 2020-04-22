package com.example.simplechatbot

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.simplechatbot.assistant.CredentialProvider
import com.example.simplechatbot.assistant.CredentialProviderImpl
import com.example.simplechatbot.assistant.SpeechAssistant
import com.example.simplechatbot.assistant.SpeechAssistantImpl
import com.example.simplechatbot.injections.ApplicationContext
import com.example.simplechatbot.injections.ViewModelKey
import com.example.simplechatbot.main.MainViewModel
import com.example.simplechatbot.onboarding.OnboardingViewModel
import com.example.simplechatbot.utils.PreferenceHelper
import com.example.simplechatbot.utils.PreferenceHelperImpl
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import javax.inject.Provider
import javax.inject.Singleton

@Module
class ChatBotApplicationModule {

    @Provides
    @Singleton
    fun provideCredentialProvider (
        @ApplicationContext context: Context
    ): CredentialProvider = CredentialProviderImpl(context)

    @Provides
    @Singleton
    fun provideSpeechAssistant (): SpeechAssistant = SpeechAssistantImpl()

    @Provides
    @Singleton
    fun providePreferenceHelper(
        @ApplicationContext context: Context
    ): PreferenceHelper = PreferenceHelperImpl(context)

    @Suppress("UNCHECKED_CAST")
    @Provides
    @Singleton
    fun provideViewModelFactory(
        providers: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
    ) = object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return requireNotNull(providers[modelClass as Class<out ViewModel>]).get() as T
        }
    }

    @Provides
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    fun provideMainViewModel(
        preferenceHelper: PreferenceHelper,
        speechAssistant: SpeechAssistant,
        credentialProvider: CredentialProvider
    ): ViewModel =
        MainViewModel(preferenceHelper, speechAssistant, credentialProvider)

    @Provides
    @IntoMap
    @ViewModelKey(OnboardingViewModel::class)
    fun provideOnboardingViewModel(
        preferenceHelper: PreferenceHelper
    ): ViewModel =
        OnboardingViewModel(preferenceHelper)
}
