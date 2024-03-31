package com.lucandia;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

enum SpecialTileType {
  pupBlast,
  pupBomb,
  pupSpeed,
  nextLevelDoor;

  public static SpecialTileType getRandomPowerUpType() {
      // Convert array to a modifiable list
      List<SpecialTileType> values = new ArrayList<>(Arrays.asList(values()));
      values.remove(SpecialTileType.nextLevelDoor); // Now this operation is supported
      int size = values.size();
      Random random = new Random();
      return values.get(random.nextInt(size));
  }

}