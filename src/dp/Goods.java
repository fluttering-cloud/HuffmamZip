package dp;

import java.util.Objects;

public class Goods {

    public String name;
    public int weight;
    public int value;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Goods pkg = (Goods) o;
        return Objects.equals(name, pkg.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
