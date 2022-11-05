package wow.commons.model.attributes;

/**
 * User: POlszewski
 * Date: 2022-01-02
 */
public abstract class ComplexAttribute extends Attribute {
	protected ComplexAttribute(AttributeId id) {
		super(id);
		if (!id.isComplexAttribute()) {
			throw new IllegalArgumentException();
		}
	}
}
