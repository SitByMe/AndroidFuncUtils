package ptv.example.zoulinheng.androidutils.utils.zip;

import com.blankj.utilcode.util.LogUtils;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.Deflater;

import ptv.example.zoulinheng.androidutils.Constants;
import ptv.example.zoulinheng.androidutils.utils.deviceutils.FileUtils;

/**
 * Created by lhZou on 2018/8/28.
 * desc:压缩/解压缩工具类
 */
public class ZipUtils {

    /**
     * 判断是不是压缩文件
     *
     * @param fileName 文件名称
     * @return
     */
    public static boolean isZip(String fileName) {
        return fileName.endsWith(".zip") || fileName.endsWith(".rar") || fileName.endsWith(".7z");
    }

    /**
     * 压缩方法
     *
     * @param inputFileName 你要压缩的文件夹（整个完整路径）
     * @param zipFileName   压缩后的文件（整个完整路径）
     */
    public static void zip(String inputFileName, String zipFileName) {
        try {
            zip(zipFileName, new File(inputFileName));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 解压通用方法
     *
     * @param zipFileString 文件路径
     * @param outPathString 解压路径
     * @param listener      加压监听
     */
    public static void UnZipFile(final String zipFileString, final String outPathString, final ZipListener listener) {
        Thread zipThread = new UnZipMainThread(zipFileString, outPathString, listener);
        zipThread.start();
    }

    public interface ZipListener {
        /**
         * 开始解压
         */
        void zipStart();

        /**
         * 解压成功
         */
        void zipSuccess();

        /**
         * 解压进度
         */
        void zipProgress(int progress);

        /**
         * 解压失败
         */
        void zipFail();
    }

    private static void zip(String zipFileName, File inputFile) throws Exception {
        if (!FileUtils.isFileExists(Constants.BASE_DOWNLOAD_ZIP_PATH)) {
            if (FileUtils.createOrExistsDir(Constants.BASE_DOWNLOAD_ZIP_PATH)) {
                LogUtils.i("创建文件夹-".concat(Constants.BASE_DOWNLOAD_ZIP_PATH).concat("-成功"));
            }
        }
        FileOutputStream f = new FileOutputStream(zipFileName);
        //使用指定校验和创建输出流
        CheckedOutputStream csum = new CheckedOutputStream(f, new CRC32());

        ZipOutputStream out = new ZipOutputStream(csum);
        //支持中文
        out.setEncoding("UTF-8");
        //设置压缩包注释
        out.setComment("生成的代码");

        BufferedOutputStream bos = new BufferedOutputStream(out);
        //启用压缩
        out.setMethod(ZipOutputStream.DEFLATED);
        //压缩级别为最强压缩，但时间要花得多一点
        out.setLevel(Deflater.BEST_COMPRESSION);

        //ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));
        zip(out, bos, inputFile, "");
        bos.close();
        out.flush();
        out.close();
    }

    private static void zip(ZipOutputStream out, BufferedOutputStream bos, File f, String base) throws Exception {
        if (f.isDirectory()) {
            File[] fl = f.listFiles();
            out.putNextEntry(new ZipEntry(base + "/"));
            base = base.length() == 0 ? "" : base + "/";
            for (File aFl : fl) {
                zip(out, bos, aFl, base + aFl.getName());
            }
        } else {
            out.putNextEntry(new ZipEntry(base));
            FileInputStream in = new FileInputStream(f);
            int b;
            //System.out.println(base);
            while ((b = in.read()) != -1) {
                bos.write(b);
            }
            bos.flush();
            in.close();
        }
    }
}
