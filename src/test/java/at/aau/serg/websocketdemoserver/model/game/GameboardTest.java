package at.aau.serg.websocketdemoserver.model.game;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.Before;
import org.junit.Test;

    public class GameboardTest {
        private Gameboard gameboard;

        @Before
        public void setup() {
            gameboard = new Gameboard(2);
        }

        @Test
        public void testGameboardInitialization() {
            // Check that the gameboard has been initialized with 26 fields
            assertEquals(26, gameboard.getFelder().length);

            // Check that the carrot counter has been initialized with a random value
            assertTrue(gameboard.getCarrotCounter() >= 0 && gameboard.getCarrotCounter() < 12);
        }

        @Test
        public void testMolehillsInitialization() {
            // Check that the correct number of molehills have been initialized
            int maxMolehills = 6;
            int molehillCount = 0;
            for (Feld feld : gameboard.getFelder()) {
                if (feld != null && feld.isIstEsEinMaulwurfLoch()) {
                    molehillCount++;
                }
            }
            assertEquals(maxMolehills, molehillCount);
        }
    }