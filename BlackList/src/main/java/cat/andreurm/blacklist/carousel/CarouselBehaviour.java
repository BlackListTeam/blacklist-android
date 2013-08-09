package cat.andreurm.blacklist.carousel;
/*
 * Copyright (C) 2007 The Android Open Source Project
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
 *
 * This source code is also licensed under the terms of the
 * Digital Aria GAMA Open Source Software License (the "GAMA license");
 * you may use this component after to agree the GAMA license.
 *
 * GAMA - Open Source Mobile UI Component Library
 *
 *  Copyright (c) 2012 Digital Aria Inc.
 *  licensing@digitalaria.com
 *  For more details, please visit: http://developer.digitalaria.com/gama/license
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Transformation;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;


class CarouselBehavior extends AdapterView<Adapter> implements GestureDetector.OnGestureListener
{
    /**
     * Duration in milliseconds from the start of a scroll during which we're
     * unsure whether the user is scrolling or flinging.
     */
    private static final int SCROLL_TO_FLING_UNCERTAINTY_TIMEOUT = 250;

    /**
     * Horizontal spacing between items.
     */
    private int _spacing = 0;

    /**
     * How long the transition animation should run when a child view changes
     * position, measured in milliseconds.
     */
    private int _animationDuration = 400;

    /**
     * The alpha of items that are not selected.
     */
    private float _unselectedAlpha;

    /**
     * Left most edge of a child seen so far during layout.
     */
    private int _leftMost;

    /**
     * Right most edge of a child seen so far during layout.
     */
    private int _rightMost;

    private int _gravity;

    /**
     * Helper for detecting touch gestures.
     */
    private GestureDetector _gestureDetector;

    /**
     * The position of the item that received the user's down touch.
     */
    private int _downTouchPosition;

    /**
     * The view of the item that received the user's down touch.
     */
    private View _downTouchView;

    /**
     * Executes the delta scrolls from a fling or scroll movement.
     */
    private FlingRunnable _flingRunnable = new FlingRunnable();

    /**
     * Sets mSuppressSelectionChanged = false. This is used to set it to false
     * in the future. It will also trigger a selection changed.
     */
    private Runnable _disableSuppressSelectionChangedRunnable = new Runnable() {
        public void run() {
            _suppressSelectionChanged = false;
            selectionChanged();
        }
    };

    /**
     * When fling runnable runs, it resets this to false. Any method along the
     * path until the end of its run() can set this to true to abort any
     * remaining fling. For example, if we've reached either the leftmost or
     * rightmost item, we will set this to true.
     */
    private boolean _shouldStopFling;

    /**
     * The currently selected item's child.
     */
    private View _selectedChild;

    /**
     * Whether to continuously callback on the item selected listener during a
     * fling.
     */
    private boolean _shouldCallbackDuringFling = true;

    /**
     * Whether to callback when an item that is not selected is clicked.
     */
    private boolean _shouldCallbackOnUnselectedItemClick = true;

    /**
     * If true, do not callback to item selected listener.
     */
    private boolean _suppressSelectionChanged;

    /**
     * If true, we have received the "invoke" (center or enter buttons) key
     * down. This is checked before we action on the "invoke" key up, and is
     * subsequently cleared.
     */
    private boolean _receivedInvokeKeyDown;

    private AdapterContextMenuInfo _contextMenuInfo;

    private long _lastUpdateEvent;

    /**
     * If true, this onScroll is the first for this user's drag (remember, a
     * drag sends many onScrolls).
     */
    private boolean _isFirstScroll;
    private int _itemCount = 0;
    private int _firstPosition = 0;
    private int _selectedPosition = AdapterView.INVALID_POSITION;
    private RecycleBin _recycler = new RecycleBin();
    private int _widthMeasureSpec;
    private int _heightMeasureSpec;
    private boolean _inLayout = false;
    private Rect _touchFrame;
    private SelectionNotifier _selectionNotifier;
    private boolean _dataChanged = true;
    private int _fillOffset = 0;
    private AdapterDataSetObserver _dataSetObserver;
    private int _oldItemCount = 0;

    private int _type;
    private int _centerCoordX;
    private int _centerCoordY;
    private int _angle;
    private boolean _doNotCallBack = false;
    private boolean _overScrollBounceEnabled = false;
    private boolean _infiniteScrollEnabled = false;
    private Camera _camera = new Camera();
    private Carousel.ChildStaticTransformationListener _transformationListener;
    private CarouselAdapter _adapter;
    private float _velocityMultiplier;
    private boolean _enabled = true;
    private Carousel.OnItemSelectionUpdatedListener _itemSelectionUpdatedListener;
    private boolean _autoScrollToSelectedChild = true;
    private int _lastPosition;

    /**
     * For rearranging carousel items
     */
    private boolean _onItemRearranging = false;
    private Carousel.ItemRearrangedListener _itemRearrangedListener;
    private WindowManager.LayoutParams _windowParams;
    private WindowManager _windowManager;
    private ImageView _dragImageView;
    private Bitmap _dragBitmap;
    private int _originalItemPosition;
    private View _movingItemView;
    private int _currentItemPosition;
    private int _previousTargetItemPosition;
    private boolean _itemRearrangeEnabled = true;

    public CarouselBehavior(Context context) {
        this(context, null);
    }

    public CarouselBehavior(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CarouselBehavior(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        _gestureDetector = new GestureDetector(context, this);
        _gestureDetector.setIsLongpressEnabled(true);

        TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.Carousel, defStyle, 0);

        int index = a.getInt(R.styleable.Carousel_android_gravity, Gravity.CENTER_VERTICAL);
        if (index >= 0) {
            setGravity(index);
        }

        int animationDuration =
                a.getInt(R.styleable.Carousel_android_animationDuration, 400);
        if (animationDuration > 0) {
            setAnimationDuration(animationDuration);
        }

        int spacing =
                a.getDimensionPixelOffset(R.styleable.Carousel_spacing, 0);
        setSpacing(spacing);

        float unselectedAlpha = a.getFloat(
                R.styleable.Carousel_unselectedAlpha, 1.0f);
        setUnselectedAlpha(unselectedAlpha);

        this.setStaticTransformationsEnabled(true);
        this.setChildrenDrawingOrderEnabled(true);
        this.setOverScrollBounceEnabled(false);
        this.setInfiniteScrollEnabled(false);
        _type = Carousel.TYPE_COVERFLOW;
        _centerCoordX = 0;
        _centerCoordY = 0;
        _angle = 0;
        _velocityMultiplier = 1;

    }

    /**
     * Sets how long the transition animation should run when a child view
     * changes position. Only relevant if animation is turned on.
     *
     * @param animationDurationMillis The duration of the transition, in
     *        milliseconds.
     */
    public void setAnimationDuration(int animationDurationMillis) {
        _animationDuration = animationDurationMillis;
    }

    /**
     * Sets the spacing between items in a Carousel.
     *
     * @param spacing The spacing in pixels between items in the Carousel
     */
    public void setSpacing(int spacing) {
        _spacing = spacing;
    }

    /**
     * Sets the alpha of items that are not selected in the Carousel.
     *
     * @param unselectedAlpha the alpha for the items that are not selected.
     */
    public void setUnselectedAlpha(float unselectedAlpha) {
        _unselectedAlpha = unselectedAlpha;
    }

    @Override
    protected boolean getChildStaticTransformation(View child, Transformation t) {
        final int childCenterCoord = child.getLeft() + child.getWidth() / 2;
        final int childWidth = child.getWidth();
        final int imageWidth = child.getLayoutParams().width;
        final int imageHeight = child.getLayoutParams().height;

        t.clear();
        t.setTransformationType(Transformation.TYPE_MATRIX);
        t.setAlpha(child == this.getSelectedView() ? 1.0f : _unselectedAlpha);

        _camera.save();
        final Matrix matrix = t.getMatrix();
        float translateX = 0;
        float translateY = 0;
        float translateZ = 0;
        float rotateY = 0;

        if (_type == Carousel.TYPE_COVERFLOW) {
            int mMaxRotationAngle = 60;
            int rotationAngle = (int) (((float) (_centerCoordX - childCenterCoord) / childWidth) * mMaxRotationAngle);

            if (Math.abs(rotationAngle) > mMaxRotationAngle) {
                rotationAngle = (rotationAngle < 0) ? -mMaxRotationAngle
                        : mMaxRotationAngle;
            }

            rotateY = rotationAngle;
            _camera.rotateY(rotateY);
        } else if (_type == Carousel.TYPE_STACK) {
            translateX = (_centerCoordX - childCenterCoord) * 0.5f;
            translateZ = childCenterCoord / 4;
            rotateY = -15.0f;

            _camera.translate(translateX, translateY, translateZ);
        } else if (_type == Carousel.TYPE_ROTARY) {
            float arc = (float) Math.PI * 2.0f;
            float radius = childWidth / 2.0f
                    / (float) Math.tan(arc / 2.0f / 20);
            float angle = (childCenterCoord - _centerCoordX) / 200.0f
                    / 20 * arc;

            _camera.translate(_centerCoordX - childCenterCoord, 0, 0);
            translateZ = (float) (-radius * Math.cos(angle) + radius);
            _camera.translate((float) (radius * Math.sin(angle)), 0, translateZ-100);
        } else if(_type == Carousel.TYPE_CYLINDER) {
            float angle = 40 * (_centerCoordX - childCenterCoord)/(this.getWidth()/2);
            if(angle!=0)
            {
                _camera.translate(0.0f, 0.0f, 50 - (float)Math.abs((childCenterCoord - _centerCoordX)
                        / Math.sin(Math.PI * angle/180) * (1 - Math.cos(Math.PI * angle/180)) ));
            }
            else _camera.translate(0.0f, 0.0f, 50.0f);
            _camera.rotateZ(_angle);
            _camera.rotateY(angle);


        } else if(_type == Carousel.TYPE_RING) {
            float arc = (float) Math.PI * 2.0f;
            float angle = (childCenterCoord - _centerCoordX) / 200.0f
                    / 20 * arc;

            _camera.translate(0, 0, Math.abs(_centerCoordX-childCenterCoord)-300);
            _camera.rotateZ(-angle*180.0f/(float)Math.PI);



        } else if (_type == Carousel.TYPE_CUSTOM) {
            _transformationListener
                    .setChildStaticTransformation(child, _camera);
        }

        _camera.getMatrix(matrix);

        matrix.preTranslate(-(imageWidth / 2), -(imageHeight / 2));
        matrix.postTranslate((imageWidth / 2), (imageHeight / 2));
        // Set Angle
        matrix.postTranslate(
                (_centerCoordX - childCenterCoord)
                        - (_centerCoordX - childCenterCoord)
                        * (float) Math.cos(_angle * Math.PI / 180.0f),
                (_centerCoordX - childCenterCoord)
                        * (float) Math.sin(_angle * Math.PI / 180.0f));

        _camera.restore();
        this.invalidate();
        return true;
    }

    protected int computeHorizontalScrollExtent() {
        return 1;
    }

    protected int computeHorizontalScrollOffset() {
        return _selectedPosition;
    }

    protected int computeHorizontalScrollRange() {
        return _itemCount;
    }

    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new CarouselBehavior.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void setSelection(int position) {
        this._selectedPosition = position;
        selectionChanged();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
		/*
		 * Remember that we are in layout to prevent more layout request from
		 * being generated.
		 */
        _inLayout = true;
        long now = SystemClock.uptimeMillis();
        if (Math.abs(now - _lastUpdateEvent) > 150) {
            super.onLayout(changed, l, t, r, b);
            layout(0, false);
        }
        _inLayout = false;
    }

    /**
     * Tracks a motion scroll. In reality, this is used to do just about any
     * movement to items (touch scroll, arrow-key scroll, set an item as selected).
     *
     * @param deltaX Change in X from the previous event.
     */
    private void trackMotionScroll(int deltaX) {

        if (getChildCount() == 0) {
            return;
        }

        boolean toLeft = deltaX < 0;

        int limitedDeltaX = getLimitedMotionScrollAmount(toLeft, deltaX);
        if (limitedDeltaX != deltaX) {
            // The above call returned a limited amount, so stop any scrolls/flings
            _flingRunnable.endFling(false);
            onFinishedMovement();
        }

        offsetChildrenLeftAndRight(limitedDeltaX);

        detachOffScreenChildren(toLeft);

        if (toLeft) {
            // If moved left, there will be empty space on the right
            fillToCarouselRight();
        } else {
            // Similarly, empty space on the left
            fillToCarouselLeft();
        }

        // Clear unused views
        _recycler.clear();

        setSelectionToCenterChild();

        invalidate();
    }

    private int getLimitedMotionScrollAmount(boolean motionToLeft, int deltaX) {
        if(this._infiniteScrollEnabled)
            return deltaX;

        int extremeItemPosition = motionToLeft ? _itemCount - 1 : 0;
        View extremeChild = getChildAt(extremeItemPosition - _firstPosition);

        if (extremeChild == null) {
            return deltaX;
        }

        int extremeChildCenter = getCenterOfView(extremeChild);
        int carouselCenter = getCenterOfCarousel();

        if (motionToLeft) {
            if (extremeChildCenter <= carouselCenter) {

                // The extreme child is past his boundary point!
                return 0;
            }
        } else {
            if (extremeChildCenter >= carouselCenter) {

                // The extreme child is past his boundary point!
                return 0;
            }
        }

        int centerDifference = carouselCenter - extremeChildCenter;

        return motionToLeft
                ? Math.max(centerDifference, deltaX)
                : Math.min(centerDifference, deltaX);

    }

    /**
     * Offset the horizontal location of all children of this view by the
     * specified number of pixels.
     *
     * @param offset the number of pixels to offset
     */
    private void offsetChildrenLeftAndRight(int offset) {
        for (int i = getChildCount() - 1; i >= 0; i--) {
            getChildAt(i).offsetLeftAndRight(offset);
        }
    }

    /**
     * Returns center position of Carousel.
     * @return The center of this Carousel.
     */
    public int getCenterOfCarousel() {
        return (getWidth() - getPaddingLeft() - getPaddingRight()) / 2 + getPaddingLeft();
    }

    /**
     * @return The center of the given view.
     */
    public static int getCenterOfView(View view) {
        if(view == null) return 0;
        return view.getLeft() + view.getWidth() / 2;
    }

    /**
     * Detaches children that are off the screen (i.e.: Carousel bounds).
     *
     * @param toLeft Whether to detach children to the left of the Carousel, or
     *            to the right.
     */
    private void detachOffScreenChildren(boolean toLeft) {
        int numChildren = getChildCount();
        int firstPosition = _firstPosition;
        int start = 0;
        int count = 0;

        if (toLeft) {
            final int carouselLeft = getPaddingLeft();
            for (int i = 0; i < numChildren; i++) {
                final View child = getChildAt(i);
                if (child.getRight() >= carouselLeft - _fillOffset) {
                    break;
                } else {
                    count++;
                    _recycler.put((firstPosition + i + _itemCount)%_itemCount, child);
                }
            }
        } else {
            final int carouselRight = getWidth() - getPaddingRight();
            for (int i = numChildren - 1; i >= 0; i--) {
                final View child = getChildAt(i);
                if (child.getLeft() <= carouselRight + _fillOffset) {
                    break;
                } else {
                    start = i;
                    count++;
                    _recycler.put((firstPosition + i + _itemCount)%_itemCount, child);
                }
            }
        }

        detachViewsFromParent(start, count);

        if (toLeft) {
            _firstPosition += count;

            _firstPosition %= _itemCount;
        }
    }

    /**
     * Scrolls the items so that the selected item is in its 'slot' (its center
     * is the carousel's center).
     */
    private void scrollIntoSlots() {
        if (getChildCount() == 0 || _selectedChild == null) return;

        int selectedCenter = getCenterOfView(_selectedChild);
        int targetCenter = getCenterOfCarousel();

        int scrollAmount = targetCenter - selectedCenter;
        if (scrollAmount != 0) {
            _flingRunnable.startUsingDistance(scrollAmount);
        } else {
            onFinishedMovement();
        }
    }

    private void onFinishedMovement() {
        if (_suppressSelectionChanged) {
            _suppressSelectionChanged = false;

            // We haven't been callbacking during the fling, so do it now
            selectionChanged();
        }
        invalidate();
    }

    private void selectionChanged() {
        if (getOnItemSelectedListener() != null) {
            if (_inLayout) {
                // If we are in a layout traversal, defer notification
                // by posting. This ensures that the view tree is
                // in a consistent state and is able to accomodate
                // new layout or invalidate requests.
                if (_selectionNotifier == null) {
                    _selectionNotifier = new SelectionNotifier();
                }
                _selectionNotifier.post(_selectionNotifier);
            } else {
                fireOnSelected();
            }
        }
    }

    /**
     * Looks for the child that is closest to the center and sets it as the
     * selected child.
     */
    private void setSelectionToCenterChild() {

        View selView = _selectedChild;
        if (_selectedChild == null) return;

        int carouselCenter = getCenterOfCarousel();
        // Common case where the current selected position is correct
        if (selView.getLeft() <= carouselCenter && selView.getRight() >= carouselCenter) {
            return;
        }

        int closestEdgeDistance = Integer.MAX_VALUE;
        int newSelectedChildIndex = 0;
        for (int i = getChildCount() - 1; i >= 0; i--) {

            View child = getChildAt(i);

            float childCenter = child.getLeft() + child.getWidth()/2.0f;
            if(Math.abs(childCenter - carouselCenter) < closestEdgeDistance)
            {
                closestEdgeDistance = (int)Math.abs(childCenter - carouselCenter);
                newSelectedChildIndex = i;
            }
        }

        if(_overScrollBounceEnabled)
        {
            if(newSelectedChildIndex==0) newSelectedChildIndex++;
            else if(newSelectedChildIndex==getChildCount()-1) newSelectedChildIndex--;
        }

        int newPos = _firstPosition + newSelectedChildIndex;

        if(_infiniteScrollEnabled) newPos=newPos%_itemCount;

        if (newPos != _selectedPosition) {
            setSelectedPositionInt(newPos);
            //            setNextSelectedPositionInt(newPos);
            //            checkSelectionChanged();
        }
    }

    /**
     * Creates and positions all views for this Carousel.
     * <p>
     * We layout rarely, most of the time {@link #trackMotionScroll(int)} takes
     * care of repositioning, adding, and removing children.
     *
     * @param delta Change in the selected position. +1 means the selection is
     *            moving to the right, so views are scrolling to the left. -1
     *            means the selection is moving to the left.
     */
    private void layout(int delta, boolean animate) {
        int childrenLeft = 0;
        int childrenWidth = getRight() - getLeft();

        //if (_dataChanged) {
        //   handleDataChanged();
        //}

        // Handle an empty carousel by removing all views.
        if (_itemCount == 0) {
            resetList();
            return;
        }

        // Update to the new selected position.
        if (_selectedPosition >= 0) {
            setSelectedPositionInt(_selectedPosition);
        }

        // All views go in recycler while we are in layout
        recycleAllViews();

        // Clear out old views
        //removeAllViewsInLayout();
        detachAllViewsFromParent();

		/*
		 * These will be used to give initial positions to views entering the
		 * carousel as we scroll
		 */
        _rightMost = 0;
        _leftMost = 0;

        // Make selected view and center it

		/*
		 * mFirstPosition will be decreased as we add views to the left later
		 * on. The 0 for x will be offset in a couple lines down.
		 */
        _firstPosition = _selectedPosition;
        View sel = makeAndAddView(_selectedPosition, 0, 0, true);

        // Put the selected child in the center
        int selectedOffset = childrenLeft + (childrenWidth / 2) - (sel.getWidth() / 2);
        sel.offsetLeftAndRight(selectedOffset);

        fillToCarouselRight();
        fillToCarouselLeft();

        // Flush any cached views that did not get reused above
        _recycler.clear();

        invalidate();
        //checkSelectionChanged();

        _dataChanged = false;
        //mNeedSync = false;
        //setNextSelectedPositionInt(mSelectedPosition);

        updateSelectedItemMetadata();
        selectionChanged();
    }

    private void fillToCarouselLeft() {
        int itemSpacing = _spacing;
        int carouselLeft = getPaddingLeft();
        int numItems = _itemCount;

        // Set state for initial iteration
        View prevIterationView = getChildAt(0);
        int curPosition;
        int curRightEdge;

        if (prevIterationView != null) {
            curPosition = _firstPosition - 1;
            curRightEdge = prevIterationView.getLeft() - itemSpacing;
        } else {
            // No children available!
            curPosition = 0;
            curRightEdge = getRight() - getLeft() - getPaddingRight();
            _shouldStopFling = true;
        }

        if (_infiniteScrollEnabled &&  curPosition < 0) {
            _firstPosition = numItems;
            curPosition = _firstPosition - 1;
        }

        while (curRightEdge > carouselLeft - _fillOffset && curPosition >= 0) {
            prevIterationView = makeAndAddView(curPosition, curPosition - _selectedPosition,
                    curRightEdge, false);
            // Remember some state
            _firstPosition = curPosition;

            // Set state for next iteration
            curRightEdge = prevIterationView.getLeft() - itemSpacing;
            curPosition--;
            if(_infiniteScrollEnabled && curPosition<0) curPosition = (curPosition + _itemCount)%_itemCount;
        }
    }

    private void fillToCarouselRight() {
        int itemSpacing = _spacing;
        int carouselRight = getRight() - getLeft() - getPaddingRight();
        int numChildren = getChildCount();
        int numItems = _itemCount;

        // Set state for initial iteration
        View prevIterationView = getChildAt(numChildren - 1);
        int curPosition;
        int curLeftEdge;

        if (prevIterationView != null) {
            if (_infiniteScrollEnabled) {
                curPosition = _firstPosition + numChildren;
                curPosition = curPosition % numItems;
            } else {
                curPosition = _firstPosition + numChildren;
            }
            curLeftEdge = prevIterationView.getRight() + itemSpacing;
        } else {
            _firstPosition = curPosition = _itemCount - 1;
            curLeftEdge = getPaddingLeft();
            _shouldStopFling = true;
        }

        while (curLeftEdge < carouselRight + _fillOffset && (_infiniteScrollEnabled || curPosition < numItems)) {
            prevIterationView = makeAndAddView(curPosition, curPosition - _selectedPosition,
                    curLeftEdge, true);

            // Set state for next iteration
            curLeftEdge = prevIterationView.getRight() + itemSpacing;
            curPosition++;
            if(_infiniteScrollEnabled) curPosition = curPosition % numItems;
        }
    }

    /**
     * Obtain a view, either by pulling an existing view from the recycler or by
     * getting a new one from the adapter. If we are animating, make sure there
     * is enough information in the view's layout parameters to animate from the
     * old to new positions.
     *
     * @param position Position in the carousel for the view to obtain
     * @param offset Offset from the selected position
     * @param x X-coordintate indicating where this view should be placed. This
     *        will either be the left or right edge of the view, depending on
     *        the fromLeft parameter
     * @param fromLeft Are we posiitoning views based on the left edge? (i.e.,
     *        building from left to right)?
     * @return A view that has been added to the carousel
     */
    private View makeAndAddView(int position, int offset, int x,
                                boolean fromLeft) {
        View child;

        if (!_dataChanged) {
            child = _recycler.get(position);
            if (child != null) {
                // Can reuse an existing view
                int childLeft = child.getLeft();

                // Remember left and right edges of where views have been placed
                _rightMost = Math.max(_rightMost, childLeft
                        + child.getMeasuredWidth());
                _leftMost = Math.min(_leftMost, childLeft);

                // Position the view
                setUpChild(child, offset, x, fromLeft);

                return child;
            }
        }

        // Nothing found in the recycler -- ask the adapter for a view
        child = _adapter.getView(position, null, this);

        // Position the view
        setUpChild(child, offset, x, fromLeft);

        return child;
    }

    /**
     * Helper for makeAndAddView to set the position of a view and fill out its
     * layout parameters.
     *
     * @param child The view to position
     * @param offset Offset from the selected position
     * @param x X-coordintate indicating where this view should be placed. This
     *        will either be the left or right edge of the view, depending on
     *        the fromLeft parameter
     * @param fromLeft Are we posiitoning views based on the left edge? (i.e.,
     *        building from left to right)?
     */
    private void setUpChild(View child, int offset, int x, boolean fromLeft) {

        // Respect layout params that are already in the view. Otherwise
        // make some up...
        LayoutParams lp = child.getLayoutParams();
        if (lp == null) {
            lp = generateDefaultLayoutParams();
        }

        addViewInLayout(child, fromLeft ? -1 : 0, lp);

        child.setSelected(offset == 0);

        // Get measure specs
        int childHeightSpec = ViewGroup.getChildMeasureSpec(_heightMeasureSpec,
                0, lp.height);
        int childWidthSpec = ViewGroup.getChildMeasureSpec(_widthMeasureSpec,
                0, lp.width);

        // Measure child
        child.measure(childWidthSpec, childHeightSpec);

        int childLeft;
        int childRight;

        // Position vertically based on gravity setting
        int childTop = calculateTop(child, true);
        int childBottom = childTop + child.getMeasuredHeight();

        int width = child.getMeasuredWidth();
        _fillOffset = 100;//width;

        if (fromLeft) {
            childLeft = x;
            childRight = childLeft + width;
        } else {
            childLeft = x - width;
            childRight = x;
        }

        child.layout(childLeft, childTop, childRight, childBottom);
    }

    /**
     * Figure out vertical placement based on mGravity
     *
     * @param child Child to place
     * @return Where the top of the child should be
     */
    private int calculateTop(View child, boolean duringLayout) {
        int myHeight = duringLayout ? getMeasuredHeight() : getHeight();
        int childHeight = duringLayout ? child.getMeasuredHeight() : child.getHeight();

        int childTop = 0;

        switch (_gravity) {
            case Gravity.TOP:
                childTop = 0;
                break;
            case Gravity.CENTER_VERTICAL:
                int availableSpace = myHeight - childHeight;
                childTop = availableSpace / 2;
                break;
            case Gravity.BOTTOM:
                childTop = myHeight - childHeight;
                break;
        }
        return childTop;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        View childView = null;
        float originalX = event.getX();
        float originalRawY = event.getRawY();

        float xPrev = event.getX() - _centerCoordX;
        float yPrev = event.getY() - _centerCoordY;
        float cosTheta = (float) Math.cos(-_angle * Math.PI / 180.0f);
        float sinTheta = (float) Math.sin(-_angle * Math.PI / 180.0f);
        float x = xPrev * cosTheta + yPrev * sinTheta;
        float y = -(xPrev * sinTheta - yPrev * cosTheta);
        x += _centerCoordX;
        y += _centerCoordY;
        event.setLocation(x, y);

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if(_itemRearrangeEnabled && _onItemRearranging && _dragImageView != null)
                {
                    _windowParams.x = this.getLeft() + (int)originalX - _dragImageView.getWidth()/2;
                    _windowParams.y = this.getTop() + (int)originalRawY - _dragImageView.getHeight()/2;

                    _windowManager.updateViewLayout(_dragImageView, _windowParams);

                    if(originalX > this.getWidth() - _dragImageView.getWidth()/2 && _selectedPosition < _itemCount-1)
                    {
                        if(_flingRunnable.isEndFling)
                        {
                            if((_angle + 90)%360 < 180) moveNext();
                            else movePrevious();
                        }
                        break;
                    }
                    else if(originalX < _dragImageView.getWidth()/2 && _selectedPosition > 0)
                    {
                        if(_flingRunnable.isEndFling)
                        {
                            if((_angle + 90)%360 < 180) movePrevious();
                            else moveNext();
                        }
                        break;
                    }

                    int targetItemPosition = this.findChildViewFromPos((int)event.getX(), (int)event.getY());

                    if(targetItemPosition > -1)
                    {
                        targetItemPosition = (targetItemPosition+_firstPosition)%_itemCount;
                        if(targetItemPosition != _previousTargetItemPosition)
                        {
                            _adapter.itemMoved(_currentItemPosition, targetItemPosition);
                            _currentItemPosition = targetItemPosition;
                        }
                        _previousTargetItemPosition = targetItemPosition;
                    }
                    _adapter.notifyDataSetChanged();

                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if(_itemRearrangeEnabled && _onItemRearranging)
                {
                    childView = _movingItemView;
                    if(childView != null)
                    {
                        childView.setVisibility(VISIBLE);
                    }

                    if(_dragImageView != null)
                    {
                        _windowManager.removeView(_dragImageView);
                        _dragImageView.setImageDrawable(null);
                        _dragImageView = null;
                    }
                    if(_dragBitmap != null)
                    {
                        _dragBitmap.recycle();
                        _dragBitmap = null;
                    }

                    if(_adapter!=null) _adapter.notifyDataSetChanged();
                    if(_itemRearrangedListener !=null && _originalItemPosition!= _previousTargetItemPosition)
                    {
                        if(_overScrollBounceEnabled) _itemRearrangedListener.itemMoved(_originalItemPosition-1, _previousTargetItemPosition-1);
                        else _itemRearrangedListener.itemMoved(_originalItemPosition, _previousTargetItemPosition);
                    }
                    _onItemRearranging = false;
                }


                break;
        }


        // Give everything to the gesture detector
        boolean retValue = _gestureDetector.onTouchEvent(event);

        int action = event.getAction();
        if (action == MotionEvent.ACTION_UP) {
            // Helper method for lifted finger
            onUp();
        } else if (action == MotionEvent.ACTION_CANCEL) {
            onCancel();
        }

        return retValue;
    }

    /**
     * Returns the touched view.
     * @return A view which is recently touched.
     */
    public View getDownTouchView() {
        return _downTouchView;
    }

    /**
     * If it enabled, selected item will be positioned to center.
     * @param enabled value to set enable/disable.
     */
    public void setAutoScrollToSelectedChildEnabled(boolean enabled) {
        _autoScrollToSelectedChild = enabled;
    }

    /**
     * Returns AutoScrollToSelectedChild mode is selected or not.
     * @return AutoScrollToSelectedChild value.
     */
    public boolean isAutoScrollToSelectedChildEnabled() {
        return _autoScrollToSelectedChild;
    }

    public boolean onSingleTapUp(MotionEvent e) {
        if(getCenterOfView(_selectedChild)!=getCenterOfCarousel())
        {
            return false;
        }
        if (_downTouchPosition >= 0) {
            // An item tap should make it selected, so scroll to this child.
            if(_autoScrollToSelectedChild)
                scrollToChild((_downTouchPosition - _firstPosition + _itemCount)%_itemCount);

            // Also pass the click so the client knows, if it wants to.
            if (_shouldCallbackOnUnselectedItemClick || _downTouchPosition == _selectedPosition) {
                if(_overScrollBounceEnabled)
                    performItemClick(_downTouchView, _adapter.getIndexArray()[_downTouchPosition-1], _adapter.getItemId(_adapter.getIndexArray()[_downTouchPosition-1]));
                else
                    performItemClick(_downTouchView, _adapter.getIndexArray()[_downTouchPosition], _adapter.getItemId(_adapter.getIndexArray()[_downTouchPosition]));

            }

            return true;
        }

        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        velocityX *= _velocityMultiplier;
        velocityY *= _velocityMultiplier;

        if (!_shouldCallbackDuringFling) {
            // We want to suppress selection changes

            // Remove any future code to set mSuppressSelectionChanged = false
            removeCallbacks(_disableSuppressSelectionChangedRunnable);

            // This will get reset once we scroll into slots
            if (!_suppressSelectionChanged) _suppressSelectionChanged = true;
        }

        // Fling the carousel!
        _flingRunnable.startUsingVelocity((int) -velocityX);

        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        if(_onItemRearranging) return true;

        _lastUpdateEvent = SystemClock.uptimeMillis();

        this.getParent().requestDisallowInterceptTouchEvent(true);

        // As the user scrolls, we want to callback selection changes so related-
        // info on the screen is up-to-date with the carousel's selection
        if (!_shouldCallbackDuringFling) {
            if (_isFirstScroll) {
				/*
				 * We're not notifying the client of selection changes during
				 * the fling, and this scroll could possibly be a fling. Don't
				 * do selection changes until we're sure it is not a fling.
				 */
                if (!_suppressSelectionChanged) _suppressSelectionChanged = true;
                postDelayed(_disableSuppressSelectionChangedRunnable, SCROLL_TO_FLING_UNCERTAINTY_TIMEOUT);
            }
        } else {
            if (_suppressSelectionChanged) _suppressSelectionChanged = false;
        }

        // Track the motion
        trackMotionScroll(-1 * (int) distanceX);

        _isFirstScroll = false;
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        // Kill any existing fling/scroll
        _flingRunnable.stop(false);

        // Get the item's view that was touched
        _downTouchPosition = pointToPosition((int) e.getX(), (int) e.getY());

        if (_downTouchPosition >= 0) {
            _downTouchView = getChildAt((_downTouchPosition - _firstPosition + _itemCount)%_itemCount);
            if(_downTouchView != null)
                _downTouchView.setPressed(true);
        }

        // Reset the multiple-scroll tracking state
        _isFirstScroll = true;

        // Must return true to get matching events for this down event.
        return true;
    }

    /**
     * Called when a touch event's action is MotionEvent.ACTION_UP.
     */
    private void onUp() {
        if (_flingRunnable._scroller.isFinished()) {
            scrollIntoSlots();
        }
    }

    /**
     * Called when a touch event's action is MotionEvent.ACTION_CANCEL.
     */
    private void onCancel() {
        onUp();
    }

    @Override
    public void onLongPress(MotionEvent event) {
        if (_downTouchPosition < 0) {
            return;
        }
        if(!_enabled) return;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(_itemRearrangeEnabled)
                {
                    int targetItemPosition = this.findChildViewFromPos((int)event.getX(), (int)event.getY());
                    if(targetItemPosition < 0) break;

                    _movingItemView = this.getChildAt(targetItemPosition);
                    targetItemPosition = (targetItemPosition + _firstPosition)%_itemCount;
                    _originalItemPosition = targetItemPosition;
                    _currentItemPosition = targetItemPosition;
                    _previousTargetItemPosition = targetItemPosition;

                    if(_movingItemView != null)
                    {
                        int x = this.getLeft() + (int)event.getX() - _movingItemView.getWidth()/2;
                        int y = this.getTop() + (int)event.getRawY() - _movingItemView.getHeight()/2;
                        addWindow(x, y, _movingItemView);

                        _onItemRearranging = true;
                    }
                }
                break;
        }

        performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
        long id = getItemIdAtPosition(_downTouchPosition);
        dispatchLongPress(_downTouchView, _downTouchPosition, id);
    }

    public void onShowPress(MotionEvent e) {
    }

    private void dispatchPress(View child) {

        if (child != null) {
            child.setPressed(true);
        }

        setPressed(true);
    }

    private void dispatchUnpress() {

        for (int i = getChildCount() - 1; i >= 0; i--) {
            getChildAt(i).setPressed(false);
        }

        setPressed(false);
    }

    public void dispatchSetSelected(boolean selected) {
    }

    protected void dispatchSetPressed(boolean pressed) {
        if (_selectedChild != null) {
            _selectedChild.setPressed(pressed);
        }
    }

    protected ContextMenuInfo getContextMenuInfo() {
        return _contextMenuInfo;
    }

    @Override
    public boolean showContextMenuForChild(View originalView) {
        final int longPressPosition = getPositionForView(originalView);
        if (longPressPosition < 0) {
            return false;
        }

        final long longPressId = _adapter.getItemId(longPressPosition);
        return dispatchLongPress(originalView, longPressPosition, longPressId);
    }

    @Override
    public boolean showContextMenu() {
        if (isPressed() && _selectedPosition >= 0) {
            int index = _selectedPosition - _firstPosition;
            View v = getChildAt(index);
            return dispatchLongPress(v, _selectedPosition, this.getItemIdAtPosition(index));
        }

        return false;
    }

    private boolean dispatchLongPress(View view, int position, long id) {
        boolean handled = false;

        if (this.getOnItemLongClickListener() != null) {
            handled = this.getOnItemLongClickListener().onItemLongClick(this, _downTouchView,
                    _downTouchPosition, id);
        }

        if (!handled) {
            _contextMenuInfo = new AdapterContextMenuInfo(view, position, id);
            handled = super.showContextMenuForChild(this);
        }

        if (handled) {
            performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
        }

        return handled;
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        return event.dispatch(this);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {

            case KeyEvent.KEYCODE_DPAD_LEFT:
                if(!_enabled) return true;
                if (movePrevious()) {
                    playSoundEffect(SoundEffectConstants.NAVIGATION_LEFT);
                }
                return true;

            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if(!_enabled) return true;
                if (moveNext()) {
                    playSoundEffect(SoundEffectConstants.NAVIGATION_RIGHT);
                }
                return true;

            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                _receivedInvokeKeyDown = true;
                // fallthrough to default handling
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER: {

                if (_receivedInvokeKeyDown) {
                    if (_itemCount > 0) {

                        dispatchPress(_selectedChild);
                        postDelayed(new Runnable() {
                            public void run() {
                                dispatchUnpress();
                            }
                        }, ViewConfiguration.getPressedStateDuration());

                        int selectedIndex = _selectedPosition - _firstPosition;
                        performItemClick(getChildAt(selectedIndex), _selectedPosition, _adapter
                                .getItemId(_selectedPosition));
                    }
                }

                // Clear the flag
                _receivedInvokeKeyDown = false;

                return true;
            }
        }

        return super.onKeyUp(keyCode, event);
    }

    /**
     * Move to previous item.
     * @return if the position is end of carousel, it returns false. Otherwise returns true.
     */
    public boolean movePrevious() {
        if (_itemCount > 0 && _selectedPosition > 0) {
            scrollToChild((_selectedPosition - _firstPosition - 1 + _itemCount)%_itemCount);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Move to next item.
     * @return if the position is end of carousel, it returns false. Otherwise returns true.
     */
    public boolean moveNext() {
        if (_itemCount > 0 && _selectedPosition < _itemCount - 1) {
            scrollToChild((_selectedPosition - _firstPosition + 1 + _itemCount)%_itemCount);
            return true;
        } else {
            return false;
        }
    }

    private boolean scrollToChild(int childPosition) {
        View child = getChildAt(childPosition);

        if (child != null) {
            int distance = getCenterOfCarousel() - getCenterOfView(child);
            _flingRunnable.startUsingDistance(distance);
            return true;
        }

        return false;
    }

    private void setSelectedPositionInt(int position) {
        _selectedPosition = position;

        if(!_doNotCallBack && _itemSelectionUpdatedListener != null && _selectedPosition > INVALID_POSITION)
        {
            if(_overScrollBounceEnabled && _selectedPosition > 0) _itemSelectionUpdatedListener.onItemSelectionUpdated(this, getSelectedView(), _adapter.getIndexArray()[_selectedPosition-1]);
            else _itemSelectionUpdatedListener.onItemSelectionUpdated(this, getSelectedView(), _adapter.getIndexArray()[_selectedPosition]);
            if(!_flingRunnable.isEndFling) _lastUpdateEvent = SystemClock.uptimeMillis();
        }

        // Updates any metadata we keep about the selected item.
        updateSelectedItemMetadata();
    }

    private void updateSelectedItemMetadata() {

        View oldSelectedChild = _selectedChild;

        View child = _selectedChild = getChildAt((_selectedPosition - _firstPosition+_itemCount)%_itemCount);
        if (child == null) {
            return;
        }

        child.setSelected(true);
        child.setFocusable(true);

        if (hasFocus()) {
            child.requestFocus();
        }

        // We unfocus the old child down here so the above hasFocus check
        // returns true
        if (oldSelectedChild != null) {

            // Make sure its drawable state doesn't contain 'selected'
            oldSelectedChild.setSelected(false);

            // Make sure it is not focusable anymore, since otherwise arrow keys
            // can make this one be focused
            oldSelectedChild.setFocusable(false);
        }

        if(_selectedPosition == _itemCount)
        {
            _selectedPosition = 0;
        }

    }

    /**
     * Sets Carousel Layout Gravity.
     * @param gravity the value to set.
     */
    public void setGravity(int gravity) {
        if (_gravity != gravity) {
            _gravity = gravity;
            requestLayout();
        }
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        if (_type == Carousel.TYPE_COVERFLOW || _type == Carousel.TYPE_ROTARY || _type == Carousel.TYPE_RING || _type == Carousel.TYPE_CYLINDER) {
            int selectedIndex = (_selectedPosition - _firstPosition + _itemCount) % _itemCount;

            // Just to be safe
            if (selectedIndex < 0)
            {
                return i;
            }

            if(i == 0)
                _lastPosition = 0;

            if (i == childCount - 1) {
                return selectedIndex;
            } else if (i >= selectedIndex) {
                _lastPosition++;
                return childCount - _lastPosition;
            } else {
                return i;
            }
        } else {
            return childCount - 1 - i;
        }
    }

    protected void onFocusChanged(boolean gainFocus, int direction,
                                  Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);

        if (gainFocus && _selectedChild != null) {
            _selectedChild.requestFocus(direction);
        }
    }

    private class SelectionNotifier extends Handler implements Runnable {
        public void run() {
            if (_dataChanged) {
                // Data has changed between when this SelectionNotifier
                // was posted and now. We need to wait until the AdapterView
                // has been synched to the new data.
                post(this);
            } else {
                fireOnSelected();
            }
        }
    }

    private void fireOnSelected() {
        if (getOnItemSelectedListener() == null)
            return;

        int selection = _selectedPosition;
        if (selection >= 0) {
            View v = getSelectedView();
            getOnItemSelectedListener().onItemSelected(this, v, selection,
                    getAdapter().getItemId(selection));
        } else {
            getOnItemSelectedListener().onNothingSelected(this);
        }
    }

    private void resetList() {
        _dataChanged = false;

        removeAllViewsInLayout();

        setSelectedPositionInt(INVALID_POSITION);
        invalidate();
    }

    private void recycleAllViews() {
        int childCount = getChildCount();
        final RecycleBin recycleBin = _recycler;

        for (int i = 0; i < childCount; i++) {
            View v = getChildAt(i);
            int index = (_firstPosition + i+ _itemCount)%_itemCount;
            recycleBin.put(index, v);
        }
    }

    /**
     * Returns current Carousel layout mode.
     * @return Carousel layout mode.
     */
    public int getType() {
        return _type;
    }

    /**
     * Change the Carousel mode.
     * @param type The value to set.
     */
    public void setType(int type) {
        this._type = type;
        this._onItemRearranging = false;

        if(_dragImageView != null)
        {
            _windowManager.removeView(_dragImageView);
            _dragImageView.setImageDrawable(null);
            _dragImageView = null;
        }
        if(_dragBitmap != null)
        {
            _dragBitmap.recycle();
            _dragBitmap = null;
        }

        this.requestLayout();
    }

    /**
     * Returns current Carousel scroll angle.
     * @return Carousel scroll plane angle.
     */
    public int getAngle() {
        return _angle;
    }

    /**
     * Change the scroll angle.
     * @param angle The value to set.
     */
    public void setAngle(int angle) {
        this._angle = angle;
    }

    /**
     * Returns current over scroll setting value.
     * @return Current over scroll setting value.
     */
    public boolean getOverScrollBounceEnabled() {
        return _overScrollBounceEnabled;
    }

    /**
     * Change the scroll bounce value. If it true, it bounces the end of the Carousel.
     * @param enabled The value to set.
     */
    public void setOverScrollBounceEnabled(boolean enabled) {
        this._overScrollBounceEnabled = enabled;
    }

    /**
     * Returns whether current scroll mode is infinite or not.
     * @return Current infinite scroll mode.
     */
    public boolean getInfiniteScrollEnabled() {
        return _infiniteScrollEnabled;
    }

    /**
     * Change the infinite scroll mode. If it true, it infinitely fling the entire items.
     * @param enabled The value to set.
     */
    public void setInfiniteScrollEnabled(boolean enabled) {
        this._infiniteScrollEnabled = enabled;
    }

    /**
     * Returns current custom fling animation.
     * @return Current custom fling animation.
     */
    public Carousel.ChildStaticTransformationListener getTransformationListener() {
        return _transformationListener;
    }

    /**
     * Change the custom fling animation. When the Carousel type is set TYPE_CUSTOM, this animation will be used.
     * @param transformationListener New custom Animation.
     */
    public void setTransformationListener(
            Carousel.ChildStaticTransformationListener transformationListener) {
        this._transformationListener = transformationListener;
    }


    public Carousel.ItemRearrangedListener getItemRearrangedListener() {
        return _itemRearrangedListener;
    }

    public void setItemRearrangedListener(Carousel.ItemRearrangedListener itemRearrangedlistener) {
        this._itemRearrangedListener = itemRearrangedlistener;
    }

    /**
     * Returns current itemSelectionUpdate listener. This event is occurred when selected item is changed.
     * @return Currnet itemSelectionUpdate listener.
     */
    public Carousel.OnItemSelectionUpdatedListener getOnItemSelectionUpdatedListener() {
        return _itemSelectionUpdatedListener;
    }

    /**
     * Change the itemSelectionUpdate listener. This event is occurred when selected item is changed.
     * @param listener New listener to set.
     */
    public void setOnItemSelectionUpdatedListener(Carousel.OnItemSelectionUpdatedListener listener) {
        this._itemSelectionUpdatedListener = listener;
    }

    public boolean getItemRearrangeEnabled() {
        return _itemRearrangeEnabled;
    }

    public void setItemRearrangeEnabled(boolean enabled) {
        this._itemRearrangeEnabled = enabled;
    }

    public boolean getOnItemRearranging()
    {
        return _onItemRearranging;
    }

    public boolean getEnabled() {
        return _enabled;
    }

    public void setEnabled(boolean enabled) {
        this._enabled = enabled;
    }

    /**
     * Returns current scroll speed.
     * @return Current scroll speed.
     */
    public float getScrollSpeed() {
        return _velocityMultiplier;
    }

    /**
     * Change the current scroll speed.
     * @param speed The value to set.
     */
    public void setScrollSpeed(float speed) {
        this._velocityMultiplier = speed;
    }

    /**
     * If childIndex is valid position, scroll flies to input view position.
     * @param childIndex The position that scroll move to.
     * @return True if childIndex is valid and fling successful.
     */
    public boolean scrollToIndex(int childIndex)
    {
        if(childIndex>=0 && childIndex < _itemCount && _selectedChild!=null)
        {
            _doNotCallBack = true;
            if(_infiniteScrollEnabled)
            {
                int amount = _selectedPosition - childIndex;
                if(amount>_itemCount/2) amount-=_itemCount;
                else if(amount<-_itemCount/2) amount+=_itemCount;

                int centerCorrection = getCenterOfView(_selectedChild) - getCenterOfCarousel();
                _flingRunnable.startUsingDistance(amount*(_selectedChild.getWidth()+_spacing)-centerCorrection);
            }
            else if(_overScrollBounceEnabled)
            {
                int centerCorrection = getCenterOfView(_selectedChild) - getCenterOfCarousel();
                _flingRunnable.startUsingDistance((_selectedPosition - childIndex -1)*(_selectedChild.getWidth()+_spacing)-centerCorrection);
            }
            else
            {
                int centerCorrection = getCenterOfView(_selectedChild) - getCenterOfCarousel();
                _flingRunnable.startUsingDistance((_selectedPosition - childIndex)*(_selectedChild.getWidth()+_spacing)-centerCorrection);
            }
            return true;
        }
        return false;
    }

    /**
     * Change the center position of the Carousel.
     * @param position The index which will be positioned to center.
     */
    public void setCenterPosition(int position) {
        if(position>=0 && position<_itemCount)
        {
            _selectedPosition = position;
            requestLayout();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    private int pointToPosition(int x, int y) {
        Rect frame = _touchFrame;
        if (frame == null) {
            _touchFrame = new Rect();
            frame = _touchFrame;
        }

        final int count = getChildCount();
        for (int i = count - 1; i >= 0; i--) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.VISIBLE) {
                child.getHitRect(frame);
                if (frame.contains(x, y)) {
                    if(_infiniteScrollEnabled) return (_firstPosition+i)%_itemCount;
                    else return _firstPosition + i;
                }
            }
        }
        return INVALID_POSITION;
    }

    private int findChildViewFromPos(int x, int y) {
        View child = null;
        for(int i=0; i<this.getChildCount(); i++)
        {
            child = this.getChildAt(i);
            boolean horizontalCheck = (x >= child.getLeft() && x < child.getLeft() + child.getWidth());
            boolean verticalCheck = (y >= child.getTop() && y < child.getTop() + child.getHeight());
            if(horizontalCheck && verticalCheck)
                return i;
        }
        return -1;
    }

    private void addWindow(int x, int y, View childView) {
        _windowParams = new WindowManager.LayoutParams();
        _windowParams.gravity = Gravity.LEFT | Gravity.TOP;
        _windowParams.x = x;
        _windowParams.y = y;

        _windowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        _windowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        _windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        _windowParams.format = PixelFormat.TRANSLUCENT;
        _windowParams.windowAnimations = 0;

        childView.setDrawingCacheEnabled(true);
        _dragBitmap = Bitmap.createBitmap(childView.getDrawingCache());
        ImageView imageView = new ImageView(getContext());
        imageView.setImageBitmap(_dragBitmap);

        _dragImageView = imageView;
        _dragImageView.setAlpha(0.5f);

        _windowManager = (WindowManager)getContext().getSystemService("window");
        _windowManager.addView(_dragImageView, _windowParams);
    }


    @Override
    public View getSelectedView() {
        if (_itemCount > 0 && _selectedPosition >= 0) {
            return getChildAt((_selectedPosition - _firstPosition + _itemCount)%_itemCount);
        } else {
            return null;
        }
    }

    @Override
    public int getSelectedItemPosition() {
        return _selectedPosition;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        _centerCoordX = (getWidth() - getPaddingLeft() - getPaddingRight()) / 2
                + getPaddingLeft();
        _centerCoordY = (getHeight() - getPaddingTop() - getPaddingBottom()) / 2 + getPaddingTop();
    }

    @Override
    public Adapter getAdapter() {
        return _adapter;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if(_adapter != null) {
            _adapter.unregisterDataSetObserver(_dataSetObserver);
            resetList();
        }

        _adapter = new CarouselAdapter(adapter);
        //super.setAdapter(_adapter);
        if (_adapter != null) {
            _oldItemCount = _itemCount;
            _itemCount = _adapter.getCount();
            // checkFocus();

            _dataSetObserver = new AdapterDataSetObserver();
            _adapter.registerDataSetObserver(_dataSetObserver);

            int position = _itemCount > 0 ? 0 : INVALID_POSITION;

            setSelectedPositionInt(position);
            // setNextSelectedPositionInt(position);

            if (_itemCount == 0) {
                // Nothing selected
                // checkSelectionChanged();
            }

        } else {
            // checkFocus();
            resetList();
            // Nothing selected
            // checkSelectionChanged();
        }

//	    int position = _itemCount > 0 ? 0 : INVALID_POSITION;
//	    setSelectedPositionInt(position);

        requestLayout();

        if (_overScrollBounceEnabled)
        {
            setSelection(_selectedPosition + 1);
        }
    }

    /**
     * Free the data of carousel and stop a move.
     */
    public void destroy() {
        _flingRunnable.stop(false);
        _adapter = null;
    }

    private class CarouselAdapter extends BaseAdapter {
        private Adapter _spinnerAdapter;
        private LayoutInflater _layoutInflater;
        private View _dummyViewBegin;
        private View _dummyViewEnd;
        private int[] _index;

        public CarouselAdapter(Adapter adapter) {
            this._spinnerAdapter = adapter;
            initIndexArray();

            this._spinnerAdapter.registerDataSetObserver(new DataSetObserver() {
                @Override
                public void onChanged() {
                    initIndexArray();
                    notifyDataSetChanged();
                }
            });
        }

        public void initIndexArray() {
            _itemCount = getCount();
            _index = new int[_spinnerAdapter.getCount()];
            for(int i=0; i<_spinnerAdapter.getCount(); i++)
            {
                _index[i] = i;
            }
        }

        public void itemMoved(int from, int to) {
            if(from == to) return;

            if(_overScrollBounceEnabled)
            {
                from--; to--;
            }

            int temp;
            if(from < to)
            {
                temp = _index[from];
                System.arraycopy(_index, from + 1, _index, from, to - from);
                _index[to] = temp;
            }
            else if(from > to)
            {
                temp = _index[from];
                System.arraycopy(_index, to, _index, to + 1, from - to);
                _index[to] = temp;
            }

            this.notifyDataSetChanged();
        }

        public int[] getIndexArray() {
            return _index;
        }

        @Override
        public int getCount() {
            if(_overScrollBounceEnabled)
                return _spinnerAdapter.getCount() + 2;
            else
                return _spinnerAdapter.getCount();
        }

        @Override
        public Object getItem(int position) {
            return _spinnerAdapter.getItem(position);
        }

        @Override
        public long getItemId(int position) {
            return _spinnerAdapter.getItemId(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (_layoutInflater == null) {
                _layoutInflater = (LayoutInflater) getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }

            if(_overScrollBounceEnabled)
            {
                if (position == 0) {
                    if (convertView == null || _dummyViewBegin == null) {
                        _dummyViewBegin = new View(getContext());
                        _dummyViewBegin.setLayoutParams(new Carousel.LayoutParams(0,
                                Carousel.LayoutParams.FILL_PARENT));
                        convertView = _dummyViewBegin;
                    }
                    return convertView;
                } else if (position == getCount() - 1) {
                    if (convertView == null || _dummyViewEnd == null) {
                        _dummyViewEnd = new View(getContext());
                        _dummyViewEnd.setLayoutParams(new Carousel.LayoutParams(0,
                                Carousel.LayoutParams.FILL_PARENT));
                        convertView = _dummyViewEnd;
                    }
                    return convertView;
                }
                else
                {
                    View v = _spinnerAdapter.getView(_index[position-1], convertView, parent);
                    v.setVisibility(VISIBLE);
                    return v;
                }
            }
            else
            {
                View v = _spinnerAdapter.getView(_index[position], convertView, parent);
                v.setVisibility(VISIBLE);
                return v;
            }
        }
    }

    private class FlingRunnable implements Runnable {
        /**
         * Tracks the decay of a fling scroll
         */
        private DAScroller _scroller;

        /**
         * X value reported by mScroller on the previous fling
         */
        private int _lastFlingX;
        public boolean isEndFling = true;

        public FlingRunnable() {
            _scroller = new DAScroller(getContext());
        }

        private void startCommon() {
            // Remove any pending flings
            removeCallbacks(this);
        }

        public void startUsingVelocity(int initialVelocity) {
            if (initialVelocity == 0 || _selectedChild == null) return;

            isEndFling = false;
            startCommon();

            int totalDistance = (int) ((initialVelocity * initialVelocity) / (2 * _scroller.getDeceleration()));
            int width = _selectedChild.getWidth()+_spacing;
            totalDistance = totalDistance/width*width;
            if(totalDistance==0) totalDistance = width;
            int centerCorrection = getCenterOfView(_selectedChild) - getCenterOfCarousel();

            if(initialVelocity < 0)
            {
                totalDistance -= centerCorrection;
            }
            else
            {
                totalDistance += centerCorrection;
            }

            float newVelocity = initialVelocity<0? -(float)Math.sqrt(totalDistance * 2 *_scroller.getDeceleration()) : (float)Math.sqrt(totalDistance * 2 *_scroller.getDeceleration());

            int initialX = initialVelocity<0? Integer.MAX_VALUE:0;
            _lastFlingX = initialX;
            _scroller.fling(initialX, 0, newVelocity, 0, 0, Integer.MAX_VALUE, 0, Integer.MAX_VALUE);
            post(this);
        }

        public void startUsingDistance(int distance) {
            if (distance == 0) return;

            isEndFling = false;
            startCommon();
            _lastFlingX = 0;
            _scroller.startScroll(0, 0, -distance, 0, _animationDuration);
            post(this);
        }

        public void stop(boolean scrollIntoSlots) {
            removeCallbacks(this);
            endFling(scrollIntoSlots);
        }

        private void endFling(boolean scrollIntoSlots) {
			/*
			 * Force the scroller's status to finished (without setting its
			 * position to the end)
			 */
            _scroller.forceFinished(true);

            if (scrollIntoSlots) scrollIntoSlots();

            _doNotCallBack = false;
            isEndFling = true;
        }

        public void run() {

            if (!_infiniteScrollEnabled && _itemCount == 0) {
                endFling(true);
                return;
            }

            _shouldStopFling = false;

            final DAScroller scroller = _scroller;
            boolean more = scroller.computeScrollOffset();
            final int x = scroller.getCurrX();

            // Flip sign to convert finger direction to list items direction
            // (e.g. finger moving down means list is moving towards the top)
            int delta = _lastFlingX - x;

            // Pretend that each frame of a fling scroll is a touch scroll
            if (delta > 0) {
                // Moving towards the left. Use first view as mDownTouchPosition
                _downTouchPosition = _firstPosition;

                // Don't fling more than 1 screen
                delta = Math.min(getWidth() - getPaddingLeft() - getPaddingRight() - 1, delta);
            } else {
                // Moving towards the right. Use last view as mDownTouchPosition
                int offsetToLast = getChildCount() - 1;
                _downTouchPosition = (_firstPosition + offsetToLast) % _itemCount;

                // Don't fling more than 1 screen
                delta = Math.max(-(getWidth() - getPaddingRight() - getPaddingLeft() - 1), delta);
            }

            trackMotionScroll(delta);

            if (more && !_shouldStopFling) {
                _lastFlingX = x;
                post(this);
            } else {
                endFling(true);
            }
        }

    }

    private class RecycleBin {
        private final SparseArray<View> _scrapHeap = new SparseArray<View>();

        public void put(int position, View v) {
            _scrapHeap.put(position, v);
        }

        View get(int position) {
            // System.out.print("Looking for " + position);
            View result = _scrapHeap.get(position);
            if (result != null) {
                // System.out.println(" HIT");
                _scrapHeap.delete(position);
            } else {
                // System.out.println(" MISS");
            }
            return result;
        }

        void clear() {
            final SparseArray<View> scrapHeap = _scrapHeap;
            final int count = scrapHeap.size();
            for (int i = 0; i < count; i++) {
                final View view = scrapHeap.valueAt(i);
                if (view != null) {
                    removeDetachedView(view, true);
                }
            }
            scrapHeap.clear();
        }
    }

    private class AdapterDataSetObserver extends DataSetObserver {

        private Parcelable _instanceState = null;

        @Override
        public void onChanged() {

            _dataChanged = true;
            _oldItemCount = _itemCount;
            _itemCount = getAdapter().getCount();

            //int position = _itemCount > 0 ? 0 : INVALID_POSITION;
            //setSelectedPositionInt(position);

            // Detect the case where a cursor that was previously invalidated
            // has
            // been repopulated with new data.
            if (CarouselBehavior.this.getAdapter().hasStableIds()
                    && _instanceState != null && _oldItemCount == 0
                    && _itemCount > 0) {
                CarouselBehavior.this.onRestoreInstanceState(_instanceState);
                _instanceState = null;
            } else {
                // rememberSyncState();
            }
            // checkFocus();
            requestLayout();
        }

        @Override
        public void onInvalidated() {
            _dataChanged = true;

            if (CarouselBehavior.this.getAdapter().hasStableIds()) {
                // Remember the current state for the case where our hosting
                // activity is being
                // stopped and later restarted
                _instanceState = CarouselBehavior.this.onSaveInstanceState();
            }

            // Data is invalid so we should reset our state
            _oldItemCount = _itemCount;
            _itemCount = 0;
            _selectedPosition = INVALID_POSITION;
            // mSelectedRowId = INVALID_ROW_ID;
            // mNextSelectedPosition = INVALID_POSITION;
            // mNextSelectedRowId = INVALID_ROW_ID;
            // mNeedSync = false;
            // checkSelectionChanged();

            // checkFocus();
            requestLayout();
        }

        public void clearSavedState() {
            _instanceState = null;
        }
    }
}
