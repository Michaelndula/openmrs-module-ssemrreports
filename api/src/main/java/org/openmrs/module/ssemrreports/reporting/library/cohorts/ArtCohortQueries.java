package org.openmrs.module.ssemrreports.reporting.library.cohorts;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Location;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.ssemrreports.reporting.library.queries.ArtQueries;
import org.openmrs.module.ssemrreports.reporting.library.queries.CommonQueries;
import org.openmrs.module.ssemrreports.reporting.library.queries.MerQueries;
import org.openmrs.module.ssemrreports.reporting.utils.SsemrReportUtils;
import org.openmrs.module.ssemrreports.reporting.utils.constants.reports.art.ArtReportsConstants;
import org.springframework.stereotype.Component;

@Component
public class ArtCohortQueries {
	
	private final MerCohortQueries merCohortQueries;
	
	public ArtCohortQueries(MerCohortQueries merCohortQueries) {
		this.merCohortQueries = merCohortQueries;
	}
	
	/**
	 * Cumulative number of patients ever started on ART at this facility at the end of the previous
	 * reporting period end of previous reporting period = start date - 1 day
	 * 
	 * @return
	 */
	public CohortDefinition getCumulativeEverOnARTAtThisFacilityInPreviousReportingPeriodCohortDefinition() {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		String sql = "SELECT su1.client_id FROM("
		        
		        + "SELECT client_id FROM ssemr_etl.ssemr_flat_encounter_personal_family_tx_history "
		        + " WHERE art_start_date IS NOT NULL AND art_start_date <= DATE_ADD(:startDate, INTERVAL -1 DAY) AND location_id=:location "
		        
		        + "UNION "
		        
		        + "SELECT client_id FROM ssemr_etl.ssemr_flat_encounter_adult_and_adolescent_intake "
		        + " WHERE art_start_date IS NOT NULL AND art_start_date <=DATE_ADD(:startDate, INTERVAL -1 DAY) AND location_id=:location "
		        
		        + "UNION "
		        
		        + "SELECT client_id FROM ssemr_etl.ssemr_flat_encounter_pediatric_intake_report "
		        + " WHERE art_start_date IS NOT NULL AND art_start_date <=DATE_ADD(:startDate, INTERVAL -1 DAY) AND location_id=:location "
		        + ")su1";
		cd.setQuery(sql);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.setDescription("Cumulative patients started on ART at the end of the previous reporting period");
		
		CompositionCohortDefinition comp = new CompositionCohortDefinition();
		comp.setName("Cummulative without IIT");
		comp.addParameter(new Parameter("startDate", "Start Date", Date.class));
		comp.addParameter(new Parameter("endDate", "End Date", Date.class));
		comp.addParameter(new Parameter("location", "Location", Location.class));
		
		comp.addSearch("CUMM", SsemrReportUtils.map(cd, "startDate=${startDate},endDate=${endDate},location=${location}"));
		comp.addSearch("IIT",
		    SsemrReportUtils.map(merCohortQueries.getIITPatientsOverPeriod(), "endDate=${endDate},location=${location}"));
		
		comp.setCompositionString("CUMM AND NOT IIT");
		return comp;
	}
	
	/**
	 * Cumulative number of patients ever started on ART at this facility at the end of the
	 * reporting period, reporting period = endDate
	 * 
	 * @return
	 */
	public CohortDefinition getCumulativeEverOnARTAtThisFacilityAtEndOfReportingCohortDefinition() {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		String sql = "SELECT su1.client_id FROM("
		
		+ "SELECT client_id FROM ssemr_etl.ssemr_flat_encounter_personal_family_tx_history "
		        + " WHERE art_start_date IS NOT NULL AND art_start_date <=:endDate AND location_id=:location "
		        
		        + "UNION "
		        
		        + "SELECT client_id FROM ssemr_etl.ssemr_flat_encounter_adult_and_adolescent_intake "
		        + " WHERE art_start_date IS NOT NULL AND art_start_date <=:endDate AND location_id=:location "
		        
		        + "UNION "
		        
		        + "SELECT client_id FROM ssemr_etl.ssemr_flat_encounter_pediatric_intake_report "
		        + " WHERE art_start_date IS NOT NULL AND art_start_date <=:endDate AND location_id=:location " + ")su1";
		cd.setQuery(sql);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.setDescription("Cumulative patients started on ART at the end of the reporting period");
		
		return cd;
	}
	
	/**
	 * New persons started on ART at this facility during the reporting period
	 * 
	 * @return
	 */
	public CohortDefinition getNewOnARTCohortDefinition() {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		cd.setQuery(MerQueries.getTxNewTotals());
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.setDescription("New persons started on ART during the reporting period");
		
		return cd;
	}
	
