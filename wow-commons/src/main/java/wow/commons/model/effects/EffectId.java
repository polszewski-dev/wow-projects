package wow.commons.model.effects;

/**
 * User: POlszewski
 * Date: 2020-10-01
 */
public enum EffectId {
	// special effect

	AmplifyCurse("Amplify Curse"),
	ShadowEmbrace_1("Shadow Embrace:1", Group.ShadowEmbrace),
	ShadowEmbrace_2("Shadow Embrace:2", Group.ShadowEmbrace),
	ShadowEmbrace_3("Shadow Embrace:3", Group.ShadowEmbrace),
	ShadowEmbrace_4("Shadow Embrace:4", Group.ShadowEmbrace),
	ShadowEmbrace_5("Shadow Embrace:5", Group.ShadowEmbrace),
	ShadowTrance("Shadow Trance"),
	ShadowVulnerability_4("Shadow Vulnerability:4", Group.ShadowVulnerability),
	ShadowVulnerability_8("Shadow Vulnerability:8", Group.ShadowVulnerability),
	ShadowVulnerability_12("Shadow Vulnerability:12", Group.ShadowVulnerability),
	ShadowVulnerability_16("Shadow Vulnerability:16", Group.ShadowVulnerability),
	ShadowVulnerability_20("Shadow Vulnerability:20", Group.ShadowVulnerability),

	// dot effects

	Corruption("Corruption"),
	CurseOfAgony("Curse of Agony", Group.Curse),
	CurseOfDoom("Curse of Doom", Group.Curse),
	SiphonLife("Siphon Life"),
	UnstableAffliction("Unstable Affliction"),
	Immolate("Immolate"),

	// channeled

	DrainLife("Drain Life"),

	// racials

	BloodFury("Blood Fury"),

	// other-class debuffs

	FireVulnerability("Fire Vulnerability"),
	ShadowWeaving("Shadow Weaving"),

	// trinkets

	TalismanOfEphemeralPower("Talisman of Ephemeral Power"),

	;

	public enum Group {
		Curse,
		ShadowEmbrace,
		ShadowVulnerability
	}

	private final String name;
	private final Group group;

	EffectId(String name) {
		this(name, null);
	}

	EffectId(String name, Group group) {
		this.name = name;
		this.group = group;
	}

	public static EffectId parse(String name) {
		if (name == null) {
			return null;
		}
		for (EffectId value : values()) {
			if (value.getName().equalsIgnoreCase(name)) {
				return value;
			}
		}
		throw new IllegalArgumentException("Unknown effect: " + name);
	}

	public String getName() {
		return name;
	}

	public Group getGroup() {
		return group;
	}

	@Override
	public String toString() {
		return name;
	}
}
