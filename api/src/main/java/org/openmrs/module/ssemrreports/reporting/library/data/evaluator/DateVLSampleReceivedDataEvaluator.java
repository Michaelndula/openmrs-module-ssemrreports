package org.openmrs.module.ssemrreports.reporting.library.data.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.evaluator.PersonDataEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.openmrs.module.ssemrreports.reporting.library.data.definition.DateVLSampleCollectedDataDefinition;
import org.openmrs.module.ssemrreports.reporting.library.data.definition.DateVLSampleReceivedDataDefinition;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Map;

/**
 * Date VL Sample Received Data Evaluator
 */
@Handler(supports = DateVLSampleReceivedDataDefinition.class, order = 50)
public class DateVLSampleReceivedDataEvaluator implements PersonDataEvaluator {
	
	@Autowired
	private EvaluationService evaluationService;
	
	public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context)
	        throws EvaluationException {
		EvaluatedPersonData c = new EvaluatedPersonData(definition, context);
		
		String qry = "SELECT client_id, DATE_FORMAT(max(date_vl_results_received), '%d-%m-%Y') as received_date FROM ssemr_etl.ssemr_flat_encounter_hiv_care_follow_up where DATE(encounter_datetime) <= :endDate group by client_id";
		
		SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
		queryBuilder.append(qry);
		Date startDate = (Date) context.getParameterValue("startDate");
		Date endDate = (Date) context.getParameterValue("endDate");
		queryBuilder.addParameter("endDate", endDate);
		queryBuilder.addParameter("startDate", startDate);
		
		Map<Integer, Object> data = evaluationService.evaluateToMap(queryBuilder, Integer.class, Object.class, context);
		c.setData(data);
		return c;
		
	}
}