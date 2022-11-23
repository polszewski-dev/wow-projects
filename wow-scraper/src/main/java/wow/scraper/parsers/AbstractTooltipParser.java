package wow.scraper.parsers;

import lombok.Getter;
import org.slf4j.Logger;
import wow.commons.model.Money;
import wow.commons.model.Percent;
import wow.commons.model.categorization.Binding;
import wow.commons.model.professions.Profession;
import wow.commons.model.professions.ProfessionSpecialization;
import wow.commons.model.pve.GameVersion;
import wow.commons.model.pve.Phase;
import wow.commons.model.pve.Side;
import wow.commons.model.unit.CharacterClass;
import wow.commons.model.unit.Race;
import wow.commons.util.parser.ParserUtil;
import wow.commons.util.parser.Rule;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-10-31
 */
@Getter
public abstract class AbstractTooltipParser {
	protected final GameVersion gameVersion;
	protected final String htmlTooltip;
	protected List<String> lines;
	protected int currentLineIdx;

	protected final Integer itemId;
	protected String name;
	protected Phase phase;
	protected Integer requiredLevel;
	protected Integer itemLevel;
	protected Binding binding;
	protected boolean unique;
	protected List<CharacterClass> classRestriction;
	protected List<Race> raceRestriction;
	protected Side sideRestriction;
	protected String requiredFactionName;
	protected String requiredFactionStanding;
	protected Profession requiredProfession;
	protected Integer requiredProfessionLevel;
	protected ProfessionSpecialization requiredProfessionSpec;
	protected String droppedBy;
	protected Percent dropChance;
	protected Money sellPrice;

	private static final boolean ERROR_ON_UNMATCHED_LINE = true;
	private static final Set<String> UNMATCHED_LINES = new TreeSet<>();

	protected final Rule rulePhase = Rule.
			prefix("Phase ", x -> this.phase = parsePhase(x));
	protected final Rule ruleRequiresLevel = Rule.
			prefix("Requires Level ", x -> this.requiredLevel = parseRequiredLevel(x));
	protected final Rule ruleItemLevel = Rule.
			prefix("Item Level ", x -> this.itemLevel = parseItemLevel(x));
	protected final Rule ruleBindsWhenPickedUp = Rule.
			exact("Binds when picked up", () -> this.binding = Binding.BINDS_ON_PICK_UP);
	protected final Rule ruleBindsWhenEquipped = Rule.
			exact("Binds when equipped", () -> this.binding = Binding.BINDS_ON_EQUIP);
	protected final Rule ruleBindsWhenUsed = Rule.
			exact("Binds when used", () -> this.binding = Binding.BINDS_ON_EQUIP);
	protected final Rule ruleUnique = Rule.
			exact("Unique", () -> this.unique = true);
	protected final Rule ruleUniqueEquipped = Rule.
			exact("Unique-Equipped", () -> this.unique = true);
	protected final Rule ruleClassRestriction = Rule.
			prefix("Classes: ", x -> this.classRestriction = ParserUtil.getValues(x, CharacterClass::parse));
	protected final Rule ruleRaceRestriction = Rule.
			prefix("Races: ", x -> this.raceRestriction = ParserUtil.getValues(x, Race::parse));
	protected final Rule ruleAllianceRestriction = Rule.
			exact("Requires any Alliance race", () -> this.sideRestriction = Side.ALLIANCE);
	protected final Rule ruleHordeRestriction = Rule.
			exact("Requires any Horde race", () -> this.sideRestriction = Side.HORDE);
	protected final Rule ruleFactionRestriction = Rule.
			regex("Requires (.*?) - (Neutral|Friendly|Honored|Revered|Exalted)", this::parseReputation);
	protected final Rule ruleProfessionRestriction = Rule.
			regex("Requires (Alchemy|Enchanting|Jewelcrafting|Tailoring|Leatherworking|Blacksmithing|Engineering) \\((\\d+)\\)", this::parseRequiredProfession);
	protected final Rule ruleProfessionSpecializationRestriction = Rule.
			regex("Requires (Gnomish Engineer|Goblin Engineer|Master Swordsmith|Mooncloth Tailoring|Shadoweave Tailoring|Spellfire Tailoring)", this::parseRequiredProfessionSpec);
	protected final Rule ruleDurability = Rule.
			matches("Durability \\d+ / \\d+", x -> {});
	protected final Rule ruleDroppedBy = Rule.
			prefix("Dropped by: ", x -> this.droppedBy = x);
	protected final Rule ruleDropChance = Rule.
			prefix("Drop Chance: ", x -> this.dropChance = parseDropChance(x));
	protected final Rule ruleSellPrice = Rule.
			prefix("Sell Price: ", x -> this.sellPrice = parseSellPrice(x));
	protected final Rule quote = Rule
			.matches("\".*\"", params -> {});

