package com.reign.api.lib.providers;

import com.reign.api.kat.models.ApiModel;
import com.reign.api.kat.responses.ApiResponse;

public interface IApiProvider
{
    <T extends ApiModel, Y extends ApiResponse<?>> boolean commit(String endpoint, T apiModel, Class<Y> responseClass);
    <Y extends ApiResponse<?>> Y fetch(String endpoint, Class<Y> responseClass);
}
