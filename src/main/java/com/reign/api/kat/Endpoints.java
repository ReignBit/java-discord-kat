package com.reign.api.kat;

public enum Endpoints {
    Hello(""),
    Guilds("guilds"),
    Guild("guilds/%s"),
    GuildData("guilds/%s/data"),
    GuildPrefix("guilds/%s/prefix"),

    AllMemberData("guilds/%s/data/members"),
    MemberData("guilds/%s/data/members/%s");

    private final String text;

    Endpoints(final String text) {
        this.text = text;
    }

    @Override
    public String toString()
    {
        return text;
    }

    /**
     * Takes a Endpoint enum and parameters, and builds an endpoint string.
     * @param endpoint Endpoints. enum
     * @param params URL parameters of the endpoint
     * @return String url
     */
    public static String buildEndpoint(Endpoints endpoint, String... params)
    {
        return String.format(endpoint.toString(), (Object[]) params);
    }
}
