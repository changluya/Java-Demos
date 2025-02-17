package com.changlu;

import java.util.Objects;

public class Rect {

    private int width;
    private int height;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Rect rect = (Rect) object;
        return width == rect.width && height == rect.height;
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height);
    }
}
