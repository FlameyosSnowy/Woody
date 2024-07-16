package me.flame.menus.menu.api;

import me.flame.menus.menu.animation.Animation;

import java.util.List;

public interface AnimationModifiable {

    List<Animation> getActiveAnimations();

    void setAnimating(boolean animating);

    boolean isAnimating();
}
