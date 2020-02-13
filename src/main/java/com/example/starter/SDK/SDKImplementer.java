package com.example.starter.SDK;

import cn.hutool.core.codec.Base64Decoder;
import cn.hutool.core.codec.Base64Encoder;
import com.fujitsu.frontech.palmsecure.*;
import com.fujitsu.frontech.palmsecure.util.PalmSecureConstant;
import com.fujitsu.frontech.palmsecure.util.PalmSecureException;
import com.fujitsu.frontech.palmsecure.util.PalmSecureHelper;
import com.fujitsu.frontech.palmsecure_smpl.PsStateCallback;
import com.fujitsu.frontech.palmsecure_smpl.PsStreamingCallback;
import com.fujitsu.frontech.palmsecure_smpl.data.PsThreadResult;
import com.fujitsu.frontech.palmsecure_smpl.exception.PsAplException;
import com.fujitsu.frontech.palmsecure_smpl.xml.PsFileAccessorIni;
import com.fujitsu.frontech.palmsecure_smpl.xml.PsFileAccessorLang;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author : bingrun.chiu
 * @description: sdk工具类
 * @date: 2020/1/9 9:39
 * capture获取的数据只能用于验证,enroll获取的数据只能用于注册.
 **/
@Slf4j
@Component
public class SDKImplementer {
    public static final int PS_GEXTENDED_DATA_TYPE = 2;
    private static int PS_REGISTER_MAX = 5000; // 实际使用的都是 1000
    private boolean isIdentifyEnable = true; // 实际使用的都是 false
    private boolean isCaptureEnable = false; // 从ini文件中读取的
    private long usingDataType = 0;
    private String dataModeTitle = "";
    private long usingGuideMode = 0;
    private long usingGExtendedMode = 0;
    private long usingSensorType = 0;
    private long usingSensorExtKind = 0;
    private PsStateCallback psStateCB = null;
    private PsStreamingCallback psStreamingCB = null;
    private PalmSecureIf palmsecureIf = null;
    private JAVA_uint32 ModuleHandle = new JAVA_uint32();

    //==================================================================================================================

    // 结束时调用此方法:
    public void terminal() {
        long ret = PalmSecureConstant.JAVA_BioAPI_FALSE;
        byte[] ModuleGuid = new byte[]{
                (byte) 0xe1, (byte) 0x9a, (byte) 0x69, (byte) 0x01,
                (byte) 0xb8, (byte) 0xc2, (byte) 0x49, (byte) 0x80,
                (byte) 0x87, (byte) 0x7e, (byte) 0x11, (byte) 0xd4,
                (byte) 0xd8, (byte) 0xf1, (byte) 0xbe, (byte) 0x79
        };
        //Detach module
        try {
            ret = palmsecureIf.JAVA_BioAPI_ModuleDetach(ModuleHandle);
            log.info("ret1 => {} ", ret);
        } catch (PalmSecureException e) {
            e.printStackTrace();
        }
        //Unload module
        try {
            ret = palmsecureIf.JAVA_BioAPI_ModuleUnload(ModuleGuid, null, null);
            log.info("ret2 => {} ", ret);
        } catch (PalmSecureException e) {
            e.printStackTrace();
        }
    }

