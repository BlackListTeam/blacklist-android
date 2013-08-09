package cat.andreurm.blacklist.carousel;
/*
    GAMA - Open Source Mobile UI Component Library
    Copyright (c) 2012 Digital Aria Inc.
    licensing@digitalaria.com
    For more details, please visit: http://developer.digitalaria.com/gama/license
*/

import android.content.Context;
import android.graphics.Camera;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.FrameLayout;

/**
 * Carousel component class. Carousel component is a FrameLayout itself. Carousel component needs item list for visualizing.
 */
public class Carousel extends FrameLayout
{
    /**
     * Coverflow shape arrangement. Items are rotated along to center position. This value used as input to {@link #setType(int)}
     */
    public static final int TYPE_COVERFLOW = 0;

    /**
     * Stack shape arrangement. Items are stacked in line from near to far. This value used as input to {@link #setType(int)}
     */
    public static final int TYPE_STACK = 1;

    /**
     * Convex lenz shape arrangement. The size of item will be changed against to distance from center position. This value used as input to {@link #setType(int)}
     */
    public static final int TYPE_ROTARY = 2;

    /**
     * Cylinder shape arrangement. The items are located in inner plane of one cylinder. This value used as input to {@link #setType(int)}
     */
    public static final int TYPE_CYLINDER = 3;

    /**
     * Sector shape arrangement. Items are rotated one cycle along to scroll line. This value used as input to {@link #setType(int)}
     */
    public static final int TYPE_RING = 4;

    /**
     * Custom arrangement. User can change the item arrangement as they want. This value used as input to {@link #setType(int)}
     * @see ChildStaticTransformationListener
     */
    public static final int TYPE_CUSTOM = 10;

    private CarouselBehavior _carouselBehavior;

    public Carousel(Context context) {
        this(context, null, 0);
    }

