package wow.commons.repository.impl.parser.excel.mapper;

import wow.commons.model.attribute.complex.ComplexAttribute;
import wow.commons.util.parser.simple.ParseResult;

/**
 * User: POlszewski
 * Date: 2022-12-18
 */
interface Mapper<T extends ComplexAttribute> {
	String toString(T attribute);

	T fromString(ParseResult parseResult);

	<C> boolean accepts(Class<C> attributeClass);

	boolean accepts(String typeName);
}
