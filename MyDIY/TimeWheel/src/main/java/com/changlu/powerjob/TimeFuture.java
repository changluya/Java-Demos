package com.changlu.powerjob;

public interface TimeFuture {

    TimerTask getTask();

    boolean cancel();

    boolean isCancelled();

    boolean isDone();

}
