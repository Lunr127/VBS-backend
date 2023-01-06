package whu.vbs;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import whu.vbs.Entity.CsvFile.*;
import whu.vbs.Mapper.*;
import whu.vbs.utils.PathUtils;
import whu.vbs.utils.VectorUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@SpringBootTest
public class avsTest {

    @Autowired
    AvsGrandTruthMapper avsGrandTruthMapper;

    @Autowired
    GrandTruthMapper grandTruthMapper;

    @Autowired
    VideoDescriptionVectorMapper videoDescriptionVectorMapper;

    @Autowired
    MasterShotBoundaryMapper msbMapper;

    @Autowired
    AvsQueryMapper avsQueryMapper;

    Map<String, Double> scoreMap = new HashMap<>();//（路径，得分）键值对: 形如（shot00001_1, 0.8）
    Map<String, List<Double>> pathMap = new HashMap<>();//（路径，向量）键值对: 形如（shot00001_1, [0.8,...,0.6]）
    List<String> pathList = new ArrayList<>();

    Map<String, List<MasterShotBoundary>> msbMap = new HashMap<>();


    Map<Integer, String[]> likeShotsMap = new HashMap<>();//(查询编号，路径键值对): 形如（1,shot00001_1）
    Map<Integer, String[]> notLikeShotsMap = new HashMap<>();//(查询编号，路径键值对): 形如（1,shot00001_1）


    Map<String, Set<String>> classificationMap = new HashMap<>();//(分类，路径list)键值对

