package com.ewu.moonx.App;

import android.app.Activity;
import android.text.Layout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ewu.moonx.R;

import org.w3c.dom.Text;

public abstract class CustomView {
    protected Activity con;
    protected View mainView;

    public CustomView(Activity con, int R_layout) {
        this.con = con;
        mainView = con.getLayoutInflater().inflate(R_layout, null);
        initEvent();
    }

    public View getMainView() {
        return mainView;
    }

    protected View _f(int R_id) {
        return mainView.findViewById(R_id);
    }

    protected TextView _t(int R_id) {
        return mainView.findViewById(R_id);
    }

    protected ImageView _i(int R_id) {
        return mainView.findViewById(R_id);
    }

    protected ViewGroup _l(int R_id) {
        return mainView.findViewById(R_id);
    }

    protected abstract void initEvent();
}
