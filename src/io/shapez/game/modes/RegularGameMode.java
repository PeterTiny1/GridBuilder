package io.shapez.game.modes;

import io.shapez.game.GameMode;
import io.shapez.game.GameRoot;
import io.shapez.game.HubGoalReward;
import io.shapez.game.LevelDefinition;

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
    final ArrayList<LevelDefinition> fullVersionLevels = generateLevelDefinitions(false);
    final ArrayList<LevelDefinition> demoVersionLevels = generateLevelDefinitions(true);

    public RegularGameMode(final GameRoot root) {
        super();
        this.root = root;
    }

    @Override
    public HashMap<String, UpgradeTiers> getUpgrades() {
        final HashMap<String, UpgradeTiers> fullVersionUpgrades = generateUpgrades(false);
        final HashMap<String, UpgradeTiers> demoVersionUpgrades = generateUpgrades(true);
        return this.root.app.restrictionMgr.getHasExtendedUpgrades() ? fullVersionUpgrades : demoVersionUpgrades;
    }

    @Override
    public ArrayList<LevelDefinition> getLevelDefinitions() {
        return this.root.app.restrictionMgr.getHasExtendedLevelsAndFreeplay() ? fullVersionLevels : demoVersionLevels;
    }

    private HashMap<String, UpgradeTiers> generateUpgrades(final boolean limitedVersion) {
        final ArrayList<Double> fixedImprovements = (ArrayList<Double>) DoubleStream.of(0.5, 0.5, 1.0, 1.0, 2.0, 1.0).boxed().collect(Collectors.toList());
        final int numEndgameUpgrades = limitedVersion ? 0 : 1000 - fixedImprovements.size() - 1;

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

    List<TierRequirement> generateInfiniteUnlocks(final int numEndgameUpgrades) {
        final List<TierRequirement> temporaryArray = Arrays.asList(new TierRequirement[numEndgameUpgrades]);
        IntStream.range(0, temporaryArray.size()).forEach(index -> temporaryArray.set(index, new TierRequirement(new UpgradeRequirement[]{new UpgradeRequirement(preparementShape, 30000 + index * 10000), new UpgradeRequirement(finalGameShape, 20000 + index * 5000), new UpgradeRequirement(rocketShape, 20000 + index * 5000)}, true)));
        return temporaryArray;
    }

    ArrayList<LevelDefinition> generateLevelDefinitions(boolean limitedVersion) {
        return new ArrayList<>() {{
            add(new LevelDefinition("CuCuCuCu", 30, HubGoalReward.RewardCutterAndTrash, false));
            add(new LevelDefinition("----CuCu", 40, HubGoalReward.NoReward, false));
            add(new LevelDefinition("RuRuRuRu", 70, HubGoalReward.RewardBalancer, false));
            add(new LevelDefinition("RuRu----", 70, HubGoalReward.RewardRotator, false));
            add(new LevelDefinition("Cu----Cu", 170, HubGoalReward.RewardTunnel, false));
            add(new LevelDefinition("Cu------", 270, HubGoalReward.RewardPainter, false));
            add(new LevelDefinition("CrCrCrCr", 300, HubGoalReward.RewardRotatorCcw, false));
            if (limitedVersion) {
                add(new LevelDefinition("CrCrCrCr", 0, HubGoalReward.RewardDemoEnd, false));
            } else {
                add(new LevelDefinition("RbRb----", 480, HubGoalReward.RewardMixer, false));
                add(new LevelDefinition("CpCpCpCp", 600, HubGoalReward.RewardMerger, false));
                add(new LevelDefinition("ScScScSc", 800, HubGoalReward.RewardStacker, false));
                add(new LevelDefinition("CgScScCg", 1000, HubGoalReward.RewardMinerChainable, false));
                add(new LevelDefinition("CbCbCbRb:CwCwCwCw", 1000, HubGoalReward.RewardBlueprints, false));
                add(new LevelDefinition("RpRpRpRp:CwCwCwCw", 3800, HubGoalReward.RewardUndergroundBeltTier2, false));
                add(new LevelDefinition("--Cg----:--Cr----", 8, HubGoalReward.RewardBeltReader, true));
                add(new LevelDefinition("SrSrSrSr:CyCyCyCy", 10000, HubGoalReward.RewardStorage, false));
                add(new LevelDefinition("SrSrSrSr:CyCyCyCy:SwSwSwSw", 6000, HubGoalReward.RewardCutterQuad, false));
                add(new LevelDefinition("CbRbRbCb:CwCwCwCw:WbWbWbWb", 20000, HubGoalReward.RewardPainterDouble, false));
                add(new LevelDefinition("Sg----Sg:CgCgCgCg:--CyCy--", 20000, HubGoalReward.RewardRotator180, false));
                add(new LevelDefinition("CpRpCp--:SwSwSwSw", 25000, HubGoalReward.RewardSplitter, false));
                add(new LevelDefinition(finalGameShape, 25000, HubGoalReward.RewardWiresPaintersAndLevers, false));
                add(new LevelDefinition("CrCwCrCw:CwCrCwCr:CrCwCrCw:CwCrCwCr", 25000, HubGoalReward.RewardFilter, false));
                add(new LevelDefinition("Cg----Cr:Cw----Cw:Sy------:Cy----Cy", 25000, HubGoalReward.RewardConstantSignal, false));
                add(new LevelDefinition("CcSyCcSy:SyCcSyCc:CcSyCcSy", 25000, HubGoalReward.RewardDisplay, false));
                add(new LevelDefinition("CcRcCcRc:RwCwRwCw:Sr--Sw--:CyCyCyCy", 25000, HubGoalReward.RewardLogicGates, false));
                add(new LevelDefinition("CrCwCrCw:CwCrCwCr:CrCwCrCw:CwCrCwCr", 25000, HubGoalReward.RewardVirtualProcessing, false));
                add(new LevelDefinition("CbCuCbCu:Sr------:--CrSrCr:CwCwCwCw", 50000, HubGoalReward.RewardFreeplay, false));
            }
        }};
    }
}
