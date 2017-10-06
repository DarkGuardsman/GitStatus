package com.builtbroken.git.status.gui.file;

import com.builtbroken.git.status.gui.MainDisplayFrame;

import javax.swing.*;
import java.awt.*;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/6/2017.
 */
public class SearchFolderPanel extends JPanel
{
    MainDisplayFrame frame;
    JPanel fileListPanel;

    public SearchFolderPanel(MainDisplayFrame frame)
    {
        this.frame = frame;
        setLayout(new BorderLayout());

        createTopMenu();
        createCenterPanel();
        createBottomMenu();

        reloadDisplayList();
    }

    protected void createCenterPanel()
    {
        //Create scroll panel
        JScrollPane scrollPane = new JScrollPane();

        fileListPanel = new JPanel();
        fileListPanel.setLayout(new BoxLayout(fileListPanel, BoxLayout.Y_AXIS));
        scrollPane.setViewportView(fileListPanel);

        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(getWidth() - 100, getHeight() - 100));
        scrollPane.setMinimumSize(new Dimension(getWidth() - 100, getHeight() - 100));
        add(scrollPane, BorderLayout.CENTER);
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

    protected void createBottomMenu()
    {
        //Menu bottom
        JPanel bottomMenuPanel = new JPanel();
        bottomMenuPanel.setMaximumSize(new Dimension(-1, 100));

        //
        JTextField filePathBox = new JTextField();
        filePathBox.setMinimumSize(new Dimension(200, -1));
        filePathBox.setPreferredSize(new Dimension(200, 30));
        filePathBox.setToolTipText("File path");

        Button findButton = new Button("Find");
        findButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            int returnVal = chooser.showOpenDialog(SearchFolderPanel.this);
            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                filePathBox.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });
        bottomMenuPanel.add(findButton);

        //Add text box after find button
        bottomMenuPanel.add(filePathBox);

        Button filterListButton = new Button("Add");
        filterListButton.addActionListener(e -> addFile(filePathBox.getText().trim()));
        bottomMenuPanel.add(filterListButton);


        add(bottomMenuPanel, BorderLayout.SOUTH);
    }

    protected void addFile(String file)
    {
        if (!frame.core.foldersToSearch.contains(file))
        {
            frame.core.foldersToSearch.add(file); //TODO check for parent
            reloadDisplayList();
        }
        else
        {
            //TODO error
        }
    }

    public void removeFile(String text)
    {
        if (frame.core.foldersToSearch.contains(text))
        {
            frame.core.foldersToSearch.remove(text);
            reloadDisplayList();
        }
    }

    protected void reloadDisplayList()
    {
        fileListPanel.removeAll();
        for (String file : frame.core.foldersToSearch)
        {
            fileListPanel.add(new FilePanel(file, this));
        }
        fileListPanel.validate();
        fileListPanel.repaint();
    }
}
