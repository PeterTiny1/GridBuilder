package io.shapez.managers;

import io.shapez.Application;
import io.shapez.core.Resources;
import io.shapez.game.GlobalConfig;
import io.shapez.game.platform.SoundManager;
import io.shapez.managers.providers.SystemPathProvider;

import javax.swing.*;
import java.awt.*;
import java.io.*;

import static io.shapez.managers.providers.MiscProvider.*;

public class SettingsManager {
    public static Application application;

    public static JFrame settingsFrame;
    public static JPanel mainPanel;

    public static JTextField txt1;

    public static JCheckBox chk1;
    public static JCheckBox chk2;
    public static JCheckBox chk3;

    public static byte tickrateScreen;
    public static boolean allowSound;
    public static boolean drawChunkEdges;
    public static boolean noLoD;

    public static void updateValues() {
        SettingsManager.tickrateScreen = Byte.parseByte(SettingsManager.txt1.getText());
        SettingsManager.allowSound = SettingsManager.chk1.isSelected();
        SettingsManager.drawChunkEdges = SettingsManager.chk2.isSelected();
        SettingsManager.noLoD = SettingsManager.chk3.isSelected();
    }

    public static void fixPaths() {
        SystemPathProvider.rootFile.mkdirs();

        //settingsFile = new File(rootFile.getPath() + "/settings.bin");
        if (!SystemPathProvider.settingsFile.canWrite()) {
            System.err.println("!!! No write permissions to settings file !!!"); // fuck!!!!!! what happened?
            return;
        }

        if (!SystemPathProvider.settingsFile.exists()) {
            try {
                SystemPathProvider.settingsFile.createNewFile();
            } catch (final IOException e) {
                System.err.println("Fixing file failed (path doesn't exist?)");
            }
        }
    }

    public boolean contains(final String str, final char chr) {
        return str.indexOf(chr) != -1;
    }

    public static void validateSettings() {
        final String __parsetickrate = String.valueOf(SettingsManager.tickrateScreen);
        if (__parsetickrate.matches("[0-9]+") && __parsetickrate.length() > 2)
            SettingsManager.tickrateScreen = clampByte(SettingsManager.tickrateScreen, (byte) 1, Byte.MAX_VALUE);
        else
            SettingsManager.tickrateScreen = 1;

    }

    public static void saveSettings(final boolean internal) throws IOException {
        SettingsManager.fixPaths();
        SettingsManager.validateSettings();
        System.out.println("Saving settings in unsafe environment");
        final FileOutputStream fs = new FileOutputStream(SystemPathProvider.settingsFile);
        final DataOutputStream ds = new DataOutputStream(fs);

        SettingsManager.updateValues();
        ds.writeByte(SettingsManager.tickrateScreen);
        ds.writeBoolean(SettingsManager.allowSound);
        ds.writeBoolean(SettingsManager.drawChunkEdges);
        ds.writeBoolean(SettingsManager.noLoD);

        ds.flush(); // push the buffer to file, just in case something failed
        ds.close();
        if (!internal)
            SoundManager.playSound(Resources.uiSuccessSound);
    }

    public static void loadSettings(final boolean internal) throws IOException {
        SettingsManager.fixPaths();
        System.out.println("Loading settings in unsafe environment");

        FileInputStream fs = null;
        try {
            fs = new FileInputStream(SystemPathProvider.settingsFile);
        } catch (final IOException e) {
            SystemPathProvider.settingsFile.createNewFile();
            e.printStackTrace();
        }
        assert fs != null;
        final DataInputStream ds = new DataInputStream(fs);

        while (ds.available() > 0) {
            SettingsManager.tickrateScreen = ds.readByte();
            SettingsManager.allowSound = ds.readBoolean();
            SettingsManager.drawChunkEdges = ds.readBoolean();
            SettingsManager.noLoD = ds.readBoolean();
        }
        if (internal) {
            SettingsManager.txt1.setText(String.valueOf(SettingsManager.tickrateScreen));
            SettingsManager.chk1.setSelected(SettingsManager.allowSound);
            SettingsManager.chk2.setSelected(SettingsManager.drawChunkEdges);
            SettingsManager.chk3.setSelected(SettingsManager.noLoD);
        }

        if (SettingsManager.noLoD)
            GlobalConfig.zoomedScale = 1;

        ds.close();
    }


    public static void initSettingsWnd() {
        SettingsManager.settingsFrame = new JFrame(settingsWndName);
        SettingsManager.mainPanel = new JPanel();
        SettingsManager.mainPanel.setLayout(new BoxLayout(SettingsManager.mainPanel, BoxLayout.LINE_AXIS));

        SettingsManager.txt1 = new JTextField();
        //txt1.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        SettingsManager.txt1.setMaximumSize(defDimensionTxt);

        SettingsManager.chk1 = new JCheckBox();
        SettingsManager.chk1.setSelected(SettingsManager.allowSound);
        SettingsManager.chk1.setText("Enable Sound");
        SettingsManager.chk1.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        SettingsManager.chk1.setFocusPainted(false); // this can be considered a hack such as ActiveControl = null


        SettingsManager.chk2 = new JCheckBox();
        SettingsManager.chk2.setSelected(SettingsManager.drawChunkEdges);
        SettingsManager.chk2.setText("Debug View");
        SettingsManager.chk2.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        SettingsManager.chk2.setFocusPainted(false); // this can be considered a hack such as ActiveControl = null

        SettingsManager.chk3 = new JCheckBox();
        SettingsManager.chk3.setSelected(SettingsManager.noLoD);
        SettingsManager.chk3.setText("No LoD");
        SettingsManager.chk3.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        SettingsManager.chk3.setFocusPainted(false); // this can be considered a hack such as ActiveControl = null

        SettingsManager.mainPanel.add(SettingsManager.chk1, BorderLayout.WEST);
        SettingsManager.mainPanel.add(SettingsManager.chk2, BorderLayout.WEST);
        SettingsManager.mainPanel.add(SettingsManager.chk3, BorderLayout.WEST);
        SettingsManager.mainPanel.add(SettingsManager.txt1, BorderLayout.WEST);


        SettingsManager.settingsFrame.add(SettingsManager.mainPanel);
        SettingsManager.settingsFrame.setResizable(false);

        SettingsManager.settingsFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(final java.awt.event.WindowEvent windowEvent) {
                try {
                    System.out.println("Saving settings...");
                    SettingsManager.saveSettings(false); // window closed...
                    SettingsManager.application.window.setVisible(true);
                } catch (final IOException e) {
                    SoundManager.playSound(Resources.uiDenySound);
                    System.err.println("Error saving settings");
                    e.printStackTrace();
                }
            }
        });
        SettingsManager.settingsFrame.setSize(defWndDimension);
        SettingsManager.settingsFrame.setLocationRelativeTo(null);
        SettingsManager.settingsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Free frame on close ...
        SettingsManager.settingsFrame.setIconImage(Resources.settingsImage);

        // style 1 (exit on close) also terminates process...


    }

    public static void showSettingswnd() {
        SettingsManager.application.window.setVisible(false);
        SettingsManager.settingsFrame.setVisible(true);

        try {
            SettingsManager.loadSettings(true);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

}