    /**
     * 正反馈样本 仅V3C1数据集
     */
    void setLikeShotsMap() {
//        likeShotsMap.put(1, new String[]{"shot04804_635", "shot04804_1075", "shot00956_15", "shot00151_87", "shot07466_11", "shot04804_896", "shot01172_285", "shot00602_129", "shot00298_90", "shot06522_48", "shot06624_32", "shot00602_148", "shot02673_158", "shot03169_107", "shot01090_427", "shot00148_38", "shot00487_107", "shot03918_623", "shot03699_3", "shot04804_197", "shot00290_25", "shot02960_89", "shot04867_488", "shot00602_133", "shot03699_2", "shot07420_22", "shot02061_18", "shot00148_42", "shot06566_163", "shot01253_91", "shot06248_56", "shot01253_93", "shot00148_52", "shot05200_155", "shot02347_4", "shot04255_3", "shot01071_255", "shot00602_86", "shot04989_99", "shot04695_135", "shot01253_35", "shot06533_278", "shot04804_251", "shot05469_2", "shot02826_100", "shot04619_195", "shot04804_1238", "shot03900_146", "shot01843_96", "shot03900_226"});
//        likeShotsMap.put(2, new String[]{"shot07435_51", "shot07435_52", "shot07435_37", "shot05459_35", "shot07435_50", "shot00352_19", "shot07435_53", "shot07435_41", "shot02100_332", "shot07435_43", "shot02100_310", "shot05329_25", "shot07049_4", "shot02100_253", "shot04539_61", "shot02940_29", "shot06738_51", "shot07435_32", "shot05074_75", "shot06639_182", "shot07049_3", "shot05459_86", "shot00352_105", "shot05459_33", "shot00352_97", "shot01638_14", "shot07435_33", "shot00915_70", "shot00072_23", "shot02174_199", "shot03119_63", "shot06524_27", "shot03588_95", "shot03638_93", "shot07435_34", "shot07435_54", "shot05510_245", "shot05074_81", "shot06767_22", "shot07435_42", "shot07182_48", "shot05074_99", "shot00034_101", "shot07056_17", "shot00815_69", "shot00387_115", "shot00915_49", "shot05459_61", "shot00248_37", "shot02890_27"});
//        likeShotsMap.put(4, new String[]{"shot03805_83", "shot04551_77", "shot05543_90", "shot05291_107", "shot05291_8", "shot06196_288", "shot01544_21", "shot06954_96", "shot04820_12", "shot06196_449", "shot04551_110", "shot04966_7", "shot04551_18", "shot04551_120", "shot04551_86", "shot05756_29", "shot04551_142", "shot03312_130", "shot04551_127", "shot07001_43", "shot05427_22", "shot07381_111", "shot00318_147", "shot05824_114", "shot02160_62", "shot04551_101", "shot03751_14", "shot04551_115", "shot02446_5", "shot03231_9", "shot05995_55", "shot05543_39", "shot04423_267", "shot05543_75", "shot04423_295", "shot04551_123", "shot05995_54", "shot07155_35", "shot05995_68", "shot05995_66", "shot05543_119", "shot04777_27", "shot04777_22", "shot05785_134", "shot05543_84", "shot04423_638", "shot02723_9", "shot04423_708", "shot05291_44", "shot04091_216"});
//        likeShotsMap.put(5, new String[]{"shot05611_186", "shot02236_2", "shot01492_125", "shot05611_112", "shot06678_33", "shot05611_193", "shot05611_187", "shot05611_145", "shot02281_81", "shot04001_106", "shot05003_303", "shot05611_195", "shot05611_176", "shot06375_2", "shot05611_71", "shot05611_185", "shot05611_100", "shot06678_34", "shot02561_128", "shot05611_110", "shot02519_91", "shot05611_67", "shot05611_113", "shot01940_25", "shot05611_146", "shot05519_19", "shot05611_194", "shot05611_69", "shot00045_442", "shot05611_150", "shot06678_26", "shot05611_65", "shot02561_455", "shot05611_188", "shot05611_108", "shot06678_30", "shot05611_114", "shot06678_31", "shot05611_151", "shot04757_103", "shot02519_92", "shot06678_29", "shot05611_70", "shot02609_223", "shot05611_184", "shot04246_61", "shot02561_456", "shot06375_3", "shot06678_32", "shot01182_26"});
//        likeShotsMap.put(6, new String[]{"shot03077_146", "shot02808_148", "shot03894_38", "shot06815_52", "shot03077_143", "shot02808_142", "shot02808_141", "shot03077_67", "shot03894_61", "shot06815_89", "shot03894_39", "shot06815_83", "shot06815_108", "shot06815_107", "shot02808_130", "shot06815_141", "shot07309_237", "shot06815_88", "shot02808_85", "shot03795_47", "shot03077_138", "shot06815_86", "shot03894_35", "shot06815_157", "shot06815_85", "shot06815_84", "shot02808_147", "shot03894_9", "shot06815_149", "shot06815_97", "shot02808_128", "shot03894_8", "shot04490_73", "shot06815_130", "shot02808_72", "shot03077_66", "shot06815_144", "shot02808_117", "shot03894_124", "shot06815_118", "shot03894_67", "shot03894_70", "shot06815_148", "shot06815_158", "shot06815_6", "shot02808_110", "shot06815_102", "shot02808_145", "shot07309_242", "shot06815_122"});
//        likeShotsMap.put(10, new String[]{"shot02285_9", "shot06195_4", "shot02645_97", "shot01559_92", "shot00668_16", "shot02763_184", "shot04052_50", "shot06007_158", "shot06007_163", "shot06007_513", "shot06007_80", "shot06188_306", "shot05522_39", "shot03719_28", "shot05133_34", "shot05734_178", "shot01559_7", "shot04979_20", "shot00481_46", "shot07389_29", "shot04979_21", "shot05522_46", "shot02957_295", "shot05734_179", "shot04175_15", "shot03011_313", "shot06523_192", "shot00172_43", "shot02285_7", "shot00172_40", "shot00172_39", "shot06007_510", "shot05195_139", "shot01577_116", "shot05795_72", "shot02763_75", "shot04096_425", "shot02075_65", "shot06188_304", "shot00481_48", "shot07051_109", "shot06007_293", "shot04780_664", "shot06007_511", "shot06188_305", "shot04123_26", "shot05522_85", "shot05587_148", "shot02143_78", "shot02381_146"});

        likeShotsMap.put(1, new String[]{"shot04804_635", "shot04804_1075", "shot04804_1321", "shot00956_15", "shot00151_87", "shot07466_11", "shot04804_896", "shot01172_285", "shot04804_820", "shot01172_62", "shot04804_957", "shot04804_344", "shot00602_129", "shot00602_37", "shot00298_90", "shot04804_907", "shot01172_183", "shot06522_48", "shot06624_32", "shot00602_148", "shot04804_488", "shot03169_107", "shot04804_506", "shot01090_427", "shot00148_38", "shot00487_107", "shot04804_944", "shot03918_623", "shot03699_3", "shot04804_197", "shot00290_25", "shot02960_89", "shot04867_488", "shot00602_133", "shot03699_2", "shot07420_22", "shot07420_16", "shot02061_18", "shot00148_42", "shot00148_41", "shot03319_151", "shot06566_163", "shot01253_91", "shot03699_1", "shot00148_39", "shot06248_56", "shot04804_945", "shot01253_93", "shot00148_52", "shot04804_1379", "shot05200_155", "shot04255_3", "shot04804_821", "shot00148_43", "shot01071_255", "shot04804_715", "shot00602_86", "shot04804_974", "shot00148_45", "shot04989_99", "shot00602_117", "shot04695_135", "shot00602_134", "shot01253_35", "shot06533_278", "shot04804_251", "shot00193_305", "shot05469_2", "shot02826_100", "shot03699_4", "shot04804_595", "shot00218_88", "shot02347_30", "shot04619_195", "shot00151_89", "shot00218_91", "shot04804_643", "shot04804_1238", "shot05358_16", "shot02684_122", "shot03900_146", "shot01253_99", "shot01843_96", "shot06248_107", "shot03900_226", "shot06624_25", "shot01333_124", "shot03279_18", "shot04299_15", "shot00218_12", "shot06288_19", "shot04867_484", "shot01090_175", "shot06827_365", "shot06624_28", "shot01090_663", "shot04989_247", "shot05246_228", "shot06248_57", "shot00148_40", "shot04804_280", "shot07412_39", "shot06248_454", "shot04464_21", "shot06852_2", "shot06674_68", "shot00148_116", "shot01849_177", "shot01420_211", "shot04804_1221", "shot02347_28", "shot00602_8", "shot06611_93", "shot06611_105", "shot00218_16", "shot00193_335", "shot02061_7", "shot03918_624", "shot02347_38", "shot06248_462", "shot07257_84", "shot03279_75", "shot00602_36", "shot06624_39", "shot06288_18", "shot06869_51", "shot06325_88", "shot01498_34", "shot02061_9", "shot06808_162", "shot02770_5", "shot06611_18", "shot03817_47"});
        likeShotsMap.put(2, new String[]{"shot07435_51", "shot07435_52", "shot07435_37", "shot05459_35", "shot07435_50", "shot00352_19", "shot07435_53", "shot07435_41", "shot02100_332", "shot07435_43", "shot02100_310", "shot05329_25", "shot00750_611", "shot07049_4", "shot02100_253", "shot06608_374", "shot04539_61", "shot02940_29", "shot07435_32", "shot07448_236", "shot05074_75", "shot06639_182", "shot05459_9", "shot02100_309", "shot07049_3", "shot05459_86", "shot00352_105", "shot05459_33", "shot00750_339", "shot01638_14", "shot05459_26", "shot07435_33", "shot00915_70", "shot00072_23", "shot06524_27", "shot07049_5", "shot03588_95", "shot03638_93", "shot00352_16", "shot07435_34", "shot07435_54", "shot05510_245", "shot05074_81", "shot06767_22", "shot07435_42", "shot07182_48", "shot05074_99", "shot00034_101", "shot04539_60", "shot07056_17", "shot00815_69", "shot00387_115", "shot00915_49"});
        likeShotsMap.put(4, new String[]{"shot03805_83", "shot04551_77", "shot05543_90", "shot05291_107", "shot05291_8", "shot06196_288", "shot01544_21", "shot06954_96", "shot04820_12", "shot06196_449", "shot04551_110", "shot04966_7", "shot04551_18", "shot04551_120", "shot04551_86", "shot05756_29", "shot04762_22", "shot06196_113", "shot04551_142", "shot03312_130", "shot04551_127", "shot04551_85", "shot04551_79", "shot07001_43", "shot05427_22", "shot07381_111", "shot04551_82", "shot05427_21", "shot00318_147", "shot05427_20", "shot05824_114", "shot02160_62", "shot04551_101", "shot03751_14", "shot04551_115", "shot02446_5", "shot03231_9", "shot04551_81", "shot04551_108", "shot05995_55", "shot03785_19", "shot05543_39", "shot04551_119", "shot04423_267", "shot04551_112", "shot04594_257", "shot05543_75", "shot04423_295", "shot04423_300", "shot04932_8", "shot04551_125", "shot04551_123", "shot05995_54", "shot07155_35", "shot05995_68", "shot05995_66", "shot04551_80", "shot05543_119", "shot04551_121", "shot04551_114", "shot04762_23", "shot04551_7", "shot04777_27", "shot04777_22", "shot05785_134", "shot04551_88", "shot05543_84", "shot00110_7", "shot04423_638", "shot05995_57", "shot02723_9", "shot05995_59", "shot04551_19", "shot04551_109", "shot04551_129", "shot04423_228", "shot04551_135", "shot07155_31", "shot04551_136", "shot04423_708", "shot05291_44", "shot04091_216", "shot04551_78", "shot00363_79", "shot04551_87", "shot02731_48", "shot04551_118", "shot00363_78", "shot04551_83", "shot04551_8", "shot04551_10", "shot00537_160", "shot03312_131", "shot04551_132", "shot01847_246", "shot04551_111", "shot06577_25", "shot02923_16", "shot04091_164", "shot01561_184", "shot04594_405", "shot04551_97"});
        likeShotsMap.put(5, new String[]{"shot05611_186", "shot02236_2", "shot01492_125", "shot05611_112", "shot06678_33", "shot05611_193", "shot05611_187", "shot05611_145", "shot02281_81", "shot04001_106", "shot05369_184", "shot05611_176", "shot07314_95", "shot06375_2", "shot05611_185", "shot06678_34", "shot02561_128", "shot05611_110", "shot02519_91", "shot01940_25", "shot05519_19", "shot05611_194", "shot05611_56", "shot00045_442", "shot06678_26", "shot01877_11", "shot02561_455", "shot02281_84", "shot00443_48", "shot01877_17", "shot05611_48", "shot06678_30", "shot05369_185", "shot06678_31", "shot04757_103", "shot02519_92", "shot06678_29", "shot01877_3", "shot02236_1", "shot07314_90", "shot05611_46", "shot06944_123", "shot02609_223", "shot04246_61", "shot02561_456", "shot07244_188", "shot01877_5", "shot02030_61", "shot01877_4", "shot06944_579", "shot07142_12", "shot00443_47", "shot01877_6", "shot06944_83", "shot05611_178", "shot01877_9", "shot06375_3", "shot06944_125", "shot07142_10", "shot04251_7", "shot06678_32", "shot01877_18", "shot07142_11", "shot06944_127", "shot04246_60", "shot01182_26", "shot04757_102", "shot05611_111", "shot04246_62", "shot05611_66", "shot01877_19", "shot01877_12", "shot04001_107", "shot06678_28", "shot04246_63", "shot00443_50", "shot06678_25", "shot01877_13"});
        likeShotsMap.put(6, new String[]{"shot03077_146", "shot02808_148", "shot03894_38", "shot06815_52", "shot03077_143", "shot03077_144", "shot03077_147", "shot02808_142", "shot02808_141", "shot03077_67", "shot03894_61", "shot06815_89", "shot03894_39", "shot06815_83", "shot06815_108", "shot06815_107", "shot02808_130", "shot06815_141", "shot07309_237", "shot06815_88", "shot02808_85", "shot03795_47", "shot03077_138", "shot06815_86", "shot03894_35", "shot06815_157", "shot03077_137", "shot06815_85", "shot06815_84", "shot02808_147", "shot03077_68", "shot03894_9", "shot06815_149", "shot06815_97", "shot02808_128", "shot03894_8", "shot06815_116", "shot04490_73", "shot06815_130", "shot02808_72", "shot06815_156", "shot03894_10", "shot03077_66", "shot06815_144", "shot02808_117", "shot03894_124", "shot06815_118", "shot03894_67", "shot03894_70", "shot06815_148", "shot06815_158", "shot03077_45", "shot06815_51", "shot06815_6", "shot02808_110", "shot06815_102", "shot06815_32", "shot02808_145", "shot05605_350", "shot07309_242", "shot06815_164", "shot03894_55", "shot02932_619", "shot03894_26", "shot06815_122", "shot06815_136", "shot06815_137", "shot03077_63", "shot03077_58", "shot06815_113", "shot03077_46", "shot06815_114", "shot03894_23", "shot03077_130", "shot03894_101", "shot06815_117", "shot03894_21", "shot02932_618", "shot06815_94", "shot03077_44", "shot06815_131", "shot02808_132", "shot07309_241", "shot03894_27", "shot03894_63", "shot03894_24", "shot06815_31", "shot03894_52", "shot06815_139", "shot06815_140", "shot03894_36", "shot03077_52", "shot03894_51", "shot06815_143", "shot03894_100", "shot03077_43", "shot03077_60", "shot02027_83", "shot02027_45", "shot06815_105", "shot03894_88", "shot06815_104", "shot07309_228", "shot06815_80", "shot06815_159", "shot02808_89"});
        likeShotsMap.put(10, new String[]{"shot02285_9", "shot06195_4", "shot02645_97", "shot01559_92", "shot00668_16", "shot02763_184", "shot04052_50", "shot06007_158", "shot06007_163", "shot06007_513", "shot06007_80", "shot06188_306", "shot05522_39", "shot03719_28", "shot02934_87", "shot05133_34", "shot05734_178", "shot01559_7", "shot04979_20", "shot00481_46", "shot07389_29", "shot04979_21", "shot05522_46", "shot02957_295", "shot05734_179", "shot04175_15", "shot03011_313", "shot06523_192", "shot00172_43", "shot02285_7", "shot00172_40", "shot00172_39", "shot06007_510", "shot05195_139", "shot01577_116", "shot05795_72", "shot03913_30", "shot02763_75", "shot02763_76", "shot04096_425", "shot02075_65", "shot03204_51", "shot00317_271", "shot06188_304", "shot00481_48", "shot06188_135", "shot06007_293", "shot04780_664", "shot03719_111", "shot06007_511", "shot06188_305", "shot03921_29", "shot04123_26", "shot05522_85", "shot01657_108", "shot06188_136", "shot05587_148", "shot06188_471", "shot05522_72", "shot02143_78", "shot02381_146", "shot06868_94", "shot01366_35", "shot07389_412", "shot06007_118", "shot06007_111", "shot05297_75", "shot03591_57", "shot06835_103", "shot02763_83", "shot03203_89", "shot05047_123", "shot06852_59", "shot00194_19", "shot03825_94", "shot01577_119", "shot02763_84", "shot05047_46", "shot02645_87", "shot00172_51", "shot06007_167", "shot00296_25", "shot04123_28", "shot06007_169", "shot01577_117", "shot03138_174", "shot02381_159", "shot05522_80", "shot01468_74", "shot03719_113", "shot02381_163", "shot06523_118", "shot04123_27", "shot06067_911", "shot03675_40", "shot06007_195", "shot02381_126", "shot06007_196", "shot06452_82", "shot06007_153", "shot00528_190", "shot02934_88", "shot00296_27", "shot03415_13", "shot06007_82", "shot02381_39", "shot03011_315", "shot04780_665", "shot06188_113", "shot06007_112", "shot02381_151", "shot03011_317", "shot04096_239", "shot01778_93", "shot06809_778", "shot06217_19", "shot05781_76", "shot03724_168", "shot02983_20", "shot04096_257"});

//        likeShotsMap.put(1, new String[]{"shot00148_38", "shot00148_39", "shot00148_40", "shot00148_41", "shot04804_344", "shot06611_93", "shot00148_42", "shot00148_43", "shot00148_45", "shot02770_5", "shot04804_635", "shot03900_146", "shot00602_8"});
//        likeShotsMap.put(2, new String[]{"shot06767_22", "shot07049_4", "shot07049_3", "shot07049_5", "shot07435_41", "shot00072_23", "shot00915_49", "shot07435_34", "shot07435_33", "shot07435_32", "shot07435_42", "shot07435_37"});
//        likeShotsMap.put(4, new String[]{"shot04777_27", "shot04551_18", "shot04551_19", "shot04966_7", "shot05543_39", "shot04551_7", "shot04551_10", "shot04423_295", "shot04551_8", "shot04777_22"});
//        likeShotsMap.put(5, new String[]{"shot04246_62", "shot02236_2", "shot04246_61", "shot04246_63", "shot06375_2", "shot06375_3", "shot04246_60", "shot02236_1", "shot05611_46", "shot06944_123", "shot05611_56", "shot05611_48"});
//        likeShotsMap.put(6, new String[]{"shot03894_8", "shot06815_83", "shot06815_84", "shot06815_80", "shot06815_52", "shot03894_63", "shot06815_97", "shot06815_31", "shot02808_85", "shot06815_32", "shot03894_61", "shot03894_9", "shot03894_67", "shot03894_23", "shot03894_24", "shot03894_21", "shot03894_27", "shot03077_58", "shot03894_26", "shot06815_94", "shot06815_51", "shot06815_89", "shot06815_85", "shot03894_52", "shot06815_86", "shot03077_60", "shot06815_88", "shot03894_51", "shot02808_72", "shot03077_63", "shot03894_35", "shot03894_10", "shot03894_55", "shot03077_66", "shot03894_38", "shot03077_67", "shot03894_39", "shot03077_68", "shot03894_36"});
//        likeShotsMap.put(10, new String[]{"shot06007_80", "shot06007_118", "shot06007_82", "shot02763_75", "shot02763_76", "shot04123_26", "shot06523_118", "shot03719_28", "shot04123_28", "shot04123_27", "shot05522_39", "shot06007_112", "shot06007_111"});
//
    }


