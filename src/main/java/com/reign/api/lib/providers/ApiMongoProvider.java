package com.reign.api.lib.providers;

import com.reign.api.kat.models.ApiModel;
import com.reign.api.kat.responses.ApiResponse;

public class ApiMongoProvider implements IApiProvider
{
    @Override
    public <T extends ApiModel, Y extends ApiResponse<?>> boolean commit(String endpoint, T apiModel, Class<Y> responseClass)
    {
        return false;
    }

    @Override
    public <Y extends ApiResponse<?>> Y fetch(String endpoint, Class<Y> responseClass)
    {
        return null;
    }
}
