package io.github.eone666.telegramnotifier.telegram

import com.elbekd.bot.Bot
import com.elbekd.bot.model.toChatId
import io.github.eone666.telegramnotifier.pluginInstance
import kotlin.jvm.Throws

class Bot @Throws(IllegalArgumentException::class) constructor(token: String) {
    val tg: Bot

    private val config = pluginInstance.config

    init {
        tg = Bot.createPolling(token = token)
        tg.start()

        tg.onCommand("/setup") { (msg, _) ->
            val args = msg.text?.split(" ")
            val chatId: Long = msg.chat.id
            if (args?.get(1) == pluginInstance.oneTimePasswordForSender.code) {
                config.token.set(pluginInstance.oneTimePasswordForSender.token) // save token from minecraft command
                config.chatId.set(chatId.toString()) // save chat_id from person who wrote command to bot
                config.isPluginConfigured.set(true)
                config.save()
                pluginInstance.initFeatures()
                tg.sendMessage(text = "Set up successfully", chatId = chatId.toChatId())
                pluginInstance.oneTimePasswordForSender.sendMessage("Set up successfully")
            } else {
                tg.sendMessage(text = "Wrong code", chatId = chatId.toChatId())
            }
        }
    }

    fun stop() {
        this.tg.stop()
    }
}