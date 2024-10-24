package wow.scraper.fetcher.impl;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import wow.scraper.fetcher.PageFetcher;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * User: POlszewski
 * Date: 2023-06-23
 */
@Component
public class WebPageFetcher implements PageFetcher {
	@SneakyThrows
	@Override
	public String fetchPage(String urlStr) {
		URL url = URI.create(urlStr).toURL();
		try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream(), UTF_8))) {
			return bufferedReader
					.lines()
					.collect(Collectors.joining("\n"));
		}
	}
}
