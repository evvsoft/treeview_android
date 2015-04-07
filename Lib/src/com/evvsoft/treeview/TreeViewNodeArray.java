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
import org.json.JSONStringer;

public class TreeViewNodeArray extends JSONArray {

	public TreeViewNode getNodeById(long id) {
		for (int i=0; i<length(); i++)
			try {
				TreeViewNode node = ((TreeViewNode) get(i)).getNodeById(id);
				if (node != null)
					return node;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		return null;
	}

	public TreeViewNode getNode(int position) {
		for (int i=0; i<length(); i++) {
			TreeViewNode node;
			try {
				node = (TreeViewNode) getJSONObject(i);
			} catch (JSONException e) {
				e.printStackTrace();
				continue;
			}
			if (position == 0)
				return node;
			position--;
			int childCount = node.getIndirectChildrenCount();
			if (childCount > position)
				return node.getNode(position);
			position -= childCount;
		}
		return null;
	}
	
	public int getIndirectChildrenCount() {
		int result = 0;
		for (int i=0; i<length(); i++)
			try {
				result++;
				TreeViewNode node = (TreeViewNode) getJSONObject(i);
				result += node.getIndirectChildrenCount();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		return result;
	}

	public int getVisibleCount() {
		int result = 0;
		for (int i=0; i<length(); i++)
			try {
				TreeViewNode node = (TreeViewNode) getJSONObject(i);
				result += node.getVisibleCount();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		return result;
	}

	public TreeViewNode getVisibleNode(int position) {
		for (int i=0; i<length(); i++)
			try {
				TreeViewNode node = (TreeViewNode) getJSONObject(i);
				int visCount = node.getVisibleCount();
				if (visCount > position)
					return node.getVisibleNode(position);
				position -= visCount;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		return null;
	}

	public long getVisibleNodeId(int position) {
		TreeViewNode node = getVisibleNode(position);
		if (node != null)
			return node.getId();
		return TreeViewNode.BAD_ID;
	}

	/**
	 * Method is called from TreeViewNode.writeTo(JSONStringer)
	 * to add a children to the current level.
	 * The current level is the root level.
	 */
    void writeBody(JSONStringer stringer) throws JSONException {
        for (int i=0; i<length(); i++)
            stringer.value(get(i));
    }

}
