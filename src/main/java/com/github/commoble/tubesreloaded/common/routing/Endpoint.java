package com.github.commoble.tubesreloaded.common.routing;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class Endpoint
{	
	public final BlockPos pos;	// TEs can become invalidated or replaced, so get new ones when needed (not sure why this comment is here)
	public final Direction face;	// the face of the block at this blockpos that represents the endpoint
	
	public Endpoint(BlockPos tePos, Direction blockFace)
	{
		this.pos = tePos;
		this.face = blockFace;
	}
	
	@Override
	public boolean equals(Object other)
	{
		if (this == other)	// same instance, must be equal
		{
			return true;
		}
		else if (other instanceof Endpoint)
		{	// if other object is an endpoint,
			// this is equivalent to the other endpoint if and only if
			// both blockpos are equivalent and both endpoints are equivalent
			Endpoint otherEndpoint = (Endpoint) other;
			return this.pos.equals(otherEndpoint.pos) && this.face.equals(otherEndpoint.face);
		}
		else
		{
			return false;	// not an endpoint, can't be equal
		}
	}
	
	/**
	 * Returns TRUE if the TE at this endpoint has an item handler and any portion
	 * of the given itemstack can be inserted into that item handler
	 * 
	 * Return FALSE if the handler cannot take the stack or if either the
	 * handler or the TE do not exist
	 * 
	 * This only simulates the insertion and does not affect the state of
	 * any itemstacks or inventories
	 * 
	 * @param world The world this endpoint lies in
	 * @param stack The stack to attempt to insert
	 * @return true or false as described above
	 */
	public boolean canInsertItem(World world, ItemStack stack)
	{
		TileEntity te = world.getTileEntity(this.pos);
		
		if (te == null) return false;
		
		LazyOptional<IItemHandler> optionalHandler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, this.face);
		return optionalHandler.map(handler -> canInsertItem(handler, stack)).orElse(false);
	}
	
	// helper function used for the above method
	// the itemstack is the one passed into the above method, the item handler is assumed to exist
	public static boolean canInsertItem(IItemHandler handler, ItemStack stack)
	{
		for (int i=0; i<handler.getSlots(); i++)
		{
			// for each slot, if the itemstack can be inserted into the slot
				// (i.e. if the type of that item is valid for that slot AND
				// if there is room to put at least part of that stack into the slot)
			// then the inventory at this endpoint can receive the stack, so return true
			if (handler.isItemValid(i, stack) && handler.insertItem(i, stack, true).getCount() < stack.getCount())
			{
				return true;
			}
		}
		
		// return false if no acceptable slot is found
		return false;
	}
	
	@Override
	public int hashCode()
	{
		return this.pos.hashCode() ^ this.face.hashCode();
	}
	
	@Override
	public String toString()
	{
		return this.pos + ";    " + this.face;
	}
	
	// inserts as much of the item as we can into a given handler
	// we don't copy the itemstack because we assume we are already given a copy of the original stack
	// return the portion that was not inserted
	public static ItemStack disperseItemToHandler(ItemStack stack, IItemHandler handler)
	{
		int slotCount = handler.getSlots();
		for (int i=0; i<slotCount; i++)
		{
			if (handler.isItemValid(i, stack))
			{
				stack = handler.insertItem(i, stack, false);
			}
			if (stack.getCount() == 0)
			{
				return stack.copy();
			}
		}
		return stack.copy();
	}

	// returns the slot index of the first available slot in the endpoint block's inventory,
	// or -1 if no slot is valid for the stack
	// handler is assumed to exist!
	public int getFirstValidSlot(ItemStack stack, IItemHandler handler)
	{

		for (int i=0; i<handler.getSlots();i++)
		{
			if (handler.isItemValid(i, stack))
				return i;
		}
		return -1;
	}
	
	
}
