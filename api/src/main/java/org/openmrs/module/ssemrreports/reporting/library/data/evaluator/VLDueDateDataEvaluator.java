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
		
		String qry = "SELECT DISTINCT fp.client_id, "
		        + "DATE_FORMAT(CASE "
		        +
		        // Adults suppressed
		        "WHEN (mp.age > 18 AND pfh.art_start_date IS NOT NULL AND fp.client_pmtct = 'No' "
		        + " AND (fp.viral_load_value < 1000 OR fp.vl_results = 'Below Detectable (BDL)') "
		        + " AND EXISTS (SELECT 1 FROM ssemr_etl.ssemr_flat_encounter_hiv_care_follow_up prev "
		        + "     WHERE prev.client_id = fp.client_id AND prev.date_vl_sample_collected < fp.date_vl_sample_collected "
		        + "     AND (prev.viral_load_value < 1000 OR prev.vl_results = 'Below Detectable (BDL)')) "
		        + ") THEN DATE_ADD(fp.date_vl_sample_collected, INTERVAL 12 MONTH) "
		        +
		        
		        "WHEN (mp.age > 18 AND pfh.art_start_date IS NOT NULL AND fp.client_pmtct = 'No' "
		        + " AND (fp.viral_load_value < 1000 OR fp.vl_results = 'Below Detectable (BDL)') "
		        + " AND NOT EXISTS (SELECT 1 FROM ssemr_etl.ssemr_flat_encounter_hiv_care_follow_up prev "
		        + "     WHERE prev.client_id = fp.client_id AND prev.date_vl_sample_collected < fp.date_vl_sample_collected "
		        + "     AND (prev.viral_load_value < 1000 OR prev.vl_results = 'Below Detectable (BDL)')) "
		        + ") THEN DATE_ADD(fp.date_vl_sample_collected, INTERVAL 6 MONTH) "
		        +
		        
		        // Adults newly on ART
		        "WHEN (mp.age > 18 AND pfh.art_start_date IS NOT NULL "
		        + " AND NOT EXISTS (SELECT 1 FROM ssemr_etl.ssemr_flat_encounter_hiv_care_follow_up v2 "
		        + "     WHERE v2.client_id = fp.client_id AND v2.date_vl_sample_collected IS NOT NULL)) "
		        + "THEN DATE_ADD(pfh.art_start_date, INTERVAL 6 MONTH) "
		        +
		        
		        // Children
		        "WHEN (mp.age <= 18 AND pfh.art_start_date IS NOT NULL) THEN DATE_ADD(pfh.art_start_date, INTERVAL 6 MONTH) "
		        + "WHEN (mp.age <= 18 AND fp.date_vl_sample_collected IS NOT NULL) THEN DATE_ADD(fp.date_vl_sample_collected, INTERVAL 6 MONTH) "
		        +
		        
		        // Pregnant PMTCT
		        "WHEN (fp.client_pmtct = 'Yes' AND fp.date_vl_sample_collected IS NOT NULL) THEN DATE_ADD(fp.date_vl_sample_collected, INTERVAL 3 MONTH) "
		        + "WHEN (fp.client_pmtct = 'Yes' AND fp.date_vl_sample_collected IS NULL) THEN DATE_ADD(fp.encounter_datetime, INTERVAL 3 MONTH) "
		        +
		        
		        // Pregnant general
		        "WHEN (fp.client_pregnant = 'Yes' AND pfh.art_start_date IS NOT NULL) THEN fp.encounter_datetime "
		        +
		        
		        // EAC
		        "WHEN (hvl.third_eac_session_date IS NOT NULL) THEN DATE_ADD(hvl.third_eac_session_date, INTERVAL 1 MONTH) "
		        +
		        
		        // Fallback
		        "WHEN pfh.art_start_date IS NOT NULL THEN DATE_ADD(pfh.art_start_date, INTERVAL 6 MONTH) " +
		        
		        "ELSE NULL END, '%d-%m-%Y') AS eligibility_date " +
		        
		        "FROM ssemr_etl.ssemr_flat_encounter_hiv_care_follow_up fp "
		        + "JOIN ssemr_etl.mamba_dim_person mp ON fp.client_id = mp.person_id "
		        + "LEFT JOIN ssemr_etl.ssemr_flat_encounter_hiv_care_enrolment en ON en.client_id = fp.client_id "
		        + "LEFT JOIN ssemr_etl.ssemr_flat_encounter_personal_family_tx_history pfh ON pfh.client_id = fp.client_id "
		        + "LEFT JOIN ssemr_etl.ssemr_flat_encounter_vl_laboratory_request vlr ON vlr.client_id = fp.client_id "
		        + "LEFT JOIN ssemr_etl.ssemr_flat_encounter_high_viral_load hvl ON hvl.client_id = fp.client_id "
		        + "WHERE pfh.art_start_date IS NOT NULL AND fp.encounter_datetime <= :endDate";
		
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
