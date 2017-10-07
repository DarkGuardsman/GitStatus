package com.builtbroken.git.status.save;

import com.builtbroken.git.status.Main;
import com.builtbroken.git.status.gui.MainDisplayFrame;
import com.builtbroken.git.status.helpers.FileHelper;
import com.builtbroken.git.status.logic.Core;
import com.builtbroken.jlib.lang.StringHelpers;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/5/2017.
 */
public class SaveHandler
{
    public static final String REPOSITORY_PATH = "repos";
    public static final String SAVE_PATH = "settings.json";

    public boolean hasUnsavedChanged = false;
    public boolean hasLoaded = false;

    File saveFile;

    public final Core core;

    public SaveHandler(File saveFile, Core core)
    {
        this.saveFile = saveFile;
        this.core = core;
    }

    public void load()
    {
        //Debug
        Main.log("Loading settings from: " + saveFile);
        long time = System.nanoTime();

        //Load if file exists
        if (saveFile.isDirectory() && saveFile.exists())
        {
            loadSettings();
            loadRepoSettings();
        }

        //Debug
        time = System.nanoTime() - time;
        Main.log("Done..." + StringHelpers.formatNanoTime(time));

        hasLoaded = true;
    }

    protected void loadSettings()
    {
        File file = new File(saveFile, SAVE_PATH);
        if (file.exists() && file.isFile())
        {
            try
            {
                JsonElement element = JsonLoader.loadJsonFile(file);
                if (element != null && element.isJsonObject())
                {
                    JsonObject saveData = element.getAsJsonObject();
                    if (saveData.has("repoFolders"))
                    {
                        loadRepoLocations(saveData.get("repoFolders").getAsJsonArray());
                    }
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    protected void loadRepoLocations(JsonArray array)
    {
        core.foldersToSearch.clear();
        for (JsonElement element : array)
        {
            if (element.isJsonPrimitive())
            {
                String file = element.getAsString();
                core.foldersToSearch.add(file);
            }
        }
    }

    protected void loadRepoSettings()
    {
        File file = new File(saveFile, REPOSITORY_PATH);
        if (file.exists())
        {

        }
    }

    public void save()
    {
        //Debug
        Main.log("Saving settings from: " + saveFile);
        long time = System.nanoTime();

        //Only save if the output folder is a directory
        if (saveFile.isDirectory())
        {
            if (!saveFile.exists())
            {
                saveFile.mkdirs();
            }
            saveSettings();
            saveRepoSettings();
        }
        else
        {
            JOptionPane.showConfirmDialog(MainDisplayFrame.INSTANCE, "Save location is not a valid directory!\nFile: " + saveFile, "Invalid output folder", JOptionPane.ERROR_MESSAGE, JOptionPane.OK_OPTION);
        }

        //Debug
        time = System.nanoTime() - time;
        Main.log("Done..." + StringHelpers.formatNanoTime(time));

        hasUnsavedChanged = false;
    }

    protected void saveSettings()
    {
        File file = new File(saveFile, SAVE_PATH);

        JsonObject object = new JsonObject();

        //Save folders to scan
        JsonArray array = new JsonArray();
        saveRepoLocations(array);
        object.add("repoFolders", array);

        FileHelper.writeJson(file, object);
    }

    protected void saveRepoLocations(JsonArray array)
    {
        for (String file : core.foldersToSearch)
        {
            array.add(new JsonPrimitive(file));
        }
    }

    protected void saveRepoSettings()
    {
        File file = new File(saveFile, REPOSITORY_PATH);
        if (!file.exists())
        {
            file.mkdirs();
        }
        if (!file.isDirectory())
        {
            JOptionPane.showConfirmDialog(MainDisplayFrame.INSTANCE, "Repos setting save location is not a valid directory!\nFile: " + saveFile, "Invalid output folder", JOptionPane.ERROR_MESSAGE, JOptionPane.OK_OPTION);
        }
        else
        {

        }
    }
}
