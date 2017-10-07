package com.builtbroken.git.status.gui.repo;

import com.builtbroken.git.status.obj.Repo;

import javax.swing.*;
import java.awt.*;

public class RepoCellRenderer extends DefaultListCellRenderer
{
    @Override
    public Component getListCellRendererComponent(JList list, Object o, int index, boolean isSelected, boolean cellHasFocus)
    {
        Repo data = (Repo) o;
        String text = "<html>";
        text += "<h3>" + data.displayName + "</h3>";
        text += "<p>File: " + data.file + "</p>";
        text += "<p>Changes: " + data.changeCount + "</p>"; //TODO expand changes to add, remove, and modified
        text += "</html>";
        return super.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
    }
}