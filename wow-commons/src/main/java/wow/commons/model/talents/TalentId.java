package wow.commons.model.talents;

/**
 * User: POlszewski
 * Date: 2020-10-01
 */
public enum TalentId {
	// affliction

	Suppression("Suppression", 1),
	ImprovedCorruption("Improved Corruption", 2),
	ImprovedCurseOfWeakness("Improved Curse of Weakness", 3),
	ImprovedDrainSoul("Improved Drain Soul", 4),
	ImprovedLifeTap("Improved Life Tap", 5),
	SoulSiphon("Soul Siphon", 6),
	ImprovedCurseOfAgony("Improved Curse of Agony", 7),
	FelConcentration("Fel Concentration", 8),
	AmplifyCurse("Amplify Curse", 9),
	GrimReach("Grim Reach", 10),
	Nightfall("Nightfall", 11),
	EmpoweredCorruption("Empowered Corruption", 12),
	ShadowEmbrace("Shadow Embrace", 13),
	SiphonLife("Siphon Life", 14),
	CurseOfExhaustion("Curse of Exhaustion", 15),
	ShadowMastery("Shadow Mastery", 16),
	Contagion("Contagion", 17),
	DarkPact("Dark Pact", 18),
	ImprovedHowlOfTerror("Improved Howl of Terror", 19),
	Malediction("Malediction", 20),
	UnstableAffliction("Unstable Affliction", 21),

	// demonology

	ImprovedHealthstone("Improved Healthstone", 22),
	ImprovedImp("Improved Imp", 23),
	DemonicEmbrace("Demonic Embrace", 24),
	ImprovedHealthFunnel("Improved Health Funnel", 25),
	ImprovedVoidwalker("Improved Voidwalker", 26),
	FelIntellect("Fel Intellect", 27),
	ImprovedSuccubus("Improved Succubus", 28),
	FelDomination("Fel Domination", 29),
	FelStamina("Fel Stamina", 30),
	DemonicAegis("Demonic Aegis", 31),
	MasterSummoner("Master Summoner", 32),
	UnholyPower("Unholy Power", 33),
	ImprovedEnslaveDemon("Improved Enslave Demon", 34),
	DemonicSacrifice("Demonic Sacrifice", 35),
	MasterConjuror("Master Conjuror", 36),
	ManaFeed("Mana Feed", 37),
	MasterDemonologist("Master Demonologist", 38),
	DemonicResilience("Demonic Resilience", 39),
	SoulLink("Soul Link", 40),
	DemonicKnowledge("Demonic Knowledge", 41),
	DemonicTactics("Demonic Tactics", 42),
	SummonFelguard("Summon Felguard", 43),

	// destruction

	ImprovedShadowBolt("Improved Shadow Bolt", 44),
	Cataclysm("Cataclysm", 45),
	Bane("Bane", 46),
	Aftermath("Aftermath", 47),
	ImprovedFirebolt("Improved Firebolt", 48),
	ImprovedLashOfPain("Improved Lash of Pain", 49),
	Devastation("Devastation", 50),
	Shadowburn("Shadowburn", 51),
	Intensity("Intensity", 52),
	DestructiveReach("Destructive Reach", 53),
	ImprovedSearingPain("Improved Searing Pain", 54),
	Pyroclasm("Pyroclasm", 55),
	ImprovedImmolate("Improved Immolate", 56),
	Ruin("Ruin", 57),
	NetherProtection("Nether Protection", 58),
	Emberstorm("Emberstorm", 59),
	Backlash("Backlash", 60),
	Conflagrate("Conflagrate", 61),
	SoulLeech("Soul Leech", 62),
	ShadowAndFlame("Shadow and Flame", 63),
	Shadowfury("Shadowfury", 64),

	;

	private final String name;
	private final int talentCalculatorPosition;

	TalentId(String name, int talentCalculatorPosition) {
		this.name = name;
		this.talentCalculatorPosition = talentCalculatorPosition;
	}

	public static TalentId parse(String name) {
		if (name == null) {
			return null;
		}
		for (TalentId value : values()) {
			if (value.getName().equalsIgnoreCase(name)) {
				return value;
			}
		}
		throw new IllegalArgumentException("Unknown talent: " + name);
	}

	public static TalentId fromTalentCalculatorPosition(int pos) {
		for (TalentId value : values()) {
			if (value.talentCalculatorPosition == pos) {
				return value;
			}
		}
		throw new IllegalArgumentException("Unknown talent#: " + pos);
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}
}
