/**
 * Copyright (C) 2009 Future Invent Informationsmanagement GmbH. All rights
 * reserved. <http://www.fuin.org/>
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see <http://www.gnu.org/licenses/>.
 */
package org.fuin.kickstart4j;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ResourceBundle;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.fuin.utils4swing.common.ScreenCenterPositioner;
import org.fuin.utils4swing.common.Utils4Swing;

/**
 * Panel with a short "start" message.
 */
// CHECKSTYLE:OFF Because this is generated code
public class StartPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    public StartPanel() {
        initComponents();
        setPreferredSize(new Dimension(400, 100));
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY
        // //GEN-BEGIN:initComponents
        ResourceBundle bundle = ResourceBundle.getBundle("org.fuin.kickstart4j.messages");
        labelStart = new JLabel();

        // ======== this ========
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));

        // ---- labelStart ----
        labelStart.setText(bundle.getString("StartPanel.labelStart.text"));
        labelStart.setFont(labelStart.getFont().deriveFont(labelStart.getFont().getSize() + 3f));
        add(labelStart);
        // JFormDesigner - End of component initialization
        // //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY
    // //GEN-BEGIN:variables
    private JLabel labelStart;

    // JFormDesigner - End of variables declaration //GEN-END:variables

    public static void main(String[] args) {
        Utils4Swing.initSystemLookAndFeel();
        final JFrame frame = Utils4Swing.createShowAndPosition("Information", new StartPanel(),
                false, new ScreenCenterPositioner());
        frame.setResizable(false);
    }

}
// CHECKSTYLE:ON
