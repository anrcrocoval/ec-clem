package plugins.fr.univ_nantes.ec_clem.transformation.schema;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum NoiseModel {
    ISOTROPIC,
    ANISOTROPIC;

    public static String[] toArray() {
        List<String> collect = Arrays.stream(NoiseModel.values()).map(Enum::name).collect(Collectors.toList());
        String[] array = new String[collect.size()];
        for(int i = 0; i < collect.size(); i++) {
            array[i] = collect.get(i);
        }
        return array;
    }
}
