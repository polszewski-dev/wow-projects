package wow.commons.model.buff;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2023-08-03
 */
@AllArgsConstructor
@Getter
public enum BuffId {
	ARCANE_BRILLIANCE("Arcane Brilliance"),
	POWER_WORD_FORTITUDE("Power Word: Fortitude"),
	PRAYER_OF_FORTITUDE("Prayer of Fortitude"),
	PRAYER_OF_SPIRIT("Prayer of Spirit"),
	GREATER_BLESSING_OF_KINGS("Greater Blessing of Kings"),
	GIFT_OF_THE_WILD("Gift of the Wild"),
	DEMON_SKIN("Demon Skin"),
	DEMON_ARMOR("Demon Armor"),
	TOUCH_OF_SHADOW("Touch of Shadow"),
	BURNING_WISH("Burning Wish"),
	SHADOWFORM("Shadowform"),
	BRILLIANT_WIZARD_OIL("Brilliant Wizard Oil"),
	RUNN_TUM_TUBER("Runn Tum Tuber"),
	FLASK_OF_SUPREME_POWER("Flask of Supreme Power"),
	SPIRIT_OF_ZANZA("Spirit of Zanza"),
	GREATER_ARCANE_ELIXIR("Greater Arcane Elixir"),
	ELIXIR_OF_SHADOW_POWER("Elixir of Shadow Power"),
	MOONKIN_AURA("Moonkin Aura"),
	RALLYING_CRY_OF_THE_DRAGONSLAYER("Rallying Cry of the Dragonslayer"),
	SPIRIT_OF_ZANDALAR("Spirit of Zandalar"),
	WARCHIEFS_BLESSING("Warchief's Blessing"),
	SAYGES_DARK_FORTUNE_OF_DAMAGE("Sayge's Dark Fortune of Damage"),
	MOLDARS_MOXIE("Mol'dar's Moxie"),
	SLIPKIKS_SAVVY("Slip'kik's Savvy"),
	SONGFLOWER_SERENADE("Songflower Serenade"),
	SHADOW_WEAVING("Shadow Weaving"),
	IMPROVED_SCORCH("Improved Scorch"),
	CURSE_OF_THE_ELEMENTS("Curse of the Elements"),
	CURSE_OF_SHADOW("Curse of Shadow"),
	FEL_ARMOR("Fel Armor"),
	SUPERIOR_WIZARD_OIL("Superior Wizard Oil"),
	WELL_FED_SP("Well Fed (sp)"),
	FLASK_OF_PURE_DEATH("Flask of Pure Death"),
	WRATH_OF_AIR_TOTEM("Wrath of Air Totem"),
	TOTEM_OF_WRATH("Totem of Wrath"),
	DRUMS_OF_BATTLE("Drums of Battle"),
	DESTRUCTION("Destruction"),
	MISERY("Misery"),
	CURSE_OF_THE_ELEMENTS_IMPROVED("Curse of the Elements (improved)"),
	;

	private final String name;

	public static BuffId parse(String value) {
		return EnumUtil.parse(value, values(), x -> x.name);
	}

	public static BuffId tryParse(String value) {
		return EnumUtil.tryParse(value, values(), x -> x.name);
	}

	@Override
	public String toString() {
		return name;
	}
}
