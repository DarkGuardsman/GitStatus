package com.builtbroken.git.status.gui.repo;

import com.builtbroken.git.status.Main;
import com.builtbroken.git.status.gui.MainDisplayFrame;
import com.builtbroken.git.status.obj.Repo;
import com.builtbroken.jlib.lang.StringHelpers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/5/2017.
 */
public class RepoListPanel extends JPanel
{
    JList displayList;

    DefaultListModel<Repo> debugDataListModel = new DefaultListModel();

    MainDisplayFrame frame;

    //Components
    JCheckBox changesOnlyCheckBox;
    Button reloadDisplayButton;
    Button filterListButton;

    public RepoListPanel(MainDisplayFrame frame)
    {
        this.frame = frame;
        setLayout(new BorderLayout());

        //Create list
        displayList = new JList(debugDataListModel);
        displayList.setLayoutOrientation(JList.VERTICAL);
        displayList.setCellRenderer(new RepoCellRenderer());

        //Build parts
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

    protected void createBottomMenu()
    {
        //Menu bottom
        JPanel bottomMenuPanel = new JPanel();
        bottomMenuPanel.setMaximumSize(new Dimension(-1, 100));

        reloadDisplayButton = new Button("Reload List");
        reloadDisplayButton.addActionListener(e -> reloadDisplayList(null));
        bottomMenuPanel.add(reloadDisplayButton);

        changesOnlyCheckBox = new JCheckBox("Changes Only", true);
        bottomMenuPanel.add(changesOnlyCheckBox);

        //TODO add spacer

        JTextField searchBox = new JTextField();
        searchBox.setMinimumSize(new Dimension(200, -1));
        searchBox.setPreferredSize(new Dimension(200, 30));
        searchBox.setToolTipText("Search filter");
        bottomMenuPanel.add(searchBox);

        filterListButton = new Button("Filter List");
        filterListButton.addActionListener(e -> reloadDisplayList(searchBox.getText().trim()));
        bottomMenuPanel.add(filterListButton);


        add(bottomMenuPanel, BorderLayout.SOUTH);
    }

    /**
     * Called to clear and then load repo's from folder
     *
     * @param action
     */
    public void loadRepos(ActionEvent action)
    {
        //Debug
        Main.log("Action: Load repositories");

        //Disable buttons
        disableActions();

        //Reload repositories
        frame.core.loadRepositoryList();

        //Reload display
        reloadDisplayList(null);

        //Enable buttons
        enableActions();
    }

    public void updateRepos(ActionEvent action)
    {
        //Debug
        Main.log("Action: Update repositories");

        //Disable buttons
        disableActions();

        //Reload repositories
        frame.core.updateRepositoryList();

        //Reload display
        reloadDisplayList(null);

        //Enable buttons
        enableActions();
    }

    protected void disableActions()
    {
        reloadDisplayButton.setEnabled(false);
        filterListButton.setEnabled(false);
    }

    protected void enableActions()
    {
        reloadDisplayButton.setEnabled(true);
        filterListButton.setEnabled(true);
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
        for (Repo data : frame.core.repositories.values())
        {
            //Filter by name
            if (filter == null || filter.isEmpty() || data.file.getPath().contains(filter)) //TODO add regex support
            {
                //Filter by changes
                if (!changesOnlyCheckBox.isSelected() || data.hasChanges)
                {
                    debugDataListModel.addElement(data);
                }
            }
        }

        //Debug
        long time2 = System.nanoTime();
        Main.log("Done: " + StringHelpers.formatNanoTime(time2 - time));
    }
}
