package org.openmrs.module.ssemrreports.reporting.library.reports;

import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.ssemrreports.manager.SsemrDataExportManager;
import org.openmrs.module.ssemrreports.reporting.library.cohorts.BaseCohortQueries;
import org.openmrs.module.ssemrreports.reporting.library.datasets.TbTreatmentDatasetDefinition;
import org.openmrs.module.ssemrreports.reporting.utils.SsemrReportUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@Component
public class SetupTbTreatmentRegister extends SsemrDataExportManager {
	
	private final TbTreatmentDatasetDefinition tbTreatmentDatasetDefinition;
	
	private final BaseCohortQueries baseCohortQueries;
	
	@Autowired
	public SetupTbTreatmentRegister(TbTreatmentDatasetDefinition tbTreatmentDatasetDefinition,
	    BaseCohortQueries baseCohortQueries) {
		this.tbTreatmentDatasetDefinition = tbTreatmentDatasetDefinition;
		this.baseCohortQueries = baseCohortQueries;
	}
	
	@Override
	public String getExcelDesignUuid() {
		return "ea49fa9e-ff13-44ce-8676-719ce87e3a2b";
	}
	
	@Override
	public String getUuid() {
		return "5d9bbc09-8c56-49c0-aa58-d9bb2a11277c";
	}
	
	@Override
	public String getName() {
		return "Line list of patients currently on TB Treatment";
	}
	
	@Override
	public String getDescription() {
		return "Line list of patients currently on TB Treatment";
	}
	
	@Override
	public ReportDefinition constructReportDefinition() {
		ReportDefinition rd = new ReportDefinition();
		rd.setUuid(getUuid());
		rd.setName(getName());
		rd.setDescription(getDescription());
		rd.addParameters(tbTreatmentDatasetDefinition.getParameters());
		rd.addDataSetDefinition("STB",
		    Mapped.mapStraightThrough(tbTreatmentDatasetDefinition.constructTbDatasetDefinition()));
		rd.setBaseCohortDefinition(SsemrReportUtils.map(baseCohortQueries.getPatientsInTbTreatment(),
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
			reportDesign = createXlsReportDesign(reportDefinition, "started_on_tb_register.xls",
			    "Line list of patients currently on TB Treatment", getExcelDesignUuid(), null);
			Properties props = new Properties();
			props.put("repeatingSections", "sheet:1,row:4,dataset:STB");
			props.put("sortWeight", "5000");
			reportDesign.setProperties(props);
		}
		catch (IOException e) {
			throw new ReportingException("Error creating report design", e);
		}
		
		return Arrays.asList(reportDesign);
	}
}
