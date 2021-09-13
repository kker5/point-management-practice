package me.benny.fcp.job.reader;

import com.google.common.collect.Lists;
import org.springframework.batch.core.annotation.AfterRead;
import org.springframework.batch.core.annotation.BeforeRead;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStreamSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class ReverseJpaPagingItemReader<T> extends ItemStreamSupport implements ItemReader<T> {
    private static final int DEFAULT_PAGE_SIZE = 100;

    private int page = 0;
    private int totalPage = 0;
    private List<T> readRows = Lists.newArrayList();

    private int pageSize = DEFAULT_PAGE_SIZE;
    private Function<Pageable, Page<T>> query;
    private Sort sort = Sort.unsorted();

    ReverseJpaPagingItemReader() {}

    public void setPageSize(int pageSize) {
        this.pageSize = (pageSize > 0) ? pageSize : DEFAULT_PAGE_SIZE;
    }

    public void setQuery(Function<Pageable, Page<T>> query) {
        this.query = query;
    }

    public void setSort(Sort sort) {
        if (!Objects.isNull(sort)) {
            //pagination을 마지막 페이지부터 하기때문에 sort direction를 모두 reverse한다.
            Iterator<Sort.Order> orderIterator = sort.iterator();
            final List<Sort.Order> reverseOrders = Lists.newLinkedList();
            while (orderIterator.hasNext()) {
                Sort.Order prev = orderIterator.next();
                reverseOrders.add(new Sort.Order(prev.getDirection().isAscending() ? Sort.Direction.DESC : Sort.Direction.ASC, prev.getProperty()));
            }
            this.sort = Sort.by(reverseOrders);
        }
    }

    /**
     * <p>
     * totalPage : 전체 페이지 개수
     * page : 현재페이지(마지막 페이지)
     * </p>
     */
    @BeforeStep
    public void beforeStep() {
        totalPage = getTargetData(0).getTotalPages();
        page = totalPage - 1;
    }

    @SuppressWarnings("unused")
    @BeforeRead
    public void beforeRead() {
        if (page < 0)
            return;
        if (readRows.isEmpty())
            readRows = Lists.newArrayList(getTargetData(page).getContent());
    }

    @Override
    public T read() {
        return readRows.isEmpty() ? null : readRows.remove(readRows.size() - 1);
    }

    @SuppressWarnings("unused")
    @AfterRead
    public void afterRead() {
        if (readRows.isEmpty()) {
            this.page--;
        }
    }

    private Page<T> getTargetData(int readPage) {
        return Objects.isNull(query)?Page.empty():query.apply(PageRequest.of(readPage, pageSize, sort));
    }
}