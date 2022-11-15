package wow.scraper;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import wow.scraper.repository.ItemDetailRepository;
import wow.scraper.repository.WowheadFetcher;

/**
 * User: POlszewski
 * Date: 2022-11-15
 */
public abstract class ScraperTool {
	private final ApplicationContext context;

	protected ScraperTool() {
		this.context = new AnnotationConfigApplicationContext("wow.scraper");
	}

	protected abstract void run() throws Exception;

	protected WowheadFetcher getWowheadFetcher() {
		return context.getBean(WowheadFetcher.class);
	}

	protected ItemDetailRepository getItemDetailRepository() {
		return context.getBean(ItemDetailRepository.class);
	}
}
