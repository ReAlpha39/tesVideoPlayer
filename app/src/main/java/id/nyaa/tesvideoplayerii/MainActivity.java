package id.nyaa.tesvideoplayerii;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.handmark.pulltorefresh.library.PullToRefreshBase;

import java.util.ArrayList;
import java.util.List;

import id.nyaa.tesvideoplayerii.bean.LocalVideoBean;
import id.nyaa.tesvideoplayerii.player.GetLocalVideosTask;
import id.nyaa.tesvideoplayerii.player.LocalVideoListAdapter;
import id.nyaa.tesvideoplayerii.player.PlayerActivity;
import id.nyaa.tesvideoplayerii.ui.title.TitleBuilder;
import id.nyaa.tesvideoplayerii.util.PermissionUtils;
import id.nyaa.tesvideoplayerii.util.ScreenUtils;
import id.nyaa.tesvideoplayerii.view.stateview.StateView;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener, GetLocalVideosTask.OnSuccessListener {
    private ListView mListView;
    private List<LocalVideoBean> mVideoList;
    private LocalVideoListAdapter mListAdapter;
    private GetLocalVideosTask mGetVideoTask;
    private StateView mStateView; // Loading status control, loading, failure, success
    private LinearLayout emptyView = null;
    private com.handmark.pulltorefresh.library.PullToRefreshListView mPullToRefreshListview;
    private Button mToTopBtn;// Back to top button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View mView = View.inflate(getBaseContext(),
                R.layout.activity_main, null);
        setContentView(mView);
        initView(mView);
        initTitle();
        initListener();
        initDatas();
    }

    private void initTitle() {
        new TitleBuilder(this).setLeftImageRes(false, 0)
                .setLeftTextOrImageListener(true, null)
                .setMiddleTitleText(getString(R.string.title));
    }

    /**
     * Initialize views
     *
     * @param mView
     */
    private void initView(View mView) {
        mStateView = mView.findViewById(R.id.mStateView);
        mToTopBtn = mView.findViewById(R.id.btn_top);
        mPullToRefreshListview = mView.findViewById(R.id.PullToRefreshListView);
        mPullToRefreshListview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        mPullToRefreshListview.setPullToRefreshOverScrollEnabled(false);

        // To get the real listview, we must pass it when setting the adapter for
        // the listview or setting the onItemClick event
        mListView = mPullToRefreshListview.getRefreshableView();

        // Add EmptyView to ListView
        AbsListView.LayoutParams emptyViewlayoutParams = new AbsListView.LayoutParams(
                AbsListView.LayoutParams.MATCH_PARENT,
                AbsListView.LayoutParams.MATCH_PARENT);
        emptyView = (LinearLayout) LayoutInflater.from(this).inflate(
                R.layout.stateview_empty_view, mPullToRefreshListview, false);
        emptyView.setLayoutParams(emptyViewlayoutParams);
        emptyView.setGravity(Gravity.CENTER);
//				mListView.setEmptyView(emptyView);
        mPullToRefreshListview.setEmptyView(emptyView);
    }

    private void initListener() {
        mToTopBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                setListViewPos();
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
                // After adding setEmptyView (footerView),
                // position-1 should be reduced by 1 to prevent the array from crossing the boundary
                intent.putExtra("video", mVideoList.get(position - 1));
                intent.putExtra("videoType", 1);
                startActivity(intent);
            }
        });

        mPullToRefreshListview.setOnScrollListener(new AbsListView.OnScrollListener() {
            int itemHeight = 0;
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
			    // When sliding starts and the Y-axis point at the bottom of the ListView
                // exceeds the maximum range of the screen, show or hide the top button
                View c = view.getChildAt(0);
                if (c == null) {
                    return ;
                }
                if(itemHeight < c.getHeight()){
                    itemHeight = c.getHeight();
                }
                int height =  (firstVisibleItem + visibleItemCount -1) * itemHeight;
                if (height >= 1.5 * ScreenUtils.getScreenHeight(MainActivity.this)) {
                    mToTopBtn.setVisibility(View.VISIBLE);
                }else {
                    mToTopBtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }
        });

        // Set refresh listener
        mPullToRefreshListview
                .setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
                    @Override
                    public void onRefresh(
                            PullToRefreshBase<ListView> refreshView) {
                        if (mPullToRefreshListview.isRefreshing()) {
                            getDatas();
                        }
                    }
                });
    }

    private void initDatas() {
        mStateView.setCurrentState(StateView.STATE_LOADING);
        mVideoList = new ArrayList<>();
        mListAdapter = new LocalVideoListAdapter(mVideoList, this);
        mListView.setAdapter(mListAdapter);
        // Android 6.0 Dynamic permission application
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            new PermissionUtils(this);
            PermissionUtils.needPermission(200);
        }else{
            getDatas();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 200) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                getDatas();
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }



    private void getDatas() {
        mGetVideoTask = new GetLocalVideosTask();
        mGetVideoTask.setOnSuccessListener(this);
        mGetVideoTask.execute(this.getContentResolver());
    }

    @Override
    public void onSuccess(List<LocalVideoBean> videos) {
        if (videos.size() > 0) {
            mVideoList.clear();
            mVideoList.addAll(videos);
            mListAdapter.notifyDataSetChanged();
        }
        ShowContent();
        // Delay about 1 second, and call the onRefreshComplete method to solve the problem
        mListView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPullToRefreshListview.onRefreshComplete();
            }
        }, 1000);
    }

    @Override
    public void onClick(View arg0) {

    }

    private void ShowContent() {
        mStateView.setCurrentState(StateView.STATE_CONTENT);
        if (mVideoList.size() == 0 && emptyView != null) {
            emptyView.setVisibility(View.GONE);
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    emptyView.setVisibility(View.VISIBLE);
                }
            }, 20);
        }
    }

    /**
     * Scroll ListView to the specified position
     *
     */
    private void setListViewPos() {
        mListView.smoothScrollToPosition(0);
    }

}

