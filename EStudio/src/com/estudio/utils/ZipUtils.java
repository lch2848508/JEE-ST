package com.estudio.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.Zip64Mode;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

public class ZipUtils {

    /**
     * 把文件压缩成zip格式
     * 
     * @param files
     *            需要压缩的文件
     * @param zipFilePath
     *            压缩后的zip文件路径 ,如"D:/test/aa.zip";
     */
    public static void compressFiles2Zip(File[] files, String zipFilePath) {
        ZipArchiveOutputStream zaos = null;
        try {
            File zipFile = new File(zipFilePath);
            zaos = new ZipArchiveOutputStream(zipFile);
            // Use Zip64 extensions for all entries where they are
            // required
            zaos.setUseZip64(Zip64Mode.AsNeeded);

            // 将每个文件用ZipArchiveEntry封装
            // 再用ZipArchiveOutputStream写到压缩文件中
            for (File file : files) {
                ZipArchiveEntry zipArchiveEntry = new ZipArchiveEntry(file, file.getName());
                zaos.putArchiveEntry(zipArchiveEntry);
                InputStream is = null;
                try {
                    is = new FileInputStream(file);
                    byte[] buffer = new byte[1024 * 16];
                    int len = -1;
                    while ((len = is.read(buffer)) != -1) {
                        zaos.write(buffer, 0, len);
                    }
                    zaos.closeArchiveEntry();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    if (is != null)
                        is.close();
                }

            }
            zaos.finish();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (zaos != null) {
                    zaos.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 把zip文件解压到指定的文件夹
     * 
     * @param zipFilePath
     *            zip文件路径, 如 "D:/test/aa.zip"
     * @param saveFileDir
     *            解压后的文件存放路径, 如"D:/test/"
     */
    public static void decompressZip(String zipFilePath, String saveFileDir) {

        File file = new File(zipFilePath);
        if (file.exists()) {
            InputStream is = null;
            // can read Zip archives
            ZipArchiveInputStream zais = null;
            try {
                is = new FileInputStream(file);
                zais = new ZipArchiveInputStream(is);
                ArchiveEntry archiveEntry = null;
                // 把zip包中的每个文件读取出来
                // 然后把文件写到指定的文件夹
                while ((archiveEntry = zais.getNextEntry()) != null) {
                    // 获取文件名
                    String entryFileName = archiveEntry.getName();
                    // 构造解压出来的文件存放路径
                    String entryFilePath = saveFileDir + entryFileName;
                    byte[] content = new byte[(int) archiveEntry.getSize()];
                    zais.read(content);
                    OutputStream os = null;
                    try {
                        // 把解压出来的文件写到指定路径
                        File entryFile = new File(entryFilePath);
                        os = new FileOutputStream(entryFile);
                        os.write(content);
                    } catch (IOException e) {
                        throw new IOException(e);
                    } finally {
                        if (os != null) {
                            os.flush();
                            os.close();
                        }
                    }

                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    if (zais != null) {
                        zais.close();
                    }
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }

    public static void zip(String outZipFilename, List<String> inputFiles) {
        File[] files = new File[inputFiles.size()];
        for (int i = 0; i < inputFiles.size(); i++)
            files[i] = new File(inputFiles.get(i));
        compressFiles2Zip(files, outZipFilename);
    }

}
