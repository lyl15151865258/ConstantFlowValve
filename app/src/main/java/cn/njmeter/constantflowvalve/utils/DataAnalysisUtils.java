package cn.njmeter.constantflowvalve.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.njmeter.constantflowvalve.bean.ProductProtocol;
import cn.njmeter.constantflowvalve.bean.MBUS;
import cn.njmeter.constantflowvalve.bean.ParameterProtocol;
import cn.njmeter.constantflowvalve.bean.SSumHeat;
import cn.njmeter.constantflowvalve.constant.ProductType;

/**
 * 数据解析工具类（设备通过蓝牙或者网络直接上传的数据原文）
 * Created by LiYuliang on 2017/3/21 0021.
 *
 * @author LiYuliang
 * @version 2017/11/27
 */

public class DataAnalysisUtils {

    /**
     * 16进制字符转十进制
     *
     * @param ch 原始字符
     * @return 十进制整数
     */
    private static int transHexS1ToInt(char ch) {
        if ('a' <= ch && ch <= 'f') {
            return ch - 'a' + 10;
        }
        if ('A' <= ch && ch <= 'F') {
            return ch - 'A' + 10;
        }
        if ('0' <= ch && ch <= '9') {
            return ch - '0';
        }
        throw new IllegalArgumentException(String.valueOf(ch));
    }

    /**
     * 字符串转十进制
     *
     * @param s 原始字符
     * @return 十进制整数
     */
    private static int transHexS2ToInt(String s) {
        char a[] = s.toCharArray();
        return transHexS1ToInt(a[0]) * 16 + transHexS1ToInt(a[1]);
    }

