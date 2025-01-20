package org.openmrs.module.ssemrreports.reporting.library.data.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.evaluator.PersonDataEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.openmrs.module.ssemrreports.reporting.library.data.definition.ClinicalStatusDataDefinition;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Map;

/**
 * Evaluates Clinical Status Data Definition
 */
@Handler(supports = ClinicalStatusDataDefinition.class, order = 50)
public class ClinicalStatusDataEvaluator implements PersonDataEvaluator {
	
	@Autowired
	private EvaluationService evaluationService;
	
	public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context)
	        throws EvaluationException {
		EvaluatedPersonData c = new EvaluatedPersonData(definition, context);
		
		String qry = "SELECT p.patient_id, " + "CASE " + "    WHEN f.death = 'Yes' THEN 'Died' "
		        + "    WHEN f.transfer_out = 'Yes' THEN 'TO' "
		        + "    WHEN f.client_refused_treatment = 'Yes' THEN 'Refused TX' " + "    WHEN EXISTS ( "
		        + "        SELECT 1 " + "        FROM openmrs.patient_appointment future_appointments "
		        + "        WHERE future_appointments.patient_id = p.patient_id "
		        + "          AND future_appointments.start_date_time > CURDATE() " + "    ) THEN 'Active' "
		        + "    WHEN DATEDIFF(CURDATE(), p.start_date_time) <= 28 THEN 'Active' "
		        + "    WHEN DATEDIFF(CURDATE(), p.start_date_time) > 28 THEN 'IIT' " + "    ELSE 'Unknown' "
		        + "END AS status " + "FROM openmrs.patient_appointment p " + "LEFT JOIN ( "
		        + "    SELECT client_id, transfer_out, death, client_refused_treatment "
		        + "    FROM ssemr_etl.ssemr_flat_encounter_end_of_follow_up " + ") f ON f.client_id = p.patient_id "
		        + "LEFT JOIN ( " + "    SELECT patient_id, MAX(start_date_time) AS max_start_date_time "
		        + "    FROM openmrs.patient_appointment " + "    GROUP BY patient_id "
		        + ") latest_appt ON p.patient_id = latest_appt.patient_id " + "WHERE f.death = 'Yes' "
		        + "   OR f.transfer_out = 'Yes' " + "   OR f.client_refused_treatment = 'Yes' " + "   OR EXISTS ( "
		        + "        SELECT 1 " + "        FROM openmrs.patient_appointment future_appointments "
		        + "        WHERE future_appointments.patient_id = p.patient_id "
		        + "          AND future_appointments.start_date_time > CURDATE() " + "    ) "
		        + "   OR DATEDIFF(CURDATE(), p.start_date_time) <= 28 "
		        + "   OR DATEDIFF(CURDATE(), p.start_date_time) > 28 " + "ORDER BY p.patient_id ASC;";
		
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
