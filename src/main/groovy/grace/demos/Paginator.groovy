package grace.demos

class Paginator {

    private int from
    private int to
    private int total
    private int pageCount
    private int pageNum
    private int pageSize
    private Pagination pagination = new Pagination()

    Paginator(String url, int total, int pageSize, int currenPage) {
        this.from = (currenPage - 1) * pageSize + 1
        this.to = currenPage * pageSize
        this.total = total
        this.pageCount = total % pageSize == 0 ? (int) (total / pageSize) : ((int) (total / pageSize) + 1)
        this.pageNum = currenPage
        this.pageSize = pageSize

        def links = []
        (1..this.pageCount).each {
            links << new Link(url: url + '?page=' + it, label: it, active: currenPage == it)
        }
        this.pagination.links = links
        if (currenPage > 1) {
            this.pagination.hasPrevious = true
            this.pagination.previousPageUrl = url + '?page=' + (currenPage - 1)
        }
        else {
            this.pagination.hasPrevious = false
            this.pagination.previousPageUrl = '#'
        }
        if (currenPage < pageCount) {
            this.pagination.hasNext = true
            this.pagination.nextPageUrl = url + '?page=' + (currenPage + 1)
        }
        else {
            this.pagination.hasNext = false
            this.pagination.nextPageUrl = '#'
        }
    }

    Pagination getPagination() {
        this.pagination
    }

    Integer getPageCount() {
        this.pageCount
    }

}
