import java.util.*;

public class CustomerService {

    private final TreeMap<Customer, String> mapOfCustomers;

    {
        mapOfCustomers = new TreeMap<>(Comparator.comparingLong(Customer::getScores));
    }

    public Map.Entry<Customer, String> getSmallest() {
        Map.Entry<Customer, String> temp = mapOfCustomers.firstEntry();
        if (temp == null) return null;
        return new AbstractMap.SimpleEntry<>(new Customer(temp.getKey()), temp.getValue());
    }

    public Map.Entry<Customer, String> getNext(Customer customer) {
        Map.Entry<Customer, String> temp = mapOfCustomers.higherEntry(customer);
        if (temp == null) return null;
        return new AbstractMap.SimpleEntry<>(new Customer(temp.getKey()), temp.getValue());
    }

    public void add(Customer customer, String data) {
        mapOfCustomers.put(customer, data);
    }

}
