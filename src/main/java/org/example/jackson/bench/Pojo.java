package org.example.jackson.bench;

import java.math.BigDecimal;
import java.util.Random;

public class Pojo {

    private int intValue;

    private long longValue;

    private double doubleValue;

    private float floatValue;

    private String stringValue;

    private BigDecimal numberValue;
    
    public static Pojo newRandomPojo(long seed) {
        Random random = new Random(seed);
        Pojo pojo = new Pojo();
        pojo.setIntValue(random.nextInt());
        pojo.setLongValue(random.nextLong());
        pojo.setFloatValue(random.nextFloat());
        pojo.setDoubleValue(random.nextDouble());
        pojo.setStringValue(Integer.toString(random.nextInt()));
        pojo.setNumberValue(BigDecimal.valueOf(pojo.getDoubleValue()));
        return pojo;
    }

    public int getIntValue() {
        return intValue;
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }

    public long getLongValue() {
        return longValue;
    }

    public void setLongValue(long longValue) {
        this.longValue = longValue;
    }

    public double getDoubleValue() {
        return doubleValue;
    }

    public void setDoubleValue(double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public float getFloatValue() {
        return floatValue;
    }

    public void setFloatValue(float floatValue) {
        this.floatValue = floatValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public BigDecimal getNumberValue() {
        return numberValue;
    }

    public void setNumberValue(BigDecimal numberValue) {
        this.numberValue = numberValue;
    }

}
