import com.flipthebird.gwthashcodeequals.EqualsBuilder;
import com.flipthebird.gwthashcodeequals.HashCodeBuilder;


public class Customer {
    private final long id;
    private String name;
    private long scores;


    public Customer(long id, String name, long scores) {
        this.id = id;
        this.name = name;
        this.scores = scores;
    }
    public Customer(Customer customer){
        this.id = customer.id;
        this.name = customer.name;
        this.scores = customer.scores;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getScores() {
        return scores;
    }

    public void setScores(long scores) {
        this.scores = scores;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer;
        try {
            customer = (Customer) o;
        } catch (ClassCastException e) {
            return false;
        }
        EqualsBuilder eb = new EqualsBuilder();
        eb.append(scores, customer.getScores());
        return eb.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hcb = new HashCodeBuilder();
        hcb.append(id);
        return hcb.hashCode();
    }
}
