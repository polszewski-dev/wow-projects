package wow.character.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import wow.character.WowCharacterSpringTest;
import wow.character.model.character.ProfIdSpecId;
import wow.character.util.TalentLinkParser;
import wow.commons.model.categorization.PveRole;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.character.CharacterClassId.WARLOCK;
import static wow.commons.model.profession.ProfessionId.ENCHANTING;
import static wow.commons.model.profession.ProfessionId.TAILORING;
import static wow.commons.model.profession.ProfessionSpecializationId.SHADOWEAVE_TAILORING;
import static wow.commons.model.pve.PhaseId.TBC_P5;
import static wow.test.commons.BuffNames.*;
import static wow.test.commons.ExclusiveFactionNames.SCRYERS;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
class CharacterTemplateRepositoryTest extends WowCharacterSpringTest {
	@Autowired
	CharacterTemplateRepository underTest;

	@Test
	void getCharacterTemplate() {
		var character = getCharacter();
		var optionalCharacterTemplate = underTest.getCharacterTemplate(WARLOCK_TEMPLATE_NAME, character);

		assertThat(optionalCharacterTemplate).isPresent();

		var characterTemplate = optionalCharacterTemplate.orElseThrow();

		assertThat(characterTemplate.getName()).isEqualTo(WARLOCK_TEMPLATE_NAME);
		assertThat(characterTemplate.getRequiredLevel()).isEqualTo(70);
		assertThat(characterTemplate.getRequiredCharacterClassIds()).isEqualTo(List.of(WARLOCK));

		var link = TalentLinkParser.parse("https://www.wowhead.com/tbc/talent-calc/warlock/-20501301332001-55500051221001303025", talentRepository);

		assertThat(characterTemplate.getTalentLink()).isEqualTo(link);
		assertThat(characterTemplate.getRequiredRole()).isEqualTo(PveRole.CASTER_DPS);
		assertThat(characterTemplate.getDefaultScript()).isEqualTo("warlock-destro-shadow");
		assertThat(characterTemplate.getActivePet()).isNull();
		assertThat(characterTemplate.getDefaultBuffs()).hasSameElementsAs(List.of(
				FEL_ARMOR,
				TOUCH_OF_SHADOW,
				ARCANE_BRILLIANCE,
				PRAYER_OF_FORTITUDE,
				PRAYER_OF_SPIRIT,
				GIFT_OF_THE_WILD,
				GREATER_BLESSING_OF_KINGS,
				WRATH_OF_AIR_TOTEM,
				TOTEM_OF_WRATH,
				WELL_FED_SP,
				BRILLIANT_WIZARD_OIL,
				FLASK_OF_PURE_DEATH
		));
		assertThat(characterTemplate.getDefaultDebuffs()).hasSameElementsAs(List.of(
				CURSE_OF_THE_ELEMENTS
		));
		assertThat(characterTemplate.getProfessions()).isEqualTo(List.of(
				new ProfIdSpecId(ENCHANTING, null),
				new ProfIdSpecId(TAILORING, SHADOWEAVE_TAILORING)
		));
		assertThat(characterTemplate.getExclusiveFactions()).hasSameElementsAs(List.of(SCRYERS));
	}

	@Test
	void getDefaultCharacterTemplate() {
		var character = getCharacter();
		var characterTemplate = underTest.getDefaultCharacterTemplate(character).orElseThrow();

		assertThat(characterTemplate.getName()).isEqualTo(WARLOCK_TEMPLATE_NAME);
		assertThat(characterTemplate.isDefault()).isTrue();
	}

	@ParameterizedTest
	@ValueSource(ints = { 10, 20, 30, 40, 50, 60, 70 })
	void namedTemplatePresentForEachLevel(int level) {
		var character = getCharacter(WARLOCK, RACE, level, TBC_P5);
		var optionalCharacterTemplate = underTest.getCharacterTemplate(WARLOCK_TEMPLATE_NAME, character);

		assertThat(optionalCharacterTemplate).isPresent();

		var characterTemplate = optionalCharacterTemplate.orElseThrow();

		assertThat(characterTemplate.getRequiredLevel()).isEqualTo(level);
	}

	@ParameterizedTest
	@ValueSource(ints = { 10, 20, 30, 40, 50, 60, 70 })
	void defaultTemplatePresentForEachLevel(int level) {
		var character = getCharacter(WARLOCK, RACE, level, TBC_P5);
		var optionalCharacterTemplate = underTest.getDefaultCharacterTemplate(character);

		assertThat(optionalCharacterTemplate).isPresent();

		var characterTemplate = optionalCharacterTemplate.orElseThrow();

		assertThat(characterTemplate.getRequiredLevel()).isEqualTo(level);
	}
}
