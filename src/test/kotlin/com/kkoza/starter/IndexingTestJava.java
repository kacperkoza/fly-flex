package com.kkoza.starter;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class IndexingTestJava {

    @Test
    public void testIndexing() {

        List<Integer> firstRange = new ArrayList<>();
        List<Integer> secondRange = new ArrayList<>();

        for (int i = 1; i < 32; i++) {
            firstRange.add(i);
            secondRange.add(i);
        }

        int offset = 1;
        int tripLength = 7;

        List<Pair> pairs = new ArrayList<>();

        for (int startDateIndex = 0; startDateIndex < firstRange.size(); startDateIndex++) {

            for (int firstEndDateIndexOffset = -offset; firstEndDateIndexOffset <= offset; firstEndDateIndexOffset++) {
                for (int secondStartDateIndexOffset = -offset; secondStartDateIndexOffset <= 0; secondStartDateIndexOffset++) {
                    for (int secondEndDateIndexOffset = -offset; secondEndDateIndexOffset <= 0; secondEndDateIndexOffset++) {
                        if (startDateIndex + tripLength + firstEndDateIndexOffset + 1 > firstRange.size()) {
                            break;
                        }

                        if (startDateIndex + tripLength + secondEndDateIndexOffset + 1 > secondRange.size()) {
                            break;
                        }

                        if (startDateIndex + secondStartDateIndexOffset < 0) {
                            continue;
                        }
                        int firstStartDateIndex = startDateIndex;
                        int secondStartDateIndex = startDateIndex + secondStartDateIndexOffset;
                        int firstEndDateIndex = startDateIndex + firstEndDateIndexOffset + tripLength;
                        int secondEndDateIndex = startDateIndex + secondStartDateIndexOffset + secondEndDateIndexOffset + tripLength;

                        IndexPair indexPair = new IndexPair(firstRange.get(firstStartDateIndex), firstRange.get(firstEndDateIndex));
                        IndexPair indexPair1 = new IndexPair(secondRange.get(secondStartDateIndex), secondRange.get(secondEndDateIndex));
                        Pair pair = new Pair(indexPair, indexPair1);
                        System.out.println(pair);
                    }
                }
            }

        }
    }

    private class Pair {
        public IndexPair firstPair;
        public IndexPair secondPair;

        public Pair(IndexPair firstPair, IndexPair secondPair) {
            this.firstPair = firstPair;
            this.secondPair = secondPair;
        }

        @Override
        public String toString() {
            return "Pair{" +
                    "firstPair=" + firstPair +
                    ", secondPair=" + secondPair +
                    '}';
        }
    }

    private class IndexPair {
        public Integer firstValue;
        public Integer secondValue;

        @Override
        public String toString() {
            return "IndexPair{" +
                    "firstValue=" + firstValue +
                    ", secondValue=" + secondValue +
                    '}';
        }

        public IndexPair(int firstValue, int secondValue) {
            this.firstValue = firstValue;
            this.secondValue = secondValue;
        }
    }
}
