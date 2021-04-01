package io.shapez.game.modes;

import io.shapez.game.GameMode;
import io.shapez.game.GameRoot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class RegularGameMode extends GameMode {
    private final String preparementShape = "CpRpCp--:SwSwSwSw";
    private final GameRoot root;
    private final String finalGameShape = "RuCw--Cw:----Ru--";
    private final String rocketShape = "CbCuCbCu:Sr------:--CrSrCr:CwCwCwCw";

    public RegularGameMode(GameRoot root) {
        super();
        this.root = root;
    }

    @Override
    public HashMap<String, UpgradeTiers> getUpgrades() {
        HashMap<String, UpgradeTiers> fullVersionUpgrades = generateUpgrades(false);
        HashMap<String, UpgradeTiers> demoVersionUpgrades = generateUpgrades(true);
        return this.root.app.restrictionMgr.getHasExtendedUpgrades() ? fullVersionUpgrades : demoVersionUpgrades;
    }

    private HashMap<String, UpgradeTiers> generateUpgrades(boolean limitedVersion) {
        ArrayList<Double> fixedImprovements = (ArrayList<Double>) DoubleStream.of(0.5, 0.5, 1.0, 1.0, 2.0, 1.0).boxed().collect(Collectors.toList());
        int numEndgameUpgrades = limitedVersion ? 0 : 1000 - fixedImprovements.size() - 1;

        for (int i = 0; i < numEndgameUpgrades; i++) {
            if (i < 20) {
                fixedImprovements.add(0.1);
            } else if (i < 50) {
                fixedImprovements.add(0.05);
            } else if (i < 100) {
                fixedImprovements.add(0.025);
            } else {
                fixedImprovements.add(0.0125);
            }
        }


        return new HashMap<>() {{
            put("belt", new UpgradeTiers(new ArrayList<>() {{
                add(new TierRequirement(new UpgradeRequirement[]{new UpgradeRequirement("CuCuCuCu", 30)}));
                add(new TierRequirement(new UpgradeRequirement[]{new UpgradeRequirement("--CuCu--", 500)}));
                add(new TierRequirement(new UpgradeRequirement[]{new UpgradeRequirement("CpCpCpCp", 1000)}));
                add(new TierRequirement(new UpgradeRequirement[]{new UpgradeRequirement("SrSrSrSr:CyCyCyCy", 6000)}));
                add(new TierRequirement(new UpgradeRequirement[]{new UpgradeRequirement("SrSrSrSr:CyCyCyCy:SwSwSwSw", 25000)}));
                add(new TierRequirement(new UpgradeRequirement[]{new UpgradeRequirement(preparementShape, 25000)}, true));
                add(new TierRequirement(new UpgradeRequirement[]{new UpgradeRequirement(preparementShape, 25000), new UpgradeRequirement(finalGameShape, 50000)}, true));
                addAll(generateInfiniteUnlocks(numEndgameUpgrades));
            }}));
            put("miner", new UpgradeTiers(new ArrayList<>() {{
                add(new TierRequirement(new UpgradeRequirement[]{new UpgradeRequirement("RuRuRuRu", 300)}));
                add(new TierRequirement(new UpgradeRequirement[]{new UpgradeRequirement("Cu------", 800)}));
                add(new TierRequirement(new UpgradeRequirement[]{new UpgradeRequirement("ScScScSc", 3500)}));
                add(new TierRequirement(new UpgradeRequirement[]{new UpgradeRequirement("CwCwCwCw:WbWbWbWb", 23000)}));
                add(new TierRequirement(new UpgradeRequirement[]{new UpgradeRequirement("CbRbRbCb:CwCwCwCw:WbWbWbWb", 50000)}));
                add(new TierRequirement(new UpgradeRequirement[]{new UpgradeRequirement(preparementShape, 25000)}, true));
                add(new TierRequirement(new UpgradeRequirement[]{new UpgradeRequirement(preparementShape, 25000), new UpgradeRequirement(finalGameShape, 50000)}));
                addAll(generateInfiniteUnlocks(numEndgameUpgrades));
            }}));
            put("processors", new UpgradeTiers(new ArrayList<>() {{
                add(new TierRequirement(new UpgradeRequirement[]{new UpgradeRequirement("SuSuSuSu", 500)}));
                add(new TierRequirement(new UpgradeRequirement[]{new UpgradeRequirement("RuRu----", 600)}));
                add(new TierRequirement(new UpgradeRequirement[]{new UpgradeRequirement("CgScScCg", 3500)}));
                add(new TierRequirement(new UpgradeRequirement[]{new UpgradeRequirement("CwCrCwCr:SgSgSgSg", 25000)}));
                add(new TierRequirement(new UpgradeRequirement[]{new UpgradeRequirement("WrRgWrRg:CwCrCwCr:SgSgSgSg", 50000)}));
                add(new TierRequirement(new UpgradeRequirement[]{new UpgradeRequirement(preparementShape, 25000)}));
                add(new TierRequirement(new UpgradeRequirement[]{new UpgradeRequirement(preparementShape, 25000), new UpgradeRequirement(finalGameShape, 50000)}));
                addAll(generateInfiniteUnlocks(numEndgameUpgrades));
            }}));
            put("painting", new UpgradeTiers(new ArrayList<>() {{
                add(new TierRequirement(new UpgradeRequirement[]{new UpgradeRequirement("RbRb----", 600)}));
                add(new TierRequirement(new UpgradeRequirement[]{new UpgradeRequirement("WrWrWrWr", 3800)}));
                add(new TierRequirement(new UpgradeRequirement[]{new UpgradeRequirement("RpRpRpRp:CwCwCwCw", 6500)}));
                add(new TierRequirement(new UpgradeRequirement[]{new UpgradeRequirement("WpWpWpWp:CwCwCwCw:WpWpWpWp", 25000)}));
                add(new TierRequirement(new UpgradeRequirement[]{new UpgradeRequirement("WpWpWpWp:CwCwCwCw:WpWpWpWp:CwCwCwCw", 50000)}));
                add(new TierRequirement(new UpgradeRequirement[]{new UpgradeRequirement(preparementShape, 25000)}));
                add(new TierRequirement(new UpgradeRequirement[]{new UpgradeRequirement(preparementShape, 25000), new UpgradeRequirement(finalGameShape, 50000)}));
                addAll(generateInfiniteUnlocks(numEndgameUpgrades));
            }}));
        }};
    }

    List<TierRequirement> generateInfiniteUnlocks(int numEndgameUpgrades) {
        List<TierRequirement> temporaryArray = Arrays.asList(new TierRequirement[numEndgameUpgrades]);
        IntStream.range(0, temporaryArray.size()).forEach(index -> temporaryArray.set(index, new TierRequirement(new UpgradeRequirement[]{new UpgradeRequirement(preparementShape, 30000 + index * 10000), new UpgradeRequirement(finalGameShape, 20000 + index * 5000), new UpgradeRequirement(rocketShape, 20000 + index * 5000)}, true)));
        return temporaryArray;
    }
}
