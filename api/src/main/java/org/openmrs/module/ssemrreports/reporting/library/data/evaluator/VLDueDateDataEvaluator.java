/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.ssemrreports.reporting.library.data.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.module.ssemrreports.reporting.library.data.definition.VLDueDateDataDefinition;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.evaluator.PersonDataEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.Date;

/**
 * VL Due Date Data Definition
 */
@Handler(supports = VLDueDateDataDefinition.class, order = 50)
public class VLDueDateDataEvaluator implements PersonDataEvaluator {
	
	@Autowired
	private EvaluationService evaluationService;
	
	public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context)
	        throws EvaluationException {
		EvaluatedPersonData c = new EvaluatedPersonData(definition, context);
		
		String qry = "select t.client_id, DATE_FORMAT(MAX(t.due_date), '%d-%m-%Y') AS max_due_date from (SELECT fp.client_id, mp.age, "
		        + "fp.vl_results, fp.viral_load_value, fp.date_vl_sample_collected, fh.art_start_date, "
		        + "fp.client_pregnant, fp.encounter_datetime, hvl.third_eac_session_date, vlr.value, "
		        + " CASE WHEN mp.age <= 18 THEN DATE_ADD(fp.date_vl_sample_collected, INTERVAL 6 MONTH) "
		        + " WHEN fp.client_pregnant = 'Yes' AND TIMESTAMPDIFF(MONTH, fh.art_start_date, CURRENT_DATE) < 6 THEN DATE_ADD(fp.date_vl_sample_collected, INTERVAL 3 MONTH) "
		        + " WHEN fp.client_pregnant = 'Yes' AND TIMESTAMPDIFF(MONTH, fh.art_start_date, CURRENT_DATE) >= 6 THEN fp.encounter_datetime"
		        + "  WHEN mp.age > 18 AND fp.viral_load_value >= 1000 THEN DATE_ADD(fp.date_vl_sample_collected, INTERVAL 3 MONTH) "
		        + " WHEN mp.age > 18 AND (fp.viral_load_value < 1000 OR fp.vl_results = 'Below Detectable (BDL)') THEN DATE_ADD(fp.date_vl_sample_collected, INTERVAL 12 MONTH) "
		        + " WHEN hvl.third_eac_session_date IS NOT NULL THEN DATE_ADD(hvl.third_eac_session_date, INTERVAL 1 MONTH)"
		        + "  ELSE NULL END as due_date FROM ssemr_etl.ssemr_flat_encounter_hiv_care_follow_up fp "
		        + " LEFT JOIN ssemr_etl.ssemr_flat_encounter_hiv_care_enrolment en ON en.client_id = fp.client_id "
		        + " LEFT JOIN ssemr_etl.ssemr_flat_encounter_vl_laboratory_request vlr ON vlr.client_id = fp.client_id "
		        + " LEFT JOIN ssemr_etl.ssemr_flat_encounter_high_viral_load hvl ON hvl.client_id = fp.client_id"
		        + " LEFT JOIN ssemr_etl.mamba_dim_person mp ON mp.person_id = fp.client_id "
		        + " LEFT JOIN ssemr_etl.ssemr_flat_encounter_personal_family_tx_history fh on fh.client_id = fp.client_id "
		        + "  WHERE DATE(fp.encounter_datetime) <= DATE(:endDate) GROUP BY fp.client_id) t GROUP BY client_id";
		
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
