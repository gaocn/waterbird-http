
本文主要介绍多线程编程中经常使用的创建任务的接口Runnable、Callable<Result>、RunnableFuture<Result>、FutureTask<Result>及相关结果类Future<Result>。在Java 5之前，没有提供API用于查询线程是否执行完毕及获取线程执行结果；在Java 5之后，并发框架包java.util.concurrent中提供了Future接口和FutureTask<Result>类对异步执行任务的框架进行了更好的支持，其中也引入了Callable<Result>，RunnableFuture<Result>接口。

## 继承关系图

```
                ____________         __________
                | Runnable |         | Future |
                    \\                   //
                     \\    implements   //
                      \\               //
                       ------------------        ____________
                       | RunnableFuture |        | Callable |
                              ||                //
                              || implements   //
                              ||             //   support => FutureTask(Callable<Result> callable)
                       ------------------  //
                       |  FutureTask    |

```

总结，Runnable和Callable<Result>是可调度执行的线程单元，Runnable可以通过包装在Thread中直接启动一个线程执行，而Callable<Result>的实现类一般是提交给ExecutorService来执行；Executor可以作为Runnable、Callable<Result>的调度容器，Future是便于对具体的调度任务执行结果进行查看且可以检查任务是否完成；不同的是Runnable的结果类型是void，所以通过Future看不到任务调度的结果，但是可以通过RunnableFuture<Result>为Runnable添加返回类型(一般通过FutureTask<Result>为Runnable创建待返回结果的任务)。


### 源码介绍
#### Runnable接口

Runnable实现类可以使用new Thread(Runnable r)放到一个新线程中跑，没有返回结果；也可以使用ExecutorService.submit(Runnable r)放到线程池中跑，返回结果为null，等于没有返回结果，但可以通过返回的Future对象查询执行状态。

```
    public interface Runnable {
        public abstract void run();
    }

```

#### Callable<Result>接口

Callable<Result>实现类只能在ExecutorService的线程池中跑，但有返回结果，也可以通过返回的Future对象查询执行状态。

```
    public interface Callable<V> {
        /**
         * Computes a result, or throws an exception if unable to do so.
         */
        V call() throws Exception;
    }

```

#### RunnableFuture<Result>接口

```
    public interface RunnableFuture<V> extends Runnable, Future<V> {
        /**
         * Sets this Future to the result of its computation
         * unless it has been cancelled.
         */
        void run();
    }

```

#### Future<Result>接口

用于查询任务执行状态，获取执行结果，或者取消未执行的任务。在ExecutorService框架中，由于使用线程池，所以Runnable与Callable实例都当做任务看待，而不会当做“线程”看待，所以Future才有取消任务执行等接口。接口中的get()方法用于获取任务执行结果，因为任务是异步执行的，所以我们可以在需要使用结果的时候才调用get()方法，调用时如果任务还未执行完就会阻塞直到任务完成；当然我们也可以调用get的另一重载版本get(long timeout, TimeUnit unit)，当阻塞时会等待指定的时间，如果时间到而任务还未完成，那么就会抛出TimeoutException。

```
    public interface Future<V> {

        /**
         * Attempts to cancel execution of this task.  This attempt will
         * fail if the task has already completed, has already been cancelled,
         * or could not be cancelled for some other reason. If successful,
         * and this task has not started when {@code cancel} is called,
         * this task should never run.  If the task has already started,
         * then the {@code mayInterruptIfRunning} parameter determines
         * whether the thread executing this task should be interrupted in
         * an attempt to stop the task.
         *
         * <p>After this method returns, subsequent calls to {@link #isDone} will
         * always return {@code true}.  Subsequent calls to {@link #isCancelled}
         * will always return {@code true} if this method returned {@code true}.
         *
         * @param mayInterruptIfRunning {@code true} if the thread executing this
         * task should be interrupted; otherwise, in-progress tasks are allowed
         * to complete
         * @return {@code false} if the task could not be cancelled,
         * typically because it has already completed normally;
         * {@code true} otherwise
         */
        boolean cancel(boolean mayInterruptIfRunning);

        /**
         * Returns {@code true} if this task was cancelled before it completed
         * normally.
         */
        boolean isCancelled();

        /**
         * Completion may be due to normal termination, an exception, or
         * cancellation -- in all of these cases, this method will return
         * {@code true}.
         */
        boolean isDone();

        /**
         * Waits if necessary for the computation to complete, and then
         * retrieves its result.
         */
        V get() throws InterruptedException, ExecutionException;

        /**
         * Waits if necessary for at most the given time for the computation
         * to complete, and then retrieves its result, if available.
         */
        V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException;
    }
```

#### FutureTask<Result>类

集Runnable、Callable、Future于一身，它首先实现了Runnable与Future接口，然后在构造函数中还要注入Callable对象（或者变形的Callable对象：Runnable + Result），所以FutureTask类既可以使用new Thread(Runnable r)放到一个新线程中跑，也可以使用ExecutorService.submit(Runnable r)放到线程池中跑，而且两种方式都可以获取返回结果。

```
    /*
     *   A cancellable asynchronous computation.  This class provides a base
     * implementation of {@link Future}, with methods to start and cancel
     * a computation, query to see if the computation is complete, and
     * retrieve the result of the computation.  The result can only be
     * retrieved when the computation has completed; the {@code get}
     * methods will block if the computation has not yet completed.  Once
     * the computation has completed, the computation cannot be restarted
     * or cancelled (unless the computation is invoked using
     * {@link #runAndReset}).
     */
    public class FutureTask<V> implements RunnableFuture<V>{...}

```


