package wow.simulator.model.effect;

import wow.commons.model.Duration;
import wow.commons.model.spells.Spell;
import wow.commons.model.spells.TickScheme;
import wow.simulator.model.unit.SpellCastContext;

/**
 * User: POlszewski
 * Date: 2023-08-16
 */
public abstract class PeriodicEffect extends Effect {
	protected final SpellCastContext context;
	protected final TickScheme tickScheme;

	protected double totalAmount;
	protected double remainingAmount;

	protected int numTicks;
	protected Duration tickInterval;

	protected PeriodicEffect(SpellCastContext context) {
		super(context.caster(), context.target());
		this.context = context;
		this.tickScheme = context.snapshot().getTickScheme();

		this.totalAmount = getTotalAmount();
		this.remainingAmount = totalAmount;

		this.numTicks = tickScheme.numTicks();
		this.tickInterval = getActualTickInterval();
	}

	@Override
	protected void setUp() {
		fromNowOnEachTick(numTicks, tickInterval, this::tick);
	}

	protected abstract double getTotalAmount();

	private void tick(int tickNo) {
		int tickAmount = getTickAmount(tickNo);
		this.remainingAmount -= tickAmount;
		onTick(tickAmount);
	}

	protected abstract void onTick(int tickAmount);

	private int getTickAmount(int tickNo) {
		if (tickNo == numTicks) {
			return (int) remainingAmount;
		} else {
			return (int) tickScheme.scale(totalAmount, tickNo - 1);
		}
	}

	private Duration getActualTickInterval() {
		if (context.spell().isChanneled()) {
			double castTime = context.snapshot().getCastTime();

			return Duration.seconds(castTime / numTicks);
		} else {
			return tickScheme.tickInterval();
		}
	}

	@Override
	public Spell getSourceSpell() {
		return context.spell();
	}

	public int getNumTicks() {
		return numTicks;
	}

	public Duration getTickInterval() {
		return tickInterval;
	}
}
