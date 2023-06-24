package wow.scraper.fetchers;

import java.io.IOException;

/**
 * User: POlszewski
 * Date: 2023-06-23
 */
public interface PageFetcher {
	String fetchPage(String urlStr) throws IOException;
}
