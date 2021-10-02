package io.shapez.game.systems;

import io.shapez.game.Component;

public class HubComponent extends Component {
    public HubComponent(Object o) {
        super();
    }

    @Override
    public String getId() {
        return "Hub";
    }
}
