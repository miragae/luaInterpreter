package pl.luainterpreter.program.value;

import java.util.Objects;

import static lua.LuaParser.*;
public class Value implements Comparable<Value> {
    public static final Value NIL = new Value(null, null);

    private Object value;
    private Class type;
    private boolean isReturn;

    public Value(Object value, Class type) {
        this.value = value;
        this.type = type;
    }

    public Value(Value other) {
        this.value = other.value;
        this.type = other.type;
        this.isReturn = false;
    }

    public <T> T get(Class<T> type) {
        return type.cast(value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, type);
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Value)) {
            return false;
        }
        Value other = (Value) obj;
        if (other.type == null) {
            return this.type == null;
        }
        if (other.type == this.type) {
            switch (type.getSimpleName()) {
                case "Integer":
                case "Float":
                case "String":
                    return other.value.equals(this.value);
            }
        }
        return other.toString().equals(this.toString());
    }

    public Class getType() {
        return type;
    }

    public void setReturn(boolean isReturn) {
        this.isReturn = isReturn;
    }

    public boolean isReturn() {
        return isReturn;
    }

    @Override
    public String toString() {
        return value == null ? "nil" : value.toString();
    }

    @Override
    public int compareTo(Value other) {
        if (other.type == this.type) {
            switch (type.getSimpleName()) {
                case "Integer":
                    return Integer.compare(get(Integer.class), other.get(Integer.class));
                case "Float":
                    return Float.compare(get(Float.class), other.get(Float.class));
                case "String":
            }
        }
        return other.toString().compareTo(this.toString());
    }

    public static class ValueOperations {

        public static Value compare(int opType, Value left, Value right) {
            return switch (opType) {
                case EQ -> new Value(left.equals(right), Boolean.class);
                case NEQ -> new Value(!left.equals(right), Boolean.class);
                case GT -> new Value(left.compareTo(right) > 0, Boolean.class);
                case GTE -> new Value(left.compareTo(right) >= 0, Boolean.class);
                case LT -> new Value(left.compareTo(right) < 0, Boolean.class);
                case LTE -> new Value(left.compareTo(right) <= 0, Boolean.class);
                default -> throw new IllegalStateException("Unexpected relational operation: " + opType);
            };
        }

        public static Value negate(Value value) {
            if (value.type == Integer.class) {
                value.value = -value.get(Integer.class);
            } else if (value.type == Float.class) {
                value.value = -value.get(Float.class);
            }
            return value;
        }

        public static Value calculate(int opType, Value left, Value right) {
            if (left.type == Integer.class && right.type == Integer.class) {
                return new Value(calculateInt(opType, left.get(Integer.class), right.get(Integer.class)), Integer.class);
            } else {
                return new Value(calculateFloat(opType, Float.parseFloat(left.toString()), Float.parseFloat(right.toString())), Float.class);
            }
        }

        public static Value calculatePower(Value left, Value right) {
            Integer power = right.get(Integer.class);
            return new Value(Double.valueOf(Math.pow(Float.parseFloat(left.toString()), power)).floatValue(), Float.class);
        }

        private static int calculateInt(int opType, int left, int right) {
            return switch (opType) {
                case ADD -> left + right;
                case SUB -> left - right;
                case MUL -> left * right;
                case DIV -> left / right;
                default -> throw new IllegalStateException("Unexpected arithmetic operation: " + opType);
            };
        }

        private static float calculateFloat(int opType, float left, float right) {
            return switch (opType) {
                case ADD -> left + right;
                case SUB -> left - right;
                case MUL -> left * right;
                case DIV -> left / right;
                default -> throw new IllegalStateException("Unexpected arithmetic operation: " + opType);
            };
        }
    }
}
