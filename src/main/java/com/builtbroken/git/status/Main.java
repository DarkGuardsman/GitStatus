package com.builtbroken.git.status;

import com.builtbroken.git.status.obj.Repo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/5/2017.
 */
public class Main
{
    public static int maxSearchDepth = 10;
    public static List<String> foldersToIgnore = new ArrayList();

    public static void main(String... args)
    {
        log("Starting");

        //Collect arguments
        final HashMap<String, String> launchSettings = loadArgs(args);

        //TODO add args to add more folders to list
        foldersToIgnore.add("build");
        foldersToIgnore.add(".git");
        foldersToIgnore.add(".gradle");
        foldersToIgnore.add("src");
        foldersToIgnore.add("output");
        foldersToIgnore.add("out");

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
                List<Repo> repos = getRepositoriesWithChanges(searchFolder);
                for (Repo repo : repos)
                {
                    log("Repo: " + repo.file + " has " + repo.changeCount + " uncommitted changes");
                }

                //https://git-scm.com/book/be/v2/Embedding-Git-in-your-Applications-JGit
                //https://github.com/centic9/jgit-cookbook
                //https://github.com/centic9/jgit-cookbook/blob/master/src/main/java/org/dstadler/jgit/porcelain/ListUncommittedChanges.java

            }
            waitForEnter("Press [ANY] key to exit!");
            System.exit(0);
        }
        else
        {
            //TODO load save file
            //TODO open GUI
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

    public static List<Repo> getRepositoriesWithChanges(File folder)
    {
        List<Repo> repos = getRepositories(folder);
        Iterator<Repo> it = repos.iterator();
        while (it.hasNext())
        {
            Repo repo = it.next();
            try
            {
                repo.open();
                repo.checkStatus();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                repo.close();
            }

            if (!repo.hasChanges())
            {
                it.remove();
            }
        }
        return repos;
    }

    public static List<Repo> getRepositories(File folder)
    {
        List<Repo> repos = new ArrayList();

        List<File> files = new ArrayList();
        findFiles(folder, files, 0);

        for (File file : files)
        {
            repos.add(new Repo(file));
        }

        return repos;
    }

    public static void findFiles(File folder, List<File> files, int depth)
    {
        for (File file : folder.listFiles())
        {
            if (file.getName().equals(".git"))
            {
                files.add(file);
                break;
            }
            else if (file.isDirectory() && depth < maxSearchDepth && !foldersToIgnore.contains(file.getName()))
            {
                findFiles(file, files, depth + 1);
            }
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
