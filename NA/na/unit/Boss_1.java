package unit;

import java.util.HashMap;

import bullet.Bullet;
import calculate.Damage;
import core.GHQ;
import damage.DamageMaterialType;
import damage.NADamage;
import paint.ImageFrame;
import paint.animation.SerialImageFrame;
import paint.dot.DotPaint;
import physics.Angle;
import physics.Point;
import unit.action.NAAction;
import unit.body.HumanBody;
import weapon.Knife;

public class Boss_1 extends NAUnit {
	final StingerStrike stingerStrike = new StingerStrike(body);
	final BackStab backStab = new BackStab(body);
	final HookThrow hookthrow = new HookThrow(body);
	final Stinger stinger = new Stinger(body);
	final KnifeThrow knifeThrow = new KnifeThrow(body);
	
	int lastPerfectDodgeFrame;
	public Boss_1() {
		super(70);
		this.POW_FIXED.setNumber(7);
		this.AGI_FIXED.setNumber(10);
		{
			body().trunk().setBasePaint(ImageFrame.create("picture/boss_1/Standing.png"));
			body().head().setBasePaint(DotPaint.BLANK_SCRIPT);
			body().legs().setBasePaint(DotPaint.BLANK_SCRIPT);
			body().foots().setBasePaint(DotPaint.BLANK_SCRIPT);
		}
		{
			final HashMap<BodyParts, DotPaint> dotPaints = new HashMap<BodyParts, DotPaint>();
			dotPaints.put(body().trunk(), body().trunk().getDotPaint());
			body().addDoableActionAnimations(StingerStrike.class, dotPaints);
			body().addDoableActionAnimations(BackStab.class, dotPaints);
			body().addDoableActionAnimations(HookThrow.class, dotPaints);
			body().addDoableActionAnimations(Stinger.class, dotPaints);
		}
		{
			final HashMap<BodyParts, DotPaint> dotPaints = new HashMap<BodyParts, DotPaint>();
			final String[] imageList = new String[5];
			imageList[0] = "picture/boss_1/knifeThrow/KnifeThrow_1.png";
			imageList[1] = "picture/boss_1/knifeThrow/KnifeThrow_2.png";
			imageList[2] = "picture/boss_1/knifeThrow/KnifeThrow_3.png";
			imageList[3] = "picture/boss_1/knifeThrow/KnifeThrow_4.png";
			imageList[4] = "picture/boss_1/knifeThrow/KnifeThrow_5.png";
			dotPaints.put(body().trunk(), new SerialImageFrame(2, imageList));
			body().addDoableActionAnimations(KnifeThrow.class, dotPaints);
		}
		this.setInvisible(true);
	}
	@Override
	public final Boss_1 respawn(int x, int y) {
		super.respawn(x, y);
		equip(addItem(new Knife()));
		super.currentWeaponBodyParts = body().melleEquipSlot();
		return this;
	}
	private int lastAttackedFrame;
	@Override
	public void damage(Damage damage, Bullet bullet) {
		/*//try perfect dodge
		if(GHQ.passedFrame(lastPerfectDodgeFrame)*GHQ.getSPF() >= 15 && this.GREEN_BAR.doubleValue() >= 20.0) {
			lastPerfectDodgeFrame = GHQ.nowFrame();
			final double rollingAngle;
			if(bullet.point().isStop())
				rollingAngle = Angle.random();
			else
				rollingAngle = bullet.point().moveAngle() + (Math.random() < 0.5 ? +1 : -1)*Math.PI/2;
			body().rolling.setRolling(SPEED_PPS.doubleValue()/10, rollingAngle);
			return;
		}else*/
			super.damage(damage, bullet);
	}
	@Override
	protected void attack() {
		if(targetUnit != null) {
			final double targetAngle = point().angleTo(targetUnit);
			final double angleDiff = angle().spinTo_Suddenly(targetAngle, 10);
			final double distance = point().distance(targetUnit);
			if(hasAttackAction()) //just do spin
				return;
			//A: move to safe place to turn on invisible mode
			/*if(!this.invisibled) {
				if(!targetUnit.isVisible(this))
					invisibled = true;
				else {
					final double speed = SPEED_PPS.doubleValue();
					point().addXY(-speed*Math.cos(targetAngle), -speed*Math.sin(targetAngle));
				}
			}*/
			//B: attack only if invisible mode is activated
			if(currentEquipment() != null) {
				if(distance < 500) {
					angle().set(point().angleTo(targetUnit));
					this.setInvisible(false);
					//attack
					final double chance = Math.random();
					if(GHQ.passedFrame(lastAttackedFrame) > 80) {
						lastAttackedFrame = GHQ.nowFrame();
						if(chance > 0.3)
							body().action(stingerStrike);
						else
							body().action(knifeThrow);
					}
				}
			} else {
				if(angle().isDeltaSmaller(targetAngle, Math.PI*10/18)) {
					if(angleDiff < 0.30 && distance < 50) {
						body().punch.setPunch();
					}
				}
			}
		}else if(isBattleStance) {
			if(GHQ.nowFrame() % 80 == 0) {
				suspiciousAngle = Angle.random();
			}
			angle().spinTo_Suddenly(suspiciousAngle, 10);
		}else
			angle().spinTo_Suddenly(point().moveAngle(), 10);
	}
	@Override
	public UnitGroup unitGroup() {
		return UnitGroup.GUARD;
	}
	@Override
	public final void loadImageData(){
		super.loadImageData();
		charaPaint = ImageFrame.create("picture/grayman.png");
	}
	private final boolean hasAttackAction() {
		return body().trunk().hasAction();
	}
}
class KnifeThrow extends NAAction {
	public KnifeThrow(Body body) {
		super(body, 100);
	}
	@Override
	public void idle() {
		super.stopActionIfFramePassed(10);
	}
	@Override
	public boolean precondition() {
		return GHQ.passedFrame(initialFrame) > 15 && ((NAUnit)owner()).GREEN_BAR.doubleValue() > 20;
	}
	@Override
	public void activated() {
		super.activated();
		//dmg: POW + AGI*2
		//stamina: -20p
		((NAUnit)owner()).setInvisible(false);
		GHQ.stage().addBullet(new Bullet(owner()) {
			{
				name = "Boss_1's knife";
				damage = new NADamage(((NAUnit)owner()).POW_FLOAT.doubleValue() + 2*((NAUnit)owner()).AGI_FLOAT.doubleValue());
				point().setSpeed(45);
				paintScript = ImageFrame.create("picture/knife.png");
			}
			@Override
			public void paint() {
				paintScript.dotPaint_turnAndCapSize(point(), point().moveAngle(), 70);
			}
		});
		((NAUnit)owner()).GREEN_BAR.consume(20);
	}
	@Override
	public void stopped() {
		((NAUnit)owner()).setInvisible(true);
	}
}
class Stinger extends NAAction {
	NAUnit targetUnit;
	public Stinger(Body body) {
		super(body, 100);
	}
	@Override
	public void idle() {
		super.stopActionIfFramePassed(5);
		if(owner().point().distance(targetUnit) > 100)
			super.stopAction();
		targetUnit.body().damaged.set();
	}
	@Override
	public boolean precondition() {
		return ((NAUnit)owner()).GREEN_BAR.doubleValue() > 5;
	}
	@Override
	public void activated() {
		super.activated();
		((HumanBody)body()).punchFrontHand(20, point().angleTo(((NAUnit)owner()).targetUnit()));
		//dmg: POW + AGI*2
		//stamina: -5p
		targetUnit.damage(new NADamage(((NAUnit)owner()).POW_FLOAT.doubleValue() + 2*((NAUnit)owner()).AGI_FLOAT.doubleValue()));
		((NAUnit)owner()).GREEN_BAR.consume(5);
	}
	@Override
	public void overwriteFailed() {
		body().setActionAppointment(this);
	}
	@Override
	public void stopped() {
		if(!super.activate())
			((NAUnit)owner()).setInvisible(true);
	}
}
class HookThrow extends NAAction {
	NAUnit hookedUnit;
	private Bullet hook = new Bullet(owner()) {
		{
			name = "Boss1's hook";
			paintScript = ImageFrame.create("picture/boss_1/hook.png");
			damage = NADamage.NULL_DAMAGE;
		}
		@Override
		public void idle() {
			final double distance = point().distance(owner());
			//destroy when distance is longer then 500px, or 
			if(distance > 600) {
				claimDelete();
				return;
			}
			//if caught a unit, head to owner's position
			if(hookedUnit != null) {
				final double FORCE = 20;
				final double ownerDirection = point().angleTo(owner());
				hookedUnit.point().setSpeed(FORCE*Math.cos(ownerDirection), FORCE*Math.sin(ownerDirection));
				point().setXY(hookedUnit);
				//destroy when caught a unit and the distance is shorter then 30px
				if(distance < 30) {
					hookedUnit.point().stop();
					claimDelete();
					((Boss_1)owner()).stinger.targetUnit = hookedUnit;
					body().action(((Boss_1)owner()).stinger);
				}
			}
			super.idle();
		}
		@Override
		public boolean hitUnitDeleteCheck(Unit unit) {
			if(hookedUnit == null) {
				hookedUnit = (NAUnit)unit;
				point().stop();
			}
			return false;
		}
	};
	public HookThrow(Body body) {
		super(body, 100);
	}
	@Override
	public void idle() {
		super.stopActionIfFramePassed(10);
	}
	@Override
	public void activated() {
		super.activated();
		((NAUnit)owner()).GREEN_BAR.consume(20);
		hookedUnit = null;
		GHQ.stage().addBullet(hook);
		hook.resetInitialFrame();
		hook.cancelDelete();
		hook.point().setXY(owner());
		hook.point().setSpeed(40);
		hook.point().setMoveAngleToTarget(((NAUnit)owner()).targetUnit());
	}
}
class BackStab extends NAAction {
	public BackStab(Body body) {
		super(body, 100);
	}
	@Override
	public void idle() {
		super.stopActionIfFramePassed(10);
	}
	@Override
	public boolean precondition() {
		return GHQ.passedFrame(initialFrame) > 5;
	}
	@Override
	public void activated() {
		super.activated();
		((HumanBody)body()).punchFrontHand(30, point().angleTo(((NAUnit)owner()).targetUnit()));
		//teleport to back of targetUnit and look at him
		final NAUnit targetUnit = ((NAUnit)owner()).targetUnit();
		if(targetUnit != null) {
			owner().point().setX(targetUnit.point().doubleX() - 30*targetUnit.angle().cos());
			owner().point().setY(targetUnit.point().doubleY() - 30*targetUnit.angle().sin());
			owner().angle().set(targetUnit.angle());
			owner().point().stop();
		}
		//dmg: (POW_FLOAT - 3)*3
		//stamina: -20p
		final Point stabPoint = new Point.IntPoint(point());
		stabPoint.addXY_DA(35, angle().get());
		for(Unit unit : GHQ.stage().units) {
			if(unit != owner() && unit.intersectsRect(stabPoint.intX(), stabPoint.intY(), 15, 15))
				unit.damage(new NADamage((((NAUnit)owner()).POW_FLOAT.doubleValue() - 3)*3, DamageMaterialType.Phy)
						.setCriticalAddition(1.0).setKnockbackRate(1.5));
		}
		((NAUnit)owner()).BLUE_BAR.consume(20);
		((NAUnit)owner()).GREEN_BAR.consume(30);
	}
	@Override
	public void overwriteFailed() {
		body().setActionAppointment(this);
	}
	@Override
	public void stopped() {
		//chain a HookThrow action
		body().action(((Boss_1)owner()).hookthrow);
	}
}
class StingerStrike extends NAAction {
	public StingerStrike(Body body) {
		super(body, 100);
	}
	@Override
	public void idle() {
		super.stopActionIfFramePassed(20);
		//dmg: (POW_FLOAT - 3)*3
		//stamina: -20p
		final Point stingerPoint = new Point.IntPoint(point());
		stingerPoint.addXY_DA(35, angle().get());
		for(Unit unit : GHQ.stage().units) {
			if(unit != owner() && unit.intersectsRect(stingerPoint.intX(), stingerPoint.intY(), 15, 15)) {
				unit.damage(new NADamage((((NAUnit)owner()).POW_FLOAT.doubleValue() - 3)*3, DamageMaterialType.Phy));
				if(unit == ((NAUnit)owner()).targetUnit()) {
					super.stopAction();
					//chain a BackStab action
					body().action(((Boss_1)owner()).backStab);
				}
			}
		}
	}
	@Override
	public boolean precondition() {
		return ((NAUnit)owner()).GREEN_BAR.isMax() && ((NAUnit)owner()).BLUE_BAR.doubleValue() >= 20 && GHQ.passedFrame(initialFrame) > 30;
	}
	@Override
	public void activated() {
		super.activated();
		((HumanBody)body()).punchFrontHand(60, point().angleTo(((NAUnit)owner()).targetUnit()));
		owner().point().addSpeed_DA(70, owner().angle().get());
		((NAUnit)owner()).GREEN_BAR.consume(50);
	}
	@Override
	public void overwriteFailed() {
		body().setActionAppointment(this);
	}
}