package com.twi.report.services.imp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.twi.base.BaseService;
import com.twi.base.domain.Page;
import com.twi.base.util.DateUtils;
import com.twi.base.util.StringUtils;
import com.twi.freecart.domain.FreeCart;
import com.twi.report.services.ReportServices;

/**
 * 报表
 * @author ouwt
 *
 */
@Service("reportServices")
public class ReportServicesImp extends BaseService implements ReportServices {

	@Override
	public List<Map<String, Object>> getFreeCartByPayTypeId(String payTypeId,String orgId) {
		StringBuffer sql=new StringBuffer();
		
		sql.append(" select Id_ as id_,name_ from free_cart where id_ in (");
		sql.append(" SELECT free_Cart_id from  free_cart_detailed where id_ in(");
		sql.append("select poi.free_Cart_Detailed_id  from  free_po_item poi where  ");
		sql.append(" poi.state_=2  AND poi.pay_type_id=? AND poi.org_id=?)");
		sql.append(")   ORDER BY Id_ ");
		return this.jdbcDao.queryForMap(sql.toString(), new Object[]{payTypeId,orgId});
	}
	
	
	@Override
	public Page<Map<String, Object>> getpRojectReport(Page<Map<String, Object>> page, String payTypeId,
			String studentCode, String studentName, String majorId,String majorName, String clazzName, String freeCartId,String mobileMumber, Date startDate,
			Date endDate,String orgId) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer sql=new StringBuffer();
		
		sql.append("SELECT  ");
		sql.append("poi.pay_money  AS payMoney,po.order_no,po.Student_name,");
		sql.append("po.Student_code,po.product_name,wxp.wx_order_no,");
		sql.append(" DATE_FORMAT(wxp.pay_time, '%Y-%m-%d') as pay_time,wxp.openid_,");
		
		//sql.append(" CASE wxp.check_state WHEN 1 THEN '已对帐'  ELSE '未对帐' END check_state_str ,");
		
		sql.append(" CASE wxp.state_  WHEN 2 THEN '已对帐' ELSE '未对帐' END check_state_str ,");
		sql.append(" CASE wxp.state_  WHEN 2 THEN '已缴费' ELSE '缴费中' END state_str ,");
		
		sql.append("fcd.major_name,fcd.clazz_name,fcd.free_Cart_name,poi.Mobile_number,poi.remark_ ");
		sql.append(" FROM  free_po_item  poi ");
		sql.append(" LEFT JOIN  free_Cart_Detailed fcd  ON fcd.ID_=poi.free_Cart_Detailed_id ");
		sql.append(" LEFT JOIN  free_po po  ON po.ID_=poi.free_po_id  ");
		sql.append(" LEFT JOIN  free_wx_pay wxp  ON wxp.free_po_id=po.id_ ");
		sql.append(" WHERE  poi.state_=2 AND po.state_=3  AND poi.pay_type_id=? AND poi.org_id=? ");
		list.add(payTypeId);//'缴费类型ID'
		list.add(orgId);//'学校ID'
		
		if (StringUtils.isNotBlank(studentCode)) {// '%学号%'
			sql.append(" AND po.Student_code LIKE  ? ");
			list.add("%" + studentCode + "%");
		}
		
		
		if (StringUtils.isNotBlank(studentName)) {// '%学生姓名%'
			sql.append(" AND  po.Student_name LIKE  ? ");
			list.add("%" +studentName + "%");
		}
		
		
		if (StringUtils.isNotBlank(majorId)) {//'专业Id'
			sql.append(" AND fcd.major_id=? ");
			list.add(majorId);
		}
		

		if (StringUtils.isNotBlank(majorName)) {//'专业Id'
			sql.append(" AND fcd.major_name like ? ");
			list.add("%" + majorName + "%");
			
		}
		
		if (StringUtils.isNotBlank(clazzName)) {//'%所属班级%'
			sql.append(" AND  fcd.clazz_name LIKE ? ");
			list.add("%" + clazzName + "%");
		}
		
		if (StringUtils.isNotBlank(majorId)) {//'%缴费账套%'
			sql.append(" AND  fcd.free_Cart_id = ? ");
			list.add(freeCartId);
		}
		
		if (StringUtils.isNotBlank(mobileMumber)) {//手机号码
			sql.append(" AND  poi.Mobile_number = ? ");
			list.add(mobileMumber);
		}
		
