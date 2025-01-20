package org.openmrs.module.ssemrreports.reporting.library.data.definition;

import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/**
 * Index Data Definition
 */
@Caching(strategy = ConfigurationPropertyCachingStrategy.class)
public class IndexDataDefinition extends BaseDataDefinition implements PersonDataDefinition {
	
	private static int counter = 0;
	
	public IndexDataDefinition() {
		super();
	}
	
	@Override
	public Class<?> getDataType() {
		return Integer.class;
	}
}
