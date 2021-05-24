package haveric.recipeManager.tools;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Iterator;

public abstract class BaseRecipeIterator {
    public void replace(ItemStack overrideItem) {

    }

    public void finish() {

    }

    public Iterator<Recipe> getIterator() {
        return getBukkitRecipeIterator();
    }

    protected Iterator<Recipe> getBukkitRecipeIterator() {
        return Bukkit.recipeIterator();
    }

    protected Field stripPrivateFinal(Class<?> clazz, String field) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
        Field fieldF = clazz.getDeclaredField(field);
        fieldF.setAccessible(true);
        // Remove final modifier
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(fieldF, fieldF.getModifiers() & ~Modifier.FINAL);
        return fieldF;
    }
}
