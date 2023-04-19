package wow.commons.repository;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
import wow.commons.model.pve.PhaseId;
import wow.commons.model.spells.Spell;
import wow.commons.model.spells.SpellId;
import wow.commons.model.talents.Talent;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.attributes.complex.special.ProcEventType.SPELL_CRIT;
import static wow.commons.model.attributes.primitive.PrimitiveAttributeId.SPELL_CRIT_PCT;
import static wow.commons.model.character.CharacterClassId.WARLOCK;
import static wow.commons.model.spells.EffectId.SHADOW_VULNERABILITY_20;
import static wow.commons.model.spells.SpellId.*;
import static wow.commons.model.spells.SpellSchool.*;
import static wow.commons.model.talents.TalentId.*;
import static wow.commons.model.talents.TalentTree.DESTRUCTION;

/**
 * User: POlszewski
 * Date: 2022-11-11
 */
class SpellRepositoryTest extends RepositoryTest {
	@Autowired
	SpellRepository underTest;

	@Test
	@DisplayName("SpellInfo is read correctly")
	void spellInfoIsCorrect() {
		Optional<Spell> optionalSpell = underTest.getSpellHighestRank(SHADOW_BOLT, 70, PhaseId.TBC_P5);

		assertThat(optionalSpell).isPresent();

		Spell spell = optionalSpell.orElseThrow();

		assertThat(spell.getSpellId()).isEqualTo(SHADOW_BOLT);
		assertThat(spell.getTalentTree()).isEqualTo(DESTRUCTION);
		assertThat(spell.getSpellSchool()).isEqualTo(SHADOW);
		assertThat(spell.getCoeffDirect().getValue()).isEqualTo(85.71, PRECISION);
		assertThat(spell.getCoeffDot()).isEqualTo(Percent.ZERO);
		assertThat(spell.getSpellInfo().getCooldown().getSeconds()).isZero();
		assertThat(spell.getSpellInfo().isIgnoresGCD()).isFalse();
		assertThat(spell.getCharacterRestriction().getCharacterClassIds()).hasSameElementsAs(List.of(WARLOCK));
		assertThat(spell.getCharacterRestriction().getRaceIds()).isEmpty();
		assertThat(spell.getCharacterRestriction().getTalentId()).isNull();
		assertThat(spell.getTimeRestriction().getVersions()).isEmpty();
		assertThat(spell.getSpellInfo().getDamagingSpellInfo().isBolt()).isTrue();
		assertThat(spell.getSpellInfo().getConversion()).isNull();
		assertThat(spell.getSpellInfo().getDamagingSpellInfo().getRequiredSpellEffect()).isNull();
		assertThat(spell.getSpellInfo().getDamagingSpellInfo().getSpellEffectRemovedOnHit()).isNull();
		assertThat(spell.getSpellInfo().getDamagingSpellInfo().getBonusDamageIfUnderSpellEffect()).isNull();
		assertThat(spell.getSpellInfo().getDamagingSpellInfo().getDotScheme()).isEmpty();
	}

	@Nested
	@DisplayName("Spell damage is correct")
	class SpellDamage {
		@ParameterizedTest(name = "spell = {0}, phase = {1}")
		@CsvSource({
			"SHADOW_BOLT, VANILLA_P1, 9, 455, 507",
			"SHADOW_BOLT, VANILLA_P5, 10, 482, 538",
			"SHADOW_BOLT, TBC_P1, 11, 544, 607",
			"IMMOLATE, VANILLA_P1, 7, 258, 258",
			"IMMOLATE, VANILLA_P5, 8, 279, 279",
			"IMMOLATE, TBC_P1, 9, 332, 332",
		})
		void vanillaP1(SpellId shadowBolt, PhaseId phaseId, int rank, int min, int max) {
			Spell spell = getSpell(shadowBolt, phaseId);

			assertThat(spell.getRank()).isEqualTo(rank);
			assertThat(spell.getMinDmg()).isEqualTo(min);
			assertThat(spell.getMaxDmg()).isEqualTo(max);
		}

		@ParameterizedTest(name = "spell = {0}, phase = {1}")
		@CsvSource({
				"CURSE_OF_AGONY, VANILLA_P1, 6, 1044",
				"CURSE_OF_AGONY, VANILLA_P5, 6, 1044",
				"CURSE_OF_AGONY, TBC_P1, 7, 1356",
				"CURSE_OF_DOOM, VANILLA_P1, 1, 3200",
				"CURSE_OF_DOOM, VANILLA_P5, 1, 3200",
				"CURSE_OF_DOOM, TBC_P1, 2, 4200",
				"CORRUPTION, VANILLA_P1, 6, 666",
				"CORRUPTION, VANILLA_P5, 7, 822",
				"CORRUPTION, TBC_P1, 8, 900",
				"IMMOLATE, VANILLA_P1, 7, 485",
				"IMMOLATE, VANILLA_P5, 8, 510",
				"IMMOLATE, TBC_P1, 9, 615",
		})
		void vanillaP1(SpellId shadowBolt, PhaseId phaseId, int rank, int dot) {
			Spell spell = getSpell(shadowBolt, phaseId);

			assertThat(spell.getRank()).isEqualTo(rank);
			assertThat(spell.getDotDmg()).isEqualTo(dot);
		}
	}

