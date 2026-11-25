package util.paging;

/**
 *
 * Represents a request for a specific page of data.
 *
 */
public class Pageable {
    private final int pageNumber;
    private final int pageSize;

    /**
     * Creates a pagination request.
     *
     * @param pageNumber page number
     * @param pageSize maximum number of elements per page
     */
    public Pageable(int pageNumber, int pageSize){

        if(pageNumber < 0)
            throw new IllegalArgumentException("Page number cannot be negative.");

        if(pageSize <= 0)
            throw new IllegalArgumentException("Page size must be positive.");

        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    public int getPageNumber(){
        return pageNumber;
    }

    public int getPageSize(){
        return pageSize;
    }
}
