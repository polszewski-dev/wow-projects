package wow.commons.repository.impl.parsers.excel.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.complex.ComplexAttribute;
import wow.commons.model.attributes.complex.EffectIncreasePerEffectOnTarget;
import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.model.attributes.complex.StatConversion;
import wow.commons.model.attributes.complex.special.*;
import wow.commons.model.attributes.primitive.PrimitiveAttribute;
import wow.commons.model.spells.EffectId;
import wow.commons.model.talents.TalentTree;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.attributes.primitive.PrimitiveAttributeId.*;

/**
 * User: POlszewski
 * Date: 2022-11-26
 */
class ComplexAttributeMapperTest {
	@Test
	@DisplayName("Equivalent is mapped correctly: 1 attribute")
	void equivalentAbility() {
		EquivalentAbility original = SpecialAbility.equivalent(Attributes.of(SPELL_POWER, 100), "This is a test");

		String result = ComplexAttributeMapper.toString(original);

		assertThat(result).isEqualTo("Equivalent -> stat=Power,Spell; amount=100; line=This is a test");

		ComplexAttribute complexAttribute = ComplexAttributeMapper.fromString(result);

		assertThat(complexAttribute).isNotNull().isInstanceOf(EquivalentAbility.class);

		EquivalentAbility parsed = (EquivalentAbility)complexAttribute;

		assertThat(parsed.getAttributes().getSpellPower()).isEqualTo(100);
		assertThat(parsed.getLine()).isEqualTo("This is a test");
	}

	@Test
	@DisplayName("OnUse is mapped correctly: 1 attribute")
	void onUseAbility1() {
		OnUseAbility original = SpecialAbility.onUse(Attributes.of(SPELL_POWER, 143), Duration.seconds(15), Duration.seconds(120), "This is a test");

		String result = ComplexAttributeMapper.toString(original);

		assertThat(result).isEqualTo("OnUse -> stat=Power,Spell; amount=143; duration=15; cooldown=120; line=This is a test");

		ComplexAttribute complexAttribute = ComplexAttributeMapper.fromString(result);

		assertThat(complexAttribute).isNotNull().isInstanceOf(OnUseAbility.class);

		OnUseAbility parsed = (OnUseAbility)complexAttribute;

		assertThat(parsed.getAttributes().getSpellPower()).isEqualTo(143);
		assertThat(parsed.getDuration().getSeconds()).isEqualTo(15);
		assertThat(parsed.getCooldown().getSeconds()).isEqualTo(120);
		assertThat(parsed.getLine()).isEqualTo("This is a test");
	}

	@Test
	@DisplayName("OnUse is mapped correctly: 2 attributes")
	void onUseAbility2() {
		OnUseAbility original = SpecialAbility.onUse(Attributes.of(
				PrimitiveAttribute.of(SPELL_DAMAGE, 120),
				PrimitiveAttribute.of(SPELL_CRIT_PCT, 2)
		), Duration.seconds(15), Duration.seconds(120), "This is a test");

		String result = ComplexAttributeMapper.toString(original);

		assertThat(result).isEqualTo("OnUse -> stat1=Power,SpellDamage; amount1=120; stat2=Crit,Spell,Percent; amount2=2; duration=15; cooldown=120; line=This is a test");

		ComplexAttribute complexAttribute = ComplexAttributeMapper.fromString(result);

		assertThat(complexAttribute).isNotNull().isInstanceOf(OnUseAbility.class);

		OnUseAbility parsed = (OnUseAbility)complexAttribute;

		assertThat(parsed.getAttributes().getSpellDamage()).isEqualTo(120);
		assertThat(parsed.getAttributes().getSpellCritPct().getValue()).isEqualTo(2);
		assertThat(parsed.getDuration().getSeconds()).isEqualTo(15);
		assertThat(parsed.getCooldown().getSeconds()).isEqualTo(120);
		assertThat(parsed.getLine()).isEqualTo("This is a test");
	}

	@Test
	@DisplayName("Proc is mapped correctly")
	void procAbility() {
		ProcAbility original = SpecialAbility.proc(ProcEvent.SPELL_CRIT, Percent.of(20), Attributes.of(SPELL_POWER, 100), Duration.seconds(15), Duration.seconds(60), "This is a test");

		String result = ComplexAttributeMapper.toString(original);

		assertThat(result).isEqualTo("Proc -> event=SPELL_CRIT; chance%=20; stat=Power,Spell; amount=100; duration=15; cooldown=60; line=This is a test");

		ComplexAttribute complexAttribute = ComplexAttributeMapper.fromString(result);

		assertThat(complexAttribute).isNotNull().isInstanceOf(ProcAbility.class);

		ProcAbility parsed = (ProcAbility)complexAttribute;

		assertThat(parsed.getEvent()).isEqualTo(ProcEvent.SPELL_CRIT);
		assertThat(parsed.getChance().getValue()).isEqualTo(20);
		assertThat(parsed.getAttributes().getSpellPower()).isEqualTo(100);
		assertThat(parsed.getDuration().getSeconds()).isEqualTo(15);
		assertThat(parsed.getCooldown().getSeconds()).isEqualTo(60);
		assertThat(parsed.getLine()).isEqualTo("This is a test");
	}

