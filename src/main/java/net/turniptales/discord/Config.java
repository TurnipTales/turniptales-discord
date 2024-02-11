package net.turniptales.discord;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import static net.turniptales.discord.TurnipTalesDiscord.TURNIPTALES_BOT;

public class Config {

    public static final Guild GUILD = TURNIPTALES_BOT.getGuildById("1008928645185810463");
    public static final Member BOT;
    // channels
    public static final TextChannel COMMUNITY_TEXT_CHANNEL = GUILD.getTextChannelById("1008929885764472952");
    public static final TextChannel SURVEY_TEXT_CHANNEL = GUILD.getTextChannelById("1206336710128959519");
    public static final TextChannel TICKET_TEXT_CHANNEL = GUILD.getTextChannelById("1009477703990267954");
    //
    public static final Category TICKET_CATEGORY = GUILD.getCategoryById("1009478372847517727");
    // player roles
    public static final Role PLAYER_ROLE = GUILD.getRoleById("1190675464549847110");
    // team roles
    public static final Role MODERATOR_ROLE = GUILD.getRoleById("1025864289346658355");
    public static final Role SUPPORTER_ROLE = GUILD.getRoleById("1009477020427747408");
    // user time roles
    public static final Role ROLE_0 = GUILD.getRoleById("1134487019058372649");
    public static final Role ROLE_1_WEEK = GUILD.getRoleById("1134487221349650462");
    public static final Role ROLE_2_WEEK = GUILD.getRoleById("1134487277205192744");
    public static final Role ROLE_1_MONTH = GUILD.getRoleById("1134487413125820477");
    public static final Role ROLE_3_MONTH = GUILD.getRoleById("1134487461452583043");
    public static final Role ROLE_6_MONTH = GUILD.getRoleById("1134487539588268094");
    public static final Role ROLE_1_YEAR = GUILD.getRoleById("1134487593585745920");
    public static final Role ROLE_2_YEAR = GUILD.getRoleById("1134487644110336010");
    public static final Role ROLE_3_YEAR = GUILD.getRoleById("1134487702864142356");

    static {
        BOT = GUILD.getMemberById("1190714643551891478");
    }
}
