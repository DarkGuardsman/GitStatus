package com.builtbroken.git.status.gui.file;

import com.builtbroken.git.status.Main;
import com.builtbroken.git.status.gui.MainDisplayFrame;
import com.builtbroken.jlib.lang.StringHelpers;

import javax.swing.*;
import java.awt.*;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/6/2017.
 */
public class SearchFolderPanel extends JPanel
{
    JList displayList;

    DefaultListModel<String> debugDataListModel = new DefaultListModel();

    MainDisplayFrame frame;

    public SearchFolderPanel(MainDisplayFrame frame)
    {
        this.frame = frame;
        setLayout(new BorderLayout());

        //Create list
        displayList = new JList(debugDataListModel);
        displayList.setLayoutOrientation(JList.VERTICAL);
        displayList.setCellRenderer(new FileCellRenderer());

        createTopMenu();
        createCenterPanel();
        createBottomMenu();
    }

    protected void createCenterPanel()
    {
        //Create scroll panel
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(displayList);
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
            reloadDisplayList(null);
        }
        else
        {
            //TODO error
        }
    }

    protected void reloadDisplayList(String filter)
    {
        //Debug
        long time = System.nanoTime();
        Main.log("Reloading search file display");

        //Clear model
        debugDataListModel.removeAllElements();

        //Populate model
        for (String data : frame.core.foldersToSearch)
        {
            //Filter by name
            if (filter == null || filter.isEmpty() || data.contains(filter)) //TODO add regex support
            {
                debugDataListModel.addElement(data);
            }
        }

        //Debug
        long time2 = System.nanoTime();
        Main.log("Done: " + StringHelpers.formatNanoTime(time2 - time));
    }
}
