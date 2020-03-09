package com.example.simplechatbot

import android.content.Context
import com.example.simplechatbot.annotationclasses.ApplicationContext
import com.example.simplechatbot.utils.AppStateManager
import com.example.simplechatbot.utils.AppStateManagerImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ChatBotApplicationModule {
    @Provides
    @Singleton
    fun provideAppStateManager(
        @ApplicationContext context: Context
    ): AppStateManager = AppStateManagerImpl(context)
}