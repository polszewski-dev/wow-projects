package wow.scraper.fetchers.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wow.scraper.fetchers.PageCache;
import wow.scraper.fetchers.PageFetcher;

import java.io.IOException;

/**
 * User: POlszewski
 * Date: 2023-06-23
 */
@Component
@AllArgsConstructor
@Slf4j
public class CachedPageFetcher implements PageFetcher {
	private final PageFetcher pageFetcher;
	private final PageCache pageCache;

	@Override
	public String fetchPage(String urlStr) throws IOException {
		String cached = pageCache.fetch(urlStr);
		if (cached != null) {
			return cached;
		}
		log.info("Fetching {}", urlStr);
		String fetched = pageFetcher.fetchPage(urlStr);
		pageCache.save(urlStr, fetched);
		return fetched;
	}
}
