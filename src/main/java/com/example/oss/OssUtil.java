package com.example.oss;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import com.aliyun.openservices.ClientConfiguration;
import com.aliyun.openservices.ClientException;
import com.aliyun.openservices.ServiceException;
import com.aliyun.openservices.oss.OSSClient;
import com.aliyun.openservices.oss.OSSErrorCode;
import com.aliyun.openservices.oss.OSSException;
import com.aliyun.openservices.oss.model.CannedAccessControlList;
import com.aliyun.openservices.oss.model.GetObjectRequest;
import com.aliyun.openservices.oss.model.OSSObjectSummary;
import com.aliyun.openservices.oss.model.ObjectListing;
import com.aliyun.openservices.oss.model.ObjectMetadata;

public class OssUtil {

    /**
     * 阿里云ACCESS_ID
     */
    private static String ACCESS_ID = "vtXujYmNXNaTGV93";
    /**
     * 阿里云ACCESS_KEY
     */
    private static String ACCESS_KEY = "aFg7zGkmk8DkxbLHgOQlDEIlYW2INn";
    /**
     * 阿里云OSS_ENDPOINT  青岛Url
     */
    private static String OSS_ENDPOINT = "http://oss-cn-hangzhou.aliyuncs.com";

    /**
     * 阿里云BUCKET_NAME  OSS
     */
    private static String BUCKET_NAME = "test-upload-paper";

    public static void testUpload(String Objectkey, String uploadFilePath) {
        // 使用默认的OSS服务器地址创建OSSClient对象,不叫OSS_ENDPOINT代表使用杭州节点，青岛节点要加上不然包异常
        OSSClient client = new OSSClient(OSS_ENDPOINT, ACCESS_ID, ACCESS_KEY);
        try {
            ensureBucket(client, BUCKET_NAME);
            setBucketPublicReadable(client, BUCKET_NAME);
            System.out.println("正在上传...");
            uploadFile(client, BUCKET_NAME, Objectkey, uploadFilePath);
            System.out.println("上传完成!");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    public static void testDowload(String Objectkey, String downloadFilePath) {
        // 使用默认的OSS服务器地址创建OSSClient对象,不叫OSS_ENDPOINT代表使用杭州节点，青岛节点要加上不然包异常
        OSSClient client = new OSSClient(OSS_ENDPOINT, ACCESS_ID, ACCESS_KEY);
        //如果你想配置OSSClient的一些细节的参数，可以在构造OSSClient的时候传入ClientConfiguration对象。
        //ClientConfiguration是OSS服务的配置类，可以为客户端配置代理，最大连接数等参数。
        //具体配置看http://aliyun_portal_storage.oss.aliyuncs.com/oss_api/oss_javahtml/OSSClient.html#id2
        //ClientConfiguration conf = new ClientConfiguration();
        //OSSClient client = new OSSClient(OSS_ENDPOINT, ACCESS_ID, ACCESS_KEY, conf);
        try {
            System.out.println("正在下载...");
            downloadFile(client, BUCKET_NAME, Objectkey, downloadFilePath);
            System.out.println("下载完成...");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            deleteBucket(client, BUCKET_NAME);
        }
    }

    /**
     * 创建Bucket
     *
     * @param client     OSSClient对象
     * @param bucketName BUCKET名
     * @throws OSSException
     * @throws ClientException
     */
    public static void ensureBucket(OSSClient client, String bucketName) throws OSSException, ClientException {
        try {
            client.createBucket(bucketName);
        } catch (ServiceException e) {
            if (!OSSErrorCode.BUCKES_ALREADY_EXISTS.equals(e.getErrorCode())) {
                throw e;
            }
        }
    }

    /**
     * 删除一个Bucket和其中的Objects
     *
     * @param client     OSSClient对象
     * @param bucketName Bucket名
     * @throws OSSException
     * @throws ClientException
     */
    private static void deleteBucket(OSSClient client, String bucketName) throws OSSException, ClientException {
        ObjectListing ObjectListing = client.listObjects(bucketName);
        List<OSSObjectSummary> listDeletes = ObjectListing.getObjectSummaries();
        for (int i = 0; i < listDeletes.size(); i++) {
            String objectName = listDeletes.get(i).getKey();
            System.out.println("objectName = " + objectName);
            //如果不为空，先删除bucket下的文件
            client.deleteObject(bucketName, objectName);
        }
        client.deleteBucket(bucketName);
    }

    /**
     * 把Bucket设置成所有人可读
     *
     * @param client     OSSClient对象
     * @param bucketName Bucket名
     * @throws OSSException
     * @throws ClientException
     */
    private static void setBucketPublicReadable(OSSClient client, String bucketName) throws OSSException, ClientException {
        //创建bucket
        client.createBucket(bucketName);
        //设置bucket的访问权限， public-read-write权限
        client.setBucketAcl(bucketName, CannedAccessControlList.PublicRead);
    }

    /**
     * 上传文件
     *
     * @param client     OSSClient对象
     * @param bucketName Bucket名
     * @param Objectkey  上传到OSS起的名
     * @param filename   本地文件名
     * @throws OSSException
     * @throws ClientException
     * @throws FileNotFoundException
     */
    private static void uploadFile(OSSClient client, String bucketName, String Objectkey, String filename)
            throws OSSException, ClientException, FileNotFoundException {
        File file = new File(filename);
        ObjectMetadata objectMeta = new ObjectMetadata();
        objectMeta.setContentLength(file.length());
        //判断上传类型，多的可根据自己需求来判定
        if (filename.endsWith("xml")) {
            objectMeta.setContentType("text/xml");
        } else if (filename.endsWith("jpg")) {
            objectMeta.setContentType("image/jpeg");
        } else if (filename.endsWith("png")) {
            objectMeta.setContentType("image/png");
        } else if (filename.endsWith("doc")) {
            objectMeta.setContentType("application/msword");
        }

        InputStream input = new FileInputStream(file);
        client.putObject(bucketName, Objectkey, input, objectMeta);
    }

    /**
     * 下载文件
     *
     * @param client     OSSClient对象
     * @param bucketName Bucket名
     * @param Objectkey  上传到OSS起的名
     * @param filename   文件下载到本地保存的路径
     * @throws OSSException
     * @throws ClientException
     */
    private static void downloadFile(OSSClient client, String bucketName, String Objectkey, String filename)
            throws OSSException, ClientException {
        client.getObject(new GetObjectRequest(bucketName, Objectkey),
                new File(filename));
    }

}