    public Carousel(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Carousel(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setWillNotDraw(true);

        int layoutIdx=0;

        _carouselBehavior = new CarouselBehavior(context, attrs, defStyle);
        addView(_carouselBehavior, layoutIdx);
    }

    /**
     * Changes the time interval for moving its position when an item is selected.
     * @param animationDurationMillis Animation length. (in millisecond)
     */
    public void setAnimationDuration(int animationDurationMillis) {
        _carouselBehavior.setAnimationDuration(animationDurationMillis);
    }

    /**
     * Changes the length between two items. In negative case, don't excess half of item width.
     * @param spacing Length between two items.
     */
    public void setSpacing(int spacing) {
        _carouselBehavior.setSpacing(spacing);
    }

    /**
     * Changes the alpha value of unselected items.
     * @param unselectedAlpha Alpha value for unselected position.
     */
    public void setUnselectedAlpha(float unselectedAlpha) {
        _carouselBehavior.setUnselectedAlpha(unselectedAlpha);
    }

    /**
     * Returns the center position of Carousel.
     * @return Center position of Carousel.
     */
    public int getCenterOfCarousel() {
        return _carouselBehavior.getCenterOfCarousel();
    }

    /**
     * Returns an item which is touched just before.
     * @return Recently touched item.
     */
    public View getDownTouchView() {
        return _carouselBehavior.getDownTouchView();
    }

    /**
     * Changes the value whether or not move to item position if selected.
     * @param enabled In case of true, selected item will be positioned in center.
     */
    public void setAutoScrollToSelectedChildEnabled(boolean enabled) {
        _carouselBehavior.setAutoScrollToSelectedChildEnabled(enabled);
    }

    /**
     * Returns the value whether or not move to item position if selected.
     * @return AutoScrollToSelectedChild The value currently set.
     * @see #setAutoScrollToSelectedChildEnabled(boolean)
     */
    public boolean isAutoScrollToSelectedChildEnabled() {
        return _carouselBehavior.isAutoScrollToSelectedChildEnabled();
    }

    /**
     * Changes the gravity of Carousel to modifying its layout.
     * @param gravity New gravity value.
     */
    public void setGravity(int gravity) {
        _carouselBehavior.setGravity(gravity);
    }

    /**
     * Returns the current Carousel arrangement value.
     * @return Current Carousel arrangement value.
     * @see #setType(int)
     */
    public int getType() {
        return _carouselBehavior.getType();
    }

    /**
     * Changes the Carousel arrangement value.
     * @param type New Carousel arrangement value.
     * @see #getType()
     */
    public void setType(int type) {
        _carouselBehavior.setType(type);
    }

    /**
     * Returns the current scroll angle.
     * @return Current scroll angle. (in degree)
     * @see #setAngle(int)
     */
    public int getAngle() {
        return _carouselBehavior.getAngle();
    }

    /**
     * Changes the scroll angle.
     * @param angle New scroll angle. (in degree)
     * @see #getAngle()
     */
    public void setAngle(int angle) {
        _carouselBehavior.setAngle(angle);
    }

    /**
     * Returns the current scroll bounce mode. If it true, Carousel bounces at the end of list.
     * @return Current scroll bounce mode.
     * @see #setOverScrollBounceEnabled(boolean)
     */
    public boolean getOverScrollBounceEnabled() {
        return _carouselBehavior.getOverScrollBounceEnabled();
    }

    /**
     * Changes the scroll bounce mode. If it true, Carousel bounces at the end of list.
     * To activate this, {@link #setInfiniteScrollEnabled(boolean)} should be set to false.
     * @param enabled New scroll bounce mode.
     * @see #getOverScrollBounceEnabled()
     */
    public void setOverScrollBounceEnabled(boolean enabled) {
        _carouselBehavior.setOverScrollBounceEnabled(enabled);
    }

    /**
     * Returns the current infinite scroll mode.
     * @return Current infinite scroll mode.
     * @see #setInfiniteScrollEnabled(boolean)
     */
    public boolean getInfiniteScrollEnabled() {
        return _carouselBehavior.getInfiniteScrollEnabled();
    }

    /**
     * Changes the infinite scroll mode. It if true, first item is appeared when last item is passed.
     * @param enabled New infinite scroll mode.
     * @see #getInfiniteScrollEnabled()
     */
    public void setInfiniteScrollEnabled(boolean enabled) {
        _carouselBehavior.setInfiniteScrollEnabled(enabled);
    }

    /**
     * Returns the current transformation listener. It changes item arrangement of Carousel.
     * @return Current transformation listener.
     * @see ChildStaticTransformationListener
     * @see #setTransformationListener(ChildStaticTransformationListener)
     */
    public ChildStaticTransformationListener getTransformationListener() {
        return _carouselBehavior.getTransformationListener();
    }

    /**
     * Changes the transformation listener to modify item arrangement of Carousel.
     * In case of {@link #TYPE_CUSTOM}, this will be activated.
     * @param transformationListener New transformation listener.
     * @see ChildStaticTransformationListener
     * @see #getTransformationListener()
     */
    public void setTransformationListener(
            ChildStaticTransformationListener transformationListener) {
        _carouselBehavior.setTransformationListener(transformationListener);
    }

    /**
     * Returns the current item update listener.
     * @return Current item update listener.
     * @see OnItemSelectionUpdatedListener
     * @see #setOnItemSelectionUpdatedListener(OnItemSelectionUpdatedListener)
     */
    public OnItemSelectionUpdatedListener getOnItemSelectionUpdatedListener() {
        return _carouselBehavior.getOnItemSelectionUpdatedListener();
    }

    /**
     * Changes the listener which will be called when an item is positioned to center.
     * @param listener New item update listener.
     * @see OnItemSelectionUpdatedListener
     * @see #getOnItemSelectionUpdatedListener()
     */
    public void setOnItemSelectionUpdatedListener(OnItemSelectionUpdatedListener listener) {
        _carouselBehavior.setOnItemSelectionUpdatedListener(listener);
    }

    /**
     * Returns the current item rearranged listener.
     * @return Current item rearranged listener.
     * @see ItemRearrangedListener
     * @see #getItemRearrangedListener()
     */
    public ItemRearrangedListener getItemRearrangedListener() {
        return _carouselBehavior.getItemRearrangedListener();
    }

    /**
     * Changes the listener which will be called when item position has been changed.
     * @param itemRearrangedlistener New item rearranged listener.
     * @see ItemRearrangedListener
     * @see #getOnItemSelectionUpdatedListener()
     */
    public void setItemRearrangedListener(ItemRearrangedListener itemRearrangedlistener) {
        _carouselBehavior.setItemRearrangedListener(itemRearrangedlistener);
    }

    /**
     * Changes the listener which will be called when an item is clicked.
     * @param listener New Item clicked listener.
     */
    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        _carouselBehavior.setOnItemClickListener(listener);
    }

