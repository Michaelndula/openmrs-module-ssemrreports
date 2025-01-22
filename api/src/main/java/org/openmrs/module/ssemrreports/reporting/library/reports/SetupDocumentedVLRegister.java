package org.openmrs.module.ssemrreports.reporting.library.reports;

import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.ssemrreports.manager.SsemrDataExportManager;
import org.openmrs.module.ssemrreports.reporting.library.cohorts.BaseCohortQueries;
import org.openmrs.module.ssemrreports.reporting.library.datasets.DocumentedVLDatasetDefinition;
import org.openmrs.module.ssemrreports.reporting.utils.SsemrReportUtils;
import org.openmrs.module.ssemrreports.reporting.utils.constants.reports.shared.SharedReportConstants;
import org.openmrs.module.ssemrreports.reporting.utils.constants.templates.shared.SharedTemplatesConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@Component
public class SetupDocumentedVLRegister extends SsemrDataExportManager {
	
	private final DocumentedVLDatasetDefinition documentedVlDatasetDefinition;
	
	private final BaseCohortQueries baseCohortQueries;
	
	@Autowired
	public SetupDocumentedVLRegister(DocumentedVLDatasetDefinition documentedVlDatasetDefinition,
	    BaseCohortQueries baseCohortQueries) {
		this.documentedVlDatasetDefinition = documentedVlDatasetDefinition;
		this.baseCohortQueries = baseCohortQueries;
	}
	
	@Override
	public String getExcelDesignUuid() {
		return SharedTemplatesConstants.PATIENTS_DOCUMENTED_VL_LIST_TEMPLATE;
	}
	
	@Override
	public String getUuid() {
		return SharedReportConstants.PATIENTS_DOCUMENTED_VL_LIST_UUID;
	}
	
	@Override
	public String getName() {
		return "Line Lists for clients with viral load samples collected";
	}
	
	@Override
	public String getDescription() {
		return "Line Lists for clients with viral load samples collected";
	}
	
	@Override
	public ReportDefinition constructReportDefinition() {
		ReportDefinition rd = new ReportDefinition();
		rd.setUuid(getUuid());
		rd.setName(getName());
		rd.setDescription(getDescription());
		rd.addParameters(documentedVlDatasetDefinition.getParameters());
		rd.addDataSetDefinition("DVLD",
		    Mapped.mapStraightThrough(documentedVlDatasetDefinition.constructDocumentedVLDatasetDefinition()));
		rd.setBaseCohortDefinition(SsemrReportUtils.map(baseCohortQueries.getPatientsWhoHaveDocumentedVL(),
		    "startDate=${startDate},endDate=${endDate+23h},location=${location}"));
		return rd;
	}
	
	@Override
	public String getVersion() {
		return "1.0-SNAPSHOT";
	}
	
	@Override
	public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
		ReportDesign reportDesign = null;
		try {
			reportDesign = createXlsReportDesign(reportDefinition, "documented_vl_register.xls",
			    "Line Lists for clients with viral load samples collected", getExcelDesignUuid(), null);
			Properties props = new Properties();
			props.put("repeatingSections", "sheet:1,row:4,dataset:DVLD");
			props.put("sortWeight", "5000");
			reportDesign.setProperties(props);
		}
		catch (IOException e) {
			throw new ReportingException(e.toString());
		}
		
		return Arrays.asList(reportDesign);
	}
}
