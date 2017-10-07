package com.builtbroken.git.status.helpers;

import com.builtbroken.git.status.obj.Repo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/5/2017.
 */
public class FileHelper
{
    public static List<String> folderNamesToIgnoreWhenSearchingForGit = new ArrayList();
    public static int maxSearchDepth = 10;

    static
    {
        //TODO add args to add more folders to list
        folderNamesToIgnoreWhenSearchingForGit.add("build");
        folderNamesToIgnoreWhenSearchingForGit.add(".git");
        folderNamesToIgnoreWhenSearchingForGit.add(".gradle");
        folderNamesToIgnoreWhenSearchingForGit.add("src");
        folderNamesToIgnoreWhenSearchingForGit.add("output");
        folderNamesToIgnoreWhenSearchingForGit.add("out");
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
            repos.add(new Repo(file, null));
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
            else if (file.isDirectory() && depth < maxSearchDepth && !folderNamesToIgnoreWhenSearchingForGit.contains(file.getName()))
            {
                findFiles(file, files, depth + 1);
            }
        }
    }

    public static void writeJson(File writeFile, JsonObject object)
    {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter file = new FileWriter(writeFile))
        {
            file.write(gson.toJson(object));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
