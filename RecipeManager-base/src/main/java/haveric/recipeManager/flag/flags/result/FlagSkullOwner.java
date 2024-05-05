package haveric.recipeManager.flag.flags.result;

import com.google.common.collect.ObjectArrays;
import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.recipes.FlaggableRecipeChoice;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.tools.Supports;
import haveric.recipeManager.tools.ToolsRecipeChoice;
import haveric.recipeManager.tools.Version;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

public class FlagSkullOwner extends Flag {

    private final String MINECRAFT_TEXTURE_URL = "https://textures.minecraft.net/texture/";
    @Override
    public String getFlagType() {
        return FlagType.SKULL_OWNER;
    }

    @Override
    protected String[] getArguments() {
        String[] arguments = new String[]{
            "{flag} <uuid>",
            "{flag} <name>",
        };

        if (Supports.playerProfile()) {
            arguments = ObjectArrays.concat(arguments, new String[]{
                "{flag} textureurl <url>",
                "{flag} <name> | textureurl <url>",
                "{flag} <uuid> | textureurl <url>",
            }, String.class);
        } else {
            arguments = ObjectArrays.concat(arguments, new String[]{
                "{flag} texture <base64>",
                "{flag} <uuid> | texture <base64>",
                "{flag} <name> | texture <base64>",
            }, String.class);
        }

        return arguments;
    }

    @Override
    protected String[] getDescription() {
        String[] description = new String[]{
            "Sets the human skull's owner to apply the skin.",
            "If you set it to '{player}' then it will use crafter's name.",
            "",
        };

        if (Supports.playerProfile()) {
            description = ObjectArrays.concat(description, new String[]{
                "For texture, you can reference https://minecraft-heads.com/, https://mineskin.org/ or any other Minecraft head repository",
                "  You can only use textures that are located on the Minecraft texture server (" + MINECRAFT_TEXTURE_URL + "). Each of the above sites should be able to provide those",
                "",
                "  NOTE: You should exclude the base texture url in the arguments",
                "  Example texture url: " + MINECRAFT_TEXTURE_URL + "c0b8b5889ee1c6388dc6c2c5dbd70b6984aefe54319a095e64db7638097b821",
                "    Value you'd use: c0b8b5889ee1c6388dc6c2c5dbd70b6984aefe54319a095e64db7638097b821",
            }, String.class);
        } else {
            description = ObjectArrays.concat(description, new String[]{
                "For textureurl, you can reference https://minecraft-heads.com/, https://mineskin.org/ or any other Minecraft head repository",
                "  You can only use the base64 encoded string of a valid mojang texture. Each of the above sites should be able to provide those",
                "",
                "  WARNING: The texture parameter will conflict with " + FlagType.ITEM_NBT + " and whichever is used last will be the one that gets used.",
            }, String.class);
        }

        return description;
    }

    @Override
    protected String[] getExamples() {
        String[] examples = new String[]{
            "{flag} Notch",
            "{flag} {player}",
        };

        if (Supports.playerProfile()) {
            examples = ObjectArrays.concat(examples, new String[]{
                "{flag} textureurl c0b8b5889ee1c6388dc6c2c5dbd70b6984aefe54319a095e64db7638097b821 // Jam texture",
            }, String.class);
        } else {
            examples = ObjectArrays.concat(examples, new String[]{
                "{flag} texture eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzBiOGI1ODg5ZWUxYzYzODhkYzZjMmM1ZGJkNzBiNjk4NGFlZmU1NDMxOWEwOTVlNjRkYjc2MzgwOTdiODIxIn19fQ== // Jam texture",
            }, String.class);
        }

        return examples;
    }


    private String owner;
    private UUID ownerUUID;
    private String textureBase64;
    private URL textureURL;

    public FlagSkullOwner() {
    }

    public FlagSkullOwner(FlagSkullOwner flag) {
        super(flag);
        owner = flag.owner;
        ownerUUID = flag.ownerUUID;
        textureBase64 = flag.textureBase64;
        textureURL = flag.textureURL;
    }

    @Override
    public FlagSkullOwner clone() {
        return new FlagSkullOwner((FlagSkullOwner) super.clone());
    }

    public boolean hasOwner() {
        return owner != null;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String newOwner) {
        owner = newOwner;
    }

    public boolean isOwnerDynamicPlayer() {
        return owner != null && owner.equalsIgnoreCase("{player}");
    }

