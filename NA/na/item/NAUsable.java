package item;

import paint.dot.DotPaint;
import preset.item.Usable;

public interface NAUsable extends Usable {
	public static final NAUsable NULL_NA_USABLE = new NAUsable() {
		@Override
		public void use() {}
		@Override
		public DotPaint getDotPaint() {
			return DotPaint.BLANK_SCRIPT;
		}
	};
	public default boolean supportSerialUse() {
		return false;
	}
}
