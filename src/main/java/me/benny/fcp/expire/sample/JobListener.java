//package me.benny.fcp.expire.sample;
//
//import org.springframework.batch.core.JobExecution;
//import org.springframework.batch.core.JobExecutionListener;
//import org.springframework.batch.core.JobInstance;
//import org.springframework.batch.item.ExecutionContext;
//
//import java.time.LocalDate;
//import java.util.HashMap;
//import java.util.Map;
//
//public class JobListener extends JobExecutionListener {
//    @Override
//    public void beforeJob(JobExecution jobExecution) {
//        /**
//         * job Instance
//         */
//        JobInstance jobInstance = jobExecution.getJobInstance();
//        // job 이름
//        jobInstance.getJobName();
//        // job instance의 ID
//        jobInstance.getInstanceId();
//
//
//        /**
//         * job Execution
//         * 1개의 Job Instance는 여러개의 Job Execution을 가질 수 있다.
//         */
//        // jobExecution의 Job Instance
//        jobExecution.getJobInstance();
//        // jobExecution 에서 사용한 Job Parameters
//        jobExecution.getJobParameters();
//        // job 시작시간과 종료시간
//        jobExecution.getStartTime();
//        jobExecution.getEndTime();
//        // job의 실행결과 (exit code)
//        jobExecution.getExitStatus();
//        // job의 현재상태 (Batch Status)
//        jobExecution.getStatus();
//        // job execution context
//        jobExecution.getExecutionContext();
//
//        Map<String, Object> executionContextMap = new HashMap<>();
//        executionContextMap.put("name", "홍길동");
//        executionContextMap.put("birth", LocalDate.of(1998, 1, 2));
//        jobExecution.setExecutionContext(new ExecutionContext(executionContextMap));
//    }
//
//    @Override
//    public void afterJob(JobExecution jobExecution) {
//
//    }
//}
