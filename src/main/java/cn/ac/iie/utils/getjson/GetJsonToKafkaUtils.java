//package cn.ac.iie.utils.getjson;
//
//import cn.ac.iie.bean.dk.DK_BEHAVIOR_LOG;
//import cn.ac.iie.bean.mm.MM_AV_IP_LOG;
//import cn.ac.iie.bean.mm.MM_VOIP_IP_LOG;
//import cn.ac.iie.bean.ntc.NTC_CONN_RECORD_LOG;
//import cn.ac.iie.bean.pxy.PXY_HTTP_LOG;
//import cn.ac.iie.common.FlowWriteConfig;
//import cn.ac.iie.common.RealtimeCountConfig;
//import cn.ac.iie.utils.ordinary.DecodeUtils;
//import cn.ac.iie.utils.ordinary.MD5Utils;
//import cn.ac.iie.utils.ordinary.TransFormUtils;
//import com.alibaba.fastjson.JSONObject;
//import com.alibaba.fastjson.parser.Feature;
//import com.zdjizhi.utils.IpLookup;
//import com.zdjizhi.utils.StringUtil;
//import org.apache.log4j.Logger;
//
///**
// * @author antlee
// * @date 2018/7/19
// */
//public class GetJsonToKafkaUtils {
//    private static Logger logger = Logger.getLogger(GetJsonToKafkaUtils.class);
//    private static IpLookup ipLookup = new IpLookup.Builder(false)
//            .loadDataFileV4(RealtimeCountConfig.IP_LIBRARY)
//            .loadDataFileV6(RealtimeCountConfig.IP_LIBRARY)
//            .build();
//
//
//    /**
//     * NTC topic对准类
//     *
//     * @param message 日志
//     * @param topic   topic名称
//     * @return 补全日志
//     */
//    public static String getNTCData(String message, String topic) {
//        switch (topic) {
//            case "NTC-HTTP-LOG":
//                return httpReplenish(message);
//            case "NTC-MAIL-LOG":
//                return mailReplenish(message);
//            case "NTC-IP-LOG":
//                return ipReplenish(message);
//            case "NTC-APP-LOG":
//                return appReplenish(message);
//            case "NTC-SSL-LOG":
//                return sslReplenish(message);
//            case "NTC-DDOS-LOG":
//                return ddosReplenish(message);
//            case "NTC-DNS-LOG":
//                return dnsReplenish(message);
//            case "NTC-COLLECT-MAIL-LOG":
//                return mailReplenish(message);
//            case "NTC-OPENVPN-LOG":
//                return openVpnLog(message);
//            case "NTC-P2P-LOG":
//                return p2pReplenish(message);
//            case "PXY-HTTP-LOG":
//                return pxyHttpReplenish(message);
//            case "NTC-BGP-LOG":
//                return bgpReplenish(message);
//            case "NTC-FTP-LOG":
//                return ftpReplenish(message);
//            case "NTC-STREAMING-MEDIA-LOG":
//                return streamMediaReplenish(message);
//            case "NTC-VOIP-LOG":
//                return voipReplenish(message);
//
////            case "NTC-COLLECT-HTTP-LOG":
////                return message;
////            case "NTC-CONN-RECORD-LOG":
////                return message;
////            case "NTC-COLLECT-RADIUS-LOG":
////                return message;
////            case "NTC-KEYWORDS-URL-LOG":
////                return message;
//
////            case "NTC-IPSEC-LOG":
////                return ipsecLog(message);
////            case "NTC-L2TP-LOG":
////                return l2tpLog(message);
////            case "NTC-SSH-LOG":
////                return sshLog(message);
////            case "NTC-PPTP-LOG":
////                return pptpReplenish(message);
//            default:
//                logger.error("There is no corresponding topic! topic name is :" + topic);
//                break;
//        }
//        return null;
//    }
//
//
//    /**
//     * HTTP Log Replenish
//     *
//     * @param message
//     * @return
//     */
//    private static String httpReplenish(String message) {
//        try {
//            NTC_HTTP_LOG ntcHttpLog = JSONObject.parseObject(message, NTC_HTTP_LOG.class);
//            String sIp = ntcHttpLog.getS_ip();
//            String dIp = ntcHttpLog.getD_ip();
//            ntcHttpLog.setServer_locate(ipLookup.countryLookup(dIp));
//            ntcHttpLog.setClient_locate(ipLookup.cityLookupDetail(sIp));
//            ntcHttpLog.setD_asn(ipLookup.asnLookup(dIp, true));
//            ntcHttpLog.setS_asn(ipLookup.asnLookup(sIp, true));
//            ntcHttpLog.setWebsite(StringUtil.getDomain(ntcHttpLog.getUrl()));
////            ntcHttpLog.setD_subscribe_id(RedisPollUtils.getJedisCluster().get(dIP));
////            ntcHttpLog.setS_subscribe_id(RedisPollUtils.getJedisCluster().get(sIp));
//            TransFormUtils.setHTTPFile(ntcHttpLog);
//            return JSONObject.toJSONString(ntcHttpLog);
//        } catch (Exception e) {
//            logger.error(("HTTP 补全日志出现错误!!! ") + e);
//            e.printStackTrace();
//            return "";
//        }
//    }
//
//
//    /**
//     * HTTP 文件方法
//     *
//     * @param message
//     * @return
//     */
//
//    public static String httpLogFile(String message) {
//        try {
//            NTC_HTTP_LOG ntcHttpLog = JSONObject.parseObject(message, NTC_HTTP_LOG.class);
//            int cfgId = ntcHttpLog.getCfg_id();
//            if (StringUtil.isBlank(ntcHttpLog.getUrl())) {
//                TransFormUtils.getUniFlow(ntcHttpLog);
//            }
//            String url = ntcHttpLog.getUrl();
//            String reqBody = ntcHttpLog.getReq_body_file();
//            if (StringUtil.isNotBlank(reqBody)) {
//                ntcHttpLog.setReq_body_key(MD5Utils.md5Encode(cfgId + url + ntcHttpLog.getReq_body_file() + ntcHttpLog.getS_ip() + ntcHttpLog.getFound_time()));
//            }
//            ntcHttpLog.setRes_body_key(TransFormUtils.getHttpKey(cfgId, url, ntcHttpLog.getRes_body_file()));
//            ntcHttpLog.setReq_hdr_key(TransFormUtils.getHttpKey(cfgId, url, ntcHttpLog.getReq_hdr_file()));
//            ntcHttpLog.setRes_hdr_key(TransFormUtils.getHttpKey(cfgId, url, ntcHttpLog.getRes_hdr_file()));
//            return JSONObject.toJSONString(ntcHttpLog);
//        } catch (Exception e) {
//            logger.error(("HTPP  文件补全出现错误!!! ") + e);
//            e.printStackTrace();
//            return "";
//        }
//    }
//
//    /**
//     * IP日志不全操作方法
//     *
//     * @param message
//     * @return
//     */
//    private static String ipReplenish(String message) {
//        try {
//            NTC_IP_LOG ntcIpLog = JSONObject.parseObject(message, NTC_IP_LOG.class);
//            String sIp = ntcIpLog.getS_ip();
//            String dIp = ntcIpLog.getD_ip();
//            ntcIpLog.setServer_locate(ipLookup.countryLookup(dIp));
//            ntcIpLog.setClient_locate(ipLookup.cityLookupDetail(sIp));
//            ntcIpLog.setD_asn(ipLookup.asnLookup(dIp, true));
//            ntcIpLog.setS_asn(ipLookup.asnLookup(sIp, true));
////            ntcIpLog.setS_subscribe_id(TransFormUtils.getSubscribe(sIp));
////            ntcIpLog.setD_subscribe_id(TransFormUtils.getSubscribe(dIP));
//            return JSONObject.toJSONString(ntcIpLog);
//        } catch (Exception e) {
//            logger.error(("IP 日志补全操作出现异常!!! ") + e);
//            e.printStackTrace();
//            return "";
//        }
//    }
//
//    /**
//     * MAIL文件传输方法
//     *
//     * @param message
//     * @return
//     */
//    public static String emailFile(String message) {
//        try {
//            NTC_MAIL_LOG ntcMailLog = JSONObject.parseObject(message, NTC_MAIL_LOG.class);
//            ntcMailLog.setEml_key(TransFormUtils.getEmlKey(ntcMailLog.getFound_time(), ntcMailLog.getMail_from(),
//                    ntcMailLog.getMail_to(), ntcMailLog.getSubject(), ntcMailLog.getEml_file()));
//            if (StringUtil.isNotBlank(ntcMailLog.getSubject())) {
//                String subjectCharset = JSONObject.parseObject(message).getString("subject_charset");
//                ntcMailLog.setSubject(DecodeUtils.base64Str(ntcMailLog.getSubject(), subjectCharset));
//            }
//            return JSONObject.toJSONString(ntcMailLog);
//        } catch (Exception e) {
//            logger.error(("MAIL 文件传输出现错误!!! ") + e);
//            e.printStackTrace();
//            return "";
//        }
//    }
//
//    /**
//     * MAIL 日志详细信息补全方法
//     *
//     * @param message
//     * @return
//     */
//    private static String mailReplenish(String message) {
//        try {
//            NTC_MAIL_LOG ntcMailLog = JSONObject.parseObject(message, NTC_MAIL_LOG.class);
//            String sIp = ntcMailLog.getS_ip();
//            String dIp = ntcMailLog.getD_ip();
//            ntcMailLog.setServer_locate(ipLookup.countryLookup(dIp));
//            ntcMailLog.setClient_locate(ipLookup.cityLookupDetail(sIp));
//            ntcMailLog.setD_asn(ipLookup.asnLookup(dIp, true));
//            ntcMailLog.setS_asn(ipLookup.asnLookup(sIp, true));
////            ntcMailLog.setD_subscribe_id(TransFormUtils.getSubscribe(dIP));
////            ntcMailLog.setS_subscribe_id(TransFormUtils.getSubscribe(sIp));
//            TransFormUtils.setMailFile(ntcMailLog);
//            return JSONObject.toJSONString(ntcMailLog);
//        } catch (Exception e) {
//            logger.error(("MAIL 日志补全出现错误!!! ") + e);
//            e.printStackTrace();
//            return "";
//        }
//    }
//
//    /**
//     * APP Log Replenish.
//     *
//     * @param message
//     * @return
//     */
//    private static String appReplenish(String message) {
//        try {
//            NTC_APP_LOG ntcAppLog = JSONObject.parseObject(message, NTC_APP_LOG.class);
//            String sIp = ntcAppLog.getS_ip();
//            String dIp = ntcAppLog.getD_ip();
//            ntcAppLog.setServer_locate(ipLookup.countryLookup(dIp));
//            ntcAppLog.setClient_locate(ipLookup.cityLookupDetail(sIp));
//            ntcAppLog.setD_asn(ipLookup.asnLookup(dIp, true));
//            ntcAppLog.setS_asn(ipLookup.asnLookup(sIp, true));
////            ntcAppLog.setD_subscribe_id(RedisPollUtils.getJedisCluster().get(dIP));
////            ntcAppLog.setS_subscribe_id(RedisPollUtils.getJedisCluster().get(sIp));
//            return JSONObject.toJSONString(ntcAppLog);
//        } catch (Exception e) {
//            logger.error(("APP 日志补全出现错误!!! ") + e);
//            return "";
//        }
//    }
//
//
//    /**
//     * DDOS Log Replenish
//     *
//     * @param message
//     * @return
//     */
//    private static String ddosReplenish(String message) {
//        try {
//            NTC_DDOS_LOG ntcDdosLog = JSONObject.parseObject(message, NTC_DDOS_LOG.class);
//            String sIp = ntcDdosLog.getS_ip();
//            String dIp = ntcDdosLog.getD_ip();
//            ntcDdosLog.setServer_locate(ipLookup.countryLookup(dIp));
//            ntcDdosLog.setClient_locate(ipLookup.cityLookupDetail(sIp));
//            ntcDdosLog.setD_asn(ipLookup.asnLookup(dIp, true));
//            ntcDdosLog.setS_asn(ipLookup.asnLookup(sIp, true));
////            ntcDdosLog.setD_subscribe_id(RedisPollUtils.getJedisCluster().get(dIP));
////            ntcDdosLog.setS_subscribe_id(RedisPollUtils.getJedisCluster().get(sIp));
//            return JSONObject.toJSONString(ntcDdosLog);
//        } catch (Exception e) {
//            logger.error(("DDOS 日志补全出现错误!!! ") + e);
//            e.printStackTrace();
//            return "";
//        }
//    }
//
//    /**
//     * SSL Log Replenish
//     *
//     * @param message
//     * @return
//     */
//    private static String sslReplenish(String message) {
//        try {
//            NTC_SSL_LOG ntcSslLog = JSONObject.parseObject(message, NTC_SSL_LOG.class);
//            String sIp = ntcSslLog.getS_ip();
//            String dIp = ntcSslLog.getD_ip();
//            ntcSslLog.setServer_locate(ipLookup.countryLookup(dIp));
//            ntcSslLog.setClient_locate(ipLookup.cityLookupDetail(sIp));
//            ntcSslLog.setD_asn(ipLookup.asnLookup(dIp, true));
//            ntcSslLog.setS_asn(ipLookup.asnLookup(sIp, true));
////            ntcSslLog.setS_subscribe_id(TransFormUtils.getSubscribe(sIp));
////            ntcSslLog.setD_subscribe_id(TransFormUtils.getSubscribe(dIP));
//            return JSONObject.toJSONString(ntcSslLog);
//        } catch (Exception e) {
//            logger.error("SSL 日志补全出现错误!!!" + e);
//            e.printStackTrace();
//            return "";
//        }
//    }
//
//    /**
//     * DNS Log Replenish
//     *
//     * @param message
//     * @return
//     */
//    private static String dnsReplenish(String message) {
//        try {
//            NTC_DNS_LOG ntcDnsLog = JSONObject.parseObject(message, NTC_DNS_LOG.class);
//            String sIp = ntcDnsLog.getS_ip();
//            String dIp = ntcDnsLog.getD_ip();
//            ntcDnsLog.setServer_locate(ipLookup.countryLookup(dIp));
//            ntcDnsLog.setClient_locate(ipLookup.cityLookupDetail(sIp));
//            ntcDnsLog.setD_asn(ipLookup.asnLookup(dIp, true));
//            ntcDnsLog.setS_asn(ipLookup.asnLookup(sIp, true));
////            ntcDnsLog.setD_subscribe_id(RedisPollUtils.getJedisCluster().get(dIP));
////            ntcDnsLog.setS_subscribe_id(RedisPollUtils.getJedisCluster().get(sIp));
//            return JSONObject.toJSONString(ntcDnsLog);
//        } catch (Exception e) {
//            logger.error(("DNS 日志补全出现错误!!! ") + e);
//            e.printStackTrace();
//            return "";
//        }
//    }
//
//    /**
//     * P2P Log Replenish
//     *
//     * @param message
//     * @return
//     */
//    private static String p2pReplenish(String message) {
//        try {
//            NTC_P2P_LOG ntcP2PLog = JSONObject.parseObject(message, NTC_P2P_LOG.class);
//            String sIp = ntcP2PLog.getS_ip();
//            String dIp = ntcP2PLog.getD_ip();
//            ntcP2PLog.setServer_locate(ipLookup.countryLookup(dIp));
//            ntcP2PLog.setClient_locate(ipLookup.cityLookupDetail(sIp));
//            ntcP2PLog.setD_asn(ipLookup.asnLookup(dIp, true));
//            ntcP2PLog.setS_asn(ipLookup.asnLookup(sIp, true));
////            ntcP2PLog.setS_subscribe_id(TransFormUtils.getSubscribe(sIp));
////            ntcP2PLog.setD_subscribe_id(TransFormUtils.getSubscribe(dIP));
//            return JSONObject.toJSONString(ntcP2PLog);
//        } catch (Exception e) {
//            logger.error(("P2P 日志补全出现错误!!! ") + e);
//            e.printStackTrace();
//            return "";
//        }
//    }
//
//    /**
//     * pxy 文件方法
//     *
//     * @param message
//     * @return
//     */
//
//    public static String pxyLogFile(String message) {
//        try {
//            PXY_HTTP_LOG pxyHttpLog = JSONObject.parseObject(message, PXY_HTTP_LOG.class);
//            int cfgId = pxyHttpLog.getCfg_id();
//            String url = pxyHttpLog.getUrl();
//            pxyHttpLog.setReq_body_key(MD5Utils.md5Encode(cfgId + url + pxyHttpLog.getReq_body() + pxyHttpLog.getS_ip() + pxyHttpLog.getFound_time()));
//            pxyHttpLog.setResp_body_key(TransFormUtils.getHttpKey(cfgId, url, pxyHttpLog.getResp_body()));
//            pxyHttpLog.setWebsite(StringUtil.getDomain(pxyHttpLog.getUrl()));
//            return JSONObject.toJSONString(pxyHttpLog);
//        } catch (Exception e) {
//            logger.error(("PXY 文件补全出现错误!!! ") + e);
//            e.printStackTrace();
//            return "";
//        }
//    }
//
//
//    /**
//     * PXYHTTP Log Replenish
//     *
//     * @param message
//     * @return
//     */
//    private static String pxyHttpReplenish(String message) {
//        try {
//            PXY_HTTP_LOG pxyHttpLog = JSONObject.parseObject(message, PXY_HTTP_LOG.class);
//            String sIp = pxyHttpLog.getS_ip();
//            String dIp = pxyHttpLog.getD_ip();
//            pxyHttpLog.setServer_locate(ipLookup.countryLookup(dIp));
//            pxyHttpLog.setClient_locate(ipLookup.cityLookupDetail(sIp));
//            pxyHttpLog.setD_asn(ipLookup.asnLookup(dIp, true));
//            pxyHttpLog.setS_asn(ipLookup.asnLookup(sIp, true));
//            if (StringUtil.isNotBlank(pxyHttpLog.getReq_body())) {
//                pxyHttpLog.setReq_body("http://" + FlowWriteConfig.ASTANA_OSS_ADDRS + "/download/" + pxyHttpLog.getReq_body_key() + "." + "html");
//            }
//            if (StringUtil.isNotBlank(pxyHttpLog.getResp_body())) {
//                pxyHttpLog.setResp_body("http://" + FlowWriteConfig.ASTANA_OSS_ADDRS + "/download/" + pxyHttpLog.getResp_body_key() + "." + "html");
//            }
//            return JSONObject.toJSONString(pxyHttpLog);
//        } catch (Exception e) {
//            logger.error("PXY 日志补全出现错误!!!" + e);
//            e.printStackTrace();
//            return "";
//        }
//    }
//
//    /**
//     * BGP Log Replenish
//     *
//     * @param message
//     * @return
//     */
//    private static String bgpReplenish(String message) {
//        try {
//            NTC_BGP_LOG ntcBgpLog = JSONObject.parseObject(message, NTC_BGP_LOG.class);
//            String sIp = ntcBgpLog.getS_ip();
//            String dIp = ntcBgpLog.getD_ip();
//            ntcBgpLog.setServer_locate(ipLookup.countryLookup(dIp));
//            ntcBgpLog.setClient_locate(ipLookup.cityLookupDetail(sIp));
//            ntcBgpLog.setD_asn(ipLookup.asnLookup(dIp, true));
//            ntcBgpLog.setS_asn(ipLookup.asnLookup(sIp, true));
////            ntcBgpLog.setD_subscribe_id(RedisPollUtils.getJedisCluster().get(dIP));
////            ntcBgpLog.setS_subscribe_id(RedisPollUtils.getJedisCluster().get(sIp));
//            return JSONObject.toJSONString(ntcBgpLog);
//        } catch (Exception e) {
//            logger.error(("BGP 日志补全出现错误!!! ") + e);
//            e.printStackTrace();
//            return "";
//        }
//    }
//
//    /**
//     * FTP Log Replenish
//     *
//     * @param message
//     * @return
//     */
//    private static String ftpReplenish(String message) {
//        try {
//            NTC_FTP_LOG ntcFtpLog = JSONObject.parseObject(message, NTC_FTP_LOG.class);
//            String sIp = ntcFtpLog.getS_ip();
//            String dIp = ntcFtpLog.getD_ip();
//            ntcFtpLog.setServer_locate(ipLookup.countryLookup(dIp));
//            ntcFtpLog.setClient_locate(ipLookup.cityLookupDetail(sIp));
//            ntcFtpLog.setD_asn(ipLookup.asnLookup(dIp, true));
//            ntcFtpLog.setS_asn(ipLookup.asnLookup(sIp, true));
////            ntcFtpLog.setD_subscribe_id(RedisPollUtils.getJedisCluster().get(dIP));
////            ntcFtpLog.setS_subscribe_id(RedisPollUtils.getJedisCluster().get(sIp));
//            return JSONObject.toJSONString(ntcFtpLog);
//        } catch (Exception e) {
//            logger.error(("FTP 日志补全出现错误!!! ") + e);
//            e.printStackTrace();
//            return "";
//        }
//    }
//
//    /**
//     * Stream Media Log Replenish
//     *
//     * @param message
//     * @return
//     */
//    private static String streamMediaReplenish(String message) {
//        try {
//            NTC_STREAMING_MEDIA_LOG ntcStreamingMediaLog = JSONObject.parseObject(message, NTC_STREAMING_MEDIA_LOG.class);
//            String sIp = ntcStreamingMediaLog.getS_ip();
//            String dIp = ntcStreamingMediaLog.getD_ip();
//            ntcStreamingMediaLog.setServer_locate(ipLookup.countryLookup(dIp));
//            ntcStreamingMediaLog.setClient_locate(ipLookup.cityLookupDetail(sIp));
//            ntcStreamingMediaLog.setD_asn(ipLookup.asnLookup(dIp, true));
//            ntcStreamingMediaLog.setS_asn(ipLookup.asnLookup(sIp, true));
////            ntcStreamingMediaLog.setS_subscribe_id(TransFormUtils.getSubscribe(sIp));
////            ntcStreamingMediaLog.setD_subscribe_id(TransFormUtils.getSubscribe(dIP));
//            return JSONObject.toJSONString(ntcStreamingMediaLog);
//        } catch (Exception e) {
//            logger.error(("Stream 日志补全出现错误!!! ") + e);
//            e.printStackTrace();
//            return "";
//        }
//    }
//
//    /**
//     * Voip Log Replenish
//     *
//     * @param message
//     * @return
//     */
//    private static String voipReplenish(String message) {
//        try {
//            NTC_VOIP_LOG ntcVoipLog = JSONObject.parseObject(message, NTC_VOIP_LOG.class);
//            String sIp = ntcVoipLog.getS_ip();
//            String dIp = ntcVoipLog.getD_ip();
//            ntcVoipLog.setServer_locate(ipLookup.countryLookup(dIp));
//            ntcVoipLog.setClient_locate(ipLookup.cityLookupDetail(sIp));
//            ntcVoipLog.setD_asn(ipLookup.asnLookup(dIp, true));
//            ntcVoipLog.setS_asn(ipLookup.asnLookup(sIp, true));
////            ntcVoipLog.setS_subscribe_id(TransFormUtils.getSubscribe(sIp));
////            ntcVoipLog.setD_subscribe_id(TransFormUtils.getSubscribe(dIP));
//            return JSONObject.toJSONString(ntcVoipLog);
//        } catch (Exception e) {
//            logger.error(("VOIP 日志补全出现错误!!! ") + e);
//            e.printStackTrace();
//            return "";
//        }
//    }
//
//    private static String openVpnLog(String message) {
//        try {
//            NTC_OPENVPN_LOG ntcOpenvpnLog = JSONObject.parseObject(message, NTC_OPENVPN_LOG.class);
//            String sIp = ntcOpenvpnLog.getS_ip();
//            String dIp = ntcOpenvpnLog.getD_ip();
//            ntcOpenvpnLog.setServer_locate(ipLookup.countryLookup(dIp));
//            ntcOpenvpnLog.setClient_locate(ipLookup.cityLookupDetail(sIp));
//            ntcOpenvpnLog.setD_asn(ipLookup.asnLookup(dIp, true));
//            ntcOpenvpnLog.setS_asn(ipLookup.asnLookup(sIp, true));
////            ntcOpenvpnLog.setS_subscribe_id(TransFormUtils.getSubscribe(ntcOpenvpnLog.getS_ip()));
////            ntcOpenvpnLog.setD_subscribe_id(TransFormUtils.getSubscribe(ntcOpenvpnLog.getD_ip()));
//            return JSONObject.toJSONString(ntcOpenvpnLog);
//        } catch (Exception e) {
//            logger.error(("Log parsing error!!! ") + e);
//            e.printStackTrace();
//            return "";
//        }
//    }
//
//
//    /**
//     * 音视频topic对准类
//     *
//     * @param message 日志
//     * @param topic   topic名
//     * @return 补全后日志
//     */
//    public static String getMMData(String message, String topic) {
//        switch (topic) {
//            case "NTC-COLLECT-VOIP-LOG":
//                return collectVoipLog(message);
//            case "MM-PORN-AUDIO-LEVEL-LOG":
//                return avIpLog(message);
//            case "MM-PORN-VIDEO-LEVEL-LOG":
//                return avIpLog(message);
//            case "MM-SAMPLE-AUDIO-LOG":
//                return avIpLog(message);
//            case "MM-SAMPLE-VIDEO-LOG":
//                return avIpLog(message);
//            case "MM-SAMPLE-PIC-LOG":
//                return avIpLog(message);
//            case "MM-SAMPLE-VOIP-LOG":
//                return voipIpLog(message);
//            case "MM-FILE-DIGEST-LOG":
//                return avIpLog(message);
//            case "MM-AV-IP-LOG":
//                return avIpLog(message);
//            case "MM-SPEAKER-RECOGNIZATION-LOG":
//                return avIpLog(message);
//            case "MM-LOGO-DETECTION-LOG":
//                return avIpLog(message);
//            case "MM-FACE-RECOGNIZATION-LOG":
//                return avIpLog(message);
//            case "MM-AV-URL-LOG":
//                return avIpLog(message);
//            case "MM-PIC-IP-LOG":
//                return avIpLog(message);
//            case "MM-PIC-URL-LOG":
//                return avIpLog(message);
//            case "MM-VOIP-IP-LOG":
//                return voipIpLog(message);
//            case "MM-VOIP-ACCOUNT-LOG":
//                return voipIpLog(message);
//            default:
//                logger.error("There is no corresponding topic! topic name is :" + topic);
//                break;
//        }
//        return null;
//    }
//
//
//    private static String avIpLog(String message) {
//        try {
//            MM_AV_IP_LOG mmAvIpLog = JSONObject.parseObject(message, MM_AV_IP_LOG.class);
//            String sIp = mmAvIpLog.getS_ip();
//            String dIp = mmAvIpLog.getD_ip();
//            mmAvIpLog.setServer_locate(ipLookup.countryLookup(dIp));
//            mmAvIpLog.setClient_locate(ipLookup.cityLookupDetail(sIp));
//            mmAvIpLog.setD_asn(ipLookup.asnLookup(dIp, true));
//            mmAvIpLog.setS_asn(ipLookup.asnLookup(sIp, true));
////            mmAvIpLog.setS_subscribe_id(TransFormUtils.getSubscribe(sIp));
////            mmAvIpLog.setD_subscribe_id(TransFormUtils.getSubscribe(dIP));
//            String url = mmAvIpLog.getLog_uri();
//            if (StringUtil.isNotBlank(url)) {
//                String key = MD5Utils.md5Encode(url);
//                String end = StringUtil.getFileExtendName(url).toLowerCase();
//                mmAvIpLog.setLog_uri("http://" + FlowWriteConfig.ALMATY_OSS_ADDRS + "/download/" + key + "." + end);
//            }
//            return JSONObject.toJSONString(mmAvIpLog);
//        } catch (Exception e) {
//            logger.error(("Log parsing error!!! ") + e);
//            e.printStackTrace();
//            return "";
//        }
//    }
//
//    private static String voipIpLog(String message) {
//        try {
//            MM_VOIP_IP_LOG mmVoipIpLog = JSONObject.parseObject(message, MM_VOIP_IP_LOG.class);
//            String sIp = mmVoipIpLog.getS_ip();
//            String dIp = mmVoipIpLog.getD_ip();
//            mmVoipIpLog.setServer_locate(ipLookup.countryLookup(dIp));
//            mmVoipIpLog.setClient_locate(ipLookup.cityLookupDetail(sIp));
//            mmVoipIpLog.setD_asn(ipLookup.asnLookup(dIp, true));
//            mmVoipIpLog.setS_asn(ipLookup.asnLookup(sIp, true));
////            mmVoipIpLog.setS_subscribe_id(TransFormUtils.getSubscribe(sIp));
////            mmVoipIpLog.setD_subscribe_id(TransFormUtils.getSubscribe(dIP));
//            String url = mmVoipIpLog.getLog_uri();
//            if (StringUtil.isNotBlank(url)) {
//                String key = MD5Utils.md5Encode(url);
//                String end = StringUtil.getFileExtendName(url).toLowerCase();
//                mmVoipIpLog.setLog_uri("http://" + FlowWriteConfig.ALMATY_OSS_ADDRS + "/download/" + key + "." + end);
//            }
//            return JSONObject.toJSONString(mmVoipIpLog);
//        } catch (Exception e) {
//            logger.error(("Log parsing error!!! ") + e);
//            e.printStackTrace();
//            return "";
//        }
//    }
//
//
//    private static String collectVoipLog(String message) {
//        try {
//            NTC_COLLECT_VOIP_LOG ntcCollectVoipLog = JSONObject.parseObject(message, NTC_COLLECT_VOIP_LOG.class);
//            if (StringUtil.isNotBlank(ntcCollectVoipLog.getTo_from_store_url())) {
//                String key = MD5Utils.md5Encode(ntcCollectVoipLog.getPid() + ntcCollectVoipLog.getTo_from_store_url() + ntcCollectVoipLog.getFound_time());
//                String url = ntcCollectVoipLog.getTo_from_store_url();
//                String end = StringUtil.getFileExtendName(url).toLowerCase();
//                ntcCollectVoipLog.setTo_from_store_url("http://" + FlowWriteConfig.ALMATY_OSS_ADDRS + "/download/" + key + "." + end);
//            }
//            if (StringUtil.isNotBlank(ntcCollectVoipLog.getFrom_to_store_url())) {
//                String key = MD5Utils.md5Encode(ntcCollectVoipLog.getPid() + ntcCollectVoipLog.getFrom_to_store_url() + ntcCollectVoipLog.getFound_time());
//                String url = ntcCollectVoipLog.getFrom_to_store_url();
//                String end = StringUtil.getFileExtendName(url).toLowerCase();
//                ntcCollectVoipLog.setFrom_to_store_url("http://" + FlowWriteConfig.ALMATY_OSS_ADDRS + "/download/" + key + "." + end);
//            }
//            return JSONObject.toJSONString(ntcCollectVoipLog);
//        } catch (Exception e) {
//            logger.error(("Log parsing error!!! ") + e);
//            e.printStackTrace();
//            return "";
//        }
//    }
//
//
//    /**
//     * 其他topic对准类
//     *
//     * @param message 日志
//     * @return 补全后日志
//     */
//    public static String getOtherData(String message, String topic) {
//        switch (topic) {
//            case "NTC-HTTP-LOG":
//                return otherHttpLog(message);
//            case "NTC-CONN-RECORD-LOG":
//                NTC_CONN_RECORD_LOG ntcConnRecordLog = JSONObject.parseObject(message, NTC_CONN_RECORD_LOG.class, Feature.OrderedField);
//                ntcConnRecordLog.setD_asn(ipLookup.asnLookup(ntcConnRecordLog.getD_ip(), true));
//                ntcConnRecordLog.setS_asn(ipLookup.asnLookup(ntcConnRecordLog.getS_ip(), true));
//                return JSONObject.toJSONString(ntcConnRecordLog);
//            default:
//                logger.error("There is no corresponding topic! topic name is :" + topic);
//                break;
//        }
//        return null;
//    }
//
//
//    private static String otherHttpLog(String message) {
//        NTC_HTTP_LOG ntcHttpLog = JSONObject.parseObject(message, NTC_HTTP_LOG.class);
//        if (ntcHttpLog.getService() == 152) {
//            if (StringUtil.isBlank(ntcHttpLog.getUrl())) {
//                TransFormUtils.getUniFlow(ntcHttpLog);
//            }
//            String sIp = ntcHttpLog.getS_ip();
//            String dIp = ntcHttpLog.getD_ip();
//            ntcHttpLog.setServer_locate(ipLookup.countryLookup(dIp));
//            ntcHttpLog.setClient_locate(ipLookup.cityLookupDetail(sIp));
//            ntcHttpLog.setD_asn(ipLookup.asnLookup(dIp, true));
//            ntcHttpLog.setS_asn(ipLookup.asnLookup(sIp, true));
//            ntcHttpLog.setWebsite(StringUtil.getDomain(ntcHttpLog.getUrl()));
////            ntcHttpLog.setD_subscribe_id(RedisPollUtils.getJedisCluster().get(dIP));
////            ntcHttpLog.setS_subscribe_id(RedisPollUtils.getJedisCluster().get(sIp));
//            return JSONObject.toJSONString(ntcHttpLog);
//        }
//        return "";
//    }
//
//
//    /**
//     * collect Mail to Mail
//     *
//     * @param message
//     * @return
//     */
//    public static String collectMailToMailLog(String message) {
//        try {
//            NTC_MAIL_LOG ntcMailLog = JSONObject.parseObject(message, NTC_MAIL_LOG.class);
//            String sIp = ntcMailLog.getS_ip();
//            String dIp = ntcMailLog.getD_ip();
//            ntcMailLog.setServer_locate(ipLookup.countryLookup(dIp));
//            ntcMailLog.setClient_locate(ipLookup.cityLookupDetail(sIp));
//            ntcMailLog.setD_asn(ipLookup.asnLookup(dIp, true));
//            ntcMailLog.setS_asn(ipLookup.asnLookup(sIp, true));
////            ntcMailLog.setS_subscribe_id(TransFormUtils.getSubscribe(sIp));
////            ntcMailLog.setD_subscribe_id(TransFormUtils.getSubscribe(dIP));
//            ntcMailLog.setEml_key(TransFormUtils.getEmlKey(ntcMailLog.getFound_time(), ntcMailLog.getMail_from(),
//                    ntcMailLog.getMail_to(), ntcMailLog.getSubject(), ntcMailLog.getEml_file()));
//            if (StringUtil.isNotBlank(ntcMailLog.getSubject())) {
//                String subjectCharset = JSONObject.parseObject(message).getString("subject_charset");
//                ntcMailLog.setSubject(DecodeUtils.base64Str(ntcMailLog.getSubject(), subjectCharset));
//            }
//            TransFormUtils.setMailFile(ntcMailLog);
//            return JSONObject.toJSONString(ntcMailLog);
//        } catch (Exception e) {
//            logger.error(("Log parsing error!!! ") + e);
//            e.printStackTrace();
//            return "";
//        }
//    }
//
//
//    /**
//     * ---------------------------------删除的topic------------------------------------------------------------
//     **/
//
//    private static String behaviorLog(String message) {
//        try {
//            DK_BEHAVIOR_LOG dkBehaviorLog = JSONObject.parseObject(message, DK_BEHAVIOR_LOG.class, Feature.OrderedField);
//            dkBehaviorLog.setServer_locate(ipLookup.countryLookup(dkBehaviorLog.getD_ip()));
//            dkBehaviorLog.setClient_locate(ipLookup.cityLookupDetail(dkBehaviorLog.getS_ip()));
//            dkBehaviorLog.setD_asn(ipLookup.asnLookup(dkBehaviorLog.getD_ip()).trim());
//            dkBehaviorLog.setS_asn(ipLookup.asnLookup(dkBehaviorLog.getS_ip()).trim());
////            dkBehaviorLog.setS_subscribe_id(TransFormUtils.getSubscribe(dkBehaviorLog.getS_ip()));
////            dkBehaviorLog.setD_subscribe_id(TransFormUtils.getSubscribe(dkBehaviorLog.getD_ip()));
//            return JSONObject.toJSONString(dkBehaviorLog);
//        } catch (Exception e) {
//            logger.error(("Log parsing error!!! ") + e);
//            e.printStackTrace();
//            return "";
//        }
//    }
//
//    private static String ipsecLog(String message) {
//        try {
//            NTC_IPSEC_LOG ntcIpsecLog = JSONObject.parseObject(message, NTC_IPSEC_LOG.class, Feature.OrderedField);
//            ntcIpsecLog.setServer_locate(ipLookup.countryLookup(ntcIpsecLog.getD_ip()));
//            ntcIpsecLog.setClient_locate(ipLookup.cityLookupDetail(ntcIpsecLog.getS_ip()));
//            ntcIpsecLog.setD_asn(ipLookup.asnLookup(ntcIpsecLog.getD_ip()).trim());
//            ntcIpsecLog.setS_asn(ipLookup.asnLookup(ntcIpsecLog.getS_ip()).trim());
////            ntcIpsecLog.setS_subscribe_id(TransFormUtils.getSubscribe(ntcIpsecLog.getS_ip()));
////            ntcIpsecLog.setD_subscribe_id(TransFormUtils.getSubscribe(ntcIpsecLog.getD_ip()));
//            return JSONObject.toJSONString(ntcIpsecLog);
//        } catch (Exception e) {
//            logger.error(("Log parsing error!!! ") + e);
//            e.printStackTrace();
//            return "";
//        }
//    }
//
//    private static String l2tpLog(String message) {
//        try {
//            NTC_L2TP_LOG ntcL2TPLog = JSONObject.parseObject(message, NTC_L2TP_LOG.class, Feature.OrderedField);
//            ntcL2TPLog.setServer_locate(ipLookup.countryLookup(ntcL2TPLog.getD_ip()));
//            ntcL2TPLog.setClient_locate(ipLookup.cityLookupDetail(ntcL2TPLog.getS_ip()));
//            ntcL2TPLog.setD_asn(ipLookup.asnLookup(ntcL2TPLog.getD_ip()).trim());
//            ntcL2TPLog.setS_asn(ipLookup.asnLookup(ntcL2TPLog.getS_ip()).trim());
////            ntcL2TPLog.setS_subscribe_id(TransFormUtils.getSubscribe(ntcL2TPLog.getS_ip()));
////            ntcL2TPLog.setD_subscribe_id(TransFormUtils.getSubscribe(ntcL2TPLog.getD_ip()));
//            return JSONObject.toJSONString(ntcL2TPLog);
//        } catch (Exception e) {
//            logger.error(("Log parsing error!!! ") + e);
//            e.printStackTrace();
//            return "";
//        }
//    }
//
//
//    private static String sshLog(String message) {
//        try {
//            NTC_SSH_LOG ntcSshLog = JSONObject.parseObject(message, NTC_SSH_LOG.class, Feature.OrderedField);
//            ntcSshLog.setServer_locate(ipLookup.countryLookup(ntcSshLog.getD_ip()));
//            ntcSshLog.setClient_locate(ipLookup.cityLookupDetail(ntcSshLog.getS_ip()));
//            ntcSshLog.setD_asn(ipLookup.asnLookup(ntcSshLog.getD_ip()).trim());
//            ntcSshLog.setS_asn(ipLookup.asnLookup(ntcSshLog.getS_ip()).trim());
//            try {
//                ntcSshLog.setVersion(ntcSshLog.getVersion().replaceAll("\n", "\\\\n"));
//                ntcSshLog.setVersion(ntcSshLog.getVersion().replaceAll("\r", "\\\\r"));
//            } catch (Exception e) {
//                ntcSshLog.setVersion("");
//            }
////            ntcSshLog.setS_subscribe_id(TransFormUtils.getSubscribe(ntcSshLog.getS_ip()));
////            ntcSshLog.setD_subscribe_id(TransFormUtils.getSubscribe(ntcSshLog.getD_ip()));
//            return JSONObject.toJSONString(ntcSshLog);
//        } catch (Exception e) {
//            logger.error("Log parsing error!!! " + e);
//            e.printStackTrace();
//            return "";
//        }
//    }
//
//
//    /**
//     * PPTP Log Replenish
//     *
//     * @param message
//     * @return
//     */
//    private static String pptpReplenish(String message) {
//        try {
//            NTC_PPTP_LOG ntcPptpLog = JSONObject.parseObject(message, NTC_PPTP_LOG.class);
//            ntcPptpLog.setServer_locate(ipLookup.countryLookup(ntcPptpLog.getD_ip()));
//            ntcPptpLog.setClient_locate(ipLookup.cityLookupDetail(ntcPptpLog.getS_ip()));
//            ntcPptpLog.setD_asn(ipLookup.asnLookup(ntcPptpLog.getD_ip()).trim());
//            ntcPptpLog.setS_asn(ipLookup.asnLookup(ntcPptpLog.getS_ip()).trim());
////            ntcPptpLog.setS_subscribe_id(TransFormUtils.getSubscribe(ntcPptpLog.getS_ip()));
////            ntcPptpLog.setD_subscribe_id(TransFormUtils.getSubscribe(ntcPptpLog.getD_ip()));
//            return JSONObject.toJSONString(ntcPptpLog);
//        } catch (Exception e) {
//            logger.error(("Log parsing error!!! ") + e);
//            e.printStackTrace();
//            return "";
//        }
//    }
//
//
//    /**---------------------------------删除的topic------------------------------------------------------------**/
//
//}
