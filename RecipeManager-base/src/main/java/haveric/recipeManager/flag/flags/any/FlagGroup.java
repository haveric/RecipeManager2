package haveric.recipeManager.flag.flags.any;

import haveric.recipeManager.ErrorReporter;
import haveric.recipeManager.Perms;
import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.common.util.RMCUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class FlagGroup extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.GROUP;
    }

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


    private Map<String, Boolean> groups = new HashMap<>();
    private Map<String, String> messages = new HashMap<>();

    // TODO finish

    public FlagGroup() {
    }

    public FlagGroup(FlagGroup flag) {
        groups.putAll(flag.groups);
        messages.putAll(flag.messages);
    }

    @Override
    public FlagGroup clone() {
        return new FlagGroup((FlagGroup) super.clone());
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
    public boolean onParse(String value, String fileName, int lineNum, int restrictedBit) {
        super.onParse(value, fileName, lineNum, restrictedBit);
        if (!Perms.getInstance().isEnabled()) {
            ErrorReporter.getInstance().warning("Flag " + getFlagType() + " does nothing because no Vault-supported permission plugin was detected.");
        }

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

            addGroup(arg, message, !not);
        }

        return true;
    }

    @Override
    public void onCheck(Args a) {
        if (!Perms.getInstance().isEnabled()) {
            return;
        }

        for (Entry<String, Boolean> e : groups.entrySet()) {
            if (e.getValue()) {
                if (!a.hasPlayerUUID() || !Perms.getInstance().playerInGroup(a.playerUUID(), e.getKey())) {
                    a.addReason("flag.group.allowed", getGroupMessage(e.getKey()), "{group}", e.getKey(), "{groups}", getGroupsString(true));
                }
            } else {
                if (a.hasPlayerUUID() && Perms.getInstance().playerInGroup(a.playerUUID(), e.getKey())) {
                    a.addReason("flag.group.unallowed", getGroupMessage(e.getKey()), "{group}", e.getKey(), "{groups}", getGroupsString(false));
                }
            }
        }
    }

    @Override
    public int hashCode() {
        String toHash = "" + super.hashCode();

        for (Map.Entry<String, Boolean> entry : groups.entrySet()) {
            toHash += entry.getKey() + entry.getValue().toString();
        }

        for (Map.Entry<String, String> entry : messages.entrySet()) {
            toHash += entry.getKey() + entry.getValue();
        }

        return toHash.hashCode();
    }
}
