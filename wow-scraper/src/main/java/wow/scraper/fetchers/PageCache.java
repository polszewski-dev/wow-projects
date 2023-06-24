package wow.scraper.fetchers;

import java.io.IOException;

/**
 * User: POlszewski
 * Date: 2023-06-23
 */
public interface PageCache {
	String fetch(String urlStr) throws IOException;

	void save(String urlStr, String value) throws IOException;
}
