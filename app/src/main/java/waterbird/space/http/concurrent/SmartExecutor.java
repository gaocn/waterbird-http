package waterbird.space.http.concurrent;

import android.util.Log;

import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import waterbird.space.http.log.HttpLog;
import waterbird.space.http.utils.HttpUtil;

/**
 * Created by 高文文 on 2016/12/29.
 *
 * A smart thread-pool executor {@link SmartExecutor}
 * <ul>
 *     <li>
 *          keep {@link #coreSize} tasks running concurrently, put these running tasks in {@link #runningQueue}, maximum number of running tasks at same time is {@link #coreSize}
 *     </li>
 *     <li>
 *         when runnning tasks number is {@link #coreSize}, put tasks to be executed in waiting list{@link #waitingQueue}, the maximum waiting tasks is {@link #maxWaitingTaskSize}.
 *     </li>
 *     <li>
 *         when {@link #waitingQueue} is full, abandon tasks according to {@link #overloadPolicy}
 *     </li>
 *     <li>
 *         when on running tasks is finished, remove it from {@link #runningQueue}, and chose a waiting task from {@link #waitingQueue} according to {@link #schedulePolicy}.
 *         add this chosed task to {@link #runningQueue} and delete from {@link #waitingQueue}.
 *     </li>
 * </ul>
 *
 */

public class SmartExecutor implements Executor{
    private static final String TAG = "SmartExecutor";
    /** 获取设备上CPU核数 */
    private static final int CPU_CORE = HttpUtil.getCoresNumbers();
    /** 线程空闲时间，超时后会被线程池回收 */
    private static final int KEEP_ALIVE_TIME = 5;
    /**
     * 线程池，实现多线程并发执行
     */
    private static ThreadPoolExecutor threadPoolExecutor;
    /** 实现同步的锁对象 */
    private final Object LOCK = new Object();
    /** 最大运行任务数 */
    private int coreSize = CPU_CORE;
    /** 运行任务队列 */
    private LinkedList<WrappedRunnable> runningQueue = new LinkedList<>();
    /** 最大等待任务数 */
    private int maxWaitingTaskSize = coreSize * 32;
    /** 等待运行的任务队列 */
    private LinkedList<WrappedRunnable> waitingQueue = new LinkedList<>();
    /**
     * 当runningQueue中可添加下一个运行任务时，根据调度策略选择要运行的任务
     */
    private SchedulePolicy schedulePolicy = SchedulePolicy.FirstComeFirstService;
    /**
     * 当阻塞队列的长度达到最大值{@link #maxWaitingTaskSize}时，再一次添加任务是根据此策略丢弃等待的任务
     */
    private OverloadPolicy overloadPolicy = OverloadPolicy.DiscardOldTaskInQueue;

    interface WrappedRunnable extends Runnable {
        Runnable getRealRunnable();
    }

    public SmartExecutor() {
        initialThreadPool();
    }

    public SmartExecutor(int coreSize, int maxWaitingTaskSize) {
        this.coreSize = coreSize;
        this.maxWaitingTaskSize = maxWaitingTaskSize;
        initialThreadPool();
    }

    private void initialThreadPool() {
        if (HttpLog.isPrint) {
            HttpLog.v(TAG, "SmartExecutor core-queue size: " + coreSize + " - " + maxWaitingTaskSize
                    + "  running-wait task: " + runningQueue.size() + " - " + waitingQueue.size());
        }
        if(threadPoolExecutor == null) {
            threadPoolExecutor = createDefaultThreadPool();
        }
    }

