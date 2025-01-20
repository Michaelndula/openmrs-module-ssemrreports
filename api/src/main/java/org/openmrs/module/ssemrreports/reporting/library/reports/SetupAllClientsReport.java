package org.openmrs.module.ssemrreports.reporting.library.reports;

import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.ssemrreports.manager.SsemrDataExportManager;
import org.openmrs.module.ssemrreports.reporting.library.cohorts.BaseCohortQueries;
import org.openmrs.module.ssemrreports.reporting.library.datasets.AllClientsDatasetDefinition;
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
public class SetupAllClientsReport extends SsemrDataExportManager {
	
	private final AllClientsDatasetDefinition allClientsDatasetDefinition;
	
	private final BaseCohortQueries baseCohortQueries;
	
	@Autowired
	public SetupAllClientsReport(AllClientsDatasetDefinition allClientsDatasetDefinition, BaseCohortQueries baseCohortQueries) {
		this.allClientsDatasetDefinition = allClientsDatasetDefinition;
		this.baseCohortQueries = baseCohortQueries;
	}
	
	@Override
	public String getExcelDesignUuid() {
		return SharedTemplatesConstants.ALL_CLIENTS_TEMPLATE;
	}
	
	@Override
	public String getUuid() {
		return SharedReportConstants.ALL_CLIENTS_LIST_UUID;
	}
	
	@Override
	public String getName() {
		return "Line list of all Client";
	}
	
	@Override
	public String getDescription() {
		return "Line list of all Client";
	}
	
	@Override
	public ReportDefinition constructReportDefinition() {
		String mappings = "endDate=${endDate+23h}";
		
		ReportDefinition rd = new ReportDefinition();
		rd.setUuid(getUuid());
		rd.setName(getName());
		rd.setDescription(getDescription());
		rd.addParameters(allClientsDatasetDefinition.getParameters());
		rd.addDataSetDefinition("ALL",
		    SsemrReportUtils.map(allClientsDatasetDefinition.constructAllClientsDatasetDefinition(), mappings));
		rd.setBaseCohortDefinition(SsemrReportUtils.map(baseCohortQueries.getAllClients(), mappings));
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
			reportDesign = createXlsReportDesign(reportDefinition, "line_list_of_all_clients.xls",
			    "Line list of all Client", getExcelDesignUuid(), null);
			Properties props = new Properties();
			props.put("repeatingSections", "sheet:1,row:4,dataset:ALL");
			props.put("sortWeight", "5000");
			reportDesign.setProperties(props);
		}
		catch (IOException e) {
			throw new ReportingException(e.toString());
		}
		
		return Arrays.asList(reportDesign);
	}
}
