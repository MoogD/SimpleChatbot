package com.example.simplechatbot

import android.content.Context
import com.example.simplechatbot.annotationclasses.ApplicationContext
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AndroidModule(val context: Context) {

    @Provides
    @Singleton
    @ApplicationContext
    fun provideContext() = context

    @Provides
    @Singleton
    fun provideResources() = context.resources!!
}