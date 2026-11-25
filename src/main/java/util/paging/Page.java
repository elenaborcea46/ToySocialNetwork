package util.paging;

import java.util.List;

/**
 * Represents a page of elements and the total number
 * of elements available in the data source.
 *
 * @param <E> the type of elements stored in the page
 */
public class Page <E>{

    private final List<E> elementsOnPage;
    private final long totalNumberOfElements;

    /**
     * Creates a page containing the retrieved elements
     * and the total number of available elements.
     *
     * @param elementsOnPage elements belonging to the requested page
     * @param totalNumberOfElements total number of elements
     */
    public Page(List<E> elementsOnPage, long totalNumberOfElements){
        this.elementsOnPage = elementsOnPage;
        this.totalNumberOfElements = totalNumberOfElements;
    }

    public List<E> getElementsOnPage() {
        return elementsOnPage;
    }

    public long getTotalNumberOfElements() {
        return totalNumberOfElements;
    }

}
