package wow.scraper;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import wow.commons.model.pve.GameVersionId;
import wow.scraper.config.ScraperConfig;
import wow.scraper.parsers.stats.StatPatternRepository;
import wow.scraper.repository.ItemDetailRepository;
import wow.scraper.repository.QuestInfoRepository;
import wow.scraper.repository.WowheadFetcher;

import java.io.IOException;

/**
 * User: POlszewski
 * Date: 2022-11-15
 */
public abstract class ScraperTool {
	private final ApplicationContext context;

	protected ScraperTool() {
		this.context = new AnnotationConfigApplicationContext("wow.scraper");
	}

	protected abstract void run() throws IOException;

	protected WowheadFetcher getWowheadFetcher() {
		return context.getBean(WowheadFetcher.class);
	}

	protected ItemDetailRepository getItemDetailRepository() {
		return context.getBean(ItemDetailRepository.class);
	}

	protected QuestInfoRepository getQuestInfoRepository() {
		return context.getBean(QuestInfoRepository.class);
	}

	protected StatPatternRepository getStatPatternRepository() {
		return context.getBean(StatPatternRepository.class);
	}

	protected ScraperConfig getScraperConfig() {
		return context.getBean(ScraperConfig.class);
	}

	protected GameVersionId getGameVersion() {
		return getScraperConfig().getGameVersion();
	}
}
