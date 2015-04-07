/*
 * Copyright © 2015 The Evvsoft TreeView Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.evvsoft.treeview;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * A view that shows items in a vertically scrolling multilevel tree.
 * It is based on standard android {@link ListView}. This differs from the
 * {@link ListView} by allowing many levels: groups which can
 * individually be expanded to show its children. The items come from the
 * {@link SimpleJsonTreeViewAdapter} associated with this view.
 * <p>
 * Expandable lists are able to show an indicator beside each item to display
 * the item's current state (the states are usually one of expanded group,
 * collapsed group, child, or last child). Use
 * {@link #setGroupIndicatorResource(int)} or {@link #setChildIndicatorResource(int)}
 * (or the corresponding XML attributes) to set these indicators (see the docs
 * for each method to see additional state that each Drawable can have). The
 * default style for an {@link TreeView} provides indicators which
 * will be shown next to Views given to the {@link TreeView}.
 * Layout wrapper contains information about the preferred position indicator.
 * Call setIndent() to set the indicator's indent is a multiplier for each level.
 *
 * @attr R.styleable#TreeView_groupIndicator
 * @attr R.styleable#TreeView_childIndicator
 * @attr R.styleable#TreeView_indent
 */
public class TreeView extends ListView {

	private static final int THEME_BAD   = -1;
	private static final int THEME_DARK  =  0;
	private static final int THEME_LIGHT =  1;

    /** The indicator resource drawn next to a group. */
    private int mGroupIndicatorRes;

    /** The indicator resource drawn next to a child. */
    private int mChildIndicatorRes;

    /** The indent applied to node's indicator. */
    private int mIndent;
    public static final int DEFAULT_INDENT = 30;

	OnTreeViewNodeClickListener mOnGroupNodeClickListener;
	OnTreeViewNodeClickListener mOnChildNodeClickListener;

	public TreeView(Context context) {
		this(context, null);
	}

    public TreeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setStyledAttributes(context, attrs, 0);
    }

    public TreeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setStyledAttributes(context, attrs, defStyle);
    }

    private void setStyledAttributes(Context context, AttributeSet attrs, int defStyle) {
    	TypedArray ta = context.obtainStyledAttributes(attrs,
    			R.styleable.TreeView, defStyle, 0);
    	try {
    		int theme = ta.getInt(R.styleable.TreeView_theme, THEME_BAD);
    		if (theme == THEME_BAD) {
    			int sdkVersion = context.getApplicationInfo().targetSdkVersion;
    			theme = sdkVersion < Build.VERSION_CODES.HONEYCOMB ? THEME_DARK : THEME_LIGHT;
    		}
    		
    		mGroupIndicatorRes = ta.getResourceId(R.styleable.TreeView_groupIndicator, 0);
    		if (mGroupIndicatorRes == 0)
    			mGroupIndicatorRes =
    			theme == THEME_DARK ?
    					R.drawable.expander_group_holo_dark :
    						R.drawable.expander_group_holo_light;
    		mChildIndicatorRes = ta.getResourceId(R.styleable.TreeView_childIndicator, 0);
    		
    		int defIndent = TypedValue.complexToDimensionPixelSize(DEFAULT_INDENT, context.getResources().getDisplayMetrics());
    		mIndent = ta.getDimensionPixelSize(R.styleable.TreeView_indent, defIndent);
    	} finally {
    		ta.recycle();
    	}
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
    	if (!(adapter instanceof SimpleJsonTreeViewAdapter))
    		throw new RuntimeException("Adapter must be class SimpleJsonTreeViewAdapter");
    	SimpleJsonTreeViewAdapter treeViewAdapter = (SimpleJsonTreeViewAdapter) adapter;
    	treeViewAdapter.setGroupIndicatorResource(mGroupIndicatorRes);
    	treeViewAdapter.setChildIndicatorResource(mChildIndicatorRes);
    	treeViewAdapter.setIndent(mIndent);
    	super.setAdapter(adapter);
    }

    protected SimpleJsonTreeViewAdapter getSimpleJsonTreeViewAdapter() {
    	ListAdapter adapter = super.getAdapter();
    	if (adapter instanceof HeaderViewListAdapter)
    		adapter = ((HeaderViewListAdapter) adapter).getWrappedAdapter();
    	return (SimpleJsonTreeViewAdapter) adapter;
    }

    @Override
    public boolean performItemClick(View v, int position, long id) {
    	int node_position = position - getHeaderViewsCount();
        // Ignore clicks in header/footer
        if (node_position < 0)
            // Clicked on a header, so ignore pass it on to super
            return super.performItemClick(v, position, id);
    	TreeViewNode node = getSimpleJsonTreeViewAdapter().getTreeViewNode(node_position);
    	if (node == null)
    		// Clicked on a footer, so ignore pass it on to super
    		return super.performItemClick(v, position, id);
    	if (node.isGroupNode())
    		return onGroupNodeClick(v, node_position, id, node);
        return onChildNodeClick(v, node_position, id, node);
    }

    public void setGroupIndicatorResource(int resId) {
    	mGroupIndicatorRes = resId;
    	SimpleJsonTreeViewAdapter adapter = getSimpleJsonTreeViewAdapter();
    	if (adapter != null)
    		adapter.setGroupIndicatorResource(resId);
    }

    public void setChildIndicatorResource(int resId) {
    	mChildIndicatorRes = resId;
    	SimpleJsonTreeViewAdapter adapter = getSimpleJsonTreeViewAdapter();
    	if (adapter != null)
    		adapter.setChildIndicatorResource(resId);
    }

    public int getIndent() {
    	return mIndent;
    }

    public void setIndent(int indent) {
    	if (indent >= 0 && indent != mIndent) {
    		mIndent = indent;
        	SimpleJsonTreeViewAdapter adapter = getSimpleJsonTreeViewAdapter();
        	if (adapter != null)
        		adapter.setIndent(indent);
    	}
    }

	public void setOnGroupNodeClickListener(OnTreeViewNodeClickListener listener) {
    	mOnGroupNodeClickListener = listener;
    }

    public void setOnChildNodeClickListener(OnTreeViewNodeClickListener listener) {
    	mOnChildNodeClickListener = listener;
    }

	protected boolean onGroupNodeClick(View v, int position, long id, TreeViewNode node) {
		if (mOnGroupNodeClickListener != null)
			mOnGroupNodeClickListener.onTreeViewNodeClick(this, v, position, id, node);
		getSimpleJsonTreeViewAdapter().setExpanded(node, !node.isExpanded());
		//TODO It is necessary to add 2 more listener OnExpand and OnCollapse
		return true;
	}

	protected boolean onChildNodeClick(View v, int position, long id, TreeViewNode node) {
		boolean result = super.performItemClick(v, position, id);
		if (mOnChildNodeClickListener != null)
			   mOnChildNodeClickListener.onTreeViewNodeClick(this, v, position, id, node);
		return result;
	}

	public interface OnTreeViewNodeClickListener {
		public void onTreeViewNodeClick(TreeView parent, View view, int position, long id,
			TreeViewNode node);
	}
}
