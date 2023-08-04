package wow.scraper.fetchers;

/**
 * User: POlszewski
 * Date: 2023-06-23
 */
public interface PageCache {
	String fetch(String urlStr);

	void save(String urlStr, String value);
}
