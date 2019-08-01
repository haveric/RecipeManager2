package haveric.recipeManager.flag.flags.any;

import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManagerCommon.util.RMCUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class FlagPermission extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.PERMISSION;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} [!]<permission>, [...] | [fail message]", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Makes the recipe or item require the crafter to have a permission.",
            "Using this flag more than once will add more permissions, the player must have at least one to allow crafting.",
            "",
            "The '<permission>' argument must be a permission node, regardless if it exists or not.",
            "",
            "Adding ! character as prefix to individual permission nodes will do the opposite check, if crafter has permission it will not craft.",
            "",
            "You can also specify more permissions separated by , character.",
            "",
            "Optionally you can specify a failure message that will be used on the specific permission(s) defined.",
            "The messages can have the following variables:",
            "  {permission}  = permission that was not found or was found and it's unallowed.",
            "  {permissions}  = a comma separated list of the allowed or unallowed permission nodes.", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} ranks.vip",
            "{flag} !jobs.builder | <red>Builders can't use this!",
            "{flag} jobs.farmer, jobs.trader | <red>You must be a farmer or trader!",
            "{flag} ! ranks.newbs, ! ranks.newbies | <yellow>Noobs can't use this. // valid with spaces too", };
    }


    private Map<String, Boolean> permissions = new HashMap<>();
    private Map<String, String> messages = new HashMap<>();

    public FlagPermission() {
    }

    public FlagPermission(FlagPermission flag) {
        permissions.putAll(flag.permissions);
        messages.putAll(flag.messages);
    }

    @Override
    public FlagPermission clone() {
        return new FlagPermission((FlagPermission) super.clone());
    }

    public Map<String, Boolean> getPermissions() {
        return permissions;
    }

    public void addPermission(String permission, String message, boolean allowed) {
        permissions.put(permission, allowed);
        messages.put(permission, message);
    }

    public Map<String, String> getMessages() {
        return messages;
    }

    public String getPermissionMessage(String permission) {
        return messages.get(permission);
    }

    public String getPermissionsString(boolean allowed) {
        StringBuilder s = new StringBuilder();

        for (Entry<String, Boolean> e : permissions.entrySet()) {
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

            /*
             * Permission permission = Bukkit.getPluginManager().getPermission(arg);
             *
             * if(permission == null) { permission = new Permission(arg, PermissionDefault.FALSE); Bukkit.getPluginManager().addPermission(permission); //RecipeErrorReporter.warning("Flag " +
             * getType() + " has permission '" + arg + "' which is not registered!"); }
             */

            addPermission(arg, message, !not);
        }

        return true;
    }

    @Override
    public void onCheck(Args a) {
        boolean success = false;
        List<String> failed = new ArrayList<>();
        for (Entry<String, Boolean> e : permissions.entrySet()) {
            if (e.getValue()) {
                if (a.hasPlayer() && a.player().hasPermission(e.getKey())) {
                    success = true;
                    break;
                }

                failed.add(e.getKey());
            }
        }

        if (!success && failed.size() > 0) {
            List<String> failedMessages = new ArrayList<>();

            for (String perm : failed) {
                String message = getPermissionMessage(perm);

                if (!failedMessages.contains(message)) {
                    failedMessages.add(message);
                    a.addReason("flag.permission.allowed", message, "{permission}", perm, "{permissions}", getPermissionsString(true));
                }
            }
        }

        success = false;
        List<String> succeeded = new ArrayList<>();
        for (Entry<String, Boolean> e : permissions.entrySet()) {
            if (!e.getValue()) {
                if (a.hasPlayer() && a.player().hasPermission(e.getKey())) {
                    succeeded.add(e.getKey());
                } else {
                    success = true;
                    break;
                }
            }
        }

        if (!success && succeeded.size() > 0) {
            List<String> succeededMessages = new ArrayList<>();

            for (String perm : succeeded) {
                String message = getPermissionMessage(perm);

                if (!succeededMessages.contains(message)) {
                    succeededMessages.add(message);
                    a.addReason("flag.permission.unallowed", message, "{permission}", perm, "{permissions}", getPermissionsString(false));
                }
            }
        }
    }

    /*
     * @Override public List<String> information() { List<String> list = new ArrayList<String>(2);
     *
     * String allowed = getPermissionsString(true); String unallowed = getPermissionsString(false);
     *
     * if(!allowed.isEmpty()) { int i = allowed.indexOf(','); String permission = allowed.substring(0, (i > 0 ? i : allowed.length())); list.add(MessagesOld.FLAG_PERMISSION_ALLOWED.get("{permission}",
     * permission, "{permissions}", allowed)); }
     *
     * if(!unallowed.isEmpty()) { int i = unallowed.indexOf(','); String permission = unallowed.substring(0, (i > 0 ? i : unallowed.length()));
     * list.add(MessagesOld.FLAG_PERMISSION_UNALLOWED.get("{permission}", permission, "{permissions}", unallowed)); }
     *
     * return list; }
     */
}
