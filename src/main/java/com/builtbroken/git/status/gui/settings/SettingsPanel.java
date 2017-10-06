package com.builtbroken.git.status.gui.settings;

import com.builtbroken.git.status.gui.MainDisplayFrame;

import javax.swing.*;
import java.awt.*;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/6/2017.
 */
public class SettingsPanel extends JPanel
{
    public final MainDisplayFrame frame;

    public SettingsPanel(MainDisplayFrame frame)
    {
        this.frame = frame;
        setLayout(new BorderLayout());

        createTopMenu();
    }

    protected void createTopMenu()
    {
        //Menu bottom
        JPanel bottomMenuPanel = new JPanel();
        bottomMenuPanel.setMaximumSize(new Dimension(-1, 100));

        Button filterListButton = new Button("Back");
        filterListButton.addActionListener(e -> frame.openRepoList());
        bottomMenuPanel.add(filterListButton);


        add(bottomMenuPanel, BorderLayout.NORTH);
    }
}
