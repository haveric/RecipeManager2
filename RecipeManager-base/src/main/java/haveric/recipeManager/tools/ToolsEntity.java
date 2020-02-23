package haveric.recipeManager.tools;

import org.bukkit.entity.EntityType;

public class ToolsEntity {
    public static boolean isAgeable(EntityType entityType) {
        boolean ageable = false;

        switch (entityType) {
            case CHICKEN:
            case COW:
            case HORSE:
            case MUSHROOM_COW:
            case OCELOT:
            case PIG:
            case SHEEP:
            case VILLAGER:
            case WOLF:
            case RABBIT:
                ageable = true;
                break;

            default:
                break;
        }

        if (!ageable && Version.has1_10Support()) {
            switch (entityType) {
                case DONKEY:
                case MULE:
                case SKELETON_HORSE:
                case ZOMBIE_HORSE:
                case POLAR_BEAR:
                    ageable = true;
                    break;

                default:
                    break;
            }
        }

        if (!ageable && Version.has1_11Support() && entityType == EntityType.LLAMA) {
            ageable = true;
        }

        if (!ageable && Version.has1_12Support() && entityType == EntityType.PARROT) {
            ageable = true;
        }

        if (!ageable && Version.has1_13BasicSupport() && entityType == EntityType.TURTLE) {
            ageable = true;
        }

        if (!ageable && Version.has1_15Support() && entityType == EntityType.BEE) {
            ageable = true;
        }

        if (!ageable && Version.has1_14Support()) {
            switch (entityType) {
                case CAT:
                case PANDA:
                case FOX:
                case WANDERING_TRADER:
                case TRADER_LLAMA:
                    ageable = true;
                    break;

                default:
                    break;
            }
        }

        return ageable;
    }

    public static boolean isTameable(EntityType entityType) {
        boolean tameable = false;

        if (!Version.has1_14Support() && entityType == EntityType.OCELOT) {
            tameable = true;
        }

        if (!tameable && entityType == EntityType.WOLF) {
            tameable = true;
        }

        if (!tameable && Version.has1_10Support()) {
            switch (entityType) {
                case DONKEY:
                case MULE:
                case SKELETON_HORSE:
                case ZOMBIE_HORSE:
                case POLAR_BEAR:
                    tameable = true;
                    break;

                default:
                    break;
            }
        }

        if (!tameable && Version.has1_11Support() && entityType == EntityType.LLAMA) {
            tameable = true;
        }

        if (!tameable && Version.has1_12Support() && entityType == EntityType.PARROT) {
            tameable = true;
        }

        if (!tameable && Version.has1_14Support()) {
            if (entityType == EntityType.CAT || entityType == EntityType.TRADER_LLAMA) {
                tameable = true;
            }
        }

        return tameable;
    }
}
