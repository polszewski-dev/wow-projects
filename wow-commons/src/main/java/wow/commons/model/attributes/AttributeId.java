package wow.commons.model.attributes;

import wow.commons.model.Duration;
import wow.commons.model.Percent;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2021-03-04
 */
public enum AttributeId {
	Strength("str"),
	Agility("agi"),
	Stamina("sta"),
	Intellect("int"),
	Spirit("spi"),

	BaseStatsIncrease("base stats"),
	BaseStatsIncreasePct(Percent.class, "base stats"),

	AttackPower,
	RangedAttackPower,
	CritPct(Percent.class),
	CritRating,
	HitPct(Percent.class),
	HitRating,
	HasteRating,
	ExpertiseRating,
	ArmorPenetration,

	SpellPower("sp"),
	SpellDamage("sd"),

	AdditionalSpellDamageTakenPct(Percent.class, "additinal spell dmg taken"),

	SpellCritRating("crit"),
	SpellCritPct(Percent.class, "crit"),
	SpellHitRating("hit"),
	SpellHitPct(Percent.class, "hit"),
	SpellHasteRating("haste"),
	SpellHastePct(Percent.class, "haste"),
	SpellPenetration("penetration"),

	IncreasedCriticalDamagePct(Percent.class, "increased crit dmg"),
	DamageTakenPct(Percent.class, "increased spell dmg"),

	AdditionalDamage("additional damage"),

	HealingPower("healing"),

	Mp5("mp5"),
	Hp5("hp5"),

	Armor("armor"),
	Dodge,
	DodgeRating,
	Defense,
	DefenseRating,
	Block,
	BlockPct(Percent.class),
	BlockRating,
	ShieldBlock,
	ShieldBlockRating,
	Parry,
	ParryRating,
	ResilienceRating("resi"),

	Resistance("resist"),
	ResistAll("resist all"),

	SpeedIncreasePct(Percent.class, "movement speed"),

	//

	SpecialAbilities(ComplexAttribute.class),

	Sockets(ComplexAttribute.class),
	SetPieces(ComplexAttribute.class),

	// talents

	castTimeReduction(Duration.class, "reduced cast time"),//
	cooldownReduction(Duration.class),
	costReductionPct(Percent.class, "reduced cost"),//
	threatReductionPct(Percent.class, "reduced threat"),
	pushbackReductionPct(Percent.class, "reduced pushback"),
	rangeIncreasePct(Percent.class, "increased range"),
	durationIncreasePct(Percent.class, "increased duration"),//
	castInstantly(Boolean.class),

	spellCoeffBonusPct(Percent.class, "increased spell coefficient"),//
	effectIncreasePct(Percent.class, "increased effect"),//
	directDamageIncreasePct(Percent.class, "increased direct damage"),//
	dotDamageIncreasePct(Percent.class, "increased dot damage"),//
	critDamageIncreasePct(Percent.class, "increased crit damage"),//Ruin!!
	extraCritCoeff("gimmick to handle ISB"),

	staIncreasePct(Percent.class, "increased sta"),
	intIncreasePct(Percent.class, "increased int"),
	spiIncreasePct(Percent.class, "increased spi"),
	maxHealthIncreasePct(Percent.class, "increased max health"),
	maxManaIncreasePct(Percent.class, "increased max mana"),
	meleeCritIncreasePct(Percent.class),
	damageTakenIncreasePct(Percent.class),//

	petStaIncreasePct(Percent.class, "increased pet sta"),
	petIntIncreasePct(Percent.class, "increased pet int"),
	petSpellCritIncreasePct(Percent.class),
	petMeleeCritIncreasePct(Percent.class),
	petMeleeDamageIncreasePct(Percent.class),

	statConversion(ComplexAttribute.class),//
	effectIncreasePerEffectOnTarget(ComplexAttribute.class),

	manaTransferredToPetPct(Percent.class),

	;

	private final Class type;
	private final String shortName;

	private static final List<AttributeId> complexAttributeIds = Stream.of(values())
			.filter(AttributeId::isComplexAttribute)
			.collect(Collectors.toUnmodifiableList());;

	AttributeId(Class type, String shortName) {
		List<Class> allowedTypes = List.of(Double.class, Percent.class, Boolean.class, Duration.class, ComplexAttribute.class);
		if (!allowedTypes.contains(type)) {
			throw new IllegalArgumentException();
		}
		this.type = type;
		this.shortName = shortName;
	}

	AttributeId(Class type) {
		this(type, null);
	}

	AttributeId(String shortName) {
		this(Double.class, shortName);
	}

	AttributeId() {
		this(Double.class, null);
	}

	public static AttributeId parse(String value) {
		if (value == null) {
			return null;
		}
		return valueOf(value);
	}

	public Class getType() {
		return type;
	}

	public boolean isDoubleAttribute() {
		return type == Double.class;
	}

	public boolean isPercentAttribute() {
		return type == Percent.class;
	}

	public boolean isBooleanAttribute() {
		return type == Boolean.class;
	}

	public boolean isDurationAttribute() {
		return type == Duration.class;
	}

	public boolean isScalarAtribute() {
		return isDoubleAttribute() || isPercentAttribute() || isDurationAttribute();
	}

	public boolean isPrimitiveAttribute() {
		return !isComplexAttribute();
	}

	public boolean isComplexAttribute() {
		return type == ComplexAttribute.class;
	}
	
	public static List<AttributeId> getComplexAttributeIds() {
		return complexAttributeIds;
	}

	@Override
	public String toString() {
		return shortName != null ? shortName : name();
	}
}
