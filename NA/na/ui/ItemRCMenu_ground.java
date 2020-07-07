package ui;

import java.awt.Color;
import java.awt.event.MouseEvent;

import core.GHQ;
import engine.NAGame;
import gui.ClickMenu;
import gui.TextButton;
import item.ItemData;
import paint.ColorFilling;
import paint.ColorFraming;

public class ItemRCMenu_ground extends ClickMenu<ItemData> {
	public ItemRCMenu_ground() {
		super(80, 20);
		super.setBGColor(Color.WHITE);
		addNewLine(new TextButton("捡起", new ColorFilling(Color.LIGHT_GRAY), new ColorFraming(Color.GRAY, GHQ.stroke1)) {
			@Override
			public boolean clicked(MouseEvent e) {
				super.clicked(e);
				get().pickup(NAGame.controllingUnit());
				NAGame.controllingUnit().addItemToStorage(get());
				disableMenu();
				return true;
			}
		});
		addNewLine(new TextButton("调查", new ColorFilling(Color.GRAY), new ColorFraming(Color.GRAY, GHQ.stroke1)));
	}
	@Override
	public void idle() {
		super.idle();
		if(get().point().distance(NAGame.controllingUnit()) > NAGame.controllingUnit().width()*2) {
			disableMenu();
		}
	}
	private void disableMenu() {
		super.disable();
	}
}