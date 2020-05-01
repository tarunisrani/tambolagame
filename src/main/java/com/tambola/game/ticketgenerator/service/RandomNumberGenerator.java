package com.tambola.game.ticketgenerator.service;

import java.security.SecureRandom;
import java.util.Random;

public class RandomNumberGenerator {
  private Random random;
  private static RandomNumberGenerator instance;
  private RandomNumberGenerator(){
    random = new Random(new Random().nextInt());
  }

  public static RandomNumberGenerator getInstance(){
    if(instance == null){
      instance = new RandomNumberGenerator();
    }
    return instance;
  }

  public Integer generateNextNumber(){
    return new SecureRandom().nextInt(90) +1;
  }
}