    /**
     * 负反馈样本 仅V3C1数据集
     */
    void setNotLikeShotsMap() {
//        notLikeShotsMap.put(1, new String[]{"shot04804_1166", "shot02684_21", "shot04804_428", "shot04892_80", "shot04804_773", "shot05511_5", "shot02347_51", "shot00654_364", "shot05511_4", "shot04804_744", "shot02684_46", "shot01967_4", "shot01404_383", "shot05368_95", "shot01498_11", "shot02347_50", "shot07466_39", "shot02426_40", "shot02684_115", "shot02347_23", "shot01617_145", "shot01386_104", "shot01880_20", "shot03626_3", "shot06248_323", "shot06808_910", "shot07043_90", "shot06674_68", "shot03319_668", "shot03319_716", "shot02347_16", "shot05511_8", "shot03001_165", "shot04804_1328", "shot06077_4", "shot01292_229", "shot05368_123", "shot01951_54", "shot03319_152", "shot02347_2", "shot05368_121", "shot00058_76", "shot02347_75", "shot01951_52", "shot02684_89", "shot06248_336", "shot06248_441", "shot07265_30", "shot06547_90", "shot05703_31"});
//        notLikeShotsMap.put(2, new String[]{"shot05550_32", "shot07054_35", "shot01883_174", "shot06121_37", "shot04429_46", "shot06121_53", "shot05507_51", "shot05507_50", "shot06311_48", "shot06614_10", "shot04583_71", "shot02351_204", "shot05707_59", "shot00487_81", "shot00750_611", "shot00256_79", "shot00352_119", "shot03916_130", "shot06121_36", "shot03263_100", "shot02876_100", "shot05071_44", "shot04120_2", "shot02444_13", "shot02351_303", "shot05115_55", "shot06121_173", "shot06771_6", "shot07321_46", "shot01904_47", "shot06034_259", "shot03055_248", "shot05459_87", "shot03082_83", "shot03415_89", "shot00320_26", "shot01477_20", "shot05371_96", "shot04841_11", "shot02412_86", "shot04539_48", "shot02168_8", "shot03394_6", "shot01423_75", "shot01477_77", "shot06103_17", "shot06624_22", "shot06771_122", "shot02100_106", "shot03919_164"});
//        notLikeShotsMap.put(4, new String[]{"shot02511_16", "shot07049_6", "shot02928_63", "shot03916_212", "shot04447_50", "shot00535_65", "shot03642_438", "shot06004_67", "shot07148_56", "shot02497_306", "shot03858_20", "shot01782_82", "shot06325_283", "shot07148_61", "shot02697_208", "shot02286_79", "shot01278_126", "shot00087_10", "shot03037_47", "shot04752_92", "shot05509_301", "shot00110_20", "shot02845_58", "shot03805_107", "shot04036_96", "shot01144_11", "shot02846_38", "shot01416_85", "shot04551_95", "shot05260_67", "shot06456_12", "shot03087_42", "shot03982_44", "shot04423_270", "shot02286_55", "shot01144_219", "shot05543_45", "shot03007_94", "shot03055_126", "shot05111_28", "shot05614_86", "shot03536_159", "shot02572_87", "shot07056_66", "shot04765_141", "shot07373_12", "shot00369_360", "shot01145_80", "shot01395_45", "shot01445_120"});
//        notLikeShotsMap.put(5, new String[]{"shot05003_317", "shot05062_133", "shot00747_12", "shot07314_94", "shot00443_46", "shot00638_127", "shot07314_93", "shot02222_59", "shot00443_49", "shot02340_271", "shot00443_48", "shot01670_238", "shot05611_108", "shot05611_48", "shot05003_302", "shot05611_181", "shot03471_39", "shot05630_139", "shot07314_92", "shot05611_147", "shot04474_157", "shot00268_106", "shot00443_47", "shot03837_1", "shot05630_157", "shot06944_125", "shot03922_28", "shot03922_29", "shot02340_272", "shot01670_237", "shot06479_714", "shot03922_6", "shot05611_42", "shot04870_79", "shot00638_84", "shot03534_83", "shot00747_11", "shot06108_96", "shot02340_268", "shot06949_561", "shot05369_182", "shot06949_440", "shot03787_7", "shot02340_269", "shot01506_55", "shot06949_499", "shot05630_155", "shot07411_318", "shot06108_93", "shot01670_226"});
//        notLikeShotsMap.put(6, new String[]{"shot04862_139", "shot03702_100", "shot06841_84", "shot05271_545", "shot06830_268", "shot06599_49", "shot01223_176", "shot03795_22", "shot02658_41", "shot06830_54", "shot04583_249", "shot07259_28", "shot06830_186", "shot01170_11", "shot06462_213", "shot04127_14", "shot02958_184", "shot03821_9", "shot04385_116", "shot04585_17", "shot06101_369", "shot06443_108", "shot05271_542", "shot01317_60", "shot04385_127", "shot04385_118", "shot04314_12", "shot02932_617", "shot04897_12", "shot07409_15", "shot07276_19", "shot00967_59", "shot01090_175", "shot00779_175", "shot04897_26", "shot06830_251", "shot03011_459", "shot00779_178", "shot06462_87", "shot01927_5", "shot01031_32", "shot00340_7", "shot05421_15", "shot06140_173", "shot00559_105", "shot00669_63", "shot04897_84", "shot01317_40", "shot01512_92", "shot01170_61"});
//        notLikeShotsMap.put(10, new String[]{"shot04749_206", "shot03011_41", "shot05734_179", "shot01094_33", "shot01094_46", "shot01272_4", "shot01094_32", "shot07252_122", "shot00172_15", "shot03921_31", "shot02529_43", "shot05481_57", "shot05727_72", "shot05070_17", "shot03517_45", "shot04178_40", "shot03204_51", "shot03011_43", "shot06868_16", "shot04852_489", "shot01094_45", "shot04003_38", "shot06007_139", "shot02763_183", "shot07236_88", "shot07192_67", "shot00891_20", "shot05734_177", "shot00355_117", "shot00547_24", "shot00670_8", "shot05794_151", "shot03290_46", "shot01306_33", "shot00172_13", "shot04888_289", "shot05276_14", "shot05794_149", "shot06363_135", "shot05539_48", "shot00155_53", "shot02534_38", "shot05794_147", "shot04780_619", "shot00155_54", "shot04069_169", "shot05451_68", "shot04749_203", "shot04574_68", "shot05513_33"});

        notLikeShotsMap.put(1, new String[]{"shot02024_72", "shot02684_21", "shot04804_903", "shot06037_108", "shot04892_80", "shot04804_773", "shot05511_5", "shot00654_364", "shot05511_4", "shot04804_744", "shot02684_46", "shot05368_95", "shot07466_39", "shot02426_40", "shot02347_23", "shot01617_145", "shot01386_104", "shot01880_20", "shot06248_323", "shot01172_163", "shot03319_668", "shot03319_716", "shot02347_16", "shot05511_8", "shot04804_1328", "shot06077_4"});
        notLikeShotsMap.put(2, new String[]{"shot05550_32", "shot01883_174", "shot06121_37", "shot04429_46", "shot00842_255", "shot03119_67", "shot06121_53", "shot05507_51", "shot06311_48", "shot06614_10", "shot04583_71", "shot02351_204", "shot00487_81", "shot00256_79", "shot00352_119", "shot03916_130", "shot06121_36", "shot02876_100", "shot05071_44", "shot02890_19", "shot04120_2", "shot02444_13", "shot02351_303", "shot05115_55", "shot06121_173", "shot06771_6", "shot07321_46", "shot04869_64", "shot01904_47", "shot06034_259", "shot03055_248", "shot05459_87", "shot03082_83", "shot05742_13", "shot03415_89", "shot04175_92", "shot01944_125", "shot05459_76", "shot06121_78", "shot02657_247", "shot00487_83", "shot05550_33", "shot00320_26", "shot01477_20", "shot05371_96", "shot04841_11", "shot04539_48", "shot03905_37", "shot03905_208", "shot02168_8", "shot03394_6", "shot01423_75", "shot02668_32", "shot01477_77", "shot06103_17", "shot06624_22", "shot06771_122", "shot03919_164", "shot02876_127", "shot01674_78", "shot02232_39", "shot03055_224", "shot02168_143", "shot06121_74", "shot04044_55", "shot03777_9", "shot04337_37", "shot06681_64", "shot00387_77", "shot05459_77", "shot03905_175", "shot02030_102", "shot02154_392", "shot02501_67"});
        notLikeShotsMap.put(4, new String[]{"shot02511_16", "shot07049_6", "shot02511_17", "shot02928_63", "shot03916_212", "shot04447_50", "shot00535_65", "shot05956_144", "shot03642_438", "shot06004_67", "shot07148_56", "shot06140_202", "shot02497_306", "shot03858_20", "shot01782_82", "shot06325_283", "shot03328_187", "shot07148_61", "shot01244_21", "shot00087_10", "shot03037_47", "shot04752_92", "shot05509_301", "shot02845_58", "shot04036_96", "shot01144_11", "shot01416_85", "shot04551_95", "shot06523_100", "shot01616_93", "shot06456_12", "shot03087_42", "shot03982_44", "shot04423_270", "shot01144_219", "shot05543_45", "shot03007_94", "shot03055_126", "shot05614_86", "shot03536_159", "shot02953_868", "shot07056_66", "shot07373_12", "shot05871_151", "shot04136_319", "shot01145_80", "shot07373_11", "shot01395_45", "shot01445_120", "shot03910_32", "shot01221_132", "shot00204_36", "shot07080_140", "shot01113_18", "shot01840_54", "shot04779_17", "shot06255_42", "shot01317_65", "shot07044_86", "shot05494_4", "shot02278_115", "shot02860_24", "shot06413_13", "shot05140_227", "shot06378_101", "shot06995_14", "shot03123_60", "shot02808_96"});
        notLikeShotsMap.put(5, new String[]{"shot05003_317", "shot01877_16", "shot00747_12", "shot07314_94", "shot05611_71", "shot00443_46", "shot00638_127", "shot07314_93", "shot03922_31", "shot05611_146", "shot02222_59", "shot01814_566", "shot02340_271", "shot02340_270", "shot01670_238", "shot00571_47", "shot05611_114", "shot03471_39", "shot05630_139", "shot07314_92", "shot05611_147", "shot05611_155", "shot04474_157", "shot03922_30", "shot00268_106", "shot05611_115", "shot05611_107", "shot05630_157", "shot04889_172", "shot06108_95", "shot01814_562", "shot03922_28", "shot00045_331", "shot02340_272", "shot01670_237", "shot05611_152", "shot06479_714", "shot00212_11", "shot03900_412", "shot05611_180", "shot03922_6", "shot01814_560", "shot00638_84", "shot03534_83", "shot06108_96"});
        notLikeShotsMap.put(6, new String[]{"shot04862_139", "shot03702_100", "shot06841_84", "shot01146_115", "shot05271_545", "shot06830_268", "shot06599_49", "shot01223_176", "shot03795_22", "shot02658_41", "shot06830_54", "shot04583_249", "shot07259_28", "shot06830_186", "shot01170_11", "shot06462_213", "shot04127_14", "shot00287_30", "shot02958_184", "shot03821_9", "shot04385_116", "shot04585_17", "shot06101_369", "shot06443_108", "shot05271_542", "shot01317_60", "shot04385_127", "shot04385_118", "shot04314_12", "shot02932_617", "shot06815_98", "shot05271_544", "shot07409_15", "shot07276_19", "shot00967_59", "shot01090_175", "shot04897_26", "shot01424_238", "shot06830_251", "shot03011_459", "shot06462_87", "shot01927_5", "shot05271_543", "shot01031_32", "shot00340_7", "shot05421_15", "shot06140_173", "shot00559_105", "shot03894_13", "shot00669_63", "shot04897_84", "shot01317_40", "shot01512_92", "shot01170_61", "shot02271_3", "shot01146_19", "shot06101_368", "shot01170_45", "shot02371_7", "shot04385_117", "shot06140_128", "shot06830_254", "shot03334_117", "shot06232_14", "shot02030_210", "shot03448_115", "shot00475_99", "shot01895_108", "shot04459_92", "shot03394_22", "shot06830_91", "shot04872_28", "shot05936_100", "shot00164_45", "shot05673_140", "shot06815_29", "shot05850_178", "shot06140_299", "shot07216_112", "shot03942_158", "shot00182_79", "shot00904_186", "shot01170_50", "shot06007_191", "shot03027_89", "shot03055_13", "shot06727_78", "shot05910_246", "shot00347_86", "shot06830_192", "shot00347_88", "shot05141_406", "shot02082_94", "shot01300_79", "shot06092_73", "shot06203_89", "shot06140_393", "shot05320_85", "shot02991_180", "shot02127_101", "shot03055_24", "shot06854_7", "shot01170_60", "shot00967_58", "shot00601_3", "shot04749_171", "shot07343_404", "shot06388_21", "shot02288_73", "shot03933_11", "shot04717_26", "shot06677_14", "shot04039_134", "shot00779_176", "shot06388_20", "shot06388_19", "shot05509_882", "shot03949_8", "shot02956_57"});
        notLikeShotsMap.put(10, new String[]{"shot01094_33", "shot06007_87", "shot01094_46", "shot01272_4", "shot01094_32", "shot07252_122", "shot00172_15", "shot02529_43", "shot05727_72", "shot05070_17", "shot04178_40", "shot06007_206", "shot06868_16", "shot04767_85", "shot04852_489", "shot00221_26", "shot01094_45", "shot04003_38", "shot06067_910", "shot01355_95", "shot07236_88", "shot07192_67", "shot03517_47", "shot00891_20", "shot05385_77", "shot05734_177", "shot05810_76", "shot06007_307", "shot07252_121", "shot06406_138", "shot00038_28", "shot01531_31", "shot06568_38", "shot06770_51", "shot06868_3", "shot06007_96", "shot00277_80", "shot00355_117", "shot00547_24", "shot05794_151", "shot03290_46", "shot04881_114", "shot04888_289", "shot05276_14", "shot03997_100", "shot05794_149", "shot06363_135", "shot05539_48"});
    }


