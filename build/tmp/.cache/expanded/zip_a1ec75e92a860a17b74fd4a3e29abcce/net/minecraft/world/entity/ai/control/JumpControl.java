package net.minecraft.world.entity.ai.control;

import net.minecraft.world.entity.Mob;

public class JumpControl implements Control {
    private final Mob mob;
    protected boolean jump;

    public JumpControl(Mob pMob) {
        this.mob = pMob;
    }

    public void jump() {
        this.jump = true;
    }

    public void tick() {
        this.mob.setJumping(this.jump);
        this.jump = false;
    }
}