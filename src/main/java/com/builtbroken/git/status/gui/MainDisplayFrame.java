package com.builtbroken.git.status.gui;

import com.builtbroken.git.status.Main;
import com.builtbroken.git.status.save.SaveHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.HashMap;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/5/2017.
 */
public class MainDisplayFrame extends JFrame
{
    public static final String WORKING_PATH = "bbm/getstatus";

    public static MainDisplayFrame INSTANCE;
    private static boolean running = true;


    public HashMap<String, String> launchSettings;
    public SaveHandler saveHandler;
    public File saveFolder;


    public MainDisplayFrame(HashMap<String, String> launchSettings)
    {
        this.launchSettings = launchSettings;
        setMinimumSize(new Dimension(800, 600));
        setSize(new Dimension(1000, 800));
        setResizable(false);
        setTitle(Main.APPLICATION_NAME);

        setLayout(new BorderLayout());
        add(new RepoListPanel(this), BorderLayout.CENTER);
        pack();
    }

    public static boolean isRunning()
    {
        return running;
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
    }

    public static void create(HashMap<String, String> launchSettings)
    {
        Main.log("Opening repo display");
        INSTANCE = new MainDisplayFrame(launchSettings);

        INSTANCE.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        INSTANCE.addWindowListener(new WindowAdapter()
        {

            @Override
            public void windowClosing(WindowEvent e)
            {
                close();
            }
        });

        INSTANCE.addWindowStateListener(e -> {
            if (e.getNewState() == ICONIFIED)
            {
                INSTANCE.sendToTray();
            }
        });

        //Setup
        INSTANCE.init();

        //Load save
        Main.log("Loading save");
        INSTANCE.saveHandler.load();

        //Create system tray
        TrayApp.create();

        //Show
        INSTANCE.show();
    }

    /**
     * Called to close the application
     */
    public static void close()
    {
        int confirm = JOptionPane.showOptionDialog(
                null, "Are You Sure to Close?",
                "Exit Confirmation", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, null, null);
        if (confirm == 0)
        {
            INSTANCE.saveHandler.save();
            INSTANCE.dispose();
            running = false;
            System.exit(0);
        }
    }

    /**
     * Called to restore the application to focus
     */
    public void focusApp()
    {
        setState(JFrame.NORMAL);
        INSTANCE.setVisible(true);
        INSTANCE.toFront();
        INSTANCE.requestFocus();
    }

    /**
     * Sends the application to the tray
     */
    public void sendToTray()
    {
        INSTANCE.setVisible(false);
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
}
