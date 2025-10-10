package wow.character.model.script;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.spell.AbilityId;
import wow.commons.util.parser.ParsedMultipleValues;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;
import static wow.character.model.script.ScriptCommand.*;
import static wow.character.model.script.ScriptSectionType.ROTATION;
import static wow.commons.util.parser.ParserUtil.parseMultipleValues;
import static wow.commons.util.parser.ParserUtil.removePrefix;

/**
 * User: POlszewski
 * Date: 2025-09-17
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ScriptCompiler {
	public static final String COMMENT_INDICATOR = "#";
	public static final String SECTION_INDICATOR = "@";
	public static final String CAST_SEQUENCE_INDICATOR = ">>=";
	public static final String OPTIONALITY_INDICATOR = "?";
	public static final String LINE_CONTINUATION_INDICATOR = "\\";

	private static final String LET_DIRECTIVE_REGEX = "@\\s*let\\s+(\\w+)\\s*=(.*)";
	private static final Pattern COMMAND_PATTERN = Pattern.compile("^(\\[(.+)]\\s*)?(.+?)(\\s*\\(Rank\\s+(\\d+)\\))?(\\s*@\\s*(\\S+))?$");

	private final String path;
	private ScriptSectionType currentSectionType = ROTATION;
	private final Map<ScriptSectionType, List<ScriptCommand>> sectionCommands = new EnumMap<>(ScriptSectionType.class);

	private final Map<String, String> macros = new HashMap<>();

	public static Script compileResource(String path) {
		var compiler = new ScriptCompiler(path);

		return compiler.compile();
	}

	private Script compile() {
		for (var line : getLines()) {
			if (line.startsWith(SECTION_INDICATOR)) {
				parseDirective(line);
			} else {
				parseCommand(line);
			}
		}

		return new Script(getSectionsByType());
	}

	private void parseDirective(String line) {
		var result = parseMultipleValues(LET_DIRECTIVE_REGEX, line);

		if (!result.isEmpty()) {
			parseLet(result);
		} else {
			parseSectionType(line);
		}
	}

	private void parseSectionType(String line) {
		var trimmed = line.substring(1).trim();
		var newSectionType = ScriptSectionType.parse(trimmed);

		if (sectionCommands.containsKey(newSectionType)) {
			throw new IllegalArgumentException("Section %s already exists".formatted(newSectionType));
		}

		this.currentSectionType = newSectionType;
	}

	private void parseLet(ParsedMultipleValues parsed) {
		var name = parsed.get(0).trim();
		var definition = parsed.get(1).trim();

		macros.put(name, definition);
	}

	private void parseCommand(String line) {
		var parts = line.split(CAST_SEQUENCE_INDICATOR);

		var composableCommands = Stream.of(parts)
				.map(String::trim)
				.map(this::parseComposableCommand)
				.toList();

		var command = compose(composableCommands);

		requireCorrectOptionality(command);

		sectionCommands
				.computeIfAbsent(currentSectionType, x -> new ArrayList<>())
				.add(command);
	}

	private ComposableCommand parseComposableCommand(String line) {
		var optional = false;

		var withoutPrefix = removePrefix(OPTIONALITY_INDICATOR, line);

		if (withoutPrefix != null) {
			optional = true;
			line = withoutPrefix.trim();
		}

		var matcher = COMMAND_PATTERN.matcher(line);

		if (!matcher.find()) {
			throw new IllegalArgumentException("Incorrect command syntax: " + line);
		}

		var conditionStr = matcher.group(2);
		var abilityIdStr = matcher.group(3);
		var rankStr = matcher.group(5);
		var targetStr = matcher.group(7);

		return getComposableCommand(conditionStr, abilityIdStr, rankStr, targetStr, optional);
	}

	private ComposableCommand getComposableCommand(String conditionStr, String abilityIdStr, String rankStr, String targetStr, boolean optional) {
		var condition = parseCondition(conditionStr);
		var target = parseTarget(targetStr);
		var itemSlot = tryParseItemSlot(abilityIdStr);

		if (itemSlot != null) {
			if (rankStr != null) {
				throw new IllegalArgumentException("Item slot %s can't have rank".formatted(abilityIdStr));
			}

			return new UseItem(
					condition,
					itemSlot,
					target,
					optional
			);
		}

		var abilityId = AbilityId.parse(abilityIdStr);

		if (rankStr != null) {
			var rank = Integer.parseInt(rankStr);

			return new CastSpellRank(
					condition,
					abilityId.name(),
					rank,
					target,
					optional
			);
		}

		return new CastSpell(
				condition,
				abilityId,
				target,
				optional
		);
	}

	private ScriptCommandCondition parseCondition(String conditionStr) {
		var conditionParser = new ScriptCommandConditionParser(conditionStr, macros);

		return conditionParser.parse();
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

	private void requireCorrectOptionality(ScriptCommand command) {
		switch (command) {
			case CastSequence(var list) -> {
				if (list.getLast().optional()) {
					throw new IllegalArgumentException("Last command in the sequence cannot be optional");
				}
			}
			case ComposableCommand composableCommand -> {
				if (composableCommand.optional()) {
					throw new IllegalArgumentException("Single commands cannot be optional");
				}
			}
		}
	}

	private List<String> getLines() {
		var lines = getUnprocessedLines();
		var result = new ArrayList<String>();

		for (int i = 0; i < lines.size();) {
			var linesToMerge = new ArrayList<Line>();
			var current = lines.get(i++);

			linesToMerge.add(current);

			while (i < lines.size() && shouldMergeTogether(current, lines.get(i))) {
				var next = lines.get(i++);

				linesToMerge.add(next);

				current = next;
			}

			var mergedLine = merge(linesToMerge);

			if (!mergedLine.isEmpty()) {
				result.add(mergedLine);
			}
		}

		return result;
	}

	@SneakyThrows
	private List<Line> getUnprocessedLines() {
		var inputStream = getClass().getResourceAsStream(path);

		Objects.requireNonNull(inputStream, "No script in " + path);

		try (var reader = new BufferedReader(new InputStreamReader(inputStream))) {
			return reader.lines()
					.map(Line::new)
					.toList();
		}
	}

	private record Line(
			String rawLine,
			String cleanedLine
	) {
		Line(String rawLine) {
			this(rawLine, removeSingleLineComment(rawLine));
		}

		public String cleanedWithoutContinuationMark() {
			if (!cleanedLine.endsWith(LINE_CONTINUATION_INDICATOR)) {
				return cleanedLine;
			}

			return cleanedLine.substring(0, cleanedLine.length() - LINE_CONTINUATION_INDICATOR.length()).trim();
		}
	}

	private boolean shouldMergeTogether(Line currentLine, Line nextLine) {
		return currentLine.cleanedLine().endsWith(LINE_CONTINUATION_INDICATOR) ||
				currentLine.cleanedLine().endsWith(CAST_SEQUENCE_INDICATOR) ||
				nextLine.cleanedLine().startsWith(CAST_SEQUENCE_INDICATOR);
	}

	private String merge(ArrayList<Line> linesToMerge) {
		return linesToMerge.stream()
				.map(Line::cleanedWithoutContinuationMark)
				.collect(joining());
	}

	private static String removeSingleLineComment(String line) {
		return line.replaceAll(COMMENT_INDICATOR + ".*", "").trim();
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
