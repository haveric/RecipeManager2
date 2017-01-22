package haveric.recipeManager;

import org.bukkit.Material;
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
    boolean applicableTo(Material type) {
        switch (type) {
            case WRITTEN_BOOK:
            case BOOK_AND_QUILL:
                return true;
            default:
                return false;
        }
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
