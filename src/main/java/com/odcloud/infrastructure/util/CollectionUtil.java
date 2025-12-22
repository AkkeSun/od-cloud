package com.odcloud.infrastructure.util;

import java.util.ArrayList;
import java.util.List;

public class CollectionUtil {

    public static <T> List<List<T>> partition(List<T> list, int chunkSize) {
        List<List<T>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += chunkSize) {
            partitions.add(list.subList(i, Math.min(i + chunkSize, list.size())));
        }
        return partitions;
    }
}
