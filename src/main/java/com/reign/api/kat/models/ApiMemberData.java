package com.reign.api.kat.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.reign.api.kat.Endpoints;
import com.reign.api.kat.responses.MemberDataResponse;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiMemberData extends ApiModel
{
    public @JsonProperty("snowflake") String snowflake;
    public @JsonProperty("guild_snowflake") String guildSnowflake;
    public @JsonProperty("level") LevelData level;

    public ApiMemberData()
    {
        super();
    }

    public ApiMemberData(String guildSnowflake, String snowflake)
    {
        this.guildSnowflake = guildSnowflake;
        this.snowflake = snowflake;
        this.level = new LevelData();
    }

    public static ApiMemberData get(String guild, String snowflake)
    {
        MemberDataResponse g = fetch(Endpoints.buildEndpoint(Endpoints.MemberData, guild, snowflake), MemberDataResponse.class);
        if (!g.data.isEmpty())
        {
            return g.get();
        }

        // doesn't exist in api, lets create it.
        ApiMemberData newMember = new ApiMemberData(guild, snowflake);
        newMember.save();
        return newMember;
    }

    @Override
    public boolean save()
    {
        return commit(Endpoints.buildEndpoint(Endpoints.MemberData, guildSnowflake, snowflake), this, MemberDataResponse.class);
    }

    public static class LevelData
    {
        public @JsonProperty("xp") int xp;

        public LevelData()
        {
            this.xp = 0;
        }

        /**
         * Add experience to the user.
         * We calculate experience based on the length of the User's message.
         *
         * @param messageContents String contents of the message.
         * @return Integer - Amount of xp added.
         */
        public int addExperience(String messageContents, float multiplier)
        {

            if (messageContents.startsWith("http"))
            {
                return xp;
            }
            int added = calculateAwardedExp(messageContents.length(), multiplier);
            xp += added;
            return added;
        }

        /**
         * Calculates the level boundary of the current xp
         *
         * @return Current level of the xp.
         */
        public int level()
        {
            double lvl = 1 + Math.sqrt(1 + 8 * (xp * xp) / 40f) / 2;
            return (int) lvl;
        }

        /**
         * Calculates the amount of awarded xp for the length of the message.
         *
         * @param length     Length of the message
         * @param multiplier float experience multiplier to multiply all awarded xp.
         * @return Int amount of awarded xp.
         */
        private int calculateAwardedExp(int length, float multiplier)
        {
            if (length <= 7)
            {
                return 0;
            }
            double xp = Math.min(Math.max((length / 0.9) * 0.3, 10), 200) * multiplier;
            return (int) Math.floor(xp);
        }
    }

}