	@Test
	@DisplayName("TalentProc is mapped correctly")
	void talentProcAbility() {
		TalentProcAbility original = SpecialAbility.talentProc(ProcEvent.SPELL_CRIT, Percent._100, EffectId.SHADOW_VULNERABILITY_20, Duration.seconds(12), 4, "This is a test");

		String result = ComplexAttributeMapper.toString(original);

		assertThat(result).isEqualTo("TalentProc -> event=SPELL_CRIT; chance%=100; effect=Shadow Vulnerability:20; duration=12; stacks=4; line=This is a test");

		ComplexAttribute complexAttribute = ComplexAttributeMapper.fromString(result);

		assertThat(complexAttribute).isNotNull().isInstanceOf(TalentProcAbility.class);

		TalentProcAbility parsed = (TalentProcAbility)complexAttribute;

		assertThat(parsed.getEvent()).isEqualTo(ProcEvent.SPELL_CRIT);
		assertThat(parsed.getChance().getValue()).isEqualTo(100);
		assertThat(parsed.getEffectId()).isEqualTo(EffectId.SHADOW_VULNERABILITY_20);
		assertThat(parsed.getDuration().getSeconds()).isEqualTo(12);
		assertThat(parsed.getStacks()).isEqualTo(4);
		assertThat(parsed.getLine()).isEqualTo("This is a test");
	}

	@Test
	@DisplayName("Misc is mapped correctly: 1 attribute")
	void miscAbility() {
		MiscAbility original = SpecialAbility.misc("This is a test");

		String result = ComplexAttributeMapper.toString(original);

		assertThat(result).isEqualTo("Misc -> line=This is a test");

		ComplexAttribute complexAttribute = ComplexAttributeMapper.fromString(result);

		assertThat(complexAttribute).isNotNull().isInstanceOf(MiscAbility.class);

		MiscAbility parsed = (MiscAbility)complexAttribute;

		assertThat(parsed.getLine()).isEqualTo("This is a test");
	}

	@Test
	@DisplayName("StatConversion is mapped correctly")
	void statConversion() {
		StatConversion original = new StatConversion(PET_STAMINA, SPELL_DAMAGE, Percent.of(10), AttributeCondition.EMPTY);

		String result = ComplexAttributeMapper.toString(original);

		assertThat(result).isEqualTo("StatConversion -> from=Pet,Stamina; to=Power,SpellDamage; ratio%=10");

		ComplexAttribute complexAttribute = ComplexAttributeMapper.fromString(result);

		assertThat(complexAttribute).isNotNull().isInstanceOf(StatConversion.class);

		StatConversion parsed = (StatConversion)complexAttribute;

		assertThat(parsed.getFromStat()).isEqualTo(PET_STAMINA);
		assertThat(parsed.getToStat()).isEqualTo(SPELL_DAMAGE);
		assertThat(parsed.getRatioPct().getValue()).isEqualTo(10);
	}

	@Test
	@DisplayName("EffectIncrease is mapped correctly")
	void effectIncrease() {
		EffectIncreasePerEffectOnTarget original = new EffectIncreasePerEffectOnTarget(TalentTree.AFFLICTION, Percent.of(2), Percent.of(10), AttributeCondition.EMPTY);

		String result = ComplexAttributeMapper.toString(original);

		assertThat(result).isEqualTo("EffectIncrease -> tree=Affliction; %=2; max%=10");

		ComplexAttribute complexAttribute = ComplexAttributeMapper.fromString(result);

		assertThat(complexAttribute).isNotNull().isInstanceOf(EffectIncreasePerEffectOnTarget.class);

		EffectIncreasePerEffectOnTarget parsed = (EffectIncreasePerEffectOnTarget)complexAttribute;

		assertThat(parsed.getEffectTree()).isEqualTo(TalentTree.AFFLICTION);
		assertThat(parsed.getIncreasePerEffectPct().getValue()).isEqualTo(2);
		assertThat(parsed.getMaxIncreasePct().getValue()).isEqualTo(10);
	}
}