package haveric.recipeManager.flag;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;


public class FlagTypeOldTest {

    @Test
    public void flagDocumentation() {
        for (FlagDescriptor flagType : FlagFactory.getInstance().getFlags().values()) {
            assertTrue(flagType.getArguments().length > 0, "Arguments missing for " + flagType.getName());
            assertTrue(flagType.getExamples().length > 0, "Examples missing for " + flagType.getName());
            assertTrue(flagType.getDescription().length > 0, "Description missing for " + flagType.getName());
        }
    }

    @Test
    public void duplicateAliases() {
        List<String> aliases = new ArrayList<>();

        for (FlagDescriptor flagType : FlagFactory.getInstance().getFlags().values()) {
            for (String name : flagType.getNames()) {
                if (aliases.contains(name)) {
                    fail("Duplicate name: " + name);
                } else {
                    aliases.add(name);
                }
            }
        }
    }
}
