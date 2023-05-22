package region;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import utils.params;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.nio.charset.StandardCharsets;

public class FTPConnector {
    /*
    Basic Conf.
     */
    private static final int FTP_PORT = 21;
    private static final String FTP_IP = params.FTP_IP;
    private static final String USER_NAME = params.Ftp_Username;
    private static final String PASSWORD = params.Ftp_Password;
    
    private static final FTPClient FTP_CLIENT = new FTPClient();

    public static boolean connectFTP() throws Exception {
        FTP_CLIENT.connect(FTP_IP, FTP_PORT);//连接FTP服务器
        FTP_CLIENT.login(USER_NAME, PASSWORD);//登录FTP服务器
        return FTPReply.isPositiveCompletion(FTP_CLIENT.getReplyCode());//连接状态
    }

    public static boolean unloadFile(String fileName){
        boolean result = false;

        String ftpPath = "\\Region01"; // need to get result from zookeeperManager
        String uploadPath = ""; // region本地文件存储地址

        InputStream in = null;
        try {
            //切换到上传目录
            if (!FTP_CLIENT.changeWorkingDirectory(ftpPath)) {
                //目录不存在就创建目录
                FTP_CLIENT.makeDirectory(ftpPath);
            }
            FTP_CLIENT.changeWorkingDirectory(ftpPath);

            //设置PassiveMode传输
            FTP_CLIENT.enterLocalPassiveMode();
            // 设置以二进制流的方式传输
            FTP_CLIENT.setFileType(FTPClient.BINARY_FILE_TYPE);

            File file = new File(uploadPath + fileName);
            in = new FileInputStream(file);
            String tempName = ftpPath + "/" + file.getName();

            result = FTP_CLIENT.storeFile(new String(tempName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1), in);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /*
    Download All Files
     */
    public static Boolean downloadAllFiles(String filePath, String downloadPath) {
        boolean result = false;

        try {
            FTP_CLIENT.enterLocalPassiveMode();

            FTP_CLIENT.changeWorkingDirectory(filePath + "/");
            FTPFile[] files = FTP_CLIENT.listFiles(); // 文件列表

            for (FTPFile file : files) {
                File downFile = new File(downloadPath + file.getName());
                OutputStream out = new FileOutputStream(downFile);
                result = FTP_CLIENT.retrieveFile(new String(file.getName().getBytes("UTF-8"), "ISO-8859-1"), out);
                out.flush();
                out.close();
                if (!result) {
                    break;
                }
            }
            return result;
        } catch (Exception e) {
            result = false;
            e.printStackTrace();
        }
        return result;
    }
}