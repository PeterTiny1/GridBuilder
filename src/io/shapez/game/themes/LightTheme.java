package io.shapez.game.themes;

import java.awt.*;

public class LightTheme {
    String uiStyle = "light";

    public static class Map {
        public static Color background = new Color(0xFFFFFF);
        public static Color grid = new Color(0xFAFAFA);
        public static int gridLineWidth = 1;
        String selectionOverlay = "rgba(74, 163, 223, 0.7)";
        String selectionOutline = "rgba(74, 163, 223, 0.5)";
        String selectionBackground = "rgba(74, 163, 223, 0.2)";
        String chunkBorders = "rgba(0, 30, 50, 0.03)";

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
            String shape = "#eaebec";
            String red = "#ffbfc1";
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
        String circleBackground = "rgba(40, 50, 65, 0.1)";
    }
}