	/**
	 * New persons started on ART at this facility during the reporting period And are on CTX
	 * 
	 * @return
	 */
	public CohortDefinition getNewOnARTonCTXCohortDefinition() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("New patients who are on CTX");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.addSearch("NEW", SsemrReportUtils.map(getNewOnARTCohortDefinition(),
		    "startDate=${startDate},endDate=${endDate},location=${location}"));
		cd.addSearch("CTX", SsemrReportUtils.map(patientsOnCTXTreatmentCohortDefinition(),
		    "startDate=${startDate},endDate=${endDate},location=${location}"));
		cd.setCompositionString("NEW AND CTX");
		return cd;
	}
	
	/**
	 * New persons started on ART at this facility during the reporting period And are on Dapsone
	 * 
	 * @return
	 */
	public CohortDefinition getNewOnARTonDapsoneCohortDefinition() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("New patients who are on Dapsone");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.addSearch("NEW", SsemrReportUtils.map(getNewOnARTCohortDefinition(),
		    "startDate=${startDate},endDate=${endDate},location=${location}"));
		cd.addSearch("DAP", SsemrReportUtils.map(patientsOnDapsoneTreatmentCohortDefinition(),
		    "startDate=${startDate},endDate=${endDate},location=${location}"));
		cd.setCompositionString("NEW AND DAP");
		return cd;
	}
	
	/**
	 * Women who are pregnant during the reporting period This is currently computed using the edd
	 * variable in the followup visit form One is pregnant if edd variable has value and is on or
	 * after the reporting end date
	 * 
	 * @return
	 */
	public CohortDefinition getPregnantWomenCohortDefinition() {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		cd.setQuery(MerQueries.getPregnantQueries());
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.setDescription("Pregnant women during the reporting period");
		
		return cd;
	}
	
	public CohortDefinition getBreastfeedingWomenCohortDefinition() {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		cd.setQuery(MerQueries.getBreastfeedingQueries());
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.setDescription("Breastfeeding women during the reporting period");
		
		return cd;
	}
	
	public CohortDefinition getPatientsOnTLDCohortDefinition() {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		
		String qry = "SELECT " + " fu.client_id " + " FROM " + " ssemr_etl.ssemr_flat_encounter_hiv_care_follow_up fu "
		        + " WHERE fu.encounter_datetime = ( " + " SELECT " + " MAX(f2.encounter_datetime) " + " FROM "
		        + " ssemr_etl.ssemr_flat_encounter_hiv_care_follow_up f2 " + " WHERE " + " f2.client_id = fu.client_id "
		        + " AND f2.encounter_datetime BETWEEN :startDate AND :endDate " + " ) "
		        + " AND TRIM(SUBSTRING_INDEX(fu.art_regimen, '=', -1)) = 'TDF/3TC/DTG' ";
		
		cd.setQuery(qry);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.setDescription("Patients on TLD during the reporting period");
		return cd;
	}
	
	public CohortDefinition getPatientsOnDTGRegimenCohortDefinition() {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		String qry =
		
		"SELECT "
		        + " fu.client_id "
		        + " FROM "
		        + " ssemr_etl.ssemr_flat_encounter_hiv_care_follow_up fu "
		        + " WHERE "
		        + " fu.encounter_datetime = ( "
		        + " SELECT "
		        + " MAX(f2.encounter_datetime) "
		        + " FROM "
		        + " ssemr_etl.ssemr_flat_encounter_hiv_care_follow_up f2 "
		        + " WHERE "
		        + " f2.client_id = fu.client_id "
		        + " AND f2.encounter_datetime BETWEEN :startDate AND :endDate "
		        + " ) "
		        + " AND TRIM(SUBSTRING_INDEX(fu.art_regimen, '=', -1)) NOT IN('TDF/3TC/DTG') AND TRIM(SUBSTRING_INDEX(fu.art_regimen, '=', -1) like '%DTG%') ";
		
		cd.setQuery(qry);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.setDescription("Patients on DTG related regimen during the reporting period");
		return cd;
	}
	
	public CohortDefinition getPatientsOnRegimenCohortDefinition(List<String> regimenList) {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		String qry = "select " + "    e.client_id " + "from ssemr_etl.ssemr_flat_encounter_hiv_care_enrolment e "
				+ "inner join ssemr_etl.ssemr_flat_encounter_hiv_care_follow_up f using(client_id) "
				+ "where date(f.encounter_datetime) between date(:startDate) and date(:endDate)  "
				+ "  and f.art_regimen in (:artRegimen) ";

		List<String> newList = new ArrayList<>();
		for (int i =0 ; i < regimenList.size(); i++) {
			newList.add("'" + regimenList.get(i) + "'"); // pad the string with "'" for use in sql query
		}
		String regimenString = "";
		if (!newList.isEmpty()) {
			regimenString = StringUtils.join(newList, ",");
		}

		qry.replace(":artRegimen", regimenString);

		cd.setQuery(qry);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Patients on related regimen during the reporting period");
		return cd;
	}
	
	public CohortDefinition getPatientsOnRegimenCohortDefinition(String regimenName) {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		String qry = "SELECT f.client_id "
		        + " FROM ssemr_etl.ssemr_flat_encounter_hiv_care_follow_up f "
		        + " WHERE f.location_id=:location and f.art_regimen IS NOT NULL AND DATE(f.encounter_datetime) <= DATE(:endDate) "
		        + " GROUP BY f.client_id "
		        + " HAVING REPLACE(MID(MAX(CONCAT(f.encounter_datetime, f.art_regimen)),20),' ','') = REPLACE(':artRegimen',' ','') ";
		
		qry = qry.replace(":artRegimen", regimenName);
		cd.setQuery(qry);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.setDescription("Patients on a regimen during the reporting period");
		return cd;
	}
	
	public CohortDefinition getPatientsOnFirstLineRegimenCohortDefinition() {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		
		String regimensString = SsemrReportUtils.concatenateStringAndQuote(ArtReportsConstants.adultFirstLineRegimen)
		        + SsemrReportUtils.concatenateStringAndQuote(ArtReportsConstants.childFirstLineRegimen);
		String qry = "SELECT client_id FROM ( " + " SELECT "
		        + " f.client_id , mid(max(CONCAT(f.encounter_datetime,f.art_regimen)),20) AS art_regimen "
		        + " FROM ssemr_etl.ssemr_flat_encounter_hiv_care_follow_up f "
		        + " WHERE f.location_id=:location AND DATE(f.encounter_datetime) <= DATE(:endDate)   "
		        + " GROUP BY client_id  " + ") c where REPLACE(c.art_regimen,' ','')  " + " IN ( " + regimensString + " ) ";
		cd.setQuery(qry);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.setDescription("Patients on a regimen during the reporting period");
		return cd;
	}
	
	public CohortDefinition getPatientsOnSecondLineRegimenCohortDefinition() {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		String regimensString = SsemrReportUtils.concatenateStringAndQuote(ArtReportsConstants.adultSecondLineRegimen)
		        + SsemrReportUtils.concatenateStringAndQuote(ArtReportsConstants.childSecondLineRegimen);
		
		String qry = "SELECT client_id from ( "
		        + " SELECT f.client_id , MID(MAX(CONCAT(f.encounter_datetime,f.art_regimen)),20) AS art_regimen "
		        + "  FROM ssemr_etl.ssemr_flat_encounter_hiv_care_follow_up f "
		        + "  WHERE f.location_id=:location AND DATE(f.encounter_datetime) <= DATE(:endDate)   "
		        + "  GROUP BY client_id  " + ") c WHERE REPLACE(c.art_regimen,' ','') in ( " + regimensString + " ) ; ";
		
		cd.setQuery(qry);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.setDescription("Patients on a second line regimen during the reporting period");
		return cd;
	}
	
	public CohortDefinition getAgeAtStartOfART(int minAge, int maxAge, String sex) {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		
		String query = "SELECT DISTINCT(client_id)"
		        + " FROM ( "
		        + " SELECT f.encounter_datetime, f.client_id, p.gender, timestampdiff(YEAR, p.birthdate, e.art_start_date) ageAtArtStart, fup.ltfu_date, f.art_regimen_no_of_days_dispensed, "
		        + " DATE_ADD(f.encounter_datetime, interval (case f.art_regimen_no_of_days_dispensed when '30 Days' then 30 when '60 Days' then 60 when '90 Days' then 90 when '180 Days' then 180 when '180+ Days' then 180 when '150 Days' then 150 else 0 end) DAY) nextDrugApptDate "
		        + " FROM("
		        
		        + " SELECT client_id,art_start_date,location_id FROM ssemr_etl.ssemr_flat_encounter_personal_family_tx_history "
		        + " WHERE art_start_date IS NOT NULL AND art_start_date <=:endDate AND location_id=:location "
		        
		        + " UNION "
		        
		        + " SELECT client_id,art_start_date,location_id FROM ssemr_etl.ssemr_flat_encounter_adult_and_adolescent_intake "
		        + " WHERE art_start_date IS NOT NULL AND art_start_date <=:endDate AND location_id=:location "
		        
		        + " UNION "
		        
		        + " SELECT client_id,art_start_date,location_id FROM ssemr_etl.ssemr_flat_encounter_pediatric_intake_report "
		        + " WHERE art_start_date IS NOT NULL AND art_start_date <=:endDate AND location_id=:location "
		        + " )e"
		        + " INNER JOIN ssemr_etl.mamba_dim_person p on p.person_id = e.client_id "
		        + " INNER JOIN ssemr_etl.ssemr_flat_encounter_hiv_care_follow_up f on f.client_id = e.client_id "
		        + " LEFT JOIN ssemr_etl.ssemr_flat_encounter_end_of_follow_up fup on e.client_id = fup.client_id "
		        + " WHERE e.location_id=:location AND (fup.date_of_death IS NULL OR fup.date_of_death > DATE(:endDate)) "
		        + " AND (fup.ltfu_date is null or fup.ltfu_date NOT BETWEEN DATE(:startDate) AND date(:endDate)) "
		        + " GROUP BY e.client_id, f.encounter_datetime, p.gender, ageAtArtStart,fup.ltfu_date,f.art_regimen_no_of_days_dispensed, nextDrugApptDate "
		        + " HAVING DATE_ADD(nextDrugApptDate, INTERVAL 30 DAY) > DATE(:endDate)) a "
		        + " WHERE  ageAtArtStart BETWEEN " + minAge + "  and " + maxAge + " and gender = '" + sex + "'";
		cd.setQuery(query);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.setDescription("Patients of a given age group and sex");
		return cd;
	}
	
	public CohortDefinition getAgeAtStartOfARTonFirstLine(int minAge, int maxAge, String sex) {
		
		String regimensString = SsemrReportUtils.concatenateStringAndQuote(ArtReportsConstants.adultFirstLineRegimen)
		        + SsemrReportUtils.concatenateStringAndQuote(ArtReportsConstants.childFirstLineRegimen);
		SqlCohortDefinition cd = new SqlCohortDefinition();
		
		String query = "SELECT b.client_id FROM( "
		        + " SELECT DISTINCT(client_id)"
		        + " FROM ( "
		        + " SELECT f.encounter_datetime, f.client_id, p.gender, timestampdiff(YEAR, p.birthdate, e.art_start_date) ageAtArtStart, fup.ltfu_date, f.art_regimen_no_of_days_dispensed, "
		        + " DATE_ADD(f.encounter_datetime, interval (case f.art_regimen_no_of_days_dispensed when '30 Days' then 30 when '60 Days' then 60 when '90 Days' then 90 when '180 Days' then 180 when '180+ Days' then 180 when '150 Days' then 150 else 0 end) DAY) nextDrugApptDate "
		        + " FROM("
		        
		        + " SELECT client_id,art_start_date,location_id FROM ssemr_etl.ssemr_flat_encounter_personal_family_tx_history "
		        + " WHERE art_start_date IS NOT NULL AND art_start_date <=:endDate AND location_id=:location "
		        
		        + " UNION "
		        
		        + " SELECT client_id,art_start_date,location_id FROM ssemr_etl.ssemr_flat_encounter_adult_and_adolescent_intake "
		        + " WHERE art_start_date IS NOT NULL AND art_start_date <=:endDate AND location_id=:location "
		        
		        + " UNION "
		        
		        + " SELECT client_id,art_start_date,location_id FROM ssemr_etl.ssemr_flat_encounter_pediatric_intake_report "
		        + " WHERE art_start_date IS NOT NULL AND art_start_date <=:endDate AND location_id=:location "
		        + " )e"
		        + " INNER JOIN ssemr_etl.mamba_dim_person p on p.person_id = e.client_id "
		        + " INNER JOIN ssemr_etl.ssemr_flat_encounter_hiv_care_follow_up f on f.client_id = e.client_id "
		        + " LEFT JOIN ssemr_etl.ssemr_flat_encounter_end_of_follow_up fup on e.client_id = fup.client_id "
		        + " WHERE e.location_id=:location AND (fup.date_of_death IS NULL OR fup.date_of_death > DATE(:endDate)) "
		        + " AND (fup.ltfu_date is null or fup.ltfu_date NOT BETWEEN DATE(:startDate) AND date(:endDate)) "
		        + " GROUP BY e.client_id, f.encounter_datetime, p.gender, ageAtArtStart,fup.ltfu_date,f.art_regimen_no_of_days_dispensed, nextDrugApptDate "
		        + " HAVING DATE_ADD(nextDrugApptDate, INTERVAL 30 DAY) > DATE(:endDate)) a "
		        + " WHERE  ageAtArtStart BETWEEN " + minAge + "  and " + maxAge + " and gender = '" + sex + "'" + " )b"
		        + " INNER JOIN "
		        
		        + "(SELECT client_id FROM ( " + " SELECT "
		        + " f.client_id , mid(max(CONCAT(f.encounter_datetime,f.art_regimen)),20) AS art_regimen "
		        + " FROM ssemr_etl.ssemr_flat_encounter_hiv_care_follow_up f "
		        + " WHERE f.location_id=:location AND DATE(f.encounter_datetime) <= DATE(:endDate)   "
		        + " GROUP BY client_id  " + ") c where REPLACE(c.art_regimen,' ','')  " + " IN ( " + regimensString
		        + " ))c " + " ON b.client_id=c.client_id";
		cd.setQuery(query);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.setDescription("Patients of a given age group and sex");
		return cd;
	}
	
	public CohortDefinition getAgeAtStartOfARTonSecondLine(int minAge, int maxAge, String sex) {
		
		String regimensString = SsemrReportUtils.concatenateStringAndQuote(ArtReportsConstants.adultSecondLineRegimen)
		        + SsemrReportUtils.concatenateStringAndQuote(ArtReportsConstants.childSecondLineRegimen);
		SqlCohortDefinition cd = new SqlCohortDefinition();
		
		String query = "SELECT b.client_id FROM( "
		        + " SELECT DISTINCT(client_id)"
		        + " FROM ( "
		        + " SELECT f.encounter_datetime, f.client_id, p.gender, timestampdiff(YEAR, p.birthdate, e.art_start_date) ageAtArtStart, fup.ltfu_date, f.art_regimen_no_of_days_dispensed, "
		        + " DATE_ADD(f.encounter_datetime, interval (case f.art_regimen_no_of_days_dispensed when '30 Days' then 30 when '60 Days' then 60 when '90 Days' then 90 when '180 Days' then 180 when '180+ Days' then 180 when '150 Days' then 150 else 0 end) DAY) nextDrugApptDate "
		        + " FROM("
		        
		        + " SELECT client_id,art_start_date,location_id FROM ssemr_etl.ssemr_flat_encounter_personal_family_tx_history "
		        + " WHERE art_start_date IS NOT NULL AND art_start_date <=:endDate AND location_id=:location "
		        
		        + " UNION "
		        
		        + " SELECT client_id,art_start_date,location_id FROM ssemr_etl.ssemr_flat_encounter_adult_and_adolescent_intake "
		        + " WHERE art_start_date IS NOT NULL AND art_start_date <=:endDate AND location_id=:location "
		        
		        + " UNION "
		        
		        + " SELECT client_id,art_start_date,location_id FROM ssemr_etl.ssemr_flat_encounter_pediatric_intake_report "
		        + " WHERE art_start_date IS NOT NULL AND art_start_date <=:endDate AND location_id=:location "
		        + " )e"
		        + " INNER JOIN ssemr_etl.mamba_dim_person p on p.person_id = e.client_id "
		        + " INNER JOIN ssemr_etl.ssemr_flat_encounter_hiv_care_follow_up f on f.client_id = e.client_id "
		        + " LEFT JOIN ssemr_etl.ssemr_flat_encounter_end_of_follow_up fup on e.client_id = fup.client_id "
		        + " WHERE e.location_id=:location AND (fup.date_of_death IS NULL OR fup.date_of_death > DATE(:endDate)) "
		        + " AND (fup.ltfu_date is null or fup.ltfu_date NOT BETWEEN DATE(:startDate) AND date(:endDate)) "
		        + " GROUP BY e.client_id, f.encounter_datetime, p.gender, ageAtArtStart,fup.ltfu_date,f.art_regimen_no_of_days_dispensed, nextDrugApptDate "
		        + " HAVING DATE_ADD(nextDrugApptDate, INTERVAL 30 DAY) > DATE(:endDate)) a "
		        + " WHERE  ageAtArtStart BETWEEN " + minAge + "  and " + maxAge + " and gender = '" + sex + "'" + " )b"
		        + " INNER JOIN "
		        
		        + "(SELECT client_id FROM ( " + " SELECT "
		        + " f.client_id , mid(max(CONCAT(f.encounter_datetime,f.art_regimen)),20) AS art_regimen "
		        + " FROM ssemr_etl.ssemr_flat_encounter_hiv_care_follow_up f "
		        + " WHERE f.location_id=:location AND DATE(f.encounter_datetime) <= DATE(:endDate)   "
		        + " GROUP BY client_id  " + ") c where REPLACE(c.art_regimen,' ','')  " + " IN ( " + regimensString
		        + " ))c " + " ON b.client_id=c.client_id";
		cd.setQuery(query);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.setDescription("Patients of a given age group and sex");
		return cd;
	}
	
	public CohortDefinition getPregnantPatientsOnRegimenCohortDefinition(String regimenName) {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		String qry = "SELECT "
		        + " f.client_id "
		        + " FROM ssemr_etl.ssemr_flat_encounter_hiv_care_follow_up f "
		        + " WHERE f.location_id=:location AND DATE(f.encounter_datetime) BETWEEN DATE(:startDate) AND DATE(:endDate) "
		        + " AND f.art_regimen = ':artRegimen' and f.client_pregnant = 'Yes' ";
		
		qry.replace(":artRegimen", regimenName);
		cd.setQuery(qry);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.setDescription("Pregnant patients on a regimen during the reporting period");
		return cd;
	}
	
	public CohortDefinition getBreastFeedingPatientsOnRegimenCohortDefinition(String regimenName) {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		String qry = "SELECT "
		        + " f.client_id "
		        + " FROM ssemr_etl.ssemr_flat_encounter_hiv_care_follow_up f "
		        + " WHERE DATE(f.encounter_datetime) BETWEEN DATE(:startDate) AND DATE(:endDate) "
		        + " AND f.location_id=:location AND f.art_regimen = ':artRegimen' AND (f.client_breastfeeding IS NOT NULL AND f.client_breastfeeding = 'Yes')";
		
		qry.replace(":artRegimen", regimenName);
		cd.setQuery(qry);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.setDescription("Breastfeeding patients on a regimen during the reporting period");
		return cd;
	}
	
	public CohortDefinition getTBAssessmentStatusCohortDefinition(String assessmentStatus) {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		String qry = "select\n"
		        + "    e.client_id\n"
		        + "from ssemr_etl.ssemr_flat_encounter_hiv_care_enrolment e\n"
		        + "inner join ssemr_etl.ssemr_flat_encounter_hiv_care_follow_up f using(client_id)\n"
		        + "where date(f.encounter_datetime) between date(:startDate) and date(:endDate) \n"
		        + "  and f.art_regimen = ':assessmentStatus' and (f.client_breastfeeding is not null and f.client_breastfeeding = 'Yes')";
		
		qry.replace(":assessmentStatus", assessmentStatus);
		cd.setQuery(qry);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Patients assessed for TB during the reporting period");
		return cd;
	}
	
	public CohortDefinition patientsCurrentlyOnTBCohortDefinition() {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		String qry = "select\n"
		        + "    e.client_id\n"
		        + "from ssemr_etl.ssemr_flat_encounter_hiv_care_enrolment e\n"
		        + "inner join ssemr_etl.ssemr_flat_encounter_hiv_care_follow_up f using(client_id)\n"
		        + " where f.on_tb_treatment = 'Yes' and date(f.encounter_datetime) between date(:startDate) and date(:endDate)\n"
		        + " group by f.client_id;";
		
		cd.setQuery(qry);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.setDescription("Patients treated for TB during the reporting period");
		return cd;
	}
	
	// ----- --- viral load sample collection
	
	public CohortDefinition getVLSampleCollectionCohortDefinition() {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		String qry = "SELECT client_id "
		        + "FROM ssemr_etl.ssemr_flat_encounter_vl_laboratory_request "
		        + "WHERE location_id=:location AND DATE(date_of_sample_collection) BETWEEN DATE(:startDate) AND DATE(:endDate) ";
		
		cd.setQuery(qry);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.setDescription("VL sample collected during the reporting period");
		return cd;
	}
	
	public CohortDefinition getVLSampleCollectionForPregnantCohortDefinition() {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		String qry = "SELECT client_id " + "FROM ssemr_etl.ssemr_flat_encounter_vl_laboratory_request "
		        + "WHERE DATE(date_of_sample_collection) BETWEEN DATE(:startDate) AND DATE(:endDate) "
		        + "AND location_id=:location AND patient_pregnant = 'Yes' ";
		
		cd.setQuery(qry);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.setDescription("VL sample collected for the pregnant during the reporting period");
		return cd;
	}
	
	public CohortDefinition getVLSampleCollectionForBreastfeedingCohortDefinition() {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		String qry = "SELECT client_id " + "FROM ssemr_etl.ssemr_flat_encounter_vl_laboratory_request "
		        + "WHERE DATE(date_of_sample_collection) BETWEEN DATE(:startDate) AND DATE(:endDate) "
		        + "AND location_id=:location AND patient_breastfeeding = 'Yes' ";
		
		cd.setQuery(qry);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.setDescription("VL sample collected for the breastfeeding during the reporting period");
		return cd;
	}
	
	public CohortDefinition patientsNewlyInitiatedOnTBTreatmentCohortDefinition() {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		
		cd.setQuery(ArtQueries.getTxNewTotalsAndOnTbWithStatusTreatment());
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.setDescription("Patients newly initiated on TB treatment during the reporting period");
		return cd;
	}
	
	public CohortDefinition patientsOnArtWithTbStatusWithStatusCohortDefinition(String status) {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		
		cd.setQuery(ArtQueries.getTxNewTotalsAndOnTbWithStatus(status));
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.setDescription("Patients with TB status recorded by end of reporting period with TB status recorded");
		return cd;
	}
	
	public CohortDefinition patientsOnINHTreatmentCohortDefinition() {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		
		cd.setQuery(ArtQueries.getTxNewTotalsAndOnTbWithStatusINH());
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.setDescription("New Patients on INH during the reporting period");
		return cd;
	}
	
	public CohortDefinition getNewOnArtWithTbStatusCohortDefinition(String status) {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("New patients who are on ART with TB status");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.addSearch("NEWTBSTATUS", SsemrReportUtils.map(patientsOnArtWithTbStatusWithStatusCohortDefinition(status),
		    "startDate=${startDate},endDate=${endDate},location=${location}"));
		cd.setCompositionString("NEWTBSTATUS");
		return cd;
	}
	
	public CohortDefinition getNewOnArtAndOnTbTreatmentCohortDefinition() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("New patients who are on ART and on TB treatment");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.addSearch("NEW", SsemrReportUtils.map(getNewOnARTCohortDefinition(),
		    "startDate=${startDate},endDate=${endDate},location=${location}"));
		cd.addSearch("NEWANDTxR", SsemrReportUtils.map(patientsNewlyInitiatedOnTBTreatmentCohortDefinition(),
		    "startDate=${startDate},endDate=${endDate},location=${location}"));
		cd.setCompositionString("NEWANDTxR");
		return cd;
	}
	
	public CohortDefinition getNewOnArtAndOnInhCohortDefinition() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("New patients who are on ART and on INH");
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.addSearch("NEW", SsemrReportUtils.map(getNewOnARTCohortDefinition(),
		    "startDate=${startDate},endDate=${endDate},location=${location}"));
		cd.addSearch("NEWANDINH", SsemrReportUtils.map(patientsOnINHTreatmentCohortDefinition(),
		    "startDate=${startDate},endDate=${endDate},location=${location}"));
		cd.setCompositionString("NEWANDINH");
		return cd;
	}
	
	public CohortDefinition patientsOnCTXTreatmentCohortDefinition() {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		String qry = "SELECT su1.client_id FROM( "
		        + " SELECT fu.client_id,MAX(fu.encounter_datetime) AS encounter_datetime FROM ssemr_etl.ssemr_flat_encounter_hiv_care_follow_up fu "
		        + " WHERE fu.on_ctx IS NOT NULL AND DATE(fu.encounter_datetime) BETWEEN :startDate AND :endDate "
		        + "GROUP BY fu.client_id " + ")su1" + " INNER JOIN "
		        + " ssemr_etl.ssemr_flat_encounter_hiv_care_follow_up fu1 ON su1.client_id=fu1.client_id "
		        + " WHERE fu1.on_ctx='Yes' AND su1.encounter_datetime=fu1.encounter_datetime ";
		cd.setQuery(qry);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.setDescription("Patients on CTX during the reporting period");
		return cd;
	}
	
	public CohortDefinition patientsOnDapsoneTreatmentCohortDefinition() {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		String qry = "SELECT su1.client_id FROM( "
		        + " SELECT fu.client_id,MAX(fu.encounter_datetime) AS encounter_datetime FROM ssemr_etl.ssemr_flat_encounter_hiv_care_follow_up fu "
		        + " WHERE fu.on_dapsone IS NOT NULL AND DATE(fu.encounter_datetime) BETWEEN :startDate AND :endDate "
		        + "GROUP BY fu.client_id " + ")su1" + " INNER JOIN "
		        + " ssemr_etl.ssemr_flat_encounter_hiv_care_follow_up fu1 ON su1.client_id=fu1.client_id "
		        + " WHERE fu1.on_dapsone='Yes' AND su1.encounter_datetime=fu1.encounter_datetime ";
		cd.setQuery(qry);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.setDescription("Patients on Dapsone during the reporting period");
		return cd;
	}
	
	public CohortDefinition patientsLtfuCohortDefinition() {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		String qry = "SELECT q2.client_id FROM ( "
		        + " SELECT q1.client_id,q1.follow_up_date,t2.art_regimen_no_of_days_dispensed FROM ( "
		        + " SELECT t1.client_id AS client_id, MAX(t1.encounter_datetime) AS follow_up_date FROM ssemr_etl.ssemr_flat_encounter_hiv_care_follow_up t1 "
		        + " WHERE t1.encounter_datetime <= :endDate AND t1.location_id=:location "
		        + " GROUP BY t1.client_id) q1 "
		        + " INNER JOIN ssemr_etl.ssemr_flat_encounter_hiv_care_follow_up t2 ON q1.client_id=t2.client_id "
		        + " WHERE q1.follow_up_date = t2.encounter_datetime "
		        + " AND DATE(DATE_ADD(DATE_ADD(q1.follow_up_date, interval CAST(t2.art_regimen_no_of_days_dispensed AS UNSIGNED) DAY), INTERVAL 28 DAY)) <= :endDate)q2";
		
		cd.setQuery(qry);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.setDescription("Patients on ltfu during the reporting period");
		return cd;
	}
	
	public CohortDefinition patientsDeadCohortDefinition() {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		String qry = "SELECT f.client_id " + "FROM ssemr_etl.ssemr_flat_encounter_end_of_follow_up f "
		        + "WHERE f.location_id=:location AND DATE(f.date_of_death) BETWEEN DATE(:startDate) AND DATE(:endDate) ";
		
		cd.setQuery(qry);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.setDescription("Patients who died during the reporting period");
		return cd;
	}
	
	// ----- --- viral load results
	
	public CohortDefinition getVLResultsCohortDefinition(int minVal, int maxVal) {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		String qry = "SELECT client_id "
		        + "FROM ssemr_etl.ssemr_flat_encounter_vl_laboratory_request "
		        + "WHERE DATE(date_results_dispatched) BETWEEN DATE(:startDate) AND DATE(:endDate) AND location_id=:location AND value BETWEEN "
		        + minVal + " AND " + maxVal;
		
		cd.setQuery(qry);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.setDescription("VL results received during the reporting period");
		return cd;
	}
	
	public CohortDefinition getVLResultsForPregnantCohortDefinition(int minVal, int maxVal) {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		String qry = "SELECT client_id " + "FROM ssemr_etl.ssemr_flat_encounter_vl_laboratory_request "
		        + "WHERE date(date_results_dispatched) between date(:startDate) and date(:endDate) "
		        + "AND location_id=:location AND patient_pregnant = 'Yes' AND value BETWEEN " + minVal + " AND " + maxVal;
		
		cd.setQuery(qry);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.setDescription("VL results for the pregnant received during the reporting period");
		return cd;
	}
	
	public CohortDefinition getVLResultsForBreastfeedingCohortDefinition(int minVal, int maxVal) {
		SqlCohortDefinition cd = new SqlCohortDefinition();
		String qry = "SELECT client_id "
		        + "FROM ssemr_etl.ssemr_flat_encounter_vl_laboratory_request "
		        + "WHERE location_id=:location AND DATE(date_results_dispatched) BETWEEN DATE(:startDate) AND DATE(:endDate) "
		        + "AND patient_breastfeeding = 'Yes' AND value BETWEEN " + minVal + " AND " + maxVal;
		
		cd.setQuery(qry);
		cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		cd.addParameter(new Parameter("endDate", "End Date", Date.class));
		cd.addParameter(new Parameter("location", "Location", Location.class));
		cd.setDescription("VL results for the breastfeeding received during the reporting period");
		return cd;
	}
	
	/**
	 * Patients who are current on art and on 1st-line
	 * 
	 * @return
	 */
	public CohortDefinition getCurrentOnArtOnFirstLineRegimen(int minAge, int maxAge, String sex) {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Number of patients currently on ART and are on first line regimen");
		cd.addParameter(new Parameter("startDate", "startDate", Date.class));
		cd.addParameter(new Parameter("endDate", "endDate", Date.class));
		cd.addParameter(new Parameter("location", "location", Location.class));
		
		cd.addSearch("currentOnArtFirstLine", SsemrReportUtils.map(getAgeAtStartOfARTonFirstLine(minAge, maxAge, sex),
		    "startDate=${startDate},endDate=${endDate},location=${location}"));
		cd.setCompositionString("currentOnArtFirstLine");
		return cd;
	}
	
	/**
	 * Patients who are current on art and on 2nd-line
	 * 
	 * @return
	 */
	public CohortDefinition getCurrentOnArtOnSecondLineRegimen(int minAge, int maxAge, String sex) {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Number of patients currently on ART and are on second line regimen");
		cd.addParameter(new Parameter("startDate", "startDate", Date.class));
		cd.addParameter(new Parameter("endDate", "endDate", Date.class));
		cd.addParameter(new Parameter("location", "location", Location.class));
		
		cd.addSearch("currentOnArtAndOnSecondLineRegimen", SsemrReportUtils.map(
		    getAgeAtStartOfARTonSecondLine(minAge, maxAge, sex),
		    "startDate=${startDate},endDate=${endDate},location=${location}"));
		cd.addSearch("onSecondLineRegimen", SsemrReportUtils.map(getPatientsOnSecondLineRegimenCohortDefinition(),
		    "startDate=${startDate},endDate=${endDate},location=${location}"));
		cd.setCompositionString("currentOnArtAndOnSecondLineRegimen");
		return cd;
	}
	
	/**
	 * Patients who are newly initiated on ART and are pregnant as at their last clinical visit
	 * 
	 * @return
	 */
	public CohortDefinition getNewOnArtAndPregnant() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Number of patients newly started on ART and pregnant");
		cd.addParameter(new Parameter("startDate", "startDate", Date.class));
		cd.addParameter(new Parameter("endDate", "endDate", Date.class));
		cd.addParameter(new Parameter("location", "location", Location.class));
		
		cd.addSearch("newlyStartedOnArt", SsemrReportUtils.map(getNewOnARTCohortDefinition(),
		    "startDate=${startDate},endDate=${endDate},location=${location}"));
		cd.addSearch("pregnant", SsemrReportUtils.map(getPregnantWomenCohortDefinition(),
		    "startDate=${startDate},endDate=${endDate},location=${location}"));
		cd.setCompositionString("newlyStartedOnArt AND pregnant");
		return cd;
	}
	
	/**
	 * Patients who are newly initiated on ART and are breastfeeding as at their last clinical visit
	 * 
	 * @return
	 */
	public CohortDefinition getNewOnArtAndBreastfeeding() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Number of patients newly started on ART and breastfeeding");
		cd.addParameter(new Parameter("startDate", "startDate", Date.class));
		cd.addParameter(new Parameter("endDate", "endDate", Date.class));
		cd.addParameter(new Parameter("location", "location", Location.class));
		
		cd.addSearch("newlyStartedOnArt", SsemrReportUtils.map(getNewOnARTCohortDefinition(),
		    "startDate=${startDate},endDate=${endDate},location=${location}"));
		cd.addSearch("breastfeeding", SsemrReportUtils.map(getBreastfeedingWomenCohortDefinition(),
		    "startDate=${startDate},endDate=${endDate},location=${location}"));
		cd.setCompositionString("newlyStartedOnArt AND breastfeeding");
		return cd;
	}
	
	public CohortDefinition getNewOnTLDRegimen() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Number of patients newly started on TLD regimen");
		cd.addParameter(new Parameter("startDate", "startDate", Date.class));
		cd.addParameter(new Parameter("endDate", "endDate", Date.class));
		cd.addParameter(new Parameter("location", "location", Location.class));
		
		cd.addSearch("newlyStartedOnArt", SsemrReportUtils.map(getNewOnARTCohortDefinition(),
		    "startDate=${startDate},endDate=${endDate},location=${location}"));
		cd.addSearch("onTLDRegimen", SsemrReportUtils.map(getPatientsOnTLDCohortDefinition(),
		    "startDate=${startDate},endDate=${endDate},location=${location}"));
		cd.setCompositionString("newlyStartedOnArt AND onTLDRegimen");
		return cd;
	}
	
	public CohortDefinition getNewOnOtherDTGBasedRegimen() {
		CompositionCohortDefinition cd = new CompositionCohortDefinition();
		cd.setName("Number of patients newly started on TLD regimen");
		cd.addParameter(new Parameter("startDate", "startDate", Date.class));
		cd.addParameter(new Parameter("endDate", "endDate", Date.class));
		cd.addParameter(new Parameter("location", "location", Location.class));
		
		cd.addSearch("newlyStartedOnArt", SsemrReportUtils.map(getNewOnARTCohortDefinition(),
		    "startDate=${startDate},endDate=${endDate},location=${location}"));
		cd.addSearch("onOtherDTGRegimen", SsemrReportUtils.map(getPatientsOnDTGRegimenCohortDefinition(),
		    "startDate=${startDate},endDate=${endDate},location=${location}"));
		cd.setCompositionString("newlyStartedOnArt AND onOtherDTGRegimen");
		return cd;
	}
	
}
