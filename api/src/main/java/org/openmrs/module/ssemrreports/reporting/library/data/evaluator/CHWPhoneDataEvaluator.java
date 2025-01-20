package org.openmrs.module.ssemrreports.reporting.library.data.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.evaluator.PersonDataEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.openmrs.module.ssemrreports.reporting.library.data.definition.CHWPhoneDataDefinition;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Evaluates COV Phone Data Definition
 */
@Handler(supports = CHWPhoneDataDefinition.class, order = 50)
public class CHWPhoneDataEvaluator implements PersonDataEvaluator {
	
	@Autowired
	private EvaluationService evaluationService;
	
	public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context)
	        throws EvaluationException {
		EvaluatedPersonData c = new EvaluatedPersonData(definition, context);
		
		String qry = "SELECT client_id, "
		        + "SUBSTRING_INDEX(MAX(CONCAT(encounter_datetime, '|', chw_phone_number)), '|', -1) AS chw_phone "
		        + "FROM ssemr_etl.ssemr_flat_encounter_community_linkage " + "GROUP BY client_id";
		
		SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
		queryBuilder.append(qry);
		Map<Integer, Object> data = evaluationService.evaluateToMap(queryBuilder, Integer.class, Object.class, context);
		c.setData(data);
		return c;
	}
}
