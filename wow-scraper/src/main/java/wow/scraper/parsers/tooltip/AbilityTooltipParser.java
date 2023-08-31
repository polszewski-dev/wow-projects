package wow.scraper.parsers.tooltip;

import lombok.Getter;
import wow.commons.model.Duration;
import wow.commons.model.spells.EffectId;
import wow.commons.model.spells.ResourceType;
import wow.commons.model.spells.SpellId;
import wow.commons.util.parser.Rule;
import wow.scraper.config.ScraperContext;
import wow.scraper.model.JsonSpellDetails;
import wow.scraper.parsers.spell.SpellMatcher;
import wow.scraper.parsers.spell.SpellParser;

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

	private Duration castTime;
	private Duration channelTime;

	private Integer range;
	private boolean unlimitedRange;

	private boolean summon;
	private boolean conjured;
	private String reagent;

	private SpellMatcher spellMatcher;
	private ParsedCosts parsedCosts;
	private ParsedCastTime parsedCastTime;
	private ParsedDirectComponent parsedDirectComponent;
	private ParsedDoTComponent parsedDoTComponent;

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
				Rule.regex("(Soul Shard|Infernal Stone|Demonic Figurine|Light Feather|Holy Candle|Sacred Candle)", x -> this.reagent = x.get(0)),

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
			rank = getScraperConfig().getRankOverrides().get(getSpellId());
			if (rank == null) {
				rank = 0;
			}
		}

		if (requiredLevel == null) {
			requiredLevel = 1;
		}

		parseSpellDetails();

		parsedCosts = new ParsedCosts();
		parsedCosts.parse();

		parsedCastTime = new ParsedCastTime();
		parsedCastTime.parse();

		parsedDirectComponent = new ParsedDirectComponent();
		parsedDirectComponent.parse();

		parsedDoTComponent = new ParsedDoTComponent();
		parsedDoTComponent.parse();
	}

	private void parseSpellDetails() {
		SpellId spellId = SpellId.parse(getName());
		SpellParser spellParser = getSpellPatternRepository().getSpellParser(spellId, gameVersion);

		if (spellParser.tryParse(description)) {
			this.spellMatcher = spellParser.getUniqueSuccessfulMatcher();
		}
	}

	@Override
	public String getName() {
		return details.getName();
	}

	@Getter
	public class ParsedCosts {
		private Integer amount;
		private ResourceType type;
		private Integer baseStatPct;

		private Integer matchedAmount;
		private ResourceType matchedType;

		protected void parse() {
			parseTooltipValues();
			parseSpellPatternValues();
		}

		private void parseTooltipValues() {
			if (manaCost != null) {
				amount = manaCost;
				type = ResourceType.MANA;
			}

			if (baseManaCost != null) {
				baseStatPct = baseManaCost;
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
				baseStatPct = baseHealthCost;
				type = ResourceType.HEALTH;
			}
		}

		private void parseSpellPatternValues() {
			if (spellMatcher == null) {
				return;
			}

			matchedAmount = spellMatcher.getCostAmount().orElse(null);
			matchedType = spellMatcher.getCostType().orElse(null);

			assertMatchedAmountAndTypeIsFilledOrEmpty();

			if (matchedAmount == null) {
				return;
			}

			if (amount == null) {
				amount = matchedAmount;
				type = matchedType;
			} else {
				assertMatchedAmountAndTypeMatchCorrespondingTooltipValues();
			}
		}

		private void assertMatchedAmountAndTypeIsFilledOrEmpty() {
			if ((matchedAmount != null && matchedType == null) || (matchedAmount == null && matchedType != null)) {
				throw new IllegalArgumentException("Both type and amount have to be either null or not null");
			}
		}

		private void assertMatchedAmountAndTypeMatchCorrespondingTooltipValues() {
			if ((int)amount != matchedAmount || type != matchedType) {
				throw new IllegalArgumentException();
			}
		}
	}

	@Getter
	public class ParsedCastTime {
		private Duration castTime;
		private boolean channeled;

		protected void parse() {
			if (AbilityTooltipParser.this.castTime != null) {
				this.castTime = AbilityTooltipParser.this.castTime;
				this.channeled = false;

				if (channelTime != null) {
					throw new IllegalArgumentException("Both cast and channel time are present");
				}
			} else if (channelTime != null) {
				this.castTime = channelTime;
				this.channeled = true;
			} else {
				throw new IllegalArgumentException("No cast time: " + getName());
			}
		}
	}

	@Getter
	public class ParsedDirectComponent {
		private Integer minDmg;
		private Integer maxDmg;
		private Integer minDmg2;
		private Integer maxDmg2;

		protected void parse() {
			if (spellMatcher == null) {
				return;
			}

			this.minDmg = spellMatcher.getMinDmg().orElse(null);
			this.maxDmg = spellMatcher.getMaxDmg().orElse(null);
			this.minDmg2 = spellMatcher.getMinDmg2().orElse(null);
			this.maxDmg2 = spellMatcher.getMaxDmg2().orElse(null);
		}
	}

	@Getter
	public class ParsedDoTComponent {
		private Duration dotDuration;
		private Duration tickInterval;
		private Integer numTicks;
		private Integer dotDamage;
		private Integer tickDamage;
		private EffectId appliedEffect;
		private Duration appliedEffectDuration;

		protected void parse() {
			if (spellMatcher == null || !hasDotComponent()) {
				return;
			}

			dotDuration = spellMatcher.getDotDuration().orElseThrow();
			tickInterval = spellMatcher.getTickInterval().orElseThrow();

			numTicks = (int)dotDuration.divideBy(tickInterval);

			var optionalDotDmg = spellMatcher.getDotDmg();
			var optionalTickDmg = spellMatcher.getTickDmg();

			if (optionalDotDmg.isPresent() && optionalTickDmg.isEmpty()) {
				dotDamage = optionalDotDmg.get();
				tickDamage = dotDamage / numTicks;
			} else if (optionalDotDmg.isEmpty() && optionalTickDmg.isPresent()) {
				tickDamage = optionalTickDmg.get();
				dotDamage = tickDamage * numTicks;
			} else {
				throw new IllegalArgumentException();
			}

			assertAllValuesAreInteger();

			appliedEffect = EffectId.parse(getName());
			appliedEffectDuration = dotDuration;
		}

		private boolean hasDotComponent() {
			return spellMatcher.getDotDuration().isPresent();
		}

		private void assertAllValuesAreInteger() {
			if (dotDuration.divideBy(tickInterval) % 1 != 0) {
				throw new IllegalArgumentException("#ticks is not integer: " + getName());
			}
			if (tickDamage * numTicks != dotDamage) {
				throw new IllegalArgumentException("Tick damage is not integer: " + getName());
			}
		}
	}
}