    // 初始化时调用此方法:
    public boolean Ps_Sample_Apl_Java() {

        File checkFile;

        checkFile = new File("PalmSecureSample.ini");
        if (! checkFile.exists()) {
            log.info("checkFile.getPath() : {} ", checkFile.getPath());
            log.info("checkFile.getAbsolutePath() : {} ", checkFile.getAbsolutePath());
            log.error("找不到PalmSecureSample.ini");
            JOptionPane.showMessageDialog(
                    null,
                    "Read Error " + "PalmSecureSample.ini",
                    null,
                    JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        // Read the Configuration file.
        PsFileAccessorIni psFileAcsIni = PsFileAccessorIni.GetInstance();
        if (psFileAcsIni == null) {
            log.error("PalmSecureSample.ini读取错误");
            JOptionPane.showMessageDialog(
                    null,
                    "Read Error " + "PalmSecureSample.ini",
                    null,
                    JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        //读取配置文件中的EnableCapture
        isCaptureEnable = (Integer.parseInt(PsFileAccessorIni.GetInstance().GetValueString("EnableCapture")) != 0);

        // Initialize the Authentication library.
        if (! Ps_Sample_Apl_Java_InitLibrary()) {
            log.error("...Initialize the Authentication library 失败");
            System.exit(0);
        }

        if (usingSensorType == PalmSecureConstant.JAVA_PvAPI_INFO_SENSOR_TYPE_2) {
            if (usingSensorExtKind == PalmSecureConstant.JAVA_PvAPI_INFO_SENSOR_MODE_COMPATIBLE) {
                PsFileAccessorLang.GetInstance().SetValue(PsFileAccessorLang.MainDialog_SensorName1, PsFileAccessorLang.GetInstance().GetValue(PsFileAccessorLang.MainDialog_SensorName1_Compati));
            } else if (usingSensorExtKind == PalmSecureConstant.JAVA_PvAPI_INFO_SENSOR_MODE_EXTEND) {
                PsFileAccessorLang.GetInstance().SetValue(
                        PsFileAccessorLang.MainDialog_SensorName1,
                        PsFileAccessorLang.GetInstance().GetValue(PsFileAccessorLang.MainDialog_SensorName1_Extend));
            }
            if (usingGExtendedMode == 1) {
                usingDataType = PS_GEXTENDED_DATA_TYPE;
                dataModeTitle = PsFileAccessorLang.GetInstance().GetValue(PsFileAccessorLang.MainDialog_DataMode1);
            } else {
                usingDataType = usingGuideMode;
                PS_REGISTER_MAX = 1000;
                dataModeTitle = PsFileAccessorLang.GetInstance().GetValue(PsFileAccessorLang.MainDialog_DataMode0);
            }

        } else if (usingSensorType == PalmSecureConstant.JAVA_PvAPI_INFO_SENSOR_TYPE_4 &&
                usingSensorExtKind == PalmSecureConstant.JAVA_PvAPI_INFO_SENSOR_MODE_EXTEND) {
            usingSensorType = PalmSecureConstant.JAVA_PvAPI_INFO_SENSOR_TYPE_8;
            PsFileAccessorLang.GetInstance().SetValue(
                    PsFileAccessorLang.MainDialog_SensorName7,
                    PsFileAccessorLang.GetInstance().GetValue(PsFileAccessorLang.MainDialog_SensorName3));
            usingDataType = usingGuideMode;
            isIdentifyEnable = false;
        } else if (usingSensorType == PalmSecureConstant.JAVA_PvAPI_INFO_SENSOR_TYPE_9) {
            usingSensorType = PalmSecureConstant.JAVA_PvAPI_INFO_SENSOR_TYPE_2;
            PsFileAccessorLang.GetInstance().SetValue(
                    PsFileAccessorLang.MainDialog_SensorName1,
                    PsFileAccessorLang.GetInstance().GetValue(PsFileAccessorLang.MainDialog_SensorName8));
            if (usingGExtendedMode == 1) {
                usingDataType = PS_GEXTENDED_DATA_TYPE;
                dataModeTitle = PsFileAccessorLang.GetInstance().GetValue(PsFileAccessorLang.MainDialog_DataMode1);
            } else {
                usingDataType = usingGuideMode;
                PS_REGISTER_MAX = 1000;
                dataModeTitle = PsFileAccessorLang.GetInstance().GetValue(PsFileAccessorLang.MainDialog_DataMode0);
            }
        } else if (usingSensorType == PalmSecureConstant.JAVA_PvAPI_INFO_SENSOR_TYPE_A) {
            if (usingGExtendedMode == 1) {
                usingDataType = PS_GEXTENDED_DATA_TYPE;
                dataModeTitle = PsFileAccessorLang.GetInstance().GetValue(PsFileAccessorLang.MainDialog_DataMode1);
            } else {
                usingDataType = usingGuideMode;
                PS_REGISTER_MAX = 1000;
                dataModeTitle = PsFileAccessorLang.GetInstance().GetValue(PsFileAccessorLang.MainDialog_DataMode0);
            }
        } else if (usingSensorType == PalmSecureConstant.JAVA_PvAPI_INFO_SENSOR_TYPE_D) {
            usingSensorType = PalmSecureConstant.JAVA_PvAPI_INFO_SENSOR_TYPE_2;
            PsFileAccessorLang.GetInstance().SetValue(
                    PsFileAccessorLang.MainDialog_SensorName1,
                    PsFileAccessorLang.GetInstance().GetValue("MainDialog.SensorNameC"));
            if (usingGExtendedMode == 1) {
                usingDataType = PS_GEXTENDED_DATA_TYPE;
                dataModeTitle = PsFileAccessorLang.GetInstance().GetValue(PsFileAccessorLang.MainDialog_DataMode1);
            } else {
                usingDataType = usingGuideMode;
                PS_REGISTER_MAX = 1000;
                dataModeTitle = PsFileAccessorLang.GetInstance().GetValue(PsFileAccessorLang.MainDialog_DataMode0);
            }
            isIdentifyEnable = false;
        }
        return true;
    }

    //------------------------------------------------------------------------------------------------------------------
    private boolean Ps_Sample_Apl_Java_InitLibrary() {

        long ret;

        String Key = PsFileAccessorIni.GetInstance().GetValueString(PsFileAccessorIni.ApplicationKey);

        byte[] ModuleGuid = new byte[]{
                (byte) 0xe1, (byte) 0x9a, (byte) 0x69, (byte) 0x01,
                (byte) 0xb8, (byte) 0xc2, (byte) 0x49, (byte) 0x80,
                (byte) 0x87, (byte) 0x7e, (byte) 0x11, (byte) 0xd4,
                (byte) 0xd8, (byte) 0xf1, (byte) 0xbe, (byte) 0x79
        };
        JAVA_PvAPI_LBINFO lbInfo = new JAVA_PvAPI_LBINFO();
        JAVA_PvAPI_ErrorInfo errorInfo = new JAVA_PvAPI_ErrorInfo();
        ///////////////////////////////////////////////////////////////////////////
        //Create a instance of PalmSecureIf class
        try {
            System.out.println("....." + "java.library.path = " + System.getProperty("java.library.path"));
//            System.setProperty("java.library.path", "/usr/local/kava:/usr/java/packages/lib/amd64:/usr/lib64:/lib64:/lib:/usr/lib");
//            System.load("/usr/local/kava/libf3bc4jni.so");
//            System.out.println("....." + "after = " + System.getProperty("java.library.path"));
            palmsecureIf = new PalmSecureIf();
        } catch (PalmSecureException e) {
            e.printStackTrace();
            return false;
        }
        //Authenticate application by key
        ///////////////////////////////////////////////////////////////////////////
        try {
            System.out.println("....." + "Key = " + Key);
            ret = palmsecureIf.JAVA_PvAPI_ApAuthenticate(Key);
            System.out.println("....." + "ret = " + ret);
            if (ret != PalmSecureConstant.JAVA_BioAPI_OK) {
                palmsecureIf.JAVA_PvAPI_GetErrorInfo(errorInfo);
                return false;
            }
        } catch (PalmSecureException e) {
            e.printStackTrace();
            return false;
        }
        ///////////////////////////////////////////////////////////////////////////
        //Load module
        try {
            ret = palmsecureIf.JAVA_BioAPI_ModuleLoad(ModuleGuid, null, null, null);
            if (ret != PalmSecureConstant.JAVA_BioAPI_OK) {
                palmsecureIf.JAVA_PvAPI_GetErrorInfo(errorInfo);
                return false;
            }
        } catch (PalmSecureException e) {
            e.printStackTrace();
            return false;
        }
        usingGExtendedMode = PsFileAccessorIni.GetInstance().GetValueInteger(PsFileAccessorIni.GExtendedMode);

        //Set gExtended mode
        ///////////////////////////////////////////////////////////////////////////
        JAVA_uint32 uiFlag = new JAVA_uint32();
        uiFlag.value = PalmSecureConstant.JAVA_PvAPI_PRE_PROFILE_G_EXTENDED_MODE;
        JAVA_uint32 lpvParamData = new JAVA_uint32();
        if (usingGExtendedMode == 1) {
            lpvParamData.value = (int) PalmSecureConstant.JAVA_PvAPI_PRE_PROFILE_G_EXTENDED_MODE_1;
        } else {
            lpvParamData.value = (int) PalmSecureConstant.JAVA_PvAPI_PRE_PROFILE_G_EXTENDED_MODE_OFF;
        }
        try {
            ret = palmsecureIf.JAVA_PvAPI_PreSetProfile(
                    uiFlag,
                    lpvParamData,
                    null,
                    null);
            if (ret != PalmSecureConstant.JAVA_BioAPI_OK) {
                palmsecureIf.JAVA_PvAPI_GetErrorInfo(errorInfo);
                return false;
            }
        } catch (PalmSecureException e) {
            e.printStackTrace();
            return false;
        }
        //Attach to module
        ///////////////////////////////////////////////////////////////////////////
        try {
//            ModuleHandle = new JAVA_uint32();
            ret = palmsecureIf.JAVA_BioAPI_ModuleAttach(
                    ModuleGuid,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    ModuleHandle);
            if (ret != PalmSecureConstant.JAVA_BioAPI_OK) {
                palmsecureIf.JAVA_PvAPI_GetErrorInfo(errorInfo);
//                return false;// Sensor = 0的时候，它返回失败无所谓，继续往下走
            }
        } catch (PalmSecureException e) {
            System.out.println("------------------------------------------------");
            e.printStackTrace();
            return false;
        }
        ///////////////////////////////////////////////////////////////////////////
        try {
            psStreamingCB = new PsStreamingCallback();
            psStateCB = new PsStateCallback();
            ret = palmsecureIf.JAVA_BioAPI_SetGUICallbacks(
                    ModuleHandle,
                    psStreamingCB,
                    this,
                    psStateCB,
                    this);
            if (ret != PalmSecureConstant.JAVA_BioAPI_OK) {
                palmsecureIf.JAVA_PvAPI_GetErrorInfo(errorInfo);
                System.out.println("....." + "errorInfo = " + errorInfo);
//                return false; // 先忽略掉
            }
        } catch (PalmSecureException e) {
            System.out.println("------------------------------------------------");
            e.printStackTrace();
            return false;
        }
        //Get library information
        ///////////////////////////////////////////////////////////////////////////
        try {
            ret = palmsecureIf.JAVA_PvAPI_GetLibraryInfo(lbInfo);
            if (ret != PalmSecureConstant.JAVA_BioAPI_OK) {
                palmsecureIf.JAVA_PvAPI_GetErrorInfo(errorInfo);
                return false;
            }
        } catch (PalmSecureException e) {
            System.out.println("------------------------------------------------");
            e.printStackTrace();
            return false;
        }
        ///////////////////////////////////////////////////////////////////////////
        usingSensorExtKind = lbInfo.uiSensorExtKind;
        usingGuideMode = PsFileAccessorIni.GetInstance().GetValueInteger(PsFileAccessorIni.GuideMode);
        return true;
    }

    //==================================================================================================================
    // 读取dat文件
    public byte[] readFile(String filename) throws PsAplException {
        if (filename == null) {
            throw new PsAplException(PsFileAccessorLang.ErrorMessage_AplErrorDataFileNotFound);
        }
        FileInputStream inStream;
        try {
            inStream = new FileInputStream(filename);
        } catch (FileNotFoundException e) {
            throw new PsAplException(PsFileAccessorLang.ErrorMessage_AplErrorDataFileNotFound);
        }
        FileChannel inChannel = inStream.getChannel();
        ByteBuffer byteBuff;
        try {
            long fileSize = inChannel.size();
            byteBuff = ByteBuffer.allocate((int) fileSize);
            inChannel.read(byteBuff);
        } catch (IOException e) {
            throw new PsAplException(PsFileAccessorLang.ErrorMessage_AplErrorFileOpen);
        } finally {
            try {
                inChannel.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return byteBuff.array().clone();
    }

    //==================================================================================================================
    // 比较方法: 将两个BIR进行比较;
    public String identifyMatch(String veinData, String[] suspects, List<String> idList) throws PalmSecureException, IOException {
        PsThreadResult stResult = new PsThreadResult();
        //Set mode not to get authentication score
        ///////////////////////////////////////////////////////////////////////////
        try {
            JAVA_uint32 dwFlag = new JAVA_uint32();
            dwFlag.value = PalmSecureConstant.JAVA_PvAPI_PROFILE_SCORE_NOTIFICATIONS;
            JAVA_uint32 dwParam1 = new JAVA_uint32();
            dwParam1.value = PalmSecureConstant.JAVA_PvAPI_PROFILE_SCORE_NOTIFICATIONS_ON;
            stResult.result = palmsecureIf.JAVA_PvAPI_SetProfile(ModuleHandle, dwFlag, dwParam1, null, null);
        } catch (PalmSecureException e) {
            e.printStackTrace();
            stResult.result = PalmSecureConstant.JAVA_BioAPI_ERRCODE_FUNCTION_FAILED;
        }
        ///////////////////////////////////////////////////////////////////////////
        PsFileAccessorIni iniAcs = PsFileAccessorIni.GetInstance();
        JAVA_sint32 maxFRRRequested = new JAVA_sint32();
        maxFRRRequested.value = PalmSecureConstant.JAVA_PvAPI_MATCHING_LEVEL_NORMAL;
        JAVA_uint32 farPrecedence = new JAVA_uint32();
        farPrecedence.value = PalmSecureConstant.JAVA_BioAPI_FALSE;
        JAVA_uint32 binning = new JAVA_uint32();
        binning.value = PalmSecureConstant.JAVA_BioAPI_FALSE;
        JAVA_uint32 maxNumberOfResults = new JAVA_uint32();
        maxNumberOfResults.value = iniAcs.GetValueInteger(PsFileAccessorIni.MaxResults);
        JAVA_uint32 numberOfResults = new JAVA_uint32();
        JAVA_BioAPI_CANDIDATE[] candidates = new JAVA_BioAPI_CANDIDATE[(int) maxNumberOfResults.value];
        JAVA_sint32 timeout = new JAVA_sint32();
        timeout.value = 0;
        ///////////////////////////////////////////////////////////////////////////
        JAVA_BioAPI_INPUT_BIR storedTemplate = new JAVA_BioAPI_INPUT_BIR();
        storedTemplate.Form = PalmSecureConstant.JAVA_BioAPI_FULLBIR_INPUT;
        try {
            storedTemplate.BIR = PalmSecureHelper.convertByteToBIR(Base64Decoder.decode(veinData));
        } catch (PalmSecureException e) {
            e.printStackTrace();
        }
        ///////////////////////////////////////////////////////////////////////////
        JAVA_BioAPI_BIR[] birAry = Stream.of(suspects).map(Base64Decoder::decode).map(item -> {
            var bir = new JAVA_BioAPI_BIR();
            try {
                bir = PalmSecureHelper.convertByteToBIR(item);
            } catch (PalmSecureException | IOException e) {
                e.printStackTrace();
            }
            return bir;
        }).toArray(JAVA_BioAPI_BIR[]::new);
        JAVA_BioAPI_BIR_ARRAY_POPULATION BIRAryPopu = new JAVA_BioAPI_BIR_ARRAY_POPULATION();
        BIRAryPopu.NumberOfMembers = suspects.length;
        BIRAryPopu.Members = birAry;
        JAVA_BioAPI_IDENTIFY_POPULATION Population = new JAVA_BioAPI_IDENTIFY_POPULATION();
        Population.Type = PalmSecureConstant.JAVA_BioAPI_ARRAY_TYPE;
        Population.BIRArray = BIRAryPopu;
        ///////////////////////////////////////////////////////////////////////////
        stResult.result = palmsecureIf.JAVA_PvAPI_PresetIdentifyPopulation(ModuleHandle, Population);
        stResult.result = palmsecureIf.JAVA_BioAPI_IdentifyMatch(
                ModuleHandle,
                null,
                maxFRRRequested,
                farPrecedence,
                storedTemplate,// 传过来的BIR
                Population,// 已有的BIR
                binning,
                maxNumberOfResults,
                numberOfResults,
                candidates,
                timeout);
        if (stResult.result != PalmSecureConstant.JAVA_BioAPI_OK) {
            log.info("匹配失败: {} ", stResult.errInfo);
        }
        var nor = numberOfResults.value;
        log.info("匹配到{}个结果",nor);
        //------------------------------------------------------------------------------------
        // 解析结果: 匹配的结果个数是否不为0
        // 解析结果: 解析id
        if (nor >= 1) {
            for (int i = 0; i < nor; i++) {
                stResult.farAchieved.add(candidates[i].FARAchieved);
                stResult.userId.add(idList.get((int) (candidates[i].BIRInArray)));
            }
            if (nor == 1) {//warning: 疑似结果只有一个:
                return idList.get((int) candidates[0].BIRInArray);
            }
            //warning: 疑似结果有多个,拿分数较高的那个. 个数可以在ini文件设置.
            if (candidates[0].FARAchieved >= candidates[1].FARAchieved) {
                return idList.get((int) candidates[0].BIRInArray);
            } else {
                return idList.get((int) candidates[1].BIRInArray);
            }
        }
        return null;
    }
//======================================================================================================================
}
