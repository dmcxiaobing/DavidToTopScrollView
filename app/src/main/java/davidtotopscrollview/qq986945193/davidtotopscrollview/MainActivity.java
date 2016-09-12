package davidtotopscrollview.qq986945193.davidtotopscrollview;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;

/**
 * @author ：程序员小冰
 * @新浪微博 ：http://weibo.com/mcxiaobing
 * @GitHub: https://github.com/QQ986945193
 * @CSDN博客: http://blog.csdn.net/qq_21376985
 * @码云OsChina ：http://git.oschina.net/MCXIAOBING
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ScrollView scrollView;// scrollView数据列表
    private Button toTopBtn;// 返回顶部的按钮


    private int scrollY = 0;// 标记上次滑动位置

    private View contentView;

    private final String TAG = "qq986945193";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        scrollView = (ScrollView) findViewById(R.id.my_scrollView);
        if (contentView == null) {
            contentView = scrollView.getChildAt(0);
        }

        toTopBtn = (Button) findViewById(R.id.top_btn);
        toTopBtn.setOnClickListener(this);

        //http://blog.csdn.net/qq_21376985
        /******************** 监听ScrollView滑动停止 *****************************/
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            private int lastY = 0;
            private int touchEventId = -9983761;
            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    View scroller = (View) msg.obj;
                    if (msg.what == touchEventId) {
                        if (lastY == scroller.getScrollY()) {
                            handleStop(scroller);
                        } else {
                            handler.sendMessageDelayed(handler.obtainMessage(
                                    touchEventId, scroller), 5);
                            lastY = scroller.getScrollY();
                        }
                    }
                }
            };

            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    handler.sendMessageDelayed(
                            handler.obtainMessage(touchEventId, v), 5);
                }
                return false;
            }

            /**
             * ScrollView 停止
             *
             * @param view
             */
            private void handleStop(Object view) {

                Log.i(TAG, "handleStop");
                ScrollView scroller = (ScrollView) view;
                scrollY = scroller.getScrollY();

                doOnBorderListener();
            }
        });
        /***********************************************************/

    }

    /**
     * ScrollView 的顶部，底部判断：
     * http://blog.csdn.net/qq_21376985
     * <p/>
     * 其中getChildAt表示得到ScrollView的child View， 因为ScrollView只允许一个child
     * view，所以contentView.getMeasuredHeight()表示得到子View的高度,
     * getScrollY()表示得到y轴的滚动距离，getHeight()为scrollView的高度。
     * 当getScrollY()达到最大时加上scrollView的高度就的就等于它内容的高度了啊~
     *
     * @param
     */
    private void doOnBorderListener() {
        // 底部判断
        if (contentView != null
                && contentView.getMeasuredHeight() <= scrollView.getScrollY()
                + scrollView.getHeight()) {
            toTopBtn.setVisibility(View.VISIBLE);
            Log.i(TAG, "bottom");
        }
        // 顶部判断
        else if (scrollView.getScrollY() == 0) {

            Log.i(TAG, "top");
        } else if (scrollView.getScrollY() > 30) {
            toTopBtn.setVisibility(View.VISIBLE);
            Log.i(TAG, "test");
        }

    }

    /**
     * 下面我们看一下这个函数: scrollView.fullScroll(ScrollView.FOCUS_DOWN);滚动到底部
     * scrollView.fullScroll(ScrollView.FOCUS_UP);滚动到顶部
     * <p/>
     * <p/>
     * 需要注意的是，该方法不能直接被调用 因为Android很多函数都是基于消息队列来同步，所以需要一部操作，
     * addView完之后，不等于马上就会显示，而是在队列中等待处理，虽然很快， 但是如果立即调用fullScroll，
     * view可能还没有显示出来，所以会失败 应该通过handler在新线程中更新
     * <p/>
     * http://blog.csdn.net/qq_21376985
     * http://weibo.com/mcxiaobing
     */
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.top_btn:
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
//                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);滚动到底部
//                        scrollView.fullScroll(ScrollView.FOCUS_UP);滚动到顶部
//
//                        需要注意的是，该方法不能直接被调用
//                        因为Android很多函数都是基于消息队列来同步，所以需要一部操作，
//                        addView完之后，不等于马上就会显示，而是在队列中等待处理，虽然很快，但是如果立即调用fullScroll， view可能还没有显示出来，所以会失败
//                                应该通过handler在新线程中更新
                        scrollView.fullScroll(ScrollView.FOCUS_UP);
                    }
                });
                toTopBtn.setVisibility(View.GONE);
                break;
        }
    }

}
