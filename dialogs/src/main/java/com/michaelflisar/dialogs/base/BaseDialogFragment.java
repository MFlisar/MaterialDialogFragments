package com.michaelflisar.dialogs.base;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.michaelflisar.dialogs.DialogSetup;
import com.michaelflisar.dialogs.events.BaseDialogEvent;
import com.michaelflisar.dialogs.helper.BaseDialogFragmentHandler;
import com.michaelflisar.dialogs.interfaces.DialogFragment;
import com.michaelflisar.dialogs.utils.DialogUtil;

import androidx.fragment.app.ExtendedFragment;
import androidx.fragment.app.FragmentActivity;

public abstract class BaseDialogFragment extends ExtendedFragment implements BaseDialogFragmentHandler.IBaseDialog, DialogFragment
{
    private static final String EXTRA_KEY = BaseDialogFragment.class.getName() + "|extraData";
    private BaseDialogFragmentHandler<BaseDialogFragment> mHandler;

    public BaseDialogFragment() {
        mHandler = new BaseDialogFragmentHandler<>(EXTRA_KEY, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler.onCreate(savedInstanceState);
    }

    @Override
    public final Dialog onCreateDialog(Bundle savedInstanceState) {
        return mHandler.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        mHandler.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mHandler.onSaveInstanceState(outState);
    }

    // -----------------------------
    // helper functions
    // -----------------------------

    public static String convertResOrString(Context context, Object value) {
        if ((value == null || (value instanceof Integer && ((Integer) value) <= 0))) {
            return null;
        } else if (value instanceof String) {
            return (String) value;
        } else if (value instanceof Integer) {
            return context.getString((Integer) value);
        } else {
            return null;
        }
    }

    // -----------------------------
    // interfaces
    // -----------------------------

    public abstract Dialog onHandleCreateDialog(Bundle savedInstanceState);

    // -----------------------------
    // forward functions to handler
    // -----------------------------

    public Bundle getExtra() {
        return mHandler.getExtra();
    }

    public Bundle createExtra() {
        mHandler.createExtra();
        return mHandler.getExtra();
    }

    public void show(FragmentActivity activity) {
        mHandler.show(activity, this);
    }

    public void showAllowingStateLoss(FragmentActivity activity) {
        mHandler.showAllowingStateLoss(activity, this);
    }

    public void show(FragmentActivity activity, String tag) {
        mHandler.show(activity, tag);
    }

    // -----------------------------
    // Result
    // -----------------------------

    protected <X extends BaseDialogEvent> void sendEvent(X event) {
        DialogSetup.INSTANCE.sendResult(event);
        trySendResultToActivity(event);
    }

    private final <X extends BaseDialogEvent> void trySendResultToActivity(X event) {
        DialogUtil.INSTANCE.trySendResult(event, getActivity());
    }
}