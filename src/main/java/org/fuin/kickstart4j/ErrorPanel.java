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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 * Panel for showing an error message.
 */
// CHECKSTYLE:OFF Because this is generated code
public class ErrorPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private CloseListener closeListener = null;

    public ErrorPanel() {
        initComponents();
    }

    /**
     * Sets the message to display.
     * 
     * @param message
     *            Error message to set.
     */
    public void setMessage(final String message) {
        if (SwingUtilities.isEventDispatchThread()) {
            textAreaMessage.setText(message);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    textAreaMessage.setText(message);
                }
            });
        }
    }

    public void setCloseListener(final CloseListener listener) {
        this.closeListener = listener;
    }

    private void buttonOKActionPerformed() {
        closeListener.onClose(this);
    }

    /**
     * Listener for close button.
     */
    public static interface CloseListener {

        /**
         * The close button was pressed.
         * 
         * @param panel
         *            Panel to be closed.
         */
        public void onClose(ErrorPanel panel);

    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY
        // GEN-BEGIN:initComponents
        ResourceBundle bundle = ResourceBundle.getBundle("org.fuin.kickstart4j.messages");
        panelHeader = new JPanel();
        labelTitle = new JLabel();
        panelFooter = new JPanel();
        buttonOK = new JButton();
        scrollPaneMessage = new JScrollPane();
        textAreaMessage = new JTextArea();

        // ======== this ========
        setLayout(new BorderLayout());

        // ======== panelHeader ========
        {
            panelHeader.setLayout(new FlowLayout());

            // ---- labelTitle ----
            labelTitle.setText(bundle.getString("ErrorPanel.labelTitle.text"));
            labelTitle.setFont(labelTitle.getFont().deriveFont(
                    labelTitle.getFont().getStyle() | Font.BOLD,
                    labelTitle.getFont().getSize() + 3f));
            panelHeader.add(labelTitle);
        }
        add(panelHeader, BorderLayout.NORTH);

        // ======== panelFooter ========
        {
            panelFooter.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 10));

            // ---- buttonOK ----
            buttonOK.setText(bundle.getString("ErrorPanel.buttonOK.text"));
            buttonOK.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    buttonOKActionPerformed();
                }
            });
            panelFooter.add(buttonOK);
        }
        add(panelFooter, BorderLayout.SOUTH);

        // ======== scrollPaneMessage ========
        {

            // ---- textAreaMessage ----
            textAreaMessage.setEditable(false);
            scrollPaneMessage.setViewportView(textAreaMessage);
        }
        add(scrollPaneMessage, BorderLayout.CENTER);
        // JFormDesigner - End of component initialization
        // //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY
    // //GEN-BEGIN:variables
    private JPanel panelHeader;
    private JLabel labelTitle;
    private JPanel panelFooter;
    private JButton buttonOK;
    private JScrollPane scrollPaneMessage;
    private JTextArea textAreaMessage;
    // JFormDesigner - End of variables declaration //GEN-END:variables
}
// CHECKSTYLE:ON
