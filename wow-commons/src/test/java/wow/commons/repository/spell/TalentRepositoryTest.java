package wow.commons.repository.spell;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import wow.commons.WowCommonsSpringTest;
import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.condition.AttributeCondition;
import wow.commons.model.attribute.condition.ConditionOperator;
import wow.commons.model.attribute.condition.MiscCondition;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.spell.SpellTarget;
import wow.commons.model.talent.Talent;
import wow.commons.model.talent.TalentId;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.attribute.AttributeId.*;
import static wow.commons.model.character.CharacterClassId.PRIEST;
import static wow.commons.model.character.CharacterClassId.WARLOCK;
import static wow.commons.model.effect.component.EventAction.TRIGGER_SPELL;
import static wow.commons.model.effect.component.EventType.SPELL_CRIT;
import static wow.commons.model.pve.GameVersionId.TBC;
import static wow.commons.model.pve.PhaseId.TBC_P5;
import static wow.commons.model.spell.AbilityId.*;
import static wow.commons.model.spell.Conversion.From.DAMAGE_DONE;
import static wow.commons.model.spell.Conversion.To.PARTY_HEALTH;
import static wow.commons.model.spell.SpellSchool.SHADOW;
import static wow.commons.model.talent.TalentId.*;
import static wow.commons.model.talent.TalentTree.AFFLICTION;

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

		assertThat(talent.getId()).isEqualTo(17810);
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
				Attribute.of(CAST_TIME, value, AttributeCondition.of(CORRUPTION))
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

		var event = effect.getEvents().get(0);

		assertEvent(
				event,
				List.of(SPELL_CRIT),
				AttributeCondition.of(SHADOW_BOLT),
				100,
				List.of(TRIGGER_SPELL),
				Duration.ZERO
		);

		var spell = event.triggeredSpell();

		assertEffectApplication(spell, SpellTarget.TARGET, 12, 4, rank, 5);

		var appliedEffect = spell.getEffectApplication().effect();

		assertModifier(appliedEffect, List.of(
				Attribute.of(DAMAGE_PCT, 4, AttributeCondition.of(SHADOW))
		));
	}

	@Test
	void talentEffectWithMultipleAttributes() {
		var talent = getTalent(WARLOCK, BANE, 5, TBC_P5);

		assertModifier(talent.getEffect(), List.of(
				Attribute.of(CAST_TIME, -0.5, ConditionOperator.comma(
						AttributeCondition.of(SHADOW_BOLT),
						AttributeCondition.of(IMMOLATE)
				)),
				Attribute.of(CAST_TIME, -2, AttributeCondition.of(SOUL_FIRE))
		));
	}

	@Test
	void talentWithConversion() {
		var talent = getTalent(PRIEST, IMPROVED_VAMPIRIC_EMBRACE, 2, TBC_P5);
		var effect = talent.getEffect();

		assertThat(effect.getAugmentedAbility()).isEqualTo(AbilityId.VAMPIRIC_EMBRACE);
		assertConversion(effect.getConversion(), AttributeCondition.of(SHADOW), DAMAGE_DONE, PARTY_HEALTH, 10);
	}

	@Test
	void talentWithStatConversion() {
		var talent = getTalent(PRIEST, IMPROVED_DIVINE_SPIRIT, 2, TBC_P5);
		var effect = talent.getEffect();

		assertThat(effect.getAugmentedAbility()).isEqualTo(AbilityId.DIVINE_SPIRIT);
		assertStatConversion(effect, 0, SPIRIT, POWER, 10, MiscCondition.SPELL);
	}

	@Test
	void talentWithDoubleConversion() {
		var talent = getTalent(WARLOCK, DEMONIC_KNOWLEDGE, 3, TBC_P5);
		var effect = talent.getEffect();

		assertStatConversion(effect, 0, PET_STAMINA, POWER, 12, MiscCondition.SPELL_DAMAGE);
		assertStatConversion(effect, 1, PET_INTELLECT, POWER, 12, MiscCondition.SPELL_DAMAGE);
	}

	@Test
	void talentWithEffectIncrease() {
		var talent = getTalent(WARLOCK, SOUL_SIPHON, 2, TBC_P5);
		var effect = talent.getEffect();
		var effectIncrease = effect.getEffectIncreasePerEffectOnTarget();

		assertThat(effectIncrease).isNotNull();
		assertThat(effectIncrease.condition()).isEqualTo(AttributeCondition.of(AFFLICTION));
		assertThat(effectIncrease.value()).isEqualTo(Percent.of(4));
		assertThat(effectIncrease.max()).isEqualTo(Percent.of(60));
	}

	private Talent getTalent(CharacterClassId classId, TalentId talentId, int rank, PhaseId phaseId) {
		return talentRepository.getTalent(classId, talentId, rank, phaseId).orElseThrow();
	}
}
