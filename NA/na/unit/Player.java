package unit;

import static java.awt.event.KeyEvent.*;

import core.GHQ;
import engine.Engine_NA;
import item.Item;
import paint.DotPaintMultiple;
import paint.ImageFrame;
import storage.TableStorage;

public class Player extends BasicPlayer{
	private static final long serialVersionUID = 8121281285749873895L;
	
	public Player(int initialGroup) {
		super(20, initialGroup, new TableStorage<Item>(5,3));
	}

	@Override
	public final String getName() {
		return "Player";
	}
	
	@Override
	public final void loadImageData(){
		super.loadImageData();
		charaPaint = new DotPaintMultiple(new ImageFrame("thhimage/Marisa.png"));
		iconPaint = new ImageFrame("thhimage/MarisaIcon.png");
	}
	
	@Override
	public final void loadSoundData(){
	}
	
	//idle
	@Override
	public void extendIdle() {
		////////////
		//aim
		////////////
		dynam.setAngle(dynam.getMouseAngle());
		////////////
		//reload
		////////////
		if(Engine_NA.s_keyL.pullEvent(VK_R))
			mainWeapon.startReloadIfNotDoing();
		////////////
		//itemPick
		////////////
		final Item PICKED_ITEM = GHQ.getCoveredDropItem_pickup(this, charaSize);
		if(PICKED_ITEM != null)
			super.inventory.items.add(PICKED_ITEM);
		////////////
		//talk
		////////////
		if(Engine_NA.s_keyL.pullEvent(VK_SPACE)) {
			final Unit npc = GHQ.getNearstVisibleEnemy(this);
			if(npc instanceof BasicNPC && npc.dynam.getDistance(this.dynam) < 240) {
				((BasicNPC) npc).startTalk();
			}
		}
	}
}