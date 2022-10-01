package wow.commons.model.attributes;

/**
 * User: POlszewski
 * Date: 2022-01-05
 */
public interface StatEquivalentProvider {
	Attributes getStatEquivalent(StatProvider statProvider);
}
