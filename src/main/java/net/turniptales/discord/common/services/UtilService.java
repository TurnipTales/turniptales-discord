package net.turniptales.discord.common.services;

import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;

import static java.lang.System.currentTimeMillis;
import static java.util.concurrent.TimeUnit.SECONDS;

public class UtilService {

    public static void sendSelfDeletingMessage(Event event, String message) {
        sendSelfDeletingMessage(event, message, 10);
    }

    public static void sendSelfDeletingMessage(Event event, String message, int seconds) {
        if (event instanceof IReplyCallback iReplyCallback) {
            iReplyCallback.reply(message + "\n-# 🚮 <t:" + (currentTimeMillis() / 1000 + seconds) + ":R>").setEphemeral(true).queue();
            iReplyCallback.getHook().deleteOriginal().queueAfter(seconds, SECONDS);
        }
    }
}
