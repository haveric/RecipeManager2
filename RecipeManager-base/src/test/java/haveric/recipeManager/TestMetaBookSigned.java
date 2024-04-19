package haveric.recipeManager;

import org.bukkit.inventory.meta.BookMeta;

public class TestMetaBookSigned extends TestMetaBook implements BookMeta {

    TestMetaBookSigned(TestMetaItem meta) {
        super(meta);
    }

    @Override
    boolean isEmpty() {
        return super.isEmpty();
    }

    @Override
    public TestMetaBookSigned clone() {
        return (TestMetaBookSigned) super.clone();
    }

    @Override
    boolean equalsCommon(TestMetaItem meta) {
        return super.equalsCommon(meta);
    }

    @Override
    boolean notUncommon(TestMetaItem meta) {
        return super.notUncommon(meta) && (meta instanceof TestMetaBookSigned || isBookEmpty());
    }
}
