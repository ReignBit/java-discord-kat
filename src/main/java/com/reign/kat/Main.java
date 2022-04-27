package com.reign.kat;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import java.awt.event.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {
        // Insert your bot's token here
        String token = "";

        DiscordApi api = new DiscordApiBuilder().setToken(token).login().join();

        // Add a listener which answers with "Pong!" if someone writes "!ping"
        api.addMessageCreateListener(event -> {
            if (event.getMessageContent().equalsIgnoreCase("!ping")) {
                event.getChannel().sendMessage("Pong!");
            }
        });

        // Print the invite url of your bot
        System.out.println("You can invite the bot by using the following url: " + api.createBotInvite());
    }

}