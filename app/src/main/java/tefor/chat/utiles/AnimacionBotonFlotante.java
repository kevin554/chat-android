package tefor.chat.utiles;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

public class AnimacionBotonFlotante extends FloatingActionButton.Behavior {

    public AnimacionBotonFlotante(Context context, AttributeSet attrs) {
        super();
    }

    public static void animar(FloatingActionButton botonFlotante, Context contexto) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            botonFlotante.setScaleX(0);
            botonFlotante.setScaleY(0);

            Interpolator interpolador = AnimationUtils.loadInterpolator(contexto,
                    android.R.interpolator.fast_out_slow_in);

            botonFlotante.animate()
                    .scaleX(1)
                    .scaleY(1)
                    .setInterpolator(interpolador)
                    .setDuration(400)
                    .setStartDelay(500);
        }
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child,
            View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed,
                dxUnconsumed, dyUnconsumed);

        if (dyConsumed > 0) {
            CoordinatorLayout.LayoutParams layoutParams =
                    (CoordinatorLayout.LayoutParams) child.getLayoutParams();

            int margenInferior = layoutParams.bottomMargin;

            child.animate()
                    .translationY(child.getHeight() + margenInferior)
                    .setInterpolator(new LinearInterpolator())
                    .start();
        } else if (dyConsumed < 0) {
            child.animate()
                    .translationY(0)
                    .setInterpolator(new LinearInterpolator())
                    .start();
        }
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout,
            FloatingActionButton child, View directTargetChild, View target, int nestedScrollAxes) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

}
