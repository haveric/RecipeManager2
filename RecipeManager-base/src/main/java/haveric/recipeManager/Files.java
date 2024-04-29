package haveric.recipeManager;

import com.google.common.collect.Sets;
import haveric.recipeManager.common.RMCChatColor;
import haveric.recipeManager.common.util.RMCUtil;
import haveric.recipeManager.flag.FlagBit;
import haveric.recipeManager.flag.FlagDescriptor;
import haveric.recipeManager.flag.FlagFactory;
import haveric.recipeManager.messages.MessageSender;
import haveric.recipeManager.tools.HtmlEscaper;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.Version;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.block.banner.PatternType;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.EntityType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.io.*;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.Map.Entry;

import static org.bukkit.Tag.REGISTRY_BLOCKS;
import static org.bukkit.Tag.REGISTRY_ITEMS;

public class Files {
    public static final String NL = System.lineSeparator();

    private final CommandSender sender;

    public static final String LASTCHANGED_CONFIG = "2.30.0-dev1";
    public static final String LASTCHANGED_MESSAGES = "2.30.0-dev1";
    public static final String LASTCHANGED_CHOICE_ALIASES = "2.17.0";
    public static final String LASTCHANGED_ITEM_DATAS = "2.7";
    public static final String LASTCHANGED_ITEM_ALIASES = "2.28.0";

    public static final String FILE_CONFIG = "config.yml";
    public static final String FILE_MESSAGES = "messages.yml";

    public static final String FILE_CHOICE_ALIASES = "choice aliases.yml";
    public static final String FILE_ITEM_DATAS = "item datas.yml";
    public static final String FILE_ITEM_ALIASES = "item aliases.yml";

    public static final String FILE_USED_VERSION = "used.version";
    public static final String FILE_CHANGELOG = "changelog.txt";

    public static final String FILE_INFO_BASICS = "basic recipes.html";
    public static final String FILE_INFO_ADVANCED = "advanced recipes.html";
    public static final String FILE_INFO_COMMANDS = "commands & permissions.html";
    public static final String FILE_INFO_NAMES = "name index.html";
    public static final String FILE_INFO_FLAGS = "recipe flags.html";
    public static final String FILE_INFO_BOOKS = "recipe books.html";

    public static final Set<String> FILE_RECIPE_EXTENSIONS = Sets.newHashSet(".txt", ".rm");

    private static final String BUKKIT_DOCS = "https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/";

    private static final String TITLE_RECIPE_FLAGS = "Recipe Flags";
    private static final String TITLE_NAME_INDEX = "Name Index";
    private static final String TITLE_COMMANDS_PERMISSIONS = "Commands &amp; Permissions";

    List<String> navItems;

    protected static void init() {
    }

    protected static void reload(CommandSender sender) {
        new Files(sender);
    }

    private Files(CommandSender newSender) {
        sender = newSender;

        navItems = new ArrayList<>();
        navItems.add("Basic Recipes");
        navItems.add("Advanced Recipes");
        navItems.add(TITLE_RECIPE_FLAGS);
        navItems.add("Recipe Books");
        navItems.add(TITLE_NAME_INDEX);
        navItems.add(TITLE_COMMANDS_PERMISSIONS);

        createDirectories();

        boolean overwrite = isNewVersion();

        createRecipeFlags(overwrite);
        createCommands(overwrite);
        createNameIndex(overwrite);
        createFile(FILE_INFO_BASICS, overwrite);
        createFile(FILE_INFO_ADVANCED, overwrite);
        createFile(FILE_INFO_BOOKS, overwrite);
        createFile(FILE_CHANGELOG, overwrite);

        if (overwrite) {
            MessageSender.getInstance().sendAndLog(newSender, "<gray>New version installed, information files and changelog have been overwritten.");
        }
    }

    public static String getNameIndexHashLink(String hash) {
        return "<a href='" + FILE_INFO_NAMES + "#" + hash + "'>" + FILE_INFO_NAMES + "#" + hash + "</a>";
    }

