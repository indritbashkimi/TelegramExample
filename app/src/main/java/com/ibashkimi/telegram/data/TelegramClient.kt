package com.ibashkimi.telegram.data

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.compose.Model
import com.ibashkimi.telegram.Configuration
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.drinkless.td.libcore.telegram.Client
import org.drinkless.td.libcore.telegram.TdApi
import java.util.*

object TelegramClient : Client.ResultHandler {

    private val TAG = TelegramClient::class.java.simpleName

    val client = Client.create(this, null, null)

    val authState = AuthState()

    lateinit var application: Application // todo

    init {
        client.send(TdApi.GetAuthorizationState(), this)
    }

    fun close() {
        client.close()
    }

    private val scope = CoroutineScope(Dispatchers.Main)

    private val requestScope = CoroutineScope(Dispatchers.IO)

    private fun setAuth(auth: Authentication) {
        scope.launch {
            authState.auth = auth
        }
    }

    override fun onResult(data: TdApi.Object) {
        Log.d(TAG, "onResult: ${data::class.java.simpleName}")
        when (data.constructor) {
            TdApi.UpdateAuthorizationState.CONSTRUCTOR -> {
                Log.d(TAG, "UpdateAuthorizationState")
                onAuthorizationStateUpdated((data as TdApi.UpdateAuthorizationState).authorizationState)
            }
            TdApi.UpdateOption.CONSTRUCTOR -> {

            }

            else -> Log.d(TAG, "Unhandled onResult call with data: $data.")
        }
    }

    private fun doAsync(job: () -> Unit) {
        requestScope.launch { job() }
    }

    fun startAuthentication() {
        Log.d(TAG, "startAuthentication called")
        if (authState.auth != Authentication.UNAUTHENTICATED) {
            throw IllegalStateException("Start authentication called but client already authenticated. State: ${authState.auth}.")
        }

        doAsync {
            val tdLibParameters = TdApi.TdlibParameters().apply {
                apiId = Configuration.API_ID
                apiHash = Configuration.API_HASH
                useMessageDatabase = true
                useSecretChats = true
                systemLanguageCode = Locale.getDefault().language
                databaseDirectory = application.filesDir.absolutePath
                deviceModel = Build.MODEL
                systemVersion = Build.VERSION.RELEASE
                applicationVersion = "0.1"
                enableStorageOptimizer = true
            }

            client.send(TdApi.SetTdlibParameters(tdLibParameters)) {
                Log.d(TAG, "SetTdlibParameters result: $it")
                when (it.constructor) {
                    TdApi.Ok.CONSTRUCTOR -> {
                        //result.postValue(true)
                    }
                    TdApi.Error.CONSTRUCTOR -> {
                        //result.postValue(false)
                    }
                }
            }
        }
    }

    fun insertPhoneNumber(phoneNumber: String) {
        Log.d("TelegramClient", "phoneNumber: $phoneNumber")
        val settings = TdApi.PhoneNumberAuthenticationSettings(
                false,
                false,
                false
        )
        client.send(TdApi.SetAuthenticationPhoneNumber(phoneNumber, settings)) {
            Log.d("TelegramClient", "phoneNumber. result: $it")
            when (it.constructor) {
                TdApi.Ok.CONSTRUCTOR -> {

                }
                TdApi.Error.CONSTRUCTOR -> {

                }
            }
        }
    }

    fun insertCode(code: String) {
        Log.d("TelegramClient", "code: $code")
        doAsync {
            client.send(TdApi.CheckAuthenticationCode(code)) {
                when (it.constructor) {
                    TdApi.Ok.CONSTRUCTOR -> {

                    }
                    TdApi.Error.CONSTRUCTOR -> {

                    }
                }
            }
        }
    }

    fun insertPassword(password: String) {
        Log.d("TelegramClient", "inserting password")
        doAsync {
            client.send(TdApi.CheckAuthenticationPassword(password)) {
                when (it.constructor) {
                    TdApi.Ok.CONSTRUCTOR -> {

                    }
                    TdApi.Error.CONSTRUCTOR -> {

                    }
                }
            }
        }
    }

