package com.builtbroken.git.status.logic;

import com.builtbroken.git.status.Main;
import com.builtbroken.git.status.helpers.FileHelper;
import com.builtbroken.git.status.obj.Repo;
import com.builtbroken.git.status.save.SaveHandler;
import com.builtbroken.jlib.lang.StringHelpers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/6/2017.
 */
public class Core
{
    public static final String WORKING_PATH = "builtbroken/getstatus";

    public HashMap<String, String> launchSettings;
    public SaveHandler saveHandler;
    public File saveFolder;

    public final HashMap<File, Repo> repositories = new HashMap();
    public final List<String> foldersToSearch = new ArrayList();

    public ThreadScan threadScan;

    private long repoScanTime = TimeUnit.MINUTES.convert(5, TimeUnit.NANOSECONDS);

    public Core(HashMap<String, String> launchSettings)
    {
        this.launchSettings = launchSettings;
    }

    public void init()
    {
        if (launchSettings.containsKey("workingDirectory"))
        {
            saveFolder = new File(launchSettings.get("workingDirectory"));
        }
        else
        {
            String workingDirectory;
            String OS = (System.getProperty("os.name")).toUpperCase();

            if (OS.contains("WIN"))
            {
                workingDirectory = System.getenv("LOCALAPPDATA") + "/" + WORKING_PATH;
                if (workingDirectory == null)
                {
                    workingDirectory = System.getenv("AppData") + "/." + WORKING_PATH;
                }
            }
            else
            {
                workingDirectory = System.getProperty("user.home");
                workingDirectory += "/Library/Application Support/" + WORKING_PATH;
            }

            saveFolder = new File(workingDirectory);
            if (!saveFolder.exists())
            {
                saveFolder.mkdirs();
            }
            saveHandler = new SaveHandler(saveFolder);
        }

        threadScan = new ThreadScan(this);
        threadScan.start();
    }

    /**
     * Called to update the status of repositories
     */
    public void updateRepositoryList()
    {
        for (Repo repo : repositories.values())
        {
            repo.exists = repo.file.exists();
            if (repo.exists)
            {
                try
                {
                    repo.open();
                    repo.checkStatus();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    //TODO print to file
                    //TODO notify user
                }
                finally
                {
                    repo.close();
                }
            }
        }
    }

    /**
     * Called to load repositories from file or check existing
     */
    public void loadRepositoryList()
    {
        //Reset
        repositories.values().forEach(r -> r.exists = false);

        //Collect repositories
        for (String folderPath : foldersToSearch)
        {
            //Debug
            Main.log("Loading repos from file: " + folderPath);

            //Check file is valid
            File file = new File(folderPath);
            if (file.exists() && file.isDirectory())
            {
                //Debug
                long time = System.nanoTime();
                Main.log("\tScanning for repos");

                List<Repo> repoList = FileHelper.getRepositories(file);
                for (Repo repo : repoList)
                {
                    repo.exists = true;
                    if (!repositories.containsKey(repo.file))
                    {
                        repositories.put(repo.file, repo);
                    }
                    else
                    {
                        repositories.get(repo.file).exists = true;
                    }
                }

                //Debug
                long time2 = System.nanoTime();
                Main.log("\tDone: " + StringHelpers.formatNanoTime(time2 - time)); //TODO log load time average to use for progress bar
            }
            else
            {
                Main.log("Invalid directory: " + folderPath);
            }
        }
    }

    public void load()
    {
        saveHandler.load();
    }

    public void save()
    {
        saveHandler.save();
    }

    /**
     * Gets the recommended folder to scan for repos
     *
     * @return
     */
    public String getRecommendedSearchFolder()
    {
        if (launchSettings.containsKey("searchFolder"))
        {
            return launchSettings.get("searchFolder");
        }
        //TODO when save system is added, get last used folder
        return null;
    }

    /**
     * Called to force save
     *
     * @param crashed - is the save being forced due to a crash
     * @return
     */
    public void forceSave(boolean crashed)
    {
        if (crashed)
        {
            //TODO backup old save data
        }
        if (saveHandler != null && saveHandler.hasUnsavedChanged)
        {
            saveHandler.save();
        }
    }

    public long getRepoScanTime()
    {
        return repoScanTime;
    }
}
