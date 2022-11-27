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
import wow.commons.model.attributes.complex.special.TalentProcAbility;
import wow.commons.model.buffs.Buff;
import wow.commons.model.buffs.BuffExclusionGroup;
import wow.commons.model.buffs.BuffType;
import wow.commons.model.spells.Spell;
import wow.commons.model.talents.Talent;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.attributes.complex.special.ProcEvent.SPELL_CRIT;
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
		Optional<Spell> optionalSpell = underTest.getSpell(SHADOW_BOLT, 11);

		assertThat(optionalSpell).isPresent();

		Spell spell = optionalSpell.orElseThrow();

		assertThat(spell.getSpellId()).isEqualTo(SHADOW_BOLT);
		assertThat(spell.getTalentTree()).isEqualTo(DESTRUCTION);
		assertThat(spell.getSpellSchool()).isEqualTo(SHADOW);
		assertThat(spell.getCoeffDirect().getValue()).isEqualTo(85.71, PRECISION);
		assertThat(spell.getCoeffDot()).isEqualTo(Percent.ZERO);
		assertThat(spell.getSpellInfo().getCooldown()).isNull();
		assertThat(spell.getSpellInfo().isIgnoresGCD()).isFalse();
		assertThat(spell.getRestriction().getRequiredTalent()).isNull();
		assertThat(spell.getSpellInfo().getDamagingSpellInfo().isBolt()).isTrue();
		assertThat(spell.getSpellInfo().getConversion()).isNull();
		assertThat(spell.getSpellInfo().getDamagingSpellInfo().getRequiredSpellEffect()).isNull();
		assertThat(spell.getSpellInfo().getDamagingSpellInfo().getSpellEffectRemovedOnHit()).isNull();
		assertThat(spell.getSpellInfo().getDamagingSpellInfo().getBonusDamageIfUnderSpellEffect()).isNull();
		assertThat(spell.getSpellInfo().getDamagingSpellInfo().getDotScheme()).isEmpty();
	}

	@Test
	@DisplayName("Talent is read correctly")
	void talentIsCorrect() {
		Optional<Talent> optionalTalent = underTest.getTalent(IMPROVED_SHADOW_BOLT, 5);

		assertThat(optionalTalent).isPresent();

		Talent talent = optionalTalent.orElseThrow();

		assertThat(talent.getTalentId()).isEqualTo(IMPROVED_SHADOW_BOLT);
		assertThat(talent.getRank()).isEqualTo(5);
		assertThat(talent.getMaxRank()).isEqualTo(5);
		assertThat(talent.getTooltip()).isNotBlank();
		assertThat(talent.getAttributes().getSpecialAbilities()).hasSize(1);

		SpecialAbility specialAbility = talent.getAttributes().getSpecialAbilities().get(0);

		assertThat(specialAbility.getCondition()).isEqualTo(AttributeCondition.of(SHADOW_BOLT));
		assertThat(specialAbility).isInstanceOf(TalentProcAbility.class);

		TalentProcAbility ability = (TalentProcAbility) specialAbility;

		assertThat(ability.getEvent()).isEqualTo(SPELL_CRIT);
		assertThat(ability.getChance()).isEqualTo(Percent._100);
		assertThat(ability.getEffectId()).isEqualTo(SHADOW_VULNERABILITY_20);
		assertThat(ability.getDuration()).isEqualTo(Duration.seconds(12));
		assertThat(ability.getStacks()).isEqualTo(4);
	}

	@Test
	@DisplayName("Buff is read correctly")
	void buffIsCorrect() {
		Optional<Buff> optionalBuff = underTest.getBuff(27228);

		assertThat(optionalBuff).isPresent();

		Buff buff = optionalBuff.orElseThrow();

		assertThat(buff.getId()).isEqualTo(27228);
		assertThat(buff.getName()).isEqualTo("Curse of the Elements");
		assertThat(buff.getRequiredLevel()).isZero();
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