    int topK = 1000;
    int countAllTeam;   //avs测评指标的C值


    /**
     * 测试函数无用 别理
     */
    @Test
    void saveAvs() {
        String tsvPath = "D:\\Download\\VBSDataset\\vbs22\\AVSans.csv";
        List<String> file = FileUtil.readLines(tsvPath, StandardCharsets.UTF_8);
        System.out.println(file.toString());
        for (int i = 1; i < file.size(); i++) {
            String[] split = file.get(i).split(",");
            AvsGrandTruth avsGrandTruth = new AvsGrandTruth();
            avsGrandTruth.setQueryId(Integer.valueOf(split[0]));
            avsGrandTruth.setVideoId(split[1]);
            avsGrandTruth.setStartTime(split[2]);
            avsGrandTruth.setEndTime(split[3]);
            avsGrandTruthMapper.insert(avsGrandTruth);
        }
    }


    /**
     * 测试函数无用 别理
     */
    @Test
    void avsGTTest() {
        String shot = "shot00136_217";
        String videoId = shot.substring(4, 9);
        int shotId = Integer.parseInt(shot.substring(10)) - 1;
        System.out.println(shotId);
        Map<String, Object> selectByVideoIdMap = new HashMap<>();
        selectByVideoIdMap.put("video_id", videoId);
        List<MasterShotBoundary> msbByVideoId = msbMapper.selectByMap(selectByVideoIdMap);

        double startTime = Double.parseDouble(msbByVideoId.get(shotId).getStartTime()) * 1000;
        double endTime = Double.parseDouble(msbByVideoId.get(shotId).getEndTime()) * 1000;


        int queryId = 1;
        Map<String, Object> selectByQueryMap = new HashMap<>();
        selectByQueryMap.put("query_id", queryId);
        List<AvsGrandTruth> avsGrandTruths = avsGrandTruthMapper.selectByMap(selectByQueryMap);

        int count = 0;

        for (int i = avsGrandTruths.size() - 1; i >= 0; i--) {
            double gtStartTime = Double.parseDouble(avsGrandTruths.get(i).getStartTime());
            double gtEndTime = Double.parseDouble(avsGrandTruths.get(i).getEndTime());
            if (Objects.equals(avsGrandTruths.get(i).getVideoId(), videoId) && gtStartTime > startTime - 10000 && gtEndTime < endTime + 10000) {
                count++;
                System.out.println(avsGrandTruths.get(i));
                avsGrandTruths.remove(i);
            }
        }

        System.out.println(count);
    }


