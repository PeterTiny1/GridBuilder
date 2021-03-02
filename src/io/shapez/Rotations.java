package io.shapez;

import java.util.HashMap;
import java.util.Map;
public class Rotations{
    public static enum cRotations {
        Up(1),
        Right(2),
        Down(3),
        Left(4);

        private int value;
        private static Map map = new HashMap<>();

        private cRotations(int value) {
            this.value = value;
        }

        static {
            for (cRotations pageType : cRotations.values()) {
                map.put(pageType.value, pageType);
            }
        }

        public static io.shapez.Rotations.cRotations valueOf(int pageType) {
            return (Rotations.cRotations) map.get(pageType);
        }

        public int getValue() {
            return value;
        }

}
}
