package com.builtbroken.git.status.gui;

import com.builtbroken.git.status.obj.Repo;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 10/5/2017.
 */
public class RepoListPanel extends JPanel
{
    JList dataLogList;

    List<Repo> debugData = new ArrayList();

    DefaultListModel<Repo> debugDataListModel = new DefaultListModel();

    public RepoListPanel()
    {
        setLayout(new BorderLayout());

        //Create list
        dataLogList = new JList(debugDataListModel);
        dataLogList.setLayoutOrientation(JList.VERTICAL);
        dataLogList.setCellRenderer(new DebugDataCellRenderer());


        //Create scroll panel
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(dataLogList);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(getWidth() - 100, getHeight() - 100));
        scrollPane.setMinimumSize(new Dimension(getWidth() - 100, getHeight() - 100));
        add(scrollPane, BorderLayout.CENTER);

        //Menu
        JPanel menuPanel = new JPanel();
        menuPanel.setMaximumSize(new Dimension(-1, 100));
        Button button = new Button("Reload");
        button.addActionListener(e -> reloadDebugData(null));
        menuPanel.add(button);

        JTextField searchBox = new JTextField();
        searchBox.setMinimumSize(new Dimension(200, -1));
        searchBox.setPreferredSize(new Dimension(200, 30));
        searchBox.setToolTipText("Search filter");
        menuPanel.add(searchBox);

        button = new Button("Search");
        button.addActionListener(e -> reloadDebugData(searchBox.getText().trim()));
        menuPanel.add(button);

        add(menuPanel, BorderLayout.NORTH);
    }



    public void reloadDebugData(String filter)
    {
        debugDataListModel.removeAllElements();
        for (Repo data : debugData)
        {
            if (filter == null || filter.isEmpty() || data.file.getPath().contains(filter)) //TODO add regex support
            {
                debugDataListModel.addElement(data);
            }
        }
    }
}