    private fun onAuthorizationStateUpdated(authorizationState: TdApi.AuthorizationState) {
        when (authorizationState.constructor) {
            TdApi.AuthorizationStateWaitTdlibParameters.CONSTRUCTOR -> {
                Log.d(TAG, "onResult: AuthorizationStateWaitTdlibParameters -> state = UNAUTHENTICATED")
                setAuth(Authentication.UNAUTHENTICATED)
            }
            TdApi.AuthorizationStateWaitEncryptionKey.CONSTRUCTOR -> {
                Log.d(TAG, "onResult: AuthorizationStateWaitEncryptionKey")
                client.send(TdApi.CheckDatabaseEncryptionKey()) {
                    when (it.constructor) {
                        TdApi.Ok.CONSTRUCTOR -> {
                            Log.d(TAG, "CheckDatabaseEncryptionKey: OK")
                        }
                        TdApi.Error.CONSTRUCTOR -> {
                            Log.d(TAG, "CheckDatabaseEncryptionKey: Error")
                        }
                    }
                }
            }
            TdApi.AuthorizationStateWaitPhoneNumber.CONSTRUCTOR -> {
                Log.d(TAG, "onResult: AuthorizationStateWaitPhoneNumber -> state = WAIT_FOR_NUMBER")
                setAuth(Authentication.WAIT_FOR_NUMBER)
            }
            TdApi.AuthorizationStateWaitCode.CONSTRUCTOR -> {
                Log.d(TAG, "onResult: AuthorizationStateWaitCode -> state = WAIT_FOR_CODE")
                setAuth(Authentication.WAIT_FOR_CODE)
            }
            TdApi.AuthorizationStateWaitPassword.CONSTRUCTOR -> {
                Log.d(TAG, "onResult: AuthorizationStateWaitPassword")
                setAuth(Authentication.WAIT_FOR_PASSWORD)
            }
            TdApi.AuthorizationStateReady.CONSTRUCTOR -> {
                Log.d(TAG, "onResult: AuthorizationStateReady -> state = AUTHENTICATED")
                setAuth(Authentication.AUTHENTICATED)
            }
            TdApi.AuthorizationStateLoggingOut.CONSTRUCTOR -> {
                Log.d(TAG, "onResult: AuthorizationStateLoggingOut")
                setAuth(Authentication.UNAUTHENTICATED)
            }
            TdApi.AuthorizationStateClosing.CONSTRUCTOR -> {
                Log.d(TAG, "onResult: AuthorizationStateClosing")
            }
            TdApi.AuthorizationStateClosed.CONSTRUCTOR -> {
                Log.d(TAG, "onResult: AuthorizationStateClosed")
            }
            else -> Log.d(TAG, "Unhandled authorizationState with data: $authorizationState.")
        }

    }

    fun loadChatIds(): TdRequest<LongArray> {
        val request = TdRequest<LongArray>()

        doAsync {
            client.send(TdApi.GetChats(TdApi.ChatListMain(), Long.MAX_VALUE, 0, 50)) {
                scope.launch {
                    when (it.constructor) {
                        TdApi.Chats.CONSTRUCTOR -> {
                            val chatIds = (it as TdApi.Chats).chatIds
                            request.result = TdResult.Success(chatIds)
                        }
                        TdApi.Error.CONSTRUCTOR -> {
                            request.result = TdResult.Error(null)
                        }
                        else -> {
                            request.result = TdResult.Error(null)
                        }
                    }
                }
            }
        }

        return request
    }

