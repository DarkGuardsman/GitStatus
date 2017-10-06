package com.builtbroken.git.status.gui;

import com.builtbroken.git.status.Main;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/5/2017.
 */
public class MainDisplayFrame extends JFrame
{
    public HashMap<String, String> launchSettings;

    public MainDisplayFrame(HashMap<String, String> launchSettings)
    {
        this.launchSettings = launchSettings;
        setMinimumSize(new Dimension(800, 600));
        setSize(new Dimension(1000, 800));
        setResizable(false);
        setTitle("Git Sync Tracker");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        add(new RepoListPanel(this), BorderLayout.CENTER);

        pack();
    }

    public static void create(HashMap<String, String> launchSettings)
    {
        Main.log("Opening repo display");
        MainDisplayFrame frame = new MainDisplayFrame(launchSettings);
        frame.show();
    }

    public String getRecommendedSearchFolder()
    {
        if (launchSettings.containsKey("searchFolder"))
        {
            return launchSettings.get("searchFolder");
        }
        //TODO when save system is added, get last used folder
        return null;
    }
}
