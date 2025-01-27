package org.openmrs.module.ssemrreports.reporting.library.data.definition;

import org.openmrs.module.reporting.common.Age;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

import java.util.Date;

/**
 * Age Data Definition
 */
@Caching(strategy = ConfigurationPropertyCachingStrategy.class)
public class CustomAgeDataDefinition extends BaseDataDefinition implements PersonDataDefinition {
	
	public static final long serialVersionUID = 1L;
	
	//****** PROPERTIES ******
	
	@ConfigurationProperty(required = false)
	private Date effectiveDate;
	
	/**
	 * Default Constructor
	 */
	public CustomAgeDataDefinition() {
		super();
	}
	
	/**
	 * Constructor to populate name only
	 */
	public CustomAgeDataDefinition(String name) {
		super(name);
	}
	
	//***** INSTANCE METHODS *****
	
	/**
	 * @see DataDefinition#getDataType()
	 */
	public Class<?> getDataType() {
		return Age.class;
	}
	
	/**
	 * @return the effectiveDate
	 */
	public Date getEffectiveDate() {
		return effectiveDate;
	}
	
	/**
	 * @param effectiveDate the effectiveDate to set
	 */
	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
}
