package wow.scraper.parser.tooltip;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import wow.commons.model.attribute.AttributeId;
import wow.commons.model.attribute.Attributes;
import wow.commons.model.attribute.condition.AttributeCondition;
import wow.commons.model.effect.Effect;
import wow.scraper.ScraperSpringTest;
import wow.scraper.model.JsonCommonDetails;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: POlszewski
 * Date: 2022-12-02
 */
public abstract class TooltipParserTest<D extends JsonCommonDetails, P extends AbstractTooltipParser<D>> extends ScraperSpringTest {
	static final ObjectMapper MAPPER = new ObjectMapper();

	P getTooltip(String path) {
		D data = getData(path);
		P parser = createParser(data);
		parser.parse();
		return parser;
	}

	protected D getData(String path) {
		return read(path, getDetailsClass());
	}

	@SneakyThrows
	<T> T read(String path, Class<T> clazz) {
		return MAPPER.readValue(
				TooltipParserTest.class.getResourceAsStream("/tooltips/" + path),
				clazz
		);
	}

	protected abstract P createParser(D data);

	protected abstract Class<D> getDetailsClass();

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
		assertThat(effect.getEffectId()).isEqualTo(effectId);
		assertThat(effect.getTooltip()).isEqualTo(tooltip);
	}
}
