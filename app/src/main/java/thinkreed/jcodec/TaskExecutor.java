package thinkreed.jcodec;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by thinkreed on 2017/10/18.
 */

public class TaskExecutor {

    private ExecutorService executor = Executors.newCachedThreadPool();

    public static TaskExecutor getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        static TaskExecutor INSTANCE = new TaskExecutor();
    }

    private TaskExecutor() {
    }

    public Future<?> execute(Runnable task) {
        return executor.submit(task);
    }

    public void cancel(Future<?> future) {
        future.cancel(true);
    }
}
