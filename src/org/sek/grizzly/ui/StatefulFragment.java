package org.sek.grizzly.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Base class that provides services for saving state in a fragment
 */
public class StatefulFragment extends Fragment {
	
	private ViewGroup root;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		root = new FrameLayout(getActivity());
		return root;
	}
	
	protected void setContentView(int resource) {
		root.removeAllViews();
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(resource, root, false);
		root.addView(v);
	}
	
	protected View findViewById(int id) {
		return root.findViewById(id);
	}
	
	protected void runOnUiThread(Runnable action) {
		getActivity().runOnUiThread(action);
	}
	
}
