package com.builtbroken.git.status.gui;

import com.builtbroken.git.status.Main;
import com.builtbroken.git.status.helpers.FileHelper;
import com.builtbroken.git.status.obj.Repo;
import com.builtbroken.jlib.lang.StringHelpers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/5/2017.
 */
public class RepoListPanel extends JPanel
{
    JList displayList;

    List<Repo> repoList = new ArrayList();

    DefaultListModel<Repo> debugDataListModel = new DefaultListModel();

    MainDisplayFrame frame;

    public RepoListPanel(MainDisplayFrame frame)
    {
        this.frame = frame;
        setLayout(new BorderLayout());

        //Create list
        displayList = new JList(debugDataListModel);
        displayList.setLayoutOrientation(JList.VERTICAL);
        displayList.setCellRenderer(new DebugDataCellRenderer());


        //Create scroll panel
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(displayList);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(getWidth() - 100, getHeight() - 100));
        scrollPane.setMinimumSize(new Dimension(getWidth() - 100, getHeight() - 100));
        add(scrollPane, BorderLayout.CENTER);

        Button button;

        //Menu top
        JPanel topMenuPanel = new JPanel();
        topMenuPanel.setMaximumSize(new Dimension(-1, 100));

        topMenuPanel.add(new JLabel("Folder To Search: "));

        JTextField fileNameBox = new JTextField();
        fileNameBox.setMinimumSize(new Dimension(200, -1));
        fileNameBox.setPreferredSize(new Dimension(200, 30));
        fileNameBox.setToolTipText("path to folder");
        fileNameBox.setText(frame.getRecommendedSearchFolder());
        topMenuPanel.add(fileNameBox);

        JCheckBox changesOnly = new JCheckBox("Changes Only", true);

        button = new Button("Load");
        button.addActionListener(e -> loadRepos(e, fileNameBox.getText(), changesOnly.isSelected()));
        topMenuPanel.add(button);

        topMenuPanel.add(changesOnly);

        add(topMenuPanel, BorderLayout.NORTH);

        //Menu bottom
        JPanel bottomMenuPanel = new JPanel();
        bottomMenuPanel.setMaximumSize(new Dimension(-1, 100));

        button = new Button("Reload");
        button.addActionListener(e -> reloadDisplayList(null));
        bottomMenuPanel.add(button);

        JTextField searchBox = new JTextField();
        searchBox.setMinimumSize(new Dimension(200, -1));
        searchBox.setPreferredSize(new Dimension(200, 30));
        searchBox.setToolTipText("Search filter");
        bottomMenuPanel.add(searchBox);

        button = new Button("Search");
        button.addActionListener(e -> reloadDisplayList(searchBox.getText().trim()));
        bottomMenuPanel.add(button);


        add(bottomMenuPanel, BorderLayout.SOUTH);
    }

    /**
     * Called to clear and then load repo's from folder
     *
     * @param e
     * @param folderPath
     */
    protected void loadRepos(ActionEvent e, String folderPath, boolean changes)
    {
        //Disable button
        if (e != null && e.getSource() instanceof Button)
        {
            ((Button) e.getSource()).setEnabled(false);
        }

        //Debug
        Main.log("Loading repos from file: " + folderPath + ", changes only: " + changes);
        repoList.clear();

        //Check file is valid
        File file = new File(folderPath);
        if (!file.exists() || !file.isDirectory())
        {
            Main.log("File is not a valid directory");
            JOptionPane.showConfirmDialog(this, "File path is not a valid directory!", "Invalid file path", JOptionPane.ERROR_MESSAGE, JOptionPane.OK_OPTION);
            return;
        }

        //Debug
        long time = System.nanoTime();
        Main.log("Scanning for repos");

        //Get files
        if (changes)
        {
            repoList = FileHelper.getRepositoriesWithChanges(file);
        }
        else
        {
            repoList = FileHelper.getRepositories(file);
        }

        //Debug
        long time2 = System.nanoTime();
        Main.log("Done: " + StringHelpers.formatNanoTime(time2 - time));

        //Reload display
        reloadDisplayList(null);

        //Enable button
        if (e != null && e.getSource() instanceof Button)
        {
            ((Button) e.getSource()).setEnabled(true);
        }
    }

    /**
     * Called to reload the display
     *
     * @param filter
     */
    protected void reloadDisplayList(String filter)
    {
        //Debug
        long time = System.nanoTime();
        Main.log("Reloading repo display");

        //Clear model
        debugDataListModel.removeAllElements();

        //Populate model
        for (Repo data : repoList)
        {
            if (filter == null || filter.isEmpty() || data.file.getPath().contains(filter)) //TODO add regex support
            {
                debugDataListModel.addElement(data);
            }
        }

        //Debug
        long time2 = System.nanoTime();
        Main.log("Done: " + StringHelpers.formatNanoTime(time2 - time));
    }
}
