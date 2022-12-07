package wow.commons.repository;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.model.attributes.complex.special.TalentProcAbility;
import wow.commons.model.buffs.Buff;
import wow.commons.model.buffs.BuffExclusionGroup;
import wow.commons.model.buffs.BuffType;
import wow.commons.model.config.ConfigurationElement;
import wow.commons.model.spells.Spell;
import wow.commons.model.talents.Talent;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.attributes.complex.special.ProcEvent.SPELL_CRIT;
import static wow.commons.model.character.CharacterClass.WARLOCK;
import static wow.commons.model.spells.EffectId.SHADOW_VULNERABILITY_20;
import static wow.commons.model.spells.SpellId.CURSE_OF_THE_ELEMENTS;
import static wow.commons.model.spells.SpellId.SHADOW_BOLT;
import static wow.commons.model.spells.SpellSchool.*;
import static wow.commons.model.talents.TalentId.IMPROVED_SHADOW_BOLT;
import static wow.commons.model.talents.TalentTree.DESTRUCTION;

/**
 * User: POlszewski
 * Date: 2022-11-11
 */
class SpellDataRepositoryTest extends RepositoryTest {
	@Autowired
	SpellDataRepository underTest;

	@Test
	@DisplayName("SpellInfo is read correctly")
	void spellInfoIsCorrect() {
		Optional<Spell> optionalSpell = underTest.getSpellHighestRank(SHADOW_BOLT, PHASE);

		assertThat(optionalSpell).isPresent();

		Spell spell = optionalSpell.orElseThrow();

		assertThat(spell.getSpellId()).isEqualTo(SHADOW_BOLT);
		assertThat(spell.getTalentTree()).isEqualTo(DESTRUCTION);
		assertThat(spell.getSpellSchool()).isEqualTo(SHADOW);
		assertThat(spell.getCoeffDirect().getValue()).isEqualTo(85.71, PRECISION);
		assertThat(spell.getCoeffDot()).isEqualTo(Percent.ZERO);
		assertThat(spell.getSpellInfo().getCooldown()).isNull();
		assertThat(spell.getSpellInfo().isIgnoresGCD()).isFalse();
		assertThat(spell.getCharacterRestriction().getTalentId()).isNull();
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
		Optional<Talent> optionalTalent = underTest.getTalent(WARLOCK, 44, 5, PHASE);

		assertThat(optionalTalent).isPresent();

		Talent talent = optionalTalent.orElseThrow();

		assertThat(talent.getTalentId()).isEqualTo(IMPROVED_SHADOW_BOLT);
		assertThat(talent.getRank()).isEqualTo(5);
		assertThat(talent.getMaxRank()).isEqualTo(5);
		assertThat(talent.getTooltip()).isEqualTo("Your Shadow Bolt critical strikes increase Shadow damage dealt to the target by 20% until 4 non-periodic damage sources are applied. Effect lasts a maximum of 12 sec.");
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
		Optional<Buff> optionalBuff = underTest.getBuff(27228, PHASE);

		assertThat(optionalBuff).isPresent();

		Buff buff = optionalBuff.orElseThrow();

		assertThat(buff.getId()).isEqualTo(27228);
		assertThat(buff.getName()).isEqualTo("Curse of the Elements");
		assertThat(buff.getRequiredLevel()).isNull();
		assertThat(buff.getType()).isEqualTo(BuffType.DEBUFF);
		assertThat(buff.getExclusionGroup()).isEqualTo(BuffExclusionGroup.COE);
		assertThat(buff.getSourceSpell()).isEqualTo(CURSE_OF_THE_ELEMENTS);

		Attributes stats = buff.getAttributes();

		assertThat(stats.getDamageTakenPct(SHADOW)).isEqualTo(Percent.of(10));
		assertThat(stats.getDamageTakenPct(FIRE)).isEqualTo(Percent.of(10));
		assertThat(stats.getDamageTakenPct(FROST)).isEqualTo(Percent.of(10));
		assertThat(stats.getDamageTakenPct(ARCANE)).isEqualTo(Percent.of(10));
	}

	@Test
	@DisplayName("Buff by id is read correctly")
	void buffByIdIsCorrect() {
		Optional<Buff> optionalBuff = underTest.getBuff(27228, PHASE);

		assertThat(optionalBuff).isPresent();

		Buff buff = optionalBuff.orElseThrow();

		assertThat(buff.getId()).isEqualTo(27228);
		assertThat(buff.getName()).isEqualTo("Curse of the Elements");
	}

	@Test
	@DisplayName("Buffs are read correctly")
	void bufByNamefIsCorrect() {
		List<String> buffNames = underTest.getBuffs(PHASE).stream()
				.map(ConfigurationElement::getName)
				.collect(Collectors.toList());

		assertThat(buffNames).hasSameElementsAs(List.of(
				"Arcane Brilliance",
				"Gift of the Wild",
				"Greater Blessing of Kings",
				"Fel Armor",
				"Touch of Shadow",
				"Burning Wish",
				"Brilliant Wizard Oil",
				"Superior Wizard Oil",
				"Well Fed (sp)",
				"Flask of Supreme Power",
				"Flask of Pure Death",
				"Moonkin Aura",
				"Wrath of Air Totem",
				"Totem of Wrath",
				"Misery",
				"Shadow Weaving",
				"Improved Scorch",
				"Curse of the Elements",
				"Curse of the Elements (improved)",
				"Drums of Battle",
				"Destruction",
				"Blood Fury"
		));
	}

	static final Offset<Double> PRECISION = Offset.offset(0.01);
}
