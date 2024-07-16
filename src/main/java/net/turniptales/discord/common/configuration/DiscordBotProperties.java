package net.turniptales.discord.common.configuration;

import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static net.turniptales.discord.TurnipTalesDiscord.discordBot;

@Getter
@Component
public class DiscordBotProperties {

    @Value("${discord.bot.token}")
    private String token;

    @Value("${discord.guild.id}")
    private String guildId;

    @Value("${discord.guild.channels.community-text-channel}")
    private String communityTextChannel;

    @Value("${discord.guild.channels.ticket-text-channel}")
    private String ticketTextChannel;

    @Value("${discord.guild.categories.ticket-category}")
    private String ticketCategory;

    @Value("${discord.guild.roles.player-role}")
    private String playerRole;

    @Value("${discord.guild.roles.senior-moderator-role}")
    private String seniorModeratorRole;

    @Value("${discord.guild.roles.moderator-role}")
    private String moderatorRole;

    @Value("${discord.guild.roles.supporter-role}")
    private String supporterRole;

    @Value("${discord.guild.roles.team-builder-role}")
    private String teamBuilderRole;

    @Value("${discord.guild.roles.team-content-role}")
    private String teamContentRole;

    @Value("${discord.guild.roles.team-social-media-role}")
    private String teamSocialMediaRole;

    @Value("${discord.guild.roles.role-0}")
    private String role0;

    @Value("${discord.guild.roles.role-1-week}")
    private String role1Week;

    @Value("${discord.guild.roles.role-2-week}")
    private String role2Week;

    @Value("${discord.guild.roles.role-1-month}")
    private String role1Month;

    @Value("${discord.guild.roles.role-3-month}")
    private String role3Month;

    @Value("${discord.guild.roles.role-6-month}")
    private String role6Month;

    @Value("${discord.guild.roles.role-1-year}")
    private String role1Year;

    @Value("${discord.guild.roles.role-2-year}")
    private String role2Year;

    @Value("${discord.guild.roles.role-3-year}")
    private String role3Year;

    public Guild getGuild() {
        return discordBot.getGuildById(this.guildId);
    }

    public TextChannel getCommunityTextChannel() {
        return getGuild().getTextChannelById(this.communityTextChannel);
    }

    public TextChannel getTicketTextChannel() {
        return getGuild().getTextChannelById(this.ticketTextChannel);
    }

    public Category getTicketCategory() {
        return getGuild().getCategoryById(this.ticketCategory);
    }

    public Role getPlayerRole() {
        return getGuild().getRoleById(this.playerRole);
    }

    public Role getSeniorModeratorRole() {
        return getGuild().getRoleById(this.seniorModeratorRole);
    }

    public Role getModeratorRole() {
        return getGuild().getRoleById(this.moderatorRole);
    }

    public Role getSupporterRole() {
        return getGuild().getRoleById(this.supporterRole);
    }

    public Role getTeamBuilderRole() {
        return getGuild().getRoleById(this.teamBuilderRole);
    }

    public Role getTeamContentRole() {
        return getGuild().getRoleById(this.teamContentRole);
    }

    public Role getTeamSocialMediaRole() {
        return getGuild().getRoleById(this.teamSocialMediaRole);
    }

    public Role getRole0() {
        return getGuild().getRoleById(this.role0);
    }

    public Role getRole1Week() {
        return getGuild().getRoleById(this.role1Week);
    }

    public Role getRole2Week() {
        return getGuild().getRoleById(this.role2Week);
    }

    public Role getRole1Month() {
        return getGuild().getRoleById(this.role1Month);
    }

    public Role getRole3Month() {
        return getGuild().getRoleById(this.role3Month);
    }

    public Role getRole6Month() {
        return getGuild().getRoleById(this.role6Month);
    }

    public Role getRole1Year() {
        return getGuild().getRoleById(this.role1Year);
    }

    public Role getRole2Year() {
        return getGuild().getRoleById(this.role2Year);
    }

    public Role getRole3Year() {
        return getGuild().getRoleById(this.role3Year);
    }
}
