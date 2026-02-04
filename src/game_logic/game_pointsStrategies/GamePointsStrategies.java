package game_logic.game_pointsStrategies;

import Interfaces.PointsStrategy;
import pieces_details.Piece;

public class GamePointsStrategies implements PointsStrategy {
        public int calcPoints(Object gameStatus) {
            if (gameStatus instanceof Piece) {
                Piece piecePoints = (Piece) gameStatus;
                switch (piecePoints.type()) {
                    case 'Q':
                        return 90;
                    case 'R':
                        return 50;
                    case 'B':
                        return 30;
                    case 'N':
                        return 30;
                    case 'P':
                        return 10;
                    default:
                        return 0;
                }
            }
            if (gameStatus instanceof String) {
                String pointsChange = (String) gameStatus;
                if (pointsChange.equals("CHECK")) {
                    return 300;
                }
                if (pointsChange.equals("LEAVE")) {
                    return 150;
                }
            }
            return 0;
        }
    }

