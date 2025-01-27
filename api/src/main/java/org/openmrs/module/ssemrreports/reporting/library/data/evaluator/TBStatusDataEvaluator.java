package org.openmrs.module.ssemrreports.reporting.library.data.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.evaluator.PersonDataEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.openmrs.module.ssemrreports.reporting.library.data.definition.TBStatusDataDefinition;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Map;

/**
 * Evaluates TB Screening Data Definition
 */
@Handler(supports = TBStatusDataDefinition.class, order = 50)
public class TBStatusDataEvaluator implements PersonDataEvaluator {
	
	@Autowired
	private EvaluationService evaluationService;
	
	public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context)
	        throws EvaluationException {
		EvaluatedPersonData c = new EvaluatedPersonData(definition, context);
		
		String qry = "SELECT client_id, " + "CASE " + "  WHEN on_tb_treatment = 'Yes' THEN 'On TB Treatment' "
		        + "  WHEN COALESCE(MID(MAX(CONCAT(encounter_datetime, tb_status)), 20), '') = '' THEN 'N/A' "
		        + "  ELSE CASE MID(MAX(CONCAT(encounter_datetime, tb_status)), 20) "
		        + "    WHEN 'No Signs' THEN 'No Signs' " + "    WHEN 'Pr TB - Presumptive TB' THEN 'Presumptive TB' "
		        + "    WHEN 'ND - TB Screening not done' THEN 'Not done' " + "    ELSE 'N/A' " + "  END "
		        + "END AS tb_status " + "FROM ssemr_etl.ssemr_flat_encounter_hiv_care_follow_up " + "GROUP BY client_id";
		
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
