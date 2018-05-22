package com.xzmc.airuishi.utils;

import android.content.Context;
import android.os.AsyncTask;
import com.xzmc.airuishi.view.MyProgressDialog;

/**
 * 自定义网络异步AsyncTask
 */
public abstract class NetAsyncTask extends AsyncTask<Void, Void, Void> {
	protected Context ctx;
	boolean openDialog = true;
	Exception exception;
	MyProgressDialog dialog;

	protected NetAsyncTask(Context ctx) {
		this.ctx = ctx;
	}

	protected NetAsyncTask(Context ctx, boolean openDialog) {
		this.ctx = ctx;
		this.openDialog = openDialog;
	}

	public NetAsyncTask setOpenDialog(boolean openDialog) {
		this.openDialog = openDialog;
		return this;
	}

	public MyProgressDialog getDialog() {
		return dialog;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (openDialog) {
			dialog = new MyProgressDialog(ctx);
			dialog.show();
		}
	}

	@Override
	protected Void doInBackground(Void... params) {
		try {
			doInBack();
		} catch (Exception e) {
			e.printStackTrace();
			exception = e;
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void aVoid) {
		super.onPostExecute(aVoid);
		if (openDialog) {
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}
		}
		onPost(exception);
	}

	protected abstract void doInBack() throws Exception;

	protected abstract void onPost(Exception e);
}
