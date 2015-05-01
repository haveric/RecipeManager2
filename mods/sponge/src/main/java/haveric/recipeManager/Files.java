package haveric.recipeManager;

import haveric.recipeManager.tools.Tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Set;

import org.spongepowered.api.GameRegistry;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;

import com.google.common.collect.Sets;


public class Files {
    public static final String NL = System.getProperty("line.separator");

    public static final String FILE_USED_VERSION = "used.version";
    public static final String FILE_INFO_NAMES = "name index.html";

    public static final Set<String> FILE_RECIPE_EXTENSIONS = Sets.newHashSet(".txt", ".rm");
    
    private static String DIR_PLUGIN;
    private static final String SPONGE_DOCS = "";

    private RecipeManager plugin;

    public Files(RecipeManager recipeManager) {
        plugin = recipeManager;
        DIR_PLUGIN = plugin.getSettings().getDefaultFolderPath() + File.separator;
    }

    public void reload() {
        createDirectories();

        boolean overwrite = isNewVersion();

        createNameIndex(overwrite);
    }

    private void createDirectories() {
        File file = new File(DIR_PLUGIN + "recipes" + File.separator + "disabled");
        file.mkdirs();

        file = new File(file.getPath() + File.separator + "Recipe files in here are ignored!");

        if (!file.exists()) {
            Tools.saveTextToFile("In the disabled folder you can place recipe files you don't want to load, instead of deleting them.", file);
        }
    }

    private boolean isNewVersion() {
        boolean newVersion = true;

        File file = new File(DIR_PLUGIN + FILE_USED_VERSION);
        String currentVersion = plugin.getVersion();


        try {
            if (file.exists()) {
                BufferedReader b = new BufferedReader(new FileReader(file));
                String version = b.readLine();
                b.close();
                newVersion = (version == null || !version.equals(currentVersion));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (newVersion || file.exists()) {
            Tools.saveTextToFile(currentVersion, file);
        }

        return newVersion;
    }

    private boolean fileExists(String file, boolean overwrite) {
        if (overwrite) {
            return false;
        }

        return new File(DIR_PLUGIN + file).exists();
    }

    private void createNameIndex(boolean overwrite) {
        if (fileExists(FILE_INFO_NAMES, overwrite)) {
            return;
        }

        StringBuilder s = new StringBuilder(24000);

        s.append("<title>Name index</title><pre style='font-family:Lucida Console;font-size:16px;width:100%;'>");
        s.append(NL).append("<a href='basic recipes.html'>Basic Recipes</a> | <a href='advanced recipes.html'>Advanced Recipes</a> | <a href='recipe flags.html'>Recipe Flags</a> | <a href='recipe books.html'>Recipe Books</a> | <b>Name Index</b> | <a href='commands & permissions.html'>Commands &amp; Permissions</a>");
        s.append(NL).append("<h1>Name index</h1>");
        s.append(NL).append("Data extracted from your server and it may contain names added by other plugins/mods!");
        s.append(NL).append("If you want to update this file just delete it and use '<i>rmreload</i>' or start the server.");
        s.append(NL);
        s.append(NL).append("<hr>");
        s.append(NL);
        s.append(NL).append("<a name='contents'></a><h3>CONTENTS</h3>");
        s.append(NL).append("- <a href='#itemtypes'><b>ITEM TYPES</b></a>");


        s.append(NL);
        s.append(NL);
        s.append(NL).append("<hr>");
        s.append(NL);
        s.append(NL).append("<a name='itemtypes'></a><a href='#contents'>^ Contents</a><h3>ITEM TYPES</h3>");
        //s.append("<a href='" + SPONGE_DOCS + "Material.html'>BukkitAPI / Material</a>");
        s.append(NL).append("Data/damage/durability values are listed at <a href='http://www.minecraftwiki.net/wiki/Data_value#Data'>Minecraft Wiki / Data Value</a>");
        s.append(NL);
        s.append(NL).append(String.format(" %-34s %-24s %-24s %-5s", "Id", "Name", "Alias", "Stack"));
        
        for (Field field : ItemTypes.class.getDeclaredFields()) {
            if (field.getType().equals(ItemType.class)) {
                try {
                    ItemType item = (ItemType) field.get(field);

                    String alias = null;//Settings.getInstance().getMaterialPrint(m);

                    String aliasString;
                    if (alias == null) {
                        aliasString = "";
                    } else {
                        aliasString = alias;
                    }

                    s.append(NL).append(String.format(" %-34s %-24s %-24s %-5d", item.getId(), field.getName(), aliasString, item.getMaxStackQuantity()));
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        /*
        GameRegistry registry = RecipeManager.getGame().getRegistry();
        Collection<ItemType> allItems = registry.getAllOf(ItemType.class);
        for (ItemType item: allItems) {
            String alias = null;//Settings.getInstance().getMaterialPrint(m);

            String aliasString;
            if (alias == null) {
                aliasString = "";
            } else {
                aliasString = alias;
            }

            s.append(NL).append(String.format(" %-34s %-24s %-5d", item.getId(), aliasString, item.getMaxStackQuantity()));
        }
        */


        Tools.saveTextToFile(s.toString(), DIR_PLUGIN + FILE_INFO_NAMES);
    }
}
