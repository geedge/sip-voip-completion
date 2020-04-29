package cn.ac.iie.bolt;

import cn.ac.iie.bean.voipSipFromToLog.RouteRelationLog;
import cn.ac.iie.bean.voipSipOrigin.SipOriginALL;
import cn.ac.iie.common.CommonService;
import cn.ac.iie.common.HashTableConfig;
import cn.ac.iie.common.RealtimeCountConfig;
import cn.ac.iie.dao.DataBaseLoad;
import cn.ac.iie.dao.KafkaDB;
import cn.ac.iie.utils.IPIPLibrary.Ipip;
import cn.ac.iie.utils.TupleUtils;
import com.alibaba.fastjson.JSONObject;
import com.zdjizhi.utils.StringUtil;
import org.apache.log4j.Logger;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetSipOriBoltDC extends BaseBasicBolt {

    private static final long serialVersionUID = -5702741658721325473L;

    private static Logger logger = Logger.getLogger(GetSipOriBoltDC.class);

    private static Ipip ipIpLook = new Ipip();

    private Long SipOriginAllCountSumMi = 0L;

    @SuppressWarnings("rawtypes")
    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        RealtimeCountConfig.configShow();
        //载入ipip库
        ipIpLook.load(RealtimeCountConfig.IPIP_LIBRARY);
    }

    @Override
    public void execute(Tuple tuple, BasicOutputCollector collector) {
        if (TupleUtils.isTick(tuple)) {
            logger.warn("Num of SipOriginALL Count(last one minutes): " + SipOriginAllCountSumMi);
            SipOriginAllCountSumMi = 0L;
        } else {
            try {
                String message = tuple.getString(0);
                if (StringUtil.isNotBlank(message)) {
                    SipOriginALL sipOriginLogDisable = JSONObject.parseObject(message, SipOriginALL.class);
                    SipOriginALL sipOriginLog = completeAbnorKeyForSipOriLog(sipOriginLogDisable, message);
                    SipOriginAllCountSumMi++;
                    //填充voip日志并发送
                    completeSipOriginAllLogAndemit(sipOriginLog, collector);

                    //填充voip路由关系表并发送
//                    completeRouteRelationLogAndemit(sipOriginLog, message, collector);
                }
            } catch (Exception e) {
                if (RealtimeCountConfig.ALL_LOG_OUTPUT_CONTROLLER.equals("yes")) {
                    logger.error("GetSipOriBoltDC get " + RealtimeCountConfig.KAFKA_SIP_ORIGIN_TOPIC + " Data is error!!! --> {" + e + "} <--");
                    logger.error("GetSipOriBoltDC the " + RealtimeCountConfig.KAFKA_SIP_ORIGIN_TOPIC + " Data is  --> {" + tuple.getString(0) + "} <--");
                    e.printStackTrace();
                } else {
                    logger.error("GetSipOriBoltDC get " + RealtimeCountConfig.KAFKA_SIP_ORIGIN_TOPIC + " Data is error!!! --> {" + e + "} <--");
                    e.printStackTrace();
                }
            }
        }
    }

    private SipOriginALL completeAbnorKeyForSipOriLog(SipOriginALL sipOriginLogDisable, String message) {
        Map mapWithLedge = JSONObject.parseObject(message, Map.class);
        sipOriginLogDisable.setCall_ID((String) (mapWithLedge.get("Call-ID")));
        sipOriginLogDisable.setUser_Agent((String) (mapWithLedge.get("User-Agent")));
        sipOriginLogDisable.setMax_Forwards((String) (mapWithLedge.get("Max-Forwards")));
        sipOriginLogDisable.setReq_Record_Route((String[]) (mapWithLedge.get("Req_Record-Route")));
        sipOriginLogDisable.setReq_Content_Type((String) (mapWithLedge.get("Req_Content-Type")));
        sipOriginLogDisable.setRes_Record_Route((String[]) (mapWithLedge.get("Res_Record-Route")));
        sipOriginLogDisable.setRes_Content_Type((String) (mapWithLedge.get("Res_Content-Type")));

        //将字符串数组字段转化为json字段便于入库
        sipOriginLogDisable.setReq_Via_Json(JSONObject.toJSONString(sipOriginLogDisable.getReq_Via()));
        sipOriginLogDisable.setReq_Record_Route_Json(JSONObject.toJSONString(sipOriginLogDisable.getReq_Record_Route()));
        sipOriginLogDisable.setReq_Route_Json(JSONObject.toJSONString(sipOriginLogDisable.getReq_Route()));
        sipOriginLogDisable.setRes_Via_Json(JSONObject.toJSONString(sipOriginLogDisable.getRes_Via()));
        sipOriginLogDisable.setRes_Record_Route_Json(JSONObject.toJSONString(sipOriginLogDisable.getRes_Record_Route()));
        sipOriginLogDisable.setRes_Route_Json(JSONObject.toJSONString(sipOriginLogDisable.getRes_Route()));

        return sipOriginLogDisable;
    }

    //填充SIP_ORIGIN_ALL日志并发送
    private void completeSipOriginAllLogAndemit(SipOriginALL sipOriginLog, BasicOutputCollector collector) {
        //获取SRC_IP相关定位
        if (StringUtil.isNotBlank(sipOriginLog.getSRC_IP())
                && sipOriginLog.getSRC_IP() != null
                && sipOriginLog.getSRC_IP().length() != 0
                && !(sipOriginLog.getSRC_IP().contains(":"))) {
            String[] src_IpLocation = ipIpLook.find(sipOriginLog.getSRC_IP());
            String src_Ip_nation = src_IpLocation[0];
            String src_Ip_region = src_IpLocation[1];
            sipOriginLog.setSRC_LOCATION_NATION(src_Ip_nation);
            sipOriginLog.setSRC_LOCATION_REGION(src_Ip_region);

            if (sipOriginLog.getSRC_LOCATION_REGION().equals("香港")
                    || sipOriginLog.getSRC_LOCATION_REGION().equals("台湾")
                    || sipOriginLog.getSRC_LOCATION_REGION().equals("澳门")) {
                sipOriginLog.setSRC_LOCATION_NATION(sipOriginLog.getSRC_LOCATION_REGION());
            }

            //设置SRC_IP国家码
            String src_Ip_nation_code = HashTableConfig.ISO_3166_1_ALPHA_2.get(sipOriginLog.getSRC_LOCATION_NATION());
            if (StringUtil.isNotBlank(src_Ip_nation_code)
                    && src_Ip_nation_code != null
                    && src_Ip_nation_code.length() != 0) {
                sipOriginLog.setSRC_LOCATION_NATION_CODE(src_Ip_nation_code);
            }
        }

        //获取DST_IP相关定位
        if (StringUtil.isNotBlank(sipOriginLog.getDST_IP())
                && sipOriginLog.getDST_IP() != null
                && sipOriginLog.getDST_IP().length() != 0
                && !(sipOriginLog.getDST_IP().contains(":"))) {
            String[] dst_IpLocation = ipIpLook.find(sipOriginLog.getDST_IP());
            String dst_Ip_nation = dst_IpLocation[0];
            String dst_Ip_region = dst_IpLocation[1];
            sipOriginLog.setDST_LOCATION_NATION(dst_Ip_nation);
            sipOriginLog.setDST_LOCATION_REGION(dst_Ip_region);

            if (sipOriginLog.getDST_LOCATION_REGION().equals("香港")
                    || sipOriginLog.getDST_LOCATION_REGION().equals("台湾")
                    || sipOriginLog.getDST_LOCATION_REGION().equals("澳门")) {
                sipOriginLog.setDST_LOCATION_NATION(sipOriginLog.getDST_LOCATION_REGION());
            }

            //设置DST_IP国家码
            String dst_Ip_nation_code = HashTableConfig.ISO_3166_1_ALPHA_2.get(sipOriginLog.getDST_LOCATION_NATION());
            if (StringUtil.isNotBlank(dst_Ip_nation_code)
                    && dst_Ip_nation_code != null
                    && dst_Ip_nation_code.length() != 0) {
                sipOriginLog.setDST_LOCATION_NATION_CODE(dst_Ip_nation_code);
            }
        }

        //设置IP_TYPE
        if (sipOriginLog.getDST_IP().contains(":") && sipOriginLog.getDST_IP().split(":").length > 2) {
            sipOriginLog.setIP_TYPE("6");
        } else {
            sipOriginLog.setIP_TYPE("4");
        }

        //处理Request_URI
        if (StringUtil.isNotBlank(sipOriginLog.getRequest_URI())
                && sipOriginLog.getRequest_URI() != null
                && sipOriginLog.getRequest_URI().length() != 0) {

            if (!(sipOriginLog.getRequest_URI().contains("sip:"))) {
                if (sipOriginLog.getRequest_URI().contains("@")) {
                    String[] splitAlte = sipOriginLog.getRequest_URI().split("@");
                    String userName = splitAlte[0];
                    String ServiceDomain = splitAlte[1].split(";")[0];
                    sipOriginLog.setUser_name(userName);
                    sipOriginLog.setService_domain(ServiceDomain);

                    if (StringUtil.isNotBlank(sipOriginLog.getService_domain())
                            && sipOriginLog.getService_domain() != null
                            && sipOriginLog.getService_domain().length() != 0) {

                        if (sipOriginLog.getService_domain().contains(":")) {
                            if (sipOriginLog.getService_domain().split(":").length <= 2) {
                                if (isIp(sipOriginLog.getService_domain().split(":")[0])) {//0索引为ip或者url
                                    String[] splitColon = sipOriginLog.getService_domain().split(":");
                                    String service_domain_v4_ip = splitColon[0];
                                    String service_domain_v4_port = splitColon[1];
                                    boolean judgeInnerIP = isInnerIP(service_domain_v4_ip);
                                    if (judgeInnerIP) {
                                        String newServiceDomain = sipOriginLog.getDST_IP() + ":" + service_domain_v4_port;
                                        sipOriginLog.setService_domain(newServiceDomain);
                                        sipOriginLog.setService_domain_valid("1");
                                    } else {
                                        //外网ip
                                        sipOriginLog.setService_domain_valid("0");
                                    }
                                } else {
                                    //url:端口号,暂时不做操作
                                }
                            } else {
                                //v6,暂时不做操作
                            }
                        } else {
                            boolean ipOrurl = isIp(sipOriginLog.getService_domain());
                            if (ipOrurl) {
                                String ipv4_serviceDomain = sipOriginLog.getService_domain();
                                boolean judgeInnerIP = isInnerIP(ipv4_serviceDomain);
                                if (judgeInnerIP) {
                                    String newServiceDomain = sipOriginLog.getDST_IP();
                                    sipOriginLog.setService_domain(newServiceDomain);
                                    sipOriginLog.setService_domain_valid("1");
                                } else {
                                    //外网ip
                                    sipOriginLog.setService_domain_valid("0");
                                }
                            } else {
                                //url网址,暂不处理
                            }
                        }
                    }
                } else if (sipOriginLog.getRequest_URI().contains(".") || sipOriginLog.getRequest_URI().contains(":")) {
                    String ServiceDomain = sipOriginLog.getRequest_URI();
                    sipOriginLog.setService_domain(ServiceDomain);

                    if (StringUtil.isNotBlank(sipOriginLog.getService_domain())
                            && sipOriginLog.getService_domain() != null
                            && sipOriginLog.getService_domain().length() != 0) {

                        if (sipOriginLog.getService_domain().contains(":")) {
                            if (sipOriginLog.getService_domain().split(":").length <= 2) {
                                if (isIp(sipOriginLog.getService_domain().split(":")[0])) {
                                    String[] splitColon = sipOriginLog.getService_domain().split(":");
                                    String service_domain_v4_ip = splitColon[0];
                                    String service_domain_v4_port = splitColon[1];
                                    boolean judgeInnerIP = isInnerIP(service_domain_v4_ip);
                                    if (judgeInnerIP) {
                                        String newServiceDomain = sipOriginLog.getDST_IP() + ":" + service_domain_v4_port;
                                        sipOriginLog.setService_domain(newServiceDomain);
                                        sipOriginLog.setService_domain_valid("1");
                                    } else {
                                        //外网ip
                                        sipOriginLog.setService_domain_valid("0");
                                    }
                                } else {
                                    //url:端口号,暂时不做操作
                                }
                            } else {
                                //v6,暂时不做操作
                            }
                        } else {
                            boolean ipOrurl = isIp(sipOriginLog.getService_domain());
                            if (ipOrurl) {
                                String ipv4_serviceDomain = sipOriginLog.getService_domain();
                                boolean judgeInnerIP = isInnerIP(ipv4_serviceDomain);
                                if (judgeInnerIP) {
                                    String newServiceDomain = sipOriginLog.getDST_IP();
                                    sipOriginLog.setService_domain(newServiceDomain);
                                    sipOriginLog.setService_domain_valid("1");
                                } else {
                                    //外网ip
                                    sipOriginLog.setService_domain_valid("0");
                                }
                            } else {
                                //url网址,暂不处理
                            }
                        }
                    }
                } else if (!(sipOriginLog.getRequest_URI().contains(".")) && !(sipOriginLog.getRequest_URI().contains(":"))) {
                    //即是一个字符串或者数字串,没有具体价值,不做处理
                } else {
                    if (RealtimeCountConfig.PART_LOG_OUTPUT_CONTROLLER.equals("yes")) {
                        logger.error("GetSipOriBoltDC--> " + RealtimeCountConfig.KAFKA_SIP_ORIGIN_TOPIC + " Request_URI no @ and is not ip , Request_URI is ---> " + sipOriginLog.getRequest_URI() + " <---");
                    }
                }
            } else {
                //此处包含sip:的情况已经在前端代码中去除,故这里不做处理
            }
        }

        //处理Res_stat_format
        if (StringUtil.isNotBlank(sipOriginLog.getRes_stat())
                && sipOriginLog.getRes_stat() != null
                && sipOriginLog.getRes_stat().length() != 0) {

            String replaceBlank = sipOriginLog.getRes_stat().toLowerCase().trim().replace(" ", "");//去除空格
            //修整Res_stat_format字段异常格式
            replaceBlank = repairResStatFormatStr(replaceBlank);
            sipOriginLog.setRes_stat_format(replaceBlank);
        }


        //处理From
        if (StringUtil.isNotBlank(sipOriginLog.getFrom())
                && sipOriginLog.getFrom() != null
                && sipOriginLog.getFrom().length() != 0) {
            String useFromStr = sipOriginLog.getFrom().replace(" ", "").replace(",", "");

            if (sipOriginLog.getFrom().contains("\"")) {
                String nick_name = getNickname(useFromStr, "from");
                sipOriginLog.setFrom_Nickname(nick_name);
            } else {
                //From字段不包含"",此时获取不到用户昵称,但能获取用户名,服务域名,下方统一获取
            }
            String user_name = getUserName(useFromStr, "from");
            sipOriginLog.setFrom_usr_name(user_name);
            String serDomainAndValid = getSerDomain(useFromStr, sipOriginLog, "from");
            if (serDomainAndValid.contains("##%##")) {
                String[] splitSpliter = serDomainAndValid.split("##%##");
                String serDomainOrNull = splitSpliter[0];
                String validStr = splitSpliter[1];
                if ((!(serDomainOrNull.equals("null")))) {
                    sipOriginLog.setFrom_ser_domain(serDomainOrNull);
                } else {
                    sipOriginLog.setFrom_ser_domain(null);
                }
                sipOriginLog.setFrom_ser_domain_valid(validStr);
            }
        }

        //处理To
        if (StringUtil.isNotBlank(sipOriginLog.getTo())
                && sipOriginLog.getTo() != null
                && sipOriginLog.getTo().length() != 0) {
            String useToStr = sipOriginLog.getTo().replace(" ", "").replace(",", "");

            if (sipOriginLog.getTo().contains("\"")) {
                String nick_name = getNickname(useToStr, "to");
                sipOriginLog.setTo_Nickname(nick_name);
            } else {
                //To字段不包含"",此时获取不到用户昵称,但能获取用户名,服务域名,下方统一获取
            }
            String user_name = getUserName(useToStr, "to");
            sipOriginLog.setTo_usr_name(user_name);
            String serDomainAndValid = getSerDomain(useToStr, sipOriginLog, "to");
            if (serDomainAndValid.contains("##%##")) {
                String[] splitSpliter = serDomainAndValid.split("##%##");
                String serDomainOrNull = splitSpliter[0];
                String validStr = splitSpliter[1];
                if ((!(serDomainOrNull.equals("null")))) {
                    //补丁,针对性修改To_ser_domain格式
                    serDomainOrNull = pointSerDomainPatch(serDomainOrNull);
                    //设置To_ser_domain
                    sipOriginLog.setTo_ser_domain(serDomainOrNull);
                } else {
                    sipOriginLog.setTo_ser_domain(null);
                }
                sipOriginLog.setTo_ser_domain_valid(validStr);
            }
        }

        //获取Cseq_method
        if (StringUtil.isNotBlank(sipOriginLog.getCseq())
                && sipOriginLog.getCseq() != null
                && sipOriginLog.getCseq().length() != 0) {
            String[] splitCseq = sipOriginLog.getCseq().split(" ");
            if (splitCseq.length == 2) {
                sipOriginLog.setCseq_method(splitCseq[1]);
            }
        }

        //处理Req_Contact
        if (StringUtil.isNotBlank(sipOriginLog.getReq_Contact())
                && sipOriginLog.getReq_Contact() != null
                && sipOriginLog.getReq_Contact().length() != 0) {
            String useReqConStr = sipOriginLog.getReq_Contact().replace(" ", "").replace(",", "");

            if (sipOriginLog.getReq_Contact().contains("\"")) {
                String nick_name = getNickname(useReqConStr, "req");
                sipOriginLog.setReq_Contact_Nickname(nick_name);
            } else {
                //字段不包含"",此时获取不到用户昵称,但能获取用户名,服务域名,下方统一获取
            }
            String user_name = getUserName(useReqConStr, "req");
            sipOriginLog.setReq_Contact_usr_name(user_name);
            String serDomainAndValid = getSerDomain(useReqConStr, sipOriginLog, "req");
            if (serDomainAndValid.contains("##%##")) {
                String[] splitSpliter = serDomainAndValid.split("##%##");
                String serDomainOrNull = splitSpliter[0];
                String validStr = splitSpliter[1];
                if ((!(serDomainOrNull.equals("null")))) {
                    sipOriginLog.setReq_Contact_ser_domain(serDomainOrNull);
                } else {
                    sipOriginLog.setReq_Contact_ser_domain(null);
                }
                sipOriginLog.setReq_ser_domain_valid(validStr);
            }
        }

        //处理Res_Contact
        if (StringUtil.isNotBlank(sipOriginLog.getRes_Contact())
                && sipOriginLog.getRes_Contact() != null
                && sipOriginLog.getRes_Contact().length() != 0) {
            String useResConStr = sipOriginLog.getRes_Contact().replace(" ", "").replace(",", "");

            if (sipOriginLog.getRes_Contact().contains("\"")) {
                String nick_name = getNickname(useResConStr, "res");
                sipOriginLog.setRes_Contact_Nickname(nick_name);
            } else {
                //字段不包含"",此时获取不到用户昵称,但能获取用户名,服务域名,下方统一获取
            }
            String user_name = getUserName(useResConStr, "res");
            sipOriginLog.setRes_Contact_usr_name(user_name);
            String serDomainAndValid = getSerDomain(useResConStr, sipOriginLog, "res");
            if (serDomainAndValid.contains("##%##")) {
                String[] splitSpliter = serDomainAndValid.split("##%##");
                String serDomainOrNull = splitSpliter[0];
                String validStr = splitSpliter[1];
                if ((!(serDomainOrNull.equals("null")))) {
                    sipOriginLog.setRes_Contact_ser_domain(serDomainOrNull);
                } else {
                    sipOriginLog.setRes_Contact_ser_domain(null);
                }
                sipOriginLog.setRes_ser_domain_valid(validStr);
            }
        }

        //根据Req_Content_Type设置Req_coding(主叫编码方式)
        if (StringUtil.isNotBlank(sipOriginLog.getReq_Content_Type())
                && sipOriginLog.getReq_Content_Type() != null
                && sipOriginLog.getReq_Content_Type().length() != 0) {
            if (sipOriginLog.getReq_Content_Type().equals("application/sdp")) {
                if (StringUtil.isNotBlank(sipOriginLog.getReq_Content())
                        && sipOriginLog.getReq_Content() != null
                        && sipOriginLog.getReq_Content().length() != 0) {
                    String codingS = getCodingS(sipOriginLog.getReq_Content());
                    if (StringUtil.isNotBlank(codingS)) {
                        sipOriginLog.setReq_coding(codingS);
                    }
                }
            }
        }

        //根据Res_Content_Type设置Res_coding(被叫编码方式)
        if (StringUtil.isNotBlank(sipOriginLog.getRes_Content_Type())
                && sipOriginLog.getRes_Content_Type() != null
                && sipOriginLog.getRes_Content_Type().length() != 0) {
            if (sipOriginLog.getRes_Content_Type().equals("application/sdp")) {
                if (StringUtil.isNotBlank(sipOriginLog.getRes_Content())
                        && sipOriginLog.getRes_Content() != null
                        && sipOriginLog.getRes_Content().length() != 0) {
                    String codingS = getCodingS(sipOriginLog.getRes_Content());
                    if (StringUtil.isNotBlank(codingS)) {
                        sipOriginLog.setRes_coding(codingS);
                    }
                }
            }
        }

        collector.emit(new Values(JSONObject.toJSONString(sipOriginLog), "origin"));
    }

    //填充voip路由关系表并发送
    private void completeRouteRelationLogAndemit(SipOriginALL sipOriginLog, String message, BasicOutputCollector collector) {
        RouteRelationLog routeRelationLog = new RouteRelationLog();
        LinkedList<String> emitList = new LinkedList<String>();
        String src_ip = sipOriginLog.getSRC_IP();
        String dst_ip = sipOriginLog.getDST_IP();

        if (StringUtil.isNotBlank(sipOriginLog.getMethod())
                && sipOriginLog.getMethod() != null
                && sipOriginLog.getMethod().length() != 0) {
            //请求侧req,顺序   sip->last->….->first->dip
            if (sipOriginLog.getReq_Route() != null && sipOriginLog.getReq_Route().length != 0) {
                LinkedList<String> reqRouteList = getRoute(sipOriginLog.getReq_Route());
                emitList.add(src_ip);
                if (reqRouteList != null) {
                    if (reqRouteList.size() > 0) {
                        for (int i = reqRouteList.size() - 1; i >= 0; i--) {
                            emitList.add(reqRouteList.get(i));
                        }
                    }
                }
                emitList.add(dst_ip);
            } else if (sipOriginLog.getReq_Via() != null && sipOriginLog.getReq_Via().length != 0) {
                LinkedList<String> reqViaList = getVia(sipOriginLog.getReq_Via());
                emitList.add(src_ip);
                if (reqViaList != null) {
                    if (reqViaList.size() > 0) {
                        for (int i = reqViaList.size() - 1; i >= 0; i--) {
                            emitList.add(reqViaList.get(i));
                        }
                    }
                }
                emitList.add(dst_ip);
            } else if (sipOriginLog.getReq_Record_Route() != null && sipOriginLog.getReq_Record_Route().length != 0) {
                LinkedList<String> reqRecordRouteList = getRecordRoute(sipOriginLog.getReq_Record_Route());
                emitList.add(src_ip);
                if (reqRecordRouteList != null) {
                    if (reqRecordRouteList.size() > 0) {
                        for (int i = reqRecordRouteList.size() - 1; i >= 0; i--) {
                            emitList.add(reqRecordRouteList.get(i));
                        }
                    }
                }
                emitList.add(dst_ip);
            } else {
                //三个字段都没有则不做处理
            }
        } else {
            //响应侧res,顺序   dip->first->….->last->sip
            if (sipOriginLog.getRes_Route() != null && sipOriginLog.getRes_Route().length != 0) {
                LinkedList<String> resRouteList = getRoute(sipOriginLog.getRes_Route());
                emitList.add(dst_ip);
                if (resRouteList != null) {
                    if (resRouteList.size() > 0) {
                        for (int i = 0; i < resRouteList.size(); i++) {
                            emitList.add(resRouteList.get(i));
                        }
                    }
                }
                emitList.add(src_ip);
            } else if (sipOriginLog.getRes_Via() != null && sipOriginLog.getRes_Via().length != 0) {
                LinkedList<String> resViaList = getVia(sipOriginLog.getRes_Via());
                emitList.add(dst_ip);
                if (resViaList != null) {
                    if (resViaList.size() > 0) {
                        for (int i = 0; i < resViaList.size(); i++) {
                            emitList.add(resViaList.get(i));
                        }
                    }
                }
                emitList.add(src_ip);
            } else if (sipOriginLog.getRes_Record_Route() != null && sipOriginLog.getRes_Record_Route().length != 0) {
                LinkedList<String> resRecordRouteList = getRecordRoute(sipOriginLog.getRes_Record_Route());
                emitList.add(dst_ip);
                if (resRecordRouteList != null) {
                    if (resRecordRouteList.size() > 0) {
                        for (int i = 0; i < resRecordRouteList.size(); i++) {
                            emitList.add(resRecordRouteList.get(i));
                        }
                    }
                }
                emitList.add(src_ip);
            } else {
                //三个字段都没有则不做处理
            }
        }
        if (emitList.size() != 0) {
            for (int i = 0; i < emitList.size(); i++) {
                if (i != emitList.size() - 1) {
                    routeRelationLog.setFrom_domain(emitList.get(i));
                    routeRelationLog.setTo_domain(emitList.get(i + 1));
                    collector.emit(new Values(JSONObject.toJSONString(routeRelationLog), "route"));
                }
            }
        }
    }

    private LinkedList<String> getRecordRoute(String[] record_route) {
        LinkedList<String> recordRouteList = new LinkedList<>();
        try {
            for (int i = 0; i < record_route.length; i++) {
                String str = record_route[i].replace("<", "").replace(">", "");
                String splitSemi = str.split(";")[0];
                if (splitSemi.split(":").length <= 3) {
                    String splitColon = splitSemi.split(":")[1];
                    recordRouteList.add(splitColon);
                } else {
                    String splitSipColon = splitSemi.split("sip:")[1];
                    recordRouteList.add(splitSipColon);
                }
            }
        } catch (Exception e) {
            if (RealtimeCountConfig.PART_LOG_OUTPUT_CONTROLLER.equals("yes")) {
                logger.error("GetSipOriBoltDC-->getRoute Split Route error ---> " + e + " <---");
                logger.error("GetSipOriBoltDC-->getRoute Split Route data is  ---> " + Arrays.toString(record_route) + " <---");
            }
        }
        if (recordRouteList.size() != 0) {
            return recordRouteList;
        } else {
            return null;
        }
    }

    private LinkedList<String> getVia(String[] via) {
        LinkedList<String> viaList = new LinkedList<>();
        try {
            for (int i = 0; i < via.length; i++) {
                if (via[i].contains(" ")) {
                    String originDomain = via[i].split(";")[0].split(" ")[1];
                    if (originDomain.contains(":")) {
                        if (originDomain.split(":").length <= 2) {
                            viaList.add(originDomain.split(":")[0]);
                        } else {
                            viaList.add(originDomain);
                        }
                    } else {
                        viaList.add(originDomain);
                    }
                }
            }
        } catch (Exception e) {
            if (RealtimeCountConfig.PART_LOG_OUTPUT_CONTROLLER.equals("yes")) {
                logger.error("GetSipOriBoltDC-->getVia Split Via error ---> " + e + " <---");
                logger.error("GetSipOriBoltDC-->getVia Split Via data is  ---> " + Arrays.toString(via) + " <---");
            }
        }
        if (viaList.size() != 0) {
            return viaList;
        } else {
            return null;
        }
    }

    private LinkedList<String> getRoute(String[] route) {
        LinkedList<String> routeList = new LinkedList<>();
        try {
            for (int i = 0; i < route.length; i++) {
                String str = route[i].replace("<", "").replace(">", "");
                String splitSemi = str.split(";")[0];
                if (splitSemi.split(":").length <= 3) {
                    String splitColon = splitSemi.split(":")[1];
                    if (splitColon.contains("@") && splitColon.split("@")[1].contains(".")) {//例如"Req_Route":	["<sip:1.228.34.22;lr>", "<sip:114.207.73.139:5060;lr>", "<sip:orig@114.207.73.141:5067;lr>"]这种形式
                        splitColon = splitColon.split("@")[1];
                    }
                    routeList.add(splitColon);
                } else {
                    String splitSipColon = splitSemi.split("sip:")[1];
                    routeList.add(splitSipColon);
                }
            }
        } catch (Exception e) {
            if (RealtimeCountConfig.PART_LOG_OUTPUT_CONTROLLER.equals("yes")) {
                logger.error("GetSipOriBoltDC-->getRoute Split Route error ---> " + e + " <---");
                logger.error("GetSipOriBoltDC-->getRoute Split Route data is  ---> " + Arrays.toString(route) + " <---");
            }
        }
        if (routeList.size() != 0) {
            return routeList;
        } else {
            return null;
        }
    }

    //获取Nickname
    private String getNickname(String useStr, String type) {
        String nick_name = useStr.split("\"")[1];
        return nick_name;
    }

    //获取UserName
    private String getUserName(String useStr, String type) {
        if (useStr.equals("None") || useStr.equals("*")) {
            return null;
        }
        if (useStr.contains("@") && (useStr.contains("sip:") || useStr.contains("sips:"))) {
            String userName = useStr.replace("sips:", "sip:").split("sip:")[1].split("@")[0];
            return userName;
        } else if (!(useStr.contains("@")) && useStr.contains("sip:")) {
            try {
                if (useStr.split(":").length <= 3) {
                    String userName = useStr.split(":")[1];
                    return userName;
                } else {
                    if (useStr.contains(";")) {
                        String userName = useStr.split(";")[0].split(":")[1];
                        return userName;
                    } else {
                        String userName = useStr.replace("<", "")
                                .replace(">", "")
                                .split("sip:")[1];
                        return userName;
                    }
                }
            } catch (Exception e) {
                if (RealtimeCountConfig.PART_LOG_OUTPUT_CONTROLLER.equals("yes")) {
                    logger.error("|~|~|GetSipOriBoltDC===getUserName can not split this data but it has sip: --->" + useStr + "<--- the type is --->" + type + "<---|~|~|");
                }
                return null;
            }
        } else if (useStr.contains("tel:")) {
            String userName = useStr.split(">")[0].split("tel:")[1];
            return userName;
        } else if ((!(useStr.contains("."))) && (!(useStr.contains(":")))) {
            return null;
        } else {
            if (RealtimeCountConfig.PART_LOG_OUTPUT_CONTROLLER.equals("yes")) {
                logger.error("|~|~|GetSipOriBoltDC===getUserName can not split this data --->" + useStr + "<--- the type is --->" + type + "<---|~|~|");
            }
            return null;
        }
    }

    //获取ser_domain
    private String getSerDomain(String useStr, SipOriginALL sipOriginLog, String type) {
        if (useStr.equals("None") || useStr.equals("*")) {
            return "null##%##0";
        }
        String serDomain;
        if (useStr.contains("<") && useStr.contains(">")) {
            if (useStr.contains("@")) {
//                serDomain = useStr.split("@")[1].split(">")[0];//旧版,由于"From":"\"4903\" <sip:4903@>"以及"To":"\"1441519470648\" <sip:1441519470648@>"报索引越界异常
                String splitFirst = useStr.split("@")[1];
                if (!(">".equals(splitFirst))) {
                    serDomain = splitFirst.split(">")[0];
                } else {
                    serDomain = null;
                }
            } else if (useStr.contains(".")) {
                if (useStr.split(":").length == 3) {
                    String[] split = useStr.split(">")[0].split(":");
                    serDomain = split[1] + ":" + split[2];
                } else {
                    serDomain = useStr.split(">")[0].split(":")[1];
                }
            } else if (useStr.contains("sip:") && useStr.split(":").length > 3) {
                serDomain = useStr.split("sip:")[1].split(">")[0];
            } else if (useStr.contains("tel:") && useStr.split(":").length >= 2) {
                serDomain = useStr.split(">")[0].split("tel:")[1];
            } else {
                serDomain = useStr.split(">")[0].split(":")[1];
            }
        } else {
            if (useStr.contains("@")) {
                serDomain = useStr.split("@")[1];
            } else if (useStr.contains(".")) {
                if (useStr.split(":").length == 3) {
                    String[] split = useStr.split(":");
                    serDomain = split[1] + ":" + split[2];
                } else {
                    serDomain = useStr.split(":")[1];
                }
            } else if (useStr.contains("sip:")) {
                serDomain = useStr.split("sip:")[1];
            } else if (useStr.contains("tel:")) {
                if (useStr.contains(";")) {
                    serDomain = useStr.split(";")[0].split("tel:")[1];
                } else {
                    serDomain = useStr.split("tel:")[1];
                }
            } else if ((!(useStr.contains(":"))) && (!(useStr.contains(".")))) {
                serDomain = null;
            } else {
                if (useStr.contains(":")) {
                    serDomain = useStr.split(":")[1];
                } else {
                    serDomain = null;
                }
            }
        }

        if (serDomain == null || serDomain.length() == 0) {
            return "null##%##0";
        }
        if (serDomain.contains(":")) {
            if (serDomain.split(":").length <= 2) {
                String[] splitColon = serDomain.split(":");
                String ipOrUrl = splitColon[0];
                String port = splitColon[1];
                if (isIp(ipOrUrl)) {
                    if (isInnerIP(ipOrUrl)) {
                        if (StringUtil.isNotBlank(sipOriginLog.getMethod())
                                && sipOriginLog.getMethod() != null
                                && sipOriginLog.getMethod().length() != 0) {
                            if (type.equals("from") || type.equals("req")) {
                                String newServiceDomain = sipOriginLog.getSRC_IP() + ":" + port;
                                return newServiceDomain + "##%##1";
                            } else {
                                String newServiceDomain = sipOriginLog.getDST_IP() + ":" + port;
                                return newServiceDomain + "##%##1";
                            }
                        } else {
                            if (type.equals("from") || type.equals("req")) {
                                String newServiceDomain = sipOriginLog.getDST_IP() + ":" + port;
                                return newServiceDomain + "##%##1";
                            } else {
                                String newServiceDomain = sipOriginLog.getSRC_IP() + ":" + port;
                                return newServiceDomain + "##%##1";
                            }
                        }
                    } else {
                        //外网ip
                        return serDomain + "##%##0";
                    }
                } else {
                    //网址
                    return serDomain + "##%##0";
                }
            } else {
                //v6
                return serDomain + "##%##0";
            }
        } else {
            boolean ipOrurl = isIp(serDomain);
            if (ipOrurl) {
                if (isInnerIP(serDomain)) {
                    if (StringUtil.isNotBlank(sipOriginLog.getMethod())
                            && sipOriginLog.getMethod() != null
                            && sipOriginLog.getMethod().length() != 0) {
                        if (type.equals("from") || type.equals("req")) {
                            String newServiceDomain = sipOriginLog.getSRC_IP();
                            return newServiceDomain + "##%##1";
                        } else {
                            String newServiceDomain = sipOriginLog.getDST_IP();
                            return newServiceDomain + "##%##1";
                        }
                    } else {
                        if (type.equals("from") || type.equals("req")) {
                            String newServiceDomain = sipOriginLog.getDST_IP();
                            return newServiceDomain + "##%##1";
                        } else {
                            String newServiceDomain = sipOriginLog.getSRC_IP();
                            return newServiceDomain + "##%##1";
                        }
                    }
                } else {
                    return serDomain + "##%##0";
                }
            } else {
                return serDomain + "##%##0";
            }
        }
    }


    //通用方法,传入url,返回domain
    private String getDomainFromUrl(String oriUrl) {
        String url = oriUrl.split("[?]")[0];
        if (url.contains("http://") || url.contains("https://")) {
            if (url.split("//")[1].split("/")[0].split(":").length <= 2) {
                String v4Domain = url.split("//")[1]
                        .split("/")[0]
                        .split(":")[0];
                return v4Domain;
            } else {
                String v6Domain = url.split("//")[1]
                        .split("/")[0];
                return v6Domain;
            }
        } else {
            if (url.split("/")[0].split(":").length <= 2) {
                String v4Domain = url.split("/")[0].split(":")[0];
                return v4Domain;
            } else {
                String v6Domain = url.split("/")[0];
                return v6Domain;
            }
        }
    }

    //通用方法,传入url,返回file_path
    private String getFilePathFromUrl(String oriUrl) {
        String url = oriUrl.split("[?]")[0];
        if (url.contains("http://") || url.contains("https://")) {
            String filePath = url.split("//")[1];
            return filePath;
        } else {
            String filePath = url;
            return filePath;
        }
    }

    //判断是否为内网ip
    private boolean isInnerIP(String ipAddress) {
        if (ipAddress.equals("1.1.1.1") || ipAddress.equals("127.0.0.1") || ipAddress.equals("0.0.0.0")) {
            return true;
        }
        boolean isInnerIp = false;
        long ipNum = getIpNum(ipAddress);

        long aBegin = getIpNum("10.0.0.0");
        long aEnd = getIpNum("10.255.255.255");
        long bBegin = getIpNum("172.16.0.0");
        long bEnd = getIpNum("172.31.255.255");
        long cBegin = getIpNum("192.168.0.0");
        long cEnd = getIpNum("192.168.255.255");
        isInnerIp = isInner(ipNum, aBegin, aEnd) || isInner(ipNum, bBegin, bEnd) || isInner(ipNum, cBegin, cEnd);
        return isInnerIp;
    }

    private long getIpNum(String ipAddress) {
        String[] ip = ipAddress.split("\\.");
        long a = Integer.parseInt(ip[0]);
        long b = Integer.parseInt(ip[1]);
        long c = Integer.parseInt(ip[2]);
        long d = Integer.parseInt(ip[3]);

        long ipNum = a * 256 * 256 * 256 + b * 256 * 256 + c * 256 + d;
        return ipNum;
    }

    private boolean isInner(long userIp, long begin, long end) {
        return (userIp >= begin) && (userIp <= end);
    }

    //判断是否是一个IP
    private boolean isIp(String IP) {
        IP = this.removeBlank(IP);
        if (IP.length() < 7 || IP.length() > 15) {
            return false;
        }
        if (IP.contains(".")) {
            String[] arr = IP.split("\\.");
            if (arr.length != 4) {
                return false;
            }
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < arr[i].length(); j++) {
                    char temp = arr[i].charAt(j);
                    if (!(temp >= '0' && temp <= '9')) {
                        return false;
                    }
                }
            }
            for (int i = 0; i < 4; i++) {
                int temp = Integer.parseInt(arr[i]);
                if (temp < 0 || temp > 255) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    private String removeBlank(String IP) {
        while (IP.startsWith(" ")) {
            IP = IP.substring(1, IP.length()).trim();
        }
        while (IP.endsWith(" ")) {
            IP = IP.substring(0, IP.length() - 1).trim();
        }
        return IP;
    }

    //获取编码
    private String getCodingS(String contentStr) {
        List<String> list = new ArrayList<String>();
        Pattern pattern = Pattern.compile("a=rtpmap:(.*?)\\r\\n");
        Matcher m = pattern.matcher(contentStr);
        while (m.find()) {
            list.add(m.group(1).replace(" ", "").toLowerCase());
        }
        if (list.size() >= 1) {
            String[] strArray = list.toArray(new String[list.size()]);
            return Arrays.toString(strArray);
        } else {
            return null;
        }
    }

    /**
     * 针对性修改ser_domain返回值的格式问题,此处主要是处理To_ser_domain字段
     *
     * @param serDomainOrNull
     * @return
     */
    private String pointSerDomainPatch(String serDomainOrNull) {
        if (serDomainOrNull.contains(";")) {
            serDomainOrNull = serDomainOrNull.split(";")[0];
        }
        if (serDomainOrNull.contains("<sip:")) {
            serDomainOrNull = serDomainOrNull.split("<sip:")[0].replace("\"", "");
        }

        if (serDomainOrNull.contains("\r") || serDomainOrNull.contains("\n")) {
            serDomainOrNull = serDomainOrNull.split("[\\r\\n]")[0];
        }
        if (serDomainOrNull.contains("\\r") || serDomainOrNull.contains("\\n")) {
            serDomainOrNull = serDomainOrNull.split("[\\\\r\\\\n]")[0];
        }

        return serDomainOrNull.replace("+", "")
                .replace("[", "")
                .replace("]", "")
                .replace(" ", "")
                ;
    }

    /**
     * 修整 Res_stat_format 字段格式
     *
     * @param replaceBlank
     * @return
     */
    private String repairResStatFormatStr(String replaceBlank) {
        if (replaceBlank.contains(",")) {
            replaceBlank = replaceBlank.split(",")[0];
        } else if (replaceBlank.contains("'")) {
            replaceBlank = replaceBlank.split("'")[0];
            if (replaceBlank.contains("-")) {
                if (replaceBlank.indexOf("-") == (replaceBlank.length() - 1)) {
                    replaceBlank = replaceBlank.replace("-", "");
                }
            }
        } else if (replaceBlank.contains("(")) {
            replaceBlank = replaceBlank.split("[(]")[0];
        } else if (replaceBlank.contains("...")) {
            replaceBlank = replaceBlank.split("\\.\\.\\.")[0];
        } else if (replaceBlank.contains(".")) {
            replaceBlank = replaceBlank.split("[.]")[0];
        } else if (replaceBlank.contains("/")) {
            replaceBlank = replaceBlank.split("/")[0];
        } else if (replaceBlank.contains("[")) {
            replaceBlank = replaceBlank.split("\\[")[0];
        } else if (replaceBlank.contains("-")) {
            replaceBlank = replaceBlank.split("-")[0];
        } else if (replaceBlank.contains(":")) {
            replaceBlank = replaceBlank.split(":")[0];
        } else if (replaceBlank.contains(";")) {
            replaceBlank = replaceBlank.split(";")[0];
        } else if (replaceBlank.contains("!")) {
            replaceBlank = replaceBlank.split("[!]")[0];
        }

        return replaceBlank.replace(" ", "")
                .replace("-", "")
                .replace(",", "")
                .replace(".", "")
                .replace("[", "")
                .replace("]", "")
                .replace("/", "")
                .replace("!", "")
                .replace(";", "")
                .replace(":", "")
                .replace("@", "")
                ;
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        Map<String, Object> conf = new HashMap<String, Object>();
        conf.put(org.apache.storm.Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS,
                RealtimeCountConfig.TOPOLOGY_TICK_TUPLE_COMP_FREQ_SECS);
        return conf;
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("jsonLog", "logType"));
    }
}