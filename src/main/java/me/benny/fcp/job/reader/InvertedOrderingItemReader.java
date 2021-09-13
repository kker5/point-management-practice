//package me.benny.fcp.job.reader;
//
//
//import org.springframework.batch.item.ItemReader
//import org.springframework.batch.item.database.HibernatePagingItemReader
//import org.springframework.batch.item.database.JpaPagingItemReader
//import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader
//import org.springframework.data.domain.Page
//import org.springframework.data.domain.PageRequest
//import org.springframework.data.domain.Pageable
//import org.springframework.data.domain.Sort
//import org.springframework.data.domain.Sort.Direction.ASC
//import org.springframework.data.domain.Sort.Direction.DESC
//import org.springframework.util.ClassUtils
//import java.util.concurrent.CopyOnWriteArrayList
//import java.util.concurrent.atomic.AtomicBoolean
//import java.util.concurrent.atomic.AtomicInteger
//
//class InvertedOrderingItemReader<T> extends AbstractItemCountingItemStreamItemReader<T> {
//    String name;
//    Sort sort;
//
//    @Override
//    protected T doRead() throws Exception {
//        return null;
//    }
//
//    @Override
//    protected void doOpen() throws Exception {
//
//    }
//
//    @Override
//    protected void doClose() throws Exception {
//
//    }
//}
//        private int pageSize = DEFAULT_PAGE_SIZE,
//        private val query: (Pageable) -> Page<T>
//        ) : AbstractItemCountingItemStreamItemReader<T>() {
//        private var initialized = AtomicBoolean(false)
//
//        private lateinit var results: CopyOnWriteArrayList<T>
//
//        private val page = AtomicInteger(0)
//
//        /**
//         * 정렬 기준 프로퍼티
//         *
//         * 읽으려는 데이터를 역전시키기 위해 주어진 정렬 조건을 반전시킨다.
//         */
//        private val sort: Sort = sort?.let {
//        Sort.by(
//        it.toList().map { order ->
//        Sort.Order(
//        if (order.direction == ASC) DESC else ASC,
//        order.property
//        )
//        }
//        )
//        } ?: Sort.unsorted()
//
//        init {
//        super.setName(name ?: ClassUtils.getShortName(this::class.java))
//        }
//
//        override fun doOpen() {
//        require(!initialized.get()) { "Cannot open an already opened ItemReader, call close first" }
//
//        initialized.set(true)
//
//        page.set(query.invoke(PageRequest.of(0, pageSize, sort)).totalPages - 1)
//        }
//
//        private fun initResult() {
//        if (!::results.isInitialized || results.isEmpty()) {
//        results = CopyOnWriteArrayList()
//        } else {
//        results.clear()
//        }
//        }
//
//        private fun doReadPage() {
//        initResult()
//
//        if (page.get() > -1) { // 읽어들일 페이지가 남아 있으면 청크 탐색을 계속한다.
//        results.addAll(query.invoke(PageRequest.of(page.get(), pageSize, sort)).content)
//        }
//
//        page.decrementAndGet()
//        }
//
//        override fun doRead(): T? {
//        if (!::results.isInitialized || results.isEmpty()) { // 청크가 비어있으면 다음 청크(실제로는 이전 페이지)를 읽는다.
//        doReadPage()
//        }
//
//        return if (results.isEmpty()) null
//        else results.removeAt(results.size - 1) // 청크의 뒷부분부터 읽는다.
//        }
//
//        override fun doClose() {
//        initialized.set(false)
//        page.set(0)
//        }
//
//        companion object {
//        private const val DEFAULT_PAGE_SIZE = 100
//        }
//        }