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
import org.openmrs.module.ssemrreports.reporting.library.data.definition.*;
import org.openmrs.module.ssemrreports.reporting.utils.constants.reports.shared.SharedReportConstants;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class AllClientsDatasetDefinition extends SsemrBaseDataSet {
	
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
	
	public DataSetDefinition constructAllClientsDatasetDefinition() {
		
		PatientDataSetDefinition dsd = new PatientDataSetDefinition();
		dsd.setName("ALL");
		dsd.setDescription("All Clients");
		dsd.addParameters(getParameters());
		dsd.addSortCriteria("PatientSequenceNumber", SortCriteria.SortDirection.ASC);
		dsd.addParameter(new Parameter("location", "Location", Location.class));
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
		
		ArtStartDateDataDefinition artStartDateDataDefinition = new ArtStartDateDataDefinition();
		artStartDateDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		
		RegimenDataDefinition regimenDataDefinition = new RegimenDataDefinition();
		regimenDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		
		PregnantDataDefinition patientPregnantDataDefinition = new PregnantDataDefinition();
		patientPregnantDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		
		BreastFeedingDataDefinition patientBreastfeedingDateDataDefinition = new BreastFeedingDataDefinition();
		patientBreastfeedingDateDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		
		NextAppointmentDateDataDefinition nextAppointmentDateDataDefinition = new NextAppointmentDateDataDefinition();
		nextAppointmentDateDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		
		VLDueDateDataDefinition vlDueDateDataDefinition = new VLDueDateDataDefinition();
		vlDueDateDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		
		RepeatVLResultDataDefinition repeatVLResultDataDefinition = new RepeatVLResultDataDefinition();
		repeatVLResultDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		
		LastVLDataDefinition lastVLDataDefinition = new LastVLDataDefinition();
		lastVLDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		
		LastEAC1DateDataDefinition lastEAC1DateDataDefinition = new LastEAC1DateDataDefinition();
		lastEAC1DateDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		
		LastEAC2DateDataDefinition lastEAC2DateDataDefinition = new LastEAC2DateDataDefinition();
		lastEAC2DateDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		
		LastEAC3DateDataDefinition lastEAC3DateDataDefinition = new LastEAC3DateDataDefinition();
		lastEAC3DateDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		
		DateExtendedEACDataDefinition dateExtendedEACDataDefinition = new DateExtendedEACDataDefinition();
		dateExtendedEACDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		
		Reached28DaysAfterIITDateDataDefinition reached28DaysAfterIITDateDataDefinition = new Reached28DaysAfterIITDateDataDefinition();
		reached28DaysAfterIITDateDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		
		ReturnToTreatmentDateDataDefinition returnToTreatmentDateDataDefinition = new ReturnToTreatmentDateDataDefinition();
		returnToTreatmentDateDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		
		COVNameDataDefinition covNameDataDefinition = new COVNameDataDefinition();
		covNameDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		
		WeightDataDefinition weightDataDefinition = new WeightDataDefinition();
		weightDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		
		BMIDataDefinition bmiDataDefinition = new BMIDataDefinition();
		bmiDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		
		ClientRepresentedDataDefinition clientRepresentedDataDefinition = new ClientRepresentedDataDefinition();
		clientRepresentedDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		
		TBScreeningDataDefinition tbScreeningDataDefinition = new TBScreeningDataDefinition();
		tbScreeningDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		
		TBStatusDataDefinition tbStatusDataDefinition = new TBStatusDataDefinition();
		tbStatusDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		
		EDDDataDefinition eddDataDefinition = new EDDDataDefinition();
		eddDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		
		LastRefillDateDefinition lastRefillDateDefinition = new LastRefillDateDefinition();
		lastRefillDateDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		
		DateVLSampleCollectedDataDefinition dateVLSampleCollectedDataDefinition = new DateVLSampleCollectedDataDefinition();
		dateVLSampleCollectedDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		
		DateVLSampleReceivedDataDefinition dateVLSampleReceivedDataDefinition = new DateVLSampleReceivedDataDefinition();
		dateVLSampleReceivedDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		
		RepeatVLSampleDateDataDefinition repeatVLSampleDateDataDefinition = new RepeatVLSampleDateDataDefinition();
		repeatVLSampleDateDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		
		CHWPhoneDataDefinition chwPhoneDataDefinition = new CHWPhoneDataDefinition();
		chwPhoneDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		
		SuspectedVirologicalFailureDataDefinition suspectedVirologicalFailureDataDefinition = new SuspectedVirologicalFailureDataDefinition();
		suspectedVirologicalFailureDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		
		AHDDataDefinition ahdDataDefinition = new AHDDataDefinition();
		ahdDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		
		ClinicalStatusDataDefinition clinicalStatusDataDefinition = new ClinicalStatusDataDefinition();
		clinicalStatusDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		
		RecurrenceOfIITDataDefinition recurrenceOfIITDataDefinition = new RecurrenceOfIITDataDefinition();
		recurrenceOfIITDataDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
		
		dsd.addColumn("id", new IndexDataDefinition(), "");
		dsd.addColumn("Identifier", identifierDef, (String) null);
		dsd.addColumn("Name", nameDef, "");
		dsd.addColumn("Age", new CustomAgeDataDefinition(), "", null);
		dsd.addColumn("Gender", new GenderDataDefinition(), "", null);
		dsd.addColumn("Weight", weightDataDefinition, "endDate=${endDate}");
		dsd.addColumn("BMI", bmiDataDefinition, "endDate=${endDate}");
		dsd.addColumn("Regimen", regimenDataDefinition, "endDate=${endDate}");
		dsd.addColumn("Date of ART initiation", artStartDateDataDefinition, "endDate=${endDate}");
		dsd.addColumn("Client Represented", clientRepresentedDataDefinition, "endDate=${endDate}");
		dsd.addColumn("Screened for TB", tbScreeningDataDefinition, "endDate=${endDate}");
		dsd.addColumn("TB Status", tbStatusDataDefinition, "endDate=${endDate}");
		dsd.addColumn("Pregnant", patientPregnantDataDefinition, "endDate=${endDate}");
		dsd.addColumn("EDD", eddDataDefinition, "endDate=${endDate}");
		dsd.addColumn("Breastfeeding", patientBreastfeedingDateDataDefinition, "endDate=${endDate}");
		dsd.addColumn("Last Refill Date", lastRefillDateDefinition, "endDate=${endDate}");
		dsd.addColumn("Next Appointment Date", nextAppointmentDateDataDefinition, "endDate=${endDate}");
		dsd.addColumn("VL Due date", vlDueDateDataDefinition, "endDate=${endDate}");
		dsd.addColumn("Repeat Viral Load result", repeatVLResultDataDefinition, "endDate=${endDate}");
		dsd.addColumn("Last VL result", lastVLDataDefinition, "endDate=${endDate}");
		dsd.addColumn("Date EAC1 Received", lastEAC1DateDataDefinition, "endDate=${endDate}");
		dsd.addColumn("Date EAC2 Received", lastEAC2DateDataDefinition, "endDate=${endDate}");
		dsd.addColumn("Date EAC3 Received", lastEAC3DateDataDefinition, "endDate=${endDate}");
		dsd.addColumn("Date Extended EAC Received", dateExtendedEACDataDefinition, "endDate=${endDate}");
		dsd.addColumn("Date reached 28 days after missed appointment", reached28DaysAfterIITDateDataDefinition,
		    "endDate=${endDate}");
		dsd.addColumn("Date returned to treatment", returnToTreatmentDateDataDefinition, "endDate=${endDate}");
		dsd.addColumn("Recurrence of IIT", recurrenceOfIITDataDefinition, "endDate=${endDate}");
		dsd.addColumn("Last VL sample collection Date", dateVLSampleCollectedDataDefinition, "endDate=${endDate}");
		dsd.addColumn("Date VL result received", dateVLSampleReceivedDataDefinition, "endDate=${endDate}");
		dsd.addColumn("Date Repeat Viral Load sample collected", repeatVLSampleDateDataDefinition, "endDate=${endDate}");
		dsd.addColumn("Suspected virological failure", suspectedVirologicalFailureDataDefinition, "endDate=${endDate}");
		dsd.addColumn("Advanced HIV Disease", ahdDataDefinition, "endDate=${endDate}");
		dsd.addColumn("Clinical Status", clinicalStatusDataDefinition, "endDate=${endDate}");
		dsd.addColumn("Telephone", new PersonAttributeDataDefinition("Phone Number", phoneNumber), "",
		    new PersonAttributeDataConverter());
		dsd.addColumn("Alternative telephone", new PersonAttributeDataDefinition("Alternative Phone Number",
		        alternativePhoneNumber), "", new PersonAttributeDataConverter());
		dsd.addColumn("Payam", personPayamAddress(), "", new CalculationResultConverter());
		dsd.addColumn("Boma", personBomaAddress(), "", new CalculationResultConverter());
		dsd.addColumn("Landmark", personLandmarkAddress(), "", new CalculationResultConverter());
		dsd.addColumn("CHW", covNameDataDefinition, "endDate=${endDate}");
		dsd.addColumn("CHW Phone", chwPhoneDataDefinition, "endDate=${endDate}");
		
		return dsd;
	}
}
