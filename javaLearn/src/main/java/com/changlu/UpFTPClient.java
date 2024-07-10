//package com.changlu;
//
//import org.apache.commons.net.ftp.FTP;
//import org.apache.commons.net.ftp.FTPClient;
//import org.apache.commons.net.ftp.FTPFile;
//import org.apache.commons.net.ftp.FTPReply;
//
//import java.io.*;
//
///**
// * 提供上传图片文件, 文件夹
// * @author MYMOON
// *
// */
//public class UpFTPClient {
//
//    private ThreadLocal<FTPClient> ftpClientThreadLocal = new ThreadLocal<FTPClient>();
//
//    private String encoding = "UTF-8";
//    private int clientTimeout = 1000 * 30;
//    private boolean binaryTransfer = true;
//
//    private String host;
//    private int port;
//    private String username;
//    private String password;
//
//    private static String localCharset = "GBK";
//    /**
//     * OPTS UTF8字符串常量
//     **/
//    private static final String OPTS_UTF8 = "OPTS UTF8";
//    /**
//     * UTF-8字符编码
//     **/
//    private static final String CHARSET_UTF8 = "UTF-8";
//    /**
//     * FTP协议里面，规定文件名编码为iso-8859-1
//     **/
//    private static String serverCharset = "ISO-8859-1";
//
//    private FTPClient getFTPClient() {
//        if (ftpClientThreadLocal.get() != null && ftpClientThreadLocal.get().isConnected()) {
//            return ftpClientThreadLocal.get();
//        } else {
//            FTPClient ftpClient = new FTPClient(); // 构造一个FtpClient实例
//            try {
//                connect(ftpClient); // 连接到ftp服务器
////                if (FTPReply.isPositiveCompletion(ftpClient.sendCommand("OPTS UTF8", "ON"))) {// 开启服务器对UTF-8的支持，如果服务器支持就用UTF-8编码，否则就使用本地编码（GBK）.
////                    localCharset = CHARSET_UTF8;
////                }
////                ftpClient.setControlEncoding(localCharset);
//                ftpClient.setControlEncoding("GBK"); // 设置字符集
//                setFileType(ftpClient); //设置文件传输类型
//                ftpClient.setSoTimeout(clientTimeout);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            ftpClientThreadLocal.set(ftpClient);
//            return ftpClient;
//        }
//    }
//
//    /**
//     * 连接到ftp服务器
//     */
//    private boolean connect(FTPClient ftpClient) throws Exception {
//        try {
//            ftpClient.connect(host, port);
//            // 连接后检测返回码来校验连接是否成功
//            int reply = ftpClient.getReplyCode();
//            if (FTPReply.isPositiveCompletion(reply)) {
//                //登陆到ftp服务器
//                if (ftpClient.login(username, password)) {
//                    return true;
//                }
//            } else {
//                ftpClient.disconnect();
//                throw new Exception("FTP server refused connection.");
//            }
//        } catch (IOException e) {
//            if (ftpClient.isConnected()) {
//                try {
//                    ftpClient.disconnect(); //断开连接
//                } catch (IOException e1) {
//                    throw new Exception("Could not disconnect from server.", e1);
//                }
//
//            }
//            throw new Exception("Could not connect to server.", e);
//        }
//        return false;
//    }
//
//    /**
//     * 断开ftp连接
//     */
//    public void disconnect() throws Exception {
//        try {
//            FTPClient ftpClient = getFTPClient();
//            ftpClient.logout();
//            if (ftpClient.isConnected()) {
//                ftpClient.disconnect();
//                ftpClient = null;
//            }
//        } catch (IOException e) {
//            throw new Exception("Could not disconnect from server.", e);
//        }
//    }
//
//    /**
//     * 设置文件传输类型
//     *
//     * @throws IOException
//     */
//    private void setFileType(FTPClient ftpClient) throws Exception {
//        try {
//            if (binaryTransfer) {
//                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
//            } else {
//                ftpClient.setFileType(FTPClient.ASCII_FILE_TYPE);
//            }
//        } catch (IOException e) {
//            throw new Exception("Could not to set file type.", e);
//        }
//    }
//
//
//    //---------------------------------------------------------------------
//    // public method
//    //---------------------------------------------------------------------
//
//    /**
//     * 上传一个本地文件到远程指定文件
//     *
//     * @param remoteDir 远程文件名(包括完整路径)
//     * @param localAbsoluteFile 本地文件名(包括完整路径)
//     * @return 成功时，返回true，失败返回false
//     */
//    public boolean uploadFile(String localAbsoluteFile, String remoteDir, String filename) throws Exception {
//        InputStream input = null;
//        try {
//            getFTPClient().makeDirectory(remoteDir);
//            // 处理传输
//            input = new FileInputStream(localAbsoluteFile);
//            boolean rs = getFTPClient().storeFile(remoteDir+filename, input);
//            return rs;
//        } catch (FileNotFoundException e) {
//            throw new Exception("local file not found.", e);
//        } catch (IOException e) {
//            throw new Exception("Could not put file to server.", e);
//        } finally {
//            try {
//                if (input != null) {
//                    input.close();
//                }
//            } catch (Exception e) {
//                throw new Exception("Couldn't close FileInputStream.", e);
//            }
//        }
//    }
//
//
//
//    /***
//     * @上传文件夹
//     * @param localDirectory  当地文件夹
//     * @param remoteDirectoryPath Ftp 服务器路径 以目录"/"结束
//     * */
//    public boolean uploadDirectory(String localDirectory, String remoteDirectoryPath) {
//        File src = new File(localDirectory);
//        try {
//            getFTPClient().makeDirectory(remoteDirectoryPath);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        File[] allFile = src.listFiles();
//        for (int currentFile = 0; currentFile < allFile.length; currentFile++) {
//            if (!allFile[currentFile].isDirectory()) {
//                String srcName = allFile[currentFile].getPath().toString();
//                uploadFile(new File(srcName), remoteDirectoryPath);
//            }
//        }
//        for (int currentFile = 0; currentFile < allFile.length; currentFile++) {
//            if (allFile[currentFile].isDirectory()) {
//                // 递归
//                uploadDirectory(allFile[currentFile].getPath().toString(),    remoteDirectoryPath);
//            }
//        }
//        return true;
//    }
//
//    /***
//     * 上传Ftp文件 配合文件夹上传
//     * @param localFile 当地文件
//     *            - 应该以/结束
//     * */
//    private boolean uploadFile(File localFile, String romotUpLoadePath) {
//        BufferedInputStream inStream = null;
//        boolean success = false;
//        try {
//            getFTPClient().changeWorkingDirectory(romotUpLoadePath);// 改变工作路径
//            inStream = new BufferedInputStream(new FileInputStream(localFile));
//            success = getFTPClient().storeFile(localFile.getName(), inStream);
//            if (success == true) {
//                return success;
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (inStream != null) {
//                try {
//                    inStream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return success;
//    }
//
//    public long getFileSize(String path) throws IOException {
//        FTPFile[] ftpFiles = getFTPClient().listFiles(encodePath(path));
////        FTPFile[] ftpFiles = getFTPClient().listFiles(path);
//        if (ftpFiles == null || ftpFiles.length == 0) {
//            throw new IOException("file does not exist path: " + path);
//        }
//        return ftpFiles[0].getSize();
//    }
//
//    private String encodePath(String path) throws IOException {
////        return new String(path.getBytes(localCharset), FTP.DEFAULT_CONTROL_ENCODING);
//        return new String(path.getBytes(), FTP.DEFAULT_CONTROL_ENCODING);
//    }
//
//    public String[] listNames(String remotePath, boolean autoClose) throws Exception{
//        try {
//            String[] listNames = getFTPClient().listNames(remotePath);
//            return listNames;
//        } catch (IOException e) {
//            throw new Exception("列出远程目录下所有的文件时出现异常", e);
//        } finally {
//            if (autoClose) {
//                disconnect(); //关闭链接
//            }
//        }
//    }
//
//    public String getEncoding() {
//        return encoding;
//    }
//
//    public void setEncoding(String encoding) {
//        this.encoding = encoding;
//    }
//
//    public int getClientTimeout() {
//        return clientTimeout;
//    }
//
//    public void setClientTimeout(int clientTimeout) {
//        this.clientTimeout = clientTimeout;
//    }
//
//    public String getHost() {
//        return host;
//    }
//
//    public void setHost(String host) {
//        this.host = host;
//    }
//
//    public int getPort() {
//        return port;
//    }
//
//    public void setPort(int port) {
//        this.port = port;
//    }
//
//    public String getUsername() {
//        return username;
//    }
//
//    public void setUsername(String username) {
//        this.username = username;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//
//    public boolean isBinaryTransfer() {
//        return binaryTransfer;
//    }
//
//    public void setBinaryTransfer(boolean binaryTransfer) {
//        this.binaryTransfer = binaryTransfer;
//    }
//
//
//    /**
//     * 目标路径 按年月存图片: 201405
//     * 限时打折 /scenery/  ticket,  hotel, catering
//     * 浪漫之游 /discount/
//     *
//     * @param args
//     */
//    public static void main(String[] args) {
//        UpFTPClient ftp = new UpFTPClient();
//        ftp.setHost("172.16.82.106");
//        ftp.setPort(21);
//        ftp.setUsername("admin");
//        ftp.setPassword("Abc!@#135");
//        try {
//            // 列表
////            String[] listNames = ftp.listNames("ls /home/dtstack/changlu/长路空间", true);
//            long fileSize = ftp.getFileSize("/data/ftp/changlu-workspace/长路/外卖销售目标.xlsx");
////            long fileSize = ftp.getFileSize("/data/ftp/changlu-workspace/长路");
////            long fileSize = ftp.getFileSize("/data/ftp/changlu-workspace/changlu");
//            System.out.println(fileSize);
//            ftp.disconnect();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//}
