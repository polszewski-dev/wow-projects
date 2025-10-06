package wow.commons.repository.spell;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import wow.commons.WowCommonsSpringTest;
import wow.commons.constant.AttributeConditions;
import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.AttributeCondition;
import wow.commons.model.attribute.AttributeScaling;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.spell.SpellTarget;
import wow.commons.model.talent.Talent;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.constant.AbilityIds.DIVINE_SPIRIT;
import static wow.commons.constant.AbilityIds.PRAYER_OF_SPIRIT;
import static wow.commons.model.attribute.AttributeId.*;
import static wow.commons.model.character.CharacterClassId.PRIEST;
import static wow.commons.model.character.CharacterClassId.WARLOCK;
import static wow.commons.model.effect.component.EventAction.TRIGGER_SPELL;
import static wow.commons.model.effect.component.EventType.SPELL_CRIT;
import static wow.commons.model.pve.GameVersionId.TBC;
import static wow.commons.model.pve.PhaseId.TBC_P5;
import static wow.commons.model.talent.TalentTree.AFFLICTION;
import static wow.test.commons.TalentNames.*;

/**
 * User: POlszewski
 * Date: 2022-11-11
 */
class TalentRepositoryTest extends WowCommonsSpringTest {
	@Autowired
	TalentRepository talentRepository;

	@Test
	void talentInfo() {
		var talent = getTalent(WARLOCK, IMPROVED_CORRUPTION, 1, TBC_P5);

		assertId(talent, 17810);
		assertThat(talent.getName()).isEqualTo("Improved Corruption");
		assertThat(talent.getRank()).isEqualTo(1);
		assertThat(talent.getMaxRank()).isEqualTo(5);
		assertThat(talent.getTalentCalculatorPosition()).isEqualTo(2);
		assertThat(talent.getTimeRestriction()).isEqualTo(TimeRestriction.of(TBC));
		assertThat(talent.getCharacterClass()).isEqualTo(WARLOCK);
		assertThat(talent.getTalentTree()).isEqualTo(AFFLICTION);
		assertThat(talent.getIcon()).isEqualTo("spell_shadow_abominationexplosion");
		assertThat(talent.getTooltip()).isEqualTo("Reduces the casting time of your Corruption spell by 0.4 sec.");
	}

	@ParameterizedTest
	@CsvSource({
			"1, -0.4",
			"2, -0.8",
			"3, -1.2",
			"4, -1.6",
			"5, -2.0"
	})
	void talentModifier(int rank, double value) {
		Talent talent = getTalent(WARLOCK, IMPROVED_CORRUPTION, rank, TBC_P5);

		assertModifier(talent.getEffect(), List.of(
				Attribute.of(CAST_TIME, value, AttributeConditions.CORRUPTION)
		));
	}

	@Test void talentWithTwoAttributeTagets() {
		Talent talent = getTalent(WARLOCK, FEL_INTELLECT, 3, TBC_P5);

		assertModifier(talent.getEffect(), List.of(
				Attribute.of(PET_INTELLECT_PCT, 15),
				Attribute.of(MAX_MANA_PCT, 3)
		));
	}

	@ParameterizedTest
	@CsvSource({
			"1, VANILLA_P6",
			"2, VANILLA_P6",
			"3, VANILLA_P6",
			"4, VANILLA_P6",
			"5, VANILLA_P6",
			"1, TBC_P5",
			"2, TBC_P5",
			"3, TBC_P5",
			"4, TBC_P5",
			"5, TBC_P5",
	})
	void talentProc(int rank, PhaseId phaseId) {
		var talent = getTalent(WARLOCK, IMPROVED_SHADOW_BOLT, rank, phaseId);

		assertThat(talent.getTooltip()).isEqualTo(
				"Your Shadow Bolt critical strikes increase Shadow damage dealt to the target by " + (rank * 4) + "% until 4 non-periodic damage sources are applied. Effect lasts a maximum of 12 sec."
		);

		var effect = talent.getEffect();

		assertThat(effect.getEvents()).hasSize(1);

		var event = effect.getEvents().getFirst();

		assertEvent(
				event,
				List.of(SPELL_CRIT),
				AttributeConditions.SHADOW_BOLT,
				100,
				List.of(TRIGGER_SPELL),
				Duration.ZERO
		);

		var spell = event.triggeredSpell();

		assertEffectApplication(spell, SpellTarget.TARGET, 12, 4, 1, 1);

		var appliedEffect = spell.getEffectApplication().effect();

		assertModifier(appliedEffect, List.of(
				Attribute.of(DAMAGE_TAKEN_PCT, rank * 4, AttributeConditions.SHADOW)
		));
	}

	@Test
	void talentEffectWithMultipleAttributes() {
		var talent = getTalent(WARLOCK, BANE, 5, TBC_P5);

		assertModifier(talent.getEffect(), List.of(
				Attribute.of(CAST_TIME, -0.5, AttributeCondition.comma(
						AttributeConditions.SHADOW_BOLT,
						AttributeConditions.IMMOLATE
				)),
				Attribute.of(CAST_TIME, -2, AttributeConditions.SOUL_FIRE)
		));
	}

	@Test
	void talentWithStatConversion() {
		var talent = getTalent(PRIEST, IMPROVED_DIVINE_SPIRIT, 2, TBC_P5);
		var effect = talent.getEffect();

		assertThat(effect.getAugmentedAbilities()).isEqualTo(List.of(DIVINE_SPIRIT, PRAYER_OF_SPIRIT));
		assertStatConversion(effect, 0, SPIRIT, POWER, 10, AttributeConditions.SPELL);
	}

	@Test
	void talentWithDoubleConversion() {
		var talent = getTalent(WARLOCK, DEMONIC_KNOWLEDGE, 3, TBC_P5);
		var effect = talent.getEffect();

		assertStatConversion(effect, 0, PET_STAMINA, POWER, 12, AttributeConditions.SPELL_DAMAGE);
		assertStatConversion(effect, 1, PET_INTELLECT, POWER, 12, AttributeConditions.SPELL_DAMAGE);
	}

	@Test
	void talentWithEffectIncrease() {
		var talent = getTalent(WARLOCK, SOUL_SIPHON, 2, TBC_P5);
		var effect = talent.getEffect();
		var effectIncrease = effect.getModifierAttributeList().getFirst();

		assertThat(effectIncrease.id()).isEqualTo(DAMAGE_PCT);
		assertThat(effectIncrease.condition()).isEqualTo(AttributeConditions.DRAIN_LIFE);
		assertThat(effectIncrease.value()).isEqualTo(4);
		assertThat(effectIncrease.scaling()).isEqualTo(new AttributeScaling.NumberOfEffectsOnTarget(
				AFFLICTION, Percent.of(60)
		));
	}

	private Talent getTalent(CharacterClassId classId, String name, int rank, PhaseId phaseId) {
		return talentRepository.getTalent(classId, name, rank, phaseId).orElseThrow();
	}
}
