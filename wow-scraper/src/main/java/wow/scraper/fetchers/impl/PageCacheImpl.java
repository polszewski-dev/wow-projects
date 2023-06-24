package wow.scraper.fetchers.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.scraper.config.ScraperConfig;
import wow.scraper.fetchers.PageCache;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * User: POlszewski
 * Date: 2023-06-23
 */
@Component
@AllArgsConstructor
public class PageCacheImpl implements PageCache {
	private static final Charset CHARSET = UTF_8;

	private final ScraperConfig scraperConfig;

	@Override
	public String fetch(String urlStr) throws IOException {
		Path path = getPath(urlStr);
		if (!path.toFile().exists()) {
			return null;
		}
		try (Stream<String> lines = Files.lines(path, UTF_8)) {
			return lines.collect(Collectors.joining("\n"));
		}
	}

	@Override
	public void save(String urlStr, String value) throws IOException {
		Path path = getPath(urlStr);
		path.toFile().getParentFile().mkdirs();
		Files.writeString(path, value, CHARSET);
	}

	private Path getPath(String urlStr) {
		urlStr = removeProtocol(urlStr);
		String[] encodedParts = Stream.of(urlStr.split("/"))
				.map(this::encodePart)
				.toArray(String[]::new);
		return Path.of(scraperConfig.getDirectoryPath() + "/cache", encodedParts);
	}

	private String removeProtocol(String urlStr) {
		int beginIndex = urlStr.indexOf("//");
		if (beginIndex < 0) {
			return urlStr;
		}
		return urlStr.substring(beginIndex + 2);
	}

	private String encodePart(String x) {
		return x.chars().mapToObj(this::encodeChar).collect(Collectors.joining());
	}

	private String encodeChar(int c) {
		if (Character.isLetterOrDigit(c) || c == '-' || c == '.') {
			return Character.toString(c);
		}
		return '_' + Integer.toHexString(c);
	}
}
