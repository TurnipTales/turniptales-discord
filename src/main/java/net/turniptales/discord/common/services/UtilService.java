package net.turniptales.discord.common.services;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.turniptales.discord.common.api.model.ConnectionDataValue;
import org.springframework.http.ResponseEntity;

import static java.lang.System.currentTimeMillis;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.SECONDS;
import static net.turniptales.discord.TurnipTalesDiscord.api;
import static net.turniptales.discord.TurnipTalesDiscord.discordBotProperties;
import static net.turniptales.discord.common.services.RoleSyncService.getHighestTimeRole;
import static net.turniptales.discord.common.services.RoleSyncService.getTimeRoles;

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

    public static void synchronise(Event event, Member member) {
        ResponseEntity<ConnectionDataValue> connectionDataValueResponseEntity = api.getData(member.getId());
        if (!connectionDataValueResponseEntity.getStatusCode().is2xxSuccessful()) {
            return;
        }

        ConnectionDataValue connectionDataValue = connectionDataValueResponseEntity.getBody();
        Guild guild = discordBotProperties.getGuild();

        guild.removeRoleFromMember(member, discordBotProperties.getSeniorModeratorRole()).queue();
        guild.removeRoleFromMember(member, discordBotProperties.getModeratorRole()).queue();
        guild.removeRoleFromMember(member, discordBotProperties.getSupporterRole()).queue();
        guild.removeRoleFromMember(member, discordBotProperties.getTeamBuilderRole()).queue();
        guild.removeRoleFromMember(member, discordBotProperties.getTeamContentRole()).queue();
        guild.removeRoleFromMember(member, discordBotProperties.getTeamSocialMediaRole()).queue();
        getTimeRoles().forEach(role -> guild.removeRoleFromMember(member, role).queue());

        guild.addRoleToMember(member, discordBotProperties.getPlayerRole()).queue();

        // ignore ADMINISTRATOR and HEAD_OF_DEVELOPMENT due to no permission
        // ignore DEVELOPER due to not overwrite support-roles
        switch (requireNonNull(connectionDataValue).getRole()) {
            case "SENIOR_MODERATOR" -> guild.addRoleToMember(member, discordBotProperties.getSeniorModeratorRole()).queue();
            case "MODERATOR" -> guild.addRoleToMember(member, discordBotProperties.getModeratorRole()).queue();
            case "SUPPORTER" -> guild.addRoleToMember(member, discordBotProperties.getSupporterRole()).queue();
        }

        connectionDataValue.getTeams().forEach(teamName -> {
            switch (teamName) {
                case "BUILDER" -> guild.addRoleToMember(member, discordBotProperties.getTeamBuilderRole()).queue();
                case "CONTENT" -> guild.addRoleToMember(member, discordBotProperties.getTeamContentRole()).queue();
                case "SOCIAL_MEDIA" -> guild.addRoleToMember(member, discordBotProperties.getTeamSocialMediaRole()).queue();
            }
        });

        guild.addRoleToMember(member, getHighestTimeRole(requireNonNull(member).getTimeJoined())).queue();

        sendSelfDeletingMessage(event, "Rechte synchronisiert mit Minecraft Account `" + connectionDataValue.getMinecraftName() + "` für: " + member.getAsMention());
    }
}
