package com.reign.kat;

import com.reign.api.kat.KatApi;
import com.reign.api.lib.providers.ApiHttpProvider;
import com.reign.api.tenor.TenorApi;
import com.reign.kat.commands.debug.DebugCategory;
import com.reign.kat.commands.fun.emote.EmoteCategory;
import com.reign.kat.commands.helpful.HelpfulCategory;
import com.reign.kat.commands.level.LevelCategory;
import com.reign.kat.commands.voice.VoiceCategory;
import com.reign.kat.lib.Config;
import com.reign.kat.lib.command.category.Category;
import com.reign.kat.lib.command.category.CommandHandler;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;

import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Bot extends ListenerAdapter{
    private static final Logger log = LoggerFactory.getLogger(Bot.class);

    public static final CommandHandler commandHandler = new CommandHandler();

    public static JDA jda;
    public static TenorApi tenorApi;
    public static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

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

    public void initialize()
    {
       log.info("Loading Config...");


       if (!Config.load())
       {
           log.error("Failed to load config. Bot is in an undefined state!");
       }
        init_apis();

        jda = JDABuilder.createDefault(Config.BOT_TOKEN)
                .addEventListeners(this)
                .addEventListeners(commandHandler)
                .setEnabledIntents(
                        GatewayIntent.GUILD_MESSAGE_REACTIONS,
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_VOICE_STATES,
                        GatewayIntent.GUILD_EMOJIS_AND_STICKERS,
                        GatewayIntent.SCHEDULED_EVENTS,
                        GatewayIntent.MESSAGE_CONTENT
                )
                .setChunkingFilter(ChunkingFilter.ALL)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .build();

        // Hide token
       Config.BOT_TOKEN = Config.BOT_TOKEN.split("\\.")[0] + "************";

       addCategories();
       postInit();
    }

    private void init_apis()
    {
        tenorApi = new TenorApi(Config.TENOR_API_KEY, "kat-java-bot");


        switch (Config.API_PROVIDER)
        {
            case "mongodb" -> {
//                KatApi.setHost(Config.API_MONGO_URI);
//                KatApi.setProvider(new ApiMongoProvider(Config.API_MONGO_DB));
                  log.error("UNSUPPORTED API PROVIDER");
            }
            case "http" -> {
                KatApi.setAuthorization(Config.BACKEND_API_HOST, Config.BACKEND_API_KEY);
                KatApi.setProvider(new ApiHttpProvider());
            }
            default ->
            {
                log.warn("Config does not have API_PROVIDER property, defaulting to http...");
                KatApi.setAuthorization(Config.BACKEND_API_HOST, Config.BACKEND_API_KEY);
                KatApi.setProvider(new ApiHttpProvider());
            }
        }
    }

    public void postInit()
    {
        ScheduledFuture<?> firstTimeOnHour = executorService.scheduleAtFixedRate(
                this::onHourEvent,
                10,
                60,
                TimeUnit.MINUTES
        );

        if (BotRunner.BotProperties.exitAfterLaunch)
            Bot.jda.shutdown();

        if (BotRunner.BotProperties.updateSlashCommands)
            commandHandler.updateSlashCommands();
    }

    public void addCategories(){
        log.info("Adding Categories");
        addCategory(new HelpfulCategory());
        addCategory(new DebugCategory());
        addCategory(new EmoteCategory());
        addCategory(new VoiceCategory());
        addCategory(new LevelCategory());
        log.info("Categories Loaded");
    }

    public void addCategory(Category cat)
    {
        commandHandler.addCategory(cat);
        jda.addEventListener(cat);
        log.info("Registered category {} ({} commands)", cat.internalName, cat.getCommandsDistinct().size());
    }

    public void removeCategory(String name)
    {
        Category cat = commandHandler.removeCategory(name);
        jda.removeEventListener(cat);
        log.info("Unregistered category {}", cat.internalName);
    }


    @Override
    public void onReady(@NotNull ReadyEvent event)
    {
        log.info("==============================");
        log.info("Started Kat v" + BotVersion.version());
        if (Config.DEBUG_MODE) { log.warn("! Debug Mode Enabled !"); }
        log.info("Logged in as {}", event.getJDA().getSelfUser().getName());
        log.info("I can see {} Users", event.getJDA().getUsers().size());
        log.info("I can see {}/{} Guilds", event.getGuildAvailableCount(), event.getGuildTotalCount());
        log.info("Invite me to your server: {}", event.getJDA().getInviteUrl(Permission.ADMINISTRATOR));
        log.info("==============================");

        jda.getPresence().setActivity(Activity.playing(BotVersion.version()));
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
        log.info("left a guild. {} | {}", guild.getId(), guild.getName());

    }

    private void onHourEvent()
    {
        log.info("Running hour event?");

        commandHandler.getCategories().forEach(Category::onHourEvent);
    }

}

