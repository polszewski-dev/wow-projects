package wow.simulator.simulation.spell;

import org.junit.jupiter.api.BeforeEach;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.RaceId;
import wow.commons.model.spell.ResourceType;
import wow.commons.model.spell.Spell;
import wow.commons.model.spell.SpellSchool;
import wow.simulator.WowSimulatorSpringTest;
import wow.simulator.log.handler.GameLogHandler;
import wow.simulator.model.unit.Pet;
import wow.simulator.model.unit.Player;
import wow.simulator.model.unit.Unit;
import wow.simulator.util.SpellInfo;
import wow.simulator.util.TestEventCollectingHandler;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static wow.simulator.util.CalcUtils.increaseByPct;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
public abstract class SpellSimulationTest extends WowSimulatorSpringTest implements GameLogHandler {
	@BeforeEach
	void setUp() {
		beforeSetUp();

		createSimulation();

		handler = new TestEventCollectingHandler();

		simulation.addHandler(handler);
		simulation.addHandler(this);

		if (createUnitsOnSetUp) {
			createDefaultUnits();
		}

		afterSetUp();
	}

	@Override
	protected void createDefaultUnits() {
		super.createDefaultUnits();

		player2 = getNakedPlayer(getPartyMemberConfig(2), "Player2");
		player3 = getNakedPlayer(getPartyMemberConfig(3), "Player3");
		player4 = getNakedPlayer(getPartyMemberConfig(4), "Player4");
		player5 = getNakedPlayer(getPartyMemberConfig(5), "Player5");
		baseline = getNakedPlayer(playerConfig, "Baseline");

		target2 = getEnemy("Target2");
		target3 = getEnemy("Target3");
		target4 = getEnemy("Target4");
		target5 = getEnemy("Target5");

		player2.setTarget(target2);
		player3.setTarget(target3);
		player4.setTarget(target4);
		player5.setTarget(target5);
		baseline.setTarget(target);

		simulation.add(player);
		simulation.add(player2);
		simulation.add(player3);
		simulation.add(player4);
		simulation.add(player5);
		simulation.add(baseline);
		simulation.add(target);
		simulation.add(target2);
		simulation.add(target3);
		simulation.add(target4);
		simulation.add(target5);

		player.invite(player2, player3, player4, player5);
	}

	protected boolean createUnitsOnSetUp = true;

	protected void beforeSetUp() {}

	protected void afterSetUp() {}

	protected Player player2;
	protected Player player3;
	protected Player player4;
	protected Player player5;
	protected Player baseline;

	protected Unit target2;
	protected Unit target3;
	protected Unit target4;
	protected Unit target5;

	protected Pet pet2;

	private PlayerConfig partyMemberConfig;
	private final PlayerConfig[] partyMemberConfigs = new PlayerConfig[4];

	private PlayerConfig getPartyMemberConfig(int playerNo) {
		if (partyMemberConfigs[playerNo - 2] != null) {
			return partyMemberConfigs[playerNo - 2];
		}
		if (partyMemberConfig != null) {
			return partyMemberConfig;
		}
		return playerConfig;
	}

	protected void setOtherPartyMemberConfig(CharacterClassId characterClassId, RaceId raceId) {
		this.partyMemberConfig = new PlayerConfig(characterClassId, raceId);
	}

	protected void setPlayer2Config(CharacterClassId characterClassId, RaceId raceId) {
		setPartyMemberConfig(2, characterClassId, raceId);
	}

	protected void setPlayer3Config(CharacterClassId characterClassId, RaceId raceId) {
		setPartyMemberConfig(3, characterClassId, raceId);
	}

	protected void setPlayer4Config(CharacterClassId characterClassId, RaceId raceId) {
		setPartyMemberConfig(4, characterClassId, raceId);
	}

	protected void setPlayer5Config(CharacterClassId characterClassId, RaceId raceId) {
		setPartyMemberConfig(5, characterClassId, raceId);
	}

	private void setPartyMemberConfig(int idx, CharacterClassId characterClassId, RaceId raceId) {
		this.partyMemberConfigs[idx - 2] = new PlayerConfig(characterClassId, raceId);
	}

	@Override
	public void increasedResource(ResourceType type, Spell spell, Unit target, int amount, int current, int previous, boolean crit, Unit caster) {
		if (spell != null) {
			return;
		}

		var ctx = getContext(target);

		switch (type) {
			case HEALTH -> ctx.regeneratedHealth += amount;
			case MANA -> ctx.regeneratedMana += amount;
		}
	}

	@Override
	public void petSummoned(Unit master, Pet pet) {
		if (master == player) {
			this.pet = pet;
		} else if (master == player2) {
			this.pet2 = pet;
		}
	}

