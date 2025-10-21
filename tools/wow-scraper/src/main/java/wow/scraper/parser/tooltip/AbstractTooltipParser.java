package wow.scraper.parser.tooltip;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wow.commons.model.Money;
import wow.commons.model.Percent;
import wow.commons.model.categorization.Binding;
import wow.commons.model.categorization.ItemSubType;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.RaceId;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.profession.ProfessionId;
import wow.commons.model.profession.ProfessionSpecializationId;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.pve.Side;
import wow.commons.util.parser.ParsedMultipleValues;
import wow.commons.util.parser.ParserUtil;
import wow.commons.util.parser.Rule;
import wow.scraper.config.ScraperContext;
import wow.scraper.config.ScraperContextSource;
import wow.scraper.model.JsonCommonDetails;
import wow.scraper.model.JsonItemDetails;
import wow.scraper.model.JsonSpellDetails;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-10-31
 */
@Getter
@Slf4j
public abstract class AbstractTooltipParser<D extends JsonCommonDetails> implements ScraperContextSource {
	protected final ScraperContext scraperContext;

	protected final D details;
	protected final GameVersionId gameVersion;
	protected List<String> lines;
	protected int currentLineIdx;

	protected String name;
	protected ItemType itemType;
	protected ItemSubType itemSubType;
	protected Binding binding;
	protected boolean unique;
	protected Integer itemLevel;
	protected PhaseId phase;
	protected Integer requiredLevel;
	protected List<CharacterClassId> requiredClass;
	protected List<RaceId> requiredRace;
	protected Side requiredSide;
	protected ProfessionId requiredProfession;
	protected Integer requiredProfessionLevel;
	protected ProfessionSpecializationId requiredProfessionSpec;
	protected String requiredFactionName;
	protected String requiredFactionStanding;
	protected String exclusiveFaction;
	protected String droppedBy;
	protected Percent dropChance;
	protected Money sellPrice;

	private static final Set<String> UNMATCHED_LINES = new TreeSet<>();

	protected final Rule rulePhase = Rule.
			regex("(SoM |SoD )?Phase (.*)", x -> this.phase = parsePhase(x.get(1)));
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
			prefix("Classes: ", x -> this.requiredClass = ParserUtil.getValues(x, CharacterClassId::parse));
	protected final Rule ruleRaceRestriction = Rule.
			prefix("Races: ", x -> this.requiredRace = ParserUtil.getValues(x, RaceId::parse));
	protected final Rule ruleAllianceRestriction = Rule.
			exact("Requires any Alliance race", () -> this.requiredSide = Side.ALLIANCE);
	protected final Rule ruleHordeRestriction = Rule.
			exact("Requires any Horde race", () -> this.requiredSide = Side.HORDE);
	protected final Rule ruleFactionRestriction = Rule.
			regex("Requires (.*?) - (Neutral|Friendly|Honored|Revered|Exalted)", this::parseReputation);
	protected final Rule ruleProfessionRestriction = Rule.
			regex("Requires (" + regexAny(ProfessionId.values()) + ") \\((\\d+)\\)", this::parseRequiredProfession);
	protected final Rule ruleProfessionSpecializationRestriction = Rule.
			regex("Requires (" + regexAny(ProfessionSpecializationId.values()) + ")", this::parseRequiredProfessionSpec);
	protected final Rule ruleDurability = Rule.
			matches("Durability \\d+ / \\d+", x -> {});
	protected final Rule ruleDroppedBy = Rule.
			prefix("Dropped by: ", x -> this.droppedBy = x);
	protected final Rule ruleDropChance = Rule.
			prefix("Drop Chance: ", x -> this.dropChance = parseDropChance(x));
	protected final Rule ruleSellPrice = Rule.
			prefix("Sell Price: ", x -> this.sellPrice = parseSellPrice(x));
	protected final Rule ruleQuote = Rule
			.matches("\".*\"", params -> {});
	protected final Rule ruleRightClickToRead = Rule
			.exact("<Right Click to Read>", () -> {});
	protected final Rule ruleIgnoreEverything = Rule.matches(".*", x -> {});

	protected AbstractTooltipParser(D details, GameVersionId gameVersion, ScraperContext scraperContext) {
		Objects.requireNonNull(details);
		Objects.requireNonNull(gameVersion);
		this.details = details;
		this.gameVersion = gameVersion;
		this.scraperContext = scraperContext;
	}

	public String getIcon() {
		return details.getIcon();
	}

	public String getTooltip() {
		return details.getHtmlTooltip();
	}

	public final void parse() {
		this.lines = cleanTooltip(details.getHtmlTooltip());

		beforeParse();

		this.name = lines.getFirst();

		for (currentLineIdx = 1; currentLineIdx < lines.size(); ++currentLineIdx) {
			String currentLine = lines.get(currentLineIdx);
			parseLine(prepareLine(currentLine));
		}

		fixCommonFields();
		afterParse();
	}

