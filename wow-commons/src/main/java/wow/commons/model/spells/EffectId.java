package wow.commons.model.spells;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2020-10-01
 */
@AllArgsConstructor
@Getter
public enum EffectId {
	// special effect

	AMPLIFY_CURSE("Amplify Curse"),
	SHADOW_EMBRACE_1("Shadow Embrace:1", Group.SHADOW_EMBRACE),
	SHADOW_EMBRACE_2("Shadow Embrace:2", Group.SHADOW_EMBRACE),
	SHADOW_EMBRACE_3("Shadow Embrace:3", Group.SHADOW_EMBRACE),
	SHADOW_EMBRACE_4("Shadow Embrace:4", Group.SHADOW_EMBRACE),
	SHADOW_EMBRACE_5("Shadow Embrace:5", Group.SHADOW_EMBRACE),
	SHADOW_TRANCE("Shadow Trance"),
	SHADOW_VULNERABILITY_4("Shadow Vulnerability:4", Group.SHADOW_VULNERABILITY),
	SHADOW_VULNERABILITY_8("Shadow Vulnerability:8", Group.SHADOW_VULNERABILITY),
	SHADOW_VULNERABILITY_12("Shadow Vulnerability:12", Group.SHADOW_VULNERABILITY),
	SHADOW_VULNERABILITY_16("Shadow Vulnerability:16", Group.SHADOW_VULNERABILITY),
	SHADOW_VULNERABILITY_20("Shadow Vulnerability:20", Group.SHADOW_VULNERABILITY),

	// dot effects

	CORRUPTION("Corruption"),
	CURSE_OF_AGONY("Curse of Agony", Group.CURSE),
	CURSE_OF_DOOM("Curse of Doom", Group.CURSE),
	SIPHON_LIFE("Siphon Life"),
	UNSTABLE_AFFLICTION("Unstable Affliction"),
	IMMOLATE("Immolate"),

	// channeled

	DRAIN_LIFE("Drain Life"),

	// racials

	BLOOD_FURY("Blood Fury"),

	// other-class debuffs

	FIRE_VULNERABILITY("Fire Vulnerability"),
	SHADOW_WEAVING("Shadow Weaving"),

	// trinkets

	TALISMAN_OF_EPHEMERAL_POWER("Talisman of Ephemeral Power");

	public enum Group {
		CURSE,
		SHADOW_EMBRACE,
		SHADOW_VULNERABILITY
	}

	private final String name;
	private final Group group;

	EffectId(String name) {
		this(name, null);
	}

	public static EffectId parse(String value) {
		return EnumUtil.parse(value, values(), x -> x.name);
	}

	@Override
	public String toString() {
		return name;
	}
}
