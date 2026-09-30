package com.cursortickring;

import net.runelite.api.Item;
import net.runelite.api.gameval.ItemID;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FoodDelayTrackerTest
{
	private final FoodDelayTracker tracker = new FoodDelayTracker();
	private final Item shark = new Item(ItemID.SHARK, 1);
	private final Item karambwan = new Item(ItemID.TBWT_COOKED_KARAMBWAN, 1);

	@Test
	public void confirmsComboEatingOnceEvenWithRepeatedClicks()
	{
		Item[] inventory = {shark, karambwan};
		tracker.clicked(shark.getId(), "Eat", inventory, 10);
		tracker.clicked(shark.getId(), "Eat", inventory, 10);
		tracker.clicked(karambwan.getId(), "Eat", inventory, 10);
		assertEquals(0, tracker.consumed(inventory, 10));
		assertEquals(5, tracker.consumed(new Item[0], 11));
		assertEquals(0, tracker.consumed(new Item[0], 11));
	}

	@Test
	public void ignoresInventoryMovesAndCancelledOrExpiredEating()
	{
		Item[] inventory = {shark, karambwan};
		tracker.clicked(shark.getId(), "Eat", inventory, 10);
		assertEquals(0, tracker.consumed(new Item[]{karambwan, shark}, 11));
		tracker.clicked(shark.getId(), "Drop", inventory, 11);
		assertEquals(0, tracker.consumed(new Item[]{karambwan}, 11));
		tracker.clicked(shark.getId(), "Eat", inventory, 10);
		tracker.expire(13);
		assertEquals(0, tracker.consumed(new Item[]{karambwan}, 13));
	}

	@Test
	public void handlesFoodThatLeavesAnotherPortionAndStackableFood()
	{
		tracker.clicked(ItemID.CAKE, "Eat", new Item[]{new Item(ItemID.CAKE, 1)}, 10);
		assertEquals(3, tracker.consumed(new Item[]{new Item(ItemID.PARTIAL_CAKE, 1)}, 11));
		tracker.clicked(ItemID.TRAIL_SWEETS, "Eat", new Item[]{new Item(ItemID.TRAIL_SWEETS, 8)}, 11);
		assertEquals(3, tracker.consumed(new Item[]{new Item(ItemID.TRAIL_SWEETS, 7)}, 12));
	}

	@Test
	public void drinkingAndResettingDoNotAddFoodDelay()
	{
		Item brew = new Item(ItemID._4DOSEPOTIONOFSARADOMIN, 1);
		tracker.clicked(brew.getId(), "Drink", new Item[]{brew}, 10);
		assertEquals(0, tracker.consumed(new Item[0], 11));
		tracker.clicked(shark.getId(), "Eat", new Item[]{shark}, 10);
		tracker.reset();
		assertEquals(0, tracker.consumed(new Item[0], 11));
		assertEquals(2, FoodDelayTracker.attackDelay(ItemID.BLIGHTED_KARAMBWAN));
		assertEquals(2, FoodDelayTracker.attackDelay(ItemID.HALIBUT));
		assertEquals(2, FoodDelayTracker.attackDelay(ItemID.PREMADE_FRUIT_BATTA));
		assertEquals(2, FoodDelayTracker.attackDelay(ItemID.ALUFT_CHOCOLATE_BOMB));
	}
}
