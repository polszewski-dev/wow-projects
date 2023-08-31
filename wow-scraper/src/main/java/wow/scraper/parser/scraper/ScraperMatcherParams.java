package wow.scraper.parser.scraper;

import lombok.Getter;

/**
 * User: POlszewski
 * Date: 2023-08-29
 */
@Getter
public abstract class ScraperMatcherParams {
	private final String originalLine;
	protected String line;

	protected ScraperMatcherParams(String line) {
		this.originalLine = line;
		this.line = line;
	}

	protected void removeNoise() {
		this.line = line.trim();
		removePrefixes();
		removeSuffixes();
	}

	private void removePrefixes() {
		String[] prefixes = {
				"Equip: "
		};

		for (String prefix : prefixes) {
			if (line.startsWith(prefix)) {
				line = line.substring(prefix.length());
			}
		}

		line = line.trim();
	}

	private void removeSuffixes() {
		while (line.endsWith(".")) {
			this.line = line.substring(0, line.length() - ".".length());
		}

		this.line = line.trim();
	}
}