    /**
     * 主要的测试函数在这里
     */
    @Test
    void initialSortingTest() {

        // 初始化正负反馈样本键值对
        setLikeShotsMap();
        setNotLikeShotsMap();


        int query;  // 查询编号
        int count;

        // 获得msb镜头边界list
        List<MasterShotBoundary> masterShotBoundaryList = msbMapper.selectList(null);

        // msb相关初始化 收起就行 不用多管
        int breakCount = 0;
        for (int index = 0; index < masterShotBoundaryList.size(); index++) {
            for (int i = 1; i <= 7475; i++) {
                String videoId = "";
                if (i < 10) {
                    videoId = "0000" + i;
                } else if (i < 100) {
                    videoId = "000" + i;
                } else if (i < 1000) {
                    videoId = "00" + i;
                } else if (i < 10000) {
                    videoId = "0" + i;
                } else if (i < 100000) {
                    videoId = String.valueOf(i);
                }

                List<MasterShotBoundary> msbByVideoId = new ArrayList<>();
                while (Objects.equals(masterShotBoundaryList.get(index).getVideoId(), videoId)) {
                    msbByVideoId.add(masterShotBoundaryList.get(index));
                    index++;
                    if (index == 2508108) {
                        break;
                    }
                }
                msbMap.put(videoId, msbByVideoId);
                breakCount = i;
            }
            if (breakCount == 7475) {
                break;
            }
        }

        // yolo相关 先注释掉
        setClassificationMap();

        // 结果输出excel需要的
        List<List<String>> rows = CollUtil.newArrayList();

        // 1-10查询
        for (query = 1; query <= 10; query++) {

            //跳过没有gt的查询
            if (query == 3 || query == 8 || query == 7 || query == 9) {
                continue;
            }

            // 每次查询重新初始化
            scoreMap = new HashMap<>();
            pathMap = new HashMap<>();
            pathList = new ArrayList<>();

            // 读取faiss产生的top10000文件
            CsvReader reader = CsvUtil.getReader();
            String csvPath = "D:\\Download\\VBSDataset\\blip_v3c1_top10000\\" + query + ".csv";
            List<GrandTruthResult> result = reader.read(ResourceUtil.getUtf8Reader(csvPath), GrandTruthResult.class);

            // 获取数据库中查询语句对应的特征向量
            AvsQuery queryVector = avsQueryMapper.selectById(query);
            List<Double> queryVectorList = VectorUtil.strToDouble(queryVector.getVector(), 1);

            // gt处理 收起就好 不用管
            for (GrandTruthResult grandTruthResult : result) {
                pathList.add(PathUtils.handleToGTPath(grandTruthResult.getShot()));

                //特征向量，并转成浮点数组
                List<Double> vectorDoubleList = VectorUtil.strToDouble(grandTruthResult.getVector(), 1);

                //计算查询文本和图片的相似度得分
                Double cosineSimilarity = VectorUtil.getCosineSimilarity(queryVectorList, vectorDoubleList);

                //建立（路径，得分）的键值对
                scoreMap.put(PathUtils.handleToGTPath(grandTruthResult.getShot()), cosineSimilarity);

                //建立（路径，概率）的键值对
                pathMap.put(PathUtils.handleToGTPath(grandTruthResult.getShot()), vectorDoubleList);
            }

            // 得分归一化
            VectorUtil.mapNormalization(scoreMap);

            // 将（路径，得分）的键值对按得分降序
            Map<String, Double> sortMap = VectorUtil.sortMapByValues(scoreMap);


            // 这里做的是一个个算法单独测 每次打开一个算法的注释即可
            // 如果需要同时测，一个算法跑完，需要还原scoreMap，再跑下一个算法，例如
            // Map<String, Double> tmpMap = sortMap;
            // 算法测试
            // sortMap = tmpMap;


            // Rocchio重排 Rocchio这里暂时有点问题 反馈样本各选了前5个先 （bug已修复 反馈各50个）
//            List<String> likeShots = Arrays.asList(likeShotsMap.get(query));
//            for (String shot : likeShots) {
//                Rocchio(shot, 0);   //Rocchio 0表示正反馈
//            }
//
//            List<String> notLikeShots = Arrays.asList(notLikeShotsMap.get(query));
//            for (String shot : notLikeShots) {
//                Rocchio(shot, 1);   //Rocchio 1表示负反馈
//            }


//            // qmr重排
//            List<String> likeShots = Arrays.asList(likeShotsMap.get(query)).subList(0, 5);
//            for (String shot : likeShots) {
//                qmr(shot);  //qmr
//                Rocchio(shot, 0);   //Rocchio
//            }


            // qir重排 也暂时有点问题 反馈样本各选了前5个先 （bug已修复 反馈各50个）
            // 使用qir，需要到qir方法里，修改qir.py文件路径
//            List<String> likeShots = Arrays.asList(likeShotsMap.get(query)).subList(0, 25);
//            List<String> notLikeShots = Arrays.asList(notLikeShotsMap.get(query)).subList(0, 25);
//            qir(likeShots, 0);
//            qir(notLikeShots, 1);


            // cvts重排
//            List<String> likeShots = Arrays.asList(likeShotsMap.get(query));
//            cvts(likeShots, queryVectorList);


//            // 重排后得分归一化
//            VectorUtil.mapNormalization(scoreMap);
//            // 将（路径，得分）的键值对按得分降序
//            sortMap = VectorUtil.sortMapByValues(scoreMap);


            List<String> urlList = new ArrayList<>();//查询结果的路径
            //将路径存入urlList
            savePathToUrlList(urlList, sortMap);


            System.out.println("--------------Query " + query + "------------------");

            // 结果输出excel需要的
            List<String> row = CollUtil.newArrayList();

            for (int i = 1; i <= 20; i++) {
                // 12.30 结果中 topK k=10，20，...，200
                List<String> topList = urlList.subList(0, i * 10);

                double score = getScore(topList, query);// 获得topList的avs得分
                double score2 = (double) Math.round(score * 100) / 100;// 保留两位小数
                row.add(String.valueOf(score2));
            }

            for (int i = 3; i <= 20; i++) {
                // 12.30 结果中 topK k=300，400，...，2000
                List<String> topList = urlList.subList(0, i * 100);

                double score = getScore(topList, query);// 获得topList的avs得分
                double score2 = (double) Math.round(score * 100) / 100;// 保留两位小数
                row.add(String.valueOf(score2));
            }


            rows.add(row);// 结果输出excel需要的

            System.out.println("------------------------------------------------");
            System.out.println();


            // 下面的注释可以先不看 之前测试的各个算法的正负反馈 上面已重新标注 yolo和label之后再说
//            // yolo
//            List<String> classificationList = new ArrayList<>();//符合分类结果的路径
//            List<String> notClassificationList = new ArrayList<>();//不符合分类结果的路径
//
//            String[] queryWord = queryVector.getQuery().split(" ");
//            for (String shot : urlList) {
//                int flag = 0;
//                for (String word : queryWord) {
//                    if (Objects.equals(word, "someone")){
//                        word = "person";
//                    }
//                    if (Objects.equals(word, "persons")){
//                        word = "person";
//                    }
//                    if (Objects.equals(word, "vehicle")){
//                        word = "car";
//                    }
//                    Set<String> shotSet = classificationMap.get(word);
//                    if ((shotSet != null) && shotSet.contains(shot)) {
//                        classificationList.add(shot);
//                        flag = 1;
//                        break;
//                    }
//                }
//                if (flag == 0){
//                    notClassificationList.add(shot);
//                }
//            }
//
//            List<String> topList = new ArrayList<>();
//            if (classificationList.size() >= 1000) {
//                topList = classificationList.subList(0, topK);
//            } else {
//                topList.addAll(classificationList);
//                topList.addAll(notClassificationList.subList(0, topK - classificationList.size()));
//            }


//            count = getGTMatch(topList, query);
//
//            System.out.println("top K = " + topK);
//            System.out.println("predict true count = " + count);
//            System.out.println("precision@" + topK + " = " + ((double) count / topK));
//            System.out.println();


//            String[] likeShots = likeShotsMap.get(query);
//            List<String> likeShots = Arrays.asList(likeShotsMap.get(query)).subList(0, 5);
//            for (String shot : likeShots) {
//                qmr(shot);  //qmr
//                Rocchio(shot, 0);   //Rocchio
//            }

//            List<String> notLikeShots = Arrays.asList(notLikeShotsMap.get(query)).subList(0, 5);
//            for (String shot : notLikeShots) {
//                Rocchio(shot, 1);   //Rocchio
//            }

//            List<String> likeShots = Arrays.asList(likeShotsMap.get(query)).subList(0, 5);
//            List<String> notLikeShots = Arrays.asList(notLikeShotsMap.get(query)).subList(0, 5);
//            qir(likeShots, 0);
//            qir(notLikeShots, 1);


//            String str = getLabelsByQuery(queryVector.getQuery());
//            List<String> labels = new ArrayList<>(Arrays.asList(str.substring(1, str.length() - 1).split("'")));
//            labels.removeIf(s -> s.length() < 2);
//            for (String label : labels) {
//                List<Double> labelVectorList = getTextVector(label);
//
//                for (String key : scoreMap.keySet()) {
//                    List<Double> vectorDoubleList = pathMap.get(key);
//
//                    //计算查询文本和图片的相似度得分
//                    double cosineSimilarity = VectorUtil.getCosineSimilarity(labelVectorList, vectorDoubleList);
//
//                    Double oldValue = scoreMap.get(key);
//
//                    //更新得分
//                    scoreMap.replace(key, oldValue + cosineSimilarity * 0.1);
//                }
//            }

//            qprp();

//            List<String> likeShots = Arrays.asList(likeShotsMap.get(query));
//            cvts(likeShots, queryVectorList);
//
//            VectorUtil.mapNormalization(scoreMap);
//            //将（路径，得分）的键值对按得分降序
//            Map<String, Double> reRankSortMap = VectorUtil.sortMapByValues(scoreMap);
//
//
//            //将路径存入urlList
//            List<String> reRankUrlList = new ArrayList<>();//查询结果的路径
//            savePathToUrlList(reRankUrlList, reRankSortMap);
//            List<String> reRankTopList = reRankUrlList.subList(0, topK);
//
//            count = getGTMatch(reRankTopList, query);
//
//            System.out.println("top K = " + topK);
//            System.out.println("predict true count = " + count);
//            System.out.println("precision@" + topK + " = " + ((double) count / topK));
//            System.out.println();

        }

        System.out.println(rows);
        // 结果输出excel 需要修改路径
        //通过工具类创建writer
        ExcelWriter writer = ExcelUtil.getWriter("C:\\Users\\Lunr\\Desktop\\result1230.xlsx");

        //一次性写出内容
        writer.write(rows, true);
        //关闭writer，释放内存
        writer.close();

    }

