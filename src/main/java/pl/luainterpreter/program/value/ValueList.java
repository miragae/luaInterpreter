package pl.luainterpreter.program.value;

import java.util.Iterator;
import java.util.List;

public class ValueList extends Value implements Iterable<Value> {

    private List<Value> values;

    public ValueList(List<Value> valueList) {
        super(valueList, List.class);
        this.values = valueList;
    }

    public List<Value> getList() {
        return values;
    }

    public void setValues(List<Value> values) {
        this.values = values;
    }

    @Override
    public Iterator<Value> iterator() {
        return values.iterator();
    }
}
