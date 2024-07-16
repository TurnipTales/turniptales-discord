package net.turniptales.discord.buttons;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public abstract class ButtonBase extends ListenerAdapter {

    private final String name;

    public ButtonBase(String name) {
        this.name = name;
    }

    public abstract void onButtonClick(ButtonInteractionEvent event);

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String componentId = event.getComponentId();
        if (componentId.equalsIgnoreCase(this.name)) {
            onButtonClick(event);
        }
    }
}