	private Spell getSpell(SpellId shadowBolt, PhaseId phaseId) {
		int level = phaseId.getGameVersionId().getMaxLevel();
		return underTest.getSpellHighestRank(shadowBolt, level, phaseId).orElseThrow();
	}

	@Test
	@DisplayName("Talent is read correctly")
	void talentIsCorrect() {
		Optional<Talent> optionalTalent = underTest.getTalent(WARLOCK, 44, 5, PhaseId.TBC_P5);

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

		assertThat(ability.getEvent().getType()).isEqualTo(SPELL_CRIT);
		assertThat(ability.getEvent().getChance()).isEqualTo(Percent._100);
		assertThat(ability.getEffectId()).isEqualTo(SHADOW_VULNERABILITY_20);
		assertThat(ability.getDuration()).isEqualTo(Duration.seconds(12));
		assertThat(ability.getStacks()).isEqualTo(4);
	}

	@Nested
	@DisplayName("Talent attributes are correct")
	class TalentAttributes {
		@ParameterizedTest(name = "{0}")
		@CsvSource({
				"VANILLA_P1, 45, 1, 5, 2",
				"TBC_P1, 54, 1, 3, 4",
		})
		void talent(PhaseId phaseId, int position, int rank, int maxRank, int statValue) {
			Talent talent = underTest.getTalent(WARLOCK, position, rank, phaseId).orElseThrow();

			assertThat(talent.getTalentId()).isEqualTo(IMPROVED_SEARING_PAIN);
			assertThat(talent.getRank()).isEqualTo(rank);
			assertThat(talent.getMaxRank()).isEqualTo(maxRank);

			assertThat(talent.getDouble(SPELL_CRIT_PCT, AttributeCondition.of(SEARING_PAIN))).isEqualTo(statValue);
		}
	}

	@Test
	@DisplayName("Talent with multiline benefit attributes are correct")
	void talentWithMultilineBenefit() {
		Talent talent = underTest.getTalent(WARLOCK, 46, 5, PhaseId.TBC_P5).orElseThrow();

		assertThat(talent.getTalentId()).isEqualTo(BANE);
		assertThat(talent.getRank()).isEqualTo(5);
		assertThat(talent.getMaxRank()).isEqualTo(5);

		assertThat(talent.getCastTime(SHADOW_BOLT).getSeconds()).isEqualTo(-0.5);
		assertThat(talent.getCastTime(IMMOLATE).getSeconds()).isEqualTo(-0.5);
		assertThat(talent.getCastTime(SOUL_FIRE).getSeconds()).isEqualTo(-2);
	}

	@Test
	@DisplayName("Buff is read correctly")
	void buffIsCorrect() {
		Optional<Buff> optionalBuff = underTest.getBuff(27228, PhaseId.TBC_P5);

		assertThat(optionalBuff).isPresent();

		Buff buff = optionalBuff.orElseThrow();

		assertThat(buff.getId()).isEqualTo(27228);
		assertThat(buff.getName()).isEqualTo("Curse of the Elements");
		assertThat(buff.getRequiredLevel()).isNull();
		assertThat(buff.getType()).isEqualTo(BuffType.DEBUFF);
		assertThat(buff.getExclusionGroup()).isEqualTo(BuffExclusionGroup.COE);
		assertThat(buff.getSourceSpell()).isEqualTo(CURSE_OF_THE_ELEMENTS);

		Attributes stats = buff.getAttributes();

		assertThat(stats.getDamagePct(SHADOW)).isEqualTo(Percent.of(10));
		assertThat(stats.getDamagePct(FIRE)).isEqualTo(Percent.of(10));
		assertThat(stats.getDamagePct(FROST)).isEqualTo(Percent.of(10));
		assertThat(stats.getDamagePct(ARCANE)).isEqualTo(Percent.of(10));
	}

	@Test
	@DisplayName("Buff by id name read correctly")
	void buffByIdIsCorrect() {
		Optional<Buff> optionalBuff = underTest.getBuff("Curse of the Elements", PhaseId.TBC_P5);

		assertThat(optionalBuff).isPresent();

		Buff buff = optionalBuff.orElseThrow();

		assertThat(buff.getId()).isEqualTo(27228);
		assertThat(buff.getName()).isEqualTo("Curse of the Elements");
	}

	@Test
	@DisplayName("Buffs are read correctly")
	void bufByNamefIsCorrect() {
		List<String> buffNames = underTest.getBuffs(PhaseId.TBC_P5).stream()
				.map(ConfigurationElement::getName)
				.toList();

		assertThat(buffNames).hasSameElementsAs(List.of(
				"Arcane Brilliance",
				"Prayer of Fortitude",
				"Prayer of Spirit",
				"Gift of the Wild",
				"Greater Blessing of Kings",
				"Fel Armor",
				"Touch of Shadow",
				"Burning Wish",
				"Shadowform",
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
				"Destruction"
		));
	}

	@Nested
	@DisplayName("Buff attributes are correct")
	class BuffAttributes {
		@ParameterizedTest(name = "{0}")
		@CsvSource({
				"VANILLA_P1, 31",
				"TBC_P1, 40",
		})
		void talent(PhaseId phaseId, int statValue) {
			Buff buff = underTest.getBuff("Arcane Brilliance", phaseId).orElseThrow();

			assertThat(buff.getIntellect()).isEqualTo(statValue);
		}
	}

	static final Offset<Double> PRECISION = Offset.offset(0.01);
}
