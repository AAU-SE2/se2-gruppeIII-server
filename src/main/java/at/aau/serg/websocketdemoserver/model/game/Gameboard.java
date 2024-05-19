package at.aau.serg.websocketdemoserver.model.game;

import java.util.Random;

public class Gameboard {
    private Feld[] felder;
    private final Random random = new Random();
    private int holeCounter1;// = random.nextInt((26/2));
    private int holeCounter2;// = (holeCounter1+(18%26)%26-4);

    private String winner;
    int oldPositionCounter1;
    int oldPositionCounter2;
    //abstand ca 18 Felder, damit sich diese genau gegenüber liegen
    private final int maxPosition; // Separate Variable für maximale Position des Spielbretts

    public Gameboard() {
        this.felder = new Feld[26]; //26 felder inkl. karotte
        this.maxPosition = felder.length - 1; // Maximale Position ist die Länge des Arrays-1
        this.holeCounter1 = random.nextInt((26/2));
        this.holeCounter2 = (holeCounter1 + (18 % 26) % 26 - 4);
        winner = null;
        initFields();
    }

    public boolean hasWinner() {
        return winner != null;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    //initFields
    //korrigiert
    private void initFields() {
        for (int i = 0; i < 26; i++) {
            //felder[i] = new Feld();
            //init maulwurfhügel
            if (i == 3 || i == 6 || i == 9 || i == 15 || i == 18 || i == 20 || i == 24) {
                felder[i] = new Feld(true);
                felder[i].setOpen(false);
            }
            //normal fields
            else {
                felder[i] = new Feld(false);
            }
            if (i == 2 || i == 13 || i == 22) {
                felder[i].setSpecialField(false);
            }
        }
        specialFieldSwitch(holeCounter1);

        //initialize fist open molewholes();
        oldPositionCounter1 = holeCounter1;
        oldPositionCounter2 = holeCounter2;
        felder[oldPositionCounter1].setOpen(true);
        felder[oldPositionCounter2].setOpen(true);
    }

    public void twistTheCarrot() {
        //die löcher die vormals geöffnet wurden, werden beim nächsten drehen ja wieder verschlossen
        felder[oldPositionCounter1].setOpen(false);
        felder[oldPositionCounter2].setOpen(false);
        //increment 0 to 15
        //wie es auf <= gesetzt wurde, ging es aus dem gültigen bereich raus
        if (holeCounter1 >= 0 && holeCounter1 < 15) {
            holeCounter1++;
        } else {
            holeCounter1 = 0;
        }

        //dekrement to 16 - 25
        if (holeCounter2 > 16 && holeCounter2 <= 25) {
            holeCounter2--;
        } else {
            holeCounter2 = 25;
        }
        if (felder[holeCounter1].isIstEsEinMaulwurfLoch() == true) {
            felder[holeCounter1].setOpen(true);
            oldPositionCounter1 = holeCounter1;
        }

        if (felder[holeCounter2].isIstEsEinMaulwurfLoch() == true) {
            felder[holeCounter2].setOpen(true);
            oldPositionCounter2 = holeCounter2;
        }
        specialFieldSwitch(holeCounter1);
    }

    //korrigiert
    //feld 2 == maulwurfhöhle, feld 13 == brücke, feld 22 == gatter
    private void specialFieldSwitch(int holeCounter1) {
        if(holeCounter1%3 == 0) {
            felder[2].setSpecialField(true);
            felder[13].setSpecialField(false);
            felder[22].setSpecialField(false);
        }
        if(holeCounter1%3 == 1) {
            felder[2].setSpecialField(false);
            felder[13].setSpecialField(true);
            felder[22].setSpecialField(false);
        }
        if(holeCounter1%3 == 2) {
            felder[2].setSpecialField(false);
            felder[13].setSpecialField(false);
            felder[22].setSpecialField(true);
        }
    }

    public int getSpielfigurPosition(Spielfigur spielfigur) {
        for (int i = 0; i < felder.length; i++) {
            if (felder[i].getSpielfigur() == spielfigur) {
                return i;
            }
        }
        return -1;
    }

    //insert figure to gameboard
    public void insertFigureToGameboard(Spieler spieler, String spielerId, String card) {
        Spielfigur addNewSpielfigur = new Spielfigur();
        int beginningPosition = 0;
        felder[beginningPosition].addSpielfigurToField(addNewSpielfigur);

        moveFigureForward(spieler, spielerId, card, beginningPosition);

    }

    //for lottiKarottiExtreme
    public void moveFigureForward(Spieler spieler, String spielerId, String card, int currentPosition) {
        int oldPosition = currentPosition;
        int cardValue = Integer.parseInt(card);
        boolean moveBackward = false;

        while (cardValue > 0) {
            int newPosition = currentPosition +1;
            if (newPosition < felder.length) {
                //wenn die brücke oben ist...
                if (newPosition == 12 && felder[12].isSpecialField() == true) {
                    while (cardValue > 0) {
                        if (!felder[newPosition].isOccupiedBySpielfigur()) {
                            //wenn das feld frei ist
                            currentPosition--;
                            cardValue --;
                        }
                        else {
                            currentPosition--;
                        }
                    }
                    moveBackward = true;
                }
                if (!felder[newPosition].isOccupiedBySpielfigur() && moveBackward == false) {
                    //wenn das feld frei ist
                    currentPosition = newPosition;
                    cardValue --;
                }
                else {
                    currentPosition = newPosition;
                }
            }
        }
        moveBackward = false;

        felder[oldPosition].removeSpielFigurFromField();
        felder[currentPosition].addSpielfigurToField(new Spielfigur());

        //maulwurf
        if (currentPosition == 2 && felder[currentPosition].isSpecialField() == true) {
            felder[currentPosition].removeSpielFigurFromField();
        }
        //gatter
        if (currentPosition == 22 && felder[currentPosition].isSpecialField() == true) {
            felder[oldPosition].removeSpielFigurFromField();
            felder[8].addSpielfigurToField(new Spielfigur());
        }
        if (currentPosition == 25) {
            //player wins...
            winner = spielerId;
            //this public variable can be accessed from outside
        }
    }

    public boolean checkWinCondition(Spieler spieler) {
        // Überprüfe, ob der Spieler genug Karotten gesammelt hat
        return spieler.hasReachedCarrot();
    }

    public Feld[] getFelder() {
        return felder;
    }

    public void setFelder(Feld[] felder) {
        this.felder = felder;}
}