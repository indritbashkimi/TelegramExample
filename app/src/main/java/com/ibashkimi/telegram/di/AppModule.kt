package com.ibashkimi.telegram.di

import android.content.Context
import android.os.Build
import com.ibashkimi.telegram.R
import com.ibashkimi.telegram.data.TelegramClient
import com.ibashkimi.telegram.data.UserRepository
import com.ibashkimi.telegram.data.chats.ChatsRepository
import com.ibashkimi.telegram.data.messages.MessagesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.drinkless.td.libcore.telegram.TdApi
import java.util.*
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    @Named("ioDispatcher")
    fun provideIoDispatcher() = Dispatchers.IO

    @Singleton
    @Provides
    @Named("applicationScope")
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())

    @Provides
    fun provideTdlibParameters(@ApplicationContext context: Context): TdApi.TdlibParameters {
        return TdApi.TdlibParameters().apply {
            // Obtain application identifier hash for Telegram API access at https://my.telegram.org
            apiId = context.resources.getInteger(R.integer.telegram_api_id)
            apiHash = context.getString(R.string.telegram_api_hash)
            useMessageDatabase = true
            useSecretChats = true
            systemLanguageCode = Locale.getDefault().language
            databaseDirectory = context.filesDir.absolutePath
            deviceModel = Build.MODEL
            systemVersion = Build.VERSION.RELEASE
            applicationVersion = "0.1"
            enableStorageOptimizer = true
        }
    }

    @Singleton
    @Provides
    fun provideTelegramClient(parameters: TdApi.TdlibParameters) = TelegramClient(parameters)

    @Provides
    fun provideChatsRepository(client: TelegramClient) = ChatsRepository(client)

    @Provides
    fun provideMessagesRepository(client: TelegramClient) = MessagesRepository(client)

    @Provides
    fun provideUserRepository(client: TelegramClient) = UserRepository(client)
}
