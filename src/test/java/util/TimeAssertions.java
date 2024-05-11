package util;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

public class TimeAssertions {
	public static void assertTime(PoolType poolType, Duration supplied) {
		assertTime(poolType, Duration.ofSeconds(1), Duration.ofSeconds(2), supplied);
	}

	public static void assertTime(
			PoolType poolType, Duration virtualExpected, Duration platformExpected, Duration supplied) {
		assertTime(poolType.isVirtual() ? virtualExpected : platformExpected, supplied, 0.1);
	}

	public static void assertTime(Duration expected, Duration supplied, double margin) {
		var min = Duration.ofNanos((long) (expected.toNanos() * (1.0 - margin)));
		var max = Duration.ofNanos((long) (expected.toNanos() * (1.0 + margin)));
		var msg = String.format("Expected %s to be between %s and %s", supplied, min, max);
		assertTrue(supplied.compareTo(min) > 0 && supplied.compareTo(max) < 0, msg);
	}
}