    fun loadChats(): TdRequest<List<TdApi.Chat>> {
        val request = TdRequest<List<TdApi.Chat>>()

        doAsync {
            client.send(TdApi.GetChats(TdApi.ChatListMain(), Long.MAX_VALUE, 0, 50)) {
                scope.launch {
                    when (it.constructor) {
                        TdApi.Chats.CONSTRUCTOR -> {
                            val chatIds = (it as TdApi.Chats).chatIds
                            val chats = ArrayList<TdApi.Chat>()
                            chatIds.forEach {
                                chats.add(loadChat(it))
                            }
                            request.result = TdResult.Success(chats)
                        }
                        TdApi.Error.CONSTRUCTOR -> {
                            request.result = TdResult.Error(null)
                        }
                        else -> {
                            request.result = TdResult.Error(null)
                        }
                    }
                }
            }
        }

        return request
    }

    fun getChat(chatId: Long): TdRequest<TdApi.Chat> {
        val request = TdRequest<TdApi.Chat>()
        requestScope.launch {
            val chat = loadChat(chatId)
            scope.launch { request.result = TdResult.Success(chat) }
        }
        return request
    }

    suspend fun loadChat(chatId: Long): TdApi.Chat {
        val chat = CompletableDeferred<TdApi.Chat>()
        doAsync {
            client.send(TdApi.GetChat(chatId)) {
                //throw Exception("Tana oh")
                Log.d(TAG, "getChat result $it")
                when (it.constructor) {
                    TdApi.Chat.CONSTRUCTOR -> {
                        chat.complete(it as TdApi.Chat)
                    }
                    TdApi.Error.CONSTRUCTOR -> {
                        chat.completeExceptionally(Exception("Something went wrong"))
                    }
                    else -> {
                        chat.completeExceptionally(Exception("Something went wrong"))
                    }
                }
            }
        }
        return chat.await()
    }

    fun getMessages(chatId: Long): TdRequest<TdApi.Messages> {
        val request = TdRequest<TdApi.Messages>()
        doAsync {
            client.send(TdApi.GetChatHistory(chatId, 0, 0, 100, false)) {
                scope.launch {
                    when (it.constructor) {
                        TdApi.Messages.CONSTRUCTOR -> {
                            request.result = TdResult.Success((it as TdApi.Messages))
                        }
                        TdApi.Error.CONSTRUCTOR -> {
                            request.result = TdResult.Error(null)
                        }
                        else -> {
                            request.result = TdResult.Error(null)
                        }
                    }
                }
            }
        }
        return request
    }

    fun getMessage(chatId: Long, messageId: Long): TdRequest<TdApi.Message> {
        val request = TdRequest<TdApi.Message>()
        requestScope.launch {
            val chat = loadMessage(chatId, messageId)
            scope.launch { request.result = TdResult.Success(chat) }
        }
        return request
    }

    private suspend fun loadMessage(chatId: Long, messageId: Long): TdApi.Message {
        val message = CompletableDeferred<TdApi.Message>()
        doAsync {
            client.send(TdApi.GetMessage(chatId, messageId)) {
                //throw Exception("Tana oh")
                Log.d(TAG, "getMessage result $it")
                when (it.constructor) {
                    TdApi.Message.CONSTRUCTOR -> {
                        message.complete(it as TdApi.Message)
                    }
                    TdApi.Error.CONSTRUCTOR -> {
                        message.completeExceptionally(Exception("Something went wrong"))
                    }
                    else -> {
                        message.completeExceptionally(Exception("Something went wrong"))
                    }
                }
            }
        }
        return message.await()
    }
}

@Model
class AuthState(var auth: Authentication = Authentication.UNKNOWN)

enum class Authentication {
    UNAUTHENTICATED,
    WAIT_FOR_NUMBER,
    WAIT_FOR_CODE,
    WAIT_FOR_PASSWORD,
    AUTHENTICATED,
    UNKNOWN
}

@Model
class TdRequest<T>(var result: TdResult<T> = TdResult.Loading())

sealed class TdResult<T> {
    class Loading<T> : TdResult<T>()
    class Success<T>(val result: T) : TdResult<T>()
    class Error<T>(val throwable: Throwable? = null) : TdResult<T>()
}