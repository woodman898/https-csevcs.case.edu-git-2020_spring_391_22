package first_agents.edu.cwru.sepia.agent;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.action.ActionFeedback;
import edu.cwru.sepia.action.ActionResult;
import edu.cwru.sepia.agent.Agent;
import edu.cwru.sepia.environment.model.history.DamageLog;
import edu.cwru.sepia.environment.model.history.History.HistoryView;
import edu.cwru.sepia.environment.model.state.State.StateView;
import edu.cwru.sepia.environment.model.state.Unit.UnitView;

public class MyCombatAgent extends Agent {
	
	private int enemyPlayerNum = 1;

	public MyCombatAgent(int playernum, String[] otherargs) {
		super(playernum);
		
		if(otherargs.length > 0)
		{
			enemyPlayerNum = new Integer(otherargs[0]);
		}
		
		System.out.println("Constructed MyCombatAgent");
	}

	@Override
	public Map<Integer, Action> initialStep(StateView newstate,
			HistoryView statehistory) {
		Map<Integer, Action> actions = new HashMap<Integer, Action>();
		
		List<Integer> myUnitIDs = newstate.getUnitIds(playernum);
		
		List<Integer> enemyUnitIDs = newstate.getUnitIds(enemyPlayerNum);
		
		if(enemyUnitIDs.size() == 0)
		{
			return actions;
		}
		for(Integer myUnitID : myUnitIDs)
		{
			actions.put(myUnitID, Action.createCompoundAttack(myUnitID, enemyUnitIDs.get(0)));
		}
		
		return actions;
	}

	@Override
	public Map<Integer, Action> middleStep(StateView newstate,HistoryView statehistory) {
		
		Map<Integer, Action> actions = new HashMap<Integer, Action>();
		List<Integer> myUnitIds = newstate.getUnitIds(playernum);
	
		List<Integer> footmanIds = new ArrayList<Integer>();
		List<Integer> archerIds = new ArrayList<Integer>();
		List<Integer> ballistaIds = new ArrayList<Integer>();
		
		List<Integer> enemyUnitIDs = newstate.getUnitIds(enemyPlayerNum);
		
		int targetX = 0;
		int targetY = 0;
		if (archerIds.size() > 0) {
			UnitView archer = newstate.getUnit(archerIds.get(0));
			targetX = archer.getXPosition();
			targetY = archer.getYPosition();
		}
		
		for(Integer unitID : myUnitIds) {
			UnitView unit = newstate.getUnit(unitID);
			
			String unitTypeName = unit.getTemplateView().getName();
			
			if(unitTypeName.toLowerCase().equals("footman")) {
				footmanIds.add(unitID);
			}else if(unitTypeName.toLowerCase().equals("archer")) {
				archerIds.add(unitID);
			}else if(unitTypeName.toLowerCase().equals("ballista")) {
				ballistaIds.add(unitID);
			}else
				System.err.println("Unexpected Unit type: "+ unitTypeName);
		}
		
		
		
		if(enemyUnitIDs.size() == 0)
		{
			return actions;
		}
		
		int currentStep = newstate.getTurnNumber();
		
		
		for(ActionResult feedback : statehistory.getCommandFeedback(playernum, currentStep-1).values())
		{
				int unitID = feedback.getAction().getUnitId();
				
				List<DamageLog> damageLogs = statehistory.getDamageLogs(currentStep);
				
				for (Integer footmanID : footmanIds) {
					actions.put(footmanID, Action.createCompoundMove(footmanID, 16, 8));
					if (currentStep >= 180) {
						actions.put(footmanID, Action.createCompoundMove(footmanID, 15, 13));
					}
					if (currentStep >= 220) {
						actions.put(footmanID, Action.createCompoundAttack(footmanID, enemyUnitIDs.get(0)));
					}
				}
				for (Integer archer : archerIds) {
					actions.put(archer, Action.createCompoundMove(archer, 16, 11));
					if (currentStep >= 200) {
						actions.put(archer, Action.createCompoundAttack(archer, enemyUnitIDs.get(0)));
					}
				}
				for (Integer ballista : ballistaIds) {
					actions.put(ballista, Action.createCompoundMove(ballista, 20, 13));
					if (currentStep >= 500) {
						actions.put(ballista, Action.createCompoundAttack(ballista, enemyUnitIDs.get(0)));
					}
				}
		}

		return actions;
	}

	@Override
	public void terminalStep(StateView newstate, HistoryView statehistory) {
		System.out.println("Finished the episode");
	}

	@Override
	public void savePlayerData(OutputStream os) {
		// TODO Auto-generated method stub

	}

	@Override
	public void loadPlayerData(InputStream is) {
		// TODO Auto-generated method stub

	}

}