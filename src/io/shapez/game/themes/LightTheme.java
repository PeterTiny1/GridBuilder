package io.shapez.game.themes;

import java.awt.*;

public class LightTheme {
    String uiStyle = "light";

    public static class Map {
        public static final Color background = new Color(0xFFFFFF);
        public static final Color grid = new Color(0xFAFAFA);
        public static final int gridLineWidth = 1;
        public static final Color selectionOverlay = new Color(74, 163, 223, 179);
        public static final Color selectionOutline = new Color(74, 163, 223, 128);
        public static final Color selectionBackground = new Color(74, 163, 223, 51);
        public static final Color chunkBorders = new Color(0.0f, 30 / 256.0f, 50 / 256.0f, 0.03f);

        public static class DirectionLock {
            public static class Regular {
                public static final Color color = new Color(74, 237, 134);
                public static final Color background = new Color(74, 237, 134, 51);
            }

            public static class Wires {
                public static final Color color = new Color(74, 237, 134);
                public static final Color background = new Color(74, 237, 134, 51);
            }
        }

        public static final Color colorBlindPickerTile = new Color(50, 50, 50, 102);

        public static class Resources {
            public static final Color shape = new Color(0xeaebec);
            public static final Color red = new Color(0xffbfc1);
            public static final Color green = new Color(0xcbffc4);
            public static final Color blue = new Color(0xbfdaff);
        }

        public static class ChunkOverview {
            public static final Color empty = new Color(0xa6afbb);
            public static final Color filled = new Color(0xc5ccd6);
            public static final Color beltColor = new Color(0x777777);
        }

        public static class Wires {
            public static final Color overlayColor = new Color(97, 161, 152, 192);
            public static final Color previewColor = new Color(97, 161, 152, 102);
            public static final Color highlightColor = new Color(72, 137, 255);
        }

        public static class ConnectedMiners {
            public static final Color overlay = new Color(40, 50, 60, 128);
            public static final Color textColor = new Color(0xffffff);
            public static final Color textColorCapped = new Color(0xef5072);
            public static final Color background = new Color(40, 50, 60, 205);
        }
    }

    public static class Items {
        public static final Color outline = new Color(0x55575a);
        public static final double outlineWidth = 0.75;
        public static final Color circleBackground = new Color(40, 50, 65, 25);
    }
}

