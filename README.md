# DavidToTopScrollView
【Android】Scrollview返回顶部，快速返回顶部的功能实现，详解代码。
作者：程序员小冰，GitHub主页：https://github.com/QQ986945193 

新浪微博：http://weibo.com/mcxiaobing 

首先给大家看一下我们今天这个最终实现的效果图： 

![这里写图片描述](http://img.blog.csdn.net/20160912101737587)

我这里只是单纯的实现了scrollview返回顶部的功能。具体效果大家可以适当地美化

在实际项目中可以换图标，去掉右侧滚动条等。具体ui美化不做解释。

好了，首先我们是当不在顶部的时候，返回顶部按钮就会出现，而到顶部之后就会隐藏此按钮，

所以我们这里就要算scrollview的滑动偏移量，当然，有这个返回顶部按钮，而且一直显示在底部，

所以当然用相对布局了。下面先给大家看一下xml布局源码：

```
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <ScrollView
        android:id="@+id/my_scrollView"
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:gravity="center"
            android:orientation="vertical">

            <TextView
                android:layout_width="match_parent"
                android:layout_height="200dp"
                android:text="Hello 程序员小冰"
                android:textSize="20dp" />

            <TextView
                android:layout_width="match_parent"
                android:layout_height="200dp"
                android:text="Android Dev Team"
                android:textSize="20dp" />

            <TextView
                android:layout_width="match_parent"
                android:layout_height="200dp"
                android:text="http://weibo.com/mcxiaobing"
                android:textSize="20dp" />

            <TextView
                android:layout_width="match_parent"
                android:layout_height="200dp"
                android:text="qq986945193"
                android:textSize="20dp" />

            <TextView
                android:layout_width="match_parent"
                android:layout_height="200dp"
                android:text="Hello IOS" />

            <TextView
                android:layout_width="match_parent"
                android:layout_height="200dp"
                android:text="java开发者"
                android:textSize="20dp" />

            <TextView
                android:layout_width="match_parent"
                android:layout_height="200dp"
                android:text="Android开发者"
                android:textSize="20dp" />
        </LinearLayout>
    </ScrollView>

    <Button
        android:id="@+id/top_btn"
        android:layout_width="50dp"
        android:layout_height="50dp"
        android:layout_alignParentBottom="true"
        android:layout_alignParentRight="true"
        android:layout_marginBottom="6dp"
        android:layout_marginRight="6dp"
        android:background="@mipmap/top_btn_bg"
        android:gravity="center"
        android:text="顶"
        android:visibility="gone" />
</RelativeLayout>

```

好了，然后就是我们的java实现代码了。下面是源代码，不懂得朋友可以留言，或者更好的建议，相互交流。

对了，特别说一下，scrollview在XML布局中只能有一个子view，不然就会报错。

所以这一点我在java代码中也特意说明了一下。java实现代码如下：

```
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

```
最后直接运行即可看到上面的效果。如果对您有帮助，欢迎star，fork。。。
