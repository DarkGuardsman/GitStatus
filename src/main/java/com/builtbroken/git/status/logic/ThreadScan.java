package com.builtbroken.git.status.logic;

import com.builtbroken.git.status.gui.MainDisplayFrame;

/**
 * Handles checking repositories for updates
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/6/2017.
 */
public class ThreadScan extends Thread
{
    public final Core core;
    public long lastScanTime;

    public ThreadScan(Core core)
    {
        super("GitStatus: File System Scanner");
        this.core = core;
    }

    @Override
    public void run()
    {
        lastScanTime = System.nanoTime();
        while (MainDisplayFrame.isRunning())
        {
            long deltaTime = System.nanoTime() - lastScanTime;
            if (deltaTime >= getScanTime())
            {
                try
                {
                    doScan();
                }
                catch (Throwable t)
                {
                    t.printStackTrace();
                }
                lastScanTime = System.nanoTime();
            }
        }
    }

    protected void doScan()
    {
        core.loadRepositoryList();
        core.updateRepositoryList();
    }

    public long getScanTime()
    {
        return MainDisplayFrame.INSTANCE.core.getRepoScanTime();
    }
}
