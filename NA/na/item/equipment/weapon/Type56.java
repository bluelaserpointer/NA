package item.equipment.weapon;

import java.util.LinkedList;

import bullet.BulletLibrary;
import core.GHQ;
import core.GHQObject;
import damage.DamageMaterial;
import item.ammo.AmmoType;
import item.equipment.weapon.gripStyle.RifleGrip;
import paint.ImageFrame;
import paint.dot.DotPaintResizer;
import physics.HitGroup;
import preset.bullet.Bullet;
import unit.NAUnit;
import weapon.Weapon;

public class Type56 extends NAFirearms {
	public Type56() {
		super(new DotPaintResizer(ImageFrame.create("picture/weapon/56.png"), 1.2));
		super.setGripStyle(new RifleGrip() {
			@Override
			public int[] handXPositions() {
				return new int[] {2, 22};
			}
			@Override
			public int[] handYPositions() {
				return new int[] {4, 3};
			}
		});
	}
	@Override
	public Weapon def_weapon() {
		return new Weapon() {
			{
				name = "ACCAR";
				magazineSize = 30;
				setCoolTime(2); //original is 50
				setReloadTime(150);
				//original is semi-automatic
			}
			@Override
			public LinkedList<Bullet> setBullets(GHQObject shooter, HitGroup standpoint) {
				final Bullet bullet = GHQ.stage().addBullet(new BulletLibrary.BaseBullet(this, shooter, standpoint));
				bullet.setDamage(DamageMaterial.Phy.makeDamage(AmmoType._7d62.weight*50*50).setKnockbackRate(0.3));
				bullet.point().addXY_allowsMoveAngle(0, 50);

				final LinkedList<Bullet> firedBullets = new LinkedList<>();
				firedBullets.add(bullet);
				return firedBullets;
			}
			@Override
			public double getLeftAmmo() {
				if(!hasOwner())
					return 0;
				return currentReloadRule.reloadableAmmoAmount(((NAUnit)owner).ammoStorage);
			}
			@Override
			public void consumeAmmo(double value) {
				currentReloadRule.reloadAmmo(Type56.this, (int)value);
			}
		};
	}
	@Override
	public String name() {
		return weapon.name;
	}
	@Override
	public double weight() {
		return 3.85; //3.85kg
	}
	@Override
	public AmmoType usingAmmoType() {
		return AmmoType._7d62;
	}
	@Override
	public boolean supportSerialUse() {
		return false;
	}
}
