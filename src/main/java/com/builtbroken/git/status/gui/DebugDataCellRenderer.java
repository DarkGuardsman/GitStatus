package com.builtbroken.git.status.gui;

import com.builtbroken.git.status.obj.Repo;

import javax.swing.*;
import java.awt.*;

public class DebugDataCellRenderer extends DefaultListCellRenderer
{
    @Override
    public Component getListCellRendererComponent(JList list, Object o, int index, boolean isSelected, boolean cellHasFocus)
    {
        Repo data = (Repo) o;
        return super.getListCellRendererComponent(list, data.file, index, isSelected, cellHasFocus);
    }
}