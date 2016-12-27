package waterbird.space.http.concurrent;

/**
 * Policy of thread-pool-executor overload.
 */
public enum OverloadPolicy {
    DiscardNewTaskInQueue,
    DiscardOldTaskInQueue,
    DiscardCurrentTask,
    CallerRuns,
    ThrowExecption
}
