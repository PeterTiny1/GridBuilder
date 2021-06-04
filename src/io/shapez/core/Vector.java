package io.shapez.core;

import io.shapez.game.GlobalConfig;

import java.awt.*;

public class Vector {
    static final int tileSize = GlobalConfig.tileSize;
    static final int halfTileSize = GlobalConfig.halfTileSize;
    public double x;
    public double y;
    double xy;

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
        this.xy = x * x + y * y;
    }

    public Vector() {
        this.x = 0;
        this.y = 0;
        this.xy = 0;
    }

    public Vector(Point mousePosition) {
        this.x = mousePosition.x;
        this.y = mousePosition.y;
        this.xy = mousePosition.x * mousePosition.x + mousePosition.y + mousePosition.y;
    }

    public static Direction invertDirection(Direction direction) {
        return switch (direction) {
            case Top -> Direction.Bottom;
            case Right -> Direction.Left;
            case Left -> Direction.Right;
            case Bottom -> Direction.Top;
        };
    }

    public static Direction transformDirectionFromMultipleOf90(Direction direction, int angle) {
        if (angle == 0 || angle == 360) {
            return direction;
        }
        return switch (direction) {
            case Top -> switch (angle) {
                case 90 -> Direction.Right;
                case 180 -> Direction.Bottom;
                case 270 -> Direction.Left;
                default -> direction;
            };
            case Right -> switch (angle) {
                case 90 -> Direction.Bottom;
                case 180 -> Direction.Left;
                case 270 -> Direction.Top;
                default -> direction;
            };
            case Bottom -> switch (angle) {
                case 90 -> Direction.Left;
                case 180 -> Direction.Right;
                case 270 -> Direction.Top;
                default -> direction;
            };
            case Left -> switch (angle) {
                case 90 -> Direction.Top;
                case 180 -> Direction.Right;
                case 270 -> Direction.Bottom;
                default -> direction;
            };
        };
    }

    public static Vector directionToVector(Direction ejectSlotWsDirection) {
        return switch (ejectSlotWsDirection) {
            case Top -> new Vector(0, -1);
            case Right -> new Vector(1, 0);
            case Bottom -> new Vector(0, 1);
            case Left -> new Vector(-1, 0);
        };
    }

    public static Vector mixVector(Vector v1, Vector v2, double a) {
        return new Vector(v1.x * (1 - a) + v2.x * a, v1.y * (1 - a) + v2.y * a);
    }

    public static int directionToAngle(Direction direction) {
        return switch (direction) {
            case Top -> 0;
            case Right -> 90;
            case Bottom -> 180;
            case Left -> 270;
        };
    }

    public double length() {
        return Math.sqrt(xy);
    }

    public Vector rotateFastMultipleOf90(int angle) {
        return switch (angle) {
            case 360, 0 -> new Vector(this.x, this.y);
            case 90 -> new Vector(-this.y, this.x);
            case 180 -> new Vector(-this.x, -this.y);
            case 270 -> new Vector(this.y, -this.x);
            default -> new Vector();
        };
    }

    public Vector sub(Vector other) {
        return new Vector(this.x - other.x, this.y - other.y);
    }

    public Vector add(Vector other) {
        return new Vector(this.x + other.x, this.y + other.y);
    }

    public boolean equals(Vector v) {
        return this.x == v.x && v.y == this.y;
    }

    public Vector toWorldSpaceCenterOfTile() {
        return new Vector(this.x * tileSize + halfTileSize, this.y * tileSize + halfTileSize);
    }

    public Vector multiplyScalar(double f) {
        return new Vector(this.x * f, this.y * f);
    }

    public Vector copy() {
        return new Vector(this.x, this.y);
    }

    public Vector divideScalar(double f) {
        return new Vector(this.x / f, this.y / f);
    }

    public Vector subScalars(double x, double y) {
        return new Vector(this.x - x, this.y - y);
    }

    public Vector toTileSpace() {
        return new Vector(Math.floor(this.x / tileSize), Math.floor(this.y / tileSize));
    }
}
