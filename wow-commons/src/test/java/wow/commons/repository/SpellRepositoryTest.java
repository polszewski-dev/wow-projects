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
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.complex.special.SpecialAbility;
import wow.commons.model.attributes.complex.special.TalentProcAbility;
import wow.commons.model.attributes.condition.AttributeCondition;
import wow.commons.model.buffs.Buff;
import wow.commons.model.buffs.BuffExclusionGroup;
import wow.commons.model.buffs.BuffId;
import wow.commons.model.buffs.BuffType;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.spells.Spell;
import wow.commons.model.spells.SpellId;
import wow.commons.model.talents.Talent;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.attributes.complex.special.ProcEventType.SPELL_CRIT;
import static wow.commons.model.attributes.primitive.PrimitiveAttributeId.SPELL_CRIT_PCT;
import static wow.commons.model.buffs.BuffCategory.RAID_BUFF;
import static wow.commons.model.categorization.PveRole.CASTER_DPS;
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
		Optional<Spell> optionalSpell = underTest.getSpell(SHADOW_BOLT, 11, PhaseId.TBC_P5);

		assertThat(optionalSpell).isPresent();

		Spell spell = optionalSpell.orElseThrow();

		assertThat(spell.getSpellId()).isEqualTo(SHADOW_BOLT);
		assertThat(spell.getTalentTree()).isEqualTo(DESTRUCTION);
		assertThat(spell.getSpellSchool()).isEqualTo(SHADOW);
		assertThat(spell.getCoeffDirect().value()).isEqualTo(85.71, PRECISION);
		assertThat(spell.getCoeffDot()).isEqualTo(Percent.ZERO);
		assertThat(spell.getSpellInfo().getCooldown().getSeconds()).isZero();
		assertThat(spell.getSpellInfo().isIgnoresGCD()).isFalse();
		assertThat(spell.getCharacterRestriction().characterClassIds()).hasSameElementsAs(List.of(WARLOCK));
		assertThat(spell.getCharacterRestriction().raceIds()).isEmpty();
		assertThat(spell.getCharacterRestriction().talentId()).isNull();
		assertThat(spell.getTimeRestriction().versions()).isEmpty();
		assertThat(spell.getSpellInfo().getDamagingSpellInfo().bolt()).isTrue();
		assertThat(spell.getSpellInfo().getConversion()).isNull();
		assertThat(spell.getSpellInfo().getDamagingSpellInfo().requiredSpellEffect()).isNull();
		assertThat(spell.getSpellInfo().getDamagingSpellInfo().spellEffectRemovedOnHit()).isNull();
		assertThat(spell.getSpellInfo().getDamagingSpellInfo().bonusDamageIfUnderSpellEffect()).isNull();
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
		void vanillaP1(SpellId spellId, PhaseId phaseId, int rank, int min, int max) {
			Spell spell = getSpell(spellId, rank, phaseId);

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
		void vanillaP1(SpellId spellId, PhaseId phaseId, int rank, int dot) {
			Spell spell = getSpell(spellId, rank, phaseId);

			assertThat(spell.getRank()).isEqualTo(rank);
			assertThat(spell.getDotDmg()).isEqualTo(dot);
		}
	}

	private Spell getSpell(SpellId spellId, int rank, PhaseId phaseId) {
		return underTest.getSpell(spellId, rank, phaseId).orElseThrow();
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

		assertThat(specialAbility.condition()).isEqualTo(AttributeCondition.of(SHADOW_BOLT));
		assertThat(specialAbility).isInstanceOf(TalentProcAbility.class);

		TalentProcAbility ability = (TalentProcAbility) specialAbility;

		assertThat(ability.event().type()).isEqualTo(SPELL_CRIT);
		assertThat(ability.event().chance()).isEqualTo(Percent._100);
		assertThat(ability.effectId()).isEqualTo(SHADOW_VULNERABILITY_20);
		assertThat(ability.duration()).isEqualTo(Duration.seconds(12));
		assertThat(ability.stacks()).isEqualTo(4);
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
		Optional<Buff> optionalBuff = underTest.getBuff(BuffId.CURSE_OF_THE_ELEMENTS, 4, PhaseId.TBC_P5);

		assertThat(optionalBuff).isPresent();

		Buff buff = optionalBuff.orElseThrow();

		assertThat(buff.getBuffId()).isEqualTo(BuffId.CURSE_OF_THE_ELEMENTS);
		assertThat(buff.getRank()).isEqualTo(4);
		assertThat(buff.getName()).isEqualTo("Curse of the Elements");
		assertThat(buff.getRequiredLevel()).isNull();
		assertThat(buff.getType()).isEqualTo(BuffType.DEBUFF);
		assertThat(buff.getExclusionGroup()).isEqualTo(BuffExclusionGroup.COE);
		assertThat(buff.getSourceSpell()).isNull();
		assertThat(buff.getPveRoles()).hasSameElementsAs(Set.of(CASTER_DPS));
		assertThat(buff.getCategories()).hasSameElementsAs(Set.of(RAID_BUFF));

		Attributes stats = buff.getAttributes();

		assertThat(stats.getDamagePct(SHADOW)).isEqualTo(Percent.of(10));
		assertThat(stats.getDamagePct(FIRE)).isEqualTo(Percent.of(10));
		assertThat(stats.getDamagePct(FROST)).isEqualTo(Percent.of(10));
		assertThat(stats.getDamagePct(ARCANE)).isEqualTo(Percent.of(10));
	}

	@Test
	@DisplayName("Buffs are read correctly")
	void bufByNamefIsCorrect() {
		List<String> buffNames = underTest.getAvailableBuffs(PhaseId.TBC_P5).stream()
				.map(buff -> buff.getName() + "#" + buff.getRank())
				.toList();

		assertThat(buffNames).hasSameElementsAs(List.of(
				"Arcane Brilliance#1",
				"Arcane Brilliance#2",
				"Prayer of Fortitude#2",
				"Prayer of Fortitude#3",
				"Prayer of Spirit#1",
				"Prayer of Spirit#2",
				"Gift of the Wild#2",
				"Gift of the Wild#3",
				"Greater Blessing of Kings#0",
				"Demon Armor#5",
				"Demon Armor#6",
				"Fel Armor#1",
				"Fel Armor#2",
				"Touch of Shadow#0",
				"Burning Wish#0",
				"Shadowform#0",
				"Brilliant Wizard Oil#0",
				"Superior Wizard Oil#0",
				"Runn Tum Tuber#0",
				"Well Fed (sp)#0",
				"Flask of Supreme Power#0",
				"Flask of Pure Death#0",
				"Spirit of Zanza#0",
				"Greater Arcane Elixir#0",
				"Elixir of Shadow Power#0",
				"Moonkin Aura#0",
				"Wrath of Air Totem#1",
				"Totem of Wrath#1",
				"Drums of Battle#0",
				"Destruction#0",
				"Misery#0",
				"Rallying Cry of the Dragonslayer#0",
				"Spirit of Zandalar#0",
				"Warchief's Blessing#0",
				"Sayge's Dark Fortune of Damage#0",
				"Mol'dar's Moxie#0",
				"Slip'kik's Savvy#0",
				"Songflower Serenade#0",
				"Shadow Weaving#0",
				"Improved Scorch#0",
				"Curse of the Elements#3",
				"Curse of the Elements#4",
				"Curse of the Elements (improved)#3",
				"Curse of the Elements (improved)#4"
		));
	}

	@Nested
	@DisplayName("Buff attributes are correct")
	class BuffAttributes {
		@ParameterizedTest(name = "{0}")
		@CsvSource({
				"VANILLA_P1, 1, 31",
				"TBC_P1, 2, 40",
		})
		void talent(PhaseId phaseId, int rank, int statValue) {
			Buff buff = underTest.getBuff(BuffId.ARCANE_BRILLIANCE, rank, phaseId).orElseThrow();

			assertThat(buff.getIntellect()).isEqualTo(statValue);
		}
	}

	static final Offset<Double> PRECISION = Offset.offset(0.01);
}
