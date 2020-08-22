package model.project.dimension;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import algorithms.bayesian_network.DimensionNet;
import algorithms.bayesian_network.RiskNet;
import config.Configuaration;
import model.project.Project;
import model.project.risk.Risk;
import model.project.risk.RiskInfo;
import model.project.task.Task;
import service.project.risk.RiskServiceImpl;
import service.project.risk.RiskServiceInterface;
import utils.Utils;

public class Dimension extends Project{
	public String dimensionId;
	public Map<String,Double> taskDeadlineMap;
	public Dimension(String path,double deadline,String dimensionId) {
		super(path,deadline);
		this.dimensionId = dimensionId;
	}
	public void calcProb() {
		update();
		// calc prob
		List<Task> tasks = getTasks();
		
		// tinh toan rui ro cho tung task trong demension 
		// readfile risk_info neu dimension id == dimension_id thi cho them risk_prob 
		List<RiskInfo> riskInfoList = readRiskInfo(Configuaration.inputPath+"risk_info.csv");
		int checkRisk;
		for(Task t:tasks) {
			double taskProb = Utils.gauss(taskDeadlineMap.get(t.getName()),t.getMostlikely(),t.getStandardDeviation());
			checkRisk =0;
			DimensionNet DNet = new DimensionNet("Task"+t.getName()+" Dimension "+dimensionId);
			for(RiskInfo r: riskInfoList) {
				if(r.check(this.dimensionId,t.getName())) {
					checkRisk =1;
//					System.out.println("d :"+this.dimensionId+" task: "+t.getName());
					double riskProb = getRiskProb(Configuaration.inputPath+"risk_relation.csv", Configuaration.inputPath+"risk_distribution.csv");
					DNet.setRiskProb(riskProb);
					DNet.setTaskProb(taskProb);
					t.setProb(DNet.calcProb());
				}
			}
			if(checkRisk ==0) {
				t.setProb(taskProb);
			}
		}
	}
	public double getRiskProb(String relatePath,String disPath) {
		RiskServiceInterface riskServices = new RiskServiceImpl();
		// get informations of risk from data input
//		List<Risk> allRisks = riskServices.readRiskRelationInfo(Configuaration.inputPath+"risk_relation.csv");
		List<Risk> allRisks = riskServices.readRiskRelationInfo(relatePath);
//		riskServices.readRiskDistribution(Configuaration.inputPath+"risk_distribution.csv", allRisks);
		riskServices.readRiskDistribution(disPath, allRisks);
		// init bayesian network for all risks
		RiskNet riskModel = new RiskNet("Riskmanagement of project",allRisks);
		riskModel.createNet();
		// calc prob of all risks and update probs
		riskModel.updateRiskProb();
		double result = allRisks.get(allRisks.size()-1).getProbability();
//		System.out.println(result);
		return result;
	}
	public List<RiskInfo> readRiskInfo(String path) {
		List<RiskInfo> riskInfoList = new ArrayList<RiskInfo>();
		try {
			FileReader fileReader = new FileReader(path);
			CSVReader csvReader = new CSVReaderBuilder(fileReader) 
                    .withSkipLines(1) 
                    .build(); 
			String [] nextRecord;
			while ((nextRecord = csvReader.readNext()) != null) { 
	            riskInfoList.add(new RiskInfo(nextRecord));
	        } 
			
		} catch (Exception e) {
			System.out.println(e);
		}
		return riskInfoList;
	}
	public Map<String, Double> getTaskDeadlineMap() {
		return taskDeadlineMap;
	}
	public void setTaskDeadlineMap(Map<String, Double> taskDeadlineMap) {
		this.taskDeadlineMap = taskDeadlineMap;
	}
	public String getDimensionId() {
		return dimensionId;
	}
	public void setDimensionId(String dimensionId) {
		this.dimensionId = dimensionId;
	}
	
}