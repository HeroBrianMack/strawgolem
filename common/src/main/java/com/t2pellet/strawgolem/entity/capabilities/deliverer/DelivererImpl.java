package com.t2pellet.strawgolem.entity.capabilities.deliverer;

import com.t2pellet.strawgolem.StrawgolemConfig;
import com.t2pellet.strawgolem.util.VisibilityUtil;
import com.t2pellet.strawgolem.util.container.ContainerUtil;
import com.t2pellet.haybalelib.entity.capability.api.AbstractCapability;
import com.t2pellet.haybalelib.entity.capability.api.ICapabilityHaver;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.*;

public class DelivererImpl<E extends LivingEntity & ICapabilityHaver> extends AbstractCapability<E> implements Deliverer {

    private final Set<BlockPos> containerSet = new HashSet<>();
    private BlockPos priorityContainer;
    private ResourceLocation level;
    private final Set<BlockPos> invalidContainers = new HashSet<>();

    protected DelivererImpl(E e) {
        super(e);
        level = entity.level().dimension().location();
    }

    @Override
    public BlockPos getDeliverPos() {
        // Clear memory if we change dimensions
        if (!entity.level().dimension().location().equals(level)) {
            clearData();
        }
        Optional<BlockPos> cachedPos = closestRememberedValidDeliverable();

        if (!cachedPos.isPresent() || !VisibilityUtil.isNearby(entity, cachedPos.get())) {
            BlockPos pos = findClosestDeliverable(entity.blockPosition());
            if (priorityContainer == null) {
                System.out.println(pos);
                priorityContainer = pos;
            }
            return pos;
        }
        return cachedPos.get();

    }

    @Override
    public void setPriorityPos(BlockPos pos) {
        if (!entity.level().dimension().location().equals(level)) {
            clearData();
        }
        System.out.println(entity.level().getBlockEntity(pos).getBlockState().getBlock());
        if (ContainerUtil.isContainer(entity.level(), pos)) {
            System.out.println("hi");
            if (!containerSet.contains(pos)) {
                containerSet.add(pos);
            }
            priorityContainer = pos;
        }
    }

    @Override
    public boolean hasPriorityPos() {
        return priorityContainer != null;
    }

    private void clearData() {
        containerSet.clear();
        priorityContainer = null;
        level = entity.level().dimension().location();
    }

    private Optional<BlockPos> closestRememberedValidDeliverable() {
        if (hasPriorityPos() && canDeliverToPos(entity.level(), priorityContainer)) {
            return Optional.of(priorityContainer);
        }
        return containerSet.stream()
                .filter(p -> canDeliverToPos(entity.level(), p))
                .min(Comparator.comparingDouble(p -> p.distManhattan(entity.blockPosition())));
    }

    private boolean canDeliverToPos(LevelAccessor level, BlockPos pos) {
        ItemStack deliveringStack = entity.getMainHandItem();

        return VisibilityUtil.canSee(entity, pos) && ContainerUtil.isContainer(level, pos)
                && !ContainerUtil.findSlotsInContainer(level, pos, deliveringStack).isEmpty()
                && !invalidContainers.contains(pos);
    }

    private BlockPos scanForDeliverable(BlockPos query) {
        int range = StrawgolemConfig.Harvesting.harvestRange.get();
        for (int x = -range; x <= range; ++x) {
            for (int y = -range / 2; y <= range / 2; ++y) {
                for (int z = -range; z <= range; ++z) {
                    BlockPos pos = query.offset(x, y, z);
                    if (ContainerUtil.isContainer(entity.level(), pos)
                            && VisibilityUtil.canSee(entity, pos)
                            && !invalidContainers.contains(pos)) {
                        containerSet.add(pos);
                        return pos;
                    }
                }
            }
        }
        return null;
    }

    private BlockPos findClosestDeliverable(BlockPos query) {
        int range = StrawgolemConfig.Harvesting.harvestRange.get();
        BlockPos closest = null;
        for (int x = -range; x <= range; ++x) {
            for (int y = -range / 2; y <= range / 2; ++y) {
                for (int z = -range; z <= range; ++z) {
                    BlockPos pos = query.offset(x, y, z);
                    if (ContainerUtil.isContainer(entity.level(), pos)
                            && VisibilityUtil.canSee(entity, pos)
                            && !invalidContainers.contains(pos)) {
                        // Should find the closest deliverable...
                        closest = closest == null || query.distManhattan(pos) < query.distManhattan(closest) ? pos : closest;
                        containerSet.add(pos);
                    }
                }
            }
        }
        return closest;
    }


    @Override
    public void addInvalidPos(BlockPos pos) {
        if (containerSet.remove(pos)) {
            invalidContainers.add(pos);
        }
    }

    @Override
    public void clearInvalidPos() {
        invalidContainers.clear();
    }

    @Override
    public void deliver(BlockPos pos) {
        ItemStack stack = entity.getMainHandItem().copy();
        // Need an item to deliver
        if (!stack.isEmpty()) {
            // Deliver what we can to the container if it exists
            if (ContainerUtil.isContainer(entity.level(), pos)) {
                List<Integer> slots = ContainerUtil.findSlotsInContainer(entity.level(), pos, stack);
                if (!slots.isEmpty()) {
                    ContainerUtil.addToContainer(entity.level(), pos, stack, slots);
                }
                // Drop remaining items
                entity.level().addFreshEntity(new ItemEntity(entity.level(), pos.getX(), pos.getY() + 1, pos.getZ(), stack));
                entity.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                // Interactions
                entity.level().gameEvent(entity, GameEvent.CONTAINER_OPEN, pos);
                entity.level().playSound(null, pos, SoundEvents.CHEST_CLOSE, SoundSource.BLOCKS, 1.0F, 1.0F);
                entity.level().gameEvent(entity, GameEvent.CONTAINER_CLOSE, pos);
            }
        }
    }

    @Override
    public Tag writeTag() {
        CompoundTag deliverTag = new CompoundTag();
        if (hasPriorityPos()) {
            deliverTag.put("priority", NbtUtils.writeBlockPos(priorityContainer));
        }
        ListTag positionsTag = new ListTag();
        for (BlockPos pos : containerSet) {
            if (ContainerUtil.isContainer(entity.level(), pos)) {
                positionsTag.add(NbtUtils.writeBlockPos(pos));
            }
        }
        deliverTag.put("positions", positionsTag);
        return deliverTag;
    }

    @Override
    public void readTag(Tag tag) {
        if (tag instanceof CompoundTag deliverTag) {
            CompoundTag priority = deliverTag.getCompound("priority");
            if (!priority.isEmpty()) {
                priorityContainer = NbtUtils.readBlockPos(priority);
            }
            ListTag positions = deliverTag.getList("positions", Tag.TAG_COMPOUND);
            readPositions(positions);
        } else {
            readPositions(tag);
        }
    }

    private void readPositions(Tag tag) {
        ListTag positions = (ListTag) tag;
        containerSet.clear();
        for (Tag position : positions) {
            BlockPos pos = NbtUtils.readBlockPos((CompoundTag) position);
            if (ContainerUtil.isContainer(entity.level(), pos)) {
                containerSet.add(pos);
            }
        }
    }
}
