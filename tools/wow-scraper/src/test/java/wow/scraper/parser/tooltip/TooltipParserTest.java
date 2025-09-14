package wow.scraper.parser.tooltip;

import org.springframework.beans.factory.annotation.Autowired;
import wow.commons.model.attribute.AttributeId;
import wow.commons.model.attribute.Attributes;
import wow.commons.model.attribute.condition.AttributeCondition;
import wow.commons.model.effect.Effect;
import wow.commons.model.effect.EffectId;
import wow.scraper.ScraperSpringTest;
import wow.scraper.model.JsonCommonDetails;
import wow.scraper.repository.ItemDetailRepository;
import wow.scraper.repository.SpellDetailRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: POlszewski
 * Date: 2022-12-02
 */
public abstract class TooltipParserTest<D extends JsonCommonDetails, P extends AbstractTooltipParser<D>, C> extends ScraperSpringTest {
	@Autowired
	ItemDetailRepository itemDetailRepository;

	@Autowired
	SpellDetailRepository spellDetailRepository;

	P getTooltip(int id, C category) {
		D data = getData(id, category);
		P parser = createParser(data);
		parser.parse();
		return parser;
	}

	protected abstract D getData(int id, C category);

	protected abstract P createParser(D data);

	protected static void assertEffect(List<Effect> effects, int idx, AttributeId id, int value, String description) {
		assertEffect(effects.get(idx), id, value, AttributeCondition.EMPTY, description);
	}

	protected static void assertEffect(List<Effect> effects, int idx, AttributeId id, int value, AttributeCondition condition, String description) {
		assertEffect(effects.get(idx), id, value, condition, description);
	}

	protected static void assertEffect(Effect effect, AttributeId id, int value, AttributeCondition condition, String description) {
		assertEffect(effect, Attributes.of(id, value, condition), description);
	}

	static void assertEffect(Effect effect, Attributes attributes, String tooltip) {
		assertThat(effect.getModifierComponent()).isNotNull();
		assertThat(effect.getModifierComponent().attributes()).isEqualTo(attributes);
		assertThat(effect.getTooltip()).isEqualTo(tooltip);
	}

	static void assertEffect(Effect effect, int effectId, String tooltip) {
		assertThat(effect.getId()).isEqualTo(EffectId.of(effectId));
		assertThat(effect.getTooltip()).isEqualTo(tooltip);
	}
}
