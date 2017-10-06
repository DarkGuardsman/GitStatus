package com.builtbroken.git.status.gui.file;

import javax.swing.*;
import java.awt.*;

public class FileCellRenderer extends DefaultListCellRenderer
{
    @Override
    public Component getListCellRendererComponent(JList list, Object o, int index, boolean isSelected, boolean cellHasFocus)
    {
        String data = (String) o;
        return super.getListCellRendererComponent(list, data, index, isSelected, cellHasFocus);
    }
}