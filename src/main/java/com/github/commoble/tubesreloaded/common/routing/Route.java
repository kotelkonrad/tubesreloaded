package com.github.commoble.tubesreloaded.common.routing;

import java.util.LinkedList;
import java.util.Queue;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** This class has a natural ordering that is inconsistent with equals() **/
public class Route implements Comparable<Route>
{
	public Queue<Direction> sequenceOfMoves;
	public Endpoint destination;
	public int length;	// this isn't the same as the size of the sequence because tubes could have unusual length
	

	public Route(Endpoint destination, int length, Queue<Direction> sequenceOfMoves)
	{
		this.destination = destination;
		this.length = length;
		this.sequenceOfMoves = sequenceOfMoves;
	}
	
	// is a route's endpoint valid for an item being inserted into the network,
	// given the position of the tube the item was inserted into,
	// the side of that tube the item was inserted into,
	// and the item itself?
	public boolean isRouteDestinationValid(World world, BlockPos startPos, Direction insertionSide, ItemStack stack)
	{
		// if the route's endpoint was the position/face the item was inserted from, this route is not valid
		if (this.destination.pos.equals(startPos.offset(insertionSide)) && this.destination.face.getOpposite().equals(insertionSide))
		{
			return false;
		}
		
		// otherwise, return whether the item is valid for the route's endpoint
		return destination.canInsertItem(world, stack);
	}

	@Override
	public int compareTo(Route other)
	{
		// TODO Auto-generated method stub
		return this.length - other.length;
	}
	
	
	public String toStringFrom(BlockPos startPos)
	{
		LinkedList<String> moveStrings = new LinkedList<String>();
		moveStrings.add(startPos.toString());
		
		for (Direction face : this.sequenceOfMoves)
		{
			if (face == null)
			{
				moveStrings.add("null");
			}
			else
			{
				moveStrings.add(face.toString());
			}
		}
		
		return String.join(", ", moveStrings);
	}
}
