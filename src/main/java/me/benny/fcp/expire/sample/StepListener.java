//package me.benny.fcp.expire.sample;
//
//import org.springframework.batch.core.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.transaction.PlatformTransactionManager;
//
//public class StepListener implements StepExecutionListener {
//    @Autowired
//    PlatformTransactionManager transactionManager;
//    @Override
//    public void beforeStep(StepExecution stepExecution) {
//        // Step의 이름
//        stepExecution.getStepName();
//        // JobExecution
//        stepExecution.getJobExecution();
//        // Step의 시작시간, 종료시간
//        stepExecution.getStartTime();
//        stepExecution.getEndTime();
//        // Execution Context
//        stepExecution.getExecutionContext();
//        // Step의 실행 결과
//        stepExecution.getExitStatus();
//        // Step의 현재 실행 상태 (Batch Status)
//        stepExecution.getStatus();
//    }
//
//    @Override
//    public ExitStatus afterStep(StepExecution stepExecution) {
//        return null;
//    }
//}
