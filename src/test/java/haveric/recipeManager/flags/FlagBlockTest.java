package haveric.recipeManager.flags;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@Ignore
@RunWith(PowerMockRunner.class)
@PrepareForTest({Location.class, Block.class})
public class FlagBlockTest extends FlagBaseTest {
    protected Location unpoweredWorkbenchLoc;
    protected Location directWorkbenchLoc;
    protected Location indirectWorkbenchLoc;

    protected Location unpoweredFurnaceLoc;
    protected Location directFurnaceLoc;
    protected Location indirectFurnaceLoc;

    protected Location unpoweredBrewingStandLoc;
    protected Location directBrewingStandLoc;
    protected Location indirectBrewingStandLoc;

    @Before
    public void setupBlocks() {
        mockStatic(Location.class);
        unpoweredWorkbenchLoc = mock(Location.class);
        directWorkbenchLoc = mock(Location.class);
        indirectWorkbenchLoc = mock(Location.class);

        mockStatic(Block.class);
        Block unpoweredWorkbench = mock(Block.class);
        when(unpoweredWorkbench.getType()).thenReturn(Material.WORKBENCH);
        when(unpoweredWorkbenchLoc.getBlock()).thenReturn(unpoweredWorkbench);

        Block directWorkbench = mock(Block.class);
        when(directWorkbench.getType()).thenReturn(Material.WORKBENCH);
        when(directWorkbench.isBlockPowered()).thenReturn(true);
        when(directWorkbench.isBlockIndirectlyPowered()).thenReturn(false);
        when(directWorkbenchLoc.getBlock()).thenReturn(directWorkbench);

        Block indirectWorkbench = mock(Block.class);
        when(indirectWorkbench.getType()).thenReturn(Material.WORKBENCH);
        when(indirectWorkbench.isBlockPowered()).thenReturn(false);
        when(indirectWorkbench.isBlockIndirectlyPowered()).thenReturn(true);
        when(indirectWorkbenchLoc.getBlock()).thenReturn(indirectWorkbench);


        unpoweredFurnaceLoc = mock(Location.class);
        directFurnaceLoc = mock(Location.class);
        indirectFurnaceLoc = mock(Location.class);

        Block unpoweredFurnace = mock(Block.class);
        when(unpoweredFurnace.getType()).thenReturn(Material.FURNACE);
        when(unpoweredFurnaceLoc.getBlock()).thenReturn(unpoweredFurnace);

        Block directFurnace = mock(Block.class);
        when(directFurnace.getType()).thenReturn(Material.FURNACE);
        when(directFurnace.isBlockPowered()).thenReturn(true);
        when(directFurnace.isBlockIndirectlyPowered()).thenReturn(false);
        when(directFurnaceLoc.getBlock()).thenReturn(directFurnace);

        Block indirectFurnace = mock(Block.class);
        when(indirectFurnace.getType()).thenReturn(Material.FURNACE);
        when(indirectFurnace.isBlockPowered()).thenReturn(false);
        when(indirectFurnace.isBlockIndirectlyPowered()).thenReturn(true);
        when(indirectFurnaceLoc.getBlock()).thenReturn(indirectFurnace);


        unpoweredBrewingStandLoc = mock(Location.class);
        directBrewingStandLoc = mock(Location.class);
        indirectBrewingStandLoc = mock(Location.class);

        Block unpoweredBrewingStand = mock(Block.class);
        when(unpoweredBrewingStand.getType()).thenReturn(Material.BREWING_STAND);
        when(unpoweredBrewingStandLoc.getBlock()).thenReturn(unpoweredBrewingStand);

        Block directBrewingStand = mock(Block.class);
        when(directBrewingStand.getType()).thenReturn(Material.BREWING_STAND);
        when(directBrewingStand.isBlockPowered()).thenReturn(true);
        when(directBrewingStand.isBlockIndirectlyPowered()).thenReturn(false);
        when(directBrewingStandLoc.getBlock()).thenReturn(directBrewingStand);

        Block indirectBrewingStand = mock(Block.class);
        when(indirectBrewingStand.getType()).thenReturn(Material.BREWING_STAND);
        when(indirectBrewingStand.isBlockPowered()).thenReturn(false);
        when(indirectBrewingStand.isBlockIndirectlyPowered()).thenReturn(true);
        when(indirectBrewingStandLoc.getBlock()).thenReturn(indirectBrewingStand);
    }
}
