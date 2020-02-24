package haveric.recipeManager.recipes;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public class BaseRecipeEvents implements Listener {
    public void clean() {
        HandlerList.unregisterAll(this);
    }
}
