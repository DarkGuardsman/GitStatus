package com.builtbroken.git.status.gui;

import com.builtbroken.git.status.Main;

import java.awt.*;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/6/2017.
 */
public class TrayApp extends TrayIcon
{
    public static TrayIcon INSTANCE;

    private static Image traySyncIcon;
    private static Image trayActiveIcon;
    private static Image trayInactiveIcon;
    private static Image trayWarningIcon;

    public TrayApp(Image image)
    {
        super(image, Main.APPLICATION_NAME);


        //Settings
        setImageAutoSize(true);

        //Actions
        addActionListener(e -> {
            MainDisplayFrame.INSTANCE.focusApp();
        });

        //Create menu
        PopupMenu menu = new PopupMenu();

        //Show app
        MenuItem messageItem = new MenuItem("Show");
        messageItem.addActionListener(e -> MainDisplayFrame.INSTANCE.focusApp());
        menu.add(messageItem);

        //Close option TODO add spacer
        MenuItem closeItem = new MenuItem("Close");
        closeItem.addActionListener(e -> MainDisplayFrame.close());
        menu.add(closeItem);

        //Add menu to tray
        setPopupMenu(menu);
    }

    public static void create()
    {
        //Setup tray icon
        //http://www.java2s.com/Tutorial/Java/0240__Swing/CreateTrayIcon.htm
        if (SystemTray.isSupported())
        {
            //Load images
            traySyncIcon = Toolkit.getDefaultToolkit().getImage("assets/icons/Sync-Yellow-icon.png");

            //Create
            INSTANCE = new TrayApp(traySyncIcon);


            SystemTray tray = SystemTray.getSystemTray();
            try
            {
                tray.add(INSTANCE);
            }
            catch (AWTException e)
            {
                System.err.println("TrayIcon could not be added.");
            }
        }
    }
}
