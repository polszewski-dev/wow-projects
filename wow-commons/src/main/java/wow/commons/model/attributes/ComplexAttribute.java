package wow.commons.model.attributes;

/**
 * User: POlszewski
 * Date: 2022-01-02
 */
public abstract class ComplexAttribute extends Attribute {
	protected ComplexAttribute(AttributeId id, AttributeCondition condition) {
		super(id, condition);
		if (!id.isComplexAttribute()) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public abstract ComplexAttribute attachCondition(AttributeCondition condition);

	@Override
	protected String getValueString(String idFmt) {
		return String.format("%s -> %s", id, this);
	}
}
