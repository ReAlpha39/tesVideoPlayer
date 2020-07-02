package id.nyaa.tesvideoplayerii.view.stateview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import id.nyaa.tesvideoplayerii.R;

public class StateView extends FrameLayout {
    //content
    public static final int STATE_CONTENT = 0;
    //loading
    public static final int STATE_LOADING = 1;
    //error
    public static final int STATE_ERROR = 2;
    //empty
    public static final int STATE_EMPTY = 3;
    //loading with content
    public static final int STATE_CONTENT_LOADING = 4;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STATE_CONTENT, STATE_LOADING, STATE_ERROR, STATE_EMPTY, STATE_CONTENT_LOADING})
    public @interface ViewState {
    }

    //default show state
    @ViewState
    public int mCurrentState = STATE_CONTENT;

    // View in various states
    private View mContentView, mLoadingView, mErrorView, mEmptyView;

    private LayoutInflater inflater;

    public StateView(Context context) {
        super(context);
    }

    public StateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initStateView(attrs);
    }

    public StateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initStateView(attrs);
    }

    /**
     * @param attrs
     */
    private void initStateView(AttributeSet attrs) {
        inflater = LayoutInflater.from(getContext());
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.StateView);

        // Initialize the layout in various states and add it to stateView

        int emptyViewResId = a.getResourceId(R.styleable.StateView_state_empty, -1);
        if (emptyViewResId > -1) {
            mEmptyView = inflater.inflate(emptyViewResId, this, false);
            addView(mEmptyView, mEmptyView.getLayoutParams());
        }
        int errorViewResId = a.getResourceId(R.styleable.StateView_state_error, -1);
        if (errorViewResId > -1) {
            mErrorView = inflater.inflate(errorViewResId, this, false);
            addView(mErrorView, mErrorView.getLayoutParams());
        }
        int contentViewResId = a.getResourceId(R.styleable.StateView_state_content,-1);
        if(contentViewResId>-1){
            mContentView = inflater.inflate(contentViewResId,this,false);
            addView(mContentView,mContentView.getLayoutParams());
        }
        int loadingViewResId = a.getResourceId(R.styleable.StateView_state_loading, -1);
        if (loadingViewResId > -1) {
            mLoadingView = inflater.inflate(loadingViewResId, this, false);
            addView(mLoadingView, mLoadingView.getLayoutParams());
        }
        // Get the specified status, default content if not specified
        int givenState = a.getInt(R.styleable.StateView_state_current, STATE_CONTENT);
        switch (givenState) {
            case STATE_CONTENT:
                mCurrentState = STATE_CONTENT;
                break;
            case STATE_LOADING:
                mCurrentState = STATE_LOADING;
                break;
            case STATE_EMPTY:
                mCurrentState = STATE_EMPTY;
                break;
            case STATE_ERROR:
                mCurrentState = STATE_ERROR;
                break;
            case STATE_CONTENT_LOADING:
                mCurrentState = STATE_CONTENT_LOADING;
                break;
        }
        a.recycle();
    }

    /**
     * Set the display and hide of the View in each state
     */
    private void setStateView() {
        switch (mCurrentState) {
            case STATE_CONTENT:
                showStateView(mContentView, "Content View");
                break;
            case STATE_EMPTY:
                showStateView(mEmptyView, "Empty View");
                break;
            case STATE_ERROR:
                showStateView(mErrorView, "Error View");
                break;
            case STATE_LOADING:
                showStateView(mLoadingView, "Loading View");
                break;
            case STATE_CONTENT_LOADING:
                if (mContentView == null) {
                    throw new NullPointerException("Content View with Loading View");
                }
                mContentView.setVisibility(VISIBLE);
                if (mLoadingView == null) {
                    throw new NullPointerException("Loading View with Content View");
                }
                mLoadingView.setVisibility(VISIBLE);
                if (mEmptyView != null) mEmptyView.setVisibility(GONE);
                if (mErrorView != null) mErrorView.setVisibility(GONE);
                break;
            default:
                showStateView(mContentView, "Content View");
                break;
        }
    }

    /**
     * @param view
     * @param viewName
     */
    private void showStateView(View view, String viewName) {
        if (mContentView != null) mContentView.setVisibility(GONE);
        if (mErrorView != null) mErrorView.setVisibility(GONE);
        if (mEmptyView != null) mEmptyView.setVisibility(GONE);
        if (mLoadingView != null) mLoadingView.setVisibility(GONE);

        if (view == null) {
            throw new NullPointerException(viewName);
        }
        view.setVisibility(VISIBLE);
    }

    /**
     * Set current status
     *
     * @param state
     */
    public void setCurrentState(@ViewState int state) {
        if (state != mCurrentState) {
            mCurrentState = state;
            setStateView();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mContentView == null) throw new IllegalArgumentException("Content view is not defined");
        setStateView();
    }

    /**
     * Copy all addView() methods
     */
    @Override
    public void addView(View child) {
        if (isValidContentView(child)) mContentView = child;
        super.addView(child);
    }

    @Override
    public void addView(View child, int index) {
        if (isValidContentView(child)) mContentView = child;
        super.addView(child, index);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (isValidContentView(child)) mContentView = child;
        super.addView(child, index, params);
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        if (isValidContentView(child)) mContentView = child;
        super.addView(child, params);
    }

    @Override
    public void addView(View child, int width, int height) {
        if (isValidContentView(child)) mContentView = child;
        super.addView(child, width, height);
    }

    @Override
    protected boolean addViewInLayout(View child, int index, ViewGroup.LayoutParams params) {
        if (isValidContentView(child)) mContentView = child;
        return super.addViewInLayout(child, index, params);
    }

    @Override
    protected boolean addViewInLayout(View child, int index, ViewGroup.LayoutParams params, boolean preventRequestLayout) {
        if (isValidContentView(child)) mContentView = child;
        return super.addViewInLayout(child, index, params, preventRequestLayout);
    }

    /**
     * Determine whether the added View is a valid contentView
     *
     * @param view
     * @return
     */
    private boolean isValidContentView(View view) {
        if (mContentView != null && mContentView != view) {
            return false;
        }
        return view != mLoadingView && view != mErrorView && view != mEmptyView;
    }


}
