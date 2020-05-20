package com.example.simplechatbot

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.simplechatbot.assistant.CredentialProviderImpl
import com.example.simplechatbot.assistant.MicrophoneListener
import com.example.simplechatbot.assistant.MicrophoneListenerImpl
import com.example.simplechatbot.assistant.SpeechAssistant
import com.example.simplechatbot.assistant.SpeechAssistantImpl
import com.example.simplechatbot.injections.ApplicationContext
import com.example.simplechatbot.injections.ViewModelKey
import com.example.simplechatbot.main.MainViewModel
import com.example.simplechatbot.onboarding.OnboardingViewModel
import com.example.simplechatbot.utils.IntentMatcher
import com.example.simplechatbot.utils.PathProvider
import com.example.simplechatbot.utils.PathProviderImp
import com.example.simplechatbot.utils.PreferenceHelper
import com.example.simplechatbot.utils.PreferenceHelperImpl
import com.example.simplechatbot.utils.PromptProvider
import com.google.api.gax.core.CredentialsProvider
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import javax.inject.Provider
import javax.inject.Singleton

@Module
class ChatBotApplicationModule {

    @Provides
    @Singleton
    fun provideCredentialProvider(
        @ApplicationContext context: Context
    ): CredentialsProvider = CredentialProviderImpl(context)

    @Provides
    fun provideIntentMatcher(): IntentMatcher =
        IntentMatcher

    @Provides
    fun providePromptProvider(): PromptProvider =
        PromptProvider

    @Provides
    @Singleton
    fun provideSpeechAssistant(
        intentMatcher: IntentMatcher,
        promptProvider: PromptProvider
    ): SpeechAssistant =
        SpeechAssistantImpl(intentMatcher, promptProvider)

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
    @Singleton
    fun providePathProvider(
        @ApplicationContext context: Context
    ): PathProvider = PathProviderImp(context)

    @Provides
    @Singleton
    fun provideMicrophoneListener(): MicrophoneListener = MicrophoneListenerImpl()

    @Provides
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    fun provideMainViewModel(
        preferenceHelper: PreferenceHelper,
        speechAssistant: SpeechAssistant,
        credentialProvider: CredentialsProvider,
        pathProvider: PathProvider,
        listener: MicrophoneListener
    ): ViewModel =
        MainViewModel(preferenceHelper, speechAssistant, credentialProvider, pathProvider, listener)

    @Provides
    @IntoMap
    @ViewModelKey(OnboardingViewModel::class)
    fun provideOnboardingViewModel(
        preferenceHelper: PreferenceHelper
    ): ViewModel =
        OnboardingViewModel(preferenceHelper)
}
