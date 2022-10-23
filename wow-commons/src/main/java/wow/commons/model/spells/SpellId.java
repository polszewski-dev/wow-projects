package wow.commons.model.spells;

import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2020-10-01
 */
public enum SpellId {
	None("None"),

	Trinket("Trinket"),
	Potion("Potion"),

	// racial

	BloodFury("Blood Fury"),

	// warlock

	AmplifyCurse("Amplify Curse"),
	Corruption("Corruption"),
	CurseOfAgony("Curse of Agony"),
	CurseOfDoom("Curse of Doom"),
	CurseOfTheElements("Curse of the Elements"),
	CurseOfExhaustion("Curse of Exhaustion"),
	CurseOfShadows("Curse of Shadows"),
	CurseOfWeakness("Curse of Weakness"),
	DarkPact("Dark Pact"),
	DeathCoil("Death Coil"),
	DrainLife("Drain Life"),
	DrainMana("Drain Mana"),
	DrainSoul("Drain Soul"),
	HowlOfTerror("Howl of Terror"),
	LifeTap("Life Tap"),
	SeedOfCorruption("Seed of Corruption"),
	SeedOfCorruptionDirect("Seed of Corruption (direct)"),
	SiphonLife("Siphon Life"),
	UnstableAffliction("Unstable Affliction"),

	CreateHealthstone("Create Healthstone"),
	DemonArmor("Demon Armor"),
	DemonicSacrifice("Demonic Sacrifice"),
	EnslaveDemon("Enslave Demon"),
	FelArmor("Fel Armor"),
	HealthFunnel("Health Funnel"),
	ShadowWard("Shadow Ward"),
	SummonImp("Summon Imp"),
	SummonVoidwalker("Summon Voidwalker"),
	SummonSuccubus("Summon Succubus"),
	SummonFelhunter("Summon Felhunter"),
	SummonFelguard("Summon Felguard"),

	Conflagrate("Conflagrate"),
	Hellfire("Hellfire"),
	Immolate("Immolate"),
	Incinerate("Incinerate"),
	RainOfFire("Rain of Fire"),
	SearingPain("Searing Pain"),
	ShadowBolt("Shadow Bolt"),
	Shadowburn("Shadowburn"),
	Shadowfury("Shadowfury"),
	SoulFire("Soul Fire"),

	Firebolt("Firebolt"),
	BloodPact("Blood Pact"),
	FireShield("Fire Shield"),
	PhaseShift("Phase Shift"),
	Torment("Torment"),
	ConsumeShadows("Consume Shadows"),
	Sacrifice("Sacrifice"),
	SoothingKiss("Soothing Kiss"),
	LashOfPain("Lash of Pain"),
	LesserInvisibility("Lesser Invisibility"),
	Seduction("Seduction"),

	;

	private final String name;

	SpellId(String name) {
		this.name = name;
	}

	public static SpellId parse(String name) {
		if (name == null) {
			return null;
		}
		return Stream.of(values())
				.filter(x -> x.getName().equalsIgnoreCase(name))
				.findAny()
				.orElseThrow(() -> new IllegalArgumentException(name));
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}
}