	@Override
	public void petUnsummoned(Unit master, Pet pet) {
		if (master == player) {
			this.pet = null;
		} else if (master == player2) {
			this.pet2 = null;
		}
	}

	@Override
	public void petDismissed(Unit master, Pet pet) {
		petUnsummoned(master, pet);
	}

	@Override
	public void petSacrificed(Unit master, Pet pet) {
		petUnsummoned(master, pet);
	}

	protected void summonedPetCasts(Unit unit, String abilityName) {
		unit.immediateAction(self -> self.getActivePet().cast(abilityName));
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
		simulateBuffSpell(abilityName, 60);
	}

	protected void simulateBuffSpell(String abilityName, double timeUntil) {
		player.cast(abilityName);

		updateUntil(timeUntil);
	}

	@Override
	protected void equip(Unit unit, String itemName) {
		super.equip(unit, itemName);
		if (unit == player) {
			super.equip(baseline, itemName);
		}
	}

	@Override
	protected void equip(Unit unit, String itemName, String enchantName) {
		super.equip(unit, itemName, enchantName);
		if (unit == player) {
			super.equip(baseline, itemName, enchantName);
		}
	}

	@Override
	protected void equip(Unit unit, String itemName, ItemSlot itemSlot) {
		super.equip(unit, itemName, itemSlot);
		if (unit == player) {
			super.equip(baseline, itemName, itemSlot);
		}
	}

	@Override
	protected void equip(Unit unit, int itemId, ItemSlot itemSlot) {
		super.equip(unit, itemId, itemSlot);
		if (unit == player) {
			super.equip(baseline, itemId, itemSlot);
		}
	}

	protected void assertStamina(Unit unit, int expected) {
		assertThat(unit.getStats().getStamina()).isEqualTo(expected);
	}

	protected void assertIntellect(Unit unit, int expected) {
		assertThat(unit.getStats().getIntellect()).isEqualTo(expected);
	}

	protected void assertSpirit(Unit unit, int expected) {
		assertThat(unit.getStats().getSpirit()).isEqualTo(expected);
	}

	protected void assertSpellPower(Unit unit, int expected) {
		assertThat(unit.getStats().getSpellPower()).isEqualTo(expected);
	}

	protected void assertSpellDamage(Unit unit, int expected) {
		assertThat(unit.getStats().getSpellDamage()).isEqualTo(expected);
	}

	protected void assertSpellDamage(Unit unit, SpellSchool school, int expected) {
		assertThat(unit.getStats().getSpellDamage(school)).isEqualTo(expected);
	}

	protected void assertSpellHealing(Unit unit, int expected) {
		assertThat(unit.getStats().getSpellHealing()).isEqualTo(expected);
	}

	protected void assertSpellHitRating(Unit unit, int expected) {
		assertThat(unit.getStats().getSpellHitRating()).isEqualTo(expected);
	}

	protected void assertSpellHitPct(Unit unit, double expected) {
		assertThat(unit.getStats().getSpellHitPct()).isEqualTo(expected);
	}

	protected void assertMp5(Unit unit, int expected) {
		assertThat(unit.getStats().getInterruptedManaRegen()).isEqualTo(expected);
	}

	protected void assertHealthGained(SpellInfo spellInfo, int sp) {
		assertHealthGained(spellInfo, player2, sp);
	}

	protected void assertSpellPowerIsIncreasedBy(Unit unit, int increase) {
		var baselineValue = baseline.getStats().getSpellPower();
		var unitValue = unit.getStats().getSpellPower();

		assertThat(unitValue).isEqualTo(baselineValue + increase);
	}

	protected void assertSpellDamageIsIncreasedBy(Unit unit, int increase) {
		var baselineValue = baseline.getStats().getSpellDamage();
		var unitValue = unit.getStats().getSpellDamage();

		assertThat(unitValue).isEqualTo(baselineValue + increase);
	}

	protected void assertSpellDamagePctIncreasedBy(Unit unit, SpellSchool school, int increase) {
		var baselineValue = baseline.getStats().getSpellDamagePct(school);
		var unitValue = unit.getStats().getSpellDamagePct(school);

		assertThat(unitValue).isEqualTo(baselineValue + increase);
	}

	protected void assertSpellHealingIsIncreasedBy(Unit unit, int increase) {
		var baselineValue = baseline.getStats().getSpellHealing();
		var unitValue = unit.getStats().getSpellHealing();

		assertThat(unitValue).isEqualTo(baselineValue + increase);
	}

	protected void assertSpellHitPctIncreasedBy(Unit unit, int increase) {
		var baselineValue = baseline.getStats().getSpellHitPct();
		var unitValue = unit.getStats().getSpellHitPct();

		assertThat(unitValue).isEqualTo(baselineValue + increase);
	}

