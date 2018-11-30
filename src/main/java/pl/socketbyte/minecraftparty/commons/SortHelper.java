package pl.socketbyte.minecraftparty.commons;

import java.util.*;

public class SortHelper {

    private SortHelper() {
    }

    public static <B> Map<B, Integer> sortByIntValue(Map<B, Integer> unsortedMap)  {
        Map<B, Integer> result = new LinkedHashMap<>();

        ArrayList<B> sortedKeys = new ArrayList<>(unsortedMap.keySet());
        for (int i=0; i<unsortedMap.size(); i++)  {
            for (int j=1; j<sortedKeys.size(); j++)  {
                if (unsortedMap.get(sortedKeys.get(j)) > unsortedMap.get(sortedKeys.get(j-1))) {
                    B temp = sortedKeys.get(j);
                    sortedKeys.set(j, sortedKeys.get(j-1));
                    sortedKeys.set(j-1, temp);

                }
            }
        }

        for (B key: sortedKeys)  {
            result.put(key, unsortedMap.get(key));
        }

        return result;
    }

    public static <B> Map<B, Long> sortByLongValue(Map<B, Long> unsortedMap)  {
        Map<B, Long> result = new LinkedHashMap<>();

        ArrayList<B> sortedKeys = new ArrayList<>(unsortedMap.keySet());
        for (int i=0; i<unsortedMap.size(); i++)  {
            for (int j=1; j<sortedKeys.size(); j++)  {
                if (unsortedMap.get(sortedKeys.get(j)) > unsortedMap.get(sortedKeys.get(j-1))) {
                    B temp = sortedKeys.get(j);
                    sortedKeys.set(j, sortedKeys.get(j-1));
                    sortedKeys.set(j-1, temp);

                }
            }
        }

        for (B key: sortedKeys)  {
            result.put(key, unsortedMap.get(key));
        }

        return result;
    }
}
