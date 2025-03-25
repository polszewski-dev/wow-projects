package wow.scraper.parser.tooltip;

import lombok.Getter;
import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.config.CharacterRestriction;
import wow.commons.model.config.Description;
import wow.commons.model.config.TalentRestriction;
import wow.commons.model.spell.*;
import wow.commons.model.talent.TalentId;
import wow.commons.util.parser.Rule;
import wow.scraper.config.ScraperContext;
import wow.scraper.model.JsonSpellDetails;
import wow.scraper.parser.spell.ability.AbilityMatcher;
import wow.scraper.parser.spell.ability.AbilityParser;

import java.util.List;

import static wow.commons.constant.SpellConstants.UNLIMITED_RANGE;

/**
 * User: POlszewski
 * Date: 2023-08-24
 */
@Getter
public class AbilityTooltipParser extends AbstractSpellTooltipParser {
	private Duration cooldown;

	private Integer manaCost;
	private Integer baseManaCost;
	private Integer healthCost;
	private Integer baseHealthCost;
	private Integer healthChannelCost;
	private Reagent reagent;

	private Duration castTime;
	private Duration channelTime;

	private Integer range;
	private boolean unlimitedRange;

	private boolean summon;
	private boolean conjured;

	private AbilityMatcher abilityMatcher;

	public AbilityTooltipParser(JsonSpellDetails details, ScraperContext scraperContext) {
		super(details, scraperContext);
	}

	@Override
	protected Rule[] getRules() {
		return new Rule[] {
				rulePhase,
				ruleRank,
				Rule.regex("Level (\\d+)", x -> {}),
				Rule.regex("Requires level (\\d+)", x -> this.requiredLevel = x.getInteger(0)),

				Rule.regex("\\((\\d+\\.?\\d*)s cooldown\\)", x -> this.cooldown = Duration.seconds(x.getDouble(0))),
				Rule.regex("(\\d+\\.?\\d*) sec cooldown", x -> this.cooldown = Duration.seconds(x.getDouble(0))),
				Rule.regex("(\\d+) sec cooldown", x -> this.cooldown = Duration.seconds(x.getInteger(0))),
				Rule.regex("(\\d+) min cooldown", x -> this.cooldown = Duration.minutes(x.getInteger(0))),
				Rule.regex("(\\d+) hour cooldown", x -> this.cooldown = Duration.hours(x.getInteger(0))),

				Rule.regex("(\\d+) Mana", x -> this.manaCost = x.getInteger(0)),
				Rule.regex("(\\d+)% of base mana", x -> this.baseManaCost = x.getInteger(0)),
				Rule.regex("(\\d+)% of base health", x -> this.baseHealthCost = x.getInteger(0)),
				Rule.regex("(\\d+) Health, plus (\\d+) per sec", x -> {
					this.healthCost = x.getInteger(0);
					this.healthChannelCost = x.getInteger(1);
				}),

				Rule.regex("(\\d+\\.?\\d*) sec cast", x -> this.castTime = Duration.seconds(x.getDouble(0))),
				Rule.regex("Channeled \\((\\d+) sec cast\\)", x -> this.channelTime = Duration.seconds(x.getDouble(0))),
				Rule.regex("Channeled \\((\\d+) min cast\\)", x -> this.channelTime = Duration.minutes(x.getInteger(0))),
				Rule.regex("Instant( cast)?", x -> this.castTime = Duration.ZERO),

				Rule.regex("(\\d+) yd range", x -> this.range = x.getInteger(0)),
				Rule.exact("Unlimited range", () -> this.unlimitedRange = true),

				ruleReqCLass,

				Rule.exact("Summon", () -> this.summon = true),

				Rule.exact("Reagents:", () -> {}),
				Rule.regex("(" + regexAny(Reagent.values()) + ")", x -> this.reagent = Reagent.parse(x.get(0))),

				Rule.exact("Conjured items disappear if logged out for more than 15 minutes.", () -> this.conjured = true),

				ruleTalent,

				ruleDescription,
		};
	}

	@Override
	protected void beforeParse() {
		// void
	}

	@Override
	protected void afterParse() {
		if (rank == null) {
			rank = getScraperDatafixes().getRankOverrides().get(getSpellId());
			if (rank == null) {
				rank = 0;
			}
		}

		if (requiredLevel == null) {
			requiredLevel = 1;
		}

		parseSpellDetails();
	}

	private void parseSpellDetails() {
		AbilityId abilityId = AbilityId.parse(getName());
		AbilityParser abilityParser = getSpellPatternRepository().getAbilityParser(abilityId, gameVersion);

		if (abilityParser.tryParse(description)) {
			this.abilityMatcher = abilityParser.getSuccessfulMatcher().orElseThrow();
		}
	}

	@Override
	public String getName() {
		return details.getName();
	}

	public Cost getCost() {
		int amount = 0;
		ResourceType type = null;
		Percent baseStatPct = Percent.ZERO;

		if (manaCost != null) {
			amount = manaCost;
			type = ResourceType.MANA;
		}

		if (baseManaCost != null) {
			baseStatPct = Percent.of(baseManaCost);
			type = ResourceType.MANA;
		}

		if (healthCost != null) {
			if (type == ResourceType.MANA) {
				throw new IllegalArgumentException("Using both health and mana");
			}
			amount = healthCost;
			type = ResourceType.HEALTH;
		}

		if (baseHealthCost != null) {
			if (type == ResourceType.MANA) {
				throw new IllegalArgumentException("Using both health and mana");
			}
			baseStatPct = Percent.of(baseHealthCost);
			type = ResourceType.HEALTH;
		}

		if (amount == 0 && type == null && baseStatPct.isZero()) {
			return null;
		}

		return new Cost(type, amount, baseStatPct, Coefficient.NONE, reagent);
	}

	public CastInfo getCastInfo() {
		if (this.castTime != null) {
			if (channelTime != null) {
				throw new IllegalArgumentException("Both cast and channel time are present");
			}
			return new CastInfo(castTime, false, false);
		} else if (this.channelTime != null) {
			return new CastInfo(channelTime, true, false);
		} else {
			throw new IllegalArgumentException("No cast time: " + getName());
		}
	}

	public Duration getCooldown() {
		return cooldown != null ? cooldown : Duration.ZERO;
	}

	public int getRange() {
		if (range != null && unlimitedRange) {
			throw new IllegalArgumentException("Both range types specified " + name);
		}

		if (range != null) {
			return range;
		} else if (unlimitedRange) {
			return UNLIMITED_RANGE;
		} else {
			return 0;
		}
	}

	public AbilityCategory getAbilityCategory() {
		return abilityMatcher != null ? abilityMatcher.getAbilityCategory() : null;
	}

	public CharacterRestriction getCharacterRestriction() {
		return new CharacterRestriction(
				getRequiredLevel(),
				requiredClass,
				List.of(),
				requiredSide,
				null,
				null,
				null,
				List.of(),
				null,
				talent ? TalentRestriction.of(TalentId.parse(getName())) : null,
				null,
				null
		);
	}

	public Description getSpellDescription() {
		return new Description(getName(), getIcon(), getDescription());
	}
}
