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

	CastTimeReduction(Duration.class, "reduced cast time"),//
	CooldownReduction(Duration.class),
	CostReductionPct(Percent.class, "reduced cost"),//
	ThreatReductionPct(Percent.class, "reduced threat"),
	PushbackReductionPct(Percent.class, "reduced pushback"),
	RangeIncreasePct(Percent.class, "increased range"),
	DurationIncreasePct(Percent.class, "increased duration"),//
	CastInstantly(Boolean.class),

	SpellCoeffBonusPct(Percent.class, "increased spell coefficient"),//
	EffectIncreasePct(Percent.class, "increased effect"),//
	DirectDamageIncreasePct(Percent.class, "increased direct damage"),//
	DotDamageIncreasePct(Percent.class, "increased dot damage"),//
	CritDamageIncreasePct(Percent.class, "increased crit damage"),//Ruin!!
	ExtraCritCoeff("gimmick to handle ISB"),

	StaIncreasePct(Percent.class, "increased sta"),
	IntIncreasePct(Percent.class, "increased int"),
	SpiIncreasePct(Percent.class, "increased spi"),
	MaxHealthIncreasePct(Percent.class, "increased max health"),
	MaxManaIncreasePct(Percent.class, "increased max mana"),
	MeleeCritIncreasePct(Percent.class),
	DamageTakenIncreasePct(Percent.class),//

	PetStaIncreasePct(Percent.class, "increased pet sta"),
	PetIntIncreasePct(Percent.class, "increased pet int"),
	PetSpellCritIncreasePct(Percent.class),
	PetMeleeCritIncreasePct(Percent.class),
	PetMeleeDamageIncreasePct(Percent.class),

	StatConversion(ComplexAttribute.class),//
	EffectIncreasePerEffectOnTarget(ComplexAttribute.class),

	ManaTransferredToPetPct(Percent.class),

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

	public boolean isScalarAttribute() {
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
