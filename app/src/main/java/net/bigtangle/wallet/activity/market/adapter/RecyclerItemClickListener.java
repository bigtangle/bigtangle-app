package net.bigtangle.wallet.activity.market.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import net.bigtangle.wallet.activity.OnItemClickListener;

public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {

    private GestureDetector mGestureDetector;

    /**
     * 被选择的view
     */
    private View selectView;
    /**
     * 被选择view的position
     */
    private int selectPosition;

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View childView = rv.findChildViewUnder(e.getX(), e.getY());
        if (childView == null) {
            return false;
        }
        selectView = childView;
        selectPosition = rv.getChildAdapterPosition(childView);
        /**
         * 交给手势控制类来处理
         */
        return mGestureDetector.onTouchEvent(e);
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    public RecyclerItemClickListener(Context context, final OnItemClickListener.Normal mListener) {

        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            /**
             * 点击
             */
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if (selectView != null && mListener != null){
                    mListener.onItemClick(selectView, selectPosition);
                    return true;
                }
                return super.onSingleTapUp(e);
            }

            /**
             * 长按
             */
            @Override
            public void onLongPress(MotionEvent e) {
                if (selectView != null && mListener != null){
                    mListener.onItemLongClick(selectView, selectPosition);
                }
            }
        });
    }
}
