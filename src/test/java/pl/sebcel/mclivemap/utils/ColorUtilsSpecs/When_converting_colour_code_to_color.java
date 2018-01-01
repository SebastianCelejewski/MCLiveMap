package pl.sebcel.mclivemap.utils.ColorUtilsSpecs;

import org.junit.Test;

import junit.framework.Assert;
import pl.sebcel.mclivemap.utils.ColorUtils;

public class When_converting_colour_code_to_color {

    @Test
    public void should_return_null_if_color_code_is_null() {
        Assert.assertEquals(null, ColorUtils.getColor(null));
    }

    @Test
    public void should_return_null_if_color_code_is_empty() {
        Assert.assertEquals(null, ColorUtils.getColor(""));
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_IllegalArgumentException_if_color_code_is_not_six_character_string() {
        ColorUtils.getColor("asdf");
        ColorUtils.getColor("asd   ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_IllegalArgumentException_if_color_code_has_any_spaces() {
        ColorUtils.getColor("asd   ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_IllegalArgumentException_if_color_code_is_not_hexadecimal_number() {
        ColorUtils.getColor("qqwwee");
    }

    @Test
    public void should_return_red_component_from_first_two_characters() {
        Assert.assertEquals(170, ColorUtils.getColor("aabbcc").getRed());
    }

    @Test
    public void should_convert_green_component_from_middle_two_characters() {
        Assert.assertEquals(187, ColorUtils.getColor("aabbcc").getGreen());
    }

    @Test
    public void should_convert_blue_component_from_last_two_caracters() {
        Assert.assertEquals(204, ColorUtils.getColor("aabbcc").getBlue());
    }
}