    /**
     * 根据List，给出avs测评分数
     * 需要修改C值，即此方法中的countAllTeam属性，已修改为全局变量
     *
     * @param urlList
     * @param query
     * @return
     */
    public double getScore(List<String> urlList, int query) {

        // 根据query_id获取avsGrandTruths 即query对应的gt avsGrandTruths的具体结果见类AvsGrandTruth
        Map<String, Object> selectByQueryMap = new HashMap<>();
        selectByQueryMap.put("query_id", query);
        List<AvsGrandTruth> avsGrandTruths = avsGrandTruthMapper.selectByMap(selectByQueryMap);

        // videoId < 7476 即V3C1的gt
        List<AvsGrandTruth> queryGrandTruths = new ArrayList<>();
        for (AvsGrandTruth avsGrandTruth : avsGrandTruths) {
            if (Integer.parseInt(avsGrandTruth.getVideoId()) < 7476) {
                queryGrandTruths.add(avsGrandTruth);
            }
        }


        Map<String, List<String>> videoShotMap = new HashMap<>(); //(videoId, shotsList)键值对

        // 得到排序列表的 videoShotMap 即根据 videoId 将排序列表分成若干个list
        // 比如(00001, ["shot00001_1", "shot00001_2"]), (00005, ["shot00005_10", "shot00005_20"])
        for (String shot : urlList) {
            String videoId = shot.substring(4, 9);
            if (videoShotMap.get(videoId) == null) {
                List<String> shots = new ArrayList<>();
                shots.add(shot);
                videoShotMap.put(videoId, shots);
            } else {
                List<String> shots = videoShotMap.get(videoId);
                shots.add(shot);
                videoShotMap.replace(videoId, shots);
            }
        }

        // 评分指标参数设置
        double p = 0.1;
        countAllTeam = 200;

        // 得分
        double sum = 0.0;

        // 遍历 videoShotMap 中的每个videoId
        for (String videoId : videoShotMap.keySet()) {

            // videoId对应的shotsList
            // 比如videoId = 00001    shotsList = ["shot00001_1", "shot00001_2"]
            List<String> shotsList = videoShotMap.get(videoId);

            // flag1 判断此videoId的shotsList里是否存在符合gt的shot 0不存在 1存在
            int flag1 = 0;

            // 到符合gt的shot为止 不正确的shot数量
            int incorrectCount = 0;

            // 遍历此videoId对应的shotsList
            for (String shot : shotsList) {

                // 得到shotId 比如shot00001_155的shotId=155
                int shotId = Integer.parseInt(shot.substring(10)) - 1;

                // 得到videoId对应的镜头边界映射文件msbByVideoId
                List<MasterShotBoundary> msbByVideoId = msbMap.get(videoId);
                if (msbByVideoId == null) {
                    continue;
                }

                // 得到此shot的startTime和endTime, frameTime为中间时刻
                double startTime = Double.parseDouble(msbByVideoId.get(shotId).getStartTime()) * 1000;
                double endTime = Double.parseDouble(msbByVideoId.get(shotId).getEndTime()) * 1000;
                double frameTime = (startTime + endTime) / 2;

                // flag2判断此shot是否符合gt 0不符合 1符合
                int flag2 = 0;

                // 遍历此query的gt
                for (AvsGrandTruth avsGrandTruth : queryGrandTruths) {

                    //得到gt的起止时间
                    double gtStartTime = Double.parseDouble(avsGrandTruth.getStartTime());
                    double gtEndTime = Double.parseDouble(avsGrandTruth.getEndTime());

                    // 如果gt的videoId和此shot的videoId相同 且frameTime在gt起止时间的±5s内 则认为此shot符合gt
                    if (Objects.equals(avsGrandTruth.getVideoId(), videoId) && ((frameTime > gtStartTime - 5000) && (frameTime < gtEndTime + 5000))) {

                        // flag2判断此shot是否符合gt 0不符合 1符合
                        flag2 = 1;
                        // 若符合gt则不需要再遍历gt 直接退出遍历gt的循环
                        break;
                    }
                }

                // 如果此shot符合gt 则flag1(判断此videoId的shotsList里是否存在符合gt的shot)置1
                if (flag2 == 1) {
                    // flag1 0不存在 1存在
                    flag1 = 1;

                    // 此shot符合gt 直接退出循环 后面的shot不用再计算了
                    break;
                } else {
                    // 此shot不符合gt incorrectCount+1
                    incorrectCount++;
                }
            }

            // flag1 判断此videoId的shotsList里是否存在符合gt的shot
            sum = sum + (flag1 - incorrectCount * p);
        }

        sum = 1000 * sum / countAllTeam;
        return sum;
    }

