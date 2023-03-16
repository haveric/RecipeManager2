package haveric.recipeManager;

import haveric.recipeManager.settings.BaseSettings;
import org.bukkit.Material;

import java.util.List;

public class TestSettings extends BaseSettings {

    public TestSettings(boolean loadDefaultConfig) {
        super(loadDefaultConfig);
    }

    @Override
    public boolean getSpecialRepair() {
        return SPECIAL_RECIPE_DEFAULT;
    }

    @Override
    public boolean getSpecialRepairMetadata() {
        return SPECIAL_REPAIR_METADATA_DEFAULT;
    }

    @Override
    public boolean getSpecialLeatherDye() {
        return SPECIAL_RECIPE_DEFAULT;
    }

    @Override
    public boolean getSpecialFireworks() {
        return SPECIAL_RECIPE_DEFAULT;
    }

    @Override
    public boolean getSpecialFireworkStar() {
        return SPECIAL_RECIPE_DEFAULT;
    }

    @Override
    public boolean getSpecialFireworkStarFade() {
        return SPECIAL_RECIPE_DEFAULT;
    }

    @Override
    public boolean getSpecialMapCloning() {
        return SPECIAL_RECIPE_DEFAULT;
    }

    @Override
    public boolean getSpecialMapExtending() {
        return SPECIAL_RECIPE_DEFAULT;
    }

    @Override
    public boolean getSpecialBookCloning() {
        return SPECIAL_RECIPE_DEFAULT;
    }

    @Override
    public boolean getSpecialAnvilCombineItem() {
        return SPECIAL_RECIPE_DEFAULT;
    }

    @Override
    public boolean getSpecialAnvilEnchant() {
        return SPECIAL_RECIPE_DEFAULT;
    }

    @Override
    public boolean getSpecialAnvilRepairMaterial() {
        return SPECIAL_RECIPE_DEFAULT;
    }

    @Override
    public boolean getSpecialAnvilRenaming() {
        return SPECIAL_RECIPE_DEFAULT;
    }

    @Override
    public boolean getSpecialGrindstoneCombineItem() {
        return SPECIAL_RECIPE_DEFAULT;
    }

    @Override
    public boolean getSpecialGrindstoneDisenchantBook() {
        return SPECIAL_RECIPE_DEFAULT;
    }

    @Override
    public boolean getSpecialGrindstoneDisenchantItem() {
        return SPECIAL_RECIPE_DEFAULT;
    }

    @Override
    public boolean getSpecialCartographyClone() {
        return SPECIAL_RECIPE_DEFAULT;
    }

    @Override
    public boolean getSpecialCartographyExtend() {
        return SPECIAL_RECIPE_DEFAULT;
    }

    @Override
    public boolean getSpecialCartographyLock() {
        return SPECIAL_RECIPE_DEFAULT;
    }

    @Override
    public boolean getSpecialBanner() {
        return SPECIAL_RECIPE_DEFAULT;
    }

    @Override
    public boolean getSpecialBannerDuplicate() {
        return SPECIAL_RECIPE_DEFAULT;
    }

    @Override
    public boolean getSpecialShieldBanner() {
        return SPECIAL_RECIPE_DEFAULT;
    }

    @Override
    public boolean getSpecialTippedArrows() {
        return SPECIAL_RECIPE_DEFAULT;
    }

    @Override
    public boolean getSpecialShulkerDye() {
        return SPECIAL_RECIPE_DEFAULT;
    }

    @Override
    public boolean getSpecialSuspiciousStew() {
        return SPECIAL_RECIPE_DEFAULT;
    }

    @Override
    public boolean getSpecialDecoratedPot() {
        return SPECIAL_RECIPE_DEFAULT;
    }

    @Override
    public boolean getSoundsRepair() {
        return SOUNDS_REPAIR_DEFAULT;
    }

    @Override
    public boolean getSoundsFailed() {
        return SOUNDS_FAILED_DEFAULT;
    }

    @Override
    public boolean getSoundsFailedClick() {
        return SOUNDS_FAILED_CLICK_DEFAULT;
    }

    @Override
    public boolean getFixModResults() {
        return FIX_MOD_RESULTS_DEFAULT;
    }

    @Override
    public boolean getUpdateBooks() {
        return UPDATE_BOOKS_DEFAULT;
    }

    @Override
    public boolean getColorConsole() {
        return COLOR_CONSOLE_DEFAULT;
    }

    @Override
    public char getFurnaceShiftClick() {
        return FURNACE_SHIFT_CLICK_DEFAULT.charAt(0);
    }

    @Override
    public boolean getMultithreading() {
        return false;
    }

    @Override
    public boolean getClearRecipes() {
        return CLEAR_RECIPES_DEFAULT;
    }

    @Override
    public boolean getUpdateCheckEnabled() {
        return UPDATE_CHECK_ENABLED_DEFAULT;
    }

    @Override
    public int getUpdateCheckFrequency() {
        return UPDATE_CHECK_FREQUENCY_DEFAULT;
    }

    @Override
    public boolean getUpdateCheckLogNewOnly() {
        return UPDATE_CHECK_LOG_NEW_ONLY_DEFAULT;
    }

    @Override
    public int getSaveFrequencyForBrewingStands() {
        return SAVE_FREQUENCY_DEFAULT;
    }

    @Override
    public int getSaveFrequencyForCampfires() {
        return SAVE_FREQUENCY_DEFAULT;
    }

    @Override
    public int getSaveFrequencyForComposters() {
        return SAVE_FREQUENCY_DEFAULT;
    }

    @Override
    public int getSaveFrequencyForCooldowns() {
        return SAVE_FREQUENCY_DEFAULT;
    }

    @Override
    public int getSaveFrequencyForFurnaces() {
        return SAVE_FREQUENCY_DEFAULT;
    }

    @Override
    public Material getFailMaterial() {
        return MATERIAL_FAIL_DEFAULT;
    }

    @Override
    public Material getSecretMaterial() {
        return MATERIAL_SECRET_DEFAULT;
    }

    @Override
    public Material getMultipleResultsMaterial() {
        return MATERIAL_MULTIPLE_RESULTS_DEFAULT;
    }

    @Override
    public boolean getDisableOverrideWarnings() {
        return DISABLE_OVERRIDE_WARNINGS_DEFAULT;
    }

    @Override
    public List<String> getRecipeCommentCharactersAsList() {
        return RECIPE_COMMENT_CHARACTERS_DEFAULT;
    }

}