    public boolean hasOwnerUUID() {
        return ownerUUID != null;
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public void setOwnerUUID(UUID newOwnerUUID) {
        ownerUUID = newOwnerUUID;
    }

    public String getTextureBase64() {
        return textureBase64;
    }

    public void setTextureBase64(String base64) {
        textureBase64 = base64;
    }

    public boolean hasTextureBase64() {
        return textureBase64 != null;
    }

    public URL getTextureURL() {
        return textureURL;
    }

    public void setTextureURL(URL url) {
        textureURL = url;
    }

    public boolean hasTextureURL() {
        return textureURL != null;
    }

    @Override
    public boolean onValidate() {
        ItemResult result = getResult();
        boolean validResult = false;
        if (result != null && (result.getItemMeta() instanceof SkullMeta)) {
            validResult = true;
        }

        boolean validFlaggable = false;
        FlaggableRecipeChoice flaggableRecipeChoice = getFlaggableRecipeChoice();
        if (flaggableRecipeChoice != null && ToolsRecipeChoice.isValidMetaType(flaggableRecipeChoice.getChoice(), SkullMeta.class)) {
            validFlaggable = true;
        }

        if (!validResult && !validFlaggable) {
            return ErrorReporter.getInstance().error("Flag " + getFlagType() + " needs a PLAYER_HEAD");
        }

        return true;
    }

    @Override
    public boolean onParse(String value, String fileName, int lineNum, int restrictedBit) {
        super.onParse(value, fileName, lineNum, restrictedBit);
        String[] args = value.split("\\|");

        for (String arg : args) {
            arg = arg.trim();

            String argLower = arg.toLowerCase();
            if (Supports.playerProfile() && argLower.startsWith("textureurl")) {
                String urlString;
                String textureString = arg.substring("textureurl".length()).trim();
                if (textureString.startsWith("http")) {
                    urlString = textureString;
                } else {
                    urlString = MINECRAFT_TEXTURE_URL + textureString;
                }

                try {
                    textureURL = new URL(urlString);
                } catch (MalformedURLException e) {
                    return ErrorReporter.getInstance().error("Flag " + getFlagType() + " has an invalid url: " + urlString);
                }
            } else if (argLower.startsWith("texture")) {
                textureBase64 = arg.substring("texture".length()).trim();
                if (Supports.playerProfile()) {
                    ErrorReporter.getInstance().warning("Flag " + getFlagType() + " is using texture argument.", "`texture <base64>` should be replaced with `textureurl <url>` for better support.");
                }
            } else {
                String[] components = arg.split("-");
                if (components.length == 5) {
                    ownerUUID = UUID.fromString(arg);
                } else {
                    owner = arg;
                }
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
        if (!a.hasResult()) {
            a.addCustomReason("Needs result!");
            return;
        }

        String ownerString = null;
        OfflinePlayer offlinePlayer = null;
        if (hasOwner()) {
            if (isOwnerDynamicPlayer()) {
                if (!a.hasPlayerUUID()) {
                    a.addCustomReason("Needs player UUID!");
                    return;
                }

                offlinePlayer = Bukkit.getOfflinePlayer(a.playerUUID());
            } else {
                ownerString = owner;
            }
        } else if (hasOwnerUUID()) {
            offlinePlayer = Bukkit.getOfflinePlayer(ownerUUID);
        }

        if (Supports.playerProfile() && !hasTextureBase64()) {
            PlayerProfile clonedProfile = null;
            if (offlinePlayer == null) {
                if (ownerString != null) {
                    clonedProfile = Bukkit.createPlayerProfile(ownerString);
                }
            } else {
                PlayerProfile originalProfile = offlinePlayer.getPlayerProfile();
                clonedProfile = originalProfile.clone();
            }

            if (clonedProfile == null) {
                clonedProfile = Bukkit.createPlayerProfile(UUID.randomUUID());
            }

            if (hasTextureURL()) {
                PlayerTextures playerTextures = clonedProfile.getTextures();
                playerTextures.setSkin(textureURL);
                clonedProfile.setTextures(playerTextures);
            }

            SkullMeta meta = (SkullMeta) a.result().getItemMeta();
            if (meta != null) {
                meta.setOwnerProfile(clonedProfile);
                clonedProfile.update();

                a.result().setItemMeta(meta);
            }
        } else {
            String name = "";
            UUID uuid = null;
            String id = "";
            String texture = "";

            if (hasTextureBase64()) {
                if (Version.has1_20_5Support()) {
                    texture = "properties:[{name:\"textures\",value:\"" + textureBase64 + "\"}]";
                } else {
                    texture = "Properties:{textures:[{Value:\"" + textureBase64 + "\"}]}";
                }
                uuid = new UUID(textureBase64.hashCode(), textureBase64.hashCode());
            }

            if (offlinePlayer != null) {
                uuid = offlinePlayer.getUniqueId();
            }

            if (Version.has1_20_5Support()) {
                if (uuid != null) {
                    long most = uuid.getMostSignificantBits();
                    long least = uuid.getLeastSignificantBits();
                    id = "id:[I;" + (int) least + "," + (int) (least >> 32) + "," + (int) most + "," + (int) (most >> 32) + "],";
                }
            } else {
                if (uuid != null) {
                    long most = uuid.getMostSignificantBits();
                    long least = uuid.getLeastSignificantBits();
                    id = "Id:[I;" + (int) least + "," + (int) (least >> 32) + "," + (int) most + "," + (int) (most >> 32) + "],";
                }
            }

            if (ownerString != null) {
                if (Version.has1_20_5Support()) {
                    name = "name:\"" + ownerString + "\",";
                } else {
                    name = "Name:\"" + ownerString + "\",";
                }
            }

            if (Version.has1_20_5Support()) {
                addNBTRaw(a, "minecraft:player_head[minecraft:profile={" + id + name + texture + "}]");
            } else {
                addNBTRaw(a, "{SkullOwner:{" + id + name + texture + "}}");
            }
        }
    }

    @Override
    public int hashCode() {
        String toHash = "" + super.hashCode();

        toHash += "owner: " + owner;
        toHash += "ownerUUID: " + ownerUUID.toString();

        if (Supports.playerProfile()) {
            toHash += "textureURL: " + textureURL.toString();
        } else {
            toHash += "textureBase64: " + textureBase64;
        }

        return toHash.hashCode();
    }

    @Override
    public boolean requiresRecipeManagerModification() {
        return isOwnerDynamicPlayer();
    }
}
