package com.onpositive.dside.tasks;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * A Debouncer is responsible for executing a task with a delay, and cancelling
 * any previous unexecuted task before doing so.
 * 
 * Forked from https://github.com/ThomasGirard/JDebounce/blob/master/src/main/java/ch/arrg/jdebounce/DebounceExecutor.java
 */
public class DebounceExecutor {

	private ScheduledExecutorService executor;
	private ScheduledFuture<?> future;

	public DebounceExecutor() {
		this.executor = Executors.newSingleThreadScheduledExecutor();
	}

	public void debounce(long delay, Runnable task) {
		if (future != null && !future.isDone()) {
			future.cancel(false);
		}

		future = executor.schedule(task, delay, TimeUnit.MILLISECONDS);
	}
}