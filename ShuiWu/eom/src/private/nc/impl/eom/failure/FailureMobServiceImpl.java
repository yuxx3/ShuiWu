package nc.impl.eom.failure;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nc.impl.am.bill.BillBaseDAO;
import nc.impl.am.common.InSqlManager;
import nc.impl.am.pflow.PfUtilPrivate;
import nc.itf.am.pub.IOrgPubService;
import nc.itf.am.pub.ITransiRuleService;
import nc.itf.eom.prv.IFailureMobService;
import nc.vo.am.common.BizContext;
import nc.vo.am.common.util.AMMobUtils;
import nc.vo.am.common.util.ArrayUtils;
import nc.vo.am.common.util.MapUtils;
import nc.vo.am.common.util.OrgUtils;
import nc.vo.am.proxy.AMProxy;
import nc.vo.eom.failure.FailureBodyVO;
import nc.vo.eom.failure.FailureHeadVO;
import nc.vo.eom.failure.FailureVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.util.VisibleUtil;

import org.granite.lang.util.Strings;

public class FailureMobServiceImpl extends BillBaseDAO<FailureVO>
  implements IFailureMobService
{
  private static String FILEPATH = "eom/failure/";
  private static String PK_EQUIP = "pk_equip";
  private static String EQUIP_NAME = "equip_name";
  private static String EQUIP_CODE = "equip_code";
  private static String PK_CATEGORY = "pk_category";
  private static String RESULTCODE = "resultcode";
  private static String RESULTMSG = "resultmsg";
  private static String ERR_EQUIP = "errEquip";
  private final String LOCATION_CALSS_ID = "e618020b-0a93-4b41-8e35-cc861286edc7";
  private final String TYPE_CLASS_ID = "dff57e94-b6a1-4067-9567-decd3d1f2b3f";
  private final String SYMPTOM_CLASS_ID = "5e815d43-fec0-4cdb-9a0e-504cda468217";
  private final String REASON_CLASS_ID = "b9569f07-4f50-448d-8a55-58eb34b271d7";

  public Map<String, Object> queryTodayFill(String groupid, String userid)
    throws BusinessException
  {
    AMMobUtils.setContextLoginInfo(groupid, userid);
    String billmaker = BizContext.getInstance().getUserId();

    Map returnMap = new LinkedHashMap();
    Date currentTime = new Date();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    String dateString = formatter.format(currentTime);
    StringBuffer sql = new StringBuffer();
    //sql.append(" select isnull(equip_code, location_code) as equip_code, ").append(" isnull(equip_name, location_name) as equip_name, symptom_name,").append(" failure_time,eom_failure_b.pk_failure_b,eom_failure.billmaketime ").append(" from eom_failure_b left join pam_equip on pam_equip.pk_equip= eom_failure_b.pk_equip ").append(" left join pam_location on pam_location.pk_location = eom_failure_b.pk_location ").append(" inner join pam_failure_symptom on pam_failure_symptom.pk_failure_symptom = eom_failure_b.pk_failure_symptom ").append(" inner join eom_failure on eom_failure.pk_failure= eom_failure_b.pk_failure ").append(" where eom_failure.dr=0 and eom_failure_b.dr= 0  ").append(" and eom_failure.billmaker= '").append(billmaker).append("' ").append(" and eom_failure.billmaketime like ").append(" '").append(dateString).append("%'").append(" order by eom_failure.billmaketime desc ");
    sql.append(" select isnull(equip_code, location_code) as equip_code, ")
    	.append(" isnull(equip_name, location_name) as equip_name, symptom_name,")
    	.append(" failure_time,eom_failure_b.pk_failure_b,eom_failure.billmaketime ")
    	.append(" from eom_failure_b left join pam_equip on pam_equip.pk_equip= eom_failure_b.pk_equip ")
    	.append(" left join pam_location on pam_location.pk_location = eom_failure_b.pk_location ")
    	.append(" inner join pam_failure_symptom on pam_failure_symptom.pk_failure_symptom = eom_failure_b.pk_failure_symptom ")
    	.append(" inner join eom_failure on eom_failure.pk_failure= eom_failure_b.pk_failure ")
    	.append(" where eom_failure.dr=0 and eom_failure_b.dr= 0  ")
    	.append(" and eom_failure.billmaker= '")
    	.append(billmaker).append("' ")
    	//.append(" and eom_failure.billmaketime like ")
    	.append("and to_date('"+dateString+"','yyyy-MM-dd')")
    	.append(" - to_date(substr(eom_failure.billmaketime,0,10), 'yyyy-MM-dd') <= 7")
    	//.append(" '").append(dateString).append("%'")
    	.append(" order by eom_failure.billmaketime desc ");

    List todaylist = AMMobUtils.mulTableLinkQuery4SQL(sql.toString(), new String[] { EQUIP_CODE, EQUIP_NAME, "symptom_name", "failure_time", "pk_failure_b", "billmaketime" });

    for (Iterator i$ = todaylist.iterator(); i$.hasNext(); ) { Object object = i$.next();
      Map tempMap = new HashMap();
      tempMap.putAll((Map)object);
      returnMap.put(tempMap.get("pk_failure_b") + "", tempMap);
    }
    return returnMap;
  }

  public Map<String, Object> queryFailureDetail(String groupid, String userid, String pk_failure)
    throws BusinessException
  {
    Map retMap = new HashMap();

    if (judgeBillStatusReturnErro(pk_failure) == true) {
      return returnErrorInfo("您查看的今日填报记录已删除，请返回重新查看");
    }

    Map equipCard = queryFailureDetailInfo(pk_failure);

    Map subTableInfo = querySubTableInfo(pk_failure);

    Map failureLocation = queryFailureLocation(pk_failure);

    Map typeSympReason = queryFailureTypeSympReason(pk_failure);

    Map pictureMap = queryFailurePicture(pk_failure, FILEPATH);

    retMap.putAll(equipCard);
    retMap.putAll(subTableInfo);
    retMap.putAll(failureLocation);
    retMap.putAll(typeSympReason);
    retMap.putAll(pictureMap);
    return retMap;
  }

  private Map<String, Object> queryFailureDetailInfo(String pk_failure_b)
    throws BusinessException
  {
    StringBuffer sql = new StringBuffer();
    sql.append(" from pam_equip a,eom_failure_b b where").append(" a.pk_equip in ( select pk_equip from eom_failure_b) and ").append(" a.pk_equip=b.pk_equip  and pk_failure_b =").append(" '").append(pk_failure_b).append("' ");

    List tempList = AMMobUtils.mulTableLinkQuerySQL(sql.toString(), new String[] { EQUIP_CODE, EQUIP_NAME });

    Map retMap = new HashMap();
    for (Iterator i$ = tempList.iterator(); i$.hasNext(); ) { Object object = i$.next();
      retMap.putAll((Map)object);
    }
    return retMap;
  }

  private boolean judgeBillStatusReturnErro(String pk_failure_b)
    throws BusinessException
  {
    StringBuffer sql = new StringBuffer();
    sql.append(" from eom_failure,eom_failure_b where ").append(" eom_failure_b.pk_failure=eom_failure.pk_failure ").append(" and eom_failure_b.pk_failure_b= '").append(pk_failure_b).append("' ").append(" and eom_failure.dr=0 ");

    List billList = AMMobUtils.mulTableLinkQuerySQL(sql.toString(), new String[] { "bill_status" });

    return (billList.size() == 0) || (billList == null);
  }

  public Map<String, Object> queryEquipCardInfo(String groupid, String userid, String equip_code)
    throws BusinessException
  {
    String pk_org = AMMobUtils.getDefaultOrgUnit(userid, groupid);
    StringBuffer sql = new StringBuffer();
    sql.append(" select a.pk_equip,a.equip_name,a.equip_code, ").append(" b.pk_location,b.location_code,b.location_name ").append(" from pam_equip a left join pam_location b on ").append(" a.pk_location = b.pk_location where ").append(" pk_equip in ").append(" ( ").append(" select pk_equip from pam_equip ").append(" where ( equip_code= '").append(equip_code).append("' ").append("or bar_code = '").append(equip_code).append("')  ").append(" and a.dr=0 and pk_usedorg= '").append(pk_org).append("' ").append("and a.card_status= ").append(3).append(" ) ");

    List equipList = AMMobUtils.mulTableLinkQuery4SQL(sql.toString(), new String[] { PK_EQUIP, EQUIP_NAME, EQUIP_CODE, "pk_location", "location_code", "location_name" });

    if ((AMMobUtils.isNull(equipList)) || (equipList.size() == 0)) {
      return returnErrorInfo("没有找到相关设备信息。");
    }
    Map retMap = new HashMap();
    for (Iterator i$ = equipList.iterator(); i$.hasNext(); ) { Object object = i$.next();
      retMap.putAll((Map)object);
    }
    return retMap;
  }

  public Map<String, Object> queryLocationInfo(String groupid, String userid, String location_code)
    throws BusinessException
  {
    String pk_org = AMMobUtils.getDefaultOrgUnit(userid, groupid);
    StringBuffer sql = new StringBuffer();
    sql.append(" from pam_location where pk_location in ( ").append(" select pk_location from pam_location where ").append(" location_code= '").append(location_code).append("' ");

    if (!getVisibleByMetaId(groupid, userid, "e618020b-0a93-4b41-8e35-cc861286edc7").toString().equals("null"))
    {
      sql.append(" and ");

      sql.append(getVisibleByMetaId(groupid, userid, "e618020b-0a93-4b41-8e35-cc861286edc7"));
    }
    sql.append(" and dr=0 and ").append("enablestate").append(" = ").append(2).append(" ) ");

    List locationList = AMMobUtils.mulTableLinkQuerySQL(sql.toString(), new String[] { "location_code", "location_name", "pk_location" });

    if ((AMMobUtils.isNull(locationList)) || (locationList.size() == 0)) {
      return returnErrorInfo("没有找到相关位置信息。");
    }
    Map locationMap = new HashMap();
    for (Iterator i$ = locationList.iterator(); i$.hasNext(); ) { Object object = i$.next();
      if (object != null) {
        locationMap.putAll((Map)object);
      }
    }
    return locationMap;
  }

  private Map<String, Object> querySubTableInfo(String pk_failure_b)
    throws BusinessException
  {
    StringBuffer sql = new StringBuffer();
    sql.append(" from ").append(" eom_failure_b ").append(" where ").append(" pk_failure_b ").append(" = ").append("'").append(pk_failure_b).append("'");

    List tempList = AMMobUtils.mulTableLinkQuerySQL(sql.toString(), new String[] { "failure_time", "restore_time", "failure_expound", "memo" });

    Map retMap = new HashMap();
    for (Iterator i$ = tempList.iterator(); i$.hasNext(); ) { Object object = i$.next();
      retMap.putAll((Map)object);
    }

    for (Object key : retMap.keySet()) {
      if ((retMap.get(key) == null) || (AMMobUtils.isNull(retMap.get(key)))) {
        retMap.put(key, "");
      }
    }
    return retMap;
  }

  private Map<String, Object> queryFailureLocation(String pk_failure_b)
    throws BusinessException
  {
    StringBuffer sql = new StringBuffer();
    sql.append(" from ").append(" pam_location ").append(" where ").append(" pk_location ").append(" in ").append(" ( ").append(" select ").append(" pk_location ").append(" FROM ").append(" eom_failure_b ").append(" where ").append(" pk_failure_b ").append(" = ").append("'").append(pk_failure_b).append("'").append(" ) ");

    List tempList = AMMobUtils.mulTableLinkQuerySQL(sql.toString(), new String[] { "location_code", "location_name" });

    Map retMap = new HashMap();
    for (Iterator i$ = tempList.iterator(); i$.hasNext(); ) { Object object = i$.next();
      retMap.putAll((Map)object);
    }
    return retMap;
  }

  private Map<String, Object> queryFailureTypeSympReason(String pk_failure_b)
    throws BusinessException
  {
    Map returnMap = new HashMap();

    Map failureTypeMap = queryFailureTypeInfo(pk_failure_b);
    Map failureSympMap = queryFailureSymptomInfo(pk_failure_b);
    Map ReasonMap = queryFailureReasonInfo(pk_failure_b);
    returnMap.putAll(failureTypeMap);
    returnMap.putAll(failureSympMap);
    returnMap.putAll(ReasonMap);
    return returnMap;
  }

  private Map<String, Object> queryFailurePicture(String pk_failure_b, String filePath)
    throws BusinessException
  {
    StringBuffer sql = new StringBuffer();

    sql.append(" from eom_failure_b where pk_failure_b ='").append(pk_failure_b).append("'  ");

    List pkList = AMMobUtils.mulTableLinkQuerySQL(sql.toString(), new String[] { "pk_failure" });

    Map pkMap = (Map)pkList.get(0);
    String pk_failure = (String)pkMap.get("pk_failure");
    Map retPicture = AMMobUtils.queryPicturesBybillPK(pk_failure, FILEPATH);
    Map retMap = new HashMap();
    List list = (List)retPicture.get("picturelist");
    retMap.put("picturecount", Integer.valueOf(list.size()));
    retMap.put("pk_failure", pk_failure);
    retMap.put("filepath", FILEPATH);
    return retMap;
  }

  private Map<String, Object> queryFailureTypeInfo(String pk_failure_b)
    throws BusinessException
  {
    StringBuffer sql = new StringBuffer();
    Map retMap = new HashMap();
    sql.append(" from pam_failure_type  where pk_failure_type in ").append(" (select pk_failure_type from eom_failure_b where pk_failure_b= '").append(pk_failure_b).append("') ");

    List tempList = AMMobUtils.mulTableLinkQuerySQL(sql.toString(), new String[] { "type_name" });

    for (Iterator i$ = tempList.iterator(); i$.hasNext(); ) { Object object = i$.next();
      retMap.putAll((Map)object);
    }
    return retMap;
  }

  private Map<String, Object> queryFailureSymptomInfo(String pk_failure_b)
    throws BusinessException
  {
    StringBuffer sql = new StringBuffer();
    Map retMap = new HashMap();
    sql.append(" from pam_failure_symptom  where pk_failure_symptom in ").append(" (select pk_failure_symptom from eom_failure_b where pk_failure_b= '").append(pk_failure_b).append("') ");

    List tempList = AMMobUtils.mulTableLinkQuerySQL(sql.toString(), new String[] { "symptom_name" });

    for (Iterator i$ = tempList.iterator(); i$.hasNext(); ) { Object object = i$.next();
      retMap.putAll((Map)object);
    }
    return retMap;
  }

  private Map<String, Object> queryFailureReasonInfo(String pk_failure_b)
    throws BusinessException
  {
    StringBuffer sql = new StringBuffer();
    Map retMap = new HashMap();

    sql.append(" from pam_failure_reason  where pk_failure_reason in ").append(" (select pk_failure_reason from eom_failure_b where pk_failure_b= '").append(pk_failure_b).append("') ");

    List tempList = AMMobUtils.mulTableLinkQuerySQL(sql.toString(), new String[] { "reason_name" });

    for (Iterator i$ = tempList.iterator(); i$.hasNext(); ) { Object object = i$.next();
      retMap.putAll((Map)object);
    }
    return retMap;
  }

  public Map<String, Object> submitNewFailureRecord(String groupid, String userid, Map saveinfo)
    throws BusinessException
  {
    AMMobUtils.setContextLoginInfo(groupid, userid);
    String pk_org = AMMobUtils.getDefaultOrgUnit(userid, groupid);
    if (AMMobUtils.isNull(pk_org)) {
      return returnErrorInfo("当前用户没有默认的资产组织，无法提交故障记录");
    }

    String pkstatus = queryEquipStatusByPK(saveinfo.get("pk_equip") + "", pk_org);
    if ((!AMMobUtils.isNull(pkstatus)) && (pkstatus.equals(ERR_EQUIP))) {
      return returnErrorInfo("当前业务单元下无此设备，请刷新设备");
    }

    if ((!AMMobUtils.isNull(pkstatus)) && 
      (!equipStatusMatchTransiRule(groupid, pkstatus))) {
      return returnErrorInfo("您提交的设备状态发生了变化，不能再填报故障");
    }

    String card_status = checkCardStatus(saveinfo.get("pk_equip") + "");
    if ((!AMMobUtils.isNull(card_status)) && 
      (!card_status.equals("3".toString().trim()))) {
      return returnErrorInfo("您提交的设备不是审批态，不能提交");
    }

    FailureVO aggvo = new FailureVO();
    FailureHeadVO headvo = setHeadVO(groupid, pk_org, userid);
    aggvo.setParent(headvo);
    FailureBodyVO bodyvo = setBodyVO(pk_org, saveinfo, 1);

    aggvo.setChildren(FailureBodyVO.class, new FailureBodyVO[] { bodyvo });

    FailureVO[] aggnewvo = (FailureVO[])pushSaveBillVO(new FailureVO[] { aggvo });

    PfUtilPrivate.runAction("SAVE", "4B08-01", aggnewvo[0], null, null, null, null);

    Map retPk = new HashMap();
    retPk.put("pk_failure", aggnewvo[0].getPrimaryKey());
    return retPk;
  }

  public Map<String, Object> saveMultiFailureRecord(String groupid, String userid, Map transMap)
    throws Exception
  {
    AMMobUtils.setContextLoginInfo(groupid, userid);
    String pk_org = AMMobUtils.getDefaultOrgUnit(userid, groupid);
    if (AMMobUtils.isNull(pk_org))
    {
      return returnErrorInfo("当前用户没有默认的资产组织，无法提交故障记录");
    }
    List errEquip = new ArrayList();
    List unuralEquip = new ArrayList();
    List errTransEquip = new ArrayList();
    for (Iterator i$ = transMap.keySet().iterator(); i$.hasNext(); ) { Object key = i$.next();
      Map map = (Map)transMap.get(key.toString());

      String pkstatus = queryEquipStatusByPK(map.get("pk_equip") + "", pk_org);
      if ((!AMMobUtils.isNull(pkstatus)) && (pkstatus.equals(ERR_EQUIP))) {
        errEquip.add(map.get("equip_name").toString());
      }

      if ((!AMMobUtils.isNull(pkstatus)) && (!pkstatus.equals(ERR_EQUIP)) && 
        (!equipStatusMatchTransiRule(groupid, pkstatus))) {
        errTransEquip.add(map.get("equip_name").toString());
      }

      String card_status = checkCardStatus(map.get("pk_equip") + "");
      if ((!AMMobUtils.isNull(card_status)) && 
        (!card_status.equals("3".toString().trim()))) {
        unuralEquip.add(map.get("equip_name").toString());
      }

    }

    if (errEquip.size() != 0) {
      return returnErrorInfo("当前业务单元下无" + errEquip.toString() + "设备");
    }
    if (errTransEquip.size() != 0) {
      return returnErrorInfo("您当前所选择提交的记录中设备" + errTransEquip.toString() + "状态发生了变化，不能再填报故障");
    }
    if (unuralEquip.size() != 0) {
      return returnErrorInfo("您当前所选择提交的记录中设备" + unuralEquip.toString() + "不是审批态，不能提交");
    }

    FailureVO aggvo = new FailureVO();
    FailureHeadVO headvo = setHeadVO(groupid, pk_org, userid);
    aggvo.setParent(headvo);

    List failureBodyVOs = new ArrayList();
    Set keysSet = transMap.keySet();
    String[] keys = (String[])keysSet.toArray(new String[keysSet.size()]);

    for (int i = 0; i < keys.length; i++) {
      if (MapUtils.isNotEmpty((Map)transMap.get(keys[i]))) {
        failureBodyVOs.add(setBodyVO(pk_org, (Map)transMap.get(keys[i]), i + 1));
      }
    }
    if (ArrayUtils.isNotEmpty(failureBodyVOs.toArray())) {
      aggvo.setChildrenVO((CircularlyAccessibleValueObject[])failureBodyVOs.toArray(new FailureBodyVO[failureBodyVOs.size()]));
    }
    FailureVO[] aggnewvo = (FailureVO[])pushSaveBillVO(new FailureVO[] { aggvo });
    PfUtilPrivate.runBatch("SAVE", "4B08-01", aggnewvo, null, null, null);

    Map retPk = new HashMap();
    retPk.put("pk_failure", aggnewvo[0].getPrimaryKey());
    return retPk;
  }

  private String queryEquipStatusByPK(String pk_equip, String pk_org)
    throws BusinessException
  {
    if ((pk_equip == null) || (AMMobUtils.isNull(pk_equip))) {
      return null;
    }
    StringBuffer sql = new StringBuffer();
    sql.append("  from pam_status where pk_status in (").append(" select pk_used_status from pam_equip where pk_equip ='").append(pk_equip).append("' ").append(" and pk_usedorg='").append(pk_org).append("' ").append(" and dr=0 ) ");

    List statusList = AMMobUtils.mulTableLinkQuerySQL(sql.toString(), new String[] { "pk_status" });

    if ((statusList == null) || (statusList.size() == 0)) {
      return ERR_EQUIP;
    }
    Map statusMap = (Map)statusList.get(0);
    String retString = statusMap.get("pk_status") + "";
    return retString;
  }

  private boolean equipStatusMatchTransiRule(String groupid, String pk_status)
    throws BusinessException
  {
    List pk_used_status = ((ITransiRuleService)AMProxy.lookup(ITransiRuleService.class)).getSupportedStatus(groupid, "4B08", "4B08-01");

    if (pk_used_status.size() == 0) {
      return true;
    }

    String[] pkusedstatus = (String[])pk_used_status.toArray(new String[pk_used_status.size()]);
    for (int i = 0; i < pkusedstatus.length; i++) {
      if ((pk_status.equals(pkusedstatus[i])) || (pk_status == pkusedstatus[i])) {
        return true;
      }
    }
    return false;
  }

  private String checkCardStatus(String pk_equip)
    throws BusinessException
  {
    if ((pk_equip == null) || (AMMobUtils.isNull(pk_equip))) {
      return null;
    }
    StringBuffer sql = new StringBuffer();

    sql.append(" from pam_equip where pk_equip ='").append(pk_equip).append("' ");

    List statusList = AMMobUtils.mulTableLinkQuerySQL(sql.toString(), new String[] { "card_status" });

    Map statusMap = (Map)statusList.get(0);
    String card_status = statusMap.get("card_status") + "";
    return card_status.toString().trim();
  }

  private FailureHeadVO setHeadVO(String groupid, String pk_org, String userid)
    throws BusinessException
  {
    FailureHeadVO headvo = new FailureHeadVO();
    headvo.setPk_group(BizContext.getInstance().getGroupId());
    headvo.setPk_org_v(OrgUtils.getCurVidByPkOrg(pk_org));
    headvo.setPk_org(pk_org);
    headvo.setBusi_type(PfUtilPrivate.retBusitypeCanStart("4B08", "4B08-01", pk_org, userid));

    headvo.setBill_type("4B08");
    headvo.setTransi_type("4B08-01");
    headvo.setPk_transitype(AMMobUtils.getBillORTransiTypeByCode("4B08-01"));

    headvo.setPk_recorder(AMMobUtils.getPsnIdByCurrentUser());
    headvo.setRecorder_date(new UFDate());

    headvo.setBillmaker(BizContext.getInstance().getUserId());
    headvo.setBillmaketime(new UFDateTime());

    headvo.setBill_status(Integer.valueOf(0));
    return headvo;
  }

  private FailureBodyVO setBodyVO(String pk_org, Map saveinfo, int rowno)
    throws BusinessException
  {
    FailureBodyVO bodyvo = new FailureBodyVO();
    bodyvo.setAttributeValue("rowno", Integer.valueOf(rowno));
    bodyvo.setAttributeValue("pk_equip", saveinfo.get("pk_equip"));
    bodyvo.setAttributeValue("pk_location", saveinfo.get("pk_location"));
    bodyvo.setAttributeValue("failure_time", saveinfo.get("failure_time"));
    if (!AMMobUtils.isNull(saveinfo.get("restore_time"))) {
      bodyvo.setAttributeValue("restore_time", saveinfo.get("restore_time"));
      bodyvo.setAttributeValue("close_flag", "TRUE");
    }
    bodyvo.setAttributeValue("pk_failure_type", saveinfo.get("pk_failure_type"));
    bodyvo.setAttributeValue("pk_failure_symptom", saveinfo.get("pk_failure_symptom"));
    bodyvo.setAttributeValue("pk_failure_reason", saveinfo.get("pk_failure_reason"));
    bodyvo.setAttributeValue("failure_expound", saveinfo.get("failure_expound"));
    bodyvo.setAttributeValue("memo", saveinfo.get("memo"));

    if (AMMobUtils.isNull(saveinfo.get("pk_equip"))) {
      return bodyvo;
    }

    Map defMap = queryEquipInfoByPk(null, null, saveinfo.get("pk_equip").toString());
    bodyvo.setAttributeValue("pk_usedunit", defMap.get("pk_usedunit"));
    bodyvo.setAttributeValue("pk_usedept", defMap.get("pk_usedept"));
    bodyvo.setAttributeValue("pk_user", defMap.get("pk_user"));

    bodyvo.setAttributeValue("pk_ownerorg", defMap.get("pk_ownerorg"));
    bodyvo.setAttributeValue("pk_usedorg", defMap.get("pk_usedorg"));
    bodyvo.setAttributeValue("pk_mandept", defMap.get("pk_mandept"));
    bodyvo.setAttributeValue("pk_manager", defMap.get("pk_manager"));

    Map pk_mainorgMap = ((IOrgPubService)AMProxy.lookup(IOrgPubService.class)).getDefaultMaintainVIDSByAssetOIDSAndCategoryIDS(new String[] { pk_org }, new String[] { defMap.get("pk_category") + "" });

    String pk_mainorg_v = pk_mainorgMap == null ? null : (String)pk_mainorgMap.get(pk_org + "|" + defMap.get("pk_category"));
    bodyvo.setAttributeValue("pk_maintainorg_v", pk_mainorg_v);
    bodyvo.setAttributeValue("pk_maintainorg", OrgUtils.getPkOrgByVid(pk_mainorg_v));
    return bodyvo;
  }

  public Map<String, Object> commitNewFailureRecord(String groupid, String userid)
    throws BusinessException
  {
    String pk_org = AMMobUtils.getDefaultOrgUnit(userid, groupid);
    if (AMMobUtils.isNull(pk_org)) {
      return returnErrorInfo("当前用户没有默认的资产组织，无法提交故障记录");
    }
    return null;
  }

  public StringBuffer getVisibleByMetaId(String groupid, String userid, String mdclassid)
    throws BusinessException
  {
    StringBuffer sbuf = new StringBuffer();
    if ((Strings.isEmpty(mdclassid)) || (Strings.isEmpty(groupid))) {
      return sbuf;
    }
    String pk_defaultOrg = AMMobUtils.getDefaultOrgUnit(userid, groupid);
    sbuf.append(VisibleUtil.getRefVisibleCondition(groupid, pk_defaultOrg, mdclassid));
    return sbuf;
  }

  public Map<String, Object> loadFailureTypeRef(String groupid, String userid)
    throws BusinessException
  {
    AMMobUtils.setContextLoginInfo(groupid, userid);
    StringBuffer sql = new StringBuffer();
    sql.append(" from pam_failure_type ").append(" where pk_group= '").append(groupid).append("' ").append(" and enablestate= ").append(2).append(" and dr=0 ");

    if (!getVisibleByMetaId(groupid, userid, "dff57e94-b6a1-4067-9567-decd3d1f2b3f").toString().equals("null"))
    {
      sql.append(" and ");

      sql.append(getVisibleByMetaId(groupid, userid, "dff57e94-b6a1-4067-9567-decd3d1f2b3f"));
    }
    sql.append(" order by type_code ");
    List typeList = AMMobUtils.mulTableLinkQuerySQL(sql.toString(), new String[] { "type_code", "type_name", "pk_failure_type" });

    Map returnMap = new LinkedHashMap();
    for (Iterator i$ = typeList.iterator(); i$.hasNext(); ) { Object object = i$.next();
      if (object != null) {
        Map retMap = (Map)object;
        returnMap.put(retMap.get("type_code").toString(), object);
      }
    }
    return returnMap;
  }

  public Map<String, Object> loadFailureSympRef(String groupid, String userid)
    throws BusinessException
  {
    StringBuffer sql = new StringBuffer();
    sql.append(" from pam_failure_symptom ").append(" where pk_group= '").append(groupid).append("' ").append(" and enablestate= ").append(2).append(" and dr=0 ");

    if (!getVisibleByMetaId(groupid, userid, "5e815d43-fec0-4cdb-9a0e-504cda468217").toString().equals("null"))
    {
      sql.append(" and ");

      sql.append(getVisibleByMetaId(groupid, userid, "5e815d43-fec0-4cdb-9a0e-504cda468217"));
    }
    sql.append(" order by symptom_code ");
    List symptomList = AMMobUtils.mulTableLinkQuerySQL(sql.toString(), new String[] { "symptom_code", "symptom_name", "pk_failure_symptom" });

    Map returnMap = new LinkedHashMap();
    for (Iterator i$ = symptomList.iterator(); i$.hasNext(); ) { Object object = i$.next();
      Map retMap = (Map)object;
      returnMap.put(retMap.get("symptom_code").toString(), object);
    }
    return returnMap;
  }

  public Map<String, Object> loadFailureReasonRef(String groupid, String userid)
    throws BusinessException
  {
    StringBuffer sql = new StringBuffer();
    sql.append(" from pam_failure_reason ").append(" where pk_group= '").append(groupid).append("' ").append(" and enablestate= ").append(2).append(" and dr=0 ");

    if (!getVisibleByMetaId(groupid, userid, "b9569f07-4f50-448d-8a55-58eb34b271d7").toString().equals("null"))
    {
      sql.append(" and ");

      sql.append(getVisibleByMetaId(groupid, userid, "b9569f07-4f50-448d-8a55-58eb34b271d7"));
    }
    sql.append(" order by reason_code ");
    List reasonList = AMMobUtils.mulTableLinkQuerySQL(sql.toString(), new String[] { "reason_code", "reason_name", "pk_failure_reason" });

    Map returnMap = new LinkedHashMap();
    for (Iterator i$ = reasonList.iterator(); i$.hasNext(); ) { Object object = i$.next();
      Map retMap = (Map)object;
      returnMap.put(retMap.get("reason_code").toString(), object);
    }
    return returnMap;
  }

  public Map<String, Object> loadFailureEquipRef(String groupid, String userid)
    throws BusinessException
  {
    Map equipMap = queryEquipInfoByPk(groupid, userid, null);
    return equipMap;
  }

  public Map<String, Object> loadFailureLocationRef(String groupid, String userid)
    throws BusinessException
  {
    StringBuffer sql = new StringBuffer();

    sql.append(" from pam_location where pk_group= '").append(groupid).append("' ").append(" and dr=0 and ").append("enablestate").append(" = ").append(2);

    if (!getVisibleByMetaId(groupid, userid, "e618020b-0a93-4b41-8e35-cc861286edc7").toString().equals("null"))
    {
      sql.append(" and ");

      sql.append(getVisibleByMetaId(groupid, userid, "e618020b-0a93-4b41-8e35-cc861286edc7"));
    }
    sql.append(" order by location_code ");
    List locationList = AMMobUtils.mulTableLinkQuerySQL(sql.toString(), new String[] { "pk_location", "location_code", "location_name" });

    Map retMap = new LinkedHashMap();
    for (Iterator i$ = locationList.iterator(); i$.hasNext(); ) { Object object = i$.next();
      if (object != null) {
        Map tempMap = (Map)object;
        retMap.put(tempMap.get("pk_location").toString(), (Map)object);
      }
    }
    return retMap;
  }

  public Map<String, Object> queryLocationInfoFromEquipCard(String groupid, String userid, String pk_equip)
    throws BusinessException
  {
    Map locationMap = queryEquipInfoByPk(groupid, userid, pk_equip);
    return locationMap;
  }

  private Map<String, Object> queryEquipInfoByPk(String groupid, String userid, String pk_equip)
    throws BusinessException
  {
    String pk_org = AMMobUtils.getDefaultOrgUnit(userid, groupid);
    StringBuffer sql = new StringBuffer();
    sql.append(" select a.pk_equip,a.equip_name,a.equip_code,b.pk_location,b.location_code, ").append(" b.location_name,a.pk_usedunit,a.pk_usedept,a.pk_user,a.pk_category, ").append(" a.pk_ownerorg,a.pk_usedorg,a.pk_mandept,a.pk_manager ").append(" from pam_equip a left join pam_location b on a.pk_location = b.pk_location where ");

    if ((!AMMobUtils.isNull(pk_equip)) && (AMMobUtils.isNull(groupid)) && (AMMobUtils.isNull(userid))) {
      sql.append(" pk_equip= '").append(pk_equip).append("' ");
    }
    else {
      List pk_used_status = ((ITransiRuleService)AMProxy.lookup(ITransiRuleService.class)).getSupportedStatus(groupid, "4B08", "4B08-01");

      if (pk_used_status.size() == 0) {
        return null;
      }
      sql.append(" a.pk_group = '").append(groupid).append("' ").append(" and ").append(" pk_usedorg = '").append(pk_org).append("' ").append(" and pk_used_status in ").append(InSqlManager.getInSQLValue(pk_used_status)).append(" and a.dr=0 ").append(" and a.card_status = ").append(3);

      if (!AMMobUtils.isNull(pk_equip)) {
        sql.append(" and pk_equip= '").append(pk_equip).append("' ");
      }
      sql.append(" order by a.equip_code ");
    }

    List equipList = AMMobUtils.mulTableLinkQuery4SQL(sql.toString(), new String[] { PK_EQUIP, EQUIP_NAME, EQUIP_CODE, "pk_location", "location_code", "location_name", "pk_usedunit", "pk_usedept", "pk_user", PK_CATEGORY, "pk_ownerorg", "pk_usedorg", "pk_mandept", "pk_manager" });

    if (!AMMobUtils.isNull(pk_equip)) {
      Map retMap = new HashMap();
      for (Iterator i$ = equipList.iterator(); i$.hasNext(); ) { Object object = i$.next();
        retMap.putAll((Map)object);
      }

      for (Object key : retMap.keySet()) {
        if ((retMap.get(key) == null) || (AMMobUtils.isNull(retMap.get(key)))) {
          retMap.put(key, "");
        }
      }
      return retMap;
    }

    Map returnMap = new LinkedHashMap();

    for (Iterator i$ = equipList.iterator(); i$.hasNext(); ) { Object object = i$.next();
      Map retMap = new HashMap();
      retMap.putAll((Map)object);

      String[] removeStr = { "pk_usedunit", "pk_usedept", "pk_user", "pk_category", "pk_ownerorg", "pk_usedorg", "pk_mandept", "pk_manager" };

      for (int i = 0; i < removeStr.length; i++) {
        retMap.remove(removeStr[i]);
      }
      returnMap.put(retMap.get(PK_EQUIP) + "", retMap);
    }
    return returnMap;
  }

  private Map<String, Object> returnErrorInfo(String errorinfo)
  {
    Map retMap = new HashMap();
    retMap.put(RESULTCODE, "2");
    retMap.put(RESULTMSG, errorinfo);
    return retMap;
  }

  public String fruploadFile(String data_pk, String filePath, String pk_group, String userId, String groupId, Map<String, Object> paramMap)
    throws BusinessException
  {
    return AMMobUtils.saveFile(data_pk, filePath, userId, paramMap);
  }

  public String frdeleteFile(String data_pk, String filePath, String pk_group, String userId, String groupId, Map<String, Object> deleteParamMap)
    throws BusinessException
  {
    return AMMobUtils.deleteFile(data_pk, filePath, deleteParamMap);
  }

  public Map<String, Object> frqueryPicturesBybillPK(String data_pk, String filePath, String pk_group, String userId, String groupId)
    throws BusinessException
  {
    return AMMobUtils.queryPicturesBybillPK(data_pk, filePath);
  }
}