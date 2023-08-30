package wow.scraper.parsers.spell;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import wow.commons.model.Duration;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.spells.ResourceType;
import wow.commons.model.spells.SpellId;
import wow.scraper.ScraperTestConfig;
import wow.scraper.repository.SpellPatternRepository;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: POlszewski
 * Date: 2023-08-29
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ScraperTestConfig.class)
class SpellParserTest {
	@Autowired
	SpellPatternRepository spellPatternRepository;

	@Test
	void parseIncinerate() {
		SpellMatcher matcher = getMatcher(SpellId.INCINERATE, "Deals 444 to 514 Fire damage to your target and an additional 111 to 128 Fire damage if the target is affected by an Immolate spell.");

		assertThat(matcher.getMinDmg()).hasValue(444);
		assertThat(matcher.getMaxDmg()).hasValue(514);
		assertThat(matcher.getMinDmg2()).hasValue(111);
		assertThat(matcher.getMaxDmg2()).hasValue(128);
		assertThat(matcher.getDotDmg()).isEmpty();
		assertThat(matcher.getTickDmg()).isEmpty();
		assertThat(matcher.getTickInterval()).isEmpty();
		assertThat(matcher.getDotDuration()).isEmpty();
		assertThat(matcher.getCostAmount()).isEmpty();
		assertThat(matcher.getCostType()).isEmpty();
	}

	@Test
	void parseImmolate() {
		SpellMatcher matcher = getMatcher(SpellId.IMMOLATE, "Burns the enemy for 332 Fire damage and then an additional 615 Fire damage over 15 sec.");

		assertThat(matcher.getMinDmg()).hasValue(332);
		assertThat(matcher.getMaxDmg()).hasValue(332);
		assertThat(matcher.getMinDmg2()).isEmpty();
		assertThat(matcher.getMaxDmg2()).isEmpty();
		assertThat(matcher.getDotDmg()).hasValue(615);
		assertThat(matcher.getTickDmg()).isEmpty();
		assertThat(matcher.getTickInterval()).hasValue(Duration.seconds(3));
		assertThat(matcher.getDotDuration()).hasValue(Duration.seconds(15));
		assertThat(matcher.getCostAmount()).isEmpty();
		assertThat(matcher.getCostType()).isEmpty();
	}

	@Test
	void parseDrainLife() {
		SpellMatcher matcher = getMatcher(SpellId.DRAIN_LIFE, "Transfers 108 health every 1 sec from the target to the caster.  Lasts 5 sec.");

		assertThat(matcher.getMinDmg()).isEmpty();
		assertThat(matcher.getMaxDmg()).isEmpty();
		assertThat(matcher.getMinDmg2()).isEmpty();
		assertThat(matcher.getMaxDmg2()).isEmpty();
		assertThat(matcher.getDotDmg()).isEmpty();
		assertThat(matcher.getTickDmg()).hasValue(108);
		assertThat(matcher.getTickInterval()).hasValue(Duration.seconds(1));
		assertThat(matcher.getDotDuration()).hasValue(Duration.seconds(5));
		assertThat(matcher.getCostAmount()).isEmpty();
		assertThat(matcher.getCostType()).isEmpty();
	}

	@Test
	void parseLifeTap() {
		SpellMatcher matcher = getMatcher(SpellId.LIFE_TAP, "Converts 582 health into 582 mana.");

		assertThat(matcher.getMinDmg()).isEmpty();
		assertThat(matcher.getMaxDmg()).isEmpty();
		assertThat(matcher.getMinDmg2()).isEmpty();
		assertThat(matcher.getMaxDmg2()).isEmpty();
		assertThat(matcher.getDotDmg()).isEmpty();
		assertThat(matcher.getTickDmg()).isEmpty();
		assertThat(matcher.getTickInterval()).isEmpty();
		assertThat(matcher.getDotDuration()).isEmpty();
		assertThat(matcher.getCostAmount()).hasValue(582);
		assertThat(matcher.getCostType()).hasValue(ResourceType.HEALTH);
	}

	@Test
	void noSpell() {
		SpellParser parser = spellPatternRepository.getSpellParser(SpellId.AMPLIFY_CURSE, GameVersionId.TBC);

		boolean success = parser.tryParse("Increases the effect of your next Curse of Doom or Curse of Agony by 50%, or your next Curse of Exhaustion by an additional 20%.  Lasts 30 sec.");

		assertThat(success).isFalse();
	}

	@Test
	void incorrectDescription() {
		SpellParser parser = spellPatternRepository.getSpellParser(SpellId.LIFE_TAP, GameVersionId.TBC);

		boolean success = parser.tryParse("xyz");

		assertThat(success).isFalse();
	}

	private SpellMatcher getMatcher(SpellId spellId, String line) {
		SpellParser parser = spellPatternRepository.getSpellParser(spellId, GameVersionId.TBC);

		boolean success = parser.tryParse(line);

		assertThat(success).isTrue();

		return parser.getUniqueSuccessfulMatcher();
	}
}