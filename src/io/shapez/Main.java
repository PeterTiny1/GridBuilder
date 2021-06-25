package io.shapez;

import io.shapez.core.Resources;

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
    JScrollPane scrollPane;

    public Main() {
        initUI();
    }

    private void initUI() {
        final MainMenu menu = new MainMenu(this);
        scrollPane = new JScrollPane(menu);

        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        add(scrollPane);
        addWindowFocusListener(this);
        addWindowStateListener(this);
        setIconImage(Resources.logo.getImage());
        setTitle(gameName + getRandomTitlebar());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final int b_HEIGHT = 500;
        final int b_WIDTH = 500;
        setPreferredSize(new Dimension(b_WIDTH, b_HEIGHT));
        pack();

    }

    public static void main(final String[] args) {
        EventQueue.invokeLater(() -> {
            final Main main;
            main = new Main();
            main.setVisible(true);
        });
    }

    @Override
    public void windowGainedFocus(final WindowEvent e) {
        if (application != null) {
            application.pressedKeys.clear();
        }
    }

    @Override
    public void windowLostFocus(final WindowEvent e) {
        if (application != null) {
            application.pressedKeys.clear();
        }
    }

    @Override
    public void windowStateChanged(final WindowEvent e) {
        //if ((e.getOldState() & Frame.ICONIFIED) == 0 && (e.getNewState() & Frame.ICONIFIED) != 0) {
        //    application.visible = false;
        //} else if ((e.getOldState() & Frame.ICONIFIED) != 0 && (e.getNewState() & Frame.ICONIFIED) == 0) {
        //    application.visible = true;
        //}
    }

    void initApplication() {
        try {
            application = new Application(this);
            scrollPane.setViewportView(application);
            application.requestFocusInWindow();
        } catch (final IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
