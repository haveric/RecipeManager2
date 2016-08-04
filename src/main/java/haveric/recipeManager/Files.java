package haveric.recipeManager;

import com.google.common.collect.Sets;
import haveric.recipeManager.flags.FlagBit;
import haveric.recipeManager.flags.FlagDescriptor;
import haveric.recipeManager.flags.FlagFactory;
import haveric.recipeManager.messages.MessageSender;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.Version;
import haveric.recipeManagerCommon.RMCChatColor;
import haveric.recipeManagerCommon.util.RMCUtil;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.banner.PatternType;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.EntityType;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

public class Files {
    public static final String NL = System.getProperty("line.separator");

    private final CommandSender sender;

    public static final String LASTCHANGED_CONFIG = "2.8";
    public static final String LASTCHANGED_MESSAGES = "2.7.3";
    public static final String LASTCHANGED_ITEM_DATAS = "2.7";
    public static final String LASTCHANGED_ITEM_ALIASES = "2.4";
    public static final String LASTCHANGED_ENCHANT_ALIASES = "2.7.3";

    public static final String FILE_CONFIG = "config.yml";
    public static final String FILE_MESSAGES = "messages.yml";

    public static final String FILE_ITEM_DATAS = "item datas.yml";
    public static final String FILE_ITEM_ALIASES = "item aliases.yml";
    public static final String FILE_ENCHANT_ALIASES = "enchant aliases.yml";

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

    protected static void init() {
    }

    protected static void reload(CommandSender sender) {
        new Files(sender);
    }

