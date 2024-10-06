package com.t2pellet.strawgolem.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class FoodParticle extends TextureSheetParticle {

    private float thetaHoriz;
    private float thetaVert;

    protected FoodParticle(ClientLevel worldIn, double xCoordIn, double yCoordIn, double zCoordIn,
                          double xSpeed, double ySpeed, double zSpeed) {
        super(worldIn, xCoordIn, yCoordIn + 0.4F, zCoordIn);
        x += random.nextFloat() - 0.5F;
        z += random.nextFloat() - 0.5F;
        setSize(0.005F, 0.005F);
        lifetime = 60;
        thetaHoriz = random.nextFloat() * (float) Math.PI;
        thetaVert = 0;
    }

    @Override
    public void tick() {
        super.tick();
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (++this.age >= this.lifetime) {
            this.remove();
        } else {
            this.move(this.xd, this.yd, this.zd);
            thetaHoriz += 0.02;
            thetaVert += 0.02;
            this.xd = Math.sin(thetaHoriz) / 80;
            this.yd = (Math.sin(2 * thetaVert)) / 30;
            this.zd = Math.cos(thetaHoriz) / 80;
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }


    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet sprite) {
            spriteSet = sprite;
        }

        @Override
        public Particle createParticle(SimpleParticleType parameters, ClientLevel world, double x, double y, double z, double vX, double vY, double vZ) {
            FoodParticle foodParticle = new FoodParticle(world, x, y, z, vX, vY, vZ);
            foodParticle.setColor(1.0F, 1.0F, 1.0F);

            foodParticle.pickSprite(spriteSet);
            return foodParticle;
        }
    }
}