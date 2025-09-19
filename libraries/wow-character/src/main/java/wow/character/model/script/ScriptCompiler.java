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
		var matcher = CONDITIONAL_ABILITY_PATTERN.matcher(line);

		if (matcher.find()) {
			var conditionStr = matcher.group(1);
			var abilityIdStr = matcher.group(2);

			return getComposableCommand(abilityIdStr, conditionStr);
		} else {
			return getComposableCommand(line, null);
		}
	}

	private static final Pattern CONDITIONAL_ABILITY_PATTERN = Pattern.compile("\\[(.+?)]\\s*(.+)");

	private ComposableCommand getComposableCommand(String abilityIdStr, String conditionStr) {
		var conditionParser = new ScriptCommandConditionParser(conditionStr);

		conditionParser.parse();

		var conditions = conditionParser.getConditions();
		var target = conditionParser.getTarget();

		var itemSlot = tryParseItemSlot(abilityIdStr);

		if (itemSlot != null) {
			return new UseItem(
					conditions,
					itemSlot,
					target
			);
		}

		var abilityId = AbilityId.parse(abilityIdStr);

		return new CastSpell(
				conditions,
				abilityId,
				target
		);
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