	protected String prepareLine(String line) {
		return line;
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

	private void fixCommonFields() {
		if (binding == null) {
			binding = Binding.NO_BINDING;
		}
	}

	protected PhaseId parsePhase(String value) {
		return PhaseId.parse(gameVersion + "_P" + value.replace('.', '_'));
	}

	protected int parseItemLevel(String value) {
		return Integer.parseInt(value);
	}

	protected int parseRequiredLevel(String value) {
		return Integer.parseInt(value);
	}

	private void parseReputation(ParsedMultipleValues factionParams) {
		this.requiredFactionName = factionParams.get(0);
		this.requiredFactionStanding = factionParams.get(1);
		this.exclusiveFaction = tryParseExclusiveFaction(requiredFactionName);
	}

	private String tryParseExclusiveFaction(String name) {
		var exclusiveFactions = List.of(
				"The Scryers",
				"The Aldor",
				"The Oracles",
				"Frenzyheart Tribe"
		);

		return exclusiveFactions.contains(name) ? name : null;
	}

	private void parseRequiredProfession(ParsedMultipleValues params) {
		this.requiredProfession = ProfessionId.parse(params.get(0));
		this.requiredProfessionLevel = params.getInteger(1);
	}

	private void parseRequiredProfessionSpec(ParsedMultipleValues params) {
		this.requiredProfessionSpec = ProfessionSpecializationId.parse(params.get(0));
	}

	private Percent parseDropChance(String value) {
		return Percent.parse(value);
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

				.replaceAll("<dfn title=\".*?\">(.*)</dfn>", "$1")

				.replaceAll("<span class='q2'>S03 - Tuning and Overrides Passive - (.+?)</span>: ", "")
				.replace("<!--ppl10138:40:49:2:200:1-->", "")

				.replaceAll("\\[<span class='q9'>\\[Max\\((\\d+), \\d+\\)]</span> / \\d+]", "$1")

				.replaceAll("\\[(\\d+) \\* \\(1\\)]", "$1")
				.replaceAll("\\[(\\d+) \\* (\\d+) \\* \\(1\\)]", "($1 * $2)")

				.replaceAll("\\[<span class='q9'>Regeneration, or Mass Regeneration, and a \\d+% chance to avoid interruption caused by damage while casting Arcane Blast or Spellfrost Bolt</span>]", "")
				.replaceAll("\\[<span class='q9'>Increases all damage you deal by \\d+%</span> / (Increases the damage you deal with physical attacks in all forms by \\d+%.)]", "$1")
				.replace("[<span class='q9'>and non-instant spell casts</span>]", "")
				.replaceAll("\\[<span class='q9'>\\d+%</span> / (\\d+%)]", "$1")
				.replaceAll("\\[<span class='q9'>Instantly heals a target with an active Rejuvenation or Regrowth effect for</span> / (Consumes a Rejuvenation or Regrowth effect on a friendly target to instantly heal them)]", "$1")
				.replaceAll("\\[<span class='q9'>Increases healing from your Riptide and Earth Shield by an additional \\d+%</span>]", "")
				.replaceAll("\\[<span class='q9'>Activating Holy Shield also grants you \\d+ Spell Power for each point of your defense skill beyond \\(\\d+ \\* \\d+\\)\\. Lasts 15 sec</span>]", "")
				.replaceAll("\\[<span class='q9'>reduce the target's Attack Power by \\(.*?\\), and increase the Paladin's Attack Power by \\d+% for 30 sec</span> / 10 sec.]", "for 10 sec.")
				.replaceAll("\\[<span class='q9'>Also Reduces all Physical and Holy threat you generate by \\d+% while Righteous Fury is not active</span>]", "")
				.replace("[<span class='q9'>Moonfire costs 50% less mana and deals 100% more damage over time, your periodic damage spells can deal critical periodic damage, you gain (60 * 3) spell damage</span>]", "")
				.replaceAll("\\[<span class='q9'>The Moonkin cannot cast healing spells while shapeshifted</span> / (The Moonkin can only cast Balance spells while shapeshifted.)]", "$1")
				.replace("[<span class='q9'>Reduces all threat from Arcane and Nature spells by 0% while in this form</span>]", "")
				.replaceAll("</div> \\(\\d+m?s cooldown\\)</td></tr></table>$", "")
				.replaceAll("</div> \\(Proc chance: \\d+%\\)</td></tr></table>$", "")

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
				.replaceAll(" +", " ")
				.replace(" .", ".")
		;

		if (tooltip.matches("&[a-z]+;")) {
			throw new IllegalArgumentException("Still something left");
		}

		return Stream.of(tooltip.split("\n"))
				.map(String::trim)
				.filter(x -> !x.isBlank())
				.toList();
	}

	public void setRequiredLevel(Integer requiredLevel) {
		if (this.requiredLevel != null && this.requiredLevel != (int)requiredLevel) {
			throw new IllegalArgumentException();
		}
		this.requiredLevel = requiredLevel;
	}

	public void setRequiredSide(Side requiredSide) {
		if (this.requiredSide != null && this.requiredSide != requiredSide) {
			throw new IllegalArgumentException();
		}
		this.requiredSide = requiredSide;
	}

	public TimeRestriction getTimeRestriction() {
		return TimeRestriction.of(
				gameVersion, getActualPhase()
		);
	}

	private PhaseId getActualPhase() {
		PhaseId phaseOverride = getPhaseOverride();

		if (phaseOverride != null && phaseOverride.getGameVersionId() == gameVersion) {
			return phaseOverride;
		}

		if (appearedInPreviousVersion()) {
			return gameVersion.getPrepatchPhase().orElseThrow();
		}

		return phase;
	}

	private boolean appearedInPreviousVersion() {
		if (details instanceof JsonItemDetails itemDetails) {
			return getItemDetailRepository().appearedInPreviousVersion(details.getId(), gameVersion, itemDetails.getCategory());
		}
		if (details instanceof JsonSpellDetails spellDetails) {
			return getSpellDetailRepository().appearedInPreviousVersion(details.getId(), gameVersion, spellDetails.getCategory());
		}
		throw new IllegalArgumentException();
	}

	protected abstract PhaseId getPhaseOverride();

	protected void unmatchedLine(String line) {
		if (getScraperConfig().isErrorOnUnmatchedLine()) {
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

	public static void reportUnmatchedLines() {
		UNMATCHED_LINES.forEach(log::info);
	}

	protected static <T> String regexAny(T[] values) {
		return Stream.of(values)
				.map(Object::toString)
				.map(Pattern::quote)
				.collect(Collectors.joining("|"));
	}
}
