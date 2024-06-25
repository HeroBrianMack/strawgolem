package com.t2pellet.strawgolem.entity.capabilities.deliverer;

import com.t2pellet.strawgolem.util.VisibilityUtil;
import com.t2pellet.strawgolem.util.container.ContainerUtil;
import com.t2pellet.tlib.entity.capability.api.AbstractCapability;
import com.t2pellet.tlib.entity.capability.api.ICapabilityHaver;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class DelivererImpl<E extends LivingEntity & ICapabilityHaver> extends AbstractCapability<E> implements Deliverer {

    private final Set<BlockPos> containerSet = new HashSet<>();
    private BlockPos priorityContainer;
    private ResourceLocation level;

    protected DelivererImpl(E e) {
        super(e);
        level = entity.level.dimension().location();
    }

    @Override
    public BlockPos getDeliverPos() {
        // Clear memory if we change dimensions
        if (!entity.level.dimension().location().equals(level)) {
            clearData();
        }
        Optional<BlockPos> cachedPos = closestRememberedValidDeliverable();
        return cachedPos.orElseGet(() -> scanForDeliverable(entity.blockPosition()));
    }

    @Override
    public void setPriorityPos(BlockPos pos) {
        if (!entity.level.dimension().location().equals(level)) {
            clearData();
        }
        if (ContainerUtil.isContainer(entity.level, pos)) {
            if (!containerSet.contains(pos)) {
                containerSet.add(pos);
            }
            priorityContainer = pos;
        }

    }

    private void clearData() {
        containerSet.clear();
        priorityContainer = null;
        level = entity.level.dimension().location();
    }

    private Optional<BlockPos> closestRememberedValidDeliverable() {
        if (priorityContainer != null && VisibilityUtil.canSee(entity, priorityContainer) && ContainerUtil.isContainer(entity.level, priorityContainer)) {
            return Optional.of(priorityContainer);
        }
        return containerSet.stream()
                .filter(p -> VisibilityUtil.canSee(entity, p) && ContainerUtil.isContainer(entity.level, p))
                .min(Comparator.comparingDouble(p -> p.distManhattan(entity.blockPosition())));
    }

    private BlockPos scanForDeliverable(BlockPos query) {
        for (int x = -24; x <= 24; ++x) {
            for (int y = -12; y <= 12; ++y) {
                for (int z = -24; z <= 24; ++z) {
                    BlockPos pos = query.offset(x, y, z);
                    if (ContainerUtil.isContainer(entity.level, pos) && VisibilityUtil.canSee(entity, pos)) {
                        containerSet.add(pos);
                        return pos;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void deliver(BlockPos pos) {
        ItemStack stack = entity.getItemInHand(InteractionHand.MAIN_HAND).copy();
        // Need an item to deliver
        if (!stack.isEmpty()) {
            // Deliver what we can to the container if it exists
            if (ContainerUtil.isContainer(entity.level, pos)) {
                Container container = (Container) entity.level.getBlockEntity(pos);
                for (int i = 0; i < container.getContainerSize(); ++i) {
                    ItemStack containerStack = container.getItem(i);
                    if (containerStack.isEmpty()) {
                        container.setItem(i, stack);
                        stack = ItemStack.EMPTY;
                    } else if (containerStack.is(stack.getItem())) {
                        int placeableCount = containerStack.getMaxStackSize() - containerStack.getCount();
                        int placingCount = Math.min(stack.getCount(), placeableCount);
                        containerStack.grow(placingCount);
                        stack.shrink(placingCount);
                    }
                    if (stack.isEmpty()) break;
                }
                // Interactions
                entity.level.gameEvent(entity, GameEvent.CONTAINER_OPEN, pos);
                entity.level.playSound(null, pos, SoundEvents.CHEST_CLOSE, SoundSource.BLOCKS, 1.0F, 1.0F);
                entity.level.gameEvent(entity, GameEvent.CONTAINER_CLOSE, pos);
            }
            // Drop remaining stack as ItemEntity
            entity.level.addFreshEntity(new ItemEntity(entity.level, pos.getX(), pos.getY() + 1, pos.getZ(), stack));
            entity.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        }
    }

    @Override
    public Tag writeTag() {
        CompoundTag deliverTag = new CompoundTag();
        if (priorityContainer != null) {
            deliverTag.put("priority", NbtUtils.writeBlockPos(priorityContainer));
        }
        ListTag positionsTag = new ListTag();
        for (BlockPos pos : containerSet) {
            if (ContainerUtil.isContainer(entity.level, pos)) {
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
            if (ContainerUtil.isContainer(entity.level, pos)) {
                containerSet.add(pos);
            }
        }
    }
}
