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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

public class TreeViewNode extends JSONObject {

	public static final long   BAD_ID = -1;
	public static final String DEFAULT_ID_NAME        = "id";
	public static final String DEFAULT_ID_PARENT_NAME = "id_parent";
	public static final String DEFAULT_IS_GROUP_NAME  = "is_group";
	public static final String DEFAULT_EXPANDED_NAME  = "expanded";
	public static final String DEFAULT_CHILDREN_ARRAY_NAME = "TreeViewChildren";
	private static final int DEFAULT_NAME_COUNT = 5;

	private String mIdFieldName;
	private String mIdParentName;
	private String mIsGroupName;
	private String mExpandedName;
	private String mChildrenArrayName;
	private long mIdParent = BAD_ID;
	private int mLevel = 0;
	private Boolean mIsGroup = false;
	private Boolean mExpanded = false;
	private Boolean mIsLast = false;

	public TreeViewNode(JSONObject obj) throws JSONException {
		this(obj, null);
	}

	/**
	 * 
	 * @param obj
	 * @param keyNames
	 * @throws JSONException
	 */
	public TreeViewNode(JSONObject obj, String[] keyNames) throws JSONException {
		super();

		int size = keyNames == null ? 0 : keyNames.length;
		for (int i=0; i<DEFAULT_NAME_COUNT; i++) {
			String key = (i<size && keyNames[i] != null && !keyNames[i].isEmpty()) ? keyNames[i] : null;
			switch (i) {
			case 0:
				mIdFieldName = key == null ? DEFAULT_ID_NAME : key;
				break;
			case 1:
				mIdParentName = key == null ? DEFAULT_ID_PARENT_NAME : key;
				break;
			case 2:
				mIsGroupName = key == null ? DEFAULT_IS_GROUP_NAME : key;
				break;
			case 3:
				mExpandedName = key == null ? DEFAULT_EXPANDED_NAME : key;
				break;
			case 4:
				mChildrenArrayName = key == null ? DEFAULT_CHILDREN_ARRAY_NAME : key;
				break;
			}
		}

		JSONArray names = obj.names();
		for (int i=0; i < names.length(); i++) {
			String name = names.optString(i, "");
			if (name.equals(mChildrenArrayName))
				throw new JSONException(mChildrenArrayName +
						"is bad name for children array.");
			if (name.isEmpty() || name.equals(mIdParentName))
				continue;
			if (name.equals(mIsGroupName)) {
				mIsGroup = obj.optInt(mIsGroupName, 0) != 0;
				continue;
			}
			if (name.equals(mExpandedName)) {
				mExpanded = obj.optInt(mExpandedName, 0) != 0;
				continue;
			}
			put(name, obj.opt(name));
		}
	}

	public long getId() {
		return optLong(mIdFieldName, BAD_ID);
	}

	public TreeViewNode getNodeById(long id) {
		try {
			if (getLong(mIdFieldName) == id)
				return this;
			if (has(mChildrenArrayName))
				return ((TreeViewNodeArray) getJSONArray(mChildrenArrayName)).getNodeById(id);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public TreeViewNode getNode(int position) {
		if (position < 0)
			return null;
		if (position == 0)
			return this;
		TreeViewNodeArray children = getChildren();
		return children == null ? null : children.getNode(position-1);
	}

	public TreeViewNodeArray getChildren() {
		if (has(mChildrenArrayName))
			try {
				return (TreeViewNodeArray) getJSONArray(mChildrenArrayName);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		return null;
	}

	/**
	 * Add child node to this node
	 * @param treeViewNode - added child node
	 * @throws JSONException 
	 */
	public void putChild(TreeViewNode treeViewNode) throws JSONException {
		TreeViewNodeArray children = getChildren();
		if (children == null) {
			children = new TreeViewNodeArray();
			put(mChildrenArrayName, children);
		}
		treeViewNode.mIdParent = optLong(mIdFieldName, BAD_ID);
		treeViewNode.setLevel(mLevel + 1);
		treeViewNode.mIsLast = true;
		if (children.length() > 0)
			((TreeViewNode) children.getJSONObject(children.length()-1)).mIsLast = false;
		mIsGroup = true;
		children.put(treeViewNode);
	}

	public int getLevel() {
		return mLevel;
	}

	private void setLevel(int level) {
		mLevel = level;
		TreeViewNodeArray children = getChildren();
		if (children != null)
			for (int i=0; i<children.length(); i++) {
				try {
					TreeViewNode node = (TreeViewNode) children.getJSONObject(i);
					node.setLevel(mLevel + 1);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
	}

	public int getIndirectChildrenCount() {
		int result = 0;
		TreeViewNodeArray children = getChildren();
		if (children != null)
			result = children.getIndirectChildrenCount();
		return result;
	}

	public int getVisibleCount() {
		int result = 1;
		if (isExpanded()) {
			TreeViewNodeArray children = getChildren();
			if (children != null)
				result += children.getVisibleCount();
		}
		return result;
	}

	public TreeViewNode getVisibleNode(int position) {
		if (position < 0)
			return null;
		if (position == 0)
			return this;
		if (isExpanded()) {
			TreeViewNodeArray children = getChildren();
			if (children != null)
				return children.getVisibleNode(position - 1);
		}
		return null;
	}

	public boolean hasChildren() {
		if (has(mChildrenArrayName))
			try {
				return getJSONArray(mChildrenArrayName).length() > 0;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		return false;
	}

	public boolean isGroupNode() {
		return mIsGroup;
	}

	public boolean isCollapsed() {
		return isGroupNode() && !mExpanded;
	}

	public boolean isExpanded() {
		return isGroupNode() && mExpanded;
	}

	public boolean isLast() {
		return mIsLast;
	}

	boolean setExpanded(boolean expanded) {
		if (isGroupNode() && expanded != mExpanded) {
			mExpanded = expanded;
			return true;
		}
		return false;
	}

	/**
	 * Overrided method called from super.toString()
	 */
    void writeTo(JSONStringer stringer) throws JSONException {
        stringer.object();
        writeBody(stringer);
        stringer.endObject();

		TreeViewNodeArray children = getChildren();
		if (children != null) 
			children.writeBody(stringer);
    }

    void writeBody(JSONStringer stringer) throws JSONException {
		JSONArray names = names();
		if (names != null) {
			for (int i=0; i < names.length(); i++) {
				String name = names.optString(i, "");
				if (name.equals(mChildrenArrayName))
					continue;
				stringer.key(name).value(opt(name));
			}
			if (mIdParent != BAD_ID)
				stringer.key(mIdParentName).value(mIdParent);
			if (mIsGroup)
				stringer.key(mIsGroupName).value(1);
			if (isExpanded())
				stringer.key(mExpandedName).value(1);
		}
    }

}
