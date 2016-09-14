package haveric.recipeManager;

import com.google.common.base.Strings;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.inventory.meta.BookMeta;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class TestMetaBook extends TestMetaItem implements BookMeta {
    private static final int MAX_PAGE_LENGTH = Short.MAX_VALUE;
    private static final int MAX_TITLE_LENGTH = 0xffff;

    private String title;
    private String author;
    public List<String> pages = new ArrayList<>();
    private Integer generation;

    TestMetaBook(TestMetaItem meta) {
        super(meta);

        if (meta instanceof TestMetaBook) {
            TestMetaBook bookMeta = (TestMetaBook) meta;
            this.title = bookMeta.title;
            this.author = bookMeta.author;
            pages.addAll(bookMeta.pages);
            this.generation = bookMeta.generation;
        }
    }

    boolean isBookEmpty() {
        return !(hasPages() || hasAuthor() || hasTitle());
    }

    public boolean hasAuthor() {
        return !Strings.isNullOrEmpty(author);
    }

    public boolean hasTitle() {
        return !Strings.isNullOrEmpty(title);
    }

    public boolean hasPages() {
        return !pages.isEmpty();
    }

    public boolean hasGeneration() {
        return generation != null;
    }

    public String getTitle() {
        return this.title;
    }

    public boolean setTitle(final String title) {
        if (title == null) {
            this.title = null;
            return true;
        } else if (title.length() > MAX_TITLE_LENGTH) {
            return false;
        }

        this.title = title;
        return true;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(final String author) {
        this.author = author;
    }

    @Override
    public Generation getGeneration() {
        return (generation == null) ? null : Generation.values()[generation];
    }

    @Override
    public void setGeneration(Generation generation) {
        this.generation = (generation == null) ? null : generation.ordinal();
    }

    public String getPage(final int page) {
        Validate.isTrue(isValidPage(page), "Invalid page number");
        return pages.get(page - 1);
    }

    public void setPage(final int page, final String text) {
        if (!isValidPage(page)) {
            throw new IllegalArgumentException("Invalid page number " + page + "/" + pages.size());
        }

        String newText = text == null ? "" : text.length() > MAX_PAGE_LENGTH ? text.substring(0, MAX_PAGE_LENGTH) : text;
        pages.set(page - 1, newText);
    }

    public void setPages(final String... pages) {
        this.pages.clear();

        addPage(pages);
    }

    public void addPage(final String... pages) {
        for (String page : pages) {
            if (page == null) {
                page = "";
            } else if (page.length() > MAX_PAGE_LENGTH) {
                page = page.substring(0, MAX_PAGE_LENGTH);
            }

            this.pages.add(page);
        }
    }

    public int getPageCount() {
        return pages.size();
    }

    public List<String> getPages() {
        final List<String> copy = new ArrayList<>(pages);
        return new AbstractList<String>() {

            @Override
            public String get(int index) {
                return copy.get(index);
            }

            @Override
            public int size() {
                return copy.size();
            }
        };
    }

    public void setPages(List<String> pages) {
        this.pages.clear();
        for (String page : pages) {
            addPage(page);
        }
    }

    private boolean isValidPage(int page) {
        return page > 0 && page <= pages.size();
    }

    @Override
    public TestMetaBook clone() {
        TestMetaBook meta = (TestMetaBook) super.clone();
        meta.pages = new ArrayList<>(pages);
        return meta;
    }

    boolean applicableTo(Material type) {
        switch (type) {
            case WRITTEN_BOOK:
            case BOOK_AND_QUILL:
                return true;
            default:
                return false;
        }
    }
}
