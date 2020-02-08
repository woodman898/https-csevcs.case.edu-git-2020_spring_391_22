package first_agents.edu.cwru.sepia.agent;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.action.ActionType;
import edu.cwru.sepia.action.TargetedAction;
import edu.cwru.sepia.agent.Agent;
import edu.cwru.sepia.environment.model.history.History.HistoryView;
import edu.cwru.sepia.environment.model.state.ResourceNode.Type;
import edu.cwru.sepia.environment.model.state.ResourceType;
import edu.cwru.sepia.environment.model.state.State.StateView;
import edu.cwru.sepia.environment.model.state.Template.TemplateView;
import edu.cwru.sepia.environment.model.state.Unit.UnitView;
import edu.cwru.sepia.util.Direction;

public class FirstClass extends Agent
{
	
	private static final long serialVersionUID = -7481143097108592969L;
	
	public FirstClass(int playernum)
	{
		super(playernum);
		
		System.out.println("Constructed My First Agent");
		// TODO Auto-generated constructor stub
	}

	@Override
	public Map<Integer, Action> initialStep(StateView newstate, HistoryView statehistory)
	{
		return middleStep(newstate, statehistory);
	}

	@Override
	public void loadPlayerData(InputStream arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public Map middleStep(StateView newstate, HistoryView statehistory)
	{
		// TODO Auto-generated method stub
		
		Map<Integer, Action> actions = new HashMap<Integer, Action>();
//		
		List<Integer> unitIDs = newstate.getUnitIds(playernum);
//		
//		for(Integer unitID : unitIDs) {
//			UnitView unitView = newstate.getUnit(unitID);
//			TemplateView templateView = unitView.getTemplateView();
//			System.out.println(templateView.getName() + ": " + unitID);
//		}
//		
		List<Integer> myUnitIds = newstate.getUnitIds(playernum);
		
		List<Integer> peasantIds = new ArrayList<Integer>();
		
		List<Integer> townhallIds = new ArrayList<Integer>();
		
		List<Integer> barracksIds = new ArrayList<Integer>();
		
		List<Integer> footmanIds = new ArrayList<Integer>();
		
		List<Integer> farmsIds = new ArrayList<Integer>();
		
		int currentGold = newstate.getResourceAmount(playernum, ResourceType.GOLD);
		
		int currentWood = newstate.getResourceAmount(playernum, ResourceType.WOOD);
		
		List<Integer> goldMines = newstate.getResourceNodeIds(Type.GOLD_MINE);
		
		List<Integer> trees = newstate.getResourceNodeIds(Type.TREE);
		
		for(Integer unitID : myUnitIds) {
			UnitView unit = newstate.getUnit(unitID);
			String unitTypeName = unit.getTemplateView().getName();
			
			if(unitTypeName.equals("TownHall")) {
				townhallIds.add(unitID);
			}else if(unitTypeName.equals("Peasant")) {
				peasantIds.add(unitID);
			}else if(unitTypeName.equals("Barracks")) {
				barracksIds.add(unitID);
			}else if(unitTypeName.equals("Footman")) {
				footmanIds.add(unitID);
			}else if(unitTypeName.equals("Farm")) {
				farmsIds.add(unitID);
			}else {
				System.out.println("Unexpexted Unit type: " + unitTypeName);
			}
		}
		
		for(Integer peasantID : peasantIds) {
			Action action = null;

			if(newstate.getUnit(peasantID).getCargoAmount() > 0) {
				action = new TargetedAction(peasantID, ActionType.COMPOUNDDEPOSIT, townhallIds.get(0));
			}else {
				if(currentGold < currentWood) {
					action = new TargetedAction(peasantID, ActionType.COMPOUNDGATHER, goldMines.get(0));
					
				}else {
					action = new TargetedAction(peasantID, ActionType.COMPOUNDGATHER, trees.get(0));
					
				}
			}
			
			actions.put(peasantID, action);
		}
	
		if((currentGold >= 700) && (currentWood >= 400) && (barracksIds.size() < 1)) {
			TemplateView barrackTemplate = newstate.getTemplate(playernum, "Barracks");
			
			int barrackTemplateID = barrackTemplate.getID();
			int peasantsID = peasantIds.get(0);
			
			actions.put(peasantsID, Action.createCompoundProduction(peasantsID, barrackTemplateID));
			 
		} else if ((currentGold >= 500) && (currentWood >= 250) && (farmsIds.size() < 1)) {
			TemplateView farmTemplate = newstate.getTemplate(playernum, "Farm");
			
			int farmTemplateID = farmTemplate.getID();
			int peasantsID = peasantIds.get(0);
			
			actions.put(peasantsID, Action.createCompoundProduction(peasantsID, farmTemplateID));
		} else if ((currentGold >= 600) && (footmanIds.size() < 2) && (barracksIds.size() > 0)) {
			TemplateView footmanTemplate = newstate.getTemplate(playernum, "Footman");
			
			int footmanTemplateID = footmanTemplate.getID();
			int barracksId = barracksIds.get(0);
			
			actions.put(barracksId, Action.createCompoundProduction(barracksId, footmanTemplateID));
			
		}
		
		return actions;
	}

	@Override
	public void savePlayerData(OutputStream arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void terminalStep(StateView newstate, HistoryView statehistory)
	{
		System.out.println("Finished the spisode");

	}

}
