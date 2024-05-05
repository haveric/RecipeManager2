package haveric.recipeManager.flag.flags.result;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.recipes.FlaggableRecipeChoice;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.tools.ToolsRecipeChoice;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.KnowledgeBookMeta;

import java.util.ArrayList;
import java.util.List;

public class FlagKnowledgeBookItem extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.KNOWLEDGE_BOOK_ITEM;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <namespace>:<key>, [...]",
            "{flag} <key>, [...]", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Add recipes to a knowledge book",
            "",
            "The <namespace> argument is the plugin that the recipe was added by or minecraft itself.",
            "The <key> is the specific recipe key",
            "  If the <key> is used alone, the namespace is defaulted to minecraft.",
            "",
            "You can add multiple recipes by separating them with a comma.", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} minecraft:bucket",
            "{flag} campfire",
            "{flag} minecraft:spruce_boat, birch_boat, minecraft:iron_pickaxe", };
    }

    private List<NamespacedKey> namespacedKeys = new ArrayList<>();

    public FlagKnowledgeBookItem() {

    }

    public FlagKnowledgeBookItem(FlagKnowledgeBookItem flag) {
        super(flag);

        setNamespacedKeys(flag.namespacedKeys);
    }

    @Override
    public FlagKnowledgeBookItem clone() {
        return new FlagKnowledgeBookItem((FlagKnowledgeBookItem) super.clone());
    }

    public void setNamespacedKeys(List<NamespacedKey> newNamespacedKeys) {
        namespacedKeys.clear();
        namespacedKeys.addAll(newNamespacedKeys);
    }

    public List<NamespacedKey> getNamespacedKeys() {
        return namespacedKeys;
    }

    public void addNamespacedKey(NamespacedKey namespacedKey) {
        namespacedKeys.add(namespacedKey);
    }

    @Override
    public boolean requiresRecipeManagerModification() {
        return false;
    }

    @Override
    public boolean onValidate() {
        ItemResult result = getResult();
        boolean validResult = false;
        if (result != null && (result.getItemMeta() instanceof KnowledgeBookMeta)) {
            validResult = true;
        }

        boolean validFlaggable = false;
        FlaggableRecipeChoice flaggableRecipeChoice = getFlaggableRecipeChoice();
        if (flaggableRecipeChoice != null && ToolsRecipeChoice.isValidMetaType(flaggableRecipeChoice.getChoice(), KnowledgeBookMeta.class)) {
            validFlaggable = true;
        }

        if (!validResult && !validFlaggable) {
            return ErrorReporter.getInstance().error("Flag " + getFlagType() + " needs a knowledge book result!");
        }

        return true;
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum, int restrictedBit) {
        super.onParse(value, fileName, lineNum, restrictedBit);

        String[] split = value.toLowerCase().split(",");
        for (String arg : split) {
            String[] keySplit = arg.trim().split(":");
            String namespace;
            String key;
            if (keySplit.length >= 2) {
                namespace = keySplit[0].trim();
                key = keySplit[1].trim();
            } else {
                namespace = NamespacedKey.MINECRAFT;
                key = keySplit[0].trim();
            }

            try {
                NamespacedKey namespacedKey = new NamespacedKey(namespace, key);
                addNamespacedKey(namespacedKey);
            } catch (IllegalArgumentException e) {
                return ErrorReporter.getInstance().error("Flag " + getFlagType() + " has invalid namespace or key. Namespace: " + namespace + ", key: " + key + " from: " + arg);
            }
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

            if (!(meta instanceof KnowledgeBookMeta)) {
                a.addCustomReason("Needs knowledge book!");
                return;
            }

            KnowledgeBookMeta knowledgeBookMeta = (KnowledgeBookMeta) meta;

            knowledgeBookMeta.setRecipes(namespacedKeys);

            a.result().setItemMeta(knowledgeBookMeta);
        }
    }

    @Override
    public int hashCode() {
        String toHash = "" + super.hashCode();

        toHash += "namespacedkeys: ";
        for (NamespacedKey namespacedKey : namespacedKeys) {
            toHash += "namespace: " + namespacedKey.getNamespace() + " - key: " + namespacedKey.getKey();
        }

        return toHash.hashCode();
    }
}
