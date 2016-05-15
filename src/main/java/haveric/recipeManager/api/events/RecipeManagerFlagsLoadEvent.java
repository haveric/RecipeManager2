package haveric.recipeManager.api.events;

import haveric.recipeManager.flags.*;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RecipeManagerFlagsLoadEvent extends Event {
    private static HandlerList handlers = new HandlerList();

    private FlagLoader loader;

    public RecipeManagerFlagsLoadEvent(FlagLoader newLoader) {
        loader = newLoader;
    }

    public void loadFlag(String mainAlias, Flag newFlag, int bits, String... aliases) {
        loader.loadCustomFlag(mainAlias, newFlag, bits, aliases);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