		if (startDate!=null) {//开始时间
			sql.append(" AND  wxp.pay_time>='"+DateUtils.format(startDate, "yyyy-MM-dd")+"'  ");
			
		}
		if (endDate!=null) {//结束时间
			sql.append("  AND    wxp.pay_time<='"+DateUtils.format(endDate, "yyyy-MM-dd")+" 23:59:59'  ");
			
		}
		
		sql.append(" ORDER BY wxp.pay_time DESC ");
	
		return this.jdbcDao.queryForPageMap(page, sql.toString(), list.toArray());
		
		
	}

	@Override
	public List<Map<String, Object>> getpRojectReport(String payTypeId, String studentCode, String studentName,
			String majorId,String majorName, String clazzName, String freeCartId,String mobileMumber, Date startDate, Date endDate,String orgId) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer sql=new StringBuffer();
		
		sql.append("SELECT  ");
		sql.append("poi.pay_money  AS payMoney,po.order_no,po.Student_name,");
		sql.append("po.Student_code,po.product_name,wxp.wx_order_no,");
        sql.append(" DATE_FORMAT(wxp.pay_time, '%Y-%m-%d') as pay_time,wxp.openid_,");
		
		//sql.append(" CASE wxp.check_state WHEN 1 THEN '已对帐'  ELSE '未对帐' END check_state_str ,");
		
		sql.append(" CASE wxp.state_ WHEN 2  THEN '已对帐' ELSE '未对帐' END check_state_str ,");
		sql.append(" CASE wxp.state_ WHEN 2  THEN '已缴费' ELSE '缴费中' END state_str ,");
		
		sql.append("fcd.major_name,fcd.clazz_name,fcd.free_Cart_name,poi.Mobile_number,poi.remark_ ");
		sql.append(" FROM  free_po_item  poi ");
		sql.append(" LEFT JOIN  free_Cart_Detailed fcd  ON fcd.ID_=poi.free_Cart_Detailed_id ");
		sql.append(" LEFT JOIN  free_po po  ON po.ID_=poi.free_po_id  ");
		sql.append(" LEFT JOIN  free_wx_pay wxp  ON wxp.free_po_id=po.id_ ");
		sql.append(" WHERE  poi.state_=2  AND po.state_=3 AND poi.pay_type_id=? AND poi.org_id=? ");
		list.add(payTypeId);//'缴费类型ID'
		list.add(orgId);//'学校ID'
		
		if (StringUtils.isNotBlank(studentCode)) {// '%学号%'
			sql.append(" AND po.Student_code LIKE  ? ");
			list.add("%" + studentCode + "%");
		}
		
		
		if (StringUtils.isNotBlank(studentName)) {// '%学生姓名%'
			sql.append(" AND  po.Student_name LIKE  ? ");
			list.add("%" +studentName + "%");
		}
		
		
		if (StringUtils.isNotBlank(majorId)) {//'专业Id'
			sql.append(" AND fcd.major_id=? ");
			list.add(majorId);
		}
		
		if (StringUtils.isNotBlank(majorName)) {//'专业Id'
			sql.append(" AND fcd.major_name like ? ");
			list.add("%" + majorName + "%");
			
		}
		
		if (StringUtils.isNotBlank(clazzName)) {//'%所属班级%'
			sql.append(" AND  fcd.clazz_name LIKE ? ");
			list.add("%" + clazzName + "%");
		}
		
		if (StringUtils.isNotBlank(majorId)) {//'%缴费账套%'
			sql.append(" AND  fcd.free_Cart_id = ? ");
			list.add(freeCartId);
		}
		if (StringUtils.isNotBlank(mobileMumber)) {//手机号码
			sql.append(" AND  poi.Mobile_number = ? ");
			list.add(mobileMumber);
		}
		if (startDate!=null) {//开始时间
			sql.append(" AND  wxp.pay_time>='"+DateUtils.format(startDate, "yyyy-MM-dd")+"'  ");
			
		}
		if (endDate!=null) {//结束时间
			sql.append("  AND    wxp.pay_time<='"+DateUtils.format(endDate, "yyyy-MM-dd")+" 23:59:59'  ");
			
		}
		
		sql.append(" ORDER BY wxp.pay_time DESC ");
		
		return this.jdbcDao.queryForMap(sql.toString(), list.toArray());
		
	}

	

}
