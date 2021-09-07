////package me.benny.fcp.expire.sample;
////
////import org.springframework.batch.core.Job;
////import org.springframework.batch.core.Step;
////import org.springframework.batch.core.StepContribution;
////import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
////import org.springframework.batch.core.configuration.annotation.JobScope;
////import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
////import org.springframework.batch.core.configuration.annotation.StepScope;
////import org.springframework.batch.core.launch.support.RunIdIncrementer;
////import org.springframework.batch.core.scope.context.ChunkContext;
////import org.springframework.batch.core.step.tasklet.Tasklet;
////import org.springframework.batch.repeat.RepeatStatus;
////import org.springframework.context.annotation.Bean;
////import org.springframework.context.annotation.Configuration;
////import org.springframework.transaction.PlatformTransactionManager;
////
////import java.time.LocalDate;
////import java.util.List;
////
////@Configuration
////public class SampleJobConfiguration {
//////    /**
//////     * 예약 가능한 식당을 자동으로 예약해주는 서비스
//////     * 단, 외국인 관광객들을 위하여 예약하다보니 한식 레스토랑만 예약한다.
//////     **/
//////    @Bean
//////    public Job reserveRestaurantJob(
//////            JobBuilderFactory jobBuilderFactory,
//////            Step searchAvailableKoreanRestaurantStep,
//////            Step reserveRestaurantStep,
//////            Step sendDepositStep
//////    ) {
//////        return jobBuilderFactory
//////                .get("reserveRestaurantJob") // job name
//////                .start(searchAvailableKoreanRestaurantStep) // step 1
//////                .next(reserveRestaurantStep) // step 2
//////                .next(sendDepositStep) // step 3
//////                .build();
//////    }
//////
//////    /**
//////     * 예약 가능한 식당을 자동으로 예약해주는 서비스
//////     * 단, 외국인 관광객들을 위하여 예약하다보니 한식 레스토랑을 먼저 예약하려고 한다.
//////     * 한식 레스토랑 찾기에 실패한다면 차선책으로 아시안 레스토랑을 찾는다.
//////     **/
//////    @Bean
//////    public Job reserveRestaurantJob(
//////            JobBuilderFactory jobBuilderFactory,
//////            Step searchAvailableKoreanRestaurantStep,
//////            Step searchAvailableAsianRestaurantStep,
//////            Step reserveRestaurantStep,
//////            Step sendDepositStep
//////    ) {
//////        return jobBuilderFactory
//////                .get("reserveRestaurantJob")
//////                .start(searchAvailableKoreanRestaurantStep)
//////                    .on("FAILED") // searchAvailableKoreanRestaurantStep가 FAILED인 경우
//////                    .to(searchAvailableAsianRestaurantStep) // searchAvailableAsianRestaurantStep 실행
//////                    .on("FAILED") // searchAvailableAsianRestaurantStep가 FAILED인 경우
//////                .end() // 아무것도 하지않고 flow 종료
//////                .from(searchAvailableKoreanRestaurantStep)
//////                    .on("*") // searchAvailableKoreanRestaurantStep가 FAILED가 아니라면
//////                    .to(reserveRestaurantStep) // reserveRestaurantStep 실행
//////                    .next(sendDepositStep) // sendDepositStep 실행
//////                .from(searchAvailableAsianRestaurantStep)
//////                    .on("*") // searchAvailableAsianRestaurantStep가 FAILED가 아니라면
//////                    .to(reserveRestaurantStep) // reserveRestaurantStep 실행
//////                    .next(sendDepositStep) // sendDepositStep 실행
//////                .end() // job 종료
//////                .build();
//////    }
//////
//////
//    /**
//     * 한식 레스토랑을 찾는 Step
//     */
//    @Bean
//    @JobScope
//    public Step searchAvailableKoreanRestaurantStep(
//            StepBuilderFactory stepBuilderFactory,
//            PlatformTransactionManager transactionManager,
//            Tasklet searchAvailableKoreanRestaurantTasklet
//    ) {
//        return stepBuilderFactory
//                .get("searchAvailableKoreanRestaurantStep")
//                .transactionManager(transactionManager)
//                .tasklet(searchAvailableKoreanRestaurantTasklet)
//                .build();
//    }
//////
//////    /**
//////     * 한식 레스토랑을 찾는 Step
//////     *
//////     * availableKoreanRestaurantFromKakaoMapItemReader
//////     * 이용이 가능한 한식 레스토랑 카카오맵으로 부터 찾아오는 ItemReader
//////     * KakaoMapToRestaurantItemProcessor
//////     * 가져온 카카오맵 정보를 레스토랑 객체로 변환
//////     * saveRestaurantItemWriter
//////     * 변환한 레스토랑객체를 저장함
//////     */
//////    @Bean
//////    @JobScope
//////    public Step searchAvailableKoreanRestaurantStep(
//////            StepBuilderFactory stepBuilderFactory,
//////            PlatformTransactionManager transactionManager,
//////            JpaPagingItemReader<KakaoMap> availableKoreanRestaurantFromKakaoMapItemReader,
//////            ItemProcessor<KakaoMap, Restaurant> KakaoMapToRestaurantItemProcessor,
//////            ItemWriter<Restaurant> saveRestaurantItemWriter
//////    ) {
//////        return stepBuilderFactory
//////                .get("searchAvailableKoreanRestaurantStep")
//////                .transactionManager(transactionManager)
//////                .<KakaoMap, Restaurant>chunk(1000)
//////                .reader(availableKoreanRestaurantFromKakaoMapItemReader)
//////                .processor(KakaoMapToRestaurantItemProcessor)
//////                .writer(saveRestaurantItemWriter)
//////                .build();
//////    }
//////
//////
//////    @Bean
//////    @JobScope
//////    public Step sampleJob() {
//////
//////    }
//////            StepBuilderFactory stepBuilderFactory,
//////            PlatformTransactionManager transactionManager,
//////            JpaPagingItemReader<KakaoMap> availableKoreanRestaurantFromKakaoMapItemReader,
//////            ItemProcessor<KakaoMap, Restaurant> KakaoMapToRestaurantItemProcessor,
//////            ItemWriter<Restaurant> saveRestaurantItemWriter
//////    ) {
//////        return stepBuilderFactory
//////                .get("searchAvailableKoreanRestaurantStep")
//////                .transactionManager(transactionManager)
//////                .<KakaoMap, Restaurant>chunk(1000)
//////                .reader(availableKoreanRestaurantFromKakaoMapItemReader)
//////                .processor(KakaoMapToRestaurantItemProcessor)
//////                .writer(saveRestaurantItemWriter)
//////                .build();
//////    }
////
////    @Bean
////    public Job sampleJob(
////            JobBuilderFactory jobBuilderFactory,
////            StepBuilderFactory stepBuilderFactory
////    ) {
////        return jobBuilderFactory.get("asdf")
////                .incrementer(new RunIdIncrementer())
////                .start(stepBuilderFactory.get("asdf").tasklet((contribution, chunkContext) -> {
////                    System.out.println("asdf");
////                    return null;
////                }).build())
////                .build();
////    }
////
//    /**
//     * finish를 log에 찍고 종료하는 Tasklet이다.
//     **/
//    @Bean
//    @StepScope
//    public Tasklet sampleTasklet() {
//        return (contribution, chunkContext) -> {
//            log.info("finish");
//            return RepeatStatus.FINISHED;
//        };
//    }
////
////    /**
////     * 무한히 종료되지 않는 Tasklet이다.
////     * 아래와 같이 구현하면 안된다.
////     **/
////    @Bean
////    @StepScope
////    public Tasklet sampleTasklet() {
////        return (contribution, chunkContext) -> {
////            log.info("never ending tasklet");
////            return RepeatStatus.CONTINUABLE;
////        };
////    }
////
////    /**
////     * sampleTasklet를 실행하는 Step을 정의한다.
////     **/
////    @Bean
////    @JobScope
////    public Step sampleTaskletStep(
////            StepBuilderFactory stepBuilderFactory,
////            PlatformTransactionManager transactionManager,
////            Tasklet sampleTasklet
////    ) {
////        return stepBuilderFactory.get("sampleTaskletStep")
////                .transactionManager(transactionManager)
////                .tasklet(sampleTasklet)
////                .build();
////    }
////
////
////    @Bean
////    @StepScope
////    public Tasklet hugeTransactionTasklet() {
////        return (contribution, chunkContext) -> {
////            // 1천만개의 데이터를 저장합니다.
////            // 1천만개의 데이터를 1개의 트랜잭션에 묶어 저장합니다.
////            for (int i = 0; i < 10000000; i++) {
////                priceRepository.save(new Price(i * 10));
////            }
////            return RepeatStatus.FINISHED;
////        };
////    }
////
////    @Bean
////    @StepScope
////    public Tasklet hugeReadTasklet(
////            PriceRepository priceRepository
////    ) {
////        return (contribution, chunkContext) -> {
////            // findByDate로 조회된 데이터가 1천만개 입니다.
////            List<Price> prices = priceRepository.findByDate(LocalDate.now());
////            // price를 모두 0으로 초기화합니다.
////            prices.forEach(price -> price.setAmount(0L));
////            // 1천만개의 데이터를 저장합니다.
////            priceRepository.saveAll(prices);
////            return RepeatStatus.FINISHED;
////        };
////    }
////
////
////    @Bean
////    @JobScope
////    public Step saveOrderedPriceStep(
////            StepBuilderFactory stepBuilderFactory,
////            PlatformTransactionManager transactionManager,
////            JpaPagingItemReader<Order> orderReader,
////            ItemProcessor<Order, Price> orderToPriceProcessor,
////            ItemWriter<Price> priceWriter
////    ) {
////        return stepBuilderFactory.get("saveOrderedPriceStep")
////                .transactionManager(transactionManager)
////                // Order를 read해서 Price로 process한뒤 Price를 Write한다.
////                .<Order, Price>chunk(1000)
////                // 주문된 데이터 read하기
////                .reader(orderReader)
////                // 주문정보에서 가격으로 변환
////                .processor(orderToPriceProcessor)
////                // 가격을 write
////                .writer(priceWriter)
////                .build();
////    }
////
////    @Bean
////    @StepScope
////    public FlatFileItemReader<Point> pointFlatFileItemReader() {
////        return new FlatFileItemReaderBuilder<Point>()
////                .name("pointFlatFileItemReader")
////                .resource(new FileSystemResource(filePath))
////                .delimited()
////                .delimiter(",")
////                .names("id", "amount")
////                .targetType(Point.class)
////                .recordSeparatorPolicy(
////                        new SimpleRecordSeparatorPolicy() {
////                            @Override
////                            public String postProcess(String record) {
////                                return record.trim();
////                            }
////                        }
////                )
////                .build();
////    }
////
////
////    @Bean
////    @StepScope
////    public JdbcCursorItemReader<Point> pointJdbcCursorItemReader() {
////        return new JdbcCursorItemReaderBuilder<Point>()
////                .fetchSize(1000)
////                .dataSource(dataSource)
////                .rowMapper(new BeanPropertyRowMapper<>(Point.class))
////                .sql("SELECT id, amount FROM point")
////                .name("pointJdbcCursorItemReader")
////                .build();
////    }
////
////    @Bean
////    @StepScope
////    public JdbcPagingItemReader<Point> pointJdbcPagingItemReader(
////            PagingQueryProvider pointQueryProvider
////    ) throws Exception {
////        Map<String, Object> parameterValues = new HashMap<>();
////        parameterValues.put("amount", 100);
////        return new JdbcPagingItemReaderBuilder<Point>()
////                .name("pointJdbcPagingItemReader")
////                .pageSize(chunkSize)
////                .fetchSize(chunkSize)
////                .dataSource(dataSource)
////                .rowMapper(new BeanPropertyRowMapper<>(Point.class))
////                .queryProvider(createQueryProvider)
////                .parameterValues(parameterValues)
////                .build();
////    }
////
////    @Bean
////    @StepScope
////    public PagingQueryProvider pointQueryProvider() throws Exception {
////        SqlPagingQueryProviderFactoryBean queryProvider =
////                new SqlPagingQueryProviderFactoryBean();
////        queryProvider.setDataSource(dataSource); // Database에 맞는 PagingQueryProvider를 선택하기 위해
////        queryProvider.setSelectClause("id, amount");
////        queryProvider.setFromClause("from point");
////        queryProvider.setWhereClause("where amount >= :amount");
////        Map<String, Order> sortKeys = new HashMap<>(1);
////        sortKeys.put("id", Order.ASCENDING);
////        queryProvider.setSortKeys(sortKeys);
////        return queryProvider.getObject();
////    }
////
////    @Bean
////    @StepScope
////    public JpaPagingItemReader pointJpaPagingItemReader() {
////        return new JpaPagingItemReaderBuilder<Point>()
////                .name("pointJpaPagingItemReader")
////                .entityManagerFactory(entityManagerFactory())
////                .queryString("select p from Point p")
////                .pageSize(1000)
////                .build();
////    }
////
////    @Bean
////    @StepScope
////    public RepositoryItemReader<Point> pointRepositoryItemReader(
////            PointRepository pointRepository
////    ) {
////        return new RepositoryItemReaderBuilder()
////                .repository(pointRepository)
////                .methodName("findByAmountGreaterThan")
////                .pageSize(1000)
////                .maxItemCount(1000)
////                .arguments(Arrays.asList(BigInteger.valueOf(100)))
////                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
////                .name("pointRepositoryItemReader")
////                .build();
////    }
////
////    /**
////     * 주문정보(Order)로 10000원이 넘는 가격(Price)을 찾는 Processor
////     **/
////    @Bean
////    @StepScope
////    public ItemProcessor<Order, Price> findExpensivePriceProcessor(
////            PriceRepository priceRepository
////    ) {
////        return order -> {
////            Price price = priceRepository.findByProductId(order.productId);
////            if (price.amount > 10000)
////                return price
////            else
////                return null // writer에 데이터를 넘기지 않는다.
////        };
////    }
////
////    @Bean
////    @StepScope
////    public CompositeItemProcessor compositeProcessor(
////            ItemProcessor<Order, Order> processor1,
////            ItemProcessor<Order, Price> processor2
////    ) {
////        List<ItemProcessor> delegates = List.of(processor1, processor2);
////        CompositeItemProcessor processor = new CompositeItemProcessor<>();
////        processor.setDelegates(delegates);
////        return processor;
////    }
////
////    public JdbcBatchItemWriter<Point> jdbcBatchItemWriter() {
////        return new JdbcBatchItemWriterBuilder<Point>()
////                .dataSource(dataSource)
////                .sql("insert into point(point_wallet_id, amount) values (:point_wallet_id, :amount)")
////                .beanMapped()
////                .build();
////    }
////
////    @Bean
////    @StepScope
////    public ItemWriter<Point> expirePointWriter(
////            PointRepository pointRepository
////    ) {
////        return points -> {
////            pointRepository.saveAll(points);
////        };
////    }
////
////    }
