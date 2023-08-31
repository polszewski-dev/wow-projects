package wow.commons.model.effect;

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
	// warlock

	CORRUPTION("Corruption"),
	CURSE_OF_AGONY("Curse of Agony", Group.CURSE),
	CURSE_OF_DOOM("Curse of Doom", Group.CURSE),
	SEED_OF_CORRUPTION("Seed of Corruption"),
	SIPHON_LIFE("Siphon Life"),
	UNSTABLE_AFFLICTION("Unstable Affliction"),
	IMMOLATE("Immolate"),

	DRAIN_LIFE("Drain Life"),
	DRAIN_MANA("Drain Mana"),
	DRAIN_SOUL("Drain Soul"),
	HELLFIRE("Hellfire"),
	RAIN_OF_FIRE("Rain of Fire"),

	SHADOW_TRANCE("Shadow Trance"),
	SHADOW_EMBRACE_1("Shadow Embrace:1", Group.SHADOW_EMBRACE, 1),
	SHADOW_EMBRACE_2("Shadow Embrace:2", Group.SHADOW_EMBRACE, 2),
	SHADOW_EMBRACE_3("Shadow Embrace:3", Group.SHADOW_EMBRACE, 3),
	SHADOW_EMBRACE_4("Shadow Embrace:4", Group.SHADOW_EMBRACE, 4),
	SHADOW_EMBRACE_5("Shadow Embrace:5", Group.SHADOW_EMBRACE, 5),
	SHADOW_VULNERABILITY_4("Shadow Vulnerability:4", Group.SHADOW_VULNERABILITY, 1),
	SHADOW_VULNERABILITY_8("Shadow Vulnerability:8", Group.SHADOW_VULNERABILITY, 2),
	SHADOW_VULNERABILITY_12("Shadow Vulnerability:12", Group.SHADOW_VULNERABILITY, 3),
	SHADOW_VULNERABILITY_16("Shadow Vulnerability:16", Group.SHADOW_VULNERABILITY, 4),
	SHADOW_VULNERABILITY_20("Shadow Vulnerability:20", Group.SHADOW_VULNERABILITY, 5),

	AMPLIFY_CURSE("Amplify Curse"),
	CURSE_OF_EXHAUSTION("Curse of Exhaustion"),
	CURSE_OF_RECKLESSNESS("Curse of Recklessness"),
	CURSE_OF_SHADOW("Curse of Shadow"),
	CURSE_OF_TONGUES("Curse of Tongues"),
	CURSE_OF_WEAKNESS("Curse of Weakness"),
	CURSE_OF_THE_ELEMENTS("Curse of the Elements"),
	DEMON_ARMOR("Demon Armor"),
	DEMON_SKIN("Demon Skin"),
	FEL_ARMOR("Fel Armor"),
	SHADOW_WARD("Shadow Ward"),

	// priest

	DEVOURING_PLAGUE("Devouring Plague"),
	MIND_FLAY("Mind Flay"),
	SHADOW_WORD_PAIN("Shadow Word: Pain"),
	STARSHARDS("Starshards"),
	VAMPIRIC_TOUCH("Vampiric Touch"),

	SHADOWFORM("Shadowform"),

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
	private final int rank;

	EffectId(String name) {
		this(name, null, 1);
	}

	EffectId(String name, Group group) {
		this(name, group, 1);
	}

	public static EffectId parse(String value) {
		return EnumUtil.parse(value, values(), x -> x.name);
	}

	@Override
	public String toString() {
		return name;
	}
}
