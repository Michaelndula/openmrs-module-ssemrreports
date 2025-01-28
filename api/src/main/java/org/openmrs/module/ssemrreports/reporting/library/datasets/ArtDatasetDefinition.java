package org.openmrs.module.ssemrreports.reporting.library.datasets;

import static org.openmrs.module.ssemrreports.reporting.library.columns.ShareDatasetColumns.getGenderColumns;
import static org.openmrs.module.ssemrreports.reporting.library.columns.ShareDatasetColumns.getMerGenderAndAgeColumns;
import static org.openmrs.module.ssemrreports.reporting.utils.SsemrReportUtils.map;

import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.ssemrreports.reporting.library.cohorts.ArtCohortQueries;
import org.openmrs.module.ssemrreports.reporting.library.cohorts.CommonCohortQueries;
import org.openmrs.module.ssemrreports.reporting.library.dimension.SsemrCommonDimension;
import org.openmrs.module.ssemrreports.reporting.library.indicator.SsemrGeneralIndicator;
import org.openmrs.module.ssemrreports.reporting.utils.constants.reports.art.ArtReportsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class ArtDatasetDefinition extends SsemrBaseDataSet {
	
	private final SsemrCommonDimension dimension;
	
	private final SsemrGeneralIndicator indicator;
	
	private final ArtCohortQueries artCohortQueries;
	
	private final CommonCohortQueries commonCohortQueries;
	
	SsemrBaseDataSet.ColumnParameters subTotalMales = new SsemrBaseDataSet.ColumnParameters(null, "Total, male", "gender=M",
	        "");
	
	SsemrBaseDataSet.ColumnParameters subTotalFemales = new SsemrBaseDataSet.ColumnParameters(null, "Total, female",
	        "gender=F", "");
	
	SsemrBaseDataSet.ColumnParameters maleInfants = new SsemrBaseDataSet.ColumnParameters(null, "<1, Male",
	        "gender=M|age=<1", "");
	
	SsemrBaseDataSet.ColumnParameters femaleInfants = new SsemrBaseDataSet.ColumnParameters(null, "<1, Female",
	        "gender=F|age=<1", "");
	
	SsemrBaseDataSet.ColumnParameters male_1_to_4 = new SsemrBaseDataSet.ColumnParameters(null, "1-4, Male",
	        "gender=M|age=1-4", "");
	
	SsemrBaseDataSet.ColumnParameters female_1_to_4 = new SsemrBaseDataSet.ColumnParameters(null, "1-4, Female",
	        "gender=F|age=1-4", "");
	
	SsemrBaseDataSet.ColumnParameters male_5_to_9 = new SsemrBaseDataSet.ColumnParameters(null, "5-9, Male",
	        "gender=M|age=5-9", "");
	
	SsemrBaseDataSet.ColumnParameters female_5_to_9 = new SsemrBaseDataSet.ColumnParameters(null, "5-9, Female",
	        "gender=F|age=5-9", "");
	
	SsemrBaseDataSet.ColumnParameters male_0_to_9 = new SsemrBaseDataSet.ColumnParameters(null, "0-9, Male",
	        "gender=M|age=0-9", "");
	
	SsemrBaseDataSet.ColumnParameters female_0_to_9 = new SsemrBaseDataSet.ColumnParameters(null, "0-9, Female",
	        "gender=F|age=0-9", "");
	
	SsemrBaseDataSet.ColumnParameters male_10_to_14 = new SsemrBaseDataSet.ColumnParameters(null, "10-14, Male",
	        "gender=M|age=10-14", "");
	
	SsemrBaseDataSet.ColumnParameters female_10_to_14 = new SsemrBaseDataSet.ColumnParameters(null, "10-14, Female",
	        "gender=F|age=10-14", "");
	
	SsemrBaseDataSet.ColumnParameters p10_to_14 = new SsemrBaseDataSet.ColumnParameters(null, "10-14", "age=10-14", "");
	
	SsemrBaseDataSet.ColumnParameters male_15_to_19 = new SsemrBaseDataSet.ColumnParameters(null, "15-19, Male",
	        "gender=M|age=15-19", "");
	
	SsemrBaseDataSet.ColumnParameters female_15_to_19 = new SsemrBaseDataSet.ColumnParameters(null, "15-19, Female",
	        "gender=F|age=15-19", "");
	
	SsemrBaseDataSet.ColumnParameters p15_to_19 = new SsemrBaseDataSet.ColumnParameters(null, "15-19", "age=15-19", "");
	
	SsemrBaseDataSet.ColumnParameters male_15_to_49 = new SsemrBaseDataSet.ColumnParameters(null, "15-49, Male",
	        "gender=M|age=15-49", "");
	
	SsemrBaseDataSet.ColumnParameters female_15_to_49 = new SsemrBaseDataSet.ColumnParameters(null, "15-49, Female",
	        "gender=F|age=15-49", "");
	
	SsemrBaseDataSet.ColumnParameters male_20_to_24 = new SsemrBaseDataSet.ColumnParameters(null, "20-24, Male",
	        "gender=M|age=20-24", "");
	
	SsemrBaseDataSet.ColumnParameters female_20_to_24 = new SsemrBaseDataSet.ColumnParameters(null, "20-24, Female",
	        "gender=F|age=20-24", "");
	
	SsemrBaseDataSet.ColumnParameters p20_to_24 = new SsemrBaseDataSet.ColumnParameters(null, "20-24", "age=20-24", "");
	
	SsemrBaseDataSet.ColumnParameters male_25_to_29 = new SsemrBaseDataSet.ColumnParameters(null, "25-29, Male",
	        "gender=M|age=25-29", "");
	
	SsemrBaseDataSet.ColumnParameters female_25_to_29 = new SsemrBaseDataSet.ColumnParameters(null, "25-29, Female",
	        "gender=F|age=25-29", "");
	
	SsemrBaseDataSet.ColumnParameters p25_to_29 = new SsemrBaseDataSet.ColumnParameters(null, "25-29", "age=25-29", "");
	
	SsemrBaseDataSet.ColumnParameters male_30_to_34 = new SsemrBaseDataSet.ColumnParameters(null, "30-34, Male",
	        "gender=M|age=30-34", "");
	
	SsemrBaseDataSet.ColumnParameters female_30_to_34 = new SsemrBaseDataSet.ColumnParameters(null, "30-34, Female",
	        "gender=F|age=30-34", "");
	
	SsemrBaseDataSet.ColumnParameters p30_to_34 = new SsemrBaseDataSet.ColumnParameters(null, "30-34", "age=30-34", "");
	
	SsemrBaseDataSet.ColumnParameters male_35_to_39 = new SsemrBaseDataSet.ColumnParameters(null, "35-39, Male",
	        "gender=M|age=35-39", "");
	
	SsemrBaseDataSet.ColumnParameters female_35_to_39 = new SsemrBaseDataSet.ColumnParameters(null, "35-39, Female",
	        "gender=F|age=35-39", "");
	
	SsemrBaseDataSet.ColumnParameters p35_to_39 = new SsemrBaseDataSet.ColumnParameters(null, "35-39", "age=35-39", "");
	
	SsemrBaseDataSet.ColumnParameters male_40_to_44 = new SsemrBaseDataSet.ColumnParameters(null, "40-44, Male",
	        "gender=M|age=40-44", "");
	
	SsemrBaseDataSet.ColumnParameters female_40_to_44 = new SsemrBaseDataSet.ColumnParameters(null, "40-44, Female",
	        "gender=F|age=40-44", "");
	
	SsemrBaseDataSet.ColumnParameters p40_to_44 = new SsemrBaseDataSet.ColumnParameters(null, "40-44", "age=40-44", "");
	
	SsemrBaseDataSet.ColumnParameters male_45_to_49 = new SsemrBaseDataSet.ColumnParameters(null, "45-49, Male",
	        "gender=M|age=45-49", "");
	
	SsemrBaseDataSet.ColumnParameters female_45_to_49 = new SsemrBaseDataSet.ColumnParameters(null, "45-49, Female",
	        "gender=F|age=45-49", "");
	
	SsemrBaseDataSet.ColumnParameters p45_to_49 = new SsemrBaseDataSet.ColumnParameters(null, "45-49", "age=45-49", "");
	
	SsemrBaseDataSet.ColumnParameters male_50_plus = new SsemrBaseDataSet.ColumnParameters(null, "50+, Male",
	        "gender=M|age=50+", "");
	
	SsemrBaseDataSet.ColumnParameters female_50_plus = new SsemrBaseDataSet.ColumnParameters(null, "50+, Female",
	        "gender=F|age=50+", "");
	
	SsemrBaseDataSet.ColumnParameters p50plus = new SsemrBaseDataSet.ColumnParameters(null, "50+", "age=50+", "");
	
	SsemrBaseDataSet.ColumnParameters colTotal = new SsemrBaseDataSet.ColumnParameters(null, "Total", null, "");
	
	List<ColumnParameters> allAgeDisaggregation = Arrays.asList(femaleInfants, maleInfants, female_1_to_4, male_1_to_4,
	    female_5_to_9, male_5_to_9, female_10_to_14, male_10_to_14, female_15_to_19, male_15_to_19, female_20_to_24,
	    male_20_to_24, female_25_to_29, male_25_to_29, female_30_to_34, male_30_to_34, female_35_to_39, male_35_to_39,
	    female_40_to_44, male_40_to_44, female_45_to_49, male_45_to_49, female_50_plus, male_50_plus, subTotalFemales,
	    subTotalMales, colTotal);
	
	List<ColumnParameters> regimenDisaggregation = Arrays.asList(male_0_to_9, female_0_to_9, male_10_to_14, female_10_to_14,
	    male_15_to_49, female_15_to_49, male_50_plus, female_50_plus, colTotal);
	
	List<ColumnParameters> pbfAgeOnlyDisaggregation = Arrays.asList(p10_to_14, p15_to_19, p20_to_24, p25_to_29, p30_to_34,
	    p35_to_39, p40_to_44, p45_to_49, p50plus, colTotal);
	
	List<ColumnParameters> pbfDisaggregation = Arrays.asList(female_10_to_14, female_15_to_19, female_20_to_24,
	    female_25_to_29, female_30_to_34, female_35_to_39, female_40_to_44, female_45_to_49, female_50_plus,
	    subTotalFemales, subTotalMales, colTotal);
	
	@Autowired
	public ArtDatasetDefinition(SsemrCommonDimension dimension, SsemrGeneralIndicator indicator,
	    ArtCohortQueries artCohortQueries, CommonCohortQueries commonCohortQueries) {
		this.dimension = dimension;
		this.indicator = indicator;
		this.artCohortQueries = artCohortQueries;
		this.commonCohortQueries = commonCohortQueries;
	}
	
	public DataSetDefinition getArtDataset() {
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
		dsd.setName("CummAndNewOnArt");
		dsd.setDescription("ART dataset");
		dsd.addParameters(getParameters());
		dsd.addDimension("gender", map(dimension.gender(), ""));
		dsd.addDimension("age", map(dimension.age(), "effectiveDate=${endDate}"));
		addRow(
		    dsd,
		    "1",
		    "Cumulative patients started on ART at the end of the previous reporting period",
		    map(indicator.getIndicator(
		        "Cumulative patients started on ART at the end of the previous reporting period",
		        map(artCohortQueries.getCumulativeEverOnARTAtThisFacilityInPreviousReportingPeriodCohortDefinition(),
		            mappings)), mappings), allAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07",
		        "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25",
		        "26", "27"));
		
		addRow(
		    dsd,
		    "2",
		    "New persons started on ART during the reporting period",
		    map(indicator.getIndicator("New persons started on ART during the reporting period",
		        map(artCohortQueries.getNewOnARTCohortDefinition(), mappings)), mappings), allAgeDisaggregation,
		    Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16",
		        "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27"));
		
		addRow(
		    dsd,
		    "3",
		    "Number of pregnant women among the new cases during the reporting period",
		    map(indicator.getIndicator("Number of pregnant women among the new cases during the reporting period",
		        map(artCohortQueries.getNewOnArtAndPregnant(), mappings)), mappings), pbfDisaggregation,
		    Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"));
		
		addRow(
		    dsd,
		    "4",
		    "Number of breastfeeding women among the new cases during the reporting period",
		    map(indicator.getIndicator("Number of breastfeeding women among the new cases during the reporting period",
		        map(artCohortQueries.getNewOnArtAndBreastfeeding(), mappings)), mappings), pbfDisaggregation,
		    Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"));
		
		addRow(
		    dsd,
		    "5",
		    "Started on TLD among new cases during the reporting period",
		    map(indicator.getIndicator("Started on TLD among new cases during the reporting period",
		        map(artCohortQueries.getNewOnTLDRegimen(), mappings)), mappings), allAgeDisaggregation, Arrays.asList("01",
		        "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19",
		        "20", "21", "22", "23", "24", "25", "26", "27"));
		
		addRow(
		    dsd,
		    "6",
		    "Started on other DTG based regimen among new cases during the reporting period",
		    map(indicator.getIndicator("Started on other DTG based regimen among new cases during the reporting period",
		        map(artCohortQueries.getNewOnOtherDTGBasedRegimen(), mappings)), mappings), allAgeDisaggregation,
		    Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16",
		        "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27"));
		
		addRow(
		    dsd,
		    "7",
		    "Cumulative no ever started on ART at this facility by end of reporting period",
		    map(indicator.getIndicator("Cumulative no ever started on ART at this facility by end of reporting period",
		        map(artCohortQueries.getCumulativeEverOnARTAtThisFacilityAtEndOfReportingCohortDefinition(), mappings)),
		        mappings), allAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10",
		        "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27"));
		
		//adding dapsone and ctx on the report
		dsd.addColumn(
		    "NCTX",
		    "Number of new clients started on CTX ",
		    map(indicator.getIndicator("Number of new clients started on CTX ",
		        map(artCohortQueries.getNewOnARTonCTXCohortDefinition(), mappings)), mappings), "");
		dsd.addColumn(
		    "NDAP",
		    "Number of new clients started Dapsone",
		    map(indicator.getIndicator("Number of new clients started Dapsone",
		        map(artCohortQueries.getNewOnARTonDapsoneCohortDefinition(), mappings)), mappings), "");
		return dsd;
	}
	
	/**
	 * Dataset for patients on ART disaggregated by age at start of ART
	 * 
	 * @return
	 */
	public DataSetDefinition getTxCurrForAgeAtEnrolmentDataset() {
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
		dsd.setName("currentOnArtByAge");
		dsd.setDescription("ART dataset - age at start of ART");
		dsd.addParameters(getParameters());
		dsd.addDimension("gender", map(dimension.gender(), ""));
		
		dsd.addColumn(
		    "8-01",
		    "Current on ART on 1st-line regimen, Male <1 at end of Last Month",
		    map(indicator.getIndicator("Current on ART on 1st-line regimen <1, male at end of Last Month",
		        map(artCohortQueries.getCurrentOnArtOnFirstLineRegimen(0, 0, "M"), mappings)), mappings), "");
		dsd.addColumn(
		    "8-02",
		    "Current on ART on 1st-line regimen, Female <1 at end of Last Month",
		    map(indicator.getIndicator("Current on ART on 1st-line regimen <1, female at end of Last Month",
		        map(artCohortQueries.getCurrentOnArtOnFirstLineRegimen(0, 0, "F"), mappings)), mappings), "");
		
		dsd.addColumn(
		    "8-03",
		    "Current on ART on 1st-line regimen, Male 1-4 at end of Last Month",
		    map(indicator.getIndicator("Current on ART on 1st-line regimen 1-4, male at end of Last Month",
		        map(artCohortQueries.getCurrentOnArtOnFirstLineRegimen(1, 4, "M"), mappings)), mappings), "");
		dsd.addColumn(
		    "8-04",
		    "Current on ART on 1st-line regimen, Female 1-4 at end of Last Month",
		    map(indicator.getIndicator("Current on ART on 1st-line regimen 1-4, female at end of Last Month",
		        map(artCohortQueries.getCurrentOnArtOnFirstLineRegimen(1, 4, "F"), mappings)), mappings), "");
		dsd.addColumn(
		    "8-05",
		    "Current on ART on 1st-line regimen, Male 5-9 at end of Last Month",
		    map(indicator.getIndicator("Current on ART on 1st-line regimen 5-9, male at end of Last Month",
		        map(artCohortQueries.getCurrentOnArtOnFirstLineRegimen(5, 9, "M"), mappings)), mappings), "");
		dsd.addColumn(
		    "8-06",
		    "Current on ART on 1st-line regimen, Female 5-9 at end of Last Month",
		    map(indicator.getIndicator("Current on ART on 1st-line regimen 5-9, female at end of Last Month",
		        map(artCohortQueries.getCurrentOnArtOnFirstLineRegimen(5, 9, "F"), mappings)), mappings), "");
		
		dsd.addColumn(
		    "8-07",
		    "Current on ART on 1st-line regimen, Male 10-14 at end of Last Month",
		    map(indicator.getIndicator("Current on ART on 1st-line regimen 10-14, male at end of Last Month",
		        map(artCohortQueries.getCurrentOnArtOnFirstLineRegimen(10, 14, "M"), mappings)), mappings), "");
		dsd.addColumn(
		    "8-08",
		    "Current on ART on 1st-line regimen, Female 10-14 at end of Last Month",
		    map(indicator.getIndicator("Current on ART on 1st-line regimen 10-14, female at end of Last Month",
		        map(artCohortQueries.getCurrentOnArtOnFirstLineRegimen(10, 14, "F"), mappings)), mappings), "");
		
		dsd.addColumn(
		    "8-09",
		    "Current on ART on 1st-line regimen, Male 15-19 at end of Last Month",
		    map(indicator.getIndicator("Current on ART on 1st-line regimen 15-19, male at end of Last Month",
		        map(artCohortQueries.getCurrentOnArtOnFirstLineRegimen(15, 19, "M"), mappings)), mappings), "");
		dsd.addColumn(
		    "8-10",
		    "Current on ART on 1st-line regimen, Female 15-19 at end of Last Month",
		    map(indicator.getIndicator("Current on ART on 1st-line regimen 15-19, female at end of Last Month",
		        map(artCohortQueries.getCurrentOnArtOnFirstLineRegimen(15, 19, "F"), mappings)), mappings), "");
		
		dsd.addColumn(
		    "8-11",
		    "Current on ART on 1st-line regimen, Male 20-24 at end of Last Month",
		    map(indicator.getIndicator("Current on ART on 1st-line regimen 20-24, male at end of Last Month",
		        map(artCohortQueries.getCurrentOnArtOnFirstLineRegimen(20, 24, "M"), mappings)), mappings), "");
		dsd.addColumn(
		    "8-12",
		    "Current on ART on 1st-line regimen, Female 20-24 at end of Last Month",
		    map(indicator.getIndicator("Current on ART on 1st-line regimen 20-24, female at end of Last Month",
		        map(artCohortQueries.getCurrentOnArtOnFirstLineRegimen(20, 24, "F"), mappings)), mappings), "");
		
		dsd.addColumn(
		    "8-13",
		    "Current on ART on 1st-line regimen, Male 25-29 at end of Last Month",
		    map(indicator.getIndicator("Current on ART on 1st-line regimen 25-29, male at end of Last Month",
		        map(artCohortQueries.getCurrentOnArtOnFirstLineRegimen(25, 29, "M"), mappings)), mappings), "");
		dsd.addColumn(
		    "8-14",
		    "Current on ART on 1st-line regimen, Female 25-29 at end of Last Month",
		    map(indicator.getIndicator("Current on ART on 1st-line regimen 25-29, female at end of Last Month",
		        map(artCohortQueries.getCurrentOnArtOnFirstLineRegimen(25, 29, "F"), mappings)), mappings), "");
		
		dsd.addColumn(
		    "8-15",
		    "Current on ART on 1st-line regimen, Male 30-34 at end of Last Month",
		    map(indicator.getIndicator("Current on ART on 1st-line regimen 30-34, male at end of Last Month",
		        map(artCohortQueries.getCurrentOnArtOnFirstLineRegimen(30, 34, "M"), mappings)), mappings), "");
		dsd.addColumn(
		    "8-16",
		    "Current on ART on 1st-line regimen, Female 30-34 at end of Last Month",
		    map(indicator.getIndicator("Current on ART on 1st-line regimen 30-34, female at end of Last Month",
		        map(artCohortQueries.getCurrentOnArtOnFirstLineRegimen(30, 34, "F"), mappings)), mappings), "");
		
		dsd.addColumn(
		    "8-17",
		    "Current on ART on 1st-line regimen, Male 35-39 at end of Last Month",
		    map(indicator.getIndicator("Current on ART on 1st-line regimen 35-39, male at end of Last Month",
		        map(artCohortQueries.getCurrentOnArtOnFirstLineRegimen(35, 39, "M"), mappings)), mappings), "");
		dsd.addColumn(
		    "8-18",
		    "Current on ART on 1st-line regimen, Female 35-39 at end of Last Month",
		    map(indicator.getIndicator("Current on ART on 1st-line regimen 35-39, female at end of Last Month",
		        map(artCohortQueries.getCurrentOnArtOnFirstLineRegimen(35, 39, "F"), mappings)), mappings), "");
		
		dsd.addColumn(
		    "8-19",
		    "Current on ART on 1st-line regimen, Male 40-44 at end of Last Month",
		    map(indicator.getIndicator("Current on ART on 1st-line regimen 40-44, male at end of Last Month",
		        map(artCohortQueries.getCurrentOnArtOnFirstLineRegimen(40, 44, "M"), mappings)), mappings), "");
		dsd.addColumn(
		    "8-20",
		    "Current on ART on 1st-line regimen, Female 40-44 at end of Last Month",
		    map(indicator.getIndicator("Current on ART on 1st-line regimen 40-44, female at end of Last Month",
		        map(artCohortQueries.getCurrentOnArtOnFirstLineRegimen(40, 44, "F"), mappings)), mappings), "");
		
		dsd.addColumn(
		    "8-21",
		    "Current on ART on 1st-line regimen, Male 45-49 at end of Last Month",
		    map(indicator.getIndicator("Current on ART on 1st-line regimen 45-49, male at end of Last Month",
		        map(artCohortQueries.getCurrentOnArtOnFirstLineRegimen(45, 49, "M"), mappings)), mappings), "");
		dsd.addColumn(
		    "8-22",
		    "Current on ART on 1st-line regimen, Female 45-49 at end of Last Month",
		    map(indicator.getIndicator("Current on ART on 1st-line regimen 45-49, female at end of Last Month",
		        map(artCohortQueries.getCurrentOnArtOnFirstLineRegimen(45, 49, "F"), mappings)), mappings), "");
		
		dsd.addColumn(
		    "8-23",
		    "Current on ART on 1st-line regimen, Male 50+ at end of Last Month",
		    map(indicator.getIndicator("Current on ART on 1st-line regimen 50+, male at end of Last Month",
		        map(artCohortQueries.getCurrentOnArtOnFirstLineRegimen(50, 150, "M"), mappings)), mappings), "");
		dsd.addColumn(
		    "8-24",
		    "Current on ART on 1st-line regimen, Female 50+",
		    map(indicator.getIndicator("Current on ART on 1st-line regimen 50+, female",
		        map(artCohortQueries.getCurrentOnArtOnFirstLineRegimen(50, 150, "F"), mappings)), mappings), "");
		
		//totals
		dsd.addColumn(
		    "MT1",
		    "Current on ART on 1st-line regimen, Male Total",
		    map(indicator.getIndicator("Current on ART on 1st-line regimen, Male Total",
		        map(artCohortQueries.getCurrentOnArtOnFirstLineRegimen(0, 150, "M"), mappings)), mappings), "");
		dsd.addColumn(
		    "FT1",
		    "Current on ART on 1st-line regimen, Female Total",
		    map(indicator.getIndicator("Current on ART on 1st-line regimen, Female Total",
		        map(artCohortQueries.getCurrentOnArtOnFirstLineRegimen(0, 150, "F"), mappings)), mappings), "");
		
		// on 2nd-line regimen
		
		dsd.addColumn(
		    "8-25",
		    "Current on ART on 2nd-line regimen, Male <1 ",
		    map(indicator.getIndicator("Current on ART on 2nd-line regimen <1, male",
		        map(artCohortQueries.getCurrentOnArtOnSecondLineRegimen(0, 0, "M"), mappings)), mappings), "");
		dsd.addColumn(
		    "8-26",
		    "Current on ART on 2nd-line regimen, Female <1",
		    map(indicator.getIndicator("Current on ART on 2nd-line regimen <1, female",
		        map(artCohortQueries.getCurrentOnArtOnSecondLineRegimen(0, 0, "F"), mappings)), mappings), "");
		
		dsd.addColumn(
		    "8-27",
		    "Current on ART on 2nd-line regimen, Male 1-4 ",
		    map(indicator.getIndicator("Current on ART on 2nd-line regimen 1-4, male",
		        map(artCohortQueries.getCurrentOnArtOnSecondLineRegimen(1, 4, "M"), mappings)), mappings), "");
		dsd.addColumn(
		    "8-28",
		    "Current on ART on 2nd-line regimen, Female 1-4",
		    map(indicator.getIndicator("Current on ART on 2nd-line regimen 1-4, female",
		        map(artCohortQueries.getCurrentOnArtOnSecondLineRegimen(1, 4, "F"), mappings)), mappings), "");
		dsd.addColumn(
		    "8-29",
		    "Current on ART on 2nd-line regimen, Male 5-9 ",
		    map(indicator.getIndicator("Current on ART on 2nd-line regimen 5-9, male",
		        map(artCohortQueries.getCurrentOnArtOnSecondLineRegimen(5, 9, "M"), mappings)), mappings), "");
		dsd.addColumn(
		    "8-30",
		    "Current on ART on 2nd-line regimen, Female 5-9",
		    map(indicator.getIndicator("Current on ART on 2nd-line regimen 5-9, female",
		        map(artCohortQueries.getCurrentOnArtOnSecondLineRegimen(5, 9, "F"), mappings)), mappings), "");
		
		dsd.addColumn(
		    "8-31",
		    "Current on ART on 2nd-line regimen, Male 10-14 ",
		    map(indicator.getIndicator("Current on ART on 2nd-line regimen 10-14, male",
		        map(artCohortQueries.getCurrentOnArtOnSecondLineRegimen(10, 14, "M"), mappings)), mappings), "");
		dsd.addColumn(
		    "8-32",
		    "Current on ART on 2nd-line regimen, Female 10-14",
		    map(indicator.getIndicator("Current on ART on 2nd-line regimen 10-14, female",
		        map(artCohortQueries.getCurrentOnArtOnSecondLineRegimen(10, 14, "F"), mappings)), mappings), "");
		
		dsd.addColumn(
		    "8-33",
		    "Current on ART on 2nd-line regimen, Male 15-19 ",
		    map(indicator.getIndicator("Current on ART on 2nd-line regimen 15-19, male",
		        map(artCohortQueries.getCurrentOnArtOnSecondLineRegimen(15, 19, "M"), mappings)), mappings), "");
		dsd.addColumn(
		    "8-34",
		    "Current on ART on 2nd-line regimen, Female 15-19",
		    map(indicator.getIndicator("Current on ART on 2nd-line regimen 15-19, female",
		        map(artCohortQueries.getCurrentOnArtOnSecondLineRegimen(15, 19, "F"), mappings)), mappings), "");
		
		dsd.addColumn(
		    "8-35",
		    "Current on ART on 2nd-line regimen, Male 20-24 ",
		    map(indicator.getIndicator("Current on ART on 2nd-line regimen 20-24, male",
		        map(artCohortQueries.getCurrentOnArtOnSecondLineRegimen(20, 24, "M"), mappings)), mappings), "");
		dsd.addColumn(
		    "8-36",
		    "Current on ART on 2nd-line regimen, Female 20-24",
		    map(indicator.getIndicator("Current on ART on 2nd-line regimen 20-24, female",
		        map(artCohortQueries.getCurrentOnArtOnSecondLineRegimen(20, 24, "F"), mappings)), mappings), "");
		
		dsd.addColumn(
		    "8-37",
		    "Current on ART on 2nd-line regimen, Male 25-29 ",
		    map(indicator.getIndicator("Current on ART on 2nd-line regimen 25-29, male",
		        map(artCohortQueries.getCurrentOnArtOnSecondLineRegimen(25, 29, "M"), mappings)), mappings), "");
		dsd.addColumn(
		    "8-38",
		    "Current on ART on 2nd-line regimen, Female 25-29",
		    map(indicator.getIndicator("Current on ART on 2nd-line regimen 25-29, female",
		        map(artCohortQueries.getCurrentOnArtOnSecondLineRegimen(25, 29, "F"), mappings)), mappings), "");
		
		dsd.addColumn(
		    "8-39",
		    "Current on ART on 2nd-line regimen, Male 30-34 ",
		    map(indicator.getIndicator("Current on ART on 2nd-line regimen 30-34, male",
		        map(artCohortQueries.getCurrentOnArtOnSecondLineRegimen(30, 34, "M"), mappings)), mappings), "");
		dsd.addColumn(
		    "8-40",
		    "Current on ART on 2nd-line regimen, Female 30-34",
		    map(indicator.getIndicator("Current on ART on 2nd-line regimen 30-34, female",
		        map(artCohortQueries.getCurrentOnArtOnSecondLineRegimen(30, 34, "F"), mappings)), mappings), "");
		
		dsd.addColumn(
		    "8-41",
		    "Current on ART on 2nd-line regimen, Male 35-39 ",
		    map(indicator.getIndicator("Current on ART on 2nd-line regimen 35-39, male",
		        map(artCohortQueries.getCurrentOnArtOnSecondLineRegimen(35, 39, "M"), mappings)), mappings), "");
		dsd.addColumn(
		    "8-42",
		    "Current on ART on 2nd-line regimen, Female 35-39",
		    map(indicator.getIndicator("Current on ART on 2nd-line regimen 35-39, female",
		        map(artCohortQueries.getCurrentOnArtOnSecondLineRegimen(35, 39, "F"), mappings)), mappings), "");
		
		dsd.addColumn(
		    "8-43",
		    "Current on ART on 2nd-line regimen, Male 40-44 ",
		    map(indicator.getIndicator("Current on ART on 2nd-line regimen 40-44, male",
		        map(artCohortQueries.getCurrentOnArtOnSecondLineRegimen(40, 44, "M"), mappings)), mappings), "");
		dsd.addColumn(
		    "8-44",
		    "Current on ART on 2nd-line regimen, Female 40-44",
		    map(indicator.getIndicator("Current on ART on 2nd-line regimen 40-44, female",
		        map(artCohortQueries.getCurrentOnArtOnSecondLineRegimen(40, 44, "F"), mappings)), mappings), "");
		
		dsd.addColumn(
		    "8-45",
		    "Current on ART on 2nd-line regimen, Male 45-49 ",
		    map(indicator.getIndicator("Current on ART on 2nd-line regimen 45-49, male",
		        map(artCohortQueries.getCurrentOnArtOnSecondLineRegimen(45, 49, "M"), mappings)), mappings), "");
		dsd.addColumn(
		    "8-46",
		    "Current on ART on 2nd-line regimen, Female 45-49",
		    map(indicator.getIndicator("Current on ART on 2nd-line regimen 45-49, female",
		        map(artCohortQueries.getCurrentOnArtOnSecondLineRegimen(45, 49, "F"), mappings)), mappings), "");
		
		dsd.addColumn(
		    "8-47",
		    "Current on ART on 2nd-line regimen, Male 50+ ",
		    map(indicator.getIndicator("Current on ART on 2nd-line regimen 50+, male",
		        map(artCohortQueries.getCurrentOnArtOnSecondLineRegimen(50, 150, "M"), mappings)), mappings), "");
		dsd.addColumn(
		    "8-48",
		    "Current on ART on 2nd-line regimen, Female 50+",
		    map(indicator.getIndicator("Current on ART on 2nd-line regimen 50+, female",
		        map(artCohortQueries.getCurrentOnArtOnSecondLineRegimen(50, 150, "F"), mappings)), mappings), "");
		//totals
		dsd.addColumn(
		    "MT2",
		    "Current on ART on 2nd-line regimen, Male Total",
		    map(indicator.getIndicator("Current on ART on 2nd-line regimen, Male Total",
		        map(artCohortQueries.getCurrentOnArtOnSecondLineRegimen(0, 150, "M"), mappings)), mappings), "");
		dsd.addColumn(
		    "FT2",
		    "Current on ART on 2nd-line regimen, Female Total",
		    map(indicator.getIndicator("Current on ART on 2nd-line regimen, Female Total",
		        map(artCohortQueries.getCurrentOnArtOnSecondLineRegimen(0, 150, "F"), mappings)), mappings), "");
		
		//add datasets for the TB cases
		addRow(
		    dsd,
		    "TBS1",
		    "TB Status No signs ",
		    map(indicator.getIndicator("TB Status No signs ",
		        map(artCohortQueries.getNewOnArtWithTbStatusCohortDefinition("No Signs"), mappings)), mappings),
		    getGenderColumns());
		addRow(
		    dsd,
		    "TBS2",
		    "Pr TB - Presumptive TB ",
		    map(indicator.getIndicator("Pr TB - Presumptive TB ",
		        map(artCohortQueries.getNewOnArtWithTbStatusCohortDefinition("Pr TB - Presumptive TB"), mappings)), mappings),
		    getGenderColumns());
		addRow(
		    dsd,
		    "TBS3",
		    "ND - TB Screening not done ",
		    map(indicator.getIndicator("ND - TB Screening not done ",
		        map(artCohortQueries.getNewOnArtWithTbStatusCohortDefinition("ND - TB Screening not done"), mappings)),
		        mappings), getGenderColumns());
		addRow(
		    dsd,
		    "TBS4",
		    "INH = Cleint was screened negative and currently on INH prophylaxis (IPT)",
		    map(indicator.getIndicator("INH = Cleint was screened negative and currently on INH prophylaxis (IPT) ",
		        map(artCohortQueries.getNewOnArtAndOnInhCohortDefinition(), mappings)), mappings), getGenderColumns());
		addRow(
		    dsd,
		    "TBS5",
		    "TB Rx = Client currently on TB treatment",
		    map(indicator.getIndicator("TB Rx = Client currently on TB treatment",
		        map(artCohortQueries.getNewOnArtAndOnTbTreatmentCohortDefinition(), mappings)), mappings),
		    getGenderColumns());
		return dsd;
	}
	
	public DataSetDefinition getTxCurrByRegimenDataset() {
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
		dsd.setName("currentOnArtByRegimen");
		dsd.setDescription("ART by regimen dataset");
		dsd.addParameters(getParameters());
		dsd.addDimension("gender", map(dimension.gender(), ""));
		dsd.addDimension("age", map(dimension.age(), "effectiveDate=${endDate}"));
		
		for (int i = 0; i < ArtReportsConstants.adultFirstLineRegimen.size(); i++) {
			String regimenName = ArtReportsConstants.adultFirstLineRegimen.get(i);
			String nameParts[] = regimenName.split("=");
			
			addRow(
			    dsd,
			    nameParts[0].trim(),
			    "Adult first line regimen - " + regimenName,
			    map(indicator.getIndicator("Adult first line regimen - " + regimenName,
			        map(artCohortQueries.getPatientsOnRegimenCohortDefinition(regimenName), mappings)), mappings),
			    regimenDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09"));
			dsd.addColumn(
			    nameParts[0].trim().concat("-10"),
			    "Adult first line regimen, breastfeeding ",
			    map(indicator.getIndicator("Adult first line regimen, breastfeeding",
			        map(artCohortQueries.getBreastFeedingPatientsOnRegimenCohortDefinition(regimenName), mappings)),
			        mappings), "");
			dsd.addColumn(
			    nameParts[0].trim().concat("-11"),
			    "Adult first line regimen, pregnant ",
			    map(indicator.getIndicator("Adult first line regimen, pregnant",
			        map(artCohortQueries.getPregnantPatientsOnRegimenCohortDefinition(regimenName), mappings)), mappings),
			    "");
			
		}
		
		for (int i = 0; i < ArtReportsConstants.adultSecondLineRegimen.size(); i++) {
			String regimenName = ArtReportsConstants.adultSecondLineRegimen.get(i);
			String nameParts[] = regimenName.split("=");
			
			addRow(
			    dsd,
			    nameParts[0].trim(),
			    "Adult second line regimen - " + regimenName,
			    map(indicator.getIndicator("Adult second line regimen - " + regimenName,
			        map(artCohortQueries.getPatientsOnRegimenCohortDefinition(regimenName), mappings)), mappings),
			    regimenDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09"));
			
			dsd.addColumn(
			    nameParts[0].trim().concat("-10"),
			    "Adult second line regimen, breastfeeding ",
			    map(indicator.getIndicator("Adult second line regimen, breastfeeding",
			        map(artCohortQueries.getBreastFeedingPatientsOnRegimenCohortDefinition(regimenName), mappings)),
			        mappings), "");
			dsd.addColumn(
			    nameParts[0].trim().concat("-11"),
			    "Adult second line regimen, pregnant ",
			    map(indicator.getIndicator("Adult second line regimen, pregnant",
			        map(artCohortQueries.getPregnantPatientsOnRegimenCohortDefinition(regimenName), mappings)), mappings),
			    "");
			
		}
		
		for (int i = 0; i < ArtReportsConstants.childFirstLineRegimen.size(); i++) {
			String regimenName = ArtReportsConstants.childFirstLineRegimen.get(i);
			String nameParts[] = regimenName.split("=");
			
			addRow(
			    dsd,
			    nameParts[0].trim(),
			    "Child first line regimen - " + regimenName,
			    map(indicator.getIndicator("Child first line regimen - " + regimenName,
			        map(artCohortQueries.getPatientsOnRegimenCohortDefinition(regimenName), mappings)), mappings),
			    regimenDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09"));
			
			dsd.addColumn(
			    nameParts[0].trim().concat("-10"),
			    "Child first line regimen, breastfeeding ",
			    map(indicator.getIndicator("Child first line regimen, breastfeeding",
			        map(artCohortQueries.getBreastFeedingPatientsOnRegimenCohortDefinition(regimenName), mappings)),
			        mappings), "");
			dsd.addColumn(
			    nameParts[0].trim().concat("-11"),
			    "Child first line regimen, pregnant ",
			    map(indicator.getIndicator("Child first line regimen, pregnant",
			        map(artCohortQueries.getPregnantPatientsOnRegimenCohortDefinition(regimenName), mappings)), mappings),
			    "");
			
		}
		
		for (int i = 0; i < ArtReportsConstants.childSecondLineRegimen.size(); i++) {
			String regimenName = ArtReportsConstants.childSecondLineRegimen.get(i);
			String nameParts[] = regimenName.split("=");
			
			addRow(
			    dsd,
			    nameParts[0].trim(),
			    "Child second line regimen - " + regimenName,
			    map(indicator.getIndicator("Child second line regimen - " + regimenName,
			        map(artCohortQueries.getPatientsOnRegimenCohortDefinition(regimenName), mappings)), mappings),
			    regimenDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09"));
			
			dsd.addColumn(
			    nameParts[0].trim().concat("-10"),
			    "Child second line regimen, breastfeeding ",
			    map(indicator.getIndicator("Child second line regimen, breastfeeding",
			        map(artCohortQueries.getBreastFeedingPatientsOnRegimenCohortDefinition(regimenName), mappings)),
			        mappings), "");
			dsd.addColumn(
			    nameParts[0].trim().concat("-11"),
			    "Child second line regimen, pregnant ",
			    map(indicator.getIndicator("Child second line regimen, pregnant",
			        map(artCohortQueries.getPregnantPatientsOnRegimenCohortDefinition(regimenName), mappings)), mappings),
			    "");
			
		}
		
		dsd.addColumn(
		    "onCtx",
		    "clients on CTX",
		    map(indicator.getIndicator("clients on CTX",
		        map(artCohortQueries.patientsOnCTXTreatmentCohortDefinition(), mappings)), mappings), "");
		
		dsd.addColumn(
		    "onDapsone",
		    "clients on Dapsoine",
		    map(indicator.getIndicator("clients on Dapsone",
		        map(artCohortQueries.patientsOnDapsoneTreatmentCohortDefinition(), mappings)), mappings), "");
		
		dsd.addColumn(
		    "ltfu",
		    "clients who are LTFU",
		    map(indicator.getIndicator("clients who are on LTFU",
		        map(artCohortQueries.patientsLtfuCohortDefinition(), mappings)), mappings), "");
		
		dsd.addColumn(
		    "dead",
		    "clients who Died",
		    map(indicator.getIndicator("clients who Died", map(artCohortQueries.patientsDeadCohortDefinition(), mappings)),
		        mappings), "");
		
		return dsd;
		
	}
	
	public DataSetDefinition getViralLoadDataset() {
		// VL smaples collecetd and results received during the previous reporting period(month)
		CohortIndicatorDataSetDefinition dsd = new CohortIndicatorDataSetDefinition();
		String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
		dsd.setName("viralLoad");
		dsd.setDescription("VL dataset");
		dsd.addParameters(getParameters());
		dsd.addDimension("gender", map(dimension.gender(), ""));
		dsd.addDimension("age", map(dimension.age(), "effectiveDate=${endDate}"));
		addRow(
		    dsd,
		    "CT",
		    "Samples collected",
		    map(indicator.getIndicator("Samples collected",
		        map(artCohortQueries.getVLSampleCollectionCohortDefinition(), mappings)), mappings), allAgeDisaggregation,
		    Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16",
		        "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27"));
		addRow(
		    dsd,
		    "CP",
		    "Samples collected - pregnant",
		    map(indicator.getIndicator("Samples collected - pregnant",
		        map(artCohortQueries.getVLSampleCollectionForPregnantCohortDefinition(), mappings)), mappings),
		    pbfAgeOnlyDisaggregation, Arrays.asList("28", "29", "30", "31", "32", "33", "34", "35", "36", "37"));
		addRow(
		    dsd,
		    "CB",
		    "Samples collected - breastfeeding",
		    map(indicator.getIndicator("Samples collected - breastfeeding",
		        map(artCohortQueries.getVLSampleCollectionForBreastfeedingCohortDefinition(), mappings)), mappings),
		    pbfAgeOnlyDisaggregation, Arrays.asList("38", "39", "40", "41", "42", "43", "44", "45", "46", "47"));
		
		addRow(
		    dsd,
		    "R<1000T",
		    "Results received",
		    map(indicator.getIndicator("Results received", map(artCohortQueries.getVLResultsCohortDefinition(), mappings)),
		        mappings), allAgeDisaggregation, Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10",
		        "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27"));
		addRow(
		    dsd,
		    "R<1000P",
		    "Results received - pregnant",
		    map(indicator.getIndicator("Results received - pregnant",
		        map(artCohortQueries.getVLResultsForPregnantCohortDefinition(), mappings)), mappings),
		    pbfAgeOnlyDisaggregation, Arrays.asList("28", "29", "30", "31", "32", "33", "34", "35", "36", "37"));
		addRow(
		    dsd,
		    "R<1000B",
		    "Results received - breastfeeding",
		    map(indicator.getIndicator("Results received - breastfeeding",
		        map(artCohortQueries.getVLResultsForBreastfeedingCohortDefinition(), mappings)), mappings),
		    pbfAgeOnlyDisaggregation, Arrays.asList("38", "39", "40", "41", "42", "43", "44", "45", "46", "47"));
		
		addRow(
		    dsd,
		    "R>=1000T",
		    "Results received All",
		    map(indicator.getIndicator("Results received All",
		        map(artCohortQueries.getVLRetentionResultsCohortDefinition(), mappings)), mappings), allAgeDisaggregation,
		    Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16",
		        "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27"));
		addRow(
		    dsd,
		    "R>=1000P",
		    "Results received - pregnant",
		    map(indicator.getIndicator("Results received - pregnant",
		        map(artCohortQueries.getRetentionVLResultsForPregnantCohortDefinition(), mappings)), mappings),
		    pbfAgeOnlyDisaggregation, Arrays.asList("28", "29", "30", "31", "32", "33", "34", "35", "36", "37"));
		addRow(
		    dsd,
		    "R>=1000B",
		    "Results received - breastfeeding",
		    map(indicator.getIndicator("Results received - breastfeeding",
		        map(artCohortQueries.getRetentionVLResultsForBreastFeedingCohortDefinition(), mappings)), mappings),
		    pbfAgeOnlyDisaggregation, Arrays.asList("38", "39", "40", "41", "42", "43", "44", "45", "46", "47"));
		return dsd;
	}
}
