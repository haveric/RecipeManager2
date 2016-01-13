package haveric.recipeManager.flags;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class FlagTypeTest {

    @Test
    public void flagDocumentation() {
        for (FlagType flagType : FlagType.values()) {
            if (flagType.flagInstance == null) {
                flagType.flagInstance = flagType.createFlagClass();
            }
            assertTrue("Arguments missing for " + flagType.getName(), flagType.getArguments().length > 0);
            assertTrue("Examples missing for " + flagType.getName(), flagType.getExamples().length > 0);
            assertTrue("Description missing for " + flagType.getName(), flagType.getDescription().length > 0);
        }
    }

    @Test
    public void duplicateAliases() {
        List<String> aliases = new ArrayList<String>();

        for (FlagType flagType : FlagType.values()) {
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
