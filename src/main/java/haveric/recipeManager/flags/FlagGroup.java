package haveric.recipeManager.flags;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Messages;
import haveric.recipeManager.Perms;

public class FlagGroup extends Flag {

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} [!]<group>, [...] | [fail message]", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Makes the recipe or item require the crafter to be in a permission group.",
            "Using this flag more than once will add more groups, the player must be in at least one group.",
            "",
            "The '<group>' argument must be a permission group.",
            "",
            "Adding ! character as prefix to individual groups will do the opposite check, if crafter is in group it will not craft.",
            "",
            "You can also specify more groups separated by , character.",
            "",
            "Optionally you can specify a failure message that will be used on the specific group(s) defined.",
            "The messages can have the following variables:",
            "  {group}   = group that was not found or was found and it's unallowed.",
            "  {groups}  = a comma separated list of the allowed or unallowed groups.",
            "",
            "NOTE: Vault with a supported permission plugin is required for this flag to work.", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} ranks.vip",
            "{flag} !jobs.builder | <red>Builders can't use this!",
            "{flag} jobs.farmer, jobs.trader | <red>You must be a farmer or trader!",
            "{flag} ! ranks.newbs, ! ranks.newbies | <yellow>Noobs can't use this. // valid with spaces too", };
    }


    private Map<String, Boolean> groups = new HashMap<String, Boolean>();
    private Map<String, String> messages = new HashMap<String, String>();

    // TODO finish

    public FlagGroup() {
    }

    public FlagGroup(FlagGroup flag) {
        groups.putAll(flag.groups);
        messages.putAll(flag.messages);
    }

    @Override
    public FlagGroup clone() {
        super.clone();
        return new FlagGroup(this);
    }

    public Map<String, Boolean> getGroups() {
        return groups;
    }

    public void addGroup(String group, String message, boolean allowed) {
        groups.put(group, allowed);
        messages.put(group, message);
    }

    public Map<String, String> getMessages() {
        return messages;
    }

    public String getGroupMessage(String group) {
        return messages.get(group);
    }

    public String getGroupsString(boolean allowed) {
        StringBuilder s = new StringBuilder();

        for (Entry<String, Boolean> e : groups.entrySet()) {
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
        if (!Perms.getInstance().isEnabled()) {
            ErrorReporter.warning("Flag " + getType() + " does nothing because no Vault-supported permission plugin was detected.");
        }

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

            addGroup(arg, message, !not);
        }

        return true;
    }

    @Override
    protected void onCheck(Args a) {
        if (!Perms.getInstance().isEnabled()) {
            return;
        }

        for (Entry<String, Boolean> e : groups.entrySet()) {
            if (e.getValue().booleanValue()) {
                if (!a.hasPlayerName() || !Perms.getInstance().playerInGroup(a.playerName(), e.getKey())) {
                    a.addReason(Messages.FLAG_GROUP_ALLOWED, getGroupMessage(e.getKey()), "{group}", e.getKey(), "{groups}", getGroupsString(true));
                }
            } else {
                if (a.hasPlayerName() && Perms.getInstance().playerInGroup(a.playerName(), e.getKey())) {
                    a.addReason(Messages.FLAG_GROUP_UNALLOWED, getGroupMessage(e.getKey()), "{group}", e.getKey(), "{groups}", getGroupsString(false));
                }
            }
        }
    }

    /*
     * @Override public List<String> information() { List<String> list = new ArrayList<String>(2);
     *
     * String allowed = getGroupsString(true); String unallowed = getGroupsString(false);
     *
     * if(!allowed.isEmpty()) { int i = allowed.indexOf(','); String group = allowed.substring(0, (i > 0 ? i : allowed.length())); list.add(Messages.FLAG_GROUP_ALLOWED.get("{group}", group,
     * "{groups}", allowed)); }
     *
     * if(!unallowed.isEmpty()) { int i = unallowed.indexOf(','); String group = unallowed.substring(0, (i > 0 ? i : unallowed.length())); list.add(Messages.FLAG_GROUP_UNALLOWED.get("{group}", group,
     * "{groups}", unallowed)); }
     *
     * return list; }
     */
}
