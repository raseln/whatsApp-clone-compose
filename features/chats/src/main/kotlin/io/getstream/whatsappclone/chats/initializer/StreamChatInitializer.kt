/*
 * Copyright 2023 Stream.IO, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.getstream.whatsappclone.chats.initializer

import android.content.Context
import androidx.startup.Initializer
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.offline.model.message.attachments.UploadAttachmentsNetworkType
import io.getstream.chat.android.offline.plugin.configuration.Config
import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory
import io.getstream.log.streamLog
import io.getstream.whatsappclone.chats.BuildConfig
import io.getstream.whatsappclone.chats.R

/**
 * StreamChatInitializer initializes all Stream Client components.
 */
class StreamChatInitializer : Initializer<Unit> {

  override fun create(context: Context) {
    streamLog { "StreamChatInitializer is initialized" }

    /**
     * initialize a global instance of the [ChatClient].
     * The ChatClient is the main entry point for all low-level operations on chat.
     * e.g, connect/disconnect user to the server, send/update/pin message, etc.
     */
    val logLevel = if (BuildConfig.DEBUG) ChatLogLevel.ALL else ChatLogLevel.NOTHING
    val offlinePluginFactory = StreamOfflinePluginFactory(
      config = Config(
        backgroundSyncEnabled = true,
        userPresence = true,
        persistenceEnabled = true,
        uploadAttachmentsNetworkType = UploadAttachmentsNetworkType.NOT_ROAMING
      ),
      appContext = context
    )
    val chatClient = ChatClient.Builder(context.getString(R.string.stream_api_key), context)
      .withPlugin(offlinePluginFactory)
      .logLevel(logLevel)
      .build()

    val user = User(
      id = "stream",
      name = "stream",
      image = "https://placekitten.com/200/300"
    )

    val token = chatClient.devToken(user.id)
    chatClient.connectUser(user, token).enqueue()
  }

  override fun dependencies(): List<Class<out Initializer<*>>> =
    listOf(StreamLogInitializer::class.java)
}
