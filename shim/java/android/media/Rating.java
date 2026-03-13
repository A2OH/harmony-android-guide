package android.media;

/**
 * Shim stub for android.media.Rating.
 * A class to encapsulate rating information used as content metadata.
 */
public class Rating {

    public static final int RATING_NONE = 0;
    public static final int RATING_HEART = 1;
    public static final int RATING_THUMB_UP_DOWN = 2;
    public static final int RATING_3_STARS = 3;
    public static final int RATING_4_STARS = 4;
    public static final int RATING_5_STARS = 5;
    public static final int RATING_PERCENTAGE = 6;

    private final int mRatingStyle;
    private final float mRatingValue;

    private Rating(int ratingStyle, float ratingValue) {
        this.mRatingStyle = ratingStyle;
        this.mRatingValue = ratingValue;
    }

    /**
     * Return a Rating instance with no rating. This is used to express that content is unrated.
     *
     * @param ratingStyle one of the RATING_* constants
     * @return a new unrated Rating instance, or null if the rating style is invalid
     */
    public static Rating newUnratedRating(int ratingStyle) {
        if (ratingStyle < RATING_HEART || ratingStyle > RATING_PERCENTAGE) {
            return null;
        }
        return new Rating(ratingStyle, -1.0f);
    }

    /**
     * Return a Rating instance with a heart-based rating.
     *
     * @param hasHeart true for a "heart selected" rating, false for "heart unselected"
     * @return a new heart Rating instance
     */
    public static Rating newHeartRating(boolean hasHeart) {
        return new Rating(RATING_HEART, hasHeart ? 1.0f : 0.0f);
    }

    /**
     * Return a Rating instance with a thumb-based rating.
     *
     * @param thumbIsUp true for a "thumbs up" rating, false for "thumbs down"
     * @return a new thumb Rating instance
     */
    public static Rating newThumbRating(boolean thumbIsUp) {
        return new Rating(RATING_THUMB_UP_DOWN, thumbIsUp ? 1.0f : 0.0f);
    }

    /**
     * Return a Rating instance with a star-based rating.
     *
     * @param starRatingStyle one of RATING_3_STARS, RATING_4_STARS, RATING_5_STARS
     * @param starRating      the rating value, ranging from 0.0f to the number of stars
     * @return a new star Rating instance, or null if the rating style or value is invalid
     */
    public static Rating newStarRating(int starRatingStyle, float starRating) {
        float maxRating;
        switch (starRatingStyle) {
            case RATING_3_STARS: maxRating = 3.0f; break;
            case RATING_4_STARS: maxRating = 4.0f; break;
            case RATING_5_STARS: maxRating = 5.0f; break;
            default: return null;
        }
        if (starRating < 0.0f || starRating > maxRating) {
            return null;
        }
        return new Rating(starRatingStyle, starRating);
    }

    /**
     * Return a Rating instance with a percentage-based rating.
     *
     * @param percentRating the rating value, ranging from 0.0f to 100.0f
     * @return a new percentage Rating instance, or null if the value is out of range
     */
    public static Rating newPercentageRating(float percentRating) {
        if (percentRating < 0.0f || percentRating > 100.0f) {
            return null;
        }
        return new Rating(RATING_PERCENTAGE, percentRating);
    }

    /**
     * Return whether there is a rating value available.
     */
    public boolean isRated() {
        return mRatingValue >= 0.0f;
    }

    /**
     * Return the rating style.
     */
    public int getRatingStyle() {
        return mRatingStyle;
    }

    /**
     * Return the star-based rating value.
     *
     * @return a rating value greater than or equal to 0.0f, or -1.0f if the rating style is not
     *         star-based, or if it is unrated
     */
    public float getStarRating() {
        if (mRatingStyle == RATING_3_STARS || mRatingStyle == RATING_4_STARS
                || mRatingStyle == RATING_5_STARS) {
            return isRated() ? mRatingValue : -1.0f;
        }
        return -1.0f;
    }

    /**
     * Return the percentage-based rating value.
     *
     * @return a rating value greater than or equal to 0.0f, or -1.0f if the rating style is not
     *         percentage-based, or if it is unrated
     */
    public float getPercentRating() {
        if (mRatingStyle == RATING_PERCENTAGE) {
            return isRated() ? mRatingValue : -1.0f;
        }
        return -1.0f;
    }

    /**
     * Return whether the rating is "thumb up".
     *
     * @return true if the rating is "thumb up", false if the rating is "thumb down",
     *         if it is unrated, or if the rating style is not thumb-based
     */
    public boolean isThumbUp() {
        if (mRatingStyle == RATING_THUMB_UP_DOWN) {
            return isRated() && mRatingValue == 1.0f;
        }
        return false;
    }

    /**
     * Return whether the rating is "heart selected".
     *
     * @return true if the rating is "heart selected", false if unrated or not heart-based
     */
    public boolean hasHeart() {
        if (mRatingStyle == RATING_HEART) {
            return isRated() && mRatingValue == 1.0f;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Rating{style=" + mRatingStyle + ", value=" + mRatingValue + "}";
    }
}
