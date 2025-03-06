package org.openmrs.module.ssemrreports.reporting.library.data.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.evaluator.PersonDataEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.openmrs.module.ssemrreports.reporting.library.data.definition.TBScreeningDataDefinition;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Map;

/**
 * Evaluates TB Screening Data Definition
 */
@Handler(supports = TBScreeningDataDefinition.class, order = 50)
public class TBScreeningDataEvaluator implements PersonDataEvaluator {
	
	@Autowired
	private EvaluationService evaluationService;
	
	public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context)
	        throws EvaluationException {
		EvaluatedPersonData c = new EvaluatedPersonData(definition, context);
		
		String qry = "SELECT client_id, " + "CASE "
		        + "WHEN MID(MAX(CONCAT(encounter_datetime, on_tb_treatment)), 20) = 'No' "
		        + "   OR MID(MAX(CONCAT(encounter_datetime, tb_status)), 20) IN ('No Signs', 'Pr TB - Presumptive TB') "
		        + "THEN 'YES' " + "WHEN MID(MAX(CONCAT(encounter_datetime, on_tb_treatment)), 20) = 'Yes' " + "THEN 'NO' "
		        + "WHEN MID(MAX(CONCAT(encounter_datetime, on_tb_treatment)), 20) IS NULL " + "THEN 'N/A' "
		        + "END AS tb_screening " + "FROM ssemr_etl.ssemr_flat_encounter_hiv_care_follow_up " + "GROUP BY client_id";
		
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
