package eu.pb4.polydecorations.util;

import eu.pb4.polydecorations.entity.DecorationsEntities;
import eu.pb4.polydecorations.entity.StatueEntity;
import eu.pb4.polydecorations.item.DecorationsItems;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DecorationDispenserBehavior {
    public static void register() {
        List<ItemConvertible> statues = new ArrayList<>();
        statues.addAll(DecorationsItems.WOODEN_STATUE.values());
        statues.addAll(DecorationsItems.OTHER_STATUE.values());

        statues.forEach(itemConvertible -> {
            DispenserBlock.registerBehavior(
                itemConvertible,
                new ItemDispenserBehavior() {
                    @Override
                    public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                        Direction direction = pointer.state().get(DispenserBlock.FACING);
                        BlockPos blockPos = pointer.pos().offset(direction);
                        ServerWorld serverWorld = pointer.world();
                        Consumer<StatueEntity> consumer = EntityType.copier(
                            statue -> {
                                statue.setYaw(direction.getPositiveHorizontalDegrees());
                                statue.setStack(itemConvertible.asItem().getDefaultStack());
                            }, serverWorld, stack, null
                        );
                        StatueEntity statueEntity = DecorationsEntities.STATUE.spawn(serverWorld, consumer, blockPos, SpawnReason.DISPENSER, false, false);
                        if (statueEntity != null) {
                            stack.decrement(1);
                        }

                        return stack;
                    }
                }
            );
        });
    }
}
