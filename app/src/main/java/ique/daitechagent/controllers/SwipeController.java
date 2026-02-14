package ique.daitechagent.controllers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import androidx.core.internal.view.SupportMenu;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import androidx.recyclerview.widget.ItemTouchHelper.Callback;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

import ique.daitechagent.R;

import static androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE;

enum ButtonsState {
  GONE,
  LEFT_VISIBLE,
  RIGHT_VISIBLE
}

public class SwipeController extends Callback {
  private static final float buttonWidth = 300.0f;
  /* access modifiers changed from: private */
  public RectF buttonInstance = null;
  /* access modifiers changed from: private */
  public ButtonsState buttonShowedState = ButtonsState.GONE;
  /* access modifiers changed from: private */
  public SwipeControllerActions buttonsActions = null;
  private Context context;
  /* access modifiers changed from: private */
  public ViewHolder currentItemViewHolder = null;
  private int moduleFocus = 1;
  /* access modifiers changed from: private */
  public boolean swipeBack = false;

  public SwipeController(Context context2, SwipeControllerActions buttonsActions2) {
    this.buttonsActions = buttonsActions2;
    this.context = context2;
  }

  @Override
  public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
    return makeMovementFlags(0, 12);
  }

  @Override
  public boolean onMove(RecyclerView recyclerView, ViewHolder viewHolder, ViewHolder target) {
    return false;
  }

  @Override
  public void onSwiped(ViewHolder viewHolder, int direction) {
  }

  @Override
  public int convertToAbsoluteDirection(int flags, int layoutDirection) {
    if (!this.swipeBack) {
      return super.convertToAbsoluteDirection(flags, layoutDirection);
    }
    this.swipeBack = this.buttonShowedState != ButtonsState.GONE;
    return 0;
  }

  @Override
  public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
    if (actionState == ACTION_STATE_SWIPE) {
      if (buttonShowedState != ButtonsState.GONE) {
        if (buttonShowedState == ButtonsState.LEFT_VISIBLE) dX = Math.max(dX, buttonWidth);
        if (buttonShowedState == ButtonsState.RIGHT_VISIBLE) dX = Math.min(dX, -buttonWidth);
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
      }
      else {
        setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
      }
    }

    if (buttonShowedState == ButtonsState.GONE) {
      super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
    currentItemViewHolder = viewHolder;
  }

  private void setTouchListener(final Canvas c, final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder, final float dX, final float dY, final int actionState, final boolean isCurrentlyActive) {
    recyclerView.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        swipeBack = event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP;
        if (swipeBack) {
          if (dX < -buttonWidth) buttonShowedState = ButtonsState.RIGHT_VISIBLE;
          else if (dX > buttonWidth) buttonShowedState  = ButtonsState.LEFT_VISIBLE;

          if (buttonShowedState != ButtonsState.GONE) {
            setTouchDownListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            setItemsClickable(recyclerView, false);
          }
        }
        return false;
      }
    });
  }

  private void setTouchDownListener(final Canvas c, final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder, final float dX, final float dY, final int actionState, final boolean isCurrentlyActive) {
    recyclerView.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
          setTouchUpListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
        return false;
      }
    });
  }

  private void setTouchUpListener(final Canvas c, final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder, final float dX, final float dY, final int actionState, final boolean isCurrentlyActive) {
    recyclerView.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
          SwipeController.super.onChildDraw(c, recyclerView, viewHolder, 0F, dY, actionState, isCurrentlyActive);
          recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
              return false;
            }
          });
          setItemsClickable(recyclerView, true);
          swipeBack = false;

          if (buttonsActions != null && buttonInstance != null && buttonInstance.contains(event.getX(), event.getY())) {
            if (buttonShowedState == ButtonsState.LEFT_VISIBLE) {
              buttonsActions.onLeftClicked(viewHolder.getAdapterPosition());
            }
            else if (buttonShowedState == ButtonsState.RIGHT_VISIBLE) {
              buttonsActions.onRightClicked(viewHolder.getAdapterPosition());
            }
          }
          buttonShowedState = ButtonsState.GONE;
          currentItemViewHolder = null;
        }
        return false;
      }
    });
  }

  /* access modifiers changed from: private */
  public void setItemsClickable(RecyclerView recyclerView, boolean isClickable) {
    for (int i = 0; i < recyclerView.getChildCount(); i++) {
      recyclerView.getChildAt(i).setClickable(isClickable);
    }
  }

  private void drawButtons(Canvas c, RecyclerView.ViewHolder viewHolder) {
    Drawable drawable;
    Drawable drawable_delete;
    Canvas canvas = c;
    View itemView = viewHolder.itemView;
    Paint p = new Paint();
    float height = ((float) itemView.getBottom()) - ((float) itemView.getTop());
    float width = height / 3.0f;
    RectF leftButton = new RectF(((float) itemView.getLeft()) + 15.0f, ((float) itemView.getTop()) + 15.0f, ((float) itemView.getLeft()) + 280.0f, ((float) itemView.getBottom()) - 15.0f);
    int i = this.moduleFocus;
    if (i == 1 || i == 3) {
      p.setColor(-16776961);
      canvas.drawRoundRect(leftButton, 16.0f, 16.0f, p);
      drawable = this.context.getResources().getDrawable(R.drawable.ic_edit_white, null);
    } else if (i == 2) {
      p.setColor(-16711936);
      canvas.drawRoundRect(leftButton, 16.0f, 16.0f, p);
      drawable = this.context.getResources().getDrawable(R.drawable.ic_add_report_white, null);
    } else {
      p.setColor(-12303292);
      canvas.drawRoundRect(leftButton, 16.0f, 16.0f, p);
      drawable = this.context.getResources().getDrawable(R.drawable.ic_list_scan_white, null);
    }
    Bitmap icon_left = drawableToBitmap(drawable);
    float f = height;
    RectF icon_dest_left = new RectF(((float) itemView.getLeft()) + width, ((float) itemView.getTop()) + width, ((float) itemView.getLeft()) + (width * 2.0f), ((float) itemView.getBottom()) - width);
    canvas.drawBitmap(icon_left, null, icon_dest_left, p);
    RectF rightButton = new RectF(((float) itemView.getRight()) - 280.0f, ((float) itemView.getTop()) + 15.0f, ((float) itemView.getRight()) - 15.0f, ((float) itemView.getBottom()) - 15.0f);
    int i2 = this.moduleFocus;
    if (i2 == 1 || i2 == 3 || i2 == 4) {
      p.setColor(SupportMenu.CATEGORY_MASK);
      canvas.drawRoundRect(rightButton, 16.0f, 16.0f, p);
      drawable_delete = this.context.getResources().getDrawable(R.drawable.ic_delete_white, null);
    } else {
      p.setColor(-12303292);
      canvas.drawRoundRect(rightButton, 16.0f, 16.0f, p);
      drawable_delete = this.context.getResources().getDrawable(R.drawable.ic_edit_location_white, null);
    }
    RectF rectF = icon_dest_left;
    canvas.drawBitmap(drawableToBitmap(drawable_delete), null, new RectF(((float) itemView.getRight()) - (2.0f * width), ((float) itemView.getTop()) + width, ((float) itemView.getRight()) - width, ((float) itemView.getBottom()) - width), p);
    this.buttonInstance = null;
    if (this.buttonShowedState == ButtonsState.LEFT_VISIBLE) {
      this.buttonInstance = leftButton;
    } else if (this.buttonShowedState == ButtonsState.RIGHT_VISIBLE) {
      this.buttonInstance = rightButton;
    }
  }

  private void drawText(String text, Canvas c, RectF button, Paint p) {
    p.setColor(Color.WHITE);
    p.setAntiAlias(true);
    p.setTextSize(60.0f);
    c.drawText(text, button.centerX() - (p.measureText(text) / 2.0f), button.centerY() + (60.0f / 2.0f), p);
  }

  public static Bitmap drawableToBitmap(Drawable drawable) {
    if (drawable instanceof BitmapDrawable) {
      return ((BitmapDrawable) drawable).getBitmap();
    }
    Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Config.ARGB_8888);
    Canvas canvas = new Canvas(bitmap);
    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
    drawable.draw(canvas);
    return bitmap;
  }

  public void onDraw(Canvas c, int md) {
    this.moduleFocus = md;
    ViewHolder viewHolder = this.currentItemViewHolder;
    if (viewHolder != null) {
      drawButtons(c, viewHolder);
    }
  }
}
