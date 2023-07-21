package wow.scraper.parsers.tooltip;

import lombok.Getter;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.categorization.ItemSubType;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.categorization.PveRole;
import wow.commons.model.professions.ProfessionId;
import wow.commons.model.pve.GameVersionId;
import wow.commons.util.parser.Rule;
import wow.scraper.model.JsonSpellDetails;
import wow.scraper.parsers.stats.StatMatcher;
import wow.scraper.parsers.stats.StatParser;
import wow.scraper.repository.StatPatternRepository;

import java.util.List;

import static wow.scraper.util.CommonAssertions.assertBothAreEqual;

/**
 * User: POlszewski
 * Date: 2023-05-19
 */
@Getter
public class EnchantTooltipParser extends AbstractSpellTooltipParser {
	private StatParser statParser;
	private String tooltip;

	public EnchantTooltipParser(JsonSpellDetails spellDetails, GameVersionId gameVersion, StatPatternRepository statPatternRepository) {
		super(spellDetails, gameVersion, statPatternRepository);
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

				Rule.test(line -> statParser.tryParse(cleanEnchantTooltip(line)), x -> tooltip = x),

				Rule.exact("5 sec cast", () -> {}),
				Rule.exact("Quest Item", () -> {}),
				Rule.regex("Max Stack: (\\d+)", x -> {}),
				Rule.regex("Requires level (\\d+)", x -> {}),
				Rule.exact("Reagents:", () -> ++currentLineIdx),
				Rule.exact("Tools:", () -> ++currentLineIdx),
				Rule.regex("Requires (Armor|Boots|Bracers|Chest|Chest, Chest|Cloak|Gloves|Melee Weapon|Weapons)", x -> {}),
		};
	}

	@Override
	protected void beforeParse() {
		this.itemType = ItemType.ENCHANT;
		this.statParser = statPatternRepository.getEnchantStatParser(gameVersion);
	}

	@Override
	protected void afterParse() {
		//VOID
	}

	public List<ItemType> getItemTypes() {
		return getMatcher().getParamItemTypes();
	}

	public List<ItemSubType> getItemSubTypes() {
		return getMatcher().getParamItemSubTypes();
	}

	@Override
	public ProfessionId getRequiredProfession() {
		ProfessionId matchedRequiredProfession = getMatcher().getRequiredProfession();

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
		ProfessionId matchedRequiredProfession = getMatcher().getRequiredProfession();
		Integer matchedRequiredProfessionLevel = getMatcher().getRequiredProfessionLevel();

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
		return getMatcher().getParamPveRoles();
	}

	private StatMatcher getMatcher() {
		return statParser.getUniqueSuccessfulMatcher();
	}

	public Attributes getParsedStats() {
		return statParser.getParsedStats();
	}

	private String cleanEnchantTooltip(String line) {
		return line
				.replaceAll("Requires a level (\\d+) or higher item\\.", "")
				.replaceAll("Can only be attached to level (\\d+) and higher items\\.", "")
				.replaceAll("Only usable on items level (\\d+) and above\\.", "")
				.replaceAll("Only useable on items level (\\d+) and above\\.", "")
				.replace("Does not stack with other enchantments for the selected equipment slot.", "")
				.trim();
	}
}
