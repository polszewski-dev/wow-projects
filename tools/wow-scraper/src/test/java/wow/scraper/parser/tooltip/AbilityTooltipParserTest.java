package wow.scraper.parser.tooltip;

import org.junit.jupiter.api.Test;
import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.spell.CastInfo;
import wow.commons.model.spell.Coefficient;
import wow.commons.model.spell.Cost;
import wow.commons.model.spell.Reagent;
import wow.scraper.model.JsonSpellDetails;
import wow.scraper.model.WowheadSpellCategory;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.character.CharacterClassId.WARLOCK;
import static wow.commons.model.pve.GameVersionId.TBC;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.commons.model.talent.TalentTree.DESTRUCTION;
import static wow.scraper.model.WowheadSpellCategory.ABILITIES_WARLOCK_DESTRO;

/**
 * User: POlszewski
 * Date: 2023-10-01
 */
class AbilityTooltipParserTest extends TooltipParserTest<JsonSpellDetails, AbilityTooltipParser, WowheadSpellCategory> {
	@Test
	void shadowBolt() {
		AbilityTooltipParser parser = getTooltip(27209, ABILITIES_WARLOCK_DESTRO);

		assertThat(parser.getName()).isEqualTo("Shadow Bolt");
		assertThat(parser.getRank()).isEqualTo(11);
		assertThat(parser.getCharacterClass()).isEqualTo(WARLOCK);
		assertThat(parser.getTalentTree()).isEqualTo(DESTRUCTION);
		assertThat(parser.getGameVersion()).isEqualTo(TBC);
		assertThat(parser.getIcon()).isEqualTo("spell_shadow_shadowbolt");

		assertThat(parser.getCost()).isEqualTo(new Cost(MANA, 420, Percent.ZERO, Coefficient.NONE, null));
		assertThat(parser.getCastInfo()).isEqualTo(new CastInfo(Duration.seconds(3), false, false));
		assertThat(parser.getCooldown()).isEqualTo(Duration.ZERO);
		assertThat(parser.isTalent()).isFalse();

		assertThat(parser.getAbilityMatcher()).isNotNull();

		var ability = parser.getAbilityMatcher().getAbility();

		assertThat(ability.getDirectCommands()).hasSize(1);
		assertThat(ability.getEffectApplication()).isNull();
	}

	@Test
	void shadowBurn() {
		AbilityTooltipParser parser = getTooltip(30546, ABILITIES_WARLOCK_DESTRO);

		assertThat(parser.getName()).isEqualTo("Shadowburn");
		assertThat(parser.getRank()).isEqualTo(8);
		assertThat(parser.getCharacterClass()).isEqualTo(WARLOCK);
		assertThat(parser.getTalentTree()).isEqualTo(DESTRUCTION);
		assertThat(parser.getGameVersion()).isEqualTo(TBC);
		assertThat(parser.getIcon()).isEqualTo("spell_shadow_scourgebuild");

		assertThat(parser.getCost()).isEqualTo(new Cost(MANA, 515, Percent.ZERO, Coefficient.NONE, Reagent.SOUL_SHARD));
		assertThat(parser.getCastInfo()).isEqualTo(new CastInfo(Duration.ZERO, false, false));
		assertThat(parser.getCooldown()).isEqualTo(Duration.seconds(15));
		assertThat(parser.isTalent()).isTrue();

		assertThat(parser.getAbilityMatcher()).isNotNull();

		var ability = parser.getAbilityMatcher().getAbility();

		assertThat(ability.getDirectCommands()).hasSize(1);
		assertThat(ability.getEffectApplication()).isNull();
	}

	@Test
	void immolate() {
		AbilityTooltipParser parser = getTooltip(27215, ABILITIES_WARLOCK_DESTRO);

		assertThat(parser.getName()).isEqualTo("Immolate");
		assertThat(parser.getRank()).isEqualTo(9);
		assertThat(parser.getCharacterClass()).isEqualTo(WARLOCK);
		assertThat(parser.getTalentTree()).isEqualTo(DESTRUCTION);
		assertThat(parser.getGameVersion()).isEqualTo(TBC);
		assertThat(parser.getIcon()).isEqualTo("spell_fire_immolation");

		assertThat(parser.getCost()).isEqualTo(new Cost(MANA, 445, Percent.ZERO, Coefficient.NONE, null));
		assertThat(parser.getCastInfo()).isEqualTo(new CastInfo(Duration.seconds(2), false, false));
		assertThat(parser.getCooldown()).isEqualTo(Duration.ZERO);
		assertThat(parser.isTalent()).isFalse();

		assertThat(parser.getAbilityMatcher()).isNotNull();

		var ability = parser.getAbilityMatcher().getAbility();

		assertThat(ability.getDirectCommands()).hasSize(1);
		assertThat(ability.getEffectApplication()).isNotNull();
	}

	@Override
	protected JsonSpellDetails getData(int id, WowheadSpellCategory category) {
		return spellDetailRepository.getDetail(TBC, category, id).orElseThrow();
	}

	@Override
	protected AbilityTooltipParser createParser(JsonSpellDetails data) {
		return new AbilityTooltipParser(data, scraperContext);
	}
}