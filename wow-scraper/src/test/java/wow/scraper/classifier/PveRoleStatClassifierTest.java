package wow.scraper.classifier;

import org.junit.jupiter.api.Test;
import wow.commons.model.categorization.PveRole;
import wow.commons.model.effect.Effect;
import wow.commons.model.pve.GameVersionId;
import wow.scraper.ScraperSpringTest;
import wow.scraper.parser.effect.ItemStatParser;
import wow.scraper.parser.stat.StatParser;

import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.categorization.PveRole.CASTER_DPS;

/**
 * User: POlszewski
 * Date: 2023-10-15
 */
class PveRoleStatClassifierTest extends ScraperSpringTest {
	@Test
	void sd() {
		var roles = classifyGem(
				"+9 Spell Damage"
		);

		assertThat(roles).isEqualTo(List.of(CASTER_DPS));
	}

	@Test
	void healing() {
		var roles = classifyGem(
				"+18 Healing"
		);

		assertThat(roles).isEmpty();
	}

	@Test
	void healingSd() {
		var roles = classifyGem(
				"+9 Healing +3 Spell Damage"
		);

		assertThat(roles).isEmpty();
	}

	@Test
	void healingSdSeparate() {
		var roles = classifyGem(
				"+13 Healing",
				"+5 Spell Damage"
		);

		assertThat(roles).isEmpty();
	}

	private List<PveRole> classifyGem(String... lines) {
		var effects = parseGem(List.of(lines));

		return PveRoleStatClassifier.classify(effects, null);
	}

	private List<Effect> parseGem(List<String> lines) {
		return parse(lines, statPatternRepository::getGemStatParser);
	}

	private List<Effect> parse(List<String> lines, Function<GameVersionId, StatParser> statParserFactory) {
		var parser = new ItemStatParser(
				GameVersionId.TBC,
				statParserFactory,
				scraperContext.getItemSpellRepository()
		);

		for (String line : lines) {
			var success = parser.tryParseItemEffect(line);
			assertThat(success).isTrue();
		}

		return parser.getItemEffects();
	}
}