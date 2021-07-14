package com.example.garbagesorting.BaiduAdvancedGeneral;

import net.sf.json.JSONArray;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * 通用物体和场景识别
 */
public class AdvancedGeneral{

    /**
     * 重要提示代码中所需工具类
     * FileUtil,Base64Util,HttpUtil,GsonUtils请从
     * https://ai.baidu.com/file/658A35ABAB2D404FBF903F64D47C1F72
     * https://ai.baidu.com/file/C8D81F3301E24D2892968F09AE1AD6E2
     * https://ai.baidu.com/file/544D677F5D4E4F17B4122FBD60DB82B3
     * https://ai.baidu.com/file/470B3ACCA3FE43788B5A963BF0B625F3
     * 下载
     */
    public static String advancedGeneral(String filePath) {
        int index = 0;
        int rIndex = 0;
        int lastLeft = 0;
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/image-classify/v2/advanced_general";
        try {
            // 本地文件路径
            //String filePath = "E:\\Microsoft Edge Download\\菜品1.jpg";//文件路径
            byte[] imgData = FileUtil.readFileByBytes(filePath);
            String imgStr = Base64Util.encode(imgData);
            String imgParam = URLEncoder.encode(imgStr, "UTF-8");

            String param = "image=" + imgParam;

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = AuthService.getAuth();





            String result = HttpUtil.post(url, accessToken, param);
            System.out.println(result);

            //保留想要的结果
            char[] chars = result.toCharArray();
            for(int i = 0;i<result.length();i++){
                if(chars[i] == '['){
                    index = i;
                }
                if(chars[i] == ']'){
                    rIndex = i+1;
                }
            }
            System.out.println(result.substring(index,rIndex));

            //String转json，遍历json结果集
            JSONArray jsonArray = JSONArray.fromObject(result.substring(index,rIndex));
            for(int i = 0 ;i < jsonArray.size() ; i++){
                System.out.println("score = "+jsonArray.getJSONObject(i).getString("score"));
                System.out.println("root = "+jsonArray.getJSONObject(i).getString("root"));
                System.out.println("keyword = "+jsonArray.getJSONObject(i).getString("keyword"));
                return jsonArray.getJSONObject(i).getString("keyword");
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
