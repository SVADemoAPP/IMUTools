package com.example.a39773.imu.untils;

import com.example.a39773.imu.ImuInfo;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

public class CsvFileUntils {

    // 写入 .csv 文件
    public static void writeCsv(String filePath, List<ImuInfo> imuInfos) {
        try {
            File file = new File(filePath);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));  // 防止出现乱码
            // 添加头部
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("timestamp", "omega_x", "omega_y", "omega_z", "alpha_x", "alpha_y", "alpha_y"));
            // 添加内容
            for (ImuInfo imuInfo :imuInfos) {
                csvPrinter.printRecord(
                        imuInfo.getTimestamp(),
                        imuInfo.getOmegaX(),
                        imuInfo.getOmegaY(),
                        imuInfo.getOmegaZ(),
                        imuInfo.getAlphaX(),
                        imuInfo.getAlphaY(),
                        imuInfo.getAlphaZ());
            }
            csvPrinter.printRecord();
            csvPrinter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
