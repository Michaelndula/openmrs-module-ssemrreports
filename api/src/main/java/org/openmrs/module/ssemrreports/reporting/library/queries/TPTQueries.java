package org.openmrs.module.ssemrreports.reporting.library.queries;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class TPTQueries {
	
	public static String getPatientsCompletedTPT() {
		String query = "SELECT client_id FROM ssemr_etl.ssemr_flat_encounter_hiv_care_follow_up e "
		        + "WHERE is_tpt_finished = 'Yes' " + "AND date_tpt_completed IS NOT NULL " + "AND encounter_datetime = ( "
		        + "   SELECT MAX(encounter_datetime) " + "   FROM ssemr_etl.ssemr_flat_encounter_hiv_care_follow_up "
		        + "   WHERE client_id = e.client_id) " + "AND encounter_datetime BETWEEN :startDate AND :endDate "
		        + "GROUP BY client_id";
		
		return query;
	}
	
	public static String getPatientsTakingTPT() {
		String query = "SELECT client_id FROM ssemr_etl.ssemr_flat_encounter_hiv_care_follow_up e "
		        + "WHERE tpt_given = 'Yes' " + "AND encounter_datetime = ( " + "   SELECT MAX(encounter_datetime) "
		        + "   FROM ssemr_etl.ssemr_flat_encounter_hiv_care_follow_up " + "   WHERE client_id = e.client_id) "
		        + "AND encounter_datetime BETWEEN :startDate AND :endDate " + "GROUP BY client_id";
		
		return query;
	}
	
	public static String getPatientsEligibleForTPT() {
		String query = "SELECT client_id FROM ssemr_etl.ssemr_flat_encounter_hiv_care_follow_up e "
		        + "WHERE encounter_datetime = ( " + "    SELECT MAX(encounter_datetime) "
		        + "    FROM ssemr_etl.ssemr_flat_encounter_hiv_care_follow_up " + "    WHERE client_id = e.client_id "
		        + ") " + "AND encounter_datetime BETWEEN :startDate AND :endDate " + "AND ( "
		        + "    (eligible_for_tpt = 'Yes' AND date_eligible_for_tpt IS NOT NULL) " + "    OR "
		        + "    (tpt_given = 'Yes') " + "    OR"
		        + "    (is_tpt_finished = 'Yes' AND date_tpt_completed IS NOT NULL )" + ") " + "GROUP BY client_id";
		
		return query;
	}
	
}
