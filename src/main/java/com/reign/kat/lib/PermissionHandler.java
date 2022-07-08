package com.reign.kat.lib;

import com.reign.kat.Bot;
import com.reign.kat.lib.utils.PermissionGroupType;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class PermissionHandler {
    private static final Logger log = LoggerFactory.getLogger(PermissionHandler.class);
    /**
     * Ensures the member has sufficient command permissions and discord permission to execute the command
     * in the requested channel.
     * @param member    The member to check permissions for.
     * @param channel   The channel to check permission to execute in.
     * @param discordPerms  The raw discord permission requirements.
     * @param cmdPerm   The command permission group required.
     * @return  boolean if they can execute or not.
     */
    public static boolean isPrivileged(Member member, GuildChannel channel, long discordPerms, PermissionGroupType cmdPerm)
    {
        if (Bot.properties.isDebug() && Bot.properties.isIgnorePermissions())
        {
            log.warn("Permission system is disabled due to being in Developer mode.");
            log.warn("All commands will pass permission checks, set debug-ignore-permission-system to false to stop this behaviour.");
            return true;
        }



        log.info("Checking permission for {} in channel {} with permission requirements {} {}", member.getIdLong(), channel.getIdLong(), discordPerms, cmdPerm);
        if (member.getIdLong() == channel.getGuild().getOwnerIdLong()) {
            log.info("Permission check passed because member == owner");
            return true;
        } // Always return true for the owner of the guild

        // If member has role in needed permission role AND has the correct discord perms
        long rawPermission = Permission.getRaw(member.getPermissions());
        if ((rawPermission & discordPerms) == discordPerms || discordPerms == 0)
        {
            // check snowflake permission
            List<Long> snowflakes = member.getRoles().stream().map(ISnowflake::getIdLong).collect(Collectors.toList());
            snowflakes.add(member.getIdLong()); // Since the member's snowflake could specifically be set to a group

            return checkSnowflakePermissions(Bot.api.getSnowflakePermission(channel.getGuild().getIdLong()), snowflakes, cmdPerm);
        }
        log.info("Permission check failed");
        return false;
    }




    static boolean checkSnowflakePermissions(HashMap<Long, PermissionGroupType> apiSnowflakes, List<Long> snowflakesToCompare, PermissionGroupType requiredGroup) {
        if (requiredGroup == PermissionGroupType.EVERYONE) {
            log.info("Permission check passed, group level EVERYONE");
            return true;
        }


        // TODO: Improve this.
        if (requiredGroup == PermissionGroupType.ADMINISTRATOR) {
            for (long id : snowflakesToCompare) {
                switch (apiSnowflakes.getOrDefault(id, PermissionGroupType.EVERYONE)) {
                    case OWNER, ADMINISTRATOR -> {
                        log.info("Permission check passed, group level >= ADMINISTRATOR");
                        return true;
                    }
                }
            }
        }

        else if (requiredGroup == PermissionGroupType.MODERATOR) {
            for (long id : snowflakesToCompare) {
                switch (apiSnowflakes.getOrDefault(id, PermissionGroupType.EVERYONE)) {
                    case OWNER, ADMINISTRATOR, MODERATOR -> {
                        log.info("Permission check passed, group level >= MODERATOR");
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
