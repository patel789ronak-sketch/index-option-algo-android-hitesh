package com.justsimple.indexalgo;

public final class StrategyEngine {
    public enum Signal { CE, PE, NONE }

    public static final class Snapshot {
        public double close, orHigh, orLow, vwap, emaFast, emaSlow, adx;
        public boolean liquid;
        public double spreadPercent;
        public long volume;
    }

    public static final class Config {
        public double minAdx = 20.0;
        public long minVolume = 1000;
        public double maxSpreadPercent = 1.0;
    }

    private StrategyEngine() {}

    public static Signal evaluate(Snapshot s, Config c) {
        if (!s.liquid || s.volume < c.minVolume || s.spreadPercent > c.maxSpreadPercent) return Signal.NONE;
        if (s.close > s.orHigh && s.close > s.vwap && s.emaFast > s.emaSlow && s.adx >= c.minAdx) return Signal.CE;
        if (s.close < s.orLow && s.close < s.vwap && s.emaFast < s.emaSlow && s.adx >= c.minAdx) return Signal.PE;
        return Signal.NONE;
    }
}
