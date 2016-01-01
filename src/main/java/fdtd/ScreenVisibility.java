package fdtd;

public enum ScreenVisibility {

    /**
     * Screen must not be shown.
     */
    MUST_HIDE(false),

    /**
     * Screen may be shown.
     */
    CAN_SHOW(true),

    /**
     * Screen must be shown.
     */
    MUST_SHOW(true);

    private final boolean canShow;

    ScreenVisibility(boolean canShow) {
        this.canShow = canShow;
    }

    public boolean canShow() {
        return canShow;
    }

}
