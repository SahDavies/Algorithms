import edu.princeton.cs.algs4.Quick3string;
import edu.princeton.cs.algs4.Stopwatch;
import edu.princeton.cs.algs4.TST;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) {
        List<String> strings = List.of(
                "COLLEGE OF ENGINEERING",
                "COLLEGE OF VETERINARY MEDICINE",
                "COLLEGE OF PLANT SCIENCE AND CROP PRODUCTION",
                "COLLEGE OF MANAGEMENT SCIENCES",
                "COLLEGE OF AGRICULTURAL MANAGEMENT AND RURAL DEVELOPMENT",
                "COLLEGE OF ENVIRONMENTAL RESOURCE MANAGEMENT",
                "COLLEGE OF FOOD SCIENCE AND HUMAN ECOLOGY",
                "COLLEGE OF ANIMAL SCIENCE AND LIVESTOCK PRODUCTION",
                "COLLEGE OF BIOLOGICAL SCIENCES",
                "COLLEGE OF PHYSICAL SCIENCES",
                "Computer Science",
                "Mathematics",
                "Physics",
                "Chemistry",
                "Statistics",
                "Pure and applied Zoology",
                "Pure and applied Botany",
                "Microbiology",
                "Food Science and Technology",
                "Hospitality and Tourism Management",
                "Nutrition and Dietetics",
                "Home Science and Management",
                "Animal Breeding and Genetics",
                "Animal Production and Health",
                "Animal Physiology",
                "Pasture and Range Management",
                "Aquaculture and Fisheries Management",
                "Environmental Management and Toxicology",
                "Forestry and Wildlife Management",
                "Water Resources Management and Agrometeorology",
                "Agricultural Economics and Farm Management",
                "Agricultural Extension and Rural Development",
                "Agricultural Administration",
                "Communication and General Studies",
                "Accounting",
                "Banking and Finance",
                "Business Enterprise Management",
                "Economics",
                "Entrepreneurial Studies",
                "Crop Protection",
                "Horticulture",
                "Plant Breeding and Seed Technology",
                "Plant Physiology and Crop Production",
                "Soil Science and Land Management",
                "Veterinary Anatomy",
                "Veterinary Physiology & Pharmacology",
                "Veterinary Pathology",
                "Veterinary Microbiology & Parasitology",
                "Veterinary Medicine & Surgery",
                "Veterinary Public Health & Reproduction",
                "Agricultural and Bio-Resources Engineering",
                "Civil Engineering",
                "Electrical and Electronics Engineering",
                "Mechanical Engineering",
                "Mechatronics Engineering"
        );

        compareSearchRunTimeOfGSuffArrayandTST(strings, "ology");

        CompareRunningTimeofStandardSortAndGSuffArray(strings, list -> Quick3string.sort(list.toArray(new String[0])));

    }

    private static void compareSearchRunTimeOfGSuffArrayandTST(List<String> strings, String prefix) {
        GSuffArray sa = new GSuffArray(strings.toArray(new String[0]));

        TST<ArrayList<String>> trie = new TST<>();

        for (int i = 0; i < sa.length(); i++) {
            String[] pair = sa.pair(i);
            ArrayList<String> value = trie.get(pair[0]);
            if (value != null) {
                value.add(pair[1]);
                trie.put(pair[0], value);
                continue;
            }
            value = new ArrayList<>();
            value.add(pair[1]);
            trie.put(pair[0], value);
        }

        double elapsedTimeSuffixArray;
        double elapsedTimeTST;

        Stopwatch timerSuffixArray = new Stopwatch();
        printTextThatContainsPrefix(sa.texts(prefix));
        elapsedTimeSuffixArray = timerSuffixArray.elapsedTime();


        Stopwatch timerTST= new Stopwatch();
        for (String text : trie.keysWithPrefix(prefix)) {
            printTextThatContainsPrefix(trie.get(text));
        }

        elapsedTimeTST = timerTST.elapsedTime();

        System.out.printf("\nSearch running time GSuffArray: %f\n\n", elapsedTimeSuffixArray);
        System.out.printf("Search running time TST: %f\n\n", elapsedTimeTST);
    }

    private static void printTextThatContainsPrefix(Iterable<String> texts) {
        for (String text : texts) {
            System.out.println(text);
        }
    }

    private static void CompareRunningTimeofStandardSortAndGSuffArray(List<String> strings , Consumer<List<Comparable>> sort) {

        double elapsedTime;

        // running time for GSuffArray
        Stopwatch timerSuffixArray = new Stopwatch();
        new GSuffArray(strings.toArray(new String[0]));

        elapsedTime = timerSuffixArray.elapsedTime();
        System.out.printf("GSuffixArray Implementation: %f\n\n", elapsedTime);

        // running time for standard sort
        Stopwatch timerQuick3Way = new Stopwatch();
        List<Comparable> subStrings = strings.stream()
                .flatMap(str ->
                        IntStream.range(0, str.length())
                                .boxed()
                                .flatMap(i ->
                                        IntStream.rangeClosed(i + 1, str.length())
                                                .mapToObj(j -> (Comparable) str.substring(i, j))
                                )
                ).toList();
        sort.accept(subStrings);

        elapsedTime = timerQuick3Way.elapsedTime();
        System.out.printf("Quick3string Implementation: %f\n\n", elapsedTime);
    }
}