    /**
     * 将scoreMap中的shot以得分从高到低存至urlList中
     *
     * @param urlList
     * @param sortMap
     */
    public void savePathToUrlList(List<String> urlList, Map<String, Double> sortMap) {
        for (String path : sortMap.keySet()) {
            urlList.add(path);
        }
    }

    int getGTMatch(List<String> topList, int query) {
        Collections.sort(topList);

        Map<String, Object> selectByQueryMap = new HashMap<>();
        selectByQueryMap.put("query_id", query);
        List<AvsGrandTruth> avsGrandTruths = avsGrandTruthMapper.selectByMap(selectByQueryMap);

        List<AvsGrandTruth> queryGrandTruths = new ArrayList<>();

        for (AvsGrandTruth avsGrandTruth : avsGrandTruths) {
            if (Integer.parseInt(avsGrandTruth.getVideoId()) < 7476) {
                queryGrandTruths.add(avsGrandTruth);
            }
        }

        int number = queryGrandTruths.size();
        System.out.println("query " + query + " total true count = " + number);

        int count = 0;

        Set<AvsGrandTruth> avsGrandTruthSet = new HashSet<>();
        for (String shot : topList) {
            String videoId = shot.substring(4, 9);
            int shotId = Integer.parseInt(shot.substring(10)) - 1;
            if (shotId > 100) {
                shotId -= 1;
            }
            List<MasterShotBoundary> msbByVideoId = msbMap.get(videoId);
            if (msbByVideoId == null) {
                continue;
            }
            double startTime = Double.parseDouble(msbByVideoId.get(shotId).getStartTime()) * 1000;
            double endTime = Double.parseDouble(msbByVideoId.get(shotId).getEndTime()) * 1000;


            for (AvsGrandTruth avsGrandTruth : queryGrandTruths) {
                double gtStartTime = Double.parseDouble(avsGrandTruth.getStartTime());
                double gtEndTime = Double.parseDouble(avsGrandTruth.getEndTime());
                if (Objects.equals(avsGrandTruth.getVideoId(), videoId) && gtStartTime > startTime - 3000 && gtEndTime < endTime + 3000) {
                    avsGrandTruthSet.add(avsGrandTruth);
                }
            }
        }
        count = avsGrandTruthSet.size();

        return count;
    }


    @Test
    void gtJudge() {

        // 需要修改query编号
        int query = 1;

        // 根据query_id获取avsGrandTruths 即query对应的gt avsGrandTruths的具体结果见类AvsGrandTruth
        Map<String, Object> selectByQueryMap = new HashMap<>();
        selectByQueryMap.put("query_id", query);
        List<AvsGrandTruth> avsGrandTruths = avsGrandTruthMapper.selectByMap(selectByQueryMap);

        // videoId < 7476 即V3C1的gt
        List<AvsGrandTruth> queryGrandTruths = new ArrayList<>();
        for (AvsGrandTruth avsGrandTruth : avsGrandTruths) {
            if (Integer.parseInt(avsGrandTruth.getVideoId()) < 7476) {
                queryGrandTruths.add(avsGrandTruth);
            }
        }

        // list中符合gt的shot
        List<String> gtMatchList = new ArrayList<>();

        // 需要修改判断的list
        // String[] shots = new String[]{"shot02024_72", "shot02684_21", "shot04804_903", "shot06037_108", "shot04892_80", "shot04804_773", "shot05511_5", "shot00654_364", "shot05511_4", "shot04804_744", "shot02684_46", "shot05368_95", "shot07466_39", "shot02426_40", "shot02347_23", "shot01617_145", "shot01386_104", "shot01880_20", "shot06248_323", "shot01172_163", "shot03319_668", "shot03319_716", "shot02347_16", "shot05511_8", "shot04804_1328", "shot06077_4"};
        String[] shots = new String[]{"shot04804_635", "shot04804_1075", "shot04804_1321", "shot00956_15", "shot00151_87", "shot07466_11", "shot04804_896", "shot01172_285", "shot04804_820", "shot01172_62", "shot04804_957", "shot04804_344", "shot00602_129", "shot00602_37", "shot00298_90", "shot04804_907", "shot01172_183", "shot06522_48", "shot06624_32", "shot00602_148", "shot04804_488", "shot03169_107", "shot04804_506", "shot01090_427", "shot00148_38", "shot00487_107", "shot04804_944", "shot03918_623", "shot03699_3", "shot04804_197", "shot00290_25", "shot02960_89", "shot04867_488", "shot00602_133", "shot03699_2", "shot07420_22", "shot07420_16", "shot02061_18", "shot00148_42", "shot00148_41", "shot03319_151", "shot06566_163", "shot01253_91", "shot03699_1", "shot00148_39", "shot06248_56", "shot04804_945", "shot01253_93", "shot00148_52", "shot04804_1379", "shot05200_155", "shot04255_3", "shot04804_821", "shot00148_43", "shot01071_255", "shot04804_715", "shot00602_86", "shot04804_974", "shot00148_45", "shot04989_99", "shot00602_117", "shot04695_135", "shot00602_134", "shot01253_35", "shot06533_278", "shot04804_251", "shot00193_305", "shot05469_2", "shot02826_100", "shot03699_4", "shot04804_595", "shot00218_88", "shot02347_30", "shot04619_195", "shot00151_89", "shot00218_91", "shot04804_643", "shot04804_1238", "shot05358_16", "shot02684_122", "shot03900_146", "shot01253_99", "shot01843_96", "shot06248_107", "shot03900_226", "shot06624_25", "shot01333_124", "shot03279_18", "shot04299_15", "shot00218_12", "shot06288_19", "shot04867_484", "shot01090_175", "shot06827_365", "shot06624_28", "shot01090_663", "shot04989_247", "shot05246_228", "shot06248_57", "shot00148_40", "shot04804_280", "shot07412_39", "shot06248_454", "shot04464_21", "shot06852_2", "shot06674_68", "shot00148_116", "shot01849_177", "shot01420_211", "shot04804_1221", "shot02347_28", "shot00602_8", "shot06611_93", "shot06611_105", "shot00218_16", "shot00193_335", "shot02061_7", "shot03918_624", "shot02347_38", "shot06248_462", "shot07257_84", "shot03279_75", "shot00602_36", "shot06624_39", "shot06288_18", "shot06869_51", "shot06325_88", "shot01498_34", "shot02061_9", "shot06808_162", "shot02770_5", "shot06611_18", "shot03817_47"};

        // String[] shots = new String[]{"shot00148_38", "shot00148_39", "shot00148_40", "shot00148_41", "shot04804_344", "shot06611_93", "shot00148_42", "shot00148_43", "shot00148_45", "shot02770_5", "shot04804_635", "shot03900_146", "shot00602_8"};

        for (String shot : shots) {
            String videoId = shot.substring(4, 9);
            String shotId = shot.substring(shot.indexOf("_") + 1);
            QueryWrapper<MasterShotBoundary> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("video_id", videoId);
            List<MasterShotBoundary> boundaryList = msbMapper.selectList(queryWrapper);

            MasterShotBoundary boundary = boundaryList.get(Integer.parseInt(shotId) - 1);
            Double startTime = Double.parseDouble(boundary.getStartTime()) * 1000;
            Double endTime = Double.parseDouble(boundary.getEndTime()) * 1000;
            double frameTime = (startTime + endTime) / 2;

            int flag2 = 0;
            // 遍历此query的gt
            for (AvsGrandTruth avsGrandTruth : queryGrandTruths) {

                //得到gt的起止时间
                double gtStartTime = Double.parseDouble(avsGrandTruth.getStartTime());
                double gtEndTime = Double.parseDouble(avsGrandTruth.getEndTime());

                // 如果gt的videoId和此shot的videoId相同 且frameTime在gt起止时间的±5s内 则认为此shot符合gt
                if (Objects.equals(avsGrandTruth.getVideoId(), videoId) && ((frameTime > gtStartTime - 10000) && (frameTime < gtEndTime + 10000))) {

                    // flag2判断此shot是否符合gt 0不符合 1符合
                    flag2 = 1;
                    // 若符合gt则不需要再遍历gt 直接退出遍历gt的循环
                    break;
                }
            }

            if (flag2 == 1) {
                gtMatchList.add(shot);
            }
        }

        // gtMatchList为list中符合gt的shot
        System.out.println(gtMatchList);
    }


    /**
     * Rocchio算法
     *
     * @param shot
     * @param bool
     */
    void Rocchio(String shot, int bool) {

        List<Double> shotVector = pathMap.get(shot);

        for (String path : pathList) {
            List<Double> pathVector = pathMap.get(path);

            double cosineSimilarity = VectorUtil.getCosineSimilarity(shotVector, pathVector);
            if (bool == 0) {
                scoreMap.replace(path, scoreMap.get(path) + 0.5 * cosineSimilarity);
            } else if (bool == 1) {
                scoreMap.replace(path, scoreMap.get(path) - 0.1 * cosineSimilarity);
            }
        }
    }

