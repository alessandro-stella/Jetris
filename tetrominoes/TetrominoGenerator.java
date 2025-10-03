package tetrominoes;

import java.util.*;

public class TetrominoGenerator {

  private final Character[] bagTemplate = {'I', 'J', 'L', 'O', 'S', 'T', 'Z'};

  public List<Character> bag;
  private Random randomizer = new Random();

  public TetrominoGenerator() {
    bag = new ArrayList<Character>(Arrays.asList(bagTemplate));
  }

  public Tetromino generate(int x, int y) {
    if (bag.size() == 0) {
      bag = new ArrayList<Character>(Arrays.asList(bagTemplate));
    }

    int randomIndex = randomizer.nextInt(bag.size());
    char pieceType = bag.remove(randomIndex);

    Tetromino returnValue;

    switch (pieceType) {
      case 'I':
        returnValue = new IPiece(x, y);
        break;

      case 'J':
        returnValue = new JPiece(x, y);
        break;

      case 'L':
        returnValue = new LPiece(x, y);
        break;

      case 'O':
        returnValue = new OPiece(x, y);
        break;

      case 'S':
        returnValue = new SPiece(x, y);
        break;

      case 'T':
        returnValue = new TPiece(x, y);
        break;

      case 'Z':
        returnValue = new ZPiece(x, y);
        break;

      default:
        returnValue = new IPiece(x, y);
        break;
    }

    return returnValue;
  }
}
