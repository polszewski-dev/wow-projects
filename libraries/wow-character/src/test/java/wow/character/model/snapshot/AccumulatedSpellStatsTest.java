package wow.character.model.snapshot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.character.WowCharacterSpringTest;
import wow.character.repository.BaseStatInfoRepository;
import wow.character.util.AttributeConditionArgs;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.AttributeId;
import wow.commons.model.attribute.condition.MiscCondition;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.spell.SpellId;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.attribute.AttributeId.*;
import static wow.commons.model.character.CharacterClassId.WARLOCK;
import static wow.commons.model.character.RaceId.ORC;
import static wow.commons.model.pve.GameVersionId.TBC;

/**
 * User: POlszewski
 * Date: 2022-12-19
 */
class AccumulatedSpellStatsTest extends WowCharacterSpringTest {
	@Autowired
	BaseStatInfoRepository baseStatInfoRepository;

	@Test
	void accumulateBaseStatInfo() {
		var level = spellStats.conditionArgs.getCaster().getLevel();
		var baseStatInfo = baseStatInfoRepository.getBaseStatInfo(TBC, WARLOCK, ORC, level).orElseThrow();

		spellStats.accumulateBaseStatInfo(baseStatInfo);

		assertThat(spellStats.getCritPct()).isEqualTo(baseStatInfo.getBaseSpellCritPct().value());
	}

	@Test
	void getDamage() {
		accumulateTestAttributes(DAMAGE);
		assertThat(spellStats.getDamage()).isEqualTo(160);
	}

	@Test
	void getDamagePct() {
		accumulateTestAttributes(DAMAGE_PCT);
		assertThat(spellStats.getDamagePct()).isEqualTo(160);
	}

	@Test
	void getEffectPct() {
		accumulateTestAttributes(EFFECT_PCT);
		assertThat(spellStats.getEffectPct()).isEqualTo(160);
	}

	@Test
	void getPower() {
		accumulateTestAttributes(POWER);
		assertThat(spellStats.getPower()).isEqualTo(160);
	}

	@Test
	void getPowerPct() {
		accumulateTestAttributes(POWER_PCT);
		assertThat(spellStats.getPowerPct()).isEqualTo(160);
	}

	@Test
	void getPowerCoeffPct() {
		accumulateTestAttributes(POWER_COEFFICIENT_PCT);
		assertThat(spellStats.getPowerCoeffPct()).isEqualTo(160);
	}

	@Test
	void getCritRating() {
		accumulateTestAttributes(CRIT_RATING);
		assertThat(spellStats.getCritRating()).isEqualTo(160);
	}

	@Test
	void getCritPct() {
		accumulateTestAttributes(CRIT_PCT);
		assertThat(spellStats.getCritPct()).isEqualTo(160);
	}

	@Test
	void getCritDamagePct() {
		accumulateTestAttributes(CRIT_DAMAGE_PCT);
		assertThat(spellStats.getCritDamagePct()).isEqualTo(160);
	}

	@Test
	void getCritDamageMultiplierPct() {
		accumulateTestAttributes(CRIT_DAMAGE_MULTIPLIER_PCT);
		assertThat(spellStats.getCritDamageMultiplierPct()).isEqualTo(160);
	}

	@Test
	void getCritCoeffPct() {
		accumulateTestAttributes(CRIT_COEFF_PCT);
		assertThat(spellStats.getCritCoeffPct()).isEqualTo(160);
	}

	@Test
	void levelScaledAttribute() {
		var spell = spellRepository.getSpell(SpellId.of(33702), PhaseId.TBC_P5).orElseThrow();
		var effect = spell.getAppliedEffect();

		spellStats.accumulateAttributes(effect.getModifierAttributeList(), 1);

		assertThat(spellStats.getPower()).isEqualTo(143);
	}

	@Test
	void copy() {
		spellStats.accumulateAttribute(DAMAGE, 1);
		spellStats.accumulateAttribute(DAMAGE_PCT, 2);
		spellStats.accumulateAttribute(EFFECT_PCT, 3);
		spellStats.accumulateAttribute(POWER, 4);
		spellStats.accumulateAttribute(POWER_PCT, 5);
		spellStats.accumulateAttribute(POWER_COEFFICIENT_PCT, 6);
		spellStats.accumulateAttribute(CRIT_RATING, 7);
		spellStats.accumulateAttribute(CRIT_PCT, 8);
		spellStats.accumulateAttribute(CRIT_DAMAGE_PCT, 9);
		spellStats.accumulateAttribute(CRIT_DAMAGE_MULTIPLIER_PCT, 10);
		spellStats.accumulateAttribute(CRIT_COEFF_PCT, 11);

		var copy = spellStats.copy();

		assertThat(copy.getDamage()).isEqualTo(spellStats.getDamage());
		assertThat(copy.getDamagePct()).isEqualTo(spellStats.getDamagePct());
		assertThat(copy.getEffectPct()).isEqualTo(spellStats.getEffectPct());
		assertThat(copy.getPower()).isEqualTo(spellStats.getPower());
		assertThat(copy.getPowerPct()).isEqualTo(spellStats.getPowerPct());
		assertThat(copy.getPowerCoeffPct()).isEqualTo(spellStats.getPowerCoeffPct());
		assertThat(copy.getCritRating()).isEqualTo(spellStats.getCritRating());
		assertThat(copy.getCritPct()).isEqualTo(spellStats.getCritPct());
		assertThat(copy.getCritDamagePct()).isEqualTo(spellStats.getCritDamagePct());
		assertThat(copy.getCritDamageMultiplierPct()).isEqualTo(spellStats.getCritDamageMultiplierPct());
		assertThat(copy.getCritCoeffPct()).isEqualTo(spellStats.getCritCoeffPct());
	}

	void accumulateTestAttributes(AttributeId attributeId) {
		var list = List.of(
				Attribute.of(attributeId, 10),
				Attribute.of(attributeId, 20, MiscCondition.PHYSICAL),
				Attribute.of(attributeId, 30, MiscCondition.SPELL),
				Attribute.of(attributeId, 40)
		);

		spellStats.accumulateAttributes(list, 2);
	}

	AccumulatedSpellStats spellStats;

	@BeforeEach
	void setUp() {
		var caster = getCharacter();
		var spell = caster.getAbility(AbilityId.SHADOW_BOLT).orElseThrow();
		var conditionArgs = AttributeConditionArgs.forSpell(caster, spell, null);

		this.spellStats = new AccumulatedSpellStats(conditionArgs);
	}
}