    /**
     * Returns currently item rearrange mode enabled or disabled.
     * @return Current item rearrange mode.
     * @see #setItemRearrangeEnabled(boolean)
     */
    public boolean getItemRearrangeEnabled() {
        return _carouselBehavior.getItemRearrangeEnabled();
    }

    /**
     * Changes the item rearrange mode enable or disable. If it true, item can be rearranged on long press event.
     * @param enabled New item rearrange mode.
     * @see #getItemRearrangeEnabled()
     */
    public void setItemRearrangeEnabled(boolean enabled) {
        _carouselBehavior.setItemRearrangeEnabled(enabled);
    }

    /**
     * Returns currently item rearranging event activated or not.
     * @return Current item rearranging event is activated or not.
     */
    public boolean getOnItemRearranging()
    {
        return _carouselBehavior.getOnItemRearranging();
    }

    /**
     * Returns the enabled state of this view.
     * @return Current enabled state of this view.
     * @see #setEnabled(boolean)
     */
    public boolean getEnabled() {
        return _carouselBehavior.getEnabled();
    }

    /**
     * Set the enabled state of this view.
     * When one item is selected and want to disable the touch event to Carousel, set this value as false to ignore scroll event to Carousel main view.
     * @param enabled New enabled state.
     * @see #getEnabled()
     */
    public void setEnabled(boolean enabled) {
        _carouselBehavior.setEnabled(enabled);
    }

    /**
     * Returns current scroll speed.
     * @return Current scroll speed.
     * @see #setScrollSpeed(float)
     */
    public float getScrollSpeed() {
        return _carouselBehavior.getScrollSpeed();
    }

    /**
     * Changes the scroll speed.
     * @param speed Times to default scroll speed.
     * @see #getScrollSpeed()
     */
    public void setScrollSpeed(float speed) {
        _carouselBehavior.setScrollSpeed(speed);
    }

    /**
     * If childIndex is valid position, scroll flies to input view position.
     * @param childIndex The position that scroll move to.
     * @return True if childIndex is valid and fling successful.
     */
    public boolean scrollToIndex(int childIndex)
    {
        return _carouselBehavior.scrollToIndex(childIndex);
    }

    /**
     * Changes the center position of the Carousel.
     * @param position The index which will be positioned to center.
     */
    public void setCenterPosition(int position) {
        _carouselBehavior.setCenterPosition(position);
    }

    /**
     * Returns the current selected view.
     * @return Current selected view.
     */
    public View getSelectedView() {
        return _carouselBehavior.getSelectedView();
    }

    /**
     * Returns the current selected item position.
     * @return Current selected item position.
     */
    public int getSelectedItemPosition() {
        return _carouselBehavior.getSelectedItemPosition();
    }

    /**
     * Returns the item adapter which Carousel currently used.
     * @return Current item adapter.
     * @see #setAdapter(android.widget.Adapter)
     */
    public Adapter getAdapter() {
        return _carouselBehavior.getAdapter();
    }

    /**
     * Changes the item adapter which Carousel will use.
     * @param adapter New item adapter.
     * @see #getAdapter()
     */
    public void setAdapter(Adapter adapter) {
        _carouselBehavior.setAdapter(adapter);
    }

    /**
     * Stop the scroll and free all data allocation.
     */
    public void destroy() {
        _carouselBehavior.destroy();
    }

    /**
     * Custom interface which transforms item arrangement along to Carousel scroll.
     */
    public static interface ChildStaticTransformationListener {

        /**
         * User defined transformation method.
         * @param child Target view.
         * @param camera Camera class for transformation.
         * @return Whether or not this transformation will be used or not.
         */
        public abstract boolean setChildStaticTransformation(View child,
                                                             Camera camera);
    }

    /**
     * Custom interface which is occurred when an item is selected.
     */
    public static interface OnItemSelectionUpdatedListener {

        /**
         * A call-back method when an item is selected by scrolling to center.
         * @param parent Parent of selected view.
         * @param view Selected view.
         * @param position The position in adapter of selected view.
         */
        public abstract void onItemSelectionUpdated(AdapterView<?> parent, View view, int position);
    }

    /**
     * Custom interface which is occurred when item position has been rearranged.
     */
    public static interface ItemRearrangedListener {

        /**
         * A call-back method when item position has been rearranged.
         * @param from Start index.
         * @param to End index.
         */
        public abstract void itemMoved(int from, int to);
    }
}
