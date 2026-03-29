package com.google.android.gms.tasks;

/**
 * Stub GMS Task — minimal completable future pattern.
 */
public class Task<TResult> {
    private TResult mResult;
    private Exception mException;
    private boolean mComplete;

    public Task() {}
    public Task(TResult result) { mResult = result; mComplete = true; }

    public boolean isComplete() { return mComplete; }
    public boolean isSuccessful() { return mComplete && mException == null; }
    public TResult getResult() { return mResult; }
    public Exception getException() { return mException; }

    public Task<TResult> addOnSuccessListener(OnSuccessListener<? super TResult> listener) {
        if (mComplete && mException == null && listener != null) {
            try { listener.onSuccess(mResult); } catch (Exception e) {}
        }
        return this;
    }

    public Task<TResult> addOnFailureListener(OnFailureListener listener) {
        if (mComplete && mException != null && listener != null) {
            try { listener.onFailure(mException); } catch (Exception e) {}
        }
        return this;
    }

    public Task<TResult> addOnCompleteListener(OnCompleteListener<TResult> listener) {
        if (mComplete && listener != null) {
            try { listener.onComplete(this); } catch (Exception e) {}
        }
        return this;
    }

    // Internal: complete the task
    void setResult(TResult result) { mResult = result; mComplete = true; }
    void setException(Exception e) { mException = e; mComplete = true; }
}