    private Files(CommandSender newSender) {
        sender = newSender;

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

    private boolean isNewVersion() {
        boolean newVersion = true;

        try {
            File file = new File(RecipeManager.getPlugin().getDataFolder() + File.separator + FILE_USED_VERSION);
            String currentVersion = RecipeManager.getPlugin().getDescription().getVersion();

            if (file.exists()) {
                BufferedReader b = new BufferedReader(new FileReader(file));
                String version = b.readLine();
                b.close();
                newVersion = (version == null || !version.equals(currentVersion));
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
        File file = new File(RecipeManager.getPlugin().getDataFolder() + File.separator + "recipes" + File.separator + "disabled");
        file.mkdirs();

        // Create disable directory info file
        file = new File(file.getPath() + File.separator + "Recipe files in here are ignored!");

        if (!file.exists()) {
            Tools.saveTextToFile("In the disabled folder you can place recipe files you don't want to load, instead of deleting them.", file.getPath());
        }
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

    private void createRecipeFlags(boolean overwrite) {
        if (fileExists(FILE_INFO_FLAGS, overwrite)) {
            return;
        }

        StringBuilder s = new StringBuilder(32000);
        Map<String, List<FlagDescriptor>> flags = new LinkedHashMap<>();

        String[] category = new String[] { "SHARED FLAGS", "RECIPE ONLY FLAGS", "RESULT ONLY FLAGS" };
        String[] description = new String[] { "Usable on anything - file header, recipe header or result items.", "Usable only on file headers or recipe headers. Can not be used on result items.", "Usable only on recipe's result items. Can not be used on recipes or file header." };

        int size = FlagFactory.getInstance().getFlags().values().size();

        for (String c : category) {
            flags.put(c, new ArrayList<FlagDescriptor>(size));
        }

        for (FlagDescriptor flag : FlagFactory.getInstance().getFlags().values()) {
            if (flag.hasBit(FlagBit.RECIPE)) {
                flags.get(category[1]).add(flag);
            } else if (flag.hasBit(FlagBit.RESULT)) {
                flags.get(category[2]).add(flag);
            } else {
                flags.get(category[0]).add(flag);
            }
        }

        s.append("<title>Recipe Flags</title><pre style='font-family:Lucida Console;font-size:16px;width:100%;'>");
        s.append(NL).append("<a href='basic recipes.html'>Basic Recipes</a> | <a href='advanced recipes.html'>Advanced Recipes</a> | <b>Recipe Flags</b> | <a href='recipe books.html'>Recipe Books</a> | <a href='name index.html'>Name Index</a> | <a href='commands & permissions.html'>Commands &amp; Permissions</a>");
        s.append(NL).append("<h1>Recipe flags</h1>");
        s.append(NL);
        s.append(NL).append("<b>WHAT ARE FLAGS ?</b>");
        s.append(NL).append("  Flags are the stuff that make a recipe very special! You can add various features to a recipe by using flags.");
        s.append(NL).append("  For examples see <a href='advanced recipes.html'><b>advanced recipes.html</b></a>.");
        s.append(NL);
        s.append(NL).append("<b>USING FLAGS</b>");
        s.append(NL).append("  Flags can be added in 3 'zones':");
        s.append(NL).append("  - at the beginning of the file - which are copied to all recipes from that file");
        s.append(NL).append("  - after recipe type (CRAFT, COMBINE, etc) - where they affect that specific recipe, you may even overwrite file flags for that specific recipe!");
        s.append(NL).append("  - after recipe's individual results - to apply flags for the result items.");
        s.append(NL);
        s.append(NL).append("<b>ABOUT ARGUMENTS</b>");
        s.append(NL).append("  Flags have arguments but not always are they all required.");
        s.append(NL).append("  Arguments enclosed between &lt; and &gt; are required and those enclosed between [ and ] are optional.");
        s.append(NL).append("  Some arguments may have 'or false', that means you can just type false in there to make it do something special (most likely disable the flag or a feature)");
        s.append(NL);
        s.append(NL).append("<b>ALIASES</b>");
        s.append(NL).append("  They're just other names for the flag that you can use, they have no special effect if used, only for your preference.");
        s.append(NL);
        s.append(NL);
        s.append(NL);
        s.append("<hr>");
        s.append(NL);
        s.append(NL).append("<a name='contents'></a><h3>CONTENTS</h3>");

        for (String c : category) {
            String key = c.replace(' ', '_').toLowerCase();

            s.append(NL).append("<a href='#").append(key).append("'><b>").append(c).append("</b></a>");

            for (FlagDescriptor flag : flags.get(c)) {
                s.append(NL).append("- <a href='#").append(flag.getName()).append("'><b>").append(flag.getNameDisplay()).append("</b></a>");
            }

            s.append(NL);
        }

        s.append(NL);

        int categoryLength = category.length;
        for (int t = 0; t < categoryLength; t++) {
            String key = category[t].replace(' ', '_').toLowerCase();

            s.append(NL).append("<a name='").append(key).append("'></a><hr>  <b>").append(category[t]).append("</b>");
            s.append(NL).append("    ").append(description[t]);

            for (FlagDescriptor flag : flags.get(category[t])) {
                String[] args = flag.getArguments();
                String[] desc = flag.getDescription();
                String[] ex = flag.getExamples();

                s.append(NL);
                s.append("<hr><a href='#contents' style='font-size:12px;'>^ Contents</a><a name='").append(flag.getName()).append("'></a>");
                s.append(NL);
                s.append(NL);

                if (args != null) {
                    for (String a : args) {
                        s.append(NL).append("  <b>").append(StringEscapeUtils.escapeHtml(a.replace("{flag}", flag.toString()))).append("</b>");
                    }
                }

                if (desc == null) {
                    desc = new String[] { "Flag not yet documented...", };
                }

                s.append(NL);

                for (String d : desc) {
                    s.append(NL);

                    if (d != null) {
                        s.append("    ").append(StringEscapeUtils.escapeHtml(d));
                    }
                }

                if (!flag.hasBit(FlagBit.NO_FALSE)) {
                    s.append(NL).append(NL).append("    Setting to 'false' or 'remove' will disable the flag.");
                }

                if (ex != null) {
                    s.append(NL).append(NL).append("    <b>Examples:</b>");

                    for (String e : ex) {
                        s.append(NL).append("      ").append(StringEscapeUtils.escapeHtml(e.replace("{flag}", flag.toString())));
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

                s.append(NL);
                s.append(NL);
            }

            s.append(NL);
        }

        Tools.saveTextToFile(s.toString(), RecipeManager.getPlugin().getDataFolder() + File.separator + FILE_INFO_FLAGS);

        MessageSender.getInstance().sendAndLog(sender, RMCChatColor.GREEN + "Generated '" + FILE_INFO_FLAGS + "' file.");
    }

    private void createCommands(boolean overwrite) {
        if (fileExists(FILE_INFO_COMMANDS, overwrite)) {
            return;
        }

        StringBuilder s = new StringBuilder();

        s.append("<title>Commands &amp; permissions</title><pre style='font-family:Lucida Console;font-size:16px;width:100%;'>");
        s.append(NL).append("<a href='basic recipes.html'>Basic Recipes</a> | <a href='advanced recipes.html'>Advanced Recipes</a> | <a href='recipe flags.html'>Recipe Flags</a> | <a href='recipe books.html'>Recipe Books</a> | <a href='name index.html'>Name Index</a> | <b>Commands &amp; Permissions</b>");
        s.append(NL).append("<h1>Commands &amp; permissions</h1>");
        s.append(NL);
        s.append(NL);
        s.append(NL).append("<h2>Commands</h2>");
        s.append("<table style='border-collapse:collapse;' border='1' cellpadding='5'>");

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
            s.append("<td width='40%'><b>");
            s.append(StringEscapeUtils.escapeHtml(usage)).append("</b><span style='font-size:14px;'>");
            s.append("<br>Permission: ").append(permission);
            s.append("<br>Aliases: ").append(aliasesString);
            s.append("</span></td>");
            s.append("<td>").append(StringEscapeUtils.escapeHtml(info)).append("</td>");
            s.append("</tr>");
        }

        s.append(NL).append("</table>");
        s.append(NL);
        s.append(NL);
        s.append(NL).append("<h2>Permissions</h2>");
        s.append("<table style='border-collapse:collapse;' border='1' cellpadding='5'>");
        s.append(NL).append("<tr>");
        s.append("<th>Permission node</th>");
        s.append("<th>Defaulted to</th>");
        s.append("<th>Description</th>");
        s.append("</tr>");

        List<Permission> permissions = desc.getPermissions();
        List<Permission> perms = new ArrayList<>(permissions.size() + FlagFactory.getInstance().getFlags().size());

        perms.addAll(permissions);

        perms.add(Bukkit.getPluginManager().getPermission(Perms.FLAG_PREFIX + "*"));

        for (FlagDescriptor type : FlagFactory.getInstance().getFlags().values()) {
            if (type.hasBit(FlagBit.NO_SKIP_PERMISSION)) {
                continue;
            }

            perms.add(Bukkit.getPluginManager().getPermission(Perms.FLAG_PREFIX + type.getName()));
        }

        for (Permission p : perms) {
            if (!p.getName().startsWith("recipemanager.")) {
                continue;
            }

            s.append(NL).append("<tr>");
            s.append("<td>").append(p.getName()).append("</td>");
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

        s.append(NL).append("</table>");
        s.append(NL);
        s.append(NL).append("For the flag permissions you can use the flag's aliases as well, I filtered them from this list because it would've become too long, but the permissions are there.");
        s.append(NL).append("For example, <i>recipemanager.flag.modexp</i> and <i>recipemanager.flag.xp</i> both affect the same flag, the @modexp flag, since 'xp' is an alias for 'modexp'.");
        s.append(NL);
        s.append(NL);
        s.append("</pre>");

        Tools.saveTextToFile(s.toString(), RecipeManager.getPlugin().getDataFolder() + File.separator + FILE_INFO_COMMANDS);

        MessageSender.getInstance().sendAndLog(sender, RMCChatColor.GREEN + "Generated '" + FILE_INFO_COMMANDS + "' file.");
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
        s.append(NL).append("- <a href='#material'><b>MATERIAL LIST</b></a>");
        s.append(NL).append("- <a href='#enchantment'><b>ENCHANTMENTS LIST</b></a>");
        s.append(NL).append("- <a href='#potiontype'><b>POTION TYPE LIST</b></a>");
        s.append(NL).append("- <a href='#potioneffect'><b>POTION EFFECT TYPE LIST</b></a>");
        s.append(NL).append("- <a href='#fireworkeffect'><b>FIREWORK EFFECT TYPE LIST</b></a>");
        s.append(NL).append("- <a href='#biomes'><b>BIOMES LIST</b></a>");
        s.append(NL).append("- <a href='#sound'><b>SOUND LIST</b></a>");
        s.append(NL).append("- <a href='#entitytype'><b>ENTITY TYPE LIST</b></a>");
        s.append(NL).append("- <a href='#dyecolor'><b>DYE COLOR LIST</b></a>");
        s.append(NL).append("- <a href='#chatcolor'><b>CHAT COLOR LIST</b></a>");

        if (Version.has18Support()) {
            s.append(NL).append("- <a href='#bannerpattern'><b>BANNER PATTERN LIST</b></a>");
        }

        s.append(NL);
        s.append(NL);
        s.append(NL).append("<hr>");
        s.append(NL);
        s.append(NL).append("<a name='material'></a><a href='#contents'>^ Contents</a><h3>MATERIAL LIST</h3>");
        s.append("<a href='" + BUKKIT_DOCS + "Material.html'>BukkitAPI / Material</a>");
        s.append(NL).append("Data/damage/durability values are listed at <a href='http://www.minecraftwiki.net/wiki/Data_value#Data'>Minecraft Wiki / Data Value</a>");
        s.append(NL);
        s.append(NL).append(String.format(" %-5s %-24s %-24s %-5s %s", "ID", "Name", "Alias", "Stack", "Max durability"));

        for (Material m : Material.values()) {
            String alias = Settings.getInstance().getMaterialPrint(m);

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

            s.append(NL).append(String.format(" %-5d %-24s %-24s %-5d %s", m.getId(), m.toString(), aliasString, m.getMaxStackSize(), durabilityString));
        }

        s.append(NL);
        s.append(NL);
        s.append(NL).append("<a name='enchantment'></a><a href='#contents'>^ Contents</a><h3>ENCHANTMENTS LIST</h3>");
        s.append("<a href='" + BUKKIT_DOCS + "enchantments/Enchantment.html'>BukkitAPI / Enchantment</a>");
        s.append(NL);
        s.append(NL).append(String.format(" %-5s %-26s %-24s %-12s %s", "ID", "Name", "Alias", "Item type", "Level range"));

        List<Enchantment> enchantments = Arrays.asList(Enchantment.values());

        Collections.sort(enchantments, new Comparator<Enchantment>() {
            public int compare(Enchantment e1, Enchantment e2) {
                int compare;
                if (e1.getId() > e2.getId()) {
                    compare = 1;
                } else {
                    compare = -1;
                }
                return compare;
            }
        });

        for (Enchantment e : enchantments) {
            EnchantmentTarget target = e.getItemTarget();
            if (target == null) {
                // Fall back to all if the target is null.
                target = EnchantmentTarget.ALL;
            }
            s.append(NL).append(String.format(" %-5d %-26s %-24s %-12s %s", e.getId(), e.getName(), Settings.getInstance().getEnchantPrint(e), target.toString().toLowerCase(), e.getStartLevel() + " to " + e.getMaxLevel()));
        }

        s.append(NL);
        s.append(NL);
        s.append(NL).append("<a name='potiontype'></a><a href='#contents'>^ Contents</a><h3>POTION TYPE LIST</h3>");
        s.append("<a href='" + BUKKIT_DOCS + "potion/PotionType.html'>BukkitAPI / PotionType</a>");
        s.append(NL);
        s.append(NL).append(String.format(" %-5s %-24s %-10s %-10s %-16s %s", "ID", "Name", "Instant ?", "Max level", "Effect type", "Data value"));

        for (PotionType t : PotionType.values()) {
            if (t != null) {
                String effectType;
                if (t.getEffectType() == null) {
                    effectType = "";
                } else {
                    effectType = t.getEffectType().getName();
                }
                s.append(NL).append(String.format(" %-5d %-24s %-10s %-10d %-16s %d", t.ordinal(), t.toString(), t.isInstant(), t.getMaxLevel(), effectType, t.getDamageValue()));
            }
        }

        s.append(NL);
        s.append(NL);
        s.append(NL).append("<a name='potioneffect'></a><a href='#contents'>^ Contents</a><h3>POTION EFFECT TYPE LIST</h3>");
        s.append("<a href='" + BUKKIT_DOCS + "potion/PotionEffect.html'>BukkitAPI / PotionEffect</a>");
        s.append(NL);
        s.append(NL).append(String.format(" %-5s %-24s %-10s %s", "ID", "Name", "Instant ?", "Duration modifier"));

        for (PotionEffectType t : PotionEffectType.values()) {
            if (t != null) {
                s.append(NL).append(String.format(" %-5d %-24s %-10s %.2f", t.getId(), t.getName(), t.isInstant(), t.getDurationModifier()));
            }
        }

        s.append(NL);
        s.append(NL).append("NOTE: The duration is compensated when setting potions in flags, so when using 2 seconds it will last 2 seconds regardless of effect type.");
        s.append(NL);
        s.append(NL).append("More about potions, effects and custom effects: http://www.minecraftwiki.net/wiki/Potion_effects");
        s.append(NL);
        s.append(NL);
        s.append(NL).append("<a name='fireworkeffect'></a><a href='#contents'>^ Contents</a><h3>FIREWORK EFFECT TYPE LIST</h3>");
        s.append("<a href='" + BUKKIT_DOCS + "FireworkEffect.Type.html'>BukkitAPI / FireworkEffect.Type</a>");
        s.append(NL);

        for (FireworkEffect.Type t : FireworkEffect.Type.values()) {
            s.append(NL).append(' ').append(t.toString());
        }

        s.append(NL);
        s.append(NL);
        s.append(NL).append("<a name='biomes'></a><a href='#contents'>^ Contents</a><h3>BIOMES LIST</h3>");
        s.append("<a href='" + BUKKIT_DOCS + "block/Biome.html'>BukkitAPI / Biome</a>");
        s.append(NL);
        s.append(NL).append(String.format(" %-5s %-24s", "ID", "Name"));

        for (Biome b : Biome.values()) {
            s.append(NL).append(String.format(" %-5d %-24s", b.ordinal(), b.name()));
        }

        s.append(NL);
        s.append(NL);
        s.append(NL).append("<a name='sound'></a><a href='#contents'>^ Contents</a><h3>SOUND LIST</h3>");
        s.append("<a href='" + BUKKIT_DOCS + "Sound.html'>BukkitAPI / Sound</a>");
        s.append(NL);

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
            s.append(NL).append(String.format(" %-24s%-24s%-24s%s", sounds[i].name(), sounds1, sounds2, sounds3));
        }

        s.append(NL);
        s.append(NL);
        s.append(NL).append("<a name='entitytype'></a><a href='#contents'>^ Contents</a><h3>ENTITY TYPE LIST</h3>");
        s.append("<a href='" + BUKKIT_DOCS + "entity/EntityType.html'>BukkitAPI / EntityType</a>");
        s.append(NL);
        s.append(NL).append(String.format(" %-5s %-24s %-24s %s", "ID", "Constant", "Name", "Alive ?"));

        for (EntityType e : EntityType.values()) {
            if (e.getTypeId() > 0) {
                s.append(NL).append(String.format(" %-5s %-24s %-24s %s", e.getTypeId(), e.name(), e.getName(), e.isAlive()));
            }
        }

        s.append(NL);
        s.append(NL);
        s.append(NL).append("<a name='dyecolor'></a><a href='#contents'>^ Contents</a><h3>DYE COLOR LIST</h3>");
        s.append("<a href='" + BUKKIT_DOCS + "DyeColor.html'>BukkitAPI / DyeColor</a>");
        s.append(NL);
        s.append(NL).append(String.format(" %-16s %-12s %-12s %s", "Name", "Color R G B", "Wool data", "Dye data"));

        for (DyeColor c : DyeColor.values()) {
            s.append(NL).append(String.format(" %-14s %-4d %-4d %-4d %-12d %d", c.name(), c.getColor().getRed(), c.getColor().getGreen(), c.getColor().getBlue(), c.getWoolData(), c.getDyeData()));
        }

        s.append(NL);
        s.append(NL);
        s.append(NL).append("<a name='chatcolor'></a><a href='#contents'>^ Contents</a><h3>CHAT COLOR LIST</h3>");
        s.append("<a href='" + BUKKIT_DOCS + "RMCChatColor.html'>BukkitAPI / ChatColor</a>");
        s.append(NL);
        s.append(NL).append(String.format(" %-16s %s", "Name", "Color character"));

        for (RMCChatColor c : RMCChatColor.values()) {
            s.append(NL).append(String.format(" %-16s %s", c.name(), c.getChar()));
        }

        if (Version.has18Support()) {
            s.append(NL);
            s.append(NL);
            s.append(NL).append("<a name='bannerpattern'></a><a href='#contents'>^ Contents</a><h3>BANNER PATTERN LIST</h3>");
            s.append("<a href='" + BUKKIT_DOCS + "block/banner/PatternType.html'>BukkitAPI / PatternType</a>");
            s.append(NL);

            for (PatternType p : PatternType.values()) {
                s.append(NL).append(' ').append(p.name());
            }
        }

        s.append(NL);
        s.append(NL);
        s.append(NL).append("</pre>");

        Tools.saveTextToFile(s.toString(), RecipeManager.getPlugin().getDataFolder() + File.separator + FILE_INFO_NAMES);

        MessageSender.getInstance().sendAndLog(sender, RMCChatColor.GREEN + "Generated '" + FILE_INFO_NAMES + "' file.");
    }
}
