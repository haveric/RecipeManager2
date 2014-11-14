package haveric.recipeManager.flags;

import haveric.recipeManager.Messages;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


public class FlagWorld extends Flag {
    // Flag definition and documentation

    private static final FlagType TYPE = FlagType.WORLD;
    protected static final String[] A = new String[] {
        "{flag} [!]<world>, [...] | [fail message]", };

    protected static final String[] D = new String[] {
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

    protected static final String[] E = new String[] {
        "{flag} world // only allows 'world'",
        "{flag} !world_nether // disallows 'world_nether'",
        "{flag} world1, world2, world3 | <red>Need to be in world 1, 2 or 3! // requires one of the 3 worlds", };

    private Map<String, Boolean> worlds = new HashMap<String, Boolean>();
    private Map<String, String> messages = new HashMap<String, String>();

    public FlagWorld() {
    }

    public FlagWorld(FlagWorld flag) {
        worlds.putAll(flag.worlds);
        messages.putAll(flag.messages);
    }

    @Override
    public FlagWorld clone() {
        super.clone();
        return new FlagWorld(this);
    }

    @Override
    public FlagType getType() {
        return TYPE;
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
            if (allowed == e.getValue().booleanValue()) {
                if (s.length() > 0) {
                    s.append(", ");
                }

                s.append(e.getKey());
            }
        }

        return s.toString();
    }

    @Override
    protected boolean onParse(String value) {
        String[] split = value.split("\\|");
        String message;
        if (split.length > 1) {
            message = split[1].trim();
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
    protected void onCheck(Args a) {
        String world = null;

        if (a.hasLocation()) {
            world = a.location().getWorld().toString().toLowerCase();
        }

        for (Entry<String, Boolean> e : worlds.entrySet()) {
            if (e.getValue().booleanValue()) {
                if (world == null || !world.equals(e.getKey())) {
                    a.addReason(Messages.FLAG_WORLD_ALLOWED, getWorldMessage(e.getKey()), "{world}", e.getKey(), "{worlds}", getWorldsString(true));
                }
            } else {
                if (world != null && world.equals(e.getKey())) {
                    a.addReason(Messages.FLAG_WORLD_UNALLOWED, getWorldMessage(e.getKey()), "{world}", e.getKey(), "{worlds}", getWorldsString(false));
                }
            }
        }
    }

    /*
     * @Override public List<String> information() { List<String> list = new ArrayList<String>(2);
     *
     * String allowed = getWorldsString(true); String unallowed = getWorldsString(false);
     *
     * if(!allowed.isEmpty()) { int i = allowed.indexOf(','); String world = allowed.substring(0, (i > 0 ? i : allowed.length())); list.add(Messages.FLAG_WORLD_ALLOWED.get("{world}", world,
     * "{worlds}", allowed)); }
     *
     * if(!unallowed.isEmpty()) { int i = unallowed.indexOf(','); String world = unallowed.substring(0, (i > 0 ? i : unallowed.length())); list.add(Messages.FLAG_WORLD_UNALLOWED.get("{world}", world,
     * "{worlds}", unallowed)); }
     *
     * return list; }
     */
}
