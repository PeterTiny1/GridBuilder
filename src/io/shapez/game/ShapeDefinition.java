package io.shapez.game;

import io.shapez.core.DpiManager;
import io.shapez.core.DrawParameters;
import io.shapez.core.Vector;
import io.shapez.game.savegame.BasicSerializableObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ShapeDefinition extends BasicSerializableObject {
    ArrayList<ShapeLayer> layers = new ArrayList<>();
    private Object bufferGnerator;
    private final Vector[] arrayQuadrantIndexToOffset = new Vector[]{new Vector(1, -1), new Vector(1, 1), new Vector(-1, 1), new Vector(-1, -1)};
    private String cachedHash = null;
    private final HashMap<SubShape, Character> subShapeToShortcode = new HashMap<>() {{
        put(SubShape.rect, 'R');
        put(SubShape.circle, 'C');
        put(SubShape.star, 'S');
        put(SubShape.windmill, 'W');
    }};

    @Override
    protected String getId() {
        return "ShapeDefinition";
    }

    ShapeDefinition() {
    }

    public void drawCentered(final double x, final double y, final DrawParameters parameters, final double diameter) {
        final double dpi = DpiManager.smoothenDpi(GlobalConfig.shapesSharpness * parameters.zoomLevel);

//        if (this.bufferGnerator == null) {
////            this.bufferGnerator = this.internalGenerateShapeBuffer(parameters.context, diameter, diameter, dpi);
//        }
    }

    public String getHash() {
        if (this.cachedHash != null) {
            return this.cachedHash;
        }
        final StringBuilder id = new StringBuilder();
        for (int layerIndex = 0; layerIndex < this.layers.size(); ++layerIndex) {
            final ShapeLayer layer = this.layers.get(layerIndex);

            for (int quadrant = 0; quadrant < layer.layerItems.length; ++quadrant) {
                final ShapeLayerItem item = layer.layerItems[quadrant];
                if (item != null) {
                    id.append(subShapeToShortcode.get(item.subShape) + Colors.colorToShortcode.get(item.color));
                } else {
                    id.append("--");
                }
            }

            if (layerIndex < this.layers.size() - 1) {
                id.append(":");
            }
        }
        this.cachedHash = id.toString();
        return id.toString();
    }

//    private Object internalGenerateShapeBuffer(final Graphics2D context, final double w, final double h, final double dpi) {
//        context.translate((int) ((w * dpi) / 2), (int) ((h * dpi) / 2));
//        context.scale((dpi * w) / 23, (dpi * h) / 23);
//        context.setColor(new Color(0xe9ecf7));
//        final int quadrantSize = 10;
//        final int quadrantHalfSize = quadrantSize / 2;
//
//        context.setColor(LightTheme.Items.circleBackground);
//        context.fillOval(0, 0, (int) (quadrantSize * 1.15), (int) (quadrantSize * 1.15));
//        for (int layerIndex = 0; layerIndex < this.layers.size(); ++layerIndex) {
//            final ShapeLayer quadrants = this.layers.get(layerIndex);
//
//            final double layerScale = Math.max(0.1, 0.9 - layerIndex * 0.22);
//
//            for (int quadrantIndex = 0; quadrantIndex < 4; ++quadrantIndex) {
//                if (quadrants.layerItems[quadrantIndex] == null) {
//                    continue;
//                }
//                final SubShape subShape = quadrants.layerItems[quadrantIndex].subShape;
//                final Colors color = quadrants.layerItems[quadrantIndex].color;
//                final Vector quadrantPos = arrayQuadrantIndexToOffset[quadrantIndex];
//                final double centerQuadrantX = quadrantPos.x * quadrantHalfSize;
//                final double centerQuadrantY = quadrantPos.y * quadrantHalfSize;
//                final double rotation = Math.toRadians(quadrantIndex * 90);
//                context.translate((int) centerQuadrantX, (int) centerQuadrantY);
//                context.rotate(rotation);
//
//                context.setColor(colorsToCode.get(color));
//            }
//        }
//    }

    private static class ShapeLayer {
        ShapeLayerItem[] layerItems = new ShapeLayerItem[4];
    }

    private static class ShapeLayerItem {
        SubShape subShape;
        Colors color;
    }

    private enum SubShape {
        rect,
        circle,
        star,
        windmill
    }
}
