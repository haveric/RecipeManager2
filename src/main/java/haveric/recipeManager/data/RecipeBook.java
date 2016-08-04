package haveric.recipeManager.data;

import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManagerCommon.RMCChatColor;
import haveric.recipeManagerCommon.data.AbstractRecipeBook;
import haveric.recipeManagerCommon.util.RMCUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RecipeBook extends AbstractRecipeBook {

    public RecipeBook(String newId) {
        super(newId);
    }

    /**
     * Add specified recipe to the book.
     *
     * @param recipe
     *            a valid recipe.
     * @return true if added, false if it already exists in the book.
     * @throws IllegalArgumentException
     *             if recipe is invalid.
     */
    public boolean addRecipe(BaseRecipe recipe) {
        if (!recipe.isValid()) {
            throw new IllegalArgumentException("Invalid recipe object - needs data!");
        }

        for (Set<String> recipes : volumes) {
            if (recipes.contains(recipe.getName())) {
                return false;
            }
        }

        addRecipe(recipe.getName());

        return true;
    }

    /**
     * @param volume
     *            volume to get
     * @return WrittenBook item.
     * @throws IllegalAccessError
     *             if book is not valid yet.
     */
    public ItemStack getBookItem(int volume) {
        if (!isValid()) {
            throw new IllegalAccessError("Book is not yet valid!");
        }

        ItemStack item = new ItemStack(Material.WRITTEN_BOOK, 1);
        item.setItemMeta(getBookMeta(volume));

        return item;
    }

    public BookMeta getBookMeta(int volume) {
        if (!isValid()) {
            throw new IllegalAccessError("Book is not yet valid!");
        }

        volume = Math.min(Math.max(volume, 1), getVolumesNum());
        int volumeID = volume - 1;
        BookMeta meta = (BookMeta) Bukkit.getItemFactory().getItemMeta(Material.WRITTEN_BOOK);

        String bookVolume = "";
        if (getVolumesNum() > 1) {
            bookVolume = " - " + Messages.getInstance().parse("recipebook.volume", "{volume}", volume);
        }
        meta.setTitle(getTitle() + bookVolume);
        meta.setAuthor(getAuthor() + RMCUtil.hideString(" " + getId() + " " + volume + " " + (System.currentTimeMillis() / 1000)));

        // Cover page
        if (cover) {
            StringBuilder coverString = new StringBuilder(256);

            coverString.append('\n').append(RMCChatColor.BLACK).append(RMCChatColor.BOLD).append(RMCChatColor.UNDERLINE).append(getTitle());

            if (getVolumesNum() > 1) {
                coverString.append('\n').append(RMCChatColor.BLACK).append("  ").append(Messages.getInstance().parse("recipebook.volumeofvolumes", "{volume}", volume, "{volumes}", getVolumesNum()));
            }

            coverString.append('\n').append(RMCChatColor.GRAY).append("        Published by\n        RecipeManager");

            if (getDescription() != null) {
                coverString.append('\n').append(RMCChatColor.DARK_BLUE).append(getDescription());
            }

            meta.addPage(coverString.toString());
        }

        // Build contents index and page content
        List<StringBuilder> index = null;

        List<String> pages = new ArrayList<>();
        int i = 0;
        int r = 2;
        int p = (int) Math.ceil(volumes.get(volumeID).size() / 13.0) + 2;

        if (contents) {
            index = new ArrayList<>();
            index.add(new StringBuilder(256).append(Messages.getInstance().get("recipebook.header.contents")).append("\n\n").append(RMCChatColor.BLACK));
        }

        for (String name : volumes.get(volumeID)) {
            BaseRecipe recipe = RecipeManager.getRecipes().getRecipeByName(name);

            if (contents) {
                if (r > 13) {
                    r = 0;
                    i++;
                    index.add(new StringBuilder(256).append(RMCChatColor.BLACK));
                }

                String indexName = recipe.printBookIndex();
                index.get(i).append(RMCChatColor.BLACK).append(p).append(". ").append(indexName).append('\n');

                if (indexName.length() >= 18) {
                    r += 2;
                } else {
                    r += 1;
                }

                p += 1;
            }

            String page = recipe.printBook();

            if (page.length() >= 255) {
                int x = page.indexOf('\n', 220);

                if (x < 0 || x > 255) {
                    x = 255;
                }

                pages.add(page.substring(0, x));
                pages.add(page.substring(x + 1));
                p++;
            } else {
                pages.add(page);
            }
        }

        if (contents) {
            for (StringBuilder s : index) {
                meta.addPage(s.toString());
            }
        }

        for (String s : pages) {
            meta.addPage(s);
        }

        if (hasEndPage()) {
            if (customEnd == null) {
                meta.addPage(String.format("\n\n\n\n\n\n        %s%s%s", RMCChatColor.BOLD, RMCChatColor.UNDERLINE, "THE END"));
            } else {
                // TODO split into pages ?

                meta.addPage(customEnd);
            }
        }

        return meta;
    }
}
