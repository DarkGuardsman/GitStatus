package com.builtbroken.git.status;

import com.builtbroken.git.status.gui.MainDisplayFrame;
import com.builtbroken.git.status.helpers.FileHelper;
import com.builtbroken.git.status.obj.Repo;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/5/2017.
 */
public class Main
{
    public static final String APPLICATION_NAME = "Git Status Tracker";

    public static void main(String... args)
    {
        //TODO add dependency downloader using Maven
        log("Starting");

        //Collect arguments
        final HashMap<String, String> launchSettings = loadArgs(args);

        if (launchSettings.containsKey("noGUI"))
        {
            if (!launchSettings.containsKey("searchFolder"))
            {
                //TODO collect input from user instead
                log("Failed in include '-searchFolder=some/file/location' in program arguments for search for git repos");
                System.exit(1);
            }
            String filePath = launchSettings.get("searchFolder");
            File searchFolder = new File(filePath);
            if (searchFolder.exists() && searchFolder.isDirectory())
            {
                List<Repo> repos = FileHelper.getRepositoriesWithChanges(searchFolder);
                for (Repo repo : repos)
                {
                    log("Repo: " + repo.file + " has " + repo.changeCount + " uncommitted changes");
                }
            }
            waitForEnter("Press [ANY] key to exit!");
            System.exit(0);
        }
        else
        {
            try
            {
                //Create and open GUI
                MainDisplayFrame.create(launchSettings);

                //Wait on app to die
                while (MainDisplayFrame.isRunning())
                {
                    Thread.sleep(100);
                }

                //Kill app, with normal exit
                System.exit(0);
            }
            catch (Throwable t)
            {
                //Log error
                t.printStackTrace(); //TODO save to file

                //Save data to prevent loss of work
                if (MainDisplayFrame.INSTANCE != null)
                {
                    MainDisplayFrame.INSTANCE.forceSave(true);
                }

                //TODO make custom error pop up with pastebin.com button
                JOptionPane.showMessageDialog(MainDisplayFrame.INSTANCE, "Unexpected error while running application!"

                                + "\nError: " + t.toString(),
                        "Crash",
                        JOptionPane.ERROR_MESSAGE);

                //Close with unexpected error
                System.exit(-1);
            }
        }
    }

    public static void waitForEnter(String message)
    {
        log(message);
        try
        {
            System.in.read(); //TODO rewrite
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void log(String msg)
    {
        System.out.println("[GitStatus] " + msg);
    }

    /**
     * Converts arguments into a hashmap for usage
     *
     * @param args
     * @return
     */
    public static HashMap<String, String> loadArgs(String... args)
    {
        final HashMap<String, String> map = new HashMap();
        if (args != null)
        {
            String currentArg = null;
            String currentValue = "";
            for (int i = 0; i < args.length; i++)
            {
                String next = args[i].trim();
                if (next == null)
                {
                    throw new IllegalArgumentException("Null argument detected in launch arguments");
                }
                else if (next.startsWith("-"))
                {
                    if (currentArg != null)
                    {
                        map.put(currentArg, currentValue);
                        currentValue = "";
                    }

                    if (next.contains("="))
                    {
                        String[] split = next.split("=");
                        currentArg = split[0].substring(1).trim();
                        currentValue = split[1].trim();
                        if (split.length > 2)
                        {
                            for (int l = 2; l < split.length; l++)
                            {
                                currentValue += "=" + split[l];
                            }
                        }
                    }
                    else
                    {
                        currentArg = next.substring(1).trim();
                    }
                }
                else if (currentArg != null)
                {
                    if (!currentValue.isEmpty())
                    {
                        currentValue += ",";
                    }
                    currentValue += next.replace("\"", "").replace("'", "").trim();
                }
                else
                {
                    throw new IllegalArgumentException("Value has no argument associated with it [" + next + "]");
                }
            }
            //Add the last loaded value to the map
            if (currentArg != null)
            {
                map.put(currentArg, currentValue);
            }
        }
        return map;
    }
}
