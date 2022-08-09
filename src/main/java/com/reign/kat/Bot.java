package com.reign.kat;

import com.reign.api.kat.KatApi;
import com.reign.api.tenor.TenorApi;
import com.reign.kat.commands.debug.DebugCategory;
import com.reign.kat.commands.fun.emote.EmoteCategory;
import com.reign.kat.commands.helpful.HelpfulCategory;
import com.reign.kat.commands.level.LevelCategory;
import com.reign.kat.commands.player.PlayerCategory;
import com.reign.kat.lib.Properties;
import com.reign.kat.lib.command.category.Category;
import com.reign.kat.lib.command.category.CommandHandler;

import com.reign.kat.lib.utils.Utilities;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

public class Bot extends ListenerAdapter{
    private static final Logger log = LoggerFactory.getLogger(Bot.class);
    private static final String version = getVersion();

    public static Properties properties;
    public static final CommandHandler commandHandler = new CommandHandler();

    public static JDA jda;
    public static TenorApi tenorApi;
    public static KatApi api;

    public static String getVersion()
    {
        return Utilities.readVersion();
    }

    public Bot() throws Exception
    {
        log.debug("Starting bot...");
        try{
            initialize();
        }catch(Exception e){
            log.error(e.toString());
            throw e;
        }
    }

    public void initialize() throws Exception{

        log.info("Getting bot properties");
        properties = new Properties();
       String token = properties.getToken();

       tenorApi = new TenorApi(properties.getTenorApiKey(), "kat-java-bot");

       try
       {
           jda = JDABuilder.createDefault(token)
                   .addEventListeners(this)
                   .addEventListeners(commandHandler)
                   .setEnabledIntents(
                           GatewayIntent.GUILD_MESSAGE_REACTIONS,
                           GatewayIntent.GUILD_MESSAGES,
                           GatewayIntent.GUILD_MEMBERS,
                           GatewayIntent.GUILD_VOICE_STATES,
                           GatewayIntent.GUILD_EMOJIS_AND_STICKERS
                   )
                   .setChunkingFilter(ChunkingFilter.ALL)
                   .setMemberCachePolicy(MemberCachePolicy.ALL)
                   .build();
       } catch (LoginException e)
       {
           log.error("Failed to login to the Discord API. Check if your Bot token is valid in your config file!");
           throw e;

       }
       addCategories();
    }

    public void addCategories(){
        log.info("Adding Categories");
        addCategory(new HelpfulCategory());
        addCategory(new DebugCategory());
        addCategory(new EmoteCategory());
        addCategory(new PlayerCategory());
        addCategory(new LevelCategory());
        log.info("Categories Loaded");
    }

    public void addCategory(Category cat)
    {
        commandHandler.addCategory(cat);
        jda.addEventListener(cat);
        log.info("Registered category {} ({} commands)", cat.name, cat.getCommandsDistinct().size());
    }

    public void removeCategory(String name)
    {
        Category cat = commandHandler.removeCategory(name);
        jda.removeEventListener(cat);
        log.info("Unregistered category {}", cat.name);
    }

    @Override
    public void onReady(@NotNull ReadyEvent event)
    {
        log.info("==============================");
        if (Bot.properties.isDebug()) { log.warn("! Debug Mode Enabled !"); }
        log.info("Logged in as {}", event.getJDA().getSelfUser().getName());
        log.info("I can see {} Users", event.getJDA().getUsers().size());
        log.info("I can see {}/{} Guilds", event.getGuildAvailableCount(), event.getGuildTotalCount());
        log.info("Invite me to your server: {}", event.getJDA().getInviteUrl(Permission.ADMINISTRATOR));
        log.info("==============================");
    }

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event)
    {
        Guild guild = event.getGuild();
        log.info("Joined a new guild. {} | {}", guild.getId(), guild.getName());

    }

    @Override
    public void onGuildLeave(@NotNull GuildLeaveEvent event)
    {
        Guild guild = event.getGuild();
        log.info("Joined a new guild. {} | {}", guild.getId(), guild.getName());

    }

}

