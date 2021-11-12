package tests_pack;

import annotations.AfterTest;
import annotations.BeforeTest;
import annotations.Test;

import java.util.Arrays;
import java.util.Collections;

@Test
public class Check {

    private int firstArg;
    private int secondArg;
    private Integer[] intArray;


    @BeforeTest
    public void setData() {
        System.out.println("It is before test");
        firstArg = 10;
        secondArg = 0;
        intArray = new Integer[]{8, 3, 7, 18, 24, 1};
    }

    @Test(description = "this test must be fail")
    public void checkNegative() {
        System.out.println("test in first instance");
        Arrays.sort(intArray);
        checkArray();
    }

    @Test(description = "This test for order and revers array in java.util.Arrays")
    public void orderAndReversArray() {
        System.out.println("test in second instance");
        Arrays.sort(intArray, Collections.reverseOrder());
        checkArray();
    }

    @AfterTest
    public void outData() {
        System.out.println("It is after test");
    }
    private void checkArray () throws RuntimeException{
        int x=0;
        int count = 0;
        for (Integer element : intArray) {
            if (count==0){
                x = element;
                count++;
            }else
            if (element>x) throw new RuntimeException("Arrays is not revers ordered");
            else x = element;
        }
    }
}
