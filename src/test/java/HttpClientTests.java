import static org.junit.jupiter.api.Assertions.assertEquals;
import static util.RunUtil.runFutures;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import lombok.SneakyThrows;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import util.PoolType;
import util.TimeAssertions;

public class HttpClientTests {
	MockWebServer server;
	int port;
	HttpClient client;

	@BeforeEach
	@SneakyThrows
	void setup() {
		server = new MockWebServer();
		server.setDispatcher(new Dispatcher() {
			@NotNull
			@Override
			@SneakyThrows
			public MockResponse dispatch(@NotNull RecordedRequest recordedRequest) throws InterruptedException {
				Thread.sleep(1000);
				return new MockResponse().setResponseCode(200).setBody("ok\n");
			}
		});
		server.start();
		port = server.getPort();
		client = HttpClient.newHttpClient();
	}

	@ParameterizedTest
	@EnumSource(PoolType.class)
	@SneakyThrows
	void testSendIsNonBlocking(PoolType poolType) {
		var time = runFutures(poolType, () -> {
			try {
				var request = HttpRequest.newBuilder()
						.GET()
						.uri(server.url("/").uri())
						.build();
				var response = client.send(request, HttpResponse.BodyHandlers.ofString());
				assertEquals(200, response.statusCode());
				assertEquals("ok\n", response.body());
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
		TimeAssertions.assertTime(poolType, time);
	}

	@ParameterizedTest
	@EnumSource(PoolType.class)
	@SneakyThrows
	void testSendAsyncIsNonBlocking(PoolType poolType) {
		var time = runFutures(poolType, () -> {
			var request =
					HttpRequest.newBuilder().GET().uri(server.url("/").uri()).build();
			var response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
					.join();
			assertEquals(200, response.statusCode());
			assertEquals("ok\n", response.body());
		});
		TimeAssertions.assertTime(poolType, time);
	}
}
