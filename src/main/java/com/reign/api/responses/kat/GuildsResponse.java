package com.reign.api.responses.kat;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public record GuildsResponse(ArrayList<Long> ids) {
    public GuildsResponse(
            @JsonProperty("data") ArrayList<Long> ids
    )
    {
        this.ids = ids;
    }
}