    /**
     * qmr 算法
     *
     * @param shot
     */
    void qmr(String shot) {
        double alpha_dt = Math.sqrt(scoreMap.get(shot));
        double beta_dt = Math.sqrt(1 - scoreMap.get(shot));

        for (String path : pathList) {
            double probability = scoreMap.get(path);

            double cosineSimilarity = VectorUtil.getCosineSimilarity(pathMap.get(path), pathMap.get(shot));

            double alpha_d = Math.sqrt(probability);
            double beta_d = Math.sqrt(1 - probability);
            double my_lambda = alpha_d * alpha_dt + beta_d * beta_dt;
            double my_probability = Math.pow(my_lambda * alpha_dt, 2);
            scoreMap.replace(path, my_probability * cosineSimilarity);
        }
    }

    /**
     * qir算法，需要改动运行的qir.py文件路径和python.exe文件路径
     *
     * @param Paths
     * @param bool
     */
    public void qir(List<String> Paths, int bool) {

        if (Paths.get(0).length() < 5) {
            return;
        }

        //对每一个反馈图片
        for (String path : Paths) {

            //得到选中的反馈图片的向量
            String selectedVector = pathMap.get(path).toString();
            selectedVector = selectedVector.substring(1, selectedVector.length() - 1);

            //反馈图片的概率得分 vectorCosineSimilarity
            Double selectedCos = scoreMap.get(path);

            //调用 python 函数得到新的查询向量
            String[] args1 = new String[]{"E:\\Git\\lavis2\\venv\\Scripts\\python.exe", "E:\\Git\\lavis2\\qir.py", selectedVector, selectedCos.toString()};
            String strQueryVector = runPython(args1);
            List<Double> newQueryVector = VectorUtil.strToDouble(String.valueOf(strQueryVector), 2);

            //更新所有图片的概率得分
            reRankByNewQuery(newQueryVector, bool);

            VectorUtil.mapNormalization(scoreMap);
        }
    }

    /**
     * qprp算法，时间复杂度太高不用测
     */
    public void qprp() {
        Map<String, Double> reRankMap = new HashMap<>();
        List<String> tmpList = pathList;

        for (int i = tmpList.size() - 1; i >= 0; i--) {
            for (int j = 0; j < i; j++) {
                double interferenceSum = 0;
                for (int k = 0; k < pathList.size(); k++) {
                    interferenceSum = interferenceSum -
                            Math.sqrt(scoreMap.get(tmpList.get(j)) * scoreMap.get(tmpList.get(k))) * VectorUtil.getPearsonsCorrelation(pathMap.get(tmpList.get(j)), pathMap.get(tmpList.get(k)));
                }
                reRankMap.put(tmpList.get(i), interferenceSum + scoreMap.get(tmpList.get(i)));
            }
        }

//        for (String i : pathList) {
//            double interferenceSum = 0;
//            for (String j : pathList) {
//                interferenceSum = interferenceSum -
//                        Math.sqrt(scoreMap.get(i) * scoreMap.get(j)) * VectorUtil.getPearsonsCorrelation(pathMap.get(i), pathMap.get(j));
//            }
//            reRankMap.put(i, scoreMap.get(i) + interferenceSum);
//        }
        scoreMap = reRankMap;

    }

    /**
     * cvts算法
     *
     * @param shots
     * @param queryVector
     */
    public void cvts(List<String> shots, List<Double> queryVector) {
        double r1 = 1.0;
        double r2 = 0.2;
        double c = r2 / shots.size();
        Map<String, Double> reRankMap = new HashMap<>();


        List<VideoDescriptionVector> videoDescriptionVectors = videoDescriptionVectorMapper.selectList(null);
        Map<String, List<Double>> vectorVisualMap = new HashMap<>();
        for (VideoDescriptionVector videoDescriptionVector : videoDescriptionVectors) {
            String videoId = videoDescriptionVector.getVideoId();
            List<Double> vector = VectorUtil.strToDouble(videoDescriptionVector.getVector(), 1);
            vectorVisualMap.put(videoId, vector);
        }

        for (String path : pathList) {
            String pathVideoId = path.substring(4, 9);
            double scoreText = r1 * Math.pow(VectorUtil.getDot(pathMap.get(path), queryVector), 2);
            double scoreVisual = r1 * Math.pow(VectorUtil.getDot(vectorVisualMap.get(pathVideoId), queryVector), 2);
            for (String shot : shots) {
                String shotVideoId = shot.substring(4, 9);
                scoreText += c * Math.pow(VectorUtil.getDot(pathMap.get(path), pathMap.get(shot)), 2);
                scoreVisual += c * Math.pow(VectorUtil.getDot(vectorVisualMap.get(pathVideoId), vectorVisualMap.get(shotVideoId)), 2);
            }
            reRankMap.put(path, scoreText + scoreVisual);
        }

        scoreMap = reRankMap;
    }


    /**
     * qir的权重在这个函数里调
     *
     * @param queryVector
     * @param bool
     */
    public void reRankByNewQuery(List<Double> queryVector, int bool) {

        for (String path : pathMap.keySet()) {

            //取出在库图片的特征向量，并转成浮点数组
            List<Double> vectorDoubleList = pathMap.get(path);

            //计算相似度得分
            double cosineSimilarity = VectorUtil.getCosineSimilarity(queryVector, vectorDoubleList);

            //原先得分
            Double preCos = scoreMap.get(path);

            //更新得分
            if (bool == 0) {
                scoreMap.replace(path, preCos + 0.5 * cosineSimilarity);
            } else if (bool == 1) {
                scoreMap.replace(path, preCos - 0.1 * cosineSimilarity);
            }

        }
    }


    // 其他一些工具方法 不用细看

    public String runPython(String[] args) {
        StringBuilder strVector = new StringBuilder();
        //调用 python 函数
        try {
            //执行 py 文件
            Process proc = Runtime.getRuntime().exec(args);
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));

            //读取特征向量
            String line = null;
            while ((line = in.readLine()) != null) {
                strVector.append(line);
            }
            in.close();

            proc.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return strVector.toString();
    }

    String getLabelsByQuery(String query) {
        StringBuilder strLabels = new StringBuilder();
        //调用 python 函数
        try {
            //执行 py 文件
            String[] args1 = new String[]{"E:\\Git\\lavis2\\venv\\Scripts\\python.exe", "E:\\Git\\lavis2\\keyphraseExtract.py", query};
            Process proc = Runtime.getRuntime().exec(args1);
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));

            //读取输出
            String line = null;
            while ((line = in.readLine()) != null) {
                strLabels.append(line);
            }
            in.close();

            proc.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return strLabels.toString();
    }

    public List<Double> getTextVector(String query) {
        List<Double> queryVector;

        //调用 python 函数得到查询文本的特征向量
        String[] args1 = new String[]{"E:\\Git\\lavis2\\venv\\Scripts\\python.exe", "E:\\Git\\lavis2\\textExtractor.py", query};
        String strQueryVector = runPython(args1);

        //将特征向量转化为浮点数组
        queryVector = VectorUtil.avsQueryStrToDouble(String.valueOf(strQueryVector));

        if (queryVector.size() == 257) {
            queryVector.remove(256);
        }

        return queryVector;
    }

    public void setClassificationMap() {
        CsvReader reader = CsvUtil.getReader();

        for (int i = 1; i <= 7475; i++) {
            if (i >= 4304 && i <= 4318 || i == 4350) {
                continue;
            }
            String fileName = "";
            if (i < 10) {
                fileName = "0000" + i;
            } else if (i < 100) {
                fileName = "000" + i;
            } else if (i < 1000) {
                fileName = "00" + i;
            } else if (i < 10000) {
                fileName = "0" + i;
            }
            String csvPath = "D:\\Download\\VBSDataset\\classification_csv\\" + fileName + ".csv";
            List<ClassificationResult> result = reader.read(ResourceUtil.getUtf8Reader(csvPath), ClassificationResult.class);
            for (ClassificationResult classificationResult : result) {
                String shotId = classificationResult.getShotsid();
                int begin = shotId.indexOf("s");
                int end = shotId.indexOf("_", begin + 11);

                String category = classificationResult.getCategory();
                String[] categoryList = category.split(",");
                for (String c : categoryList) {
                    if (Objects.equals(c, "")) {
                        break;
                    }
                    if (classificationMap.get(c) != null) {
                        Set<String> shotSet = classificationMap.get(c);
                        shotSet.add(shotId.substring(begin, end));
                        classificationMap.replace(c, shotSet);
                    } else {
                        Set<String> shotSet = new HashSet<>();
                        shotSet.add(shotId.substring(begin, end));
                        classificationMap.put(c, shotSet);
                    }
                }
            }
        }
    }

}
