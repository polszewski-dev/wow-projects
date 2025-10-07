package wow.character.model.script;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.spell.AbilityId;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;
import static wow.character.model.script.ScriptCommand.*;
import static wow.character.model.script.ScriptSectionType.ROTATION;

/**
 * User: POlszewski
 * Date: 2025-09-17
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ScriptCompiler {
	public static final String COMMENT_INDICATOR = "#";
	public static final String SECTION_INDICATOR = "@";
	public static final String CAST_SEQUENCE_INDICATOR = ">>=";

	private final String path;
	private ScriptSectionType currentSectionType = ROTATION;
	private final Map<ScriptSectionType, List<ScriptCommand>> sectionCommands = new EnumMap<>(ScriptSectionType.class);

	public static Script compileResource(String path) {
		var compiler = new ScriptCompiler(path);

		return compiler.compile();
	}

	private Script compile() {
		for (var line : getLines()) {
			line = removeSingleLineComment(line);

			if (line.isEmpty()) {
				continue;
			}

			if (line.startsWith(SECTION_INDICATOR)) {
				parseSectionType(line);
			} else {
				parseCommand(line);
			}
		}

		return new Script(getSectionsByType());
	}

	private String removeSingleLineComment(String line) {
		return line.replaceAll(COMMENT_INDICATOR + ".*", "").trim();
	}

	private void parseSectionType(String line) {
		var trimmed = line.substring(1).trim();
		var newSectionType = ScriptSectionType.parse(trimmed);

		if (sectionCommands.containsKey(newSectionType)) {
			throw new IllegalArgumentException("Section %s already exists".formatted(newSectionType));
		}

		this.currentSectionType = newSectionType;
	}

	private void parseCommand(String line) {
		var parts = line.split(CAST_SEQUENCE_INDICATOR);

		var composableCommands = Stream.of(parts)
				.map(String::trim)
				.map(this::parseComposableCommand)
				.toList();

		var command = compose(composableCommands);

		sectionCommands
				.computeIfAbsent(currentSectionType, x -> new ArrayList<>())
				.add(command);
	}

	private ComposableCommand parseComposableCommand(String line) {
		var matcher = COMMAND_PATTERN.matcher(line);

		if (!matcher.find()) {
			throw new IllegalArgumentException("Incorrect command syntax: " + line);
		}

		var conditionStr = matcher.group(2);
		var abilityIdStr = matcher.group(3);
		var rankStr = matcher.group(5);
		var targetStr = matcher.group(7);

		return getComposableCommand(conditionStr, abilityIdStr, rankStr, targetStr);
	}

	private static final Pattern COMMAND_PATTERN = Pattern.compile("^(\\[(.+)]\\s*)?(.+?)(\\s*\\(Rank\\s+(\\d+)\\))?(\\s*@\\s*(\\S+))?$");

	private ComposableCommand getComposableCommand(String conditionStr, String abilityIdStr, String rankStr, String targetStr) {
		var conditionParser = new ScriptCommandConditionParser(conditionStr);

		var condition = conditionParser.parse();

		var target = parseTarget(targetStr);
		var itemSlot = tryParseItemSlot(abilityIdStr);

		if (itemSlot != null) {
			if (rankStr != null) {
				throw new IllegalArgumentException("Item slot %s can't have rank".formatted(abilityIdStr));
			}

			return new UseItem(
					condition,
					itemSlot,
					target
			);
		}

		var abilityId = AbilityId.parse(abilityIdStr);

		if (rankStr != null) {
			var rank = Integer.parseInt(rankStr);

			return new CastSpellRank(
					condition,
					abilityId.name(),
					rank,
					target
			);
		}

		return new CastSpell(
				condition,
				abilityId,
				target
		);
	}

	private ScriptCommandTarget parseTarget(String targetStr) {
		if (targetStr == null) {
			return ScriptCommandTarget.DEFAULT;
		}

		return Stream.of(ScriptCommandTarget.values())
				.filter(x -> x.name().equalsIgnoreCase(targetStr))
				.findAny()
				.orElseThrow(() -> new IllegalArgumentException("Invalid target: " + targetStr));
	}

	private ItemSlot tryParseItemSlot(String line) {
		return ItemSlot.getDpsSlots().stream()
				.filter(x -> x.name().replace("_", "").equalsIgnoreCase(line))
				.findAny()
				.orElse(null);
	}

	@SneakyThrows
	private List<String> getLines() {
		var inputStream = getClass().getResourceAsStream(path);

		Objects.requireNonNull(inputStream, "No script in " + path);

		try (var reader = new BufferedReader(new InputStreamReader(inputStream))) {
			return reader.lines().toList();
		}
	}

	private Map<ScriptSectionType, ScriptSection> getSectionsByType() {
		return sectionCommands.entrySet().stream()
				.collect(
						toMap(
								Map.Entry::getKey,
								x -> new ScriptSection(x.getValue())
						)
				);
	}
}
