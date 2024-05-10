import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import lombok.SneakyThrows;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import util.PoolType;
import util.Pools;

public class YieldTests {
	@ParameterizedTest
	@EnumSource(PoolType.class)
	@SneakyThrows
	void testThreadSleepIsNonBlocking(PoolType poolType) {
		var time = runFutures(poolType, () -> {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		});
		assertTime(poolType, time);
	}

	private static void assertTime(PoolType poolType, Duration time) {
		if (poolType.isVirtual()) {
			assertTrue(time.compareTo(Duration.ofMillis(900)) > 0, time.toString());
			assertTrue(time.compareTo(Duration.ofMillis(1100)) < 0, time.toString());
		} else {
			assertTrue(time.compareTo(Duration.ofMillis(1900)) > 0, time.toString());
			assertTrue(time.compareTo(Duration.ofMillis(2100)) < 0, time.toString());
		}
	}

	@SneakyThrows
	public Duration runFutures(PoolType poolType, Runnable runnable) {
		return runFutures(Pools.pool(poolType), runnable);
	}

	@SneakyThrows
	public Duration runFutures(ExecutorService pool, Runnable runnable) {
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
