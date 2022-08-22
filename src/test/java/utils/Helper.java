package utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

public final class Helper {
  public static String getRandomAlphanumeric(final int length) {
    return StringUtils.capitalize(RandomStringUtils.randomAlphanumeric(length));
  }
}
