package com.reign.api.kat.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.reign.api.kat.models.ApiMemberData;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MemberDataResponse extends ApiResponse<ApiMemberData>{
    public @JsonProperty("data") List<ApiMemberData> data;
    public @JsonProperty("msg") String message;
    public @JsonProperty("error") String err;
    public @JsonProperty("status") int status;

    public ApiMemberData get() {
        if (data.size() > 0)
            return data.get(0);
        return null;
    }
}
