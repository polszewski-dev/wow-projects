package wow.scraper.parser.spell.activated;

import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.scraper.parser.spell.SpellMatcherParams;
import wow.scraper.parser.stat.CooldownParser;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: POlszewski
 * Date: 2023-09-03
 */
public class ActivatedAbilityMatcherParams extends SpellMatcherParams {
	private Duration parsedCooldown;
	private Percent parsedProcChance;
	private Duration parsedProcCooldown;

	public ActivatedAbilityMatcherParams(String line) {
		super(line);

		removeNoise();
		parseProcAndCooldown();
		removeNoise();
	}

	public Optional<Duration> getParsedCooldown() {
		return Optional.ofNullable(parsedCooldown);
	}

	public Optional<Percent> getParsedProcChance() {
		return Optional.ofNullable(parsedProcChance);
	}

	public Optional<Duration> getParsedProcCooldown() {
		return Optional.ofNullable(parsedProcCooldown);
	}

	@Override
	protected void removeNoise() {
		this.line = line.replace("(750ms cooldown)", "");
		super.removeNoise();
	}

	private void parseProcAndCooldown() {
		parseProcChanceAndCooldown();
		parseProcChance();
		parseCooldown();
	}

	private void parseProcChance() {
		parse("\\(Proc chance: (\\d+)%\\)", matcher -> this.parsedProcChance = Percent.parse(matcher.group(1)));
	}

	private void parseProcChanceAndCooldown() {
		parse("\\(Proc chance: (\\d+)%, (.+?) cooldown\\)", matcher -> {
			this.parsedProcChance = Percent.parse(matcher.group(1));
			this.parsedProcCooldown = CooldownParser.parseCooldown(matcher.group(2));
		});
	}

	private void parseCooldown() {
		parse(" \\((.+?) Cooldown\\)", matcher -> this.parsedCooldown = CooldownParser.parseCooldown(matcher.group(1)));
	}

	private void parse(String pattern, Consumer<Matcher> onMatch) {
		Matcher matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(line);

		if (!matcher.find()) {
			return;
		}

		onMatch.accept(matcher);

		line = line.replace(matcher.group(), "").trim();
	}
}
