package config;

import java.awt.*;

public class ColorChoose {

    public static Color getColor(double percent) {
        if (0 <= percent && percent < 12) {
            return ResultColor.EMPTYPLAYERS;
        } else if (12 <= percent && percent < 50) {
            return ResultColor.LOWPLAYERS;
        } else if (50 <= percent && percent <= 70) {
            return ResultColor.MEDIUMPLAYERS;
        } else if (70 <= percent && percent <= 80) {
            return ResultColor.HIGHPLAYERS;
        } else {
            return ResultColor.FULLPLAYERS;
        }
    }

}
