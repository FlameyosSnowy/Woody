package me.flame.menus.menu.animation.variants;

import me.flame.menus.menu.Menu;
import me.flame.menus.menu.animation.Animation;
import me.flame.menus.menu.animation.AnimationScheduler;
import me.flame.menus.menu.animation.Frame;
import org.jetbrains.annotations.Nullable;

/**
 * @since 2.0.0
 */
public class NormalAnimation extends Animation {
    public NormalAnimation(int delay, int repeat, Frame[] frames, AnimationScheduler scheduler) {
        super(delay, repeat, frames, scheduler);
    }

    @Override
    public @Nullable Frame onFinish(Menu menu) {
        stop();
        return null;
    }
}
