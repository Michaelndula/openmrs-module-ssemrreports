package org.openmrs.module.ssemrreports.reporting.library.data.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.evaluator.PersonDataEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.openmrs.module.ssemrreports.reporting.library.data.definition.AHDDataDefinition;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Map;

/**
 * Evaluates AHD Data Definition
 */
@Handler(supports = AHDDataDefinition.class, order = 50)
public class AHDDataEvaluator implements PersonDataEvaluator {
	
	@Autowired
	private EvaluationService evaluationService;
	
	public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context)
	        throws EvaluationException {
		EvaluatedPersonData c = new EvaluatedPersonData(definition, context);
		
		String qry = "SELECT client_id, " + "CASE "
		        + "  WHEN cd4 < 200 OR who_clinical_stage IN ('WHO Stage 3', 'WHO Stage 4') OR "
		        + "       TIMESTAMPDIFF(YEAR, birthdate, CURDATE()) < 5 OR on_tb_treatment = 'Yes' THEN 'Yes' "
		        + "  ELSE 'No' " + "END AS result " + "FROM ( "
		        + "  SELECT e.client_id, e.cd4, e.who_clinical_stage, e.on_tb_treatment, p.birthdate "
		        + "  FROM ssemr_etl.ssemr_flat_encounter_hiv_care_follow_up e "
		        + "  JOIN ssemr_etl.mamba_dim_person p ON e.client_id = p.person_id "
		        + "  WHERE DATE(e.encounter_datetime) <= DATE(:endDate) " + "  AND e.encounter_datetime = ( "
		        + "    SELECT MAX(encounter_datetime) " + "    FROM ssemr_etl.ssemr_flat_encounter_hiv_care_follow_up "
		        + "    WHERE client_id = e.client_id " + "  ) " + ") AS latest_encounter;";
		
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
