package com.yctc.zhiting.utils;

public class AreaCodeConstant {
    public static final String AREA_CODE = "[\n" +
            "    {\"cn\":\"中国\",\"en\":\"China\",\"code\":\"86\"},\n" +
            "             {\"cn\":\"中国香港\",\"en\":\"Hong Kong\",\"code\":\"852\"},\n" +
            "             {\"cn\":\"中国台湾\",\"en\":\"Taiwan\",\"code\":\"886\"},\n" +
            "             {\"cn\":\"阿富汗\",\"en\":\"Afghanistan\",\"code\":\"93\"},\n" +
            "             {\"cn\":\"阿尔巴尼亚\",\"en\":\"Albania\",\"code\":\"355\"},\n" +
            "             {\"cn\":\"阿尔及利亚\",\"en\":\"Algeria\",\"code\":\"213\"},\n" +
            "             {\"cn\":\"美属萨摩亚\",\"en\":\"American Samoa\",\"code\":\"684\"},\n" +
            "             {\"cn\":\"安道尔\",\"en\":\"Andorra\",\"code\":\"376\"},\n" +
            "             {\"cn\":\"安哥拉\",\"en\":\"Angola\",\"code\":\"244\"},\n" +
            "             {\"cn\":\"安圭拉\",\"en\":\"Anguilla\",\"code\":\"1264\"},\n" +
            "             {\"cn\":\"南极洲\",\"en\":\"Antarctica\",\"code\":\"672\"},\n" +
            "             {\"cn\":\"安提瓜和巴布达\",\"en\":\"Antigua and Barbuda\",\"code\":\"1268\"},\n" +
            "             {\"cn\":\"阿根廷\",\"en\":\"Argentina\",\"code\":\"54\"},\n" +
            "             {\"cn\":\"亚美尼亚\",\"en\":\"Armenia\",\"code\":\"374\"},\n" +
            "             {\"cn\":\"阿鲁巴\",\"en\":\"Aruba\",\"code\":\"297\"},\n" +
            "             {\"cn\":\"澳大利亚\",\"en\":\"Australia\",\"code\":\"61\"},\n" +
            "             {\"cn\":\"奥地利\",\"en\":\"Austria\",\"code\":\"43\"},\n" +
            "             {\"cn\":\"阿塞拜疆\",\"en\":\"Azerbaijan\",\"code\":\"994\"},\n" +
            "             {\"cn\":\"巴林\",\"en\":\"Bahrain\",\"code\":\"973\"},\n" +
            "             {\"cn\":\"孟加拉国\",\"en\":\"Bangladesh\",\"code\":\"880\"},\n" +
            "             {\"cn\":\"巴巴多斯\",\"en\":\"Barbados\",\"code\":\"1246\"},\n" +
            "             {\"cn\":\"白俄罗斯\",\"en\":\"Belarus\",\"code\":\"375\"},\n" +
            "             {\"cn\":\"比利时\",\"en\":\"Belgium\",\"code\":\"32\"},\n" +
            "             {\"cn\":\"伯利兹\",\"en\":\"Belize\",\"code\":\"501\"},\n" +
            "             {\"cn\":\"贝宁\",\"en\":\"Benin\",\"code\":\"229\"},\n" +
            "             {\"cn\":\"百慕大\",\"en\":\"Bermuda\",\"code\":\"1441\"},\n" +
            "             {\"cn\":\"不丹\",\"en\":\"Bhutan\",\"code\":\"975\"},\n" +
            "             {\"cn\":\"玻利维亚\",\"en\":\"Bolivia\",\"code\":\"591\"},\n" +
            "             {\"cn\":\"波黑\",\"en\":\"Bosnia and Herzegovina\",\"code\":\"387\"},\n" +
            "             {\"cn\":\"博茨瓦纳\",\"en\":\"Botswana\",\"code\":\"267\"},\n" +
            "             {\"cn\":\"巴西\",\"en\":\"Brazil\",\"code\":\"55\"},\n" +
            "             {\"cn\":\"英属维尔京群岛\",\"en\":\"British Virgin Islands\",\"code\":\"1284\"},\n" +
            "             {\"cn\":\"文莱\",\"en\":\"Brunei Darussalam\",\"code\":\"673\"},\n" +
            "             {\"cn\":\"保加利亚\",\"en\":\"Bulgaria\",\"code\":\"359\"},\n" +
            "             {\"cn\":\"布基纳法索\",\"en\":\"Burkina Faso\",\"code\":\"226\"},\n" +
            "             {\"cn\":\"缅甸\",\"en\":\"Burma\",\"code\":\"95\"},\n" +
            "             {\"cn\":\"布隆迪\",\"en\":\"Burundi\",\"code\":\"257\"},\n" +
            "             {\"cn\":\"柬埔寨\",\"en\":\"Cambodia\",\"code\":\"855\"},\n" +
            "             {\"cn\":\"喀麦隆\",\"en\":\"Cameroon\",\"code\":\"237\"},\n" +
            "             {\"cn\":\"加拿大\",\"en\":\"Canada\",\"code\":\"1\"},\n" +
            "             {\"cn\":\"佛得角\",\"en\":\"Cape Verde\",\"code\":\"238\"},\n" +
            "             {\"cn\":\"开曼群岛\",\"en\":\"Cayman Islands\",\"code\":\"1345\"},\n" +
            "             {\"cn\":\"中非\",\"en\":\"Central African Republic\",\"code\":\"236\"},\n" +
            "             {\"cn\":\"乍得\",\"en\":\"Chad\",\"code\":\"235\"},\n" +
            "             {\"cn\":\"智利\",\"en\":\"Chile\",\"code\":\"56\"},\n" +
            "             {\"cn\":\"圣诞岛\",\"en\":\"Christmas Island\",\"code\":\"61\"},\n" +
            "             {\"cn\":\"科科斯（基林）群岛\",\"en\":\"Cocos (Keeling) Islands\",\"code\":\"61\"},\n" +
            "             {\"cn\":\"哥伦比亚\",\"en\":\"Colombia\",\"code\":\"57\"},\n" +
            "             {\"cn\":\"科摩罗\",\"en\":\"Comoros\",\"code\":\"269\"},\n" +
            "             {\"cn\":\"刚果（金）\",\"en\":\"Democratic Republic of the Congo\",\"code\":\"243\"},\n" +
            "             {\"cn\":\"刚果（布）\",\"en\":\"Republic of the Congo\",\"code\":\"242\"},\n" +
            "             {\"cn\":\"库克群岛\",\"en\":\"Cook Islands\",\"code\":\"682\"},\n" +
            "             {\"cn\":\"哥斯达黎加\",\"en\":\"Costa Rica\",\"code\":\"506\"},\n" +
            "             {\"cn\":\"科特迪瓦\",\"en\":\"Cote d'Ivoire\",\"code\":\"225\"},\n" +
            "             {\"cn\":\"克罗地亚\",\"en\":\"Croatia\",\"code\":\"385\"},\n" +
            "             {\"cn\":\"古巴\",\"en\":\"Cuba\",\"code\":\"53\"},\n" +
            "             {\"cn\":\"塞浦路斯\",\"en\":\"Cyprus\",\"code\":\"357\"},\n" +
            "             {\"cn\":\"捷克\",\"en\":\"Czech Republic\",\"code\":\"420\"},\n" +
            "             {\"cn\":\"丹麦\",\"en\":\"Denmark\",\"code\":\"45\"},\n" +
            "             {\"cn\":\"吉布提\",\"en\":\"Djibouti\",\"code\":\"253\"},\n" +
            "             {\"cn\":\"多米尼克\",\"en\":\"Dominica\",\"code\":\"1767\"},\n" +
            "             {\"cn\":\"多米尼加\",\"en\":\"Dominican Republic\",\"code\":\"1809\"},\n" +
            "             {\"cn\":\"厄瓜多尔\",\"en\":\"Ecuador\",\"code\":\"593\"},\n" +
            "             {\"cn\":\"埃及\",\"en\":\"Egypt\",\"code\":\"20\"},\n" +
            "             {\"cn\":\"萨尔瓦多\",\"en\":\"El Salvador\",\"code\":\"503\"},\n" +
            "             {\"cn\":\"赤道几内亚\",\"en\":\"Equatorial Guinea\",\"code\":\"240\"},\n" +
            "             {\"cn\":\"厄立特里亚\",\"en\":\"Eritrea\",\"code\":\"291\"},\n" +
            "             {\"cn\":\"爱沙尼亚\",\"en\":\"Estonia\",\"code\":\"372\"},\n" +
            "             {\"cn\":\"埃塞俄比亚\",\"en\":\"Ethiopia\",\"code\":\"251\"},\n" +
            "             {\"cn\":\"福克兰群岛\",\"en\":\"Falkland Islands\",\"code\":\"500\"},\n" +
            "             {\"cn\":\"法罗群岛\",\"en\":\"Faroe Islands\",\"code\":\"298\"},\n" +
            "             {\"cn\":\"斐济\",\"en\":\"Fiji\",\"code\":\"679\"},\n" +
            "             {\"cn\":\"芬兰\",\"en\":\"Finland\",\"code\":\"358\"},\n" +
            "             {\"cn\":\"法国\",\"en\":\"France\",\"code\":\"33\"},\n" +
            "             {\"cn\":\"法属圭亚那\",\"en\":\"French Guiana\",\"code\":\"594\"},\n" +
            "             {\"cn\":\"法属波利尼西亚\",\"en\":\"French Polynesia\",\"code\":\"689\"},\n" +
            "             {\"cn\":\"加蓬\",\"en\":\"Gabon\",\"code\":\"241\"},\n" +
            "             {\"cn\":\"格鲁吉亚\",\"en\":\"Georgia\",\"code\":\"995\"},\n" +
            "             {\"cn\":\"德国\",\"en\":\"Germany\",\"code\":\"49\"},\n" +
            "             {\"cn\":\"加纳\",\"en\":\"Ghana\",\"code\":\"233\"},\n" +
            "             {\"cn\":\"直布罗陀\",\"en\":\"Gibraltar\",\"code\":\"350\"},\n" +
            "             {\"cn\":\"希腊\",\"en\":\"Greece\",\"code\":\"30\"},\n" +
            "             {\"cn\":\"格陵兰\",\"en\":\"Greenland\",\"code\":\"299\"},\n" +
            "             {\"cn\":\"格林纳达\",\"en\":\"Grenada\",\"code\":\"1473\"},\n" +
            "             {\"cn\":\"瓜德罗普\",\"en\":\"Guadeloupe\",\"code\":\"590\"},\n" +
            "             {\"cn\":\"关岛\",\"en\":\"Guam\",\"code\":\"1671\"},\n" +
            "             {\"cn\":\"危地马拉\",\"en\":\"Guatemala\",\"code\":\"502\"},\n" +
            "             {\"cn\":\"根西岛\",\"en\":\"Guernsey\",\"code\":\"1481\"},\n" +
            "             {\"cn\":\"几内亚\",\"en\":\"Guinea\",\"code\":\"224\"},\n" +
            "             {\"cn\":\"几内亚比绍\",\"en\":\"Guinea-Bissau\",\"code\":\"245\"},\n" +
            "             {\"cn\":\"圭亚那\",\"en\":\"Guyana\",\"code\":\"592\"},\n" +
            "             {\"cn\":\"海地\",\"en\":\"Haiti\",\"code\":\"509\"},\n" +
            "             {\"cn\":\"梵蒂冈\",\"en\":\"Holy See (Vatican City)\",\"code\":\"379\"},\n" +
            "             {\"cn\":\"洪都拉斯\",\"en\":\"Honduras\",\"code\":\"504\"},\n" +
            "             {\"cn\":\"匈牙利\",\"en\":\"Hungary\",\"code\":\"36\"},\n" +
            "             {\"cn\":\"冰岛\",\"en\":\"Iceland\",\"code\":\"354\"},\n" +
            "             {\"cn\":\"印度\",\"en\":\"India\",\"code\":\"91\"},\n" +
            "             {\"cn\":\"印度尼西亚\",\"en\":\"Indonesia\",\"code\":\"62\"},\n" +
            "             {\"cn\":\"伊朗\",\"en\":\"Iran\",\"code\":\"98\"},\n" +
            "             {\"cn\":\"伊拉克\",\"en\":\"Iraq\",\"code\":\"964\"},\n" +
            "             {\"cn\":\"爱尔兰\",\"en\":\"Ireland\",\"code\":\"353\"},\n" +
            "             {\"cn\":\"以色列\",\"en\":\"Israel\",\"code\":\"972\"},\n" +
            "             {\"cn\":\"意大利\",\"en\":\"Italy\",\"code\":\"39\"},\n" +
            "             {\"cn\":\"牙买加\",\"en\":\"Jamaica\",\"code\":\"1876\"},\n" +
            "             {\"cn\":\"日本\",\"en\":\"Japan\",\"code\":\"81\"},\n" +
            "             {\"cn\":\"约旦\",\"en\":\"Jordan\",\"code\":\"962\"},\n" +
            "             {\"cn\":\"哈萨克斯坦\",\"en\":\"Kazakhstan\",\"code\":\"73\"},\n" +
            "             {\"cn\":\"肯尼亚\",\"en\":\"Kenya\",\"code\":\"254\"},\n" +
            "             {\"cn\":\"基里巴斯\",\"en\":\"Kiribati\",\"code\":\"686\"},\n" +
            "             {\"cn\":\"朝鲜\",\"en\":\"North Korea\",\"code\":\"850\"},\n" +
            "             {\"cn\":\"韩国\",\"en\":\"South Korea\",\"code\":\"82\"},\n" +
            "             {\"cn\":\"科威特\",\"en\":\"Kuwait\",\"code\":\"965\"},\n" +
            "             {\"cn\":\"吉尔吉斯斯坦\",\"en\":\"Kyrgyzstan\",\"code\":\"996\"},\n" +
            "             {\"cn\":\"老挝\",\"en\":\"Laos\",\"code\":\"856\"},\n" +
            "             {\"cn\":\"拉脱维亚\",\"en\":\"Latvia\",\"code\":\"371\"},\n" +
            "             {\"cn\":\"黎巴嫩\",\"en\":\"Lebanon\",\"code\":\"961\"},\n" +
            "             {\"cn\":\"莱索托\",\"en\":\"Lesotho\",\"code\":\"266\"},\n" +
            "             {\"cn\":\"利比里亚\",\"en\":\"Liberia\",\"code\":\"231\"},\n" +
            "             {\"cn\":\"利比亚\",\"en\":\"Libya\",\"code\":\"218\"},\n" +
            "             {\"cn\":\"列支敦士登\",\"en\":\"Liechtenstein\",\"code\":\"423\"},\n" +
            "             {\"cn\":\"立陶宛\",\"en\":\"Lithuania\",\"code\":\"370\"},\n" +
            "             {\"cn\":\"卢森堡\",\"en\":\"Luxembourg\",\"code\":\"352\"},\n" +
            "             {\"cn\":\"澳门\",\"en\":\"Macao\",\"code\":\"853\"},\n" +
            "             {\"cn\":\"前南马其顿\",\"en\":\"The Former Yugoslav Republic of Macedonia\",\"code\":\"389\"},\n" +
            "             {\"cn\":\"马达加斯加\",\"en\":\"Madagascar\",\"code\":\"261\"},\n" +
            "             {\"cn\":\"马拉维\",\"en\":\"Malawi\",\"code\":\"265\"},\n" +
            "             {\"cn\":\"马来西亚\",\"en\":\"Malaysia\",\"code\":\"60\"},\n" +
            "             {\"cn\":\"马尔代夫\",\"en\":\"Maldives\",\"code\":\"960\"},\n" +
            "             {\"cn\":\"马里\",\"en\":\"Mali\",\"code\":\"223\"},\n" +
            "             {\"cn\":\"马耳他\",\"en\":\"Malta\",\"code\":\"356\"},\n" +
            "             {\"cn\":\"马绍尔群岛\",\"en\":\"Marshall Islands\",\"code\":\"692\"},\n" +
            "             {\"cn\":\"马提尼克\",\"en\":\"Martinique\",\"code\":\"596\"},\n" +
            "             {\"cn\":\"毛里塔尼亚\",\"en\":\"Mauritania\",\"code\":\"222\"},\n" +
            "             {\"cn\":\"毛里求斯\",\"en\":\"Mauritius\",\"code\":\"230\"},\n" +
            "             {\"cn\":\"马约特\",\"en\":\"Mayotte\",\"code\":\"269\"},\n" +
            "             {\"cn\":\"墨西哥\",\"en\":\"Mexico\",\"code\":\"52\"},\n" +
            "             {\"cn\":\"密克罗尼西亚\",\"en\":\"Federated States of Micronesia\",\"code\":\"691\"},\n" +
            "             {\"cn\":\"摩尔多瓦\",\"en\":\"Moldova\",\"code\":\"373\"},\n" +
            "             {\"cn\":\"摩纳哥\",\"en\":\"Monaco\",\"code\":\"377\"},\n" +
            "             {\"cn\":\"蒙古\",\"en\":\"Mongolia\",\"code\":\"976\"},\n" +
            "             {\"cn\":\"蒙特塞拉特\",\"en\":\"Montserrat\",\"code\":\"1664\"},\n" +
            "             {\"cn\":\"摩洛哥\",\"en\":\"Morocco\",\"code\":\"212\"},\n" +
            "             {\"cn\":\"莫桑比克\",\"en\":\"Mozambique\",\"code\":\"258\"},\n" +
            "             {\"cn\":\"纳米尼亚\",\"en\":\"Namibia\",\"code\":\"264\"},\n" +
            "             {\"cn\":\"瑙鲁\",\"en\":\"Nauru\",\"code\":\"674\"},\n" +
            "             {\"cn\":\"尼泊尔\",\"en\":\"Nepal\",\"code\":\"977\"},\n" +
            "             {\"cn\":\"荷兰\",\"en\":\"Netherlands\",\"code\":\"31\"},\n" +
            "             {\"cn\":\"荷属安的列斯\",\"en\":\"Netherlands Antilles\",\"code\":\"599\"},\n" +
            "             {\"cn\":\"新喀里多尼亚\",\"en\":\"New Caledonia\",\"code\":\"687\"},\n" +
            "             {\"cn\":\"新西兰\",\"en\":\"New Zealand\",\"code\":\"64\"},\n" +
            "             {\"cn\":\"尼加拉瓜\",\"en\":\"Nicaragua\",\"code\":\"505\"},\n" +
            "             {\"cn\":\"尼日尔\",\"en\":\"Niger\",\"code\":\"227\"},\n" +
            "             {\"cn\":\"尼日利亚\",\"en\":\"Nigeria\",\"code\":\"234\"},\n" +
            "             {\"cn\":\"纽埃\",\"en\":\"Niue\",\"code\":\"683\"},\n" +
            "             {\"cn\":\"诺福克岛\",\"en\":\"Norfolk Island\",\"code\":\"6723\"},\n" +
            "             {\"cn\":\"北马里亚纳\",\"en\":\"Northern Mariana Islands\",\"code\":\"1\"},\n" +
            "             {\"cn\":\"挪威\",\"en\":\"Norway\",\"code\":\"47\"},\n" +
            "             {\"cn\":\"阿曼\",\"en\":\"Oman\",\"code\":\"968\"},\n" +
            "             {\"cn\":\"巴基斯坦\",\"en\":\"Pakistan\",\"code\":\"92\"},\n" +
            "             {\"cn\":\"帕劳\",\"en\":\"Palau\",\"code\":\"680\"},\n" +
            "             {\"cn\":\"巴拿马\",\"en\":\"Panama\",\"code\":\"507\"},\n" +
            "             {\"cn\":\"巴布亚新几内亚\",\"en\":\"Papua New Guinea\",\"code\":\"675\"},\n" +
            "             {\"cn\":\"巴拉圭\",\"en\":\"Paraguay\",\"code\":\"595\"},\n" +
            "             {\"cn\":\"秘鲁\",\"en\":\"Peru\",\"code\":\"51\"},\n" +
            "             {\"cn\":\"菲律宾\",\"en\":\"Philippines\",\"code\":\"63\"},\n" +
            "             {\"cn\":\"波兰\",\"en\":\"Poland\",\"code\":\"48\"},\n" +
            "             {\"cn\":\"葡萄牙\",\"en\":\"Portugal\",\"code\":\"351\"},\n" +
            "             {\"cn\":\"波多黎各\",\"en\":\"Puerto Rico\",\"code\":\"1809\"},\n" +
            "             {\"cn\":\"卡塔尔\",\"en\":\"Qatar\",\"code\":\"974\"},\n" +
            "             {\"cn\":\"留尼汪\",\"en\":\"Reunion\",\"code\":\"262\"},\n" +
            "             {\"cn\":\"罗马尼亚\",\"en\":\"Romania\",\"code\":\"40\"},\n" +
            "             {\"cn\":\"俄罗斯\",\"en\":\"Russia\",\"code\":\"7\"},\n" +
            "             {\"cn\":\"卢旺达\",\"en\":\"Rwanda\",\"code\":\"250\"},\n" +
            "             {\"cn\":\"圣赫勒拿\",\"en\":\"Saint Helena\",\"code\":\"290\"},\n" +
            "             {\"cn\":\"圣基茨和尼维斯\",\"en\":\"Saint Kitts and Nevis\",\"code\":\"1869\"},\n" +
            "             {\"cn\":\"圣卢西亚\",\"en\":\"Saint Lucia\",\"code\":\"1758\"},\n" +
            "             {\"cn\":\"圣皮埃尔和密克隆\",\"en\":\"Saint Pierre and Miquelon\",\"code\":\"508\"},\n" +
            "             {\"cn\":\"圣文森特和格林纳丁斯\",\"en\":\"Saint Vincent and the Grenadines\",\"code\":\"1784\"},\n" +
            "             {\"cn\":\"萨摩亚\",\"en\":\"Samoa\",\"code\":\"685\"},\n" +
            "             {\"cn\":\"圣马力诺\",\"en\":\"San Marino\",\"code\":\"378\"},\n" +
            "             {\"cn\":\"圣多美和普林西比\",\"en\":\"Sao Tome and Principe\",\"code\":\"239\"},\n" +
            "             {\"cn\":\"沙特阿拉伯\",\"en\":\"Saudi Arabia\",\"code\":\"966\"},\n" +
            "             {\"cn\":\"塞内加尔\",\"en\":\"Senegal\",\"code\":\"221\"},\n" +
            "             {\"cn\":\"塞尔维亚和黑山\",\"en\":\"Serbia and Montenegro\",\"code\":\"381\"},\n" +
            "             {\"cn\":\"塞舌尔\",\"en\":\"Seychelles\",\"code\":\"248\"},\n" +
            "             {\"cn\":\"塞拉利\",\"en\":\"Sierra Leone\",\"code\":\"232\"},\n" +
            "             {\"cn\":\"新加坡\",\"en\":\"Singapore\",\"code\":\"65\"},\n" +
            "             {\"cn\":\"斯洛伐克\",\"en\":\"Slovakia\",\"code\":\"421\"},\n" +
            "             {\"cn\":\"斯洛文尼亚\",\"en\":\"Slovenia\",\"code\":\"386\"},\n" +
            "             {\"cn\":\"所罗门群岛\",\"en\":\"Solomon Islands\",\"code\":\"677\"},\n" +
            "             {\"cn\":\"索马里\",\"en\":\"Somalia\",\"code\":\"252\"},\n" +
            "             {\"cn\":\"南非\",\"en\":\"South Africa\",\"code\":\"27\"},\n" +
            "             {\"cn\":\"西班牙\",\"en\":\"Spain\",\"code\":\"34\"},\n" +
            "             {\"cn\":\"斯里兰卡\",\"en\":\"Sri Lanka\",\"code\":\"94\"},\n" +
            "             {\"cn\":\"苏丹\",\"en\":\"Sudan\",\"code\":\"249\"},\n" +
            "             {\"cn\":\"苏里南\",\"en\":\"Suriname\",\"code\":\"597\"},\n" +
            "             {\"cn\":\"斯瓦尔巴岛和扬马延岛\",\"en\":\"Svalbard\",\"code\":\"47\"},\n" +
            "             {\"cn\":\"斯威士兰\",\"en\":\"Swaziland\",\"code\":\"268\"},\n" +
            "             {\"cn\":\"瑞典\",\"en\":\"Sweden\",\"code\":\"46\"},\n" +
            "             {\"cn\":\"瑞士\",\"en\":\"Switzerland\",\"code\":\"41\"},\n" +
            "             {\"cn\":\"叙利亚\",\"en\":\"Syria\",\"code\":\"963\"},\n" +
            "             {\"cn\":\"塔吉克斯坦\",\"en\":\"Tajikistan\",\"code\":\"992\"},\n" +
            "             {\"cn\":\"坦桑尼亚\",\"en\":\"Tanzania\",\"code\":\"255\"},\n" +
            "             {\"cn\":\"泰国\",\"en\":\"Thailand\",\"code\":\"66\"},\n" +
            "             {\"cn\":\"巴哈马\",\"en\":\"The Bahamas\",\"code\":\"1242\"},\n" +
            "             {\"cn\":\"冈比亚\",\"en\":\"The Gambia\",\"code\":\"220\"},\n" +
            "             {\"cn\":\"多哥\",\"en\":\"Togo\",\"code\":\"228\"},\n" +
            "             {\"cn\":\"托克劳\",\"en\":\"Tokelau\",\"code\":\"690\"},\n" +
            "             {\"cn\":\"汤加\",\"en\":\"Tonga\",\"code\":\"676\"},\n" +
            "             {\"cn\":\"特立尼达和多巴哥\",\"en\":\"Trinidad and Tobago\",\"code\":\"1868\"},\n" +
            "             {\"cn\":\"突尼斯\",\"en\":\"Tunisia\",\"code\":\"216\"},\n" +
            "             {\"cn\":\"土耳其\",\"en\":\"Turkey\",\"code\":\"90\"},\n" +
            "             {\"cn\":\"土库曼斯坦\",\"en\":\"Turkmenistan\",\"code\":\"993\"},\n" +
            "             {\"cn\":\"特克斯和凯科斯群岛\",\"en\":\"Turks and Caicos Islands\",\"code\":\"1649\"},\n" +
            "             {\"cn\":\"图瓦卢\",\"en\":\"Tuvalu\",\"code\":\"688\"},\n" +
            "             {\"cn\":\"乌干达\",\"en\":\"Uganda\",\"code\":\"256\"},\n" +
            "             {\"cn\":\"乌克兰\",\"en\":\"Ukraine\",\"code\":\"380\"},\n" +
            "             {\"cn\":\"阿拉伯联合酋长国\",\"en\":\"United Arab Emirates\",\"code\":\"971\"},\n" +
            "             {\"cn\":\"英国\",\"en\":\"United Kingdom\",\"code\":\"44\"},\n" +
            "             {\"cn\":\"美国\",\"en\":\"United States\",\"code\":\"1\"},\n" +
            "             {\"cn\":\"乌拉圭\",\"en\":\"Uruguay\",\"code\":\"598\"},\n" +
            "             {\"cn\":\"乌兹别克斯坦\",\"en\":\"Uzbekistan\",\"code\":\"998\"},\n" +
            "             {\"cn\":\"瓦努阿图\",\"en\":\"Vanuatu\",\"code\":\"678\"},\n" +
            "             {\"cn\":\"委内瑞拉\",\"en\":\"Venezuela\",\"code\":\"58\"},\n" +
            "             {\"cn\":\"越南\",\"en\":\"Vietnam\",\"code\":\"84\"},\n" +
            "             {\"cn\":\"美属维尔京群岛\",\"en\":\"Virgin Islands\",\"code\":\"1340\"},\n" +
            "             {\"cn\":\"瓦利斯和富图纳\",\"en\":\"Wallis and Futuna\",\"code\":\"681\"},\n" +
            "             {\"cn\":\"也门\",\"en\":\"Yemen\",\"code\":\"967\"},\n" +
            "             {\"cn\":\"赞比亚\",\"en\":\"Zambia\",\"code\":\"260\"},\n" +
            "             {\"cn\":\"津巴布韦\",\"en\":\"Zimbabwe\",\"code\":\"263\"},\n" +
            "             {\"cn\":\"南苏丹\",\"en\":\"South Sudan\",\"code\":\"211\"}\n" +
            "    ]";
}
