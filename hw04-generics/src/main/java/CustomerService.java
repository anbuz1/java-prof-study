import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class CustomerService {

    private final TreeMap<Customer, String> mapOfCustomers;

    {
        mapOfCustomers = new TreeMap<>(Comparator.comparingLong(Customer::getScores));
    }

    public Map.Entry<Customer, String> getSmallest() {
        return mapOfCustomers.firstEntry();
    }

    public Map.Entry<Customer, String> getNext(Customer customer) {
        return mapOfCustomers.higherEntry(customer);
    }

    public void add(Customer customer, String data) {
        mapOfCustomers.put(customer, data);
    }

}