#### ExecutorService接口：线程池执行调度框架

```
    public interface ExecutorService extends Executor {

        /**
         * Initiates an orderly shutdown in which previously submitted
         * tasks are executed, but no new tasks will be accepted.
         * Invocation has no additional effect if already shut down.
         *
         * <p>This method does not wait for previously submitted tasks to
         * complete execution.  Use {@link #awaitTermination awaitTermination}
         * to do that.
         */
        void shutdown();

        /**
         * Attempts to stop all actively executing tasks, halts the
         * processing of waiting tasks, and returns a list of the tasks
         * that were awaiting execution.
         *
         * <p>This method does not wait for actively executing tasks to
         * terminate.  Use {@link #awaitTermination awaitTermination} to
         * do that.
         *
         * <p>There are no guarantees beyond best-effort attempts to stop
         * processing actively executing tasks.  For example, typical
         * implementations will cancel via {@link Thread#interrupt}, so any
         * task that fails to respond to interrupts may never terminate.
         */
        List<Runnable> shutdownNow();

        /**
         * Returns {@code true} if this executor has been shut down.
         *
         */
        boolean isShutdown();

        /**
         * Returns {@code true} if all tasks have completed following shut down.
         * Note that {@code isTerminated} is never {@code true} unless
         * either {@code shutdown} or {@code shutdownNow} was called first.
         *
         */
        boolean isTerminated();

        /**
         * Blocks until all tasks have completed execution after a shutdown
         * request, or the timeout occurs, or the current thread is
         * interrupted, whichever happens first.
         *
         */
        boolean awaitTermination(long timeout, TimeUnit unit)
            throws InterruptedException;

        /**
         * Submits a value-returning task for execution and returns a
         * Future representing the pending results of the task. The
         * Future's {@code get} method will return the task's result upon
         * successful completion.
         *
         * <p>
         * If you would like to immediately block waiting
         * for a task, you can use constructions of the form
         * {@code result = exec.submit(aCallable).get();}
         *
         * <p>Note: The {@link Executors} class includes a set of methods
         * that can convert some other common closure-like objects,
         * for example, {@link java.security.PrivilegedAction} to
         * {@link Callable} form so they can be submitted.
         *
         */
        <T> Future<T> submit(Callable<T> task);

        /**
         * Submits a Runnable task for execution and returns a Future
         * representing that task. The Future's {@code get} method will
         * return the given result upon successful completion.
         *
         */
        <T> Future<T> submit(Runnable task, T result);

        /**
         * Submits a Runnable task for execution and returns a Future
         * representing that task. The Future's {@code get} method will
         * return {@code null} upon <em>successful</em> completion.
         *
         */
        Future<?> submit(Runnable task);

        /**
         * Executes the given tasks, returning a list of Futures holding
         * their status and results when all complete.
         * {@link Future#isDone} is {@code true} for each
         * element of the returned list.
         * Note that a <em>completed</em> task could have
         * terminated either normally or by throwing an exception.
         * The results of this method are undefined if the given
         * collection is modified while this operation is in progress.
         *
         */
        <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)
            throws InterruptedException;

        /**
         * Executes the given tasks, returning a list of Futures holding
         * their status and results
         * when all complete or the timeout expires, whichever happens first.
         * {@link Future#isDone} is {@code true} for each
         * element of the returned list.
         * Upon return, tasks that have not completed are cancelled.
         * Note that a <em>completed</em> task could have
         * terminated either normally or by throwing an exception.
         * The results of this method are undefined if the given
         * collection is modified while this operation is in progress.
         *
         */
        <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks,
                                      long timeout, TimeUnit unit)
            throws InterruptedException;

        /**
         * Executes the given tasks, returning the result
         * of one that has completed successfully (i.e., without throwing
         * an exception), if any do. Upon normal or exceptional return,
         * tasks that have not completed are cancelled.
         * The results of this method are undefined if the given
         * collection is modified while this operation is in progress.
         *
         */
        <T> T invokeAny(Collection<? extends Callable<T>> tasks)
            throws InterruptedException, ExecutionException;

        /**
         * Executes the given tasks, returning the result
         * of one that has completed successfully (i.e., without throwing
         * an exception), if any do before the given timeout elapses.
         * Upon normal or exceptional return, tasks that have not
         * completed are cancelled.
         * The results of this method are undefined if the given
         * collection is modified while this operation is in progress.
         */
        <T> T invokeAny(Collection<? extends Callable<T>> tasks,
                        long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException;
    }
```

### 使用举例

```
        Callable<String> c = new Callable<String>() {
            public String call() {
                try {
                    TimeUnit.SECONDS.sleep(new Random().nextInt(5));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                return "Callable--"+Thread.currentThread().getName();
            }
        };

        //seed a single thread
        FutureTask<String> ft1 = new FutureTask<String>(c);
        Thread t = new Thread(ft1);
        t.start();

        Runnable r = new Runnable() {
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(new Random().nextInt(5));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        FutureTask<String> ft2 = new FutureTask<String>(r, "Runnable");//give return value directly
        FutureTask<String> ft3 = new FutureTask<String>(c);

        ExecutorService es = Executors.newFixedThreadPool(2);//init ExecutorService
        es.submit(ft2);


```
