package cn.ac.iie.common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class HashTableConfig implements Serializable {

    private static final long serialVersionUID = -6843770738516927321L;

    /**
     * ISO 3166-1 alpha2
     */
    public static final Map<String, String> ISO_3166_1_ALPHA_2 = new HashMap<String, String>() {

        //		private static final long serialVersionUID = 5231960246987011322L;
        private static final long serialVersionUID = -6972673762779232428L;

        {

            put("阿富汗","AF");
            put("奥兰","AX");
            put("阿尔巴尼亚","AL");
            put("阿尔及利亚","DZ");
            put("美属萨摩亚","AS");
            put("安道尔","AD");
            put("安哥拉","AO");
            put("安圭拉","AI");
            put("南极洲","AQ");
            put("安提瓜和巴布达","AG");
            put("阿根廷","AR");
            put("亚美尼亚","AM");
            put("阿鲁巴","AW");
            put("澳大利亚","AU");
            put("奥地利","AT");
            put("阿塞拜疆","AZ");
            put("巴哈马","BS");
            put("巴林","BH");
            put("孟加拉国","BD");
            put("巴巴多斯","BB");
            put("白俄罗斯","BY");
            put("比利时","BE");
            put("伯利兹","BZ");
            put("贝宁","BJ");
            put("百慕大","BM");
            put("不丹","BT");
            put("玻利维亚","BO");
            put("荷兰加勒比区","BQ");
            put("波斯尼亚和黑塞哥维那","BA");
            put("博茨瓦纳","BW");
            put("布韦岛","BV");
            put("巴西","BR");
            put("英属印度洋领地","IO");
            put("文莱","BN");
            put("保加利亚","BG");
            put("布基纳法索","BF");
            put("布隆迪","BI");
            put("佛得角","CV");
            put("柬埔寨","KH");
            put("喀麦隆","CM");
            put("加拿大","CA");
            put("开曼群岛","KY");
            put("中非","CF");
            put("乍得","TD");
            put("智利","CL");
            put("中国","CN");
            put("圣诞岛","CX");
            put("科科斯（基林）群岛","CC");
            put("哥伦比亚","CO");
            put("科摩罗","KM");
            put("刚果（布）","CG");
            put("刚果（金）","CD");
            put("库克群岛","CK");
            put("哥斯达黎加","CR");
            put("科特迪瓦","CI");
            put("克罗地亚","HR");
            put("古巴","CU");
            put("库拉索","CW");
            put("塞浦路斯","CY");
            put("捷克","CZ");
            put("丹麦","DK");
            put("吉布提","DJ");
            put("多米尼克","DM");
            put("多米尼加","DO");
            put("厄瓜多尔","EC");
            put("埃及","EG");
            put("萨尔瓦多","SV");
            put("赤道几内亚","GQ");
            put("厄立特里亚","ER");
            put("爱沙尼亚","EE");
            put("埃塞俄比亚","ET");
            put("福克兰群岛","FK");
            put("法罗群岛","FO");
            put("斐济","FJ");
            put("芬兰","FI");
            put("法国","FR");
            put("法属圭亚那","GF");
            put("法属波利尼西亚","PF");
            put("法属南方和南极洲领地","TF");
            put("加蓬","GA");
            put("冈比亚","GM");
            put("格鲁吉亚","GE");
            put("德国","DE");
            put("加纳","GH");
            put("直布罗陀","GI");
            put("希腊","GR");
            put("格陵兰","GL");
            put("格林纳达","GD");
            put("瓜德罗普","GP");
            put("关岛","GU");
            put("危地马拉","GT");
            put("根西","GG");
            put("几内亚","GN");
            put("几内亚比绍","GW");
            put("圭亚那","GY");
            put("海地","HT");
            put("赫德岛和麦克唐纳群岛","HM");
            put("梵蒂冈","VA");
            put("洪都拉斯","HN");
            put("香港","HK");
            put("匈牙利","HU");
            put("冰岛","IS");
            put("印度","IN");
            put("印尼","ID");
            put("伊朗","IR");
            put("伊拉克","IQ");
            put("爱尔兰","IE");
            put("马恩岛","IM");
            put("以色列","IL");
            put("意大利","IT");
            put("牙买加","JM");
            put("日本","JP");
            put("泽西","JE");
            put("约旦","JO");
            put("哈萨克斯坦","KZ");
            put("肯尼亚","KE");
            put("基里巴斯","KI");
            put("朝鲜","KP");
            put("韩国","KR");
            put("科威特","KW");
            put("吉尔吉斯斯坦","KG");
            put("老挝","LA");
            put("拉脱维亚","LV");
            put("黎巴嫩","LB");
            put("莱索托","LS");
            put("利比里亚","LR");
            put("利比亚","LY");
            put("列支敦士登","LI");
            put("立陶宛","LT");
            put("卢森堡","LU");
            put("澳门","MO");
            put("马其顿","MK");
            put("马达加斯加","MG");
            put("马拉维","MW");
            put("马来西亚","MY");
            put("马尔代夫","MV");
            put("马里","ML");
            put("马耳他","MT");
            put("马绍尔群岛","MH");
            put("马提尼克","MQ");
            put("毛里塔尼亚","MR");
            put("毛里求斯","MU");
            put("马约特","YT");
            put("墨西哥","MX");
            put("密克罗尼西亚联邦","FM");
            put("摩尔多瓦","MD");
            put("摩纳哥","MC");
            put("蒙古国","MN");
            put("黑山","ME");
            put("蒙特塞拉特","MS");
            put("摩洛哥","MA");
            put("莫桑比克","MZ");
            put("缅甸","MM");
            put("纳米比亚","NA");
            put("瑙鲁","NR");
            put("尼泊尔","NP");
            put("荷兰","NL");
            put("新喀里多尼亚","NC");
            put("新西兰","NZ");
            put("尼加拉瓜","NI");
            put("尼日尔","NE");
            put("尼日利亚","NG");
            put("纽埃","NU");
            put("诺福克岛","NF");
            put("北马里亚纳群岛","MP");
            put("挪威","NO");
            put("阿曼","OM");
            put("巴基斯坦","PK");
            put("帕劳","PW");
            put("巴勒斯坦","PS");
            put("巴拿马","PA");
            put("巴布亚新几内亚","PG");
            put("巴拉圭","PY");
            put("秘鲁","PE");
            put("菲律宾","PH");
            put("皮特凯恩群岛","PN");
            put("波兰","PL");
            put("葡萄牙","PT");
            put("波多黎各","PR");
            put("卡塔尔","QA");
            put("留尼汪","RE");
            put("罗马尼亚","RO");
            put("俄罗斯","RU");
            put("卢旺达","RW");
            put("圣巴泰勒米","BL");
            put("圣赫勒拿、阿森松和特里斯坦-达库尼亚","SH");
            put("圣基茨和尼维斯","KN");
            put("圣卢西亚","LC");
            put("法属圣马丁","MF");
            put("圣皮埃尔和密克隆","PM");
            put("圣文森特和格林纳丁斯","VC");
            put("萨摩亚","WS");
            put("圣马力诺","SM");
            put("圣多美和普林西比","ST");
            put("沙特阿拉伯","SA");
            put("塞内加尔","SN");
            put("塞尔维亚","RS");
            put("塞舌尔","SC");
            put("塞拉利昂","SL");
            put("新加坡","SG");
            put("圣马丁","SX");
            put("斯洛伐克","SK");
            put("斯洛文尼亚","SI");
            put("所罗门群岛","SB");
            put("索马里","SO");
            put("南非","ZA");
            put("南乔治亚和南桑威奇群岛","GS");
            put("南苏丹","SS");
            put("西班牙","ES");
            put("斯里兰卡","LK");
            put("苏丹","SD");
            put("苏里南","SR");
            put("斯瓦尔巴和扬马延","SJ");
            put("斯威士兰","SZ");
            put("瑞典","SE");
            put("瑞士","CH");
            put("叙利亚","SY");
            put("台湾","TW");
            put("塔吉克斯坦","TJ");
            put("坦桑尼亚","TZ");
            put("泰国","TH");
            put("东帝汶","TL");
            put("多哥","TG");
            put("托克劳","TK");
            put("汤加","TO");
            put("特立尼达和多巴哥","TT");
            put("突尼斯","TN");
            put("土耳其","TR");
            put("土库曼斯坦","TM");
            put("特克斯和凯科斯群岛","TC");
            put("图瓦卢","TV");
            put("乌干达","UG");
            put("乌克兰","UA");
            put("阿联酋","AE");
            put("英国","GB");
            put("美国","US");
            put("美国本土外小岛屿","UM");
            put("乌拉圭","UY");
            put("乌兹别克斯坦","UZ");
            put("瓦努阿图","VU");
            put("委内瑞拉","VE");
            put("越南","VN");
            put("英属维尔京群岛","VG");
            put("美属维尔京群岛","VI");
            put("瓦利斯和富图纳","WF");
            put("西撒哈拉","EH");
            put("也门","YE");
            put("赞比亚","ZM");
            put("津巴布韦","ZW");

        }
    };

    /**
     * 业务类型
     */
    public static final Map<String, Integer> SERVICE_TYPE_MAP = new HashMap<String, Integer>() {
        private static final long serialVersionUID = 8445342694006806126L;

        {
            put("DF-IP-PORT-LOG", 1);
            put("FX-IP-PORT", 2);
            put("DF-HTTP-REQ-LOG", 3);
            put("DF-HTTP-RES-LOG", 4);
            put("DF-HTTP-KEYWORD-LOG", 5);
            put("DF-DNS-LOG", 6);
            put("DF-PPTP-LOG", 7);
            put("DF-L2TP-LOG", 8);
            put("DF-IPSEC-LOG", 9);
            put("DF-OPENVPN-LOG", 10);
            put("DF-SSH-LOG", 11);
            put("DF-SSL-LOG", 12);
            put("DF-MAIL-LOG", 13);
            put("DF-FTP-LOG", 14);

            put("DJ-IP-PORT-LOG", 48);
            put("DJ-HTTP-REQ-LOG", 49);
            put("DJ-HTTP-RES-LOG", 50);
            put("DJ-HTTP-KEYWORD-LOG", 51);
            put("DJ-DNS-LOG", 52);
            put("DJ-FTP-LOG", 53);
            put("DJ-PPTP-LOG", 54);
            put("DJ-L2TP-LOG", 55);
            put("DJ-IPSEC-LOG", 56);
            put("DJ-OPENVPN-LOG", 57);
            put("DJ-SSH-LOG", 58);
            put("DJ-SSL-LOG", 59);
            put("DJ-MAIL-LOG", 60);
        }
    };

    //日志表字段数（不包括id，因为id前面不传回来）-----20181228修改
    public static final Map<String, Integer> LOG_SUB_OPTION_NUM_MAP = new HashMap<String, Integer>() {

        private static final long serialVersionUID = 5231960246987011322L;

        {
            put("DF-IP-PORT-LOG", 0);
            put("DF-HTTP-REQ-LOG", 12);
            put("DF-HTTP-RES-LOG", 15);
            put("DF-HTTP-KEYWORD-LOG", 10);
            put("DF-MAIL-LOG", 5);
            put("DF-DNS-LOG", 9);
            put("DF-FTP-LOG", 1);
            put("DF-PPTP-LOG", 3);
            put("DF-L2TP-LOG", 4);
            put("DF-IPSEC-LOG", 2);
            put("DF-OPENVPN-LOG", 4);
            put("DF-SSH-LOG", 6);
            put("DF-SSL-LOG", 6);
            put("DF-TUNNEL-RANDOM-LOG", 1);

            put("NTC-CONN-RECORD-LOG", 5);//正式用,这是表上的字段数
//			put("NTC-CONN-RECORD-LOG", 3);//测试用,这是测试数据上的字段数


            put("DJ-IP-PORT-LOG", 0);
            put("DJ-HTTP-REQ-LOG", 12);
            put("DJ-HTTP-RES-LOG", 15);
            put("DJ-HTTP-KEYWORD-LOG", 10);
            put("DJ-MAIL-LOG", 5);
            put("DJ-DNS-LOG", 9);
            put("DJ-FTP-LOG", 1);
            put("DJ-PPTP-LOG", 3);
            put("DJ-L2TP-LOG", 4);
            put("DJ-IPSEC-LOG", 2);
            put("DJ-OPENVPN-LOG", 4);
            put("DJ-SSH-LOG", 6);
            put("DJ-SSL-LOG", 6);

        }
    };

}