package com.cursortickring;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.runelite.api.Item;
import net.runelite.api.gameval.ItemID;

final class FoodDelayTracker
{
	private final Map<Integer, PendingFood> pending = new HashMap<>();

	synchronized void clicked(int itemId, String option, Item[] inventory, int tick)
	{
		if ("Eat".equals(option))
		{
			pending.put(itemId, new PendingFood(count(inventory, itemId), tick + 2));
		}
		else
		{
			pending.remove(itemId);
		}
	}

	synchronized int consumed(Item[] inventory, int tick)
	{
		int delay = 0;
		Iterator<Map.Entry<Integer, PendingFood>> iterator = pending.entrySet().iterator();
		while (iterator.hasNext())
		{
			Map.Entry<Integer, PendingFood> entry = iterator.next();
			PendingFood food = entry.getValue();
			if (tick > food.expiresAt)
			{
				iterator.remove();
			}
			else if (count(inventory, entry.getKey()) < food.quantity)
			{
				delay += attackDelay(entry.getKey());
				iterator.remove();
			}
		}
		return delay;
	}

	synchronized void expire(int tick)
	{
		pending.values().removeIf(food -> tick > food.expiresAt);
	}

	synchronized void reset()
	{
		pending.clear();
	}

	private static int count(Item[] inventory, int itemId)
	{
		int quantity = 0;
		for (Item item : inventory)
		{
			if (item != null && item.getId() == itemId)
			{
				quantity += item.getQuantity();
			}
		}
		return quantity;
	}

	static int attackDelay(int itemId)
	{
		switch (itemId)
		{
			case ItemID.TBWT_COOKED_KARAMBWAN:
			case ItemID.BR_TBWT_COOKED_KARAMBWAN:
			case ItemID.BLIGHTED_KARAMBWAN:
			case ItemID.HALIBUT:
			case ItemID.CHOCOLATE_BOMB:
			case ItemID.TANGLED_TOADS_LEGS:
			case ItemID.WORM_HOLE:
			case ItemID.VEG_BALL:
			case ItemID.WORM_CRUNCHIES:
			case ItemID.CHOCCHIP_CRUNCHIES:
			case ItemID.SPICY_CRUNCHIES:
			case ItemID.TOAD_CRUNCHIES:
			case ItemID.WORM_BATTA:
			case ItemID.TOAD_BATTA:
			case ItemID.FRUIT_BATTA:
			case ItemID.VEGETABLE_BATTA:
			case ItemID.CHEESE_TOM_BATTA:
			case ItemID.PREMADE_CHOCOLATE_BOMB:
			case ItemID.PREMADE_TANGLED_TOADS_LEGS:
			case ItemID.PREMADE_WORM_HOLE:
			case ItemID.PREMADE_VEG_BALL:
			case ItemID.PREMADE_WORM_CRUNCHIES:
			case ItemID.PREMADE_CHOCCHIP_CRUNCHIES:
			case ItemID.PREMADE_SPICY_CRUNCHIES:
			case ItemID.PREMADE_TOAD_CRUNCHIES:
			case ItemID.PREMADE_WORM_BATTA:
			case ItemID.PREMADE_TOAD_BATTA:
			case ItemID.PREMADE_FRUIT_BATTA:
			case ItemID.PREMADE_VEGETABLE_BATTA:
			case ItemID.PREMADE_CHEESE_TOM_BATTA:
			case ItemID.ALUFT_FRUIT_BATTA:
			case ItemID.ALUFT_TOAD_BATTA:
			case ItemID.ALUFT_WORM_BATTA:
			case ItemID.ALUFT_VEGETABLE_BATTA:
			case ItemID.ALUFT_CHEESE_TOM_BATTA:
			case ItemID.ALUFT_TOAD_CRUNCHIES:
			case ItemID.ALUFT_SPICY_CRUNCHIES:
			case ItemID.ALUFT_WORM_CRUNCHIES:
			case ItemID.ALUFT_CHOCCHIP_CRUNCHIES:
			case ItemID.ALUFT_WORM_HOLE:
			case ItemID.ALUFT_VEG_BALL:
			case ItemID.ALUFT_TANGLED_TOADS_LEGS:
			case ItemID.ALUFT_CHOCOLATE_BOMB:
				return 2;
			default:
				return 3;
		}
	}

	private static final class PendingFood
	{
		private final int quantity;
		private final int expiresAt;

		private PendingFood(int quantity, int expiresAt)
		{
			this.quantity = quantity;
			this.expiresAt = expiresAt;
		}
	}
}
