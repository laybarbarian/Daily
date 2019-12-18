package com.hp.daily.util;


import com.jcraft.jsch.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SFTPUtil {

    private Session session;

    public SFTPUtil() {
    }

    private String host = "49.233.179.36";
    private int port = 22;

    private String username = "root";
    private String password = "Laybarbarian~1";

    private String baseroot = "/data/";

    public ChannelSftp sftp;

    public void login(){
        try {
            JSch jsch = new JSch();

            session = jsch.getSession(username, host, port);
            session.setPassword(password);
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");

            session.setConfig(config);
            session.connect();

            Channel channel = session.openChannel("sftp");
            channel.connect();

            sftp = (ChannelSftp) channel;

        } catch (JSchException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭连接 server
     */
    public void logout(){
        if (sftp != null) {
            if (sftp.isConnected()) {
                sftp.disconnect();
            }
        }
        if (session != null) {
            if (session.isConnected()) {
                session.disconnect();
            }
        }
    }

    public void upload(String directory, String sftpFileName, InputStream input) throws SftpException{
        try {
            sftp.cd(baseroot+directory);
        } catch (SftpException e) {
            sftp.mkdir(baseroot+directory);
            sftp.cd(baseroot+directory);
        }
        sftp.put(input, sftpFileName);
    }

}
