package haveric.recipeManager.flag.flags.result;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.tools.Tools;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

public class FlagFoodPotionEffect extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.FOOD_POTION_EFFECT;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <chance> | <potionEffect>", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Adds a potion effect as a food component to an item",
            "Using this flag more than once will add more potion effects.",
            "",
            "<chance> is the probability this potion effect will be applied. Value must be between 0 and 1 inclusive.",
            "",
            "<potionEffect> The potion effect that will be applied to the player on eating the food.",
            "  See " + FlagType.POTION_EFFECT + " for options",
            "",
            "NOTE: Use with " + FlagType.FOOD + " if you want the item to actually be consumable as the default settings will not eat the item.", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} 1 | heal // 100% chance to apply heal potion effect",
            "{flag} 0.5 | blindness | duration 60 | amplifier 5 // 50% chance to apply effect",
            "{flag} 0.1 | poison | ambient | amplifier 666 | duration 6.66 // 10% chance to apply effect", };
    }

    private List<PotionEffect> effects = new ArrayList<>();
    private List<Float> effectsChance = new ArrayList<>();

    public FlagFoodPotionEffect() {

    }

    public FlagFoodPotionEffect(FlagFoodPotionEffect flag) {
        super(flag);
        effects.addAll(flag.effects);
        effectsChance.addAll(flag.effectsChance);
    }

    @Override
    public FlagFoodPotionEffect clone() {
        return new FlagFoodPotionEffect((FlagFoodPotionEffect) super.clone());
    }

    @Override
    public boolean requiresRecipeManagerModification() {
        return false;
    }

    public void setEffects(List<PotionEffect> newEffects, List<Float> newChances) {
        if (newEffects == null || newChances == null) {
            remove();
        } else {
            effects.clear();
            effects.addAll(newEffects);

            effectsChance.clear();
            effectsChance.addAll(newChances);
        }
    }

    public void addEffect(PotionEffect effect, Float chance) {
        effects.add(effect);
        effectsChance.add(chance);
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum, int restrictedBit) {
        super.onParse(value, fileName, lineNum, restrictedBit);
        String[] split = value.split("\\|", 2);

        float chance;
        try {
            chance = Float.parseFloat(split[0]);
        } catch (NumberFormatException e) {
            ErrorReporter.getInstance().warning("The " + getFlagType() + " flag has invalid chance number: " + value, "Defaulting to a chance of 1.");
            chance = 1;
        }

        PotionEffect effect = Tools.parsePotionEffect(split[1], getFlagType());
        if (effect != null) {
            addEffect(effect, chance);
        }

        return true;
    }

    @Override
    public void onPrepare(Args a) {
        onCrafted(a);
    }

    @Override
    public void onCrafted(Args a) {
        if (canAddMeta(a)) {
            ItemMeta meta = a.result().getItemMeta();

            if (meta != null) {
                FoodComponent food = meta.getFood();
                for (int i = 0; i < effects.size(); i++) {
                    PotionEffect effect = effects.get(i);
                    Float chance = effectsChance.get(i);

                    food.addEffect(effect, chance);
                }

                meta.setFood(food);
            }

            a.result().setItemMeta(meta);
        }
    }

    @Override
    public int hashCode() {
        StringBuilder toHash = new StringBuilder("" + super.hashCode());

        for (int i = 0; i < effects.size(); i++) {
            toHash.append("effect: " ).append(effects.get(i).hashCode());
            toHash.append("chance: " ).append(effectsChance.get(i));
        }

        return toHash.toString().hashCode();
    }
}
