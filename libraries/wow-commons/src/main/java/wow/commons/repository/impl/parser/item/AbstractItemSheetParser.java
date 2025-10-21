package wow.commons.repository.impl.parser.item;

import wow.commons.model.categorization.Binding;
import wow.commons.model.categorization.ItemRarity;
import wow.commons.model.categorization.ItemSubType;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.config.Description;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.effect.Effect;
import wow.commons.model.effect.EffectSource;
import wow.commons.model.effect.impl.EffectImpl;
import wow.commons.model.item.BasicItemInfo;
import wow.commons.model.item.ItemSource;
import wow.commons.model.source.Source;
import wow.commons.model.spell.ActivatedAbility;
import wow.commons.model.spell.SpellId;
import wow.commons.model.spell.impl.ActivatedAbilityImpl;
import wow.commons.repository.impl.parser.excel.WowExcelSheetParser;
import wow.commons.repository.impl.parser.excel.mapper.ItemEffectMapper;
import wow.commons.repository.spell.SpellRepository;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.IntStream;

import static wow.commons.repository.impl.parser.excel.CommonColumnNames.colEffectDescr;
import static wow.commons.repository.impl.parser.excel.CommonColumnNames.colEffectStats;
import static wow.commons.repository.impl.parser.item.ItemBaseExcelColumnNames.*;

/**
 * User: POlszewski
 * Date: 2022-11-24
 */
public abstract class AbstractItemSheetParser extends WowExcelSheetParser {
	protected final SourceParserFactory sourceParserFactory;
	protected final SpellRepository spellRepository;
	private final ItemEffectMapper itemEffectMapper;

	protected AbstractItemSheetParser(String sheetName, SourceParserFactory sourceParserFactory, SpellRepository spellRepository) {
		super(sheetName);
		this.sourceParserFactory = sourceParserFactory;
		this.spellRepository = spellRepository;
		this.itemEffectMapper = new ItemEffectMapper(spellRepository);
	}

	protected int getId() {
		return colId.getInteger();
	}

	private final ExcelColumn colItemType = column(ITEM_TYPE);
	private final ExcelColumn colItemSubtype = column(ITEM_SUBTYPE);
	private final ExcelColumn colRarity = column(RARITY);
	private final ExcelColumn colBinding = column(BINDING);
	private final ExcelColumn colUnique = column(UNIQUE);
	private final ExcelColumn colItemLevel = column(ITEM_LEVEL);
	private final ExcelColumn colSource = column(SOURCE);
	private final ExcelColumn colActivatedAbility = column(ITEM_ACTIVATED_ABILITY);

	protected BasicItemInfo getBasicItemInfo() {
		var itemType = colItemType.getEnum(ItemType::parse);
		var itemSubType = colItemSubtype.getEnum(ItemSubType::parse, null);
		var rarity = colRarity.getEnum(ItemRarity::parse);
		var binding = colBinding.getEnum(Binding::parse);
		var unique = colUnique.getBoolean();
		var itemLevel = colItemLevel.getInteger();
		var source = colSource.getString();
		var sources = getSources(source);

		return new BasicItemInfo(itemType, itemSubType, rarity, binding, unique, itemLevel, sources);
	}

	private Set<Source> getSources(String source) {
		var reqPhase = getTimeRestriction().earliestPhaseId();
		return sourceParserFactory.create(reqPhase).parse(source);
	}

	protected List<Effect> readItemEffects(String prefix, int maxEffects, TimeRestriction timeRestriction, EffectSource source) {
		return IntStream.rangeClosed(1, maxEffects)
				.mapToObj(i -> readItemEffect(prefix, i, timeRestriction, source))
				.filter(Objects::nonNull)
				.toList();
	}

	protected Effect readItemEffect(String prefix, int i, TimeRestriction timeRestriction, EffectSource source) {
		var colStats = column(colEffectStats(prefix, i), true);
		var colDescr = column(colEffectDescr(prefix, i), true);

		var stats = colStats.getString(null);
		var descr = colDescr.getString(null);

		if (stats == null) {
			if (descr != null) {
				return Effect.EMPTY;
			}
			return null;
		}

		var phaseId = timeRestriction.earliestPhaseId();
		var effect = (EffectImpl) itemEffectMapper.fromString(stats, phaseId);

		if (effect.getDescription() == null) {
			effect.setDescription(new Description("", null, descr));
		}

		if (!Objects.equals(effect.getTooltip(), descr)) {
			throw new IllegalArgumentException(descr);
		}

		effect.attachSource(source);

		return effect;
	}

	protected ActivatedAbility getActivatedAbility(ItemSource source) {
		var spellId = colActivatedAbility.getNullableInteger(SpellId::ofNullable);

		if (spellId == null) {
			return null;
		}

		var phaseId = getTimeRestriction().earliestPhaseId();
		var activatedAbility = (ActivatedAbilityImpl) spellRepository.getAbility(spellId, phaseId).orElseThrow();

		activatedAbility.attachSource(source);

		return activatedAbility;
	}
}