    /**
     * 计量单位标准化
     *
     * @param productProtocol 水表、热表读取值实体类
     */
    public static void dataUnitToStandard(ProductProtocol productProtocol) {
        BigDecimal b = new BigDecimal(0);
        double f1;
        try {
            //冷量单位转换
            double sumCool = productProtocol.getSumCool();
            productProtocol.setSumCool(0);
            switch (productProtocol.getSumCoolUnit()) {
                case "W*h":
                    b = new BigDecimal(sumCool / 1000);
                    break;
                case "10W*h":
                    b = new BigDecimal(sumCool / 100);
                    break;
                case "100W*h":
                    b = new BigDecimal(sumCool / 10);
                    break;
                case "kW*h":
                    b = new BigDecimal(sumCool);
                    break;
                case "MW*h":
                    b = new BigDecimal(sumCool * 1000);
                    break;
                case "J":
                    b = new BigDecimal(sumCool * 0.0000002778);
                    break;
                case "10J":
                    b = new BigDecimal(sumCool * 0.000002778);
                    break;
                case "100J":
                    b = new BigDecimal(sumCool * 0.00002778);
                    break;
                case "kJ":
                    b = new BigDecimal(sumCool * 0.0002778);
                    break;
                case "10kJ":
                    b = new BigDecimal(sumCool * 0.002778);
                    break;
                case "100kJ":
                    b = new BigDecimal(sumCool * 0.02778);
                    break;
                case "MJ":
                    b = new BigDecimal(sumCool * 0.2778);
                    break;
                case "10MJ":
                    b = new BigDecimal(sumCool * 2.778);
                    break;
                case "100MJ":
                    b = new BigDecimal(sumCool * 27.78);
                    break;
                case "GJ":
                    b = new BigDecimal(sumCool * 277.8);
                    break;
                case "10GJ":
                    b = new BigDecimal(sumCool * 2778);
                    break;
                case "100GJ":
                    b = new BigDecimal(sumCool * 27778);
                    break;
                default:
                    break;
            }
            f1 = b.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
            productProtocol.setSumCool(f1);
            productProtocol.setSumCoolUnit("kW*h");

            //热量单位转换
            double sumHeat = productProtocol.getSumHeat();
            productProtocol.setSumHeat(0);
            switch (productProtocol.getSumHeatUnit()) {
                case "W*h":
                    b = new BigDecimal(sumHeat / 1000);
                    break;
                case "10W*h":
                    b = new BigDecimal(sumHeat / 100);
                    break;
                case "100W*h":
                    b = new BigDecimal(sumHeat / 10);
                    break;
                case "kW*h":
                    b = new BigDecimal(sumHeat);
                    break;
                case "10kW*h":
                    b = new BigDecimal(sumHeat * 10);
                    break;
                case "100kW*h":
                    b = new BigDecimal(sumHeat * 100);
                    break;
                case "MW*h":
                    b = new BigDecimal(sumHeat * 1000);
                    break;
                case "J":
                    b = new BigDecimal(sumHeat * 0.0000002778);
                    break;
                case "10J":
                    b = new BigDecimal(sumHeat * 0.000002778);
                    break;
                case "100J":
                    b = new BigDecimal(sumHeat * 0.00002778);
                    break;
                case "kJ":
                    b = new BigDecimal(sumHeat * 0.0002778);
                    break;
                case "10kJ":
                    b = new BigDecimal(sumHeat * 0.002778);
                    break;
                case "100kJ":
                    b = new BigDecimal(sumHeat * 0.02778);
                    break;
                case "MJ":
                    b = new BigDecimal(sumHeat * 0.2778);
                    break;
                case "10MJ":
                    b = new BigDecimal(sumHeat * 2.778);
                    break;
                case "100MJ":
                    b = new BigDecimal(sumHeat * 27.78);
                    break;
                case "GJ":
                    b = new BigDecimal(sumHeat * 277.8);
                    break;
                case "10GJ":
                    b = new BigDecimal(sumHeat * 2778);
                    break;
                case "100GJ":
                    b = new BigDecimal(sumHeat * 27778);
                    break;
                default:
                    break;
            }
            f1 = b.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
            productProtocol.setSumHeat(f1);
            productProtocol.setSumHeatUnit("kW*h");

            //累计流量单位转换
            double total = productProtocol.getTotal();
            productProtocol.setTotal(0);
            switch (productProtocol.getTotalUnit()) {
                case "mL":
                    b = new BigDecimal(total / 1000000);
                    break;
                case "10mL":
                    b = new BigDecimal(total / 100000);
                    break;
                case "100mL":
                    b = new BigDecimal(total / 10000);
                    break;
                case "L":
                    b = new BigDecimal(total / 1000);
                    break;
                case "10L":
                    b = new BigDecimal(total / 100);
                    break;
                case "100L":
                    b = new BigDecimal(total / 10);
                    break;
                case "m³":
                    b = new BigDecimal(total);
                    break;
                default:
                    break;
            }
            f1 = b.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
            productProtocol.setTotal(f1);
            productProtocol.setTotalUnit("m³");

            //反向流量单位转换
            double oppositeTotal = productProtocol.getOppositeTotal();
            productProtocol.setOppositeTotal(0);
            switch (productProtocol.getOppositeTotalUnit()) {
                case "mL":
                    b = new BigDecimal(oppositeTotal / 1000000);
                    break;
                case "10mL":
                    b = new BigDecimal(oppositeTotal / 100000);
                    break;
                case "100mL":
                    b = new BigDecimal(oppositeTotal / 10000);
                    break;
                case "L":
                    b = new BigDecimal(oppositeTotal / 1000);
                    break;
                case "10L":
                    b = new BigDecimal(oppositeTotal / 100);
                    break;
                case "100L":
                    b = new BigDecimal(oppositeTotal / 10);
                    break;
                case "m³":
                    b = new BigDecimal(oppositeTotal);
                    break;
                default:
                    break;
            }
            f1 = b.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
            productProtocol.setOppositeTotal(f1);
            productProtocol.setOppositeTotalUnit("m³");

            //功率单位转换
            double power = productProtocol.getPower();
            productProtocol.setPower(0);
            switch (productProtocol.getSumHeatUnit()) {
                case "W":
                    b = new BigDecimal(power / 1000);
                    break;
                case "10W":
                    b = new BigDecimal(power / 100);
                    break;
                case "100W":
                    b = new BigDecimal(power / 10);
                    break;
                case "kW":
                    b = new BigDecimal(power);
                    break;
                case "10kW":
                    b = new BigDecimal(power * 10);
                    break;
                case "100kW":
                    b = new BigDecimal(power * 100);
                    break;
                case "MW":
                    b = new BigDecimal(power * 1000);
                    break;
                case "GJ/h":
                    b = new BigDecimal(power * 1000 / 3.6);
                    break;
                default:
                    break;
            }
            f1 = b.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
            productProtocol.setPower(f1);
            productProtocol.setPowerUnit("kW");

            //瞬时流速单位转换
            double flowRate = productProtocol.getFlowRate();
            productProtocol.setFlowRate(0);
            switch (productProtocol.getFlowRateUnit()) {
                case "mL/h":
                    b = new BigDecimal(flowRate / 1000000);
                    break;
                case "10mL/h":
                    b = new BigDecimal(flowRate / 100000);
                    break;
                case "100mL/h":
                    b = new BigDecimal(flowRate / 10000);
                    break;
                case "L/h":
                    b = new BigDecimal(flowRate / 1000);
                    break;
                case "10L/h":
                    b = new BigDecimal(flowRate / 100);
                    break;
                case "100L/h":
                    b = new BigDecimal(flowRate / 10);
                    break;
                case "m³/h":
                    b = new BigDecimal(flowRate);
                    break;
                case "10m³/h":
                    b = new BigDecimal(flowRate * 10);
                    break;
                case "100m³/h":
                    b = new BigDecimal(flowRate * 100);
                    break;
                default:
                    break;
            }
            f1 = b.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
            productProtocol.setFlowRate(f1);
            productProtocol.setFlowRateUnit("m³/h");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化实体类
     *
     * @param productProtocol 水表、热表读取值实体类对象
     */
    private static void initProtocol(ProductProtocol productProtocol) {
        productProtocol.setMeterID("-");
        productProtocol.setMBUSAddress("-");
        productProtocol.setProductTypeTX("20");
        productProtocol.setSumCool(0);
        productProtocol.setSumCoolUnit("kW*h");
        productProtocol.setSumHeat(0);
        productProtocol.setSumHeatUnit("kW*h");
        productProtocol.setTotal(0);
        productProtocol.setTotalUnit("m³");
        productProtocol.setOppositeTotal(0);
        productProtocol.setOppositeTotalUnit("m³");
        productProtocol.setPower(0);
        productProtocol.setPowerUnit("kW");
        productProtocol.setFlowRate(0);
        productProtocol.setFlowRateUnit("m³/h");
        productProtocol.setSumOpenValveM(0);
        productProtocol.setCloseTime("-");
        productProtocol.setLosePowerTime("-");
        productProtocol.setLoseConTime("-");
        productProtocol.setInsideT(0);
        productProtocol.setInsideTSet("-");
        productProtocol.setValveStatus("-");
        productProtocol.setT1InP(0);
        productProtocol.setT2InP(0);
        productProtocol.setWorkTimeInP(0);
        productProtocol.setTimeInP("-");
        productProtocol.setVol("-");
        productProtocol.setStatus("-");
    }

    /**
     * 解析热表读表数据
     *
     * @param Data
     * @param productProtocol
     * @return
     */
    private static int MCAnalysisJSMTRxStr(String Data, ProductProtocol productProtocol) {
        int r;
        initProtocol(productProtocol);
        productProtocol.setProductTypeTX(Data.substring(2, 2 + 2));
        productProtocol.setMeterID(Data.substring(4, 4 + 8));
        String DataArea = Data.substring(22, 22 + 94);
        DecimalFormat df = new DecimalFormat(".##");
        try {
            double volF = AnalysisUtils.HexS2ToInt(DataArea.substring(4, 4 + 2)) * 2 / 100f;
            productProtocol.setVol(df.format(volF));
            r = 1;
        } catch (Exception e) {
            productProtocol.setVol("err 电压 " + DataArea.substring(4, 4 + 2));
            r = 0;
        }
        if (r == 0) {
            return r;
        }

        try {
            String coolS = DataArea.substring(12, 12 + 2) + DataArea.substring(10, 10 + 2) + DataArea.substring(8, 8 + 2) + DataArea.substring(6, 6 + 2);
            productProtocol.setSumCool(Double.valueOf(coolS));
            r = 1;
        } catch (Exception e) {
            productProtocol.setSumCool(0);
            r = 0;
        }
        if (DataArea.substring(14, 14 + 2).equals("02")) {
            productProtocol.setSumCoolUnit("W*h");
        } else if (DataArea.substring(14, 14 + 2).equals("03")) {
            productProtocol.setSumCoolUnit("10W*h");
        } else if (DataArea.substring(14, 14 + 2).equals("04")) {
            productProtocol.setSumCoolUnit("100W*h");
        } else if (DataArea.substring(14, 14 + 2).equals("05")) {
            productProtocol.setSumCoolUnit("kW*h");
        } else if (DataArea.substring(14, 14 + 2).equals("0B")) {
            productProtocol.setSumCoolUnit("kJ");
        } else if (DataArea.substring(14, 14 + 2).equals("0C")) {
            productProtocol.setSumCoolUnit("10kJ");
        } else if (DataArea.substring(14, 14 + 2).equals("0D")) {
            productProtocol.setSumCoolUnit("100kJ");
        } else if (DataArea.substring(14, 14 + 2).equals("0E")) {
            productProtocol.setSumCoolUnit("MJ");
        } else if (DataArea.substring(14, 14 + 2).equals("0F")) {
            productProtocol.setSumCoolUnit("10MJ");
        } else if (DataArea.substring(14, 14 + 2).equals("10")) {
            productProtocol.setSumCoolUnit("100MJ");
        } else if (DataArea.substring(14, 14 + 2).equals("11")) {
            productProtocol.setSumCoolUnit("GJ");
        } else if (DataArea.substring(14, 14 + 2).equals("12")) {
            productProtocol.setSumCoolUnit("10GJ");
        } else if (DataArea.substring(14, 14 + 2).equals("13")) {
            productProtocol.setSumCoolUnit("100GJ");
        } else {
            productProtocol.setSumCoolUnit("err " + DataArea.substring(14, 14 + 2));
        }
        if (r == 0) {
            return r;
        }

        try {
            String heatS = DataArea.substring(22, 22 + 2) + DataArea.substring(20, 20 + 2) + DataArea.substring(18, 18 + 2) + DataArea.substring(16, 16 + 2);
            productProtocol.setSumHeat(Double.valueOf(heatS));
            r = 1;
        } catch (Exception e) {
            productProtocol.setSumHeat(0);
            r = 0;
        }
        if (DataArea.substring(24, 24 + 2).equals("02")) {
            productProtocol.setSumHeatUnit("W*h");
        } else if (DataArea.substring(24, 24 + 2).equals("03")) {
            productProtocol.setSumHeatUnit("10W*h");
        } else if (DataArea.substring(24, 24 + 2).equals("04")) {
            productProtocol.setSumHeatUnit("100W*h");
        } else if (DataArea.substring(24, 24 + 2).equals("05")) {
            productProtocol.setSumHeatUnit("kW*h");
        } else if (DataArea.substring(24, 24 + 2).equals("0B")) {
            productProtocol.setSumHeatUnit("kJ");
        } else if (DataArea.substring(24, 24 + 2).equals("0C")) {
            productProtocol.setSumHeatUnit("10kJ");
        } else if (DataArea.substring(24, 24 + 2).equals("0D")) {
            productProtocol.setSumHeatUnit("100kJ");
        } else if (DataArea.substring(24, 24 + 2).equals("0E")) {
            productProtocol.setSumHeatUnit("MJ");
        } else if (DataArea.substring(24, 24 + 2).equals("0F")) {
            productProtocol.setSumHeatUnit("10MJ");
        } else if (DataArea.substring(24, 24 + 2).equals("10")) {
            productProtocol.setSumHeatUnit("100MJ");
        } else if (DataArea.substring(24, 24 + 2).equals("11")) {
            productProtocol.setSumHeatUnit("GJ");
        } else if (DataArea.substring(24, 24 + 2).equals("12")) {
            productProtocol.setSumHeatUnit("10GJ");
        } else if (DataArea.substring(24, 24 + 2).equals("13")) {
            productProtocol.setSumHeatUnit("100GJ");
        } else {
            productProtocol.setSumHeatUnit("kW*h");
        }
        if (r == 0) {
            return r;
        }

        try {
            String powerS = DataArea.substring(32, 32 + 2) + DataArea.substring(30, 30 + 2) + DataArea.substring(28, 28 + 2) + DataArea.substring(26, 26 + 2);
            productProtocol.setPower(Double.valueOf(powerS));
            r = 1;
        } catch (Exception e) {
            productProtocol.setPower(0);
            r = 0;
        }
        if (DataArea.substring(34, 34 + 2).equals("14")) {
            productProtocol.setPowerUnit("W");
        } else if (DataArea.substring(34, 34 + 2).equals("15")) {
            productProtocol.setPowerUnit("10W");
        } else if (DataArea.substring(34, 34 + 2).equals("16")) {
            productProtocol.setPowerUnit("100W");
        } else if (DataArea.substring(34, 34 + 2).equals("17")) {
            productProtocol.setPowerUnit("kW");
        } else {
            productProtocol.setPowerUnit("kW");
        }
        if (r == 0) {
            return r;
        }

        try {
            String flowrateS = DataArea.substring(42, 42 + 2) + DataArea.substring(40, 40 + 2) + DataArea.substring(38, 38 + 2) + DataArea.substring(36, 36 + 2);
            productProtocol.setFlowRate(Double.valueOf(flowrateS));
            r = 1;
        } catch (Exception e) {
            productProtocol.setFlowRate(0);
            r = 0;
        }
        if (DataArea.substring(44, 44 + 2).equals("32")) {
            productProtocol.setFlowRateUnit("L/h");
        } else if (DataArea.substring(44, 44 + 2).equals("33")) {
            productProtocol.setFlowRateUnit("10L/h");
        } else if (DataArea.substring(44, 44 + 2).equals("34")) {
            productProtocol.setFlowRateUnit("100L/h");
        } else if (DataArea.substring(44, 44 + 2).equals("35")) {
            productProtocol.setFlowRateUnit("m³/h");
        } else {
            productProtocol.setFlowRateUnit("m³/h");
        }
        if (r == 0) {
            return r;
        }

        try {
            String totalS = DataArea.substring(52, 52 + 2) + DataArea.substring(50, 50 + 2) + DataArea.substring(48, 48 + 2) + DataArea.substring(46, 46 + 2);
            productProtocol.setTotal(Double.valueOf(totalS));
            r = 1;
        } catch (Exception e) {
            productProtocol.setTotal(0);
            r = 0;
        }
        if (DataArea.substring(54, 54 + 2).equals("26")) {
            productProtocol.setTotalUnit("mL");
        } else if (DataArea.substring(54, 54 + 2).equals("27")) {
            productProtocol.setTotalUnit("10mL");
        } else if (DataArea.substring(54, 54 + 2).equals("28")) {
            productProtocol.setTotalUnit("100mL");
        } else if (DataArea.substring(54, 54 + 2).equals("29")) {
            productProtocol.setTotalUnit("L");
        } else if (DataArea.substring(54, 54 + 2).equals("2A")) {
            productProtocol.setTotalUnit("10L");
        } else if (DataArea.substring(54, 54 + 2).equals("2B")) {
            productProtocol.setTotalUnit("100L");
        } else if (DataArea.substring(54, 54 + 2).equals("2C")) {
            productProtocol.setTotalUnit("m³");
        } else {
            productProtocol.setTotalUnit("m³");
        }
        if (r == 0) {
            return r;
        }

        try {
            String t1S = DataArea.substring(60, 60 + 2) + DataArea.substring(58, 58 + 2) + "." + DataArea.substring(56, 56 + 2);
            productProtocol.setT1InP(Double.valueOf(t1S));
            r = 1;
        } catch (Exception e) {
            productProtocol.setT1InP(0);
            r = 0;
        }
        if (r == 0) {
            return r;
        }

        try {
            String t2S = DataArea.substring(66, 66 + 2) + DataArea.substring(64, 64 + 2) + "." + DataArea.substring(62, 62 + 2);
            productProtocol.setT2InP(Double.valueOf(t2S));
            r = 1;
        } catch (Exception e) {
            productProtocol.setT2InP(0);
            r = 0;
        }
        if (r == 0) {
            return r;
        }

        try {
            String worktimeinpS = DataArea.substring(72, 72 + 2) + DataArea.substring(70, 70 + 2) + DataArea.substring(68, 68 + 2);
            productProtocol.setWorkTimeInP(Integer.valueOf(worktimeinpS));
            r = 1;
        } catch (Exception e) {
            productProtocol.setWorkTimeInP(0);
            r = 0;
        }
        if (r == 0) {
            return r;
        }

        String timeinpS = DataArea.substring(86, 86 + 2) + DataArea.substring(84, 84 + 2) + "-" + DataArea.substring(82, 82 + 2) + "-" + DataArea.substring(80, 80 + 2) + " " + DataArea.substring(78, 78 + 2) + ":" + DataArea.substring(76, 76 + 2) + ":" + DataArea.substring(74, 74 + 2);
        productProtocol.setTimeInP(timeinpS);
        String statusb = AnalysisUtils.HexS2ToBinS(DataArea.substring(90, 90 + 2));
        String status = "";
        char a[] = statusb.toCharArray();
        if (a[2] == '1') {
            status = status + "低电";
        } else if (a[3] == '1') {
            status = status + "流量故障";
        } else if (a[4] == '1') {
            status = status + "PCB故障";
        } else if (a[5] == '1') {
            status = status + "无水";
        } else if (a[6] == '1') {
            status = status + "铂电阻断路";
        } else if (a[7] == '1') {
            status = status + "铂电阻短路";
        }
        if (a[0] == '0' & a[1] == '0' & a[2] == '0' & a[3] == '0' & a[4] == '0' & a[5] == '0' & a[6] == '0' & a[7] == '0') {
            status = "正常";
        }
        productProtocol.setStatus(status);
        return r;
    }

    /**
     * 解析水表读表数据
     *
     * @param data          蓝牙工具返回的数据
     * @param productProtocol 表数据实体类
     * @return 0或1
     */
    private static int analysisWaterMeterData(String data, ProductProtocol productProtocol) {
        int r;
        initProtocol(productProtocol);
        //解析产品类型
        productProtocol.setProductTypeTX(data.substring(2, 2 + 2));
        //解析表号
        productProtocol.setMeterID(data.substring(4, 4 + 8));
        String dataArea = data.substring(22, 22 + 46);
        //解析电压
        DecimalFormat df = new DecimalFormat(".##");
        try {
            double volF = AnalysisUtils.HexS2ToInt(dataArea.substring(4, 4 + 2)) * 2 / 100f;
            productProtocol.setVol(df.format(volF));
            r = 1;
        } catch (Exception e) {
            productProtocol.setVol("err 电压 " + dataArea.substring(4, 4 + 2));
            r = 0;
        }
        if (r == 0) {
            return r;
        }
        //解析累计流量
        try {
            String totalS = dataArea.substring(12, 12 + 2) + dataArea.substring(10, 10 + 2) + dataArea.substring(8, 8 + 2) + dataArea.substring(6, 6 + 2);
            productProtocol.setTotal(Double.valueOf(totalS));
            r = 1;
        } catch (Exception e) {
            productProtocol.setTotal(0);
            r = 0;
        }
        //解析累计流量单位
        productProtocol.setTotalUnit(AnalysisUtils.getFlowUnit(dataArea.substring(14, 14 + 2)));
        if (r == 0) {
            return r;
        }
        //解析瞬时流速
        try {
            String flowrateS = dataArea.substring(22, 22 + 2) + dataArea.substring(20, 20 + 2) + dataArea.substring(18, 18 + 2) + dataArea.substring(16, 16 + 2);
            productProtocol.setFlowRate(Double.valueOf(flowrateS));
            r = 1;
        } catch (Exception e) {
            productProtocol.setFlowRate(0);
            r = 0;
        }
        //解析瞬时流速单位
        productProtocol.setFlowRateUnit(AnalysisUtils.getFlowRateUnit(dataArea.substring(24, 24 + 2)));
        if (r == 0) {
            return r;
        }
        //解析表具时间
        String timeInp = dataArea.substring(38, 38 + 2) + dataArea.substring(36, 36 + 2) + "-" + dataArea.substring(34, 34 + 2) + "-" + dataArea.substring(32, 32 + 2) + " " + dataArea.substring(30, 30 + 2) + ":" + dataArea.substring(28, 28 + 2) + ":" + dataArea.substring(26, 26 + 2);
        productProtocol.setTimeInP(timeInp);
        //解析故障信息
        String meterStatus = AnalysisUtils.HexS2ToBinS(dataArea.substring(40, 40 + 2));
        productProtocol.setStatus(AnalysisUtils.getMeterWarning(meterStatus.toCharArray()));
        //解析阀门状态
        String valveStatusS = dataArea.substring(42, 42 + 2);
        productProtocol.setValveStatus(AnalysisUtils.getValveStatus(valveStatusS));
        return r;
    }

    /**
     * 解析户用水表读表数据（不带压力）
     *
     * @param data          蓝牙工具返回的数据
     * @param productProtocol 表数据实体类
     * @return 0或1
     */
    private static int analysisWaterMeterData1(String data, ProductProtocol productProtocol) {
        int r;
        initProtocol(productProtocol);
        //解析产品类型
        productProtocol.setProductTypeTX(data.substring(2, 2 + 2));
        //解析表号
        productProtocol.setMeterID(data.substring(4, 4 + 8));
        String dataArea = data.substring(22, 22 + 56);
        //解析电压
        DecimalFormat df = new DecimalFormat(".##");
        try {
            double volF = AnalysisUtils.HexS2ToInt(dataArea.substring(4, 4 + 2)) * 2 / 100f;
            productProtocol.setVol(df.format(volF));
            r = 1;
        } catch (Exception e) {
            productProtocol.setVol("err 电压 " + dataArea.substring(4, 4 + 2));
            r = 0;
        }
        if (r == 0) {
            return r;
        }
        //解析累计流量
        try {
            String totalS = dataArea.substring(12, 12 + 2) + dataArea.substring(10, 10 + 2) + dataArea.substring(8, 8 + 2) + dataArea.substring(6, 6 + 2);
            productProtocol.setTotal(Double.valueOf(totalS));
            r = 1;
        } catch (Exception e) {
            productProtocol.setTotal(0);
            r = 0;
        }
        //解析累计流量单位
        productProtocol.setTotalUnit(AnalysisUtils.getFlowUnit(dataArea.substring(14, 14 + 2)));
        if (r == 0) {
            return r;
        }
        //解析反向流量
        try {
            String oppositetotalS = dataArea.substring(22, 22 + 2) + dataArea.substring(20, 20 + 2) + dataArea.substring(18, 18 + 2) + dataArea.substring(16, 16 + 2);
            productProtocol.setOppositeTotal(Double.valueOf(oppositetotalS));
            r = 1;
        } catch (Exception e) {
            productProtocol.setOppositeTotal(0);
            r = 0;
        }
        //解析反向流量单位
        productProtocol.setOppositeTotalUnit(AnalysisUtils.getFlowUnit(dataArea.substring(24, 24 + 2)));
        if (r == 0) {
            return r;
        }
        //解析瞬时流速
        try {
            String flowrateS = dataArea.substring(32, 32 + 2) + dataArea.substring(30, 30 + 2) + dataArea.substring(28, 28 + 2) + dataArea.substring(26, 26 + 2);
            productProtocol.setFlowRate(Double.valueOf(flowrateS));
            r = 1;
        } catch (Exception e) {
            productProtocol.setFlowRate(0);
            r = 0;
        }
        //解析瞬时流速单位
        productProtocol.setFlowRateUnit(AnalysisUtils.getFlowRateUnit(dataArea.substring(34, 34 + 2)));
        if (r == 0) {
            return r;
        }
        //解析表具时间
        String timeinpS = dataArea.substring(48, 48 + 2) + dataArea.substring(46, 46 + 2) + "-" + dataArea.substring(44, 44 + 2) + "-" + dataArea.substring(42, 42 + 2) + " " + dataArea.substring(40, 40 + 2) + ":" + dataArea.substring(38, 38 + 2) + ":" + dataArea.substring(36, 36 + 2);
        productProtocol.setTimeInP(timeinpS);
        //解析故障信息
        String meterStatus = AnalysisUtils.HexS2ToBinS(dataArea.substring(50, 50 + 2));
        productProtocol.setStatus(AnalysisUtils.getMeterWarning(meterStatus.toCharArray()));
        //解析阀门状态
        String valveStatusS = dataArea.substring(52, 52 + 2);
        productProtocol.setValveStatus(AnalysisUtils.getValveStatus(valveStatusS));
        return r;
    }

    /**
     * 解析管网大口径水表读表数据（带压力）
     *
     * @param data          蓝牙工具返回的数据
     * @param productProtocol 表数据实体类
     * @return 0或1
     */
    private static int analysisWaterMeterData2(String data, ProductProtocol productProtocol) {
        int r;
        initProtocol(productProtocol);
        //解析产品类型
        productProtocol.setProductTypeTX(data.substring(2, 2 + 2));
        //解析表号
        productProtocol.setMeterID(data.substring(4, 4 + 8));
        String dataArea = data.substring(22, 22 + 66);
        //解析电压
        DecimalFormat df = new DecimalFormat(".##");
        try {
            double volF = AnalysisUtils.HexS2ToInt(dataArea.substring(4, 4 + 2)) * 2 / 100f;
            productProtocol.setVol(df.format(volF));
            r = 1;
        } catch (Exception e) {
            productProtocol.setVol("err 电压 " + dataArea.substring(4, 4 + 2));
            r = 0;
        }
        if (r == 0) {
            return r;
        }
        //解析累计流量
        try {
            String totalS = dataArea.substring(12, 12 + 2) + dataArea.substring(10, 10 + 2) + dataArea.substring(8, 8 + 2) + dataArea.substring(6, 6 + 2);
            productProtocol.setTotal(Double.valueOf(totalS));
            r = 1;
        } catch (Exception e) {
            productProtocol.setTotal(0);
            r = 0;
        }
        //解析累计流量单位
        productProtocol.setTotalUnit(AnalysisUtils.getFlowUnit(dataArea.substring(14, 14 + 2)));
        if (r == 0) {
            return r;
        }
        //解析反向流量
        try {
            String oppositetotalS = dataArea.substring(22, 22 + 2) + dataArea.substring(20, 20 + 2) + dataArea.substring(18, 18 + 2) + dataArea.substring(16, 16 + 2);
            productProtocol.setOppositeTotal(Double.valueOf(oppositetotalS));
            r = 1;
        } catch (Exception e) {
            productProtocol.setOppositeTotal(0);
            r = 0;
        }
        //解析反向流量单位
        productProtocol.setOppositeTotalUnit(AnalysisUtils.getFlowUnit(dataArea.substring(24, 24 + 2)));
        if (r == 0) {
            return r;
        }
        //解析瞬时流速
        try {
            String flowrateS = dataArea.substring(32, 32 + 2) + dataArea.substring(30, 30 + 2) + dataArea.substring(28, 28 + 2) + dataArea.substring(26, 26 + 2);
            productProtocol.setFlowRate(Double.valueOf(flowrateS));
            r = 1;
        } catch (Exception e) {
            productProtocol.setFlowRate(0);
            r = 0;
        }
        //解析瞬时流速单位
        productProtocol.setFlowRateUnit(AnalysisUtils.getFlowRateUnit(dataArea.substring(34, 34 + 2)));
        if (r == 0) {
            return r;
        }
        //解析表具时间
        String timeinpS = dataArea.substring(58, 58 + 2) + dataArea.substring(56, 56 + 2) + "-" + dataArea.substring(54, 54 + 2) + "-" + dataArea.substring(52, 52 + 2) + " " + dataArea.substring(50, 50 + 2) + ":" + dataArea.substring(48, 48 + 2) + ":" + dataArea.substring(46, 46 + 2);
        productProtocol.setTimeInP(timeinpS);
        //解析故障信息
        String meterStatus = AnalysisUtils.HexS2ToBinS(dataArea.substring(60, 60 + 2));
        productProtocol.setStatus(AnalysisUtils.getMeterWarning(meterStatus.toCharArray()));
        //解析阀门状态
        String valveStatusS = dataArea.substring(62, 62 + 2);
        productProtocol.setValveStatus(AnalysisUtils.getValveStatus(valveStatusS));
        return r;
    }


    /**
     * 解析消火栓读表数据
     * 6859781680170011118121901FBF000000002B000000002B00000000330B69000000353916190817208506007C16
     *
     * @param Data          蓝牙工具返回的数据
     * @param productProtocol 表数据实体类
     * @return 0或1
     */
    private static int analysisHydrantData(String Data, ProductProtocol productProtocol) {
        int r;
        initProtocol(productProtocol);
        productProtocol.setProductTypeTX(Data.substring(2, 2 + 2));
        productProtocol.setMeterID(Data.substring(4, 4 + 8));
        String DataArea = Data.substring(22, 22 + 46);

        try {
            String totalS = DataArea.substring(12, 12 + 2) + DataArea.substring(10, 10 + 2) + DataArea.substring(8, 8 + 2) + DataArea.substring(6, 6 + 2);
            productProtocol.setTotal(Double.valueOf(totalS));
            r = 1;
        } catch (Exception e) {
            productProtocol.setTotal(0);
            r = 0;
        }
        if (DataArea.substring(14, 14 + 2).equals("26")) {
            productProtocol.setTotalUnit("mL");
        } else if (DataArea.substring(14, 14 + 2).equals("27")) {
            productProtocol.setTotalUnit("10mL");
        } else if (DataArea.substring(14, 14 + 2).equals("28")) {
            productProtocol.setTotalUnit("100mL");
        } else if (DataArea.substring(14, 14 + 2).equals("29")) {
            productProtocol.setTotalUnit("L");
        } else if (DataArea.substring(14, 14 + 2).equals("2A")) {
            productProtocol.setTotalUnit("10L");
        } else if (DataArea.substring(14, 14 + 2).equals("2B")) {
            productProtocol.setTotalUnit("100L");
        } else if (DataArea.substring(14, 14 + 2).equals("2C")) {
            productProtocol.setTotalUnit("m³");
        } else {
            productProtocol.setTotalUnit("m³");
        }
        if (r == 0) {
            return r;
        }

        try {
            String flowrateS = DataArea.substring(22, 22 + 2) + DataArea.substring(20, 20 + 2) + DataArea.substring(18, 18 + 2) + DataArea.substring(16, 16 + 2);
            productProtocol.setFlowRate(Double.valueOf(flowrateS));
            r = 1;
        } catch (Exception e) {
            productProtocol.setFlowRate(0);
            r = 0;
        }
        if (DataArea.substring(24, 24 + 2).equals("32")) {
            productProtocol.setFlowRateUnit("L/h");
        } else if (DataArea.substring(24, 24 + 2).equals("33")) {
            productProtocol.setFlowRateUnit("10L/h");
        } else if (DataArea.substring(24, 24 + 2).equals("34")) {
            productProtocol.setFlowRateUnit("100L/h");
        } else if (DataArea.substring(24, 24 + 2).equals("35")) {
            productProtocol.setFlowRateUnit("m³/h");
        } else {
            productProtocol.setFlowRateUnit("m³/h");
        }
        if (r == 0) {
            return r;
        }

        String timeinpS = DataArea.substring(38, 38 + 2) + DataArea.substring(36, 36 + 2) + "-" + DataArea.substring(34, 34 + 2) + "-" + DataArea.substring(32, 32 + 2) + " " + DataArea.substring(30, 30 + 2) + ":" + DataArea.substring(28, 28 + 2) + ":" + DataArea.substring(26, 26 + 2);
        productProtocol.setTimeInP(timeinpS);
        String statusb = AnalysisUtils.HexS2ToBinS(DataArea.substring(40, 40 + 2));
        String status = "";
        char a[] = statusb.toCharArray();
        if (a[2] == '1') {
            status = status + "低电";
        } else if (a[3] == '1') {
            status = status + "流量故障";
        } else if (a[4] == '1') {
            status = status + "PCB故障";
        } else if (a[5] == '1') {
            status = status + "无水";
        } else if (a[6] == '1') {
            status = status + "铂电阻断路";
        } else if (a[7] == '1') {
            status = status + "铂电阻短路";
        }
        if (a[0] == '0' & a[1] == '0' & a[2] == '0' & a[3] == '0' & a[4] == '0' & a[5] == '0' & a[6] == '0' & a[7] == '0') {
            status = "正常";
        }
        productProtocol.setStatus(status);

        String valveStatusS = DataArea.substring(42, 42 + 2);
        switch (valveStatusS) {
            case "55":
                productProtocol.setValveStatus("阀开");
                break;
            case "99":
                productProtocol.setValveStatus("阀关");
                break;
            case "59":
                productProtocol.setValveStatus("异常");
                break;
            default:
                productProtocol.setValveStatus("-");
                break;
        }
        return r;
    }


    private static int MCAnalysisSDSLRxStr(String Data, ProductProtocol productProtocol) {
        int r = 0;
        initProtocol(productProtocol);
        productProtocol.setProductTypeTX(Data.substring(2, 2 + 2));
        productProtocol.setMeterID(Data.substring(4, 4 + 2) + Data.substring(6, 6 + 2) + Data.substring(8, 8 + 2) + Data.substring(10, 10 + 2));
        String DataArea = Data.substring(22, 22 + 94);

        try {
            String heatS = DataArea.substring(22, 22 + 2) + DataArea.substring(20, 20 + 2) + DataArea.substring(18, 18 + 2) + DataArea.substring(16, 16 + 2);
            productProtocol.setSumHeat(Double.valueOf(heatS));
            r = 1;
        } catch (Exception e) {
            productProtocol.setSumHeat(0);
            r = 0;
        }
        if (DataArea.substring(24, 24 + 2).equals("02")) {
            productProtocol.setSumHeatUnit("W*h");
        } else if (DataArea.substring(24, 24 + 2).equals("03")) {
            productProtocol.setSumHeatUnit("10W*h");
        } else if (DataArea.substring(24, 24 + 2).equals("04")) {
            productProtocol.setSumHeatUnit("100W*h");
        } else if (DataArea.substring(24, 24 + 2).equals("05")) {
            productProtocol.setSumHeatUnit("kW*h");
        } else if (DataArea.substring(24, 24 + 2).equals("0B")) {
            productProtocol.setSumHeatUnit("kJ");
        } else if (DataArea.substring(24, 24 + 2).equals("0C")) {
            productProtocol.setSumHeatUnit("10kJ");
        } else if (DataArea.substring(24, 24 + 2).equals("0D")) {
            productProtocol.setSumHeatUnit("100kJ");
        } else if (DataArea.substring(24, 24 + 2).equals("0E")) {
            productProtocol.setSumHeatUnit("MJ");
        } else if (DataArea.substring(24, 24 + 2).equals("0F")) {
            productProtocol.setSumHeatUnit("10MJ");
        } else if (DataArea.substring(24, 24 + 2).equals("10")) {
            productProtocol.setSumHeatUnit("100MJ");
        } else if (DataArea.substring(24, 24 + 2).equals("11")) {
            productProtocol.setSumHeatUnit("GJ");
        } else if (DataArea.substring(24, 24 + 2).equals("12")) {
            productProtocol.setSumHeatUnit("10GJ");
        } else if (DataArea.substring(24, 24 + 2).equals("13")) {
            productProtocol.setSumHeatUnit("100GJ");
        } else {
            productProtocol.setSumHeatUnit("kW*h");
        }
        if (r == 0) {
            return r;
        }

        try {
            String powerS = DataArea.substring(32, 32 + 2) + DataArea.substring(30, 30 + 2) + DataArea.substring(28, 28 + 2) + DataArea.substring(26, 26 + 2);
            productProtocol.setPower(Double.valueOf(powerS));
            r = 1;
        } catch (Exception e) {
            productProtocol.setPower(0);
            r = 0;
        }
        if (DataArea.substring(34, 34 + 2).equals("14")) {
            productProtocol.setPowerUnit("W");
        } else if (DataArea.substring(34, 34 + 2).equals("15")) {
            productProtocol.setPowerUnit("10W");
        } else if (DataArea.substring(34, 34 + 2).equals("16")) {
            productProtocol.setPowerUnit("100W");
        } else if (DataArea.substring(34, 34 + 2).equals("17")) {
            productProtocol.setPowerUnit("kW");
        } else {
            productProtocol.setPowerUnit("kW");
        }
        if (r == 0) {
            return r;
        }
        try {
            String flowrateS = DataArea.substring(42, 42 + 2) + DataArea.substring(40, 40 + 2) + DataArea.substring(38, 38 + 2) + DataArea.substring(36, 36 + 2);
            productProtocol.setFlowRate(Double.valueOf(flowrateS));
            r = 1;
        } catch (Exception e) {
            productProtocol.setFlowRate(0);
            r = 0;
        }
        if (DataArea.substring(44, 44 + 2).equals("32")) {
            productProtocol.setFlowRateUnit("L/h");
        } else if (DataArea.substring(44, 44 + 2).equals("33")) {
            productProtocol.setFlowRateUnit("10L/h");
        } else if (DataArea.substring(44, 44 + 2).equals("34")) {
            productProtocol.setFlowRateUnit("100L/h");
        } else if (DataArea.substring(44, 44 + 2).equals("35")) {
            productProtocol.setFlowRateUnit("m³/h");
        } else {
            productProtocol.setFlowRateUnit("m³/h");
        }
        if (r == 0) {
            return r;
        }

        try {
            String totalS = DataArea.substring(52, 52 + 2) + DataArea.substring(50, 50 + 2) + DataArea.substring(48, 48 + 2) + DataArea.substring(46, 46 + 2);
            productProtocol.setTotal(Double.valueOf(totalS));
            r = 1;
        } catch (Exception e) {
            productProtocol.setTotal(0);
            r = 0;
        }
        if (DataArea.substring(54, 54 + 2).equals("26")) {
            productProtocol.setTotalUnit("mL");
        } else if (DataArea.substring(54, 54 + 2).equals("27")) {
            productProtocol.setTotalUnit("10mL");
        } else if (DataArea.substring(54, 54 + 2).equals("28")) {
            productProtocol.setTotalUnit("100mL");
        } else if (DataArea.substring(54, 54 + 2).equals("29")) {
            productProtocol.setTotalUnit("L");
        } else if (DataArea.substring(54, 54 + 2).equals("2A")) {
            productProtocol.setTotalUnit("10L");
        } else if (DataArea.substring(54, 54 + 2).equals("2B")) {
            productProtocol.setTotalUnit("100L");
        } else if (DataArea.substring(54, 54 + 2).equals("2C")) {
            productProtocol.setTotalUnit("m³");
        } else {
            productProtocol.setTotalUnit("m³");
        }
        if (r == 0) {
            return r;
        }

        try {
            String t1S = DataArea.substring(60, 60 + 2) + DataArea.substring(58, 58 + 2) + "." + DataArea.substring(56, 56 + 2);
            productProtocol.setT1InP(Double.valueOf(t1S));
            r = 1;
        } catch (Exception e) {
            productProtocol.setT1InP(0);
            r = 0;
        }
        if (r == 0) {
            return r;
        }

        try {
            String t2S = DataArea.substring(66, 66 + 2) + DataArea.substring(64, 64 + 2) + "." + DataArea.substring(62, 62 + 2);
            productProtocol.setT2InP(Double.valueOf(t2S));
            r = 1;
        } catch (Exception e) {
            productProtocol.setT2InP(0);
            r = 0;
        }
        if (r == 0) {
            return r;
        }

        try {
            String worktimeinpS = DataArea.substring(72, 72 + 2) + DataArea.substring(70, 70 + 2) + DataArea.substring(68, 68 + 2);
            productProtocol.setWorkTimeInP(Integer.valueOf(worktimeinpS));
            r = 1;
        } catch (Exception e) {
            productProtocol.setWorkTimeInP(0);
            r = 0;
        }
        if (r == 0) {
            return r;
        }

        String timeinpS = DataArea.substring(86, 86 + 2) + DataArea.substring(84, 84 + 2) + "-" + DataArea.substring(82, 82 + 2) + "-" + DataArea.substring(80, 80 + 2) + " " + DataArea.substring(78, 78 + 2) + ":" + DataArea.substring(76, 76 + 2) + ":" + DataArea.substring(74, 74 + 2);
        productProtocol.setTimeInP(timeinpS);
        String statusb = AnalysisUtils.HexS2ToBinS(DataArea.substring(90, 90 + 2));
        String status = "";
        char b[] = statusb.toCharArray();
        if (b[2] == '1') {
            status = status + "低电";
        }
        if (b[3] == '1') {
            status = status + "超量程";
        }
        if (b[4] == '1') {
            status = status + "PCB故障";
        }
        if (b[5] == '1') {
            status = status + "无水";
        }
        if (b[6] == '1') {
            status = status + "sener断路";
        }
        if (b[7] == '1') {
            status = status + "sener短路";
        }
        if (b[0] == '0' & b[1] == '0' & b[2] == '0' & b[3] == '0' & b[4] == '0' & b[5] == '0' & b[6] == '0' & b[7] == '0') {
            status = "正常";
        }
        productProtocol.setStatus(status);
        return r;
    }

    /**
     * 解析热表读表数据
     *
     * @param Data
     * @param productProtocol
     * @return
     */
    private static int MVCAnalysisJSMTRxStr(String Data, ProductProtocol productProtocol) {
        int r;
        initProtocol(productProtocol);
        productProtocol.setProductTypeTX(Data.substring(2, 2 + 2));
        productProtocol.setMeterID(Data.substring(4, 4 + 8));
        String DataArea = Data.substring(22, 22 + 94);
        DecimalFormat df = new DecimalFormat(".##");
        try {
            double volF = AnalysisUtils.HexS2ToInt(DataArea.substring(4, 4 + 2)) * 2 / 100f;
            productProtocol.setVol(df.format(volF));
            r = 1;
        } catch (Exception e) {
            productProtocol.setVol("err 电压 " + DataArea.substring(4, 4 + 2));
            r = 0;
        }
        if (r == 0) {
            return r;
        }

        productProtocol.setCloseTime("20" + DataArea.substring(10, 10 + 2) + "-" + DataArea.substring(8, 8 + 2) + "-" + DataArea.substring(6, 6 + 2));

        try {
            String insideTS = DataArea.substring(14, 14 + 2) + DataArea.substring(12, 12 + 2);
            double insideT = Integer.valueOf(insideTS) / 10f;
            productProtocol.setInsideT(Double.valueOf(df.format(insideT)));
            r = 1;
        } catch (Exception e) {
            productProtocol.setInsideT(0);
            r = 0;
        }
        if (r == 0) {
            return r;
        }

        try {
            String heatS = DataArea.substring(22, 22 + 2) + DataArea.substring(20, 20 + 2) + DataArea.substring(18, 18 + 2) + DataArea.substring(16, 16 + 2);
            productProtocol.setSumHeat(Double.valueOf(heatS));
            r = 1;
        } catch (Exception e) {
            productProtocol.setSumHeat(0);
            r = 0;
        }
        if (DataArea.substring(24, 24 + 2).equals("02")) {
            productProtocol.setSumHeatUnit("W*h");
        } else if (DataArea.substring(24, 24 + 2).equals("03")) {
            productProtocol.setSumHeatUnit("10W*h");
        } else if (DataArea.substring(24, 24 + 2).equals("04")) {
            productProtocol.setSumHeatUnit("100W*h");
        } else if (DataArea.substring(24, 24 + 2).equals("05")) {
            productProtocol.setSumHeatUnit("kW*h");
        } else if (DataArea.substring(24, 24 + 2).equals("0B")) {
            productProtocol.setSumHeatUnit("kJ");
        } else if (DataArea.substring(24, 24 + 2).equals("0C")) {
            productProtocol.setSumHeatUnit("10kJ");
        } else if (DataArea.substring(24, 24 + 2).equals("0D")) {
            productProtocol.setSumHeatUnit("100kJ");
        } else if (DataArea.substring(24, 24 + 2).equals("0E")) {
            productProtocol.setSumHeatUnit("MJ");
        } else if (DataArea.substring(24, 24 + 2).equals("0F")) {
            productProtocol.setSumHeatUnit("10MJ");
        } else if (DataArea.substring(24, 24 + 2).equals("10")) {
            productProtocol.setSumHeatUnit("100MJ");
        } else if (DataArea.substring(24, 24 + 2).equals("11")) {
            productProtocol.setSumHeatUnit("GJ");
        } else if (DataArea.substring(24, 24 + 2).equals("12")) {
            productProtocol.setSumHeatUnit("10GJ");
        } else if (DataArea.substring(24, 24 + 2).equals("13")) {
            productProtocol.setSumHeatUnit("100GJ");
        } else {
            productProtocol.setSumHeatUnit("kW*h");
        }
        if (r == 0) {
            return r;
        }
        String permissionb = AnalysisUtils.HexS2ToBinS(DataArea.substring(52, 52 + 2)) + AnalysisUtils.HexS2ToBinS(DataArea.substring(50, 50 + 2));
        String permission = "";
        char a[] = permissionb.toCharArray();
        if (a[1] == '1') {
            permission = permission + "冷量使能+";
        }
        if (a[2] == '1') {
            permission = permission + "远程预付费使能+";
        }
        if (a[3] == '1') {
            permission = permission + "远程不供电使能+";
        }
        if (a[4] == '1') {
            permission = permission + "自检使能+";
        }
        if (a[5] == '1') {
            permission = permission + "无线不使能+";
        }
        if (a[6] == '1') {
            permission = permission + "远程设定温度使能+";
        }
        if (a[7] == '0') {
            permission = permission + "停止通讯-开阀+";
        }
        if (a[7] == '1') {
            permission = permission + "停止通讯-关阀+";
        }
        if (a[8] == '1') {
            permission = permission + "停止通讯使能+";
        }
        if (a[9] == '0') {
            permission = permission + "断电-开阀+";
        }
        if (a[9] == '1') {
            permission = permission + "断电-关阀+";
        }
        if (a[10] == '1') {
            permission = permission + "断电使能+";
        }
        if (a[11] == '1') {
            permission = permission + "室温测量使能+";
        }
        if (a[12] == '1') {
            permission = permission + "T2测量使能+";
        }
        if (a[13] == '1') {
            permission = permission + "T1测量使能+";
        }
        if (a[14] == '1') {
            permission = permission + "截止日期使能+";
        }
        if (a[15] == '1') {
            permission = permission + "远程锁闭使能+";
        }
        //productProtocol.setPermission(DataArea.substring(52,52+2)+DataArea.substring(50,50+2)+"-"+permission);

        try {
            String insideTSetS = DataArea.substring(34, 34 + 2) + DataArea.substring(32, 32 + 2);
            double insideTSet = Integer.valueOf(insideTSetS) / 10f;
            productProtocol.setInsideT(Double.valueOf(df.format(insideTSet)));
            r = 1;
        } catch (Exception e) {
            productProtocol.setInsideT(0);
            r = 0;
        }
        if (r == 0) {
            return r;
        }

        try {
            String flowrateS = DataArea.substring(42, 42 + 2) + DataArea.substring(40, 40 + 2) + DataArea.substring(38, 38 + 2) + DataArea.substring(36, 36 + 2);
            productProtocol.setFlowRate(Double.valueOf(flowrateS));
            r = 1;
        } catch (Exception e) {
            productProtocol.setFlowRate(0);
            r = 0;
        }
        if (DataArea.substring(44, 44 + 2).equals("32")) {
            productProtocol.setFlowRateUnit("L/h");
        } else if (DataArea.substring(44, 44 + 2).equals("33")) {
            productProtocol.setFlowRateUnit("10L/h");
        } else if (DataArea.substring(44, 44 + 2).equals("34")) {
            productProtocol.setFlowRateUnit("100L/h");
        } else if (DataArea.substring(44, 44 + 2).equals("35")) {
            productProtocol.setFlowRateUnit("m³/h");
        } else {
            productProtocol.setFlowRateUnit("m³/h");
        }
        if (r == 0) {
            return r;
        }

        try {
            String totalS = DataArea.substring(52, 52 + 2) + DataArea.substring(50, 50 + 2) + DataArea.substring(48, 48 + 2) + DataArea.substring(46, 46 + 2);
            productProtocol.setTotal(Double.valueOf(totalS));
            r = 1;
        } catch (Exception e) {
            productProtocol.setTotal(0);
            r = 0;
        }
        if (DataArea.substring(54, 54 + 2).equals("26")) {
            productProtocol.setTotalUnit("mL");
        } else if (DataArea.substring(54, 54 + 2).equals("27")) {
            productProtocol.setTotalUnit("10mL");
        } else if (DataArea.substring(54, 54 + 2).equals("28")) {
            productProtocol.setTotalUnit("100mL");
        } else if (DataArea.substring(54, 54 + 2).equals("29")) {
            productProtocol.setTotalUnit("L");
        } else if (DataArea.substring(54, 54 + 2).equals("2A")) {
            productProtocol.setTotalUnit("10L");
        } else if (DataArea.substring(54, 54 + 2).equals("2B")) {
            productProtocol.setTotalUnit("100L");
        } else if (DataArea.substring(54, 54 + 2).equals("2C")) {
            productProtocol.setTotalUnit("m³");
        } else {
            productProtocol.setTotalUnit("m³");
        }
        if (r == 0) {
            return r;
        }

        try {
            String t1S = DataArea.substring(60, 60 + 2) + DataArea.substring(58, 58 + 2) + "." + DataArea.substring(56, 56 + 2);
            productProtocol.setT1InP(Double.valueOf(t1S));
            r = 1;
        } catch (Exception e) {
            productProtocol.setT1InP(0);
            r = 0;
        }
        if (r == 0) {
            return r;
        }

        try {
            String t2S = DataArea.substring(66, 66 + 2) + DataArea.substring(64, 64 + 2) + "." + DataArea.substring(62, 62 + 2);
            productProtocol.setT2InP(Double.valueOf(t2S));
            r = 1;
        } catch (Exception e) {
            productProtocol.setT2InP(0);
            r = 0;
        }
        if (r == 0) {
            return r;
        }

        try {
            String worktimeinpS = DataArea.substring(72, 72 + 2) + DataArea.substring(70, 70 + 2) + DataArea.substring(68, 68 + 2);
            productProtocol.setWorkTimeInP(Integer.valueOf(worktimeinpS));
            r = 1;
        } catch (Exception e) {
            productProtocol.setWorkTimeInP(0);
            r = 0;
        }
        if (r == 0) {
            return r;
        }

        String timeinpS = DataArea.substring(86, 86 + 2) + DataArea.substring(84, 84 + 2) + "-" + DataArea.substring(82, 82 + 2) + "-" + DataArea.substring(80, 80 + 2) + " " + DataArea.substring(78, 78 + 2) + ":" + DataArea.substring(76, 76 + 2) + ":" + DataArea.substring(74, 74 + 2);
        productProtocol.setTimeInP(timeinpS);

        String valveStatusS = DataArea.substring(88, 88 + 2);
        switch (valveStatusS) {
            case "55":
                productProtocol.setValveStatus("阀开");
                r = 1;
                break;
            case "99":
                productProtocol.setValveStatus("阀关");
                r = 1;
                break;
            case "59":
                productProtocol.setValveStatus("异常");
                r = 1;
                break;
            default:
                productProtocol.setValveStatus("-");
                r = 0;
                break;
        }
        String statusb = AnalysisUtils.HexS2ToBinS(DataArea.substring(90, 90 + 2));
        String status = "";
        char b[] = statusb.toCharArray();
        if (b[2] == '1') {
            status = status + "低电";
        } else if (b[3] == '1') {
            status = status + "流量故障";
        } else if (b[4] == '1') {
            status = status + "PCB故障";
        } else if (b[5] == '1') {
            status = status + "无水";
        } else if (b[6] == '1') {
            status = status + "铂电阻断路";
        } else if (b[7] == '1') {
            status = status + "铂电阻短路";
        }
        if (b[0] == '0' & b[1] == '0' & b[2] == '0' & b[3] == '0' & b[4] == '0' & b[5] == '0' & b[6] == '0' & b[7] == '0') {
            status = "正常";
        }
        productProtocol.setStatus(status);
        return r;
    }

    private static int VAnalysisJSMTRxStr(String Data, ProductProtocol productProtocol) {
        int r;
        initProtocol(productProtocol);
        productProtocol.setProductTypeTX(Data.substring(2, 2 + 2));
        productProtocol.setMeterID(Data.substring(4, 4 + 8));
        String valveStatusS = Data.substring(28, 28 + 2);
        switch (valveStatusS) {
            case "55":
                productProtocol.setValveStatus("阀开");
                break;
            case "99":
                productProtocol.setValveStatus("阀关");
                break;
            case "59":
                productProtocol.setValveStatus("异常");
                break;
            default:
                productProtocol.setValveStatus("-");
                break;
        }
        r = 1;
        return r;
    }

    //解析读取到的参数（水表、热表、消火栓）
    private static int analysisParameter(String Data, ParameterProtocol parameterprotocol) {
        int r;
        try {
            parameterprotocol.setMeterid(Data.substring(4, 4 + 8));
            parameterprotocol.setQnx(Data.substring(32, 32 + 2) + Data.substring(30, 30 + 2) + Data.substring(28, 28 + 2));
            parameterprotocol.setQn2x(Data.substring(38, 38 + 2) + Data.substring(36, 36 + 2) + Data.substring(34, 34 + 2));
            parameterprotocol.setQn1x(Data.substring(44, 44 + 2) + Data.substring(42, 42 + 2) + Data.substring(40, 40 + 2));
            parameterprotocol.setQminx(Data.substring(50, 50 + 2) + Data.substring(48, 48 + 2) + Data.substring(46, 46 + 2));
            parameterprotocol.setT1x(Data.substring(56, 56 + 2) + Data.substring(54, 54 + 2) + Data.substring(52, 52 + 2));
            parameterprotocol.setT2x(Data.substring(62, 62 + 2) + Data.substring(60, 60 + 2) + Data.substring(58, 58 + 2));
            parameterprotocol.setT0_10(Data.substring(68, 68 + 2) + Data.substring(66, 66 + 2) + Data.substring(64, 64 + 2));
            parameterprotocol.setT10_30(Data.substring(74, 74 + 2) + Data.substring(72, 72 + 2) + Data.substring(70, 70 + 2));
            parameterprotocol.setT30_50(Data.substring(80, 80 + 2) + Data.substring(78, 78 + 2) + Data.substring(76, 76 + 2));
            parameterprotocol.setT50_70(Data.substring(86, 86 + 2) + Data.substring(84, 84 + 2) + Data.substring(82, 82 + 2));
            parameterprotocol.setT70_90(Data.substring(92, 92 + 2) + Data.substring(90, 90 + 2) + Data.substring(88, 88 + 2));
            parameterprotocol.setChecktime(Data.substring(98, 98 + 2) + Data.substring(96, 96 + 2) + Data.substring(94, 94 + 2));
            parameterprotocol.setAmendx(Data.substring(104, 104 + 2) + Data.substring(102, 102 + 2) + Data.substring(100, 100 + 2));
            parameterprotocol.setUnitStr(Data.substring(110, 110 + 2) + Data.substring(108, 108 + 2) + Data.substring(106, 106 + 2));
            parameterprotocol.setSlope(Data.substring(116, 116 + 2) + Data.substring(114, 114 + 2) + Data.substring(112, 112 + 2));
            parameterprotocol.setStartx(Data.substring(122, 122 + 2) + Data.substring(120, 120 + 2) + Data.substring(118, 118 + 2));
            parameterprotocol.setMetersize(Data.substring(126, 126 + 2) + Data.substring(124, 124 + 2));
            parameterprotocol.setVer(Data.substring(134, 134 + 2) + Data.substring(132, 132 + 2) + Data.substring(130, 130 + 2));
            parameterprotocol.setDivid1(Data.substring(140, 140 + 2) + Data.substring(138, 138 + 2) + Data.substring(136, 136 + 2));
            parameterprotocol.setDivid2(Data.substring(146, 146 + 2) + Data.substring(144, 144 + 2) + Data.substring(142, 142 + 2));
            parameterprotocol.setDivid3(Data.substring(152, 152 + 2) + Data.substring(150, 150 + 2) + Data.substring(148, 148 + 2));
            parameterprotocol.setSleeptime(Data.substring(162, 162 + 2) + Data.substring(160, 160 + 2));
            parameterprotocol.setPointwhere(Data.substring(158, 158 + 2) + Data.substring(156, 156 + 2) + Data.substring(154, 154 + 2));
            r = 1;
        } catch (Exception e) {
            e.printStackTrace();
            r = 0;
        }
        return r;
    }

    private static int AAnalysisJSMTRxStr(String Data, ProductProtocol productProtocol) {
        int r;
        initProtocol(productProtocol);
        productProtocol.setProductTypeTX(Data.substring(2, 2 + 2));
        productProtocol.setMeterID(Data.substring(4, 4 + 8));
        String DataArea = Data.substring(22, 22 + 94);
        DecimalFormat df = new DecimalFormat(".##");
        try {
            double volF = AnalysisUtils.HexS2ToInt(DataArea.substring(4, 4 + 2)) * 2 / 100f;
            productProtocol.setVol(df.format(volF));
            r = 1;
        } catch (Exception e) {
            productProtocol.setVol("err 电压 " + DataArea.substring(4, 4 + 2));
            r = 0;
        }
        if (r == 0) {
            return r;
        }

        String closetime = "20" + DataArea.substring(10, 10 + 2) + "-" + DataArea.substring(8, 8 + 2) + "-" + DataArea.substring(6, 6 + 2);
        productProtocol.setCloseTime(closetime);
        String losePowerTime = DataArea.substring(14, 14 + 2) + DataArea.substring(12, 12 + 2);
        productProtocol.setLosePowerTime(losePowerTime);
        try {
            String sumOpenValveMS = DataArea.substring(20, 20 + 2) + DataArea.substring(18, 18 + 2) + DataArea.substring(16, 16 + 2);
            productProtocol.setSumOpenValveM(Integer.valueOf(sumOpenValveMS));
            double sumopenvalveh = Integer.valueOf(sumOpenValveMS) / 60f;
            r = 1;
        } catch (Exception e) {
            productProtocol.setSumOpenValveM(0);
            r = 0;
        }
        if (r == 0) {
            return r;
        }
        String loseConTime = DataArea.substring(24, 24 + 2) + DataArea.substring(22, 22 + 2);
        productProtocol.setLoseConTime(loseConTime);
        try {
            String insideTS = DataArea.substring(28, 28 + 2) + DataArea.substring(26, 26 + 2);
            double insidet = Integer.valueOf(insideTS) / 10f;
            productProtocol.setInsideT(Double.valueOf(df.format(insidet)));
            r = 1;
        } catch (Exception e) {
            productProtocol.setInsideT(0);
            r = 0;
        }
        if (r == 0) {
            return r;
        }
        try {
            String insideTSetS = DataArea.substring(32, 32 + 2) + DataArea.substring(30, 30 + 2);
            double insideTSet = Integer.valueOf(insideTSetS) / 10f;
            productProtocol.setInsideTSet(df.format(insideTSet));
            r = 1;
        } catch (Exception e) {
            productProtocol.setInsideTSet("0");
            r = 0;
        }
        if (r == 0) {
            return r;
        }
        String loseConTimeSet = DataArea.substring(38, 38 + 2) + DataArea.substring(36, 36 + 2);
        //productProtocol.setLoseConTimeSet(loseConTimeSet);
        String losePowerTimeSet = DataArea.substring(42, 42 + 2) + DataArea.substring(40, 40 + 2);
        //productProtocol.setLosePowerTimeSet(losePowerTimeSet);
        String sumOpenValveSS = DataArea.substring(48, 48 + 2) + DataArea.substring(46, 46 + 2);
        String permissionb = AnalysisUtils.HexS2ToBinS(DataArea.substring(52, 52 + 2)) + AnalysisUtils.HexS2ToBinS(DataArea.substring(50, 50 + 2));
        String permission = "";
        char a[] = permissionb.toCharArray();
        if (a[3] == '1') {
            permission = permission + "远程不供电使能+";
        }
        if (a[4] == '1') {
            permission = permission + "自检使能+";
        }
        if (a[5] == '1') {
            permission = permission + "无线不使能+";
        }
        if (a[6] == '1') {
            permission = permission + "远程设定温度使能+";
        }
        if (a[7] == '0') {
            permission = permission + "停止通讯-开阀+";
        }
        if (a[7] == '1') {
            permission = permission + "停止通讯-关阀+";
        }
        if (a[8] == '1') {
            permission = permission + "停止通讯使能+";
        }
        if (a[9] == '0') {
            permission = permission + "断电-开阀+";
        }
        if (a[9] == '1') {
            permission = permission + "断电-关阀+";
        }
        if (a[10] == '1') {
            permission = permission + "断电使能+";
        }
        if (a[11] == '1') {
            permission = permission + "室温测量使能+";
        }
        if (a[12] == '1') {
            permission = permission + "T2测量使能+";
        }
        if (a[13] == '1') {
            permission = permission + "T1测量使能+";
        }
        if (a[14] == '1') {
            permission = permission + "截止日期使能+";
        }
        if (a[15] == '1') {
            permission = permission + "远程锁闭使能+";
        }
        //productProtocol.setPermission(DataArea.substring(52,52+2)+DataArea.substring(50,50+2)+"-"+permission);
        try {
            String t2S = DataArea.substring(66, 66 + 2) + DataArea.substring(64, 64 + 2) + "." + DataArea.substring(62, 62 + 2);
            productProtocol.setT2InP(Double.valueOf(t2S));
            r = 1;
        } catch (Exception e) {
            productProtocol.setT2InP(0);
            r = 0;
        }
        if (r == 0) {
            return r;
        }
        try {
            String worktimeinpS = DataArea.substring(72, 72 + 2) + DataArea.substring(70, 70 + 2) + DataArea.substring(68, 68 + 2);
            productProtocol.setWorkTimeInP(Integer.valueOf(worktimeinpS));
            r = 1;
        } catch (Exception e) {
            productProtocol.setWorkTimeInP(0);
            r = 0;
        }
        if (r == 0) {
            return r;
        }

        String timeinpS = DataArea.substring(86, 86 + 2) + DataArea.substring(84, 84 + 2) + "-" + DataArea.substring(82, 82 + 2) + "-" + DataArea.substring(80, 80 + 2) + " " + DataArea.substring(78, 78 + 2) + ":" + DataArea.substring(76, 76 + 2) + ":" + DataArea.substring(74, 74 + 2);
        productProtocol.setTimeInP(timeinpS);

        String valveStatusS = DataArea.substring(88, 88 + 2);
        switch (valveStatusS) {
            case "55":
                productProtocol.setValveStatus("阀开");
                break;
            case "99":
                productProtocol.setValveStatus("阀关");
                break;
            case "59":
                productProtocol.setValveStatus("异常");
                break;
            default:
                productProtocol.setValveStatus("-");
                break;
        }
        return r;
    }

    private static int VSmartAnalysisJSMTRxStr(String Data, ProductProtocol productProtocol) {
        int r;
        initProtocol(productProtocol);
        productProtocol.setProductTypeTX(Data.substring(2, 2 + 2));
        productProtocol.setMeterID(Data.substring(4, 4 + 8));
        String DataArea = Data.substring(22, 22 + 94);
        DecimalFormat df = new DecimalFormat(".##");
        try {
            double volF = AnalysisUtils.HexS2ToInt(DataArea.substring(4, 4 + 2)) * 2 / 100f;
            productProtocol.setVol(df.format(volF));
            r = 1;
        } catch (Exception e) {
            productProtocol.setVol("err 电压 " + DataArea.substring(4, 4 + 2));
            r = 0;
        }
        if (r == 0) {
            return r;
        }

        String closetime = "20" + DataArea.substring(10, 10 + 2) + "-" + DataArea.substring(8, 8 + 2) + "-" + DataArea.substring(6, 6 + 2);
        productProtocol.setCloseTime(closetime);


        try {
            String sumHeatS = DataArea.substring(40, 40 + 2) + DataArea.substring(38, 38 + 2) + DataArea.substring(36, 36 + 2) + DataArea.substring(34, 34 + 2);
            productProtocol.setSumHeat(Integer.valueOf(sumHeatS));
            productProtocol.setSumHeatUnit("kW*h");
            r = 1;
        } catch (Exception e) {
            productProtocol.setSumHeat(0);
            productProtocol.setSumHeatUnit("kW*h");
            r = 0;
        }
        if (r == 0) {
            return r;
        }
        String permissionb = AnalysisUtils.HexS2ToBinS(DataArea.substring(52, 52 + 2)) + AnalysisUtils.HexS2ToBinS(DataArea.substring(50, 50 + 2));
        String permission = "";
        char a[] = permissionb.toCharArray();
        if (a[4] == '1') {
            permission = permission + "自检使能+";
        }
        if (a[14] == '1') {
            permission = permission + "截止日期使能+";
        }
        if (a[15] == '1') {
            permission = permission + "远程锁闭使能+";
        }
        //productProtocol.setPermission(DataArea.substring(52,52+2)+DataArea.substring(50,50+2)+"-"+permission);

        try {
            int worktimeinp = AnalysisUtils.HexS4ToInt(DataArea.substring(72, 72 + 2) + DataArea.substring(70, 70 + 2));
            productProtocol.setWorkTimeInP(worktimeinp);
            r = 1;
        } catch (Exception e) {
            productProtocol.setWorkTimeInP(0);
            r = 0;
        }
        if (r == 0) {
            return r;
        }

        String timeinpS = DataArea.substring(86, 86 + 2) + DataArea.substring(84, 84 + 2) + "-" + DataArea.substring(82, 82 + 2) + "-" + DataArea.substring(80, 80 + 2) + " " + DataArea.substring(78, 78 + 2) + ":" + DataArea.substring(76, 76 + 2) + ":" + DataArea.substring(74, 74 + 2);
        productProtocol.setTimeInP(timeinpS);

        String valveStatusS = DataArea.substring(88, 88 + 2);
        switch (valveStatusS) {
            case "55":
                productProtocol.setValveStatus("阀开");
                break;
            case "99":
                productProtocol.setValveStatus("阀关");
                break;
            case "59":
                productProtocol.setValveStatus("异常");
                break;
            default:
                productProtocol.setValveStatus("-");
                break;
        }
        r = 1;
        return r;
    }

    private static int MEAnalysisRxStr(String Data, ProductProtocol productProtocol) {
        int r = 1;
        initProtocol(productProtocol);
        productProtocol.setProductTypeTX("20");
        productProtocol.setMBUSAddress(Data.substring(10, 10 + 2));
        productProtocol.setMeterID(Data.substring(20, 20 + 2) + Data.substring(18, 18 + 2) + Data.substring(16, 16 + 2) + Data.substring(14, 14 + 2));
        String LenS = Data.substring(2, 2 + 2);
        String DataArea = Data.substring(8, 8 + AnalysisUtils.HexS2ToInt(LenS) * 2);
        String statusb = AnalysisUtils.HexS2ToBinS(DataArea.substring(24, 24 + 2));
        String status = "";
        char b[] = statusb.toCharArray();
        if (b[2] == '1') {
            status = status + "低电";
        }
        if (b[3] == '1') {
            status = status + "超量程";
        }
        if (b[4] == '1') {
            status = status + "PCB故障";
        }
        if (b[5] == '1') {
            status = status + "无水";
        }
        if (b[6] == '1') {
            status = status + "sener断路";
        }
        if (b[7] == '1') {
            status = status + "sener短路";
        }
        if (b[0] == '0' & b[1] == '0' & b[2] == '0' & b[3] == '0' & b[4] == '0' & b[5] == '0' & b[6] == '0' & b[7] == '0') {
            status = "正常";
        }
        productProtocol.setStatus(status);

        String heat = "0";
        String otherenergy = "";
        productProtocol.setSumHeatUnit("kWh");
        int SumHeat1 = DataArea.indexOf("0C03");
        int SumHeat2 = DataArea.indexOf("0C04");
        int SumHeat3 = DataArea.indexOf("0C05");
        int SumHeat4 = DataArea.indexOf("0C06");
        int SumHeat5 = DataArea.indexOf("0C07");
        int SumHeat6 = DataArea.indexOf("0CFB00");
        int SumHeat7 = DataArea.indexOf("0CFB01");
        int SumHeat8 = DataArea.indexOf("0C0E");
        int SumHeat9 = DataArea.indexOf("0C0F");
        int SumHeat10 = DataArea.indexOf("0CFB08");
        int SumHeat11 = DataArea.indexOf("0CFB09");
        if (SumHeat1 >= 0) {
            heat = DataArea.substring(SumHeat1 + 10, SumHeat1 + 10 + 2) + DataArea.substring(SumHeat1 + 8, SumHeat1 + 8 + 2) + DataArea.substring(SumHeat1 + 6, SumHeat1 + 6 + 2) + DataArea.substring(SumHeat1 + 4, SumHeat1 + 4 + 2);
            try {
                otherenergy = DataArea.substring(SumHeat1 + 4, DataArea.length() - SumHeat1 - 4);
                r = 1;
            } catch (Exception e) {
                r = -1;
            }
            productProtocol.setSumHeatUnit("W*h");
        } else if (SumHeat2 >= 0) {
            heat = DataArea.substring(SumHeat2 + 10, SumHeat2 + 10 + 2) + DataArea.substring(SumHeat2 + 8, SumHeat2 + 8 + 2) + DataArea.substring(SumHeat2 + 6, SumHeat2 + 6 + 2) + DataArea.substring(SumHeat2 + 4, SumHeat2 + 4 + 2);
            try {
                otherenergy = DataArea.substring(SumHeat2 + 4, DataArea.length() - SumHeat2 - 4);
                r = 1;
            } catch (Exception e) {
                r = -2;
            }
            productProtocol.setSumHeatUnit("10W*h");
        } else if (SumHeat3 >= 0) {
            heat = DataArea.substring(SumHeat3 + 10, SumHeat3 + 10 + 2) + DataArea.substring(SumHeat3 + 8, SumHeat3 + 8 + 2) + DataArea.substring(SumHeat3 + 6, SumHeat3 + 6 + 2) + DataArea.substring(SumHeat3 + 4, SumHeat3 + 4 + 2);
            try {
                otherenergy = DataArea.substring(SumHeat3 + 4, DataArea.length() - SumHeat3 - 4);
                r = 1;
            } catch (Exception e) {
                r = -3;
            }
            productProtocol.setSumHeatUnit("100W*h");
        } else if (SumHeat4 >= 0) {
            heat = DataArea.substring(SumHeat4 + 10, SumHeat4 + 10 + 2) + DataArea.substring(SumHeat4 + 8, SumHeat4 + 8 + 2) + DataArea.substring(SumHeat4 + 6, SumHeat4 + 6 + 2) + DataArea.substring(SumHeat4 + 4, SumHeat4 + 4 + 2);
            try {
                otherenergy = DataArea.substring(SumHeat4 + 4, DataArea.length() - SumHeat4 - 4);
                r = 1;
            } catch (Exception e) {
                r = -4;
            }
            productProtocol.setSumHeatUnit("kW*h");
        } else if (SumHeat5 >= 0) {
            heat = DataArea.substring(SumHeat5 + 10, SumHeat5 + 10 + 2) + DataArea.substring(SumHeat5 + 8, SumHeat5 + 8 + 2) + DataArea.substring(SumHeat5 + 6, SumHeat5 + 6 + 2) + DataArea.substring(SumHeat5 + 4, SumHeat5 + 4 + 2);
            try {
                otherenergy = DataArea.substring(SumHeat5 + 4, DataArea.length() - SumHeat5 - 4);
                r = 1;
            } catch (Exception e) {
                r = -5;
            }
            productProtocol.setSumHeatUnit("10kW*h");
        } else if (SumHeat6 >= 0) {
            heat = DataArea.substring(SumHeat6 + 10, SumHeat6 + 10 + 2) + DataArea.substring(SumHeat6 + 8, SumHeat6 + 8 + 2) + DataArea.substring(SumHeat6 + 6, SumHeat6 + 6 + 2) + DataArea.substring(SumHeat6 + 4, SumHeat6 + 4 + 2);
            try {
                otherenergy = DataArea.substring(SumHeat6 + 4, DataArea.length() - SumHeat6 - 4);
                r = 1;
            } catch (Exception e) {
                r = -6;
            }
            productProtocol.setSumHeatUnit("100kW*h");
        } else if (SumHeat7 >= 0) {
            heat = DataArea.substring(SumHeat7 + 10, SumHeat7 + 10 + 2) + DataArea.substring(SumHeat7 + 8, SumHeat7 + 8 + 2) + DataArea.substring(SumHeat7 + 6, SumHeat7 + 6 + 2) + DataArea.substring(SumHeat7 + 4, SumHeat7 + 4 + 2);
            try {
                otherenergy = DataArea.substring(SumHeat7 + 4, DataArea.length() - SumHeat7 - 4);
                r = 1;
            } catch (Exception e) {
                r = -7;
            }
            productProtocol.setSumHeatUnit("MW*h");
        } else if (SumHeat8 >= 0) {
            heat = DataArea.substring(SumHeat8 + 10, SumHeat8 + 10 + 2) + DataArea.substring(SumHeat8 + 8, SumHeat8 + 8 + 2) + DataArea.substring(SumHeat8 + 6, SumHeat8 + 6 + 2) + DataArea.substring(SumHeat8 + 4, SumHeat8 + 4 + 2);
            try {
                otherenergy = DataArea.substring(SumHeat8 + 4, DataArea.length() - SumHeat8 - 4);
                r = 1;
            } catch (Exception e) {
                r = -8;
            }
            productProtocol.setSumHeatUnit("MJ");
        } else if (SumHeat9 >= 0) {
            heat = DataArea.substring(SumHeat9 + 10, SumHeat9 + 10 + 2) + DataArea.substring(SumHeat9 + 8, SumHeat9 + 8 + 2) + DataArea.substring(SumHeat9 + 6, SumHeat9 + 6 + 2) + DataArea.substring(SumHeat9 + 4, SumHeat9 + 4 + 2);
            try {
                otherenergy = DataArea.substring(SumHeat9 + 4, DataArea.length() - SumHeat9 - 4);
                r = 1;
            } catch (Exception e) {
                r = -9;
            }
            productProtocol.setSumHeatUnit("10MJ");
        } else if (SumHeat10 >= 0) {
            heat = DataArea.substring(SumHeat10 + 10, SumHeat10 + 10 + 2) + DataArea.substring(SumHeat10 + 8, SumHeat10 + 8 + 2) + DataArea.substring(SumHeat10 + 6, SumHeat10 + 6 + 2) + DataArea.substring(SumHeat10 + 4, SumHeat10 + 4 + 2);
            try {
                otherenergy = DataArea.substring(SumHeat10 + 4, DataArea.length() - SumHeat10 - 4);
                r = 1;
            } catch (Exception e) {
                r = -10;
            }
            productProtocol.setSumHeatUnit("100MJ");
        } else if (SumHeat11 >= 0) {
            heat = DataArea.substring(SumHeat11 + 10, SumHeat11 + 10 + 2) + DataArea.substring(SumHeat11 + 8, SumHeat11 + 8 + 2) + DataArea.substring(SumHeat11 + 6, SumHeat11 + 6 + 2) + DataArea.substring(SumHeat11 + 4, SumHeat11 + 4 + 2);
            try {
                otherenergy = DataArea.substring(SumHeat11 + 4, DataArea.length() - SumHeat11 - 4);
                r = 1;
            } catch (Exception e) {
                r = -11;
            }
            productProtocol.setSumHeatUnit("GJ");
        }
        if (r == 1) {
            productProtocol.setSumHeat(Double.valueOf(heat));
        } else {
            productProtocol.setSumHeat(0);
            return r;
        }


        //双热单热处理部分
        int Heat1 = otherenergy.indexOf("0C03");
        int Heat2 = otherenergy.indexOf("0C04");
        int Heat3 = otherenergy.indexOf("0C05");
        int Heat4 = otherenergy.indexOf("0C06");
        int Heat5 = otherenergy.indexOf("0C07");
        int Heat6 = otherenergy.indexOf("0CFB00");
        int Heat7 = otherenergy.indexOf("0CFB01");
        int Heat8 = otherenergy.indexOf("0C0E");
        int Heat9 = otherenergy.indexOf("0C0F");
        int Heat10 = otherenergy.indexOf("0CFB08");
        int Heat11 = otherenergy.indexOf("0CFB09");
        if (Heat1 >= 0) {
            heat = otherenergy.substring(Heat1 + 10, Heat1 + 10 + 2) + otherenergy.substring(Heat1 + 8, Heat1 + 8 + 2) + otherenergy.substring(Heat1 + 6, Heat1 + 6 + 2) + otherenergy.substring(Heat1 + 4, Heat1 + 4 + 2);
            try {
                productProtocol.setSumCool(productProtocol.getSumHeat());
                productProtocol.setSumCoolUnit(productProtocol.getSumHeatUnit());
                r = 1;
            } catch (Exception e) {
                r = -1;
            }
            productProtocol.setSumHeatUnit("W*h");
        } else if (Heat2 >= 0) {
            heat = otherenergy.substring(Heat2 + 10, Heat2 + 10 + 2) + otherenergy.substring(Heat2 + 8, Heat2 + 8 + 2) + otherenergy.substring(Heat2 + 6, Heat2 + 6 + 2) + otherenergy.substring(Heat2 + 4, Heat2 + 4 + 2);
            try {
                productProtocol.setSumCool(productProtocol.getSumHeat());
                productProtocol.setSumCoolUnit(productProtocol.getSumHeatUnit());
                r = 1;
            } catch (Exception e) {
                r = -2;
            }
            productProtocol.setSumHeatUnit("10W*h");
        } else if (Heat3 >= 0) {
            heat = otherenergy.substring(Heat3 + 10, Heat3 + 10 + 2) + otherenergy.substring(Heat3 + 8, Heat3 + 8 + 2) + otherenergy.substring(Heat3 + 6, Heat3 + 6 + 2) + otherenergy.substring(Heat3 + 4, Heat3 + 4 + 2);
            try {
                productProtocol.setSumCool(productProtocol.getSumHeat());
                productProtocol.setSumCoolUnit(productProtocol.getSumHeatUnit());
                r = 1;
            } catch (Exception e) {
                r = -3;
            }
            productProtocol.setSumHeatUnit("100W*h");
        } else if (Heat4 >= 0) {
            heat = otherenergy.substring(Heat4 + 10, Heat4 + 10 + 2) + otherenergy.substring(Heat4 + 8, Heat4 + 8 + 2) + otherenergy.substring(Heat4 + 6, Heat4 + 6 + 2) + otherenergy.substring(Heat4 + 4, Heat4 + 4 + 2);
            try {
                productProtocol.setSumCool(productProtocol.getSumHeat());
                productProtocol.setSumCoolUnit(productProtocol.getSumHeatUnit());
                r = 1;
            } catch (Exception e) {
                r = -4;
            }
            productProtocol.setSumHeatUnit("kW*h");
        } else if (Heat5 >= 0) {
            heat = otherenergy.substring(Heat5 + 10, Heat5 + 10 + 2) + otherenergy.substring(Heat5 + 8, Heat5 + 8 + 2) + otherenergy.substring(Heat5 + 6, Heat5 + 6 + 2) + otherenergy.substring(Heat5 + 4, Heat5 + 4 + 2);
            try {
                productProtocol.setSumCool(productProtocol.getSumHeat());
                productProtocol.setSumCoolUnit(productProtocol.getSumHeatUnit());
                r = 1;
            } catch (Exception e) {
                r = -5;
            }
            productProtocol.setSumHeatUnit("10kW*h");
        } else if (Heat6 >= 0) {
            heat = otherenergy.substring(Heat6 + 10, Heat6 + 10 + 2) + otherenergy.substring(Heat6 + 8, Heat6 + 8 + 2) + otherenergy.substring(Heat6 + 6, Heat6 + 6 + 2) + otherenergy.substring(Heat6 + 4, Heat6 + 4 + 2);
            try {
                productProtocol.setSumCool(productProtocol.getSumHeat());
                productProtocol.setSumCoolUnit(productProtocol.getSumHeatUnit());
                r = 1;
            } catch (Exception e) {
                r = -6;
            }
            productProtocol.setSumHeatUnit("100kW*h");
        } else if (Heat7 >= 0) {
            heat = otherenergy.substring(Heat7 + 10, Heat7 + 10 + 2) + otherenergy.substring(Heat7 + 8, Heat7 + 8 + 2) + otherenergy.substring(Heat7 + 6, Heat7 + 6 + 2) + otherenergy.substring(Heat7 + 4, Heat7 + 4 + 2);
            try {
                productProtocol.setSumCool(productProtocol.getSumHeat());
                productProtocol.setSumCoolUnit(productProtocol.getSumHeatUnit());
                r = 1;
            } catch (Exception e) {
                r = -7;
            }
            productProtocol.setSumHeatUnit("MW*h");
        } else if (Heat8 >= 0) {
            heat = otherenergy.substring(Heat8 + 10, Heat8 + 10 + 2) + otherenergy.substring(Heat8 + 8, Heat8 + 8 + 2) + otherenergy.substring(Heat8 + 6, Heat8 + 6 + 2) + otherenergy.substring(Heat8 + 4, Heat8 + 4 + 2);
            try {
                productProtocol.setSumCool(productProtocol.getSumHeat());
                productProtocol.setSumCoolUnit(productProtocol.getSumHeatUnit());
                r = 1;
            } catch (Exception e) {
                r = -8;
            }
            productProtocol.setSumHeatUnit("MJ");
        } else if (Heat9 >= 0) {
            heat = otherenergy.substring(Heat9 + 10, Heat9 + 10 + 2) + otherenergy.substring(Heat9 + 8, Heat9 + 8 + 2) + otherenergy.substring(Heat9 + 6, Heat9 + 6 + 2) + otherenergy.substring(Heat9 + 4, Heat9 + 4 + 2);
            try {
                productProtocol.setSumCool(productProtocol.getSumHeat());
                productProtocol.setSumCoolUnit(productProtocol.getSumHeatUnit());
                r = 1;
            } catch (Exception e) {
                r = -9;
            }
            productProtocol.setSumHeatUnit("10MJ");
        } else if (Heat10 >= 0) {
            heat = otherenergy.substring(Heat10 + 10, Heat10 + 10 + 2) + otherenergy.substring(Heat10 + 8, Heat10 + 8 + 2) + otherenergy.substring(Heat10 + 6, Heat10 + 6 + 2) + otherenergy.substring(Heat10 + 4, Heat10 + 4 + 2);
            try {
                productProtocol.setSumCool(productProtocol.getSumHeat());
                productProtocol.setSumCoolUnit(productProtocol.getSumHeatUnit());
                r = 1;
            } catch (Exception e) {
                r = -10;
            }
            productProtocol.setSumHeatUnit("100MJ");
        } else if (Heat11 >= 0) {
            heat = otherenergy.substring(Heat11 + 10, Heat11 + 10 + 2) + otherenergy.substring(Heat11 + 8, Heat11 + 8 + 2) + otherenergy.substring(Heat11 + 6, Heat11 + 6 + 2) + otherenergy.substring(Heat11 + 4, Heat11 + 4 + 2);
            try {
                productProtocol.setSumCool(productProtocol.getSumHeat());
                productProtocol.setSumCoolUnit(productProtocol.getSumHeatUnit());
                r = 1;
            } catch (Exception e) {
                r = -11;
            }
            productProtocol.setSumHeatUnit("GJ");
        }
        if (r == 1) {
            productProtocol.setSumHeat(Double.valueOf(heat));
        } else {
            productProtocol.setSumHeat(0);
            return r;
        }


        String total = "0";
        productProtocol.setTotalUnit("m³");
        int Total1 = DataArea.indexOf("0C10");
        int Total2 = DataArea.indexOf("0C11");
        int Total3 = DataArea.indexOf("0C12");
        int Total4 = DataArea.indexOf("0C13");
        int Total5 = DataArea.indexOf("0C14");
        int Total6 = DataArea.indexOf("0C15");
        int Total7 = DataArea.indexOf("0C16");
        if (Total1 >= 0) {
            total = DataArea.substring(Total1 + 10, Total1 + 10 + 2) + DataArea.substring(Total1 + 8, Total1 + 8 + 2) + DataArea.substring(Total1 + 6, Total1 + 6 + 2) + DataArea.substring(Total1 + 4, Total1 + 4 + 2);
            try {
                r = 1;
            } catch (Exception e) {
                r = -1;
            }
            productProtocol.setTotalUnit("mL");
        } else if (Total2 >= 0) {
            total = DataArea.substring(Total2 + 10, Total2 + 10 + 2) + DataArea.substring(Total2 + 8, Total2 + 8 + 2) + DataArea.substring(Total2 + 6, Total2 + 6 + 2) + DataArea.substring(Total2 + 4, Total2 + 4 + 2);
            try {
                r = 1;
            } catch (Exception e) {
                r = -2;
            }
            productProtocol.setTotalUnit("10mL");
        } else if (Total3 >= 0) {
            total = DataArea.substring(Total3 + 10, Total3 + 10 + 2) + DataArea.substring(Total3 + 8, Total3 + 8 + 2) + DataArea.substring(Total3 + 6, Total3 + 6 + 2) + DataArea.substring(Total3 + 4, Total3 + 4 + 2);
            try {
                r = 1;
            } catch (Exception e) {
                r = -3;
            }
            productProtocol.setTotalUnit("100mL");
        } else if (Total4 >= 0) {
            total = DataArea.substring(Total4 + 10, Total4 + 10 + 2) + DataArea.substring(Total4 + 8, Total4 + 8 + 2) + DataArea.substring(Total4 + 6, Total4 + 6 + 2) + DataArea.substring(Total4 + 4, Total4 + 4 + 2);
            try {
                r = 1;
            } catch (Exception e) {
                r = -4;
            }
            productProtocol.setTotalUnit("L");
        } else if (Total5 >= 0) {
            total = DataArea.substring(Total5 + 10, Total5 + 10 + 2) + DataArea.substring(Total5 + 8, Total5 + 8 + 2) + DataArea.substring(Total5 + 6, Total5 + 6 + 2) + DataArea.substring(Total5 + 4, Total5 + 4 + 2);
            try {
                r = 1;
            } catch (Exception e) {
                r = -5;
            }
            productProtocol.setTotalUnit("10L");
        } else if (Total6 >= 0) {
            total = DataArea.substring(Total6 + 10, Total6 + 10 + 2) + DataArea.substring(Total6 + 8, Total6 + 8 + 2) + DataArea.substring(Total6 + 6, Total6 + 6 + 2) + DataArea.substring(Total6 + 4, Total6 + 4 + 2);
            try {
                r = 1;
            } catch (Exception e) {
                r = -6;
            }
            productProtocol.setTotalUnit("100L");
        } else if (Total7 >= 0) {
            total = DataArea.substring(Total7 + 10, Total7 + 10 + 2) + DataArea.substring(Total7 + 8, Total7 + 8 + 2) + DataArea.substring(Total7 + 6, Total7 + 6 + 2) + DataArea.substring(Total7 + 4, Total7 + 4 + 2);
            try {
                r = 1;
            } catch (Exception e) {
                r = -7;
            }
            productProtocol.setTotalUnit("m³");
        }
        if (r == 1) {
            productProtocol.setTotal(Double.valueOf(total));
        } else {
            productProtocol.setTotal(0);
            return r;
        }

        String power = "0";
        productProtocol.setPowerUnit("kW");
        int Power1 = DataArea.indexOf("0B28");
        int Power2 = DataArea.indexOf("0B29");
        int Power3 = DataArea.indexOf("0B2A");
        int Power4 = DataArea.indexOf("0B2B");
        int Power5 = DataArea.indexOf("0B2C");
        int Power6 = DataArea.indexOf("0B2D");
        int Power7 = DataArea.indexOf("0B2E");
        int Power8 = DataArea.indexOf("0B2F");
        if (Power1 >= 0) {
            power = DataArea.substring(Power1 + 8, Power1 + 8 + 2) + DataArea.substring(Power1 + 6, Power1 + 6 + 2) + DataArea.substring(Power1 + 4, Power1 + 4 + 2);
            try {
                r = 1;
            } catch (Exception e) {
                r = -1;
            }
            productProtocol.setPowerUnit("0.001W");
        } else if (Power2 >= 0) {
            power = DataArea.substring(Power2 + 8, Power2 + 8 + 2) + DataArea.substring(Power2 + 6, Power2 + 6 + 2) + DataArea.substring(Power2 + 4, Power2 + 4 + 2);
            try {
                r = 1;
            } catch (Exception e) {
                r = -2;
            }
            productProtocol.setPowerUnit("0.01W");
        } else if (Power3 >= 0) {
            power = DataArea.substring(Power3 + 8, Power3 + 8 + 2) + DataArea.substring(Power3 + 6, Power3 + 6 + 2) + DataArea.substring(Power3 + 4, Power3 + 4 + 2);
            try {
                r = 1;
            } catch (Exception e) {
                r = -3;
            }
            productProtocol.setPowerUnit("0.1W");
        } else if (Power4 >= 0) {
            power = DataArea.substring(Power4 + 8, Power4 + 8 + 2) + DataArea.substring(Power4 + 6, Power4 + 6 + 2) + DataArea.substring(Power4 + 4, Power4 + 4 + 2);
            try {
                r = 1;
            } catch (Exception e) {
                r = -4;
            }
            productProtocol.setPowerUnit("W");
        } else if (Power5 >= 0) {
            power = DataArea.substring(Power5 + 8, Power5 + 8 + 2) + DataArea.substring(Power5 + 6, Power5 + 6 + 2) + DataArea.substring(Power5 + 4, Power5 + 4 + 2);
            try {
                r = 1;
            } catch (Exception e) {
                r = -5;
            }
            productProtocol.setPowerUnit("10W");
        } else if (Power6 >= 0) {
            power = DataArea.substring(Power6 + 8, Power6 + 8 + 2) + DataArea.substring(Power6 + 6, Power6 + 6 + 2) + DataArea.substring(Power6 + 4, Power6 + 4 + 2);
            try {
                r = 1;
            } catch (Exception e) {
                r = -6;
            }
            productProtocol.setPowerUnit("100W");
        } else if (Power7 >= 0) {
            power = DataArea.substring(Power7 + 8, Power7 + 8 + 2) + DataArea.substring(Power7 + 6, Power7 + 6 + 2) + DataArea.substring(Power7 + 4, Power7 + 4 + 2);
            try {
                r = 1;
            } catch (Exception e) {
                r = -7;
            }
            productProtocol.setPowerUnit("kW");
        } else if (Power8 >= 0) {
            power = DataArea.substring(Power8 + 8, Power8 + 8 + 2) + DataArea.substring(Power8 + 6, Power8 + 6 + 2) + DataArea.substring(Power8 + 4, Power8 + 4 + 2);
            try {
                r = 1;
            } catch (Exception e) {
                r = -8;
            }
            productProtocol.setPowerUnit("10kW");
        }
        if (r == 1) {
            productProtocol.setPower(Double.valueOf(power));
        } else {
            productProtocol.setPower(0);
            return r;
        }

        String flowrate = "0";
        productProtocol.setFlowRateUnit("m³/h");
        int FlowRate1 = DataArea.indexOf("0B38");
        int FlowRate2 = DataArea.indexOf("0B39");
        int FlowRate3 = DataArea.indexOf("0B3A");
        int FlowRate4 = DataArea.indexOf("0B3B");
        int FlowRate5 = DataArea.indexOf("0B3C");
        int FlowRate6 = DataArea.indexOf("0B3D");
        int FlowRate7 = DataArea.indexOf("0B3E");
        if (FlowRate1 >= 0) {
            flowrate = DataArea.substring(FlowRate1 + 8, FlowRate1 + 8 + 2) + DataArea.substring(FlowRate1 + 6, FlowRate1 + 6 + 2) + DataArea.substring(FlowRate1 + 4, FlowRate1 + 4 + 2);
            try {
                r = 1;
            } catch (Exception e) {
                r = -1;
            }
            productProtocol.setFlowRateUnit("mL/h");
        } else if (FlowRate2 >= 0) {
            flowrate = DataArea.substring(FlowRate2 + 8, FlowRate2 + 8 + 2) + DataArea.substring(FlowRate2 + 6, FlowRate2 + 6 + 2) + DataArea.substring(FlowRate2 + 4, FlowRate2 + 4 + 2);
            try {
                r = 1;
            } catch (Exception e) {
                r = -2;
            }
            productProtocol.setFlowRateUnit("10mL/h");
        } else if (FlowRate3 >= 0) {
            flowrate = DataArea.substring(FlowRate3 + 8, FlowRate3 + 8 + 2) + DataArea.substring(FlowRate3 + 6, FlowRate3 + 6 + 2) + DataArea.substring(FlowRate3 + 4, FlowRate3 + 4 + 2);
            try {
                r = 1;
            } catch (Exception e) {
                r = -3;
            }
            productProtocol.setFlowRateUnit("100mL/h");
        } else if (FlowRate4 >= 0) {
            flowrate = DataArea.substring(FlowRate4 + 8, FlowRate4 + 8 + 2) + DataArea.substring(FlowRate4 + 6, FlowRate4 + 6 + 2) + DataArea.substring(FlowRate4 + 4, FlowRate4 + 4 + 2);
            try {
                r = 1;
            } catch (Exception e) {
                r = -4;
            }
            productProtocol.setFlowRateUnit("L/h");
        } else if (FlowRate5 >= 0) {
            flowrate = DataArea.substring(FlowRate5 + 8, FlowRate5 + 8 + 2) + DataArea.substring(FlowRate5 + 6, FlowRate5 + 6 + 2) + DataArea.substring(FlowRate5 + 4, FlowRate5 + 4 + 2);
            try {
                r = 1;
            } catch (Exception e) {
                r = -5;
            }
            productProtocol.setFlowRateUnit("10L/h");
        } else if (FlowRate6 >= 0) {
            flowrate = DataArea.substring(FlowRate6 + 8, FlowRate6 + 8 + 2) + DataArea.substring(FlowRate6 + 6, FlowRate6 + 6 + 2) + DataArea.substring(FlowRate6 + 4, FlowRate6 + 4 + 2);
            try {
                r = 1;
            } catch (Exception e) {
                r = -6;
            }
            productProtocol.setFlowRateUnit("100L/h");
        } else if (FlowRate7 >= 0) {
            flowrate = DataArea.substring(FlowRate7 + 8, FlowRate7 + 8 + 2) + DataArea.substring(FlowRate7 + 6, FlowRate7 + 6 + 2) + DataArea.substring(FlowRate7 + 4, FlowRate7 + 4 + 2);
            try {
                r = 1;
            } catch (Exception e) {
                r = -7;
            }
            productProtocol.setFlowRateUnit("m³/h");
        }
        if (r == 1) {
            productProtocol.setFlowRate(Double.valueOf(flowrate));
        } else {
            productProtocol.setFlowRate(0);
            return r;
        }


        String Temp1 = "0";
        int T11 = DataArea.indexOf("0A58");
        int T12 = DataArea.indexOf("0A59");
        int T13 = DataArea.indexOf("0A5A");
        int T14 = DataArea.indexOf("0A5B");
        if (T11 >= 0) {
            Temp1 = DataArea.substring(T11 + 6, T11 + 6 + 2) + DataArea.substring(T11 + 4, T11 + 4 + 2);
            try {
                Temp1 = String.valueOf(Double.valueOf(Temp1) / 1000);
                r = 1;
            } catch (Exception e) {
                r = -1;
            }
        } else if (T12 >= 0) {
            Temp1 = DataArea.substring(T12 + 6, T12 + 6 + 2) + DataArea.substring(T12 + 4, T12 + 4 + 2);
            try {
                Temp1 = String.valueOf(Double.valueOf(Temp1) / 100);
                r = 1;
            } catch (Exception e) {
                r = -2;
            }
        } else if (T13 >= 0) {
            Temp1 = DataArea.substring(T13 + 6, T13 + 6 + 2) + DataArea.substring(T13 + 4, T13 + 4 + 2);
            try {
                Temp1 = String.valueOf(Double.valueOf(Temp1) / 10);
                r = 1;
            } catch (Exception e) {
                r = -3;
            }
        } else if (T14 >= 0) {
            Temp1 = DataArea.substring(T14 + 6, T14 + 6 + 2) + DataArea.substring(T14 + 4, T14 + 4 + 2);
            try {
                Temp1 = String.valueOf(Double.valueOf(Temp1));
                r = 1;
            } catch (Exception e) {
                r = -4;
            }
        }
        if (r == 1) {
            productProtocol.setT1InP(Double.valueOf(Temp1));
        } else {
            productProtocol.setT1InP(0);
            return r;
        }


        String Temp2 = "0";
        int T21 = DataArea.indexOf("0A5C");
        int T22 = DataArea.indexOf("0A5D");
        int T23 = DataArea.indexOf("0A5E");
        int T24 = DataArea.indexOf("0A5F");
        if (T21 >= 0) {
            Temp2 = DataArea.substring(T21 + 6, T21 + 6 + 2) + DataArea.substring(T21 + 4, T21 + 4 + 2);
            try {
                Temp2 = String.valueOf(Double.valueOf(Temp2) / 1000);
                r = 1;
            } catch (Exception e) {
                r = -1;
            }
        } else if (T22 >= 0) {
            Temp2 = DataArea.substring(T22 + 6, T22 + 6 + 2) + DataArea.substring(T22 + 4, T22 + 4 + 2);
            try {
                Temp2 = String.valueOf(Double.valueOf(Temp2) / 100);
                r = 1;
            } catch (Exception e) {
                r = -2;
            }
        } else if (T23 >= 0) {
            Temp2 = DataArea.substring(T23 + 6, T23 + 6 + 2) + DataArea.substring(T23 + 4, T23 + 4 + 2);
            try {
                Temp2 = String.valueOf(Double.valueOf(Temp2) / 10);
                r = 1;
            } catch (Exception e) {
                r = -3;
            }
        } else if (T24 >= 0) {
            Temp2 = DataArea.substring(T24 + 6, T24 + 6 + 2) + DataArea.substring(T24 + 4, T24 + 4 + 2);
            try {
                Temp2 = String.valueOf(Double.valueOf(Temp2));
                r = 1;
            } catch (Exception e) {
                r = -4;
            }
        }
        if (r == 1) {
            productProtocol.setT2InP(Double.valueOf(Temp2));
        } else {
            productProtocol.setT2InP(0);
            return r;
        }


        String WorkTime = "0";
        int WorkTime1 = DataArea.indexOf("0B26");
        if (WorkTime1 >= 0) {
            WorkTime = DataArea.substring(WorkTime1 + 8, WorkTime1 + 8 + 2) + DataArea.substring(WorkTime1 + 6, WorkTime1 + 6 + 2) + DataArea.substring(WorkTime1 + 4, WorkTime1 + 4 + 2);
            try {
                WorkTime = String.valueOf(Integer.valueOf(WorkTime));
                r = 1;
            } catch (Exception e) {
                r = -1;
            }
        }
        if (r == 1) {
            productProtocol.setWorkTimeInP(Integer.valueOf(WorkTime));
        } else {
            productProtocol.setWorkTimeInP(0);
            return r;
        }


        String Time = "0";
        int Time1 = DataArea.indexOf("046D");
        if (Time1 >= 0) {
            Time = AnalysisUtils.HexS2ToBinS(DataArea.substring(Time1 + 10, Time1 + 10 + 2)) + AnalysisUtils.HexS2ToBinS(DataArea.substring(Time1 + 8, Time1 + 8 + 2)) + AnalysisUtils.HexS2ToBinS(DataArea.substring(Time1 + 6, Time1 + 6 + 2)) + AnalysisUtils.HexS2ToBinS(DataArea.substring(Time1 + 4, Time1 + 4 + 2));
            char timechar[] = Time.toCharArray();
            String year = "0" + timechar[0] + timechar[1] + timechar[2] + timechar[3] + timechar[8] + timechar[9] + timechar[10];
            String month = "" + timechar[4] + timechar[5] + timechar[6] + timechar[7];
            String day = "000" + timechar[11] + timechar[12] + timechar[13] + timechar[14] + timechar[15];
            String hundreadyear = "00" + timechar[17] + timechar[18];
            String hour = "000" + timechar[19] + timechar[20] + timechar[21] + timechar[22] + timechar[23];
            String min = "00" + timechar[26] + timechar[27] + timechar[28] + timechar[29] + timechar[30] + timechar[31];
            year = AnalysisUtils.BinS8ToHexS2(year);
            month = "0" + AnalysisUtils.BinS4ToHexS2(month);
            day = AnalysisUtils.BinS8ToHexS2(day);
            hundreadyear = "0" + AnalysisUtils.BinS4ToHexS2(hundreadyear);
            hour = AnalysisUtils.BinS8ToHexS2(hour);
            min = AnalysisUtils.BinS8ToHexS2(min);
            int yeari = 2000 + AnalysisUtils.HexS2ToInt(hundreadyear) * 100 + AnalysisUtils.HexS2ToInt(year);
            int monthi = AnalysisUtils.HexS2ToInt(month);
            int dayi = AnalysisUtils.HexS2ToInt(day);
            int houri = AnalysisUtils.HexS2ToInt(hour);
            int mini = AnalysisUtils.HexS2ToInt(min);
            year = String.valueOf(yeari);
            month = String.valueOf(monthi);
            day = String.valueOf(dayi);
            hour = String.valueOf(houri);
            min = String.valueOf(mini);
            switch (year.length()) {
                case 0:
                    year = "0000";
                    break;
                case 1:
                    year = "000" + year;
                    break;
                case 2:
                    year = "00" + year;
                    break;
                case 3:
                    year = "0" + year;
                    break;
                case 4:
                    break;
                default:
                    break;
            }
            switch (month.length()) {
                case 0:
                    month = "00";
                    break;
                case 1:
                    month = "0" + month;
                    break;
                case 2:
                    break;
                default:
                    break;
            }
            switch (day.length()) {
                case 0:
                    day = "00";
                    break;
                case 1:
                    day = "0" + day;
                    break;
                case 2:
                    break;
                default:
                    break;
            }
            switch (hour.length()) {
                case 0:
                    hour = "00";
                    break;
                case 1:
                    hour = "0" + hour;
                    break;
                case 2:
                    break;
                default:
                    break;
            }
            switch (min.length()) {
                case 0:
                    min = "00";
                    break;
                case 1:
                    min = "0" + min;
                    break;
                case 2:
                    break;
                default:
                    break;
            }
            try {
                Time = year + "-" + month + "-" + day + " " + hour + ":" + min + ":00";
                r = 1;
            } catch (Exception e) {
                r = -1;
            }
        }
        if (r != 1) {
            if (r == 0) {
                productProtocol.setTimeInP("err ");
                return r;
            } else if (r == -1) {
                productProtocol.setTimeInP("err " + DataArea.substring(Time1 + 10, Time1 + 10 + 2) + DataArea.substring(Time1 + 8, Time1 + 8 + 2) + DataArea.substring(Time1 + 6, Time1 + 6 + 2) + DataArea.substring(Time1 + 4, Time1 + 4 + 2));
                return r;
            }
        } else {
            productProtocol.setTimeInP(Time);
        }
        r = 1;
        return r;
    }

    private static int MAnalysisJSMTRxStr(String Data, MBUS mbus) {
        int r;
        String valveStatusS = Data.substring(26, 26 + 2);
        switch (valveStatusS) {
            case "99":
                mbus.setStatus("已执行");
                break;
            case "59":
                mbus.setStatus("短路");
                break;
            default:
                mbus.setStatus("?");
                break;
        }
        r = 1;
        return r;
    }

    private static int ReportAnalysisJSMTRxStr(String Data, ProductProtocol productProtocol) {
        int r;
        initProtocol(productProtocol);
        productProtocol.setProductTypeTX(Data.substring(2, 2 + 2));
        productProtocol.setProductTypeTX(Data.substring(2, 2 + 2));
        productProtocol.setMeterID(Data.substring(4, 4 + 8));
        DecimalFormat df = new DecimalFormat(".##");
        String DataArea = "";
        try {
            DataArea = Data.substring(40, 40 + Data.length() - 40 - 4);
            r = 1;
        } catch (Exception e) {
            r = 0;
        }
        if (r == 0) {
            return r;
        }
        try {
            String heatS = DataArea.substring(0, 8);
            int heat = AnalysisUtils.HexS8ToInt(heatS);
            productProtocol.setSumHeat(Double.valueOf(df.format(heat)));
            r = 1;
        } catch (Exception e) {
            productProtocol.setSumHeat(0);
            r = 0;
        }
        productProtocol.setSumHeatUnit("kW*h");
        if (r == 0) {
            return r;
        }

        try {
            String totalS = DataArea.substring(8, 8 + 8);
            int total = AnalysisUtils.HexS8ToInt(totalS);
            productProtocol.setTotal(Double.valueOf(df.format(total)));
            r = 1;
        } catch (Exception e) {
            productProtocol.setTotal(0);
            r = 0;
        }
        productProtocol.setTotalUnit("m³");
        if (r == 0) {
            return r;
        }

        try {
            String flowrateS = DataArea.substring(16, 16 + 8);
            double flowrate = Integer.valueOf(flowrateS) / 100f;
            productProtocol.setFlowRate(Double.valueOf(df.format(flowrate)));
            r = 1;
        } catch (Exception e) {
            productProtocol.setFlowRate(0);
            r = 0;
        }
        productProtocol.setFlowRateUnit("m³/h");
        if (r == 0) {
            return r;
        }

        try {
            String t1S = DataArea.substring(24, 24 + 4) + "." + DataArea.substring(28, 28 + 2);
            double t1 = Double.valueOf(t1S);
            productProtocol.setT1InP(Double.valueOf(df.format(t1)));
            r = 1;
        } catch (Exception e) {
            productProtocol.setT1InP(0);
            r = 0;
        }
        if (r == 0) {
            return r;
        }

        try {
            String t2S = DataArea.substring(30, 30 + 4) + "." + DataArea.substring(34, 34 + 2);
            double t2 = Double.valueOf(t2S);
            productProtocol.setT2InP(Double.valueOf(df.format(t2)));
            r = 1;
        } catch (Exception e) {
            productProtocol.setT2InP(0);
            r = 0;
        }
        if (r == 0) {
            return r;
        }

        String Status = DataArea.substring(36, 36 + 2);
        if (Status.endsWith("00")) {
            productProtocol.setStatus("正常");
        } else if (Status.endsWith("01")) {
            productProtocol.setStatus("无水");
        } else if (Status.endsWith("02")) {
            productProtocol.setStatus("室温失联");
        } else if (Status.endsWith("03")) {
            productProtocol.setStatus("失联");
        } else if (Status.endsWith("04")) {
            productProtocol.setStatus("-");
        } else if (Status.endsWith("05")) {
            productProtocol.setStatus("其他");
        }

        try {
            String insideTSetS = DataArea.substring(38, 38 + 6);
            double insideTSet = Integer.valueOf(insideTSetS) / 100f;
            productProtocol.setInsideTSet(df.format(insideTSet));
            r = 1;
        } catch (Exception e) {
            productProtocol.setInsideTSet("0");
            r = 0;
        }
        if (r == 0) {
            return r;
        }

        try {
            String insideTS = DataArea.substring(46, 46 + 6);
            double insidet = Integer.valueOf(insideTS) / 100f;
            if (DataArea.substring(44, 44 + 2).endsWith("FF")) {
                insidet = 0 - insidet;
            }
            productProtocol.setInsideT(Double.valueOf(df.format(insidet)));
            r = 1;
        } catch (Exception e) {
            productProtocol.setInsideT(0);
            r = 0;
        }
        if (r == 0) {
            return r;
        }

        try {
            String sumOpenValveMS = DataArea.substring(52, 52 + 8);
            int sumOpenValveM = AnalysisUtils.HexS8ToInt(sumOpenValveMS);
            productProtocol.setSumOpenValveM(Double.valueOf(df.format(sumOpenValveM)));
            r = 1;
        } catch (Exception e) {
//				   productProtocol.setSumOpenValveS("0");
            r = 0;
        }
        if (r == 0) {
            return r;
        }

        try {
            String sumOpenValveHS = DataArea.substring(60, 60 + 8);
            int sumOpenValveH = AnalysisUtils.HexS8ToInt(sumOpenValveHS);
//		    	   productProtocol.setSumOpenValveH(df.format(sumOpenValveH));
            r = 1;
        } catch (Exception e) {
//				   productProtocol.setSumOpenValveS("0");
            r = 0;
        }
        if (r == 0) {
            return r;
        }

        String valveStatusS = DataArea.substring(68, 68 + 2);
        switch (valveStatusS) {
            case "55":
                productProtocol.setValveStatus("阀开");
                break;
            case "99":
                productProtocol.setValveStatus("阀关");
                break;
            case "59":
                productProtocol.setValveStatus("异常");
                break;
            default:
                productProtocol.setValveStatus("-");
                break;
        }

        try {
            String worktimeinpS = DataArea.substring(70, 70 + 8);
            int worktimeinp = AnalysisUtils.HexS8ToInt(worktimeinpS);
            productProtocol.setWorkTimeInP(worktimeinp);
            r = 1;
        } catch (Exception e) {
            productProtocol.setWorkTimeInP(0);
            r = 0;
        }
        if (r == 0) {
            return r;
        }

        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        try {
            String timeinpS = DataArea.substring(78, 78 + 2) + DataArea.substring(80, 80 + 2) + "-" + DataArea.substring(82, 82 + 2) + "-" + DataArea.substring(84, 84 + 2) + " " + DataArea.substring(86, 86 + 2) + ":" + DataArea.substring(88, 88 + 2) + ":" + DataArea.substring(90, 90 + 2);

            Date timeinpdate = formatDate.parse(timeinpS);
            productProtocol.setTimeInP(formatDate.format(timeinpdate));
        } catch (Exception e) {
            productProtocol.setTimeInP(formatDate.format(new Date(System.currentTimeMillis())));
        }
        return r;
    }

    private static void initSSumHeat(SSumHeat ssumheat) {
        ssumheat.setImei("");
        ssumheat.setMeterid("");
        ssumheat.setSumheat(0);
        ssumheat.setTotal(0);
        ssumheat.setFlowrate(0);
        ssumheat.setT1inp(0);
        ssumheat.setT2inp(0);
        ssumheat.setStatus("");
        ssumheat.setSumheat1(0);
        ssumheat.setSumheat2(0);
        ssumheat.setDsumheat(0);
        ssumheat.setInsidetset("");
        ssumheat.setInsidet(0);
        ssumheat.setEqualinsidet(0);
        ssumheat.setSumopenvalvem1(0);
        ssumheat.setSumopenvalvem2(0);
        ssumheat.setDsumopenvalveh(0);
        ssumheat.setValvestatus("");
        ssumheat.setDutyratio(0);
        ssumheat.setSsumheat(0);
        ssumheat.setTotalssumheat(0);
        ssumheat.setSysstatus("");
        ssumheat.setAvgtime(new Date(System.currentTimeMillis()));
    }

    private static int AvgAnalysisJSMTRxStr(String Data, SSumHeat ssumheat) {
        int r = 0;
        initSSumHeat(ssumheat);
        ssumheat.setImei(Data.substring(29, 29 + 11));
        ssumheat.setMeterid(Data.substring(4, 4 + 8));
        DecimalFormat df = new DecimalFormat(".##");
        String DataArea = "";
        try {
            DataArea = Data.substring(40, 40 + Data.length() - 40 - 4);
            r = 1;
        } catch (Exception e) {
            r = 0;
        }
        if (r == 0) {
            return r;
        }
        try {
            String heatS = DataArea.substring(0, 8);
            int heat = AnalysisUtils.HexS8ToInt(heatS);
            ssumheat.setSumheat(Double.valueOf(df.format(heat)));
            r = 1;
        } catch (Exception e) {
            ssumheat.setSumheat(0);
            r = 0;
        }
        if (r == 0) {
            return r;
        }

        try {
            String totalS = DataArea.substring(8, 8 + 8);
            int total = AnalysisUtils.HexS8ToInt(totalS);
            ssumheat.setTotal(Double.valueOf(df.format(total)));
            r = 1;
        } catch (Exception e) {
            ssumheat.setTotal(0);
            r = 0;
        }
        if (r == 0) {
            return r;
        }

        try {
            String flowrateS = DataArea.substring(16, 16 + 8);
            double flowrate = Integer.valueOf(flowrateS) / 100f;
            ssumheat.setFlowrate(Double.valueOf(df.format(flowrate)));
            r = 1;
        } catch (Exception e) {
            ssumheat.setFlowrate(0);
            r = 0;
        }
        if (r == 0) {
            return r;
        }

        try {
            String t1S = DataArea.substring(24, 24 + 4) + "." + DataArea.substring(28, 28 + 2);
            double t1 = Double.valueOf(t1S);
            ssumheat.setT1inp(Double.valueOf(df.format(t1)));
            r = 1;
        } catch (Exception e) {
            ssumheat.setT1inp(0);
            r = 0;
        }
        if (r == 0) {
            return r;
        }

        try {
            String t2S = DataArea.substring(30, 30 + 4) + "." + DataArea.substring(34, 34 + 2);
            double t2 = Double.valueOf(t2S);
            ssumheat.setT2inp(Double.valueOf(df.format(t2)));
            r = 1;
        } catch (Exception e) {
            ssumheat.setT2inp(0);
            r = 0;
        }
        if (r == 0) {
            return r;
        }

        String Status = DataArea.substring(36, 36 + 2);
        if (Status.endsWith("00")) {
            ssumheat.setStatus("正常");
        } else if (Status.endsWith("01")) {
            ssumheat.setStatus("无水");
        } else if (Status.endsWith("02")) {
            ssumheat.setStatus("-");
        } else if (Status.endsWith("03")) {
            ssumheat.setStatus("失联");
        } else if (Status.endsWith("04")) {
            ssumheat.setStatus("其他");
        }

        try {
            String heat1S = DataArea.substring(38, 38 + 8);
            int heat1 = AnalysisUtils.HexS8ToInt(heat1S);
            ssumheat.setSumheat1(Double.valueOf(df.format(heat1)));
            r = 1;
        } catch (Exception e) {
            ssumheat.setSumheat1(0);
            r = 0;
        }
        if (r == 0) {
            return r;
        }

        try {
            String heat2S = DataArea.substring(46, 46 + 8);
            int heat2 = AnalysisUtils.HexS8ToInt(heat2S);
            ssumheat.setSumheat2(Double.valueOf(df.format(heat2)));
            r = 1;
        } catch (Exception e) {
            ssumheat.setSumheat2(0);
            r = 0;
        }
        if (r == 0) {
            return r;
        }

        try {
            String dheatS = DataArea.substring(54, 54 + 8);
            int dheat = AnalysisUtils.HexS8ToInt(dheatS);
            ssumheat.setDsumheat(Double.valueOf(df.format(dheat)));
            r = 1;
        } catch (Exception e) {
            ssumheat.setDsumheat(0);
            r = 0;
        }
        if (r == 0) {
            return r;
        }

        try {
            String insideTSetS = DataArea.substring(62, 62 + 6);
            double insideTSet = Integer.valueOf(insideTSetS) / 100f;
            ssumheat.setInsidetset(df.format(insideTSet));
            r = 1;
        } catch (Exception e) {
            ssumheat.setInsidetset("0");
            r = 0;
        }
        if (r == 0) {
            return r;
        }

        try {
            String insideTS = DataArea.substring(70, 70 + 6);
            double insidet = Integer.valueOf(insideTS) / 100f;
            if (DataArea.substring(68, 68 + 2).endsWith("FF")) {
                insidet = 0 - insidet;
            }
            ssumheat.setInsidet(Double.valueOf(df.format(insidet)));
            r = 1;
        } catch (Exception e) {
            ssumheat.setInsidet(0);
            r = 0;
        }
        if (r == 0) {
            return r;
        }

        try {
            String equalinsideTS = DataArea.substring(78, 78 + 6);
            double equalinsidet = Integer.valueOf(equalinsideTS) / 100f;
            if (DataArea.substring(76, 76 + 2).endsWith("FF")) {
                equalinsidet = 0 - equalinsidet;
            }
            ssumheat.setEqualinsidet(Double.valueOf(df.format(equalinsidet)));
            r = 1;
        } catch (Exception e) {
            ssumheat.setEqualinsidet(0);
            r = 0;
        }
        if (r == 0) {
            return r;
        }

        try {
            String sumOpenValveM1S = DataArea.substring(84, 84 + 8);
            int sumOpenValveM1 = AnalysisUtils.HexS8ToInt(sumOpenValveM1S);
            ssumheat.setSumopenvalvem1(Double.valueOf(df.format(sumOpenValveM1)));
            r = 1;
        } catch (Exception e) {
            ssumheat.setSumopenvalvem1(0);
            r = 0;
        }
        if (r == 0) {
            return r;
        }

        try {
            String sumOpenValveM2S = DataArea.substring(92, 92 + 8);
            int sumOpenValveM2 = AnalysisUtils.HexS8ToInt(sumOpenValveM2S);
            ssumheat.setSumopenvalvem2(Double.valueOf(df.format(sumOpenValveM2)));
            r = 1;
        } catch (Exception e) {
            ssumheat.setSumopenvalvem2(0);
            r = 0;
        }
        if (r == 0) {
            return r;
        }

        try {
            String dsumOpenValveHS = DataArea.substring(100, 100 + 8);
            int dsumOpenValveH = AnalysisUtils.HexS8ToInt(dsumOpenValveHS);
            ssumheat.setDsumopenvalveh(Double.valueOf(df.format(dsumOpenValveH)));
            r = 1;
        } catch (Exception e) {
            ssumheat.setDsumopenvalveh(0);
            r = 0;
        }
        if (r == 0) {
            return r;
        }

        String ValveStatus = DataArea.substring(108, 108 + 2);
        if (ValveStatus.endsWith("55")) {
            ssumheat.setValvestatus("阀开");
        } else if (ValveStatus.endsWith("99")) {
            ssumheat.setValvestatus("阀关");
        } else if (ValveStatus.endsWith("59")) {
            ssumheat.setValvestatus("异常");
        } else if (ValveStatus.endsWith("00")) {
            ssumheat.setValvestatus("其他");
        }
        try {
            String dutyratioS = DataArea.substring(110, 110 + 6);
            double dutyratio = Integer.valueOf(dutyratioS) / 100f;
            ssumheat.setDutyratio(Double.valueOf(df.format(dutyratio)));
            r = 1;
        } catch (Exception e) {
            ssumheat.setDutyratio(0);
            r = 0;
        }
        if (r == 0) {
            return r;
        }

        try {
            String ssumheatS = DataArea.substring(116, 116 + 8);
            int ssumheatd = AnalysisUtils.HexS8ToInt(ssumheatS);
            ssumheat.setSsumheat(Double.valueOf(df.format(ssumheatd)));
            r = 1;
        } catch (Exception e) {
            ssumheat.setSsumheat(0);
            r = 0;
        }
        if (r == 0) {
            return r;
        }

        try {
            String totalssumheatS = DataArea.substring(124, 124 + 8);
            int totalssumheat = AnalysisUtils.HexS8ToInt(totalssumheatS);
            ssumheat.setTotalssumheat(Double.valueOf(df.format(totalssumheat)));
            r = 1;
        } catch (Exception e) {
            ssumheat.setTotalssumheat(0);
            r = 0;
        }
        if (r == 0) {
            return r;
        }

        String SysStatus = DataArea.substring(132, 132 + 2);
        if (SysStatus.endsWith("00")) {
            ssumheat.setSysstatus("正常");
        } else if (SysStatus.endsWith("01")) {
            ssumheat.setSysstatus("室温失联");
        } else if (SysStatus.endsWith("02")) {
            ssumheat.setSysstatus("-");
        } else if (SysStatus.endsWith("03")) {
            ssumheat.setSysstatus("失联");
        } else if (SysStatus.endsWith("04")) {
            ssumheat.setSysstatus("其他");
        }
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        try {
            String avgtimeS = DataArea.substring(134, 134 + 2) + DataArea.substring(136, 136 + 2) + "-" + DataArea.substring(138, 138 + 2) + "-" + DataArea.substring(140, 140 + 2) + " " + DataArea.substring(142, 142 + 2) + ":" + DataArea.substring(144, 144 + 2) + ":" + DataArea.substring(146, 146 + 2);
            Date avgtime = formatDate.parse(avgtimeS);
            ssumheat.setAvgtime(avgtime);
        } catch (Exception e) {
            ssumheat.setAvgtime(new Date(System.currentTimeMillis()));
        }

        return r;
    }

    /**
     * 判断是否是一个完整的指令
     *
     * @param rx 指令
     * @return 是否完整
     */
    public static boolean txIsLegal(String rx) {
        try {
            //判断指令头“68”的位置
            int positionHeadInstruction = rx.indexOf("68");
            if (positionHeadInstruction >= 0) {
                //指令总长度
                String txLength = rx.substring(positionHeadInstruction + 20, positionHeadInstruction + 22);
                return "16".equals(rx.substring(positionHeadInstruction + (AnalysisUtils.HexS2ToInt(txLength) + 13) * 2 - 2, positionHeadInstruction + (AnalysisUtils.HexS2ToInt(txLength) + 13) * 2));
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    //解析收到的指令
    public static int txIsLegal(String rx, ProductProtocol productProtocol, SSumHeat ssumheat, MBUS mbus, ParameterProtocol parameterprotcol) {

        //**************************************************************指令示例******************************************************************
        //热量表            	   String rx="682094000007001111812E902F000000000005000000000500000000140000000032000000002C2729009026005001003855092503142000007816";
        //阀控表                   String rx="682022540126001111812E901FE931129992010000000005000C0370020000000032000000002A0021009820009788005144112103142099077516";
        //英文                 	   String rx="682F2F680800720500253397362707850000000974080970080C10461900000B3B2600000A599032046D3A13D9130B260200004616";
        //阀门                     String rx="6820225401260011118404A01700991F16";
        //内部参数                 String rx="FEFE6820432008350011119148901F007437010137012936011836010000000000000210006029000549003768005887000000001200000036000030002002002000000735000010060030020036000010000016006716";
        //通断控制器               String rx="684947268622001111812E901FB3311299000087270000009402000201FFFF1E0032340720002C000000000000882700550712191114205507C816";
        //水表                     String rx="682F2F680800721211111188362707850000000974080970080C15829507000B3C3415000A598327046D2407D1150B264404000D16";
        //热量表                   String rx="68393968080072181111118836150D0D0000000974080970080C0F340000000C0F088521000C15723211000B2D8002010B3C7317000A5922730A5D2423D816";
        //时间面积分摊   报表      String rx="6851000000000001111111120011118438903F00000000000000000000000000000000000000030030000000324000056085000009000100000027201307281000300000006401017416";
        //时间面积分摊   分摊      String rx="6851111111120011118549903F00000000000001000000000000000000000000000000000000021530231001531258000000102700300000003240000032400005608500056085000000000000000000000000001491600220130726160031C316";

        int r = 0;
        try {
            //判断指令头“68”的位置
            int position_head_instruction = rx.indexOf("68");
            if (position_head_instruction >= 0) {
                //指令头
                String head_instruction = rx.substring(position_head_instruction, position_head_instruction + 2);
                //产品类型（68后面两位）
                String productType = rx.substring(position_head_instruction + 2, position_head_instruction + 4);
                //产品类型（产品8位编码的前两位）
                String ProductTypeS1 = rx.substring(position_head_instruction + 4, position_head_instruction + 6);
                String HeadS1 = rx.substring(position_head_instruction + 6, position_head_instruction + 8);
                String length_tx;                   //指令总长度
                String data;                        //指令具体内容
                String control_code1;               //控制码1
                String control_code2;               //控制码2
                String control_code3;               //控制码3
                //超声波水表
                if (head_instruction.equals("68") & productType.equals(ProductType.WATER_METER_STRING)) {
                    length_tx = rx.substring(position_head_instruction + 20, position_head_instruction + 22);
                    if (rx.substring(position_head_instruction + (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2, position_head_instruction + (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2).equals("16")) {
                        data = rx.substring(position_head_instruction, position_head_instruction + (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2);
                        control_code1 = data.substring(18, 20);
                        control_code2 = data.substring(22, 24);
                        control_code3 = data.substring(24, 26);
                        //获取内部参数
                        if (control_code1.equals("91") & control_code2.equals("90") & control_code3.equals("1F")) {
                            if (AnalysisUtils.checkCSSum(data.substring(0, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2), 0, data.substring((AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2))) {
                                if (analysisParameter(data, parameterprotcol) == 1) {
                                    r = ProductType.WATER_METER_READ_METER_INTER_PARAMETER;
                                }
                            }
                        }
                        //校正时钟返回的数据
                        if (control_code1.equals("84") & control_code2.equals("A0") & control_code3.equals("15")) {
                            if (AnalysisUtils.checkCSSum(data.substring(0, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2), 0, data.substring((AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2))) {
                                r = ProductType.WATER_METER_CORRECTION_TIME;
                            }
                        }
                        //普通开关阀门返回的数据
                        if (control_code1.equals("84") & control_code2.equals("A0") & control_code3.equals("17")) {
                            if (AnalysisUtils.checkCSSum(data.substring(0, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2), 0, data.substring((AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2))) {
                                r = ProductType.WATER_METER_OPEN_CLOSE_VALVE_NORMAL;
                            }
                        }
                        //强制开关阀门返回的数据
                        if (control_code1.equals("89") & control_code2.equals("A0") & control_code3.equals("17")) {
                            if (AnalysisUtils.checkCSSum(data.substring(0, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2), 0, data.substring((AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2))) {
                                r = ProductType.WATER_METER_OPEN_CLOSE_VALVE_MANDATORY;
                            }
                        }
                        //读表数据    681061510110001111811B901FB3819999992981999999290000000032183908230817200499CF16
                        if (control_code1.equals("81") & control_code2.equals("90") & (control_code3.equals("1F") || control_code3.equals("2F"))) {
                            if (AnalysisUtils.checkCSSum(data.substring(0, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2), 0, data.substring((AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2))) {
                                if (data.substring(20, 20 + 2).equals("16")) {
                                    if (analysisWaterMeterData(data, productProtocol) == 1) {
                                        r = ProductType.WATER_METER_READ_METER_DATA;
                                    }
                                }
                                //户用阀控水表、户用不带阀水表
                                //681065230067001111811B901FB30000000029000000002900000000324000102711172004996716
                                //681065314129001111811B901FB41343000029000000002900000000324124102711172004005B16
                                if (data.substring(20, 20 + 2).equals("1B")) {
                                    if (analysisWaterMeterData1(data, productProtocol) == 1) {
                                        r = ProductType.WATER_METER_READ_METER_DATA;
                                    }
                                }
                                //管网大口径带压力水表
                                //6810123456780011118120901FB3601790942B601790942B00000000330B690000005755092711172006000E16
                                if (data.substring(20, 20 + 2).equals("20")) {
                                    if (analysisWaterMeterData2(data, productProtocol) == 1) {
                                        r = ProductType.WATER_METER_READ_METER_DATA;
                                    }
                                }
                            }
                        }
                        //读取分段参数  6810726305490011119A27903F000019004011001303000012000000000000020032000025000000003002007500004000001F16
                        if (control_code1.equals("9A") & control_code2.equals("90") & control_code3.equals("3F")) {
                            if (AnalysisUtils.checkCSSum(data.substring(0, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2), 0, data.substring((AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2))) {
                                r = ProductType.WATER_METER_READ_SEGMENTED_PARAMETER;
                            }
                        }
                        //读取分段参数  681011111111001111B810902F0C00C00300D4F01200B0365008C81016
                        if (control_code1.equals("B8") & control_code2.equals("90") & control_code3.equals("2F")) {
                            if (AnalysisUtils.checkCSSum(data.substring(0, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2), 0, data.substring((AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2))) {
                                r = ProductType.WATER_METER_READ_MEASUREMENT_PARAMETER;
                            }
                        }
                        //读取流量误差  681011111111001111B60E901FE803E803E803E803E803E803D316
                        if (control_code1.equals("B6") & control_code2.equals("90") & control_code3.equals("1F")) {
                            if (AnalysisUtils.checkCSSum(data.substring(0, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2), 0, data.substring((AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2))) {
                                r = ProductType.WATER_METER_READ_FLOW_DEVIATION;
                            }
                        }
                        //读取历史修改记录  681011111111001111362A907F1802052047356810111111110011113814A0210C00009000000040000012F0D436B00850C8A316006416
                        if (control_code1.equals("36") & control_code2.equals("90") & control_code3.equals("7F")) {
                            if (AnalysisUtils.checkCSSum(data.substring(0, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2), 0, data.substring((AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2))) {
                                r = ProductType.WATER_METER_READ_MODIFY_RECORD;
                            }
                        }
                        //不带单位读取日报月报  681072630549001111A14391201810020000000000000000000000000000FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF1603FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF6716
                        if (control_code1.equals("A1") & control_code2.equals("91") & control_code3.equals("20")) {
                            if (AnalysisUtils.checkCSSum(data.substring(0, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2), 0, data.substring((AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2))) {
                                r = ProductType.WATER_METER_READ_DAILY_REPORT_WITHOUT_UNIT;
                            }
                        }
                        //带单位读取日报  681011111111001111A12C922018A5A5A5FFFFFFFF2AFFFFFFFF2AFFFFFF33FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFC816
                        if (control_code1.equals("A1") & control_code2.equals("92") & control_code3.equals("20")) {
                            if (AnalysisUtils.checkCSSum(data.substring(0, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2), 0, data.substring((AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2))) {
                                r = ProductType.WATER_METER_READ_DAILY_REPORT_WITH_UNIT;
                            }
                        }
                        //读取压力标定  684811111111001111BB0E805500000000000F424000000D2577167B
                        if (control_code1.equals("BB") & control_code2.equals("80") & control_code3.equals("55")) {
                            if (AnalysisUtils.checkCSSum(data.substring(0, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2), 0, data.substring((AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2))) {
                                r = ProductType.WATER_METER_READ_PRESSURE;
                            }
                        }
                        //压力标定成功
                        if (control_code1.equals("BB") & control_code2.equals("80") & control_code3.equals("54")) {
                            if (AnalysisUtils.checkCSSum(data.substring(0, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2), 0, data.substring((AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2))) {
                                r = ProductType.WATER_METER_SET_PRESSURE_SUCCESS;
                            }
                        }
                        //压力标定失败
                        if (control_code1.equals("7B") & control_code2.equals("80") & control_code3.equals("54")) {
                            if (AnalysisUtils.checkCSSum(data.substring(0, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2), 0, data.substring((AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2))) {
                                r = ProductType.WATER_METER_SET_PRESSURE_FAIL;
                            }
                        }
                        //充值流量成功返回值  681065270014001111CD07901F00000003E8A816    （03E8表示1000L）
                        if (control_code1.equals("CD") & control_code2.equals("90") & control_code3.equals("1F")) {
                            if (AnalysisUtils.checkCSSum(data.substring(0, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2), 0, data.substring((AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2))) {
                                r = ProductType.WATER_METER_RECHARGE_FLOW;
                            }
                        }
                        //初始化流量成功返回值  681065270014001111DE07901F00000003E8B916    （03E8表示1000L）
                        if (control_code1.equals("DE") & control_code2.equals("90") & control_code3.equals("1F")) {
                            if (AnalysisUtils.checkCSSum(data.substring(0, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2), 0, data.substring((AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2))) {
                                r = ProductType.WATER_METER_INITIALIZE_FLOW;
                            }
                        }
                        //读取剩余水量、总充值水量、初始水量、当前水量  681065270014001111CD13902F00000000FCB70000640000000000000000F016
                        if (control_code1.equals("CD") & control_code2.equals("90") & control_code3.equals("2F")) {
                            if (AnalysisUtils.checkCSSum(data.substring(0, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2), 0, data.substring((AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2))) {
                                r = ProductType.WATER_METER_READ_REMAIN_FLOW;
                            }
                        }
                    }
                }
                //智能消火栓
                if (head_instruction.equals("68") & productType.equals(ProductType.HYDRANT_STRING)) {
                    length_tx = rx.substring(position_head_instruction + 20, position_head_instruction + 20 + 2);
                    if (rx.substring(position_head_instruction + (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2, position_head_instruction + (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2).equals("16")) {
                        data = rx.substring(position_head_instruction, position_head_instruction + (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2);
                        control_code1 = data.substring(18, 18 + 2);
                        control_code2 = data.substring(22, 22 + 2);
                        control_code3 = data.substring(24, 24 + 2);
                        if ((AnalysisUtils.checkCSSum(data.substring(0, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2), 0, data.substring((AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2)))) {
                            //消火栓读取报警参数     685978168017001111A30E901F500000030001300002000010FE16
                            if (control_code1.equals("A3") & control_code2.equals("90") & control_code3.equals("1F")) {
                                r = ProductType.HYDRANT_READ_WARNING_PARAMETER;
                            }
                            //消火栓读取报警状态     685978168017001111A005901F850600E716
                            if (control_code1.equals("A0") & control_code2.equals("90") & control_code3.equals("1F")) {
                                r = ProductType.HYDRANT_READ_WARNING_STATE;
                            }
                            //消火栓读取表数据     6859781680170011118121901FBF000000002B000000002B00000000330B69000000353916190817208506007C16
                            if (control_code1.equals("81") & control_code2.equals("90") & control_code3.equals("1F")) {
                                if (analysisHydrantData(data, productProtocol) == 1) {
                                    r = ProductType.HYDRANT_READ_METER_DATA;
                                }
                            }
                            //消火栓读取当前（正在）使用数据       685978168017001111A115901F00000000000000000029000000000000200000B616
                            if (control_code1.equals("A1") & control_code2.equals("90") & control_code3.equals("1F")) {
                                r = ProductType.HYDRANT_READ_CURRENT_USE_DATA;
                            }
                            //消火栓读取历史使用数据       685978168017001111A51E901F00000100FFFFFFFFFFFFFFFFFFFFFF2038967295FFFFFFFFFFFFFF207E16
                            if (control_code1.equals("A5") & control_code2.equals("90") & control_code3.equals("1F")) {
                                r = ProductType.HYDRANT_READ_HISTORY_USE_DATA;
                            }
                            //消火栓读取报警使能     685978168017001111A605901FF5F7075516
                            if (control_code1.equals("A6") & control_code2.equals("90") & control_code3.equals("1F")) {
                                r = ProductType.HYDRANT_READ_WARNING_ENABLE;
                            }
                            //消火栓读取GPS坐标        685978168017001111A22F901F0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000008816
                            if (control_code1.equals("A2") & control_code2.equals("90") & control_code3.equals("1F")) {
                                r = ProductType.HYDRANT_READ_GPS;
                            }

                            //消火栓读取参数        6859781680170011119148901F005037005037005037005037000000000000000000000000000000000000000000000000002000000036000000002002005000001282070080050050020037000001000023004116
                            if (control_code1.equals("91") & control_code2.equals("90") & control_code3.equals("1F")) {
                                if (analysisParameter(data, parameterprotcol) == 1) {
                                    r = ProductType.HYDRANT_READ_METER_INTER_PARAMETER;
                                }
                            }
                            //校正时钟返回的数据
                            if (control_code1.equals("84") & control_code2.equals("A0") & control_code3.equals("15")) {
                                if (AnalysisUtils.checkCSSum(data.substring(0, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2), 0, data.substring((AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2))) {
                                    r = ProductType.HYDRANT_CORRECTION_TIME;
                                }
                            }
                            //开关阀门返回的数据
                            if (control_code1.equals("A4") & control_code2.equals("90") & control_code3.equals("1F")) {
                                if (AnalysisUtils.checkCSSum(data.substring(0, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2), 0, data.substring((AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2))) {
                                    r = ProductType.HYDRANT_OPEN_CLOSE_VALVE;
                                }
                            }
                            //修改类型返回的数据
                            if (control_code1.equals("A8") & control_code2.equals("90") & control_code3.equals("1F")) {
                                if (AnalysisUtils.checkCSSum(data.substring(0, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2), 0, data.substring((AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2))) {
                                    r = ProductType.HYDRANT_CHANGE_TYPE;
                                }
                            }
                        }
                    }
                }
                //超声波热量表
                if (head_instruction.equals("68") & productType.equals(ProductType.HEAT_METER)) {
                    length_tx = rx.substring(position_head_instruction + 20, position_head_instruction + 20 + 2);
                    if (rx.substring(position_head_instruction + (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2, position_head_instruction + (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2).equals("16")) {
                        data = rx.substring(position_head_instruction, position_head_instruction + (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2);
                        control_code1 = data.substring(18, 18 + 2);
                        control_code2 = data.substring(22, 22 + 2);
                        control_code3 = data.substring(24, 24 + 2);
                        //热表读表数据
                        //MTH-9型       682041550526001111812E901FB63112990000766400000E000CD000000000000032000005002A0835008731002700000625192508172055049D16
                        //MTH-6型   FEFE682077268009001111812E901FB70001000005000100000500000000150000000032000001002A8329001330000100004433192508172000044B16
                        if (control_code1.equals("81") & control_code2.equals("90") & (control_code3.equals("1F") || control_code3.equals("2F"))) {
                            if (AnalysisUtils.checkCSSum(data.substring(0, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2), 0, data.substring((AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2))) {
                                if (MVCAnalysisJSMTRxStr(data, productProtocol) == 1 || MCAnalysisJSMTRxStr(data, productProtocol) == 1) {
                                    r = ProductType.HEAT_METER_READ_METER_DATA;
                                }
                            }
                        }
                        if (control_code1.equals("81") & control_code2.equals("1F") & control_code3.equals("90")) {
                            if (AnalysisUtils.checkCSSum(data.substring(0, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2), 0, data.substring((AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2))) {
                                if (MCAnalysisSDSLRxStr(data, productProtocol) == 1) {
                                    r = 1;
                                }
                            }
                        }
                        if (control_code1.equals("84") & control_code2.equals("A0") & control_code3.equals("17")) {
                            if (AnalysisUtils.checkCSSum(data.substring(0, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2), 0, data.substring((AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2))) {
                                if (VAnalysisJSMTRxStr(data, productProtocol) == 1) {
                                    r = 1;
                                }
                            }
                        }
                        //读取内部参数
                        if (control_code1.equals("91") & control_code2.equals("90") & control_code3.equals("1F")) {
                            if (AnalysisUtils.checkCSSum(data.substring(0, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2), 0, data.substring((AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2))) {
                                if (analysisParameter(data, parameterprotcol) == 1) {
                                    r = ProductType.HEAT_METER_READ_METER_INTER_PARAMETER;
                                }
                            }
                        }
                        if (control_code1.equals("CD") & control_code2.equals("90") & control_code3.equals("2F")) {
                            if (AnalysisUtils.checkCSSum(data.substring(0, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2), 0, data.substring((AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2))) {

                            }
                        }
                    }
                }
                if (head_instruction.equals("68") & productType.equals("25")) {
                    length_tx = rx.substring(position_head_instruction + 20, position_head_instruction + 20 + 2);
                    if (rx.substring(position_head_instruction + (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2, position_head_instruction + (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2).equals("16")) {
                        data = rx.substring(position_head_instruction, position_head_instruction + (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2);
                        control_code1 = data.substring(18, 18 + 2);
                        control_code2 = data.substring(22, 22 + 2);
                        control_code3 = data.substring(24, 24 + 2);
                        if (control_code1.equals("81") & control_code2.equals("1F") & control_code3.equals("90")) {
                            if (AnalysisUtils.checkCSSum(data.substring(0, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2), 0, data.substring((AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2))) {
                                if (MCAnalysisSDSLRxStr(data, productProtocol) == 1) {
                                    r = 1;
                                }
                            }
                        }
                    }
                }
                //M-BUS主机
                if (head_instruction.equals("68") & productType.equals(ProductType.COLLECTOR)) {
                    length_tx = rx.substring(position_head_instruction + 20, position_head_instruction + 20 + 2);
                    if (rx.substring(position_head_instruction + (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2, position_head_instruction + (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2).equals("16")) {
                        data = rx.substring(position_head_instruction, position_head_instruction + (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2);
                        control_code1 = data.substring(18, 18 + 2);
                        control_code2 = data.substring(22, 22 + 2);
                        control_code3 = data.substring(24, 24 + 2);
                        if (control_code1.equals("84") & control_code2.equals("A0") & control_code3.equals("18")) {
                            if (AnalysisUtils.checkCSSum(data.substring(0, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2), 0, data.substring((AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2))) {
                                if (MAnalysisJSMTRxStr(data, mbus) == 1) {
                                    r = 2;
                                }
                            }
                        }
                    }
                }
                //通断控制器
                if (head_instruction.equals("68") & productType.equals(ProductType.ON_OFF_CONTROLLER)) {
                    length_tx = rx.substring(position_head_instruction + 20, position_head_instruction + 20 + 2);
                    if (rx.substring(position_head_instruction + (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2, position_head_instruction + (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2).equals("16")) {
                        data = rx.substring(position_head_instruction, position_head_instruction + (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2);
                        control_code1 = data.substring(18, 18 + 2);
                        control_code2 = data.substring(22, 22 + 2);
                        control_code3 = data.substring(24, 24 + 2);
                        if (control_code1.equals("81") & control_code2.equals("90") & (control_code3.equals("1F") || control_code3.equals("2F"))) {
                            if (AnalysisUtils.checkCSSum(data.substring(0, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2), 0, data.substring((AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2))) {
                                if (AAnalysisJSMTRxStr(data, productProtocol) == 1) {
                                    r = 1;
                                }
                            }
                        }
                        if (control_code1.equals("84") & control_code2.equals("A0") & control_code3.equals("17")) {
                            if (AnalysisUtils.checkCSSum(data.substring(0, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2), 0, data.substring((AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2))) {
                                if (VAnalysisJSMTRxStr(data, productProtocol) == 1) {
                                    r = 1;
                                }
                            }
                        }
                    }
                }
                //LORA无线模块
                if (head_instruction.equals("68") & productType.equals(ProductType.CONTROL_VALVE)) {
                    length_tx = rx.substring(position_head_instruction + 20, position_head_instruction + 20 + 2);
                    if (rx.substring(position_head_instruction + (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2, position_head_instruction + (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2).equals("16")) {
                        data = rx.substring(position_head_instruction, position_head_instruction + (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2);
                        control_code1 = data.substring(18, 18 + 2);
                        control_code2 = data.substring(22, 22 + 2);
                        control_code3 = data.substring(24, 24 + 2);
                        if (control_code1.equals("81") & control_code2.equals("90") & (control_code3.equals("1F") || control_code3.equals("2F"))) {
                            if (AnalysisUtils.checkCSSum(data.substring(0, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2), 0, data.substring((AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2))) {
                                if (VSmartAnalysisJSMTRxStr(data, productProtocol) == 1) {
                                    r = 1;
                                }
                            }
                        }
                        if (control_code1.equals("84") & control_code2.equals("A0") & control_code3.equals("17")) {
                            if (AnalysisUtils.checkCSSum(data.substring(0, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2), 0, data.substring((AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2))) {
                                if (VAnalysisJSMTRxStr(data, productProtocol) == 1) {
                                    r = 1;
                                }
                            }
                        }
                    }
                }
                if (head_instruction.equals("68") & productType.equals("51")) {
                    length_tx = rx.substring(position_head_instruction + 20, position_head_instruction + 20 + 2);
                    if (rx.substring(position_head_instruction + (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2, position_head_instruction + (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2).equals("16")) {
                        data = rx.substring(position_head_instruction, position_head_instruction + (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2);
                        control_code1 = data.substring(18, 18 + 2);
                        control_code2 = data.substring(22, 22 + 2);
                        control_code3 = data.substring(24, 24 + 2);
                        if (control_code1.equals("84") & control_code2.equals("90") & control_code3.equals("3F")) {
                            if (AnalysisUtils.checkCSSum(data.substring(0, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2), 0, data.substring((AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2))) {
                                if (ReportAnalysisJSMTRxStr(data, productProtocol) == 1) {
                                    r = 3;
                                }
                            }
                        }
                        if (control_code1.equals("85") & control_code2.equals("90") & control_code3.equals("3F")) {
                            if (AnalysisUtils.checkCSSum(data.substring(0, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2), 0, data.substring((AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2))) {
                                if (AvgAnalysisJSMTRxStr(data, ssumheat) == 1) {
                                    r = 4;
                                }
                            }
                        }
                    }
                }
                if (head_instruction.equals("68") & productType.equals(ProductTypeS1) & head_instruction.equals(HeadS1)) {
                    length_tx = rx.substring(position_head_instruction + 2, position_head_instruction + 2 + 2);
                    if (rx.substring(position_head_instruction + (AnalysisUtils.HexS2ToInt(length_tx) + 6) * 2 - 2, position_head_instruction + (AnalysisUtils.HexS2ToInt(length_tx) + 6) * 2).equals("16")) {
                        data = rx.substring(position_head_instruction, position_head_instruction + (AnalysisUtils.HexS2ToInt(length_tx) + 6) * 2);
                        control_code1 = data.substring(8, 8 + 2);
                        control_code2 = data.substring(12, 12 + 2);
                        if (control_code1.equals("08") & control_code2.equals("72")) {
                            if (AnalysisUtils.checkCSSum(data.substring(0, (AnalysisUtils.HexS2ToInt(length_tx) + 6) * 2 - 2 - 2), 4, data.substring((AnalysisUtils.HexS2ToInt(length_tx) + 6) * 2 - 2 - 2, (AnalysisUtils.HexS2ToInt(length_tx) + 6) * 2 - 2))) {
                                if (MEAnalysisRxStr(data, productProtocol) == 1) {
                                    r = 1;
                                }
                            }
                        }
                    }
                }
                //智能恒流阀
                if (head_instruction.equals("68") & productType.equals(ProductType.CONSTANT_FLOW_VALVE)) {
                    length_tx = rx.substring(position_head_instruction + 20, position_head_instruction + 22);
                    if (rx.substring(position_head_instruction + (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2, position_head_instruction + (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2).equals("16")) {
                        data = rx.substring(position_head_instruction, position_head_instruction + (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2);
                        control_code1 = data.substring(18, 20);
                        control_code2 = data.substring(22, 24);
                        control_code3 = data.substring(24, 26);
                        //读取恒流阀数据 684983091071001111 D61E901F B600000000000000C3003200000000000029 0509 0404111311061820 E016
                        if (control_code1.equals("D6") & control_code2.equals("90") & control_code3.equals("1F")) {
                            if (AnalysisUtils.checkCSSum(data.substring(0, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2), 0, data.substring((AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2))) {
                                if (parserConstantFlowValveInternalData(data, productProtocol)) {
                                    r = ProductType.CONSTANT_FLOW_VALVE_READ_PARAMETER;
                                }
                            }
                        }
                        //                                                                控制字    圈数    流速   序号
                        //指定圈数（5000圈）           的回复 684920180611001111 D608A426   11      1518    0000    9C    A416
                        //增大圈数（500圈）            的回复 684920180611001111 D608A426   13      01F4    0000    9D    6F16
                        //减小圈数（500圈）            的回复 684920180611001111 D608A426   12      01F4    0000    9E    6F16
                        //指定流速（500L/h）           的回复 684920180611001111 D608A426   21      0000    01F4    9F    7F16
                        //增大流速（50L/h）            的回复 684920180611001111 D608A426   22      0000    0032    90    AE16
                        //减小流速（50L/h）            的回复 684920180611001111 D608A426   23      0000    0032    91    B016
                        //流速、圈数回零               的回复 684920180611001111 D608A426   30      0000    0000    92    8C16
                        //流量点标定（1400圈-100L/h）  的回复 684920180611001111 D608A426   41~45   0578    0064    93    7F16
                        //设置睡眠月份（4月~9月）      的回复 684920180611001111 D608A426   50      0009    0004    94    BB16
                        //设置上传间隔（4小时）        的回复 684920180611001111 D608A426   51      0000    0004    95    B416
                        if (control_code1.equals("D6") & control_code2.equals("A4") & control_code3.equals("26")) {
                            if (AnalysisUtils.checkCSSum(data.substring(0, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2), 0, data.substring((AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2 - 2, (AnalysisUtils.HexS2ToInt(length_tx) + 13) * 2 - 2))) {
                                if (parserConstantFlowValveReplyData(data, productProtocol)) {
                                    switch (data.substring(26, 28)) {
                                        case "11":
                                            r = ProductType.CONSTANT_FLOW_VALVE_TURNS;
                                            break;
                                        case "12":
                                            r = ProductType.CONSTANT_FLOW_VALVE_REDUCE_TURNS;
                                            break;
                                        case "13":
                                            r = ProductType.CONSTANT_FLOW_VALVE_ADD_TURNS;
                                            break;
                                        case "21":
                                            r = ProductType.CONSTANT_FLOW_VALVE_FLOW;
                                            break;
                                        case "22":
                                            r = ProductType.CONSTANT_FLOW_VALVE_ADD_FLOW;
                                            break;
                                        case "23":
                                            r = ProductType.CONSTANT_FLOW_VALVE_REDUCE_FLOW;
                                            break;
                                        case "30":
                                            r = ProductType.CONSTANT_FLOW_VALVE_ZEROING;
                                            break;
                                        case "41":
                                            r = ProductType.CONSTANT_FLOW_VALVE_DEMARCATE1;
                                            break;
                                        case "42":
                                            r = ProductType.CONSTANT_FLOW_VALVE_DEMARCATE2;
                                            break;
                                        case "43":
                                            r = ProductType.CONSTANT_FLOW_VALVE_DEMARCATE3;
                                            break;
                                        case "44":
                                            r = ProductType.CONSTANT_FLOW_VALVE_DEMARCATE4;
                                            break;
                                        case "45":
                                            r = ProductType.CONSTANT_FLOW_VALVE_DEMARCATE5;
                                            break;
                                        case "50":
                                            r = ProductType.CONSTANT_FLOW_VALVE_SLEEP_MONTHS;
                                            break;
                                        case "51":
                                            r = ProductType.CONSTANT_FLOW_VALVE_UPLOAD_INTERVAL;
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return r;
        } catch (Exception e) {
            r = 0;
            return r;
        }
    }

    /**
     * 解析恒流阀内部数据数据
     *
     * @param rx 指令原文
     * @return 解析后的对象
     */
    private static boolean parserConstantFlowValveInternalData(String rx, ProductProtocol productProtocol) {
        //读取恒流阀数据 684920180611001111D61E901FB60000009028001528019A00003495000029050904011208120618207A16
        // 6849201806 11001111D6 1E901FB400 0000002800 1528019A00 0087290000 2905090401 1416110618 20 DE16
        try {
            productProtocol.setMeterID(rx.substring(4, 12));
            try {
                float volF = AnalysisUtils.HexS2ToInt(rx.substring(26, 28)) * 2 / 100f;
                productProtocol.setVol(String.valueOf(MathUtils.formatFloat(volF, 2)));
            } catch (Exception e) {
                productProtocol.setVol("err 电压 " + rx.substring(26, 28));
                return false;
            }
            try {
                String t1S = rx.substring(32, 34) + rx.substring(30, 32) + "." + rx.substring(28, 30);
                productProtocol.setT1InP(Double.valueOf(t1S));
            } catch (Exception e) {
                productProtocol.setT1InP(0);
                return false;
            }
            try {
                String t2S = rx.substring(38, 40) + rx.substring(36, 38) + "." + rx.substring(34, 36);
                productProtocol.setT2InP(Double.valueOf(t2S));
            } catch (Exception e) {
                productProtocol.setT2InP(0);
                return false;
            }
            productProtocol.setQuanShu(AnalysisUtils.HexS4ToInt(rx.substring(40, 44)));
            productProtocol.setFlowRate(AnalysisUtils.HexS4ToInt(rx.substring(44, 48)));
            String status = rx.substring(48, 50);
            if (status.equals("00")) {
                productProtocol.setStatus("正常");
            } else {
                productProtocol.setStatus("其他");
            }
            String valveStatus = rx.substring(50, 52);
            if (valveStatus.equals("00")) {
                productProtocol.setValveStatus("正常");
            } else {
                productProtocol.setValveStatus("其他");
            }
            try {
                String totalS = rx.substring(58, 60) + rx.substring(56, 58) + rx.substring(54, 56) + rx.substring(52, 54);
                productProtocol.setTotal(Double.valueOf(totalS));
            } catch (Exception e) {
                productProtocol.setTotal(0);
                return false;
            }
            productProtocol.setTotalUnit(AnalysisUtils.getFlowUnit(rx.substring(60, 62)));
            productProtocol.setStartSleep(AnalysisUtils.HexS2ToInt(rx.substring(62, 64)));
            productProtocol.setStopSleep(AnalysisUtils.HexS2ToInt(rx.substring(64, 66)));
            productProtocol.setUploadInterval(AnalysisUtils.HexS2ToInt(rx.substring(66, 68)));
            String meterTime = rx.substring(80, 82) + rx.substring(78, 80) + "-" + rx.substring(76, 78) + "-" + rx.substring(74, 76) + " " + rx.substring(72, 74) + ":" + rx.substring(70, 72) + ":" + rx.substring(68, 70);
            productProtocol.setTimeInP(meterTime);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 解析恒流阀对下发指令的回复
     *
     * @param rx 指令原文
     * @return 解析后的对象
     */
    private static boolean parserConstantFlowValveReplyData(String rx, ProductProtocol productProtocol) {
        //                                                                控制字    圈数    流速   序号
        //指定圈数（5000圈）           的回复 684920180611001111 D608A426   11      1518    0000    9C    A416
        try {
            productProtocol.setMeterID(rx.substring(4, 12));
            try {
                productProtocol.setQuanShu(AnalysisUtils.HexS4ToInt(rx.substring(28, 32)));
                productProtocol.setFlowRate(AnalysisUtils.HexS4ToInt(rx.substring(32, 36)));
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
