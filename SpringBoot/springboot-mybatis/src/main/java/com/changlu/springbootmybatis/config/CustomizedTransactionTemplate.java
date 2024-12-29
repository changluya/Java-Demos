package com.changlu.springbootmybatis.config;

import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.List;
import java.util.function.Function;

@Component
public class CustomizedTransactionTemplate extends DefaultTransactionDefinition {

    private final PlatformTransactionManager transactionManager;

    public CustomizedTransactionTemplate(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }


    public void execute(Runnable runnable) {
        TransactionStatus status = this.transactionManager.getTransaction(this);
        try {
            runnable.run();
        } catch (Exception e) {
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
            this.transactionManager.rollback(status);
            return null;
        }
        this.transactionManager.commit(status);
        return result;
    }
}