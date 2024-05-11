import static util.RunUtil.runFutures;

import lombok.SneakyThrows;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import util.PoolType;
import util.TimeAssertions;

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
		TimeAssertions.assertTime(poolType, time);
	}
}
