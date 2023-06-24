package wow.scraper.fetchers.impl;

import org.springframework.stereotype.Component;
import wow.scraper.fetchers.PageFetcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * User: POlszewski
 * Date: 2023-06-23
 */
@Component
public class WebPageFetcher implements PageFetcher {
	@Override
	public String fetchPage(String urlStr) throws IOException {
		URL url = new URL(urlStr);
		try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream(), UTF_8))) {
			return bufferedReader
					.lines()
					.collect(Collectors.joining("\n"));
		}
	}
}
