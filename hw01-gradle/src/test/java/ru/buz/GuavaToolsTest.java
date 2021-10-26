package ru.buz;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GuavaToolsTest {
    @Test
    public void whenRemoveNullFromList_thenRemoved() {
        List<String> names = Lists.newArrayList("John", null, "Adam", null, "Jane");
        List<String> namesExpected = Lists.newArrayList("John", "Adam", "Jane");

        names = new GuavaTools().removeNullFromList(names);

        assertEquals(3, names.size());
        assertEquals(namesExpected,names);
    }

}