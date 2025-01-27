package org.openmrs.module.ssemrreports.reporting.library.data.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.common.VitalStatus;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.AgeDataDefinition;
import org.openmrs.module.reporting.data.person.definition.BirthdateDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.VitalStatusDataDefinition;
import org.openmrs.module.reporting.data.person.evaluator.PersonDataEvaluator;
import org.openmrs.module.reporting.data.person.service.PersonDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.ssemrreports.reporting.library.data.definition.CustomAgeDataDefinition;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Handler(supports = CustomAgeDataDefinition.class, order = 50)
public class CustomAgeEvaluator implements PersonDataEvaluator {
	
	@Autowired
	PersonDataService personDataService;
	
	/**
	 * @see PersonDataEvaluator#evaluate(PersonDataDefinition, EvaluationContext)
	 * @should return all ages for the given context
	 */
	public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context)
	        throws EvaluationException {
		CustomAgeDataDefinition add = (CustomAgeDataDefinition) definition;
		EvaluatedPersonData ret = new EvaluatedPersonData(definition, context);
		
		// Evaluate the BirthdateDataDefinition to get birthdates for the cohort
		EvaluatedPersonData birthdateData = personDataService.evaluate(new BirthdateDataDefinition(), context);
		
		// Evaluate VitalStatusDataDefinition to get vital status data
		EvaluatedPersonData vitalStatusData = personDataService.evaluate(new VitalStatusDataDefinition(), context);
		
		// Loop through each person in the cohort
		for (Map.Entry<Integer, Object> e : birthdateData.getData().entrySet()) {
			Integer personId = e.getKey();
			Object birthDateObj = e.getValue();
			
			// Extract the actual birth date from the Birthdate object
			Date birthDate;
			if (birthDateObj instanceof org.openmrs.module.reporting.common.Birthdate) {
				birthDate = ((org.openmrs.module.reporting.common.Birthdate) birthDateObj).getBirthdate();
			} else if (birthDateObj instanceof Date) {
				birthDate = (Date) birthDateObj;
			} else {
				continue; // Skip invalid data
			}
			
			// Handle vital status and effective age date
			VitalStatus vitalStatus = (VitalStatus) vitalStatusData.getData().get(personId);
			Date effectiveAgeDate = ObjectUtil.nvl(add.getEffectiveDate(), new Date());
			Date deathDate = (vitalStatus != null ? vitalStatus.getDeathDate() : null);
			
			if (deathDate != null && deathDate.before(effectiveAgeDate)) {
				effectiveAgeDate = deathDate;
			}
			
			// Calculate the age in days
			long ageInDays = (effectiveAgeDate.getTime() - birthDate.getTime()) / (1000 * 60 * 60 * 24);
			
			// Convert the age to years, months, weeks, or days
			String ageWithUnit = formatAgeWithUnit(ageInDays);
			
			// Add the formatted age to the result
			ret.addData(personId, ageWithUnit);
		}
		return ret;
	}
	
	/**
	 * Format age dynamically into years, months, weeks, or days based on the number of days.
	 */
	private String formatAgeWithUnit(long ageInDays) {
		if (ageInDays < 7) {
			return ageInDays + " day(s)";
		} else if (ageInDays < 30) {
			return (ageInDays / 7) + " week(s)";
		} else if (ageInDays < 365) {
			return (ageInDays / 30) + " month(s)";
		} else {
			return (ageInDays / 365) + " year(s)";
		}
	}
}
