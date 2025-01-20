package org.openmrs.module.ssemrreports.reporting.library.data.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.evaluator.PersonDataEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.ssemrreports.reporting.library.data.definition.IndexDataDefinition;

/**
 * Evaluates Index Data Definition
 */
@Handler(supports = IndexDataDefinition.class, order = 50)
public class IndexDataEvaluator implements PersonDataEvaluator {
	
	@Override
	public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context)
	        throws EvaluationException {
		EvaluatedPersonData evaluatedData = new EvaluatedPersonData(definition, context);
		
		int counter = 1; // Start the ID from 1
		
		// Get all person IDs in the context
		for (Integer personId : context.getBaseCohort().getMemberIds()) {
			evaluatedData.addData(personId, counter++);
		}
		
		return evaluatedData;
	}
}
