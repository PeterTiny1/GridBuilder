package io.shapez;

import io.shapez.core.Resources;
import io.shapez.game.Application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowStateListener;
import java.io.IOException;

import static io.shapez.managers.providers.MiscProvider.gameName;
import static io.shapez.managers.providers.MiscProvider.getRandomTitlebar;

public class Main extends JFrame implements WindowFocusListener, WindowStateListener {
    Application application;

    public Main() throws IOException {
        initUI();
    }

    private void initUI() throws IOException {
        application = new Application(this);
        JScrollPane scrollPane = new JScrollPane(application);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        application.addMouseWheelListener(application);
        add(scrollPane);
        addWindowFocusListener(this);
        addWindowStateListener(this);
        pack();
        setIconImage(Resources.logo.getImage());
        setTitle(gameName + getRandomTitlebar());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            Main main = null;
            try {
                main = new Main();
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert main != null;
            main.setVisible(true);
        });
    }

    @Override
    public void windowGainedFocus(WindowEvent e) {
        application.pressedKeys.clear();
    }

    @Override
    public void windowLostFocus(WindowEvent e) {
        application.pressedKeys.clear();
    }

    @Override
    public void windowStateChanged(WindowEvent e) {
        if ((e.getOldState() & Frame.ICONIFIED) == 0 && (e.getNewState() & Frame.ICONIFIED) != 0) {
            application.visible = false;
        } else if ((e.getOldState() & Frame.ICONIFIED) != 0 && (e.getNewState() & Frame.ICONIFIED) == 0) {
            application.visible = true;
        }
    }
}
