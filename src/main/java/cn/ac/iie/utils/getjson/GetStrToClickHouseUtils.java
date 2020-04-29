//package cn.ac.iie.utils.getjson;
//
//import cn.ac.iie.bean.dk.DK_BEHAVIOR_LOG;
//import cn.ac.iie.bean.mm.MM_AV_IP_LOG;
//import cn.ac.iie.bean.mm.MM_VOIP_IP_LOG;
//import cn.ac.iie.bean.ntc.NTC_CONN_RECORD_LOG;
//import cn.ac.iie.bean.ntcwidely.NTC_COLLECT_SSL_LOG;
//import cn.ac.iie.bean.pxy.PXY_HTTP_LOG;
//import cn.ac.iie.common.FlowWriteConfig;
//import cn.ac.iie.common.RealtimeCountConfig;
//import cn.ac.iie.utils.ordinary.DecodeUtils;
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
//public class GetStrToClickHouseUtils {
//    private static Logger logger = Logger.getLogger(GetStrToClickHouseUtils.class);
//    private static IpLookup ipLookup = new IpLookup.Builder(false)
//            .loadDataFileV4(RealtimeCountConfig.IP_LIBRARY)
//            .loadDataFileV6(RealtimeCountConfig.IP_LIBRARY)
//            .build();
//
//    /**
//     * NTC topic对准类
//     *
//     * @param message 日志
//     * @param topic   topic名称
//     * @return 补全日志
//     */
//    public static String getData(String message, String topic) {
//        switch (topic) {
//            case "NTC-CONN-RECORD-LOG":
//                return ntcConnLog(message);
//            case "NTC-COLLECT-MAIL-LOG":
//                return mailLog(message);
//            case "NTC-COLLECT-SSL-LOG":
//                return sslLog(message);
//            case "NTC-SSL-LOG":
//                return sslLog(message);
//            case "NTC-APP-LOG":
//                return appLog(message);
//            case "NTC-HTTP-LOG":
//                return httpLog(message);
//            case "NTC-IP-LOG":
//                return ipLog(message);
//            case "PXY-HTTP-LOG":
//                return pHttpLog(message);
//            case "NTC-DNS-LOG":
//                return dnsLog(message);
//            case "NTC-BGP-LOG":
//                return bgpLog(message);
//            case "NTC-DDOS-LOG":
//                return ddosLog(message);
//            case "NTC-FTP-LOG":
//                return ftpLog(message);
//            case "NTC-MAIL-LOG":
//                return mailLog(message);
//            case "NTC-OPENVPN-LOG":
//                return openVpnLog(message);
//            case "NTC-P2P-LOG":
//                return p2pLog(message);
//            case "NTC-STREAMING-MEDIA-LOG":
//                return streamMediaLog(message);
//            case "NTC-VOIP-LOG":
//                return voipLog(message);
//            case "NTC-KEYWORDS-URL-LOG":
//                return message;
//            case "MM-SAMPLE-AUDIO-LOG":
//                return avIpLog(message);
//            case "MM-SAMPLE-VIDEO-LOG":
//                return avIpLog(message);
//            case "MM-PORN-AUDIO-LEVEL-LOG":
//                return avIpLog(message);
//            case "MM-PORN-VIDEO-LEVEL-LOG":
//                return avIpLog(message);
//            case "MM-SAMPLE-PIC-LOG":
//                return avIpLog(message);
//            case "MM-SAMPLE-VOIP-LOG":
//                return voipIpLog(message);
//            case "MM-FILE-DIGEST-LOG":
//                return avIpLog(message);
//            case "NTC-COLLECT-VOIP-LOG":
//                return collectVoipLog(message);
//
////            case "MM-AV-IP-LOG":
////                return avIpLog(message);
////            case "MM-AV-URL-LOG":
////                return avIpLog(message);
////            case "MM-PIC-IP-LOG":
////                return avIpLog(message);
////            case "MM-PIC-URL-LOG":
////                return avIpLog(message);
////            case "MM-VOIP-IP-LOG":
////                return voipIpLog(message);
////            case "MM-SPEAKER-RECOGNIZATION-LOG":
////                return avIpLog(message);
////            case "MM-LOGO-DETECTION-LOG":
////                return avIpLog(message);
////            case "MM-FACE-RECOGNIZATION-LOG":
////                return avIpLog(message);
////            case "MM-VOIP-ACCOUNT-LOG":
////                return voipIpLog(message);
////            case "NTC-IPSEC-LOG":
////                return ipsecLog(message);
////            case "NTC-L2TP-LOG":
////                return l2tpLog(message);
////            case "NTC-PPTP-LOG":
////                return pptpLog(message);
////            case "NTC-SSH-LOG":
////                return sshLog(message);
//
//            default:
//                logger.error("There is no corresponding topic! topic name is :" + topic);
//                break;
//        }
//        return null;
//    }
//
//
//    private static String ntcConnLog(String message) {
//        try {
//            NTC_CONN_RECORD_LOG ntcConnRecordLog = JSONObject.parseObject(message, NTC_CONN_RECORD_LOG.class);
//            String appLabel = ntcConnRecordLog.getApp_label();
//            String sIp = ntcConnRecordLog.getS_ip();
//            String dIp = ntcConnRecordLog.getD_ip();
//            ntcConnRecordLog.setServer_locate(ipLookup.countryLookup(dIp));
//            ntcConnRecordLog.setClient_locate(ipLookup.cityLookupDetail(sIp));
//            ntcConnRecordLog.setD_asn(ipLookup.asnLookup(dIp, true));
//            ntcConnRecordLog.setS_asn(ipLookup.asnLookup(sIp, true));
//            if (StringUtil.isNotBlank(appLabel)) {
//                String[] split = ntcConnRecordLog.getApp_label().split(";");
//                ntcConnRecordLog.setProto_id(Integer.parseInt(split[0].split("=")[1]));
//                ntcConnRecordLog.setApp_id(Integer.parseInt(split[1].split("=")[1]));
//                ntcConnRecordLog.setOs_id(Integer.parseInt(split[2].split("=")[1]));
//                ntcConnRecordLog.setBs_id(Integer.parseInt(split[3].split("=")[1]));
//                ntcConnRecordLog.setWeb_id(Integer.parseInt(split[4].split("=")[1]));
//                ntcConnRecordLog.setBehav_id(Integer.parseInt(split[5].split("=")[1]));
//            } else {
//                ntcConnRecordLog.setProto_id(0);
//                ntcConnRecordLog.setApp_id(0);
//                ntcConnRecordLog.setOs_id(0);
//                ntcConnRecordLog.setBs_id(0);
//                ntcConnRecordLog.setWeb_id(0);
//                ntcConnRecordLog.setBehav_id(0);
//            }
//            return JSONObject.toJSONString(ntcConnRecordLog);
//        } catch (Exception e) {
//            logger.error("Log parsing error!!! \n" + message);
//            e.printStackTrace();
//            return "";
//        }
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
////            mmAvIpLog.setS_subscribe_id(TransFormUtils.getSubscribe(mmAvIpLog.getS_ip()));
////            mmAvIpLog.setD_subscribe_id(TransFormUtils.getSubscribe(mmAvIpLog.getD_ip()));
//            return JSONObject.toJSONString(mmAvIpLog);
//        } catch (Exception e) {
//            logger.error("Log parsing error!!! \n" + message);
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
////            mmVoipIpLog.setS_subscribe_id(TransFormUtils.getSubscribe(mmVoipIpLog.getS_ip()));
////            mmVoipIpLog.setD_subscribe_id(TransFormUtils.getSubscribe(mmVoipIpLog.getD_ip()));
//            return JSONObject.toJSONString(mmVoipIpLog);
//        } catch (Exception e) {
//            logger.error("Log parsing error!!! \n" + message);
//            e.printStackTrace();
//            return "";
//        }
//    }
//
//    private static String collectVoipLog(String message) {
//        try {
//            NTC_COLLECT_VOIP_LOG ntcCollectVoipLog = JSONObject.parseObject(message, NTC_COLLECT_VOIP_LOG.class);
//            return JSONObject.toJSONString(ntcCollectVoipLog);
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.error("Log parsing error!!! \n" + message);
//            return "";
//        }
//    }
//
//
//    private static String appLog(String message) {
//        try {
//            NTC_APP_LOG ntcAppLog = JSONObject.parseObject(message, NTC_APP_LOG.class);
//            String sIp = ntcAppLog.getS_ip();
//            String dIp = ntcAppLog.getD_ip();
//            ntcAppLog.setServer_locate(ipLookup.countryLookup(dIp));
//            ntcAppLog.setClient_locate(ipLookup.cityLookupDetail(sIp));
//            ntcAppLog.setD_asn(ipLookup.asnLookup(dIp, true));
//            ntcAppLog.setS_asn(ipLookup.asnLookup(sIp, true));
////            ntcAppLog.setS_subscribe_id(TransFormUtils.getSubscribe(ntcAppLog.getS_ip()));
////            ntcAppLog.setD_subscribe_id(TransFormUtils.getSubscribe(ntcAppLog.getD_ip()));
//            return JSONObject.toJSONString(ntcAppLog);
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.error("Log parsing error!!! \n" + message);
//            return "";
//        }
//    }
//
//    private static String bgpLog(String message) {
//        try {
//            NTC_BGP_LOG ntcBgpLog = JSONObject.parseObject(message, NTC_BGP_LOG.class);
//            String sIp = ntcBgpLog.getS_ip();
//            String dIp = ntcBgpLog.getD_ip();
//            ntcBgpLog.setServer_locate(ipLookup.countryLookup(dIp));
//            ntcBgpLog.setClient_locate(ipLookup.cityLookupDetail(sIp));
//            ntcBgpLog.setD_asn(ipLookup.asnLookup(dIp, true));
//            ntcBgpLog.setS_asn(ipLookup.asnLookup(sIp, true));
////            ntcBgpLog.setS_subscribe_id(TransFormUtils.getSubscribe(ntcBgpLog.getS_ip()));
////            ntcBgpLog.setD_subscribe_id(TransFormUtils.getSubscribe(ntcBgpLog.getD_ip()));
//            return JSONObject.toJSONString(ntcBgpLog);
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.error("Log parsing error!!! \n" + message);
//            return "";
//        }
//    }
//
//    private static String ddosLog(String message) {
//        try {
//            NTC_DDOS_LOG ntcDdosLog = JSONObject.parseObject(message, NTC_DDOS_LOG.class);
//            String sIp = ntcDdosLog.getS_ip();
//            String dIp = ntcDdosLog.getD_ip();
//            ntcDdosLog.setServer_locate(ipLookup.countryLookup(dIp));
//            ntcDdosLog.setClient_locate(ipLookup.cityLookupDetail(sIp));
//            ntcDdosLog.setD_asn(ipLookup.asnLookup(dIp, true));
//            ntcDdosLog.setS_asn(ipLookup.asnLookup(sIp, true));
////            ntcDdosLog.setS_subscribe_id(TransFormUtils.getSubscribe(ntcDdosLog.getS_ip()));
////            ntcDdosLog.setD_subscribe_id(TransFormUtils.getSubscribe(ntcDdosLog.getD_ip()));
//            return JSONObject.toJSONString(ntcDdosLog);
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.error("Log parsing error!!! \n" + message);
//            return "";
//        }
//    }
//
//    private static String dnsLog(String message) {
//        try {
//            NTC_DNS_LOG ntcDnsLog = JSONObject.parseObject(message, NTC_DNS_LOG.class);
//            String sIp = ntcDnsLog.getS_ip();
//            String dIp = ntcDnsLog.getD_ip();
//            ntcDnsLog.setServer_locate(ipLookup.countryLookup(dIp));
//            ntcDnsLog.setClient_locate(ipLookup.cityLookupDetail(sIp));
//            ntcDnsLog.setD_asn(ipLookup.asnLookup(dIp, true));
//            ntcDnsLog.setS_asn(ipLookup.asnLookup(sIp, true));
////            ntcDnsLog.setS_subscribe_id(TransFormUtils.getSubscribe(ntcDnsLog.getS_ip()));
////            ntcDnsLog.setD_subscribe_id(TransFormUtils.getSubscribe(ntcDnsLog.getD_ip()));
//            return JSONObject.toJSONString(ntcDnsLog);
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.error("Log parsing error!!! \n" + message);
//            return "";
//        }
//    }
//
//    private static String ftpLog(String message) {
//        try {
//            NTC_FTP_LOG ntcFtpLog = JSONObject.parseObject(message, NTC_FTP_LOG.class);
//            String sIp = ntcFtpLog.getS_ip();
//            String dIp = ntcFtpLog.getD_ip();
//            ntcFtpLog.setServer_locate(ipLookup.countryLookup(dIp));
//            ntcFtpLog.setClient_locate(ipLookup.cityLookupDetail(sIp));
//            ntcFtpLog.setD_asn(ipLookup.asnLookup(dIp, true));
//            ntcFtpLog.setS_asn(ipLookup.asnLookup(sIp, true));
////            ntcFtpLog.setS_subscribe_id(TransFormUtils.getSubscribe(ntcFtpLog.getS_ip()));
////            ntcFtpLog.setD_subscribe_id(TransFormUtils.getSubscribe(ntcFtpLog.getD_ip()));
//            return JSONObject.toJSONString(ntcFtpLog);
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.error("Log parsing error!!! \n" + message);
//            return "";
//        }
//    }
//
//    private static String httpLog(String message) {
//        try {
//            NTC_HTTP_LOG ntcHttpLog = JSONObject.parseObject(message, NTC_HTTP_LOG.class);
////            if (StringUtil.isBlank(ntcHttpLog.getUrl())) {
////                TransFormUtils.getUniFlow(ntcHttpLog);
////            }
//            String sIp = ntcHttpLog.getS_ip();
//            String dIp = ntcHttpLog.getD_ip();
//            ntcHttpLog.setServer_locate(ipLookup.countryLookup(dIp));
//            ntcHttpLog.setClient_locate(ipLookup.cityLookupDetail(sIp));
//            ntcHttpLog.setD_asn(ipLookup.asnLookup(dIp, true));
//            ntcHttpLog.setS_asn(ipLookup.asnLookup(sIp, true));
////            ntcHttpLog.setS_subscribe_id(TransFormUtils.getSubscribe(ntcHttpLog.getS_ip()));
////            ntcHttpLog.setD_subscribe_id(TransFormUtils.getSubscribe(ntcHttpLog.getD_ip()));
//            ntcHttpLog.setWebsite(StringUtil.getDomain(ntcHttpLog.getUrl()));
//            return JSONObject.toJSONString(ntcHttpLog);
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.error("Log parsing error!!! \n" + message);
//            return "";
//        }
//    }
//
//    private static String ipLog(String message) {
//        try {
//            NTC_IP_LOG ntcIpLog = JSONObject.parseObject(message, NTC_IP_LOG.class);
//            String sIp = ntcIpLog.getS_ip();
//            String dIp = ntcIpLog.getD_ip();
//            ntcIpLog.setServer_locate(ipLookup.countryLookup(dIp));
//            ntcIpLog.setClient_locate(ipLookup.cityLookupDetail(sIp));
//            ntcIpLog.setD_asn(ipLookup.asnLookup(dIp, true));
//            ntcIpLog.setS_asn(ipLookup.asnLookup(sIp, true));
////            ntcIpLog.setS_subscribe_id(TransFormUtils.getSubscribe(ntcIpLog.getS_ip()));
////            ntcIpLog.setD_subscribe_id(TransFormUtils.getSubscribe(ntcIpLog.getD_ip()));
//            return JSONObject.toJSONString(ntcIpLog);
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.error("Log parsing error!!! \n" + message);
//            return "";
//        }
//    }
//
//
//    private static String mailLog(String message) {
//        try {
//            NTC_MAIL_LOG ntcMailLog = JSONObject.parseObject(message, NTC_MAIL_LOG.class);
//            String sIp = ntcMailLog.getS_ip();
//            String dIp = ntcMailLog.getD_ip();
//            ntcMailLog.setServer_locate(ipLookup.countryLookup(dIp));
//            ntcMailLog.setClient_locate(ipLookup.cityLookupDetail(sIp));
//            ntcMailLog.setD_asn(ipLookup.asnLookup(dIp, true));
//            ntcMailLog.setS_asn(ipLookup.asnLookup(sIp, true));
////            ntcMailLog.setS_subscribe_id(TransFormUtils.getSubscribe(ntcMailLog.getS_ip()));
////            ntcMailLog.setD_subscribe_id(TransFormUtils.getSubscribe(ntcMailLog.getD_ip()));
//            if (StringUtil.isNotBlank(ntcMailLog.getSubject())) {
//                String subjectCharset = JSONObject.parseObject(message).getString("subject_charset");
//                ntcMailLog.setSubject(DecodeUtils.base64Str(ntcMailLog.getSubject(), subjectCharset));
//            }
//            return JSONObject.toJSONString(ntcMailLog);
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.error("Log parsing error!!! \n" + message);
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
//            e.printStackTrace();
//            logger.error("Log parsing error!!! \n" + message);
//            return "";
//        }
//    }
//
//    private static String p2pLog(String message) {
//        try {
//            NTC_P2P_LOG ntcP2PLog = JSONObject.parseObject(message, NTC_P2P_LOG.class);
//            String sIp = ntcP2PLog.getS_ip();
//            String dIp = ntcP2PLog.getD_ip();
//            ntcP2PLog.setServer_locate(ipLookup.countryLookup(dIp));
//            ntcP2PLog.setClient_locate(ipLookup.cityLookupDetail(sIp));
//            ntcP2PLog.setD_asn(ipLookup.asnLookup(dIp, true));
//            ntcP2PLog.setS_asn(ipLookup.asnLookup(sIp, true));
////            ntcP2PLog.setS_subscribe_id(TransFormUtils.getSubscribe(ntcP2PLog.getS_ip()));
////            ntcP2PLog.setD_subscribe_id(TransFormUtils.getSubscribe(ntcP2PLog.getD_ip()));
//            return JSONObject.toJSONString(ntcP2PLog);
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.error("Log parsing error!!! \n" + message);
//            return "";
//        }
//    }
//
//
//    private static String streamMediaLog(String message) {
//        try {
//            NTC_STREAMING_MEDIA_LOG ntcStreamingMediaLog = JSONObject.parseObject(message, NTC_STREAMING_MEDIA_LOG.class);
//            String sIp = ntcStreamingMediaLog.getS_ip();
//            String dIp = ntcStreamingMediaLog.getD_ip();
//            ntcStreamingMediaLog.setServer_locate(ipLookup.countryLookup(dIp));
//            ntcStreamingMediaLog.setClient_locate(ipLookup.cityLookupDetail(sIp));
//            ntcStreamingMediaLog.setD_asn(ipLookup.asnLookup(dIp, true));
//            ntcStreamingMediaLog.setS_asn(ipLookup.asnLookup(sIp, true));
////            ntcStreamingMediaLog.setS_subscribe_id(TransFormUtils.getSubscribe(ntcStreamingMediaLog.getS_ip()));
////            ntcStreamingMediaLog.setD_subscribe_id(TransFormUtils.getSubscribe(ntcStreamingMediaLog.getD_ip()));
//            return JSONObject.toJSONString(ntcStreamingMediaLog);
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.error("Log parsing error!!! \n" + message);
//            return "";
//        }
//    }
//
//    private static String voipLog(String message) {
//        try {
//            NTC_VOIP_LOG ntcVoipLog = JSONObject.parseObject(message, NTC_VOIP_LOG.class);
//            String sIp = ntcVoipLog.getS_ip();
//            String dIp = ntcVoipLog.getD_ip();
//            ntcVoipLog.setServer_locate(ipLookup.countryLookup(dIp));
//            ntcVoipLog.setClient_locate(ipLookup.cityLookupDetail(sIp));
//            ntcVoipLog.setD_asn(ipLookup.asnLookup(dIp, true));
//            ntcVoipLog.setS_asn(ipLookup.asnLookup(sIp, true));
////            ntcVoipLog.setS_subscribe_id(TransFormUtils.getSubscribe(ntcVoipLog.getS_ip()));
////            ntcVoipLog.setD_subscribe_id(TransFormUtils.getSubscribe(ntcVoipLog.getD_ip()));
//            return JSONObject.toJSONString(ntcVoipLog);
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.error("Log parsing error!!! \n" + message);
//            return "";
//        }
//    }
//
//
//    private static String sslLog(String message) {
//        try {
//            NTC_COLLECT_SSL_LOG ntcSslLog = JSONObject.parseObject(message, NTC_COLLECT_SSL_LOG.class);
//            String sIp = ntcSslLog.getS_ip();
//            String dIp = ntcSslLog.getD_ip();
//            ntcSslLog.setServer_locate(ipLookup.countryLookup(dIp));
//            ntcSslLog.setClient_locate(ipLookup.cityLookupDetail(sIp));
//            ntcSslLog.setD_asn(ipLookup.asnLookup(dIp, true));
//            ntcSslLog.setS_asn(ipLookup.asnLookup(sIp, true));
////            ntcSslLog.setS_subscribe_id(TransFormUtils.getSubscribe(ntcSslLog.getS_ip()));
////            ntcSslLog.setD_subscribe_id(TransFormUtils.getSubscribe(ntcSslLog.getD_ip()));
//            return JSONObject.toJSONString(ntcSslLog);
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.error("Log parsing error!!! \n" + message);
//            return "";
//        }
//    }
//
//    private static String pHttpLog(String message) {
//        try {
//            PXY_HTTP_LOG pxyHttpLog = JSONObject.parseObject(message, PXY_HTTP_LOG.class);
//            String sIp = pxyHttpLog.getS_ip();
//            String dIp = pxyHttpLog.getD_ip();
//            pxyHttpLog.setServer_locate(ipLookup.countryLookup(dIp));
//            pxyHttpLog.setClient_locate(ipLookup.cityLookupDetail(sIp));
//            pxyHttpLog.setD_asn(ipLookup.asnLookup(dIp, true));
//            pxyHttpLog.setS_asn(ipLookup.asnLookup(sIp, true));
//            pxyHttpLog.setWebsite(StringUtil.getDomain(pxyHttpLog.getUrl()));
//            return JSONObject.toJSONString(pxyHttpLog);
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.error("Log parsing error!!! \n" + message);
//            return "";
//        }
//    }
//
//
//    /**
//     * ---------------------------------删除的topic------------------------------------------------------------
//     **/
//
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
//            logger.error("Log parsing error!!! \n" + message);
//            e.printStackTrace();
//            return "";
//        }
//    }
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
//            e.printStackTrace();
//            logger.error("Log parsing error!!! \n" + message);
//            return "";
//        }
//    }
//
//    private static String pptpLog(String message) {
//        try {
//            NTC_PPTP_LOG ntcPptpLog = JSONObject.parseObject(message, NTC_PPTP_LOG.class, Feature.OrderedField);
//            ntcPptpLog.setServer_locate(ipLookup.countryLookup(ntcPptpLog.getD_ip()));
//            ntcPptpLog.setClient_locate(ipLookup.cityLookupDetail(ntcPptpLog.getS_ip()));
//            ntcPptpLog.setD_asn(ipLookup.asnLookup(ntcPptpLog.getD_ip()).trim());
//            ntcPptpLog.setS_asn(ipLookup.asnLookup(ntcPptpLog.getS_ip()).trim());
////            ntcPptpLog.setS_subscribe_id(TransFormUtils.getSubscribe(ntcPptpLog.getS_ip()));
////            ntcPptpLog.setD_subscribe_id(TransFormUtils.getSubscribe(ntcPptpLog.getD_ip()));
//            return JSONObject.toJSONString(ntcPptpLog);
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.error("Log parsing error!!! \n" + message);
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
//            e.printStackTrace();
//            logger.error("Log parsing error!!! \n" + message);
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
//            e.printStackTrace();
//            logger.error("Log parsing error!!! \n" + message);
//            return "";
//        }
//    }
//
//
//}
