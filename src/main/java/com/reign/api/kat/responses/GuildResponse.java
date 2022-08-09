package com.reign.api.kat.responses;

import com.reign.api.kat.models.ApiGuild;


public class GuildResponse extends ApiResponse<ApiGuild>{
    public ApiGuild get() {
        if (data.size() > 0)
            return data.get(0);
        return null;
    }
}

