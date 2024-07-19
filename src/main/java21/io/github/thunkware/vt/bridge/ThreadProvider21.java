package io.github.thunkware.vt.bridge;

import io.github.thunkware.vt.bridge.ThreadTool.Builder.OfPlatform;
import io.github.thunkware.vt.bridge.ThreadTool.Builder.OfVirtual;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

final class ThreadProvider21 implements ThreadProvider {

    private final ThreadProviderConfig config = new ThreadProviderConfig();

    @Override
    public ThreadProviderConfig getConfig() {
        return config;
    }

    @Override
    public boolean hasVirtualThreads() {
        return true;
    }

    @Override
    public final boolean isVirtual(final Thread thread) {
        return thread.isVirtual();
    }

    @Override
    public Thread startVirtualThread(final Runnable task) {
        Thread thread = unstartedVirtualThread(task);
        thread.start();
        return thread;
    }

    @Override
    public Thread unstartedVirtualThread(Runnable task) {
        return unstartedVirtualThread(task, config.getThreadCustomizer());
    }

    private Thread unstartedVirtualThread(Runnable task, ThreadCustomizer threadCustomizer) {
        Thread thread = Thread.ofVirtual().unstarted(task);
        threadCustomizer.customize(thread);
        return thread;
    }

    @Override
    public ExecutorService newThreadPerTaskExecutor(ThreadFactory threadFactory) {
        return Executors.newThreadPerTaskExecutor(config.getThreadCustomizer().asThreadFactory(threadFactory));
    }

    @Override
    public ExecutorService newVirtualThreadPerTaskExecutor() {
        ThreadFactory factory = Thread.ofVirtual().factory();
        return newThreadPerTaskExecutor(factory);
    }

    @Override
    public ExecutorService newVirtualThreadPerTaskExecutor(ThreadCustomizer threadCustomizer) {
        return newThreadPerTaskExecutor(runnable -> unstartedVirtualThread(runnable, threadCustomizer));
    }

    @Override
    public OfPlatform ofPlatform() {
        return new ThreadBuilders21.PlatformThreadBuilder();
    }

    @Override
    public OfVirtual ofVirtual() {
        return new ThreadBuilders21.VirtualThreadBuilder();
    }

}
