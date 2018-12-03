package datasettester;


/**
 * Represents PrecRecResult from the ball and the foot category.
 * @author Matti J. Frind
 */
public class Result {
    private PrecRecResult ballResult;
    private PrecRecResult footResult;

    public Result(PrecRecResult ball, PrecRecResult foot) {
        this.ballResult = ball;
        this.footResult = foot;
    }

    public PrecRecResult getBallResult() {
        return ballResult;
    }

    public void setBallResult(PrecRecResult ballResult) {
        this.ballResult = ballResult;
    }

    public PrecRecResult getFootResult() {
        return footResult;
    }

    public void setFootResult(PrecRecResult footResult) {
        this.footResult = footResult;
    }
    
}
