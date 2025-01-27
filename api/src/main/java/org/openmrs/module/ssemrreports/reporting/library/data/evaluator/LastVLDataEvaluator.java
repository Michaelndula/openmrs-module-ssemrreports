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
import org.openmrs.module.ssemrreports.reporting.library.data.definition.LastVLDataDefinition;
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
 * Evaluates Last VL Test Date Data Definition
 */
@Handler(supports = LastVLDataDefinition.class, order = 50)
public class LastVLDataEvaluator implements PersonDataEvaluator {
	
	@Autowired
	private EvaluationService evaluationService;
	
	public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context)
	        throws EvaluationException {
		EvaluatedPersonData c = new EvaluatedPersonData(definition, context);
		
		String qry = "SELECT t.client_id, "
		        + "CASE "
		        + "  WHEN t.last_vl_result = 'Viral Load Value' THEN t.last_vl_result_value "
		        + "  WHEN t.last_vl_result = 'Below Detectable (BDL)' THEN 'BDL' "
		        + "  ELSE t.last_vl_result "
		        + "END AS final_vl_result "
		        + "FROM ( "
		        + "  SELECT client_id, "
		        + "         MID(MAX(CONCAT(encounter_datetime, CASE WHEN vl_results IS NOT NULL THEN vl_results ELSE '' END)), 20) AS last_vl_result, "
		        + "         MID(MAX(CONCAT(encounter_datetime, CASE WHEN viral_load_value IS NOT NULL THEN viral_load_value ELSE '' END)), 20) AS last_vl_result_value "
		        + "  FROM ssemr_etl.ssemr_flat_encounter_hiv_care_follow_up "
		        + "  WHERE DATE(encounter_datetime) <= DATE(:endDate) " + "  GROUP BY client_id " + ") AS t;";
		
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