	protected void assertSpellCritRatingIsIncreasedBy(Unit unit, int increase) {
		var baselineValue = baseline.getStats().getSpellCritRating();
		var unitValue = unit.getStats().getSpellCritRating();

		assertThat(unitValue).isEqualTo(baselineValue + increase);
	}

	protected void assertSpellCritPctIsIncreasedBy(Unit unit, double increase) {
		assertSpellCritPctIsIncreasedBy(unit, baseline, increase);
	}

	protected void assertSpellCritPctIsIncreasedBy(Unit unit, Unit baselineUnit, double increase) {
		var baselineValue = baselineUnit.getStats().getSpellCritPct();
		var unitValue = unit.getStats().getSpellCritPct();

		assertThat(unitValue).isEqualTo(baselineValue + increase, PRECISION);
	}

	protected void assertSpellHasteRatingIsIncreasedBy(Unit unit, int increase) {
		var baselineValue = baseline.getStats().getSpellHasteRating();
		var unitValue = unit.getStats().getSpellHasteRating();

		assertThat(unitValue).isEqualTo(baselineValue + increase);
	}

	protected void assertSpellHastePctIsIncreasedBy(Unit unit, int increase) {
		var baselineValue = baseline.getStats().getSpellHastePct();
		var unitValue = unit.getStats().getSpellHastePct();

		assertThat(unitValue).isEqualTo(baselineValue + increase);
	}

	protected void assertStaminaIsIncreasedBy(Unit unit, int increase) {
		var baselineValue = baseline.getStats().getStamina();
		var unitValue = unit.getStats().getStamina();

		assertThat(unitValue).isEqualTo(baselineValue + increase);
	}

	protected void assertStaminaIsIncreasedBy(Unit unit, Unit baselineUnit, int increase) {
		var baselineValue = baselineUnit.getStats().getStamina();
		var unitValue = unit.getStats().getStamina();

		assertThat(unitValue).isEqualTo(baselineValue + increase);
	}

	protected void assertIntellectIsIncreasedBy(Unit unit, int increase) {
		var baselineValue = baseline.getStats().getIntellect();
		var unitValue = unit.getStats().getIntellect();

		assertThat(unitValue).isEqualTo(baselineValue + increase);
	}

	protected void assertSpiritIsIncreasedBy(Unit unit, int increase) {
		var baselineValue = baseline.getStats().getSpirit();
		var unitValue = unit.getStats().getSpirit();

		assertThat(unitValue).isEqualTo(baselineValue + increase);
	}

	protected void assertStatsAreIncreasedBy(Unit unit, int increase) {
		assertStaminaIsIncreasedBy(unit, increase);
		assertIntellectIsIncreasedBy(unit, increase);
		assertSpiritIsIncreasedBy(unit, increase);
	}

	protected void assertStaminaIsIncreasedByPct(Unit unit, int pctIncrease) {
		assertStaminaIsIncreasedByPct(unit, baseline, pctIncrease);
	}

	protected void assertStaminaIsIncreasedByPct(Unit unit, Unit baselineUnit, int pctIncrease) {
		var baselineValue = baselineUnit.getStats().getStamina();
		var unitValue = unit.getStats().getStamina();

		assertThat(unitValue).isEqualTo(increaseByPct(baselineValue, pctIncrease));
	}

	protected void assertIntellectIsIncreasedByPct(Unit unit, int pctIncrease) {
		assertIntellectIsIncreasedByPct(unit, baseline, pctIncrease);
	}

	protected void assertIntellectIsIncreasedByPct(Unit unit, Unit baselineUnit, int pctIncrease) {
		var baselineValue = baselineUnit.getStats().getIntellect();
		var unitValue = unit.getStats().getIntellect();

		assertThat(unitValue).isEqualTo(increaseByPct(baselineValue, pctIncrease));
	}

	protected void assertSpiritIsIncreasedByPct(Unit unit, int pctIncrease) {
		var baselineValue = baseline.getStats().getSpirit();
		var unitValue = unit.getStats().getSpirit();

		assertThat(unitValue).isEqualTo(increaseByPct(baselineValue, pctIncrease));
	}

	protected void assertStatsAreIncreasedByPct(Unit unit, int pctIncrease) {
		assertStaminaIsIncreasedByPct(unit, pctIncrease);
		assertIntellectIsIncreasedByPct(unit, pctIncrease);
		assertSpiritIsIncreasedByPct(unit, pctIncrease);
	}

	protected void assertMp5IsIncreasedBy(Unit unit, int increase) {
		var baselineValue = baseline.getStats().getInterruptedManaRegen();
		var unitValue = unit.getStats().getInterruptedManaRegen();

		assertThat(unitValue).isEqualTo(baselineValue + increase);
	}
}
