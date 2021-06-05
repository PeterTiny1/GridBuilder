package io.shapez.game.themes;

import java.awt.*;

public class LightTheme {
    String uiStyle = "light";

    public static class Map {
        public static final Color background = new Color(0xFFFFFF);
        public static final Color grid = new Color(0xFAFAFA);
        public static final int gridLineWidth = 1;
        String selectionOverlay = "rgba(74, 163, 223, 0.7)";
        String selectionOutline = "rgba(74, 163, 223, 0.5)";
        String selectionBackground = "rgba(74, 163, 223, 0.2)";
        public static final Color chunkBorders = new Color(0.0f, 30 / 256.0f, 50 / 256.0f, 0.03f);

        public static class DirectionLock {
            public static class Regular {
                String color = "rgb(74, 237, 134)";
                String background = "rgba(74, 237, 134, 0.2)";
            }

            public static class Wires {
                String color = "rgb(74, 237, 134)";
                String background = "rgba(74, 237, 134, 0.2)";
            }
        }

        String colorBlindPickerTile = "rgba(50, 50, 50, 0.4)";

        public static class Resources {
            public static Color shape = new Color(0xeaebec);
            public static Color red = new Color(0xffbfc1);
            String green = "#cbffc4";
            String blue = "#bfdaff";
        }

        public static class ChunkOverview {
            String empty = "#a6afbb";
            String filled = "#c5ccd6";
            String beltColor = "#777";
        }

        public static class Wires {
            String overlayColor = "rgba(97, 161, 152, 0.75)";
            String previewColor = "rgb(97, 161, 152, 0.4)";
            String highlightColor = "rgba(72, 137, 255, 1)";
        }

        public static class ConnectedMiners {
            String overlay = "rgba(40, 50, 60, 0.5)";
            String textColor = "#fff";
            String textColorCapped = "#ef5072";
            String background = "rgba(40, 50, 60, 0.8)";
        }
    }

    public static class Items {
        String outline = "#55575a";
        double outlineWidth = 0.75;
        public static final Color circleBackground = new Color(40, 50, 65, 25);
    }
}

