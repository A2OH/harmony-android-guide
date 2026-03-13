package android.hardware.camera2.params;

import android.graphics.Point;
import android.graphics.Rect;

/**
 * Android-compatible camera2 Face shim. Represents a detected face.
 */
public class Face {

    /** Maximum face detection confidence score. */
    public static final int SCORE_MAX = 100;
    /** Minimum face detection confidence score. */
    public static final int SCORE_MIN = 1;

    /** Sentinel value for getId() when face ID is unavailable. */
    public static final int ID_UNSUPPORTED = -1;

    private final Rect  mBounds;
    private final int   mScore;
    private final int   mId;
    private final Point mLeftEye;
    private final Point mRightEye;
    private final Point mMouth;

    /**
     * Create a face without landmark positions (id will be ID_UNSUPPORTED).
     *
     * @param bounds bounding rectangle
     * @param score  confidence in [SCORE_MIN, SCORE_MAX]
     */
    public Face(Rect bounds, int score) {
        this(bounds, score, ID_UNSUPPORTED, null, null, null);
    }

    /**
     * Create a face with full landmark positions.
     *
     * @param bounds        bounding rectangle
     * @param score         confidence in [SCORE_MIN, SCORE_MAX]
     * @param id            face tracking id (ID_UNSUPPORTED if unavailable)
     * @param leftEyePosition  position of left eye, or null
     * @param rightEyePosition position of right eye, or null
     * @param mouthPosition    position of mouth, or null
     */
    public Face(Rect bounds, int score, int id,
                Point leftEyePosition, Point rightEyePosition, Point mouthPosition) {
        mBounds   = bounds;
        mScore    = score;
        mId       = id;
        mLeftEye  = leftEyePosition;
        mRightEye = rightEyePosition;
        mMouth    = mouthPosition;
    }

    /** @return bounding rectangle of this face in sensor coordinates */
    public Rect getBounds() { return mBounds; }

    /** @return confidence score in [SCORE_MIN, SCORE_MAX] */
    public int getScore() { return mScore; }

    /** @return face id, or ID_UNSUPPORTED if unavailable */
    public int getId() { return mId; }

    /** @return left eye position, or null if unavailable */
    public Point getLeftEyePosition()  { return mLeftEye; }

    /** @return right eye position, or null if unavailable */
    public Point getRightEyePosition() { return mRightEye; }

    /** @return mouth position, or null if unavailable */
    public Point getMouthPosition() { return mMouth; }

    @Override
    public String toString() {
        return "Face{bounds=" + mBounds + ", score=" + mScore + ", id=" + mId + "}";
    }
}
