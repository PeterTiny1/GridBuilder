package io.shapez.managers;

import io.shapez.game.GlobalConfig;
import io.shapez.core.Resources;

import javax.swing.*;
import java.awt.*;
import java.io.*;

public class SettingsManager {

    public static JFrame settingsFrame;
    public static JPanel mainPanel;

    public static JCheckBox chk1;
    public static JCheckBox chk2;
    public static JCheckBox chk3;


    public static boolean allowSound;
    public static boolean drawChunkEdges;
    public static boolean noLoD;

    public static void fixPaths() {
        SystemPathManager.rootFile.mkdirs();

        //settingsFile = new File(rootFile.getPath() + "/settings.bin");
        if (!SystemPathManager.settingsFile.canWrite()) {
            System.err.println("!!! No write permissions to settings file !!!"); // fuck!!!!!! what happened?
            return;
        }

        if (!SystemPathManager.settingsFile.exists()) {
            try {
                SystemPathManager.settingsFile.createNewFile();
            } catch (IOException e) {
                System.err.println("Fixing file failed (path doesn't exist?)");
            }
        }
    }

    public static void saveSettings(boolean internal) throws IOException {
        fixPaths();
        System.out.println("Saving settings in unsafe environment");
        FileOutputStream fs = new FileOutputStream(SystemPathManager.settingsFile);
        DataOutputStream ds = new DataOutputStream(fs);

        ds.writeBoolean(allowSound);
        ds.writeBoolean(drawChunkEdges);
        ds.writeBoolean(noLoD);

        ds.flush(); // push the buffer to file, just in case something failed
        ds.close();
        if (!internal)
            SoundManager.playSound(Resources.uiSuccessSound);
    }

    public static void loadSettings(boolean internal) throws IOException {
        fixPaths();
        System.out.println("Loading settings in unsafe environment");


        FileInputStream fs = new FileInputStream(SystemPathManager.settingsFile);

        DataInputStream ds = new DataInputStream(fs);

        while (ds.available() > 0) {
            allowSound = ds.readBoolean();
            drawChunkEdges = ds.readBoolean();
            noLoD = ds.readBoolean();
        }
        if (internal) {
            chk1.setSelected(allowSound);
            chk2.setSelected(drawChunkEdges);
            chk3.setSelected(noLoD);
        }

        if (noLoD)
            GlobalConfig.zoomedScale = 1;

        ds.close();
    }


    public static void initSettingsWnd() {
        settingsFrame = new JFrame("GridBuilder - Settings");
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.LINE_AXIS));

        chk1 = new JCheckBox();
        chk1.setSelected(allowSound);
        chk1.addItemListener(e -> {
            allowSound = chk1.isSelected();
            System.out.println(e.getSource());
            try {
                System.out.println("Saving settings...");
                saveSettings(true); // load settings
            } catch (IOException ee) {
                System.err.println("Error saving settings");
                ee.printStackTrace();
            }
        });
        chk1.setText("Enable Sound");
        chk1.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        chk1.setFocusPainted(false); // this can be considered a hack such as ActiveControl = null


        chk2 = new JCheckBox();
        chk2.setSelected(drawChunkEdges);
        chk2.addItemListener(e -> {
            drawChunkEdges = chk2.isSelected();
            try {
                System.out.println("Saving settings...");
                saveSettings(true); // load settings
            } catch (IOException ee) {
                System.err.println("Error saving settings");
                ee.printStackTrace();
            }
        });
        chk2.setText("Draw Chunk Edges");
        chk2.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        chk2.setFocusPainted(false); // this can be considered a hack such as ActiveControl = null

        chk3 = new JCheckBox();
        chk3.setSelected(noLoD);
        chk3.addItemListener(e -> {
            noLoD = chk3.isSelected();
            try {
                System.out.println("Saving settings...");
                saveSettings(true); // load settings
            } catch (IOException ee) {
                System.err.println("Error saving settings");
                ee.printStackTrace();
            }
        });
        chk3.setText("No LoD");
        chk3.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        chk3.setFocusPainted(false); // this can be considered a hack such as ActiveControl = null

        mainPanel.add(chk1, BorderLayout.WEST);
        mainPanel.add(chk2, BorderLayout.WEST);
        mainPanel.add(chk3, BorderLayout.WEST);


        settingsFrame.add(mainPanel);
        settingsFrame.setResizable(false);

        settingsFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                try {
                    System.out.println("Saving settings...");
                    saveSettings(false); // window closed...
                } catch (IOException e) {
                    SoundManager.playSound(Resources.uiDenySound);
                    System.err.println("Error saving settings");
                    e.printStackTrace();
                }
            }
        });
        settingsFrame.setSize(300, 100);
        settingsFrame.setLocationRelativeTo(null);
        settingsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Free frame on close ...
        // style 1 (exit on close) also terminates process...

        settingsFrame.setVisible(true);

        try {
            loadSettings(true);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
