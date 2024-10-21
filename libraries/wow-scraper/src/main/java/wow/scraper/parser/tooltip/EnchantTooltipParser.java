package wow.scraper.parser.tooltip;

import lombok.Getter;
import wow.commons.model.categorization.ItemSubType;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.categorization.PveRole;
import wow.commons.model.effect.Effect;
import wow.commons.model.profession.ProfessionId;
import wow.commons.model.pve.GameVersionId;
import wow.commons.util.parser.Rule;
import wow.scraper.config.ScraperContext;
import wow.scraper.model.JsonSpellDetails;
import wow.scraper.parser.effect.ItemStatParser;
import wow.scraper.parser.stat.StatMatcher;

import java.util.List;
import java.util.Optional;

import static wow.scraper.util.CommonAssertions.assertBothAreEqual;

/**
 * User: POlszewski
 * Date: 2023-05-19
 */
@Getter
public class EnchantTooltipParser extends AbstractSpellTooltipParser {
	private ItemStatParser itemStatParser;
	private StatMatcher matcher;
	private String tooltip;
	private Integer requiredItemLevel;

	public EnchantTooltipParser(JsonSpellDetails spellDetails, GameVersionId gameVersion, ScraperContext scraperContext) {
		super(spellDetails, gameVersion, scraperContext);
	}

	@Override
	protected Rule[] getRules() {
		return new Rule[] {
				rulePhase,
				ruleRequiresLevel,
				ruleItemLevel,
				ruleBindsWhenPickedUp,
				ruleBindsWhenEquipped,
				ruleUnique,
				ruleUniqueEquipped,
				ruleClassRestriction,
				ruleFactionRestriction,
				ruleProfessionRestriction,
				ruleDroppedBy,
				ruleDropChance,
				ruleSellPrice,

				Rule.exact("5 sec cast", () -> {}),
				Rule.exact("Quest Item", () -> {}),
				Rule.regex("Max Stack: (\\d+)", x -> {}),
				Rule.regex("Requires level (\\d+)", x -> {}),
				Rule.exact("Reagents:", () -> ++currentLineIdx),
				Rule.exact("Tools:", () -> ++currentLineIdx),
				Rule.regex("Requires (Armor|Boots|Bracers|Chest|Chest, Chest|Cloak|Gloves|Melee Weapon|Weapons)", x -> {}),

				Rule.test(this::tryParseItemEffect, x -> tooltip = x),
		};
	}

	@Override
	protected void beforeParse() {
		this.itemType = ItemType.ENCHANT;
		this.itemStatParser = new ItemStatParser(
				gameVersion,
				getStatPatternRepository()::getEnchantStatParser,
				getItemSpellRepository()
		);
	}

	@Override
	protected void afterParse() {
		//VOID
	}

	private boolean tryParseItemEffect(String line) {
		var statParser = getStatPatternRepository().getEnchantStatParser(gameVersion);
		var tooltipCleaner = new EnchantTooltipCleaner(line);
		var cleanedLine = tooltipCleaner.cleanEnchantTooltip();

		if (!statParser.tryParse(cleanedLine)) {
			return false;
		}

		this.matcher = statParser.getSuccessfulMatcher().orElseThrow();
		this.itemStatParser.tryParseItemEffect(cleanedLine);
		this.requiredItemLevel = tooltipCleaner.getRequiredItemLevel();
		return true;
	}

	public List<ItemType> getItemTypes() {
		return matcher.getParamItemTypes();
	}

	public List<ItemSubType> getItemSubTypes() {
		return matcher.getParamItemSubTypes();
	}

	@Override
	public ProfessionId getRequiredProfession() {
		ProfessionId matchedRequiredProfession = matcher.getRequiredProfession();

		if (matchedRequiredProfession != null && requiredProfession != null) {
			assertBothAreEqual("required profession", matchedRequiredProfession, requiredProfession);
			return requiredProfession;
		}

		if (matchedRequiredProfession != null) {
			return matchedRequiredProfession;
		}

		return requiredProfession;
	}

	@Override
	public Integer getRequiredProfessionLevel() {
		ProfessionId matchedRequiredProfession = matcher.getRequiredProfession();
		Integer matchedRequiredProfessionLevel = matcher.getRequiredProfessionLevel();

		if (matchedRequiredProfession != null && requiredProfession != null) {
			assertBothAreEqual("required profession", matchedRequiredProfessionLevel, requiredProfessionLevel);
			return requiredProfessionLevel;
		}

		if (matchedRequiredProfession != null) {
			return matchedRequiredProfessionLevel;
		}

		return requiredProfessionLevel;
	}

	@Override
	public String getTooltip() {
		return tooltip;
	}

	public List<PveRole> getPveRoles() {
		return matcher.getParamPveRoles();
	}

	public Optional<Effect> getEffect() {
		return itemStatParser.getUniqueItemEffect();
	}

	@Override
	public String getIcon() {
		String icon = details.getIcon();
		if ("inv_misc_note_01".equals(icon)) {
			return "spell_holy_greaterheal";
		}
		return icon;
	}
}