	protected AbstractTooltipParser(Integer itemId, String htmlTooltip, GameVersion gameVersion) {
		this.gameVersion = gameVersion;
		this.itemId = itemId;
		this.htmlTooltip = htmlTooltip;
	}

	public final void parse() {
		this.lines = cleanTooltip(htmlTooltip);

		beforeParse();

		this.name = lines.get(0);

		for (currentLineIdx = 1; currentLineIdx < lines.size(); ++currentLineIdx) {
			String currentLine = lines.get(currentLineIdx);
			parseLine(currentLine);
		}

		afterParse();
	}

	private void parseLine(String currentLine) {
		for (Rule rule : getRules()) {
			if (rule.matchAndTakeAction(currentLine)) {
				return;
			}
		}
		unmatchedLine(currentLine);
	}

	protected abstract Rule[] getRules();

	protected abstract void beforeParse();
	protected abstract void afterParse();

	protected Phase parsePhase(String value) {
		return Phase.parse(gameVersion + "_P" + value);
	}

	protected int parseItemLevel(String value) {
		return Integer.parseInt(value);
	}

	protected int parseRequiredLevel(String value) {
		return Integer.parseInt(value);
	}

	private void parseReputation(Object[] factionParams) {
		this.requiredFactionName = (String)factionParams[0];
		this.requiredFactionStanding = (String)factionParams[1];
	}

	private void parseRequiredProfession(Object[] params) {
		this.requiredProfession = Profession.parse((String)params[0]);
		this.requiredProfessionLevel = (Integer)params[1];
	}

	private void parseRequiredProfessionSpec(Object[] params) {
		this.requiredProfessionSpec = ProfessionSpecialization.parse((String)params[0]);
	}

	private Percent parseDropChance(String value) {
		return Percent.of(Double.parseDouble(value.replace("%", "")));
	}

	protected Money parseSellPrice(String value) {
		return Money.parse(value.replace(" ", ""));
	}

	protected static List<String> cleanTooltip(String tooltip) {
		tooltip = tooltip
				.replaceAll("<!--.*?-->", "")
				.replaceAll("<a[^>]*?>", "")
				.replace("</a>", "")
				.replaceAll("<div[^>]*?>", "\n")
				.replace("<br>", "\n")
				.replace("<br />", "\n")
				.replace("<td>", "\n")

				.replaceAll("<span class=\"moneygold\">(\\d+)</span>", "$1g")
				.replaceAll("<span class=\"moneysilver\">(\\d+)</span>", "$1s")
				.replaceAll("<span class=\"moneycopper\">(\\d+)</span>", "$1c")

				.replace("<dfn title=\"her\">his</dfn>", "his")

				.replaceAll("</[^>]+?>", "\n")
				.replaceAll("<[^>]+?>", "")
		;

		if (tooltip.contains("<") || tooltip.contains(">")) {
			throw new IllegalArgumentException("Still something left");
		}

		tooltip = tooltip
				.replace("&nbsp;", " ")
				.replace("&lt;", "<")
				.replace("&gt;", ">")
				.replace("&quot;", "\"")
		;

		if (tooltip.matches("&[a-z]+;")) {
			throw new IllegalArgumentException("Still something left");
		}

		return Stream.of(tooltip.split("\n"))
				.map(String::trim)
				.filter(x -> !x.isBlank())
				.collect(Collectors.toList());
	}

	protected void unmatchedLine(String line) {
		if (ERROR_ON_UNMATCHED_LINE) {
			throw new IllegalArgumentException(line);
		}

		line = line
				.replace("(", "\\(")
				.replace(")", "\\)")
				.replace("+", "\\+")
				.replaceAll("(\\d+.\\d+)", "\\(\\\\d+\\.\\\\d+\\)")
				.replaceAll("(\\d+)", "\\(\\\\d+\\)")
				.replace("[", "\\[")
				.replace("]", "\\]")
		;

		UNMATCHED_LINES.add(line);
	}

	public static void reportUnmatchedLines(Logger log) {
		UNMATCHED_LINES.forEach(log::info);
	}
}
