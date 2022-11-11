/**
 * References:
 * <dl>
 *     <dt>This class has been derived from "FastDoubleParser".</dt>
 *     <dd>Copyright (c) Werner Randelshofer. Apache 2.0 License.
 *         <a href="https://github.com/wrandelshofer/FastDoubleParser">github.com</a>.</dd>
 * </dl>
 */

package org.example.jackson.bench.doubleparser;

import java.math.BigDecimal;

/**
 * Parses a {@code double} from a {@link CharSequence}.
 */
final class JavaBigDecimalFromCharSequence {
    final static int MAX_EXPONENT_NUMBER = Integer.MAX_VALUE;


    /**
     * Creates a new instance.
     */
    public JavaBigDecimalFromCharSequence() {

    }

    private boolean isDigit(char c) {
        return '0' <= c && c <= '9';
    }

    private int tryToParseEightDigits(CharSequence str, int offset) {
        return FastDoubleSwar.tryToParseEightDigits(str, offset);
    }

    private int parseEightDigits(CharSequence str, int offset) {
        return FastDoubleSwar.parseEightDigits(str, offset);
    }

    public BigDecimal parseFloatingPointLiteral(CharSequence str, int offset, int length) {
        long significand = 0L;
        final int integerPartIndex;
        int decimalPointIndex = -1;
        final int exponentIndicatorIndex;

        final int endIndex = offset + length;
        int index = offset;
        char ch = str.charAt(index);
        boolean illegal = false;


        // Parse optional sign
        // -------------------
        final boolean isNegative = ch == '-';
        if (isNegative || ch == '+') {
            ch = ++index < endIndex ? str.charAt(index) : 0;
            if (ch == 0) {
                return null;
            }
        }

        // Parse significand
        integerPartIndex = index;
        for (; index < endIndex; index++) {
            ch = str.charAt(index);
            if (isDigit(ch)) {
                // This might overflow, we deal with it later.
                significand = 10 * (significand) + ch - '0';
            } else if (ch == '.') {
                illegal |= decimalPointIndex >= 0;
                decimalPointIndex = index;
                for (; index < endIndex - 4; index += 4) {
                    int eightDigits = FastDoubleSwar.tryToParseFourDigits(str, index + 1);
                    if (eightDigits < 0) {
                        break;
                    }
                    // This might overflow, we deal with it later.
                    significand = 10_000L * significand + eightDigits;
                }
            } else {
                break;
            }
        }

        final int digitCount;
        final int significandEndIndex = index;
        long exponent;
        if (decimalPointIndex < 0) {
            digitCount = significandEndIndex - integerPartIndex;
            decimalPointIndex = significandEndIndex;
            exponent = 0;
        } else {
            digitCount = significandEndIndex - integerPartIndex - 1;
            exponent = decimalPointIndex - significandEndIndex + 1;
        }

        // Parse exponent number
        // ---------------------
        int expNumber = 0;
        if (ch == 'e' || ch == 'E') {
            exponentIndicatorIndex = index;
            ch = ++index < endIndex ? str.charAt(index) : 0;
            boolean isExponentNegative = ch == '-';
            if (isExponentNegative || ch == '+') {
                ch = ++index < endIndex ? str.charAt(index) : 0;
            }
            illegal |= !isDigit(ch);
            do {
                // Guard against overflow
                if (expNumber < MAX_EXPONENT_NUMBER) {
                    expNumber = 10 * (expNumber) + ch - '0';
                }
                ch = ++index < endIndex ? str.charAt(index) : 0;
            } while (isDigit(ch));
            if (isExponentNegative) {
                expNumber = -expNumber;
            }
            exponent += expNumber;
        } else {
            exponentIndicatorIndex = endIndex;
        }
        if (illegal || index < endIndex
                || digitCount == 0
                || Math.abs(exponent) > MAX_EXPONENT_NUMBER) {
            return null;
        }

        if (digitCount <= 18) {
            return new BigDecimal(isNegative ? -significand : significand).scaleByPowerOfTen((int) exponent);
        }
        return parseDecFloatLiteral(str, integerPartIndex, decimalPointIndex, exponentIndicatorIndex, isNegative, (int) exponent);
    }

    private BigDecimal parseDecFloatLiteral(CharSequence str, int integerPartIndex, int pointIndex, int exponentIndicatorIndex, boolean isNegative, int exponent) {
        boolean hasIntegerPart = pointIndex - integerPartIndex > 0;
        boolean hasFractionalPart = exponentIndicatorIndex - pointIndex > 1;
        BigDecimal integerPart = hasIntegerPart
                ? parseDigits(str, integerPartIndex, pointIndex, exponent + exponentIndicatorIndex - pointIndex - (hasFractionalPart ? 1 : 0))
                : BigDecimal.ZERO;
        BigDecimal fractionalPart = hasFractionalPart
                ? parseDigits(str, pointIndex + 1, exponentIndicatorIndex, exponent)
                : BigDecimal.ZERO;

        BigDecimal result = integerPart.add(fractionalPart);
        return isNegative ? result.negate() : result;
    }

    private BigDecimal parseDigits(CharSequence str, int index, int endIndex, int exponent) {

        // Recursion base case: All sequences of 18 or fewer digits fit into a long.
        int digitCount = endIndex - index;
        if (digitCount <= 18) {
            int significand = 0;
            int prerollLimit = index + (digitCount & 7);
            for (; index < prerollLimit; index++) {
                significand = 10 * (significand) + str.charAt(index) - '0';
            }
            long significandL = significand;
            for (; index < endIndex; index += 8) {
                significandL = significandL * 100_000_000L + parseEightDigits(str, index);
            }
            return BigDecimal.valueOf(significandL, -exponent);
        }

        // Recursion regular case: Sequences of more than 18 digits do not fit into a long.
        int mid = (index + endIndex) >>> 1;
        BigDecimal high = parseDigits(str, index, mid, exponent + endIndex - mid);
        BigDecimal low = parseDigits(str, mid, endIndex, exponent);
        return low.add(high);
    }
}