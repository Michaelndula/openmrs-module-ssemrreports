package org.openmrs.module.ssemrreports.reporting.library.queries;

public class ArtQueries {
	
	public static String getCitizenAndNonCitizensQuery(String response) {
		String sql = "SELECT p.patient_id FROM patient p " + " INNER JOIN person_attribute pa ON p.patient_id=pa.person_id "
		        + " INNER JOIN person_attribute_type pat ON pat.person_attribute_type_id=pa.person_attribute_type_id "
		        + " WHERE p.voided = 0 AND pa.voided = 0 AND pat.retired = 0 "
		        + " AND pat.uuid='60EA1904-2D15-486C-BAEA-2BC78DD498F2' " + " AND pa.value='" + response + "'";
		
		return sql;
	}
	
	public static String getTxNewTotalsAndOnTbWithStatus(String status) {
		return "SELECT d.client_id FROM ("
		        + " SELECT agg.client_id AS client_id FROM ("
		        
		        + "SELECT su1.client_id FROM("
		        
		        + " SELECT client_id FROM ssemr_etl.ssemr_flat_encounter_personal_family_tx_history "
		        + " WHERE art_start_date IS NOT NULL AND art_start_date BETWEEN  :startDate AND :endDate AND location_id=:location "
		        
		        + " UNION "
		        
		        + "SELECT client_id FROM ssemr_etl.ssemr_flat_encounter_adult_and_adolescent_intake "
		        + " WHERE art_start_date IS NOT NULL AND art_start_date BETWEEN :startDate AND :endDate AND location_id=:location "
		        
		        + "UNION "
		        
		        + "SELECT client_id FROM ssemr_etl.ssemr_flat_encounter_pediatric_intake_report "
		        + " WHERE art_start_date IS NOT NULL AND art_start_date BETWEEN :startDate AND :endDate AND location_id=:location "
		        + ")su1"
		        
		        + " WHERE su1.client_id NOT IN("
		        
		        + " SELECT efu.client_id FROM ssemr_etl.ssemr_flat_encounter_end_of_follow_up efu "
		        + " WHERE efu.ltfu ='Yes' "
		        + " AND DATE(efu.ltfu_date) BETWEEN :startDate AND :endDate "
		        + " UNION "
		        
		        + " SELECT ai.client_id FROM ssemr_etl.ssemr_flat_encounter_art_interruption ai "
		        + " WHERE ai.date_of_treatment_interruption IS NOT NULL "
		        + " AND DATE(ai.date_of_treatment_interruption) BETWEEN :startDate AND :endDate "
		        + " UNION "
		        
		        + " SELECT efu.client_id FROM ssemr_etl.ssemr_flat_encounter_end_of_follow_up efu "
		        + " WHERE efu.transfer_out ='Yes' AND efu.transfer_out_date IS NOT NULL "
		        + " AND DATE(efu.transfer_out_date) BETWEEN :startDate AND :endDate "
		        + ") "
		        + ")agg "
		        + ")d "
		        + "INNER JOIN "
		        + "( "
		        + "SELECT f.client_id "
		        + " FROM ssemr_etl.ssemr_flat_encounter_hiv_care_follow_up f "
		        + " WHERE f.tb_status IS NOT NULL AND DATE(f.encounter_datetime) BETWEEN DATE(:startDate) AND DATE(:endDate) "
		        + " AND f.tb_status='" + status + "'" + " )e" + " ON e.client_id=d.client_id";
	}
	
