package wow.simulator.model.unit.action;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wow.commons.model.spells.Spell;
import wow.commons.model.spells.SpellId;
import wow.commons.model.talents.TalentId;
import wow.simulator.WowSimulatorSpringTest;
import wow.simulator.model.action.ActionStatus;
import wow.simulator.model.time.Time;
import wow.simulator.model.unit.Unit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: POlszewski
 * Date: 2023-08-11
 */
class CastSpellActionTest extends WowSimulatorSpringTest {
	@Test
	void cast() {
		CastSpellAction action = getCastSpellAction(SpellId.CORRUPTION);

		clock.advanceTo(Time.at(10));

		action.start();

		action.update();

		assertThat(action.getStatus()).isEqualTo(ActionStatus.IN_PROGRESS);
		assertThat(action.getNextUpdateTime()).hasValue(Time.at(12));

		clock.advanceTo(Time.at(12));

		action.update();

		assertThat(action.getStatus()).isEqualTo(ActionStatus.FINISHED);
	}

	@Test
	void castShorterThanGcd() {
		enableTalent(TalentId.IMPROVED_CORRUPTION, 4);

		CastSpellAction action = getCastSpellAction(SpellId.CORRUPTION);

		clock.advanceTo(Time.at(10));

		action.start();

		action.update();

		assertThat(action.getStatus()).isEqualTo(ActionStatus.IN_PROGRESS);
		assertThat(action.getNextUpdateTime()).hasValue(Time.at(10.4));

		clock.advanceTo(Time.at(10.4));

		action.update();

		assertThat(action.getStatus()).isEqualTo(ActionStatus.IN_PROGRESS);
		assertThat(action.getNextUpdateTime()).hasValue(Time.at(11.5));

		clock.advanceTo(Time.at(11.5));

		action.update();

		assertThat(action.getStatus()).isEqualTo(ActionStatus.FINISHED);
	}

	@Test
	void instant() {
		enableTalent(TalentId.IMPROVED_CORRUPTION, 5);

		CastSpellAction action = getCastSpellAction(SpellId.CORRUPTION);

		clock.advanceTo(Time.at(10));

		action.start();

		action.update();

		assertThat(action.getStatus()).isEqualTo(ActionStatus.IN_PROGRESS);
		assertThat(action.getNextUpdateTime()).hasValue(Time.at(11.5));

		clock.advanceTo(Time.at(11.5));

		action.update();

		assertThat(action.getStatus()).isEqualTo(ActionStatus.FINISHED);
	}

	@Test
	void instantIgnoringGcd() {
		enableTalent(TalentId.AMPLIFY_CURSE, 1);

		CastSpellAction action = getCastSpellOnSelfAction(SpellId.AMPLIFY_CURSE);

		clock.advanceTo(Time.at(10));

		action.start();

		action.update();

		assertThat(action.getStatus()).isEqualTo(ActionStatus.FINISHED);
	}

	@Test
	void channel() {
		CastSpellAction action = getCastSpellAction(SpellId.DRAIN_LIFE);

		clock.advanceTo(Time.at(10));

		action.start();

		action.update();

		assertThat(action.getStatus()).isEqualTo(ActionStatus.IN_PROGRESS);
		assertThat(action.getNextUpdateTime()).hasValue(Time.at(11));

		clock.advanceTo(Time.at(11));

		action.update();

		assertThat(action.getStatus()).isEqualTo(ActionStatus.IN_PROGRESS);
		assertThat(action.getNextUpdateTime()).hasValue(Time.at(12));

		clock.advanceTo(Time.at(12));

		action.update();

		assertThat(action.getStatus()).isEqualTo(ActionStatus.IN_PROGRESS);
		assertThat(action.getNextUpdateTime()).hasValue(Time.at(13));

		clock.advanceTo(Time.at(13));

		action.update();

		assertThat(action.getStatus()).isEqualTo(ActionStatus.IN_PROGRESS);
		assertThat(action.getNextUpdateTime()).hasValue(Time.at(14));

		clock.advanceTo(Time.at(14));

		action.update();

		assertThat(action.getStatus()).isEqualTo(ActionStatus.IN_PROGRESS);
		assertThat(action.getNextUpdateTime()).hasValue(Time.at(15));

		clock.advanceTo(Time.at(15));

		action.update();

		assertThat(action.getStatus()).isEqualTo(ActionStatus.FINISHED);
	}

	@Test
	void cantPayManaCost() {
		CastSpellAction action = getCastSpellAction(SpellId.CORRUPTION);

		setMana(player, 0);

		assertThat(player.getCurrentMana()).isZero();

		clock.advanceTo(Time.at(10));

		action.start();
		action.update();

		assertThat(action.getStatus()).isEqualTo(ActionStatus.INTERRUPTED);
	}

	void enableTalent(TalentId talentId, int rank) {
		player.getCharacter().getTalents().enableTalent(talentId, rank);
		getCharacterService().updateAfterRestrictionChange(player.getCharacter());
	}

	CastSpellAction getCastSpellAction(SpellId spellId) {
		return getSpellAction(spellId, target);
	}

	CastSpellAction getCastSpellOnSelfAction(SpellId spellId) {
		return getSpellAction(spellId, player);
	}

	private CastSpellAction getSpellAction(SpellId spellId, Unit target) {
		Spell spell = player.getSpell(spellId).orElseThrow();
		return new CastSpellAction(player, spell, target);
	}

	@BeforeEach
	void setup() {
		setupTestObjects();
	}
}