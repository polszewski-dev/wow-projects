package wow.commons.model.config;

/**
 * User: POlszewski
 * Date: 2022-11-24
 */
public interface ConfigurationElement<T> extends Described, TimeRestricted, CharacterRestricted {
	T getId();
}