    public static ThreadPoolExecutor createDefaultThreadPool() {
        return new ThreadPoolExecutor(
                CPU_CORE,
                Integer.MAX_VALUE,
                KEEP_ALIVE_TIME,
                TimeUnit.SECONDS,
                /** 适合场景：消费者生产者节奏一至 缓存值为1的阻塞队列
                    They are well suited for handoff designs, in which an object running in one
                        thread must sync up with an object running in another thread in order
                        to hand it some information, event, or task.
                 */
                new SynchronousQueue<Runnable>(),
                new ThreadFactory() {
                    static final String NAME = "waterbird-";
                    AtomicInteger count = new AtomicInteger();
                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, NAME + count.getAndIncrement());
                    }
                },
                new ThreadPoolExecutor.DiscardPolicy() /** 对拒绝任务直接无声抛弃，没有异常信息 */
        );

    }

    public static ThreadPoolExecutor getThreadPoolExecutor() {
        return threadPoolExecutor;
    }

    public static void setThreadPoolExecutor(ThreadPoolExecutor threadPoolExecutor) {
        SmartExecutor.threadPoolExecutor = threadPoolExecutor;
    }

    /**
     *  when {@link SmartExecutor#execute(Runnable)} is called, actions it will perform：
     *  <ol>
     *      <li>
     *          {@link #runningQueue} size less than {@link #coreSize} add tash to {@link #runningQueue} and execute it;
     *      </li>
     *      <li>
     *          {@link #waitingQueue} size less than {@link #maxWaitingTaskSize} add task to {@link #waitingQueue}
     *      </li>
     *      <li>
     *          {@link #waitingQueue} size more than {@link #maxWaitingTaskSize} discard a task according to {@link #overloadPolicy}
     *      </li>
     *      <li>
     *          if any one task in {@link #runningQueue} is finished, remove it from {@link #runningQueue}, chosing a task according to {@link #schedulePolicy} from {@link #waitingQueue}
     *              add it to {@link #runningQueue} to make it run.
     *      </li>
     *  </ol>
     */
    @Override
    public void execute(final Runnable command) {
        if(command == null) {
            return;
        }

        final WrappedRunnable scheduler = new WrappedRunnable() {
            //public Runnable realRunnable;
            @Override
            public Runnable getRealRunnable() {
                return command;
            }

            @Override
            public void run() {
                try {
                    command.run();
                } finally {
                    scheduleNext(this);

                }
            }
        };
        boolean isCallerRun = false;
        synchronized (LOCK) {
            if( runningQueue.size() < coreSize) {
                runningQueue.add(scheduler);
                threadPoolExecutor.execute(scheduler);
            } else if( waitingQueue.size() < maxWaitingTaskSize) {
                waitingQueue.add(scheduler);
            } else {
                switch (overloadPolicy) {
                    case DiscardOldTaskInQueue:
                        waitingQueue.pollFirst();
                        waitingQueue.add(scheduler);
                        break;
                    case DiscardNewTaskInQueue:
                        waitingQueue.pollLast();
                        waitingQueue.add(scheduler);
                        break;
                    case DiscardCurrentTask:
                        break;
                    case CallerRuns:
                        isCallerRun = true;
                        break;
                    case ThrowExecption:
                            throw new RuntimeException("Task rejected from lite smart executor. " + command.toString());
                }
            }
        }

        if(isCallerRun) {
            if (HttpLog.isPrint) {
                HttpLog.i(TAG, "SmartExecutor task running in caller thread");
            }
            command.run();
        }

    }

    private void scheduleNext(WrappedRunnable scheduler) {
        synchronized (LOCK) {
            boolean isRemoved = runningQueue.remove(scheduler);
            if(!isRemoved) {
                runningQueue.clear();
                if(HttpLog.isPrint) {
                    HttpLog.d(TAG, "SmartExecutor scheduler remove failed, so clear all(running taks) to avoid unpreditable error : " + scheduler);
                }
            }

            if(waitingQueue.size() > 0) {
                WrappedRunnable nextRunner = null;

                switch (schedulePolicy) {
                    case FirstComeFirstService:
                        nextRunner = waitingQueue.pollFirst();
                        break;
                    case FirstComeLastService:
                        nextRunner = waitingQueue.pollLast();
                        break;
                    default:
                        nextRunner = waitingQueue.pollLast();
                        break;
                }

                if(nextRunner != null) {
                    runningQueue.add(nextRunner);
                    threadPoolExecutor.execute(nextRunner);
                    HttpLog.d(TAG, "Thread " + Thread.currentThread().getName() + " execute next task..");
                } else {
                    HttpLog.e(TAG,
                            "SmartExecutor get a NULL task from waiting queue: " + Thread.currentThread().getName());
                }

            } else {
                if(HttpLog.isPrint) {
                    HttpLog.d(TAG, "all tasks is completed. current thread: " + Thread.currentThread().getName());
                }
            }
        }
    }


    /*_____________________  API  Uesd to Submit Task    ____________________*/

    protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
        return new FutureTask<T>(runnable, value);
    }

    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        return new FutureTask<T>(callable);
    }

    /**
     * submit runnable
     */
    public Future<?> submit(Runnable task) {
        RunnableFuture<Void> fTask = newTaskFor(task, null);
        execute(fTask);
        return fTask;
    }

    /**
     * submit runnable
     */
    public <T> Future<T> submit(Runnable task, T result) {
        RunnableFuture<T> fTask = newTaskFor(task, result);
        execute(fTask);
        return fTask;
    }

    /**
     * submit callable
     */
    public <T> Future<T> submit(Callable<T> task) {
        RunnableFuture<T> ftask = newTaskFor(task);
        execute(ftask);
        return ftask;
    }

    /**
     * submit RunnableFuture task
     */
    public <T> void submit(RunnableFuture<T> task) {
        execute(task);
    }

    /*______________________     getters & setters        _____________________*/


    public void printThreadPoolInfo() {
        if (HttpLog.isPrint) {
            Log.i(TAG, "___________________________");
            Log.i(TAG, "state (shutdown - terminating - terminated): " + threadPoolExecutor.isShutdown()
                    + " - " + threadPoolExecutor.isTerminating() + " - " + threadPoolExecutor.isTerminated());
            Log.i(TAG, "pool size (core - max): " + threadPoolExecutor.getCorePoolSize()
                    + " - " + threadPoolExecutor.getMaximumPoolSize());
            Log.i(TAG, "task (active - complete - total): " + threadPoolExecutor.getActiveCount()
                    + " - " + threadPoolExecutor.getCompletedTaskCount() + " - " + threadPoolExecutor.getTaskCount());
            Log.i(TAG, "waitingList size : " + threadPoolExecutor.getQueue().size() + " , " + threadPoolExecutor.getQueue());
        }
    }

    public int getCoreSize() {
        return coreSize;
    }
    /**
     * Set maximum number of concurrent tasks at the same time.
     * Recommended core size is CPU count.
     *
     * @param coreSize number of concurrent tasks at the same time
     * @return this
     */
    public SmartExecutor setCoreSize(int coreSize) {
        if(coreSize < 0) {
            throw new IllegalArgumentException("coreSize can not < 0 ");
        }
        this.coreSize = coreSize;
        if (HttpLog.isPrint) {
            HttpLog.v(TAG, "SmartExecutor core-queue size: " + coreSize + " - " + maxWaitingTaskSize
                    + "  running-wait task: " + runningQueue.size() + " - " + waitingQueue.size());
        }
        return this;
    }

    public int getRunningQueueSize() {
        return runningQueue.size();
    }

    public int getWaitingQueueSize() {
        return waitingQueue.size();
    }

    public int getQueueSize() {
        return maxWaitingTaskSize;
    }
    /**
     * Adjust maximum number of waiting queue size by yourself or based on phone performance.
     * For example: CPU count * 32;
     */
    public SmartExecutor setMaxWaitingTaskSize(int maxWaitingTaskSize) {
        if(maxWaitingTaskSize < 0) {
            throw new IllegalArgumentException("maxWaitingTaskSize can not < 0 ");
        }
        this.maxWaitingTaskSize = maxWaitingTaskSize;
        if (HttpLog.isPrint) {
            HttpLog.v(TAG, "SmartExecutor core-queue size: " + coreSize + " - " + maxWaitingTaskSize
                    + "  running-wait task: " + runningQueue.size() + " - " + waitingQueue.size());
        }
        return this;
    }

    public OverloadPolicy getOverloadPolicy() {
        return overloadPolicy;
    }

    public void setOverloadPolicy(OverloadPolicy overloadPolicy) {
        if(overloadPolicy == null) {
            throw new NullPointerException("OverloadPolicy can not be null !");
        }
        this.overloadPolicy = overloadPolicy;
    }

    public SchedulePolicy getSchedulePolicy() {
        return schedulePolicy;
    }

    public void setSchedulePolicy(SchedulePolicy schedulePolicy) {
        if(schedulePolicy == null) {
            throw new NullPointerException("SchedulePolicy can not be null !");
        }
        this.schedulePolicy = schedulePolicy;
    }
}
