package com.reign.kat;

import com.reign.api.TenorApi;
import com.reign.kat.commands.debug.DebugCategory;
import com.reign.kat.commands.fun.emote.EmoteCategory;
import com.reign.kat.commands.player.PlayerCategory;
import com.reign.kat.lib.Properties;
import com.reign.kat.lib.command.category.Category;
import com.reign.kat.lib.command.category.CommandHandler;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

public class Bot extends ListenerAdapter{
    private static final Logger log = LoggerFactory.getLogger(Bot.class);
    private static final String version = "0.0.1-alpha";

    public static Properties properties;
    public static final CommandHandler commandHandler = new CommandHandler();

    public static JDA jda;
    public static TenorApi tenorApi;

    public static String getVersion()
    {
        return version;
    }

    public Bot() throws Exception
    {
        this.log.debug("Starting bot...");
        try{
            initialize();
        }catch(Exception e){
            this.log.error(e.toString());
            throw e;
        }
    }

    public void initialize() throws Exception{

        this.log.info("Getting bot properties");
        this.properties = new Properties();
       String token = this.properties.getToken();

       tenorApi = new TenorApi(this.properties.getTenorApiKey(), "kat-java-bot");

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
           this.log.error("Failed to login to the Discord API. Check if your Bot token is valid in your config file!");
           throw e;

       }
       addCategories();
    }

    public void addCategories(){
        log.info("Adding Categories");
        addCategory(new DebugCategory());
        addCategory(new EmoteCategory());
        addCategory(new PlayerCategory());
        log.info("Categories Loaded");
    }

    public void addCategory(Category cat)
    {
        commandHandler.addCategory(cat);
        jda.addEventListener(cat);
        log.info("Registered category {}", cat.name);
    }

    public void removeCategory(String name)
    {
        Category cat = commandHandler.removeCategory(name);
        jda.removeEventListener(cat);
        log.info("Unregistered category {}", cat.name);
    }

    @Override
    public void onReady(ReadyEvent event)
    {
        log.info("==============================");
        log.info("Logged in as {}", event.getJDA().getSelfUser().getName());
        log.info("I can see {} Users", event.getJDA().getUsers().size());
        log.info("I can see {}/{} Guilds", event.getGuildAvailableCount(), event.getGuildTotalCount());
        log.info("Invite me to your server: {}", event.getJDA().getInviteUrl(Permission.ADMINISTRATOR));
        log.info("==============================");
    }

}

