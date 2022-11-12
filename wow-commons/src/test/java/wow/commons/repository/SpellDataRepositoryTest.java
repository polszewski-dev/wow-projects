package wow.commons.repository;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import wow.commons.WowCommonsTestConfig;
import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.model.attributes.complex.modifiers.TemporaryEffect;
import wow.commons.model.buffs.Buff;
import wow.commons.model.buffs.BuffExclusionGroup;
import wow.commons.model.buffs.BuffType;
import wow.commons.model.spells.SpellInfo;
import wow.commons.model.talents.TalentInfo;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.attributes.complex.modifiers.ProcEvent.SPELL_CRIT;
import static wow.commons.model.effects.EffectId.SHADOW_VULNERABILITY_20;
import static wow.commons.model.spells.SpellId.CURSE_OF_THE_ELEMENTS;
import static wow.commons.model.spells.SpellId.SHADOW_BOLT;
import static wow.commons.model.spells.SpellSchool.*;
import static wow.commons.model.talents.TalentId.IMPROVED_SHADOW_BOLT;
import static wow.commons.model.talents.TalentTree.DESTRUCTION;

/**
 * User: POlszewski
 * Date: 2022-11-11
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = WowCommonsTestConfig.class)
class SpellDataRepositoryTest {
	@Autowired
	SpellDataRepository underTest;

	@Test
	@DisplayName("SpellInfo is read correctly")
	void spellInfoIsCorrect() {
		Optional<SpellInfo> optionalSpellInfo = underTest.getSpellInfo(SHADOW_BOLT);

		assertThat(optionalSpellInfo).isPresent();

		SpellInfo spellInfo = optionalSpellInfo.orElseThrow();

		assertThat(spellInfo.getSpellId()).isEqualTo(SHADOW_BOLT);
		assertThat(spellInfo.getTalentTree()).isEqualTo(DESTRUCTION);
		assertThat(spellInfo.getSpellSchool()).isEqualTo(SHADOW);
		assertThat(spellInfo.getCoeffDirect().getValue()).isEqualTo(85.71, PRECISION);
		assertThat(spellInfo.getCoeffDot()).isEqualTo(Percent.ZERO);
		assertThat(spellInfo.getCooldown()).isNull();
		assertThat(spellInfo.isIgnoresGCD()).isFalse();
		assertThat(spellInfo.getRequiredTalent()).isNull();
		assertThat(spellInfo.isBolt()).isTrue();
		assertThat(spellInfo.getConversion()).isNull();
		assertThat(spellInfo.getRequiredSpellEffect()).isNull();
		assertThat(spellInfo.getSpellEffectRemovedOnHit()).isNull();
		assertThat(spellInfo.getBonusDamageIfUnderSpellEffect()).isNull();
		assertThat(spellInfo.getDotScheme()).isEmpty();
	}

	@Test
	@DisplayName("TalentInfo is read correctly")
	void talentInfoIsCorrect() {
		Optional<TalentInfo> optionalTalentInfo = underTest.getTalentInfo(IMPROVED_SHADOW_BOLT, 5);

		assertThat(optionalTalentInfo).isPresent();

		TalentInfo talentInfo = optionalTalentInfo.orElseThrow();

		assertThat(talentInfo.getTalentId()).isEqualTo(IMPROVED_SHADOW_BOLT);
		assertThat(talentInfo.getRank()).isEqualTo(5);
		assertThat(talentInfo.getMaxRank()).isEqualTo(5);
		assertThat(talentInfo.getDescription()).isNotBlank();
		assertThat(talentInfo.getAttributes().getSpecialAbilities()).hasSize(1);

		SpecialAbility specialAbility = talentInfo.getAttributes().getSpecialAbilities().get(0);

		assertThat(specialAbility.getCondition()).isEqualTo(AttributeCondition.of(SHADOW_BOLT));
		assertThat(specialAbility.getAttributeModifier()).isInstanceOf(TemporaryEffect.class);

		TemporaryEffect attributeModifier = (TemporaryEffect) specialAbility.getAttributeModifier();

		assertThat(attributeModifier.getEvent()).isEqualTo(SPELL_CRIT);
		assertThat(attributeModifier.getChance()).isEqualTo(Percent._100);
		assertThat(attributeModifier.getEffectId()).isEqualTo(SHADOW_VULNERABILITY_20);
		assertThat(attributeModifier.getDuration()).isEqualTo(Duration.seconds(12));
		assertThat(attributeModifier.getStacks()).isEqualTo(4);
	}

	@Test
	@DisplayName("Buff is read correctly")
	void buffIsCorrect() {
		Optional<Buff> optionalBuff = underTest.getBuff(27228);

		assertThat(optionalBuff).isPresent();

		Buff buff = optionalBuff.orElseThrow();

		assertThat(buff.getId()).isEqualTo(27228);
		assertThat(buff.getName()).isEqualTo("Curse of the Elements");
		assertThat(buff.getLevel()).isZero();
		assertThat(buff.getType()).isEqualTo(BuffType.DEBUFF);
		assertThat(buff.getExclusionGroup()).isEqualTo(BuffExclusionGroup.COE);
		assertThat(buff.getSourceSpell()).isEqualTo(CURSE_OF_THE_ELEMENTS);
		assertThat(buff.getDuration()).isNull();
		assertThat(buff.getCooldown()).isNull();

		Attributes stats = buff.getAttributes();

		assertThat(stats.getDamageTakenPct(SHADOW)).isEqualTo(Percent.of(10));
		assertThat(stats.getDamageTakenPct(FIRE)).isEqualTo(Percent.of(10));
		assertThat(stats.getDamageTakenPct(FROST)).isEqualTo(Percent.of(10));
		assertThat(stats.getDamageTakenPct(ARCANE)).isEqualTo(Percent.of(10));
	}

	static final Offset<Double> PRECISION = Offset.offset(0.01);
}
