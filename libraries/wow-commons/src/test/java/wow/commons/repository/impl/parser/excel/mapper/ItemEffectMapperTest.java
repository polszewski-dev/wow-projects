package wow.commons.repository.impl.parser.excel.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.commons.WowCommonsSpringTest;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.Attributes;
import wow.commons.model.attribute.condition.AttributeCondition;
import wow.commons.model.attribute.condition.MiscCondition;
import wow.commons.model.effect.impl.EffectImpl;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.spell.AbilityId;
import wow.commons.repository.spell.SpellRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.attribute.AttributeId.*;

/**
 * User: POlszewski
 * Date: 2023-10-02
 */
class ItemEffectMapperTest extends WowCommonsSpringTest {
	@Autowired
	SpellRepository spellRepository;

	@Test
	void attributeEffect() {
		var attributes = Attributes.of(
				Attribute.of(POWER, 10, MiscCondition.SPELL),
				Attribute.of(CRIT_RATING, 20, MiscCondition.SPELL),
				Attribute.of(CAST_TIME, -0.25, AttributeCondition.of(AbilityId.FLAMESTRIKE))
		);
		var original = EffectImpl.newAttributeEffect(attributes);
		var serialized = itemEffectMapper.toString(original);
		var deserialized = itemEffectMapper.fromString(serialized, phaseId);

		assertThat(deserialized.getModifierComponent()).isEqualTo(original.getModifierComponent());
	}

	@Test
	void permanentEffect() {
		var original = spellRepository.getEffect(172, phaseId).orElseThrow();
		var serialized = itemEffectMapper.toString(original);
		var deserialized = itemEffectMapper.fromString(serialized, phaseId);

		assertThat(deserialized).isEqualTo(original);
	}

	ItemEffectMapper itemEffectMapper;
	PhaseId phaseId = PhaseId.TBC_P5;
	
	@BeforeEach
	void setUp() {
		itemEffectMapper = new ItemEffectMapper(spellRepository);
	}
}