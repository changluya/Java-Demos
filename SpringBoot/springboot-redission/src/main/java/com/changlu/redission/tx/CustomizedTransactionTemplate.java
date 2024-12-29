package com.changlu.redission.tx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.function.Function;

/**
 * 自定义
 */
@Component
public class CustomizedTransactionTemplate extends DefaultTransactionDefinition {

    private final static Logger LOGGER = LoggerFactory.getLogger(CustomizedTransactionTemplate.class);

    private final PlatformTransactionManager transactionManager;

    public CustomizedTransactionTemplate(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void execute(Runnable runnable) {
        TransactionStatus status = this.transactionManager.getTransaction(this);
        try {
            runnable.run();
        } catch (Exception e) {
            LOGGER.info("TransactionTemplate execute fail:{}", e.getMessage(), e);
            this.transactionManager.rollback(status);
        }
        this.transactionManager.commit(status);
    }


    public <T,R> R execute(Function<T, R> function, T t) {
        TransactionStatus status = this.transactionManager.getTransaction(this);
        R result;
        try {
            result = function.apply(t);
        } catch (Exception e) {
            LOGGER.info("TransactionTemplate execute fail:{}", e.getMessage(), e);
            this.transactionManager.rollback(status);
            return null;
        }
        this.transactionManager.commit(status);
        return result;
    }

    public <T,R> R executeWithThrow(Function<T, R> function, T t) {
        TransactionStatus status = this.transactionManager.getTransaction(this);
        R result;
        try {
            result = function.apply(t);
        } catch (Exception e) {
            LOGGER.info("TransactionTemplate execute fail:{}", e.getMessage(), e);
            this.transactionManager.rollback(status);
            throw new RuntimeException("TransactionTemplate execute fail, rollback", e);
        }
        this.transactionManager.commit(status);
        return result;
    }

}