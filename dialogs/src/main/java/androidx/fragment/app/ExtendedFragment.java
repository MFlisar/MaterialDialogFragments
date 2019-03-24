package androidx.fragment.app;

public class ExtendedFragment extends DialogFragment {
    public void showAllowingStateLoss(FragmentManager manager, String tag) {
        this.mDismissed = false;
        this.mShownByMe = true;
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }

    public int showAllowingStateLoss(FragmentTransaction transaction, String tag) {
        this.mDismissed = false;
        this.mShownByMe = true;
        transaction.add(this, tag);
        this.mViewDestroyed = false;
        this.mBackStackId = transaction.commitAllowingStateLoss();
        return this.mBackStackId;
    }
}