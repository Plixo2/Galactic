package de.plixo.common;

import lombok.AllArgsConstructor;

public abstract sealed class Constant
        permits Constant.BoolConstant, Constant.NumberConstant, Constant.StringConstant {

    @AllArgsConstructor
    public static final class BoolConstant extends Constant {
        public boolean bool;

        @Override
        public String toString() {
            return String.valueOf(bool);
        }
    }

    @AllArgsConstructor
    public static final class NumberConstant extends Constant {
        public Number number;

        @Override
        public String toString() {
            return number.toString();
        }
    }

    @AllArgsConstructor
    public static final class StringConstant extends Constant {
        public String string;

        @Override
        public String toString() {
            return "\"" + string + "\"";
        }
    }
}
