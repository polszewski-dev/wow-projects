package wow.scraper;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import wow.commons.util.parser.ParserUtil;
import wow.scraper.fetcher.PageCache;
import wow.scraper.fetcher.PageFetcher;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-12-02
 */
@ComponentScan(basePackages = {
		"wow.scraper"
})
@PropertySource("classpath:test.properties")
public class ScraperSpringTestConfig {
	@Primary
	@Bean
	public PageFetcher pageFetcher(PageCache pageCache) {
		return new DummyPageFetcher();
	}

	@Slf4j
	private static class DummyPageFetcher implements PageFetcher {
		@Override
		public String fetchPage(String urlStr) {
			urlStr = removeSuffixes(urlStr);

			var result = ParserUtil.parseMultipleValues("https://www.wowhead.com/([^/]+)/([^/]+)/(.*/)?([^/]+)", urlStr);

			if (!result.isEmpty()) {
				var version = result.get(0);
				var type = result.get(1);
				var subtype = changeToMoreObviousName(result.get(3));

				var middle = result.get(2);

				if (middle != null && (middle.startsWith("talents") || middle.startsWith("abilities"))) {
					subtype = middle.replace("/", "-") + subtype;
				}

				if (middle != null && middle.contains("cloth")) {
					subtype = subtype + "_cloth";
				}

				return read("/wowhead/%s/%s/%s".formatted(version, type, subtype)).orElse(getEmptyResponse(type));
			}

			result = ParserUtil.parseMultipleValues("https://nether.wowhead.com/tooltip/([^/]+)/(\\d+)\\?dataEnv=(\\d+)&locale=0", urlStr);

			if (!result.isEmpty()) {
				var type = result.get(0);
				var id = result.getInteger(1);
				var dataEnv = result.getInteger(2);

				return read("/tooltip/%s/%s/%s".formatted(getVersionFromDataEnv(dataEnv), type, id)).orElseThrow();
			}

			result = ParserUtil.parseMultipleValues("https://www.wowhead.com/([^/]+)/(.+)=(\\d+)", urlStr);

			if (!result.isEmpty()) {
				var version = result.get(0);
				var type = result.get(1);
				var id = result.getInteger(2);

				return read("/wowhead/%s/%s_%s".formatted(version, type, id)).orElseThrow();
			}

			result = ParserUtil.parseMultipleValues("https://www.wowhead.com/([^/]+)/([^?]+)\\?filter-any=(.+)", urlStr);

			if (!result.isEmpty()) {
				var version = result.get(0);
				var type = result.get(1);
				var ids = parseIds(result.get(2));

				if (type.equals("spells") && ids.contains(30546)) {
					return read("/wowhead/%s/%s/spells_filter-any_30546".formatted(version, type)).orElseThrow();
				}

				return getEmptyResponse("type");
			}

			log.error("Unknown address: " + urlStr);
			throw new IllegalArgumentException(urlStr);
		}

		private List<Integer> parseIds(String queryString) {
			var idString = queryString.substring(queryString.lastIndexOf(";") + 1);

			return Stream.of(idString.split(":"))
					.map(Integer::valueOf)
					.toList();
		}

		private String changeToMoreObviousName(String subtype) {
			return switch (subtype) {
				case "slot:1" -> "head";
				case "slot:5" -> "chest";
				case "slot:6" -> "waist";
				default -> subtype;
			};
		}

		private String removeSuffixes(String urlStr) {
			var suffixes = new String [] {
					"/quality:3:4:5?filter=6;1;0",
					"/type:0:1:2:3:4:5:6:8?filter=81;7;0"
			};

			for (var suffix : suffixes) {
				if (urlStr.endsWith(suffix)) {
					urlStr = urlStr.substring(0, urlStr.length() - suffix.length());
				}
			}

			return urlStr;
		}

		private String getEmptyResponse(String type) {
			return switch (type) {
				case "items" -> "var listviewitems = [];";
				case "spells" -> "var listviewspells = [];";
				default -> throw new IllegalArgumentException();
			};
		}

		private String getVersionFromDataEnv(int dataEnv) {
			return switch (dataEnv) {
				case 4 -> "classic";
				case 5 -> "tbc";
				case 6 -> "wotlk";
				default -> throw new IllegalArgumentException();
			};
		}

		@SneakyThrows
		private Optional<String> read(String path) {
			try (var input = getClass().getResourceAsStream(path)) {
				if (input == null) {
					return Optional.empty();
				}
				var text = new BufferedReader(new InputStreamReader(input))
						.lines()
						.collect(Collectors.joining("\n"));
				return Optional.of(text);
			}
		}
	}
}
