package test;

import org.junit.jupiter.api.Test;
import static org.junit.Assert.assertEquals;

import src.Main;

public class MainTest {
  @Test
  public void test_add() {
    final Main main = new Main();
    System.out.println("Test add(1, 2)");
    assertEquals(main.add(0, 1), 1);
  }
}
