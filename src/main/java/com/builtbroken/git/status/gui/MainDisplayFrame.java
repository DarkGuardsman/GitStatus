package com.builtbroken.git.status.gui;

import com.builtbroken.git.status.Main;
import com.builtbroken.git.status.gui.file.SearchFolderPanel;
import com.builtbroken.git.status.gui.repo.RepoListPanel;
import com.builtbroken.git.status.gui.settings.SettingsPanel;
import com.builtbroken.git.status.logic.Core;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
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

    RepoListPanel repoListPanel;
    SearchFolderPanel searchFolderPanel;
    SettingsPanel settingsPanel;

    JMenuBar menuBar;

    public MainDisplayFrame(Core core)
    {
        this.core = core;
        setMinimumSize(new Dimension(800, 600));
        setSize(new Dimension(1000, 800));
        setResizable(false);
        setTitle(Main.APPLICATION_NAME);

        setLayout(new BorderLayout());
        buildMenu();
        buildCenterPanel();
        pack();
    }

    public void openAboutDialog()
    {
        //TODO implement
    }

    public void openSearchFolderSettings()
    {
        hideCenterPanels();
        searchFolderPanel.reloadDisplayList();
        searchFolderPanel.setVisible(true);
    }

    public void openSettings()
    {
        hideCenterPanels();
        settingsPanel.setVisible(true);
    }

    public void openRepoList()
    {
        hideCenterPanels();
        repoListPanel.setVisible(true);
    }

    protected void hideCenterPanels()
    {
        repoListPanel.setVisible(false);
        searchFolderPanel.setVisible(false);
        settingsPanel.setVisible(false);
    }

    protected void buildMenu()
    {
        menuBar = new JMenuBar();

        //File menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        fileMenu.getAccessibleContext().setAccessibleDescription("Menu containing file options and settings.");

        JMenuItem scanFoldersMenuItem = new JMenuItem("Scan Folders", KeyEvent.VK_S);
        scanFoldersMenuItem.addActionListener(e -> repoListPanel.loadRepos(e));
        scanFoldersMenuItem.getAccessibleContext().setAccessibleDescription("Runs a scan on folders to look for repositories.");
        fileMenu.add(scanFoldersMenuItem);

        JMenuItem scanRepositoriesMenuItem = new JMenuItem("Check Repositories", KeyEvent.VK_S);
        scanRepositoriesMenuItem.addActionListener(e -> repoListPanel.updateRepos(e));
        scanRepositoriesMenuItem.getAccessibleContext().setAccessibleDescription("Reloads repositories making sure they still exist and checking for status changes.");
        fileMenu.add(scanRepositoriesMenuItem);

        fileMenu.addSeparator();

        JMenuItem settingsMenuItem = new JMenuItem("Settings", KeyEvent.VK_S);
        settingsMenuItem.addActionListener(e -> openSettings());
        settingsMenuItem.getAccessibleContext().setAccessibleDescription("Opens program settings.");
        fileMenu.add(settingsMenuItem);

        fileMenu.addSeparator();

        JMenuItem exitMenuItem = new JMenuItem("Exit", KeyEvent.VK_X);
        exitMenuItem.addActionListener(e -> close());
        exitMenuItem.getAccessibleContext().setAccessibleDescription("Closes the program");
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        //Edit menu
        JMenu editMenu = new JMenu("Edit");
        editMenu.setMnemonic(KeyEvent.VK_E);
        editMenu.getAccessibleContext().setAccessibleDescription("Menu containing options to edit the program.");

        JMenuItem editSearchLocationMenuItem = new JMenuItem("Search Folders", KeyEvent.VK_S);
        editSearchLocationMenuItem.addActionListener(e -> openSearchFolderSettings());
        editSearchLocationMenuItem.getAccessibleContext().setAccessibleDescription("Opens dialog to add, edit, or remove folders locations to search for repositories.");
        editMenu.add(editSearchLocationMenuItem);

        menuBar.add(editMenu);

        //Help menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        helpMenu.getAccessibleContext().setAccessibleDescription("Menu containing options to access information about the program.");
        menuBar.add(helpMenu);

        JMenuItem aboutMenuItem = new JMenuItem("About", KeyEvent.VK_A);
        aboutMenuItem.addActionListener(e -> openAboutDialog());
        aboutMenuItem.getAccessibleContext().setAccessibleDescription("Opens dialog containing information about the program");
        helpMenu.add(aboutMenuItem);

        setJMenuBar(menuBar);
    }

    protected void buildCenterPanel()
    {
        JPanel panel = new JPanel();
        panel.setLayout(new CardLayout());

        panel.add(repoListPanel = new RepoListPanel(this));
        panel.add(searchFolderPanel = new SearchFolderPanel(this));
        panel.add(settingsPanel = new SettingsPanel(this));

        searchFolderPanel.setVisible(false);
        settingsPanel.setVisible(false);

        add(panel, BorderLayout.CENTER);
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
