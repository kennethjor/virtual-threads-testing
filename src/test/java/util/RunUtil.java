package util;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import lombok.SneakyThrows;

public class RunUtil {
	@SneakyThrows
	public static Duration runFutures(PoolType poolType, Runnable runnable) {
		return runFutures(Pools.pool(poolType), runnable);
	}

	@SneakyThrows
	public static Duration runFutures(ExecutorService pool, Runnable runnable) {
		var futures = new Future[Pools.CONCURRENCY * 2];
		var start = Instant.now();
		for (int i = 0; i < Pools.CONCURRENCY * 2; i++) {
			futures[i] = pool.submit(runnable);
		}
		for (Future<?> future : futures) {
			future.get();
		}
		return Duration.between(start, Instant.now());
	}
}
