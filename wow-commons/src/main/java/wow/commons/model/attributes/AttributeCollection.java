package wow.commons.model.attributes;

/**
 * User: POlszewski
 * Date: 2022-01-05
 */
public interface AttributeCollection {
	<T extends AttributeCollector> T collectAttributes(T collector);
}
