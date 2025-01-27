package org.openmrs.module.ssemrreports.reporting.library.datasets;

import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.SortCriteria;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.person.definition.*;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.ssemrreports.reporting.calculation.BomaAddressCalculation;
import org.openmrs.module.ssemrreports.reporting.calculation.LandmarkAddressCalculation;
import org.openmrs.module.ssemrreports.reporting.calculation.PayamAddressCalculation;
import org.openmrs.module.ssemrreports.reporting.converter.CalculationResultConverter;
import org.openmrs.module.ssemrreports.reporting.library.data.converter.PersonAttributeDataConverter;
import org.openmrs.module.ssemrreports.reporting.library.data.definition.TbUnitNumberDataDefinition;
import org.openmrs.module.ssemrreports.reporting.library.data.definition.*;
import org.openmrs.module.ssemrreports.reporting.library.data.definition.DateStartedTbDataDefinition;
import org.openmrs.module.ssemrreports.reporting.utils.constants.reports.shared.SharedReportConstants;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class TbTreatmentDatasetDefinition extends SsemrBaseDataSet {
	
	private DataDefinition personPayamAddress() {
		CalculationDataDefinition cd = new CalculationDataDefinition("payam", new PayamAddressCalculation());
		return cd;
	}
	
	private DataDefinition personBomaAddress() {
		CalculationDataDefinition cd = new CalculationDataDefinition("boma", new BomaAddressCalculation());
		return cd;
	}
	
	private DataDefinition personLandmarkAddress() {
		CalculationDataDefinition cd = new CalculationDataDefinition("landmark", new LandmarkAddressCalculation());
		return cd;
	}
	
	public DataSetDefinition constructTbDatasetDefinition() {
		
		String DATE_FORMAT = "dd-MMM-yyyy";
		PatientDataSetDefinition dsd = new PatientDataSetDefinition();
		dsd.setName("STB");
		dsd.addParameters(getParameters());
		dsd.setDescription("Line list of Clients On TB Treatment");
		dsd.addSortCriteria("Psn", SortCriteria.SortDirection.ASC);
		dsd.addParameter(new Parameter("location", "Location", Location.class));
		dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
		dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
		DataConverter nameFormatter = new ObjectFormatter("{givenName} {middleName} {familyName}");
		DataDefinition nameDef = new ConvertedPersonDataDefinition("name", new PreferredNameDataDefinition(), nameFormatter);
		
		PatientIdentifierType openmrsID = Context.getPatientService().getPatientIdentifierTypeByUuid(
		    SharedReportConstants.UNIQUE_ART_NUMBER_TYPE_UUID);
		DataConverter identifierFormatter = new ObjectFormatter("{identifier}");
		DataDefinition identifierDef = new ConvertedPatientDataDefinition("identifier", new PatientIdentifierDataDefinition(
		        openmrsID.getName(), openmrsID), identifierFormatter);
		
		PersonAttributeType phoneNumber = Context.getPersonService().getPersonAttributeTypeByUuid(
		    SharedReportConstants.PHONE_NUMBER_ATTRIBUTE_TYPE_UUID);
		
		PersonAttributeType alternativePhoneNumber = Context.getPersonService().getPersonAttributeTypeByUuid(
		    SharedReportConstants.ALTERNATIVE_PHONE_NUMBER_ATTRIBUTE_TYPE_UUID);
		
		COVNameDataDefinition covNameDataDefinition = new COVNameDataDefinition();
		covNameDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		
		LinkedToCOVDataDefinition linkedToCOVDataDefinition = new LinkedToCOVDataDefinition();
		linkedToCOVDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		
		ETLArtStartDateDataDefinition etlArtStartDateDataDefinition = new ETLArtStartDateDataDefinition();
		etlArtStartDateDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		
		PregnantDataDefinition pregnantDataDefinition = new PregnantDataDefinition();
		pregnantDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		
		BreastFeedingDataDefinition breastfeedingDataDefinition = new BreastFeedingDataDefinition();
		breastfeedingDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		
		DateStartedTbDataDefinition dateStartedTbDataDefinition = new DateStartedTbDataDefinition();
		dateStartedTbDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		
		TbUnitNumberDataDefinition tbUnitNumberDataDefinition = new TbUnitNumberDataDefinition();
		tbUnitNumberDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		
		dsd.addColumn("id", new IndexDataDefinition(), "");
		dsd.addColumn("Identifier", identifierDef, (String) null);
		dsd.addColumn("Name", nameDef, "");
		dsd.addColumn("Telephone", new PersonAttributeDataDefinition("Phone Number", phoneNumber), "",
		    new PersonAttributeDataConverter());
		dsd.addColumn("Alternative telephone", new PersonAttributeDataDefinition("Alternative Phone Number",
		        alternativePhoneNumber), "", new PersonAttributeDataConverter());
		dsd.addColumn("Age", new CustomAgeDataDefinition(), "", null);
		dsd.addColumn("Gender", new GenderDataDefinition(), "", null);
		dsd.addColumn("Pregnant", pregnantDataDefinition, "endDate=${endDate}");
		dsd.addColumn("Breastfeeding", breastfeedingDataDefinition, "endDate=${endDate}");
		dsd.addColumn("Date of ART initiation", etlArtStartDateDataDefinition, "endDate=${endDate}");
		dsd.addColumn("TB unit number", tbUnitNumberDataDefinition, "endDate=${endDate}");
		dsd.addColumn("Date Started TB", dateStartedTbDataDefinition, "endDate=${endDate}");
		dsd.addColumn("Payam", personPayamAddress(), "", new CalculationResultConverter());
		dsd.addColumn("Boma", personBomaAddress(), "", new CalculationResultConverter());
		dsd.addColumn("Name of COV", covNameDataDefinition, "endDate=${endDate}");
		dsd.addColumn("Linked to COV (Y/N)", linkedToCOVDataDefinition, "endDate=${endDate}");
		dsd.addColumn("Landmark", personLandmarkAddress(), "", new CalculationResultConverter());
		
		return dsd;
	}
}
