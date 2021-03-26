package io.shapez.core;

import io.shapez.game.Board;

public class Vector {
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

    public static Direction invertDirection(Direction direction) {
        switch (direction) {
            case Top:
                return Direction.Bottom;
            case Right:
                return Direction.Left;
            case Left:
                return Direction.Right;
            case Bottom:
                return Direction.Top;
            default:
                return direction;
        }
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

    public Vector sub(Vector other) {
        return new Vector(this.x - other.x, this.y - other.y);
    }

    public Vector add(Vector other) {
        return new Vector(this.x + other.x, this.y + other.y);
    }

    public boolean equals(Vector v) {
        return this.x == v.x && v.y == this.y;
    }
}
