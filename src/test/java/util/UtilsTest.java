package test.java.util;

import main.java.util.Utils;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class UtilsTest {

    @Test
    public void testDecimalToUnsignedBinary() {
        assertEquals(Utils.decimalToUnsignedBinary(0), "0000000000000000");
        assertEquals(Utils.decimalToUnsignedBinary(Short.MAX_VALUE), "0111111111111111");
        assertEquals(Utils.decimalToUnsignedBinary(-Short.MAX_VALUE), "1000000000000001");
        assertEquals(Utils.decimalToUnsignedBinary(Short.MIN_VALUE), "1000000000000000");
    }

    @Test
    public void testUnsignedBinaryToDecimal() {
        assertEquals(Utils.unsignedBinaryToDecimal("0000000000000000"), 0);
        assertEquals(Utils.unsignedBinaryToDecimal("0111111111111111"), Short.MAX_VALUE);
        assertEquals(Utils.unsignedBinaryToDecimal("1000000000000001"), -Short.MAX_VALUE);
        assertEquals(Utils.unsignedBinaryToDecimal("1000000000000000"), Short.MIN_VALUE);
    }

    @Test
    public void testFloatingPointValue() {
        assertEquals(Utils.binaryToFloatingPoint("0000000011011011"), BigDecimal.valueOf(219));
        assertEquals(Utils.binaryToFloatingPoint("0010110111011011"), BigDecimal.valueOf(2.19E47));
        assertEquals(Utils.binaryToFloatingPoint("1010110111011011"), BigDecimal.valueOf(-2.19E47));
        assertEquals(Utils.binaryToFloatingPoint("0110110111011011"), BigDecimal.valueOf(2.19E-43));
        assertEquals(Utils.binaryToFloatingPoint("1110110111011011"), BigDecimal.valueOf(-2.19E-43));
        assertEquals(Utils.binaryToFloatingPoint("0100000111010111"), BigDecimal.valueOf(21.5));
    }

    @Test
    public void floatingPointToBinary() {
        assertEquals(Utils.floatingPointToBinary(BigDecimal.valueOf(21.5)), "0100000111010111");
        assertEquals(Utils.floatingPointToBinary(BigDecimal.valueOf(219)), "0000000011011011");
        assertEquals(Utils.floatingPointToBinary(BigDecimal.valueOf(2.19E47)), "0010110111011011");
        assertEquals(Utils.floatingPointToBinary(BigDecimal.valueOf(-2.19E47)), "1010110111011011");
    }

    @Test
    public void isOverflow() {
        assertFalse(Utils.isOverflow(BigDecimal.valueOf(255)));
        assertTrue(Utils.isOverflow(BigDecimal.valueOf(256))); // Just crossing 2^8-1 mantissa
        assertFalse(Utils.isOverflow(BigDecimal.valueOf(1E31)));
        assertTrue(Utils.isOverflow(BigDecimal.valueOf(1E32))); // Just crossing 2^6-1 exponent
    }

    @Test
    public void test() {
        assertEquals(Utils.binaryInstruction("LDR 3,0,31"), "0000011100011111");
        assertEquals(Utils.binaryInstruction("IN 0,2"),     "1111010000000010");
        assertEquals(Utils.binaryInstruction("SRC 3,3,1,1"),"0111111111000001");
        assertEquals(Utils.binaryInstruction("MLT 0,2"),    "0101000010000000");
        assertEquals(Utils.binaryInstruction("DVD 2,0"),    "0101011000000000");
        assertEquals(Utils.binaryInstruction("JZ 0,0,1"),   "0010100000000001");
        assertEquals(Utils.binaryInstruction("AIR 3,10"),   "0001101100001010");
        assertEquals(Utils.binaryInstruction("JCC 1,0,1"),  "0011000100000001");
    }

}
