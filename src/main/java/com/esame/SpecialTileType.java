package com.esame;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Rappresenta i tipi di tessere speciali disponibili nel gioco.
 */
enum SpecialTileType {

  /**
   * Casella che aumenta il raggio di esplosione delle bombe del giocatore.
   */
  pupBlast,

  /**
   * Casella che aumenta il numero di bombe che il giocatore può piazzare.
   */
  pupBomb,

  /**
   * Casella che aumenta la velocità di movimento del giocatore.
   */
  pupSpeed,

  /**
   * Casella che fa accedere il giocatore al livello successivo.
   */
  nextLevelDoor;

  /**
   * Restituisce un tipo di power-up casuale, escludendo il tipo nextLevelDoor.
   *
   * @return un tipo di casella speciale casuale
   */
  public static SpecialTileType getRandomPowerUpType() {
      // Convert array to a modifiable list
      List<SpecialTileType> values = new ArrayList<>(Arrays.asList(values()));
      values.remove(SpecialTileType.nextLevelDoor); // Now this operation is supported
      int size = values.size();
      Random random = new Random();
      return values.get(random.nextInt(size));
  }

}