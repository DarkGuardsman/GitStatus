package com.builtbroken.git.status.save;

import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;

import java.io.*;

/**
 * Handles loading Json files
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/5/2017.
 */
//Copied from VoltzEngine, make sure to share changes to improve both projects
public class JsonLoader
{
    /**
     * Loads a json file from the resource path
     *
     * @param file - file to read from
     * @return json file as a json element object
     * @throws IOException
     */
    public static JsonElement loadJsonFile(File file) throws IOException
    {
        if (file.exists() && file.isFile())
        {
            FileReader stream = new FileReader(file);
            JsonElement element = loadJson(file.getName(), new BufferedReader(stream));
            stream.close();
            return element;
        }
        return null;
    }

    /**
     * Loads a json file from a reader
     *
     * @param fileName - file the reader loaded from, used only for error logs
     * @param reader   - reader with the data
     */
    public static JsonElement loadJson(String fileName, Reader reader)
    {
        try
        {
            JsonReader jsonReader = new JsonReader(reader);
            return Streams.parse(jsonReader);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to parse file '" + fileName + "'", e);
        }
    }

    /**
     * Creates an json element from a string
     *
     * @param data - string data, needs to be formatted correctly,
     *             e.g. { "content" : { "more":"content"} }
     * @return json element
     * @throws JsonSyntaxException - if string is not formatted correctly
     */
    public static JsonElement createElement(String data)
    {
        JsonReader jsonReader = new JsonReader(new StringReader(data));
        return Streams.parse(jsonReader);
    }
}