	public static String getTxNewTotalsAndOnTbWithStatusINH() {
		return "SELECT d.client_id FROM ("
		        + " SELECT agg.client_id AS client_id FROM ("
		        
		        + "SELECT su1.client_id FROM("
		        
		        + " SELECT client_id FROM ssemr_etl.ssemr_flat_encounter_personal_family_tx_history "
		        + " WHERE art_start_date IS NOT NULL AND art_start_date BETWEEN  :startDate AND :endDate AND location_id=:location "
		        
		        + " UNION "
		        
		        + "SELECT client_id FROM ssemr_etl.ssemr_flat_encounter_adult_and_adolescent_intake "
		        + " WHERE art_start_date IS NOT NULL AND art_start_date BETWEEN :startDate AND :endDate AND location_id=:location "
		        
		        + "UNION "
		        
		        + "SELECT client_id FROM ssemr_etl.ssemr_flat_encounter_pediatric_intake_report "
		        + " WHERE art_start_date IS NOT NULL AND art_start_date BETWEEN :startDate AND :endDate AND location_id=:location "
		        + ")su1"
		        
		        + " WHERE su1.client_id NOT IN("
		        
		        + " SELECT efu.client_id FROM ssemr_etl.ssemr_flat_encounter_end_of_follow_up efu "
		        + " WHERE efu.ltfu ='Yes' " + " AND DATE(efu.ltfu_date) BETWEEN :startDate AND :endDate " + " UNION "
		        
		        + " SELECT ai.client_id FROM ssemr_etl.ssemr_flat_encounter_art_interruption ai "
		        + " WHERE ai.date_of_treatment_interruption IS NOT NULL "
		        + " AND DATE(ai.date_of_treatment_interruption) BETWEEN :startDate AND :endDate " + " UNION "
		        
		        + " SELECT efu.client_id FROM ssemr_etl.ssemr_flat_encounter_end_of_follow_up efu "
		        + " WHERE efu.transfer_out ='Yes' AND efu.transfer_out_date IS NOT NULL "
		        + " AND DATE(efu.transfer_out_date) BETWEEN :startDate AND :endDate " + ") " + ")agg " + ")d "
		        + "INNER JOIN " + "( " + " SELECT client_id FROM ssemr_etl.ssemr_flat_encounter_hiv_care_follow_up f "
		        + " WHERE f.inh = 'Yes' AND DATE(f.encounter_datetime) BETWEEN DATE(:startDate) AND DATE(:endDate) " + " )e"
		        + " ON e.client_id=d.client_id";
	}
	
	public static String getTxNewTotalsAndOnTbWithStatusTreatment() {
		return "SELECT d.client_id FROM ("
		        + " SELECT agg.client_id AS client_id FROM ("
		        
		        + "SELECT su1.client_id FROM("
		        
		        + " SELECT client_id FROM ssemr_etl.ssemr_flat_encounter_personal_family_tx_history "
		        + " WHERE art_start_date IS NOT NULL AND art_start_date BETWEEN  :startDate AND :endDate AND location_id=:location "
		        
		        + " UNION "
		        
		        + "SELECT client_id FROM ssemr_etl.ssemr_flat_encounter_adult_and_adolescent_intake "
		        + " WHERE art_start_date IS NOT NULL AND art_start_date BETWEEN :startDate AND :endDate AND location_id=:location "
		        
		        + "UNION "
		        
		        + "SELECT client_id FROM ssemr_etl.ssemr_flat_encounter_pediatric_intake_report "
		        + " WHERE art_start_date IS NOT NULL AND art_start_date BETWEEN :startDate AND :endDate AND location_id=:location "
		        + ")su1"
		        
		        + " WHERE su1.client_id NOT IN("
		        
		        + " SELECT efu.client_id FROM ssemr_etl.ssemr_flat_encounter_end_of_follow_up efu "
		        + " WHERE efu.ltfu ='Yes' "
		        + " AND DATE(efu.ltfu_date) BETWEEN :startDate AND :endDate "
		        + " UNION "
		        
		        + " SELECT ai.client_id FROM ssemr_etl.ssemr_flat_encounter_art_interruption ai "
		        + " WHERE ai.date_of_treatment_interruption IS NOT NULL "
		        + " AND DATE(ai.date_of_treatment_interruption) BETWEEN :startDate AND :endDate "
		        + " UNION "
		        
		        + " SELECT efu.client_id FROM ssemr_etl.ssemr_flat_encounter_end_of_follow_up efu "
		        + " WHERE efu.transfer_out ='Yes' AND efu.transfer_out_date IS NOT NULL "
		        + " AND DATE(efu.transfer_out_date) BETWEEN :startDate AND :endDate "
		        + ") "
		        + ")agg "
		        + ")d "
		        + "INNER JOIN "
		        + "( SELECT client_id FROM ssemr_etl.ssemr_flat_encounter_hiv_care_follow_up f "
		        + " WHERE f.on_tb_treatment = 'Yes' AND DATE(f.encounter_datetime) BETWEEN date(:startDate) AND date(:endDate)  "
		        + " )e" + " ON e.client_id=d.client_id";
	}
}
