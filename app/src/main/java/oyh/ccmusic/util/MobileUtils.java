package oyh.ccmusic.util;


import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import oyh.ccmusic.activity.AppliContext;

/**
 * Created by yihong.ou on 17-9-18.
 */
public class MobileUtils {
	/**
	 * 隐藏输入法软键盘
	 * @param view
	 */
	public static void hideInputMethod(View view) {
		InputMethodManager imm = (InputMethodManager) AppliContext.sContext
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if(imm.isActive()) {
			imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
}
