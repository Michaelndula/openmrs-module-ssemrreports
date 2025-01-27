package org.openmrs.module.ssemrreports.reporting.library.reports;

import org.openmrs.Location;
import org.openmrs.module.reporting.ReportingException;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.ssemrreports.manager.SsemrDataExportManager;
import org.openmrs.module.ssemrreports.reporting.library.cohorts.BaseCohortQueries;
import org.openmrs.module.ssemrreports.reporting.library.datasets.MerIndicatorsDatasetDefinition;
import org.openmrs.module.ssemrreports.reporting.utils.SsemrReportUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class SetupMerTxPvlsIndicatorsReport extends SsemrDataExportManager {
	
	private final MerIndicatorsDatasetDefinition merIndicatorsDatasetDefinition;
	
	private final BaseCohortQueries baseCohortQueries;
	
	@Autowired
	public SetupMerTxPvlsIndicatorsReport(MerIndicatorsDatasetDefinition merIndicatorsDatasetDefinition,
	    BaseCohortQueries baseCohortQueries) {
		this.merIndicatorsDatasetDefinition = merIndicatorsDatasetDefinition;
		this.baseCohortQueries = baseCohortQueries;
	}
	
	@Override
	public String getExcelDesignUuid() {
		return "edaf9845-ec1b-4918-a055-fe315afe9ed7";
	}
	
	@Override
	public String getUuid() {
		return "6ba23385-a11a-4dc0-bcec-dbb02f93e43a";
	}
	
	@Override
	public String getName() {
		return "TX_PVLS MER Indicators Report";
	}
	
	@Override
	public String getDescription() {
		return "TX_PVLS  MER Indicators Reports";
	}
	
	@Override
	public ReportDefinition constructReportDefinition() {
		ReportDefinition rd = new ReportDefinition();
		String mappings = "startDate=${endDate-12m+1d},endDate=${endDate+23h},location=${location}";
		String mappingsQ4 = "startDate=${endDate-3m+1d},endDate=${endDate+23h},location=${location}";
		String mappingsQ3 = "startDate=${endDate-6m+1d},endDate=${endDate-3m+23h},location=${location}";
		String mappingsQ2 = "startDate=${endDate-9m+1d},endDate=${endDate-6m+23h},location=${location}";
		String mappingsQ1 = "startDate=${endDate-12m+1d},endDate=${endDate-9m+23h},location=${location}";
		rd.setUuid(getUuid());
		rd.setName(getName());
		rd.setDescription(getDescription());
		rd.addParameter(new Parameter("endDate", "End Date", Date.class));
		rd.addParameter(new Parameter("location", "Location", Location.class));
		rd.addDataSetDefinition("TxP", SsemrReportUtils.map(merIndicatorsDatasetDefinition.getTxPvlsDataset(), mappings));
		rd.addDataSetDefinition("TxPQ1", SsemrReportUtils.map(merIndicatorsDatasetDefinition.getTxPvlsDataset(), mappingsQ1));
		rd.addDataSetDefinition("TxPQ2", SsemrReportUtils.map(merIndicatorsDatasetDefinition.getTxPvlsDataset(), mappingsQ2));
		rd.addDataSetDefinition("TxPQ3", SsemrReportUtils.map(merIndicatorsDatasetDefinition.getTxPvlsDataset(), mappingsQ3));
		rd.addDataSetDefinition("TxPQ4", SsemrReportUtils.map(merIndicatorsDatasetDefinition.getTxPvlsDataset(), mappingsQ4));
		rd.setBaseCohortDefinition(SsemrReportUtils.map(baseCohortQueries.getAccurateClientsOnArtPerFacility(),
		    "endDate=${endDate},location=${location}"));
		
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
			reportDesign = createXlsReportDesign(reportDefinition, "tx_pvls_mer_indicators.xls",
			    "TX PVLS MER Indicators Report", getExcelDesignUuid(), null);
		}
		catch (IOException e) {
			throw new ReportingException(e.toString());
		}
		
		return Arrays.asList(reportDesign);
	}
}
