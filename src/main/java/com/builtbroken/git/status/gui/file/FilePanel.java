package com.builtbroken.git.status.gui.file;

import javax.swing.*;
import java.awt.*;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/6/2017.
 */
public class FilePanel extends JPanel
{
    public String file;
    public SearchFolderPanel panel;

    public FilePanel(String file, SearchFolderPanel panel)
    {
        this.file = file;
        this.panel = panel;

        setLayout(new BorderLayout());
        setMaximumSize(new Dimension(panel.getWidth(), 30));

        add(new JLabel(file), BorderLayout.WEST);

        Button button = new Button("Remove");
        button.addActionListener(e -> panel.removeFile(file));
        add(button, BorderLayout.EAST);
    }
}
