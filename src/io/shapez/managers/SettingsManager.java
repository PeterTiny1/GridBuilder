package io.shapez.managers;

import io.shapez.core.Resources;
import io.shapez.Application;
import io.shapez.game.GlobalConfig;
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
        tickrateScreen = Byte.parseByte(txt1.getText());
        allowSound = chk1.isSelected();
        drawChunkEdges = chk2.isSelected();
        noLoD = chk3.isSelected();
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
            } catch (IOException e) {
                System.err.println("Fixing file failed (path doesn't exist?)");
            }
        }
    }

    public boolean contains(String str, char chr) {
        return str.indexOf(chr) != -1;
    }

    public static void validateSettings() {
        String __parsetickrate = String.valueOf(tickrateScreen);
        if (__parsetickrate.matches("[0-9]+") && __parsetickrate.length() > 2)
            tickrateScreen = clampByte(tickrateScreen, (byte) 1, Byte.MAX_VALUE);
        else
            tickrateScreen = 1;

    }

    public static void saveSettings(boolean internal) throws IOException {
        fixPaths();
        validateSettings();
        System.out.println("Saving settings in unsafe environment");
        FileOutputStream fs = new FileOutputStream(SystemPathProvider.settingsFile);
        DataOutputStream ds = new DataOutputStream(fs);

        updateValues();
        ds.writeByte(tickrateScreen);
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

        FileInputStream fs = null;
        try {
            fs = new FileInputStream(SystemPathProvider.settingsFile);
        } catch (IOException e) {
            SystemPathProvider.settingsFile.createNewFile();
            e.printStackTrace();
        }
        assert fs != null;
        DataInputStream ds = new DataInputStream(fs);

        while (ds.available() > 0) {
            tickrateScreen = ds.readByte();
            allowSound = ds.readBoolean();
            drawChunkEdges = ds.readBoolean();
            noLoD = ds.readBoolean();
        }
        if (internal) {
            txt1.setText(String.valueOf(tickrateScreen));
            chk1.setSelected(allowSound);
            chk2.setSelected(drawChunkEdges);
            chk3.setSelected(noLoD);
        }

        if (noLoD)
            GlobalConfig.zoomedScale = 1;

        ds.close();
    }


    private static void internal_OnClose() {

    }

    public static void initSettingsWnd() {
        settingsFrame = new JFrame(settingsWndName);
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.LINE_AXIS));

        txt1 = new JTextField();
        //txt1.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        txt1.setMaximumSize(defDimensionTxt);

        chk1 = new JCheckBox();
        chk1.setSelected(allowSound);
        chk1.setText("Enable Sound");
        chk1.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        chk1.setFocusPainted(false); // this can be considered a hack such as ActiveControl = null


        chk2 = new JCheckBox();
        chk2.setSelected(drawChunkEdges);
        chk2.setText("Debug View");
        chk2.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        chk2.setFocusPainted(false); // this can be considered a hack such as ActiveControl = null

        chk3 = new JCheckBox();
        chk3.setSelected(noLoD);
        chk3.setText("No LoD");
        chk3.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        chk3.setFocusPainted(false); // this can be considered a hack such as ActiveControl = null

        mainPanel.add(chk1, BorderLayout.WEST);
        mainPanel.add(chk2, BorderLayout.WEST);
        mainPanel.add(chk3, BorderLayout.WEST);
        mainPanel.add(txt1, BorderLayout.WEST);


        settingsFrame.add(mainPanel);
        settingsFrame.setResizable(false);

        settingsFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                try {
                    System.out.println("Saving settings...");
                    saveSettings(false); // window closed...
                    application.window.setVisible(true);
                } catch (IOException e) {
                    SoundManager.playSound(Resources.uiDenySound);
                    System.err.println("Error saving settings");
                    e.printStackTrace();
                }
            }
        });
        settingsFrame.setSize(defWndDimension);
        settingsFrame.setLocationRelativeTo(null);
        settingsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Free frame on close ...
        settingsFrame.setIconImage(Resources.settingsImage);

        // style 1 (exit on close) also terminates process...


    }

    public static void showSettingswnd() {
        application.window.setVisible(false);
        settingsFrame.setVisible(true);

        try {
            loadSettings(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
