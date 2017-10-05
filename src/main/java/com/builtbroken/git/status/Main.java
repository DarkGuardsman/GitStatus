package com.builtbroken.git.status;

import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
                List<File> files = new ArrayList();
                findFiles(searchFolder, files, 0);

                for (File file : files)
                {
                    File folder = file.getParentFile();
                    log("Repo: " + folder);

                    FileRepositoryBuilder builder = new FileRepositoryBuilder();
                    try (Repository repository = builder.setGitDir(folder)
                            .readEnvironment() // scan environment GIT_* variables
                            .findGitDir() // scan up the file system tree
                            .build())
                    {
                        System.out.println("Having repository: " + repository.getDirectory());

                        // the Ref holds an ObjectId for any type of object (tree, commit, blob, tree)
                        Ref head = repository.exactRef("refs/heads/master");
                        System.out.println("Ref of refs/heads/master: " + head);
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }

                //https://git-scm.com/book/be/v2/Embedding-Git-in-your-Applications-JGit
                //https://github.com/centic9/jgit-cookbook
                //https://github.com/centic9/jgit-cookbook/blob/master/src/main/java/org/dstadler/jgit/porcelain/ListUncommittedChanges.java
            }
        }
        else
        {
            //TODO load save file
            //TODO open GUI
        }
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