    private boolean isNewVersion() {
        boolean newVersion = true;

        try {
            File file = new File(RecipeManager.getPlugin().getDataFolder() + File.separator + FILE_USED_VERSION);
            String currentVersion = RecipeManager.getPlugin().getDescription().getVersion();

            if (file.exists()) {
                BufferedReader b = new BufferedReader(new FileReader(file));
                String oldVersion = b.readLine();
                b.close();
                newVersion = (oldVersion == null || !oldVersion.equals(currentVersion));

                // Port disabled folder to extracted folder outside recipes
                if (Updater.isVersionOlderThan(oldVersion, "2.23.1-dev4") == 1) {
                    File oldDisabledFolder = new File(RecipeManager.getPlugin().getDataFolder() + File.separator + "recipes" + File.separator + "disabled");
                    File oldIgnoredFile = new File(oldDisabledFolder.getPath() + File.separator + "Recipe files in here are ignored!");
                    File newExtractedFolder = new File(RecipeManager.getPlugin().getDataFolder() + File.separator + "extracted");

                    oldIgnoredFile.delete();
                    if (oldDisabledFolder.exists()) {
                        java.nio.file.Files.move(oldDisabledFolder.toPath(), newExtractedFolder.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }

            if (newVersion || file.exists()) {
                BufferedWriter b = new BufferedWriter(new FileWriter(file, false));
                b.write(currentVersion);
                b.close();
            }
        } catch (Throwable e) {
            MessageSender.getInstance().error(null, e, null);
        }

        return newVersion;
    }

    private void createDirectories() {
        // Create base directories
        File file = new File(RecipeManager.getPlugin().getDataFolder() + File.separator + "extracted");
        file.mkdirs();
    }

    private boolean fileExists(String file, boolean overwrite) {
        if (overwrite) {
            return false;
        }

        return new File(RecipeManager.getPlugin().getDataFolder() + File.separator + file).exists();
    }

    private void createFile(String file, boolean overwrite) {
        if (fileExists(file, overwrite)) {
            return;
        }

        RecipeManager.getPlugin().saveResource(file, true);
    }

    private void addHeader(StringBuilder s, String title) {
        s.append("<!doctype html>").append(NL).append("<html lang='en'>").append(NL).append("<head>");
        s.append(NL).append("<meta charset='UTF-8'>");
        s.append(NL).append("<title>").append(title).append(" - RecipeManager2</title>");
        s.append(NL).append("<link rel='stylesheet' href='https://www.recipemanager.dev/css/vendor.css?v=1'/>");
        s.append(NL).append("<link rel='stylesheet' href='https://www.recipemanager.dev/css/app.css?v=1'/>");
        s.append(NL).append("</head>").append(NL).append("<body>");
        addNav(s, title);
        s.append(NL).append("<div class='container-full'>");
        s.append(NL).append("<div class='doc-section__group'>");
        s.append(NL).append("<h1 class='doc-section__group-title'>").append(title).append("</h1>");
    }

    private void addNav(StringBuilder s, String title) {
        s.append(NL).append("<nav class='nav-docs'><div class='container'>");

        s.append(NL);
        boolean first = true;
        for (String navItem : navItems) {
            if (!first) {
                s.append(" | ");
            }

            if (navItem.equals(title)) {
                s.append("<b>").append(navItem).append("</b>");
            } else {
                s.append("<a href='").append(navItem.toLowerCase().replaceAll("&amp;", "&")).append(".html'>").append(navItem).append("</a>");
            }

            first = false;
        }

        s.append(NL).append("</div></nav>");
    }

    private void createRecipeFlags(boolean overwrite) {
        if (fileExists(FILE_INFO_FLAGS, overwrite)) {
            return;
        }

        StringBuilder s = new StringBuilder(32000);
        Map<String, List<FlagDescriptor>> flags = new LinkedHashMap<>();

        String[] category = new String[] { "Recipe", "Ingredient", "Result" };

        int size = FlagFactory.getInstance().getFlags().values().size();

        for (String c : category) {
            flags.put(c, new ArrayList<>(size));
        }


        Map<String, FlagDescriptor> unsortedFlags = FlagFactory.getInstance().getFlags();
        List<Map.Entry<String, FlagDescriptor>> entries = new ArrayList<>(unsortedFlags.entrySet());
        entries.sort(Entry.comparingByKey());

        Map<String, FlagDescriptor> sortedFlags = new LinkedHashMap<>();
        for (Map.Entry<String, FlagDescriptor> entry : entries) {
            sortedFlags.put(entry.getKey(), entry.getValue());
        }

        for (FlagDescriptor flag : sortedFlags.values()) {
            if (flag.hasBit(FlagBit.RECIPE)) {
                flags.get(category[0]).add(flag);
            }

            if (flag.hasBit(FlagBit.INGREDIENT)) {
                flags.get(category[1]).add(flag);
            }

            if (flag.hasBit(FlagBit.RESULT)) {
                flags.get(category[2]).add(flag);
            }
        }

        addHeader(s, TITLE_RECIPE_FLAGS);

        s.append(NL).append("<pre>");
        s.append(NL).append("<b>WHAT ARE FLAGS ?</b>");
        s.append(NL).append("  Flags are the stuff that make a recipe very special! You can add various features to a recipe by using flags.");
        s.append(NL).append("  For examples see <a href='advanced recipes.html'><b>advanced recipes.html</b></a>.");
        s.append(NL);
        s.append(NL).append("<b>USING FLAGS</b>");
        s.append(NL).append("  Flags can be added in 4 'zones':");
        s.append(NL).append("  - at the beginning of the file - which are copied to all recipes from that file.");
        s.append(NL).append("  - after recipe type (CRAFT, COMBINE, etc) - where they affect that specific recipe, you may even overwrite file flags for that specific recipe!");
        s.append(NL).append("  - after recipe ingredients - to apply basic item requirements to a specific ingredient.");
        s.append(NL).append("  - after recipe's individual results - to apply flags for the result items.");
        s.append(NL);
        s.append(NL).append("<b>ABOUT ARGUMENTS</b>");
        s.append(NL).append("  Flags have arguments but not always are they all required.");
        s.append(NL).append("  Arguments enclosed between &lt; and &gt; are required and those enclosed between [ and ] are optional.");
        s.append(NL).append("  Some arguments may have 'or false', that means you can just type false in there to make it do something special (most likely disable the flag or a feature)");
        s.append(NL);
        s.append(NL).append("<b>ALIASES</b>");
        s.append(NL).append("  They're just other names for the flag that you can use, they have no special effect if used, only for your preference.");
        s.append(NL).append("</pre>");
        s.append(NL).append("</div>");
        s.append(NL).append("<div class='doc-section__group clearfix'>");
        s.append(NL).append("<div class='table-of-contents'>");
        s.append(NL).append("<a id='contents'></a>");
        s.append(NL).append("<h2 class='table-of-contents__title'>Contents</h2>");
        s.append(NL).append("</div>");

        for (String c : category) {
            s.append(NL).append("<div class='flaggroup'>");
            s.append(NL).append("<span class='flagtype flagtype-").append(c.toLowerCase()).append("'>").append(c).append(" Flags").append("</span>");
            s.append(NL).append("<ul class='flaggroup__list'>");
            for (FlagDescriptor flag : flags.get(c)) {
                s.append(NL).append("<li><a href='#").append(flag.getName()).append("'><b>").append(flag.getNameDisplay()).append("</b></a></li>");
            }
            s.append(NL).append("</ul>");
            s.append(NL).append("</div>");
        }

        s.append(NL).append("</div>");

        for (FlagDescriptor flag : sortedFlags.values()) {
            String[] args;
            String[] desc;
            String[] ex;
            try {
                args = flag.getArguments();
                desc = flag.getDescription();
                ex = flag.getExamples();
            } catch (NoSuchFieldError e) {
                MessageSender.getInstance().error(null, e, ChatColor.RED + "Failed to load information for flag: " + flag.getName());
                continue;
            }

            s.append(NL).append("<div class='doc-section__group'>");
            s.append(NL).append("<a href='#contents' class='back-to-top'>^ Contents</a><a id='").append(flag.getName()).append("'></a>");

            if (args != null) {
                for (String a : args) {
                    s.append(NL).append("<h3 class='doc-section__title'>").append(HtmlEscaper.htmlEscaper().escape(a.replace("{flag}", flag.getNameDisplay()))).append("</h3>");
                }
            }
            s.append(NL).append("<pre>");
            if (desc == null) {
                desc = new String[] { "Flag not yet documented...", };
            }

            s.append(NL).append("    ");
            if (flag.hasBit(FlagBit.RECIPE)) {
                s.append("<span class='flagtype flagtype-recipe'>Recipe</span>");
            }

            if (flag.hasBit(FlagBit.INGREDIENT)) {
                s.append("<span class='flagtype flagtype-ingredient'>Ingredient</span>");
            }

            if (flag.hasBit(FlagBit.RESULT)) {
                s.append("<span class='flagtype flagtype-result'>Result</span>");
            }
            s.append(NL);

            s.append(NL).append("<span>");
            for (String d : desc) {
                s.append(NL);

                if (d != null) {
                    s.append("    ");
                    if (d.contains("<a href")) {
                        s.append(d);
                    } else {
                        s.append(HtmlEscaper.htmlEscaper().escape(d));
                    }
                }
            }
            s.append(NL).append("</span>");

            if (!flag.hasBit(FlagBit.NO_FALSE)) {
                s.append(NL).append(NL).append("    Setting to 'false' or 'remove' will disable the flag.");
            }

            if (ex != null) {
                s.append(NL).append(NL).append("    <b>Examples:</b>");

                for (String e : ex) {
                    s.append(NL).append("      ").append(HtmlEscaper.htmlEscaper().escape(e.replace("{flag}", flag.getNameDisplay())));
                }
            }

            int flagNamesLength = flag.getNames().size();
            if (flagNamesLength > 1) {
                s.append(NL).append(NL).append("    <b>Aliases:</b> ");

                for (int i = 1; i < flagNamesLength; i++) {
                    if (i != 1) {
                        s.append(", ");
                    }

                    s.append('@').append(flag.getNames().get(i));
                }
            }

            s.append(NL).append("</pre>");
            s.append(NL).append("</div>");
        }

        s.append(NL).append("</div>");
        addNav(s, TITLE_RECIPE_FLAGS);
        appendFooter(s);
        saveAndLog(s, FILE_INFO_FLAGS);
    }

    private void createCommands(boolean overwrite) {
        if (fileExists(FILE_INFO_COMMANDS, overwrite)) {
            return;
        }

        StringBuilder s = new StringBuilder();

        addHeader(s, TITLE_COMMANDS_PERMISSIONS);

        s.append(NL).append("</div><pre>");
        s.append(NL).append("<h2>Commands</h2>");
        s.append("<table>");

        PluginDescriptionFile desc = RecipeManager.getPlugin().getDescription();
        Map<String, Map<String, Object>> cmds = desc.getCommands();
        Map<String, Object> data;

        for (Entry<String, Map<String, Object>> e : cmds.entrySet()) {
            data = e.getValue();

            if (data == null) {
                continue;
            }

            Object obj = data.get("permission");
            String permission;
            if (obj == null) {
                permission = null;
            } else {
                permission = obj.toString();
            }

            obj = data.get("usage");
            String usage;
            if (obj == null) {
                usage = null;
            } else {
                usage = obj.toString().replace("<command>", e.getKey());
            }

            obj = data.get("description");
            String info;
            if (obj == null) {
                info = null;
            } else {
                info = obj.toString();
            }

            obj = data.get("aliases");
            List<String> aliases;
            if (obj instanceof List) {
                aliases = (List<String>) obj;
            } else {
                aliases = null;
            }

            String aliasesString;
            if (aliases == null) {
                aliasesString = "N/A";
            } else {
                aliasesString = RMCUtil.collectionToString(aliases);
            }

            s.append(NL).append("<tr>");
            s.append("<td class='command'><b>");
            s.append(HtmlEscaper.htmlEscaper().escape(usage)).append("</b><span class='command__text'>");
            s.append("<br>Permission: ").append(permission);
            s.append("<br>Aliases: ").append(aliasesString);
            s.append("</span></td>");
            s.append("<td>").append(HtmlEscaper.htmlEscaper().escape(info)).append("</td>");
            s.append("</tr>");
        }

        s.append(NL).append("</table>");
        s.append(NL);
        s.append(NL);
        s.append(NL).append("<h2>Permissions</h2>");
        s.append("<table>");
        s.append(NL).append("<tr>");
        s.append("<th>Permission node</th>");
        s.append("<th>Defaulted to</th>");
        s.append("<th>Description</th>");
        s.append("</tr>");

        List<Permission> permissions = desc.getPermissions();
        List<Permission> perms = new ArrayList<>(permissions.size());

        perms.addAll(permissions);
        printPermissions(s, perms, null, ".command.");
        printPermissions(s, perms, ".command.", null);

        List<Permission> flagPerms = new ArrayList<>(FlagFactory.getInstance().getFlags().size() + 1);
        flagPerms.add(Bukkit.getPluginManager().getPermission(Perms.FLAG_PREFIX + "*"));
        for (FlagDescriptor type : FlagFactory.getInstance().getFlags().values()) {
            if (type.hasBit(FlagBit.NO_SKIP_PERMISSION)) {
                continue;
            }

            flagPerms.add(Bukkit.getPluginManager().getPermission(Perms.FLAG_PREFIX + type.getName()));
        }
        printPermissions(s, flagPerms, null, null);

        s.append(NL).append("</table>");
        s.append(NL);
        s.append(NL).append("For the flag permissions you can use the flag's aliases as well, I filtered them from this list because it would've become too long, but the permissions are there.");
        s.append(NL).append("For example, <i>recipemanager.flag.modexp</i> and <i>recipemanager.flag.xp</i> both affect the same flag, the @modexp flag, since 'xp' is an alias for 'modexp'.");
        s.append(NL);
        s.append(NL);
        s.append("</pre>");
        s.append("</div>");
        addNav(s, TITLE_COMMANDS_PERMISSIONS);
        appendFooter(s);
        saveAndLog(s, FILE_INFO_COMMANDS);
    }

    private void printPermissions(StringBuilder s, List<Permission> perms, String filter, String exclude) {
        perms.sort(Comparator.comparing(Permission::getName));
        for (Permission p : perms) {
            String name = p.getName();
            if (!name.startsWith("recipemanager.")) {
                continue;
            }

            if (filter != null && !name.contains(filter)) {
                continue;
            }

            if (exclude != null && name.contains(exclude)) {
                continue;
            }

            s.append(NL).append("<tr>");
            s.append("<td>").append(name).append("</td>");
            s.append("<td>");

            switch (p.getDefault()) {
                case TRUE:
                    s.append("Everybody");
                    break;
                case OP:
                    s.append("OP");
                    break;
                case NOT_OP:
                    s.append("Non-OP");
                    break;
                default:
                    s.append("Noone");
            }

            s.append("</td>");
            s.append("<td>").append(p.getDescription()).append("</td>");
            s.append("</tr>");
        }
    }

    private void createNameIndex(boolean overwrite) {
        if (fileExists(FILE_INFO_NAMES, overwrite)) {
            return;
        }

        StringBuilder s = new StringBuilder(24000);

        addHeader(s, TITLE_NAME_INDEX);

        s.append(NL).append("<pre>");
        s.append(NL).append("Data extracted from your server and it may contain names added by other plugins/mods!");
        s.append(NL).append("If you want to update this file just delete it and use '<i>rmreload</i>' or start the server.");
        s.append(NL).append("</pre>");
        s.append(NL).append("<div class='table-of-contents'>");
        s.append(NL).append("<a id='contents'></a><h2 class='table-of-contents__title'>Contents</h2>");
        s.append(NL).append("</div>");
        s.append(NL).append("<pre>");
        s.append(NL).append("- <a href='#bannerpattern'><b>BANNER PATTERN LIST</b></a>");
        s.append(NL).append("- <a href='#biomes'><b>BIOMES LIST</b></a>");
        s.append(NL).append("- <a href='#chatcolor'><b>CHAT COLOR LIST</b></a>");
        s.append(NL).append("- <a href='#dyecolor'><b>DYE COLOR LIST</b></a>");
        s.append(NL).append("- <a href='#enchantment'><b>ENCHANTMENTS LIST</b></a>");
        s.append(NL).append("- <a href='#entitytype'><b>ENTITY TYPE LIST</b></a>");
        s.append(NL).append("- <a href='#fireworkeffect'><b>FIREWORK EFFECT TYPE LIST</b></a>");
        s.append(NL).append("- <a href='#inventory'><b>INVENTORY LIST</b></a>");
        if (Version.has1_13BasicSupport()) {
            s.append(NL).append("- <a href='#itemattribute'><b>ITEM ATTRIBUTE LIST</b></a>");
        }
        s.append(NL).append("- <a href='#material'><b>MATERIAL LIST</b></a>");
        s.append(NL).append("- <a href='#particle'><b>PARTICLE LIST</b></a>");
        s.append(NL).append("- <a href='#potioneffect'><b>POTION EFFECT TYPE LIST</b></a>");
        s.append(NL).append("- <a href='#potiontype'><b>POTION TYPE LIST</b></a>");
        s.append(NL).append("- <a href='#sound'><b>SOUND LIST</b></a>");
        if (Version.has1_13Support()) {
            s.append(NL).append("- <a href='#tag'><b>TAG LIST</b></a>");
        }

        s.append("</pre></div><div class='doc-section__group'><pre>");
        addNameIndexHeading(s, "bannerpattern", "BANNER PATTERN LIST", "block/banner/PatternType", "PatternType");
        for (PatternType p : PatternType.values()) {
            s.append(NL).append(' ').append(p.name());
        }

        s.append(NL).append("</pre></div><div class='doc-section__group'><pre>");
        addNameIndexHeading(s, "biomes", "BIOMES LIST", "block/Biome", "Biome");
        s.append(NL).append(String.format("<b> %-5s %-24s</b>", "ID", "Name"));

        for (Biome b : Biome.values()) {
            s.append(NL).append(String.format(" %-5d %-24s", b.ordinal(), b.name()));
        }

        s.append(NL).append("</pre></div><div class='doc-section__group'><pre>");
        addNameIndexHeading(s, "chatcolor", "CHAT COLOR LIST", "ChatColor", "ChatColor");
        s.append(NL).append(String.format("<b> %-16s %s</b>", "Name", "Color character"));

        for (RMCChatColor c : RMCChatColor.values()) {
            s.append(NL).append(String.format(" %-16s %s", c.name(), c.getChar()));
        }

        s.append(NL).append("</pre></div><div class='doc-section__group'><pre>");
        addNameIndexHeading(s, "dyecolor", "DYE COLOR LIST", "DyeColor", "DyeColor");
        s.append(NL).append(String.format("<b> %-17s %-6s</b>", "", "Color"));
        s.append(NL).append(String.format("<b> %-14s %-4s %-4s %-4s %-12s %s</b>", "Name", "R", "G", " B", "Wool data", "Dye data"));

        for (DyeColor c : DyeColor.values()) {
            s.append(NL).append(String.format(" %-14s %-4d %-4d %-4d %-12d %d", c.name(), c.getColor().getRed(), c.getColor().getGreen(), c.getColor().getBlue(), c.getWoolData(), c.getDyeData()));
        }

        s.append(NL).append("</pre></div><div class='doc-section__group'><pre>");
        addNameIndexHeading(s, "enchantment", "ENCHANTMENTS LIST", "enchantments/Enchantment", "Enchantment");
        if (Version.has1_20_5Support()) {
            s.append(NL).append(String.format("<b> %-26s %s</b>", "Key", "Level range"));

            List<Enchantment> enchantmentsList = new ArrayList<>();
            for (Enchantment enchantment : Registry.ENCHANTMENT) {
                enchantmentsList.add(enchantment);
            }

            enchantmentsList.sort(Comparator.comparing(e -> e.getKey().getKey()));

            for (Enchantment e : enchantmentsList) {
                // TODO: Figure out the replacement for Enchantment.getItemTarget()

                s.append(NL).append(String.format(" %-26s %s", e.getKey().getKey(), e.getStartLevel() + " to " + e.getMaxLevel()));
            }
        } else if (Version.has1_13Support()) {
            s.append(NL).append(String.format("<b> %-26s %-12s %s</b>", "Key", "Item type", "Level range"));

            List<Enchantment> enchantments = Arrays.asList(Enchantment.values());

            enchantments.sort(Comparator.comparing(e -> e.getKey().getKey()));

            for (Enchantment e : enchantments) {
                EnchantmentTarget target = e.getItemTarget();

                String targetString;
                if (target == null) { // Just in case a custom enchantment is missing the enchantment target
                    targetString = "any";
                } else {
                    targetString = target.toString().toLowerCase();
                }
                s.append(NL).append(String.format(" %-26s %-14s %s", e.getKey().getKey(), targetString, e.getStartLevel() + " to " + e.getMaxLevel()));
            }
        } else { // Key didn't exist yet in 1.12
            s.append(NL).append(String.format(" %-26s %-14s %s", "Name", "Item type", "Level range"));

            List<Enchantment> enchantments = Arrays.asList(Enchantment.values());

            enchantments.sort(Comparator.comparing(Enchantment::getName));

            for (Enchantment e : enchantments) {
                EnchantmentTarget target = e.getItemTarget();

                String targetString = target.toString().toLowerCase();

                s.append(NL).append(String.format(" %-26s %-12s %s", e.getName(), targetString, e.getStartLevel() + " to " + e.getMaxLevel()));
            }
        }

        s.append(NL).append("</pre></div><div class='doc-section__group'><pre>");
        addNameIndexHeading(s, "entitytype", "ENTITY TYPE LIST", "entity/EntityType", "EntityType");
        s.append(NL).append(String.format("<b> %-5s %-24s %-24s %s</b>", "ID", "Constant", "Name", "Alive ?"));

        for (EntityType e : EntityType.values()) {
            if (e.getTypeId() > 0) {
                s.append(NL).append(String.format(" %-5s %-24s %-24s %s", e.getTypeId(), e.name(), e.getName(), e.isAlive()));
            }
        }

        s.append(NL).append("</pre></div><div class='doc-section__group'><pre>");
        addNameIndexHeading(s, "fireworkeffect", "FIREWORK EFFECT TYPE LIST", "FireworkEffect.Type", "Firework Effect Type");

        for (FireworkEffect.Type t : FireworkEffect.Type.values()) {
            s.append(NL).append(' ').append(t.toString());
        }

        s.append(NL).append("</pre></div><div class='doc-section__group'><pre>");
        addNameIndexHeading(s, "inventory", "INVENTORY LIST", "event/inventory/InventoryType", "Inventory Type");

        for (InventoryType t : InventoryType.values()) {
            s.append(NL).append(' ').append(t.toString());
        }

        if (Version.has1_13BasicSupport()) {
            s.append(NL).append("</pre></div><div class='doc-section__group'><pre>");
            addNameIndexHeading(s, "itemattribute", "ITEM ATTRIBUTE LIST", "attribute/Attribute", "Attribute Type");

            for (Attribute a : Attribute.values()) {
                s.append(NL).append(' ').append(a.toString());
            }
        }

        s.append(NL).append("</pre></div><div class='doc-section__group'><pre>");
        addNameIndexHeading(s, "material", "MATERIAL LIST", "Material", "Material");
        s.append("Data/damage/durability values are listed at <a href='https://minecraft.wiki/w/Java_Edition_data_values'>Minecraft Wiki / Data Values</a>");
        s.append(NL);
        s.append(NL).append(String.format("<b> %-34s %-34s %-5s  %-14s  %-5s %-4s</b>", "Name", "Alias", "Stack", "Max durability", "Block", "Item"));

        for (Material m : Material.values()) {
            String alias = RecipeManager.getSettings().getMaterialPrint(m);

            String aliasString;
            if (alias == null) {
                aliasString = "";
            } else {
                aliasString = alias;
            }

            String durabilityString = "";
            if (m.getMaxDurability() != 0) {
                durabilityString += m.getMaxDurability();
            }

            String block = "";
            if (m.isBlock()) {
                block = "Block";
            }

            String item = "";
            if (m.isItem()) {
                item = "Item";
            }

            s.append(NL).append(String.format(" %-34s %-34s %-5d  %-14s   %-5s %-4s", m, aliasString, m.getMaxStackSize(), durabilityString, block, item));
        }

        s.append(NL).append("</pre></div><div class='doc-section__group'><pre>");
        addNameIndexHeading(s, "particle", "PARTICLE LIST", "Particle", "Particle");

        for (Particle p : Particle.values()) {
            s.append(NL).append(' ').append(p.name());
        }

        s.append(NL).append("</pre></div><div class='doc-section__group'><pre>");
        addNameIndexHeading(s, "potioneffect", "POTION EFFECT TYPE LIST", "potion/PotionEffect", "PotionEffect");
        s.append(NL).append(String.format("<b> %-5s %-24s %-10s</b>", "ID", "Name", "Instant ?"));

        for (PotionEffectType t : PotionEffectType.values()) {
            if (t != null) {
                s.append(NL).append(String.format(" %-5d %-24s %-10s", t.getId(), t.getName(), t.isInstant()));
            }
        }

        s.append(NL);
        s.append(NL).append("NOTE: The duration is compensated when setting potions in flags, so when using 2 seconds it will last 2 seconds regardless of effect type.");
        s.append(NL);
        s.append(NL).append("More about potions, effects and custom effects: <a href=\"https://minecraft.wiki/w/Effect\">https://minecraft.wiki/w/Effect</a>");

        s.append(NL).append("</pre></div><div class='doc-section__group'><pre>");
        addNameIndexHeading(s, "potiontype", "POTION TYPE LIST", "potion/PotionType", "PotionType");
        s.append(NL).append(String.format("<b> %-5s %-24s %-10s %-10s %-16s</b>", "ID", "Name", "Instant ?", "Max level", "Effect type"));

        for (PotionType t : PotionType.values()) {
            if (t != null) {
                String effectType;
                if (t.getEffectType() == null) {
                    effectType = "";
                } else {
                    effectType = t.getEffectType().getName();
                }
                s.append(NL).append(String.format(" %-5d %-24s %-10s %-10d %-16s", t.ordinal(), t, t.isInstant(), t.getMaxLevel(), effectType));
            }
        }

        s.append(NL).append("</pre></div><div class='doc-section__group'><pre>");
        addNameIndexHeading(s, "sound", "SOUND LIST", "Sound", "Sound");
        Sound[] sounds = Sound.values();

        int soundsLength = sounds.length;
        for (int i = 0; i < soundsLength; i += 4) {
            String sounds1 = "";
            String sounds2 = "";
            String sounds3 = "";

            if (i + 1 < soundsLength) {
                sounds1 = sounds[i + 1].name();
            }
            if (i + 2 < soundsLength) {
                sounds2 = sounds[i + 2].name();
            }

            if (i + 3 < soundsLength) {
                sounds3 = sounds[i + 3].name();
            }
            s.append(NL).append(String.format(" %-46s%-46s%-46s%s", sounds[i].name(), sounds1, sounds2, sounds3));
        }

        if (Version.has1_13Support()) {
            s.append(NL).append("</pre></div><div class='doc-section__group'><pre>");
            addNameIndexHeading(s, "tag", "TAG LIST", "Tag", "Tag");

            displayTags(s, "Blocks", REGISTRY_BLOCKS);
            s.append(NL);
            displayTags(s, "Items", REGISTRY_ITEMS);
        }

        s.append(NL).append("</pre>");
        s.append(NL).append("</div></div>");
        addNav(s, TITLE_NAME_INDEX);
        appendFooter(s);
        saveAndLog(s, FILE_INFO_NAMES);
    }

    private void displayTags(StringBuilder s, String name, String tagType) {
        s.append(NL).append("<b>").append(name).append("</b>");
        Iterable<Tag<Material>> blockTags = Bukkit.getTags(tagType, Material.class);
        Map<String, String> sortedTags = new TreeMap<>();
        for (Tag<Material> tag : blockTags) {
            Set<Material> tags = tag.getValues();
            List<String> materials = new ArrayList<>();
            for (Material material : tags) {
                materials.add(material.getKey().getKey().toUpperCase());
            }

            Collections.sort(materials);
            sortedTags.put(tag.getKey().toString(), materials.toString());
        }

        for (Map.Entry<String, String> entry : sortedTags.entrySet()) {
            s.append(NL).append(String.format(" %-36s %s", entry.getKey(), entry.getValue()));
        }
    }

    private void appendFooter(StringBuilder s) {
        s.append(NL).append("<footer class='footer'><div class='container'><div class='footer__links'>");
        s.append(NL).append("<a class='footer__link' href='https://dev.bukkit.org/projects/recipemanager'><img class='logo-bukkitdev' src='https://www.recipemanager.dev/img/logos/bukkit-dev.png?v=1' alt='BukkitDev'/></a>");
        s.append(NL).append("<a class='footer__link' href='https://discordapp.com/invite/3JY9JC3'><img class='logo-discord' src='https://www.recipemanager.dev/img/logos/discord.png?v=1' alt='Discord'/></a>");
        s.append(NL).append("<a class='footer__link' href='https://github.com/haveric/RecipeManager2'><img class='logo-github' src='https://www.recipemanager.dev/img/logos/github-white.png?v=1' alt='Github'/></a>");
        s.append(NL).append("</div></div></footer>");
        s.append(NL).append("</body>").append(NL).append("</html>");
    }

    private void saveAndLog(StringBuilder s, String file) {
        Tools.saveTextToFile(s.toString(), RecipeManager.getPlugin().getDataFolder() + File.separator + file);
        MessageSender.getInstance().sendAndLog(sender, RMCChatColor.GREEN + "Generated '" + file + "' file.");
    }

    private void addNameIndexHeading(StringBuilder s, String name, String title, String partialUrl, String urlTitle) {
        s.append("<a id='").append(name).append("'></a><a href='#contents' class='back-to-top'>^ Contents</a><h3>").append(title).append("</h3>");
        s.append("<a href='").append(BUKKIT_DOCS).append(partialUrl).append(".html'>BukkitAPI / ").append(urlTitle).append("</a>");
        s.append(NL);
    }
}
