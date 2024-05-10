package util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

public class Pools {
	private static final ExecutorService PLATFORM = createPool(PoolType.PLATFORM);
	private static final ExecutorService VIRTUAL = createPool(PoolType.VIRTUAL);
	public static final int CONCURRENCY = ForkJoinPool.getCommonPoolParallelism();

	private static ExecutorService createPool(PoolType type) {
		if (type == PoolType.PLATFORM) {
			return ForkJoinPool.commonPool();
		} else if (type == PoolType.VIRTUAL) {
			return Executors.newVirtualThreadPerTaskExecutor();
		} else {
			throw new IllegalArgumentException("Unknown pool type: " + type);
		}
	}

	public static ExecutorService pool(PoolType type) {
		if (type == PoolType.PLATFORM) {
			return PLATFORM;
		} else if (type == PoolType.VIRTUAL) {
			return VIRTUAL;
		} else {
			throw new IllegalArgumentException("Unknown pool type: " + type);
		}
	}
}
