package io.shapez.core;

import io.shapez.game.MouseButton;

import java.util.LinkedList;
import java.util.function.BiConsumer;

public class MouseButtonHandler {
    final LinkedList<BiConsumer<Vector, MouseButton>> receivers = new LinkedList<>();
    int modifyCount = 0;

    void add(BiConsumer<Vector, MouseButton> receiver) {
        this.receivers.addLast(receiver);
        ++this.modifyCount;
    }

    void addToTop(BiConsumer<Vector, MouseButton> receiver) {
        this.receivers.addFirst(receiver);
        ++modifyCount;
    }

    public void dispatch(Vector location, MouseButton button) {
        int modifyState = this.modifyCount;

        for (BiConsumer<Vector, MouseButton> receiver : this.receivers) {
            receiver.accept(location, button);
            if (modifyState != modifyCount) {
                return;
            }
        }
    }

    void remove(BiConsumer<Vector, MouseButton> receiver) {
        this.receivers.remove(receiver);
    }

    void removeAll() {
        this.receivers.clear();
    }
}
