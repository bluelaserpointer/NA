package unit;

import core.GHQ;

public abstract class BasicEnemy extends BasicUnit{
	private static final long serialVersionUID = -5866340512027239900L;

	public BasicEnemy(int charaSize, int initialGroup) {
		super(charaSize, initialGroup);
	}
	
	@Override
	public void paint(boolean doAnimation) {
		if(!isAlive())
			return;
		super.paint(doAnimation);
		GHQ.paintHPArc((int) dynam.getX(), (int) dynam.getY(), 20,status.get(RED_BAR), status.getDefault(RED_BAR));
	}

}