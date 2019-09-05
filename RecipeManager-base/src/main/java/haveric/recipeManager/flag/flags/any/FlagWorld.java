package haveric.recipeManager.flag.flags.any;

import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManagerCommon.util.RMCUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class FlagWorld extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.WORLD;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} [!]<world>, [...] | [fail message]", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Makes the recipe or item work only in certain worlds.",
            "Using this flag more than once will add more worlds.",
            "",
            "The '<world>' argument can be a world name.",
            "Adding ! character as prefix to individual worlds will do the opposite check, will not craft in specified world.",
            "You should require or disallow worlds, using both would be logically pointless.",
            "",
            "Optionally you can specify a failure message that will be used on the specific world(s) defined.",
            "The messages can have the following variables:",
            "  {world}   = current world.",
            "  {worlds}  = a comma separated list of the required or unallowed worlds.", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} world // only allows 'world'",
            "{flag} !world_nether // disallows 'world_nether'",
            "{flag} world1, world2, world3 | <red>Need to be in world 1, 2 or 3! // requires one of the 3 worlds", };
    }

    private Map<String, Boolean> worlds = new HashMap<>();
    private Map<String, String> messages = new HashMap<>();

    public FlagWorld() {
    }

    public FlagWorld(FlagWorld flag) {
        worlds.putAll(flag.worlds);
        messages.putAll(flag.messages);
    }

    @Override
    public FlagWorld clone() {
        return new FlagWorld((FlagWorld) super.clone());
    }

    public Map<String, Boolean> getWorlds() {
        return worlds;
    }

    public void addWorld(String world, String message, boolean allowed) {
        worlds.put(world, allowed);
        messages.put(world, message);
    }

    public Map<String, String> getMessages() {
        return messages;
    }

    public String getWorldMessage(String world) {
        return messages.get(world);
    }

    public String getWorldsString(boolean allowed) {
        StringBuilder s = new StringBuilder();

        for (Entry<String, Boolean> e : worlds.entrySet()) {
            if (allowed == e.getValue()) {
                if (s.length() > 0) {
                    s.append(", ");
                }

                s.append(e.getKey());
            }
        }

        return s.toString();
    }

    @Override
    public boolean onParse(String value) {
        String[] split = value.split("\\|");
        String message;
        if (split.length > 1) {
            message = RMCUtil.trimExactQuotes(split[1]);
        } else {
            message = null;
        }
        split = split[0].toLowerCase().split(",");

        for (String arg : split) {
            arg = arg.trim();
            boolean not = arg.charAt(0) == '!';

            if (not) {
                arg = arg.substring(1).trim();
            }

            addWorld(arg, message, !not);
        }

        return true;
    }

    @Override
    public void onCheck(Args a) {
        String world = null;

        if (a.hasLocation()) {
            world = a.location().getWorld().getName().toLowerCase();
        }

        for (Entry<String, Boolean> e : worlds.entrySet()) {
            if (e.getValue()) {
                if (world == null || !world.equals(e.getKey())) {
                    a.addReason("flag.world.allowed", getWorldMessage(e.getKey()), "{world}", e.getKey(), "{worlds}", getWorldsString(true));
                }
            } else {
                if (world != null && world.equals(e.getKey())) {
                    a.addReason("flag.world.unallowed", getWorldMessage(e.getKey()), "{world}", e.getKey(), "{worlds}", getWorldsString(false));
                }
            }
        }
    }

    /*
     * @Override public List<String> information() { List<String> list = new ArrayList<String>(2);
     *
     * String allowed = getWorldsString(true); String unallowed = getWorldsString(false);
     *
     * if(!allowed.isEmpty()) { int i = allowed.indexOf(','); String world = allowed.substring(0, (i > 0 ? i : allowed.length())); list.add(MessagesOld.FLAG_WORLD_ALLOWED.get("{world}", world,
     * "{worlds}", allowed)); }
     *
     * if(!unallowed.isEmpty()) { int i = unallowed.indexOf(','); String world = unallowed.substring(0, (i > 0 ? i : unallowed.length())); list.add(MessagesOld.FLAG_WORLD_UNALLOWED.get("{world}", world,
     * "{worlds}", unallowed)); }
     *
     * return list; }
     */

    @Override
    public int hashCode() {
        String toHash = "" + super.hashCode();

        toHash += "worlds: ";
        for (Map.Entry<String, Boolean> entry : worlds.entrySet()) {
            toHash += entry.getKey() + entry.getValue().toString();
        }

        toHash += "messages: ";
        for (Map.Entry<String, String> entry : messages.entrySet()) {
            toHash += entry.getKey() + entry.getValue();
        }

        return toHash.hashCode();
    }
}
