package com.reign.kat.lib.voice.speech;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.reign.kat.lib.voice.speech.tokens.Token;
import com.reign.kat.lib.voice.speech.tokens.TokenResult;
import com.reign.kat.lib.voice.speech.tokens.TokenType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Allows us to parse text into a series of generalized tokens which make up the structure of the sentence.
 */
public class Tokenizer
{
    private static final Logger log = LoggerFactory.getLogger(Tokenizer.class);
    private static int TABLE_VERSION = 0;

    private static final Map<String, TokenType> tokenTable = new HashMap<>();


    /**
     * Initializes the Tokenizer and creates the necessary token bindings
     * @param tableVersion tokentable format version to use.
     *                     (will throw an error if we try to read an incompatible file version)
     * @param tokenTablePath path to tokentable.json
     */
    public static void init(int tableVersion, String tokenTablePath)
    {
        TABLE_VERSION = tableVersion;

        long then = System.currentTimeMillis();
        buildTokenTable(tokenTablePath);
        log.debug("Build token table in {}ms", System.currentTimeMillis() - then);
    }


    /**
     * Attempts to convert a list of Strings into a valid series of tokens
     * @param text List of strings to convert
     * @return List of tokens created from text
     */
    public static TokenResult tokenize(String[] text)
    {
        LinkedList<Token> tokens = new LinkedList<>();
        for (String str :
                text)
        {
            // Strip punctuation and lower case the text
            str = str.replaceAll("\\p{Punct}", "").toLowerCase();
            tokens.add(new Token(tokenTable.getOrDefault(str, TokenType.NONE), str));
        }

        return new TokenResult(tokens, String.join(" ", text));
    }


    private static void buildTokenTable(String path)
    {
        try(InputStream is = new FileInputStream(path))
        {
            JsonObject json = (JsonObject) JsonParser.parseString(new String(is.readAllBytes()));
            if (json.get("version").getAsInt() != TABLE_VERSION)
            {
                log.error("Incompatible tokentable version (found '{}', expected '{}'.", json.get("version").getAsInt(), TABLE_VERSION);
                throw new RuntimeException("Incompatible version found.");
            }

            JsonArray mappings = json.getAsJsonArray("mappings");

            for (JsonElement map :
                    mappings)
            {
                String type = ((JsonObject) map).get("token").getAsString();
                ((JsonObject) map).get("text").getAsJsonArray().forEach(jsonElement -> {

                    try
                    {
                        tokenTable.put(jsonElement.getAsString(), TokenType.valueOf(type));
                    }
                    catch (IllegalArgumentException e)
                    {
                        log.error("Token Table has invalid TokenType: {}", type, e);
                    }
                });

                log.debug("Token table added {} value(s) for {} type", ((JsonObject) map).size(), type);
            }
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
