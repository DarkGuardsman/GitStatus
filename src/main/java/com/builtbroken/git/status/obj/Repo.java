package com.builtbroken.git.status.obj;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/5/2017.
 */
public class Repo
{
    //https://git-scm.com/book/be/v2/Embedding-Git-in-your-Applications-JGit
    //https://github.com/centic9/jgit-cookbook
    //https://github.com/centic9/jgit-cookbook/blob/master/src/main/java/org/dstadler/jgit/porcelain/ListUncommittedChanges.java

    public File file;
    public String id;

    //User settings, loaded from JSON
    /** Display name of the repo */
    public String displayName;
    /** Group to display the repo inside */
    public String projectGroup;
    /** Should the repo be displayed, or ignored */
    public boolean ignore;

    //Data about repo
    public boolean hasChanges = false;
    public boolean isOpen = false;
    public boolean exists = false;

    public int changeCount = 0;

    //Internal stuff
    private Repository repository;
    private Git git;


    public Repo(File file, String id)
    {
        this.id = id;
        this.file = file;
        if (displayName == null)
        {
            if (id != null)
            {
                displayName = id;
            }
            else if (file != null)
            {
                displayName = file.getParentFile().getName();
            }
        }
        if (id == null && file != null)
        {
            this.id = file.getParentFile().getName(); //not final id, is adjust to avoid overlap
        }
    }

    public void open() throws IOException
    {
        if (!isOpen)
        {
            isOpen = true;
            FileRepositoryBuilder builder = new FileRepositoryBuilder();
            repository = builder.setGitDir(file)
                    .readEnvironment() // scan environment GIT_* variables
                    .findGitDir() // scan up the file system tree
                    .build();

            git = new Git(repository);
        }

        if (displayName == null)
        {
            displayName = file.getParentFile().getName();
        }
    }

    public void close()
    {
        if (isOpen)
        {
            isOpen = false;
            git.close();
            repository.close();
        }
    }

    public void checkStatus() throws GitAPIException
    {
        Status status = git.status().call();
        hasChanges = status.hasUncommittedChanges();
        changeCount = status.getUncommittedChanges().size();
    }

    public boolean hasChanges()
    {
        return hasChanges;
    }

    @Override
    public boolean equals(Object object)
    {
        if (object instanceof Repo)
        {
            return ((Repo) object).file.equals(((Repo) object).file);
        }
        return false;
    }

    @Override
    public String toString()
    {
        return "Repo[" + file.getPath() + "]@" + hashCode();
    }
}
