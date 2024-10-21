package wow.minmax.converter.spring;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;
import wow.commons.model.categorization.ItemSubType;

import java.io.IOException;

/**
 * User: POlszewski
 * Date: 2023-05-23
 */
@JsonComponent
public class ItemSubTypeConverter {
	public static class Serialize extends JsonSerializer<ItemSubType> {
		@Override
		public void serialize(ItemSubType value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
			if (value == null) {
				jgen.writeNull();
			} else {
				jgen.writeString(((Enum<?>) value).name());
			}
		}
	}

	public static class Deserialize extends JsonDeserializer<ItemSubType> {
		@Override
		public ItemSubType deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
			String text = jp.getText();
			if (text == null) {
				return null;
			} else {
				return ItemSubType.valueOf(text);
			}
		}
	}

	private ItemSubTypeConverter() {}
}
