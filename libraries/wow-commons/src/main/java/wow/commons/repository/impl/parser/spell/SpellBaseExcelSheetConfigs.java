package wow.commons.repository.impl.parser.spell;

/**
 * User: POlszewski
 * Date: 2025-12-02
 */
public final class SpellBaseExcelSheetConfigs {
	public static final AbstractSpellSheetParser.Config ABILITIES_CONFIG = new AbstractSpellSheetParser.Config(3, 3);
	public static final AbstractSpellSheetParser.Config ABILITY_SPELLS_CONFIG = new AbstractSpellSheetParser.Config(3, 1);
	public static final AbstractSpellSheetParser.Config ITEM_SPELLS_CONFIG = new AbstractSpellSheetParser.Config(3, 5);
	public static final AbstractSpellSheetParser.Config TALENT_SPELLS_CONFIG = new AbstractSpellSheetParser.Config(3, 1);
	public static final AbstractSpellSheetParser.Config CONSUME_SPELLS_CONFIG = new AbstractSpellSheetParser.Config(3, 1);

	public static final SpellEffectSheetParser.Config ABILITY_EFFECTS_CONFIG = new SpellEffectSheetParser.Config(2, 3, 0, 2, true);
	public static final SpellEffectSheetParser.Config ITEM_EFFECTS_CONFIG = new SpellEffectSheetParser.Config(1, 3, 1, 3, true);
	public static final SpellEffectSheetParser.Config TALENT_EFFECTS_CONFIG = new SpellEffectSheetParser.Config(1, 3, 0, 1, false);
	public static final SpellEffectSheetParser.Config RACIAL_EFFECTS_CONFIG = new SpellEffectSheetParser.Config(0, 3, 0, 0, false);
	public static final SpellEffectSheetParser.Config BUFF_EFFECTS_CONFIG = new SpellEffectSheetParser.Config(0, 3, 0, 0, false);
	public static final SpellEffectSheetParser.Config CONSUME_EFFECTS_CONFIG = new SpellEffectSheetParser.Config(0, 3, 0, 0, false);

	public static final int MAX_TALENT_MODIFIER_ATTRIBUTES = 6;
	public static final int MAX_TALENT_STAT_CONVERSIONS = 2;
	public static final int MAX_TALENT_EVENTS = 1;

	private SpellBaseExcelSheetConfigs() {}
}
