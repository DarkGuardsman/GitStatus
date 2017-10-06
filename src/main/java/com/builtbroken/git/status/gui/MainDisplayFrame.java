package com.builtbroken.git.status.gui;

import com.builtbroken.git.status.Main;
import com.builtbroken.git.status.logic.Core;
import com.builtbroken.git.status.logic.ThreadScan;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/5/2017.
 */
public class MainDisplayFrame extends JFrame
{
    public static MainDisplayFrame INSTANCE;
    private static boolean running = true;

    public final Core core;

    public MainDisplayFrame(Core core)
    {
        this.core = core;
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

    public static void create(HashMap<String, String> launchSettings)
    {
        Core core = new Core(launchSettings);

        Main.log("Opening repo display");
        INSTANCE = new MainDisplayFrame(core);

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
        core.init();

        //Load save
        Main.log("Loading save");
        core.load();

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
            INSTANCE.core.save();
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

}
