package wow.character.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.character.WowCharacterSpringTest;
import wow.character.model.character.CharacterTemplate;
import wow.character.util.TalentLinkParser;
import wow.commons.model.buff.BuffId;
import wow.commons.model.categorization.PveRole;
import wow.commons.model.pve.PhaseId;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.buff.BuffId.*;
import static wow.commons.model.character.CharacterClassId.WARLOCK;
import static wow.commons.model.character.ExclusiveFaction.SCRYERS;
import static wow.commons.model.profession.ProfessionId.ENCHANTING;
import static wow.commons.model.profession.ProfessionId.TAILORING;
import static wow.commons.model.profession.ProfessionSpecializationId.SHADOWEAVE_TAILORING;
import static wow.commons.model.spell.AbilityId.*;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
class CharacterTemplateRepositoryTest extends WowCharacterSpringTest {
	@Autowired
	CharacterTemplateRepository underTest;

	@Test
	void getCharacterTemplate() {
		Optional<CharacterTemplate> optionalCharacterTemplate = underTest.getCharacterTemplate(WARLOCK_TEMPLATE_NAME, WARLOCK, 70, PhaseId.TBC_P5);

		assertThat(optionalCharacterTemplate).isPresent();

		CharacterTemplate characterTemplate = optionalCharacterTemplate.orElseThrow();

		assertThat(characterTemplate.getName()).isEqualTo(WARLOCK_TEMPLATE_NAME);
		assertThat(characterTemplate.getLevel()).isEqualTo(70);
		assertThat(characterTemplate.getCharacterClassId()).isEqualTo(WARLOCK);
		var link = TalentLinkParser.parse("https://www.wowhead.com/tbc/talent-calc/warlock/-20501301332001-55500051221001303025", talentRepository);
		assertThat(characterTemplate.getTalentLink()).isEqualTo(link);
		assertThat(characterTemplate.getRole()).isEqualTo(PveRole.CASTER_DPS);
		assertThat(characterTemplate.getDefaultRotationTemplate().getAbilityIds()).isEqualTo(List.of(CURSE_OF_DOOM, CORRUPTION, IMMOLATE, SHADOW_BOLT));
		assertThat(characterTemplate.getActivePet()).isNull();
		assertThat(characterTemplate.getDefaultBuffs()).hasSameElementsAs(List.of(
				FEL_ARMOR_IMPROVED, TOUCH_OF_SHADOW, BuffId.ARCANE_BRILLIANCE, BuffId.PRAYER_OF_FORTITUDE, BuffId.PRAYER_OF_SPIRIT,
				GIFT_OF_THE_WILD, GREATER_BLESSING_OF_KINGS, WRATH_OF_AIR_TOTEM,
				TOTEM_OF_WRATH, WELL_FED_SP, BRILLIANT_WIZARD_OIL, FLASK_OF_PURE_DEATH
		));
		assertThat(characterTemplate.getDefaultDebuffs()).hasSameElementsAs(List.of(
				BuffId.CURSE_OF_THE_ELEMENTS
		));
		assertThat(characterTemplate.getProfessions().get(0).getProfessionId()).isEqualTo(ENCHANTING);
		assertThat(characterTemplate.getProfessions().get(0).getSpecializationId()).isNull();
		assertThat(characterTemplate.getProfessions().get(1).getProfessionId()).isEqualTo(TAILORING);
		assertThat(characterTemplate.getProfessions().get(1).getSpecializationId()).isEqualTo(SHADOWEAVE_TAILORING);
		assertThat(characterTemplate.getExclusiveFactions()).hasSameElementsAs(List.of(SCRYERS));
	}

	@Test
	void getDefaultCharacterTemplate() {
		var characterTemplate = underTest.getDefaultCharacterTemplate(WARLOCK, 70, PhaseId.TBC_P5).orElseThrow();

		assertThat(characterTemplate.getName()).isEqualTo(WARLOCK_TEMPLATE_NAME);
		assertThat(characterTemplate.isDefault()).isTrue();
	}
}
