package wow.simulator.simulation.spell;

import org.junit.jupiter.api.BeforeEach;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.RaceId;
import wow.commons.model.spell.ResourceType;
import wow.commons.model.spell.Spell;
import wow.simulator.WowSimulatorSpringTest;
import wow.simulator.log.handler.GameLogHandler;
import wow.simulator.model.unit.Player;
import wow.simulator.model.unit.Unit;
import wow.simulator.util.SpellInfo;
import wow.simulator.util.TestEventCollectingHandler;

import static wow.commons.model.character.CharacterClassId.WARLOCK;
import static wow.commons.model.character.RaceId.ORC;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
public abstract class SpellSimulationTest extends WowSimulatorSpringTest implements GameLogHandler {
	@BeforeEach
	void setUp() {
		beforeSetUp();

		setupTestObjects();

		handler = new TestEventCollectingHandler();

		simulation.addHandler(handler);
		simulation.addHandler(this);

		simulation.add(player);
		simulation.add(target);

		player2 = getNakedPlayer(partyMemberClassId, partyMemberRaceId, "Player2");
		player3 = getNakedPlayer(partyMemberClassId, partyMemberRaceId, "Player3");
		player4 = getNakedPlayer(partyMemberClassId, partyMemberRaceId, "Player4");

		simulation.add(player2);
		simulation.add(player3);
		simulation.add(player4);

		target2 = getEnemy("Target2");
		target3 = getEnemy("Target3");
		target4 = getEnemy("Target4");

		simulation.add(target2);
		simulation.add(target3);
		simulation.add(target4);

		player2.setTarget(target2);
		player3.setTarget(target3);
		player4.setTarget(target4);

		afterSetUp();
	}

	protected void beforeSetUp() {}

	protected void afterSetUp() {}

	protected Player player2;
	protected Player player3;
	protected Player player4;

	protected Unit target2;
	protected Unit target3;
	protected Unit target4;

	protected CharacterClassId partyMemberClassId = WARLOCK;
	protected RaceId partyMemberRaceId = ORC;

	@Override
	public void increasedResource(ResourceType type, Spell spell, Unit target, int amount, int current, int previous, boolean crit) {
		if (spell != null) {
			return;
		}

		var ctx = getContext(target);

		switch (type) {
			case HEALTH -> ctx.regeneratedHealth += amount;
			case MANA -> ctx.regeneratedMana += amount;
		}
	}

	protected void simulateDamagingSpell(String abilityName, int spellDamage) {
		addSdBonus(spellDamage);

		player.cast(abilityName);

		updateUntil(60);
	}

	protected void simulateHealingSpell(String abilityName, int healing) {
		addHealingBonus(healing);

		player2.setCurrentHealth(1);
		player.cast(abilityName, player2);

		updateUntil(60);
	}

	protected void simulateBuffSpell(String abilityName) {
		player.cast(abilityName);

		updateUntil(60);
	}

	protected void assertHealthGained(SpellInfo spellInfo, int sp) {
		assertHealthGained(spellInfo, player2, sp);
	}
}
