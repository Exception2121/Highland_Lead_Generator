package com.highland.leads;

import com.highland.leads.services.WebScanner;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
/*
 * Created by JFormDesigner on Sat Aug 08 10:21:06 EDT 2020
 */



/**
 * @author William Phillips
 */
public class GUI extends JDialog {
    public static long startTime;
    public static long endTime;

    public GUI(Window owner) {
        super(owner);
        initComponents();
    }

    public static void run() throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        startTime = System.nanoTime();
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        new GUI(null);
    }

    private void textField3KeyPressed(KeyEvent e) {
        //com.higland.leads.WebScanner.startDate = ((JTextField)e.getComponent()).getText();
    }

    private void textField4KeyPressed(KeyEvent e) {
        //com.higland.leads.WebScanner.endDate = ((JTextField)e.getComponent()).getText();
    }

    private void textField1KeyPressed(KeyEvent e) {
        //com.higland.leads.WebScanner.username = ((JTextField)e.getComponent()).getText();
    }

    private void textField2KeyPressed(KeyEvent e) {
        //com.higland.leads.WebScanner.password = ((JTextField)e.getComponent()).getText();
    }

    private void button1MousePressed(MouseEvent e) {
        if(!textField1.getText().equals("") && !textField2.getText().equals("") && !textField3.getText().equals("") && !textField4.getText().equals("")) {
            WebScanner.startDate = textField3.getText();
            WebScanner.endDate = textField4.getText();
            WebScanner.username = textField1.getText();
            WebScanner.password = textField2.getText();

            WebScanner w = new WebScanner();
            Thread scanThread = new Thread(w);
            scanThread.start();
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        panel1 = new JPanel();
        textField1 = new JTextField();
        textField2 = new JTextField();
        label1 = new JLabel();
        label2 = new JLabel();
        textField3 = new JTextField();
        label3 = new JLabel();
        label4 = new JLabel();
        textField4 = new JTextField();
        button1 = new JButton();
        label5 = new JLabel();
        progressBar1 = new JProgressBar();
        label6 = new JLabel();

        //======== this ========
        setTitle("Highland Shutter & Shade com.higland.leads.Lead Generation Tool");
        var contentPane = getContentPane();
        this.setVisible(true);

        //======== panel1 ========
        {
            panel1.setBackground(Color.white);

            //---- textField1 ----
            textField1.setText("danfowens2021");
            textField1.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    textField1KeyPressed(e);
                }
            });

            //---- textField2 ----
            textField2.setText("Sellmoreshutters2021#");
            textField2.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    textField2KeyPressed(e);
                }
            });

            //---- label1 ----
            label1.setText("Username");
            label1.setForeground(new Color(51, 51, 51));
            label1.setFont(label1.getFont().deriveFont(label1.getFont().getSize() + 5f));

            //---- label2 ----
            label2.setText("Password");
            label2.setForeground(new Color(51, 51, 51));
            label2.setFont(label2.getFont().deriveFont(label2.getFont().getSize() + 5f));

            //---- textField3 ----
            textField3.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    textField3KeyPressed(e);
                }
            });

            //---- label3 ----
            label3.setText("Date From");
            label3.setForeground(new Color(51, 51, 51));
            label3.setFont(label3.getFont().deriveFont(label3.getFont().getSize() + 5f));

            //---- label4 ----
            label4.setText("Date To");
            label4.setForeground(new Color(51, 51, 51));
            label4.setFont(label4.getFont().deriveFont(label4.getFont().getSize() + 5f));

            //---- textField4 ----
            textField4.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    textField4KeyPressed(e);
                }
            });

            //---- button1 ----
            button1.setText("Start");
            button1.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    button1MousePressed(e);
                }
            });

            //---- label5 ----
            label5.setText("Highland Shutter & Shade");
            label5.setForeground(new Color(51, 51, 51));
            label5.setFont(label5.getFont().deriveFont(label5.getFont().getSize() + 10f));

            //---- label6 ----
            label6.setText("ex: 06212020");
            label6.setForeground(new Color(51, 51, 51));
            label6.setFont(label6.getFont().deriveFont(label6.getFont().getSize() + 2f));

            GroupLayout panel1Layout = new GroupLayout(panel1);
            panel1.setLayout(panel1Layout);
            panel1Layout.setHorizontalGroup(
                panel1Layout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                        .addGap(56, 56, 56)
                        .addGroup(panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGap(62, 62, 62)
                                .addComponent(label2))
                            .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                .addComponent(textField1, GroupLayout.Alignment.LEADING)
                                .addComponent(textField2, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 211, GroupLayout.PREFERRED_SIZE))
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGap(61, 61, 61)
                                .addComponent(label1)))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(label6))
                            .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addGroup(panel1Layout.createParallelGroup()
                                    .addGroup(panel1Layout.createSequentialGroup()
                                        .addGap(8, 8, 8)
                                        .addComponent(label3))
                                    .addComponent(textField3, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE))
                                .addGroup(panel1Layout.createParallelGroup()
                                    .addComponent(textField4, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                                    .addGroup(panel1Layout.createSequentialGroup()
                                        .addGap(15, 15, 15)
                                        .addComponent(label4)))))
                        .addGap(100, 100, 100))
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addGroup(panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGap(30, 30, 30)
                                .addComponent(progressBar1, GroupLayout.PREFERRED_SIZE, 459, GroupLayout.PREFERRED_SIZE))
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGap(175, 175, 175)
                                .addComponent(button1, GroupLayout.PREFERRED_SIZE, 148, GroupLayout.PREFERRED_SIZE))
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGap(126, 126, 126)
                                .addComponent(label5)))
                        .addContainerGap(40, Short.MAX_VALUE))
            );
            panel1Layout.setVerticalGroup(
                panel1Layout.createParallelGroup()
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addComponent(label5)
                        .addGap(41, 41, 41)
                        .addGroup(panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(label1)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(textField1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(5, 5, 5)
                                .addComponent(label2)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(textField2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(label3)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(textField3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label4)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(textField4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label6)))
                        .addGap(19, 19, 19)
                        .addComponent(button1)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 74, Short.MAX_VALUE)
                        .addComponent(progressBar1, GroupLayout.PREFERRED_SIZE, 13, GroupLayout.PREFERRED_SIZE)
                        .addGap(45, 45, 45))
            );
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addComponent(panel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE))
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addComponent(panel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JPanel panel1;
    private JTextField textField1;
    private JTextField textField2;
    private JLabel label1;
    private JLabel label2;
    private JTextField textField3;
    private JLabel label3;
    private JLabel label4;
    private JTextField textField4;
    private JButton button1;
    private JLabel label5;
    public static JProgressBar progressBar1;
    private JLabel label6;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
