package net.wesleybrown.javapong;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.joml.Vector3f;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RectangleTest {

    @Test
    public void creatingRectangleWithWidthOrHeightLessThanOrEqualToZeroThrowsException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new Rectangle(new Vector3f(0.0f, 0.0f, 0.0f), 0.0f, 0.0f);
            new Rectangle(new Vector3f(0.0f, 0.0f, 0.0f), 1.0f, 0.0f);
            new Rectangle(new Vector3f(0.0f, 0.0f, 0.0f), 0.0f, 1.0f);
        });
    }

    @Test
    public void testIntersectsWithNoOverlap() {
        final Rectangle[][] inputs = {
                {
                    new Rectangle(new Vector3f(0.0f, 0.0f, 0.0f), 1.0f, 1.0f),
                    new Rectangle(new Vector3f(5.0f, 0.0f, 0.0f), 1.0f, 1.0f)
                },
                {
                    new Rectangle(new Vector3f(0.0f, 0.0f, 0.0f), 1.0f, 1.0f),
                    new Rectangle(new Vector3f(1.0f, 0.0f, 0.0f), 0.999f, 1.0f)
                },
                {
                    new Rectangle(new Vector3f(1.001f, 1.001f, 0.0f), 2.0f, 2.0f),
                    new Rectangle(new Vector3f(-1.001f, -1.001f, 0.0f), 2.0f, 2.0f)
                }
        };

        final boolean[] expected = {
                false,
                false,
                false
        };

        testIntersectsWith(expected, inputs);
    }

    @Test
    public void testIntersectsWithOverlap() {
        final Rectangle[][] inputs = {
                {
                    new Rectangle(new Vector3f(0.0f, 0.0f, 0.0f), 1.0f, 1.0f),
                    new Rectangle(new Vector3f(0.5f, 0.0f, 0.0f), 1.0f, 1.0f)
                },
                {
                    new Rectangle(new Vector3f(1.0f, 1.0f, 0.0f), 2.0f, 2.0f),
                    new Rectangle(new Vector3f(-1.0f, -1.0f, 0.0f), 2.0f, 2.0f)
                },
                {
                    new Rectangle(new Vector3f(0.0f, 0.0f, 0.0f), 1.0f, 1.0f),
                    new Rectangle(new Vector3f(0.0f, 0.0f, 0.0f), 1.0f, 1.0f)
                }
        };

        final boolean[] expected = {
                true,
                true,
                true
        };

        testIntersectsWith(expected, inputs);
    }

    private void testIntersectsWith(final boolean[] expected, final Rectangle[][] inputs) {
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], inputs[i][0].intersectsWith(inputs[i][1]));
        }
    }
}
