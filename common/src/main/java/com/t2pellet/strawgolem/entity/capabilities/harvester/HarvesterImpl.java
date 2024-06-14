package com.t2pellet.strawgolem.entity.capabilities.harvester;

import com.t2pellet.strawgolem.StrawgolemConfig;
import com.t2pellet.strawgolem.entity.StrawGolem;
import com.t2pellet.strawgolem.util.crop.CropUtil;
import com.t2pellet.strawgolem.util.crop.SeedUtil;
import com.t2pellet.tlib.Services;
import com.t2pellet.tlib.entity.capability.api.AbstractCapability;
import com.t2pellet.tlib.entity.capability.api.ICapabilityHaver;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StemGrownBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;

import java.util.*;
import java.util.stream.Collectors;

class HarvesterImpl<E extends Entity & ICapabilityHaver> extends AbstractCapability<E> implements Harvester {

    private final Deque<BlockPos> harvestQueue = new ArrayDeque<>();
    private BlockPos currentHarvestPos = null;

    protected HarvesterImpl(E e) {
        super(e);
    }

    @Override
    public void queueHarvest(BlockPos pos) {
        harvestQueue.add(pos);
    }

    @Override
    public void clearQueue() {
        harvestQueue.clear();
    }

    @Override
    public void clearHarvest() {
        currentHarvestPos = null;
    }

    @Override
    public Optional<BlockPos> startHarvest() {
        currentHarvestPos = harvestQueue.poll();
        return Optional.ofNullable(currentHarvestPos);
    }

    @Override
    public void completeHarvest() {
        if (!isHarvesting()) return;
        Services.SIDE.scheduleServer(20, this::harvestBlock);
    }

    @Override
    public boolean isHarvesting() {
        return currentHarvestPos != null;
    }

    @Override
    public boolean isHarvestingBlock() {
        return isHarvesting() && entity.level.getBlockState(currentHarvestPos).getBlock() instanceof StemGrownBlock;
    }

    @Override
    public Optional<BlockPos> getHarvesting() {
        return Optional.ofNullable(currentHarvestPos);
    }

    @Override
    public Tag writeTag() {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        Iterator<BlockPos> queueIterator = harvestQueue.descendingIterator();
        while (queueIterator.hasNext()) {
            BlockPos next = queueIterator.next();
            list.add(NbtUtils.writeBlockPos(next));
        }
        tag.put("list", list);
        if (currentHarvestPos != null) {
            tag.put("pos", NbtUtils.writeBlockPos(currentHarvestPos));
        }
        return tag;
    }

    @Override
    public void readTag(Tag tag) {
        CompoundTag compoundTag = (CompoundTag) tag;
        CompoundTag posTag = compoundTag.getCompound("pos");
        ListTag queueTag = compoundTag.getList("list", Tag.TAG_COMPOUND);
        for (Tag queuedTag : queueTag) {
            BlockPos queuedPos = NbtUtils.readBlockPos((CompoundTag) queuedTag);
            harvestQueue.add(queuedPos);
        }
        if (!posTag.isEmpty()) {
            currentHarvestPos = NbtUtils.readBlockPos(posTag);
        } else currentHarvestPos = null;
    }

    @Override
    public void findHarvestables() {
        int range = StrawgolemConfig.Harvesting.harvestRange.get();
        for (int x = 0; x < range; ++x) {
            for (int y = 0; y < range; ++y) {
                for (int z = 0; z < range; ++z) {
                    BlockPos entityPos = entity.blockPosition();
                    BlockPos[] positions = new BlockPos[]{
                            entityPos.offset(x, y, z),
                            entityPos.offset(x, -y, z),
                            entityPos.offset(x, y, -z),
                            entityPos.offset(x, -y, -z),
                            entityPos.offset(-x, y, z),
                            entityPos.offset(-x, -y, z),
                            entityPos.offset(x, y, -z),
                            entityPos.offset(-x, -y, -z),
                    };
                    for (BlockPos position : positions) {
                        if (CropUtil.isGrownCrop(entity.level, position)) {
                            queueHarvest(position);
                        }
                    }
                }
            }
        }
    }

    private void harvestBlock() {
        if (!entity.level.isClientSide && isHarvesting() && CropUtil.isGrownCrop(entity.level, currentHarvestPos)) {
            BlockState state = entity.level.getBlockState(currentHarvestPos);
            BlockState defaultState = state.getBlock() instanceof StemGrownBlock ? Blocks.AIR.defaultBlockState() : state.getBlock().defaultBlockState();
            entity.setItemSlot(EquipmentSlot.MAINHAND, pickupLoot(state));
            // Break block
            entity.level.destroyBlock(currentHarvestPos, false, entity);
            entity.level.setBlockAndUpdate(currentHarvestPos, defaultState);
            entity.level.gameEvent(defaultState.isAir() ? GameEvent.BLOCK_DESTROY : GameEvent.BLOCK_PLACE, currentHarvestPos, GameEvent.Context.of(entity, defaultState));
            // Update state and sync
            currentHarvestPos = null;
            synchronize();
        } else clearHarvest();
    }

    private ItemStack pickupLoot(BlockState state) {
        if (state.getBlock() instanceof StemGrownBlock) return new ItemStack(state.getBlock().asItem(), 1);
        LootContext.Builder builder = new LootContext.Builder((ServerLevel) entity.level).withParameter(LootContextParams.TOOL, ItemStack.EMPTY).withParameter(LootContextParams.ORIGIN, entity.position());
        List<ItemStack> drops = state.getDrops(builder);
        Optional<ItemStack> pickupStack = drops.stream().filter((d) -> !SeedUtil.isSeed(d) || d.getItem().isEdible()).findFirst();
        return pickupStack.orElse(ItemStack.EMPTY);
    }
}
