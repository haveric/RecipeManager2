package haveric.recipeManager.flags;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class FlagTypeOldTest {

    @Test
    public void flagDocumentation() {
        for (FlagDescriptor flagType : FlagFactory.getInstance().getFlags().values()) {
            assertTrue("Arguments missing for " + flagType.getName(), flagType.getArguments().length > 0);
            assertTrue("Examples missing for " + flagType.getName(), flagType.getExamples().length > 0);
            assertTrue("Description missing for " + flagType.getName(), flagType.getDescription().length > 0);
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
