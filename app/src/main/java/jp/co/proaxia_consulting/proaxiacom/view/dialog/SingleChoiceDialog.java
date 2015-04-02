package jp.co.proaxia_consulting.proaxiacom.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;

import android.content.DialogInterface;
import android.os.Bundle;

import jp.co.proaxia_consulting.proaxiacom.R;

public class SingleChoiceDialog extends DialogFragment {
	private DialogInterface.OnClickListener singleChoiceClickListener = null;
	private DialogInterface.OnClickListener okClickListener = null;
	private DialogInterface.OnClickListener cancelClickListener = null;
	
	public static SingleChoiceDialog newInstance(int title, String[] items, int checkedItem) {
		SingleChoiceDialog instance = new SingleChoiceDialog();
		Bundle args = new Bundle();
		args.putInt("title", title);
		args.putStringArray("items", items);
		args.putInt("checked_item", checkedItem);
        instance.setArguments(args);
        return instance;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle safedInstanceState) {
		int title = getArguments().getInt("title");
		String[] items = getArguments().getStringArray("items");
		int checkedItem = getArguments().getInt("checked_item");
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(title);
		builder.setSingleChoiceItems(items, checkedItem, this.singleChoiceClickListener)
		.setPositiveButton("OK", this.okClickListener)
        .setNegativeButton("Cancel", this.cancelClickListener);
		
		return builder.create();
	}
	
	/**
     * SingleChoiceクリックリスナーの登録
     */	
	public void setOnSingleChoiceClickListener(DialogInterface.OnClickListener listener) {
		this.singleChoiceClickListener = listener;
	}
	
	/**
     * OKクリックリスナーの登録
     */	
	public void setOnOkClickListener(DialogInterface.OnClickListener listener) {
		this.okClickListener = listener;
	}
	
	/**
     * Cancelクリックリスナーの登録
     */	
	public void setOnCancelClickListener(DialogInterface.OnClickListener listener) {
		this.cancelClickListener = listener;
	}
}

