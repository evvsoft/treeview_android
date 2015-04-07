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

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SimpleJsonTreeViewAdapter extends BaseAdapter {

	private final static String[] KEYS_DEFAULT  = {TreeViewNode.DEFAULT_ID_NAME, TreeViewNode.DEFAULT_ID_PARENT_NAME};
	private final static int[]    TO_DEFAULT    = {android.R.id.text1};

	private final static int     STATE_NONE     = 0;
	private final static int     STATE_LAST     = 1;
	private final static int     STATE_EXPANDED = 2;
	private final static int[][] STATE =
		{{}, {android.R.attr.state_last}, {android.R.attr.state_expanded}};

	private TreeViewNodeArray mNodes;
	private String[] mKeys;
	private String mIdField;
	private String mIdParentField;
	private int mExpandedGroupLayout;
	private int mCollapsedGroupLayout;
	private String[] mGroupFrom;
	private int[] mGroupTo;
    private int mChildLayout;
    private int mLastChildLayout;
    private String[] mChildFrom;
    private int[] mChildTo;
    private int mGroupIndicatorRes;
    private int mChildIndicatorRes;
    private int mIndent;
	private LayoutInflater mInflater;
    private ViewBinder mViewBinder;
	
    /**
     * Constructor
     * 
     * @param context The context where the {@link TreeView} associated
     *            with this SimpleJsonTreeViewAdapter is running
     * @param data The JSON single-level array of JSON objects. Each JSON object
     *            should include ID field, optional reference to the parent ID,
     *            optional isGroup flag and all the entries specified in
     *            "from"
     * @param from A list of at least one key that will be fetched from the JSON object
     *            associated with single TextView item.
     * @throws JSONException
     */
	public SimpleJsonTreeViewAdapter(Context context, JSONArray data,
			String[] from) throws JSONException {
		this(context, data, KEYS_DEFAULT,
				android.R.layout.simple_list_item_1,
				android.R.layout.simple_list_item_1,
				from, TO_DEFAULT,
				android.R.layout.simple_list_item_1,
				android.R.layout.simple_list_item_1,
				from, TO_DEFAULT);
	}
	
    /**
     * Constructor
     * 
     * @param context The context where the {@link TreeView} associated
     *            with this SimpleJsonTreeViewAdapter is running
     * @param data The JSON single-level array of JSON objects. Each JSON object
     *            should include ID field, optional reference to the parent ID,
     *            optional isGroup flag and all the entries specified in
     *            "groupFrom" or "childFrom" depending on whether the group item
     * @param keys An array of names of key fields and for internal use.
     *            The first item at index 0 is the name of ID field.
     *            The second item at index 1 is the name of parent ID field.
     *            3-d item is the name of internal flag of group.
     *            4-th item is the name of internal flag of expanded group.
     *            5-th item is the name of internal array of children nodes.
     * @param groupLayout resource identifier of a view layout that
     *            defines the views for a group. The layout file
     *            should include at least those named views defined in "groupTo"
     * @param groupFrom A list of keys that will be fetched from the JSON object
     *            associated with each group.
     * @param groupTo The group views that should display column in the
     *            "groupFrom" parameter. These should all be TextViews. The
     *            first N views in this list are given the values of the first N
     *            columns in the groupFrom parameter.
     * @param childLayout resource identifier of a view layout that defines the
     *            views for a child (unless it is the last child within a group,
     *            in which case the lastChildLayout is used). The layout file
     *            should include at least those named views defined in "childTo"
     * @param childFrom A list of keys that will be fetched from the JSON object
     *            associated with each child.
     * @param childTo The child views that should display column in the
     *            "childFrom" parameter. These should all be TextViews. The
     *            first N views in this list are given the values of the first N
     *            columns in the childFrom parameter.
     * @throws JSONException
     */
    public SimpleJsonTreeViewAdapter(Context context,
    		JSONArray data, String[] keys,
    		int groupLayout, String[] groupFrom, int[] groupTo,
            int childLayout, String[] childFrom, int[] childTo) throws JSONException {
        this(context, data, keys, groupLayout, groupLayout, groupFrom, groupTo,
                childLayout, childLayout, childFrom, childTo);
    }

    /**
     * Constructor
     * 
     * @param context The context where the {@link TreeView} associated
     *            with this SimpleJsonTreeViewAdapter is running
     * @param data The JSON single-level array of JSON objects. Each JSON object
     *            should include ID field, optional reference to the parent ID,
     *            optional isGroup flag and all the entries specified in
     *            "groupFrom" or "childFrom" depending on whether the group item
     * @param keys An array of names of key fields and for internal use.
     *            The first item at index 0 is the name of ID field.
     *            The second item at index 1 is the name of parent ID field.
     *            3-d item is the name of internal flag of group.
     *            4-th item is the name of internal flag of expanded group.
     *            5-th item is the name of internal array of children nodes.
     * @param expandedGroupLayout resource identifier of a view layout that
     *            defines the views for an expanded group. The layout file
     *            should include at least those named views defined in "groupTo"
     * @param collapsedGroupLayout resource identifier of a view layout that
     *            defines the views for a collapsed group. The layout file
     *            should include at least those named views defined in "groupTo"
     * @param groupFrom A list of keys that will be fetched from the JSON object
     *            associated with each group.
     * @param groupTo The group views that should display column in the
     *            "groupFrom" parameter. These should all be TextViews. The
     *            first N views in this list are given the values of the first N
     *            columns in the groupFrom parameter.
     * @param childLayout resource identifier of a view layout that defines the
     *            views for a child (unless it is the last child within a group,
     *            in which case the lastChildLayout is used). The layout file
     *            should include at least those named views defined in "childTo"
     * @param childFrom A list of keys that will be fetched from the JSON object
     *            associated with each child.
     * @param childTo The child views that should display column in the
     *            "childFrom" parameter. These should all be TextViews. The
     *            first N views in this list are given the values of the first N
     *            columns in the childFrom parameter.
     * @throws JSONException
     */
    public SimpleJsonTreeViewAdapter(Context context,
    		JSONArray data, String[] keys,
    		int expandedGroupLayout, int collapsedGroupLayout,
    		String[] groupFrom, int[] groupTo,
            int childLayout, String[] childFrom, int[] childTo) throws JSONException {
        this(context, data, keys, expandedGroupLayout, collapsedGroupLayout,
                groupFrom, groupTo, childLayout, childLayout,
                childFrom, childTo);
    }

    /**
     * Constructor
     * 
     * @param context The context where the {@link TreeView} associated
     *            with this SimpleJsonTreeViewAdapter is running
     * @param data The JSON single-level array of JSON objects. Each JSON object
     *            should include ID field, optional reference to the parent ID,
     *            optional isGroup flag and all the entries specified in
     *            "groupFrom" or "childFrom" depending on whether the group item
     * @param keys An array of names of key fields and for internal use.
     *            The first item at index 0 is the name of ID field.
     *            The second item at index 1 is the name of parent ID field.
     *            3-d item is the name of internal flag of group.
     *            4-th item is the name of internal flag of expanded group.
     *            5-th item is the name of internal array of children nodes.
     * @param expandedGroupLayout resource identifier of a view layout that
     *            defines the views for an expanded group. The layout file
     *            should include at least those named views defined in "groupTo"
     * @param collapsedGroupLayout resource identifier of a view layout that
     *            defines the views for a collapsed group. The layout file
     *            should include at least those named views defined in "groupTo"
     * @param groupFrom A list of keys that will be fetched from the JSON object
     *            associated with each group.
     * @param groupTo The group views that should display column in the
     *            "groupFrom" parameter. These should all be TextViews. The
     *            first N views in this list are given the values of the first N
     *            columns in the groupFrom parameter.
     * @param childLayout resource identifier of a view layout that defines the
     *            views for a child (unless it is the last child within a group,
     *            in which case the lastChildLayout is used). The layout file
     *            should include at least those named views defined in "childTo"
     * @param lastChildLayout resource identifier of a view layout that defines
     *            the views for the last child within each group. The layout
     *            file should include at least those named views defined in
     *            "childTo"
     * @param childFrom A list of keys that will be fetched from the JSON object
     *            associated with each child.
     * @param childTo The child views that should display column in the
     *            "childFrom" parameter. These should all be TextViews. The
     *            first N views in this list are given the values of the first N
     *            columns in the childFrom parameter.
     * @throws JSONException
     */
    public SimpleJsonTreeViewAdapter(Context context,
    		JSONArray data, String[] keys,
    		int expandedGroupLayout, int collapsedGroupLayout,
    		String[] groupFrom, int[] groupTo,
            int childLayout, int lastChildLayout,
            String[] childFrom, int[] childTo) throws JSONException {
    	this.mKeys = keys;
    	if (keys.length >= 1)
    		this.mIdField = keys[0];
    	if (keys.length >= 2)
    		this.mIdParentField = keys[1];
    	this.mExpandedGroupLayout  = expandedGroupLayout;
    	this.mCollapsedGroupLayout = collapsedGroupLayout;
    	this.mGroupFrom       = groupFrom;
    	this.mGroupTo         = groupTo;
    	this.mChildLayout     = childLayout;
    	this.mLastChildLayout = lastChildLayout;
    	this.mChildFrom       = childFrom;
    	this.mChildTo         = childTo;
    	this.mNodes    = convertToTreeJSONArray(data);
    	this.mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private Boolean putChild(TreeViewNodeArray toArray, JSONObject obj) throws JSONException {
    	long id_parent = obj.optLong(this.mIdParentField, TreeViewNode.BAD_ID);
    	if (id_parent == TreeViewNode.BAD_ID) {
    		toArray.put(new TreeViewNode(obj, mKeys));
    		return true;
    	}
    	TreeViewNode node = toArray.getNodeById(id_parent);
    	if (node != null) {
    		node.putChild(new TreeViewNode(obj, mKeys));
    		return true;
    	}
		return false;
    }

    private TreeViewNodeArray convertToTreeJSONArray(JSONArray data) throws JSONException {
    	TreeViewNodeArray result = new TreeViewNodeArray();
    	ArrayList<Integer> notHandled = new ArrayList<Integer>();

    	for (int i=0; i<data.length(); i++) {
    		JSONObject obj = data.getJSONObject(i);
    		if (!(putChild(result, obj)))
    			// Parent node not found, may be, it has not yet added
    			notHandled.add(i);
    	}
    	// Now try again to add more items
    	Boolean prevAdded = true;
    	while (prevAdded && (notHandled.size() > 0)) {
    		prevAdded = false;
    		for (int i=0; i<notHandled.size();) {
    			JSONObject obj = data.getJSONObject(notHandled.get(i));
    			if (putChild(result, obj)) {
    				prevAdded = true;
    				notHandled.remove(i);
    			} else
    				i++;
    		}
    	}
    	// Forget items with id_parent pointing to nonexistent parents
    	return result;
    }

	@Override
    public boolean hasStableIds() {
        return mIdField.length() > 0;
    }
    
	@Override
	public int getCount() {
		return mNodes.getVisibleCount();
	}

	@Override
	public Object getItem(int position) {
		return getTreeViewNode(position);
	}

	public TreeViewNode getTreeViewNode(int position) {
		return mNodes.getVisibleNode(position);
	}

	@Override
	public long getItemId(int position) {
		return mNodes.getVisibleNodeId(position);
	}

	public void collapse(int position) {
		setExpanded(position, false);
	}

	public void expand(int position) {
		setExpanded(position, true);
	}

	public void setExpanded(int position, boolean expanded) {
		TreeViewNode node = getTreeViewNode(position);
		setExpanded(node, expanded);
	}

	public void setExpanded(TreeViewNode node, boolean expanded) {
		if (node != null && node.setExpanded(expanded))
			notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final TreeViewNode node = getTreeViewNode(position);
		if (node == null)
			return null;
		Boolean isGroup = node.isGroupNode();
        return createViewFromResource(node, position, convertView, parent, isGroup ?
        	(node.isCollapsed() ? mCollapsedGroupLayout : mExpandedGroupLayout) :
        	(node.isLast() ? mLastChildLayout : mChildLayout), isGroup);
	}

    private View createViewFromResource(TreeViewNode node, int position, View convertView,
            ViewGroup parent, int resource, Boolean isGroup) {
    	View v;
        if (convertView == null || convertView.getId() != (int) node.getId()) {
        	v = mInflater.inflate(R.layout.treeview_item_wrapper, parent, false);
        	v.setId((int) node.getId());
        	View list_item = mInflater.inflate(resource, parent, false);
        	((ViewGroup) v).addView(list_item);

        	View indented = v.findViewById(R.id.treeview_item_image);
        	int resId = isGroup ? mGroupIndicatorRes : mChildIndicatorRes;
        	if (resId != 0)
        		((ImageView) indented).setImageResource(resId);
        	else {
        		((ViewGroup) v).removeView(indented);
        		indented = list_item;
        	}

        	ViewGroup.LayoutParams params = indented.getLayoutParams();
        	((LinearLayout.LayoutParams) params).leftMargin = mIndent * node.getLevel();
        	indented.requestLayout();
        } else
            v = convertView;

        ImageView image = (ImageView) v.findViewById(R.id.treeview_item_image);
        if (image != null)
        	image.setImageState(STATE[node.isExpanded() ? STATE_EXPANDED :
        		!node.isGroupNode() && node.isLast() ?
        			STATE_LAST : STATE_NONE], true);

        bindView(node, v);

        return v;
    }

    private void bindView(TreeViewNode node, View view) {
        final ViewBinder binder = mViewBinder;
        final String[] from = node.hasChildren() ? mGroupFrom : mChildFrom;
        final int[] to = node.hasChildren() ? mGroupTo : mChildTo;
        final int count = to.length;

        for (int i = 0; i < count; i++) {
            final View v = view.findViewById(to[i]);
            if (v != null) {
                final Object data = node.opt(from[i]);
                String text = data == null ? "" : data.toString();
                if (text == null)
                    text = "";

                boolean bound = false;
                if (binder != null)
                    bound = binder.setViewValue(v, data, text);

                if (!bound) {
                    if (v instanceof Checkable) {
                        if (data instanceof Boolean)
                            ((Checkable) v).setChecked((Boolean) data);
                        else if (v instanceof TextView)
                            // Note: keep the instanceof TextView check at the bottom of these
                            // ifs since a lot of views are TextViews (e.g. CheckBoxes).
                            setViewText((TextView) v, text);
                        else
                            throw new IllegalStateException(v.getClass().getName() +
                                    " should be bound to a Boolean, not a " +
                                    (data == null ? "<unknown type>" : data.getClass()));
                    } else if (v instanceof TextView) {
                        // Note: keep the instanceof TextView check at the bottom of these
                        // ifs since a lot of views are TextViews (e.g. CheckBoxes).
                        setViewText((TextView) v, text);
                    } else if (v instanceof ImageView) {
                        if (data instanceof Integer)
                            setViewImage((ImageView) v, (Integer) data);                            
                        else
                            setViewImage((ImageView) v, text);
                    } else
                        throw new IllegalStateException(v.getClass().getName() + " is not a " +
                                " view that can be bounds by this SimpleAdapter");
                }
            }
        }
    }

    /**
     * Returns the {@link ViewBinder} used to bind data to views.
     *
     * @return a ViewBinder or null if the binder does not exist
     *
     * @see #setViewBinder(ViewBinder)
     */
    public ViewBinder getViewBinder() {
        return mViewBinder;
    }

    /**
     * Sets the binder used to bind data to views.
     *
     * @param viewBinder the binder used to bind data to views, can be null to
     *        remove the existing binder
     *
     * @see #getViewBinder()
     */
    public void setViewBinder(ViewBinder viewBinder) {
        mViewBinder = viewBinder;
    }

    /**
     * Called by bindView() to set the image for an ImageView but only if
     * there is no existing ViewBinder or if the existing ViewBinder cannot
     * handle binding to an ImageView.
     *
     * This method is called instead of {@link #setViewImage(ImageView, String)}
     * if the supplied data is an int or Integer.
     *
     * @param v ImageView to receive an image
     * @param value the value retrieved from the data set
     *
     * @see #setViewImage(ImageView, String)
     */
    public static void setViewImage(ImageView v, int value) {
        v.setImageResource(value);
    }

    /**
     * Called by bindView() to set the image for an ImageView but only if
     * there is no existing ViewBinder or if the existing ViewBinder cannot
     * handle binding to an ImageView.
     *
     * By default, the value will be treated as an image resource. If the
     * value cannot be used as an image resource, the value is used as an
     * image Uri.
     *
     * This method is called instead of {@link #setViewImage(ImageView, int)}
     * if the supplied data is not an int or Integer.
     *
     * @param v ImageView to receive an image
     * @param value the value retrieved from the data set
     *
     * @see #setViewImage(ImageView, int)
     */
    public static void setViewImage(ImageView v, String value) {
        try {
            v.setImageResource(Integer.parseInt(value));
        } catch (NumberFormatException nfe) {
            v.setImageURI(Uri.parse(value));
        }
    }

    /**
     * Called by bindView() to set the text for a TextView but only if
     * there is no existing ViewBinder or if the existing ViewBinder cannot
     * handle binding to a TextView.
     *
     * @param v TextView to receive text
     * @param text the text to be set for the TextView
     */
    public static void setViewText(TextView v, String text) {
        v.setText(text);
    }

    void setGroupIndicatorResource(int resId) {
    	if (mGroupIndicatorRes != resId) {
    		mGroupIndicatorRes = resId;
    		notifyDataSetInvalidated();
    	}
    }

    void setChildIndicatorResource(int resId) {
    	if (mChildIndicatorRes != resId) {
    		mChildIndicatorRes = resId;
    		notifyDataSetInvalidated();
    	}
    }

    public int getIndent() {
    	return mIndent;
    }

    void setIndent(int indent) {
    	if (indent >= 0 && indent != mIndent) {
    		mIndent = indent;
    		notifyDataSetInvalidated();
    	}
    }

    @Override
    public String toString() {
		return mNodes.toString();
    }

    /**
     * This class can be used by external clients of SimpleJsonTreeViewAdapter
     * to bind values to views.
     *
     * You should use this class to bind values to views that are not
     * directly supported by SimpleJsonTreeViewAdapter or to change the way binding
     * occurs for views supported by SimpleJsonTreeViewAdapter.
     *
     * @see #setViewImage(ImageView, int)
     * @see #setViewImage(ImageView, String)
     * @see #setViewText(TextView, String)
     */
    public static interface ViewBinder {
        /**
         * Binds the specified data to the specified view.
         *
         * When binding is handled by this ViewBinder, this method must return true.
         * If this method returns false, SimpleJsonTreeViewAdapter will attempts
         * to handle the binding on its own.
         *
         * @param view the view to bind the data to
         * @param data the data to bind to the view
         * @param textRepresentation a safe String representation of the supplied data:
         *        it is either the result of data.toString() or an empty String but it
         *        is never null
         *
         * @return true if the data was bound to the view, false otherwise
         */
        boolean setViewValue(View view, Object data, String textRepresentation);
    }

}
